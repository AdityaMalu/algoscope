"""Run the Python and C++ solutions on the same inputs as the Java traces and compare outputs.

Usage: python tools/verify.py [topic-file-stem ...]   e.g. hashing   (no args = all)
Reads build/out.json (written by build.py) for the Java inputs and outputs.
"""
import ast, importlib.util, json, os, re, subprocess, sys

sys.stdout.reconfigure(encoding="utf-8")
ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PY, CPP, BUILD = (os.path.join(ROOT, *p) for p in (("alt", "py"), ("alt", "cpp"), ("build",)))
sys.path.insert(0, PY)
sys.path.insert(0, os.path.join(ROOT, "alt"))
from spec import SPEC  # noqa: E402
import common  # noqa: E402


# ---------- input parsing ----------
def split_top(s):
    segs, depth, q, start = [], 0, None, 0
    for i, ch in enumerate(s):
        if q:
            if ch == q and s[i - 1] != "\\": q = None
            continue
        if ch in "\"'": q = ch
        elif ch in "[({": depth += 1
        elif ch in "])}": depth -= 1
        elif ch == "," and depth == 0 and re.match(r"\s*[A-Za-z_]\w*\s*=(?!=)", s[i + 1:]):
            segs.append(s[start:i]); start = i + 1
    segs.append(s[start:])
    return segs


def lit(v):
    v = v.strip()
    parts = re.split(r'("(?:[^"\\]|\\.)*")', v)
    for i in range(0, len(parts), 2):
        p = parts[i]
        p = re.sub(r"(?<![\w@.#\-])([A-Za-z@#.][\w@.#]*)", lambda m: {"null": "None", "true": "True", "false": "False"}.get(m.group(1), '"' + m.group(1) + '"'), p)
        parts[i] = p
    return ast.literal_eval("".join(parts))


def parse(s, names):
    d = {}
    for seg in split_top(s):
        m = re.match(r"\s*([A-Za-z_]\w*)\s*=\s*(.*)$", seg, re.S)
        if not m: raise ValueError("bad segment " + seg)
        d[m.group(1)] = lit(m.group(2))
    return [d[n] for n in names]


# ---------- conversions ----------
def to_py(t, v):
    if t == "vc": return [str(x) for x in v]
    if t == "vvc": return [[str(x) for x in r] for r in v]
    if t == "list": return common.mk_list(v)
    if t == "tree": return common.mk_tree(v)
    if t == "vlist": return [common.mk_list(x) for x in v]
    if t == "double": return float(v)
    if t in ("vi", "vvi", "vs", "vvs"): return json.loads(json.dumps(v))
    return v


def cstr(s): return json.dumps(str(s), ensure_ascii=False)


def to_cpp(t, v):
    if t in ("int",): return str(v)
    if t == "long": return f"{v}LL"
    if t == "double": return repr(float(v))
    if t == "bool": return "true" if v else "false"
    if t == "str": return f"string({cstr(v)})"
    if t == "vi": return "vector<int>{" + ",".join(map(str, v)) + "}"
    if t == "vvi": return "vector<vector<int>>{" + ",".join("{" + ",".join(map(str, r)) + "}" for r in v) + "}"
    if t == "vs": return "vector<string>{" + ",".join(cstr(x) for x in v) + "}"
    if t == "vvs": return "vector<vector<string>>{" + ",".join("{" + ",".join(cstr(x) for x in r) + "}" for r in v) + "}"
    if t == "vc": return "vector<char>{" + ",".join("'" + str(x) + "'" for x in v) + "}"
    if t == "vvc": return "vector<vector<char>>{" + ",".join("{" + ",".join("'" + str(x) + "'" for x in r) + "}" for r in v) + "}"
    if t == "rl": return "vector<pair<int,int>>{" + ",".join("{%d,%d}" % (x, -1 if r is None else r) for x, r in v) + "}"
    if t == "list": return "mkList({" + ",".join(map(str, v)) + "})"
    if t == "vlist": return "vector<ListNode*>{" + ",".join("mkList({" + ",".join(map(str, x)) + "})" for x in v) + "}"
    if t == "tree": return "mkTree({" + ",".join('"null"' if x is None else f'"{x}"' for x in v) + "})"
    raise ValueError(t)


def norm(s, unordered):
    s = re.sub(r"\s+", "", s)
    if unordered:
        try:
            o = json.loads(s)
            if isinstance(o, list): return json.dumps(sorted(o, key=lambda x: json.dumps(x)))
        except Exception:
            pass
    return s


def same(java, other, unordered):
    if other == "null" and java == "[]": other = "[]"
    return norm(java, unordered) == norm(other, unordered)


def where(marker):
    for d, pat, ext in ((PY, "#@code ", ".py"), (CPP, "//@code ", ".cpp")):
        pass
    res = {}
    for d, pat, ext, key in ((PY, "#@code ", ".py", "py"), (CPP, "//@code ", ".cpp", "cpp")):
        for f in os.listdir(d):
            if f.endswith(ext) and (pat + marker + "\n") in open(os.path.join(d, f), encoding="utf-8").read():
                res[key] = f[: -len(ext)]
    return res


