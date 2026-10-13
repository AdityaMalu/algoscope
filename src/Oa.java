import java.util.*;

/** OA Simulation */
class Oa {
    static void run() {
        P.begin("Maximum Units on a Truck", "maximumUnits");
        P.category("Greedy · Sort by value");
        P.summary("Load at most `truckSize` boxes to maximise total units. Every box takes one slot, so take boxes with the most units per box first: sort types by units descending and fill greedily.");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(1)", "Sorting dominates; counting sort works since units ≤ 1000.");
        P.ideas("All boxes cost the same space, so value per box is all that matters.",
            "Take as many of the best type as fit, then move to the next.",
            "Stop when the truck is full.");
        P.pits("Sorting by the number of boxes instead of units per box.",
            "Forgetting to cap the last type at the remaining space.");
        P.trace("boxTypes = [[1, 3], [2, 2], [3, 1]], truckSize = 4", () -> maximumUnits(new int[][]{{1, 3}, {2, 2}, {3, 1}}, 4));
        P.edge("Truck bigger than supply", "boxTypes = [[1, 5]], truckSize = 10", "5", () -> maximumUnits(new int[][]{{1, 5}}, 10), "Take everything.");
        P.edge("Partial last type", "boxTypes = [[5, 10], [2, 5], [4, 7], [3, 9]], truckSize = 10", "91", () -> maximumUnits(new int[][]{{5, 10}, {2, 5}, {4, 7}, {3, 9}}, 10), "5×10 + 3×9 + 2×7 = 91.");
        P.edge("Truck size 1", "boxTypes = [[3, 1], [1, 9]], truckSize = 1", "9", () -> maximumUnits(new int[][]{{3, 1}, {1, 9}}, 1), "One box with 9 units.");
        P.done();

        P.begin("Count Number of Teams", "numTeams");
        P.category("Counting · Fix the middle");
        P.summary("Count triples i < j < k with strictly increasing or strictly decreasing ratings. Fix the middle soldier j: count smaller and larger ratings on each side. Increasing teams = smallerLeft × largerRight, decreasing = largerLeft × smallerRight.");
        P.cx("O(n²)", "O(n²)", "O(n²)", "O(1)", "A Fenwick tree brings it to O(n log n).");
        P.ideas("Every team has exactly one middle member.",
            "For a fixed middle, left and right choices are independent, so multiply.",
            "Ratings are distinct, so 'smaller' and 'larger' cover everything.");
        P.pits("Triple loops are O(n³).",
            "Counting only increasing teams.");
        P.trace("rating = [2, 5, 3, 4, 1]", () -> numTeams(new int[]{2, 5, 3, 4, 1}));
        P.edge("No teams", "rating = [2, 1, 3]", "0", () -> numTeams(new int[]{2, 1, 3}), "Neither order works for the only triple.");
        P.edge("Strictly increasing", "rating = [1, 2, 3, 4]", "4", () -> numTeams(new int[]{1, 2, 3, 4}), "Every triple: C(4, 3) = 4.");
        P.edge("Fewer than three", "rating = [1, 2]", "0", () -> numTeams(new int[]{1, 2}), "No middle soldier.");
        P.done();

        P.begin("Number of Ways to Select Buildings", "numberOfWays");
        P.category("Counting · Fix the middle");
        P.summary("Choose three buildings so no two consecutive picks have the same type: the patterns are \"010\" and \"101\". For each building as the middle, multiply the count of the opposite type on its left by the count on its right.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Running counts of 0s and 1s to the left; right counts come from totals.");
        P.ideas("A valid selection is fully determined by its middle building and its type.",
            "Middle '0' needs a '1' on each side; middle '1' needs a '0' on each side.",
            "Right-side counts = totals minus left counts minus the current building.");
        P.pits("The answer can exceed 2³¹; use long.",
            "Checking all triples is O(n³).");
        P.trace("s = \"001101\"", () -> numberOfWays("001101"));
        P.edge("All the same", "s = \"11100\"", "0", () -> numberOfWays("11100"), "No 1 has a 0 on both sides, and no 0 has a 1 on both sides.");
        P.edge("Alternating", "s = \"0101\"", "2", () -> numberOfWays("0101"), "010 (0,1,2) and 101 (1,2,3).");
        P.edge("Too short", "s = \"01\"", "0", () -> numberOfWays("01"), "Need three buildings.");
        P.done();

        P.begin("Sum of Total Strength of Wizards", "totalStrength");
        P.category("Monotonic stack · Prefix of prefix sums");
        P.summary("For every subarray, strength = min × sum; return the total mod 1e9+7. Count by the minimum: for each wizard i, a monotonic stack finds the range where it's the minimum. The sum of all subarray sums inside that range comes from a prefix-of-prefix-sums array in O(1).");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Two monotonic-stack passes and two prefix arrays.");
        P.ideas("Each subarray is assigned to one minimum: the previous smaller element (strict) and next smaller-or-equal bound it, so ties aren't double-counted.",
            "Subarrays with min at i: start in (L, i], end in [i, R).",
            "Sum over those of subarray sums = (pp[R+1] − pp[i+1])·(i − L) − (pp[i+1] − pp[L+1])·(R − i), where pp is the prefix sum of prefix sums.",
            "Multiply by `strength[i]` and add, all mod 1e9+7.");
        P.pits("Using strict comparisons on both sides double-counts equal minimums.",
            "Negative values after subtracting under a modulus; add MOD back.",
            "O(n²) enumeration times out at n = 10⁵.");
        P.trace("strength = [1, 3, 1, 2]", () -> totalStrength(new int[]{1, 3, 1, 2}));
        P.edge("LeetCode example 2", "strength = [5, 4, 6]", "213", () -> totalStrength(new int[]{5, 4, 6}), "Six subarrays: 25 + 16 + 36 + 36 + 40 + 60.");
        P.edge("Single wizard", "strength = [7]", "49", () -> totalStrength(new int[]{7}), "7 × 7.");
        P.edge("Equal strengths", "strength = [2, 2]", "16", () -> totalStrength(new int[]{2, 2}), "4 + 4 + 2×4 = 16; ties are counted once each.");
        P.done();
    }

