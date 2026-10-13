"""Compile the Java solutions, run them with the tracer, and write the page's data files.

Usage: python build.py [Topic ...]   (no topics = all)
"""
import json, os, re, subprocess, sys

ROOT = os.path.dirname(os.path.abspath(__file__))
SRC, BUILD, DATA = (os.path.join(ROOT, d) for d in ("src", "build", "data"))


def slug(s):
    return re.sub(r"[^a-z0-9]+", "-", s.lower()).strip("-")


def t_statement_end(lines, i):
    """Return the index of the last line of a statement starting at line i (string-aware paren balance)."""
    depth, in_str, esc_next = 0, None, False
    j = i
    while j < len(lines):
        for ch in lines[j]:
            if in_str:
                if esc_next: esc_next = False
                elif ch == "\\": esc_next = True
                elif ch == in_str: in_str = None
                continue
            if ch in "\"'": in_str = ch
            elif ch == "(": depth += 1
            elif ch == ")": depth -= 1
        if depth <= 0 and lines[j].rstrip().endswith(";"):
            return j
        j += 1
    return i


def is_code(line):
    s = line.strip()
    return bool(s) and not re.fullmatch(r"[{}();\s]*", s) and not s.startswith("//")


src_cache = {}


def region(file, marker):
    if file not in src_cache:
        with open(os.path.join(SRC, file), encoding="utf-8") as fh:
            src_cache[file] = fh.read().split("\n")
    lines = src_cache[file]
    start = next((i for i, l in enumerate(lines) if l.strip() == "//@code " + marker), None)
    if start is None:
        raise SystemExit(f"marker //@code {marker} not found in {file}")
    end = next(i for i in range(start + 1, len(lines)) if lines[i].strip() == "//@end")
    # orig 1-based line numbers start+2 .. end
    hidden, t_ranges = set(), []
    i = start + 1
    while i < end:
        s = lines[i].strip()
        if s.startswith("T."):
            j = t_statement_end(lines, i)
            t_ranges.append((i + 1, j + 1))
            hidden.update(range(i, j + 1))
            i = j + 1
            continue
        if s.endswith("// @hide"):
            hidden.add(i)
        i += 1
    kept = [(i + 1, lines[i]) for i in range(start + 1, end) if i not in hidden]
    # collapse blank runs and trim
    out, prev_blank = [], True
    for n, l in kept:
        blank = not l.strip()
        if blank and prev_blank: continue
        out.append((n, l)); prev_blank = blank
    while out and not out[-1][1].strip(): out.pop()
    ind = min((len(l) - len(l.lstrip()) for _, l in out if l.strip()), default=0)
    disp, mapping, codeflag = [], {}, {}
    for k, (n, l) in enumerate(out):
        t = l[ind:] if len(l) >= ind else l.lstrip()
        t = re.sub(r"^(\s*)((?:public|private|protected)\s+)?static\s+(?!final)", r"\1\2", t)
        t = t.replace(" implements TN", "")
        disp.append(t.rstrip())
        mapping[n] = k + 1
        codeflag[n] = is_code(l)
    return {"code": "\n".join(disp), "map": mapping, "codeflag": codeflag, "t_ranges": t_ranges, "lo": start + 2, "hi": end, "src": lines}


ALT = {
    "Python": (os.path.join(ROOT, "alt", "py"), ".py", "#@code ", "#@end", re.compile(r"\s*#\s*@([\d,]+)\s*$")),
    "C++": (os.path.join(ROOT, "alt", "cpp"), ".cpp", "//@code ", "//@end", re.compile(r"\s*//\s*@([\d,]+)\s*$")),
}
alt_cache = {}


