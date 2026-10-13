const int DIRS[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

//@code floodFill
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
void fill(vector<vector<int>>& image, int r, int c, int original, int color);

vector<vector<int>> floodFill(vector<vector<int>>& image, int sr, int sc, int color) {
    int original = image[sr][sc];
    if (original == color) {
        return image;  // @5
    }
    fill(image, sr, sc, original, color);
    return image;
}

void fill(vector<vector<int>>& image, int r, int c, int original, int color) {
    if (r < 0 || c < 0 || r >= (int)image.size() || c >= (int)image[0].size() || image[r][c] != original) return;
    image[r][c] = color;  // @13
    for (auto& d : DIRS) fill(image, r + d[0], c + d[1], original, color);
}
//@end

//@code numIslands
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
void sink(vector<vector<char>>& grid, int r, int c) {
    if (r < 0 || c < 0 || r >= (int)grid.size() || c >= (int)grid[0].size() || grid[r][c] != '1') return;
    grid[r][c] = '0';  // @17
    for (auto& d : DIRS) sink(grid, r + d[0], c + d[1]);
}

int numIslands(vector<vector<char>>& grid) {
    int count = 0;
    for (int r = 0; r < (int)grid.size(); r++) {
        for (int c = 0; c < (int)grid[0].size(); c++) {
            if (grid[r][c] == '1') {
                count++;  // @7
                sink(grid, r, c);
            }
        }
    }
    return count;  // @12
}
//@end

//@code maxAreaOfIsland
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
int area(vector<vector<int>>& grid, int r, int c) {
    if (r < 0 || c < 0 || r >= (int)grid.size() || c >= (int)grid[0].size() || grid[r][c] != 1) return 0;
    grid[r][c] = 0;  // @17
    int total = 1;
    for (auto& d : DIRS) total += area(grid, r + d[0], c + d[1]);
    return total;
}

int maxAreaOfIsland(vector<vector<int>>& grid) {
    int best = 0;
    for (int r = 0; r < (int)grid.size(); r++) {
        for (int c = 0; c < (int)grid[0].size(); c++) {
            if (grid[r][c] == 1) {
                best = max(best, area(grid, r, c));  // @8
            }
        }
    }
    return best;
}
//@end

//@code cloneGraph
class Node {
public:
    int val;
    vector<Node*> neighbors;
    Node(int v) : val(v) {}
};

Node* clone(Node* node, unordered_map<Node*, Node*>& copies) {
    if (!node) return nullptr;
    if (copies.count(node)) return copies[node];
    Node* copy = new Node(node->val);
    copies[node] = copy;  // @15
    for (Node* nb : node->neighbors) {
        copy->neighbors.push_back(clone(nb, copies));  // @17
    }
    return copy;
}

Node* cloneGraph(Node* node) {
    unordered_map<Node*, Node*> copies;
    return clone(node, copies);
}
//@end

Node* buildGraph(const vector<vector<int>>& adj) {
    if (adj.empty()) return nullptr;
    vector<Node*> ns;
    for (size_t i = 0; i < adj.size(); i++) ns.push_back(new Node(i + 1));
    for (size_t i = 0; i < adj.size(); i++) for (int j : adj[i]) ns[i]->neighbors.push_back(ns[j - 1]);
    return ns[0];
}

Raw adjOf(Node* n) {
    if (!n) return {"[]"};
    map<int, Node*> all; vector<Node*> st{n};
    while (!st.empty()) { Node* c = st.back(); st.pop_back(); if (all.count(c->val)) continue; all[c->val] = c; for (Node* x : c->neighbors) st.push_back(x); }
    vector<vector<int>> out;
    for (auto& [v, c] : all) { vector<int> l; for (Node* x : c->neighbors) l.push_back(x->val); out.push_back(l); }
    return {fmt(out)};
}

//@code orangesRotting
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
int orangesRotting(vector<vector<int>>& grid) {
    int m = grid.size(), n = grid[0].size(), fresh = 0;
    queue<pair<int, int>> q;
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) {
            if (grid[r][c] == 2) q.push({r, c});
            if (grid[r][c] == 1) fresh++;  // @8
        }
    int minutes = 0;
    while (!q.empty() && fresh > 0) {
        minutes++;
        for (int size = q.size(); size > 0; size--) {
            auto [r0, c0] = q.front(); q.pop();
            for (auto& d : DIRS) {
                int r = r0 + d[0], c = c0 + d[1];
                if (r < 0 || c < 0 || r >= m || c >= n || grid[r][c] != 1) continue;
                grid[r][c] = 2;
                fresh--;
                q.push({r, c});  // @20
            }
        }
    }
    return fresh == 0 ? minutes : -1;  // @24
}
//@end

//@code updateMatrix
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
vector<vector<int>> updateMatrix(vector<vector<int>>& mat) {
    int m = mat.size(), n = mat[0].size();
    queue<pair<int, int>> q;
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) {
            if (mat[r][c] == 0) q.push({r, c});
            else mat[r][c] = -1;  // @8
        }
    while (!q.empty()) {
        auto [r0, c0] = q.front(); q.pop();
        for (auto& d : DIRS) {
            int r = r0 + d[0], c = c0 + d[1];
            if (r < 0 || c < 0 || r >= m || c >= n || mat[r][c] != -1) continue;
            mat[r][c] = mat[r0][c0] + 1;
            q.push({r, c});  // @16
        }
    }
    return mat;
}
//@end

//@code solve
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
void mark(vector<vector<char>>& board, int r, int c) {
    if (r < 0 || c < 0 || r >= (int)board.size() || c >= (int)board[0].size() || board[r][c] != 'O') return;
    board[r][c] = '#';  // @21
    for (auto& d : DIRS) mark(board, r + d[0], c + d[1]);
}

void solve(vector<vector<char>>& board) {
    int m = board.size(), n = board[0].size();
    for (int r = 0; r < m; r++) {
        mark(board, r, 0);
        mark(board, r, n - 1);
    }
    for (int c = 0; c < n; c++) {
        mark(board, 0, c);
        mark(board, m - 1, c);  // @10
    }
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) {
            if (board[r][c] == 'O') board[r][c] = 'X';
            else if (board[r][c] == '#') board[r][c] = 'O';  // @15
        }
}
//@end

//@code pacificAtlantic
// DIRS = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}: down, up, right, left
void climb(vector<vector<int>>& h, vector<vector<bool>>& seen, int r, int c) {
    if (seen[r][c]) return;
    seen[r][c] = true;  // @22
    for (auto& d : DIRS) {
        int nr = r + d[0], nc = c + d[1];
        if (nr >= 0 && nc >= 0 && nr < (int)h.size() && nc < (int)h[0].size() && h[nr][nc] >= h[r][c]) climb(h, seen, nr, nc);
    }
}

vector<vector<int>> pacificAtlantic(vector<vector<int>>& heights) {
    int m = heights.size(), n = heights[0].size();
    vector<vector<bool>> pac(m, vector<bool>(n)), atl(m, vector<bool>(n));
    for (int r = 0; r < m; r++) {
        climb(heights, pac, r, 0);
        climb(heights, atl, r, n - 1);
    }
    for (int c = 0; c < n; c++) {
        climb(heights, pac, 0, c);
        climb(heights, atl, m - 1, c);  // @11
    }
    vector<vector<int>> res;
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++)
            if (pac[r][c] && atl[r][c]) res.push_back({r, c});  // @16
    return res;
}
//@end
