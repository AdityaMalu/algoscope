import java.util.*;

/** 1D Dynamic Programming */
class Dp1 {
    static void run() {
        P.begin("Climbing Stairs", "climbStairs");
        P.category("1D DP · Fibonacci");
        P.summary("Count the ways to climb n steps taking 1 or 2 at a time. The last move to step i came from step i−1 or i−2, so `ways(i) = ways(i−1) + ways(i−2)`. Only the last two values are needed.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Two rolling variables instead of an array.");
        P.ideas("Every route to step i ends with a 1-step from i−1 or a 2-step from i−2.",
            "Those two groups don't overlap, so add them.",
            "It's the Fibonacci sequence shifted by one.");
        P.pits("Plain recursion without memoisation is O(2ⁿ).",
            "Off-by-one in the base cases: ways(1) = 1, ways(2) = 2.");
        P.trace("n = 5", () -> climbStairs(5));
        P.edge("n = 1", "n = 1", "1", () -> climbStairs(1), "One single step.");
        P.edge("n = 2", "n = 2", "2", () -> climbStairs(2), "1+1 or 2.");
        P.edge("Largest allowed input", "n = 45", "1836311903", () -> climbStairs(45), "Still fits in an int (just under 2³¹).");
        P.done();

        P.begin("Min Cost Climbing Stairs", "minCostClimbingStairs");
        P.category("1D DP · Min of two choices");
        P.summary("Each step has a cost to step on; you can start at step 0 or 1 and climb 1 or 2 at a time. The cheapest way to reach step i is `min(dp[i−1] + cost[i−1], dp[i−2] + cost[i−2])`. The top is one past the last step.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Can be reduced to O(1) with two variables.");
        P.ideas("`dp[i]` = cheapest cost to stand on step i (before paying its own cost).",
            "`dp[0] = dp[1] = 0`: you can start on either for free.",
            "The answer is `dp[n]`, the floor above the last step.");
        P.pits("Returning `dp[n−1]` instead of `dp[n]`.",
            "Charging the cost of the step you land on instead of the one you leave.");
        P.trace("cost = [1, 100, 1, 1, 1, 100, 1, 1, 100, 1]", () -> minCostClimbingStairs(new int[]{1, 100, 1, 1, 1, 100, 1, 1, 100, 1}));
        P.edge("Two steps", "cost = [10, 15]", "10", () -> minCostClimbingStairs(new int[]{10, 15}), "Start on step 0 and jump 2.");
        P.edge("Three steps", "cost = [10, 15, 20]", "15", () -> minCostClimbingStairs(new int[]{10, 15, 20}), "Start on step 1 and jump 2.");
        P.edge("Zero costs", "cost = [0, 0, 0]", "0", () -> minCostClimbingStairs(new int[]{0, 0, 0}), "Free all the way.");
        P.done();

        P.begin("House Robber", "rob");
        P.category("1D DP · Take or skip");
        P.summary("Rob houses in a row for the most money without robbing two neighbours. For each house: either skip it (keep the best so far) or rob it (its money plus the best up to two houses back). Keep two rolling values.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Two variables: best including up to i−1 and up to i−2.");
        P.ideas("`best(i) = max(best(i−1), best(i−2) + nums[i])`.",
            "`prev1` is the best up to the previous house, `prev2` up to the one before.",
            "Skipping two houses in a row is never forced; the recurrence handles it.");
        P.pits("Greedy (take every other house) fails on [2, 1, 1, 2].",
            "Initialising with nums[0] and nums[1] separately leads to edge-case bugs; zeros work.");
        P.trace("nums = [2, 7, 9, 3, 1]", () -> rob(new int[]{2, 7, 9, 3, 1}));
        P.edge("Single house", "nums = [5]", "5", () -> rob(new int[]{5}), "Rob it.");
        P.edge("Skip two in a row", "nums = [2, 1, 1, 2]", "4", () -> rob(new int[]{2, 1, 1, 2}), "Rob the first and last houses.");
        P.edge("All zeros", "nums = [0, 0, 0]", "0", () -> rob(new int[]{0, 0, 0}), "Nothing to gain.");
        P.edge("Two houses", "nums = [1, 3]", "3", () -> rob(new int[]{1, 3}), "Pick the richer one.");
        P.done();

        P.begin("House Robber II", "robCircle");
        P.category("1D DP · Split the circle");
        P.summary("Houses are in a circle, so the first and last are neighbours. Either the first house is skipped or the last one is. Run the linear House Robber on houses 0..n−2 and on 1..n−1, and take the better result.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Two linear passes.");
        P.ideas("You can't rob both house 0 and house n−1.",
            "So the answer is the best of the two straight-line cases that each exclude one of them.",
            "One house is a special case: rob it.");
        P.pits("Forgetting the single-house case (both ranges would be empty).",
            "Excluding both ends at once loses answers.");
        P.trace("nums = [2, 3, 2, 5, 1]", () -> robCircle(new int[]{2, 3, 2, 5, 1}));
        P.edge("Ends are neighbours", "nums = [2, 3, 2]", "3", () -> robCircle(new int[]{2, 3, 2}), "Can't take both 2s.");
        P.edge("Single house", "nums = [7]", "7", () -> robCircle(new int[]{7}), "No neighbours to worry about.");
        P.edge("Four houses", "nums = [1, 2, 3, 1]", "4", () -> robCircle(new int[]{1, 2, 3, 1}), "1 + 3, excluding the last house.");
        P.done();

        P.begin("Coin Change", "coinChange");
        P.category("1D DP · Unbounded knapsack");
        P.summary("Find the fewest coins that make `amount`, with unlimited coins of each type. `dp[a]` is the fewest coins for amount a; for each coin c, `dp[a] = min(dp[a], dp[a − c] + 1)`. Fill amounts from 0 upward.");
        P.cx("O(S·k)", "O(S·k)", "O(S·k)", "O(S)", "S = amount, k = number of coin types.");
        P.ideas("The last coin used was some c, leaving `a − c` to be made optimally.",
            "Use `amount + 1` as infinity: no answer can need that many coins.",
            "If `dp[amount]` is still infinity, return -1.");
        P.pits("Greedy (largest coin first) fails: coins [1, 3, 4], amount 6 → greedy 4+1+1, best 3+3.",
            "Using Integer.MAX_VALUE as infinity overflows when you add 1.");
        P.trace("coins = [1, 3, 4], amount = 6", () -> coinChange(new int[]{1, 3, 4}, 6));
        P.edge("Impossible", "coins = [2], amount = 3", "-1", () -> coinChange(new int[]{2}, 3), "Odd amounts can't be made from 2s.");
        P.edge("Amount 0", "coins = [1], amount = 0", "0", () -> coinChange(new int[]{1}, 0), "No coins needed.");
        P.edge("Greedy would fail", "coins = [1, 3, 4], amount = 6", "2", () -> coinChange(new int[]{1, 3, 4}, 6), "3 + 3 beats 4 + 1 + 1.");
        P.edge("Large coin only", "coins = [5], amount = 5", "1", () -> coinChange(new int[]{5}, 5), "Exactly one coin.");
        P.done();

        P.begin("Longest Increasing Subsequence", "lengthOfLIS");
        P.category("1D DP · Patience sorting");
        P.summary("Find the length of the longest strictly increasing subsequence in O(n log n). Keep `tails`, where `tails[k]` is the smallest possible tail of an increasing subsequence of length k+1. For each number, binary-search the first tail ≥ it and replace it (or append if none).");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "One binary search per element. The O(n²) DP is the simpler alternative.");
        P.ideas("A smaller tail is always better: it leaves more room for future numbers.",
            "`tails` stays sorted, so binary search applies.",
            "Replacing doesn't change the length; appending extends the best length by one.",
            "`tails` isn't an actual subsequence, but its length is the answer.");
        P.pits("Using `>` instead of `>=` in the search allows equal values (non-strict LIS).",
            "Returning `tails` as the subsequence; it isn't one.");
        P.trace("nums = [10, 9, 2, 5, 3, 7, 101, 18]", () -> lengthOfLIS(new int[]{10, 9, 2, 5, 3, 7, 101, 18}));
        P.edge("All equal", "nums = [7, 7, 7, 7]", "1", () -> lengthOfLIS(new int[]{7, 7, 7, 7}), "Strictly increasing, so equal values don't extend.");
        P.edge("Strictly decreasing", "nums = [5, 4, 3, 2]", "1", () -> lengthOfLIS(new int[]{5, 4, 3, 2}), "Each number replaces the only tail.");
        P.edge("Already sorted", "nums = [1, 2, 3, 4]", "4", () -> lengthOfLIS(new int[]{1, 2, 3, 4}), "Every number appends.");
        P.edge("Single element", "nums = [0]", "1", () -> lengthOfLIS(new int[]{0}), "Length 1.");
        P.done();

