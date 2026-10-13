import java.util.*;

/** Amazon-tagged: Trees & Matrix */
class Amzn3 {
    static TreeNode R0;
    static final Set<TreeNode> DONE = Collections.newSetFromMap(new IdentityHashMap<>());
    static void reset(TreeNode r) { R0 = r; DONE.clear(); }
    static S tv(TreeNode cur, String mark) {
        S s = T.tree("tree", R0);
        for (TreeNode d : DONE) s.m(d, "done");
        if (cur != null) s.p("node", cur).m(cur, mark);
        return s;
    }

    static void run() {
        P.begin("All Nodes Distance K in Binary Tree", "distanceK");
        P.category("Trees · Tree as a graph");
        P.summary("Return every node exactly k edges from a target node. Tree edges only point down, so first record each node's parent. Then BFS from the target in three directions (left, right, parent) for k levels.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "One pass for parents, one BFS.");
        P.ideas("Adding parent links turns the tree into an undirected graph.",
            "BFS level k from the target is exactly the answer.",
            "A visited set stops the BFS from walking back the way it came.");
        P.pits("Only searching down from the target misses nodes reached through the parent.",
            "Forgetting the visited set loops between a child and its parent.");
        P.trace("root = [3, 5, 1, 6, 2, 0, 8, null, null, 7, 4], target = 5, k = 2", () -> { reset(F.Tr("3,5,1,6,2,0,8,null,null,7,4")); List<Integer> r = distanceK(R0, F.find(R0, 5), 2); Collections.sort(r); return r; });
        P.edge("k = 0", "root = [1, 2, 3], target = 2, k = 0", "[2]", () -> { reset(F.Tr("1,2,3")); return distanceK(R0, F.find(R0, 2), 0); }, "The target itself.");
        P.edge("k larger than the tree", "root = [1], target = 1, k = 3", "[]", () -> { reset(F.Tr("1")); return distanceK(R0, R0, 3); }, "The BFS runs out of nodes.");
        P.edge("Answer through the parent", "root = [0, 1, null, 3, 2], target = 2, k = 1", "[1]", () -> { reset(F.Tr("0,1,null,3,2")); return distanceK(R0, F.find(R0, 2), 1); }, "2 is a leaf; its only neighbour is its parent.");
        P.done();

        P.begin("Path Sum II", "pathSum");
        P.category("Trees · DFS with a path");
        P.summary("Find every root-to-leaf path whose values sum to the target. DFS while keeping the current path and the remaining sum; at a leaf with remaining 0, save a copy of the path. Remove the node from the path when returning.");
        P.cx("O(n)", "O(n log n)", "O(n²)", "O(h)", "Copying each found path costs up to O(h).");
        P.ideas("Subtract each node's value from the remaining target on the way down.",
            "Only leaves can end a path.",
            "Backtrack: remove the last node from the path after exploring a node.");
        P.pits("Saving a path at a non-leaf node whose running sum happens to match.",
            "Adding `path` instead of `new ArrayList<>(path)`.",
            "Pruning when the sum exceeds the target is wrong with negative values.");
        P.trace("root = [5, 4, 8, 11, null, 13, 4, 7, 2, null, null, 5, 1], targetSum = 22", () -> { reset(F.Tr("5,4,8,11,null,13,4,7,2,null,null,5,1")); return pathSum(R0, 22); });
        P.edge("Empty tree", "root = [], targetSum = 0", "[]", () -> { reset(null); return pathSum(null, 0); }, "No root-to-leaf paths at all.");
        P.edge("Negative values", "root = [-2, null, -3], targetSum = -5", "[[-2, -3]]", () -> { reset(F.Tr("-2,null,-3")); return pathSum(R0, -5); }, "No early pruning, so negatives work.");
        P.edge("Match at a non-leaf", "root = [1, 2], targetSum = 1", "[]", () -> { reset(F.Tr("1,2")); return pathSum(R0, 1); }, "Node 1 isn't a leaf, so it can't end a path.");
        P.done();

        P.begin("Flatten Binary Tree to Linked List", "flatten");
        P.category("Trees · Morris-style rewiring");
        P.summary("Flatten a tree into a right-leaning list in preorder, in place. For each node with a left child, find the rightmost node of its left subtree, attach the node's right subtree there, then move the left subtree to the right. Continue down the right pointers.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each edge is walked a constant number of times; no recursion stack.");
        P.ideas("In preorder, the right subtree comes right after the last node of the left subtree.",
            "That last node is the rightmost node of the left subtree.",
            "Splice: `rightmost.right = node.right; node.right = node.left; node.left = null`.");
        P.pits("Forgetting to set `left = null`.",
            "Recursive solutions work but use O(h) stack.");
        P.trace("root = [1, 2, 5, 3, 4, null, 6]", () -> { reset(F.Tr("1,2,5,3,4,null,6")); flatten(R0); return R0; });
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); flatten(null); return null; }, "Nothing to do.");
        P.edge("Already flat", "root = [1, null, 2, null, 3]", "[1,null,2,null,3]", () -> { reset(F.Tr("1,null,2,null,3")); flatten(R0); return R0; }, "No left children to move.");
        P.edge("Left chain", "root = [1, 2, null, 3]", "[1,null,2,null,3]", () -> { reset(F.Tr("1,2,null,3")); flatten(R0); return R0; }, "Each left child swings to the right.");
        P.done();

        P.begin("Maximal Square", "maximalSquare");
        P.category("2D DP · Square side lengths");
        P.summary("Find the largest square of 1s. `dp[r][c]` is the side of the largest all-1 square whose bottom-right corner is (r, c): one more than the smallest of its top, left and top-left neighbours. Return the largest side squared.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Can use one row.");
        P.ideas("A square of side s ending at (r, c) needs squares of side s − 1 ending above, left and diagonally.",
            "So the side is limited by the smallest of the three, plus one.",
            "A padded (m+1)×(n+1) table avoids border checks.");
        P.pits("Returning the side instead of the area.",
            "Using max instead of min.");
        P.trace("matrix = [[1,0,1,0,0],[1,0,1,1,1],[1,1,1,1,1],[1,0,0,1,0]]", () -> maximalSquare(F.grid("10100", "10111", "11111", "10010")));
        P.edge("All zeros", "matrix = [[0, 0]]", "0", () -> maximalSquare(F.grid("00")), "No square.");
        P.edge("Single 1", "matrix = [[0, 1], [1, 0]]", "1", () -> maximalSquare(F.grid("01", "10")), "Diagonal 1s don't form a 2×2.");
        P.edge("All ones", "matrix = [[1, 1], [1, 1]]", "4", () -> maximalSquare(F.grid("11", "11")), "The whole matrix.");
        P.done();

        P.begin("Vertical Order Traversal of a Binary Tree", "verticalTraversal");
        P.category("Trees · Coordinates + sort");
        P.summary("Report nodes column by column, left to right; within a column, top to bottom, and nodes at the same position sorted by value. Give every node a (column, row) coordinate with DFS, then sort all (col, row, value) triples and group by column.");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Sorting the n triples dominates.");
        P.ideas("Root is (col 0, row 0); left child is col − 1, right child is col + 1, both row + 1.",
            "Sort by column, then row, then value: this handles the tie rule.",
            "Group consecutive triples with the same column.");
        P.pits("BFS order alone doesn't sort ties at the same position by value.",
            "Confusing this with LeetCode 314, which keeps BFS order for ties.");
        P.trace("root = [3, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("3,9,20,null,null,15,7")); return verticalTraversal(R0); });
        P.edge("Tie at the same position", "root = [1, 2, 3, 4, 6, 5, 7]", "[[4], [2], [1, 5, 6], [3], [7]]", () -> { reset(F.Tr("1,2,3,4,6,5,7")); return verticalTraversal(R0); }, "5 and 6 share (col 0, row 2) and are sorted by value.");
        P.edge("Single node", "root = [1]", "[[1]]", () -> { reset(F.Tr("1")); return verticalTraversal(R0); }, "One column.");
        P.done();

        P.begin("Longest Increasing Path in a Matrix", "longestIncreasingPath");
        P.category("Graphs · DFS + memoisation");
        P.summary("Find the longest strictly increasing path moving in 4 directions. The longest path starting at a cell is 1 + the longest from any bigger neighbour. Strict increase means no cycles, so memoise each cell's answer and every cell is solved once.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Each cell's answer is computed once.");
        P.ideas("Edges only go from smaller to larger values: the graph is a DAG.",
            "`memo[r][c]` = longest increasing path starting at (r, c).",
            "A visited set isn't needed; strictness prevents revisiting a cell on the same path.",
            "Topological sort by out-degree (peeling) is the BFS alternative.");
        P.pits("DFS without memoisation is exponential.",
            "Allowing equal neighbours creates cycles.");
        P.trace("matrix = [[9, 9, 4], [6, 6, 8], [2, 1, 1]]", () -> longestIncreasingPath(new int[][]{{9, 9, 4}, {6, 6, 8}, {2, 1, 1}}));
        P.edge("Single cell", "matrix = [[1]]", "1", () -> longestIncreasingPath(new int[][]{{1}}), "A path of one cell.");
        P.edge("All equal", "matrix = [[7, 7], [7, 7]]", "1", () -> longestIncreasingPath(new int[][]{{7, 7}, {7, 7}}), "No strictly increasing step exists.");
        P.edge("Snake", "matrix = [[1, 2, 3], [6, 5, 4]]", "6", () -> longestIncreasingPath(new int[][]{{1, 2, 3}, {6, 5, 4}}), "1 → 2 → 3 → 4 → 5 → 6.");
        P.done();
    }

    //@code distanceK
    static List<Integer> distanceK(TreeNode root, TreeNode target, int k) {
        Map<TreeNode, TreeNode> parent = new HashMap<>();
        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            TreeNode n = stack.pop();
            if (n.left != null) { parent.put(n.left, n); stack.push(n.left); }
            if (n.right != null) { parent.put(n.right, n); stack.push(n.right); }
        }
        T.s("Record parents", "Now every node can move up as well as down.", tv(target, "active"), T.v("target", target.val, "k", k));
        Queue<TreeNode> queue = new ArrayDeque<>();
        Set<TreeNode> seen = new HashSet<>();
        queue.offer(target);
        seen.add(target);
        for (int d = 0; d < k && !queue.isEmpty(); d++) {
            for (int size = queue.size(); size > 0; size--) {
                TreeNode n = queue.poll();
                for (TreeNode next : new TreeNode[]{n.left, n.right, parent.get(n)}) {
                    if (next != null && seen.add(next)) queue.offer(next);
                }
            }
            DONE.addAll(seen); // @hide
            T.s("Distance " + (d + 1), "Expand every node at distance " + d + " to its unvisited left, right and parent.", tvq(queue, target), T.queue("queue", TreesA.qv(queue)), T.v("d", d + 1));
        }
        List<Integer> res = new ArrayList<>();
        for (TreeNode n : queue) res.add(n.val);
        return res;
    }
    //@end
    static S tvq(Collection<TreeNode> q, TreeNode target) { S s = tv(target, "active"); for (TreeNode n : q) s.m(n, "found"); return s; }

    //@code pathSum
    static List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> res = new ArrayList<>();
        dfs(root, targetSum, new ArrayList<>(), res);
        return res;
    }

    static void dfs(TreeNode node, int remain, List<Integer> path, List<List<Integer>> res) {
        if (node == null) return;
        path.add(node.val);
        remain -= node.val;
        T.call("dfs(" + node.val + ")");
        boolean leaf = node.left == null && node.right == null;
        if (leaf && remain == 0) res.add(new ArrayList<>(path));
        T.s((leaf ? "Leaf " : "Visit ") + node.val, leaf ? (remain == 0 ? "Remaining is 0 at a leaf: save " + path + "." : "Leaf with remaining " + remain + ": not a match.") : "Remaining after " + node.val + ": " + remain + ". Keep going down.",
            tv(node, leaf && remain == 0 ? "found" : leaf ? "dim" : "active"), T.arr("path", path), T.arr("res", res), T.v("remain", remain));
        dfs(node.left, remain, path, res);
        dfs(node.right, remain, path, res);
        path.remove(path.size() - 1);
        T.ret();
    }
    //@end

    //@code flatten
    static void flatten(TreeNode root) {
        TreeNode node = root;
        while (node != null) {
            if (node.left != null) {
                TreeNode rightmost = node.left;
                while (rightmost.right != null) rightmost = rightmost.right;
                T.s("Rightmost of the left subtree", "For node " + node.val + ", the last preorder node on the left is " + rightmost.val + ".", tv(node, "active").m(rightmost, "compare"));
                rightmost.right = node.right;
                node.right = node.left;
                node.left = null;
                T.s("Splice", node.val + "'s right subtree hangs off " + rightmost.val + "; the left subtree moves to the right.", tv(node, "swap").m(rightmost, "compare"));
            }
            DONE.add(node); // @hide
            node = node.right;
        }
        T.n("Flat", "Every node now has only a right child, in preorder.", tv(null, ""));
    }
    //@end

    //@code maximalSquare
    static int maximalSquare(char[][] matrix) {
        int m = matrix.length, n = matrix[0].length, best = 0;
        int[][] dp = new int[m + 1][n + 1];
        for (int r = 1; r <= m; r++) {
            for (int c = 1; c <= n; c++) {
                if (matrix[r - 1][c - 1] == '1') {
                    dp[r][c] = 1 + Math.min(dp[r - 1][c - 1], Math.min(dp[r - 1][c], dp[r][c - 1]));
                    best = Math.max(best, dp[r][c]);
                    T.s("Cell (" + (r - 1) + ", " + (c - 1) + ")", "1 + min(" + dp[r - 1][c] + ", " + dp[r][c - 1] + ", " + dp[r - 1][c - 1] + ") = " + dp[r][c] + ". Largest side so far: " + best + ".",
                        T.mat("matrix", matrix).m(r - 1, c - 1, "active"), T.mat("dp (padded)", dp).m(r, c, "swap").m(r - 1, c, "compare").m(r, c - 1, "compare").m(r - 1, c - 1, "compare"), T.v("best", best));
                }
            }
        }
        return best * best;
    }
    //@end

    //@code verticalTraversal
    static List<List<Integer>> verticalTraversal(TreeNode root) {
        List<int[]> nodes = new ArrayList<>();
        collect(root, 0, 0, nodes);
        nodes.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] != b[1] ? a[1] - b[1] : a[2] - b[2]);
        T.s("Sort by (col, row, value)", "Ties at the same position end up sorted by value.", tv(null, ""), T.arr("(col, row, val)", Greedy.iv(trip(nodes))));
        List<List<Integer>> res = new ArrayList<>();
        int prevCol = Integer.MIN_VALUE;
        for (int[] t : nodes) {
            if (t[0] != prevCol) {
                res.add(new ArrayList<>());
                prevCol = t[0];
            }
            res.get(res.size() - 1).add(t[2]);
        }
        T.s("Group by column", res.size() + " columns, left to right.", T.arr("res", res));
        return res;
    }

    static void collect(TreeNode node, int col, int row, List<int[]> out) {
        if (node == null) return;
        out.add(new int[]{col, row, node.val});
        DONE.add(node); // @hide
        T.s("Node " + node.val + " at col " + col + ", row " + row, "Left children go to col − 1, right children to col + 1.", tv(node, "active"), T.arr("(col, row, val)", Greedy.iv(trip(out))));
        collect(node.left, col - 1, row + 1, out);
        collect(node.right, col + 1, row + 1, out);
    }
    //@end
    static List<int[]> trip(List<int[]> l) { List<int[]> r = new ArrayList<>(); for (int[] t : l) r.add(new int[]{t[0], t[1] * 1000 + t[2]}); return r; }

    static int[][] M0;
    //@code longestIncreasingPath
    static int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length, best = 0;
        int[][] memo = new int[m][n];
        M0 = matrix; // @hide
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                best = Math.max(best, longest(matrix, r, c, memo));
        T.n("Longest path", "The best starting cell gives a path of " + best + ".", T.mat("memo", memo), T.v("best", best));
        return best;
    }

    static int longest(int[][] g, int r, int c, int[][] memo) {
        if (memo[r][c] != 0) return memo[r][c];
        int best = 1;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        T.call("longest(" + r + ", " + c + ")");
        for (int[] d : dirs) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nc >= 0 && nr < g.length && nc < g[0].length && g[nr][nc] > g[r][c])
                best = Math.max(best, 1 + longest(g, nr, nc, memo));
        }
        memo[r][c] = best;
        T.s("Solved (" + r + ", " + c + ")", "Value " + g[r][c] + ": the longest increasing path starting here has " + best + " cell(s). Memoised.", T.mat("matrix", g).m(r, c, "active"), T.mat("memo", memo).m(r, c, "swap"));
        T.ret();
        return best;
    }
    //@end
}
