"""How to call each solution when checking Python/C++ against Java.

args:   argument types (int long double bool str vi vvi vs vvs vc vvc list tree)
names:  argument names as they appear in the input strings
inplace: index of the argument to print instead of the return value
unordered: compare top-level list elements as a multiset
skip:   case indices not compared (several answers are valid)
custom: {case index: {"py": code setting R, "cpp": code declaring R}}  (0 = main trace)
"""

SPEC = {}


def add(marker, args=(), names=(), **kw):
    SPEC[marker] = {"args": list(args), "names": list(names), **kw}


# Arrays & Hashing
add("twoSum", ["vi", "int"], ["nums", "target"])
add("isAnagram", ["str", "str"], ["s", "t"])
add("containsDuplicate", ["vi"], ["nums"])
add("majorityElement", ["vi"], ["nums"])
add("groupAnagrams", ["vs"], ["strs"], unordered=True)
add("topKFrequent", ["vi", "int"], ["nums", "k"], unordered=True, skip=[5])
add("productExceptSelf", ["vi"], ["nums"])
add("longestConsecutive", ["vi"], ["nums"])

# Two Pointers
add("isPalindrome", ["str"], ["s"])
add("moveZeroes", ["vi"], ["nums"], inplace=0)
add("twoSumSorted", ["vi", "int"], ["numbers", "target"], py="twoSum", cpp="twoSum")
add("sortColors", ["vi"], ["nums"], inplace=0)
add("threeSum", ["vi"], ["nums"])
add("maxArea", ["vi"], ["height"])
add("trap", ["vi"], ["height"])

# Sliding Window
add("maxProfit", ["vi"], ["prices"])
add("lengthOfLongestSubstring", ["str"], ["s"])
add("longestOnes", ["vi", "int"], ["nums", "k"])
add("totalFruit", ["vi"], ["fruits"])
add("characterReplacement", ["str", "int"], ["s", "k"])
add("checkInclusion", ["str", "str"], ["s1", "s2"])
add("minWindow", ["str", "str"], ["s", "t"])

# Binary Search
add("search", ["vi", "int"], ["nums", "target"])
add("searchInsert", ["vi", "int"], ["nums", "target"])
add("findMin", ["vi"], ["nums"])
add("searchMatrix", ["vvi", "int"], ["matrix", "target"])
add("searchRotated", ["vi", "int"], ["nums", "target"])
add("minEatingSpeed", ["vi", "int"], ["piles", "h"])
add("shipWithinDays", ["vi", "int"], ["weights", "days"])

# Stack
add("isValid", ["str"], ["s"])
add("MinStack", custom={
    0: {"py": "s = MinStack(); o = []\ns.push(-2); o.append(None); s.push(0); o.append(None); s.push(-3); o.append(None)\no.append(s.getMin()); s.pop(); o.append(None); o.append(s.top()); o.append(s.getMin()); R = o",
        "cpp": "MinStack s; Out o; s.push(-2); o.nul(); s.push(0); o.nul(); s.push(-3); o.nul(); o.add(s.getMin()); s.pop(); o.nul(); o.add(s.top()); o.add(s.getMin()); auto R = o;"},
    1: {"py": "s = MinStack(); o = []\ns.push(1); o.append(None); s.push(1); o.append(None); s.pop(); o.append(None); o.append(s.getMin()); R = o",
        "cpp": "MinStack s; Out o; s.push(1); o.nul(); s.push(1); o.nul(); s.pop(); o.nul(); o.add(s.getMin()); auto R = o;"},
    2: {"py": "s = MinStack(); o = []\nfor x in (1, 2, 3): s.push(x); o.append(None)\no.append(s.getMin()); R = o",
        "cpp": "MinStack s; Out o; for (int x : {1, 2, 3}) { s.push(x); o.nul(); } o.add(s.getMin()); auto R = o;"},
    3: {"py": "s = MinStack(); o = []\ns.push(2147483647); o.append(None); s.push(-2147483648); o.append(None); o.append(s.getMin()); R = o",
        "cpp": "MinStack s; Out o; s.push(INT_MAX); o.nul(); s.push(INT_MIN); o.nul(); o.add(s.getMin()); auto R = o;"},
})
add("nextGreaterElement", ["vi", "vi"], ["nums1", "nums2"])
add("evalRPN", ["vs"], ["tokens"])
add("dailyTemperatures", ["vi"], ["temperatures"])
add("asteroidCollision", ["vi"], ["asteroids"])
add("largestRectangleArea", ["vi"], ["heights"])

