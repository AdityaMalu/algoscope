"""Record a line-by-line trace of real Python code for AlgoScope.

Runs inside Pyodide (Python compiled to WebAssembly) in a Web Worker, so pasted Python is actually
executed in the viewer's browser instead of being imagined by an AI model. It also runs under plain
CPython, which is how tests/test_pytrace.py checks it.

Entry point: run_job(json_text) -> json_text
  job    = {"code": str, "input": str, "edges": [str, ...] | null, "autoEdges": bool}
  result = {"steps", "output", "error", "stdout", "capped", "input", "inputGuessed", "title", "edgeCases"}
Each step has the same shape as the pre-computed traces: {line, title, explain, vars, structures}.
"""
import ast, collections, io, json, math, re, sys, types

MAX_EVENTS = 200000         # trace events per run before giving up (infinite loops, huge inputs)
HEAD, TAIL = 300, 100       # steps kept from the start and the end of a long run
MAX_ITEMS = 80              # elements shown per container
POINTERS = {"i", "j", "l", "r", "lo", "hi", "low", "high", "left", "right", "mid", "start", "end", "slow", "fast",
            "p", "p1", "p2", "ptr", "idx", "index", "pos", "cur", "curr", "write", "read", "w", "top", "front", "back",
            "begin", "stop", "a", "b"}
STACKS = {"stack", "st", "stk", "s_stack", "mono", "monostack"}
HEAPS = {"heap", "pq", "minheap", "maxheap", "min_heap", "max_heap", "h"}
LIST_PARAMS = {"head", "l1", "l2", "list1", "list2", "head1", "head2", "heada", "headb"}
TREE_PARAMS = {"root", "root1", "root2", "subroot", "t", "t1", "t2", "tree"}

PRELUDE = """
from typing import *
import collections, heapq, bisect, math, itertools, functools, string, re, random
from collections import defaultdict, deque, Counter, OrderedDict
from heapq import heappush, heappop, heapify, heappushpop, nlargest, nsmallest
from functools import lru_cache, cache, reduce
from math import inf
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next
class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right
"""


class Abort(Exception):
    pass


class InputError(Exception):
    pass


# ---------- value conversion ----------

def is_list_node(v):
    return hasattr(v, "next") and (hasattr(v, "val") or hasattr(v, "value")) and not isinstance(v, (type, types.ModuleType))


def is_tree_node(v):
    return hasattr(v, "left") and hasattr(v, "right") and not isinstance(v, (type, types.ModuleType))


def node_val(v):
    return getattr(v, "val", getattr(v, "value", None))


def scalar(v):
    return v is None or isinstance(v, (bool, int, float, str))


def conv(v, depth=0):
    """A JSON-safe copy of a Python value, trimmed for display."""
    if v is None or isinstance(v, bool):
        return v
    if isinstance(v, int):
        return v if abs(v) < 2 ** 53 else str(v)
    if isinstance(v, float):
        return v if math.isfinite(v) else ("inf" if v > 0 else "-inf" if v < 0 else "nan")
    if isinstance(v, str):
        return v if len(v) <= 200 else v[:197] + "…"
    if is_list_node(v) or is_tree_node(v):
        return f"node({conv(node_val(v), depth + 1)})"
    if depth > 3:
        return "…"
    if isinstance(v, dict):
        out = {}
        for i, (k, x) in enumerate(v.items()):
            if i >= MAX_ITEMS:
                out["…"] = f"{len(v) - MAX_ITEMS} more"
                break
            out[key_str(k)] = conv(x, depth + 1)
        return out
    if isinstance(v, (set, frozenset)):
        return [conv(x, depth + 1) for x in sorted_safe(v)[:MAX_ITEMS]]
    if isinstance(v, (list, tuple, collections.deque)):
        items = [conv(x, depth + 1) for x in list(v)[:MAX_ITEMS]]
        return items + ["…"] if len(v) > MAX_ITEMS else items
    r = repr(v)
    return r if len(r) <= 120 else r[:117] + "…"


