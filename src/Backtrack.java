import java.util.*;

/** Backtracking */
class Backtrack {
    static void run() {
        P.begin("Subsets", "subsets");
        P.category("Backtracking · Include / exclude");
        P.summary("Generate every subset. At each recursion level, record the current path as a subset, then try extending it with each remaining element after `start`. Undo the choice after recursing. Using only later elements avoids duplicates like [2, 1].");
        P.cx("O(n·2ⁿ)", "O(n·2ⁿ)", "O(n·2ⁿ)", "O(n)", "2ⁿ subsets, each copied in O(n). Recursion depth n.");
        P.ideas("Every node of the recursion tree is a subset, so add the path on entry.",
            "`start` ensures elements are picked in increasing index order.",
            "Add, recurse, remove: the path is restored for the next choice.",
            "Bitmasks from 0 to 2ⁿ−1 are an iterative alternative.");
        P.pits("Adding `path` itself instead of a copy: every result ends up empty.",
            "Starting the loop at 0 instead of `start` creates duplicates.");
        P.trace("nums = [1, 2, 3]", () -> subsets(new int[]{1, 2, 3}));
        P.edge("Empty input", "nums = []", "[[]]", () -> subsets(new int[]{}), "The empty set is still a subset.");
        P.edge("Single element", "nums = [0]", "[[], [0]]", () -> subsets(new int[]{0}), "Take it or don't.");
        P.edge("Negative values", "nums = [-1, 2]", "[[], [-1], [-1, 2], [2]]", () -> subsets(new int[]{-1, 2}), "Values don't matter, only positions.");
        P.done();

        P.begin("Permutations", "permute");
        P.category("Backtracking · Used flags");
        P.summary("Generate every ordering of distinct numbers. Build the permutation position by position; at each position try every number not used yet, mark it used, recurse, then unmark it.");
        P.cx("O(n·n!)", "O(n·n!)", "O(n·n!)", "O(n)", "n! permutations, each copied in O(n).");
        P.ideas("A boolean `used[]` array tracks which numbers are already in the path.",
            "When the path has n numbers, it's a complete permutation.",
            "Unmark after recursing so later branches can use the number.");
        P.pits("Using `path.contains(x)` to check usage is O(n) per check.",
            "Forgetting to copy the path when saving it.");
        P.trace("nums = [1, 2, 3]", () -> permute(new int[]{1, 2, 3}));
        P.edge("Single element", "nums = [1]", "[[1]]", () -> permute(new int[]{1}), "One permutation.");
        P.edge("Two elements", "nums = [0, 1]", "[[0, 1], [1, 0]]", () -> permute(new int[]{0, 1}), "2! = 2 orderings.");
        P.done();

        P.begin("Combination Sum", "combinationSum");
        P.category("Backtracking · Unlimited reuse");
        P.summary("Find all combinations of candidates (each usable any number of times) that sum to `target`. Sort, then recurse with a remaining target. Passing the same index `i` again allows reuse; stopping when a candidate exceeds the remainder prunes the search.");
        P.cx("O(2^(T/m))", "exponential", "exponential", "O(T/m)", "T = target, m = smallest candidate (bounds the depth).");
        P.ideas("Recurse with `i` (not `i + 1`) to allow picking the same number again.",
            "Starting each loop at `start` keeps combinations in non-decreasing order, so no duplicates.",
            "After sorting, once a candidate is too big, every later one is too, so break.");
        P.pits("Recursing with `i + 1` forbids reuse.",
            "Looping from 0 produces [2, 3, 2] and [3, 2, 2] as different results.");
        P.trace("candidates = [2, 3, 6, 7], target = 7", () -> combinationSum(new int[]{2, 3, 6, 7}, 7));
        P.edge("No solution", "candidates = [2], target = 1", "[]", () -> combinationSum(new int[]{2}, 1), "2 is already too big.");
        P.edge("Reuse needed", "candidates = [2, 3, 5], target = 8", "[[2, 2, 2, 2], [2, 3, 3], [3, 5]]", () -> combinationSum(new int[]{2, 3, 5}, 8), "2 is used four times in the first answer.");
        P.edge("Target equals a candidate", "candidates = [1], target = 1", "[[1]]", () -> combinationSum(new int[]{1}, 1), "One element.");
        P.done();

        P.begin("Subsets II", "subsetsWithDup");
        P.category("Backtracking · Skip duplicates");
        P.summary("Generate all subsets when the input has duplicates, without repeating any subset. Sort so equal values are adjacent; at each level, skip a value if it equals the previous value tried at the same level.");
        P.cx("O(n·2ⁿ)", "O(n·2ⁿ)", "O(n·2ⁿ)", "O(n)", "Worst case all distinct.");
        P.ideas("Sorting puts duplicates next to each other.",
            "`i > start && nums[i] == nums[i-1]` means the same value was already tried at this level.",
            "Using a duplicate deeper in the tree (i == start) is allowed: that's how [2, 2] is formed.");
        P.pits("Skipping with `i > 0` instead of `i > start` loses subsets like [2, 2].",
            "Deduplicating with a Set of lists works but is slower.");
        P.trace("nums = [1, 2, 2]", () -> subsetsWithDup(new int[]{1, 2, 2}));
        P.edge("All duplicates", "nums = [2, 2, 2]", "[[], [2], [2, 2], [2, 2, 2]]", () -> subsetsWithDup(new int[]{2, 2, 2}), "One subset per count.");
        P.edge("Unsorted input", "nums = [4, 1, 4]", "[[], [1], [1, 4], [1, 4, 4], [4], [4, 4]]", () -> subsetsWithDup(new int[]{4, 1, 4}), "Sorting first is what makes the skip work.");
        P.edge("Single element", "nums = [0]", "[[], [0]]", () -> subsetsWithDup(new int[]{0}), "No duplicates to skip.");
        P.done();

        P.begin("Combination Sum II", "combinationSum2");
        P.category("Backtracking · Use once, skip duplicates");
        P.summary("Each candidate can be used once and the input has duplicates. Sort, recurse with `i + 1`, and skip a candidate equal to the previous one at the same level so identical combinations aren't produced twice.");
        P.cx("O(2ⁿ)", "exponential", "O(n·2ⁿ)", "O(n)", "Pruning by sorted order cuts most branches.");
        P.ideas("Combines Subsets II (skip duplicates) with Combination Sum (target pruning).",
            "`i + 1` means each element is used at most once.",
            "Break once `candidates[i] > remain`.");
        P.pits("Recursing with `i` allows reuse, which this problem forbids.",
            "Skipping duplicates with `i > 0` instead of `i > start`.");
        P.trace("candidates = [10, 1, 2, 7, 6, 1, 5], target = 8", () -> combinationSum2(new int[]{10, 1, 2, 7, 6, 1, 5}, 8));
        P.edge("Duplicates allowed in one combination", "candidates = [1, 1, 2], target = 2", "[[1, 1], [2]]", () -> combinationSum2(new int[]{1, 1, 2}, 2), "The two 1s are different elements.");
        P.edge("No combination", "candidates = [3, 5], target = 4", "[]", () -> combinationSum2(new int[]{3, 5}, 4), "Nothing sums to 4.");
        P.edge("Many duplicates", "candidates = [2, 5, 2, 1, 2], target = 5", "[[1, 2, 2], [5]]", () -> combinationSum2(new int[]{2, 5, 2, 1, 2}, 5), "[1, 2, 2] appears once even though there are three 2s.");
        P.done();

        P.begin("Word Search", "exist");
        P.category("Backtracking · Grid DFS");
        P.summary("Does the word exist as a path of adjacent cells, using each cell at most once? Try every starting cell. DFS matches one letter at a time, temporarily marking the cell as used, and unmarks it on the way back.");
        P.cx("O(m·n)", "O(m·n·3^L)", "O(m·n·3^L)", "O(L)", "L = word length. Each step has at most 3 new directions.");
        P.ideas("A cell can be reused in a different path, but not twice in the same path.",
            "Mark in place (e.g. '#') instead of a separate visited array.",
            "Restore the letter after exploring: that's the backtracking step.",
            "Stop as soon as one path matches.");
        P.pits("Forgetting to restore the cell.",
            "Checking `index == word.length()` after the bounds check instead of before.");
        P.trace("board = [[A,B,C,E],[S,F,C,S],[A,D,E,E]], word = ABCCED", () -> exist(F.grid("ABCE", "SFCS", "ADEE"), "ABCCED"));
        P.edge("Reusing a cell", "board = [[A,B,C,E],[S,F,C,S],[A,D,E,E]], word = ABCB", "false", () -> exist(F.grid("ABCE", "SFCS", "ADEE"), "ABCB"), "The only B can't be used twice.");
        P.edge("Single cell", "board = [[a]], word = a", "true", () -> exist(F.grid("a"), "a"), "Match on the first cell.");
        P.edge("Word longer than the board", "board = [[a, b]], word = abc", "false", () -> exist(F.grid("ab"), "abc"), "There's no 'c'.");
        P.done();

        P.begin("Palindrome Partitioning", "partition");
        P.category("Backtracking · Cut points");
        P.summary("Split a string into pieces that are all palindromes, returning every way. From position `start`, try each end `i`; if `s[start..i]` is a palindrome, take it and recurse on the rest. Reaching the end means one full partition.");
        P.cx("O(n·2ⁿ)", "O(n·2ⁿ)", "O(n·2ⁿ)", "O(n)", "Up to 2ⁿ⁻¹ partitions; each palindrome check is O(n).");
        P.ideas("Each recursion level chooses the next cut.",
            "Only palindromic prefixes are worth exploring.",
            "Precomputing a palindrome table makes each check O(1).");
        P.pits("Off-by-one with `substring(start, i + 1)`.",
            "Forgetting to copy the path into the result.");
        P.trace("s = \"aab\"", () -> partition("aab"));
        P.edge("Single character", "s = \"a\"", "[[\"a\"]]", () -> partition("a"), "One piece.");
        P.edge("All the same", "s = \"aaa\"", "[[\"a\", \"a\", \"a\"], [\"a\", \"aa\"], [\"aa\", \"a\"], [\"aaa\"]]", () -> partition("aaa"), "Every cut works.");
        P.edge("No long palindromes", "s = \"abc\"", "[[\"a\", \"b\", \"c\"]]", () -> partition("abc"), "Only single letters.");
        P.done();

        P.begin("N-Queens", "solveNQueens");
        P.category("Backtracking · Constraint sets");
        P.summary("Place n queens so none attack each other. Place one queen per row; for each row try every column not under attack. Three sets track used columns and both diagonals (`r - c` and `r + c`), so checking a square is O(1).");
        P.cx("O(n!)", "O(n!)", "O(n!)", "O(n)", "At most n choices in the first row, n−1 in the next, and so on.");
        P.ideas("One queen per row, so recursion depth is n.",
            "Squares on the same ↘ diagonal share `r - c`; on the same ↙ diagonal they share `r + c`.",
            "Add to the sets before recursing, remove after.");
        P.pits("Checking the whole board for every placement is O(n) per check.",
            "Forgetting one of the two diagonals.");
        P.trace("n = 4", () -> solveNQueens(4));
        P.edge("n = 1", "n = 1", "[[\"Q\"]]", () -> solveNQueens(1), "One queen, one square.");
        P.edge("n = 2", "n = 2", "[]", () -> solveNQueens(2), "No arrangement works.");
        P.edge("n = 3", "n = 3", "[]", () -> solveNQueens(3), "No arrangement works either.");
        P.done();
    }

