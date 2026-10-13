import java.util.*;

/** Weak areas */
class Weak {
    static void run() {
        P.begin("Game of Life", "gameOfLife");
        P.category("Matrix · In-place state encoding");
        P.summary("Compute the next generation of Conway's Game of Life in place. Every cell must use the old states of its neighbours, so encode transitions in the cell itself: bit 0 is the current state, bit 1 the next state. After filling every cell's next bit, shift everything right by one.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(1)", "Each cell checks 8 neighbours.");
        P.ideas("Rules: a live cell with 2 or 3 live neighbours lives; a dead cell with exactly 3 becomes alive; everything else dies or stays dead.",
            "`board[r][c] & 1` is still the original state while you're writing bit 1.",
            "A final `>>= 1` turns next states into current states.");
        P.pits("Updating cells directly corrupts their neighbours' counts.",
            "Counting the cell itself as a neighbour.",
            "Copying the board works but uses O(m·n) space.");
        P.trace("board = [[0, 1, 0], [0, 0, 1], [1, 1, 1], [0, 0, 0]]", () -> { int[][] b = {{0, 1, 0}, {0, 0, 1}, {1, 1, 1}, {0, 0, 0}}; gameOfLife(b); return b; });
        P.edge("Block stays still", "board = [[1, 1], [1, 1]]", "[[1, 1], [1, 1]]", () -> { int[][] b = {{1, 1}, {1, 1}}; gameOfLife(b); return b; }, "Each cell has 3 live neighbours.");
        P.edge("Lonely cell dies", "board = [[1]]", "[[0]]", () -> { int[][] b = {{1}}; gameOfLife(b); return b; }, "Zero neighbours: underpopulation.");
        P.edge("Blinker flips", "board = [[0, 1, 0], [0, 1, 0], [0, 1, 0]]", "[[0, 0, 0], [1, 1, 1], [0, 0, 0]]", () -> { int[][] b = {{0, 1, 0}, {0, 1, 0}, {0, 1, 0}}; gameOfLife(b); return b; }, "The vertical line becomes horizontal.");
        P.done();

        P.begin("3Sum Closest", "threeSumClosest");
        P.category("Two Pointers · Sort + converging");
        P.summary("Find three numbers whose sum is closest to the target. Sort, fix the first number, and move two pointers inward: a sum below the target moves the left pointer up, above it moves the right pointer down. Track the closest sum seen.");
        P.cx("O(n²)", "O(n²)", "O(n²)", "O(1)", "Sorting plus an O(n) scan per first number.");
        P.ideas("Same skeleton as 3Sum, but you keep the best distance instead of exact matches.",
            "An exact match can return immediately.",
            "Moving the pointer toward the target is the only move that can improve the distance.");
        P.pits("Initialising `closest` to a huge number and then overflowing on `closest - target`; start with the first triple.",
            "Comparing sums instead of distances.");
        P.trace("nums = [-1, 2, 1, -4], target = 1", () -> threeSumClosest(new int[]{-1, 2, 1, -4}, 1));
        P.edge("Exact match", "nums = [0, 0, 0], target = 0", "0", () -> threeSumClosest(new int[]{0, 0, 0}, 0), "Returned immediately.");
        P.edge("Target far away", "nums = [1, 1, 1, 0], target = 100", "3", () -> threeSumClosest(new int[]{1, 1, 1, 0}, 100), "The largest triple.");
        P.edge("Negative target", "nums = [4, 0, 5, -5, 3, 3, 0, -4, -5], target = -2", "-2", () -> threeSumClosest(new int[]{4, 0, 5, -5, 3, 3, 0, -4, -5}, -2), "An exact triple exists: -5 + 0 + 3.");
        P.done();

        P.begin("Integer to English Words", "numberToWords");
        P.category("Strings · Chunks of three digits");
        P.summary("Spell out a non-negative integer in English. Split it into groups of three digits (ones, thousands, millions, billions). Each group under 1000 is spelled with a small helper that handles hundreds, the teens, and tens; then append the group's scale word.");
        P.cx("O(1)", "O(1)", "O(1)", "O(1)", "At most four groups for a 32-bit integer.");
        P.ideas("English repeats the same pattern for every three digits, plus a scale word.",
            "Numbers below 20 are irregular, so look them up directly.",
            "Skip groups that are 0 (no \"Zero Thousand\").",
            "Zero itself is a special case.");
        P.pits("Printing \"Zero\" inside larger numbers.",
            "Extra or trailing spaces; trim at the end.",
            "Teens like 13 are not \"Ten Three\".");
        P.trace("num = 1234567", () -> numberToWords(1234567));
        P.edge("Zero", "num = 0", "\"Zero\"", () -> numberToWords(0), "Special case.");
        P.edge("Teens and empty groups", "num = 1000013", "\"One Million Thirteen\"", () -> numberToWords(1000013), "The thousands group is 0 and is skipped.");
        P.edge("Largest int", "num = 2147483647", "\"Two Billion One Hundred Forty Seven Million Four Hundred Eighty Three Thousand Six Hundred Forty Seven\"", () -> numberToWords(2147483647), "All four groups are used.");
        P.edge("Round hundred", "num = 100", "\"One Hundred\"", () -> numberToWords(100), "No trailing words.");
        P.done();
    }