def key_str(k):
    if isinstance(k, str):
        return k
    c = conv(k, 3)
    return c if isinstance(c, str) else json.dumps(c)


def sorted_safe(xs):
    try:
        return sorted(xs)
    except TypeError:
        return sorted(xs, key=repr)


def short(v, n=40):
    s = json.dumps(conv(v), ensure_ascii=False) if not isinstance(conv(v), str) else conv(v)
    return s if len(s) <= n else s[: n - 1] + "…"


def to_output(v):
    """The function result as text: linked lists and trees become lists, like LeetCode prints them."""
    if is_list_node(v):
        vals, seen = [], set()
        while v is not None and id(v) not in seen and len(vals) < 200:
            seen.add(id(v)); vals.append(node_val(v)); v = v.next
        return json.dumps(conv(vals))
    if is_tree_node(v):
        out, q = [], [v]
        while q and len(out) < 400:
            n = q.pop(0)
            if n is None:
                out.append(None); continue
            out.append(node_val(n)); q.append(n.left); q.append(n.right)
        while out and out[-1] is None:
            out.pop()
        return json.dumps(conv(out))
    c = conv(v)
    return json.dumps(c, ensure_ascii=False) if not isinstance(c, str) or isinstance(v, str) else c


# ---------- recorder ----------

