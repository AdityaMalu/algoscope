import java.util.*;

/** Advanced Graphs (Dijkstra / MST) */
class GraphsC {
    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    static Object inf(int v) { return v == Integer.MAX_VALUE ? "∞" : v; }
    static List<Object> infs(int[] a) { List<Object> l = new ArrayList<>(); for (int v : a) l.add(inf(v)); return l; }
    static List<Object> infs(int[][] a) { List<Object> l = new ArrayList<>(); for (int[] r : a) l.add(infs(r)); return l; }
    static S pqView(String name, PriorityQueue<int[]> pq, String fmt) {
        S s = new S("heap", name);
        List<Object> v = new ArrayList<>();
        for (Object o : pq.toArray()) { int[] e = (int[]) o; v.add(String.format(fmt, e.length > 0 ? e[0] : 0, e.length > 1 ? e[1] : 0, e.length > 2 ? e[2] : 0)); }
        s.put("values", v);
        return s;
    }
    static S wg(String name, int n, int[][] edges, int base, boolean directed) {
        S s = T.graph(name, directed);
        for (int i = base; i < n + base; i++) s.node(i, i);
        for (int[] e : edges) s.edge(e[0], e[1], e[2]);
        return s;
    }

    static void run() {
        P.begin("Network Delay Time", "networkDelayTime");
        P.category("Graphs · Dijkstra");
        P.summary("A signal starts at node k; how long until every node receives it? That's the largest shortest-path distance from k. Dijkstra's algorithm pops the closest unsettled node from a min-heap and relaxes its outgoing edges.");
        P.cx("O(E log V)", "O(E log V)", "O(E log V)", "O(V + E)", "Each edge can push one heap entry.");
        P.ideas("Weights are non-negative, so the closest unsettled node's distance is final.",
            "Skip stale heap entries whose distance is larger than the recorded best.",
            "Answer = max distance; if any node is unreachable, -1.");
        P.pits("Using BFS ignores the weights.",
            "Forgetting to skip stale entries makes it slower (still correct).",
            "Nodes are 1-indexed.");
        P.trace("times = [[2,1,1],[2,3,1],[3,4,1],[1,4,4]], n = 4, k = 2", () -> networkDelayTime(new int[][]{{2, 1, 1}, {2, 3, 1}, {3, 4, 1}, {1, 4, 4}}, 4, 2));
        P.edge("Unreachable node", "times = [[1,2,1]], n = 2, k = 2", "-1", () -> networkDelayTime(new int[][]{{1, 2, 1}}, 2, 2), "Edges are directed: 2 can't reach 1.");
        P.edge("Single node", "times = [], n = 1, k = 1", "0", () -> networkDelayTime(new int[][]{}, 1, 1), "The signal is already everywhere.");
        P.edge("Longer path is faster", "times = [[1,2,10],[1,3,1],[3,2,1]], n = 3, k = 1", "2", () -> networkDelayTime(new int[][]{{1, 2, 10}, {1, 3, 1}, {3, 2, 1}}, 3, 1), "1 → 3 → 2 costs 2, beating the direct edge of 10.");
        P.done();

        P.begin("Shortest Path in Binary Matrix", "shortestPathBinaryMatrix");
        P.category("Graphs · BFS with 8 directions");
        P.summary("Find the shortest path of 0-cells from the top-left to the bottom-right, moving in 8 directions. Every move costs the same, so BFS finds the shortest path. Path length counts cells, so the start is 1.");
        P.cx("O(1)", "O(n²)", "O(n²)", "O(n²)", "Each cell is queued at most once.");
        P.ideas("Unweighted shortest path = BFS.",
            "Diagonal moves count, so check all 8 neighbours.",
            "Mark cells visited when queuing, not when popping, to avoid duplicates.",
            "If the start or end is 1, there's no path.");
        P.pits("Using only 4 directions.",
            "Returning the number of moves instead of the number of cells.");
        P.trace("grid = [[0, 0, 0], [1, 1, 0], [1, 1, 0]]", () -> shortestPathBinaryMatrix(new int[][]{{0, 0, 0}, {1, 1, 0}, {1, 1, 0}}));
        P.edge("Blocked start", "grid = [[1, 0], [0, 0]]", "-1", () -> shortestPathBinaryMatrix(new int[][]{{1, 0}, {0, 0}}), "You can't stand on a 1.");
        P.edge("Single cell", "grid = [[0]]", "1", () -> shortestPathBinaryMatrix(new int[][]{{0}}), "The path is just the start.");
        P.edge("Diagonal shortcut", "grid = [[0, 1], [1, 0]]", "2", () -> shortestPathBinaryMatrix(new int[][]{{0, 1}, {1, 0}}), "One diagonal move.");
        P.done();

        P.begin("Min Cost to Connect All Points", "minCostConnectPoints");
        P.category("Graphs · Prim's MST");
        P.summary("Connect all points with the least total Manhattan distance: a minimum spanning tree. Prim's algorithm grows the tree one point at a time, always adding the cheapest edge from the tree to a point outside it. On a dense graph, an O(n²) array version beats using a heap.");
        P.cx("O(n²)", "O(n²)", "O(n²)", "O(n)", "Every pair of points is a potential edge.");
        P.ideas("`dist[i]` = cheapest edge connecting point i to the tree so far.",
            "Each round, add the outside point with the smallest dist.",
            "Then update dist for the remaining points using the new point.",
            "Kruskal with union-find also works, but needs all n² edges sorted.");
        P.pits("Building all n² edges into a heap uses a lot of memory.",
            "Using Euclidean instead of Manhattan distance.");
        P.trace("points = [[0, 0], [2, 2], [3, 10], [5, 2], [7, 0]]", () -> minCostConnectPoints(new int[][]{{0, 0}, {2, 2}, {3, 10}, {5, 2}, {7, 0}}));
        P.edge("Single point", "points = [[0, 0]]", "0", () -> minCostConnectPoints(new int[][]{{0, 0}}), "Nothing to connect.");
        P.edge("Two points", "points = [[0, 0], [1, 1]]", "2", () -> minCostConnectPoints(new int[][]{{0, 0}, {1, 1}}), "|1| + |1| = 2.");
        P.edge("Negative coordinates", "points = [[-1, -1], [1, 1], [0, 0]]", "4", () -> minCostConnectPoints(new int[][]{{-1, -1}, {1, 1}, {0, 0}}), "Both edges to the middle point cost 2.");
        P.done();

        P.begin("Cheapest Flights Within K Stops", "findCheapestPrice");
        P.category("Graphs · Bellman-Ford with a limit");
        P.summary("Find the cheapest flight from src to dst with at most k stops (k + 1 flights). Run Bellman-Ford for k + 1 rounds; each round relaxes every flight once, using a copy of the previous round's prices so a round adds at most one more flight.");
        P.cx("O(k·E)", "O(k·E)", "O(k·E)", "O(n)", "k + 1 rounds over all flights.");
        P.ideas("After round i, `price[v]` is the cheapest cost using at most i flights.",
            "Relax from the previous round's copy, or a round could chain several flights.",
            "Plain Dijkstra fails because a cheaper path may use too many stops.");
        P.pits("Updating prices in place lets one round use multiple flights.",
            "Treating k as the number of flights instead of stops.");
        P.trace("n = 4, flights = [[0,1,100],[1,2,100],[2,0,100],[1,3,600],[2,3,200]], src = 0, dst = 3, k = 1", () -> findCheapestPrice(4, new int[][]{{0, 1, 100}, {1, 2, 100}, {2, 0, 100}, {1, 3, 600}, {2, 3, 200}}, 0, 3, 1));
        P.edge("Stop limit forces the pricier route", "n = 3, flights = [[0,1,100],[1,2,100],[0,2,500]], src = 0, dst = 2, k = 0", "500", () -> findCheapestPrice(3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 0), "Zero stops means the direct flight only.");
        P.edge("Enough stops", "same flights, k = 1", "200", () -> findCheapestPrice(3, new int[][]{{0, 1, 100}, {1, 2, 100}, {0, 2, 500}}, 0, 2, 1), "One stop through 1 is cheaper.");
        P.edge("Unreachable", "n = 2, flights = [], src = 0, dst = 1, k = 1", "-1", () -> findCheapestPrice(2, new int[][]{}, 0, 1, 1), "No flights at all.");
        P.done();

        P.begin("Path With Minimum Effort", "minimumEffortPath");
        P.category("Graphs · Dijkstra on a grid");
        P.summary("A path's effort is its largest height difference between consecutive cells; find the path with the smallest effort. Dijkstra works with \"cost = max edge so far\" instead of a sum: always expand the cell with the smallest effort to reach it.");
        P.cx("O(mn log mn)", "O(mn log mn)", "O(mn log mn)", "O(mn)", "Each cell can be pushed a few times.");
        P.ideas("`effort[r][c]` = the smallest maximum step needed to reach (r, c).",
            "Moving to a neighbour costs `max(effort, |height difference|)`.",
            "The first time the bottom-right is popped, its effort is final.",
            "Binary search on the answer plus BFS is an alternative.");
        P.pits("Summing differences instead of taking the max.",
            "Stopping when the target is first pushed rather than first popped.");
        P.trace("heights = [[1, 2, 2], [3, 8, 2], [5, 3, 5]]", () -> minimumEffortPath(new int[][]{{1, 2, 2}, {3, 8, 2}, {5, 3, 5}}));
        P.edge("Single cell", "heights = [[7]]", "0", () -> minimumEffortPath(new int[][]{{7}}), "No moves needed.");
        P.edge("Flat grid", "heights = [[1, 1], [1, 1]]", "0", () -> minimumEffortPath(new int[][]{{1, 1}, {1, 1}}), "Every step is free.");
        P.edge("Single row", "heights = [[1, 10, 6, 7, 9, 10, 4, 9]]", "9", () -> minimumEffortPath(new int[][]{{1, 10, 6, 7, 9, 10, 4, 9}}), "No detour is possible, so the 1 → 10 jump is forced.");
        P.done();

        P.begin("Swim in Rising Water", "swimInWater");
        P.category("Graphs · Dijkstra (minimax)");
        P.summary("At time t you can swim through cells with height ≤ t. Find the earliest time to reach the bottom-right. The answer is the smallest possible maximum height along a path, so expand cells in order of height with a min-heap, tracking the highest cell seen.");
        P.cx("O(n² log n)", "O(n² log n)", "O(n² log n)", "O(n²)", "Each cell is pushed once.");
        P.ideas("A path is usable at time t exactly when its highest cell is ≤ t.",
            "Always expand the lowest reachable cell next (min-heap by height).",
            "The time needed so far is the largest height popped.",
            "Stop when the target is popped.");
        P.pits("BFS by steps ignores heights.",
            "Forgetting that the start cell's own height counts.");
        P.trace("grid = [[0, 1, 2, 3, 4], [24, 23, 22, 21, 5], [12, 13, 14, 15, 16], [11, 17, 18, 19, 20], [10, 9, 8, 7, 6]]", () -> swimInWater(new int[][]{{0, 1, 2, 3, 4}, {24, 23, 22, 21, 5}, {12, 13, 14, 15, 16}, {11, 17, 18, 19, 20}, {10, 9, 8, 7, 6}}));
        P.edge("2×2 grid", "grid = [[0, 2], [1, 3]]", "3", () -> swimInWater(new int[][]{{0, 2}, {1, 3}}), "The target itself is height 3.");
        P.edge("Single cell", "grid = [[0]]", "0", () -> swimInWater(new int[][]{{0}}), "Already there.");
        P.edge("High start", "grid = [[3, 0], [1, 2]]", "3", () -> swimInWater(new int[][]{{3, 0}, {1, 2}}), "You can't leave until the water covers the start.");
        P.done();
    }

