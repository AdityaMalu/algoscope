import java.util.*;

/** Heap / Priority Queue */
class Heaps {
    static void run() {
        P.begin("Kth Largest Element in an Array", "findKthLargest");
        P.category("Heap · Size-k min-heap");
        P.summary("Keep a min-heap of the k largest values seen so far. Push each number; if the heap grows past k, pop its smallest. At the end the heap's top is the k-th largest.");
        P.cx("O(n log k)", "O(n log k)", "O(n log k)", "O(k)", "Quickselect is O(n) on average, O(n²) worst case.");
        P.ideas("The k-th largest is the smallest of the top k values.",
            "A min-heap of size k keeps exactly those top k, smallest on top.",
            "When a 6th value arrives for k = 5, the smallest of the six can't be in the top 5: pop it.");
        P.pits("Using a max-heap of all n values is O(n log n) and O(n) space.",
            "Sorting is O(n log n); fine to mention, but the heap or quickselect is expected.",
            "Duplicates count separately: [3, 3, 2], k = 2 gives 3.");
        P.trace("nums = [3, 2, 1, 5, 6, 4], k = 2", () -> findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2));
        P.edge("Duplicates", "nums = [3, 2, 3, 1, 2, 4, 5, 5, 6], k = 4", "4", () -> findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4), "6, 5, 5, 4: the two 5s count separately.");
        P.edge("k = 1", "nums = [7, 2, 9], k = 1", "9", () -> findKthLargest(new int[]{7, 2, 9}, 1), "The maximum.");
        P.edge("k = n", "nums = [7, 2, 9], k = 3", "2", () -> findKthLargest(new int[]{7, 2, 9}, 3), "The minimum.");
        P.edge("Negatives", "nums = [-1, -5, -3], k = 2", "-3", () -> findKthLargest(new int[]{-1, -5, -3}, 2), "Ordering works the same.");
        P.done();

        P.begin("Last Stone Weight", "lastStoneWeight");
        P.category("Heap · Max-heap simulation");
        P.summary("Repeatedly smash the two heaviest stones: equal stones both vanish, otherwise the difference goes back. A max-heap always gives the two heaviest in O(log n).");
        P.cx("O(n log n)", "O(n log n)", "O(n log n)", "O(n)", "Each smash removes at least one stone.");
        P.ideas("Each round needs the two largest values: exactly what a max-heap provides.",
            "Push back `y - x` only when it's non-zero.",
            "Return the last stone, or 0 if none are left.");
        P.pits("Java's PriorityQueue is a min-heap; pass `Collections.reverseOrder()`.",
            "Re-sorting the array each round is O(n² log n).");
        P.trace("stones = [2, 7, 4, 1, 8, 1]", () -> lastStoneWeight(new int[]{2, 7, 4, 1, 8, 1}));
        P.edge("Single stone", "stones = [1]", "1", () -> lastStoneWeight(new int[]{1}), "Nothing to smash.");
        P.edge("Everything cancels", "stones = [2, 2]", "0", () -> lastStoneWeight(new int[]{2, 2}), "Equal stones both vanish.");
        P.edge("All the same", "stones = [3, 3, 3]", "3", () -> lastStoneWeight(new int[]{3, 3, 3}), "Two cancel, one is left.");
        P.done();

        P.begin("K Closest Points to Origin", "kClosest");
        P.category("Heap · Size-k max-heap");
        P.summary("Keep the k closest points in a max-heap keyed by squared distance. When the heap grows past k, drop the farthest. What remains are the k closest.");
        P.cx("O(n log k)", "O(n log k)", "O(n log k)", "O(k)", "Compare squared distances; no square roots needed.");
        P.ideas("To keep the k smallest, use a max-heap and evict its top.",
            "`x² + y²` preserves the distance order without `Math.sqrt`.",
            "Output order doesn't matter.");
        P.pits("Using sqrt and doubles: slower and introduces rounding.",
            "Comparator overflow with `a - b` style comparisons on large values; use Integer.compare.");
        P.trace("points = [[3, 3], [5, -1], [-2, 4], [1, 1]], k = 2", () -> kClosest(new int[][]{{3, 3}, {5, -1}, {-2, 4}, {1, 1}}, 2));
        P.edge("k equals n", "points = [[1, 3], [-2, 2]], k = 2", "[[1, 3], [-2, 2]]", () -> kClosest(new int[][]{{1, 3}, {-2, 2}}, 2), "Every point is returned.");
        P.edge("Origin itself", "points = [[0, 0], [1, 1]], k = 1", "[[0, 0]]", () -> kClosest(new int[][]{{0, 0}, {1, 1}}, 1), "Distance 0 is the closest.");
        P.edge("k = 1", "points = [[1, 3], [-2, 2]], k = 1", "[[-2, 2]]", () -> kClosest(new int[][]{{1, 3}, {-2, 2}}, 1), "8 < 10.");
        P.done();

        P.begin("Task Scheduler", "leastInterval");
        P.category("Heap · Greedy counting");
        P.summary("Tasks of the same type need `n` idle slots between them. The most frequent task decides the shape: it creates `maxFreq - 1` gaps of size `n`. Fill them with other tasks; if there are more tasks than slots, no idling is needed and the answer is the task count.");
        P.cx("O(N)", "O(N)", "O(N)", "O(1)", "Counting 26 letters; the formula is O(1) after that.");
        P.ideas("Lay out the most frequent task with n slots after each: `(maxFreq - 1) × (n + 1)` frames.",
            "Tasks tied for the max frequency fill the final frame: add `countMax`.",
            "If tasks outnumber the frames, there are no idle slots: answer is `tasks.length`.");
        P.pits("Forgetting the `max(tasks.length, …)` part.",
            "Counting only one task with the maximum frequency.",
            "A max-heap simulation also works and is good to explain; the formula is faster.");
        P.trace("tasks = [A, A, A, B, B, B], n = 2", () -> leastInterval(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 2));
        P.edge("No cooldown", "tasks = [A, A, A, B, B, B], n = 0", "6", () -> leastInterval(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 0), "Run back to back.");
        P.edge("Plenty of variety", "tasks = [A, B, C, D, E, A, B, C, D, E], n = 2", "10", () -> leastInterval("ABCDEABCDE".toCharArray(), 2), "Enough different tasks to fill every gap.");
        P.edge("One task type", "tasks = [A, A, A], n = 3", "9", () -> leastInterval(new char[]{'A', 'A', 'A'}, 3), "A _ _ _ A _ _ _ A.");
        P.edge("Single task", "tasks = [A], n = 5", "1", () -> leastInterval(new char[]{'A'}, 5), "No repeats, no idling.");
        P.done();

        P.begin("Top K Frequent Words", "topKFrequentWords");
        P.category("Heap · Custom comparator");
        P.summary("Return the k most frequent words, ties broken alphabetically. Count words, then keep a size-k min-heap whose \"smallest\" is the least frequent word (and, among equal counts, the alphabetically last). Pop everything and reverse.");
        P.cx("O(n log k)", "O(n log k)", "O(n log k)", "O(n)", "n distinct words.");
        P.ideas("The heap's top must be the word that should be evicted first.",
            "Evict lower counts first; for equal counts, evict the alphabetically larger word.",
            "Popping gives worst-to-best, so reverse at the end.");
        P.pits("Getting the tie-break backwards in the min-heap comparator.",
            "Forgetting to reverse the popped order.");
        P.trace("words = [i, love, leetcode, i, love, coding], k = 2", () -> topKFrequentWords(new String[]{"i", "love", "leetcode", "i", "love", "coding"}, 2));
        P.edge("Ties broken alphabetically", "words = [b, a, c], k = 2", "[\"a\", \"b\"]", () -> topKFrequentWords(new String[]{"b", "a", "c"}, 2), "All counts are 1, so alphabetical order decides.");
        P.edge("k = 1", "words = [x, y, y], k = 1", "[\"y\"]", () -> topKFrequentWords(new String[]{"x", "y", "y"}, 1), "y appears twice.");
        P.edge("LeetCode example 2", "words = [the, day, is, sunny, the, the, the, sunny, is, is], k = 4", "[\"the\", \"is\", \"sunny\", \"day\"]",
            () -> topKFrequentWords(new String[]{"the", "day", "is", "sunny", "the", "the", "the", "sunny", "is", "is"}, 4), "Counts 4, 3, 2, 1.");
        P.done();

        P.begin("Reorganize String", "reorganizeString");
        P.category("Heap · Greedy");
        P.summary("Rearrange letters so no two neighbours are equal. Greedily place the most frequent remaining letter, but never the one just placed: pop the top two letters from a max-heap, append both, and push them back with one fewer count.");
        P.cx("O(n log 26)", "O(n)", "O(n)", "O(1)", "The heap holds at most 26 letters.");
        P.ideas("If any letter appears more than `(n + 1) / 2` times, it's impossible.",
            "Placing the two most frequent letters together each round keeps them from bunching up later.",
            "When only one letter is left, it must have count 1.");
        P.pits("Placing the most frequent letter twice in a row.",
            "Missing the impossibility check, then producing an invalid string.");
        P.trace("s = \"aaabbc\"", () -> reorganizeString("aaabbc"));
        P.edge("Impossible", "s = \"aaab\"", "\"\"", () -> reorganizeString("aaab"), "3 a's need at least 5 characters.");
        P.edge("Single character", "s = \"a\"", "\"a\"", () -> reorganizeString("a"), "Nothing to separate.");
        P.edge("Two different", "s = \"ab\"", "\"ab\"", () -> reorganizeString("ab"), "Already fine.");
        P.edge("Exactly at the limit", "s = \"aab\"", "\"aba\"", () -> reorganizeString("aab"), "a appears (3 + 1) / 2 = 2 times: just possible.");
        P.done();

        P.begin("Find Median from Data Stream", "MedianFinder");
        P.category("Heap · Two heaps");
        P.summary("Keep the smaller half of the numbers in a max-heap and the larger half in a min-heap, balanced so the max-heap has the same size or one more. The median is the max-heap's top, or the average of both tops.");
        P.cx("O(log n)", "O(log n)", "O(log n)", "O(n)", "addNum is O(log n); findMedian is O(1).");
        P.ideas("`low` (max-heap) holds the smaller half; `high` (min-heap) holds the larger half.",
            "Every value in `low` is ≤ every value in `high`.",
            "Add to `low`, move its top to `high`, then move back if `high` got bigger.",
            "Odd count: median is `low.peek()`. Even: average the two tops.");
        P.pits("Integer division when averaging; use 2.0.",
            "Letting the heaps drift more than one apart.",
            "Sorting on every add is O(n log n) per call.");
        P.trace("addNum(5), addNum(15), findMedian(), addNum(1), findMedian(), addNum(3), findMedian()", () -> {
            MedianFinder m = new MedianFinder(); List<Object> out = new ArrayList<>(); out.add(null);
            m.addNum(5); out.add(null); m.addNum(15); out.add(null); out.add(m.findMedian()); m.addNum(1); out.add(null); out.add(m.findMedian()); m.addNum(3); out.add(null); out.add(m.findMedian());
            return out;
        });
        P.edge("Single number", "addNum(7), findMedian()", "[null, null, 7.0]", () -> {
            MedianFinder m = new MedianFinder(); List<Object> out = new ArrayList<>(); out.add(null); m.addNum(7); out.add(null); out.add(m.findMedian()); return out;
        }, "The max-heap holds the only value.");
        P.edge("Negative numbers", "addNum(-1), addNum(-2), findMedian()", "[null, null, null, -1.5]", () -> {
            MedianFinder m = new MedianFinder(); List<Object> out = new ArrayList<>(); out.add(null); m.addNum(-1); out.add(null); m.addNum(-2); out.add(null); out.add(m.findMedian()); return out;
        }, "Average of -2 and -1.");
        P.edge("Descending input", "addNum(3), addNum(2), addNum(1), findMedian()", "[null, null, null, null, 2.0]", () -> {
            MedianFinder m = new MedianFinder(); List<Object> out = new ArrayList<>(); out.add(null); m.addNum(3); out.add(null); m.addNum(2); out.add(null); m.addNum(1); out.add(null); out.add(m.findMedian()); return out;
        }, "Rebalancing keeps the middle value on top of `low`.");
        P.done();
    }

    //@code findKthLargest
    static int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int i = 0; i < nums.length; i++) {
            heap.offer(nums[i]);
            if (heap.size() > k) {
                T.n("Too big: pop the smallest", "The heap has " + heap.size() + " values; the smallest (" + heap.peek() + ") can't be in the top " + k + ".", T.arr("nums", nums).p("i", i).m(i, "active"), T.heap("heap (min on top)", heap).m(0, "error"));
                heap.poll();
            }
            T.s("Push " + nums[i], "The heap holds the " + heap.size() + " largest values so far. Its top, " + heap.peek() + ", is the smallest of them.", T.arr("nums", nums).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.heap("heap (min on top)", heap).m(0, "compare"));
        }
        T.n("Answer", "The top of the heap is the " + k + "th largest: " + heap.peek() + ".", T.heap("heap (min on top)", heap).m(0, "found"));
        return heap.peek();
    }
    //@end

    //@code lastStoneWeight
    static int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> heap = new PriorityQueue<>(Collections.reverseOrder());
        for (int s : stones) heap.offer(s);
        T.s("Max-heap of stones", "The heaviest stone is always on top.", T.heap("heap (max on top)", heap));
        while (heap.size() > 1) {
            int y = heap.poll(), x = heap.poll();
            if (y != x) heap.offer(y - x);
            T.s("Smash " + y + " and " + x, y == x ? "Equal weights: both are destroyed." : y + " − " + x + " = " + (y - x) + " goes back into the heap.", T.heap("heap (max on top)", heap), T.v("y", y, "x", x));
        }
        return heap.isEmpty() ? 0 : heap.peek();
    }
    //@end

    static S ptHeap(PriorityQueue<int[]> pq) {
        S s = new S("heap", "heap (farthest on top)");
        List<Object> v = new ArrayList<>();
        for (Object o : pq.toArray()) { int[] p = (int[]) o; v.add("(" + p[0] + "," + p[1] + ") d²=" + (p[0] * p[0] + p[1] * p[1])); }
        s.put("values", v);
        return s;
    }

    //@code kClosest
    static int[][] kClosest(int[][] points, int k) {
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(b[0] * b[0] + b[1] * b[1], a[0] * a[0] + a[1] * a[1]));
        for (int i = 0; i < points.length; i++) {
            heap.offer(points[i]);
            int[] far = heap.size() > k ? heap.peek() : null; // @hide
            if (heap.size() > k) heap.poll();
            T.s(far != null ? "Drop the farthest" : "Add point " + i, far != null ? "(" + far[0] + ", " + far[1] + ") has the largest squared distance, " + (far[0] * far[0] + far[1] * far[1]) + "." : "The heap holds at most " + k + " points.",
                T.arr("points", points).p("i", i).m(i, "active"), ptHeap(heap));
        }
        int[][] res = new int[k][];
        for (int i = 0; i < k; i++) res[i] = heap.poll();
        return res;
    }
    //@end

    //@code leastInterval
    static int leastInterval(char[] tasks, int n) {
        int[] count = new int[26];
        for (char t : tasks) count[t - 'A']++;
        int maxFreq = 0;
        for (int c : count) maxFreq = Math.max(maxFreq, c);
        int countMax = 0;
        for (int c : count) if (c == maxFreq) countMax++;
        T.s("Count the tasks", "The most frequent task appears " + maxFreq + " times; " + countMax + " task type(s) share that count.", T.arr("tasks", tasks), letterCounts(count), T.v("maxFreq", maxFreq, "countMax", countMax, "n", n));
        int frames = (maxFreq - 1) * (n + 1) + countMax;
        T.s("Build the frames", "(" + maxFreq + " − 1) × (" + n + " + 1) + " + countMax + " = " + frames + " slots, including idles.", T.arr("schedule", layout(count, n)), T.v("frames", frames));
        int result = Math.max(tasks.length, frames);
        T.s("Answer", frames >= tasks.length ? "All tasks fit in the frames; idle slots are needed." : "There are more tasks than slots, so no idling is needed.", T.v("tasks", tasks.length, "frames", frames, "result", result));
        return result;
    }
    //@end
    static S letterCounts(int[] c) { TreeMap<Character, Integer> m = new TreeMap<>(); for (int i = 0; i < 26; i++) if (c[i] > 0) m.put((char) ('A' + i), c[i]); return T.map("count", m); }
    static List<String> layout(int[] count, int n) {
        int[] c = count.clone(); List<String> out = new ArrayList<>();
        while (true) {
            Integer[] idx = new Integer[26]; for (int i = 0; i < 26; i++) idx[i] = i;
            Arrays.sort(idx, (a, b) -> c[b] - c[a]);
            if (c[idx[0]] == 0) break;
            List<String> frame = new ArrayList<>();
            for (int j = 0; j <= n; j++) { if (c[idx[j % 26]] > 0 && j < 26) { frame.add(String.valueOf((char) ('A' + idx[j]))); c[idx[j]]--; } else frame.add("idle"); }
            boolean any = false; for (int x : c) if (x > 0) any = true;
            if (!any) { while (!frame.isEmpty() && frame.get(frame.size() - 1).equals("idle")) frame.remove(frame.size() - 1); }
            out.addAll(frame);
            if (!any) break;
        }
        return out;
    }

    static S wordHeap(PriorityQueue<String> pq, Map<String, Integer> count) {
        S s = new S("heap", "heap (evict first on top)");
        List<Object> v = new ArrayList<>();
        for (Object o : pq.toArray()) v.add(o + ":" + count.get(o));
        s.put("values", v);
        return s;
    }

    //@code topKFrequentWords
    static List<String> topKFrequent(String[] words, int k) {
        Map<String, Integer> count = new HashMap<>();
        for (String w : words) count.merge(w, 1, Integer::sum);
        T.s("Count the words", count.size() + " distinct words.", T.map("count", new TreeMap<>(count)));
        PriorityQueue<String> heap = new PriorityQueue<>((a, b) -> count.get(a).equals(count.get(b)) ? b.compareTo(a) : count.get(a) - count.get(b));
        for (String w : count.keySet()) {
            heap.offer(w);
            if (heap.size() > k) {
                T.n("Evict the weakest", "\"" + heap.peek() + "\" has the lowest count (or loses the alphabetical tie-break).", wordHeap(heap, count).m(0, "error"));
                heap.poll();
            }
            T.s("Offer \"" + w + "\"", "The heap keeps the best " + k + " words seen so far.", wordHeap(heap, count).m(0, "compare"));
        }
        LinkedList<String> res = new LinkedList<>();
        while (!heap.isEmpty()) res.addFirst(heap.poll());
        T.s("Pop and reverse", "Popping gives worst first, so add each word to the front.", T.arr("res", res).range(0, res.size() - 1, "found"));
        return res;
    }
    //@end
    static List<String> topKFrequentWords(String[] w, int k) { return topKFrequent(w, k); }

    static S chHeap(PriorityQueue<int[]> pq) {
        S s = new S("heap", "heap (most left on top)");
        List<Object> v = new ArrayList<>();
        for (Object o : pq.toArray()) { int[] p = (int[]) o; v.add((char) ('a' + p[0]) + ":" + p[1]); }
        s.put("values", v);
        return s;
    }

    //@code reorganizeString
    static String reorganizeString(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) count[c - 'a']++;
        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> b[1] - a[1]);
        for (int i = 0; i < 26; i++) {
            if (count[i] > (s.length() + 1) / 2) {
                T.n("Impossible", "'" + (char) ('a' + i) + "' appears " + count[i] + " times, more than (" + s.length() + " + 1) / 2 = " + ((s.length() + 1) / 2) + ".", T.str("s", s));
                return "";
            }
            if (count[i] > 0) heap.offer(new int[]{i, count[i]});
        }
        StringBuilder sb = new StringBuilder();
        T.s("Max-heap by count", "Always place the letters with the most copies left.", chHeap(heap), T.str("result", ""));
        while (heap.size() >= 2) {
            int[] a = heap.poll(), b = heap.poll();
            sb.append((char) ('a' + a[0])).append((char) ('a' + b[0]));
            if (--a[1] > 0) heap.offer(a);
            if (--b[1] > 0) heap.offer(b);
            T.s("Place the top two", "Append '" + (char) ('a' + a[0]) + "' then '" + (char) ('a' + b[0]) + "'. They're different, so no neighbours match.", chHeap(heap), T.str("result", sb.toString()).range(sb.length() - 2, sb.length() - 1, "swap"));
        }
        if (!heap.isEmpty()) {
            sb.append((char) ('a' + heap.poll()[0]));
            T.s("Place the last letter", "Only one letter type is left, with one copy.", T.str("result", sb.toString()).m(sb.length() - 1, "swap"));
        }
        return sb.toString();
    }
    //@end

    //@code MedianFinder
    static class MedianFinder {
        private final PriorityQueue<Integer> low = new PriorityQueue<>(Collections.reverseOrder());
        private final PriorityQueue<Integer> high = new PriorityQueue<>();

        void addNum(int num) {
            low.offer(num);
            high.offer(low.poll());
            if (high.size() > low.size()) low.offer(high.poll());
            T.s("addNum(" + num + ")", "Push into `low`, move its largest to `high`, then rebalance so `low` has the extra element. Sizes: " + low.size() + " and " + high.size() + ".",
                T.heap("low (max-heap, smaller half)", low).m(0, "active"), T.heap("high (min-heap, larger half)", high).m(0, "active"));
        }

        double findMedian() {
            double m = low.size() > high.size() ? low.peek() : (low.peek() + high.peek()) / 2.0;
            T.s("findMedian()", low.size() > high.size() ? "Odd count: the median is the top of `low`, " + low.peek() + "." : "Even count: average " + low.peek() + " and " + high.peek() + " = " + m + ".",
                T.heap("low (max-heap, smaller half)", low).m(0, "found"), T.heap("high (min-heap, larger half)", high).m(0, low.size() > high.size() ? "dim" : "found"));
            return m;
        }
    }
    //@end
}
