"""Shared helpers for the Python solutions (not shown on the page)."""
import bisect, heapq, math, random
from collections import Counter, defaultdict, deque, OrderedDict
from typing import Dict, List, Optional, Set, Tuple


class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


class TreeNode:
    def __init__(self, val=0, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right


class Raw:
    def __init__(self, s): self.s = s


def mk_list(vals):
    dummy = cur = ListNode()
    for v in vals:
        cur.next = ListNode(v)
        cur = cur.next
    return dummy.next


def mk_tree(vals):
    if not vals or vals[0] is None: return None
    root = TreeNode(vals[0]); q = deque([root]); i = 1
    while q and i < len(vals):
        n = q.popleft()
        if i < len(vals) and vals[i] is not None: n.left = TreeNode(vals[i]); q.append(n.left)
        i += 1
        if i < len(vals) and vals[i] is not None: n.right = TreeNode(vals[i]); q.append(n.right)
        i += 1
    return root


def find(root, v):
    if root is None: return None
    if root.val == v: return root
    return find(root.left, v) or find(root.right, v)


def fmt(x):
    if x is None: return "null"
    if isinstance(x, Raw): return x.s
    if isinstance(x, bool): return "true" if x else "false"
    if isinstance(x, int): return str(x)
    if isinstance(x, float):
        return f"{x:.1f}" if x == int(x) else f"{x:.5f}".rstrip("0")
    if isinstance(x, str): return '"' + x + '"'
    if isinstance(x, ListNode):
        out, seen, c = [], set(), x
        while c is not None:
            if id(c) in seen: out.append("…cycle"); break
            seen.add(id(c)); out.append(str(c.val)); c = c.next
        return "[" + ", ".join(out) + "]"
    if isinstance(x, TreeNode):
        out, q = [], deque([x])
        while q:
            n = q.popleft()
            if n is None: out.append("null"); continue
            out.append(str(n.val)); q.append(n.left); q.append(n.right)
        while out and out[-1] == "null": out.pop()
        return "[" + ",".join(out) + "]"
    if isinstance(x, dict): return "{" + ", ".join(f"{k}={fmt(v)}" for k, v in x.items()) + "}"
    if isinstance(x, (list, tuple)): return "[" + ", ".join(fmt(e) for e in x) + "]"
    return str(x)


def mk_cycle(vals, pos):
    head = mk_list(vals)
    tail, at, i, c = None, None, 0, head
    while c:
        if i == pos: at = c
        tail = c; c = c.next; i += 1
    if tail: tail.next = at
    return head
