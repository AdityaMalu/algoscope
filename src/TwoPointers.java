import java.util.*;

/** Two Pointers */
class TwoPointers {
    static void run() {
        P.begin("Valid Palindrome", "isPalindrome");
        P.category("Two Pointers · Converging");
        P.summary("Check whether a string reads the same both ways after dropping non-alphanumeric characters and ignoring case. Put one pointer at each end, skip characters that don't count, compare, and move inward.");
        P.cx("O(1)", "O(n)", "O(n)", "O(1)", "Best case is a mismatch on the first comparison. No cleaned copy of the string is built.");
        P.ideas("A palindrome mirrors around its centre, so compare `s[l]` with `s[r]` and move both inward.",
            "Skip punctuation and spaces in place instead of building a filtered string.",
            "Lower-case both characters before comparing.",
            "Keep `l < r` in the inner skip loops so the pointers never cross.");
        P.pits("Building a cleaned string uses O(n) extra space; the interviewer may ask for O(1).",
            "Dropping the `l < r` guard in the skip loops runs off the end on strings like \".,\".",
            "`Character.isLetter` misses digits; the problem counts digits too.");
        P.trace("s = \"A man, a plan, a canal: Panama\"", () -> isPalindrome("A man, a plan, a canal: Panama"));
        P.edge("Not a palindrome", "s = \"race a car\"", "false", () -> isPalindrome("race a car"), "After cleaning it's \"raceacar\"; 'e' and 'a' mismatch.");
        P.edge("Only punctuation", "s = \" .,\"", "true", () -> isPalindrome(" .,"), "Nothing to compare, so it counts as a palindrome.");
        P.edge("Digits matter", "s = \"0P\"", "false", () -> isPalindrome("0P"), "'0' and 'p' differ; digits are not skipped.");
        P.edge("Single character", "s = \"a\"", "true", () -> isPalindrome("a"), "`l == r` from the start, so the loop never runs.");
        P.edge("Mixed case", "s = \"Aba\"", "true", () -> isPalindrome("Aba"), "'A' and 'a' match after lower-casing.");
        P.done();

        P.begin("Move Zeroes", "moveZeroes");
        P.category("Two Pointers · Read/write");
        P.summary("Move every zero to the end while keeping the order of the non-zero values, in place. A write pointer `w` marks where the next non-zero belongs; the read pointer swaps each non-zero into place.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass, in place.");
        P.ideas("Everything left of `w` is the non-zero values in their original order.",
            "When `r` finds a non-zero, swap it to `w` and advance `w`.",
            "Swapping (instead of overwriting) leaves the zeros behind at the end automatically.");
        P.pits("Overwriting and then filling zeros works too, but takes two passes.",
            "Moving zeros by shifting elements is O(n²).",
            "Swapping when `w == r` is harmless; don't add special cases for it.");
        P.trace("nums = [0, 1, 0, 3, 12]", () -> { int[] a = {0, 1, 0, 3, 12}; moveZeroes(a); return a; });
        P.edge("No zeros", "nums = [1, 2, 3]", "[1, 2, 3]", () -> { int[] a = {1, 2, 3}; moveZeroes(a); return a; }, "Each element swaps with itself.");
        P.edge("All zeros", "nums = [0, 0, 0]", "[0, 0, 0]", () -> { int[] a = {0, 0, 0}; moveZeroes(a); return a; }, "`w` never moves.");
        P.edge("Single element", "nums = [0]", "[0]", () -> { int[] a = {0}; moveZeroes(a); return a; }, "Nothing to move.");
        P.edge("Zeros already at the end", "nums = [4, 2, 0, 0]", "[4, 2, 0, 0]", () -> { int[] a = {4, 2, 0, 0}; moveZeroes(a); return a; }, "Order is preserved.");
        P.edge("Negative values", "nums = [0, -1, 0, -2]", "[-1, -2, 0, 0]", () -> { int[] a = {0, -1, 0, -2}; moveZeroes(a); return a; }, "Only zero is special; negatives are kept in order.");
        P.done();

        P.begin("Two Sum II - Input Array Is Sorted", "twoSumSorted");
        P.category("Two Pointers · Converging");
        P.summary("The array is sorted, so put one pointer at each end. If the sum is too small, move the left pointer right to increase it; if too big, move the right pointer left. The pointers meet the answer in one pass with O(1) space.");
        P.cx("O(1)", "O(n)", "O(n)", "O(1)", "Best case is when the two ends are the answer.");
        P.ideas("Sorted order tells you which way to move: left increases the sum, right decreases it.",
            "Moving the smaller side can never skip the answer, because that element can't pair with anything bigger than `r` either.",
            "Return 1-based indices, as the problem asks.");
        P.pits("Returning 0-based indices.",
            "Using a hash map works but ignores the sorted property and the O(1) space requirement.",
            "Binary search for each element is O(n log n).");
        P.trace("numbers = [2, 3, 4, 7, 11, 15], target = 9", () -> twoSumSorted(new int[]{2, 3, 4, 7, 11, 15}, 9));
        P.edge("Negative numbers", "numbers = [-3, -1, 0, 2, 5], target = -1", "[1, 4]", () -> twoSumSorted(new int[]{-3, -1, 0, 2, 5}, -1), "-3 + 2 = -1. Negative values follow the same rules.");
        P.edge("Two elements", "numbers = [1, 2], target = 3", "[1, 2]", () -> twoSumSorted(new int[]{1, 2}, 3), "The ends are the answer immediately.");
        P.edge("Duplicates", "numbers = [1, 3, 3, 4], target = 6", "[2, 3]", () -> twoSumSorted(new int[]{1, 3, 3, 4}, 6), "The two 3s are different positions.");
        P.edge("Answer in the middle", "numbers = [1, 2, 3, 4, 4, 9, 56, 90], target = 8", "[4, 5]", () -> twoSumSorted(new int[]{1, 2, 3, 4, 4, 9, 56, 90}, 8), "Both pointers walk inward several times.");
        P.done();

        P.begin("Sort Colors", "sortColors");
        P.category("Two Pointers · Dutch national flag");
        P.summary("Sort an array of 0s, 1s and 2s in one pass. Keep three regions: 0s before `lo`, 2s after `hi`, and 1s between `lo` and `mid`. `mid` scans the unknown region and swaps each value into its region.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each element is examined once, in place.");
        P.ideas("Invariant: `[0, lo)` are 0s, `[lo, mid)` are 1s, `(hi, n)` are 2s, `[mid, hi]` is unknown.",
            "A 0 swaps to `lo`; both `lo` and `mid` advance because the swapped-in value is a known 1.",
            "A 2 swaps to `hi`; only `hi` moves, because the value swapped into `mid` hasn't been checked yet.",
            "A 1 is already in place, so just advance `mid`.");
        P.pits("Advancing `mid` after swapping with `hi` skips an unchecked value.",
            "Looping `while (mid < hi)` misses the last element; it must be `mid <= hi`.",
            "Counting sort is two passes; the follow-up asks for one.");
        P.trace("nums = [2, 0, 2, 1, 1, 0]", () -> { int[] a = {2, 0, 2, 1, 1, 0}; sortColors(a); return a; });
        P.edge("Already sorted", "nums = [0, 1, 2]", "[0, 1, 2]", () -> { int[] a = {0, 1, 2}; sortColors(a); return a; }, "Swaps only with itself.");
        P.edge("Reverse sorted", "nums = [2, 1, 0]", "[0, 1, 2]", () -> { int[] a = {2, 1, 0}; sortColors(a); return a; }, "The 2 goes to the end first, then the 0 to the front.");
        P.edge("Single colour", "nums = [1, 1, 1]", "[1, 1, 1]", () -> { int[] a = {1, 1, 1}; sortColors(a); return a; }, "Only `mid` moves.");
        P.edge("Single element", "nums = [2]", "[2]", () -> { int[] a = {2}; sortColors(a); return a; }, "One swap with itself and `hi` drops below `mid`.");
        P.edge("Only 0s and 2s", "nums = [2, 0, 2, 0]", "[0, 0, 2, 2]", () -> { int[] a = {2, 0, 2, 0}; sortColors(a); return a; }, "The empty middle region is fine.");
        P.done();

        P.begin("3Sum", "threeSum");
        P.category("Two Pointers · Sort + converging");
        P.summary("Find all unique triplets that sum to zero. Sort the array, fix the first number `nums[i]`, then solve Two Sum II on the rest with two pointers. Skip equal neighbours to avoid duplicate triplets.");
        P.cx("O(n²)", "O(n²)", "O(n²)", "O(1)", "Sorting is O(n log n); the pair search is O(n) for each i. Output space not counted.");
        P.ideas("After sorting, each fixed `nums[i]` turns the problem into finding a pair summing to `-nums[i]`.",
            "Two pointers find that pair in O(n) because the array is sorted.",
            "Skip `nums[i] == nums[i-1]` so each first value is used once.",
            "After a hit, step both pointers past equal values so the same pair isn't added again.",
            "If `nums[i] > 0`, nothing after it can sum to zero, so stop.");
        P.pits("Using a HashSet of lists to dedupe is slower and still needs sorted triplets.",
            "Skipping duplicates of `i` with `nums[i] == nums[i+1]` drops valid triplets like [-1, -1, 2].",
            "Forgetting to move both pointers after a match causes an infinite loop.");
        P.trace("nums = [-1, 0, 1, 2, -1, -4]", () -> threeSum(new int[]{-1, 0, 1, 2, -1, -4}));
        P.edge("All zeros", "nums = [0, 0, 0, 0]", "[[0, 0, 0]]", () -> threeSum(new int[]{0, 0, 0, 0}), "Duplicate skipping keeps a single [0, 0, 0].");
        P.edge("No triplet", "nums = [0, 1, 1]", "[]", () -> threeSum(new int[]{0, 1, 1}), "The only candidate sums to 2.");
        P.edge("Fewer than three numbers", "nums = [1, -1]", "[]", () -> threeSum(new int[]{1, -1}), "The outer loop doesn't run.");
        P.edge("Many duplicates", "nums = [-2, 0, 0, 2, 2]", "[[-2, 0, 2]]", () -> threeSum(new int[]{-2, 0, 0, 2, 2}), "Both pointer skips are needed here.");
        P.edge("All positive", "nums = [1, 2, 3]", "[]", () -> threeSum(new int[]{1, 2, 3}), "Breaks immediately because nums[0] > 0.");
        P.done();

        P.begin("Container With Most Water", "maxArea");
        P.category("Two Pointers · Greedy shrink");
        P.summary("Pick two lines that hold the most water: area = min(height) × width. Start with the widest pair and always move the shorter line inward, since keeping it can never produce a bigger area.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each step discards one line.");
        P.ideas("The shorter line limits the area. Any container that keeps it and is narrower is strictly worse.",
            "So moving the shorter pointer is the only move that might find a bigger area.",
            "Track the best area seen along the way.");
        P.pits("Moving the taller line can only lose area.",
            "Checking every pair is O(n²) and times out.",
            "Width is `r - l`, not `r - l + 1`.");
        P.trace("height = [1, 8, 6, 2, 5, 4, 8, 3, 7]", () -> maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}));
        P.edge("Two lines", "height = [1, 1]", "1", () -> maxArea(new int[]{1, 1}), "One pair, width 1.");
        P.edge("Equal heights", "height = [4, 4, 4, 4]", "12", () -> maxArea(new int[]{4, 4, 4, 4}), "The widest pair wins.");
        P.edge("Increasing heights", "height = [1, 2, 3, 4, 5]", "6", () -> maxArea(new int[]{1, 2, 3, 4, 5}), "Best is 2 × 3 or 3 × 2.");
        P.edge("Zero heights", "height = [0, 2, 0]", "0", () -> maxArea(new int[]{0, 2, 0}), "Every pair includes a line of height 0, so nothing is held.");
        P.done();

        P.begin("Trapping Rain Water", "trap");
        P.category("Two Pointers · Running max");
        P.summary("Water above a bar is `min(maxLeft, maxRight) - height`. With two pointers, the side with the lower bar is limited by its own running max, because the other side is known to have something at least as tall. So process that side, add its water, and move inward.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass with four variables, instead of two O(n) max arrays.");
        P.ideas("Water at i is `min(leftMax, rightMax) - h[i]`.",
            "If `h[l] < h[r]`, the right side has a bar ≥ h[l], so the water at l depends only on `leftMax`.",
            "Update the running max first; if the current bar is the new max, it holds 0 water.",
            "Move the pointer on the lower side and repeat until they meet.");
        P.pits("Using the global max instead of the running max on each side.",
            "Computing max arrays is fine (O(n) space) but know the O(1) version.",
            "Moving the taller side gives wrong answers.");
        P.trace("height = [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]", () -> trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}));
        P.edge("No bars", "height = []", "0", () -> trap(new int[]{}), "The loop never runs.");
        P.edge("Increasing heights", "height = [1, 2, 3, 4]", "0", () -> trap(new int[]{1, 2, 3, 4}), "Nothing on the left can hold water.");
        P.edge("V shape", "height = [3, 0, 3]", "3", () -> trap(new int[]{3, 0, 3}), "The middle holds min(3, 3) - 0 = 3.");
        P.edge("Uneven walls", "height = [4, 2, 0, 3, 2, 5]", "9", () -> trap(new int[]{4, 2, 0, 3, 2, 5}), "The shorter wall (4) caps the water.");
        P.edge("Two bars", "height = [5, 5]", "0", () -> trap(new int[]{5, 5}), "No space between them.");
        P.done();
    }

    static Map<Integer, String> palMarks(String s, int l, int r) {
        Map<Integer, String> m = new HashMap<>();
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isLetterOrDigit(s.charAt(i))) m.put(i, "dim");
            else if (i < l || i > r) m.put(i, "done");
        }
        return m;
    }
    static Map<Integer, String> colorMarks(int[] a, int lo, int mid, int hi) {
        Map<Integer, String> m = new HashMap<>();
        for (int i = 0; i < lo; i++) m.put(i, "done");
        for (int i = hi + 1; i < a.length; i++) m.put(i, "done");
        if (mid < a.length && mid <= hi) m.put(mid, "active");
        return m;
    }

    //@code isPalindrome
    static boolean isPalindrome(String s) {
        int l = 0, r = s.length() - 1;
        while (l < r) {
            while (l < r && !Character.isLetterOrDigit(s.charAt(l))) l++;
            while (l < r && !Character.isLetterOrDigit(s.charAt(r))) r--;
            T.n("Compare the two ends", "Skip punctuation, then compare '" + s.charAt(l) + "' with '" + s.charAt(r) + "' (ignoring case).",
                T.str("s", s).p("l", l).p("r", r).mark(palMarks(s, l, r)).m(l, "compare").m(r, "compare"), T.v("l", l, "r", r));
            if (Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) {
                T.n("Mismatch", "'" + s.charAt(l) + "' ≠ '" + s.charAt(r) + "', so it's not a palindrome.",
                    T.str("s", s).p("l", l).p("r", r).mark(palMarks(s, l, r)).m(l, "error").m(r, "error"), T.v("l", l, "r", r));
                return false;
            }
            l++;
            r--;
        }
        T.n("Pointers met", "Every pair matched, so it's a palindrome.", T.str("s", s).mark(palMarks(s, -1, -2)), T.v("l", l, "r", r));
        return true;
    }
    //@end

    //@code moveZeroes
    static void moveZeroes(int[] nums) {
        int w = 0;
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] == 0) {
                T.n("Skip the zero", "`nums[" + r + "]` is 0. Leave it; `w` stays at " + w + ".", T.arr("nums", nums).p("w", w).p("r", r).range(0, w - 1, "done").m(r, "dim"), T.v("w", w, "r", r));
                continue;
            }
            int tmp = nums[w]; nums[w] = nums[r]; nums[r] = tmp;
            T.s("Swap the non-zero forward", nums[w] + " moves to index " + w + ". Everything before `w` is now non-zero, in order.",
                T.arr("nums", nums).p("w", w).p("r", r).range(0, w - 1, "done").m(w, "swap").m(r, w == r ? "swap" : "compare"), T.v("w", w, "r", r));
            w++;
        }
        T.n("Done", "Non-zeros fill `[0, " + w + ")`; the rest are zeros.", T.arr("nums", nums).range(0, w - 1, "found").range(w, nums.length - 1, "dim"), T.v("w", w));
    }
    //@end

    //@code twoSumSorted
    static int[] twoSum(int[] numbers, int target) {
        int l = 0, r = numbers.length - 1;
        while (l < r) {
            int sum = numbers[l] + numbers[r];
            String move = sum == target ? "That's the target." : sum < target ? "Too small, so move `l` right to a bigger value." : "Too big, so move `r` left to a smaller value."; // @hide
            T.s("Check the sum", "`" + numbers[l] + " + " + numbers[r] + " = " + sum + "`. " + move,
                T.arr("numbers", numbers).p("l", l).p("r", r).range(0, l - 1, "dim").range(r + 1, numbers.length - 1, "dim").m(l, "compare").m(r, "compare"), T.v("l", l, "r", r, "sum", sum, "target", target));
            if (sum == target) {
                T.n("Found it", "Return 1-based positions " + (l + 1) + " and " + (r + 1) + ".", T.arr("numbers", numbers).p("l", l).p("r", r).m(l, "found").m(r, "found"), T.v("l", l, "r", r, "sum", sum));
                return new int[]{l + 1, r + 1};
            }
            if (sum < target) l++;
            else r--;
        }
        return new int[]{-1, -1};
    }
    //@end
    static int[] twoSumSorted(int[] a, int t) { return twoSum(a, t); }

    //@code sortColors
    static void sortColors(int[] nums) {
        int lo = 0, mid = 0, hi = nums.length - 1;
        T.s("Three regions", "0s go before `lo`, 2s after `hi`. `mid` scans the unknown middle.", T.arr("nums", nums).p("lo", lo).p("mid", mid).p("hi", hi), T.v("lo", lo, "mid", mid, "hi", hi));
        while (mid <= hi) {
            if (nums[mid] == 0) {
                swap(nums, lo, mid);
                T.s("0: swap to the front", "Swap `nums[" + mid + "]` with `nums[" + lo + "]`, then move both `lo` and `mid` forward.",
                    T.arr("nums", nums).p("lo", lo).p("mid", mid).p("hi", hi).mark(colorMarks(nums, lo, mid, hi)).m(lo, "swap").m(mid, "swap"), T.v("lo", lo, "mid", mid, "hi", hi));
                lo++;
                mid++;
            } else if (nums[mid] == 1) {
                T.n("1: already in place", "1s belong in the middle. Just move `mid` forward.", T.arr("nums", nums).p("lo", lo).p("mid", mid).p("hi", hi).mark(colorMarks(nums, lo, mid, hi)), T.v("lo", lo, "mid", mid, "hi", hi));
                mid++;
            } else {
                swap(nums, mid, hi);
                T.s("2: swap to the back", "Swap `nums[" + mid + "]` with `nums[" + hi + "]` and shrink `hi`. `mid` stays: the value swapped in hasn't been checked.",
                    T.arr("nums", nums).p("lo", lo).p("mid", mid).p("hi", hi).mark(colorMarks(nums, lo, mid, hi)).m(hi, "swap").m(mid, "swap"), T.v("lo", lo, "mid", mid, "hi", hi));
                hi--;
            }
        }
        T.n("Sorted", "`mid` passed `hi`, so no unknown values remain.", T.arr("nums", nums).range(0, nums.length - 1, "found"), T.v("lo", lo, "mid", mid, "hi", hi));
    }

    static void swap(int[] a, int i, int j) {
        int t = a[i]; a[i] = a[j]; a[j] = t;
    }
    //@end

    //@code threeSum
    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        T.s("Sort first", "Sorting lets two pointers find pairs and makes duplicates sit next to each other.", T.arr("nums", nums));
        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < nums.length - 2; i++) {
            if (nums[i] > 0) {
                T.n("Nothing left can sum to 0", "`nums[" + i + "] = " + nums[i] + "` is positive, and everything after it is bigger.", T.arr("nums", nums).p("i", i).m(i, "dim"), T.v("i", i));
                break;
            }
            if (i > 0 && nums[i] == nums[i - 1]) {
                T.n("Skip the duplicate", "`nums[" + i + "]` equals the previous value, so its triplets were already found.", T.arr("nums", nums).p("i", i).m(i, "dim").m(i - 1, "compare"), T.v("i", i));
                continue;
            }
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                T.s("Check the triplet", "`" + nums[i] + " + " + nums[l] + " + " + nums[r] + " = " + sum + "`. " + (sum < 0 ? "Too small: move `l` right." : sum > 0 ? "Too big: move `r` left." : "Zero!"),
                    T.arr("nums", nums).p("i", i).p("l", l).p("r", r).m(i, "active").m(l, "compare").m(r, "compare"), T.v("i", i, "l", l, "r", r, "sum", sum), T.arr("res", res));
                if (sum < 0) l++;
                else if (sum > 0) r--;
                else {
                    res.add(Arrays.asList(nums[i], nums[l], nums[r]));
                    T.s("Record the triplet", "Add [" + nums[i] + ", " + nums[l] + ", " + nums[r] + "], then skip equal values on both sides.",
                        T.arr("nums", nums).p("i", i).p("l", l).p("r", r).m(i, "found").m(l, "found").m(r, "found"), T.v("i", i, "l", l, "r", r), T.arr("res", res).m(res.size() - 1, "swap"));
                    while (l < r && nums[l] == nums[l + 1]) l++;
                    while (l < r && nums[r] == nums[r - 1]) r--;
                    l++;
                    r--;
                }
            }
        }
        return res;
    }
    //@end

    //@code maxArea
    static int maxArea(int[] height) {
        int l = 0, r = height.length - 1, best = 0;
        while (l < r) {
            int area = Math.min(height[l], height[r]) * (r - l);
            best = Math.max(best, area);
            T.s("Measure the container", "`min(" + height[l] + ", " + height[r] + ") × " + (r - l) + " = " + area + "`. Best so far: " + best + ". "
                    + (height[l] < height[r] ? "The left line is shorter, so move `l`." : "The right line is not taller, so move `r`."),
                T.arr("height", height).bars().p("l", l).p("r", r).range(0, l - 1, "dim").range(r + 1, height.length - 1, "dim").m(l, "active").m(r, "active"), T.v("l", l, "r", r, "area", area, "best", best));
            if (height[l] < height[r]) l++;
            else r--;
        }
        return best;
    }
    //@end

    //@code trap
    static int trap(int[] height) {
        int[] water = new int[height.length]; // @hide
        int l = 0, r = height.length - 1, leftMax = 0, rightMax = 0, total = 0;
        while (l < r) {
            if (height[l] < height[r]) {
                leftMax = Math.max(leftMax, height[l]);
                total += leftMax - height[l];
                water[l] = leftMax - height[l]; // @hide
                T.s("Fill at l", "The left bar (" + height[l] + ") is lower, so its water is limited by `leftMax = " + leftMax + "`. Water here: " + (leftMax - height[l]) + ".",
                    T.arr("height", height).bars().p("l", l).p("r", r).m(l, "active"), T.arr("water", water).m(l, "swap"), T.v("l", l, "r", r, "leftMax", leftMax, "rightMax", rightMax, "total", total));
                l++;
            } else {
                rightMax = Math.max(rightMax, height[r]);
                total += rightMax - height[r];
                water[r] = rightMax - height[r]; // @hide
                T.s("Fill at r", "The right bar (" + height[r] + ") is not taller, so its water is limited by `rightMax = " + rightMax + "`. Water here: " + (rightMax - height[r]) + ".",
                    T.arr("height", height).bars().p("l", l).p("r", r).m(r, "active"), T.arr("water", water).m(r, "swap"), T.v("l", l, "r", r, "leftMax", leftMax, "rightMax", rightMax, "total", total));
                r--;
            }
        }
        T.n("Total water", "The pointers met. Total trapped: " + total + ".", T.arr("height", height).bars(), T.arr("water", water), T.v("total", total));
        return total;
    }
    //@end
}
