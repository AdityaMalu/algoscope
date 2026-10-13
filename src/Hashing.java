import java.util.*;

/** Arrays & Hashing */
class Hashing {
    static void run() {
        P.begin("Two Sum", "twoSum");
        P.category("Arrays & Hashing · Hash map");
        P.summary("Find two indices whose values add up to `target`. Instead of checking every pair, walk the array once and remember each value's index in a hash map; for each number you only need to ask whether its complement `target - x` has already appeared.");
        P.cx("O(1)", "O(n)", "O(n)", "O(n)", "Best case is when the first two numbers are the pair. The map can grow to n entries.");
        P.ideas("For each `nums[i]`, the only partner that works is `need = target - nums[i]`.",
            "A hash map from value to index answers \"have I seen `need`?\" in O(1).",
            "Check the map before inserting `nums[i]`, so an element never pairs with itself.",
            "One pass is enough: when the second element of the pair arrives, the first is already in the map.");
        P.pits("Inserting before checking lets `[3, 2, 4], target = 6` return `[0, 0]` (using 3 twice).",
            "Sorting first loses the original indices unless you carry them along.",
            "The brute-force double loop is O(n²); interviewers expect the hash map version.",
            "Returning values instead of indices.");
        P.trace("nums = [2, 7, 11, 15], target = 9", () -> twoSum(new int[]{2, 7, 11, 15}, 9));
        P.edge("Same value used twice", "nums = [3, 3], target = 6", "[0, 1]", () -> twoSum(new int[]{3, 3}, 6), "The first 3 is stored, then the second 3 finds it. Two different indices, same value.");
        P.edge("Don't reuse one element", "nums = [3, 2, 4], target = 6", "[1, 2]", () -> twoSum(new int[]{3, 2, 4}, 6), "When i = 0, `need = 3` is not in the map yet, so 3 never pairs with itself.");
        P.edge("Negative numbers", "nums = [-3, 4, 3, 90], target = 0", "[0, 2]", () -> twoSum(new int[]{-3, 4, 3, 90}, 0), "Complements can be negative; the map doesn't care about sign.");
        P.edge("Zeros", "nums = [0, 4, 3, 0], target = 0", "[0, 3]", () -> twoSum(new int[]{0, 4, 3, 0}, 0), "0 + 0 = 0 works because the two zeros are at different indices.");
        P.edge("Pair at the very end", "nums = [1, 2, 3, 4, 5], target = 9", "[3, 4]", () -> twoSum(new int[]{1, 2, 3, 4, 5}, 9), "The worst case: the whole array is scanned before the match.");
        P.risky("No valid pair", "nums = [1, 2], target = 7", "[]", () -> twoSum(new int[]{1, 2}, 7), "LeetCode guarantees exactly one answer. This code returns an empty array otherwise; say so out loud in an interview.");
        P.done();

        P.begin("Valid Anagram", "isAnagram");
        P.category("Arrays & Hashing · Counting");
        P.summary("Two strings are anagrams when they use exactly the same letters the same number of times. Count letters of `s` up and letters of `t` down in one array of 26 counters; they are anagrams only if every counter ends at zero.");
        P.cx("O(1)", "O(n)", "O(n)", "O(1)", "Best case is a length mismatch. The 26 counters are constant space.");
        P.ideas("Different lengths can never be anagrams, so check that first.",
            "One pass: `count[s[i]]++` and `count[t[i]]--` together.",
            "If any counter is non-zero at the end, some letter is over- or under-used.",
            "An int[26] beats a HashMap for lowercase letters: faster and simpler.");
        P.pits("Forgetting the length check, then reading past the end of the shorter string.",
            "`c - 'a'` breaks on uppercase or Unicode; use a HashMap<Character, Integer> for the follow-up.",
            "Sorting both strings works but is O(n log n).");
        P.trace("s = \"anagram\", t = \"nagaram\"", () -> isAnagram("anagram", "nagaram"));
        P.edge("Different lengths", "s = \"ab\", t = \"abc\"", "false", () -> isAnagram("ab", "abc"), "Rejected before any counting.");
        P.edge("Same letters, different counts", "s = \"aacc\", t = \"ccac\"", "false", () -> isAnagram("aacc", "ccac"), "'a' ends at +1 and 'c' at -1.");
        P.edge("Empty strings", "s = \"\", t = \"\"", "true", () -> isAnagram("", ""), "No letters, all counters zero.");
        P.edge("Identical strings", "s = \"rat\", t = \"rat\"", "true", () -> isAnagram("rat", "rat"), "Every letter cancels at the same index.");
        P.risky("Uppercase letters", "s = \"Ab\", t = \"bA\"", "throws ArrayIndexOutOfBoundsException", () -> isAnagram("Ab", "bA"), "`'A' - 'a'` is -32. Only lowercase English letters are allowed by the problem; for Unicode use a map.");
        P.done();

        P.begin("Contains Duplicate", "containsDuplicate");
        P.category("Arrays & Hashing · Hash set");
        P.summary("Return true if any value appears at least twice. Add each value to a hash set; `add` returns false the moment a value is already there.");
        P.cx("O(1)", "O(n)", "O(n)", "O(n)", "Best case is a duplicate in the first two elements. The set can hold all n values.");
        P.ideas("A HashSet answers \"seen before?\" in O(1).",
            "`set.add(x)` returns false if x was already present, so check and insert in one call.",
            "Stop at the first duplicate; no need to finish the scan.");
        P.pits("Sorting and comparing neighbours is O(n log n) and mutates the input.",
            "Comparing every pair is O(n²) and times out.");
        P.trace("nums = [1, 2, 3, 1]", () -> containsDuplicate(new int[]{1, 2, 3, 1}));
        P.edge("All distinct", "nums = [1, 2, 3, 4]", "false", () -> containsDuplicate(new int[]{1, 2, 3, 4}), "The scan finishes without a collision.");
        P.edge("Single element", "nums = [7]", "false", () -> containsDuplicate(new int[]{7}), "One value can't repeat.");
        P.edge("Duplicate right away", "nums = [5, 5, 1]", "true", () -> containsDuplicate(new int[]{5, 5, 1}), "Found at index 1, the rest is never read.");
        P.edge("Negatives and zero", "nums = [0, -1, -1]", "true", () -> containsDuplicate(new int[]{0, -1, -1}), "Hashing works the same for negative values.");
        P.done();

        P.begin("Majority Element", "majorityElement");
        P.category("Arrays & Hashing · Boyer–Moore voting");
        P.summary("Find the value that appears more than n/2 times. Boyer–Moore voting keeps one candidate and a counter: matching values vote for it, others vote against, and when the counter hits zero a new candidate takes over. The true majority always survives.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Always one full pass with two variables.");
        P.ideas("Pair each majority element with a different element and cancel them; the majority still has some left over.",
            "`count == 0` means everything so far cancelled out, so start fresh with the current value.",
            "The final candidate is the majority, as long as one is guaranteed to exist.");
        P.pits("Without the guarantee, the survivor might not be a majority; verify with a second pass.",
            "Changing the candidate when count is 1 instead of 0.",
            "A HashMap of counts works but uses O(n) space; interviewers ask for O(1).");
        P.trace("nums = [2, 2, 1, 1, 1, 2, 2]", () -> majorityElement(new int[]{2, 2, 1, 1, 1, 2, 2}));
        P.edge("Single element", "nums = [5]", "5", () -> majorityElement(new int[]{5}), "The first element becomes the candidate and is never challenged.");
        P.edge("Majority at the end", "nums = [1, 2, 3, 3, 3]", "3", () -> majorityElement(new int[]{1, 2, 3, 3, 3}), "1 and 2 cancel, then 3 takes over.");
        P.edge("Negative values", "nums = [-1, -1, 2]", "-1", () -> majorityElement(new int[]{-1, -1, 2}), "Values are compared, not used as indices.");
        P.risky("No majority exists", "nums = [1, 2, 3]", "3", () -> majorityElement(new int[]{1, 2, 3}), "The algorithm still returns something. Without the problem's guarantee, count the survivor in a second pass.");
        P.done();

        P.begin("Group Anagrams", "groupAnagrams");
        P.category("Arrays & Hashing · Canonical key");
        P.summary("Group words that are anagrams of each other. Sorting a word's letters gives a key that all its anagrams share, so a map from sorted key to list of words collects the groups in one pass.");
        P.cx("O(n·k log k)", "O(n·k log k)", "O(n·k log k)", "O(n·k)", "n words of length up to k. A 26-count key makes it O(n·k).");
        P.ideas("Anagrams become identical after sorting their letters: \"eat\", \"tea\", \"ate\" → \"aet\".",
            "Use that sorted string as a HashMap key; the value is the list of words in that group.",
            "`computeIfAbsent` creates the list the first time a key appears.");
        P.pits("Using `char[]` directly as the key: arrays use identity hashing, so nothing groups.",
            "Building the count-key as \"1112\" without separators can collide; use '#' between counts.",
            "Assuming the output order matters; any order is accepted.");
        P.trace("strs = [\"eat\", \"tea\", \"tan\", \"ate\", \"nat\", \"bat\"]", () -> groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
        P.edge("One empty string", "strs = [\"\"]", "[[\"\"]]", () -> groupAnagrams(new String[]{""}), "The empty string sorts to \"\" and forms its own group.");
        P.edge("Single word", "strs = [\"a\"]", "[[\"a\"]]", () -> groupAnagrams(new String[]{"a"}), "One group with one word.");
        P.edge("No anagrams at all", "strs = [\"abc\", \"def\"]", "[[\"abc\"], [\"def\"]]", () -> groupAnagrams(new String[]{"abc", "def"}), "Every word gets its own key.");
        P.edge("Duplicate words", "strs = [\"ab\", \"ba\", \"ab\"]", "[[\"ab\", \"ba\", \"ab\"]]", () -> groupAnagrams(new String[]{"ab", "ba", "ab"}), "Duplicates are kept; they land in the same group.");
        P.done();

        P.begin("Top K Frequent Elements", "topKFrequent");
        P.category("Arrays & Hashing · Bucket sort");
        P.summary("Return the k most frequent values. Count frequencies with a map, then place each value into a bucket indexed by its frequency. Reading buckets from the highest frequency down gives the answer in O(n), no sorting needed.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "A frequency can be at most n, so there are n + 1 buckets.");
        P.ideas("Frequencies are bounded by n, so they can be used as array indices.",
            "`bucket[f]` holds every value that appears exactly f times.",
            "Walk buckets from n down to 1 and collect values until you have k.");
        P.pits("Sorting the map entries by count is O(n log n), fine but not optimal.",
            "A min-heap of size k is O(n log k); know both and when each is better.",
            "Sizing the bucket array as n instead of n + 1 crashes when every element is equal.");
        P.trace("nums = [1, 1, 1, 2, 2, 3], k = 2", () -> topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2));
        P.edge("Single element", "nums = [1], k = 1", "[1]", () -> topKFrequent(new int[]{1}, 1), "One value in bucket 1.");
        P.edge("All values equal", "nums = [4, 4, 4], k = 1", "[4]", () -> topKFrequent(new int[]{4, 4, 4}, 1), "Frequency 3 = n, which is why the array has n + 1 buckets.");
        P.edge("k equals the number of distinct values", "nums = [1, 2], k = 2", "[1, 2]", () -> topKFrequent(new int[]{1, 2}, 2), "Every value is returned.");
        P.edge("Negative values", "nums = [-1, -1, 2], k = 1", "[-1]", () -> topKFrequent(new int[]{-1, -1, 2}, 1), "Values are map keys, so negatives are fine.");
        P.risky("Ties at the cut-off", "nums = [1, 2, 3, 1, 2, 3], k = 2", "[1, 2]", () -> topKFrequent(new int[]{1, 2, 3, 1, 2, 3}, 2), "Three values tie; which two you get depends on map order. LeetCode guarantees the answer is unique, so this can't happen there.");
        P.done();

        P.begin("Product of Array Except Self", "productExceptSelf");
        P.category("Arrays & Hashing · Prefix and suffix products");
        P.summary("For each index, return the product of every other element, without division. The answer at i is (product of everything left of i) × (product of everything right of i): fill left products in one pass, then multiply in right products in a second pass.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Two passes. The output array doesn't count as extra space, and the suffix is one variable.");
        P.ideas("`res[i] = prefix(i) × suffix(i)`, where neither includes `nums[i]`.",
            "First pass: `res[i] = res[i-1] × nums[i-1]` stores prefix products.",
            "Second pass right-to-left: keep a running `suffix` and multiply it in.",
            "No division means zeros need no special handling.");
        P.pits("Using division breaks on zeros, and the problem forbids it anyway.",
            "Off-by-one: the prefix for index 0 is 1 (empty product), not nums[0].",
            "Updating `suffix` before multiplying includes `nums[i]` in its own answer.");
        P.trace("nums = [1, 2, 3, 4]", () -> productExceptSelf(new int[]{1, 2, 3, 4}));
        P.edge("One zero", "nums = [-1, 1, 0, -3, 3]", "[0, 0, 9, 0, 0]", () -> productExceptSelf(new int[]{-1, 1, 0, -3, 3}), "Only the zero's own index gets a non-zero product.");
        P.edge("Two zeros", "nums = [0, 0, 2]", "[0, 0, 0]", () -> productExceptSelf(new int[]{0, 0, 2}), "Every product includes at least one zero.");
        P.edge("Two elements", "nums = [5, 6]", "[6, 5]", () -> productExceptSelf(new int[]{5, 6}), "Each answer is simply the other element.");
        P.edge("Negatives", "nums = [-1, -2, -3]", "[6, 3, 2]", () -> productExceptSelf(new int[]{-1, -2, -3}), "Signs multiply normally.");
        P.done();

        P.begin("Longest Consecutive Sequence", "longestConsecutive");
        P.category("Arrays & Hashing · Hash set");
        P.summary("Find the length of the longest run of consecutive integers, in O(n). Put all values in a set. A value starts a run only if `x - 1` is absent; from each start, count upward while `x + len` exists. Each number is visited at most twice, so the total is linear.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "The inner while only runs from run starts, so every value is extended over at most once.");
        P.ideas("Only begin counting at a number whose predecessor is missing; that's the start of a run.",
            "From a start, walk `x+1, x+2, …` using O(1) set lookups.",
            "Iterating over the set (not the array) skips duplicates for free.");
        P.pits("Starting a count from every number makes it O(n²).",
            "Sorting is O(n log n), which the problem rules out.",
            "Forgetting the empty-array case (answer 0).");
        P.trace("nums = [100, 4, 200, 1, 3, 2]", () -> longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}));
        P.edge("Empty array", "nums = []", "0", () -> longestConsecutive(new int[]{}), "No runs; `best` stays 0.");
        P.edge("Duplicates", "nums = [1, 2, 0, 1]", "3", () -> longestConsecutive(new int[]{1, 2, 0, 1}), "The set removes the second 1; the run is 0, 1, 2.");
        P.edge("Negative numbers", "nums = [0, -1, -2, 5]", "3", () -> longestConsecutive(new int[]{0, -1, -2, 5}), "-2 is the start, and the run crosses zero.");
        P.edge("Single element", "nums = [7]", "1", () -> longestConsecutive(new int[]{7}), "A lone value is a run of length 1.");
        P.edge("Several runs", "nums = [9, 1, 4, 7, 3, -1, 0, 5, 8, -1, 6]", "7", () -> longestConsecutive(new int[]{9, 1, 4, 7, 3, -1, 0, 5, 8, -1, 6}), "Runs -1..1 (3) and 3..9 (7); the longer wins.");
        P.done();
    }

