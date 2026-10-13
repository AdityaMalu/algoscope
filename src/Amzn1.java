import java.util.*;

/** Amazon-tagged: Arrays & Strings */
class Amzn1 {
    static void run() {
        P.begin("Subarray Sum Equals K", "subarraySum");
        P.category("Prefix sums · Hash map");
        P.summary("Count contiguous subarrays summing to k. A subarray (j, i] sums to k exactly when `prefix[i] − prefix[j] = k`. Walk once keeping a map of how often each prefix sum has appeared; at each position add the count of `prefix − k`.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "One pass with a hash map.");
        P.ideas("Subarray sum = difference of two prefix sums.",
            "`seen.get(prefix - k)` = number of earlier start points that work.",
            "Seed the map with `{0: 1}` so subarrays starting at index 0 count.",
            "Works with negative numbers, unlike a sliding window.");
        P.pits("Sliding window fails with negative numbers.",
            "Forgetting the `{0: 1}` seed misses subarrays from the start.",
            "Updating the map before checking counts the empty subarray when k = 0.");
        P.trace("nums = [1, 2, 3, -2, 2], k = 3", () -> subarraySum(new int[]{1, 2, 3, -2, 2}, 3));
        P.edge("Repeated values", "nums = [1, 1, 1], k = 2", "2", () -> subarraySum(new int[]{1, 1, 1}, 2), "[1,1] twice.");
        P.edge("k = 0 with zeros", "nums = [0, 0, 0], k = 0", "6", () -> subarraySum(new int[]{0, 0, 0}, 0), "Every non-empty subarray: 3 + 2 + 1.");
        P.edge("Negative numbers", "nums = [1, -1, 0], k = 0", "3", () -> subarraySum(new int[]{1, -1, 0}, 0), "[1,-1], [0], [1,-1,0].");
        P.edge("No subarray", "nums = [1, 2], k = 5", "0", () -> subarraySum(new int[]{1, 2}, 5), "Total is only 3.");
        P.done();

        P.begin("Find All Anagrams in a String", "findAnagrams");
        P.category("Sliding Window · Fixed size");
        P.summary("Return every start index where a window of `s` is an anagram of `p`. Slide a window of length |p| and keep letter counts; compare the window's counts with p's counts after each slide.");
        P.cx("O(n)", "O(26·n)", "O(26·n)", "O(1)", "Comparing two 26-count arrays per slide; a match counter makes it O(n).");
        P.ideas("Anagram = same letter counts.",
            "Add the entering letter, remove the leaving letter.",
            "Record the window's start whenever the counts match.");
        P.pits("Rebuilding counts for each window is O(n·m).",
            "Off-by-one on when to start removing the left letter.");
        P.trace("s = \"cbaebabacd\", p = \"abc\"", () -> findAnagrams("cbaebabacd", "abc"));
        P.edge("Overlapping matches", "s = \"abab\", p = \"ab\"", "[0, 1, 2]", () -> findAnagrams("abab", "ab"), "Every window of length 2 works.");
        P.edge("p longer than s", "s = \"a\", p = \"ab\"", "[]", () -> findAnagrams("a", "ab"), "No full window fits.");
        P.edge("Repeated letters", "s = \"aaab\", p = \"aa\"", "[0, 1]", () -> findAnagrams("aaab", "aa"), "Counts must match exactly.");
        P.done();

        P.begin("String Compression", "compress");
        P.category("Two Pointers · Read/write in place");
        P.summary("Compress runs of repeated characters in place: \"aabccc\" becomes \"a2bc3\". A read pointer finds each run; a write pointer writes the character and, if the run is longer than 1, each digit of its length. Return the new length.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "The write pointer never passes the read pointer.");
        P.ideas("Find the end of the current run with the read pointer.",
            "Write the character, then the count's digits if the count > 1.",
            "Counts ≥ 10 take several characters: write `String.valueOf(count)` digit by digit.");
        P.pits("Writing \"a1\" for single characters.",
            "Writing the count as one char, which breaks for counts ≥ 10.");
        P.trace("chars = [a, a, b, b, c, c, c]", () -> { char[] c = "aabbccc".toCharArray(); int len = compress(c); return new R(len + ", " + F.s(Arrays.copyOf(c, len))); });
        P.edge("Single character", "chars = [a]", "1, [\"a\"]", () -> { char[] c = "a".toCharArray(); int len = compress(c); return new R(len + ", " + F.s(Arrays.copyOf(c, len))); }, "No count is written for a run of 1.");
        P.edge("Run of 12", "chars = [a, b × 12]", "4, [\"a\", \"b\", \"1\", \"2\"]", () -> { char[] c = "abbbbbbbbbbbb".toCharArray(); int len = compress(c); return new R(len + ", " + F.s(Arrays.copyOf(c, len))); }, "12 is written as two digits.");
        P.edge("No repeats", "chars = [a, b, c]", "3, [\"a\", \"b\", \"c\"]", () -> { char[] c = "abc".toCharArray(); int len = compress(c); return new R(len + ", " + F.s(Arrays.copyOf(c, len))); }, "Unchanged.");
        P.done();

        P.begin("Next Permutation", "nextPermutation");
        P.category("Arrays · Pivot and reverse");
        P.summary("Rearrange numbers into the next lexicographically larger permutation, in place. Find the rightmost index i with `nums[i] < nums[i+1]` (the pivot). Swap it with the smallest larger value to its right, then reverse the suffix after i to make it as small as possible.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "At most two scans and one reversal.");
        P.ideas("The suffix after the pivot is non-increasing: it's already the largest arrangement of those numbers.",
            "To get the next permutation, bump the pivot up by the smallest possible amount.",
            "The suffix stays non-increasing after the swap, so reversing makes it ascending (smallest).",
            "No pivot means the array is the last permutation: reverse it all.");
        P.pits("Swapping with the first larger element instead of the rightmost (smallest) larger one.",
            "Sorting the suffix instead of reversing it: correct but O(n log n).",
            "Using `>` instead of `>=` when scanning for the pivot, which breaks with duplicates.");
        P.trace("nums = [1, 3, 5, 4, 2]", () -> { int[] a = {1, 3, 5, 4, 2}; nextPermutation(a); return a; });
        P.edge("Last permutation wraps", "nums = [3, 2, 1]", "[1, 2, 3]", () -> { int[] a = {3, 2, 1}; nextPermutation(a); return a; }, "No pivot: reverse everything.");
        P.edge("Duplicates", "nums = [1, 5, 1]", "[5, 1, 1]", () -> { int[] a = {1, 5, 1}; nextPermutation(a); return a; }, "The pivot is 1 at index 0; the next larger is 5.");
        P.edge("Single element", "nums = [1]", "[1]", () -> { int[] a = {1}; nextPermutation(a); return a; }, "Nothing to change.");
        P.edge("Simple swap", "nums = [1, 2, 3]", "[1, 3, 2]", () -> { int[] a = {1, 2, 3}; nextPermutation(a); return a; }, "Pivot at index 1.");
        P.done();

        P.begin("Spiral Matrix", "spiralOrder");
        P.category("Matrix · Shrinking boundaries");
        P.summary("Read a matrix in clockwise spiral order. Keep four boundaries (top, bottom, left, right). Read the top row left to right, the right column top to bottom, the bottom row right to left, the left column bottom to top, shrinking each boundary after use.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(1)", "Each cell is read once; output not counted.");
        P.ideas("Each pass peels one layer side by side.",
            "Shrink the boundary right after reading its side.",
            "Check `top <= bottom` and `left <= right` before the bottom row and left column, or a single row/column is read twice.");
        P.pits("Missing the mid-loop checks duplicates the middle row or column.",
            "Using a visited array works but costs O(m·n) space.");
        P.trace("matrix = [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]]", () -> spiralOrder(new int[][]{{1, 2, 3, 4}, {5, 6, 7, 8}, {9, 10, 11, 12}}));
        P.edge("Single row", "matrix = [[1, 2, 3]]", "[1, 2, 3]", () -> spiralOrder(new int[][]{{1, 2, 3}}), "The bottom-row pass is skipped.");
        P.edge("Single column", "matrix = [[1], [2], [3]]", "[1, 2, 3]", () -> spiralOrder(new int[][]{{1}, {2}, {3}}), "The left-column pass is skipped.");
        P.edge("Square", "matrix = [[1, 2], [3, 4]]", "[1, 2, 4, 3]", () -> spiralOrder(new int[][]{{1, 2}, {3, 4}}), "One full layer.");
        P.done();

        P.begin("Rotate Image", "rotate");
        P.category("Matrix · Transpose + reverse");
        P.summary("Rotate an n×n matrix 90° clockwise in place. Transpose it (swap across the main diagonal), then reverse every row. The two reflections together are a rotation.");
        P.cx("O(n²)", "O(n²)", "O(n²)", "O(1)", "Each cell is swapped a constant number of times.");
        P.ideas("Clockwise rotation = transpose, then mirror left-right.",
            "Transpose only swaps cells above the diagonal (j > i), or it undoes itself.",
            "Counter-clockwise would reverse columns instead of rows.");
        P.pits("Transposing the whole matrix (j from 0) swaps everything twice.",
            "Using a second matrix breaks the in-place requirement.");
        P.trace("matrix = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]", () -> { int[][] m = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}; rotate(m); return m; });
        P.edge("1×1", "matrix = [[1]]", "[[1]]", () -> { int[][] m = {{1}}; rotate(m); return m; }, "Unchanged.");
        P.edge("2×2", "matrix = [[1, 2], [3, 4]]", "[[3, 1], [4, 2]]", () -> { int[][] m = {{1, 2}, {3, 4}}; rotate(m); return m; }, "One swap, then two row reversals.");
        P.done();

        P.begin("Set Matrix Zeroes", "setZeroes");
        P.category("Matrix · In-place markers");
        P.summary("If a cell is 0, set its whole row and column to 0, using O(1) extra space. Use the first row and first column as marker storage: mark `matrix[i][0]` and `matrix[0][j]` for each zero, then fill. Two booleans remember whether the first row and column themselves need zeroing.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(1)", "Two passes over the matrix.");
        P.ideas("Zeroing immediately would spread zeros to cells that weren't originally zero.",
            "The first row and column become flags for their columns and rows.",
            "Record the first row/column's own status before overwriting them.",
            "Zero the first row and column last.");
        P.pits("Zeroing cells during the scan cascades into wrong rows and columns.",
            "Zeroing the first row/column too early destroys the markers.");
        P.trace("matrix = [[1, 1, 1], [1, 0, 1], [1, 1, 1]]", () -> { int[][] m = {{1, 1, 1}, {1, 0, 1}, {1, 1, 1}}; setZeroes(m); return m; });
        P.edge("Zero in the first row", "matrix = [[0, 1, 2, 0], [3, 4, 5, 2], [1, 3, 1, 5]]", "[[0, 0, 0, 0], [0, 4, 5, 0], [0, 3, 1, 0]]", () -> { int[][] m = {{0, 1, 2, 0}, {3, 4, 5, 2}, {1, 3, 1, 5}}; setZeroes(m); return m; }, "The first row itself must be zeroed at the end.");
        P.edge("No zeros", "matrix = [[1, 2], [3, 4]]", "[[1, 2], [3, 4]]", () -> { int[][] m = {{1, 2}, {3, 4}}; setZeroes(m); return m; }, "Nothing changes.");
        P.edge("Single cell zero", "matrix = [[0]]", "[[0]]", () -> { int[][] m = {{0}}; setZeroes(m); return m; }, "Already zero.");
        P.done();
    }

    //@code subarraySum
    static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> seen = new HashMap<>();
        seen.put(0, 1);
        int prefix = 0, count = 0;
        T.s("Seed the map", "Prefix sum 0 has appeared once (before the array starts).", T.arr("nums", nums), T.map("seen prefix sums", seen), T.v("prefix", prefix, "count", count));
        for (int i = 0; i < nums.length; i++) {
            prefix += nums[i];
            int add = seen.getOrDefault(prefix - k, 0);
            count += add;
            seen.merge(prefix, 1, Integer::sum);
            T.s("Prefix through index " + i + " is " + prefix, "Look for earlier prefix " + prefix + " − " + k + " = " + (prefix - k) + ": seen " + add + " time(s). Count: " + count + ".",
                T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.map("seen prefix sums", seen).m(prefix - k, add > 0 ? "found" : "compare").m(prefix, "swap"), T.v("prefix", prefix, "count", count));
        }
        return count;
    }
    //@end

    //@code findAnagrams
    static List<Integer> findAnagrams(String s, String p) {
        List<Integer> res = new ArrayList<>();
        if (p.length() > s.length()) return res;
        int[] need = new int[26], win = new int[26];
        for (char c : p.toCharArray()) need[c - 'a']++;
        for (int i = 0; i < s.length(); i++) {
            win[s.charAt(i) - 'a']++;
            if (i >= p.length()) win[s.charAt(i - p.length()) - 'a']--;
            int start = i - p.length() + 1;
            if (start >= 0 && Arrays.equals(win, need)) res.add(start);
            T.s("Window ending at " + i, start < 0 ? "The window is still filling up." : Arrays.equals(win, need) ? "\"" + s.substring(start, i + 1) + "\" has the same letters as \"" + p + "\": record " + start + "." : "\"" + s.substring(start, i + 1) + "\" doesn't match.",
                T.str("s", s).p("i", i).range(Math.max(0, start), i, start >= 0 && Arrays.equals(win, need) ? "found" : "visit"), Sliding.cmp(need, win, "window / need"), T.arr("res", res));
        }
        return res;
    }
    //@end

    //@code compress
    static int compress(char[] chars) {
        int write = 0, read = 0;
        while (read < chars.length) {
            char c = chars[read];
            int start = read;
            while (read < chars.length && chars[read] == c) read++;
            int count = read - start;
            chars[write++] = c;
            if (count > 1) {
                for (char d : String.valueOf(count).toCharArray()) chars[write++] = d;
            }
            T.s("Run of '" + c + "' × " + count, count > 1 ? "Write '" + c + "' then \"" + count + "\"." : "A single character: just write '" + c + "'.", T.arr("chars", chars).p("write", Math.min(write, chars.length - 1)).p("read", Math.min(read, chars.length - 1)).range(0, write - 1, "done").range(start, read - 1, "visit"), T.v("write", write, "read", read, "count", count));
        }
        return write;
    }
    //@end

    //@code nextPermutation
    static void nextPermutation(int[] nums) {
        int i = nums.length - 2;
        while (i >= 0 && nums[i] >= nums[i + 1]) i--;
        T.s(i >= 0 ? "Pivot at index " + i : "No pivot", i >= 0 ? "`nums[" + i + "] = " + nums[i] + "` is smaller than the element after it. Everything to its right is non-increasing." : "The whole array is non-increasing: it's the last permutation.",
            T.arr("nums", nums).p("i", Math.max(i, 0)).range(i + 1, nums.length - 1, "visit").m(i, "active"));
        if (i >= 0) {
            int j = nums.length - 1;
            while (nums[j] <= nums[i]) j--;
            swap(nums, i, j);
            T.s("Swap with the next larger value", "The rightmost value bigger than the pivot is at " + j + ". Swap them.", T.arr("nums", nums).p("i", i).p("j", j).m(i, "swap").m(j, "swap"));
        }
        reverse(nums, i + 1, nums.length - 1);
        T.s("Reverse the suffix", "Reversing makes the part after the pivot as small as possible.", T.arr("nums", nums).range(i + 1, nums.length - 1, "found"));
    }

    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    static void reverse(int[] a, int l, int r) { while (l < r) swap(a, l++, r--); }
    //@end

    static S spiral(int[][] m, List<Integer> res, int top, int bottom, int left, int right, int r, int c) {
        S s = T.mat("matrix", m);
        Set<Integer> taken = new HashSet<>(res);
        for (int i = 0; i < m.length; i++) for (int j = 0; j < m[0].length; j++) if (taken.contains(m[i][j])) s.m(i, j, "done");
        if (r >= 0) s.m(r, c, "active");
        return s;
    }

    //@code spiralOrder
    static List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> res = new ArrayList<>();
        int top = 0, bottom = matrix.length - 1, left = 0, right = matrix[0].length - 1;
        while (top <= bottom && left <= right) {
            for (int c = left; c <= right; c++) res.add(matrix[top][c]);
            T.s("Top row → ", "Read row " + top + " from column " + left + " to " + right + ".", spiral(matrix, res, top, bottom, left, right, -1, -1), T.v("top", top, "bottom", bottom, "left", left, "right", right), T.arr("res", res));
            top++;
            for (int r = top; r <= bottom; r++) res.add(matrix[r][right]);
            T.s("Right column ↓", "Read column " + right + " from row " + top + " to " + bottom + ".", spiral(matrix, res, top, bottom, left, right, -1, -1), T.v("top", top, "bottom", bottom, "left", left, "right", right), T.arr("res", res));
            right--;
            if (top <= bottom) {
                for (int c = right; c >= left; c--) res.add(matrix[bottom][c]);
                T.s("Bottom row ←", "Read row " + bottom + " from column " + right + " back to " + left + ".", spiral(matrix, res, top, bottom, left, right, -1, -1), T.v("top", top, "bottom", bottom, "left", left, "right", right), T.arr("res", res));
                bottom--;
            }
            if (left <= right) {
                for (int r = bottom; r >= top; r--) res.add(matrix[r][left]);
                T.s("Left column ↑", "Read column " + left + " from row " + bottom + " up to " + top + ".", spiral(matrix, res, top, bottom, left, right, -1, -1), T.v("top", top, "bottom", bottom, "left", left, "right", right), T.arr("res", res));
                left++;
            }
        }
        return res;
    }
    //@end

