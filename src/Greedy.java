import java.util.*;

/** Greedy & Intervals */
class Greedy {
    static List<String> iv(int[][] a) { List<String> l = new ArrayList<>(); for (int[] x : a) l.add("[" + x[0] + "," + x[1] + "]"); return l; }
    static List<String> iv(List<int[]> a) { List<String> l = new ArrayList<>(); for (int[] x : a) l.add("[" + x[0] + "," + x[1] + "]"); return l; }
    static S ivs(String name, int[][] a) { return T.arr(name, iv(a)); }
    static S ivs(String name, List<int[]> a) { return T.arr(name, iv(a)); }

    static void run() {
        P.begin("Maximum Subarray", "maxSubArray");
        P.category("Greedy · Kadane's algorithm");
        P.summary("Find the contiguous subarray with the largest sum. Walk once, keeping the best sum of a subarray ending here: either extend the previous one or start fresh at the current number, whichever is bigger.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass, two variables.");
        P.ideas("`cur = max(x, cur + x)`: a negative running sum only drags future sums down, so drop it.",
            "Track the best `cur` ever seen.",
            "Initialise with the first element so all-negative arrays work.");
        P.pits("Initialising best to 0 returns 0 for all-negative input.",
            "Resetting `cur` to 0 instead of to x.");
        P.trace("nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]", () -> maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}));
        P.edge("All negative", "nums = [-3, -1, -2]", "-1", () -> maxSubArray(new int[]{-3, -1, -2}), "The best subarray is the single largest element.");
        P.edge("Single element", "nums = [5]", "5", () -> maxSubArray(new int[]{5}), "Just itself.");
        P.edge("All positive", "nums = [1, 2, 3]", "6", () -> maxSubArray(new int[]{1, 2, 3}), "The whole array.");
        P.edge("Zero in the middle", "nums = [2, 0, -1, 3]", "4", () -> maxSubArray(new int[]{2, 0, -1, 3}), "Extending through the -1 still pays off.");
        P.done();

        P.begin("Jump Game", "canJump");
        P.category("Greedy · Farthest reach");
        P.summary("Each value is the maximum jump from that index. Can you reach the end? Track the farthest index reachable so far. If you ever stand on an index beyond it, you're stuck.");
        P.cx("O(1)", "O(n)", "O(n)", "O(1)", "Stops as soon as the end is reachable.");
        P.ideas("`reach = max(reach, i + nums[i])` for every index you can stand on.",
            "If `i > reach`, index i can't be reached, and neither can anything after it.",
            "No need to know which jumps to take.");
        P.pits("BFS or DP over all jumps is O(n²).",
            "Treating a 0 at the last index as a problem; you're already there.");
        P.trace("nums = [2, 3, 1, 1, 4]", () -> canJump(new int[]{2, 3, 1, 1, 4}));
        P.edge("Stuck on a zero", "nums = [3, 2, 1, 0, 4]", "false", () -> canJump(new int[]{3, 2, 1, 0, 4}), "Every path lands on index 3, which can't move.");
        P.edge("Single element", "nums = [0]", "true", () -> canJump(new int[]{0}), "You start at the end.");
        P.edge("Zero at the start", "nums = [0, 1]", "false", () -> canJump(new int[]{0, 1}), "You can't leave index 0.");
        P.edge("Big first jump", "nums = [5, 0, 0, 0, 0]", "true", () -> canJump(new int[]{5, 0, 0, 0, 0}), "The first jump covers everything.");
        P.done();

        P.begin("Merge Intervals", "merge");
        P.category("Intervals · Sort and sweep");
        P.summary("Merge all overlapping intervals. Sort by start. Walk through them: if an interval starts before the last merged one ends, extend that end; otherwise start a new merged interval.");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Sorting dominates.");
        P.ideas("After sorting by start, overlapping intervals are next to each other.",
            "Only the last merged interval can overlap the current one.",
            "Touching intervals like [1,4] and [4,5] count as overlapping.");
        P.pits("Setting the new end to the current end instead of `max(end, cur.end)` breaks on [1,10], [2,3].",
            "Comparing with `<` instead of `<=` misses touching intervals.");
        P.trace("intervals = [[1, 3], [8, 10], [2, 6], [15, 18], [9, 12]]", () -> merge(new int[][]{{1, 3}, {8, 10}, {2, 6}, {15, 18}, {9, 12}}));
        P.edge("Touching", "intervals = [[1, 4], [4, 5]]", "[[1, 5]]", () -> merge(new int[][]{{1, 4}, {4, 5}}), "Sharing an endpoint counts as overlap.");
        P.edge("Contained", "intervals = [[1, 10], [2, 3]]", "[[1, 10]]", () -> merge(new int[][]{{1, 10}, {2, 3}}), "max keeps the end at 10.");
        P.edge("Single interval", "intervals = [[5, 7]]", "[[5, 7]]", () -> merge(new int[][]{{5, 7}}), "Nothing to merge.");
        P.edge("No overlaps", "intervals = [[1, 2], [3, 4]]", "[[1, 2], [3, 4]]", () -> merge(new int[][]{{1, 2}, {3, 4}}), "Kept separate.");
        P.done();

        P.begin("Insert Interval", "insert");
        P.category("Intervals · Three phases");
        P.summary("Insert a new interval into a sorted, non-overlapping list and merge as needed. Copy every interval that ends before the new one starts, merge every interval that overlaps it, then copy the rest.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "The input is already sorted, so no sort is needed.");
        P.ideas("Phase 1: `end < new.start` → entirely before, copy it.",
            "Phase 2: `start <= new.end` → overlaps, grow the new interval.",
            "Phase 3: everything else is after, copy it.");
        P.pits("Re-sorting and running Merge Intervals works but is O(n log n).",
            "Forgetting to add the merged new interval itself.");
        P.trace("intervals = [[1, 2], [3, 5], [6, 7], [8, 10], [12, 16]], newInterval = [4, 8]", () -> insert(new int[][]{{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}}, new int[]{4, 8}));
        P.edge("Empty list", "intervals = [], newInterval = [5, 7]", "[[5, 7]]", () -> insert(new int[][]{}, new int[]{5, 7}), "Just the new interval.");
        P.edge("Insert at the end", "intervals = [[1, 5]], newInterval = [6, 8]", "[[1, 5], [6, 8]]", () -> insert(new int[][]{{1, 5}}, new int[]{6, 8}), "No overlap.");
        P.edge("Swallows everything", "intervals = [[2, 3], [4, 5]], newInterval = [1, 9]", "[[1, 9]]", () -> insert(new int[][]{{2, 3}, {4, 5}}, new int[]{1, 9}), "Every interval is inside the new one.");
        P.done();

        P.begin("Jump Game II", "jump");
        P.category("Greedy · BFS levels");
        P.summary("Find the fewest jumps to reach the end. Think in levels: all indices reachable with k jumps form a window. While scanning the current window, track the farthest you could get with one more jump; when you reach the window's end, take a jump and move to the next window.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass.");
        P.ideas("`end` marks the edge of what the current number of jumps can reach.",
            "`far` is the farthest reachable with one more jump from anywhere in the window.",
            "At `i == end`, you must jump: `jumps++`, `end = far`.",
            "Loop to `n − 2`: you never jump from the last index.");
        P.pits("Looping to n − 1 counts an extra jump when `end` lands exactly on the last index.",
            "Greedily jumping as far as possible from each position is wrong.");
        P.trace("nums = [2, 3, 1, 1, 4]", () -> jump(new int[]{2, 3, 1, 1, 4}));
        P.edge("Single element", "nums = [0]", "0", () -> jump(new int[]{0}), "Already at the end.");
        P.edge("Ones", "nums = [1, 1, 1, 1]", "3", () -> jump(new int[]{1, 1, 1, 1}), "One step at a time.");
        P.edge("One big jump", "nums = [4, 1, 1, 1, 1]", "1", () -> jump(new int[]{4, 1, 1, 1, 1}), "The first jump reaches the end.");
        P.done();

        P.begin("Gas Station", "canCompleteCircuit");
        P.category("Greedy · Reset on deficit");
        P.summary("Find the station from which you can drive the whole circle. If total gas ≥ total cost, a solution exists. Drive from station 0 keeping the tank; whenever the tank goes negative, no station from the current start up to here works, so start fresh at the next station.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass.");
        P.ideas("If the total gas is less than the total cost, it's impossible from anywhere.",
            "If you run dry at station i starting from s, every start between s and i also runs dry by i.",
            "So jump the start to i + 1 and reset the tank.");
        P.pits("Trying every start is O(n²).",
            "Forgetting the total check: the last start candidate isn't guaranteed without it.");
        P.trace("gas = [1, 2, 3, 4, 5], cost = [3, 4, 5, 1, 2]", () -> canCompleteCircuit(new int[]{1, 2, 3, 4, 5}, new int[]{3, 4, 5, 1, 2}));
        P.edge("Impossible", "gas = [2, 3, 4], cost = [3, 4, 3]", "-1", () -> canCompleteCircuit(new int[]{2, 3, 4}, new int[]{3, 4, 3}), "Total gas 9 < total cost 10.");
        P.edge("Single station", "gas = [5], cost = [4]", "0", () -> canCompleteCircuit(new int[]{5}, new int[]{4}), "Enough to loop back.");
        P.edge("Exactly enough", "gas = [1, 1], cost = [1, 1]", "0", () -> canCompleteCircuit(new int[]{1, 1}, new int[]{1, 1}), "The tank hits 0 but never goes negative.");
        P.done();

        P.begin("Non-overlapping Intervals", "eraseOverlapIntervals");
        P.category("Intervals · Earliest end first");
        P.summary("Remove the fewest intervals so the rest don't overlap. Sort by end time and greedily keep each interval that starts at or after the last kept end. Keeping the earliest-ending interval leaves the most room for the rest.");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(1)", "Sorting dominates.");
        P.ideas("This is activity selection: maximise kept intervals, then removed = n − kept.",
            "Sort by end, not start.",
            "Touching intervals ([1,2] and [2,3]) don't overlap here.");
        P.pits("Sorting by start and keeping the first can keep a long interval that blocks many.",
            "Using `>` instead of `>=` treats touching intervals as overlapping.");
        P.trace("intervals = [[1, 2], [2, 3], [3, 4], [1, 3]]", () -> eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 3}}));
        P.edge("All identical", "intervals = [[1, 2], [1, 2], [1, 2]]", "2", () -> eraseOverlapIntervals(new int[][]{{1, 2}, {1, 2}, {1, 2}}), "Keep one.");
        P.edge("Already disjoint", "intervals = [[1, 2], [2, 3]]", "0", () -> eraseOverlapIntervals(new int[][]{{1, 2}, {2, 3}}), "Touching is allowed.");
        P.edge("One long interval", "intervals = [[1, 100], [2, 3], [4, 5]]", "1", () -> eraseOverlapIntervals(new int[][]{{1, 100}, {2, 3}, {4, 5}}), "Removing the long one keeps two.");
        P.done();

        P.begin("Minimum Number of Arrows to Burst Balloons", "findMinArrowShots");
        P.category("Intervals · Earliest end first");
        P.summary("Each balloon spans [start, end] on the x-axis; an arrow at x bursts every balloon covering x. Sort by end; shoot at the first balloon's end, which bursts everything starting at or before it. The next balloon that starts after the arrow needs a new arrow.");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(1)", "Sorting dominates.");
        P.ideas("Shooting at the smallest end bursts the most balloons that must be burst anyway.",
            "A balloon starting after the arrow's x needs a new arrow.",
            "Touching counts: a balloon starting exactly at x is burst.");
        P.pits("`a[1] - b[1]` in the comparator overflows for values near ±2³¹; use Integer.compare.",
            "Using `>=` when comparing start to the arrow, which misses touching balloons.");
        P.trace("points = [[10, 16], [2, 8], [1, 6], [7, 12]]", () -> findMinArrowShots(new int[][]{{10, 16}, {2, 8}, {1, 6}, {7, 12}}));
        P.edge("No overlaps", "points = [[1, 2], [3, 4], [5, 6], [7, 8]]", "4", () -> findMinArrowShots(new int[][]{{1, 2}, {3, 4}, {5, 6}, {7, 8}}), "One arrow each.");
        P.edge("Touching balloons", "points = [[1, 2], [2, 3], [3, 4], [4, 5]]", "2", () -> findMinArrowShots(new int[][]{{1, 2}, {2, 3}, {3, 4}, {4, 5}}), "Arrows at 2 and 4.");
        P.edge("Overflow-prone values", "points = [[-2147483646, -2147483645], [2147483646, 2147483647]]", "2", () -> findMinArrowShots(new int[][]{{-2147483646, -2147483645}, {2147483646, 2147483647}}), "Integer.compare avoids the subtraction overflow.");
        P.done();
    }

    //@code maxSubArray
    static int maxSubArray(int[] nums) {
        int cur = nums[0], best = nums[0];
        int start = 0, bestL = 0, bestR = 0; // @hide
        T.s("Start with the first element", "`cur = best = " + nums[0] + "`.", T.arr("nums", nums).m(0, "active"), T.v("cur", cur, "best", best));
        for (int i = 1; i < nums.length; i++) {
            if (cur + nums[i] < nums[i]) start = i; // @hide
            cur = Math.max(nums[i], cur + nums[i]);
            best = Math.max(best, cur);
            if (best == cur) { bestL = start; bestR = i; } // @hide
            T.s(cur == nums[i] && start == i ? "Start fresh at " + nums[i] : "Extend with " + nums[i], (start == i ? "The previous sum was negative, so start a new subarray here." : "Adding " + nums[i] + " to the running sum.") + " `cur = " + cur + "`, best = " + best + ".",
                T.arr("nums", nums).p("i", i).range(start, i, "visit").range(bestL, bestR, "found").m(i, "active"), T.v("cur", cur, "best", best));
        }
        return best;
    }
    //@end

    //@code canJump
    static boolean canJump(int[] nums) {
        int reach = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > reach) {
                T.n("Stuck", "Index " + i + " is beyond the farthest reachable index " + reach + ".", T.arr("nums", nums).p("i", i).range(0, reach, "visit").range(reach + 1, nums.length - 1, "error"), T.v("i", i, "reach", reach));
                return false;
            }
            reach = Math.max(reach, i + nums[i]);
            T.s("Stand on " + i, "From here you can reach index " + (i + nums[i]) + ". Farthest so far: " + reach + ".", T.arr("nums", nums).p("i", i).range(0, Math.min(reach, nums.length - 1), "visit").m(i, "active"), T.v("i", i, "reach", reach));
            if (reach >= nums.length - 1) {
                T.n("End reachable", "The last index " + (nums.length - 1) + " is within reach.", T.arr("nums", nums).range(0, nums.length - 1, "found"), T.v("reach", reach));
                return true;
            }
        }
        return true;
    }
    //@end

    //@code merge
    static int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        T.s("Sort by start", "Overlapping intervals are now neighbours.", ivs("intervals", intervals));
        List<int[]> res = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            int[] cur = intervals[i];
            if (!res.isEmpty() && cur[0] <= res.get(res.size() - 1)[1]) {
                int[] last = res.get(res.size() - 1);
                last[1] = Math.max(last[1], cur[1]);
                T.s("Overlap: extend", "[" + cur[0] + "," + cur[1] + "] starts before the last merged interval ends. Merged: [" + last[0] + "," + last[1] + "].", ivs("intervals", intervals).p("i", i).range(0, i - 1, "done").m(i, "active"), ivs("res", res).m(res.size() - 1, "swap"));
            } else {
                res.add(new int[]{cur[0], cur[1]});
                T.s("No overlap: new interval", "[" + cur[0] + "," + cur[1] + "] starts after the last merged end.", ivs("intervals", intervals).p("i", i).range(0, i - 1, "done").m(i, "active"), ivs("res", res).m(res.size() - 1, "swap"));
            }
        }
        return res.toArray(new int[0][]);
    }
    //@end

    //@code insert
    static int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> res = new ArrayList<>();
        int i = 0, n = intervals.length;
        int[] add = newInterval.clone();
        while (i < n && intervals[i][1] < add[0]) {
            res.add(intervals[i]);
            T.s("Before: copy", "[" + intervals[i][0] + "," + intervals[i][1] + "] ends before " + add[0] + ".", ivs("intervals", intervals).p("i", i).m(i, "done"), T.arr("new", add), ivs("res", res));
            i++;
        }
        while (i < n && intervals[i][0] <= add[1]) {
            add[0] = Math.min(add[0], intervals[i][0]);
            add[1] = Math.max(add[1], intervals[i][1]);
            T.s("Overlap: absorb", "[" + intervals[i][0] + "," + intervals[i][1] + "] overlaps. The new interval grows to [" + add[0] + "," + add[1] + "].", ivs("intervals", intervals).p("i", i).m(i, "swap"), T.arr("new", add).range(0, 1, "swap"), ivs("res", res));
            i++;
        }
        res.add(add);
        T.s("Place the merged interval", "[" + add[0] + "," + add[1] + "] goes in.", ivs("intervals", intervals), ivs("res", res).m(res.size() - 1, "found"));
        while (i < n) {
            res.add(intervals[i]);
            i++;
        }
        T.s("After: copy the rest", "The remaining intervals start after the merged one ends.", ivs("res", res));
        return res.toArray(new int[0][]);
    }
    //@end

    //@code jump
    static int jump(int[] nums) {
        int jumps = 0, end = 0, far = 0;
        for (int i = 0; i < nums.length - 1; i++) {
            far = Math.max(far, i + nums[i]);
            T.s("Scan index " + i, "With one more jump you could reach " + far + ".", T.arr("nums", nums).p("i", i).p("end", end).range(0, end, "visit").m(i, "active"), T.v("i", i, "jumps", jumps, "end", end, "far", far));
            if (i == end) {
                jumps++;
                end = far;
                T.s("Jump", "This window is finished. Jump: " + jumps + " so far; the new window ends at " + end + ".", T.arr("nums", nums).p("i", i).p("end", Math.min(end, nums.length - 1)).range(i + 1, Math.min(end, nums.length - 1), "swap"), T.v("i", i, "jumps", jumps, "end", end, "far", far));
            }
        }
        return jumps;
    }
    //@end

    //@code canCompleteCircuit
    static int canCompleteCircuit(int[] gas, int[] cost) {
        int total = 0, tank = 0, start = 0;
        for (int i = 0; i < gas.length; i++) {
            int gain = gas[i] - cost[i];
            total += gain;
            tank += gain;
            T.n(tank < 0 ? "Ran dry at " + i : "Drive past station " + i, tank < 0 ? "Tank is " + tank + ". No start from " + start + " to " + i + " works; try " + (i + 1) + "." : "Gain " + gain + "; tank is " + tank + ".",
                T.arr("gas", gas).p("start", start).p("i", i).range(start, i, tank < 0 ? "dim" : "visit").m(i, tank < 0 ? "error" : "active"), T.arr("cost", cost).m(i, tank < 0 ? "error" : "active"), T.v("tank", tank, "total", total));
            if (tank < 0) {
                start = i + 1;
                tank = 0;
            }
        }
        T.n(total >= 0 ? "Start at " + start : "Impossible", total >= 0 ? "Total gas covers total cost, so the last candidate start works." : "Total gas is less than total cost.", T.arr("gas", gas).m(start, total >= 0 ? "found" : "dim"), T.v("total", total, "start", start));
        return total >= 0 ? start : -1;
    }
    //@end

    //@code eraseOverlapIntervals
    static int eraseOverlapIntervals(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        T.s("Sort by end", "Earliest-ending intervals leave the most room.", ivs("intervals", intervals));
        int kept = 0, lastEnd = Integer.MIN_VALUE;
        Map<Integer, String> marks = new HashMap<>(); // @hide
        for (int i = 0; i < intervals.length; i++) {
            boolean keep = intervals[i][0] >= lastEnd; // @hide
            marks.put(i, keep ? "done" : "dim"); // @hide
            T.n((keep ? "Keep [" : "Remove [") + intervals[i][0] + "," + intervals[i][1] + "]", keep ? "It starts at or after the last kept end. Kept: " + (kept + 1) + "." : "It starts before " + lastEnd + ", so it overlaps the last kept interval.",
                ivs("intervals", intervals).mark(marks).p("i", i).m(i, keep ? "found" : "error"), T.v("kept", keep ? kept + 1 : kept, "lastEnd", keep ? intervals[i][1] : lastEnd));
            if (intervals[i][0] >= lastEnd) {
                kept++;
                lastEnd = intervals[i][1];
            }
        }
        return intervals.length - kept;
    }
    //@end

    //@code findMinArrowShots
    static int findMinArrowShots(int[][] points) {
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        T.s("Sort by end", "Balloons that end earliest decide where arrows go.", ivs("balloons", points));
        int arrows = 1;
        long x = points[0][1];
        T.s("First arrow at x = " + x, "Shoot at the earliest end.", ivs("balloons", points).m(0, "found"), T.v("arrows", arrows, "x", x));
        for (int i = 1; i < points.length; i++) {
            boolean fresh = points[i][0] > x; // @hide
            T.n(fresh ? "New arrow at x = " + points[i][1] : "Already burst", fresh ? "This balloon starts after the last arrow." : "[" + points[i][0] + "," + points[i][1] + "] covers x = " + x + ".",
                ivs("balloons", points).p("i", i).range(0, i - 1, "done").m(i, fresh ? "found" : "visit"), T.v("arrows", fresh ? arrows + 1 : arrows, "x", fresh ? points[i][1] : x));
            if (points[i][0] > x) {
                arrows++;
                x = points[i][1];
            }
        }
        return arrows;
    }
    //@end
}
