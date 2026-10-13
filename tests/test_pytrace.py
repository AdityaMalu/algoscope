"""Tests for runner/pytrace.py, the tracer that runs pasted Python in the browser (via Pyodide).

They run under plain CPython, which behaves the same for tracing.
"""
import json, os, sys, time

sys.path.insert(0, os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "runner"))
import pytrace  # noqa: E402


def run(code, inp="", **kw):
    return json.loads(pytrace.run_job(json.dumps({"code": code, "input": inp, **kw})))


TWO_SUM = '''class Solution:
    def twoSum(self, nums: List[int], target: int) -> List[int]:
        seen = {}
        for i, x in enumerate(nums):
            if target - x in seen:
                return [seen[target - x], i]
            seen[x] = i
        return []
'''


def test_two_sum_trace():
    r = run(TWO_SUM, "nums = [2,7,11,15], target = 9")
    assert r["error"] is None and r["output"] == "[0, 1]"
    lines = [s["line"] for s in r["steps"]]
    assert lines == [2, 3, 4, 5, 7, 4, 5, 6]
    s = r["steps"][4]
    arr = next(d for d in s["structures"] if d["name"] == "nums")
    assert arr["kind"] == "array" and arr["pointers"] == {"i": 0}
    seen = next(d for d in s["structures"] if d["name"] == "seen")
    assert seen["entries"] == [["2", 0]] and seen["marks"] == {"2": "swap"}
    assert r["steps"][-1]["title"] == "Return [0, 1]"


def test_positional_and_call_inputs():
    assert run(TWO_SUM, "[3, 3], 6")["output"] == "[0, 1]"
    assert run("def add(a, b):\n    return a + b\n", "add(2, 5)")["output"] == "7"
    assert run("def f(x):\n    return x\n", "null")["output"] == "null"


def test_guessed_input():
    r = run(TWO_SUM)
    assert r["inputGuessed"] and r["input"].startswith("nums = [")
    assert r["error"] is None


def test_linked_list_reverse():
    r = run('''def reverseList(head):
    prev = None
    cur = head
    while cur:
        nxt = cur.next
        cur.next = prev
        prev = cur
        cur = nxt
    return prev
''', "head = [1, 2, 3]")
    assert r["output"] == "[3, 2, 1]"
    relink = next(s for s in r["steps"] if s["line"] == 6)
    ll = next(d for d in relink["structures"] if d["kind"] == "linkedlist")
    assert ll["marks"] and set(ll["pointers"]) >= {"head", "cur", "nxt"}


def test_tree_recursion_has_call_stack():
    r = run('''def maxDepth(root: Optional[TreeNode]) -> int:
    if not root:
        return 0
    return 1 + max(maxDepth(root.left), maxDepth(root.right))
''', "root = [3, 9, 20, None, None, 15, 7]")
    assert r["output"] == "3"
    deep = [s for s in r["steps"] if any(d["kind"] == "callstack" for d in s["structures"])]
    assert deep
    tree = next(d for d in deep[0]["structures"] if d["kind"] == "tree")
    assert len(tree["nodes"]) == 5  # the whole tree is drawn, not just the current subtree


def test_script_mode_and_stdout():
    r = run("nums = [3, 1, 2]\nnums.sort()\nprint(nums)\n")
    assert r["error"] is None and r["stdout"] == "[1, 2, 3]\n"
    assert r["steps"][0]["title"] == "Start"


def test_errors_are_reported_on_their_line():
    r = run("def f(a):\n    return a[10]\n", "[1, 2]")
    assert r["error"].startswith("IndexError") and r["errorLine"] == 2
    assert r["steps"][-1]["line"] == 2 and r["steps"][-1]["title"].startswith("Error")
    r = run("def f(x):\n  return x +\n", "1")
    assert r["error"].startswith("SyntaxError on line 2")
    r = run("def f(n):\n    return f(n + 1)\n", "1")
    assert r["error"].startswith("RecursionError")


def test_infinite_loop_stops():
    t = time.time()
    r = run("def f(n):\n    while True:\n        n += 1\n", "5")
    assert r["error"].startswith("Stopped after")
    assert time.time() - t < 30


def test_long_run_keeps_head_and_tail():
    r = run("def fib(n):\n    return n if n < 2 else fib(n - 1) + fib(n - 2)\n", "fib(16)")
    assert r["output"] == "987" and r["capped"]
    assert len(r["steps"]) == pytrace.HEAD + 1 + pytrace.TAIL
    assert "skipped" in r["steps"][pytrace.HEAD]["title"]
    assert r["steps"][-1]["title"] == "Return 987"


def test_design_class_ops():
    r = run('''class LRUCache:
    def __init__(self, capacity):
        self.cap = capacity
        self.d = OrderedDict()
    def get(self, key):
        if key not in self.d:
            return -1
        self.d.move_to_end(key)
        return self.d[key]
    def put(self, key, value):
        self.d[key] = value
        self.d.move_to_end(key)
        if len(self.d) > self.cap:
            self.d.popitem(last=False)
''', '["LRUCache","put","put","get","put","get"], [[2],[1,1],[2,2],[1],[3,3],[2]]')
    assert r["error"] is None and r["output"] == "[null, null, null, 1, null, -1]"
    assert any(d["name"] == "self.d" for s in r["steps"] for d in s["structures"])


def test_auto_edge_cases():
    r = run(TWO_SUM, "nums = [2, 7, 11, 15], target = 9", autoEdges=True)
    names = [e["name"] for e in r["edgeCases"]]
    assert "Empty `nums`" in names and all(e["steps"] for e in r["edgeCases"])
    crash = run("def first(nums):\n    return nums[0]\n", "[4, 5]", autoEdges=True)
    empty = next(e for e in crash["edgeCases"] if e["name"] == "Empty `nums`")
    assert empty["error"].startswith("IndexError")


def test_given_edge_cases_are_run():
    r = run(TWO_SUM, "[2, 7], 9", edges=[{"name": "dupes", "input": "[3, 3], 6", "expected": "[0, 1]"}])
    assert r["edgeCases"][0]["actual"] == "[0, 1]"


def test_auto_edges_for_trees():
    r = run("def depth(root):\n    if not root:\n        return 0\n    return 1 + max(depth(root.left), depth(root.right))\n",
            "root = [1, 2, 3]", autoEdges=True)
    names = [e["name"] for e in r["edgeCases"]]
    assert names == ["Empty tree", "Single node", "Skewed tree (a chain)"]
    assert [e["actual"] for e in r["edgeCases"]] == ["0", "1", "4"]