# Linked List
add("reverseList", ["list"], ["head"])
add("mergeTwoLists", ["list", "list"], ["list1", "list2"])
add("hasCycle", ["list"], ["head"], custom={
    0: {"py": "R = hasCycle(mk_cycle([3, 2, 0, -4], 1))", "cpp": "auto R = hasCycle(mkCycle({3, 2, 0, -4}, 1));"},
    3: {"py": "R = hasCycle(mk_cycle([1], 0))", "cpp": "auto R = hasCycle(mkCycle({1}, 0));"},
})
add("middleNode", ["list"], ["head"])
add("removeNthFromEnd", ["list", "int"], ["head", "n"])
add("reorderList", ["list"], ["head"], inplace=0)
_LRU = [
    ("c = LRUCache(2); o = [None]\nc.put(1, 1); o.append(None); c.put(2, 2); o.append(None); o.append(c.get(1)); c.put(3, 3); o.append(None)\no.append(c.get(2)); c.put(4, 4); o.append(None); o.append(c.get(1)); o.append(c.get(3)); o.append(c.get(4)); R = o",
     "LRUCache c(2); Out o; o.nul(); c.put(1, 1); o.nul(); c.put(2, 2); o.nul(); o.add(c.get(1)); c.put(3, 3); o.nul(); o.add(c.get(2)); c.put(4, 4); o.nul(); o.add(c.get(1)); o.add(c.get(3)); o.add(c.get(4)); auto R = o;"),
    ("c = LRUCache(2); o = [None]\nc.put(1, 1); o.append(None); c.put(2, 2); o.append(None); c.put(1, 10); o.append(None); c.put(3, 3); o.append(None); o.append(c.get(1)); o.append(c.get(2)); R = o",
     "LRUCache c(2); Out o; o.nul(); c.put(1, 1); o.nul(); c.put(2, 2); o.nul(); c.put(1, 10); o.nul(); c.put(3, 3); o.nul(); o.add(c.get(1)); o.add(c.get(2)); auto R = o;"),
    ("c = LRUCache(1); o = [None]\nc.put(1, 1); o.append(None); c.put(2, 2); o.append(None); o.append(c.get(1)); o.append(c.get(2)); R = o",
     "LRUCache c(1); Out o; o.nul(); c.put(1, 1); o.nul(); c.put(2, 2); o.nul(); o.add(c.get(1)); o.add(c.get(2)); auto R = o;"),
    ("c = LRUCache(2); o = [None]; o.append(c.get(5)); R = o", "LRUCache c(2); Out o; o.nul(); o.add(c.get(5)); auto R = o;"),
]
add("LRUCache", custom={i: {"py": p, "cpp": c} for i, (p, c) in enumerate(_LRU)})
add("mergeKLists", ["vlist"], ["lists"])

# Trees: Traversal
add("maxDepth", ["tree"], ["root"])
add("invertTree", ["tree"], ["root"])
add("isSameTree", ["tree", "tree"], ["p", "q"])
add("isBalanced", ["tree"], ["root"])
add("diameterOfBinaryTree", ["tree"], ["root"])
add("levelOrder", ["tree"], ["root"])
add("rightSideView", ["tree"], ["root"])
add("zigzagLevelOrder", ["tree"], ["root"])

# Trees: BST & Advanced
add("isValidBST", ["tree"], ["root"])
add("kthSmallest", ["tree", "int"], ["root", "k"])
add("lcaBST", ["tree", "int", "int"], ["root", "p", "q"],
    py_expr="lowestCommonAncestorBST(a0, find(a0, a1), find(a0, a2)).val",
    cpp_expr="lowestCommonAncestorBST(a0, findNode(a0, a1), findNode(a0, a2))", cpp_post="R->val")
add("lowestCommonAncestor", ["tree", "int", "int"], ["root", "p", "q"],
    py_expr="lowestCommonAncestor(a0, find(a0, a1), find(a0, a2)).val",
    cpp_expr="lowestCommonAncestor(a0, findNode(a0, a1), findNode(a0, a2))", cpp_post="R->val")
