//@code uniquePaths
int uniquePaths(int m, int n) {
    vector<vector<int>> dp(m, vector<int>(n, 1));
    for (int r = 1; r < m; r++) {
        for (int c = 1; c < n; c++) {
            dp[r][c] = dp[r - 1][c] + dp[r][c - 1];  // @6
        }
    }
    return dp[m - 1][n - 1];
}
//@end

//@code minPathSum
int minPathSum(vector<vector<int>>& grid) {
    int m = grid.size(), n = grid[0].size();
    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            if (r == 0 && c == 0) continue;
            int top = r > 0 ? grid[r - 1][c] : INT_MAX;
            int left = c > 0 ? grid[r][c - 1] : INT_MAX;
            grid[r][c] += min(top, left);  // @8
        }
    }
    return grid[m - 1][n - 1];
}
//@end

//@code longestCommonSubsequence
int longestCommonSubsequence(string a, string b) {
    int m = a.size(), n = b.size();
    vector<vector<int>> dp(m + 1, vector<int>(n + 1));
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (a[i - 1] == b[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1] + 1;  // @7
            } else {
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1]);  // @9
            }
        }
    }
    return dp[m][n];
}
//@end

//@code canPartition
bool canPartition(vector<int>& nums) {
    int total = accumulate(nums.begin(), nums.end(), 0);
    if (total % 2 != 0) {
        return false;  // @5
    }
    int target = total / 2;
    vector<bool> dp(target + 1);
    dp[0] = true;  // @9
    for (int x : nums) {
        for (int s = target; s >= x; s--) {
            dp[s] = dp[s] || dp[s - x];  // @12
        }
        if (dp[target]) {
            return true;  // @15
        }
    }
    return false;
}
//@end

//@code change
int change(int amount, vector<int>& coins) {
    vector<long long> dp(amount + 1);
    dp[0] = 1;
    for (int coin : coins) {
        for (int a = coin; a <= amount; a++) {
            dp[a] += dp[a - coin];  // @6
        }
    }
    return dp[amount];
}
//@end

//@code findTargetSumWays
int findTargetSumWays(vector<int>& nums, int target) {
    int total = accumulate(nums.begin(), nums.end(), 0);
    if (abs(target) > total || (total + target) % 2 != 0) {
        return 0;  // @5
    }
    int want = (total + target) / 2;
    vector<int> dp(want + 1);
    dp[0] = 1;  // @9
    for (int x : nums) {
        for (int s = want; s >= x; s--) {
            dp[s] += dp[s - x];  // @12
        }
    }
    return dp[want];
}
//@end

//@code minDistance
int minDistance(string a, string b) {
    int m = a.size(), n = b.size();
    vector<vector<int>> dp(m + 1, vector<int>(n + 1));
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;  // @5
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (a[i - 1] == b[j - 1]) {
                dp[i][j] = dp[i - 1][j - 1];  // @9
            } else {
                dp[i][j] = 1 + min({dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1]});  // @11
            }
        }
    }
    return dp[m][n];
}
//@end

//@code maxProfitCooldown
int maxProfit(vector<int>& prices) {
    int hold = INT_MIN, sold = 0, rest = 0;
    for (int p : prices) {
        int prevSold = sold;
        sold = hold == INT_MIN ? INT_MIN : hold + p;
        hold = max(hold, rest - p);
        rest = max(rest, prevSold);  // @8
    }
    return max(sold, rest);
}
//@end