def alt_region(lang, marker):
    d, ext, start_tag, end_tag, tag_re = ALT[lang]
    if not os.path.isdir(d): return None
    for f in sorted(os.listdir(d)):
        if not f.endswith(ext): continue
        key = (lang, f)
        if key not in alt_cache:
            with open(os.path.join(d, f), encoding="utf-8") as fh: alt_cache[key] = fh.read().split(chr(10))
        lines = alt_cache[key]
        if start_tag + marker not in lines: continue
        i = lines.index(start_tag + marker)
        j = next(k for k in range(i + 1, len(lines)) if lines[k].strip() == end_tag)
        body = lines[i + 1:j]
        while body and not body[0].strip(): body.pop(0)
        while body and not body[-1].strip(): body.pop()
        ind = min((len(l) - len(l.lstrip()) for l in body if l.strip()), default=0)
        code, mapping = [], {}
        for n, l in enumerate(body, 1):
            m = tag_re.search(l)
            if m:
                for jl in m.group(1).split(","):
                    mapping.setdefault(int(jl), n)
                l = l[: m.start()]
            code.append(l[ind:].rstrip())
        return {"code": chr(10).join(code), "lines": mapping}
    return None


def attach_alt(rec, warn):
    used = {s["line"] for s in rec["steps"]}
    for e in rec["edgeCases"]: used |= {s["line"] for s in e["steps"]}
    used.discard(0)
    out = {}
    for lang in ALT:
        r = alt_region(lang, rec["marker"])
        if not r: continue
        lines = r["lines"]
        for jl in sorted(used):
            if jl not in lines:
                lower = [k for k in lines if k < jl]
                lines[jl] = lines[max(lower)] if lower else 1
                warn.append(f"{rec['title']}: {lang} has no line tagged @{jl}")
        out[lang] = {"code": r["code"], "lines": {str(k): v for k, v in sorted(lines.items()) if k in used}}
    if out: rec["alt"] = out


def map_line(reg, raw, direction):
    if not (reg["lo"] <= raw <= reg["hi"]):
        return 0
    anchor = raw
    for a, b in reg["t_ranges"]:
        if a <= raw <= b:
            anchor = a; break
    if anchor in reg["map"] and reg["codeflag"].get(anchor):
        return reg["map"][anchor]
    # A step written right after a switch block points at the switch itself.
    k = anchor - 1
    while k >= reg["lo"] and not reg["src"][k - 1].strip():
        k -= 1
    if k >= reg["lo"] and reg["src"][k - 1].strip() in ("}", "};"):
        depth = 0
        for j in range(k, reg["lo"] - 1, -1):
            depth += reg["src"][j - 1].count("}") - reg["src"][j - 1].count("{")
            if depth == 0:
                if re.match(r"\s*(\w+\s*=\s*)?switch\b", reg["src"][j - 1]) and j in reg["map"]:
                    return reg["map"][j]
                break
    up, down = range(anchor - 1, reg["lo"] - 1, -1), range(anchor + 1, reg["hi"] + 1)
    for rng in ((up, down) if direction == 0 else (down, up)):
        for n in rng:
            if n in reg["map"] and reg["codeflag"][n]:
                return reg["map"][n]
    return 0


def fix_steps(steps, reg, file, warn, title):
    out = []
    for s in steps:
        line = map_line(reg, s["raw"], s["dir"]) if s["file"] == file else 0
        if line == 0: warn.append(f"{title}: step '{s['title']}' has no line (raw {s['file']}:{s['raw']})")
        out.append({"line": line, "title": s["title"], "explain": s["explain"], "vars": s["vars"], "structures": s["structures"]})
    return out


def natural(k):
    return (len(k), k)


def stable(o):
    """Sort "marks"/"edgeMarks" keys. Java fills them from hash-ordered collections, so their order can change between runs."""
    if isinstance(o, list):
        return [stable(x) for x in o]
    if isinstance(o, dict):
        return {k: ({m: v[m] for m in sorted(v, key=natural)} if k in ("marks", "edgeMarks") and isinstance(v, dict) else stable(v))
                for k, v in o.items()}
    return o