    static S life(int[][] b, int r, int c) {
        int m = b.length, n = b[0].length; Object[][] v = new Object[m][n];
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) v[i][j] = (b[i][j] & 1) + "→" + ((b[i][j] >> 1) & 1);
        S s = T.mat("board (now → next)", v);
        for (int i = 0; i < m; i++) for (int j = 0; j < n; j++) if ((b[i][j] & 1) == 1) s.m(i, j, "done");
        if (r >= 0) { for (int i = r - 1; i <= r + 1; i++) for (int j = c - 1; j <= c + 1; j++) if (i >= 0 && j >= 0 && i < m && j < n && !(i == r && j == c)) s.m(i, j, "compare"); s.m(r, c, "active"); }
        return s;
    }

    //@code gameOfLife
    static void gameOfLife(int[][] board) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++) {
            for (int c = 0; c < n; c++) {
                int live = 0;
                for (int dr = -1; dr <= 1; dr++)
                    for (int dc = -1; dc <= 1; dc++) {
                        if (dr == 0 && dc == 0) continue;
                        int nr = r + dr, nc = c + dc;
                        if (nr >= 0 && nc >= 0 && nr < m && nc < n) live += board[nr][nc] & 1;
                    }
                boolean alive = (board[r][c] & 1) == 1;
                if ((alive && (live == 2 || live == 3)) || (!alive && live == 3)) board[r][c] |= 2;
                T.s("Cell (" + r + ", " + c + ")", (alive ? "Alive" : "Dead") + " with " + live + " live neighbour(s) → " + (((board[r][c] >> 1) & 1) == 1 ? "alive" : "dead") + " next. Store that in bit 1.", life(board, r, c), T.v("live", live));
            }
        }
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++) board[r][c] >>= 1;
        T.s("Shift to the next generation", "`>>= 1` makes every next state the current state.", T.mat("board", board));
    }
    //@end

    //@code threeSumClosest
    static int threeSumClosest(int[] nums, int target) {
        Arrays.sort(nums);
        int closest = nums[0] + nums[1] + nums[2];
        for (int i = 0; i < nums.length - 2; i++) {
            int l = i + 1, r = nums.length - 1;
            while (l < r) {
                int sum = nums[i] + nums[l] + nums[r];
                if (Math.abs(sum - target) < Math.abs(closest - target)) closest = sum;
                T.s("Try " + nums[i] + " + " + nums[l] + " + " + nums[r] + " = " + sum, "Distance to " + target + ": " + Math.abs(sum - target) + ". Closest so far: " + closest + ". " + (sum < target ? "Too small: move `l` right." : sum > target ? "Too big: move `r` left." : "Exact!"),
                    T.arr("nums", nums).p("i", i).p("l", l).p("r", r).m(i, "active").m(l, "compare").m(r, "compare"), T.v("sum", sum, "closest", closest, "target", target));
                if (sum == target) return sum;
                if (sum < target) l++;
                else r--;
            }
        }
        return closest;
    }
    //@end

    //@code numberToWords
    static final String[] BELOW_20 = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    static final String[] TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
    static final String[] SCALES = {"", "Thousand", "Million", "Billion"};

    static String numberToWords(int num) {
        if (num == 0) return "Zero";
        String res = "";
        List<Integer> groups = new ArrayList<>(); // @hide
        for (int x = num; x > 0; x /= 1000) groups.add(0, x % 1000); // @hide
        int scale = 0;
        while (num > 0) {
            int chunk = num % 1000;
            if (chunk != 0) res = three(chunk) + SCALES[scale] + " " + res;
            T.s("Group " + scale + ": " + chunk, chunk == 0 ? "This group is 0, so it adds nothing." : "\"" + three(chunk).trim() + "\"" + (scale > 0 ? " + \"" + SCALES[scale] + "\"" : "") + " goes in front.", T.arr("groups (high → low)", groups).m(groups.size() - 1 - scale, chunk == 0 ? "dim" : "active"), T.v("res", res.trim()));
            num /= 1000;
            scale++;
        }
        return res.trim();
    }

    static String three(int n) {
        String s = "";
        if (n >= 100) {
            s += BELOW_20[n / 100] + " Hundred ";
            n %= 100;
        }
        if (n >= 20) {
            s += TENS[n / 10] + " ";
            n %= 10;
        }
        if (n > 0) s += BELOW_20[n] + " ";
        return s;
    }
    //@end
}