        P.begin("Word Break", "wordBreak");
        P.category("1D DP · Prefix reachability");
        P.summary("Can the string be split into dictionary words? `dp[i]` is true if the first i characters can be split. `dp[i]` is true when some `dp[j]` is true and `s[j..i)` is a word.");
        P.cx("O(n²)", "O(n²·L)", "O(n²·L)", "O(n)", "n² (j, i) pairs, each with a substring lookup of length up to L.");
        P.ideas("`dp[0] = true`: the empty prefix is always splittable.",
            "For each end i, look for a split point j where the prefix works and the rest is a word.",
            "Limiting j to the longest word length speeds it up.");
        P.pits("Greedy longest-match fails: \"aaaaaaa\" with [\"aaaa\", \"aaa\"].",
            "Plain recursion without memoisation is exponential.");
        P.trace("s = \"leetcode\", wordDict = [leet, code]", () -> wordBreak("leetcode", Arrays.asList("leet", "code")));
        P.edge("Reuse a word", "s = \"applepenapple\", wordDict = [apple, pen]", "true", () -> wordBreak("applepenapple", Arrays.asList("apple", "pen")), "\"apple\" is used twice.");
        P.edge("Can't finish", "s = \"catsandog\", wordDict = [cats, dog, sand, and, cat]", "false", () -> wordBreak("catsandog", Arrays.asList("cats", "dog", "sand", "and", "cat")), "Every split leaves \"og\" or similar.");
        P.edge("Greedy trap", "s = \"goalspecial\", wordDict = [go, goal, goals, special]", "true", () -> wordBreak("goalspecial", Arrays.asList("go", "goal", "goals", "special")), "Taking the longest word \"goals\" first leaves \"pecial\"; DP also tries \"goal\" + \"special\".");
        P.done();