add("buildTree", ["vi", "vi"], ["preorder", "inorder"])
add("maxPathSum", ["tree"], ["root"])
add("Codec", ["tree"], ["root"], py_expr="Codec().deserialize(Codec().serialize(a0))",
    cpp_expr="[&] { Codec c; return c.deserialize(c.serialize(a0)); }()")

# Heap / Priority Queue
add("findKthLargest", ["vi", "int"], ["nums", "k"])
add("lastStoneWeight", ["vi"], ["stones"])
add("kClosest", ["vvi", "int"], ["points", "k"], unordered=True)
add("leastInterval", ["vc", "int"], ["tasks", "n"])
add("topKFrequentWords", ["vs", "int"], ["words", "k"], py="topKFrequent", cpp="topKFrequent")
add("reorganizeString", ["str"], ["s"], skip=[0, 3])
_MF = [
    ("m = MedianFinder(); o = [None]\nm.addNum(5); o.append(None); m.addNum(15); o.append(None); o.append(m.findMedian()); m.addNum(1); o.append(None); o.append(m.findMedian()); m.addNum(3); o.append(None); o.append(m.findMedian()); R = o",
     "MedianFinder m; Out o; o.nul(); m.addNum(5); o.nul(); m.addNum(15); o.nul(); o.add(m.findMedian()); m.addNum(1); o.nul(); o.add(m.findMedian()); m.addNum(3); o.nul(); o.add(m.findMedian()); auto R = o;"),
    ("m = MedianFinder(); o = [None]; m.addNum(7); o.append(None); o.append(m.findMedian()); R = o",
     "MedianFinder m; Out o; o.nul(); m.addNum(7); o.nul(); o.add(m.findMedian()); auto R = o;"),
    ("m = MedianFinder(); o = [None]; m.addNum(-1); o.append(None); m.addNum(-2); o.append(None); o.append(m.findMedian()); R = o",
     "MedianFinder m; Out o; o.nul(); m.addNum(-1); o.nul(); m.addNum(-2); o.nul(); o.add(m.findMedian()); auto R = o;"),
    ("m = MedianFinder(); o = [None]\nfor x in (3, 2, 1): m.addNum(x); o.append(None)\no.append(m.findMedian()); R = o",
     "MedianFinder m; Out o; o.nul(); for (int x : {3, 2, 1}) { m.addNum(x); o.nul(); } o.add(m.findMedian()); auto R = o;"),
]
add("MedianFinder", custom={i: {"py": p, "cpp": c} for i, (p, c) in enumerate(_MF)})

# Graphs: BFS / DFS
add("floodFill", ["vvi", "int", "int", "int"], ["image", "sr", "sc", "color"])
add("numIslands", ["vvc"], ["grid"])
add("maxAreaOfIsland", ["vvi"], ["grid"])
add("cloneGraph", ["vvi"], ["adjList"], py_expr="adj_of(cloneGraph(build_graph(a0)))", cpp_expr="adjOf(cloneGraph(buildGraph(a0)))")
add("orangesRotting", ["vvi"], ["grid"])
add("updateMatrix", ["vvi"], ["mat"])
add("solve", ["vvc"], ["board"], inplace=0)
add("pacificAtlantic", ["vvi"], ["heights"])

# Graphs: Topological Sort & Union-Find
add("canFinish", ["int", "vvi"], ["numCourses", "prerequisites"])
add("findCircleNum", ["vvi"], ["isConnected"])
add("eventualSafeNodes", ["vvi"], ["graph"])
add("findOrder", ["int", "vvi"], ["numCourses", "prerequisites"])
add("findRedundantConnection", ["vvi"], ["edges"])
add("accountsMerge", ["vvs"], ["accounts"], unordered=True)
add("ladderLength", ["str", "str", "vs"], ["beginWord", "endWord", "wordList"])

# Backtracking
add("subsets", ["vi"], ["nums"])
add("permute", ["vi"], ["nums"])
add("combinationSum", ["vi", "int"], ["candidates", "target"])
add("subsetsWithDup", ["vi"], ["nums"])
add("combinationSum2", ["vi", "int"], ["candidates", "target"])
add("exist", ["vvc", "str"], ["board", "word"])
add("partition", ["str"], ["s"])
add("solveNQueens", ["int"], ["n"])

