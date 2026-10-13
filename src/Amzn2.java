import java.util.*;

/** Amazon-tagged: Mixed */
class Amzn2 {
    static void run() {
        P.begin("Copy List with Random Pointer", "copyRandomList");
        P.category("Linked List · Hash map of copies");
        P.summary("Deep-copy a list where each node also has a `random` pointer to any node (or null). First pass: create a copy of every node and map original → copy. Second pass: set each copy's `next` and `random` by looking up the originals' targets in the map.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "The interleaving trick brings space to O(1).");
        P.ideas("A random pointer may point forward, so copies must exist before wiring.",
            "The map translates any original node into its copy.",
            "`map.get(null)` returns null, which handles null pointers for free.");
        P.pits("Pointing a copy's random at the original node.",
            "Trying to wire random pointers in a single pass before their targets exist.");
        P.trace("head = [[7, null], [13, 0], [11, 4], [10, 2], [1, 0]]", () -> show(copyRandomList(buildR(new int[][]{{7, -1}, {13, 0}, {11, 4}, {10, 2}, {1, 0}}))));
        P.edge("Empty list", "head = []", "[]", () -> show(copyRandomList(null)), "Return null.");
        P.edge("Random points to itself", "head = [[1, 0]]", "[[1, 0]]", () -> show(copyRandomList(buildR(new int[][]{{1, 0}}))), "The copy's random must point to the copy, not the original.");
        P.edge("All randoms null", "head = [[3, null], [3, null]]", "[[3, null], [3, null]]", () -> show(copyRandomList(buildR(new int[][]{{3, -1}, {3, -1}}))), "Duplicate values are still separate nodes.");
        P.done();

        P.begin("Decode String", "decodeString");
        P.category("Stack · Nested brackets");
        P.summary("Decode strings like \"3[a2[c]]\" into \"accaccacc\". Use two stacks: on '[', push the current repeat count and the text built so far, then start fresh. On ']', pop them and append the inner text repeated that many times.");
        P.cx("O(n)", "O(output)", "O(output)", "O(n)", "The output can be much longer than the input.");
        P.ideas("Brackets nest, so the innermost group must finish first: a stack.",
            "Numbers can have several digits: `k = k * 10 + digit`.",
            "On ']', the text built inside the brackets is repeated and attached to the outer text.");
        P.pits("Assuming single-digit counts.",
            "String concatenation in loops is slow; use StringBuilder.");
        P.trace("s = \"3[a2[c]]\"", () -> decodeString("3[a2[c]]"));
        P.edge("Adjacent groups", "s = \"2[abc]3[cd]ef\"", "\"abcabccdcdcdef\"", () -> decodeString("2[abc]3[cd]ef"), "Each group is decoded independently.");
        P.edge("Multi-digit count", "s = \"10[a]\"", "\"aaaaaaaaaa\"", () -> decodeString("10[a]"), "The count is read digit by digit.");
        P.edge("No brackets", "s = \"abc\"", "\"abc\"", () -> decodeString("abc"), "Plain text passes through.");
        P.done();

        P.begin("Find the Duplicate Number", "findDuplicate");
        P.category("Fast and slow pointers · Cycle in an array");
        P.summary("n + 1 numbers in the range 1..n, exactly one value repeated; find it without modifying the array and in O(1) space. Treat each value as a pointer `i → nums[i]`. The duplicate value is where two pointers lead into the same index, the entrance to a cycle. Floyd's tortoise and hare finds it.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Two phases, each linear.");
        P.ideas("Index 0 is never a target (values are 1..n), so it's a safe start outside the cycle.",
            "Phase 1: slow moves 1 step, fast 2 steps, until they meet inside the cycle.",
            "Phase 2: reset one pointer to 0; move both 1 step. They meet at the cycle entrance: the duplicate.");
        P.pits("Sorting or marking negatives modifies the array, which is forbidden.",
            "A HashSet uses O(n) space.");
        P.trace("nums = [1, 3, 4, 2, 2]", () -> findDuplicate(new int[]{1, 3, 4, 2, 2}));
        P.edge("Duplicate repeated many times", "nums = [2, 2, 2, 2, 2]", "2", () -> findDuplicate(new int[]{2, 2, 2, 2, 2}), "The value can appear more than twice.");
        P.edge("Smallest case", "nums = [1, 1]", "1", () -> findDuplicate(new int[]{1, 1}), "n = 1.");
        P.edge("Duplicate at the start", "nums = [3, 1, 3, 4, 2]", "3", () -> findDuplicate(new int[]{3, 1, 3, 4, 2}), "Index 0 points into the cycle immediately.");
        P.done();

        P.begin("Valid Sudoku", "isValidSudoku");
        P.category("Hashing · Seen sets");
        P.summary("Check that the filled cells of a 9×9 board break no rule: no repeated digit in any row, column, or 3×3 box. Scan every cell once and record keys like \"5 in row 0\", \"5 in col 3\", \"5 in box 1\" in a set; a failed insert means a repeat.");
        P.cx("O(1)", "O(81)", "O(81)", "O(81)", "The board size is fixed.");
        P.ideas("The box index of (r, c) is `(r / 3) * 3 + c / 3`.",
            "One set of string keys covers all three rules.",
            "Empty cells ('.') are ignored; the board doesn't need to be solvable.");
        P.pits("Trying to check solvability; the problem only asks about the current cells.",
            "Getting the box index formula wrong.");
        P.trace("board = LeetCode example 1 (valid)", () -> isValidSudoku(F.grid("53..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1", "7...2...6", ".6....28.", "...419..5", "....8..79")));
        P.edge("Repeat in a box", "board = example 1 with an 8 at the top-left", "false", () -> isValidSudoku(F.grid("83..7....", "6..195...", ".98....6.", "8...6...3", "4..8.3..1", "7...2...6", ".6....28.", "...419..5", "....8..79")), "Two 8s in the first column (and the top-left box).");
        P.edge("Empty board", "board = all '.'", "true", () -> isValidSudoku(F.grid(".........", ".........", ".........", ".........", ".........", ".........", ".........", ".........", ".........")), "No digits, no conflicts.");
        P.done();

        P.begin("Basic Calculator II", "calculate");
        P.category("Stack · Operator precedence");
        P.summary("Evaluate an expression with + − × ÷ and spaces, respecting precedence. Read numbers left to right, remembering the operator before each one. + and − push the number (or its negative) on a stack; × and ÷ combine immediately with the stack's top. The answer is the stack's sum.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Can be done in O(1) space by keeping only the last term.");
        P.ideas("× and ÷ bind tighter, so apply them right away to the previous term.",
            "+ and − are deferred: push signed terms and add them at the end.",
            "Process the pending operator when you hit the next operator or the end of the string.",
            "Division truncates toward zero.");
        P.pits("Forgetting to process the last number.",
            "Treating spaces as operators.",
            "Applying + immediately breaks precedence: \"3+2*2\" must be 7.");
        P.trace("s = \"3+2*2-8/3\"", () -> calculate("3+2*2-8/3"));
        P.edge("Spaces", "s = \" 3/2 \"", "1", () -> calculate(" 3/2 "), "Spaces are skipped; 3/2 truncates to 1.");
        P.edge("Multi-digit numbers", "s = \"14-3*4\"", "2", () -> calculate("14-3*4"), "14 − 12.");
        P.edge("Single number", "s = \"42\"", "42", () -> calculate("42"), "Processed at the end of the string.");
        P.done();

        P.begin("Sliding Window Maximum", "maxSlidingWindow");
        P.category("Monotonic deque");
        P.summary("Return the maximum of every window of size k. Keep a deque of indices whose values are decreasing. Before adding index i, pop smaller values from the back (they can never be a maximum again) and drop the front if it slid out of the window. The front is always the current maximum.");
        P.cx("O(n)", "O(n)", "O(n)", "O(k)", "Each index is pushed and popped at most once.");
        P.ideas("A value smaller than a newer value is useless: the newer one outlives it and beats it.",
            "So the deque stays decreasing from front to back.",
            "Store indices to know when the front leaves the window.",
            "Record `nums[deque.front]` once the first full window is formed.");
        P.pits("A max-heap works but is O(n log n) and needs lazy deletion.",
            "Storing values instead of indices loses track of expiry.");
        P.trace("nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3", () -> maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3));
        P.edge("k = 1", "nums = [4, 2, 7], k = 1", "[4, 2, 7]", () -> maxSlidingWindow(new int[]{4, 2, 7}, 1), "Each window is a single element.");
        P.edge("k = n", "nums = [1, 5, 2], k = 3", "[5]", () -> maxSlidingWindow(new int[]{1, 5, 2}, 3), "One window.");
        P.edge("Decreasing input", "nums = [9, 8, 7, 6], k = 2", "[9, 8, 7]", () -> maxSlidingWindow(new int[]{9, 8, 7, 6}, 2), "Nothing gets popped from the back; the front expires each time.");
        P.done();

        P.begin("First Missing Positive", "firstMissingPositive");
        P.category("Arrays · Cyclic placement");
        P.summary("Find the smallest missing positive integer in O(n) time and O(1) space. The answer is in 1..n+1, so place each value v in 1..n at index v − 1 by swapping. Afterwards, the first index i with `nums[i] != i + 1` gives the answer i + 1.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each swap puts one value in its final place, so at most n swaps.");
        P.ideas("Only values 1..n matter; anything else can be ignored.",
            "Swap `nums[i]` to its home `nums[i] − 1` until the current slot holds a useless or already-placed value.",
            "Check `nums[nums[i] − 1] != nums[i]` to avoid infinite swaps on duplicates.",
            "If every slot is correct, the answer is n + 1.");
        P.pits("Sorting is O(n log n); a HashSet is O(n) space.",
            "Looping forever on duplicates like [1, 1].");
        P.trace("nums = [3, 4, -1, 1]", () -> firstMissingPositive(new int[]{3, 4, -1, 1}));
        P.edge("All present", "nums = [1, 2, 3]", "4", () -> firstMissingPositive(new int[]{1, 2, 3}), "Answer is n + 1.");
        P.edge("Large values only", "nums = [7, 8, 9, 11, 12]", "1", () -> firstMissingPositive(new int[]{7, 8, 9, 11, 12}), "Nothing lands in slot 0.");
        P.edge("Duplicates", "nums = [1, 1]", "2", () -> firstMissingPositive(new int[]{1, 1}), "The duplicate check prevents an infinite loop.");
        P.edge("Zero and negatives", "nums = [0, -5, 2]", "1", () -> firstMissingPositive(new int[]{0, -5, 2}), "1 is missing.");
        P.done();
    }