    //@code maximumUnits
    static int maximumUnits(int[][] boxTypes, int truckSize) {
        Arrays.sort(boxTypes, (a, b) -> Integer.compare(b[1], a[1]));
        T.s("Sort by units per box", "Best boxes first.", Greedy.ivs("boxTypes [count, units]", boxTypes), T.v("space", truckSize));
        int total = 0;
        for (int i = 0; i < boxTypes.length && truckSize > 0; i++) {
            int take = Math.min(truckSize, boxTypes[i][0]);
            total += take * boxTypes[i][1];
            truckSize -= take;
            T.s("Load " + take + " box(es)", take + " × " + boxTypes[i][1] + " units. Total: " + total + "; space left: " + truckSize + ".", Greedy.ivs("boxTypes [count, units]", boxTypes).p("i", i).range(0, i - 1, "done").m(i, "active"), T.v("take", take, "total", total, "space", truckSize));
        }
        return total;
    }
    //@end

    //@code numTeams
    static int numTeams(int[] rating) {
        int n = rating.length, teams = 0;
        for (int j = 1; j < n - 1; j++) {
            int lessL = 0, moreL = 0, lessR = 0, moreR = 0;
            for (int i = 0; i < j; i++) {
                if (rating[i] < rating[j]) lessL++;
                else moreL++;
            }
            for (int k = j + 1; k < n; k++) {
                if (rating[k] < rating[j]) lessR++;
                else moreR++;
            }
            teams += lessL * moreR + moreL * lessR;
            T.s("Middle soldier " + j + " (rating " + rating[j] + ")", "Left: " + lessL + " smaller, " + moreL + " larger. Right: " + lessR + " smaller, " + moreR + " larger. Adds " + lessL + "×" + moreR + " + " + moreL + "×" + lessR + " = " + (lessL * moreR + moreL * lessR) + ".",
                T.arr("rating", rating).p("j", j).mark(sides(rating, j)).m(j, "active"), T.v("lessL", lessL, "moreL", moreL, "lessR", lessR, "moreR", moreR, "teams", teams));
        }
        return teams;
    }
    //@end
    static Map<Integer, String> sides(int[] a, int j) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < a.length; i++) if (i != j) m.put(i, a[i] < a[j] ? "visit" : "compare"); return m; }

    //@code numberOfWays
    static long numberOfWays(String s) {
        long total0 = 0, total1 = 0;
        for (char c : s.toCharArray()) {
            if (c == '0') total0++;
            else total1++;
        }
        long left0 = 0, left1 = 0, ways = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '0') {
                long add = left1 * (total1 - left1);
                ways += add;
                T.s("Middle '0' at " + i, left1 + " ones on the left × " + (total1 - left1) + " on the right = " + add + " \"101\" picks.", T.str("s", s).p("i", i).m(i, "active").range(0, i - 1, "visit"), T.v("left0", left0, "left1", left1, "ways", ways));
                left0++;
            } else {
                long add = left0 * (total0 - left0);
                ways += add;
                T.s("Middle '1' at " + i, left0 + " zeros on the left × " + (total0 - left0) + " on the right = " + add + " \"010\" picks.", T.str("s", s).p("i", i).m(i, "active").range(0, i - 1, "visit"), T.v("left0", left0, "left1", left1, "ways", ways));
                left1++;
            }
        }
        return ways;
    }
    //@end

    //@code totalStrength
    static int totalStrength(int[] strength) {
        final long MOD = 1_000_000_007L;
        int n = strength.length;
        int[] left = new int[n], right = new int[n];
        Deque<Integer> st = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!st.isEmpty() && strength[st.peek()] >= strength[i]) st.pop();
            left[i] = st.isEmpty() ? -1 : st.peek();
            st.push(i);
        }
        st.clear();
        for (int i = n - 1; i >= 0; i--) {
            while (!st.isEmpty() && strength[st.peek()] > strength[i]) st.pop();
            right[i] = st.isEmpty() ? n : st.peek();
            st.push(i);
        }
        T.s("Range where each wizard is the minimum", "`left[i]` = previous strictly smaller index, `right[i]` = next smaller-or-equal index.", T.arr("strength", strength), T.arr("left", left), T.arr("right", right));
        long[] prefix = new long[n + 1], pp = new long[n + 2];
        for (int i = 0; i < n; i++) prefix[i + 1] = (prefix[i] + strength[i]) % MOD;
        for (int i = 0; i <= n; i++) pp[i + 1] = (pp[i] + prefix[i]) % MOD;
        T.s("Prefix sums, then prefix sums of those", "`pp` lets us add up many subarray sums in O(1).", T.arr("prefix", prefix), T.arr("pp", pp));
        long ans = 0;
        for (int i = 0; i < n; i++) {
            int l = left[i], r = right[i];
            long pos = (pp[r + 1] - pp[i + 1] + MOD) % MOD * (i - l) % MOD;
            long neg = (pp[i + 1] - pp[l + 1] + MOD) % MOD * (r - i) % MOD;
            long sums = (pos - neg + MOD) % MOD;
            ans = (ans + strength[i] * sums) % MOD;
            T.s("Wizard " + i + " as the minimum", "Subarrays start in (" + l + ", " + i + "] and end in [" + i + ", " + r + "). Their sums total " + sums + "; times " + strength[i] + " adds " + (strength[i] * sums % MOD) + ". Running total: " + ans + ".",
                T.arr("strength", strength).p("i", i).range(l + 1, r - 1, "visit").m(i, "active"), T.v("l", l, "r", r, "sums", sums, "ans", ans));
        }
        return (int) ans;
    }
    //@end
}