        P.begin("Decode Ways", "numDecodings");
        P.category("1D DP · One or two digits");
        P.summary("Count how many ways a digit string can be decoded with A=1 … Z=26. At each position, the last letter used either one digit (1–9) or two digits (10–26). `dp[i] = (one-digit valid ? dp[i−1] : 0) + (two-digit valid ? dp[i−2] : 0)`.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Can be reduced to two variables.");
        P.ideas("'0' can't stand alone; it must be part of 10 or 20.",
            "Two digits are valid only from 10 to 26.",
            "`dp[0] = 1` (empty string has one decoding) makes the recurrence work.");
        P.pits("Treating \"06\" as a valid two-digit code.",
            "Forgetting that a leading '0' makes the whole string undecodable.");
        P.trace("s = \"226\"", () -> numDecodings("226"));
        P.edge("Leading zero", "s = \"06\"", "0", () -> numDecodings("06"), "'0' alone is invalid and \"06\" isn't a letter.");
        P.edge("Zero inside", "s = \"10\"", "1", () -> numDecodings("10"), "Only \"J\".");
        P.edge("Bad zero", "s = \"100\"", "0", () -> numDecodings("100"), "The second 0 can't attach to anything valid.");
        P.edge("Mixed", "s = \"11106\"", "2", () -> numDecodings("11106"), "AAJF or KJF.");
        P.done();
    }

    //@code climbStairs
    static int climbStairs(int n) {
        int prev2 = 1, prev1 = 1;
        T.s("Base cases", "1 way to stand at step 0, 1 way to reach step 1.", T.v("prev2", prev2, "prev1", prev1));
        for (int i = 2; i <= n; i++) {
            int cur = prev1 + prev2;
            T.s("Ways to reach step " + i, "From step " + (i - 1) + " (" + prev1 + " ways) plus from step " + (i - 2) + " (" + prev2 + " ways) = " + cur + ".", T.arr("ways", waysUpTo(i)).m(i, "swap").m(i - 1, "compare").m(i - 2, "compare"), T.v("i", i, "prev2", prev2, "prev1", prev1, "cur", cur));
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }
    //@end
    static int[] waysUpTo(int i) { int[] w = new int[i + 1]; w[0] = 1; if (i >= 1) w[1] = 1; for (int k = 2; k <= i; k++) w[k] = w[k - 1] + w[k - 2]; return w; }

    //@code minCostClimbingStairs
    static int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        int[] dp = new int[n + 1];
        for (int i = 2; i <= n; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
            T.s("Cheapest way to step " + i, "From " + (i - 1) + ": " + dp[i - 1] + " + " + cost[i - 1] + " = " + (dp[i - 1] + cost[i - 1]) + "; from " + (i - 2) + ": " + dp[i - 2] + " + " + cost[i - 2] + " = " + (dp[i - 2] + cost[i - 2]) + ". Take " + dp[i] + ".",
                T.arr("cost", cost).m(i - 1, "compare").m(i - 2, "compare"), T.arr("dp", dp).p("i", i).range(i + 1, n, "dim").m(i, "swap"), T.v("i", i));
        }
        T.n("Top floor", "`dp[" + n + "] = " + dp[n] + "`.", T.arr("dp", dp).m(n, "found"));
        return dp[n];
    }
    //@end

    //@code rob
    static int rob(int[] nums) {
        int prev2 = 0, prev1 = 0;
        for (int i = 0; i < nums.length; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            T.s("House " + i + " ($" + nums[i] + ")", "Skip it: " + prev1 + ". Rob it: " + prev2 + " + " + nums[i] + " = " + (prev2 + nums[i]) + ". Best: " + cur + ".",
                T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, prev2 + nums[i] > prev1 ? "found" : "dim"), T.v("i", i, "prev2", prev2, "prev1", prev1, "cur", cur));
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }
    //@end

    //@code robCircle
    static int robCircle(int[] nums) {
        if (nums.length == 1) return nums[0];
        int a = robRange(nums, 0, nums.length - 2);
        T.s("Skip the last house", "Best over houses 0.." + (nums.length - 2) + " is " + a + ".", T.arr("nums", nums).range(0, nums.length - 2, "visit").m(nums.length - 1, "dim"), T.v("a", a));
        int b = robRange(nums, 1, nums.length - 1);
        T.s("Skip the first house", "Best over houses 1.." + (nums.length - 1) + " is " + b + ". Answer: max(" + a + ", " + b + ") = " + Math.max(a, b) + ".", T.arr("nums", nums).range(1, nums.length - 1, "visit").m(0, "dim"), T.v("a", a, "b", b));
        return Math.max(a, b);
    }

    static int robRange(int[] nums, int lo, int hi) {
        int prev2 = 0, prev1 = 0;
        for (int i = lo; i <= hi; i++) {
            int cur = Math.max(prev1, prev2 + nums[i]);
            T.s("House " + i, "max(skip " + prev1 + ", rob " + prev2 + " + " + nums[i] + ") = " + cur + ".", T.arr("nums", nums).p("i", i).range(lo, hi, "visit").m(i, "active").range(0, lo - 1, "dim").range(hi + 1, nums.length - 1, "dim"), T.v("i", i, "prev2", prev2, "prev1", prev1, "cur", cur));
            prev2 = prev1;
            prev1 = cur;
        }
        return prev1;
    }
    //@end

    //@code coinChange
    static int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;
        T.s("Initialise", "`dp[0] = 0`; every other amount starts at " + (amount + 1) + " (means impossible).", T.arr("dp", dp).m(0, "done"), T.arr("coins", coins));
        for (int a = 1; a <= amount; a++) {
            for (int c : coins) {
                if (c <= a && dp[a - c] + 1 < dp[a]) {
                    dp[a] = dp[a - c] + 1;
                    T.s("Improve dp[" + a + "]", "Use a " + c + " coin on top of the best for " + (a - c) + " (" + dp[a - c] + " coins): " + dp[a] + " coins.", T.arr("dp", dp).p("a", a).range(0, a - 1, "done").m(a - c, "compare").m(a, "swap"), T.arr("coins", coins).m(indexOf(coins, c), "active"), T.v("a", a, "c", c));
                }
            }
        }
        int result = dp[amount] > amount ? -1 : dp[amount];
        T.n("Answer", result == -1 ? "`dp[" + amount + "]` was never improved, so it's impossible." : "Fewest coins for " + amount + ": " + result + ".", T.arr("dp", dp).m(amount, result == -1 ? "error" : "found"));
        return result;
    }
    //@end
    static int indexOf(int[] a, int v) { for (int i = 0; i < a.length; i++) if (a[i] == v) return i; return -1; }

    //@code lengthOfLIS
    static int lengthOfLIS(int[] nums) {
        int[] tails = new int[nums.length];
        int size = 0;
        for (int i = 0; i < nums.length; i++) {
            int x = nums[i];
            int lo = 0, hi = size;
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails[mid] < x) lo = mid + 1;
                else hi = mid;
            }
            tails[lo] = x;
            if (lo == size) size++;
            T.s("Place " + x, lo + 1 == size
                    ? x + " is bigger than every tail, so it extends the longest run to length " + size + "."
                    : x + " replaces `tails[" + lo + "]`: a smaller tail for runs of length " + (lo + 1) + ".",
                T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.arr("tails", Arrays.copyOf(tails, size)).m(lo, "swap"), T.v("x", x, "pos", lo, "size", size));
        }
        return size;
    }
    //@end

    //@code wordBreak
    static boolean wordBreak(String s, List<String> wordDict) {
        Set<String> words = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;
        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && words.contains(s.substring(j, i))) {
                    dp[i] = true;
                    T.s("Prefix of length " + i + " works", "`dp[" + j + "]` is true and \"" + s.substring(j, i) + "\" is a word.", T.str("s", s).range(0, j - 1, "done").range(j, i - 1, "found"), T.arr("dp", dp).m(j, "compare").m(i, "swap"), T.v("i", i, "j", j));
                    break;
                }
            }
        }
        T.n("Answer", "`dp[" + s.length() + "]` is " + dp[s.length()] + ".", T.arr("dp", dp).m(s.length(), dp[s.length()] ? "found" : "error"));
        return dp[s.length()];
    }
    //@end

    //@code numDecodings
    static int numDecodings(String s) {
        int n = s.length();
        int[] dp = new int[n + 1];
        dp[0] = 1;
        for (int i = 1; i <= n; i++) {
            int one = s.charAt(i - 1) - '0';
            if (one != 0) dp[i] += dp[i - 1];
            if (i >= 2) {
                int two = Integer.parseInt(s.substring(i - 2, i));
                if (two >= 10 && two <= 26) dp[i] += dp[i - 2];
            }
            T.s("Ways to decode " + i + " digit(s)", (one != 0 ? "'" + one + "' as a letter adds " + dp[i - 1] + ". " : "'0' can't stand alone. ")
                    + (i >= 2 ? ("\"" + s.substring(i - 2, i) + "\"" + (inRange(s, i) ? " is 10–26, adding " + dp[i - 2] + "." : " isn't 10–26.")) : ""),
                T.str("s", s).range(0, i - 2, "done").m(i - 1, "active"), T.arr("dp", dp).m(i, "swap"), T.v("i", i));
        }
        return dp[n];
    }
    //@end
    static boolean inRange(String s, int i) { int t = Integer.parseInt(s.substring(i - 2, i)); return t >= 10 && t <= 26; }
}
