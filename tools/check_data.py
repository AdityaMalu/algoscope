"""Sanity-check the pre-computed traces in data/ before they ship.

Checks every problem in data/index.json: required fields, step lines inside the shown code,
known structure kinds, alt-language line maps, and edge-case traces.

Usage: python tools/check_data.py   (exit code 1 if anything is wrong)
"""
import json, os, sys

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
DATA = os.path.join(root, "data")
KINDS = {"array", "string", "text", "list", "matrix", "grid", "table", "dp", "linkedlist", "linked_list",
         "tree", "bst", "trie", "heap", "graph", "stack", "callstack", "call_stack", "queue", "deque",
         "map", "hashmap", "dict", "set", "hashset", "object", "calltree"}
FIELDS = {"id": str, "title": str, "level": str, "topic": str, "code": str, "summary": str,
          "complexity": dict, "intuition": list, "pitfalls": list, "steps": list, "edgeCases": list}
errors = []


def err(where, msg):
    errors.append(f"{where}: {msg}")


def check_steps(where, steps, nlines, calls=None):
    if not steps:
        err(where, "no steps"); return
    prev = {}
    for i, s in enumerate(steps):
        cur = {}
        if isinstance(s, dict):
            full = []
            for d in s.get("structures", []):
                if isinstance(d, dict) and "$" in d:
                    if d["$"] not in prev:
                        err(f"{where} step {i + 1}", f"refers to {d['$']!r}, which the previous step doesn't have"); continue
                    d = prev[d["$"]]
                full.append(d)
                if isinstance(d, dict): cur[d.get("name")] = d
                if isinstance(d, dict) and d.get("kind") == "calltree" and not 0 <= d.get("up", -1) <= len(calls or []):
                    err(f"{where} step {i + 1}", "recursion tree refers past the recorded calls")
            s = dict(s, structures=full)
        prev = cur
        w = f"{where} step {i + 1}"
        if not isinstance(s, dict):
            err(w, "not an object"); continue
        line = s.get("line")
        if not isinstance(line, int) or not 0 <= line <= nlines:
            err(w, f"line {line!r} outside 0..{nlines}")
        if not isinstance(s.get("vars", {}), dict):
            err(w, "vars is not an object")
        for d in s.get("structures", []):
            if not isinstance(d, dict) or str(d.get("kind", "")).lower() not in KINDS:
                err(w, f"unknown structure {str(d)[:60]}")


def main():
    index = json.load(open(os.path.join(DATA, "index.json"), encoding="utf-8"))
    files, ids = {}, set()
    for e in index:
        if e["id"] in ids:
            err(e["id"], "duplicate id")
        ids.add(e["id"])
        if not e.get("file"):
            err(e["id"], "no trace file"); continue
        if e["file"] not in files:
            path = os.path.join(DATA, e["file"])
            if not os.path.exists(path):
                err(e["id"], f"missing {e['file']}"); continue
            files[e["file"]] = {p["id"]: p for p in json.load(open(path, encoding="utf-8"))}
        p = files[e["file"]].get(e["id"])
        if not p:
            err(e["id"], f"not in {e['file']}"); continue
        for k, t in FIELDS.items():
            if not isinstance(p.get(k), t):
                err(e["id"], f"field {k} missing or not {t.__name__}")
        if not isinstance(p.get("code"), str):
            continue
        n = len(p["code"].split("\n"))
        check_steps(e["id"], p.get("steps") or [], n, p.get("calls"))
        if e.get("steps") != len(p.get("steps") or []):
            err(e["id"], "index step count is stale")
        for j, ec in enumerate(p.get("edgeCases") or []):
            if ec.get("steps"):
                check_steps(f"{e['id']} edge {j + 1}", ec["steps"], n, ec.get("calls"))
        for lang, alt in (p.get("alt") or {}).items():
            m = len(alt["code"].split("\n"))
            for jl, al in alt["lines"].items():
                if not 1 <= int(jl) <= n or not 1 <= al <= m:
                    err(f"{e['id']} {lang}", f"line map {jl}->{al} out of range")
    for x in errors:
        print(x)
    print(f"checked {len(index)} problems in {len(files)} files: {len(errors)} problem(s)")
    sys.exit(1 if errors else 0)


if __name__ == "__main__":
    main()
