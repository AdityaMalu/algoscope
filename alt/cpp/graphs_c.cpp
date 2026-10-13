const int DIRS[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

//@code networkDelayTime
int networkDelayTime(vector<vector<int>>& times, int n, int k) {
    vector<vector<pair<int, int>>> adj(n + 1);
    for (auto& t : times) adj[t[0]].push_back({t[1], t[2]});
    vector<int> dist(n + 1, INT_MAX);
    dist[k] = 0;
    priority_queue<pair<int, int>, vector<pair<int, int>>, greater<>> pq;
    pq.push({0, k});  // @9
    while (!pq.empty()) {
        auto [d, u] = pq.top(); pq.pop();
        if (d > dist[u]) continue;
        for (auto [v, w] : adj[u]) {
            if (d + w < dist[v]) {
                dist[v] = d + w;
                pq.push({dist[v], v});  // @18
            }
        }
    }
    int mx = *max_element(dist.begin() + 1, dist.end());
    return mx == INT_MAX ? -1 : mx;  // @24
}
//@end

//@code shortestPathBinaryMatrix
int shortestPathBinaryMatrix(vector<vector<int>>& grid) {
    int n = grid.size();
    if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1) {
        return -1;  // @4
    }
    vector<vector<int>> dist(n, vector<int>(n));
    dist[0][0] = 1;
    queue<pair<int, int>> q;
    q.push({0, 0});
    while (!q.empty()) {
        auto [r, c] = q.front(); q.pop();
        if (r == n - 1 && c == n - 1) {
            return dist[r][c];  // @14
        }
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                int nr = r + dr, nc = c + dc;
                if (nr < 0 || nc < 0 || nr >= n || nc >= n || grid[nr][nc] == 1 || dist[nr][nc] != 0) continue;
                dist[nr][nc] = dist[r][c] + 1;
                q.push({nr, nc});  // @21
            }
    }
    return -1;
}
//@end

//@code minCostConnectPoints
int minCostConnectPoints(vector<vector<int>>& points) {
    int n = points.size(), total = 0;
    vector<int> dist(n, INT_MAX);
    vector<bool> inTree(n);
    dist[0] = 0;
    for (int round = 0; round < n; round++) {
        int u = -1;
        for (int i = 0; i < n; i++)
            if (!inTree[i] && (u == -1 || dist[i] < dist[u])) u = i;
        inTree[u] = true;
        total += dist[u];
        for (int v = 0; v < n; v++) {
            int d = abs(points[u][0] - points[v][0]) + abs(points[u][1] - points[v][1]);
            if (!inTree[v] && d < dist[v]) {
                dist[v] = d;  // @16
            }
        }
    }
    return total;
}
//@end

//@code findCheapestPrice
int findCheapestPrice(int n, vector<vector<int>>& flights, int src, int dst, int k) {
    vector<int> price(n, INT_MAX);
    price[src] = 0;  // @4
    for (int round = 0; round <= k; round++) {
        vector<int> next = price;
        for (auto& f : flights) {
            int u = f[0], v = f[1], w = f[2];
            if (price[u] != INT_MAX && price[u] + w < next[v]) next[v] = price[u] + w;
        }
        price = next;  // @11
    }
    return price[dst] == INT_MAX ? -1 : price[dst];
}
//@end

//@code minimumEffortPath
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
int minimumEffortPath(vector<vector<int>>& heights) {
    int m = heights.size(), n = heights[0].size();
    vector<vector<int>> effort(m, vector<int>(n, INT_MAX));
    effort[0][0] = 0;
    priority_queue<array<int, 3>, vector<array<int, 3>>, greater<>> pq;
    pq.push({0, 0, 0});
    while (!pq.empty()) {
        auto [e, r, c] = pq.top(); pq.pop();
        if (e > effort[r][c]) continue;
        if (r == m - 1 && c == n - 1) {
            return e;  // @14
        }
        for (auto& d : DIRS) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nc < 0 || nr >= m || nc >= n) continue;
            int ne = max(e, abs(heights[nr][nc] - heights[r][c]));
            if (ne < effort[nr][nc]) {
                effort[nr][nc] = ne;
                pq.push({ne, nr, nc});  // @22
            }
        }
    }
    return 0;
}
//@end

//@code swimInWater
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
int swimInWater(vector<vector<int>>& grid) {
    int n = grid.size(), time = 0;
    vector<vector<bool>> seen(n, vector<bool>(n));
    priority_queue<array<int, 3>, vector<array<int, 3>>, greater<>> pq;
    pq.push({grid[0][0], 0, 0});
    seen[0][0] = true;
    while (!pq.empty()) {
        auto [h, r, c] = pq.top(); pq.pop();
        time = max(time, h);  // @11
        if (r == n - 1 && c == n - 1) return time;
        for (auto& d : DIRS) {
            int nr = r + d[0], nc = c + d[1];
            if (nr < 0 || nc < 0 || nr >= n || nc >= n || seen[nr][nc]) continue;
            seen[nr][nc] = true;
            pq.push({grid[nr][nc], nr, nc});
        }
    }
    return time;
}
//@end
