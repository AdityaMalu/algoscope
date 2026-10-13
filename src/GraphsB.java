import java.util.*;

/** Graphs: Topological Sort & Union-Find */
class GraphsB {
    static S dg(String name, int n, int[][] edges, boolean directed) {
        S s = T.graph(name, directed);
        for (int i = 0; i < n; i++) s.node(i, i);
        for (int[] e : edges) s.edge(e[0], e[1], null);
        return s;
    }
    /** prerequisites [a, b] mean b → a */
    static int[][] flip(int[][] pre) { int[][] r = new int[pre.length][]; for (int i = 0; i < pre.length; i++) r[i] = new int[]{pre[i][1], pre[i][0]}; return r; }

    static void run() {
        P.begin("Course Schedule", "canFinish");
        P.category("Graphs · Kahn's topological sort");
        P.summary("Courses are nodes and prerequisites are edges; you can finish them all only if the graph has no cycle. Kahn's algorithm repeatedly takes a course with no remaining prerequisites. If every course gets taken, there's no cycle.");
        P.cx("O(V + E)", "O(V + E)", "O(V + E)", "O(V + E)", "Each course and prerequisite is processed once.");
        P.ideas("In-degree = number of unfinished prerequisites.",
            "Start with every course whose in-degree is 0.",
            "Taking a course lowers its dependants' in-degrees; any that hit 0 join the queue.",
            "If a cycle exists, its courses never reach in-degree 0.");
        P.pits("Getting the edge direction backwards: [a, b] means b must come before a.",
            "DFS cycle detection needs three colours (unvisited, in progress, done); two aren't enough.");
        P.trace("numCourses = 4, prerequisites = [[1, 0], [2, 0], [3, 1], [3, 2]]", () -> canFinish(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        P.edge("Simple cycle", "numCourses = 2, prerequisites = [[1, 0], [0, 1]]", "false", () -> canFinish(2, new int[][]{{1, 0}, {0, 1}}), "Each course waits for the other.");
        P.edge("No prerequisites", "numCourses = 3, prerequisites = []", "true", () -> canFinish(3, new int[][]{}), "Every course starts with in-degree 0.");
        P.edge("Self loop", "numCourses = 1, prerequisites = [[0, 0]]", "false", () -> canFinish(1, new int[][]{{0, 0}}), "A course that requires itself can never start.");
        P.edge("Cycle in one component", "numCourses = 4, prerequisites = [[1, 0], [2, 3], [3, 2]]", "false", () -> canFinish(4, new int[][]{{1, 0}, {2, 3}, {3, 2}}), "0 and 1 finish, but 2 and 3 are stuck.");
        P.done();

        P.begin("Number of Provinces", "findCircleNum");
        P.category("Graphs · Union-Find");
        P.summary("Count connected groups of cities from an adjacency matrix. Start with every city as its own group; union the two groups of every connected pair. Each successful union merges two groups, so the answer is n minus the number of successful unions.");
        P.cx("O(n² α(n))", "O(n² α(n))", "O(n² α(n))", "O(n)", "The matrix has n² entries; union-find operations are nearly O(1).");
        P.ideas("`parent[i]` points toward the representative (root) of i's group.",
            "find() follows parents to the root, compressing the path as it goes.",
            "union() links one root under the other if they differ.",
            "DFS over the matrix counting components works equally well.");
        P.pits("Forgetting path compression makes find() slow on long chains.",
            "Counting roots without calling find() on each node first.");
        P.trace("isConnected = [[1, 1, 0, 0], [1, 1, 0, 0], [0, 0, 1, 1], [0, 0, 1, 1]]", () -> findCircleNum(new int[][]{{1, 1, 0, 0}, {1, 1, 0, 0}, {0, 0, 1, 1}, {0, 0, 1, 1}}));
        P.edge("No connections", "isConnected = [[1, 0, 0], [0, 1, 0], [0, 0, 1]]", "3", () -> findCircleNum(new int[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}), "Every city is alone.");
        P.edge("All connected", "isConnected = [[1, 1, 1], [1, 1, 1], [1, 1, 1]]", "1", () -> findCircleNum(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 1}}), "Two unions merge everything.");
        P.edge("Chain", "isConnected = [[1, 1, 0], [1, 1, 1], [0, 1, 1]]", "1", () -> findCircleNum(new int[][]{{1, 1, 0}, {1, 1, 1}, {0, 1, 1}}), "0 and 2 are connected through 1.");
        P.edge("Single city", "isConnected = [[1]]", "1", () -> findCircleNum(new int[][]{{1}}), "One province.");
        P.done();

        P.begin("Find Eventual Safe States", "eventualSafeNodes");
        P.category("Graphs · Reverse topological sort");
        P.summary("A node is safe if every path from it ends at a terminal node (no outgoing edges). Reverse the edges and run Kahn's algorithm from the terminal nodes: a node becomes safe once all of its outgoing edges lead to safe nodes.");
        P.cx("O(V + E)", "O(V + E)", "O(V + E)", "O(V + E)", "Building the reverse graph and the BFS are both linear.");
        P.ideas("Terminal nodes are safe by definition.",
            "Count each node's outgoing edges (its out-degree).",
            "When a safe node is confirmed, every node pointing to it loses one unresolved edge.",
            "A node whose count hits 0 is safe; nodes on or leading into cycles never get there.");
        P.pits("Forgetting to sort the output.",
            "DFS with colouring also works; recursion depth can be an issue on long chains.");
        P.trace("graph = [[1, 2], [2, 3], [5], [0], [5], [], []]", () -> eventualSafeNodes(new int[][]{{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}}));
        P.edge("Everything is a cycle", "graph = [[1], [0]]", "[]", () -> eventualSafeNodes(new int[][]{{1}, {0}}), "No terminal nodes to start from.");
        P.edge("No edges", "graph = [[], [], []]", "[0, 1, 2]", () -> eventualSafeNodes(new int[][]{{}, {}, {}}), "Every node is terminal.");
        P.edge("Self loop", "graph = [[0], []]", "[1]", () -> eventualSafeNodes(new int[][]{{0}, {}}), "Node 0 loops on itself forever.");
        P.done();

        P.begin("Course Schedule II", "findOrder");
        P.category("Graphs · Kahn's topological sort");
        P.summary("Return an order to take all courses, or an empty array if impossible. Run Kahn's algorithm and record each course as it leaves the queue. If fewer than n courses were recorded, there's a cycle.");
        P.cx("O(V + E)", "O(V + E)", "O(V + E)", "O(V + E)", "Same as Course Schedule, plus the output array.");
        P.ideas("The order courses leave the queue is a valid topological order.",
            "Many valid orders can exist; any one is accepted.",
            "An incomplete order means some courses are stuck in a cycle.");
        P.pits("Returning a partial order when there's a cycle.",
            "Reversing the edge direction.");
        P.trace("numCourses = 4, prerequisites = [[1, 0], [2, 0], [3, 1], [3, 2]]", () -> findOrder(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}}));
        P.edge("Cycle", "numCourses = 2, prerequisites = [[0, 1], [1, 0]]", "[]", () -> findOrder(2, new int[][]{{0, 1}, {1, 0}}), "Neither course can start.");
        P.edge("Single course", "numCourses = 1, prerequisites = []", "[0]", () -> findOrder(1, new int[][]{}), "Take it.");
        P.edge("Chain", "numCourses = 3, prerequisites = [[1, 0], [2, 1]]", "[0, 1, 2]", () -> findOrder(3, new int[][]{{1, 0}, {2, 1}}), "Only one valid order.");
        P.done();

        P.begin("Redundant Connection", "findRedundantConnection");
        P.category("Graphs · Union-Find");
        P.summary("A tree had one extra edge added, creating exactly one cycle. Add edges one by one with union-find; the first edge whose two endpoints are already connected closes the cycle and is the answer.");
        P.cx("O(n α(n))", "O(n α(n))", "O(n α(n))", "O(n)", "One find/union per edge.");
        P.ideas("Two nodes already in the same set are connected by some path.",
            "Adding an edge between them creates a cycle.",
            "Processing edges in order returns the last such edge in the input, as required, because there's exactly one.");
        P.pits("Nodes are 1-indexed; size parent as n + 1.",
            "DFS for every edge is O(n²).");
        P.trace("edges = [[1, 2], [2, 3], [3, 4], [1, 4], [1, 5]]", () -> findRedundantConnection(new int[][]{{1, 2}, {2, 3}, {3, 4}, {1, 4}, {1, 5}}));
        P.edge("Triangle", "edges = [[1, 2], [1, 3], [2, 3]]", "[2, 3]", () -> findRedundantConnection(new int[][]{{1, 2}, {1, 3}, {2, 3}}), "The third edge closes the triangle.");
        P.edge("Cycle closed early", "edges = [[1, 2], [2, 1], [1, 3]]", "[2, 1]", () -> findRedundantConnection(new int[][]{{1, 2}, {2, 1}, {1, 3}}), "A duplicate edge is a cycle of length 2.");
        P.done();

        P.begin("Accounts Merge", "accountsMerge");
        P.category("Graphs · Union-Find on strings");
        P.summary("Merge accounts that share any email. Union every email in an account with the account's first email. Then group emails by their root, sort each group, and attach the owner's name.");
        P.cx("O(N log N)", "O(N log N)", "O(N log N)", "O(N)", "N total emails; sorting the groups dominates.");
        P.ideas("Emails are the nodes; being in the same account is an edge.",
            "Union-find with a HashMap<String, String> parent handles string keys.",
            "The same person can have two accounts with the same name that don't share emails; they stay separate.");
        P.pits("Merging by name: different people can share a name.",
            "Forgetting to sort emails within each merged account.");
        P.trace("accounts = [[John, a@x, b@x], [John, c@x], [John, b@x, d@x], [Mary, m@x]]", () -> accountsMerge(F.lists(new String[][]{{"John", "a@x", "b@x"}, {"John", "c@x"}, {"John", "b@x", "d@x"}, {"Mary", "m@x"}})));
        P.edge("Same name, different people", "accounts = [[Alex, a@x], [Alex, b@x]]", "[[\"Alex\", \"a@x\"], [\"Alex\", \"b@x\"]]", () -> accountsMerge(F.lists(new String[][]{{"Alex", "a@x"}, {"Alex", "b@x"}})), "No shared email, so they stay separate.");
        P.edge("Chain of shared emails", "accounts = [[K, a, b], [K, b, c], [K, c, d]]", "[[\"K\", \"a\", \"b\", \"c\", \"d\"]]", () -> accountsMerge(F.lists(new String[][]{{"K", "a", "b"}, {"K", "b", "c"}, {"K", "c", "d"}})), "Transitive links merge all three.");
        P.edge("Duplicate email in one account", "accounts = [[Z, z, z]]", "[[\"Z\", \"z\"]]", () -> accountsMerge(F.lists(new String[][]{{"Z", "z", "z"}})), "Each email appears once in the output.");
        P.done();

        P.begin("Word Ladder", "ladderLength");
        P.category("Graphs · BFS shortest path");
        P.summary("Find the fewest words to go from `beginWord` to `endWord`, changing one letter at a time, using only words in the list. Each word is a node; BFS from the start finds the shortest path. Neighbours come from trying all 26 letters at every position.");
        P.cx("O(N·L·26)", "O(N·L·26)", "O(N·L·26)", "O(N)", "N words of length L; each word generates L·26 candidates.");
        P.ideas("Shortest number of steps in an unweighted graph = BFS.",
            "Generate neighbours by mutating one letter instead of comparing every pair of words.",
            "Remove words from the set when queued so they're never visited twice.",
            "The answer counts words, so the start is level 1.");
        P.pits("Forgetting that endWord must be in the list.",
            "Comparing every pair of words to build the graph is O(N²·L).",
            "Off-by-one: the count includes both the first and last word.");
        P.trace("beginWord = hit, endWord = cog, wordList = [hot, dot, dog, lot, log, cog]", () -> ladderLength("hit", "cog", Arrays.asList("hot", "dot", "dog", "lot", "log", "cog")));
        P.edge("End word missing", "beginWord = hit, endWord = cog, wordList = [hot, dot, dog, lot, log]", "0", () -> ladderLength("hit", "cog", Arrays.asList("hot", "dot", "dog", "lot", "log")), "cog is not in the list.");
        P.edge("One step", "beginWord = a, endWord = c, wordList = [a, b, c]", "2", () -> ladderLength("a", "c", Arrays.asList("a", "b", "c")), "a → c directly.");
        P.edge("Disconnected", "beginWord = hot, endWord = dog, wordList = [dog]", "0", () -> ladderLength("hot", "dog", Arrays.asList("dog")), "hot and dog differ in two letters with nothing in between.");
        P.done();
    }

    //@code canFinish
    static boolean canFinish(int n, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        int[] indegree = new int[n];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) queue.offer(i);
        T.s("Courses ready to take", "In-degree counts unfinished prerequisites. Courses with 0 go in the queue.", dg("courses (b → a means b before a)", n, flip(prerequisites), true), T.arr("indegree", indegree), T.queue("queue", queue));
        int taken = 0;
        Set<Integer> done = new HashSet<>(); // @hide
        while (!queue.isEmpty()) {
            int c = queue.poll();
            taken++;
            done.add(c); // @hide
            for (int next : adj.get(c)) {
                if (--indegree[next] == 0) queue.offer(next);
            }
            T.s("Take course " + c, "Taken: " + taken + ". Each course that depended on " + c + " loses one prerequisite.", mark(dg("courses (b → a means b before a)", n, flip(prerequisites), true), done).m(c, "active"), T.arr("indegree", indegree), T.queue("queue", queue));
        }
        T.n(taken == n ? "All courses taken" : "Stuck in a cycle", taken == n ? "No cycle." : (n - taken) + " course(s) never reached in-degree 0.", mark(dg("courses (b → a means b before a)", n, flip(prerequisites), true), done), T.v("taken", taken, "n", n));
        return taken == n;
    }
    //@end
    static S mark(S s, Collection<Integer> done) { for (int d : done) s.m(d, "done"); return s; }

    static S uf(int[] parent, int from) {
        S s = T.graph("parent pointers", true);
        for (int i = from; i < parent.length; i++) s.node(i, i);
        for (int i = from; i < parent.length; i++) if (parent[i] != i) s.edge(i, parent[i], null);
        for (int i = from; i < parent.length; i++) if (parent[i] == i) s.m(i, "found");
        return s;
    }

    //@code findCircleNum
    static int[] parent;

    static int find(int x) {
        if (parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }

    static int findCircleNum(int[][] isConnected) {
        int n = isConnected.length, groups = n;
        parent = new int[n];
        for (int i = 0; i < n; i++) parent[i] = i;
        T.s("Every city is its own province", groups + " groups to start.", uf(parent, 0), T.mat("isConnected", isConnected), T.v("groups", groups));
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 0) continue;
                int a = find(i), b = find(j);
                if (a != b) {
                    parent[a] = b;
                    groups--;
                }
                T.s(a != b ? "Union " + i + " and " + j : "Already together", a != b ? "They were in different groups (roots " + a + " and " + b + "). Merge them: " + groups + " group(s) left." : i + " and " + j + " share root " + a + ".",
                    uf(parent, 0).m(i, a != b ? "active" : "visit").m(j, a != b ? "active" : "visit"), T.mat("isConnected", isConnected).m(i, j, a != b ? "active" : "visit"), T.v("groups", groups));
            }
        return groups;
    }
    //@end

    //@code eventualSafeNodes
    static List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> rev = new ArrayList<>();
        for (int i = 0; i < n; i++) rev.add(new ArrayList<>());
        int[] out = new int[n];
        for (int u = 0; u < n; u++) {
            out[u] = graph[u].length;
            for (int v : graph[u]) rev.get(v).add(u);
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (out[i] == 0) queue.offer(i);
        T.s("Terminal nodes are safe", "Nodes with no outgoing edges start the queue.", safeView(graph, new boolean[n]), T.arr("unresolved out-edges", out), T.queue("queue", queue));
        boolean[] safe = new boolean[n];
        while (!queue.isEmpty()) {
            int v = queue.poll();
            safe[v] = true;
            for (int u : rev.get(v)) {
                if (--out[u] == 0) queue.offer(u);
            }
            T.s("Node " + v + " is safe", "Every node pointing to " + v + " has one fewer unresolved edge.", safeView(graph, safe).m(v, "active"), T.arr("unresolved out-edges", out), T.queue("queue", queue));
        }
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++) if (safe[i]) res.add(i);
        return res;
    }
    //@end
    static S safeView(int[][] g, boolean[] safe) {
        S s = T.graph("graph", true);
        for (int i = 0; i < g.length; i++) s.node(i, i);
        for (int u = 0; u < g.length; u++) for (int v : g[u]) s.edge(u, v, null);
        for (int i = 0; i < g.length; i++) if (safe[i]) s.m(i, "done");
        return s;
    }

    //@code findOrder
    static int[] findOrder(int n, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) adj.add(new ArrayList<>());
        int[] indegree = new int[n];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indegree[i] == 0) queue.offer(i);
        int[] order = new int[n];
        int k = 0;
        T.s("Start with free courses", "Courses with no prerequisites can be taken first.", dg("courses", n, flip(prerequisites), true), T.arr("indegree", indegree), T.queue("queue", queue));
        while (!queue.isEmpty()) {
            int c = queue.poll();
            order[k++] = c;
            for (int next : adj.get(c)) {
                if (--indegree[next] == 0) queue.offer(next);
            }
            T.s("Take course " + c, "Append it to the order and unlock its dependants.", mark(dg("courses", n, flip(prerequisites), true), firstK(order, k)).m(c, "active"), T.arr("indegree", indegree), T.queue("queue", queue), T.arr("order", Arrays.copyOf(order, k)));
        }
        return k == n ? order : new int[0];
    }
    //@end
    static List<Integer> firstK(int[] a, int k) { List<Integer> l = new ArrayList<>(); for (int i = 0; i < k; i++) l.add(a[i]); return l; }

    //@code findRedundantConnection
    static int[] findRedundantConnection(int[][] edges) {
        int[] parent = new int[edges.length + 1];
        for (int i = 0; i < parent.length; i++) parent[i] = i;
        for (int[] e : edges) {
            int a = root(parent, e[0]), b = root(parent, e[1]);
            if (a == b) {
                T.n("Cycle!", e[0] + " and " + e[1] + " already share root " + a + ". This edge is redundant.", uf(parent, 1).m(e[0], "error").m(e[1], "error"), T.arr("edge", e));
                return e;
            }
            parent[a] = b;
            T.s("Add edge " + e[0] + "–" + e[1], "Different roots (" + a + ", " + b + "): link them.", uf(parent, 1).m(e[0], "active").m(e[1], "active"), T.arr("edge", e));
        }
        return new int[0];
    }

    static int root(int[] parent, int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
    //@end

    static S emailUf(Map<String, String> parent) {
        S s = T.graph("email parent pointers", true);
        for (String e : new TreeSet<>(parent.keySet())) s.node(e, e);
        for (Map.Entry<String, String> e : parent.entrySet()) if (!e.getKey().equals(e.getValue())) s.edge(e.getKey(), e.getValue(), null);
        for (Map.Entry<String, String> e : parent.entrySet()) if (e.getKey().equals(e.getValue())) s.m(e.getKey(), "found");
        return s;
    }

    //@code accountsMerge
    static Map<String, String> par = new HashMap<>();

    static String findEmail(String x) {
        if (!par.get(x).equals(x)) par.put(x, findEmail(par.get(x)));
        return par.get(x);
    }

    static List<List<String>> accountsMerge(List<List<String>> accounts) {
        par.clear();
        Map<String, String> owner = new HashMap<>();
        for (List<String> acc : accounts) {
            String first = acc.get(1);
            for (int i = 1; i < acc.size(); i++) {
                String email = acc.get(i);
                par.putIfAbsent(email, email);
                owner.put(email, acc.get(0));
                par.put(findEmail(email), findEmail(first));
            }
            T.s("Union " + acc.get(0) + "'s emails", "Link every email in " + acc.subList(1, acc.size()) + " to the group of " + first + ".", emailUf(par).m(first, "active"));
        }
        Map<String, TreeSet<String>> groups = new HashMap<>();
        for (String email : par.keySet()) groups.computeIfAbsent(findEmail(email), k -> new TreeSet<>()).add(email);
        T.s("Group by root", groups.size() + " merged account(s).", T.map("groups", groups));
        List<List<String>> res = new ArrayList<>();
        for (TreeSet<String> emails : groups.values()) {
            List<String> acc = new ArrayList<>();
            acc.add(owner.get(emails.first()));
            acc.addAll(emails);
            res.add(acc);
        }
        res.sort((x, y) -> x.get(1).compareTo(y.get(1))); // @hide
        return res;
    }
    //@end

    //@code ladderLength
    static int ladderLength(String beginWord, String endWord, List<String> wordList) {
        Set<String> words = new HashSet<>(wordList);
        if (!words.contains(endWord)) {
            T.n("Unreachable", "\"" + endWord + "\" isn't in the word list.", T.set("words", new TreeSet<>(words)));
            return 0;
        }
        Queue<String> queue = new ArrayDeque<>();
        queue.offer(beginWord);
        words.remove(beginWord);
        int level = 1;
        Map<String, String> from = new LinkedHashMap<>(); // @hide
        while (!queue.isEmpty()) {
            T.s("Level " + level, "Every word in the queue is " + (level - 1) + " change(s) from \"" + beginWord + "\".", T.queue("queue", queue), T.set("unvisited words", new TreeSet<>(words)), T.map("reached from", from), T.v("level", level));
            for (int size = queue.size(); size > 0; size--) {
                char[] w = queue.poll().toCharArray();
                String cur = new String(w); // @hide
                for (int i = 0; i < w.length; i++) {
                    char orig = w[i];
                    for (char ch = 'a'; ch <= 'z'; ch++) {
                        w[i] = ch;
                        String next = new String(w);
                        if (next.equals(endWord)) {
                            from.put(next, cur); // @hide
                            T.n("Reached " + endWord, "\"" + cur + "\" → \"" + endWord + "\". The ladder has " + (level + 1) + " words.", T.map("reached from", from).m(endWord, "found"), T.v("level", level + 1));
                            return level + 1;
                        }
                        if (words.remove(next)) {
                            queue.offer(next);
                            from.put(next, cur); // @hide
                            T.s("\"" + cur + "\" → \"" + next + "\"", "Change position " + i + " to '" + ch + "'. It's in the list, so queue it.", T.queue("queue", queue), T.set("unvisited words", new TreeSet<>(words)), T.map("reached from", from).m(next, "swap"), T.v("level", level));
                        }
                    }
                    w[i] = orig;
                }
            }
            level++;
        }
        return 0;
    }
    //@end
}
