"""Turn one raw deep trace (from the instrumented Java run) into the steps the page shows.

The instrumented code records a step for every executed line (see tools/Instr.java and the T.enter/T.at/T.c/T.r
calls in src/T.java). This module:
  1. merges the hand-written T.s/T.n narration into the automatic step it describes,
  2. writes a title and plain-English explanation for every automatic step,
  3. adds the call stack and, for recursive code, the recursion tree,
  4. trims very long runs (keeping the start and the end),
  5. delta-encodes structures that did not change since the previous step (the page expands them on load).
"""
import json, re

# steps kept per run: (head, tail)
BUDGET = {"main": (380, 40), "edge": (170, 30)}

COND_KINDS = {1, 2, 3, 4}


# ---------------------------------------------------------------- formatting
def fmt(v):
    if v is None: return "null"
    if v is True: return "true"
    if v is False: return "false"
    if isinstance(v, str): return v
    if isinstance(v, (int, float)): return str(v)
    if isinstance(v, list): return "[" + ", ".join(fmt(x) for x in v) + "]"
    if isinstance(v, dict): return "{" + ", ".join(f"{k}: {fmt(x)}" for k, x in v.items()) + "}"
    return str(v)


def short(v, n=40):
    s = fmt(v)
    return s if len(s) <= n else s[: n - 1] + "…"


def code(s):
    return "`" + s.replace("`", "'") + "`"


def content(d):
    """What a structure holds, ignoring pointers and marks (used to detect changes)."""
    k = d.get("kind")
    if k == "matrix": return d.get("rows")
    if k in ("map", "set"): return d.get("entries", d.get("values"))
    if k in ("linkedlist", "tree"): return sorted(json.dumps([n.get("id"), n.get("value"), n.get("next"), n.get("left"), n.get("right"), n.get("children")]) for n in d.get("nodes", []))
    if k == "graph": return sorted(json.dumps([e.get("from"), e.get("to"), e.get("weight")]) for e in d.get("edges", [])) + sorted(json.dumps([n.get("id"), n.get("label")]) for n in d.get("nodes", []))
    if k == "calltree": return None
    return d.get("values")


def describe_change(d, old):
    """One short phrase for how structure d changed since old (None if it did not)."""
    name, k = d.get("name"), d.get("kind")
    a, b = content(old), content(d)
    if a == b: return None
    n = code(name)
    if k in ("array", "stack", "queue", "heap") and isinstance(a, list) and isinstance(b, list):
        if len(b) == len(a) + 1 and b[:-1] == a:
            verb = {"stack": "pushes", "queue": "enqueues"}.get(k, "appends")
            return f"{n} {verb} {short(b[-1], 24)}" + (f" → {short(b)}" if k == "array" and len(b) <= 12 else "")
        if len(a) == len(b) + 1 and a[:-1] == b:
            return f"{n} {'pops' if k == 'stack' else 'removes'} {short(a[-1], 24)}" + (f" → {short(b)}" if k == "array" and len(b) <= 12 else "")
        if k == "queue" and len(a) == len(b) + 1 and a[1:] == b:
            return f"{n} dequeues {short(a[0], 24)}"
        if len(a) == len(b):
            diff = [i for i in range(len(a)) if a[i] != b[i]]
            if 0 < len(diff) <= 2:
                return ", ".join(f"{code(f'{name}[{i}]')} {short(a[i], 12)} → {short(b[i], 16)}" for i in diff)
        return f"{n} becomes {short(b)}"
    if k == "matrix" and isinstance(a, list) and isinstance(b, list) and len(a) == len(b):
        diff = []
        for i, (ra, rb) in enumerate(zip(a, b)):
            if ra != rb and isinstance(ra, list) and isinstance(rb, list) and len(ra) == len(rb):
                diff += [(i, j) for j in range(len(ra)) if ra[j] != rb[j]]
            elif ra != rb:
                diff.append((i, None))
        if diff and len(diff) <= 3 and all(j is not None for _, j in diff):
            return ", ".join(f"{code(f'{name}[{i}][{j}]')} = {short(b[i][j], 16)}" for i, j in diff)
        return f"{n} changes"
    if k in ("map", "set") and isinstance(a, list) and isinstance(b, list):
        as_entry = lambda e: e if isinstance(e, list) and e else [e]
        da = {json.dumps(as_entry(e)[0]): as_entry(e) for e in a}
        db = {json.dumps(as_entry(e)[0]): as_entry(e) for e in b}
        parts = []
        for key, e in db.items():
            if key not in da: parts.append(f"{n} adds {short(e[0], 16)}" + (f" → {short(e[1], 16)}" if len(e) > 1 else ""))
            elif da[key] != e: parts.append(f"{code(name + '[' + fmt(e[0]) + ']')} = {short(e[1], 16)}")
        for key, e in da.items():
            if key not in db: parts.append(f"{n} removes {short(e[0], 16)}")
        return ", ".join(parts[:3]) + (" …" if len(parts) > 3 else "") if parts else None
    if k == "linkedlist": return "the links change"
    if k == "tree": return "the tree changes"
    return f"{n} changes"


