//@code distanceK
vector<int> distanceK(TreeNode* root, TreeNode* target, int k) {
    unordered_map<TreeNode*, TreeNode*> parent;
    vector<TreeNode*> stack{root};
    while (!stack.empty()) {
        TreeNode* n = stack.back(); stack.pop_back();
        if (n->left) { parent[n->left] = n; stack.push_back(n->left); }
        if (n->right) { parent[n->right] = n; stack.push_back(n->right); }  // @8
    }
    queue<TreeNode*> q;
    unordered_set<TreeNode*> seen{target};
    q.push(target);
    for (int d = 0; d < k && !q.empty(); d++) {
        for (int size = q.size(); size > 0; size--) {
            TreeNode* n = q.front(); q.pop();
            for (TreeNode* next : {n->left, n->right, parent[n]}) {
                if (next && seen.insert(next).second) q.push(next);  // @18
            }
        }
    }
    vector<int> res;
    for (; !q.empty(); q.pop()) res.push_back(q.front()->val);
    return res;
}
//@end

//@code pathSum
void dfs(TreeNode* node, int remain, vector<int>& path, vector<vector<int>>& res) {
    if (!node) return;
    path.push_back(node->val);
    remain -= node->val;
    bool leaf = !node->left && !node->right;
    if (leaf && remain == 0) res.push_back(path);  // @12
    dfs(node->left, remain, path, res);
    dfs(node->right, remain, path, res);
    path.pop_back();
}

vector<vector<int>> pathSum(TreeNode* root, int targetSum) {
    vector<vector<int>> res;
    vector<int> path;
    dfs(root, targetSum, path, res);
    return res;
}
//@end

//@code flatten
void flatten(TreeNode* root) {
    TreeNode* node = root;
    while (node) {
        if (node->left) {
            TreeNode* rightmost = node->left;
            while (rightmost->right) rightmost = rightmost->right;  // @6
            rightmost->right = node->right;
            node->right = node->left;
            node->left = nullptr;  // @9
        }
        node = node->right;  // @11
    }
}
//@end

//@code maximalSquare
int maximalSquare(vector<vector<char>>& matrix) {
    int m = matrix.size(), n = matrix[0].size(), best = 0;
    vector<vector<int>> dp(m + 1, vector<int>(n + 1));
    for (int r = 1; r <= m; r++) {
        for (int c = 1; c <= n; c++) {
            if (matrix[r - 1][c - 1] == '1') {
                dp[r][c] = 1 + min({dp[r - 1][c - 1], dp[r - 1][c], dp[r][c - 1]});
                best = max(best, dp[r][c]);  // @8
            }
        }
    }
    return best * best;
}
//@end

//@code verticalTraversal
void collect(TreeNode* node, int col, int row, vector<array<int, 3>>& out) {
    if (!node) return;
    out.push_back({col, row, node->val});  // @19
    collect(node->left, col - 1, row + 1, out);
    collect(node->right, col + 1, row + 1, out);
}

vector<vector<int>> verticalTraversal(TreeNode* root) {
    vector<array<int, 3>> nodes;
    collect(root, 0, 0, nodes);
    sort(nodes.begin(), nodes.end());  // @4
    vector<vector<int>> res;
    int prevCol = INT_MIN;
    for (auto& [col, row, val] : nodes) {
        if (col != prevCol) {
            res.emplace_back();
            prevCol = col;
        }
        res.back().push_back(val);  // @12
    }
    return res;
}
//@end

//@code longestIncreasingPath
int longest(vector<vector<int>>& g, int r, int c, vector<vector<int>>& memo) {
    if (memo[r][c]) return memo[r][c];
    int best = 1;
    const int dirs[4][2] = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
    for (auto& d : dirs) {
        int nr = r + d[0], nc = c + d[1];
        if (nr >= 0 && nc >= 0 && nr < (int)g.size() && nc < (int)g[0].size() && g[nr][nc] > g[r][c])
            best = max(best, 1 + longest(g, nr, nc, memo));
    }
    memo[r][c] = best;  // @19
    return best;
}

int longestIncreasingPath(vector<vector<int>>& matrix) {
    int m = matrix.size(), n = matrix[0].size(), best = 0;
    vector<vector<int>> memo(m, vector<int>(n));
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++)
            best = max(best, longest(matrix, r, c, memo));
    return best;  // @7
}
//@end