# 1D Dynamic Programming
add("climbStairs", ["int"], ["n"])
add("minCostClimbingStairs", ["vi"], ["cost"])
add("rob", ["vi"], ["nums"])
add("robCircle", ["vi"], ["nums"])
add("coinChange", ["vi", "int"], ["coins", "amount"])
add("lengthOfLIS", ["vi"], ["nums"])
add("wordBreak", ["str", "vs"], ["s", "wordDict"])
add("numDecodings", ["str"], ["s"])

# 2D Dynamic Programming
add("uniquePaths", ["int", "int"], ["m", "n"])
add("minPathSum", ["vvi"], ["grid"])
add("longestCommonSubsequence", ["str", "str"], ["text1", "text2"])
add("canPartition", ["vi"], ["nums"])
add("change", ["int", "vi"], ["amount", "coins"])
add("findTargetSumWays", ["vi", "int"], ["nums", "target"])
add("minDistance", ["str", "str"], ["word1", "word2"])
add("maxProfitCooldown", ["vi"], ["prices"], py="maxProfit", cpp="maxProfit")

# Greedy & Intervals
add("maxSubArray", ["vi"], ["nums"])
add("canJump", ["vi"], ["nums"])
add("merge", ["vvi"], ["intervals"])
add("insert", ["vvi", "vi"], ["intervals", "newInterval"])
add("jump", ["vi"], ["nums"])
add("canCompleteCircuit", ["vi", "vi"], ["gas", "cost"])
add("eraseOverlapIntervals", ["vvi"], ["intervals"])
add("findMinArrowShots", ["vvi"], ["points"])

# Trie & Strings
_TRIE = [
    ("t = Trie(); o = [None]\nt.insert('apple'); o.append(None); o.append(t.search('apple')); o.append(t.search('app')); o.append(t.startsWith('app')); t.insert('app'); o.append(None); o.append(t.search('app')); R = o",
     "Trie t; Out o; o.nul(); t.insert(\"apple\"); o.nul(); o.add(t.search(\"apple\")); o.add(t.search(\"app\")); o.add(t.startsWith(\"app\")); t.insert(\"app\"); o.nul(); o.add(t.search(\"app\")); auto R = o;"),
    ("t = Trie(); o = [None, t.search('a')]; R = o", "Trie t; Out o; o.nul(); o.add(t.search(\"a\")); auto R = o;"),
    ("t = Trie(); o = [None]; t.insert('car'); o.append(None); o.append(t.search('ca')); o.append(t.startsWith('ca')); R = o",
     "Trie t; Out o; o.nul(); t.insert(\"car\"); o.nul(); o.add(t.search(\"ca\")); o.add(t.startsWith(\"ca\")); auto R = o;"),
    ("t = Trie(); o = [None]; t.insert('go'); o.append(None); o.append(t.search('gone')); R = o",
     "Trie t; Out o; o.nul(); t.insert(\"go\"); o.nul(); o.add(t.search(\"gone\")); auto R = o;"),
]
add("Trie", custom={i: {"py": p, "cpp": c} for i, (p, c) in enumerate(_TRIE)})
add("longestCommonPrefix", ["vs"], ["strs"])
add("countSubstrings", ["str"], ["s"])
_WD = [
    ("w = WordDictionary(); o = [None]\nfor x in ('bad', 'dad', 'mad'): w.addWord(x); o.append(None)\nfor q in ('pad', 'bad', '.ad', 'b..'): o.append(w.search(q))\nR = o",
     "WordDictionary w; Out o; o.nul(); for (string x : {\"bad\", \"dad\", \"mad\"}) { w.addWord(x); o.nul(); } for (string q : {\"pad\", \"bad\", \".ad\", \"b..\"}) o.add(w.search(q)); auto R = o;"),
    ("w = WordDictionary(); o = [None]; w.addWord('ab'); o.append(None); o.append(w.search('..')); o.append(w.search('...')); R = o",
     "WordDictionary w; Out o; o.nul(); w.addWord(\"ab\"); o.nul(); o.add(w.search(\"..\")); o.add(w.search(\"...\")); auto R = o;"),
    ("w = WordDictionary(); o = [None]; w.addWord('at'); o.append(None); o.append(w.search('a.')); R = o",
     "WordDictionary w; Out o; o.nul(); w.addWord(\"at\"); o.nul(); o.add(w.search(\"a.\")); auto R = o;"),
]
add("WordDictionary", custom={i: {"py": p, "cpp": c} for i, (p, c) in enumerate(_WD)})
add("suggestedProducts", ["vs", "str"], ["products", "searchWord"])
add("longestPalindrome", ["str"], ["s"])
add("findWords", ["vvc", "vs"], ["board", "words"], unordered=True)