    // ---------- trace helpers ----------
    static S cnt(int[] count, String letters, char a, char b) {
        TreeMap<Character, Integer> m = new TreeMap<>();
        for (char c : letters.toCharArray()) m.put(c, count[c - 'a']);
        S s = T.map("count", m);
        s.m(a, "active"); s.m(b, "compare");
        return s;
    }
    static S runSet(Set<Integer> set, int start, int len, String mark) {
        S s = T.set("set", set);
        for (int i = 0; i < len; i++) s.m(start + i, mark);
        return s;
    }

    //@code twoSum
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        T.s("Start with an empty map", "Walk the array once, remembering each value's index in `seen`.",
            T.arr("nums", nums), T.map("seen", seen), T.v("target", target));
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            T.s("Compute the complement", "`need = " + target + " - " + nums[i] + " = " + need + "`. Has " + need + " appeared before?",
                T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.map("seen", seen).m(need, "compare"), T.v("target", target, "i", i, "need", need));
            if (seen.containsKey(need)) {
                int j = seen.get(need);
                T.n("Found the pair", need + " was seen at index " + j + ", so `nums[" + j + "] + nums[" + i + "] = " + target + "`.",
                    T.arr("nums", nums).p("i", i).m(j, "found").m(i, "found"), T.map("seen", seen).m(need, "found"), T.v("target", target, "i", i, "need", need, "j", j));
                return new int[]{j, i};
            }
            seen.put(nums[i], i);
            T.s("Remember nums[i]", "Not found. Store `" + nums[i] + " → " + i + "` so a later number can pair with it.",
                T.arr("nums", nums).p("i", i).range(0, i, "visit"), T.map("seen", seen).m(nums[i], "swap"), T.v("target", target, "i", i, "need", need));
        }
        T.n("No pair exists", "Every number was checked and no complement was found.", T.arr("nums", nums).range(0, nums.length - 1, "dim"), T.map("seen", seen));
        return new int[]{};
    }
    //@end

    //@code isAnagram
    static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            T.n("Lengths differ", s.length() + " ≠ " + t.length() + ", so they can't be anagrams.", T.str("s", s), T.str("t", t));
            return false;
        }
        int[] count = new int[26];
        T.s("26 counters, all zero", "Letters of `s` will count up, letters of `t` count down.", T.str("s", s), T.str("t", t), cnt(count, s + t, ' ', ' '));
        for (int i = 0; i < s.length(); i++) {
            count[s.charAt(i) - 'a']++;
            count[t.charAt(i) - 'a']--;
            T.s("Count s[i] up, t[i] down", "'" + s.charAt(i) + "' goes up by one, '" + t.charAt(i) + "' goes down by one.",
                T.str("s", s).p("i", i).range(0, i - 1, "done").m(i, "active"), T.str("t", t).p("i", i).range(0, i - 1, "done").m(i, "compare"), cnt(count, s + t, s.charAt(i), t.charAt(i)), T.v("i", i));
        }
        for (int k = 0; k < 26; k++) {
            if (count[k] != 0) {
                T.n("A counter is not zero", "'" + (char) ('a' + k) + "' ends at " + count[k] + ", so the letter counts differ.", T.str("s", s), T.str("t", t), cnt(count, s + t, (char) ('a' + k), ' '));
                return false;
            }
        }
        T.n("Every counter is zero", "Each letter appears equally often in both strings.", T.str("s", s).range(0, s.length() - 1, "found"), T.str("t", t).range(0, t.length() - 1, "found"), cnt(count, s + t, ' ', ' '));
        return true;
    }
    //@end

    //@code containsDuplicate
    static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int i = 0; i < nums.length; i++) {
            T.n("Try to add nums[i]", "Is " + nums[i] + " already in the set?", T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.set("seen", seen).m(nums[i], "compare"), T.v("i", i));
            if (!seen.add(nums[i])) {
                T.n("Duplicate found", nums[i] + " was already in the set, so `add` returned false.", T.arr("nums", nums).p("i", i).m(i, "found"), T.set("seen", seen).m(nums[i], "found"), T.v("i", i));
                return true;
            }
        }
        T.n("All distinct", "The scan finished without a repeated value.", T.arr("nums", nums).range(0, nums.length - 1, "done"), T.set("seen", seen));
        return false;
    }
    //@end

    //@code majorityElement
    static int majorityElement(int[] nums) {
        int candidate = 0, count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (count == 0) {
                candidate = nums[i];
                T.s("New candidate", "The count is 0, so everything before cancelled out. " + nums[i] + " becomes the candidate.",
                    T.arr("nums", nums).p("i", i).range(0, i - 1, "dim").m(i, "active"), T.v("i", i, "candidate", candidate, "count", count));
            }
            count += (nums[i] == candidate) ? 1 : -1;
            T.s(nums[i] == candidate ? "Vote for the candidate" : "Vote against", nums[i] == candidate
                    ? nums[i] + " matches the candidate, so `count` goes up to " + count + "."
                    : nums[i] + " differs from " + candidate + ", so it cancels one vote: `count` drops to " + count + ".",
                T.arr("nums", nums).p("i", i).m(i, nums[i] == candidate ? "done" : "compare"), T.v("i", i, "candidate", candidate, "count", count));
        }
        T.n("The survivor is the majority", candidate + " is left standing with `count = " + count + "`.", T.arr("nums", nums).mark(markAll(nums, candidate)), T.v("candidate", candidate, "count", count));
        return candidate;
    }
    //@end
    static Map<Integer, String> markAll(int[] a, int v) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < a.length; i++) m.put(i, a[i] == v ? "found" : "dim"); return m; }

    //@code groupAnagrams
    static List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();
        for (int i = 0; i < strs.length; i++) {
            char[] c = strs[i].toCharArray();
            Arrays.sort(c);
            String key = new String(c);
            T.s("Sort the letters", "\"" + strs[i] + "\" sorted is \"" + key + "\". All its anagrams share this key.",
                T.arr("strs", strs).p("i", i).range(0, i - 1, "done").m(i, "active"), T.map("groups", groups).m(key, "compare"), T.v("i", i, "key", key));
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(strs[i]);
            T.s("Add to its group", "\"" + strs[i] + "\" joins the \"" + key + "\" group, which now has " + groups.get(key).size() + " word" + (groups.get(key).size() == 1 ? "" : "s") + ".",
                T.arr("strs", strs).p("i", i).range(0, i, "done"), T.map("groups", groups).m(key, "swap"), T.v("i", i, "key", key));
        }
        T.n("Return the groups", groups.size() + " groups, one per distinct sorted key.", T.map("groups", groups));
        return new ArrayList<>(groups.values());
    }
    //@end

    //@code topKFrequent
    @SuppressWarnings("unchecked") // @hide
    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int x : nums) freq.merge(x, 1, Integer::sum);
        T.s("Count every value", "One pass builds `value → count`.", T.arr("nums", nums), T.map("freq", freq), T.v("k", k));
        List<Integer>[] bucket = new List[nums.length + 1];
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            int f = e.getValue();
            if (bucket[f] == null) bucket[f] = new ArrayList<>();
            bucket[f].add(e.getKey());
            T.s("Drop into bucket[count]", e.getKey() + " appears " + f + " time" + (f == 1 ? "" : "s") + ", so it goes into `bucket[" + f + "]`.",
                T.map("freq", freq).m(e.getKey(), "active"), T.arr("bucket", bucket).m(f, "swap"), T.v("k", k, "f", f));
        }
        int[] res = new int[k];
        int idx = 0;
        for (int f = bucket.length - 1; f >= 0 && idx < k; f--) {
            if (bucket[f] == null) continue;
            for (int x : bucket[f]) {
                if (idx == k) break;
                res[idx++] = x;
                T.s("Take from the highest bucket", "`bucket[" + f + "]` is the highest non-empty bucket left, so take " + x + ".",
                    T.arr("bucket", bucket).p("f", f).range(f + 1, bucket.length - 1, "dim").m(f, "active"), T.arr("res", res).range(0, idx - 2, "done").m(idx - 1, "swap"), T.v("k", k, "f", f, "idx", idx));
            }
        }
        T.n("Done", "Collected " + k + " values, from most to least frequent.", T.arr("res", res).range(0, k - 1, "found"));
        return res;
    }
    //@end

    //@code productExceptSelf
    static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] res = new int[n];
        res[0] = 1;
        T.s("Nothing is left of index 0", "The prefix product for index 0 is the empty product, 1.", T.arr("nums", nums), T.arr("res", res).m(0, "swap"));
        for (int i = 1; i < n; i++) {
            res[i] = res[i - 1] * nums[i - 1];
            T.s("Prefix product", "`res[" + i + "] = res[" + (i - 1) + "] × nums[" + (i - 1) + "] = " + res[i - 1] + " × " + nums[i - 1] + " = " + res[i] + "`: the product of everything left of " + i + ".",
                T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.arr("res", res).p("i", i).m(i, "swap"), T.v("i", i));
        }
        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            res[i] *= suffix;
            T.s("Multiply in the suffix", "`suffix = " + suffix + "` is the product of everything right of " + i + ". `res[" + i + "]` becomes " + res[i] + ".",
                T.arr("nums", nums).p("i", i).range(i + 1, n - 1, "visit").m(i, "active"), T.arr("res", res).p("i", i).range(i + 1, n - 1, "done").m(i, "found"), T.v("i", i, "suffix", suffix));
            suffix *= nums[i];
        }
        T.n("Done", "Each `res[i]` is prefix × suffix, never including `nums[i]` itself.", T.arr("nums", nums), T.arr("res", res).range(0, n - 1, "found"));
        return res;
    }
    //@end

    //@code longestConsecutive
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) set.add(x);
        T.s("Put every value in a set", "Lookups are O(1), and duplicates disappear.", T.arr("nums", nums), T.set("set", set));
        int best = 0;
        for (int x : set) {
            if (set.contains(x - 1)) {
                T.n("Not a run start", (x - 1) + " is in the set, so " + x + " is inside a run that starts earlier. Skip it.", runSet(set, x, 1, "dim").m(x - 1, "compare"), T.v("x", x, "best", best));
                continue;
            }
            int len = 1;
            T.s("Start of a run", (x - 1) + " is missing, so " + x + " starts a run.", runSet(set, x, 1, "active"), T.v("x", x, "len", len, "best", best));
            while (set.contains(x + len)) {
                len++;
                T.s("Extend the run", (x + len - 1) + " is in the set. The run is now " + len + " long.", runSet(set, x, len, "visit").m(x + len - 1, "active"), T.v("x", x, "len", len, "best", best));
            }
            best = Math.max(best, len);
            T.s("Run ends", (x + len) + " is missing. This run has length " + len + "; best is " + best + ".", runSet(set, x, len, "done"), T.v("x", x, "len", len, "best", best));
        }
        return best;
    }
    //@end
}
