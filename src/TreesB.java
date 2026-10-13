import java.util.*;

/** Trees: BST & Advanced */
class TreesB {
    static TreeNode R0;
    static final Set<TreeNode> DONE = Collections.newSetFromMap(new IdentityHashMap<>());
    static void reset(TreeNode r) { R0 = r; DONE.clear(); }
    static S tv(TreeNode cur, String mark) {
        S s = T.tree("tree", R0);
        for (TreeNode d : DONE) s.m(d, "done");
        if (cur != null) s.p("node", cur).m(cur, mark);
        return s;
    }
    static String b(long v) { return v == Long.MIN_VALUE ? "-∞" : v == Long.MAX_VALUE ? "+∞" : String.valueOf(v); }

    static void run() {
        P.begin("Validate Binary Search Tree", "isValidBST");
        P.category("Trees · Bounds recursion");
        P.summary("Check that every node is strictly between the bounds set by its ancestors, not just bigger than its left child and smaller than its right child. Pass down an allowed range: going left tightens the upper bound, going right tightens the lower bound.");
        P.cx("O(1)", "O(n)", "O(n)", "O(h)", "Stops at the first violation.");
        P.ideas("A BST constraint is global: everything in the left subtree must be smaller than the node.",
            "Carry `(low, high)` down the recursion.",
            "Use `long` bounds so nodes equal to Integer.MIN_VALUE/MAX_VALUE still work.",
            "An in-order traversal must be strictly increasing; that's an equivalent check.");
        P.pits("Only comparing a node with its direct children misses [5, 4, 6, null, null, 3, 7].",
            "Using int bounds with Integer.MIN_VALUE as -∞ fails when a node is exactly MIN_VALUE.",
            "Allowing duplicates; this problem needs strict inequality.");
        P.trace("root = [5, 3, 8, 1, 4, 7, 9]", () -> { reset(F.Tr("5,3,8,1,4,7,9")); return isValidBST(R0); });
        P.edge("Grandchild breaks the rule", "root = [5, 4, 6, null, null, 3, 7]", "false", () -> { reset(F.Tr("5,4,6,null,null,3,7")); return isValidBST(R0); }, "3 is in the right subtree of 5 but smaller than 5.");
        P.edge("Duplicate value", "root = [2, 2, 2]", "false", () -> { reset(F.Tr("2,2,2")); return isValidBST(R0); }, "Strict inequality: equal values are not allowed.");
        P.edge("Integer limits", "root = [2147483647]", "true", () -> { reset(F.Tr("2147483647")); return isValidBST(R0); }, "long bounds make the maximum int value valid.");
        P.edge("Single node", "root = [1]", "true", () -> { reset(F.Tr("1")); return isValidBST(R0); }, "No constraints to break.");
        P.done();

        P.begin("Kth Smallest Element in a BST", "kthSmallest");
        P.category("Trees · In-order traversal");
        P.summary("An in-order traversal of a BST visits values in sorted order. Walk it iteratively with a stack: go left as far as possible, pop, count, then move right. The k-th popped node is the answer.");
        P.cx("O(h + k)", "O(h + k)", "O(n)", "O(h)", "You can stop after k pops instead of traversing the whole tree.");
        P.ideas("In-order (left, node, right) on a BST is sorted ascending.",
            "An explicit stack lets you stop early once k nodes are counted.",
            "Push the whole left spine before popping.");
        P.pits("Collecting all values into a list first is O(n) time and space.",
            "Off-by-one: k is 1-indexed.");
        P.trace("root = [5, 3, 6, 2, 4, null, null, 1], k = 3", () -> { reset(F.Tr("5,3,6,2,4,null,null,1")); return kthSmallest(R0, 3); });
        P.edge("k = 1", "root = [3, 1, 4, null, 2], k = 1", "1", () -> { reset(F.Tr("3,1,4,null,2")); return kthSmallest(R0, 1); }, "The leftmost node.");
        P.edge("k = n", "root = [2, 1, 3], k = 3", "3", () -> { reset(F.Tr("2,1,3")); return kthSmallest(R0, 3); }, "The rightmost node.");
        P.edge("Right-skewed", "root = [1, null, 2, null, 3], k = 2", "2", () -> { reset(F.Tr("1,null,2,null,3")); return kthSmallest(R0, 2); }, "No left spine at all.");
        P.done();

        P.begin("Lowest Common Ancestor of a Binary Search Tree", "lcaBST");
        P.category("Trees · BST property");
        P.summary("In a BST, if both p and q are smaller than a node, their LCA is in the left subtree; if both are bigger, it's in the right subtree. The first node where they split (or that equals one of them) is the LCA.");
        P.cx("O(1)", "O(h)", "O(h)", "O(1)", "Iterative walk down one path.");
        P.ideas("The BST ordering tells you which side both nodes are on.",
            "The split point, where p and q go different ways, is the lowest common ancestor.",
            "A node can be its own ancestor: if p equals the current node, it's the answer.");
        P.pits("Ignoring the BST property and using the general binary-tree solution (works, but misses the point).",
            "Comparing node references instead of values when the inputs are values.");
        P.trace("root = [6, 2, 8, 0, 4, 7, 9, null, null, 3, 5], p = 3, q = 5", () -> { reset(F.Tr("6,2,8,0,4,7,9,null,null,3,5")); return lcaBST(R0, F.find(R0, 3), F.find(R0, 5)).val; });
        P.edge("Split at the root", "root = [6, 2, 8, 0, 4, 7, 9], p = 2, q = 8", "6", () -> { reset(F.Tr("6,2,8,0,4,7,9")); return lcaBST(R0, F.find(R0, 2), F.find(R0, 8)).val; }, "p is left, q is right.");
        P.edge("One node is the ancestor", "root = [6, 2, 8, 0, 4, 7, 9], p = 2, q = 4", "2", () -> { reset(F.Tr("6,2,8,0,4,7,9")); return lcaBST(R0, F.find(R0, 2), F.find(R0, 4)).val; }, "2 is an ancestor of 4, so 2 is the LCA.");
        P.edge("Two nodes", "root = [2, 1], p = 2, q = 1", "2", () -> { reset(F.Tr("2,1")); return lcaBST(R0, F.find(R0, 2), F.find(R0, 1)).val; }, "The root is p itself.");
        P.done();

        P.begin("Lowest Common Ancestor of a Binary Tree", "lowestCommonAncestor");
        P.category("Trees · Post-order search");
        P.summary("Search both subtrees for p and q. A node returns itself if it is p or q; otherwise it returns whatever its subtrees found. The first node that gets a non-null answer from both sides is the LCA.");
        P.cx("O(1)", "O(n)", "O(n)", "O(h)", "Worst case visits every node.");
        P.ideas("Return value means \"I found p, q, or their LCA in this subtree\".",
            "Both sides non-null: p and q are split here, so this node is the LCA.",
            "One side non-null: pass that result up.",
            "If the current node is p or q, return it immediately; the other can only be below it.");
        P.pits("Assuming p and q are in different subtrees.",
            "Searching each node separately with a contains() helper is O(n²).");
        P.trace("root = [3, 5, 1, 6, 2, 0, 8, null, null, 7, 4], p = 5, q = 1", () -> { reset(F.Tr("3,5,1,6,2,0,8,null,null,7,4")); return lowestCommonAncestor(R0, F.find(R0, 5), F.find(R0, 1)).val; });
        P.edge("Ancestor of itself", "root = [3, 5, 1, 6, 2, 0, 8, null, null, 7, 4], p = 5, q = 4", "5", () -> { reset(F.Tr("3,5,1,6,2,0,8,null,null,7,4")); return lowestCommonAncestor(R0, F.find(R0, 5), F.find(R0, 4)).val; }, "The search returns 5 without looking below it.");
        P.edge("Two nodes", "root = [1, 2], p = 1, q = 2", "1", () -> { reset(F.Tr("1,2")); return lowestCommonAncestor(R0, F.find(R0, 1), F.find(R0, 2)).val; }, "The root is p.");
        P.edge("Deep leaves", "root = [3, 5, 1, 6, 2, 0, 8, null, null, 7, 4], p = 7, q = 8", "3", () -> { reset(F.Tr("3,5,1,6,2,0,8,null,null,7,4")); return lowestCommonAncestor(R0, F.find(R0, 7), F.find(R0, 8)).val; }, "They meet only at the root.");
        P.done();

        P.begin("Construct Binary Tree from Preorder and Inorder Traversal", "buildTree");
        P.category("Trees · Divide and conquer");
        P.summary("The first preorder value is the root. Finding it in the inorder array splits inorder into left and right subtrees, and the left part's size tells you how much of preorder belongs to the left subtree. Recurse on both parts; a map from value to inorder index keeps it O(n).");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Each node is created once; the index map is O(n).");
        P.ideas("Preorder = root, left subtree, right subtree.",
            "Inorder = left subtree, root, right subtree.",
            "The root's inorder index splits the tree; `mid - inLo` is the left subtree size.",
            "Consume preorder left to right with a single global index.");
        P.pits("Searching inorder linearly every call is O(n²).",
            "Building the right subtree before the left when using a running preorder index.",
            "Values must be unique for the map to work.");
        P.trace("preorder = [3, 9, 20, 15, 7], inorder = [9, 3, 15, 20, 7]", () -> buildTree(new int[]{3, 9, 20, 15, 7}, new int[]{9, 3, 15, 20, 7}));
        P.edge("Single node", "preorder = [-1], inorder = [-1]", "[-1]", () -> buildTree(new int[]{-1}, new int[]{-1}), "Just the root.");
        P.edge("Left-skewed", "preorder = [3, 2, 1], inorder = [1, 2, 3]", "[3,2,null,1]", () -> buildTree(new int[]{3, 2, 1}, new int[]{1, 2, 3}), "Every root is the last inorder element of its range.");
        P.edge("Right-skewed", "preorder = [1, 2, 3], inorder = [1, 2, 3]", "[1,null,2,null,3]", () -> buildTree(new int[]{1, 2, 3}, new int[]{1, 2, 3}), "Every left range is empty.");
        P.done();

        P.begin("Binary Tree Maximum Path Sum", "maxPathSum");
        P.category("Trees · Post-order with global best");
        P.summary("A path can bend at most once, at its highest node. For each node, compute the best downward path from it (`node + max(0, left, right)`) and, separately, the best path bending there (`node + left + right`, dropping negative sides). Track the best bend seen.");
        P.cx("O(n)", "O(n)", "O(n)", "O(h)", "One post-order traversal.");
        P.ideas("Return to the parent only a single downward branch, because a path can't fork twice.",
            "Clamp negative branch sums to 0: skipping a branch is better than taking it.",
            "Update the global answer with both branches at the current node.",
            "Initialise the answer to the smallest int so all-negative trees work.");
        P.pits("Initialising the best to 0 fails for trees with only negative values.",
            "Returning `node + left + right` to the parent creates an invalid forked path.");
        P.trace("root = [-10, 9, 20, null, null, 15, 7]", () -> { reset(F.Tr("-10,9,20,null,null,15,7")); return maxPathSum(R0); });
        P.edge("All negative", "root = [-3, -2, -1]", "-1", () -> { reset(F.Tr("-3,-2,-1")); return maxPathSum(R0); }, "The best path is the single node -1.");
        P.edge("Single node", "root = [5]", "5", () -> { reset(F.Tr("5")); return maxPathSum(R0); }, "A path of one node.");
        P.edge("Small positive tree", "root = [1, 2, 3]", "6", () -> { reset(F.Tr("1,2,3")); return maxPathSum(R0); }, "2 → 1 → 3 bends at the root.");
        P.edge("Skip a negative child", "root = [2, -1]", "2", () -> { reset(F.Tr("2,-1")); return maxPathSum(R0); }, "The -1 branch is clamped to 0.");
        P.done();

        P.begin("Serialize and Deserialize Binary Tree", "Codec");
        P.category("Trees · Preorder with null markers");
        P.summary("Turn a tree into a string and back. Write a preorder traversal with `#` for null children; the markers make the shape unambiguous. To rebuild, read tokens in the same preorder: a number creates a node and recursively builds its left and right subtrees, `#` returns null.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Every node and null child is written and read once.");
        P.ideas("Preorder plus explicit nulls encodes the exact shape.",
            "Deserialization mirrors serialization exactly: same order, same recursion.",
            "A queue of tokens tracks the read position.");
        P.pits("Leaving out null markers: [1, 2] and [1, null, 2] would serialize the same.",
            "String concatenation in a loop is O(n²); use a StringBuilder.",
            "Splitting on \",\" and forgetting negative numbers have a '-' sign (parseInt handles it).");
        P.trace("root = [1, 2, 3, null, null, 4, 5]", () -> { reset(F.Tr("1,2,3,null,null,4,5")); Codec c = new Codec(); return c.deserialize(c.serialize(R0)); });
        P.edge("Empty tree", "root = []", "[]", () -> { reset(null); Codec c = new Codec(); return c.deserialize(c.serialize(null)); }, "Serializes to \"#\".");
        P.edge("Negative values", "root = [-1, -2]", "[-1,-2]", () -> { reset(F.Tr("-1,-2")); Codec c = new Codec(); return c.deserialize(c.serialize(R0)); }, "parseInt reads the minus sign.");
        P.edge("Right-skewed", "root = [1, null, 2]", "[1,null,2]", () -> { reset(F.Tr("1,null,2")); Codec c = new Codec(); return c.deserialize(c.serialize(R0)); }, "The # after 1 records the missing left child.");
        P.done();
    }