    static RNode buildR(int[][] spec) {
        RNode[] ns = new RNode[spec.length];
        for (int i = 0; i < spec.length; i++) ns[i] = new RNode(spec[i][0]);
        for (int i = 0; i < spec.length; i++) { if (i + 1 < spec.length) ns[i].next = ns[i + 1]; if (spec[i][1] >= 0) ns[i].random = ns[spec[i][1]]; }
        return spec.length == 0 ? null : ns[0];
    }
    static R show(RNode h) {
        List<RNode> all = new ArrayList<>(); for (RNode c = h; c != null; c = c.next) all.add(c);
        StringJoiner j = new StringJoiner(", ", "[", "]");
        for (RNode c : all) j.add("[" + c.val + ", " + (c.random == null ? "null" : String.valueOf(indexIn(all, c.random))) + "]");
        return new R(j.toString());
    }
    static int indexIn(List<RNode> l, RNode n) { for (int i = 0; i < l.size(); i++) if (l.get(i) == n) return i; return -99; }
    static S rview(String name, RNode head, RNode hot) {
        S s = T.graph(name, true);
        List<RNode> all = new ArrayList<>(); for (RNode c = head; c != null; c = c.next) all.add(c);
        for (int i = 0; i < all.size(); i++) s.node(T.id(all.get(i)), all.get(i).val);
        for (RNode c : all) { if (c.next != null) s.edge(T.id(c), T.id(c.next), null); }
        for (RNode c : all) if (c.random != null) { s.edge(T.id(c), T.id(c.random), "rand"); s.em(T.id(c), T.id(c.random), "visit"); }
        if (hot != null) s.m(T.id(hot), "active");
        return s;
    }
    static S copies(Map<RNode, RNode> map, RNode head) {
        S s = T.graph("copies", true);
        List<RNode> all = new ArrayList<>(); for (RNode c = head; c != null; c = c.next) all.add(c);
        int i = 0;
        for (RNode o : all) { RNode c = map.get(o); if (c != null) s.node(T.id(c), c.val + "′"); i++; }
        for (RNode o : all) { RNode c = map.get(o); if (c == null) continue; if (c.next != null) s.edge(T.id(c), T.id(c.next), null); if (c.random != null) { s.edge(T.id(c), T.id(c.random), "rand"); s.em(T.id(c), T.id(c.random), "visit"); } }
        return s;
    }