def main():
    sys.stdout.reconfigure(encoding="utf-8")
    topics = sys.argv[1:]
    os.makedirs(BUILD, exist_ok=True); os.makedirs(DATA, exist_ok=True)
    javas = [os.path.join(SRC, f) for f in os.listdir(SRC) if f.endswith(".java")]
    r = subprocess.run(["javac", "-encoding", "UTF-8", "-nowarn", "-d", BUILD] + javas, capture_output=True, text=True)
    if r.returncode:
        print(r.stdout, r.stderr); sys.exit(1)
    out_json = os.path.join(BUILD, "out.json")
    r = subprocess.run(["java", "-Xss64m", "-cp", BUILD, "Main", out_json] + topics, capture_output=True, text=True, encoding="utf-8")
    print(r.stdout.strip());
    if r.returncode:
        print(r.stderr); sys.exit(1)
    with open(out_json, encoding="utf-8") as fh: probs = json.load(fh)
    with open(os.path.join(ROOT, "sheet.json"), encoding="utf-8") as fh: sheet = json.load(fh)
    by_title = {p["title"]: p for p in probs}
    warn = []
    for p in probs:
        if p["title"] not in {s["name"] for s in sheet}: warn.append(f"{p['title']}: not in sheet")
    # merge into existing per-topic files so partial builds keep earlier topics
    groups = {}
    for s in sheet:
        p = by_title.get(s["name"])
        if not p: continue
        reg = region(p["file"], p["marker"])
        rec = {
            "id": slug(s["name"]), "title": s["name"], "level": s["level"], "topic": s["topic"], "url": s["url"],
            "language": "Java", "category": p.get("category", s["topic"]), "summary": p.get("summary", ""),
            "complexity": p.get("complexity", {}), "intuition": p.get("intuition", []), "pitfalls": p.get("pitfalls", []),
            "input": p.get("input", ""), "output": p.get("output", ""), "capped": p.get("capped", False),
            "code": reg["code"], "marker": p["marker"],
            "steps": fix_steps(p.get("steps", []), reg, p["file"], warn, s["name"]),
            "edgeCases": [],
        }
        for e in p["edgeCases"]:
            e2 = {k: e[k] for k in ("name", "input", "expected", "actual", "status", "why", "capped")}
            e2["steps"] = fix_steps(e["steps"], reg, p["file"], warn, s["name"] + " / " + e["name"])
            rec["edgeCases"].append(e2)
        attach_alt(rec, warn)
        rec.pop("marker")
        groups.setdefault(slug(s["topic"]), []).append(rec)
    for g, recs in groups.items():
        path = os.path.join(DATA, g + ".json")
        existing = {}
        if os.path.exists(path):
            with open(path, encoding="utf-8") as fh: existing = {x["id"]: x for x in json.load(fh)}
        for x in recs: existing[x["id"]] = x
        with open(path, "w", encoding="utf-8") as fh: json.dump(stable(list(existing.values())), fh, separators=(",", ":"), ensure_ascii=False)
    # index over everything on disk
    have = {}
    for f in os.listdir(DATA):
        if f.endswith(".json") and f != "index.json":
            with open(os.path.join(DATA, f), encoding="utf-8") as fh:
                for x in json.load(fh): have[x["id"]] = (f, x)
    index = []
    for s in sheet:
        i = slug(s["name"])
        entry = {"id": i, "title": s["name"], "level": s["level"], "topic": s["topic"], "url": s["url"]}
        if i in have:
            entry["file"] = have[i][0]; entry["steps"] = len(have[i][1]["steps"]); entry["edges"] = len(have[i][1]["edgeCases"])
        index.append(entry)
    with open(os.path.join(DATA, "index.json"), "w", encoding="utf-8") as fh: json.dump(index, fh, separators=(",", ":"), ensure_ascii=False)
    total = sum(os.path.getsize(os.path.join(DATA, f)) for f in os.listdir(DATA))
    print(f"built {sum(len(v) for v in groups.values())} problems this run; {len(have)}/{len(sheet)} available; data {total/1e6:.2f} MB")
    for w in warn: print("WARN", w)


if __name__ == "__main__":
    main()