    //@code networkDelayTime
    static int networkDelayTime(int[][] times, int n, int k) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) adj.add(new ArrayList<>());
        for (int[] t : times) adj.get(t[0]).add(new int[]{t[1], t[2]});
        int[] dist = new int[n + 1];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        pq.offer(new int[]{k, 0});
        Set<Integer> settled = new HashSet<>(); // @hide
        T.s("Start at node " + k, "Distance 0 to itself; everything else is ∞.", wg("network", n, times, 1, true).m(k, "active"), T.arr("dist", infs(dist)).m(k, "swap"), pqView("heap (node, dist)", pq, "(%d, %d)"));
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int u = top[0], d = top[1];
            if (d > dist[u]) continue;
            settled.add(u); // @hide
            for (int[] e : adj.get(u)) {
                int v = e[0], w = e[1];
                if (d + w < dist[v]) {
                    dist[v] = d + w;
                    pq.offer(new int[]{v, dist[v]});
                    T.s("Relax " + u + " → " + v, d + " + " + w + " = " + dist[v] + " beats the old distance. Push it.", mark(wg("network", n, times, 1, true), settled).m(u, "active").em(u, v, "active"), T.arr("dist", infs(dist)).m(v, "swap"), pqView("heap (node, dist)", pq, "(%d, %d)"));
                }
            }
        }
        int max = 0;
        for (int i = 1; i <= n; i++) max = Math.max(max, dist[i]);
        T.n(max == Integer.MAX_VALUE ? "Some node is unreachable" : "Slowest node", max == Integer.MAX_VALUE ? "At least one distance is still ∞." : "The last node hears the signal at time " + max + ".", mark(wg("network", n, times, 1, true), settled), T.arr("dist", infs(dist)));
        return max == Integer.MAX_VALUE ? -1 : max;
    }
    //@end
    static S mark(S s, Collection<Integer> done) { for (int d : done) s.m(d, "done"); return s; }

    //@code shortestPathBinaryMatrix
    static int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
            T.n("Blocked", "The start or the end is a 1.", T.mat("grid", grid).m(0, 0, "error").m(n - 1, n - 1, "error"));
            return -1;
        }
        int[][] dist = new int[n][n];
        dist[0][0] = 1;
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{0, 0});
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int r = cell[0], c = cell[1];
            if (r == n - 1 && c == n - 1) {
                T.n("Reached the corner", "Shortest path visits " + dist[r][c] + " cells.", blk(grid, dist).m(r, c, "found"));
                return dist[r][c];
            }
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++) {
                    int nr = r + dr, nc = c + dc;
                    if (nr < 0 || nc < 0 || nr >= n || nc >= n || grid[nr][nc] == 1 || dist[nr][nc] != 0) continue;
                    dist[nr][nc] = dist[r][c] + 1;
                    queue.offer(new int[]{nr, nc});
                }
            T.s("Expand (" + r + ", " + c + ")", "Queue every open, unvisited neighbour (8 directions) at distance " + (dist[r][c] + 1) + ".", blk(grid, dist).m(r, c, "active"), T.v("r", r, "c", c, "dist", dist[r][c]));
        }
        return -1;
    }
    //@end
    static S blk(int[][] g, int[][] d) {
        int n = g.length; Object[][] v = new Object[n][n];
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) v[i][j] = g[i][j] == 1 ? "█" : d[i][j] == 0 ? "" : d[i][j];
        S s = T.mat("distance (█ = blocked)", v);
        for (int i = 0; i < n; i++) for (int j = 0; j < n; j++) if (d[i][j] > 0) s.m(i, j, "visit"); else if (g[i][j] == 1) s.m(i, j, "dim");
        return s;
    }

    //@code minCostConnectPoints
    static int minCostConnectPoints(int[][] points) {
        int n = points.length, total = 0;
        int[] dist = new int[n];
        boolean[] inTree = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[0] = 0;
        List<int[]> chosen = new ArrayList<>(); // @hide
        int[] from = new int[n]; // @hide
        for (int round = 0; round < n; round++) {
            int u = -1;
            for (int i = 0; i < n; i++)
                if (!inTree[i] && (u == -1 || dist[i] < dist[u])) u = i;
            inTree[u] = true;
            total += dist[u];
            if (round > 0) chosen.add(new int[]{from[u], u, dist[u]}); // @hide
            for (int v = 0; v < n; v++) {
                int d = Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]);
                if (!inTree[v] && d < dist[v]) {
                    dist[v] = d;
                    from[v] = u; // @hide
                }
            }
            T.s("Add point " + u, (round == 0 ? "Start the tree at point 0." : "Its cheapest connection costs " + chosen.get(chosen.size() - 1)[2] + ". Total: " + total + ".") + " Update each outside point's cheapest edge.",
                mst(points, inTree, chosen).m(u, "active"), T.arr("dist", infs(dist)).mark(treeMarks(inTree)), T.v("total", total));
        }
        return total;
    }
    //@end
    static Map<Integer, String> treeMarks(boolean[] t) { Map<Integer, String> m = new HashMap<>(); for (int i = 0; i < t.length; i++) if (t[i]) m.put(i, "done"); return m; }
    static S mst(int[][] pts, boolean[] in, List<int[]> chosen) {
        S s = T.graph("points", false);
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        for (int[] p : pts) { minX = Math.min(minX, p[0]); maxX = Math.max(maxX, p[0]); minY = Math.min(minY, p[1]); maxY = Math.max(maxY, p[1]); }
        for (int i = 0; i < pts.length; i++) {
            double x = maxX == minX ? 50 : 100.0 * (pts[i][0] - minX) / (maxX - minX), y = maxY == minY ? 50 : 100 - 100.0 * (pts[i][1] - minY) / (maxY - minY);
            s.nodeAt(i, "(" + pts[i][0] + "," + pts[i][1] + ")", x, y);
        }
        for (int[] e : chosen) s.edge(e[0], e[1], e[2]);
        for (int i = 0; i < pts.length; i++) if (in[i]) s.m(i, "done");
        return s;
    }

    //@code findCheapestPrice
    static int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
        int[] price = new int[n];
        Arrays.fill(price, Integer.MAX_VALUE);
        price[src] = 0;
        T.s("Start", "Only the source is reachable with 0 flights.", wg("flights", n, flights, 0, true).m(src, "active"), T.arr("price", infs(price)));
        for (int round = 0; round <= k; round++) {
            int[] next = price.clone();
            for (int[] f : flights) {
                int u = f[0], v = f[1], w = f[2];
                if (price[u] != Integer.MAX_VALUE && price[u] + w < next[v]) next[v] = price[u] + w;
            }
            price = next;
            T.s("Round " + (round + 1), "Allow up to " + (round + 1) + " flight(s). Each flight relaxes from the previous round's prices.", wg("flights", n, flights, 0, true).m(src, "active").m(dst, "compare"), T.arr("price", infs(price)).m(dst, "compare"), T.v("round", round + 1, "stops allowed", round));
        }
        return price[dst] == Integer.MAX_VALUE ? -1 : price[dst];
    }
    //@end

    //@code minimumEffortPath
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int minimumEffortPath(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        int[][] effort = new int[m][n];
        for (int[] row : effort) Arrays.fill(row, Integer.MAX_VALUE);
        effort[0][0] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{0, 0, 0});
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            int e = top[0], r = top[1], c = top[2];
            if (e > effort[r][c]) continue;
            if (r == m - 1 && c == n - 1) {
                T.n("Target popped", "Its effort, " + e + ", is final.", T.mat("effort", infs(effort)).m(r, c, "found"), T.mat("heights", heights).m(r, c, "found"));
                return e;
            }
            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nc < 0 || nr >= m || nc >= n) continue;
                int ne = Math.max(e, Math.abs(heights[nr][nc] - heights[r][c]));
                if (ne < effort[nr][nc]) {
                    effort[nr][nc] = ne;
                    pq.offer(new int[]{ne, nr, nc});
                }
            }
            T.s("Expand (" + r + ", " + c + ")", "Lowest effort so far: " + e + ". Neighbours get max(" + e + ", height difference) if that's better.", T.mat("effort", infs(effort)).m(r, c, "active"), T.mat("heights", heights).m(r, c, "active"));
        }
        return 0;
    }
    //@end

    //@code swimInWater
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int swimInWater(int[][] grid) {
        int n = grid.length, time = 0;
        boolean[][] seen = new boolean[n][n];
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        pq.offer(new int[]{grid[0][0], 0, 0});
        seen[0][0] = true;
        while (!pq.isEmpty()) {
            int[] top = pq.poll();
            time = Math.max(time, top[0]);
            int r = top[1], c = top[2];
            T.s("Swim to (" + r + ", " + c + ")", "It's the lowest reachable cell (height " + top[0] + "). Time needed so far: " + time + ".", sw(grid, seen).m(r, c, "active"), pqView("heap (height, r, c)", pq, "%d @(%d,%d)"), T.v("time", time));
            if (r == n - 1 && c == n - 1) return time;
            for (int[] d : DIRS) {
                int nr = r + d[0], nc = c + d[1];
                if (nr < 0 || nc < 0 || nr >= n || nc >= n || seen[nr][nc]) continue;
                seen[nr][nc] = true;
                pq.offer(new int[]{grid[nr][nc], nr, nc});
            }
        }
        return time;
    }
    //@end
    static S sw(int[][] g, boolean[][] seen) { S s = T.mat("grid", g); for (int i = 0; i < g.length; i++) for (int j = 0; j < g.length; j++) if (seen[i][j]) s.m(i, j, "visit"); return s; }
}
