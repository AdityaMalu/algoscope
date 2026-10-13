import java.util.*;

/** Amazon-tagged: Design-style coding */
class Design {
    static void run() {
        P.begin("Insert Delete GetRandom O(1)", "RandomizedSet");
        P.category("Design · Array + hash map");
        P.summary("Support insert, remove and getRandom in average O(1). Keep values in an ArrayList (for random access) and a map from value to its index. To remove, move the last element into the removed slot and pop the end, so the list never has holes.");
        P.cx("O(1)", "O(1)", "O(1)", "O(n)", "Average O(1) because of hashing.");
        P.ideas("An array gives O(1) random picks; a map gives O(1) lookups.",
            "Removing from the middle is O(n), so swap with the last element and remove the end instead.",
            "Update the moved element's index in the map.");
        P.pits("Forgetting to update the moved element's index.",
            "Removing by index with `list.remove(int)` from the middle is O(n).",
            "Using a HashSet alone: there's no O(1) random pick.");
        P.trace("insert(1), remove(2), insert(2), getRandom(), remove(1), insert(2), getRandom()", () -> {
            RandomizedSet r = new RandomizedSet(); List<Object> out = new ArrayList<>(); out.add(null);
            out.add(r.insert(1)); out.add(r.remove(2)); out.add(r.insert(2)); out.add(r.getRandom()); out.add(r.remove(1)); out.add(r.insert(2)); out.add(r.getRandom());
            return out;
        });
        P.edge("Remove the last element", "insert(5), insert(6), remove(6)", "[null, true, true, true]", () -> {
            RandomizedSet r = new RandomizedSet(); List<Object> out = new ArrayList<>(); out.add(null); out.add(r.insert(5)); out.add(r.insert(6)); out.add(r.remove(6)); return out;
        }, "The swap is with itself.");
        P.edge("Duplicate insert", "insert(3), insert(3)", "[null, true, false]", () -> {
            RandomizedSet r = new RandomizedSet(); List<Object> out = new ArrayList<>(); out.add(null); out.add(r.insert(3)); out.add(r.insert(3)); return out;
        }, "Already present.");
        P.edge("Remove missing", "remove(9)", "[null, false]", () -> {
            RandomizedSet r = new RandomizedSet(); List<Object> out = new ArrayList<>(); out.add(null); out.add(r.remove(9)); return out;
        }, "Nothing to remove.");
        P.done();

        P.begin("Time Based Key-Value Store", "TimeMap");
        P.category("Design · Binary search on timestamps");
        P.summary("Store values per key with timestamps; get(key, t) returns the value with the largest timestamp ≤ t. Timestamps arrive in increasing order, so each key's list is already sorted: binary search it.");
        P.cx("O(1)", "O(log n)", "O(log n)", "O(n)", "set is O(1) (append); get is a binary search.");
        P.ideas("Map each key to a list of (timestamp, value) pairs.",
            "Strictly increasing timestamps keep each list sorted for free.",
            "get = find the last timestamp ≤ t (an upper-bound search, minus one).",
            "TreeMap.floorEntry is a one-line alternative.");
        P.pits("Returning a value with a timestamp greater than t.",
            "Linear scans make get O(n).");
        P.trace("set(foo, bar, 1), get(foo, 1), get(foo, 3), set(foo, bar2, 4), get(foo, 4), get(foo, 5)", () -> {
            TimeMap m = new TimeMap(); List<Object> out = new ArrayList<>(); out.add(null);
            m.set("foo", "bar", 1); out.add(null); out.add(m.get("foo", 1)); out.add(m.get("foo", 3)); m.set("foo", "bar2", 4); out.add(null); out.add(m.get("foo", 4)); out.add(m.get("foo", 5));
            return out;
        });
        P.edge("Before any timestamp", "set(a, x, 5), get(a, 2)", "[null, null, \"\"]", () -> {
            TimeMap m = new TimeMap(); List<Object> out = new ArrayList<>(); out.add(null); m.set("a", "x", 5); out.add(null); out.add(m.get("a", 2)); return out;
        }, "No timestamp ≤ 2.");
        P.edge("Unknown key", "get(zzz, 1)", "[null, \"\"]", () -> {
            TimeMap m = new TimeMap(); List<Object> out = new ArrayList<>(); out.add(null); out.add(m.get("zzz", 1)); return out;
        }, "Return the empty string.");
        P.done();

        P.begin("Design Underground System", "UndergroundSystem");
        P.category("Design · Two hash maps");
        P.summary("Track check-ins and check-outs, and report average travel time between two stations. One map holds each traveller's open check-in (station, time). On check-out, add the trip to a route map keyed \"from→to\" holding total time and trip count.");
        P.cx("O(1)", "O(1)", "O(1)", "O(P + S²)", "P passengers in transit, S stations.");
        P.ideas("Check-in data is only needed until that person checks out; remove it then.",
            "Store totals and counts, not individual trips, so the average is O(1).",
            "Routes are directional: A→B differs from B→A.");
        P.pits("Storing every trip and averaging on demand is O(trips) per query.",
            "Integer division when averaging.");
        P.trace("checkIn(45, Leyton, 3), checkIn(32, Paradise, 8), checkOut(45, Waterloo, 15), checkOut(32, Cambridge, 22), getAverageTime(Paradise, Cambridge), checkIn(10, Leyton, 24), checkOut(10, Waterloo, 38), getAverageTime(Leyton, Waterloo)", () -> {
            UndergroundSystem u = new UndergroundSystem(); List<Object> out = new ArrayList<>(); out.add(null);
            u.checkIn(45, "Leyton", 3); out.add(null); u.checkIn(32, "Paradise", 8); out.add(null); u.checkOut(45, "Waterloo", 15); out.add(null); u.checkOut(32, "Cambridge", 22); out.add(null);
            out.add(u.getAverageTime("Paradise", "Cambridge")); u.checkIn(10, "Leyton", 24); out.add(null); u.checkOut(10, "Waterloo", 38); out.add(null); out.add(u.getAverageTime("Leyton", "Waterloo"));
            return out;
        });
        P.edge("Non-integer average", "checkIn(1, A, 0), checkOut(1, B, 3), checkIn(2, A, 0), checkOut(2, B, 4), getAverageTime(A, B)", "[null, null, null, null, null, 3.5]", () -> {
            UndergroundSystem u = new UndergroundSystem(); List<Object> out = new ArrayList<>(); out.add(null);
            u.checkIn(1, "A", 0); out.add(null); u.checkOut(1, "B", 3); out.add(null); u.checkIn(2, "A", 0); out.add(null); u.checkOut(2, "B", 4); out.add(null); out.add(u.getAverageTime("A", "B")); return out;
        }, "(3 + 4) / 2.0.");
        P.edge("Same person, two trips", "checkIn(7, A, 1), checkOut(7, B, 5), checkIn(7, B, 6), checkOut(7, A, 10), getAverageTime(B, A)", "[null, null, null, null, null, 4.0]", () -> {
            UndergroundSystem u = new UndergroundSystem(); List<Object> out = new ArrayList<>(); out.add(null);
            u.checkIn(7, "A", 1); out.add(null); u.checkOut(7, "B", 5); out.add(null); u.checkIn(7, "B", 6); out.add(null); u.checkOut(7, "A", 10); out.add(null); out.add(u.getAverageTime("B", "A")); return out;
        }, "The check-in record is cleared after each trip, and B→A is its own route.");
        P.done();

        P.begin("Snapshot Array", "SnapshotArray");
        P.category("Design · Versioned values + binary search");
        P.summary("An array that can take snapshots and read any index as of any snapshot. Copying the array on every snap is too slow. Instead, each index keeps a list of (snapId, value) changes; get binary-searches for the last change made at or before the requested snapshot.");
        P.cx("O(1)", "O(log s)", "O(log s)", "O(n + sets)", "set and snap are O(1); get is a binary search over that index's history.");
        P.ideas("Only store values that changed, tagged with the current snapshot id.",
            "Several sets between snaps overwrite the same (snapId, value) entry.",
            "get(index, id) = last entry with snapId ≤ id.");
        P.pits("Copying the whole array on snap: O(n) per snap.",
            "Off-by-one: snap returns the id of the snapshot just taken, then the counter increments.");
        P.trace("SnapshotArray(3): set(0, 5), snap(), set(0, 6), get(0, 0), snap(), get(0, 1), get(2, 1)", () -> {
            SnapshotArray a = new SnapshotArray(3); List<Object> out = new ArrayList<>(); out.add(null);
            a.set(0, 5); out.add(null); out.add(a.snap()); a.set(0, 6); out.add(null); out.add(a.get(0, 0)); out.add(a.snap()); out.add(a.get(0, 1)); out.add(a.get(2, 1));
            return out;
        });
        P.edge("Several sets before a snap", "SnapshotArray(1): set(0, 1), set(0, 2), snap(), get(0, 0)", "[null, null, null, 0, 2]", () -> {
            SnapshotArray a = new SnapshotArray(1); List<Object> out = new ArrayList<>(); out.add(null); a.set(0, 1); out.add(null); a.set(0, 2); out.add(null); out.add(a.snap()); out.add(a.get(0, 0)); return out;
        }, "The last value before the snap wins.");
        P.edge("Never set", "SnapshotArray(2): snap(), get(1, 0)", "[null, 0, 0]", () -> {
            SnapshotArray a = new SnapshotArray(2); List<Object> out = new ArrayList<>(); out.add(null); out.add(a.snap()); out.add(a.get(1, 0)); return out;
        }, "Untouched indices read as 0.");
        P.done();

        P.begin("Design Twitter", "Twitter");
        P.category("Design · K-way merge with a heap");
        P.summary("Post tweets, follow and unfollow, and get the 10 most recent tweets from yourself and the people you follow. Each user keeps their own tweets, newest last, with a global timestamp. The news feed merges those lists with a max-heap by time and stops after 10.");
        P.cx("O(1)", "O(F + 10 log F)", "O(F + 10 log F)", "O(users + tweets)", "F = number of followees. post, follow and unfollow are O(1).");
        P.ideas("A global counter orders tweets across users.",
            "The feed is a merge of sorted lists: push each user's newest tweet, pop the newest overall, push that user's next tweet.",
            "Users always see their own tweets: include yourself in the merge.",
            "Unfollowing yourself should do nothing.");
        P.pits("Collecting every tweet and sorting is O(T log T) per feed.",
            "Allowing a user to unfollow themselves.");
        P.trace("postTweet(1, 5), getNewsFeed(1), follow(1, 2), postTweet(2, 6), getNewsFeed(1), unfollow(1, 2), getNewsFeed(1)", () -> {
            Twitter t = new Twitter(); List<Object> out = new ArrayList<>(); out.add(null);
            t.postTweet(1, 5); out.add(null); out.add(t.getNewsFeed(1)); t.follow(1, 2); out.add(null); t.postTweet(2, 6); out.add(null); out.add(t.getNewsFeed(1)); t.unfollow(1, 2); out.add(null); out.add(t.getNewsFeed(1));
            return out;
        });
        P.edge("Unfollow yourself", "postTweet(1, 7), unfollow(1, 1), getNewsFeed(1)", "[null, null, null, [7]]", () -> {
            Twitter t = new Twitter(); List<Object> out = new ArrayList<>(); out.add(null); t.postTweet(1, 7); out.add(null); t.unfollow(1, 1); out.add(null); out.add(t.getNewsFeed(1)); return out;
        }, "You always see your own tweets.");
        P.edge("More than 10 tweets", "postTweet(1, 0) … postTweet(1, 11), getNewsFeed(1)", "[11, 10, 9, 8, 7, 6, 5, 4, 3, 2]", () -> {
            Twitter t = new Twitter(); for (int i = 0; i <= 11; i++) t.postTweet(1, i); return t.getNewsFeed(1);
        }, "Only the 10 most recent.");
        P.done();

        P.begin("Maximum Frequency Stack", "FreqStack");
        P.category("Design · Stack per frequency");
        P.summary("pop removes the most frequent element, breaking ties by recency. Keep each value's frequency and a stack for every frequency level. Pushing x with new frequency f puts it on stack f; popping takes from the highest non-empty level.");
        P.cx("O(1)", "O(1)", "O(1)", "O(n)", "Both operations are O(1).");
        P.ideas("An element with frequency 3 sits on stacks 1, 2 and 3: one copy per level it reached.",
            "The top of the highest stack is the most frequent and, among ties, the most recent.",
            "After popping, decrease its frequency; if that stack empties, lower `maxFreq`.");
        P.pits("A heap of (freq, time) works but is O(log n).",
            "Forgetting to lower maxFreq when its stack becomes empty.");
        P.trace("push(5), push(7), push(5), push(7), push(4), push(5), pop(), pop(), pop(), pop()", () -> {
            FreqStack f = new FreqStack(); List<Object> out = new ArrayList<>(); out.add(null);
            for (int x : new int[]{5, 7, 5, 7, 4, 5}) { f.push(x); out.add(null); }
            for (int i = 0; i < 4; i++) out.add(f.pop());
            return out;
        });
        P.edge("All different", "push(1), push(2), push(3), pop(), pop()", "[null, null, null, null, 3, 2]", () -> {
            FreqStack f = new FreqStack(); List<Object> out = new ArrayList<>(); out.add(null); f.push(1); out.add(null); f.push(2); out.add(null); f.push(3); out.add(null); out.add(f.pop()); out.add(f.pop()); return out;
        }, "All frequency 1: behaves like a normal stack.");
        P.edge("Single value", "push(9), push(9), pop(), pop()", "[null, null, null, 9, 9]", () -> {
            FreqStack f = new FreqStack(); List<Object> out = new ArrayList<>(); out.add(null); f.push(9); out.add(null); f.push(9); out.add(null); out.add(f.pop()); out.add(f.pop()); return out;
        }, "maxFreq drops from 2 to 1 between pops.");
        P.done();
    }