    //@code isValidBST
    static boolean isValidBST(TreeNode root) {
        return valid(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    static boolean valid(TreeNode node, long low, long high) {
        if (node == null) return true;
        T.call("valid(" + node.val + ", " + b(low) + ", " + b(high) + ")");
        if (node.val <= low || node.val >= high) {
            T.n("Out of range", node.val + " must be between " + b(low) + " and " + b(high) + ", but it isn't.", tv(node, "error"), T.v("low", b(low), "high", b(high)));
            T.ret();
            return false;
        }
        DONE.add(node); // @hide
        T.n("In range", b(low) + " < " + node.val + " < " + b(high) + ". Left children must be < " + node.val + ", right children > " + node.val + ".", tv(node, "active"), T.v("low", b(low), "high", b(high)));
        boolean ok = valid(node.left, low, node.val) && valid(node.right, node.val, high);
        T.ret();
        return ok;
    }
    //@end

    //@code kthSmallest
    static int kthSmallest(TreeNode root, int k) {
        Deque<TreeNode> stack = new ArrayDeque<>();
        TreeNode curr = root;
        int count = 0;
        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }
            T.s("Go left as far as possible", "The stack holds the path; its top is the smallest unvisited value.", tv(stack.peek(), "visit"), T.stack("stack", TreesA.qv(stack)));
            curr = stack.pop();
            count++;
            DONE.add(curr); // @hide
            if (count == k) {
                T.n("k-th visited", curr.val + " is visit number " + count + " in sorted order.", tv(curr, "found"), T.stack("stack", TreesA.qv(stack)), T.v("count", count, "k", k));
                return curr.val;
            }
            T.s("Visit " + curr.val, "Sorted visit number " + count + ". Next, explore its right subtree.", tv(curr, "active"), T.stack("stack", TreesA.qv(stack)), T.v("count", count, "k", k));
            curr = curr.right;
        }
        return -1;
    }
    //@end