class Recorder:
    """Turns trace events into steps. Each run happens twice: first only counting steps (known=None),
    then recording the first HEAD and the last TAIL of them, so long runs stay fast and small."""
    def __init__(self, code, known=None):
        self.known = known
        self.lines = code.split("\n")
        self.head, self.tail, self.total = [], [], 0
        self.events = 0
        self.stack = []          # frames of the user's code, outermost first
        self.last = {}           # frame id -> line that is about to run
        self.prev = {}           # structure name -> values at the previous step (for change marks)
        self.prev_vars = {}
        self.ids = {}            # id(node) -> stable display id
        self.keep = []           # keep nodes alive so ids aren't reused
        self.roots = []          # list heads / tree roots seen in the outermost call

    # sys.settrace callback
    def trace(self, frame, event, arg):
        if frame.f_code.co_filename != "<user>":
            return None
        self.events += 1
        if self.events > MAX_EVENTS:
            raise Abort()
        fid = id(frame)
        if event == "call":
            self.stack.append(frame)
            self.last[fid] = None
            if len(self.stack) == 1:
                self.roots = [v for v in frame.f_locals.values() if is_list_node(v) or is_tree_node(v)]
            args = ", ".join(f"{k}={short(frame.f_locals.get(k), 24)}" for k in frame.f_code.co_varnames[: frame.f_code.co_argcount] if k != "self")
            name = frame.f_code.co_name
            self.emit(frame, frame.f_code.co_firstlineno, f"Call {name}({args})" if name != "<module>" else "Start",
                      "Enter `" + name + "`." if name != "<module>" else "Run the script from the top.")
        elif event == "line":
            prev = self.last.get(fid)
            if prev is not None:
                self.emit(frame, prev)
            self.last[fid] = frame.f_lineno
        elif event == "return":
            prev = self.last.get(fid) or frame.f_lineno
            name = frame.f_code.co_name
            if name == "<module>":
                self.emit(frame, prev)
            else:
                self.emit(frame, prev, f"Return {short(arg, 50)}" if arg is not None else f"{name} finishes",
                          f"`{name}` returns `{short(arg, 60)}`." if arg is not None else f"`{name}` returns.", ret=arg)
            if self.stack and self.stack[-1] is frame:
                self.stack.pop()
            self.last.pop(fid, None)
        return self.trace

    def node_id(self, n):
        k = id(n)
        if k not in self.ids:
            self.ids[k] = "n" + str(len(self.ids) + 1)
            self.keep.append(n)
        return self.ids[k]

    def emit(self, frame, line, title=None, explain=None, ret=None):
        self.total += 1
        if self.known is None:
            return
        if self.total <= HEAD:
            self.head.append(self.snapshot(frame, line, title, explain, ret))
        elif self.total > self.known - TAIL:
            self.tail.append(self.snapshot(frame, line, title, explain, ret))

    def steps(self):
        if self.total <= HEAD + TAIL:
            return self.head + self.tail, False
        skipped = self.total - HEAD - len(self.tail)
        gap = {"line": 0, "title": f"… {skipped} steps skipped …", "explain": "This run is long, so the middle is skipped. The steps below are the end of the run.",
               "vars": {}, "structures": []}
        return self.head + [gap] + self.tail, True

    # ----- one step -----
    def snapshot(self, frame, line, title, explain, ret):
        loc = {}
        for k, v in list(frame.f_locals.items()):
            if k.startswith("__") or isinstance(v, (types.ModuleType, types.FunctionType, types.BuiltinFunctionType, types.MethodType, type)):
                continue
            if k == "self" and hasattr(v, "__dict__"):
                for a, x in vars(v).items():
                    if not a.startswith("__"):
                        loc["self." + a] = x
                continue
            loc[k] = v
        structures, drawn = [], set()
        ints = {k: v for k, v in loc.items() if isinstance(v, int) and not isinstance(v, bool)}

        def ptrs_for(n):
            return {k: v for k, v in ints.items() if k.split(".")[-1].lower() in POINTERS and -1 <= v <= n}

        # linked lists and trees: one drawing each, with every variable that points into it
        list_vars = [(k, v) for k, v in loc.items() if is_list_node(v) and not is_tree_node(v)]
        tree_vars = [(k, v) for k, v in loc.items() if is_tree_node(v)]
        for vs, build in ((list_vars, self.list_structure), (tree_vars, self.tree_structure)):
            if vs:
                d = build(vs)
                self.mark_nodes(d)
                structures.append(d)
                drawn.update(k for k, _ in vs)

        for k, v in loc.items():
            if k in drawn:
                continue
            base = k.split(".")[-1].lower()
            d = None
            if isinstance(v, (list, tuple)) and all(scalar(x) for x in v):
                vals = [conv(x) for x in v[:MAX_ITEMS]]
                if base in STACKS or base.endswith("stack"):
                    d = {"kind": "stack", "name": k, "values": vals}
                elif base in HEAPS and isinstance(v, list) and is_heap(v):
                    d = {"kind": "heap", "name": k, "values": vals}
                else:
                    d = {"kind": "array", "name": k, "values": vals, "pointers": ptrs_for(len(v))}
            elif isinstance(v, (list, tuple)) and v and all(isinstance(r, (list, tuple)) and all(scalar(x) for x in r) for r in v):
                rows = [[conv(x) for x in r[:30]] for r in v[:30]]
                kind = "dp" if base in ("dp", "memo", "table", "cache") else "grid" if base in ("grid", "board", "maze") else "matrix"
                d = {"kind": kind, "name": k, "rows": rows}
            elif isinstance(v, str) and 1 < len(v) <= 60 and ptrs_for(len(v)) and not k.startswith("self."):
                d = {"kind": "string", "name": k, "values": list(v), "pointers": ptrs_for(len(v))}
            elif isinstance(v, dict):
                d = {"kind": "map", "name": k, "entries": [[key_str(a), conv(b, 1)] for a, b in list(v.items())[:MAX_ITEMS]]}
            elif isinstance(v, (set, frozenset)):
                d = {"kind": "set", "name": k, "entries": [[conv(x, 1)] for x in sorted_safe(v)[:MAX_ITEMS]]}
            elif isinstance(v, collections.deque):
                d = {"kind": "queue", "name": k, "values": [conv(x, 1) for x in list(v)[:MAX_ITEMS]]}
            if d is not None:
                self.mark_changes(d)
                structures.append(d)
                drawn.add(k)

        if len(self.stack) > 1:
            structures.append({"kind": "callstack", "name": "calls", "values": [self.call_label(f) for f in self.stack]})

        vars_ = {k: conv(v) for k, v in loc.items() if k not in drawn}
        if ret is not None:
            vars_["return"] = conv(ret)
        changed = [k for k, v in vars_.items() if k in self.prev_vars and self.prev_vars[k] != v or k not in self.prev_vars]
        self.prev_vars = vars_
        src = self.lines[line - 1].strip() if 0 < line <= len(self.lines) else ""
        if explain is None:
            bits = [f"`{k}` = `{short(vars_[k], 30)}`" for k in changed[:4]]
            bits += [f"`{d['name']}` changed" for d in structures if d.get("marks") and d["kind"] != "callstack"][:3]
            if bits:
                explain = "Now " + ", ".join(bits) + "."
            elif re.match(r"(if|elif|while)", src):
                explain = "The condition is checked. No variable changed."
            else:
                explain = "No variable changed on this line."
        return {"line": line, "title": title or (src if len(src) <= 70 else src[:67] + "…"), "explain": explain,
                "vars": vars_, "structures": structures}

    def mark_changes(self, d):
        key = d["kind"] + ":" + d["name"]
        if "values" in d:
            cur = d["values"]
            old = self.prev.get(key)
            if isinstance(old, list):
                marks = {str(i): "swap" for i, x in enumerate(cur) if i >= len(old) or old[i] != x}
                if marks and len(marks) < max(2, len(cur)):
                    d["marks"] = marks
            self.prev[key] = cur
        elif "entries" in d:
            cur = {json.dumps(e[0]): e[1:] for e in d["entries"]}
            old = self.prev.get(key)
            if isinstance(old, dict):
                marks = {str(e[0]) if not isinstance(e[0], str) else e[0]: "swap" for e in d["entries"] if json.dumps(e[0]) not in old or old[json.dumps(e[0])] != e[1:]}
                if marks and len(marks) < max(2, len(cur)):
                    d["marks"] = marks
            self.prev[key] = cur
        elif "rows" in d:
            old = self.prev.get(key)
            if isinstance(old, list):
                marks = {f"{i},{j}": "swap" for i, r in enumerate(d["rows"]) for j, x in enumerate(r)
                         if i >= len(old) or j >= len(old[i]) or old[i][j] != x}
                if marks and len(marks) < 12:
                    d["marks"] = marks
            self.prev[key] = d["rows"]

    def mark_nodes(self, d):
        """Mark list/tree nodes whose value or links changed since the previous step."""
        key = d["kind"] + ":nodes"
        cur = {n["id"]: (n.get("value"), n.get("next"), n.get("left"), n.get("right")) for n in d["nodes"]}
        old = self.prev.get(key)
        if isinstance(old, dict):
            marks = {i: "swap" for i, x in cur.items() if i in old and old[i] != x}
            if marks:
                d["marks"] = marks
        self.prev[key] = cur

    def call_label(self, f):
        args = ", ".join(short(f.f_locals.get(k), 12) for k in f.f_code.co_varnames[: f.f_code.co_argcount] if k != "self")
        return f"{f.f_code.co_name}({args})" if f.f_code.co_name != "<module>" else "script"

    def walk(self, starts, kids, limit=60):
        order, seen = [], set()
        todo = list(starts)
        while todo and len(order) < limit:
            n = todo.pop(0)
            if n is None or id(n) in seen:
                continue
            seen.add(id(n)); order.append(n)
            todo.extend(kids(n))
        return order

    def list_structure(self, list_vars):
        starts = [r for r in self.roots if is_list_node(r)] + [v for _, v in list_vars]
        nodes = self.walk(starts, lambda n: [n.next] if is_list_node(n.next) else [])
        return {"kind": "linkedlist", "name": list_vars[0][0] if len(list_vars) == 1 else "list",
                "nodes": [{"id": self.node_id(n), "value": conv(node_val(n), 1),
                           "next": self.node_id(n.next) if is_list_node(n.next) else None} for n in nodes],
                "pointers": {k: self.node_id(v) for k, v in list_vars}}

    def tree_structure(self, tree_vars):
        starts = [r for r in self.roots if is_tree_node(r)] + [v for _, v in tree_vars]
        nodes = self.walk(starts, lambda n: [c for c in (n.left, n.right) if is_tree_node(c)], 80)
        root = starts[0]
        return {"kind": "tree", "name": "tree", "root": self.node_id(root),
                "nodes": [{"id": self.node_id(n), "value": conv(node_val(n), 1),
                           "left": self.node_id(n.left) if is_tree_node(n.left) else None,
                           "right": self.node_id(n.right) if is_tree_node(n.right) else None} for n in nodes],
                "pointers": {k: self.node_id(v) for k, v in tree_vars}}


