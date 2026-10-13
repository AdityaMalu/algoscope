import java.util.*;

/** Binary Search */
class BinSearch {
    static S win(int[] a, int lo, int hi, int mid, String midMark) {
        S s = T.arr("nums", a).p("lo", lo).p("hi", hi);
        for (int i = 0; i < a.length; i++) if (i < lo || i > hi) s.m(i, "dim");
        if (mid >= 0) s.p("mid", mid).m(mid, midMark);
        return s;
    }

    static void run() {
        P.begin("Binary Search", "search");
        P.category("Binary Search · Classic");
        P.summary("Find `target` in a sorted array in O(log n). Look at the middle of the window `[lo, hi]`; if it's too small, the target can only be to the right, otherwise to the left. Each step halves the window.");
        P.cx("O(1)", "O(log n)", "O(log n)", "O(1)", "Best case when the first middle is the target.");
        P.ideas("Invariant: if the target exists, it's inside `[lo, hi]`.",
            "`mid = lo + (hi - lo) / 2` avoids int overflow.",
            "Move `lo = mid + 1` or `hi = mid - 1`; `mid` itself is already ruled out.",
            "The loop ends when `lo > hi`: the window is empty.");
        P.pits("`while (lo < hi)` misses a one-element window.",
            "`(lo + hi) / 2` overflows for indices near 2³¹.",
            "`lo = mid` (without +1) can loop forever.");
        P.trace("nums = [-1, 0, 3, 5, 9, 12], target = 9", () -> search(new int[]{-1, 0, 3, 5, 9, 12}, 9));
        P.edge("Target missing", "nums = [-1, 0, 3, 5, 9, 12], target = 2", "-1", () -> search(new int[]{-1, 0, 3, 5, 9, 12}, 2), "The window shrinks to empty between 0 and 3.");
        P.edge("Single element, found", "nums = [5], target = 5", "0", () -> search(new int[]{5}, 5), "`lo = hi = mid = 0`.");
        P.edge("Single element, missing", "nums = [5], target = -5", "-1", () -> search(new int[]{5}, -5), "One comparison, then `hi = -1`.");
        P.edge("Target at the edges", "nums = [1, 2, 3, 4], target = 4", "3", () -> search(new int[]{1, 2, 3, 4}, 4), "`lo` walks to the last index.");
        P.edge("Smaller than everything", "nums = [2, 4, 6], target = 1", "-1", () -> search(new int[]{2, 4, 6}, 1), "`hi` drops below 0.");
        P.done();

        P.begin("Search Insert Position", "searchInsert");
        P.category("Binary Search · Lower bound");
        P.summary("Return the index of `target`, or where it would be inserted to keep the array sorted. That's the first index whose value is ≥ target (the lower bound). Binary search with a half-open window `[lo, hi)` finds it directly.");
        P.cx("O(log n)", "O(log n)", "O(log n)", "O(1)", "Always about log₂(n) halvings.");
        P.ideas("Answer = first index i with `nums[i] >= target`; it can be n (insert at the end).",
            "If `nums[mid] < target`, the answer is right of mid: `lo = mid + 1`.",
            "Otherwise mid might be the answer: `hi = mid`.",
            "When `lo == hi`, that's the answer.");
        P.pits("Starting with `hi = n - 1` can't return n for targets larger than everything.",
            "Mixing closed and half-open window rules.");
        P.trace("nums = [1, 3, 5, 6], target = 5", () -> searchInsert(new int[]{1, 3, 5, 6}, 5));
        P.edge("Insert in the middle", "nums = [1, 3, 5, 6], target = 2", "1", () -> searchInsert(new int[]{1, 3, 5, 6}, 2), "2 goes before 3.");
        P.edge("Insert at the end", "nums = [1, 3, 5, 6], target = 7", "4", () -> searchInsert(new int[]{1, 3, 5, 6}, 7), "The lower bound is n, past the last index.");
        P.edge("Insert at the front", "nums = [1, 3, 5, 6], target = 0", "0", () -> searchInsert(new int[]{1, 3, 5, 6}, 0), "Everything is ≥ 0.");
        P.edge("Single element", "nums = [1], target = 1", "0", () -> searchInsert(new int[]{1}, 1), "Found at index 0.");
        P.done();

        P.begin("Find Minimum in Rotated Sorted Array", "findMin");
        P.category("Binary Search · Rotated array");
        P.summary("A sorted array was rotated, e.g. [3, 4, 5, 1, 2]. Compare the middle with the rightmost element: if `nums[mid] > nums[hi]`, the drop (and the minimum) is to the right of mid; otherwise the minimum is at mid or to its left.");
        P.cx("O(log n)", "O(log n)", "O(log n)", "O(1)", "Values are distinct, so every comparison discards half.");
        P.ideas("The minimum is the only element smaller than its predecessor: the rotation point.",
            "`nums[mid] > nums[hi]` means the rotation point is in `(mid, hi]`.",
            "Otherwise `[mid, hi]` is sorted, so the minimum is in `[lo, mid]`.",
            "Stop when `lo == hi`.");
        P.pits("Comparing with `nums[lo]` instead of `nums[hi]` breaks on arrays that aren't rotated.",
            "Using `hi = mid - 1` in the else branch can skip the minimum.");
        P.trace("nums = [4, 5, 6, 7, 0, 1, 2]", () -> findMin(new int[]{4, 5, 6, 7, 0, 1, 2}));
        P.edge("Not rotated", "nums = [11, 13, 15, 17]", "11", () -> findMin(new int[]{11, 13, 15, 17}), "`hi` keeps moving left to index 0.");
        P.edge("Rotated by one", "nums = [2, 1]", "1", () -> findMin(new int[]{2, 1}), "`nums[0] > nums[1]`, so `lo` moves to 1.");
        P.edge("Single element", "nums = [1]", "1", () -> findMin(new int[]{1}), "Already `lo == hi`.");
        P.edge("Minimum at the end", "nums = [2, 3, 4, 5, 1]", "1", () -> findMin(new int[]{2, 3, 4, 5, 1}), "The drop is at the last position.");
        P.done();

        P.begin("Search a 2D Matrix", "searchMatrix");
        P.category("Binary Search · Flattened index");
        P.summary("Each row is sorted and starts after the previous row ends, so the whole matrix is one sorted list read row by row. Binary search over indices 0..m·n−1 and convert each index to `(i / n, i % n)`.");
        P.cx("O(1)", "O(log(m·n))", "O(log(m·n))", "O(1)", "One binary search over all cells.");
        P.ideas("Treat the matrix as a sorted 1D array of length m·n.",
            "Index k maps to row `k / n`, column `k % n`.",
            "Then it's plain binary search.");
        P.pits("Two separate binary searches (row, then column) also work but are easier to get wrong.",
            "Using m instead of n (the number of columns) in the index conversion.");
        P.trace("matrix = [[1, 3, 5, 7], [10, 11, 16, 20], [23, 30, 34, 60]], target = 16", () -> searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 16));
        P.edge("Missing value", "matrix = [[1, 3, 5, 7], [10, 11, 16, 20]], target = 13", "false", () -> searchMatrix(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}}, 13), "13 would sit between 11 and 16.");
        P.edge("Single cell", "matrix = [[5]], target = 5", "true", () -> searchMatrix(new int[][]{{5}}, 5), "One comparison.");
        P.edge("Single row", "matrix = [[1, 3]], target = 3", "true", () -> searchMatrix(new int[][]{{1, 3}}, 3), "Works like a normal array.");
        P.edge("Single column", "matrix = [[1], [3], [5]], target = 4", "false", () -> searchMatrix(new int[][]{{1}, {3}, {5}}, 4), "n = 1, so each index is its own row.");
        P.done();

        P.begin("Search in Rotated Sorted Array", "searchRotated");
        P.category("Binary Search · Rotated array");
        P.summary("Find `target` in a rotated sorted array in O(log n). At least one half around `mid` is always sorted. Check whether the target lies inside that sorted half; if so, search there, otherwise search the other half.");
        P.cx("O(1)", "O(log n)", "O(log n)", "O(1)", "Values are distinct.");
        P.ideas("If `nums[lo] <= nums[mid]`, the left half `[lo, mid]` is sorted; otherwise the right half is.",
            "For the sorted half you can test `target` against its two ends.",
            "If the target is inside that range, keep that half; otherwise keep the other.");
        P.pits("Using `<` instead of `<=` in `nums[lo] <= nums[mid]` breaks when lo == mid.",
            "Getting the inclusive/exclusive bounds wrong in the range checks.",
            "Finding the pivot first and then searching works, but is two passes.");
        P.trace("nums = [4, 5, 6, 7, 0, 1, 2], target = 0", () -> searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 0));
        P.edge("Target missing", "nums = [4, 5, 6, 7, 0, 1, 2], target = 3", "-1", () -> searchRotated(new int[]{4, 5, 6, 7, 0, 1, 2}, 3), "No half's range contains 3.");
        P.edge("Single element", "nums = [1], target = 0", "-1", () -> searchRotated(new int[]{1}, 0), "One check.");
        P.edge("Not rotated", "nums = [1, 2, 3, 4, 5], target = 4", "3", () -> searchRotated(new int[]{1, 2, 3, 4, 5}, 4), "The left half is always sorted.");
        P.edge("Two elements, rotated", "nums = [3, 1], target = 1", "1", () -> searchRotated(new int[]{3, 1}, 1), "`lo == mid` here, which is why the check uses `<=`.");
        P.done();

        P.begin("Koko Eating Bananas", "minEatingSpeed");
        P.category("Binary Search · On the answer");
        P.summary("Find the smallest eating speed k so Koko finishes all piles within h hours. Higher speeds only make it easier, so the feasible speeds form a range [answer, max]. Binary search k between 1 and the largest pile, checking total hours each time.");
        P.cx("O(n log m)", "O(n log m)", "O(n log m)", "O(1)", "m = largest pile. Each check is one pass over the piles.");
        P.ideas("Hours at speed k: sum of `ceil(pile / k)`.",
            "Feasibility is monotonic: if k works, k + 1 works too.",
            "Binary search for the first k that works.",
            "`(p + k - 1) / k` is an integer ceiling.");
        P.pits("Summing hours in an int can overflow for large inputs; use long.",
            "Upper bound must be max(piles); any faster gives the same hours.",
            "Using float division for the ceiling.");
        P.trace("piles = [3, 6, 7, 11], h = 8", () -> minEatingSpeed(new int[]{3, 6, 7, 11}, 8));
        P.edge("h equals the number of piles", "piles = [30, 11, 23, 4, 20], h = 5", "30", () -> minEatingSpeed(new int[]{30, 11, 23, 4, 20}, 5), "One hour per pile means eating the biggest pile in one hour.");
        P.edge("Plenty of time", "piles = [5, 5], h = 100", "1", () -> minEatingSpeed(new int[]{5, 5}, 100), "Speed 1 already fits.");
        P.edge("Single pile", "piles = [10], h = 3", "4", () -> minEatingSpeed(new int[]{10}, 3), "ceil(10 / 4) = 3 hours; speed 3 would need 4.");
        P.edge("Huge pile (overflow check)", "piles = [1000000000], h = 2", "500000000", () -> minEatingSpeed(new int[]{1000000000}, 2), "Hours are summed in a long, so nothing overflows.");
        P.done();

        P.begin("Capacity To Ship Packages Within D Days", "shipWithinDays");
        P.category("Binary Search · On the answer");
        P.summary("Find the smallest ship capacity that ships all packages, in order, within `days`. The answer lies between the heaviest package and the total weight. For a candidate capacity, greedily fill each day; binary search the smallest capacity that needs ≤ days.");
        P.cx("O(n log S)", "O(n log S)", "O(n log S)", "O(1)", "S = sum of weights. Each check is a greedy pass.");
        P.ideas("Lower bound is `max(weights)`: the heaviest package must fit.",
            "Upper bound is `sum(weights)`: everything in one day.",
            "Greedy check: load packages until the next one won't fit, then start a new day.",
            "Bigger capacity never needs more days, so binary search applies.");
        P.pits("Starting `lo` at 1 lets capacities smaller than one package \"work\" in the check.",
            "Reordering packages; they must ship in the given order.");
        P.trace("weights = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10], days = 5", () -> shipWithinDays(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 5));
        P.edge("One day", "weights = [3, 2, 2, 4, 1, 4], days = 1", "16", () -> shipWithinDays(new int[]{3, 2, 2, 4, 1, 4}, 1), "Everything at once: the total weight.");
        P.edge("A day per package", "weights = [1, 2, 3], days = 3", "3", () -> shipWithinDays(new int[]{1, 2, 3}, 3), "The heaviest package sets the capacity.");
        P.edge("More days than packages", "weights = [5, 1], days = 10", "5", () -> shipWithinDays(new int[]{5, 1}, 10), "Extra days don't help below the heaviest package.");
        P.edge("Classic", "weights = [1, 2, 3, 1, 1], days = 4", "3", () -> shipWithinDays(new int[]{1, 2, 3, 1, 1}, 4), "[1, 2], [3], [1, 1] fits in 3 days at capacity 3.");
        P.done();
    }

    //@code search
    static int search(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            T.s("Look at the middle", "`mid = " + mid + "`, `nums[mid] = " + nums[mid] + "`. Compare with " + target + ".", win(nums, lo, hi, mid, "compare"), T.v("lo", lo, "hi", hi, "mid", mid, "target", target));
            if (nums[mid] == target) {
                T.n("Found", "`nums[" + mid + "] = " + target + "`.", win(nums, lo, hi, mid, "found"), T.v("lo", lo, "hi", hi, "mid", mid));
                return mid;
            }
            if (nums[mid] < target) {
                lo = mid + 1;
                T.s("Go right", nums[mid] + " < " + target + ", so discard the left half. `lo = " + lo + "`.", win(nums, lo, hi, -1, ""), T.v("lo", lo, "hi", hi, "mid", mid));
            } else {
                hi = mid - 1;
                T.s("Go left", nums[mid] + " > " + target + ", so discard the right half. `hi = " + hi + "`.", win(nums, lo, hi, -1, ""), T.v("lo", lo, "hi", hi, "mid", mid));
            }
        }
        T.n("Not found", "The window is empty (`lo > hi`).", win(nums, lo, hi, -1, ""), T.v("lo", lo, "hi", hi));
        return -1;
    }
    //@end

    //@code searchInsert
    static int searchInsert(int[] nums, int target) {
        int lo = 0, hi = nums.length;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] < target) {
                lo = mid + 1;
                T.s("Too small, go right", "`nums[" + mid + "] = " + nums[mid] + "` < " + target + ", so the insert point is after " + mid + ". `lo = " + lo + "`.",
                    T.arr("nums", nums).p("lo", lo).p("hi", hi).p("mid", mid).m(mid, "dim").range(0, lo - 1, "dim"), T.v("lo", lo, "hi", hi, "mid", mid));
            } else {
                hi = mid;
                T.s("Could be here, go left", "`nums[" + mid + "] = " + nums[mid] + "` ≥ " + target + ", so " + mid + " is a candidate. `hi = " + hi + "`.",
                    T.arr("nums", nums).p("lo", lo).p("hi", hi).p("mid", mid).m(mid, "compare").range(hi + 1, nums.length - 1, "dim"), T.v("lo", lo, "hi", hi, "mid", mid));
            }
        }
        T.n("Answer", "`lo == hi == " + lo + "`: the first index whose value is ≥ " + target + ".", T.arr("nums", nums).p("lo", lo).m(lo, "found"), T.v("lo", lo));
        return lo;
    }
    //@end

    //@code findMin
    static int findMin(int[] nums) {
        int lo = 0, hi = nums.length - 1;
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] > nums[hi]) {
                lo = mid + 1;
                T.s("Drop is on the right", "`nums[mid] = " + nums[mid] + "` > `nums[hi] = " + nums[hi] + "`, so the minimum is after mid. `lo = " + lo + "`.", win(nums, lo, hi, mid, "dim"), T.v("lo", lo, "hi", hi, "mid", mid));
            } else {
                hi = mid;
                T.s("Right side is sorted", "`nums[mid] = " + nums[mid] + "` ≤ `nums[hi]`, so the minimum is at mid or before it. `hi = " + hi + "`.", win(nums, lo, hi, mid, "compare"), T.v("lo", lo, "hi", hi, "mid", mid));
            }
        }
        T.n("Minimum found", "`lo == hi == " + lo + "`, value " + nums[lo] + ".", win(nums, lo, lo, lo, "found"), T.v("lo", lo));
        return nums[lo];
    }
    //@end

    static S cell(int[][] m, int lo, int hi, int k, String mark) {
        int n = m[0].length;
        S s = T.mat("matrix", m);
        for (int i = 0; i < m.length * n; i++) if (i < lo || i > hi) s.m(i / n, i % n, "dim");
        if (k >= 0) s.m(k / n, k % n, mark);
        return s;
    }

    //@code searchMatrix
    static boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int lo = 0, hi = m * n - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int val = matrix[mid / n][mid % n];
            T.s("Middle cell", "Index " + mid + " is row " + (mid / n) + ", column " + (mid % n) + ": value " + val + ".", cell(matrix, lo, hi, mid, "compare"), T.v("lo", lo, "hi", hi, "mid", mid, "val", val, "target", target));
            if (val == target) {
                T.n("Found", val + " is the target.", cell(matrix, lo, hi, mid, "found"), T.v("mid", mid));
                return true;
            }
            if (val < target) lo = mid + 1;
            else hi = mid - 1;
        }
        T.n("Not in the matrix", "The search range is empty.", cell(matrix, lo, hi, -1, ""), T.v("lo", lo, "hi", hi));
        return false;
    }
    //@end

    //@code searchRotated
    static int searchRotated(int[] nums, int target) {
        int lo = 0, hi = nums.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (nums[mid] == target) {
                T.n("Found", "`nums[" + mid + "] = " + target + "`.", win(nums, lo, hi, mid, "found"), T.v("lo", lo, "hi", hi, "mid", mid));
                return mid;
            }
            if (nums[lo] <= nums[mid]) {
                boolean in = nums[lo] <= target && target < nums[mid]; // @hide
                T.n("Left half is sorted", "`[" + nums[lo] + " … " + nums[mid] + "]` is sorted. " + target + (in ? " is inside it, so go left." : " is not inside it, so go right."),
                    win(nums, lo, hi, mid, "compare").range(lo, mid - 1, "visit"), T.v("lo", lo, "hi", hi, "mid", mid, "target", target));
                if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;
                else lo = mid + 1;
            } else {
                boolean in = nums[mid] < target && target <= nums[hi]; // @hide
                T.n("Right half is sorted", "`[" + nums[mid] + " … " + nums[hi] + "]` is sorted. " + target + (in ? " is inside it, so go right." : " is not inside it, so go left."),
                    win(nums, lo, hi, mid, "compare").range(mid + 1, hi, "visit"), T.v("lo", lo, "hi", hi, "mid", mid, "target", target));
                if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;
                else hi = mid - 1;
            }
        }
        T.n("Not found", "The window is empty.", win(nums, lo, hi, -1, ""), T.v("lo", lo, "hi", hi));
        return -1;
    }
    //@end

    //@code minEatingSpeed
    static int minEatingSpeed(int[] piles, int h) {
        int lo = 1, hi = 0;
        for (int p : piles) hi = Math.max(hi, p);
        T.s("Search range of speeds", "Speed is between 1 and the biggest pile, " + hi + ".", T.arr("piles", piles), T.v("lo", lo, "hi", hi, "h", h));
        while (lo < hi) {
            int k = lo + (hi - lo) / 2;
            long hours = 0;
            for (int p : piles) hours += (p + k - 1) / k;
            T.s("Try speed k = " + k, "Hours needed: " + hoursText(piles, k) + " = " + hours + ". " + (hours <= h ? "Fits in " + h + ", so try slower." : "More than " + h + ", so go faster."),
                T.arr("piles", piles), T.arr("hours per pile", perPile(piles, k)), T.v("lo", lo, "hi", hi, "k", k, "hours", hours, "h", h));
            if (hours <= h) hi = k;
            else lo = k + 1;
        }
        T.n("Slowest speed that fits", "`lo == hi == " + lo + "`.", T.arr("piles", piles), T.arr("hours per pile", perPile(piles, lo)), T.v("k", lo));
        return lo;
    }
    //@end
    static int[] perPile(int[] p, int k) { int[] r = new int[p.length]; for (int i = 0; i < p.length; i++) r[i] = (p[i] + k - 1) / k; return r; }
    static String hoursText(int[] p, int k) { StringJoiner j = new StringJoiner(" + "); for (int x : perPile(p, k)) j.add(String.valueOf(x)); return j.toString(); }

    //@code shipWithinDays
    static int shipWithinDays(int[] weights, int days) {
        int lo = 0, hi = 0;
        for (int w : weights) { lo = Math.max(lo, w); hi += w; }
        T.s("Capacity range", "At least the heaviest package (" + lo + "), at most everything at once (" + hi + ").", T.arr("weights", weights), T.v("lo", lo, "hi", hi, "days", days));
        while (lo < hi) {
            int cap = lo + (hi - lo) / 2;
            int need = daysNeeded(weights, cap);
            T.s("Try capacity " + cap, "Loading greedily takes " + need + " day" + (need == 1 ? "" : "s") + ". " + (need <= days ? "That fits, so try smaller." : "Too many, so go bigger."),
                T.arr("weights", weights), T.arr("day of each package", dayOf(weights, cap)), T.v("lo", lo, "hi", hi, "cap", cap, "need", need, "days", days));
            if (need <= days) hi = cap;
            else lo = cap + 1;
        }
        T.n("Smallest capacity", "`lo == hi == " + lo + "`.", T.arr("weights", weights), T.arr("day of each package", dayOf(weights, lo)), T.v("capacity", lo));
        return lo;
    }

    static int daysNeeded(int[] weights, int cap) {
        int d = 1, load = 0;
        for (int w : weights) {
            if (load + w > cap) { d++; load = 0; }
            load += w;
        }
        return d;
    }
    //@end
    static int[] dayOf(int[] w, int cap) { int[] r = new int[w.length]; int d = 1, load = 0; for (int i = 0; i < w.length; i++) { if (load + w[i] > cap) { d++; load = 0; } load += w[i]; r[i] = d; } return r; }
}