    static List<List<Integer>> RES;

    //@code subsets
    static List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), res);
        return res;
    }

    static void backtrack(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
        res.add(new ArrayList<>(path));
        T.call("backtrack(start=" + start + ")");
        T.s("Record " + path, "Every path is a subset. Next, try adding each element from index " + start + " on.", T.arr("nums", nums).p("start", start).range(0, start - 1, "dim"), T.arr("path", path), T.arr("res", res).m(res.size() - 1, "swap"));
        for (int i = start; i < nums.length; i++) {
            path.add(nums[i]);
            backtrack(nums, i + 1, path, res);
            path.remove(path.size() - 1);
        }
        T.ret();
    }
    //@end

    //@code permute
    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        dfs(nums, new boolean[nums.length], new ArrayList<>(), res);
        return res;
    }

    static void dfs(int[] nums, boolean[] used, List<Integer> path, List<List<Integer>> res) {
        if (path.size() == nums.length) {
            res.add(new ArrayList<>(path));
            T.s("Complete: " + path, "All numbers are placed.", T.arr("nums", nums).mark(usedMarks(used)), T.arr("path", path).range(0, path.size() - 1, "found"), T.arr("res", res).m(res.size() - 1, "swap"));
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            path.add(nums[i]);
            T.call("place " + nums[i]);
            T.s("Place " + nums[i], "Position " + (path.size() - 1) + " gets " + nums[i] + ". It's marked used for deeper positions.", T.arr("nums", nums).mark(usedMarks(used)).m(i, "active"), T.arr("path", path).m(path.size() - 1, "swap"), T.arr("res", res));
            dfs(nums, used, path, res);
            T.ret();
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }
    //@end
    static Map<Integer, String> usedMarks(boolean[] u) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < u.length; i++) if (u[i]) m.put(i, "done"); return m; }

    //@code combinationSum
    static List<List<Integer>> combinationSum(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> res = new ArrayList<>();
        search(candidates, 0, target, new ArrayList<>(), res);
        return res;
    }

    static void search(int[] c, int start, int remain, List<Integer> path, List<List<Integer>> res) {
        if (remain == 0) {
            res.add(new ArrayList<>(path));
            T.s("Found " + path, "The remainder is 0.", T.arr("candidates", c), T.arr("path", path).range(0, path.size() - 1, "found"), T.arr("res", res).m(res.size() - 1, "swap"), T.v("remain", remain));
            return;
        }
        for (int i = start; i < c.length; i++) {
            if (c[i] > remain) {
                T.n("Prune", c[i] + " > remaining " + remain + ", and every later candidate is bigger.", T.arr("candidates", c).p("i", i).m(i, "dim"), T.arr("path", path), T.v("remain", remain));
                T.prune(c[i] + " > " + remain);
                break;
            }
            path.add(c[i]);
            T.call("pick " + c[i]);
            T.s("Pick " + c[i], "Remaining: " + (remain - c[i]) + ". Recurse with the same index so " + c[i] + " can be picked again.", T.arr("candidates", c).p("i", i).range(0, start - 1, "dim").m(i, "active"), T.arr("path", path).m(path.size() - 1, "swap"), T.v("remain", remain - c[i]));
            search(c, i, remain - c[i], path, res);
            T.ret();
            path.remove(path.size() - 1);
        }
    }
    //@end

    //@code subsetsWithDup
    static List<List<Integer>> subsetsWithDup(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> res = new ArrayList<>();
        go(nums, 0, new ArrayList<>(), res);
        return res;
    }

    static void go(int[] nums, int start, List<Integer> path, List<List<Integer>> res) {
        res.add(new ArrayList<>(path));
        T.call("go(start=" + start + ")");
        T.s("Record " + path, "Then extend with values from index " + start + ".", T.arr("nums", nums).p("start", start).range(0, start - 1, "dim"), T.arr("path", path), T.arr("res", res).m(res.size() - 1, "swap"));
        for (int i = start; i < nums.length; i++) {
            if (i > start && nums[i] == nums[i - 1]) {
                T.n("Skip duplicate " + nums[i], "The same value was already tried at this level; it would repeat subsets.", T.arr("nums", nums).p("i", i).m(i, "dim").m(i - 1, "compare"), T.arr("path", path));
                T.prune("skip " + nums[i]);
                continue;
            }
            path.add(nums[i]);
            go(nums, i + 1, path, res);
            path.remove(path.size() - 1);
        }
        T.ret();
    }
    //@end

    //@code combinationSum2
    static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        Arrays.sort(candidates);
        List<List<Integer>> res = new ArrayList<>();
        find(candidates, 0, target, new ArrayList<>(), res);
        return res;
    }

    static void find(int[] c, int start, int remain, List<Integer> path, List<List<Integer>> res) {
        if (remain == 0) {
            res.add(new ArrayList<>(path));
            T.s("Found " + path, "Sum reached the target.", T.arr("candidates", c), T.arr("path", path).range(0, path.size() - 1, "found"), T.arr("res", res).m(res.size() - 1, "swap"));
            return;
        }
        for (int i = start; i < c.length; i++) {
            if (i > start && c[i] == c[i - 1]) continue; //@prune "skip " + c[i]
            if (c[i] > remain) break; //@prune c[i] + " > " + remain
            path.add(c[i]);
            T.call("pick " + c[i]);
            T.s("Pick " + c[i], "Remaining: " + (remain - c[i]) + ". Next picks start after index " + i + ".", T.arr("candidates", c).p("i", i).range(0, start - 1, "dim").m(i, "active"), T.arr("path", path).m(path.size() - 1, "swap"), T.v("remain", remain - c[i]));
            find(c, i + 1, remain - c[i], path, res);
            T.ret();
            path.remove(path.size() - 1);
        }
    }
    //@end

    static char[][] B0;
    static S board(char[][] b, int r, int c, String mark) {
        S s = T.mat("board", b);
        for (int i = 0; i < b.length; i++) for (int j = 0; j < b[0].length; j++) if (b[i][j] == '#') s.m(i, j, "visit");
        if (r >= 0) s.m(r, c, mark);
        return s;
    }

    //@code exist
    static boolean exist(char[][] board, String word) {
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++)
                if (match(board, word, 0, r, c)) return true;
        return false;
    }

    static boolean match(char[][] b, String word, int k, int r, int c) {
        if (k == word.length()) return true;
        if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] != word.charAt(k)) return false;
        char saved = b[r][c];
        b[r][c] = '#';
        T.call("match(k=" + k + ", " + r + ", " + c + ")");
        T.s("Match '" + word.charAt(k) + "' at (" + r + ", " + c + ")", "Letter " + (k + 1) + " of \"" + word + "\" matches. Mark the cell used and try its neighbours for '" + (k + 1 < word.length() ? word.charAt(k + 1) : '✓') + "'.",
            board(b, r, c, "active"), T.str("word", word).range(0, k - 1, "done").m(k, "active"));
        boolean found = match(b, word, k + 1, r + 1, c) || match(b, word, k + 1, r - 1, c)
            || match(b, word, k + 1, r, c + 1) || match(b, word, k + 1, r, c - 1);
        b[r][c] = saved;
        T.s(found ? "Path complete" : "Dead end: undo", found ? "The rest of the word was found from here." : "No neighbour continues the word. Restore '" + saved + "' and back up.", board(b, r, c, found ? "found" : "dim"), T.str("word", word).range(0, k, found ? "found" : "done"));
        T.ret();
        return found;
    }
    //@end

    //@code partition
    static List<List<String>> partition(String s) {
        List<List<String>> res = new ArrayList<>();
        cut(s, 0, new ArrayList<>(), res);
        return res;
    }

    static void cut(String s, int start, List<String> path, List<List<String>> res) {
        if (start == s.length()) {
            res.add(new ArrayList<>(path));
            T.s("Partition complete", path + " covers the whole string.", T.str("s", s), T.arr("path", path).range(0, path.size() - 1, "found"), T.arr("res", res).m(res.size() - 1, "swap"));
            return;
        }
        for (int i = start; i < s.length(); i++) {
            if (!isPal(s, start, i)) {
                T.n("Not a palindrome", "\"" + s.substring(start, i + 1) + "\" doesn't read the same backwards.", T.str("s", s).range(0, start - 1, "done").range(start, i, "dim"), T.arr("path", path));
                T.prune("\"" + s.substring(start, i + 1) + "\" ✗");
                continue;
            }
            path.add(s.substring(start, i + 1));
            T.call("cut at " + (i + 1));
            T.s("Take \"" + s.substring(start, i + 1) + "\"", "It's a palindrome. Partition the rest from index " + (i + 1) + ".", T.str("s", s).range(0, start - 1, "done").range(start, i, "active"), T.arr("path", path).m(path.size() - 1, "swap"));
            cut(s, i + 1, path, res);
            T.ret();
            path.remove(path.size() - 1);
        }
    }

    static boolean isPal(String s, int l, int r) {
        while (l < r) if (s.charAt(l++) != s.charAt(r--)) return false;
        return true;
    }
    //@end

    static char[][] qboard(int n, int[] cols, int rows) {
        char[][] b = new char[n][n];
        for (char[] row : b) Arrays.fill(row, '.');
        for (int r = 0; r < rows; r++) b[r][cols[r]] = 'Q';
        return b;
    }
    static S attacked(int n, Set<Integer> cols, Set<Integer> d1, Set<Integer> d2, int[] q, int rows) {
        S s = T.mat("board", qboard(n, q, rows));
        for (int r = 0; r < n; r++) for (int c = 0; c < n; c++) {
            if (r < rows && q[r] == c) s.m(r, c, "done");
            else if (cols.contains(c) || d1.contains(r - c) || d2.contains(r + c)) s.m(r, c, "dim");
        }
        return s;
    }

    //@code solveNQueens
    static List<List<String>> solveNQueens(int n) {
        List<List<String>> res = new ArrayList<>();
        place(n, 0, new int[n], new HashSet<>(), new HashSet<>(), new HashSet<>(), res);
        return res;
    }

    static void place(int n, int r, int[] queens, Set<Integer> cols, Set<Integer> diag, Set<Integer> anti, List<List<String>> res) {
        if (r == n) {
            List<String> board = new ArrayList<>();
            for (int c : queens) {
                char[] row = new char[n];
                Arrays.fill(row, '.');
                row[c] = 'Q';
                board.add(new String(row));
            }
            res.add(board);
            T.s("Solution found", "All " + n + " queens are placed safely.", attacked(n, cols, diag, anti, queens, n), T.v("solutions", res.size()));
            return;
        }
        for (int c = 0; c < n; c++) {
            if (cols.contains(c) || diag.contains(r - c) || anti.contains(r + c)) continue; //@prune "col " + c
            queens[r] = c;
            cols.add(c);
            diag.add(r - c);
            anti.add(r + c);
            T.call("row " + r + " → col " + c);
            T.s("Queen at (" + r + ", " + c + ")", "Column " + c + " and both diagonals are free. Faded squares are now under attack.", attacked(n, cols, diag, anti, queens, r + 1).m(r, c, "active"), T.v("row", r, "col", c));
            place(n, r + 1, queens, cols, diag, anti, res);
            T.ret();
            cols.remove(c);
            diag.remove(r - c);
            anti.remove(r + c);
        }
    }
    //@end
}
