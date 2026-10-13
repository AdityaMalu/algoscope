import java.util.*;

/** Graphs: BFS / DFS */
class GraphsA {
    static final int[][] DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    static int[][] TAG; // trace-only: 0 untouched, 1 current group, 2 finished
    static void tagInit(int m, int n) { TAG = new int[m][n]; }
    static void finish() { for (int[] r : TAG) for (int j = 0; j < r.length; j++) if (r[j] == 1) r[j] = 2; }
    static S grid(String name, Object g, int r, int c, String mark) {
        S s = T.mat(name, g);
        if (TAG != null) for (int i = 0; i < TAG.length; i++) for (int j = 0; j < TAG[i].length; j++) if (TAG[i][j] > 0) s.m(i, j, TAG[i][j] == 1 ? "visit" : "done");
        if (r >= 0) s.m(r, c, mark);
        return s;
    }

    static void run() {
        P.begin("Flood Fill", "floodFill");
        P.category("Graphs · DFS on a grid");
        P.summary("Starting from one pixel, repaint it and every connected pixel of the same original colour. DFS spreads in four directions and only continues into cells that still have the original colour.");
        P.cx("O(1)", "O(m·n)", "O(m·n)", "O(m·n)", "Each pixel is painted once; recursion can be as deep as the region.");
        P.ideas("Pixels of the original colour that touch each other form one region.",
            "Paint a cell before recursing so it isn't visited again.",
            "If the new colour equals the original, return immediately, or the DFS never ends.");
        P.pits("Missing the same-colour check: infinite recursion.",
            "Moving diagonally; only 4 directions count.",
            "Forgetting bounds checks before reading `image[r][c]`.");
        P.trace("image = [[1, 1, 1], [1, 1, 0], [1, 0, 1]], sr = 1, sc = 1, color = 2", () -> { tagInit(3, 3); return floodFill(new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}}, 1, 1, 2); });
        P.edge("Same colour", "image = [[0, 0, 0], [0, 0, 0]], sr = 0, sc = 0, color = 0", "[[0, 0, 0], [0, 0, 0]]", () -> { tagInit(2, 3); return floodFill(new int[][]{{0, 0, 0}, {0, 0, 0}}, 0, 0, 0); }, "Nothing changes; the early return prevents an infinite loop.");
        P.edge("Single pixel", "image = [[5]], sr = 0, sc = 0, color = 3", "[[3]]", () -> { tagInit(1, 1); return floodFill(new int[][]{{5}}, 0, 0, 3); }, "Only one cell to paint.");
        P.edge("Diagonals don't connect", "image = [[1, 0], [0, 1]], sr = 0, sc = 0, color = 9", "[[9, 0], [0, 1]]", () -> { tagInit(2, 2); return floodFill(new int[][]{{1, 0}, {0, 1}}, 0, 0, 9); }, "The bottom-right 1 touches only diagonally.");
        P.done();

        P.begin("Number of Islands", "numIslands");
        P.category("Graphs · DFS on a grid");
        P.summary("Count groups of connected land cells. Scan the grid; each time you find unvisited land, that's a new island. DFS from it and sink every connected land cell (turn it to water) so it's never counted again.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Each cell is visited a constant number of times; recursion depth can reach m·n.");
        P.ideas("An island is a connected component of '1' cells (4-directional).",
            "The outer scan finds a new island each time it hits a '1'.",
            "DFS sinks the whole island, marking it visited in place.",
            "BFS or union-find work equally well.");
        P.pits("Counting cells instead of islands.",
            "Using diagonal neighbours.",
            "Very large grids can overflow the recursion stack; BFS avoids that.");
        P.trace("grid = [[1,1,0,0,0],[1,1,0,0,0],[0,0,1,0,0],[0,0,0,1,1]]", () -> { tagInit(4, 5); return numIslands(F.grid("11000", "11000", "00100", "00011")); });
        P.edge("All water", "grid = [[0, 0], [0, 0]]", "0", () -> { tagInit(2, 2); return numIslands(F.grid("00", "00")); }, "The scan never finds land.");
        P.edge("All land", "grid = [[1, 1], [1, 1]]", "1", () -> { tagInit(2, 2); return numIslands(F.grid("11", "11")); }, "One DFS sinks everything.");
        P.edge("Diagonal cells", "grid = [[1, 0], [0, 1]]", "2", () -> { tagInit(2, 2); return numIslands(F.grid("10", "01")); }, "Diagonal touches don't connect.");
        P.edge("Snake-shaped island", "grid = [[1,1,1],[0,0,1],[1,1,1]]", "1", () -> { tagInit(3, 3); return numIslands(F.grid("111", "001", "111")); }, "The DFS follows the bend.");
        P.done();

        P.begin("Max Area of Island", "maxAreaOfIsland");
        P.category("Graphs · DFS on a grid");
        P.summary("Find the largest island by cell count. Same as counting islands, but each DFS returns the number of cells it sank: 1 for the current cell plus the areas from its four neighbours.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Every cell is visited once.");
        P.ideas("DFS returns the area of the island it explores.",
            "Sink cells as you go (set to 0) so they aren't counted twice.",
            "Keep the maximum area across all islands.");
        P.pits("Returning 0 when no land exists is correct; don't return -1.",
            "Forgetting to mark visited before recursing.");
        P.trace("grid = [[1,1,0,0],[1,0,0,1],[0,0,1,1],[0,0,1,0]]", () -> { tagInit(4, 4); return maxAreaOfIsland(new int[][]{{1, 1, 0, 0}, {1, 0, 0, 1}, {0, 0, 1, 1}, {0, 0, 1, 0}}); });
        P.edge("No land", "grid = [[0, 0, 0]]", "0", () -> { tagInit(1, 3); return maxAreaOfIsland(new int[][]{{0, 0, 0}}); }, "Max stays 0.");
        P.edge("Single cell", "grid = [[1]]", "1", () -> { tagInit(1, 1); return maxAreaOfIsland(new int[][]{{1}}); }, "One island of area 1.");
        P.edge("Two equal islands", "grid = [[1, 0, 1]]", "1", () -> { tagInit(1, 3); return maxAreaOfIsland(new int[][]{{1, 0, 1}}); }, "Both have area 1.");
        P.done();

        P.begin("Clone Graph", "cloneGraph");
        P.category("Graphs · DFS with a visited map");
        P.summary("Deep-copy a connected undirected graph. DFS through the original; a map from original node to its copy both prevents infinite loops on cycles and lets each copy link to the copies of its neighbours.");
        P.cx("O(V + E)", "O(V + E)", "O(V + E)", "O(V)", "Each node is copied once and each edge followed twice.");
        P.ideas("The map `old → copy` doubles as the visited set.",
            "Create the copy before recursing into neighbours, so cycles find it in the map.",
            "Each copy's neighbour list holds copies, never originals.");
        P.pits("Recursing before storing the copy in the map loops forever on cycles.",
            "Returning original nodes inside the clone's neighbour lists.",
            "Not handling a null input.");
        P.trace("adjList = [[2, 4], [1, 3], [2, 4], [1, 3]]", () -> adjOf(cloneGraph(build(new int[][]{{2, 4}, {1, 3}, {2, 4}, {1, 3}}))));
        P.edge("Single node, no edges", "adjList = [[]]", "[[]]", () -> adjOf(cloneGraph(build(new int[][]{{}}))), "Copy one node with an empty neighbour list.");
        P.edge("Empty graph", "adjList = []", "[]", () -> adjOf(cloneGraph(null)), "Return null.");
        P.edge("Two nodes", "adjList = [[2], [1]]", "[[2], [1]]", () -> adjOf(cloneGraph(build(new int[][]{{2}, {1}}))), "The cycle 1 → 2 → 1 is handled by the map.");
        P.done();

        P.begin("Rotting Oranges", "orangesRotting");
        P.category("Graphs · Multi-source BFS");
        P.summary("Every minute, rotten oranges rot their fresh neighbours. Start a BFS from all rotten oranges at once; each BFS layer is one minute. If fresh oranges remain at the end, return -1.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Each orange enters the queue at most once.");
        P.ideas("All initially rotten oranges spread at the same time: put them all in the queue first.",
            "Process the queue level by level; each level is one minute.",
            "Count fresh oranges up front and decrement as they rot.",
            "Fresh oranges left over can't be reached: -1.");
        P.pits("Running a separate BFS from each rotten orange gives wrong times.",
            "Counting a minute for the last level even though nothing new rotted.",
            "Returning 0 vs -1 when there are no fresh oranges at all (answer 0).");
        P.trace("grid = [[2, 1, 1], [1, 1, 0], [0, 1, 1]]", () -> { tagInit(3, 3); return orangesRotting(new int[][]{{2, 1, 1}, {1, 1, 0}, {0, 1, 1}}); });
        P.edge("Unreachable orange", "grid = [[2, 1, 1], [0, 1, 1], [1, 0, 1]]", "-1", () -> { tagInit(3, 3); return orangesRotting(new int[][]{{2, 1, 1}, {0, 1, 1}, {1, 0, 1}}); }, "The bottom-left orange is cut off by empty cells.");
        P.edge("No fresh oranges", "grid = [[0, 2]]", "0", () -> { tagInit(1, 2); return orangesRotting(new int[][]{{0, 2}}); }, "Nothing needs to rot.");
        P.edge("No rotten oranges", "grid = [[1]]", "-1", () -> { tagInit(1, 1); return orangesRotting(new int[][]{{1}}); }, "Nothing can start the rot.");
        P.edge("Two sources", "grid = [[2, 1, 1, 2]]", "1", () -> { tagInit(1, 4); return orangesRotting(new int[][]{{2, 1, 1, 2}}); }, "Both ends rot inward in the same minute.");
        P.done();

        P.begin("01 Matrix", "updateMatrix");
        P.category("Graphs · Multi-source BFS");
        P.summary("For every cell, find the distance to the nearest 0. Start a BFS from all zeros at once: the first time the BFS reaches a cell is its shortest distance.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Each cell is set once.");
        P.ideas("Reverse the question: spread outward from every 0 simultaneously.",
            "Mark 1-cells as unknown (-1) and fill them when first reached.",
            "BFS order guarantees the first visit is the shortest path.");
        P.pits("Running a BFS from each 1 separately is O((m·n)²).",
            "A two-pass DP (top-left, then bottom-right) also works; know it as an alternative.");
        P.trace("mat = [[0, 0, 0], [0, 1, 0], [1, 1, 1]]", () -> { tagInit(3, 3); return updateMatrix(new int[][]{{0, 0, 0}, {0, 1, 0}, {1, 1, 1}}); });
        P.edge("Single zero far away", "mat = [[1, 1, 1], [1, 1, 1], [1, 1, 0]]", "[[4, 3, 2], [3, 2, 1], [2, 1, 0]]", () -> { tagInit(3, 3); return updateMatrix(new int[][]{{1, 1, 1}, {1, 1, 1}, {1, 1, 0}}); }, "Distances grow outward from the corner.");
        P.edge("All zeros", "mat = [[0, 0]]", "[[0, 0]]", () -> { tagInit(1, 2); return updateMatrix(new int[][]{{0, 0}}); }, "Every cell is its own nearest zero.");
        P.edge("Single cell", "mat = [[0]]", "[[0]]", () -> { tagInit(1, 1); return updateMatrix(new int[][]{{0}}); }, "Trivial.");
        P.done();

        P.begin("Surrounded Regions", "solve");
        P.category("Graphs · DFS from the border");
        P.summary("Capture every 'O' region that is fully surrounded by 'X'. Reverse the problem: any 'O' connected to the border can't be captured. Mark those safe with a DFS from every border 'O', then flip the rest to 'X' and restore the safe ones.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Each cell is visited a constant number of times.");
        P.ideas("A region escapes capture only if it touches the border.",
            "DFS from border 'O's and mark them (e.g. '#').",
            "Final pass: 'O' → 'X' (captured), '#' → 'O' (safe).");
        P.pits("Trying to detect surrounded regions directly is much harder.",
            "Forgetting the left/right border columns.");
        P.trace("board = [[X,X,X,X],[X,O,O,X],[X,X,O,X],[X,O,X,X]]", () -> { tagInit(4, 4); char[][] b = F.grid("XXXX", "XOOX", "XXOX", "XOXX"); solve(b); return b; });
        P.edge("Region touching the border", "board = [[X,O,X],[X,O,X],[X,X,X]]", "[[\"X\", \"O\", \"X\"], [\"X\", \"O\", \"X\"], [\"X\", \"X\", \"X\"]]", () -> { tagInit(3, 3); char[][] b = F.grid("XOX", "XOX", "XXX"); solve(b); return b; }, "The column of O's reaches the top edge, so it's safe.");
        P.edge("Single cell", "board = [[O]]", "[[\"O\"]]", () -> { tagInit(1, 1); char[][] b = F.grid("O"); solve(b); return b; }, "A border cell is never captured.");
        P.edge("Fully enclosed", "board = [[X,X,X],[X,O,X],[X,X,X]]", "[[\"X\", \"X\", \"X\"], [\"X\", \"X\", \"X\"], [\"X\", \"X\", \"X\"]]", () -> { tagInit(3, 3); char[][] b = F.grid("XXX", "XOX", "XXX"); solve(b); return b; }, "The centre O is captured.");
        P.done();

        P.begin("Pacific Atlantic Water Flow", "pacificAtlantic");
        P.category("Graphs · Reverse DFS from two oceans");
        P.summary("Find cells whose water can flow to both oceans (water flows to equal or lower neighbours). Flip the direction: from each ocean's border, climb to equal or higher neighbours to find everything that drains into it. The answer is the overlap.");
        P.cx("O(m·n)", "O(m·n)", "O(m·n)", "O(m·n)", "Two traversals, each visiting every cell at most once.");
        P.ideas("Searching from every cell is O((m·n)²); searching backwards from the oceans is linear.",
            "Pacific touches the top and left edges; Atlantic the bottom and right.",
            "Reverse flow: move to a neighbour only if it's at least as high.",
            "Cells reachable from both searches are the answer.");
        P.pits("Using `<` instead of `<=`: equal heights let water flow.",
            "Mixing up which edges belong to which ocean.");
        P.trace("heights = [[1, 2, 2, 3], [3, 2, 3, 4], [2, 4, 5, 3], [6, 7, 1, 4]]", () -> pacificAtlantic(new int[][]{{1, 2, 2, 3}, {3, 2, 3, 4}, {2, 4, 5, 3}, {6, 7, 1, 4}}));
        P.edge("Single cell", "heights = [[1]]", "[[0, 0]]", () -> pacificAtlantic(new int[][]{{1}}), "It touches both oceans.");
        P.edge("Flat grid", "heights = [[1, 1], [1, 1]]", "[[0, 0], [0, 1], [1, 0], [1, 1]]", () -> pacificAtlantic(new int[][]{{1, 1}, {1, 1}}), "Equal heights flow everywhere.");
        P.edge("Single row", "heights = [[3, 1, 2]]", "[[0, 0], [0, 1], [0, 2]]", () -> pacificAtlantic(new int[][]{{3, 1, 2}}), "Every cell touches the top (Pacific) and bottom (Atlantic) edges.");
        P.done();
    }

    //@code floodFill
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int original = image[sr][sc];
        if (original == color) {
            T.n("Nothing to do", "The pixel already has colour " + color + ".", grid("image", image, sr, sc, "found"));
            return image;
        }
        fill(image, sr, sc, original, color);
        return image;
    }

    static void fill(int[][] image, int r, int c, int original, int color) {
        if (r < 0 || c < 0 || r >= image.length || c >= image[0].length || image[r][c] != original) return;
        image[r][c] = color;
        TAG[r][c] = 1; // @hide
        T.call("fill(" + r + ", " + c + ")");
        T.s("Paint (" + r + ", " + c + ")", "It had the original colour " + original + "; paint it " + color + " and spread to its 4 neighbours.", grid("image", image, r, c, "active"));
        for (int[] d : DIRS) fill(image, r + d[0], c + d[1], original, color);
        T.ret();
    }
    //@end

    //@code numIslands
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int numIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    T.s("New island #" + count, "Unvisited land at (" + r + ", " + c + "). Sink the whole island.", grid("grid", grid, r, c, "found"), T.v("count", count));
                    sink(grid, r, c);
                    finish(); // @hide
                }
            }
        }
        T.n("Scan finished", count + " island(s).", grid("grid", grid, -1, -1, ""), T.v("count", count));
        return count;
    }

    static void sink(char[][] grid, int r, int c) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length || grid[r][c] != '1') return;
        grid[r][c] = '0';
        TAG[r][c] = 1; // @hide
        T.call("sink(" + r + ", " + c + ")");
        T.s("Sink (" + r + ", " + c + ")", "Turn it to water so it's not counted again, then visit its neighbours.", grid("grid", grid, r, c, "active"));
        for (int[] d : DIRS) sink(grid, r + d[0], c + d[1]);
        T.ret();
    }
    //@end

    //@code maxAreaOfIsland
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int maxAreaOfIsland(int[][] grid) {
        int best = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[0].length; c++) {
                if (grid[r][c] == 1) {
                    int area = area(grid, r, c);
                    best = Math.max(best, area);
                    finish(); // @hide
                    T.s("Island measured", "The island starting at (" + r + ", " + c + ") has area " + area + ". Best: " + best + ".", grid("grid", grid, -1, -1, ""), T.v("area", area, "best", best));
                }
            }
        }
        return best;
    }

    static int area(int[][] grid, int r, int c) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length || grid[r][c] != 1) return 0;
        grid[r][c] = 0;
        TAG[r][c] = 1; // @hide
        T.call("area(" + r + ", " + c + ")");
        T.s("Count (" + r + ", " + c + ")", "Sink it and add the areas of its neighbours.", grid("grid", grid, r, c, "active"));
        int total = 1;
        for (int[] d : DIRS) total += area(grid, r + d[0], c + d[1]);
        T.ret();
        return total;
    }
    //@end

    static Node build(int[][] adj) {
        Node[] ns = new Node[adj.length];
        for (int i = 0; i < adj.length; i++) ns[i] = new Node(i + 1);
        for (int i = 0; i < adj.length; i++) for (int j : adj[i]) ns[i].neighbors.add(ns[j - 1]);
        return adj.length == 0 ? null : ns[0];
    }
    static R adjOf(Node n) {
        if (n == null) return new R("[]");
        TreeMap<Integer, Node> all = new TreeMap<>(); Deque<Node> st = new ArrayDeque<>(); st.push(n);
        while (!st.isEmpty()) { Node c = st.pop(); if (all.containsKey(c.val)) continue; all.put(c.val, c); for (Node x : c.neighbors) st.push(x); }
        List<List<Integer>> out = new ArrayList<>();
        for (Node c : all.values()) { List<Integer> l = new ArrayList<>(); for (Node x : c.neighbors) l.add(x.val); out.add(l); }
        return new R(F.s(out));
    }
    static Node ORIG;
    static S gview(String name, Node start, Collection<Node> mark, String m) {
        S s = T.graph(name, false);
        if (start == null) return s;
        TreeMap<Integer, Node> all = new TreeMap<>(); Deque<Node> st = new ArrayDeque<>(); st.push(start);
        while (!st.isEmpty()) { Node c = st.pop(); if (all.containsKey(c.val)) continue; all.put(c.val, c); for (Node x : c.neighbors) st.push(x); }
        for (Node c : all.values()) s.node(c.val, c.val);
        Set<String> seen = new HashSet<>();
        for (Node c : all.values()) for (Node x : c.neighbors) { String k = Math.min(c.val, x.val) + "-" + Math.max(c.val, x.val); if (seen.add(k)) s.edge(c.val, x.val, null); }
        if (mark != null) for (Node c : mark) s.m(c.val, m);
        return s;
    }
    static S cloneView(Map<Node, Node> copies) {
        S s = T.graph("clone (built so far)", false);
        TreeMap<Integer, Node> all = new TreeMap<>();
        for (Node c : copies.values()) all.put(c.val, c);
        for (Node c : all.values()) s.node(c.val, c.val);
        Set<String> seen = new HashSet<>();
        for (Node c : all.values()) for (Node x : c.neighbors) { String k = Math.min(c.val, x.val) + "-" + Math.max(c.val, x.val); if (seen.add(k)) s.edge(c.val, x.val, null); }
        return s;
    }

    //@code cloneGraph
    static class Node {
        int val;
        List<Node> neighbors = new ArrayList<>();
        Node(int v) { val = v; }
    }

    static Node cloneGraph(Node node) {
        ORIG = node; // @hide
        return clone(node, new HashMap<>());
    }

    static Node clone(Node node, Map<Node, Node> copies) {
        if (node == null) return null;
        if (copies.containsKey(node)) return copies.get(node);
        Node copy = new Node(node.val);
        copies.put(node, copy);
        T.call("clone(" + node.val + ")");
        T.s("Copy node " + node.val, "Create its copy and store it in the map before visiting neighbours, so cycles find it.", gview("original", ORIG, copies.keySet(), "done").m(node.val, "active"), cloneView(copies).m(node.val, "swap"));
        for (Node nb : node.neighbors) {
            copy.neighbors.add(clone(nb, copies));
            T.s("Link " + node.val + " → " + nb.val, "The copy of " + node.val + " now points to the copy of " + nb.val + ".", gview("original", ORIG, copies.keySet(), "done").m(node.val, "active").em(node.val, nb.val, "active"), cloneView(copies).em(node.val, nb.val, "active"));
        }
        T.ret();
        return copy;
    }
    //@end

    //@code orangesRotting
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length, fresh = 0;
        Queue<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == 2) queue.offer(new int[]{r, c});
                if (grid[r][c] == 1) fresh++;
            }
        T.s("All rotten oranges start together", queue.size() + " rotten, " + fresh + " fresh.", grid("grid", grid, -1, -1, ""), T.v("fresh", fresh, "minutes", 0));
        int minutes = 0;
        while (!queue.isEmpty() && fresh > 0) {
            minutes++;
            for (int size = queue.size(); size > 0; size--) {
                int[] cell = queue.poll();
                for (int[] d : DIRS) {
                    int r = cell[0] + d[0], c = cell[1] + d[1];
                    if (r < 0 || c < 0 || r >= m || c >= n || grid[r][c] != 1) continue;
                    grid[r][c] = 2;
                    fresh--;
                    queue.offer(new int[]{r, c});
                    TAG[r][c] = 1; // @hide
                }
            }
            T.s("Minute " + minutes, "Every orange that was rotten at the start of this minute rots its fresh neighbours. Fresh left: " + fresh + ".", grid("grid", grid, -1, -1, ""), T.v("fresh", fresh, "minutes", minutes));
            finish(); // @hide
        }
        T.n(fresh == 0 ? "All rotten" : "Some can't be reached", fresh == 0 ? "Took " + minutes + " minute(s)." : fresh + " fresh orange(s) are cut off, so return -1.", grid("grid", grid, -1, -1, ""), T.v("fresh", fresh, "minutes", minutes));
        return fresh == 0 ? minutes : -1;
    }
    //@end

    //@code updateMatrix
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static int[][] updateMatrix(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        Queue<int[]> queue = new ArrayDeque<>();
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++) {
                if (mat[r][c] == 0) queue.offer(new int[]{r, c});
                else mat[r][c] = -1;
            }
        T.s("Start from every 0", "Zeros are at distance 0; every other cell is unknown (-1).", grid("dist", mat, -1, -1, ""));
        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            for (int[] d : DIRS) {
                int r = cell[0] + d[0], c = cell[1] + d[1];
                if (r < 0 || c < 0 || r >= m || c >= n || mat[r][c] != -1) continue;
                mat[r][c] = mat[cell[0]][cell[1]] + 1;
                queue.offer(new int[]{r, c});
                T.s("Reach (" + r + ", " + c + ")", "First reached from (" + cell[0] + ", " + cell[1] + "), so its distance is " + mat[r][c] + ".", grid("dist", mat, r, c, "swap").m(cell[0], cell[1], "active"));
            }
        }
        return mat;
    }
    //@end

    //@code solve
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static void solve(char[][] board) {
        int m = board.length, n = board[0].length;
        for (int r = 0; r < m; r++) {
            mark(board, r, 0);
            mark(board, r, n - 1);
        }
        for (int c = 0; c < n; c++) {
            mark(board, 0, c);
            mark(board, m - 1, c);
        }
        T.s("Border regions marked safe", "Every 'O' connected to the edge is now '#'.", grid("board", board, -1, -1, ""));
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++) {
                if (board[r][c] == 'O') board[r][c] = 'X';
                else if (board[r][c] == '#') board[r][c] = 'O';
            }
        T.s("Flip", "Unmarked 'O's were surrounded: they become 'X'. '#' cells go back to 'O'.", T.mat("board", board));
    }

    static void mark(char[][] board, int r, int c) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || board[r][c] != 'O') return;
        board[r][c] = '#';
        TAG[r][c] = 1; // @hide
        T.s("Safe: (" + r + ", " + c + ")", "This 'O' connects to the border. Mark it '#'.", grid("board", board, r, c, "active"));
        for (int[] d : DIRS) mark(board, r + d[0], c + d[1]);
    }
    //@end

    //@code pacificAtlantic
    // DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
    static List<List<Integer>> pacificAtlantic(int[][] heights) {
        int m = heights.length, n = heights[0].length;
        boolean[][] pac = new boolean[m][n], atl = new boolean[m][n];
        PAC = pac; // @hide
        for (int r = 0; r < m; r++) {
            climb(heights, pac, r, 0);
            climb(heights, atl, r, n - 1);
        }
        for (int c = 0; c < n; c++) {
            climb(heights, pac, 0, c);
            climb(heights, atl, m - 1, c);
        }
        T.s("Both searches done", "Cells reachable uphill from each ocean's edges.", ocean("Pacific reach", heights, pac), ocean("Atlantic reach", heights, atl));
        List<List<Integer>> res = new ArrayList<>();
        for (int r = 0; r < m; r++)
            for (int c = 0; c < n; c++)
                if (pac[r][c] && atl[r][c]) res.add(Arrays.asList(r, c));
        T.s("Overlap", res.size() + " cells drain into both oceans.", both(heights, pac, atl), T.arr("res", res));
        return res;
    }

    static void climb(int[][] h, boolean[][] seen, int r, int c) {
        if (seen[r][c]) return;
        seen[r][c] = true;
        T.s((seen == PAC ? "Pacific" : "Atlantic") + " reaches (" + r + ", " + c + ")", "Height " + h[r][c] + ". Water here can flow down to the " + (seen == PAC ? "Pacific" : "Atlantic") + ". Climb to neighbours at least this high.",
            ocean(seen == PAC ? "Pacific reach" : "Atlantic reach", h, seen).m(r, c, "active"));
        for (int[] d : DIRS) {
            int nr = r + d[0], nc = c + d[1];
            if (nr >= 0 && nc >= 0 && nr < h.length && nc < h[0].length && h[nr][nc] >= h[r][c]) climb(h, seen, nr, nc);
        }
    }
    //@end
    static boolean[][] PAC;
    static S ocean(String name, int[][] h, boolean[][] s) { S x = T.mat(name, h); for (int i = 0; i < h.length; i++) for (int j = 0; j < h[0].length; j++) if (s[i][j]) x.m(i, j, "visit"); return x; }
    static S both(int[][] h, boolean[][] a, boolean[][] b) { S x = T.mat("heights", h); for (int i = 0; i < h.length; i++) for (int j = 0; j < h[0].length; j++) x.m(i, j, a[i][j] && b[i][j] ? "found" : "dim"); return x; }
}