def is_heap(a):
    try:
        return all(a[(i - 1) // 2] <= a[i] for i in range(1, len(a)))
    except TypeError:
        return False


# ---------- choosing what to call and with what ----------

def entry_point(tree, g):
    """(callable, ast.FunctionDef) for the function the input should be passed to, or (None, None)."""
    funcs = [n for n in tree.body if isinstance(n, (ast.FunctionDef, ast.AsyncFunctionDef))]
    sol = next((n for n in tree.body if isinstance(n, ast.ClassDef) and n.name == "Solution"), None)
    if sol:
        methods = [m for m in sol.body if isinstance(m, ast.FunctionDef) and not m.name.startswith("_")]
        if methods:
            m = main_of(methods)
            return getattr(g["Solution"](), m.name), m
    if funcs:
        f = main_of(funcs)
        return g[f.name], f
    return None, None


def main_of(fns):
    """The function no other function calls (the others are helpers); the last such one wins."""
    called = set()
    for f in fns:
        for n in ast.walk(f):
            if isinstance(n, ast.Call):
                fn = n.func
                called.add(fn.attr if isinstance(fn, ast.Attribute) else getattr(fn, "id", None))
    top = [f for f in fns if f.name not in called] or fns
    return top[-1]


def params_of(fn):
    a = fn.args
    names = [x.arg for x in a.posonlyargs + a.args]
    if names and names[0] in ("self", "cls"):
        names = names[1:]
    ann = {x.arg: ast.unparse(x.annotation) if x.annotation is not None else "" for x in a.posonlyargs + a.args}
    return names, ann


def is_script(tree):
    """True when the code does something at the top level (calls, loops, prints), not just definitions."""
    for n in tree.body:
        if isinstance(n, (ast.FunctionDef, ast.AsyncFunctionDef, ast.ClassDef, ast.Import, ast.ImportFrom)):
            continue
        if isinstance(n, ast.Expr) and isinstance(n.value, ast.Constant):
            continue
        if isinstance(n, ast.Assign) and isinstance(n.value, ast.Constant):
            continue
        return True
    return False


def parse_input(text, g):
    """('call', expr) | ('kw', dict) | ('pos', list) from the input box text."""
    text = text.strip()
    try:
        node = ast.parse(text, mode="eval").body
        if isinstance(node, ast.Call) and not isinstance(node.func, ast.Constant):
            return "call", text
    except SyntaxError:
        pass
    text = re.sub(r"\s*\n\s*", ", ", text).strip(", ")
    env = dict(g)
    env.update({"null": None, "true": True, "false": False, "__kw": lambda **k: k, "__pos": lambda *a: list(a)})
    try:
        return "kw", eval("__kw(" + text + ")", env)
    except Exception:
        pass
    try:
        return "pos", eval("__pos(" + text + ")", env)
    except Exception as e:
        raise InputError(f"Couldn't read the input `{text}`. Write it like `nums = [2, 7, 11, 15], target = 9` or `[2, 7, 11, 15], 9`.") from e


def build_list(vals, g):
    head = None
    for v in reversed(vals):
        head = g["ListNode"](v, head)
    return head


def build_tree(vals, g):
    if not vals or vals[0] is None:
        return None
    T = g["TreeNode"]
    root = T(vals[0]); q = [root]; i = 1
    while q and i < len(vals):
        n = q.pop(0)
        if i < len(vals) and vals[i] is not None:
            n.left = T(vals[i]); q.append(n.left)
        i += 1
        if i < len(vals) and vals[i] is not None:
            n.right = T(vals[i]); q.append(n.right)
        i += 1
    return root


def adapt(name, ann, v, g):
    """Turn a plain list into a ListNode/TreeNode when the parameter wants one."""
    if not isinstance(v, list):
        return v
    low = name.lower()
    if "ListNode" in ann or (not ann and low in LIST_PARAMS):
        return build_list(v, g)
    if "TreeNode" in ann or (not ann and low in TREE_PARAMS):
        return build_tree(v, g)
    return v


def guess_value(name, ann):
    """A small sample argument from a parameter's type hint or name, used when no input is given."""
    low, a = name.lower(), ann.replace(" ", "")
    if "ListNode" in a or low in LIST_PARAMS:
        return [1, 2, 3, 4, 5]
    if "TreeNode" in a or low in TREE_PARAMS:
        return [4, 2, 7, 1, 3, 6, 9]
    if a.startswith(("List[List[str]]", "list[list[str]]")) or low in ("board",):
        return [["1", "1", "0"], ["0", "1", "0"], ["1", "0", "1"]]
    if a.startswith(("List[List", "list[list")) or low in ("grid", "matrix", "intervals", "edges", "mat", "points", "pairs"):
        return [[1, 3], [2, 6], [8, 10], [15, 18]] if low in ("intervals", "pairs") else [[0, 1], [1, 2], [2, 3]] if low == "edges" else [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    if a.startswith(("List[str]", "list[str]")) or low in ("words", "strs", "tokens"):
        return ["eat", "tea", "tan", "ate", "nat", "bat"]
    if a.startswith(("List", "list")) or low in ("nums", "arr", "a", "array", "prices", "heights", "height", "nums1", "nums2", "coins", "weights", "piles", "stones", "temperatures", "values"):
        return [5, 1, 4, 2, 3] if low != "nums" else [2, 7, 11, 15]
    if a == "str" or low in ("s", "t", "word", "text", "string", "pattern", "p"):
        return "abcabcbb" if low in ("s", "text", "string") else "abc"
    if a == "bool":
        return True
    if a == "float":
        return 2.5
    return 9 if low == "target" else 3


def call_text(names, args):
    return ", ".join(f"{n} = {v!r}" for n, v in zip(names, args))


# ---------- running ----------

def fresh_globals():
    g = {"__name__": "__main__"}
    exec(compile(PRELUDE, "<prelude>", "exec"), g)
    g["random"].seed(0)  # the counting pass and the recording pass must see the same run
    return g


def is_ops(val, g):
    """LeetCode design input: ["LRUCache", "put", ...], [[2], [1, 1], ...]."""
    return (len(val) == 2 and isinstance(val[0], list) and bool(val[0]) and all(isinstance(x, str) for x in val[0])
            and isinstance(val[1], list) and isinstance(g.get(val[0][0]), type))


def run_once(code, inp, rec, res):
    """One run of the code under rec. Fills res output/title/guessed input; raises on errors."""
    g = fresh_globals()
    tree = ast.parse(code)
    compiled = compile(tree, "<user>", "exec")
    if is_script(tree) and not inp.strip():
        sys.settrace(rec.trace)
        try:
            exec(compiled, g)
        finally:
            sys.settrace(None)
        res["title"] = "Your script"
        return
    exec(compiled, g)  # define the functions and classes without tracing the definitions
    fn, node = entry_point(tree, g)
    if inp.strip():
        kind, val = parse_input(inp, g)
    elif fn is not None:
        names, ann = params_of(node)
        val, kind = [guess_value(n, ann.get(n, "")) for n in names], "pos"
        res["input"], res["inputGuessed"] = call_text(names, val), True
    else:
        raise InputError("There is nothing to call. Paste a function, a class Solution, or a script that runs something.")
    if kind == "call":
        sys.settrace(rec.trace)
        try:
            value = eval(compile(val, "<input>", "eval"), g)
        finally:
            sys.settrace(None)
        res["title"] = val.split("(")[0].strip()
    elif kind == "pos" and is_ops(val, g):
        value = run_ops(g[val[0][0]], val[0], val[1], rec)
        res["title"] = val[0][0]
    else:
        if fn is None:
            raise InputError("There is no function to pass that input to.")
        names, ann = params_of(node)
        if kind == "kw" and set(val) <= set(names):
            names = [n for n in names if n in val]
            args = [val[n] for n in names]
        else:
            args = list(val.values()) if kind == "kw" else list(val)
        args = [adapt(n, ann.get(n, ""), a, g) for n, a in zip(names, args)] + args[len(names):]
        sys.settrace(rec.trace)
        try:
            value = fn(*args)
        finally:
            sys.settrace(None)
        res["title"] = node.name
    res["output"] = to_output(value)


def execute(code, inp):
    """Run the code (calling its main function with inp) and return the trace."""
    res = {"output": None, "error": None, "errorLine": None, "input": inp, "inputGuessed": False, "title": None}
    old, printed = sys.stdout, ""
    rec = Recorder(code)
    try:
        sys.stdout = io.StringIO()
        try:
            run_once(code, inp, rec, dict(res))           # pass 1: only count the steps
        except BaseException as e:
            if isinstance(e, (KeyboardInterrupt, SystemExit)):
                raise
        rec = Recorder(code, known=rec.total)
        sys.stdout = io.StringIO()
        run_once(code, inp, rec, res)                    # pass 2: record the first and last steps
    except Abort:
        res["error"] = f"Stopped after {MAX_EVENTS:,} steps. Is there an infinite loop, or is the input very large?"
    except InputError as e:
        res["error"] = str(e)
    except SyntaxError as e:
        res["error"] = f"SyntaxError on line {e.lineno}: {e.msg}"
        res["errorLine"] = e.lineno
    except RecursionError:
        res["error"] = "RecursionError: the recursion went too deep (is a base case missing?)"
    except Exception as e:
        tb, line = e.__traceback__, None
        while tb is not None:
            if tb.tb_frame.f_code.co_filename == "<user>":
                line = tb.tb_lineno
            tb = tb.tb_next
        res["error"] = f"{type(e).__name__}: {e}" + (f" (line {line})" if line else "")
        res["errorLine"] = line
    finally:
        sys.settrace(None)
        if isinstance(sys.stdout, io.StringIO):
            printed = sys.stdout.getvalue()
        sys.stdout = old
    steps, capped = rec.steps()
    if res["error"] and steps:
        steps[-1] = dict(steps[-1])
        steps[-1]["title"] = "Error: " + res["error"][:80]
        steps[-1]["explain"] = "The run stops here. " + res["error"]
        if res["errorLine"]:
            steps[-1]["line"] = res["errorLine"]
    res.update({"steps": steps, "capped": capped, "stdout": printed[-4000:], "count": rec.total})
    return res


def run_ops(cls, ops, args, rec):
    sys.settrace(rec.trace)
    try:
        obj = cls(*args[0])
        outs = [None]
        for op, a in zip(ops[1:], args[1:]):
            outs.append(getattr(obj, op)(*a))
    finally:
        sys.settrace(None)
    return outs


def auto_edges(code, inp):
    """Inputs that often break code, derived from the input that was traced (no AI involved)."""
    g = fresh_globals()
    tree = ast.parse(code)
    if is_script(tree):
        return []
    exec(compile(tree, "<user>", "exec"), g)
    fn, node = entry_point(tree, g)
    if fn is None:
        return []
    names, ann = params_of(node)
    try:
        kind, val = parse_input(inp, g)
    except InputError:
        return []
    if kind == "call":
        return []
    args = [val.get(n) for n in names] if kind == "kw" else list(val)
    if kind == "kw" and not set(val) <= set(names):
        args = list(val.values())
    cases = []

    def add(name, i, v, why):
        a = list(args); a[i] = v
        cases.append({"name": name, "input": call_text(names, a), "why": why})

    for i, v in enumerate(args):
        probe = adapt(names[i], ann.get(names[i], ""), [1], g) if i < len(names) and isinstance(v, list) else None
        if probe is not None and not isinstance(probe, list):   # a linked list or tree parameter
            tree = is_tree_node(probe)
            what = "tree" if tree else "list"
            add(f"Empty {what}", i, [], f"`{names[i]}` is None. Code that reads `{names[i]}.val` first crashes.")
            add("Single node", i, v[:1] or [1], "The smallest non-empty " + what + "; the loop or recursion stops right away.")
            if tree:
                add("Skewed tree (a chain)", i, [1, 2, None, 3, None, 4], "Every node has one child, so recursion goes as deep as the number of nodes.")
            else:
                add("Two nodes", i, (v + [v[0]])[:2] if v else [1, 2], "Pointer updates are easy to get wrong with exactly two nodes.")
            break
        if isinstance(v, list) and v and all(scalar(x) for x in v):
            add(f"Empty `{names[i]}`", i, [], "Nothing to loop over. Code often indexes `[0]` or divides by the length.")
            add(f"One element in `{names[i]}`", i, v[:1], "Loops may not run at all, and two-pointer code may never move.")
            if all(isinstance(x, (int, float)) and not isinstance(x, bool) for x in v):
                add(f"`{names[i]}` all the same", i, [v[0]] * len(v), "Duplicates can break strict comparisons and `seen` sets.")
                add(f"`{names[i]}` with negatives", i, [-x if x else -1 for x in v], "Negative numbers break code that assumes sums only grow.")
                add(f"`{names[i]}` reversed", i, list(reversed(v)), "The opposite order often hits the worst case.")
            break
        if isinstance(v, str) and v:
            add(f"Empty `{names[i]}`", i, "", "An empty string has no first character.")
            add(f"One character in `{names[i]}`", i, v[:1], "The smallest non-empty input.")
            add(f"`{names[i]}` all the same character", i, v[0] * len(v), "Repeats test duplicate handling.")
            break
        if isinstance(v, int) and not isinstance(v, bool):
            add(f"`{names[i]}` = 0", i, 0, "Zero is a common off-by-one and division trap.")
    return cases[:6]


def run_job(text):
    job = json.loads(text)
    code, inp = job.get("code", ""), job.get("input", "") or ""
    main = execute(code, inp)
    edges = []
    specs = job.get("edges")
    if specs is None and job.get("autoEdges") and not main["error"]:
        try:
            specs = auto_edges(code, main["input"])
        except Exception:
            specs = []
    for spec in specs or []:
        r = execute(code, spec.get("input", ""))
        e = dict(spec)
        e.update({"actual": r["output"] if not r["error"] else r["error"], "error": r["error"], "steps": r["steps"], "capped": r["capped"]})
        edges.append(e)
    main["edgeCases"] = edges
    return json.dumps(main, ensure_ascii=False)
