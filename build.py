"""Compile the Java solutions, run them with the tracer, and write the page's data files.

Usage: python build.py [Topic ...]   (no topics = all)
"""
import json, os, re, subprocess, sys

ROOT = os.path.dirname(os.path.abspath(__file__))
SRC, BUILD, DATA = (os.path.join(ROOT, d) for d in ("src", "build", "data"))
sys.path.insert(0, os.path.join(ROOT, "tools"))
import deepen  # noqa: E402


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
        t = re.sub(r"\s*//@prune .*$", "", t)
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


ALT_GAPS = {}

# Line alignment between the Java solution and its Python/C++ translation, for lines without an @tag.
SYN = {"append": "add", "push_back": "add", "emplace_back": "add", "push": "add", "offer": "add", "addlast": "add", "insert": "add",
       "pop_back": "pop", "remove": "pop", "poll": "pop", "removelast": "pop", "pollfirst": "pop", "popleft": "pop", "pollLast": "pop",
       "length": "len", "size": "len", "len": "len", "and": "&&", "or": "||", "not": "!", "elif": "if", "true": "true", "false": "false",
       "none": "null", "nullptr": "null", "null": "null", "containskey": "in", "contains": "in", "count": "in", "get": "[]", "at": "[]",
       "put": "[]=", "def": "fn", "void": "fn"}
STOP = {"int", "long", "double", "boolean", "bool", "char", "string", "str", "list", "vector", "arraylist", "linkedlist", "new", "auto", "const",
        "final", "static", "self", "this", "std", "var", "integer", "map", "hashmap", "unordered_map", "set", "hashset", "unordered_set",
        "deque", "arraydeque", "range", "in", "of", "for_each", "end", "begin", "math", "collections", "arrays", "le", "ge"}


def toks(line, lang):
    line = re.sub(r"#.*$" if lang == "Python" else r"//.*$", "", line)
    out = set()
    for t in re.findall(r"[A-Za-z_]\w*|\d+|==|!=|<=|>=|&&|\|\||[<>+\-*/%]", line):
        t = SYN.get(t.lower(), t.lower())
        if t not in STOP: out.add(t)
    return out


def align(java, alt, tagged, lang):
    """Best alt line for each Java line: most shared tokens, nudged toward where the tagged lines say it should be."""
    jt = [toks(l, "Java") for l in java]
    at = [toks(l, lang) for l in alt]
    anchors = sorted(tagged.items())
    out = {}
    for j in range(1, len(java) + 1):
        A = jt[j - 1]
        if not A: continue
        before = [(k, v) for k, v in anchors if k <= j]
        after = [(k, v) for k, v in anchors if k > j]
        if before and after:
            (j0, a0), (j1, a1) = before[-1], after[0]
            exp = a0 + (j - j0) * (a1 - a0) / max(1, j1 - j0)
        elif before: exp = before[-1][1] + (j - before[-1][0])
        elif after: exp = after[0][1] - (after[0][0] - j)
        else: exp = j
        best, score = None, 0.0
        for a in range(1, len(alt) + 1):
            B = at[a - 1]
            if not B: continue
            sim = len(A & B) / len(A | B)
            sc = sim + 0.25 / (1 + abs(a - exp) / 3)
            if sim >= 0.25 and sc > score: best, score = a, sc
        if best: out[j] = best
    return out


def attach_alt(rec, warn):
    used = {s["line"] for s in rec["steps"]}
    for e in rec["edgeCases"]: used |= {s["line"] for s in e["steps"]}
    used.discard(0)
    out = {}
    for lang in ALT:
        r = alt_region(lang, rec["marker"])
        if not r: continue
        lines = r["lines"]
        guessed = align(rec["code"].split(chr(10)), r["code"].split(chr(10)), lines, lang)
        for jl in sorted(used):
            if jl not in lines:
                if jl in guessed: lines[jl] = guessed[jl]
                else:
                    lower = [k for k in lines if k < jl]
                    lines[jl] = lines[max(lower)] if lower else 1
                    ALT_GAPS[lang] = ALT_GAPS.get(lang, 0) + 1
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
        if line == 0 and s.get("hand"): warn.append(f"{title}: step '{s['title']}' has no line (raw {s['file']}:{s['raw']})")
        t = {"line": line, "title": s["title"], "explain": s["explain"], "vars": s["vars"], "structures": s["structures"]}
        for k in ("kind", "st", "up", "back", "cond", "ret", "hand", "dir"):
            if k in s: t[k] = s[k]
        out.append(t)
    return out


def deep_steps(raw, calls, reg, file, warn, title, which):
    """Mapped, narrated, trimmed steps of one run, plus its recursion-tree nodes."""
    steps = fix_steps(raw, reg, file, warn, title)
    steps = [s for s in steps if s["line"] or s.get("hand")]   # steps outside the shown code (driver calls)
    return deepen.process(steps, calls, reg["code"].split(chr(10)), which)


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
    # Instrument the solutions (tools/Instr.java) so every executed line records a step, then compile and run them.
    tools, isrc = os.path.join(BUILD, "tools"), os.path.join(BUILD, "isrc")
    r = subprocess.run(["javac", "-encoding", "UTF-8", "-nowarn", "-d", tools, os.path.join(ROOT, "tools", "Instr.java")], capture_output=True, text=True)
    if r.returncode:
        print(r.stdout, r.stderr); sys.exit(1)
    r = subprocess.run(["java", "-cp", tools, "Instr", SRC, isrc], capture_output=True, text=True, encoding="utf-8")
    if r.returncode:
        print(r.stdout, r.stderr); sys.exit(1)
    javas = [os.path.join(isrc, f) for f in sorted(os.listdir(isrc)) if f.endswith(".java")]
    r = subprocess.run(["javac", "-encoding", "UTF-8", "-nowarn", "-d", BUILD] + javas, capture_output=True, text=True)
    if r.returncode:
        print(r.stdout, r.stderr); sys.exit(1)
    out_json = os.path.join(BUILD, "out.json")
    r = subprocess.run(["java", "-Dalgoscope.deep=true", "-Xss64m", "-Xmx3g", "-cp", BUILD, "Main", out_json] + topics, capture_output=True, text=True, encoding="utf-8")
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
            "edgeCases": [],
        }
        steps, capped, calls = deep_steps(p.get("steps", []), p.get("calls"), reg, p["file"], warn, s["name"], "main")
        rec["steps"], rec["capped"] = steps, rec["capped"] or capped
        if calls: rec["calls"] = calls
        for e in p["edgeCases"]:
            e2 = {k: e[k] for k in ("name", "input", "expected", "actual", "status", "why", "capped")}
            steps, capped, calls = deep_steps(e["steps"], e.get("calls"), reg, p["file"], warn, s["name"] + " / " + e["name"], "edge")
            e2["steps"], e2["capped"] = steps, e2["capped"] or capped
            if calls: e2["calls"] = calls
            rec["edgeCases"].append(e2)
        attach_alt(rec, warn)
        rec.pop("marker")
        rec["steps"] = deepen.encode(rec["steps"])
        for e2 in rec["edgeCases"]: e2["steps"] = deepen.encode(e2["steps"])
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
    for lang, n in ALT_GAPS.items(): print(f"note: {n} Java lines have no {lang} tag and use the nearest earlier tagged line")


if __name__ == "__main__":
    main()
