//@code subsets
void backtrack(vector<int>& nums, int start, vector<int>& path, vector<vector<int>>& res) {
    res.push_back(path);  // @8
    for (int i = start; i < (int)nums.size(); i++) {
        path.push_back(nums[i]);
        backtrack(nums, i + 1, path, res);
        path.pop_back();
    }
}

vector<vector<int>> subsets(vector<int>& nums) {
    vector<vector<int>> res;
    vector<int> path;
    backtrack(nums, 0, path, res);
    return res;
}
//@end

//@code permute
void dfs(vector<int>& nums, vector<bool>& used, vector<int>& path, vector<vector<int>>& res) {
    if (path.size() == nums.size()) {
        res.push_back(path);  // @9
        return;
    }
    for (int i = 0; i < (int)nums.size(); i++) {
        if (used[i]) continue;
        used[i] = true;
        path.push_back(nums[i]);  // @15
        dfs(nums, used, path, res);
        path.pop_back();
        used[i] = false;
    }
}

vector<vector<int>> permute(vector<int>& nums) {
    vector<vector<int>> res;
    vector<bool> used(nums.size());
    vector<int> path;
    dfs(nums, used, path, res);
    return res;
}
//@end

//@code combinationSum
void search(vector<int>& c, int start, int remain, vector<int>& path, vector<vector<int>>& res) {
    if (remain == 0) {
        res.push_back(path);  // @10
        return;
    }
    for (int i = start; i < (int)c.size(); i++) {
        if (c[i] > remain) {
            break;  // @15
        }
        path.push_back(c[i]);  // @17
        search(c, i, remain - c[i], path, res);
        path.pop_back();
    }
}

vector<vector<int>> combinationSum(vector<int>& candidates, int target) {
    sort(candidates.begin(), candidates.end());
    vector<vector<int>> res;
    vector<int> path;
    search(candidates, 0, target, path, res);
    return res;
}
//@end

//@code subsetsWithDup
void go(vector<int>& nums, int start, vector<int>& path, vector<vector<int>>& res) {
    res.push_back(path);  // @9
    for (int i = start; i < (int)nums.size(); i++) {
        if (i > start && nums[i] == nums[i - 1]) {
            continue;  // @12
        }
        path.push_back(nums[i]);
        go(nums, i + 1, path, res);
        path.pop_back();
    }
}

vector<vector<int>> subsetsWithDup(vector<int>& nums) {
    sort(nums.begin(), nums.end());
    vector<vector<int>> res;
    vector<int> path;
    go(nums, 0, path, res);
    return res;
}
//@end

//@code combinationSum2
void find2(vector<int>& c, int start, int remain, vector<int>& path, vector<vector<int>>& res) {
    if (remain == 0) {
        res.push_back(path);  // @10
        return;
    }
    for (int i = start; i < (int)c.size(); i++) {
        if (i > start && c[i] == c[i - 1]) continue;
        if (c[i] > remain) break;
        path.push_back(c[i]);  // @16
        find2(c, i + 1, remain - c[i], path, res);
        path.pop_back();
    }
}

vector<vector<int>> combinationSum2(vector<int>& candidates, int target) {
    sort(candidates.begin(), candidates.end());
    vector<vector<int>> res;
    vector<int> path;
    find2(candidates, 0, target, path, res);
    return res;
}
//@end

//@code exist
bool match(vector<vector<char>>& b, const string& word, int k, int r, int c) {
    if (k == (int)word.size()) return true;
    if (r < 0 || c < 0 || r >= (int)b.size() || c >= (int)b[0].size() || b[r][c] != word[k]) return false;
    char saved = b[r][c];
    b[r][c] = '#';  // @12
    bool found = match(b, word, k + 1, r + 1, c) || match(b, word, k + 1, r - 1, c)
        || match(b, word, k + 1, r, c + 1) || match(b, word, k + 1, r, c - 1);
    b[r][c] = saved;  // @15
    return found;
}

bool exist(vector<vector<char>>& board, string word) {
    for (int r = 0; r < (int)board.size(); r++)
        for (int c = 0; c < (int)board[0].size(); c++)
            if (match(board, word, 0, r, c)) return true;
    return false;
}
//@end

//@code partition
bool isPal(const string& s, int l, int r) {
    while (l < r) if (s[l++] != s[r--]) return false;
    return true;
}

void cut(const string& s, int start, vector<string>& path, vector<vector<string>>& res) {
    if (start == (int)s.size()) {
        res.push_back(path);  // @9
        return;
    }
    for (int i = start; i < (int)s.size(); i++) {
        if (!isPal(s, start, i)) {
            continue;  // @14
        }
        path.push_back(s.substr(start, i - start + 1));  // @16
        cut(s, i + 1, path, res);
        path.pop_back();
    }
}

vector<vector<string>> partition(string s) {
    vector<vector<string>> res;
    vector<string> path;
    cut(s, 0, path, res);
    return res;
}
//@end

//@code solveNQueens
void place(int n, int r, vector<int>& queens, set<int>& cols, set<int>& diag, set<int>& anti, vector<vector<string>>& res) {
    if (r == n) {
        vector<string> board;
        for (int c : queens) {
            string row(n, '.');
            row[c] = 'Q';
            board.push_back(row);
        }
        res.push_back(board);  // @16
        return;
    }
    for (int c = 0; c < n; c++) {
        if (cols.count(c) || diag.count(r - c) || anti.count(r + c)) continue;
        queens[r] = c;
        cols.insert(c);
        diag.insert(r - c);
        anti.insert(r + c);  // @24
        place(n, r + 1, queens, cols, diag, anti, res);
        cols.erase(c);
        diag.erase(r - c);
        anti.erase(r + c);
    }
}

vector<vector<string>> solveNQueens(int n) {
    vector<vector<string>> res;
    vector<int> queens(n);
    set<int> cols, diag, anti;
    place(n, 0, queens, cols, diag, anti, res);
    return res;
}
//@end