ASSIGN = re.compile(r"^\s*(?:[\w<>\[\],. ]+\s+)?([A-Za-z_]\w*)\s*(?:\[[^=]*\])?\s*(?:[-+*/%&|^]|<<|>>>?)?=(?!=)")


def unchanged(s, line):
    """For a line that writes something without changing it: say which value stayed the same."""
    for d in s.get("auto_structs", s.get("structures", [])):
        for k, m in (d.get("marks") or {}).items():
            if m != "swap": continue
            if d.get("kind") == "array" and k.isdigit() and int(k) < len(d.get("values", [])):
                return f"{code(d['name'] + '[' + k + ']')} stays {short(d['values'][int(k)], 20)}."
            if d.get("kind") == "matrix" and "," in k:
                r, c = (int(x) for x in k.split(","))
                rows = d.get("rows", [])
                if r < len(rows) and isinstance(rows[r], list) and c < len(rows[r]):
                    return f"{code(d['name'] + '[' + str(r) + '][' + str(c) + ']')} stays {short(rows[r][c], 20)}."
    m = ASSIGN.match(after_header(line))
    if m and m.group(1) in s.get("vars", {}):
        return f"{code(m.group(1))} stays {short(s['vars'][m.group(1)], 20)}."
    f = re.match(r"\s*([A-Za-z_][\w.]*\.\w+)\s*=\s*([^=;][^;]*);", after_header(line))
    if f:   # a field write the view doesn't draw (e.g. a prev link)
        return f"{code(f.group(1))} now refers to {code(f.group(2).strip())}."
    return None


def after_header(line):
    """The statement part of a one-line `for (...) stmt;` / `if (...) stmt;`."""
    m = re.match(r"\s*(?:\}\s*else\s+)?(?:for|if|while)\s*\(", line)
    if not m: return line
    depth = 0
    for j in range(m.end() - 1, len(line)):
        if line[j] == "(": depth += 1
        elif line[j] == ")":
            depth -= 1
            if depth == 0: return after_header(line[j + 1:])
    return line


def moved_pointers(d, old):
    """Node variables (curr, prev, node, ...) that now point somewhere else."""
    val = {n.get("id"): n.get("value") for n in d.get("nodes", [])}
    a, b = old.get("pointers") or {}, d.get("pointers") or {}
    return [f"{code(k)} → node {short(val.get(v, '?'), 12)}" for k, v in b.items() if a.get(k) != v]


# ---------------------------------------------------------------- reading the code line
def cond_text(line):
    m = re.search(r"\b(if|while|for)\s*\(", line)
    if not m: return None
    i, depth = m.end() - 1, 0
    for j in range(i, len(line)):
        if line[j] == "(": depth += 1
        elif line[j] == ")":
            depth -= 1
            if depth == 0:
                inner = line[i + 1: j]
                if m.group(1) == "for":
                    parts = split_top(inner, ";")
                    if len(parts) == 3: inner = parts[1].strip()
                return inner.strip()
    return line[i + 1:].strip()


def split_top(s, sep):
    out, depth, cur = [], 0, ""
    for ch in s:
        if ch in "([{": depth += 1
        elif ch in ")]}": depth -= 1
        if ch == sep and depth == 0: out.append(cur); cur = ""
        else: cur += ch
    out.append(cur)
    return out


def foreach_var(line):
    m = re.search(r"for\s*\(\s*(?:final\s+)?[\w<>\[\],.? ]+?\s+(\w+)\s*:", line)
    return m.group(1) if m else None


def trim_line(line, n=72):
    s = line.strip()
    if s.endswith("{"): s = s[:-1].rstrip()
    return s if len(s) <= n else s[: n - 1] + "…"