    //@code copyRandomList
    static class RNode {
        int val;
        RNode next, random;
        RNode(int v) { val = v; }
    }

    static RNode copyRandomList(RNode head) {
        Map<RNode, RNode> map = new HashMap<>();
        for (RNode cur = head; cur != null; cur = cur.next) {
            map.put(cur, new RNode(cur.val));
            T.s("Copy node " + cur.val, "Create an unwired copy and remember original → copy.", rview("original", head, cur), copies(map, head));
        }
        for (RNode cur = head; cur != null; cur = cur.next) {
            map.get(cur).next = map.get(cur.next);
            map.get(cur).random = map.get(cur.random);
            T.s("Wire copy of " + cur.val, "Its next and random point to the copies of the original's targets.", rview("original", head, cur), copies(map, head));
        }
        return map.get(head);
    }
    //@end

    //@code decodeString
    static String decodeString(String s) {
        Deque<Integer> counts = new ArrayDeque<>();
        Deque<StringBuilder> texts = new ArrayDeque<>();
        StringBuilder cur = new StringBuilder();
        int k = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                k = k * 10 + (c - '0');
            } else if (c == '[') {
                counts.push(k);
                texts.push(cur);
                cur = new StringBuilder();
                k = 0;
                T.s("Open a group", "Save the count and the text so far; start a new inner text.", T.str("s", s).p("i", i).m(i, "active"), T.stack("counts", counts), T.stack("texts", texts), T.v("cur", cur.toString()));
            } else if (c == ']') {
                StringBuilder outer = texts.pop();
                int times = counts.pop();
                String inner = cur.toString(); // @hide
                for (int t = 0; t < times; t++) outer.append(cur);
                cur = outer;
                T.s("Close a group", "Repeat \"" + inner + "\" " + times + " time(s) and attach it to the outer text.", T.str("s", s).p("i", i).m(i, "active"), T.stack("counts", counts), T.stack("texts", texts), T.v("cur", cur.toString()));
            } else {
                cur.append(c);
                T.s("Letter '" + c + "'", "Append to the current text.", T.str("s", s).p("i", i).m(i, "active"), T.stack("counts", counts), T.stack("texts", texts), T.v("cur", cur.toString()));
            }
        }
        return cur.toString();
    }
    //@end

    //@code findDuplicate
    static int findDuplicate(int[] nums) {
        int slow = 0, fast = 0;
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
            T.s("Phase 1: move", "slow follows one pointer (to index " + slow + "), fast follows two (to index " + fast + ").", T.arr("nums", nums).p("slow", slow).p("fast", fast).m(slow, "visit").m(fast, "active"), arrows(nums));
        } while (slow != fast);
        T.s("They meet inside the cycle", "Both are at index " + slow + ".", T.arr("nums", nums).p("slow", slow).p("fast", fast).m(slow, "compare"), arrows(nums));
        slow = 0;
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
            T.s("Phase 2: move both by one", "slow restarted at 0. Now at " + slow + " and " + fast + ".", T.arr("nums", nums).p("slow", slow).p("fast", fast).m(slow, "visit").m(fast, "active"), arrows(nums));
        }
        T.n("Cycle entrance", "They meet at index " + slow + ". Two indices point here, so " + slow + " is the duplicate value.", T.arr("nums", nums).p("slow", slow).m(slow, "found"), arrows(nums).m(slow, "found"));
        return slow;
    }
    //@end
    static S arrows(int[] nums) { S s = T.graph("i → nums[i]", true); for (int i = 0; i < nums.length; i++) s.node(i, i); for (int i = 0; i < nums.length; i++) s.edge(i, nums[i], null); return s; }

    //@code isValidSudoku
    static boolean isValidSudoku(char[][] board) {
        Set<String> seen = new HashSet<>();
        int count = 0; // @hide
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++) {
                char d = board[r][c];
                if (d == '.') continue;
                int box = (r / 3) * 3 + c / 3;
                boolean fresh = seen.add(d + " in row " + r) && seen.add(d + " in col " + c) && seen.add(d + " in box " + box);
                if (fresh) count++; // @hide
                T.s(fresh ? "Record " + d + " at (" + r + ", " + c + ")" : "Check " + d + " at (" + r + ", " + c + ")", fresh ? "Add \"" + d + " in row " + r + "\", \"" + d + " in col " + c + "\" and \"" + d + " in box " + box + "\". " + count + " digits checked." : "One of its three keys is already in the set.",
                    T.mat("board", board).m(r, c, fresh ? "active" : "error"), T.v("r", r, "c", c, "box", box, "keys", seen.size()));
                if (!fresh) {
                    T.n("Repeat found", d + " already appears in row " + r + ", column " + c + " or box " + box + ".", T.mat("board", board).m(r, c, "error"), T.v("r", r, "c", c, "box", box));
                    return false;
                }
            }
        T.n("Valid", "No digit repeats in any row, column or box.", T.mat("board", board));
        return true;
    }
    //@end

    //@code calculate
    static int calculate(String s) {
        Deque<Integer> stack = new ArrayDeque<>();
        int num = 0;
        char op = '+';
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) num = num * 10 + (c - '0');
            if ((!Character.isDigit(c) && c != ' ') || i == s.length() - 1) {
                switch (op) {
                    case '+' -> stack.push(num);
                    case '-' -> stack.push(-num);
                    case '*' -> stack.push(stack.pop() * num);
                    case '/' -> stack.push(stack.pop() / num);
                }
                T.s("Apply '" + op + "' to " + num, op == '+' || op == '-' ? "Push " + (op == '-' ? -num : num) + "; addition waits until the end." : "Combine with the top of the stack right away: " + stack.peek() + ".", T.str("s", s).p("i", i).range(0, i, "done"), T.stack("stack", stack).m(stack.size() - 1, "swap"), T.v("num", num, "op", String.valueOf(op)));
                op = c;
                num = 0;
            }
        }
        int sum = 0;
        for (int v : stack) sum += v;
        T.s("Add up the stack", "All × and ÷ are done; the stack holds signed terms. Sum: " + sum + ".", T.stack("stack", stack), T.v("result", sum));
        return sum;
    }
    //@end

    //@code maxSlidingWindow
    static int[] maxSlidingWindow(int[] nums, int k) {
        int[] res = new int[nums.length - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < nums.length; i++) {
            if (!dq.isEmpty() && dq.peekFirst() <= i - k) dq.pollFirst();
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i]) dq.pollLast();
            dq.offerLast(i);
            if (i >= k - 1) res[i - k + 1] = nums[dq.peekFirst()];
            T.s("Add index " + i, "Drop expired indices from the front and smaller values from the back. " + (i >= k - 1 ? "Window max: " + nums[dq.peekFirst()] + "." : "The first window isn't full yet."),
                T.arr("nums", nums).p("i", i).range(Math.max(0, i - k + 1), i, "visit").m(dq.peekFirst(), "found"), T.queue("deque (indices)", dq), T.arr("res", Arrays.copyOf(res, Math.max(0, i - k + 2))));
        }
        return res;
    }
    //@end

    //@code firstMissingPositive
    static int firstMissingPositive(int[] nums) {
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
                int v = nums[i];
                nums[i] = nums[v - 1];
                nums[v - 1] = v;
                T.s("Send " + v + " home", v + " belongs at index " + (v - 1) + ". Swap it there.", T.arr("nums", nums).p("i", i).mark(homes(nums)).m(v - 1, "swap"), T.v("i", i));
            }
        }
        for (int i = 0; i < n; i++) {
            if (nums[i] != i + 1) {
                T.n("First gap", "Index " + i + " should hold " + (i + 1) + " but holds " + nums[i] + ".", T.arr("nums", nums).mark(homes(nums)).m(i, "error"));
                return i + 1;
            }
        }
        T.n("All placed", "1.." + n + " are all present, so the answer is " + (n + 1) + ".", T.arr("nums", nums).mark(homes(nums)));
        return n + 1;
    }
    //@end
    static Map<Integer, String> homes(int[] a) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < a.length; i++) if (a[i] == i + 1) m.put(i, "done"); return m; }
}
