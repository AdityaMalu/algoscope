//@code maxProfit
int maxProfit(vector<int>& prices) {
    int minPrice = INT_MAX, best = 0;
    for (int p : prices) {
        minPrice = min(minPrice, p);
        best = max(best, p - minPrice);  // @5
    }
    return best;
}
//@end

//@code lengthOfLongestSubstring
int lengthOfLongestSubstring(string s) {
    unordered_map<char, int> last;
    int l = 0, best = 0;
    for (int r = 0; r < (int)s.size(); r++) {
        char c = s[r];
        if (last.count(c) && last[c] >= l) {
            l = last[c] + 1;  // @7
        }
        last[c] = r;
        best = max(best, r - l + 1);  // @10
    }
    return best;
}
//@end

//@code longestOnes
int longestOnes(vector<int>& nums, int k) {
    int l = 0, zeros = 0, best = 0;
    for (int r = 0; r < (int)nums.size(); r++) {
        if (nums[r] == 0) zeros++;
        while (zeros > k) {
            if (nums[l] == 0) zeros--;
            l++;  // @7
        }
        best = max(best, r - l + 1);  // @9
    }
    return best;
}
//@end

//@code totalFruit
int totalFruit(vector<int>& fruits) {
    unordered_map<int, int> count;
    int l = 0, best = 0;
    for (int r = 0; r < (int)fruits.size(); r++) {
        count[fruits[r]]++;
        while (count.size() > 2) {
            if (--count[fruits[l]] == 0) count.erase(fruits[l]);
            l++;  // @9
        }
        best = max(best, r - l + 1);  // @11
    }
    return best;
}
//@end

//@code characterReplacement
int characterReplacement(string s, int k) {
    int count[26] = {0};
    int l = 0, maxCount = 0, best = 0;
    for (int r = 0; r < (int)s.size(); r++) {
        maxCount = max(maxCount, ++count[s[r] - 'A']);
        if (r - l + 1 - maxCount > k) {
            count[s[l] - 'A']--;  // @7
            l++;
        }
        best = max(best, r - l + 1);  // @10
    }
    return best;
}
//@end

//@code checkInclusion
bool checkInclusion(string s1, string s2) {
    if (s1.size() > s2.size()) return false;
    int need[26] = {0}, win[26] = {0};
    for (size_t i = 0; i < s1.size(); i++) {
        need[s1[i] - 'a']++;
        win[s2[i] - 'a']++;
    }
    int matches = 0;
    for (int i = 0; i < 26; i++) if (need[i] == win[i]) matches++;
    int m = s1.size();  // @10
    for (int r = m; r < (int)s2.size(); r++) {
        if (matches == 26) {
            return true;  // @13
        }
        int in = s2[r] - 'a', out = s2[r - m] - 'a';
        win[in]++;
        if (win[in] == need[in]) matches++;
        else if (win[in] == need[in] + 1) matches--;
        win[out]--;
        if (win[out] == need[out]) matches++;
        else if (win[out] == need[out] - 1) matches--;  // @21
    }
    return matches == 26;  // @23
}
//@end

//@code minWindow
string minWindow(string s, string t) {
    int need[128] = {0};
    for (char c : t) need[c]++;
    int missing = t.size(), l = 0, bestL = 0, bestLen = INT_MAX;  // @4
    for (int r = 0; r < (int)s.size(); r++) {
        if (need[s[r]]-- > 0) {
            missing--;  // @7
        }
        while (missing == 0) {
            if (r - l + 1 < bestLen) {
                bestLen = r - l + 1;
                bestL = l;  // @12
            }
            if (++need[s[l]] > 0) {
                missing++;  // @15
            }
            l++;
        }
    }
    return bestLen == INT_MAX ? "" : s.substr(bestL, bestLen);  // @20
}
//@end