    //@code lcaBST
    static TreeNode lowestCommonAncestorBST(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode node = root;
        while (node != null) {
            if (p.val < node.val && q.val < node.val) {
                T.s("Both are smaller", p.val + " and " + q.val + " are both < " + node.val + ": go left.", tv(node, "visit").m(p, "compare").m(q, "compare"));
                node = node.left;
            } else if (p.val > node.val && q.val > node.val) {
                T.s("Both are bigger", p.val + " and " + q.val + " are both > " + node.val + ": go right.", tv(node, "visit").m(p, "compare").m(q, "compare"));
                node = node.right;
            } else {
                T.n("They split here", p.val + " and " + q.val + " are on different sides of " + node.val + " (or one of them is " + node.val + "). This is the LCA.", tv(node, "found").m(p, "compare").m(q, "compare"));
                return node;
            }
        }
        return null;
    }
    //@end
    static TreeNode lcaBST(TreeNode r, TreeNode p, TreeNode q) { return lowestCommonAncestorBST(r, p, q); }

    static TreeNode P0, Q0;

    //@code lowestCommonAncestor
    static TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) return null;
        if (root == p || root == q) {
            DONE.add(root); // @hide
            T.n("Found " + root.val, root.val + " is one of the targets. Return it.", tv(root, "found").m(p, "compare").m(q, "compare").m(root, "found"));
            return root;
        }
        T.call("lca(" + root.val + ")");
        T.n("Search both sides of " + root.val, "Look for " + p.val + " and " + q.val + " in the left and right subtrees.", tv(root, "active").m(p, "compare").m(q, "compare"));
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);
        TreeNode result = (left != null && right != null) ? root : (left != null ? left : right);
        DONE.add(root); // @hide
        T.s(left != null && right != null ? "Both sides found something" : "Pass the result up",
            left != null && right != null ? "Left returned " + left.val + " and right returned " + right.val + ", so " + root.val + " is the LCA."
                : "Left: " + (left == null ? "null" : left.val) + ", right: " + (right == null ? "null" : right.val) + ". Return " + (result == null ? "null" : result.val) + ".",
            tv(root, left != null && right != null ? "found" : "visit").m(p, "compare").m(q, "compare"));
        T.ret();
        return result;
    }
    //@end

    static TreeNode BUILT, PAR;
    static boolean LEFT;
    static void attach(TreeNode node) { if (BUILT == null) BUILT = node; if (PAR != null) { if (LEFT) PAR.left = node; else PAR.right = node; } }

    //@code buildTree
    static int pre;

    static TreeNode buildTree(int[] preorder, int[] inorder) {
        Map<Integer, Integer> index = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) index.put(inorder[i], i);
        pre = 0;
        BUILT = null; PAR = null; // @hide
        return build(preorder, inorder, index, 0, inorder.length - 1);
    }

    static TreeNode build(int[] preorder, int[] inorder, Map<Integer, Integer> index, int lo, int hi) {
        if (lo > hi) return null;
        int val = preorder[pre++];
        TreeNode node = new TreeNode(val);
        int mid = index.get(val);
        attach(node); // @hide
        T.call("build(" + lo + ", " + hi + ")");
        T.s("Next preorder value is a root", val + " is the root of inorder[" + lo + ".." + hi + "]. It sits at inorder index " + mid + ": left subtree is " + lo + ".." + (mid - 1) + ", right is " + (mid + 1) + ".." + hi + ".",
            T.arr("preorder", preorder).p("pre", pre - 1).range(0, pre - 2, "done").m(pre - 1, "active"), T.arr("inorder", inorder).range(lo, mid - 1, "visit").m(mid, "active").range(mid + 1, hi, "compare"), T.tree("built so far", BUILT).m(node, "swap"));
        PAR = node; LEFT = true; // @hide
        node.left = build(preorder, inorder, index, lo, mid - 1);
        PAR = node; LEFT = false; // @hide
        node.right = build(preorder, inorder, index, mid + 1, hi);
        T.ret();
        return node;
    }
    //@end

    //@code maxPathSum
    static int bestSum;

    static int maxPathSum(TreeNode root) {
        bestSum = Integer.MIN_VALUE;
        gain(root);
        return bestSum;
    }

    static int gain(TreeNode node) {
        if (node == null) return 0;
        T.call("gain(" + node.val + ")");
        int left = Math.max(0, gain(node.left));
        int right = Math.max(0, gain(node.right));
        bestSum = Math.max(bestSum, node.val + left + right);
        DONE.add(node); // @hide
        T.s("Best path bending at " + node.val, node.val + " + " + left + " + " + right + " = " + (node.val + left + right) + ". Best so far: " + bestSum + ". Return " + (node.val + Math.max(left, right)) + " (one branch only) to the parent.",
            tv(node, "found"), T.v("left", left, "right", right, "bestSum", bestSum, "returns", node.val + Math.max(left, right)));
        T.ret();
        return node.val + Math.max(left, right);
    }
    //@end

    static final List<String> OUT = new ArrayList<>();

    //@code Codec
    static class Codec {
        String serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            write(root, sb);
            T.s("Serialized", "Preorder with # for every null child.", T.tree("tree", R0), T.arr("tokens", sb.toString().split(",")));
            return sb.toString();
        }

        private void write(TreeNode node, StringBuilder sb) {
            if (sb.length() > 0) sb.append(',');
            if (node == null) {
                sb.append('#');
                return;
            }
            sb.append(node.val);
            DONE.add(node); // @hide
            T.s("Write " + node.val, "Preorder: write the node, then its left subtree, then its right subtree.", tv(node, "active"), T.arr("tokens", sb.toString().split(",")));
            write(node.left, sb);
            write(node.right, sb);
        }

        TreeNode deserialize(String data) {
            Queue<String> tokens = new ArrayDeque<>(Arrays.asList(data.split(",")));
            BUILT = null; PAR = null; // @hide
            return read(tokens);
        }

        private TreeNode read(Queue<String> tokens) {
            String t = tokens.poll();
            if (t.equals("#")) return null;
            TreeNode node = new TreeNode(Integer.parseInt(t));
            attach(node); // @hide
            T.s("Read " + t, "A number makes a node; its left and right subtrees come next in the stream.", T.queue("remaining tokens", tokens), T.tree("rebuilt", BUILT).m(node, "swap"));
            PAR = node; LEFT = true; // @hide
            node.left = read(tokens);
            PAR = node; LEFT = false; // @hide
            node.right = read(tokens);
            return node;
        }
    }
    //@end
}