    //@code rotate
    static void rotate(int[][] matrix) {
        int n = matrix.length;
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                int t = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = t;
                T.s("Transpose: swap (" + i + "," + j + ") and (" + j + "," + i + ")", "Reflect across the main diagonal.", T.mat("matrix", matrix).m(i, j, "swap").m(j, i, "swap"));
            }
        for (int[] row : matrix) {
            for (int l = 0, r = n - 1; l < r; l++, r--) {
                int t = row[l];
                row[l] = row[r];
                row[r] = t;
            }
        }
        T.s("Reverse every row", "Mirror left-right. Together with the transpose, that's a 90° clockwise turn.", T.mat("matrix", matrix).mark(allCells(n)));
    }
    //@end
    static Map<String, String> allCells(int n) { Map<String, String> m = new HashMap<>(); for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) m.put(i + "," + j, "found"); return m; }

    //@code setZeroes
    static void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRow = false, firstCol = false;
        for (int j = 0; j < n; j++) if (matrix[0][j] == 0) firstRow = true;
        for (int i = 0; i < m; i++) if (matrix[i][0] == 0) firstCol = true;
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                    T.s("Zero at (" + i + ", " + j + ")", "Mark row " + i + " in the first column and column " + j + " in the first row.", T.mat("matrix", matrix).m(i, j, "error").m(i, 0, "swap").m(0, j, "swap"), T.v("firstRow", firstRow, "firstCol", firstCol));
                }
        for (int i = 1; i < m; i++)
            for (int j = 1; j < n; j++)
                if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;
        T.s("Fill from the markers", "Every inner cell whose row or column is marked becomes 0.", T.mat("matrix", matrix), T.v("firstRow", firstRow, "firstCol", firstCol));
        if (firstRow) for (int j = 0; j < n; j++) matrix[0][j] = 0;
        if (firstCol) for (int i = 0; i < m; i++) matrix[i][0] = 0;
        T.s("First row and column last", "Zero them only if they originally contained a 0.", T.mat("matrix", matrix), T.v("firstRow", firstRow, "firstCol", firstCol));
    }
    //@end
}
