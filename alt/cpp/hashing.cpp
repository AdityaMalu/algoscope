//@code twoSum
vector<int> twoSum(vector<int>& nums, int target) {
    unordered_map<int, int> seen;  // @2
    for (int i = 0; i < (int)nums.size(); i++) {
        int need = target - nums[i];  // @4
        auto it = seen.find(need);
        if (it != seen.end()) {
            return {it->second, i};  // @7
        }
        seen[nums[i]] = i;  // @9
    }
    return {};  // @11
}
//@end

//@code isAnagram
bool isAnagram(string s, string t) {
    if (s.size() != t.size()) {
        return false;  // @3
    }
    int count[26] = {0};  // @5
    for (size_t i = 0; i < s.size(); i++) {
        count[s[i] - 'a']++;
        count[t[i] - 'a']--;  // @8
    }
    for (int k = 0; k < 26; k++) {
        if (count[k] != 0) {
            return false;  // @12
        }
    }
    return true;  // @15
}
//@end

//@code containsDuplicate
bool containsDuplicate(vector<int>& nums) {
    unordered_set<int> seen;
    for (int x : nums) {
        if (!seen.insert(x).second) {  // @4
            return true;  // @5
        }
    }
    return false;  // @8
}
//@end

//@code majorityElement
int majorityElement(vector<int>& nums) {
    int candidate = 0, count = 0;
    for (int x : nums) {
        if (count == 0) {
            candidate = x;  // @5
        }
        count += (x == candidate) ? 1 : -1;  // @7
    }
    return candidate;  // @9
}
//@end

//@code groupAnagrams
vector<vector<string>> groupAnagrams(vector<string>& strs) {
    unordered_map<string, vector<string>> groups;
    for (const string& s : strs) {
        string key = s;
        sort(key.begin(), key.end());  // @6
        groups[key].push_back(s);  // @7
    }
    vector<vector<string>> res;
    for (auto& [key, words] : groups) res.push_back(words);
    return res;  // @9
}
//@end

//@code topKFrequent
vector<int> topKFrequent(vector<int>& nums, int k) {
    unordered_map<int, int> freq;
    for (int x : nums) freq[x]++;  // @3
    vector<vector<int>> bucket(nums.size() + 1);
    for (auto& [x, f] : freq) {
        bucket[f].push_back(x);  // @8
    }
    vector<int> res;
    for (int f = (int)bucket.size() - 1; f > 0 && (int)res.size() < k; f--) {
        for (int x : bucket[f]) {
            if ((int)res.size() == k) break;
            res.push_back(x);  // @16
        }
    }
    return res;  // @19
}
//@end

//@code productExceptSelf
vector<int> productExceptSelf(vector<int>& nums) {
    int n = nums.size();
    vector<int> res(n, 1);  // @4
    for (int i = 1; i < n; i++) {
        res[i] = res[i - 1] * nums[i - 1];  // @6
    }
    int suffix = 1;
    for (int i = n - 1; i >= 0; i--) {
        res[i] *= suffix;  // @10
        suffix *= nums[i];
    }
    return res;  // @13
}
//@end

//@code longestConsecutive
int longestConsecutive(vector<int>& nums) {
    unordered_set<int> set(nums.begin(), nums.end());  // @3
    int best = 0;
    for (int x : set) {
        if (set.count(x - 1)) {
            continue;  // @7
        }
        int len = 1;  // @9
        while (set.count(x + len)) {
            len++;  // @11
        }
        best = max(best, len);  // @13
    }
    return best;
}
//@end