# ---------------------------------------------------------------- main entry
def process(raw, calls, code_lines, which):
    """raw: steps with 'line' already mapped; calls: [[parent, label, full, result], ...]."""
    calls = calls or []
    steps = merge(raw)
    explain(steps, calls, code_lines)
    frames(steps, calls)
    steps, capped = trim(steps, *BUDGET[which])
    up = max([s.get("up", 0) for s in steps] + [0])
    for s in steps:
        for k in ("kind", "st", "up", "back", "cond", "ret", "hand", "auto"): s.pop(k, None)
    tree_calls = [[c[0], c[1], c[3]] for c in calls[:up]] if any(d.get("kind") == "calltree" for s in steps for d in s["structures"]) else None
    return steps, capped, tree_calls


def merge(raw):
    """Fold each hand-written step into the automatic step it narrates."""
    out, pending = [], None
    for s in raw:
        if s.get("hand"):
            if s.get("dir") == 0 and out and out[-1].get("auto") and not out[-1].get("narrated"):
                absorb(out[-1], s)
            elif s.get("dir") == 1 and pending is None:
                pending = s
            else:
                if pending is not None: out.append(standalone(pending)); pending = None
                out.append(standalone(s))
        else:
            s = dict(s, auto=True)
            if pending is not None and s.get("kind") != 11:
                absorb(s, pending); pending = None
            out.append(s)
    if pending is not None: out.append(standalone(pending))
    return out


def standalone(h):
    return dict(h, narrated=True)


def absorb(auto, hand):
    auto["narrated"] = True
    auto["auto_structs"] = auto.get("structures", [])
    auto["hand_title"], auto["hand_explain"] = hand["title"], hand["explain"]
    hs = hand.get("structures", [])
    names = {d.get("name") for d in hs}
    kinds = {d.get("kind") for d in hs}
    linked = {"tree", "linkedlist", "graph"}
    same = {(d.get("kind"), json.dumps(content(d))) for d in hs}
    keep = [d for d in auto.get("structures", []) if d.get("name") not in names and not (d.get("kind") in linked and kinds & linked)
            and (d.get("kind"), json.dumps(content(d))) not in same]
    auto["structures"] = hs + keep
    auto["vars"] = {**auto.get("vars", {}), **hand.get("vars", {})}


def explain(steps, calls, code_lines):
    # Variables are compared with the previous step of the same call (each call has its own locals);
    # structures with the previous step overall (they are usually shared, like a result list filled by deeper calls).
    last = {}   # frame node id -> vars at the previous step of that frame
    prev_structs = {}
    label = lambda i: calls[i][1] if 0 <= i < len(calls) else "?"
    for s in steps:
        st = s.get("st") or []
        top = st[-1] if st else -1
        if not s.get("auto"):
            last[top] = s.get("vars", {})
            prev_structs = {d.get("name"): d for d in s.get("structures", [])}
            continue
        line = code_lines[s["line"] - 1] if 1 <= s["line"] <= len(code_lines) else ""
        kind = s.get("kind", 0)
        pv, ps = last.get(top), prev_structs
        changes = []
        if pv is not None and kind != 11:
            for k, v in s.get("vars", {}).items():
                if k not in pv: changes.append(f"{code(k)} = {short(v, 24)}")
                elif pv[k] != v: changes.append(f"{code(k)} {short(pv[k], 16)} → {short(v, 24)}")
            for d in s.get("auto_structs", s.get("structures", [])):
                old = ps.get(d.get("name"))
                if old is not None and d.get("kind") in ("linkedlist", "tree"):
                    changes += moved_pointers(d, old)
                if old is None:
                    if d.get("kind") not in ("tree", "linkedlist") and s.get("back") is None: changes.append(f"{code(d.get('name'))} = {short(content(d) if content(d) is not None else '…')}")
                else:
                    c = describe_change(d, old)
                    if c: changes.append(c)
        last[top] = s.get("vars", {})
        prev_structs = {d.get("name"): d for d in s.pop("auto_structs", s.get("structures", []))}
        if kind == 5:   # the loop variable is already named in the text
            v = foreach_var(line)
            changes = [c for c in changes if not c.startswith(code(v) + " ")]
        title, text = auto_text(s, kind, line, top, st, label, calls)
        if changes: text = (text + " " if text else "") + "; ".join(changes[:5]) + ("; …" if len(changes) > 5 else "") + "."
        if not changes and kind == 0:
            stay = unchanged(s, line)
            if stay: text = (text + " " if text else "") + stay
        if not text: text = "Run this line. Nothing you can see changes."
        if s.get("narrated"):
            s["title"], s["explain"], s["detail"] = s.pop("hand_title"), s.pop("hand_explain"), text
        else:
            s["title"], s["explain"] = title, text


