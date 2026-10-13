import java.util.*;

/** Trees: Traversal */
class TreesA {
    static TreeNode R0, R1;
    static final Set<TreeNode> DONE = Collections.newSetFromMap(new LinkedHashMap<>());
    static final Map<TreeNode, Object> NOTE = new LinkedHashMap<>();

    static void reset(TreeNode r) { R0 = r; DONE.clear(); NOTE.clear(); }
    static String nm(TreeNode n) { return n == null ? "null" : String.valueOf(n.val); }
    static S tv(TreeNode cur, String mark) {
        S s = T.tree("tree", R0);
        for (TreeNode d : DONE) s.m(d, "done");
        if (cur != null) s.p("node", cur).m(cur, mark);
        return s;
    }
    static S notes(String name) {
        Map<String, Object> m = new LinkedHashMap<>();
        for (Map.Entry<TreeNode, Object> e : NOTE.entrySet()) m.put("node " + e.getKey().val, e.getValue());
        return T.map(name, m);
    }
    static List<Integer> qv(Collection<TreeNode> q) { List<Integer> l = new ArrayList<>(); for (TreeNode t : q) l.add(t.val); return l; }

    static void run() {
        P.begin("Maximum Depth of Binary Tree", "maxDepth");
        P.category("Trees · DFS recursion");
        P.summary("The depth of a tree is 1 (for the root) plus the deeper of its two subtrees. An empty tree has depth 0. Recursion computes it bottom-up: each node waits for its children's depths.");
        P.cx("O(n)", "O(n)", "O(n)", "O(h)", "Every node is visited once; the call stack is as deep as the tree (h), O(n) for a skewed tree.");
        P.ideas("Base case: `null` has depth 0.",
            "Recursive case: `1 + max(depth(left), depth(right))`.",
            "BFS counting levels gives the same answer iteratively.");
        P.pits("Returning 1 for a null node (off by one).",
            "Very deep skewed trees can overflow the call stack; BFS avoids recursion.");
        P.trace("root = [3, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("3,9,20,null,null,15,7")); return maxDepth(R0); });
        P.edge("Empty tree", "root = []", "0", () -> { reset(null); return maxDepth(null); }, "The base case returns immediately.");
        P.edge("Single node", "root = [1]", "1", () -> { reset(F.Tr("1")); return maxDepth(R0); }, "Both children return 0.");
        P.edge("Skewed tree", "root = [1, 2, null, 3, null, 4]", "4", () -> { reset(F.Tr("1,2,null,3,null,4")); return maxDepth(R0); }, "A chain: the recursion is as deep as the number of nodes.");
        P.edge("Unbalanced", "root = [1, null, 2]", "2", () -> { reset(F.Tr("1,null,2")); return maxDepth(R0); }, "Only the right subtree adds depth.");
        P.done();

        P.begin("Invert Binary Tree", "invertTree");
        P.category("Trees · DFS recursion");
        P.summary("Mirror a tree: swap every node's left and right children. Swap at the current node, then invert both subtrees (or the other way round); every node gets swapped exactly once.");
        P.cx("O(n)", "O(n)", "O(n)", "O(h)", "One swap per node; recursion depth h.");
        P.ideas("Mirroring a tree = swap the children, then mirror each child.",
            "Pre-order and post-order both work; in-order does not (it re-swaps one side).",
            "Return the root so the call can be chained.");
        P.pits("Overwriting `root.left` before saving it.",
            "Inverting only one level.");
        P.trace("root = [4, 2, 7, 1, 3, 6, 9]", () -> { reset(F.Tr("4,2,7,1,3,6,9")); return invertTree(R0); });
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); return invertTree(null); }, "Nothing to swap.");
        P.edge("Single node", "root = [1]", "[1]", () -> { reset(F.Tr("1")); return invertTree(R0); }, "Swapping two nulls changes nothing.");
        P.edge("One child", "root = [2, 1]", "[2, null, 1]", () -> { reset(F.Tr("2,1")); return invertTree(R0); }, "The left child moves to the right.");
        P.done();

        P.begin("Same Tree", "isSameTree");
        P.category("Trees · Parallel DFS");
        P.summary("Two trees are the same when they have the same shape and values. Walk both at once: two nulls match, one null doesn't, and otherwise the values must match and both pairs of subtrees must be the same.");
        P.cx("O(1)", "O(n)", "O(n)", "O(h)", "Stops at the first difference.");
        P.ideas("Compare structure and value at each pair of nodes.",
            "Both null → true; exactly one null → false.",
            "`&&` short-circuits, so a mismatch on the left skips the right side.");
        P.pits("Comparing only in-order traversals: different shapes can give the same sequence.",
            "Checking `p.val == q.val` before checking for null.");
        P.trace("p = [1, 2, 3], q = [1, 2, 3]", () -> { reset(F.Tr("1,2,3")); R1 = F.Tr("1,2,3"); return isSameTree(R0, R1); });
        P.edge("Different shape", "p = [1, 2], q = [1, null, 2]", "false", () -> { reset(F.Tr("1,2")); R1 = F.Tr("1,null,2"); return isSameTree(R0, R1); }, "2 is a left child in p and a right child in q.");
        P.edge("Different values", "p = [1, 2, 1], q = [1, 1, 2]", "false", () -> { reset(F.Tr("1,2,1")); R1 = F.Tr("1,1,2"); return isSameTree(R0, R1); }, "The left children differ.");
        P.edge("Both empty", "p = [], q = []", "true", () -> { reset(null); R1 = null; return isSameTree(null, null); }, "Two nulls match.");
        P.edge("One empty", "p = [1], q = []", "false", () -> { reset(F.Tr("1")); R1 = null; return isSameTree(R0, null); }, "Only one side has a node.");
        P.done();

        P.begin("Balanced Binary Tree", "isBalanced");
        P.category("Trees · Bottom-up height");
        P.summary("A tree is height-balanced when, at every node, the two subtree heights differ by at most 1. Compute heights bottom-up and return -1 as soon as any subtree is unbalanced, so the whole check is one pass.");
        P.cx("O(n)", "O(n)", "O(n)", "O(h)", "Each node's height is computed once.");
        P.ideas("Checking balance top-down with a separate height function is O(n²) on skewed trees.",
            "Return height when balanced and -1 when not, in a single recursion.",
            "Once a child reports -1, pass -1 straight up.");
        P.pits("Only checking the root's two subtrees; every node must be balanced.",
            "Confusing balanced with complete or full.");
        P.trace("root = [3, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("3,9,20,null,null,15,7")); return isBalanced(R0); });
        P.edge("Unbalanced deep left", "root = [1, 2, 2, 3, 3, null, null, 4, 4]", "false", () -> { reset(F.Tr("1,2,2,3,3,null,null,4,4")); return isBalanced(R0); }, "At the root, left height 3 vs right height 1.");
        P.edge("Empty tree", "root = []", "true", () -> { reset(null); return isBalanced(null); }, "An empty tree is balanced.");
        P.edge("Chain of three", "root = [1, null, 2, null, 3]", "false", () -> { reset(F.Tr("1,null,2,null,3")); return isBalanced(R0); }, "At node 1: left 0, right 2.");
        P.edge("Balanced everywhere except deep inside", "root = [1, 2, 3, 4, null, null, null, 5]", "false", () -> { reset(F.Tr("1,2,3,4,null,null,null,5")); return isBalanced(R0); }, "Node 2 has left height 2 and right height 0.");
        P.done();

        P.begin("Diameter of Binary Tree", "diameterOfBinaryTree");
        P.category("Trees · Height with a global best");
        P.summary("The diameter is the longest path (in edges) between any two nodes. The longest path through a node is `height(left) + height(right)`. Compute heights bottom-up and keep the best sum seen.");
        P.cx("O(n)", "O(n)", "O(n)", "O(h)", "One post-order traversal.");
        P.ideas("Every path has a highest node; at that node it's left height + right height.",
            "The recursion returns height; the diameter is tracked on the side.",
            "The path doesn't have to pass through the root.");
        P.pits("Assuming the diameter goes through the root.",
            "Counting nodes instead of edges.");
        P.trace("root = [1, 2, 3, 4, 5]", () -> { reset(F.Tr("1,2,3,4,5")); return diameterOfBinaryTree(R0); });
        P.edge("Single node", "root = [1]", "0", () -> { reset(F.Tr("1")); return diameterOfBinaryTree(R0); }, "No edges.");
        P.edge("Two nodes", "root = [1, 2]", "1", () -> { reset(F.Tr("1,2")); return diameterOfBinaryTree(R0); }, "One edge.");
        P.edge("Longest path avoids the root", "root = [1, 2, null, 3, 4, 5, null, null, 6]", "4", () -> { reset(F.Tr("1,2,null,3,4,5,null,null,6")); return diameterOfBinaryTree(R0); }, "5-3-2-4-6 bends at node 2, below the root.");
        P.done();

        P.begin("Binary Tree Level Order Traversal", "levelOrder");
        P.category("Trees · BFS by level");
        P.summary("Return node values level by level, left to right. Use a queue: at the start of each round, the queue holds exactly one level. Pop that many nodes, record their values, and push their children for the next level.");
        P.cx("O(n)", "O(n)", "O(n)", "O(w)", "w = the widest level, up to n/2.");
        P.ideas("A queue processes nodes in the order they were discovered: breadth-first.",
            "Snapshot `size = queue.size()` to know where the current level ends.",
            "Children are added left before right.");
        P.pits("Forgetting the size snapshot mixes levels together.",
            "Adding null children to the queue.");
        P.trace("root = [3, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("3,9,20,null,null,15,7")); return levelOrder(R0); });
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); return levelOrder(null); }, "Return an empty list.");
        P.edge("Single node", "root = [1]", "[[1]]", () -> { reset(F.Tr("1")); return levelOrder(R0); }, "One level.");
        P.edge("Skewed", "root = [1, 2, null, 3]", "[[1], [2], [3]]", () -> { reset(F.Tr("1,2,null,3")); return levelOrder(R0); }, "Each level has one node.");
        P.done();

        P.begin("Binary Tree Right Side View", "rightSideView");
        P.category("Trees · BFS by level");
        P.summary("Standing on the right, you see the last node of each level. Do a level-order traversal and record the value of the last node popped in every level.");
        P.cx("O(n)", "O(n)", "O(n)", "O(w)", "Every node is visited once.");
        P.ideas("The rightmost node of a level is the last one popped in that round.",
            "Left children still matter: a left node is visible if the right side is shorter.",
            "A DFS visiting right first and recording the first node at each depth also works.");
        P.pits("Only following right children misses deeper left nodes.",
            "Recording the first node of each level instead of the last.");
        P.trace("root = [1, 2, 3, null, 5, null, 4]", () -> { reset(F.Tr("1,2,3,null,5,null,4")); return rightSideView(R0); });
        P.edge("Left side visible", "root = [1, 2, 3, 4]", "[1, 3, 4]", () -> { reset(F.Tr("1,2,3,4")); return rightSideView(R0); }, "4 is a left descendant but the last node on its level.");
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); return rightSideView(null); }, "Nothing to see.");
        P.edge("Only left children", "root = [1, 2, null, 3]", "[1, 2, 3]", () -> { reset(F.Tr("1,2,null,3")); return rightSideView(R0); }, "Each level has a single node.");
        P.done();

        P.begin("Binary Tree Zigzag Level Order Traversal", "zigzagLevelOrder");
        P.category("Trees · BFS with direction");
        P.summary("Like level order, but alternate direction each level: left-to-right, then right-to-left. Do a normal BFS and, on odd levels, insert values at the front of the level's list instead of the back.");
        P.cx("O(n)", "O(n)", "O(n)", "O(w)", "Adding to the front of a LinkedList is O(1).");
        P.ideas("The traversal order never changes; only where values go in the level list.",
            "Flip a boolean after each level.",
            "`LinkedList.addFirst` reverses a level without a separate reverse step.");
        P.pits("Changing the order children are queued breaks later levels.",
            "Reversing with `Collections.reverse` is fine but an extra pass.");
        P.trace("root = [3, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("3,9,20,null,null,15,7")); return zigzagLevelOrder(R0); });
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); return zigzagLevelOrder(null); }, "No levels.");
        P.edge("Three full levels", "root = [1, 2, 3, 4, 5, 6, 7]", "[[1], [3, 2], [4, 5, 6, 7]]", () -> { reset(F.Tr("1,2,3,4,5,6,7")); return zigzagLevelOrder(R0); }, "The third level reads left to right again.");
        P.edge("Single node", "root = [1]", "[[1]]", () -> { reset(F.Tr("1")); return zigzagLevelOrder(R0); }, "One level.");
        P.done();
    }

    //@code maxDepth
    static int maxDepth(TreeNode root) {
        if (root == null) return 0;
        T.call("maxDepth(" + root.val + ")");
        T.s("Visit node " + root.val, "First find the depths of its left and right subtrees.", tv(root, "active"), notes("depth"));
        int left = maxDepth(root.left);
        int right = maxDepth(root.right);
        int depth = 1 + Math.max(left, right);
        DONE.add(root); NOTE.put(root, depth); // @hide
        T.s("Node " + root.val + " is done", "Left depth " + left + ", right depth " + right + ", so depth = 1 + max = " + depth + ".", tv(root, "found"), notes("depth").m("node " + root.val, "swap"), T.v("left", left, "right", right, "depth", depth));
        T.ret();
        return depth;
    }
    //@end

    //@code invertTree
    static TreeNode invertTree(TreeNode root) {
        if (root == null) return null;
        T.call("invertTree(" + root.val + ")");
        TreeNode tmp = root.left;
        root.left = root.right;
        root.right = tmp;
        DONE.add(root); // @hide
        T.s("Swap children of " + root.val, "Left is now " + nm(root.left) + ", right is now " + nm(root.right) + ". Then invert each side.", tv(root, "swap").m(root.left, "visit").m(root.right, "visit"));
        invertTree(root.left);
        invertTree(root.right);
        T.ret();
        return root;
    }
    //@end

    static S pq(TreeNode p, TreeNode q, String mark) {
        S a = T.tree("p", R0); if (p != null) a.p("p", p).m(p, mark);
        return a;
    }
    static S qq(TreeNode q, String mark) {
        S b = T.tree("q", R1); if (q != null) b.p("q", q).m(q, mark);
        return b;
    }

    //@code isSameTree
    static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) {
            T.n("Shape differs", "One side is null (" + nm(p) + " vs " + nm(q) + "), the other isn't.", pq(p, q, "error"), qq(q, "error"));
            return false;
        }
        T.call("isSameTree(" + p.val + ", " + q.val + ")");
        T.n("Compare values", p.val + (p.val == q.val ? " = " : " ≠ ") + q.val + (p.val == q.val ? ". Now compare the subtrees." : "."), pq(p, q, p.val == q.val ? "done" : "error"), qq(q, p.val == q.val ? "done" : "error"));
        boolean same = p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
        T.ret();
        return same;
    }
    //@end

    //@code isBalanced
    static boolean isBalanced(TreeNode root) {
        return height(root) != -1;
    }

    static int height(TreeNode node) {
        if (node == null) return 0;
        T.call("height(" + node.val + ")");
        int left = height(node.left);
        int right = left == -1 ? -1 : height(node.right);
        int h = (left == -1 || right == -1 || Math.abs(left - right) > 1) ? -1 : 1 + Math.max(left, right);
        DONE.add(node); NOTE.put(node, h == -1 ? "unbalanced" : h); // @hide
        T.s("Height of " + node.val, h == -1
                ? (left == -1 || right == -1 ? "A subtree is already unbalanced, so pass -1 up." : "Left " + left + " vs right " + right + " differ by more than 1: unbalanced.")
                : "Left " + left + ", right " + right + ": balanced, height " + h + ".",
            tv(node, h == -1 ? "error" : "found"), notes("height").m("node " + node.val, h == -1 ? "error" : "swap"), T.v("left", left, "right", right, "h", h));
        T.ret();
        return h;
    }
    //@end

    //@code diameterOfBinaryTree
    static int best;

    static int diameterOfBinaryTree(TreeNode root) {
        best = 0;
        depth(root);
        return best;
    }

    static int depth(TreeNode node) {
        if (node == null) return 0;
        T.call("depth(" + node.val + ")");
        int left = depth(node.left);
        int right = depth(node.right);
        best = Math.max(best, left + right);
        DONE.add(node); NOTE.put(node, left + 1 > right + 1 ? left + 1 : right + 1); // @hide
        T.s("Path through " + node.val, "Left height " + left + " + right height " + right + " = " + (left + right) + " edges. Best so far: " + best + ".", tv(node, "found"), notes("height"), T.v("left", left, "right", right, "best", best));
        T.ret();
        return 1 + Math.max(left, right);
    }
    //@end

    //@code levelOrder
    static List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            T.s("Start a level", size + " node(s) in the queue: exactly this level.", tv(null, ""), T.queue("queue", qv(queue)), T.arr("res", res));
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
                DONE.add(node); // @hide
                T.s("Pop " + node.val, "Record it and queue its children.", tv(node, "active"), T.queue("queue", qv(queue)), T.arr("level", level));
            }
            res.add(level);
        }
        T.n("All levels done", res.size() + " levels.", tv(null, ""), T.arr("res", res));
        return res;
    }
    //@end

    //@code rightSideView
    static List<Integer> rightSideView(TreeNode root) {
        List<Integer> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (i == size - 1) res.add(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
                DONE.add(node); // @hide
                T.s(i == size - 1 ? "Last on its level: visible" : "Pop " + node.val, i == size - 1 ? node.val + " is the rightmost node of this level." : node.val + " is hidden behind a node to its right.",
                    tv(node, i == size - 1 ? "found" : "visit"), T.queue("queue", qv(queue)), T.arr("res", res));
            }
        }
        return res;
    }
    //@end

    //@code zigzagLevelOrder
    static List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) return res;
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);
        boolean leftToRight = true;
        while (!queue.isEmpty()) {
            int size = queue.size();
            LinkedList<Integer> level = new LinkedList<>();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (leftToRight) level.addLast(node.val);
                else level.addFirst(node.val);
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
                DONE.add(node); // @hide
                T.s("Pop " + node.val, (leftToRight ? "Left-to-right level: append " : "Right-to-left level: put at the front: ") + node.val + ".", tv(node, "active"), T.queue("queue", qv(queue)), T.arr("level", level), T.v("leftToRight", leftToRight));
            }
            res.add(level);
            leftToRight = !leftToRight;
        }
        T.n("Done", "Directions alternated every level.", tv(null, ""), T.arr("res", res));
        return res;
    }
    //@end
}