def main():
    only = set(sys.argv[1:])
    probs = json.load(open(os.path.join(BUILD, "out.json"), encoding="utf-8"))
    by_marker = {p["marker"]: p for p in probs}
    jobs = {}  # topic stem -> list of (marker, idx, name, expected, spec, args)
    report = {}
    skipped = []
    for marker, p in by_marker.items():
        loc = where(marker)
        if not loc: continue
        stem = loc.get("py") or loc.get("cpp")
        if only and stem not in only: continue
        spec = SPEC.get(marker)
        if spec is None:
            report[marker] = ["no spec"]; continue
        cases = [("main", p["input"], p["output"])] + [(e["name"], e["input"], e["actual"]) for e in p["edgeCases"]]
        lst = jobs.setdefault(stem, [])
        for idx, (name, inp, out) in enumerate(cases):
            if out.startswith("throws") or idx in spec.get("skip", ()):
                skipped.append(f"{marker}#{idx}"); continue
            if idx in spec.get("custom", {}):
                lst.append((marker, idx, name, out, spec, ("custom", spec["custom"][idx])))
                continue
            if not spec["names"]:
                skipped.append(f"{marker}#{idx}"); continue
            try:
                args = parse(inp, spec["names"])
            except Exception:
                skipped.append(f"{marker}#{idx}"); continue
            lst.append((marker, idx, name, out, spec, ("args", args)))

    os.makedirs(BUILD, exist_ok=True)
    totals = {"py": [0, 0], "cpp": [0, 0]}
    fails = []
    for stem, tests in sorted(jobs.items()):
        # ---- Python
        mod = None
        pyfile = os.path.join(PY, stem + ".py")
        if os.path.exists(pyfile):
            spec_ = importlib.util.spec_from_file_location(stem, pyfile)
            mod = importlib.util.module_from_spec(spec_); spec_.loader.exec_module(mod)
        # ---- C++ harness
        cppfile = os.path.join(CPP, stem + ".cpp")
        cpp_out = {}
        if os.path.exists(cppfile):
            lines = ['#include "../alt/cpp/common.h"', f'#include "../alt/cpp/{stem}.cpp"', "int main() {", "    ios::sync_with_stdio(false);"]
            for k, (marker, idx, name, out, spec, (kind, data)) in enumerate(tests):
                lines.append("    {")
                if kind == "custom":
                    lines.append("        " + data["cpp"])
                    lines.append(f'        cout << "{k}|" << fmt(R) << "\\n";')
                else:
                    for i, (t, v) in enumerate(zip(spec["args"], data)):
                        lines.append(f"        auto a{i} = {to_cpp(t, v)};")
                    call = spec.get("cpp_expr") or f"{spec.get('cpp', marker)}({', '.join(f'a{i}' for i in range(len(data)))})"
                    if "inplace" in spec:
                        lines.append(f"        {call};")
                        lines.append(f'        cout << "{k}|" << fmt(a{spec["inplace"]}) << "\\n";')
                    else:
                        post = spec.get("cpp_post", "R")
                        lines.append(f"        auto R = {call};")
                        lines.append(f'        cout << "{k}|" << fmt({post}) << "\\n";')
                lines.append("    }")
            lines.append("}")
            src = os.path.join(BUILD, f"verify_{stem}.cpp")
            open(src, "w", encoding="utf-8").write("\n".join(lines))
            exe = os.path.join(BUILD, f"verify_{stem}.exe")
            r = subprocess.run(["g++", "-std=c++17", "-O1", "-w", "-o", exe, src], capture_output=True, text=True)
            if r.returncode:
                print(f"C++ COMPILE ERROR in {stem}:\n{r.stderr[:3000]}")
            else:
                r = subprocess.run([exe], capture_output=True, text=True, encoding="utf-8", timeout=60)
                for line in r.stdout.splitlines():
                    k, _, val = line.partition("|")
                    cpp_out[int(k)] = val
                if r.returncode: print(f"C++ RUNTIME exit {r.returncode} in {stem} after {len(cpp_out)} tests")
        for k, (marker, idx, name, out, spec, (kind, data)) in enumerate(tests):
            uo = spec.get("unordered", False)
            if mod is not None and hasattr(mod, "__file__"):
                try:
                    if kind == "custom":
                        env = {**vars(mod)}
                        exec(data["py"], env)
                        got = common.fmt(env["R"])
                    else:
                        args = [to_py(t, v) for t, v in zip(spec["args"], data)]
                        if "py_expr" in spec:
                            res = eval(spec["py_expr"], {**vars(mod), **{f"a{i}": v for i, v in enumerate(args)}})
                        else:
                            res = getattr(mod, spec.get("py", marker))(*args)
                        if "inplace" in spec: res = args[spec["inplace"]]
                        if "py_post" in spec: res = eval(spec["py_post"], {**vars(mod), "R": res})
                        got = common.fmt(res)
                    ok = same(out, got, uo)
                except Exception as ex:
                    got, ok = f"EXC {type(ex).__name__}: {ex}", False
                totals["py"][0 if ok else 1] += 1
                if not ok: fails.append(f"PY  {marker}#{idx} {name}: java={out} py={got}")
            if os.path.exists(cppfile):
                got = cpp_out.get(k, "MISSING")
                ok = same(out, got, uo)
                totals["cpp"][0 if ok else 1] += 1
                if not ok: fails.append(f"CPP {marker}#{idx} {name}: java={out} cpp={got}")
    for f in fails: print(f)
    for m, r in report.items(): print("NOTE", m, r)
    print("not checked (no parsable input, Java threw, or answer is not unique):", len(skipped), " ".join(skipped))
    print(f"Python: {totals['py'][0]} passed, {totals['py'][1]} failed | C++: {totals['cpp'][0]} passed, {totals['cpp'][1]} failed")
    if totals["py"][1] or totals["cpp"][1]: sys.exit(1)


if __name__ == "__main__":
    main()