def auto_text(s, kind, line, top, st, label, calls):
    depth = len(st)
    me = label(top)
    if kind == 11:
        full = calls[top][2] if 0 <= top < len(calls) else me
        if depth <= 1: return f"Call {me}", f"Start {code(full)}."
        return f"Call {me}", f"{code(label(st[-2]))} calls {code(full)}. The call stack is now {depth} deep."
    if kind in COND_KINDS:
        c = cond_text(line) or trim_line(line)
        v = bool(s.get("cond"))
        res = "true" if v else "false"
        if kind == 1: tail = "so the `if` body runs." if v else "so the `if` body is skipped."
        elif kind == 2: tail = "so the `if` branch runs." if v else "so the `else` branch runs."
        else: tail = "so the loop body runs." if v else "so the loop ends."
        return f"{short(c, 56)} → {res}", f"{code(c)} is **{res}**, {tail}"
    if kind == 5:
        var = foreach_var(line)
        val = s.get("vars", {}).get(var) if var else None
        if var and var not in s.get("vars", {}):
            d = next((d for d in s.get("structures", []) if d.get("name") == var), None)
            if d is not None: val = content(d)
        return (f"Next item: {var} = {short(val, 20)}" if var else "Next item"), (f"The loop takes the next item: {code(var)} = {short(val, 30)}." if var else "The loop takes the next item.")
    if kind == 6:
        r = s.get("ret")
        if depth >= 2: return f"Return {short(r, 30)}", f"{code(me)} returns {code(short(r, 60))} to {code(label(st[-2]))}."
        return f"Return {short(r, 30)}", f"{code(me)} finishes and returns {code(short(r, 60))}."
    if kind == 7: return "Return", f"{code(me)} finishes."
    if kind == 8: return "continue", "`continue` skips the rest of this iteration and goes to the next one."
    if kind == 9: return "break", "`break` leaves the loop."
    if kind == 10: return "throw", "The code throws an exception here."
    text = ""
    if s.get("back") is not None:
        b = s["back"]
        r = calls[b][3] if 0 <= b < len(calls) else None
        text = f"Back from {code(label(b))}" + (f", which returned {code(short(r, 40))}." if r is not None else ".")
    return trim_line(line), text


def frames(steps, calls):
    """Add the call stack (when calls nest) and the recursion tree (when a method calls itself)."""
    if not calls: return
    name = lambda i: calls[i][1].split("(")[0]
    recursive = any(0 <= c[0] < len(calls) and name(c[0]) == c[1].split("(")[0] for c in calls)
    deep = max([len(s.get("st") or []) for s in steps] + [0]) >= 2
    if not (recursive or deep): return
    for s in steps:
        st = s.get("st") or []
        if not st: continue
        extra = []
        if recursive: extra.append({"kind": "calltree", "name": "recursion tree", "up": s.get("up", 0), "path": st})
        if deep: extra.append({"kind": "stack", "name": "call stack", "values": [calls[i][2] for i in st if 0 <= i < len(calls)]})
        s["structures"] = (extra[:1] if recursive else []) + s.get("structures", []) + (extra[1:] if recursive else extra)


def trim(steps, head, tail):
    if len(steps) <= head + tail + 1: return steps, False
    skipped = len(steps) - head - tail
    marker = dict(steps[head])
    marker["title"] = f"… {skipped} more steps skipped"
    marker["explain"] = "The run keeps going the same way. This is where it was; the next step jumps ahead to the end of the run."
    marker.pop("detail", None)
    return steps[:head] + [marker] + steps[-tail:], True


def encode(steps):
    """Replace structures/vars equal to the previous step's with references (expanded by the page)."""
    prev_s, prev_v = {}, None
    out = []
    for s in steps:
        s = dict(s)
        cur = {}
        enc = []
        for d in s.get("structures", []):
            key = json.dumps(d, sort_keys=True, ensure_ascii=False)
            cur[d.get("name")] = key
            enc.append({"$": d.get("name")} if prev_s.get(d.get("name")) == key else d)
        if s.get("vars") == prev_v and prev_v is not None: s.pop("vars")
        else: prev_v = s.get("vars")
        s["structures"] = enc
        prev_s = cur
        out.append(s)
    return out
