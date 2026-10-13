//@code climbStairs
int climbStairs(int n) {
    int prev2 = 1, prev1 = 1;  // @2
    for (int i = 2; i <= n; i++) {
        int cur = prev1 + prev2;  // @4
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
//@end

//@code minCostClimbingStairs
int minCostClimbingStairs(vector<int>& cost) {
    int n = cost.size();
    vector<int> dp(n + 1);
    for (int i = 2; i <= n; i++) {
        dp[i] = min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);  // @5
    }
    return dp[n];  // @7
}
//@end

//@code rob
int rob(vector<int>& nums) {
    int prev2 = 0, prev1 = 0;
    for (int x : nums) {
        int cur = max(prev1, prev2 + x);  // @4
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}
//@end

//@code robCircle
int robRange(vector<int>& nums, int lo, int hi) {
    int prev2 = 0, prev1 = 0;
    for (int i = lo; i <= hi; i++) {
        int cur = max(prev1, prev2 + nums[i]);  // @11
        prev2 = prev1;
        prev1 = cur;
    }
    return prev1;
}

int robCircle(vector<int>& nums) {
    if (nums.size() == 1) return nums[0];
    int a = robRange(nums, 0, nums.size() - 2);  // @3
    int b = robRange(nums, 1, nums.size() - 1);  // @4
    return max(a, b);
}
//@end

//@code coinChange
int coinChange(vector<int>& coins, int amount) {
    vector<int> dp(amount + 1, amount + 1);
    dp[0] = 0;  // @4
    for (int a = 1; a <= amount; a++) {
        for (int c : coins) {
            if (c <= a && dp[a - c] + 1 < dp[a]) {
                dp[a] = dp[a - c] + 1;  // @8
            }
        }
    }
    return dp[amount] > amount ? -1 : dp[amount];  // @13
}
//@end

//@code lengthOfLIS
int lengthOfLIS(vector<int>& nums) {
    vector<int> tails;
    for (int x : nums) {
        auto it = lower_bound(tails.begin(), tails.end(), x);
        if (it == tails.end()) tails.push_back(x);
        else *it = x;  // @13
    }
    return tails.size();
}
//@end

//@code wordBreak
bool wordBreak(string s, vector<string>& wordDict) {
    unordered_set<string> words(wordDict.begin(), wordDict.end());
    vector<bool> dp(s.size() + 1);
    dp[0] = true;
    for (size_t i = 1; i <= s.size(); i++) {
        for (size_t j = 0; j < i; j++) {
            if (dp[j] && words.count(s.substr(j, i - j))) {
                dp[i] = true;  // @8
                break;
            }
        }
    }
    return dp[s.size()];  // @13
}
//@end

//@code numDecodings
int numDecodings(string s) {
    int n = s.size();
    vector<int> dp(n + 1);
    dp[0] = 1;
    for (int i = 1; i <= n; i++) {
        if (s[i - 1] != '0') dp[i] += dp[i - 1];
        if (i >= 2) {
            int two = stoi(s.substr(i - 2, 2));
            if (two >= 10 && two <= 26) dp[i] += dp[i - 2];  // @10
        }
    }
    return dp[n];
}
//@end
