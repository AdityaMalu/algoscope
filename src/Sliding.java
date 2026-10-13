import java.util.*;

/** Sliding Window */
class Sliding {
    static void run() {
        P.begin("Best Time to Buy and Sell Stock", "maxProfit");
        P.category("Sliding Window · Running minimum");
        P.summary("Buy once, sell once later, maximise profit. Scan left to right keeping the cheapest price so far; selling today earns `price - minSoFar`. The best of those is the answer.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "One pass, two variables.");
        P.ideas("The best sell day for today is fixed; the only question is the cheapest earlier buy day.",
            "Keep `minPrice` as you go, so that question is O(1).",
            "If prices only fall, the best profit stays 0 (don't trade).");
        P.pits("Selling before buying: the min must come from earlier days only.",
            "Initialising profit to a negative number; the answer is never below 0.",
            "Tracking the global max and min separately ignores their order.");
        P.trace("prices = [7, 1, 5, 3, 6, 4]", () -> maxProfit(new int[]{7, 1, 5, 3, 6, 4}));
        P.edge("Prices only fall", "prices = [7, 6, 4, 3, 1]", "0", () -> maxProfit(new int[]{7, 6, 4, 3, 1}), "No profitable trade, so don't trade.");
        P.edge("Single day", "prices = [5]", "0", () -> maxProfit(new int[]{5}), "You can't buy and sell on different days.");
        P.edge("Max before min", "prices = [9, 1, 3]", "2", () -> maxProfit(new int[]{9, 1, 3}), "9 − 1 isn't allowed (wrong order); 3 − 1 = 2 is.");
        P.edge("Flat prices", "prices = [3, 3, 3]", "0", () -> maxProfit(new int[]{3, 3, 3}), "Every trade earns 0.");
        P.edge("Best at the end", "prices = [2, 4, 1, 7]", "6", () -> maxProfit(new int[]{2, 4, 1, 7}), "The minimum moves to 1, then 7 − 1 = 6 beats 4 − 2.");
        P.done();

        P.begin("Longest Substring Without Repeating Characters", "lengthOfLongestSubstring");
        P.category("Sliding Window · Variable size");
        P.summary("Grow a window to the right one character at a time. When the new character is already inside the window, jump the left edge just past its previous position. The window always has unique characters, and its widest size is the answer.");
        P.cx("O(n)", "O(n)", "O(n)", "O(min(n, charset))", "Each character is visited once by `r`; `l` only jumps forward.");
        P.ideas("`last[c]` remembers the latest index of each character.",
            "If `c` was seen inside the window (`last[c] >= l`), move `l` to `last[c] + 1`.",
            "`l` never moves backward, so use `max(l, last[c] + 1)`.",
            "After fixing `l`, the window `[l, r]` has no repeats; record its length.");
        P.pits("Moving `l` back to an old index when the repeat is outside the window (\"abba\").",
            "Using a set and removing one character at a time works but is two-pointer O(2n); know both.",
            "Off-by-one: the length is `r - l + 1`.");
        P.trace("s = \"abcabcbb\"", () -> lengthOfLongestSubstring("abcabcbb"));
        P.edge("Empty string", "s = \"\"", "0", () -> lengthOfLongestSubstring(""), "No characters, no window.");
        P.edge("All the same", "s = \"bbbbb\"", "1", () -> lengthOfLongestSubstring("bbbbb"), "`l` jumps forward every step.");
        P.edge("Repeat outside the window", "s = \"abba\"", "2", () -> lengthOfLongestSubstring("abba"), "At the last 'a', its old index 0 is left of `l = 2`, so `l` must not move back.");
        P.edge("Spaces and symbols", "s = \"a b!\"", "4", () -> lengthOfLongestSubstring("a b!"), "Every character counts, including the space; all four are distinct.");
        P.edge("Jump to just after the repeat", "s = \"dvdf\"", "3", () -> lengthOfLongestSubstring("dvdf"), "At the second 'd', `l` moves to 1 (not 2), giving \"vdf\".");
        P.done();

        P.begin("Max Consecutive Ones III", "longestOnes");
        P.category("Sliding Window · At most k bad");
        P.summary("Find the longest run of 1s if you may flip at most k zeros. Grow the window to the right counting zeros inside it; whenever there are more than k, shrink from the left until there are k again.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each index enters and leaves the window at most once.");
        P.ideas("Reframe: the longest window containing at most k zeros.",
            "Expand `r`; if `nums[r]` is 0, increase `zeros`.",
            "While `zeros > k`, move `l` right, decreasing `zeros` when a 0 leaves.",
            "Every valid window is a candidate answer.");
        P.pits("Actually flipping values in the array.",
            "Shrinking with `if` instead of `while` (fine only in the non-shrinking variant).",
            "Forgetting k = 0, where the answer is the longest run of 1s.");
        P.trace("nums = [1, 1, 0, 0, 1, 1, 1, 0, 1], k = 2", () -> longestOnes(new int[]{1, 1, 0, 0, 1, 1, 1, 0, 1}, 2));
        P.edge("k = 0", "nums = [1, 0, 1, 1, 0], k = 0", "2", () -> longestOnes(new int[]{1, 0, 1, 1, 0}, 0), "No flips: the longest plain run of 1s.");
        P.edge("All zeros", "nums = [0, 0, 0], k = 2", "2", () -> longestOnes(new int[]{0, 0, 0}, 2), "Only two zeros can be flipped.");
        P.edge("k larger than zeros", "nums = [0, 1, 0], k = 5", "3", () -> longestOnes(new int[]{0, 1, 0}, 5), "Flip everything; the whole array.");
        P.edge("All ones", "nums = [1, 1, 1], k = 1", "3", () -> longestOnes(new int[]{1, 1, 1}, 1), "No zeros to flip.");
        P.done();

        P.begin("Fruit Into Baskets", "totalFruit");
        P.category("Sliding Window · At most 2 distinct");
        P.summary("You have two baskets, each holding one fruit type. Find the longest contiguous stretch with at most two distinct types. Count types inside the window with a map; when a third type enters, shrink from the left until one type disappears.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "The map never holds more than 3 keys.");
        P.ideas("This is \"longest subarray with at most 2 distinct values\".",
            "Add `fruits[r]` to a count map.",
            "While the map has 3 keys, decrement `fruits[l]` and remove it at 0.",
            "Track the widest valid window.");
        P.pits("Forgetting to remove keys whose count drops to 0, so `size()` stays 3.",
            "Restarting from scratch on a third type is O(n²).");
        P.trace("fruits = [1, 2, 3, 2, 2]", () -> totalFruit(new int[]{1, 2, 3, 2, 2}));
        P.edge("One type", "fruits = [4, 4, 4]", "3", () -> totalFruit(new int[]{4, 4, 4}), "One basket is enough.");
        P.edge("Alternating three types", "fruits = [1, 2, 1, 3, 1, 3]", "4", () -> totalFruit(new int[]{1, 2, 1, 3, 1, 3}), "The best stretch is [1, 3, 1, 3].");
        P.edge("Single tree", "fruits = [7]", "1", () -> totalFruit(new int[]{7}), "Pick the one fruit.");
        P.edge("Two types", "fruits = [0, 1, 0, 1]", "4", () -> totalFruit(new int[]{0, 1, 0, 1}), "The window never needs to shrink.");
        P.done();

        P.begin("Longest Repeating Character Replacement", "characterReplacement");
        P.category("Sliding Window · Most frequent char");
        P.summary("You may replace up to k characters to make a run of one letter. A window is fixable when `length - maxCount <= k`, where maxCount is the most common letter inside. Grow right; if the window becomes unfixable, slide it by one.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "26 counters; `maxCount` never needs to decrease.");
        P.ideas("Keep the most frequent letter and replace the rest: cost = `window - maxCount`.",
            "When cost exceeds k, move `l` forward one step (the window slides instead of shrinking).",
            "`maxCount` can stay stale-high: the answer only grows when a truly higher count appears.");
        P.pits("Recomputing max over 26 counts every step is fine (O(26n)) but not required.",
            "Shrinking with a `while` loop is also correct; just know why the `if` version works.",
            "Counting only uppercase: the problem guarantees 'A'–'Z'.");
        P.trace("s = \"AABABBA\", k = 1", () -> characterReplacement("AABABBA", 1));
        P.edge("k covers everything", "s = \"ABAB\", k = 2", "4", () -> characterReplacement("ABAB", 2), "Replace both Bs (or both As).");
        P.edge("k = 0", "s = \"AABBB\", k = 0", "3", () -> characterReplacement("AABBB", 0), "Just the longest existing run.");
        P.edge("Single character", "s = \"Z\", k = 0", "1", () -> characterReplacement("Z", 0), "A window of 1 is always valid.");
        P.edge("All different", "s = \"ABCD\", k = 1", "2", () -> characterReplacement("ABCD", 1), "Any two letters, replacing one.");
        P.done();

        P.begin("Permutation in String", "checkInclusion");
        P.category("Sliding Window · Fixed size");
        P.summary("Does `s2` contain a permutation of `s1`? Slide a window of length |s1| over `s2` and compare letter counts. Keep a `matches` counter of how many of the 26 letters have equal counts, so each slide costs O(1).");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "n = |s2|. Two arrays of 26 counts.");
        P.ideas("A permutation has the same letter counts, so compare count arrays.",
            "The window has fixed size |s1|: add the new right letter, remove the old left letter.",
            "`matches == 26` means the window is a permutation.");
        P.pits("Comparing full 26-entry arrays each slide is O(26n), still fine but slower.",
            "Not handling |s1| > |s2| (answer false).",
            "Updating `matches` incorrectly when a count moves away from equal.");
        P.trace("s1 = \"ab\", s2 = \"eidbaooo\"", () -> checkInclusion("ab", "eidbaooo"));
        P.edge("No permutation", "s1 = \"ab\", s2 = \"eidboaoo\"", "false", () -> checkInclusion("ab", "eidboaoo"), "'b' and 'a' are never adjacent.");
        P.edge("s1 longer than s2", "s1 = \"abc\", s2 = \"ab\"", "false", () -> checkInclusion("abc", "ab"), "Rejected immediately.");
        P.edge("Equal strings", "s1 = \"abc\", s2 = \"cba\"", "true", () -> checkInclusion("abc", "cba"), "The first window already matches.");
        P.edge("Repeated letters", "s1 = \"aab\", s2 = \"baaab\"", "true", () -> checkInclusion("aab", "baaab"), "\"baa\" at the start has two a's and one b.");
        P.done();

        P.begin("Minimum Window Substring", "minWindow");
        P.category("Sliding Window · Shrink to minimum");
        P.summary("Find the smallest substring of `s` containing every character of `t` (with multiplicity). Expand right until the window covers `t`, then shrink from the left as long as it still covers, recording the smallest window seen.");
        P.cx("O(m + n)", "O(m + n)", "O(m + n)", "O(1)", "Each pointer moves at most |s| times; the counts use a fixed 128-size array.");
        P.ideas("`need[c]` counts how many of each character are still missing.",
            "`missing` is the total still needed; the window covers `t` when it hits 0.",
            "While covered, record the window and move `l` right, putting characters back into `need`.",
            "When a needed character leaves, `missing` goes up and you resume expanding.");
        P.pits("Treating `t` as a set; duplicates in `t` must be counted.",
            "Returning the length instead of the substring.",
            "Shrinking with `if` instead of `while`, which leaves windows too big.");
        P.trace("s = \"ADOBECODEBANC\", t = \"ABC\"", () -> minWindow("ADOBECODEBANC", "ABC"));
        P.edge("t longer than s", "s = \"a\", t = \"aa\"", "\"\"", () -> minWindow("a", "aa"), "Two a's are needed but only one exists.");
        P.edge("Exact match", "s = \"a\", t = \"a\"", "\"a\"", () -> minWindow("a", "a"), "The whole string.");
        P.edge("Duplicates in t", "s = \"aaflslflsldkalskaaa\", t = \"aaa\"", "\"aaa\"", () -> minWindow("aaflslflsldkalskaaa", "aaa"), "Three a's in a row at the end.");
        P.edge("Case sensitive", "s = \"aA\", t = \"A\"", "\"A\"", () -> minWindow("aA", "A"), "'a' does not count as 'A'.");
        P.done();
    }

    //@code maxProfit
    static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE, best = 0;
        for (int i = 0; i < prices.length; i++) {
            minPrice = Math.min(minPrice, prices[i]);
            best = Math.max(best, prices[i] - minPrice);
            T.s("Sell today?", "Cheapest buy so far is " + minPrice + ". Selling at " + prices[i] + " earns " + (prices[i] - minPrice) + ". Best: " + best + ".",
                T.arr("prices", prices).p("i", i).range(0, i - 1, "visit").m(indexOf(prices, minPrice, i), "done").m(i, "active"), T.v("i", i, "minPrice", minPrice, "best", best));
        }
        return best;
    }
    //@end
    static int indexOf(int[] a, int v, int upto) { for (int i = upto; i >= 0; i--) if (a[i] == v) return i; return -1; }

    //@code lengthOfLongestSubstring
    static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> last = new HashMap<>();
        int l = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            char c = s.charAt(r);
            if (last.containsKey(c) && last.get(c) >= l) {
                l = last.get(c) + 1;
                T.s("Repeat inside the window", "'" + c + "' was last at index " + (l - 1) + ", inside the window. Move `l` to " + l + ".",
                    T.str("s", s).p("l", l).p("r", r).range(l, r - 1, "visit").m(l - 1, "dim").m(r, "compare"), T.map("last", last).m(c, "compare"), T.v("l", l, "r", r, "best", best));
            }
            last.put(c, r);
            best = Math.max(best, r - l + 1);
            T.s("Window is unique", "`s[" + l + ".." + r + "]` = \"" + s.substring(l, r + 1) + "\", length " + (r - l + 1) + ". Best: " + best + ".",
                T.str("s", s).p("l", l).p("r", r).range(l, r, "visit").m(r, "active"), T.map("last", last).m(c, "swap"), T.v("l", l, "r", r, "best", best));
        }
        return best;
    }
    //@end

    //@code longestOnes
    static int longestOnes(int[] nums, int k) {
        int l = 0, zeros = 0, best = 0;
        for (int r = 0; r < nums.length; r++) {
            if (nums[r] == 0) zeros++;
            while (zeros > k) {
                if (nums[l] == 0) zeros--;
                l++;
                T.s("Too many zeros: shrink", "The window had more than " + k + " zeros. Drop index " + (l - 1) + "; zeros inside: " + zeros + ".",
                    T.arr("nums", nums).p("l", l).p("r", r).range(l, r, "visit").m(l - 1, "dim"), T.v("l", l, "r", r, "zeros", zeros, "k", k, "best", best));
            }
            best = Math.max(best, r - l + 1);
            T.s("Valid window", "`[" + l + ", " + r + "]` has " + zeros + " zero" + (zeros == 1 ? "" : "s") + " (≤ " + k + "). Length " + (r - l + 1) + ", best " + best + ".",
                T.arr("nums", nums).p("l", l).p("r", r).range(l, r, "visit").m(r, nums[r] == 0 ? "swap" : "active"), T.v("l", l, "r", r, "zeros", zeros, "k", k, "best", best));
        }
        return best;
    }
    //@end

    //@code totalFruit
    static int totalFruit(int[] fruits) {
        Map<Integer, Integer> count = new HashMap<>();
        int l = 0, best = 0;
        for (int r = 0; r < fruits.length; r++) {
            count.merge(fruits[r], 1, Integer::sum);
            while (count.size() > 2) {
                count.merge(fruits[l], -1, Integer::sum);
                if (count.get(fruits[l]) == 0) count.remove(fruits[l]);
                l++;
                T.s("Third type: shrink", "Three fruit types in the window. Drop index " + (l - 1) + " (type " + fruits[l - 1] + ").",
                    T.arr("fruits", fruits).p("l", l).p("r", r).range(l, r, "visit").m(l - 1, "dim"), T.map("count", count), T.v("l", l, "r", r, "best", best));
            }
            best = Math.max(best, r - l + 1);
            T.s("Pick fruits[r]", "Window `[" + l + ", " + r + "]` has " + count.size() + " type" + (count.size() == 1 ? "" : "s") + ". Length " + (r - l + 1) + ", best " + best + ".",
                T.arr("fruits", fruits).p("l", l).p("r", r).range(l, r, "visit").m(r, "active"), T.map("count", count).m(fruits[r], "swap"), T.v("l", l, "r", r, "best", best));
        }
        return best;
    }
    //@end

    static S letters(int[] c, char hot) {
        TreeMap<Character, Integer> m = new TreeMap<>();
        for (int i = 0; i < 26; i++) if (c[i] > 0) m.put((char) ('A' + i), c[i]);
        S s = T.map("count", m);
        s.m(hot, "active");
        return s;
    }

    //@code characterReplacement
    static int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int l = 0, maxCount = 0, best = 0;
        for (int r = 0; r < s.length(); r++) {
            maxCount = Math.max(maxCount, ++count[s.charAt(r) - 'A']);
            if (r - l + 1 - maxCount > k) {
                T.n("Too many replacements", "Window length " + (r - l + 1) + " minus the top count " + maxCount + " = " + (r - l + 1 - maxCount) + " > " + k + ". Slide `l` forward.",
                    T.str("s", s).p("l", l).p("r", r).range(l, r, "visit").m(l, "dim"), letters(count, s.charAt(l)), T.v("l", l, "r", r, "maxCount", maxCount, "best", best));
                count[s.charAt(l) - 'A']--;
                l++;
            }
            best = Math.max(best, r - l + 1);
            T.s("Window fits", "Length " + (r - l + 1) + ", most common letter appears " + maxCount + " times, so " + (r - l + 1 - maxCount) + " replacement(s) needed. Best: " + best + ".",
                T.str("s", s).p("l", l).p("r", r).range(l, r, "visit").m(r, "active"), letters(count, s.charAt(r)), T.v("l", l, "r", r, "maxCount", maxCount, "best", best));
        }
        return best;
    }
    //@end

    static S cmp(int[] a, int[] b, String name) {
        TreeMap<Character, String> m = new TreeMap<>();
        for (int i = 0; i < 26; i++) if (a[i] > 0 || b[i] > 0) m.put((char) ('a' + i), b[i] + " / " + a[i]);
        S s = T.map(name, m);
        for (int i = 0; i < 26; i++) if (a[i] > 0 || b[i] > 0) s.m((char) ('a' + i), a[i] == b[i] ? "done" : "compare");
        return s;
    }

    //@code checkInclusion
    static boolean checkInclusion(String s1, String s2) {
        if (s1.length() > s2.length()) return false;
        int[] need = new int[26], win = new int[26];
        for (int i = 0; i < s1.length(); i++) {
            need[s1.charAt(i) - 'a']++;
            win[s2.charAt(i) - 'a']++;
        }
        int matches = 0;
        for (int i = 0; i < 26; i++) if (need[i] == win[i]) matches++;
        int m = s1.length();
        T.s("First window", "Count letters of `s1` and of the first " + m + " letters of `s2`. " + matches + " of 26 letters already match.",
            T.str("s2", s2).range(0, m - 1, "visit"), cmp(need, win, "window / need"), T.v("matches", matches));
        for (int r = m; r < s2.length(); r++) {
            if (matches == 26) {
                T.n("Permutation found", "All 26 counts match for \"" + s2.substring(r - m, r) + "\".", T.str("s2", s2).range(r - m, r - 1, "found"), cmp(need, win, "window / need"), T.v("matches", matches));
                return true;
            }
            int in = s2.charAt(r) - 'a', out = s2.charAt(r - m) - 'a';
            win[in]++;
            if (win[in] == need[in]) matches++;
            else if (win[in] == need[in] + 1) matches--;
            win[out]--;
            if (win[out] == need[out]) matches++;
            else if (win[out] == need[out] - 1) matches--;
            T.s("Slide the window", "Add '" + s2.charAt(r) + "', remove '" + s2.charAt(r - m) + "'. Matching letters: " + matches + ".",
                T.str("s2", s2).p("r", r).range(r - m + 1, r, "visit").m(r, "active").m(r - m, "dim"), cmp(need, win, "window / need"), T.v("r", r, "matches", matches));
        }
        T.n("Last window", matches == 26 ? "The final window matches." : "The final window doesn't match either.", T.str("s2", s2).range(s2.length() - m, s2.length() - 1, matches == 26 ? "found" : "dim"), T.v("matches", matches));
        return matches == 26;
    }
    //@end

    static S needMap(int[] need, String t) {
        TreeMap<Character, Integer> m = new TreeMap<>();
        for (char c : t.toCharArray()) m.put(c, need[c]);
        S s = T.map("need", m);
        for (char c : t.toCharArray()) s.m(c, need[c] <= 0 ? "done" : "compare");
        return s;
    }

    //@code minWindow
    static String minWindow(String s, String t) {
        int[] need = new int[128];
        for (char c : t.toCharArray()) need[c]++;
        int missing = t.length(), l = 0, bestL = 0, bestLen = Integer.MAX_VALUE;
        T.s("Count what's needed", "Need " + missing + " characters: \"" + t + "\".", T.str("s", s), needMap(need, t), T.v("missing", missing));
        for (int r = 0; r < s.length(); r++) {
            if (need[s.charAt(r)]-- > 0) {
                missing--;
                T.s("Needed character enters", "'" + s.charAt(r) + "' was needed. Still missing: " + missing + ".",
                    T.str("s", s).p("l", l).p("r", r).range(l, r, "visit").m(r, "active"), needMap(need, t), T.v("l", l, "r", r, "missing", missing));
            }
            while (missing == 0) {
                if (r - l + 1 < bestLen) {
                    bestLen = r - l + 1;
                    bestL = l;
                    T.s("New smallest window", "\"" + s.substring(l, r + 1) + "\" covers `t`, length " + bestLen + ".",
                        T.str("s", s).p("l", l).p("r", r).range(l, r, "found"), needMap(need, t), T.v("l", l, "r", r, "bestLen", bestLen));
                }
                if (++need[s.charAt(l)] > 0) {
                    missing++;
                    T.s("Shrink too far", "Removing '" + s.charAt(l) + "' breaks coverage. Missing: " + missing + ". Expand again.",
                        T.str("s", s).p("l", l).p("r", r).range(l + 1, r, "visit").m(l, "dim"), needMap(need, t), T.v("l", l, "r", r, "missing", missing));
                }
                l++;
            }
        }
        T.n("Answer", bestLen == Integer.MAX_VALUE ? "No window covers `t`." : "Smallest window: \"" + s.substring(bestL, bestL + bestLen) + "\".",
            T.str("s", s).range(bestL, bestLen == Integer.MAX_VALUE ? -1 : bestL + bestLen - 1, "found"), T.v("bestL", bestL, "bestLen", bestLen == Integer.MAX_VALUE ? "∞" : bestLen));
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestL, bestL + bestLen);
    }
    //@end
}
