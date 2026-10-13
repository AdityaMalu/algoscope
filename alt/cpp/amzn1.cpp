//@code subarraySum
int subarraySum(vector<int>& nums, int k) {
    unordered_map<int, int> seen{{0, 1}};
    int prefix = 0, count = 0;  // @4
    for (int x : nums) {
        prefix += x;
        auto it = seen.find(prefix - k);
        if (it != seen.end()) count += it->second;
        seen[prefix]++;  // @9
    }
    return count;
}
//@end

//@code findAnagrams
vector<int> findAnagrams(string s, string p) {
    vector<int> res;
    if (p.size() > s.size()) return res;
    array<int, 26> need{}, win{};
    for (char c : p) need[c - 'a']++;
    for (int i = 0; i < (int)s.size(); i++) {
        win[s[i] - 'a']++;
        if (i >= (int)p.size()) win[s[i - p.size()] - 'a']--;
        int start = i - (int)p.size() + 1;
        if (start >= 0 && win == need) res.push_back(start);  // @10
    }
    return res;
}
//@end

//@code compress
int compress(vector<char>& chars) {
    int write = 0, read = 0, n = chars.size();
    while (read < n) {
        char c = chars[read];
        int start = read;
        while (read < n && chars[read] == c) read++;
        int count = read - start;
        chars[write++] = c;
        if (count > 1) {
            for (char d : to_string(count)) chars[write++] = d;  // @10
        }
    }
    return write;
}
//@end

//@code nextPermutation
void nextPermutation(vector<int>& nums) {
    int i = (int)nums.size() - 2;
    while (i >= 0 && nums[i] >= nums[i + 1]) i--;  // @3
    if (i >= 0) {
        int j = (int)nums.size() - 1;
        while (nums[j] <= nums[i]) j--;
        swap(nums[i], nums[j]);  // @7
    }
    reverse(nums.begin() + i + 1, nums.end());  // @9
}
//@end

//@code spiralOrder
vector<int> spiralOrder(vector<vector<int>>& matrix) {
    vector<int> res;
    int top = 0, bottom = (int)matrix.size() - 1, left = 0, right = (int)matrix[0].size() - 1;
    while (top <= bottom && left <= right) {
        for (int c = left; c <= right; c++) res.push_back(matrix[top][c]);  // @5
        top++;
        for (int r = top; r <= bottom; r++) res.push_back(matrix[r][right]);  // @7
        right--;
        if (top <= bottom) {
            for (int c = right; c >= left; c--) res.push_back(matrix[bottom][c]);  // @10
            bottom--;
        }
        if (left <= right) {
            for (int r = bottom; r >= top; r--) res.push_back(matrix[r][left]);  // @14
            left++;
        }
    }
    return res;
}
//@end

//@code rotate
void rotate(vector<vector<int>>& matrix) {
    int n = matrix.size();
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++)
            swap(matrix[i][j], matrix[j][i]);  // @7
    for (auto& row : matrix)
        reverse(row.begin(), row.end());  // @13
}
//@end

//@code setZeroes
void setZeroes(vector<vector<int>>& matrix) {
    int m = matrix.size(), n = matrix[0].size();
    bool firstRow = false, firstCol = false;
    for (int j = 0; j < n; j++) if (matrix[0][j] == 0) firstRow = true;
    for (int i = 0; i < m; i++) if (matrix[i][0] == 0) firstCol = true;
    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            if (matrix[i][j] == 0) {
                matrix[i][0] = 0;
                matrix[0][j] = 0;  // @10
            }
    for (int i = 1; i < m; i++)
        for (int j = 1; j < n; j++)
            if (matrix[i][0] == 0 || matrix[0][j] == 0) matrix[i][j] = 0;  // @14
    if (firstRow) for (int j = 0; j < n; j++) matrix[0][j] = 0;
    if (firstCol) for (int i = 0; i < m; i++) matrix[i][0] = 0;  // @16
}
//@end
