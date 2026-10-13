//@code maximumUnits
int maximumUnits(vector<vector<int>>& boxTypes, int truckSize) {
    sort(boxTypes.begin(), boxTypes.end(), [](auto& a, auto& b) { return a[1] > b[1]; });  // @2
    int total = 0;
    for (size_t i = 0; i < boxTypes.size() && truckSize > 0; i++) {
        int take = min(truckSize, boxTypes[i][0]);
        total += take * boxTypes[i][1];
        truckSize -= take;  // @7
    }
    return total;
}
//@end

//@code numTeams
int numTeams(vector<int>& rating) {
    int n = rating.size(), teams = 0;
    for (int j = 1; j < n - 1; j++) {
        int lessL = 0, moreL = 0, lessR = 0, moreR = 0;
        for (int i = 0; i < j; i++) {
            if (rating[i] < rating[j]) lessL++;
            else moreL++;
        }
        for (int k = j + 1; k < n; k++) {
            if (rating[k] < rating[j]) lessR++;
            else moreR++;
        }
        teams += lessL * moreR + moreL * lessR;  // @13
    }
    return teams;
}
//@end

//@code numberOfWays
long long numberOfWays(string s) {
    long long total0 = count(s.begin(), s.end(), '0'), total1 = s.size() - total0;
    long long left0 = 0, left1 = 0, ways = 0;
    for (char c : s) {
        if (c == '0') {
            ways += left1 * (total1 - left1);  // @11
            left0++;
        } else {
            ways += left0 * (total0 - left0);  // @15
            left1++;
        }
    }
    return ways;
}
//@end

//@code totalStrength
int totalStrength(vector<int>& strength) {
    const long long MOD = 1'000'000'007LL;
    int n = strength.size();
    vector<int> left(n), right(n);
    stack<int> st;
    for (int i = 0; i < n; i++) {
        while (!st.empty() && strength[st.top()] >= strength[i]) st.pop();
        left[i] = st.empty() ? -1 : st.top();
        st.push(i);
    }
    st = stack<int>();
    for (int i = n - 1; i >= 0; i--) {
        while (!st.empty() && strength[st.top()] > strength[i]) st.pop();
        right[i] = st.empty() ? n : st.top();
        st.push(i);  // @15
    }
    vector<long long> prefix(n + 1), pp(n + 2);
    for (int i = 0; i < n; i++) prefix[i + 1] = (prefix[i] + strength[i]) % MOD;
    for (int i = 0; i <= n; i++) pp[i + 1] = (pp[i] + prefix[i]) % MOD;  // @19
    long long ans = 0;
    for (int i = 0; i < n; i++) {
        int l = left[i], r = right[i];
        long long pos = (pp[r + 1] - pp[i + 1] + MOD) % MOD * (i - l) % MOD;
        long long neg = (pp[i + 1] - pp[l + 1] + MOD) % MOD * (r - i) % MOD;
        long long sums = (pos - neg + MOD) % MOD;
        ans = (ans + strength[i] * sums) % MOD;  // @26
    }
    return ans;
}
//@end