# Advanced Graphs
add("networkDelayTime", ["vvi", "int", "int"], ["times", "n", "k"])
add("shortestPathBinaryMatrix", ["vvi"], ["grid"])
add("minCostConnectPoints", ["vvi"], ["points"])
add("findCheapestPrice", ["int", "vvi", "int", "int", "int"], ["n", "flights", "src", "dst", "k"])
add("minimumEffortPath", ["vvi"], ["heights"])
add("swimInWater", ["vvi"], ["grid"])

# OA Simulation
add("maximumUnits", ["vvi", "int"], ["boxTypes", "truckSize"])
add("numTeams", ["vi"], ["rating"])
add("numberOfWays", ["str"], ["s"])
add("totalStrength", ["vi"], ["strength"])

# Amazon-tagged: Arrays & Strings
add("subarraySum", ["vi", "int"], ["nums", "k"])
add("findAnagrams", ["str", "str"], ["s", "p"])
add("compress", ["vc"], ["chars"], py_expr="(lambda L: Raw(str(L) + ', ' + fmt(a0[:L])))(compress(a0))",
    cpp_expr="[&] { int L = compress(a0); return Raw{to_string(L) + \", \" + fmt(vector<char>(a0.begin(), a0.begin() + L))}; }()")
add("nextPermutation", ["vi"], ["nums"], inplace=0)
add("spiralOrder", ["vvi"], ["matrix"])
add("rotate", ["vvi"], ["matrix"], inplace=0)
add("setZeroes", ["vvi"], ["matrix"], inplace=0)

# Amazon-tagged: Mixed
add("copyRandomList", ["rl"], ["head"], py_expr="show_r(copyRandomList(build_r(a0)))", cpp_expr="showR(copyRandomList(buildR(a0)))")
add("decodeString", ["str"], ["s"])
add("findDuplicate", ["vi"], ["nums"])
_SUDOKU = {
    0: ["53..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1", "7...2...6", ".6....28.", "...419..5", "....8..79"],
    1: ["83..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1", "7...2...6", ".6....28.", "...419..5", "....8..79"],
    2: ["........."] * 9,
}
add("isValidSudoku", custom={i: {"py": f"R = isValidSudoku([list(r) for r in {rows!r}])",
                                 "cpp": "vector<vector<char>> b; for (string r : {" + ",".join(f'"{x}"' for x in rows) + "}) b.push_back(vector<char>(r.begin(), r.end())); auto R = isValidSudoku(b);"}
                             for i, rows in _SUDOKU.items()})
add("calculate", ["str"], ["s"])
add("maxSlidingWindow", ["vi", "int"], ["nums", "k"])
add("firstMissingPositive", ["vi"], ["nums"])


# Amazon-tagged: Design-style coding (operation scripts: (method, args) after the constructor)
def _ops(cls, ctor_args, ops, wrap=True):
    py = [f"x = {cls}({', '.join(map(repr, ctor_args))}); o = [None]"]
    cpp = [f"{cls} x{'(' + ', '.join(map(str, ctor_args)) + ')' if ctor_args else ''}; Out o; o.nul();"]
    for name, args, returns in ops:
        pa = ", ".join(repr(a) for a in args)
        ca = ", ".join(f'"{a}"' if isinstance(a, str) else str(a) for a in args)
        if returns:
            py.append(f"o.append(x.{name}({pa}))"); cpp.append(f"o.add(x.{name}({ca}));")
        else:
            py.append(f"x.{name}({pa}); o.append(None)"); cpp.append(f"x.{name}({ca}); o.nul();")
    return {"py": "\n".join(py) + "\nR = o", "cpp": " ".join(cpp) + " auto R = o;"}