    //@code RandomizedSet
    static class RandomizedSet {
        private final List<Integer> list = new ArrayList<>();
        private final Map<Integer, Integer> index = new HashMap<>();
        private final Random rand = new Random(42);

        boolean insert(int val) {
            if (index.containsKey(val)) {
                T.n("insert(" + val + "): already there", "Return false.", T.arr("list", list).m(index.get(val), "compare"), T.map("index", index).m(val, "compare"));
                return false;
            }
            index.put(val, list.size());
            list.add(val);
            T.s("insert(" + val + ")", "Append it and record its index " + (list.size() - 1) + ".", T.arr("list", list).m(list.size() - 1, "swap"), T.map("index", index).m(val, "swap"));
            return true;
        }

        boolean remove(int val) {
            Integer i = index.get(val);
            if (i == null) {
                T.n("remove(" + val + "): not found", "Return false.", T.arr("list", list), T.map("index", index));
                return false;
            }
            int last = list.get(list.size() - 1);
            list.set(i, last);
            index.put(last, i);
            list.remove(list.size() - 1);
            index.remove(val);
            T.s("remove(" + val + ")", "Move the last value (" + last + ") into slot " + i + ", fix its index, and drop the end.", T.arr("list", list).m(i, "swap"), T.map("index", index).m(last, "swap"));
            return true;
        }

