import java.util.*;

/** 2D Dynamic Programming */
class Dp2 {
    static List<String> chars(String s, String first) { List<String> l = new ArrayList<>(); l.add(first); for (char c : s.toCharArray()) l.add(String.valueOf(c)); return l; }
    static List<Integer> range(int n) { List<Integer> l = new ArrayList<>(); for (int i = 0; i <= n; i++) l.add(i); return l; }

    static void run() {
        P.begin("Unique Paths", "uniquePaths");
        P.category("2D DP · Grid paths");
        P.summary("Count paths from the top-left to the bottom-right of an m×n grid moving only right or down. Each cell is reached from above or from the left, so `dp[r][c] = dp[r−1][c] + dp[r][c−1]`. The first row and column have exactly one path each.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "One row of size n is enough if you overwrite in place.");
        P.ideas("Every path into (r, c) comes from (r−1, c) or (r, c−1).",
            "Border cells can only be reached in one way.",
            "The closed form C(m+n−2, m−1) also works, but watch for overflow.");
        P.pits("Recursion without memoisation is exponential.",
            "Mixing up m (rows) and n (columns).");
        P.trace("m = 3, n = 4", () -> uniquePaths(3, 4));
        P.edge("Single cell", "m = 1, n = 1", "1", () -> uniquePaths(1, 1), "You're already there.");
        P.edge("Single row", "m = 1, n = 5", "1", () -> uniquePaths(1, 5), "Only right moves.");
        P.edge("Square", "m = 3, n = 3", "6", () -> uniquePaths(3, 3), "C(4, 2) = 6.");
        P.done();

        P.begin("Minimum Path Sum", "minPathSum");
        P.category("2D DP · Grid paths");
        P.summary("Find the cheapest path from top-left to bottom-right moving right or down, where each cell has a cost. The cheapest way into a cell is its cost plus the cheaper of the cell above and the cell to the left.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(1)", "Overwrites the grid in place.");
        P.ideas("`grid[r][c] += min(top, left)`, with the borders having only one option.",
            "Filling row by row guarantees both neighbours are already final.",
            "Doing it in place saves memory, but mutates the input.");
        P.pits("Forgetting the first row and first column have only one neighbour.",
            "Greedily picking the cheaper next cell fails.");
        P.trace("grid = [[1, 3, 1], [1, 5, 1], [4, 2, 1]]", () -> minPathSum(new int[][]{{1, 3, 1}, {1, 5, 1}, {4, 2, 1}}));
        P.edge("Single cell", "grid = [[5]]", "5", () -> minPathSum(new int[][]{{5}}), "Just its own cost.");
        P.edge("Single row", "grid = [[1, 2, 3]]", "6", () -> minPathSum(new int[][]{{1, 2, 3}}), "Sum of the row.");
        P.edge("Zeros", "grid = [[0, 0], [0, 0]]", "0", () -> minPathSum(new int[][]{{0, 0}, {0, 0}}), "Every path costs 0.");
        P.done();

        P.begin("Longest Common Subsequence", "longestCommonSubsequence");
        P.category("2D DP · Two strings");
        P.summary("Find the length of the longest sequence that appears in both strings in order (not necessarily contiguous). `dp[i][j]` is the LCS of the first i and first j characters. Matching characters extend the diagonal; otherwise take the better of dropping one character from either string.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Can be reduced to O(min(m, n)) with two rows.");
        P.ideas("If `a[i−1] == b[j−1]`, that character is part of the LCS: `dp[i−1][j−1] + 1`.",
            "Otherwise one of the two last characters isn't used: `max(dp[i−1][j], dp[i][j−1])`.",
            "Row 0 and column 0 are empty prefixes: 0.");
        P.pits("Confusing subsequence with substring (contiguous).",
            "Off-by-one between dp indices and string indices.");
        P.trace("text1 = \"abcde\", text2 = \"ace\"", () -> longestCommonSubsequence("abcde", "ace"));
        P.edge("No common characters", "text1 = \"abc\", text2 = \"def\"", "0", () -> longestCommonSubsequence("abc", "def"), "Every cell stays 0.");
        P.edge("Identical", "text1 = \"abc\", text2 = \"abc\"", "3", () -> longestCommonSubsequence("abc", "abc"), "The whole diagonal matches.");
        P.edge("Order matters", "text1 = \"ab\", text2 = \"ba\"", "1", () -> longestCommonSubsequence("ab", "ba"), "Either 'a' or 'b', not both in order.");
        P.done();

        P.begin("Partition Equal Subset Sum", "canPartition");
        P.category("2D DP · 0/1 knapsack");
        P.summary("Can the array be split into two parts with equal sums? Only if some subset sums to half the total. `dp[s]` says whether sum s is reachable; for each number, update sums from high to low so each number is used once.");
        P.cx("O(n·S)", "O(n·S)", "O(n·S)", "O(S)", "S = total / 2. The 2D table collapses to one row.");
        P.ideas("An odd total can never be split evenly.",
            "Target = total / 2; it's a subset-sum question.",
            "Iterate sums downward so a number isn't reused within the same round.",
            "Stop early if the target becomes reachable.");
        P.pits("Iterating sums upward turns it into unbounded knapsack (reuses numbers).",
            "Forgetting the odd-total check.");
        P.trace("nums = [1, 5, 11, 5]", () -> canPartition(new int[]{1, 5, 11, 5}));
        P.edge("Odd total", "nums = [1, 2, 3, 5]", "false", () -> canPartition(new int[]{1, 2, 3, 5}), "Total 11 can't be halved.");
        P.edge("Even total, still impossible", "nums = [1, 2, 5]", "false", () -> canPartition(new int[]{1, 2, 5}), "Total 8 gives target 4, but no subset sums to 4.");
        P.edge("Two equal numbers", "nums = [3, 3]", "true", () -> canPartition(new int[]{3, 3}), "One each side.");
        P.edge("Single number", "nums = [2]", "false", () -> canPartition(new int[]{2}), "Target 1 can't be made from a single 2.");
        P.done();

        P.begin("Coin Change II", "change");
        P.category("2D DP · Unbounded knapsack (count)");
        P.summary("Count the combinations of coins (unlimited supply) that make `amount`. Process one coin type at a time: `dp[a] += dp[a − coin]`. Putting coins in the outer loop counts each combination once, regardless of order.");
        P.cx("O(S·k)", "O(S·k)", "O(S·k)", "O(S)", "S = amount, k = coin types.");
        P.ideas("`dp[0] = 1`: one way to make 0 (use nothing).",
            "Outer loop over coins means each combination is built in a fixed coin order.",
            "Inner loop upward allows reusing the current coin.");
        P.pits("Swapping the loops counts permutations (1+2 and 2+1 separately).",
            "Using min instead of sum (that's Coin Change I).");
        P.trace("amount = 5, coins = [1, 2, 5]", () -> change(5, new int[]{1, 2, 5}));
        P.edge("Impossible", "amount = 3, coins = [2]", "0", () -> change(3, new int[]{2}), "No combination of 2s makes 3.");
        P.edge("Amount 0", "amount = 0, coins = [7]", "1", () -> change(0, new int[]{7}), "The empty combination.");
        P.edge("Single coin", "amount = 10, coins = [10]", "1", () -> change(10, new int[]{10}), "One coin.");
        P.done();

        P.begin("Target Sum", "findTargetSumWays");
        P.category("2D DP · Subset-sum count");
        P.summary("Assign + or − to every number so the total equals `target`; count the ways. If P is the sum of the + numbers, then `P − (total − P) = target`, so `P = (total + target) / 2`. Count subsets with that sum.");
        P.cx("O(n·S)", "O(n·S)", "O(n·S)", "O(S)", "S = (total + target) / 2.");
        P.ideas("Rewriting the signs as a subset sum turns it into a counting knapsack.",
            "If `total + target` is odd or `|target| > total`, the answer is 0.",
            "Iterate sums downward so each number is used once.",
            "Zeros double the count (±0 are different assignments); the DP handles that.");
        P.pits("Brute-force recursion is O(2ⁿ).",
            "Forgetting the parity and range checks.",
            "Negative target: use `abs(target)` in the range check.");
        P.trace("nums = [1, 1, 1, 1, 1], target = 3", () -> findTargetSumWays(new int[]{1, 1, 1, 1, 1}, 3));
        P.edge("Single number", "nums = [1], target = 1", "1", () -> findTargetSumWays(new int[]{1}, 1), "+1.");
        P.edge("Unreachable", "nums = [1, 2], target = 4", "0", () -> findTargetSumWays(new int[]{1, 2}, 4), "|4| > total 3.");
        P.edge("Zeros", "nums = [0, 0, 1], target = 1", "4", () -> findTargetSumWays(new int[]{0, 0, 1}, 1), "Each 0 can be +0 or −0: 2 × 2 = 4 ways.");
        P.edge("Negative target", "nums = [1, 2, 3], target = -2", "1", () -> findTargetSumWays(new int[]{1, 2, 3}, -2), "Only −1 + 2 − 3 = −2.");
        P.done();

        P.begin("Edit Distance", "minDistance");
        P.category("2D DP · Two strings");
        P.summary("Minimum inserts, deletes and replacements to turn `word1` into `word2`. `dp[i][j]` = cost for the first i and j characters. Equal characters cost nothing extra (diagonal); otherwise 1 + the best of replace (diagonal), delete (up) or insert (left).");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Two rows are enough for the length only.");
        P.ideas("Row 0: building `word2[0..j)` from nothing takes j inserts. Column 0: i deletes.",
            "Match: `dp[i][j] = dp[i−1][j−1]`.",
            "Otherwise `1 + min(replace, delete, insert)`.");
        P.pits("Mixing up which neighbour means insert vs delete (they're symmetric, so the answer survives, but the explanation doesn't).",
            "Forgetting the base row and column.");
        P.trace("word1 = \"horse\", word2 = \"ros\"", () -> minDistance("horse", "ros"));
        P.edge("Empty source", "word1 = \"\", word2 = \"abc\"", "3", () -> minDistance("", "abc"), "Three inserts.");
        P.edge("Identical", "word1 = \"same\", word2 = \"same\"", "0", () -> minDistance("same", "same"), "Nothing to change.");
        P.edge("Completely different", "word1 = \"abc\", word2 = \"xyz\"", "3", () -> minDistance("abc", "xyz"), "Three replacements.");
        P.done();

        P.begin("Best Time to Buy and Sell Stock with Cooldown", "maxProfitCooldown");
        P.category("2D DP · State machine");
        P.summary("Trade as often as you like, but after selling you must wait a day. Track three states each day: `hold` (own a share), `sold` (just sold today), `rest` (no share, free to buy). Each state's best value comes from yesterday's states.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Three variables per day.");
        P.ideas("hold = max(hold, rest − price): keep holding, or buy today from a rested state.",
            "sold = hold + price: sell what you held.",
            "rest = max(rest, sold): yesterday's sale forces today's rest.",
            "Answer: max(sold, rest); you never end holding.");
        P.pits("Buying from `sold` ignores the cooldown.",
            "Updating states in place without saving yesterday's values.");
        P.trace("prices = [1, 2, 3, 0, 2]", () -> maxProfitCooldown(new int[]{1, 2, 3, 0, 2}));
        P.edge("Single day", "prices = [1]", "0", () -> maxProfitCooldown(new int[]{1}), "No time to sell.");
        P.edge("Falling prices", "prices = [5, 4, 3]", "0", () -> maxProfitCooldown(new int[]{5, 4, 3}), "Never buy.");
        P.edge("Cooldown matters", "prices = [1, 4, 2, 7]", "6", () -> maxProfitCooldown(new int[]{1, 4, 2, 7}), "Buying at 1 and selling at 7 earns 6. Doing 1→4 and then 2→7 would need buying the day right after selling.");
        P.done();
    }

    //@code uniquePaths
    static int uniquePaths(int m, int n) {
        int[][] dp = new int[m][n];
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (r == 0 || c == 0) dp[r][c] = 1;
                else dp[r][c] = dp[r - 1][c] + dp[r][c - 1];
                T.s("Cell (" + r + ", " + c + ")", r == 0 || c == 0 ? "On the border: only one way to get here." : "From above (" + dp[r - 1][c] + ") + from the left (" + dp[r][c - 1] + ") = " + dp[r][c] + ".",
                    mx(dp, r, c, r > 0 && c > 0));
            }
        }
        return dp[m - 1][n - 1];
    }
    //@end
    static S mx(int[][] dp, int r, int c, boolean both) {
        S s = T.mat("dp", dp);
        int n = dp[0].length;
        for (int i = 0; i < dp.length; i++) for (int j = 0; j < n; j++) if (i > r || (i == r && j > c)) s.m(i, j, "dim");
        if (both) { if (r > 0) s.m(r - 1, c, "compare"); if (c > 0) s.m(r, c - 1, "compare"); }
        s.m(r, c, "swap");
        return s;
    }

    //@code minPathSum
    static int minPathSum(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                if (r == 0 && c == 0) continue;
                int top = r > 0 ? grid[r - 1][c] : Integer.MAX_VALUE;
                int left = c > 0 ? grid[r][c - 1] : Integer.MAX_VALUE;
                grid[r][c] += Math.min(top, left);
                T.s("Cell (" + r + ", " + c + ")", "Add the cheaper of " + (r > 0 ? "above (" + top + ")" : "") + (r > 0 && c > 0 ? " and " : "") + (c > 0 ? "left (" + left + ")" : "") + ". Cheapest total here: " + grid[r][c] + ".", mx(grid, r, c, true));
            }
        }
        return grid[m - 1][n - 1];
    }
    //@end

    //@code longestCommonSubsequence
    static int longestCommonSubsequence(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    T.s("'" + a.charAt(i - 1) + "' matches", "Extend the diagonal: " + dp[i - 1][j - 1] + " + 1 = " + dp[i][j] + ".", two(dp, i, j, true).labels(chars(a, "∅"), chars(b, "∅")));
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                    T.s("'" + a.charAt(i - 1) + "' vs '" + b.charAt(j - 1) + "'", "No match. Take max(up " + dp[i - 1][j] + ", left " + dp[i][j - 1] + ") = " + dp[i][j] + ".", two(dp, i, j, false).labels(chars(a, "∅"), chars(b, "∅")));
                }
            }
        }
        return dp[m][n];
    }
    //@end
    static S two(int[][] dp, int i, int j, boolean diag) {
        S s = T.mat("dp", dp);
        for (int r = 0; r < dp.length; r++) for (int c = 0; c < dp[0].length; c++) if (r > i || (r == i && c > j)) s.m(r, c, "dim");
        if (diag) s.m(i - 1, j - 1, "compare"); else { s.m(i - 1, j, "compare"); s.m(i, j - 1, "compare"); }
        s.m(i, j, diag ? "found" : "swap");
        return s;
    }

    //@code canPartition
    static boolean canPartition(int[] nums) {
        int total = 0;
        for (int x : nums) total += x;
        if (total % 2 != 0) {
            T.n("Odd total", "The sum is " + total + "; it can't be split into two equal halves.", T.arr("nums", nums));
            return false;
        }
        int target = total / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;
        T.s("Target is half the total", "Is there a subset summing to " + target + "? Only sum 0 is reachable so far.", T.arr("nums", nums), T.arr("dp (sum reachable?)", dp).m(0, "done"));
        for (int i = 0; i < nums.length; i++) {
            for (int s = target; s >= nums[i]; s--) {
                dp[s] = dp[s] || dp[s - nums[i]];
            }
            T.s("Use " + nums[i], "Every sum s with `dp[s - " + nums[i] + "]` true becomes reachable. Sums go downward so " + nums[i] + " is used at most once.", T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.arr("dp (sum reachable?)", dp).mark(trueMarks(dp)), T.v("target", target));
            if (dp[target]) {
                T.n("Target reached", "A subset sums to " + target + ", so the rest sums to " + target + " too.", T.arr("dp (sum reachable?)", dp).m(target, "found"));
                return true;
            }
        }
        return false;
    }
    //@end
    static Map<Integer, String> trueMarks(boolean[] d) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < d.length; i++) if (d[i]) m.put(i, "done"); return m; }

    //@code change
    static int change(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1;
        for (int coin : coins) {
            for (int a = coin; a <= amount; a++) {
                dp[a] += dp[a - coin];
            }
            T.s("Allow coin " + coin, "For every amount a ≥ " + coin + ", add the ways to make a − " + coin + ". Combinations now use coins up to " + coin + ".", T.arr("coins", coins).m(indexOf(coins, coin), "active"), T.arr("dp (ways)", dp).range(coin, amount, "swap").m(0, "done"));
        }
        return dp[amount];
    }
    //@end
    static int indexOf(int[] a, int v) { for (int i = 0; i < a.length; i++) if (a[i] == v) return i; return -1; }

    //@code findTargetSumWays
    static int findTargetSumWays(int[] nums, int target) {
        int total = 0;
        for (int x : nums) total += x;
        if (Math.abs(target) > total || (total + target) % 2 != 0) {
            T.n("No assignment works", "Need |target| ≤ " + total + " and an even (total + target).", T.arr("nums", nums), T.v("total", total, "target", target));
            return 0;
        }
        int want = (total + target) / 2;
        int[] dp = new int[want + 1];
        dp[0] = 1;
        T.s("Reduce to a subset sum", "The + numbers must sum to (" + total + " + " + target + ") / 2 = " + want + ". Count subsets with that sum.", T.arr("nums", nums), T.arr("dp (ways)", dp).m(0, "done"), T.v("want", want));
        for (int i = 0; i < nums.length; i++) {
            for (int s = want; s >= nums[i]; s--) {
                dp[s] += dp[s - nums[i]];
            }
            T.s("Use " + nums[i], "Each sum s gains the ways to reach s − " + nums[i] + ".", T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.arr("dp (ways)", dp).m(want, "compare"), T.v("want", want));
        }
        return dp[want];
    }
    //@end

    //@code minDistance
    static int minDistance(String a, String b) {
        int m = a.length(), n = b.length();
        int[][] dp = new int[m + 1][n + 1];
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        T.s("Base row and column", "Turning a prefix into the empty string takes that many deletes, and vice versa for inserts.", T.mat("dp", dp).labels(chars(a, "∅"), chars(b, "∅")));
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                    T.s("'" + a.charAt(i - 1) + "' = '" + b.charAt(j - 1) + "'", "Free: copy the diagonal, " + dp[i][j] + ".", edit(dp, i, j, true).labels(chars(a, "∅"), chars(b, "∅")));
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                    T.s("'" + a.charAt(i - 1) + "' ≠ '" + b.charAt(j - 1) + "'", "1 + min(replace " + dp[i - 1][j - 1] + ", delete " + dp[i - 1][j] + ", insert " + dp[i][j - 1] + ") = " + dp[i][j] + ".", edit(dp, i, j, false).labels(chars(a, "∅"), chars(b, "∅")));
                }
            }
        }
        return dp[m][n];
    }
    //@end
    static S edit(int[][] dp, int i, int j, boolean same) {
        S s = T.mat("dp", dp);
        for (int r = 0; r < dp.length; r++) for (int c = 0; c < dp[0].length; c++) if (r > i || (r == i && c > j)) s.m(r, c, "dim");
        s.m(i - 1, j - 1, "compare");
        if (!same) { s.m(i - 1, j, "compare"); s.m(i, j - 1, "compare"); }
        s.m(i, j, "swap");
        return s;
    }

    //@code maxProfitCooldown
    static int maxProfit(int[] prices) {
        int hold = Integer.MIN_VALUE, sold = 0, rest = 0;
        for (int i = 0; i < prices.length; i++) {
            int p = prices[i];
            int prevSold = sold;
            sold = hold == Integer.MIN_VALUE ? Integer.MIN_VALUE : hold + p;
            hold = Math.max(hold, rest - p);
            rest = Math.max(rest, prevSold);
            T.s("Day " + i + " (price " + p + ")", "hold = " + hold + " (keep or buy from rest), sold = " + show(sold) + " (sell what was held), rest = " + rest + " (cool down or stay out).",
                T.arr("prices", prices).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.v("hold", hold, "sold", show(sold), "rest", rest));
        }
        return Math.max(sold, rest);
    }
    //@end
    static Object show(int v) { return v == Integer.MIN_VALUE ? "-∞" : v; }
    static int maxProfitCooldown(int[] p) { return maxProfit(p); }
}