I, G, R_ = "insert", "getRandom", "remove"
add("RandomizedSet", custom={
    1: _ops("RandomizedSet", [], [(I, [5], 1), (I, [6], 1), (R_, [6], 1)]),
    2: _ops("RandomizedSet", [], [(I, [3], 1), (I, [3], 1)]),
    3: _ops("RandomizedSet", [], [(R_, [9], 1)]),
})
add("TimeMap", custom={
    0: _ops("TimeMap", [], [("set", ["foo", "bar", 1], 0), ("get", ["foo", 1], 1), ("get", ["foo", 3], 1), ("set", ["foo", "bar2", 4], 0), ("get", ["foo", 4], 1), ("get", ["foo", 5], 1)]),
    1: _ops("TimeMap", [], [("set", ["a", "x", 5], 0), ("get", ["a", 2], 1)]),
    2: _ops("TimeMap", [], [("get", ["zzz", 1], 1)]),
})
CI, CO, AV = "checkIn", "checkOut", "getAverageTime"
add("UndergroundSystem", custom={
    0: _ops("UndergroundSystem", [], [(CI, [45, "Leyton", 3], 0), (CI, [32, "Paradise", 8], 0), (CO, [45, "Waterloo", 15], 0), (CO, [32, "Cambridge", 22], 0),
                                      (AV, ["Paradise", "Cambridge"], 1), (CI, [10, "Leyton", 24], 0), (CO, [10, "Waterloo", 38], 0), (AV, ["Leyton", "Waterloo"], 1)]),
    1: _ops("UndergroundSystem", [], [(CI, [1, "A", 0], 0), (CO, [1, "B", 3], 0), (CI, [2, "A", 0], 0), (CO, [2, "B", 4], 0), (AV, ["A", "B"], 1)]),
    2: _ops("UndergroundSystem", [], [(CI, [7, "A", 1], 0), (CO, [7, "B", 5], 0), (CI, [7, "B", 6], 0), (CO, [7, "A", 10], 0), (AV, ["B", "A"], 1)]),
})
add("SnapshotArray", custom={
    0: _ops("SnapshotArray", [3], [("set", [0, 5], 0), ("snap", [], 1), ("set", [0, 6], 0), ("get", [0, 0], 1), ("snap", [], 1), ("get", [0, 1], 1), ("get", [2, 1], 1)]),
    1: _ops("SnapshotArray", [1], [("set", [0, 1], 0), ("set", [0, 2], 0), ("snap", [], 1), ("get", [0, 0], 1)]),
    2: _ops("SnapshotArray", [2], [("snap", [], 1), ("get", [1, 0], 1)]),
})
add("Twitter", custom={
    0: _ops("Twitter", [], [("postTweet", [1, 5], 0), ("getNewsFeed", [1], 1), ("follow", [1, 2], 0), ("postTweet", [2, 6], 0), ("getNewsFeed", [1], 1), ("unfollow", [1, 2], 0), ("getNewsFeed", [1], 1)]),
    1: _ops("Twitter", [], [("postTweet", [1, 7], 0), ("unfollow", [1, 1], 0), ("getNewsFeed", [1], 1)]),
    2: {"py": "x = Twitter()\nfor i in range(12): x.postTweet(1, i)\nR = x.getNewsFeed(1)",
        "cpp": "Twitter x; for (int i = 0; i <= 11; i++) x.postTweet(1, i); auto R = x.getNewsFeed(1);"},
})
add("FreqStack", custom={
    0: _ops("FreqStack", [], [("push", [v], 0) for v in (5, 7, 5, 7, 4, 5)] + [("pop", [], 1)] * 4),
    1: _ops("FreqStack", [], [("push", [v], 0) for v in (1, 2, 3)] + [("pop", [], 1)] * 2),
    2: _ops("FreqStack", [], [("push", [9], 0), ("push", [9], 0), ("pop", [], 1), ("pop", [], 1)]),
})

# Amazon-tagged: Trees & Matrix
add("distanceK", ["tree", "int", "int"], ["root", "target", "k"], unordered=True,
    py_expr="distanceK(a0, find(a0, a1), a2)", cpp_expr="distanceK(a0, findNode(a0, a1), a2)")
add("pathSum", ["tree", "int"], ["root", "targetSum"])
add("flatten", ["tree"], ["root"], inplace=0)
add("maximalSquare", ["vvc"], ["matrix"])
add("verticalTraversal", ["tree"], ["root"])
add("longestIncreasingPath", ["vvi"], ["matrix"])

# Weak areas
add("gameOfLife", ["vvi"], ["board"], inplace=0)
add("threeSumClosest", ["vi", "int"], ["nums", "target"])
add("numberToWords", ["int"], ["num"])