        int getRandom() {
            int pick = list.get(rand.nextInt(list.size()));
            T.n("getRandom()", "Pick a random index; here it returned " + pick + ".", T.arr("list", list).m(list.indexOf(pick), "found"), T.map("index", index));
            return pick;
        }
    }
    //@end

    //@code TimeMap
    static class TimeMap {
        private final Map<String, List<Integer>> times = new HashMap<>();
        private final Map<String, List<String>> values = new HashMap<>();

        void set(String key, String value, int timestamp) {
            times.computeIfAbsent(key, k -> new ArrayList<>()).add(timestamp);
            values.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
            T.s("set(" + key + ", " + value + ", " + timestamp + ")", "Append to the key's history; timestamps stay sorted.", T.arr(key + " timestamps", times.get(key)).m(times.get(key).size() - 1, "swap"), T.arr(key + " values", values.get(key)).m(values.get(key).size() - 1, "swap"));
        }

        String get(String key, int timestamp) {
            List<Integer> ts = times.get(key);
            if (ts == null) {
                T.n("get(" + key + ", " + timestamp + ")", "Unknown key: return \"\".", T.v("key", key));
                return "";
            }
            int lo = 0, hi = ts.size();
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (ts.get(mid) <= timestamp) lo = mid + 1;
                else hi = mid;
            }
            String result = lo == 0 ? "" : values.get(key).get(lo - 1);
            T.s("get(" + key + ", " + timestamp + ")", lo == 0 ? "Every stored timestamp is after " + timestamp + ": return \"\"." : "The last timestamp ≤ " + timestamp + " is " + ts.get(lo - 1) + ": \"" + result + "\".", T.arr(key + " timestamps", ts).m(lo - 1, "found"), T.arr(key + " values", values.get(key)).m(lo - 1, "found"));
            return result;
        }
    }
    //@end

    //@code UndergroundSystem
    static class UndergroundSystem {
        private final Map<Integer, String> inStation = new HashMap<>();
        private final Map<Integer, Integer> inTime = new HashMap<>();
        private final Map<String, double[]> routes = new HashMap<>();

        void checkIn(int id, String station, int t) {
            inStation.put(id, station);
            inTime.put(id, t);
            T.s("checkIn(" + id + ", " + station + ", " + t + ")", "Remember where and when passenger " + id + " started.", T.map("open trips", open()), T.map("routes [total, count]", routeView()));
        }

        void checkOut(int id, String station, int t) {
            String key = inStation.remove(id) + "→" + station;
            int start = inTime.remove(id);
            double[] r = routes.computeIfAbsent(key, k -> new double[2]);
            r[0] += t - start;
            r[1]++;
            T.s("checkOut(" + id + ", " + station + ", " + t + ")", "Trip " + key + " took " + (t - start) + ". Add it to the route's total and count.", T.map("open trips", open()), T.map("routes [total, count]", routeView()).m(key, "swap"));
        }

        double getAverageTime(String from, String to) {
            double[] r = routes.get(from + "→" + to);
            T.n("getAverageTime(" + from + ", " + to + ")", r[0] + " / " + (int) r[1] + " = " + (r[0] / r[1]) + ".", T.map("routes [total, count]", routeView()).m(from + "→" + to, "found"));
            return r[0] / r[1];
        }
        private Map<Integer, String> open() { Map<Integer, String> m = new TreeMap<>(); for (int k : inStation.keySet()) m.put(k, inStation.get(k) + " @" + inTime.get(k)); return m; } // @hide
        private Map<String, String> routeView() { Map<String, String> m = new TreeMap<>(); for (Map.Entry<String, double[]> e : routes.entrySet()) m.put(e.getKey(), "[" + (int) e.getValue()[0] + ", " + (int) e.getValue()[1] + "]"); return m; } // @hide
    }
    //@end

    //@code SnapshotArray
    static class SnapshotArray {
        private final List<int[]>[] history;
        private int snapId = 0;

        @SuppressWarnings("unchecked") // @hide
        SnapshotArray(int length) {
            history = new List[length];
            for (int i = 0; i < length; i++) {
                history[i] = new ArrayList<>();
                history[i].add(new int[]{0, 0});
            }
        }

        void set(int index, int val) {
            List<int[]> h = history[index];
            if (h.get(h.size() - 1)[0] == snapId) h.get(h.size() - 1)[1] = val;
            else h.add(new int[]{snapId, val});
            T.s("set(" + index + ", " + val + ")", "Record value " + val + " for snapshot " + snapId + " at index " + index + ".", view().m(index, "swap"), T.v("snapId", snapId));
        }

        int snap() {
            T.n("snap()", "Snapshot " + snapId + " is taken; later sets belong to snapshot " + (snapId + 1) + ".", view(), T.v("snapId", snapId));
            return snapId++;
        }

        int get(int index, int id) {
            List<int[]> h = history[index];
            int lo = 0, hi = h.size() - 1;
            while (lo < hi) {
                int mid = (lo + hi + 1) / 2;
                if (h.get(mid)[0] <= id) lo = mid;
                else hi = mid - 1;
            }
            T.s("get(" + index + ", " + id + ")", "The last change at index " + index + " made in snapshot ≤ " + id + " has value " + h.get(lo)[1] + ".", view().m(index, "found"), T.v("snapId", snapId));
            return h.get(lo)[1];
        }
        private S view() { List<String> v = new ArrayList<>(); for (List<int[]> h : history) { StringJoiner j = new StringJoiner(" "); for (int[] e : h) j.add("s" + e[0] + ":" + e[1]); v.add(j.toString()); } return T.arr("history per index", v); } // @hide
    }
    //@end

    //@code Twitter
    static class Twitter {
        private int time = 0;
        private final Map<Integer, List<int[]>> tweets = new HashMap<>();
        private final Map<Integer, Set<Integer>> follows = new HashMap<>();

        void postTweet(int userId, int tweetId) {
            tweets.computeIfAbsent(userId, k -> new ArrayList<>()).add(new int[]{time++, tweetId});
            T.s("postTweet(" + userId + ", " + tweetId + ")", "Stamp it with time " + (time - 1) + ".", T.map("tweets [time:id]", tweetView()), T.map("follows", follows));
        }

        List<Integer> getNewsFeed(int userId) {
            PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> b[0] - a[0]);
            Set<Integer> users = new HashSet<>(follows.getOrDefault(userId, Set.of()));
            users.add(userId);
            for (int u : users) {
                List<int[]> list = tweets.get(u);
                if (list != null && !list.isEmpty()) pq.offer(new int[]{list.get(list.size() - 1)[0], list.get(list.size() - 1)[1], u, list.size() - 1});
            }
            List<Integer> feed = new ArrayList<>();
            while (!pq.isEmpty() && feed.size() < 10) {
                int[] top = pq.poll();
                feed.add(top[1]);
                if (top[3] > 0) {
                    int[] prev = tweets.get(top[2]).get(top[3] - 1);
                    pq.offer(new int[]{prev[0], prev[1], top[2], top[3] - 1});
                }
            }
            T.s("getNewsFeed(" + userId + ")", "Merge the tweet lists of " + new TreeSet<>(users) + " newest first, stopping at 10.", T.map("tweets [time:id]", tweetView()), T.arr("feed", feed).range(0, feed.size() - 1, "found"));
            return feed;
        }

        void follow(int a, int b) {
            if (a != b) follows.computeIfAbsent(a, k -> new HashSet<>()).add(b);
            T.s("follow(" + a + ", " + b + ")", a + " now sees " + b + "'s tweets.", T.map("follows", follows));
        }

        void unfollow(int a, int b) {
            if (follows.containsKey(a)) follows.get(a).remove(b);
            T.s("unfollow(" + a + ", " + b + ")", a == b ? "You can't unfollow yourself; nothing changes." : a + " stops seeing " + b + "'s tweets.", T.map("follows", follows));
        }
        private Map<Integer, List<String>> tweetView() { Map<Integer, List<String>> m = new TreeMap<>(); for (Map.Entry<Integer, List<int[]>> e : tweets.entrySet()) { List<String> l = new ArrayList<>(); for (int[] t : e.getValue()) l.add(t[0] + ":" + t[1]); m.put(e.getKey(), l); } return m; } // @hide
    }
    //@end

    //@code FreqStack
    static class FreqStack {
        private final Map<Integer, Integer> freq = new HashMap<>();
        private final Map<Integer, Deque<Integer>> group = new HashMap<>();
        private int maxFreq = 0;

        void push(int x) {
            int f = freq.merge(x, 1, Integer::sum);
            maxFreq = Math.max(maxFreq, f);
            group.computeIfAbsent(f, k -> new ArrayDeque<>()).push(x);
            T.s("push(" + x + ")", x + " now has frequency " + f + "; push it onto stack " + f + ".", T.map("freq", freq).m(x, "swap"), groups(), T.v("maxFreq", maxFreq));
        }

        int pop() {
            int x = group.get(maxFreq).pop();
            freq.merge(x, -1, Integer::sum);
            if (group.get(maxFreq).isEmpty()) maxFreq--;
            T.s("pop() → " + x, x + " was on top of the highest-frequency stack. maxFreq is now " + maxFreq + ".", T.map("freq", freq).m(x, "swap"), groups(), T.v("maxFreq", maxFreq));
            return x;
        }
        private S groups() { Map<Integer, List<Integer>> m = new TreeMap<>(); for (Map.Entry<Integer, Deque<Integer>> e : group.entrySet()) { List<Integer> l = new ArrayList<>(e.getValue()); Collections.reverse(l); m.put(e.getKey(), l); } return T.map("stack per frequency (top last)", m); } // @hide
    }
    //@end
}
