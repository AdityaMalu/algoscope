//@code findKthLargest
int findKthLargest(vector<int>& nums, int k) {
    priority_queue<int, vector<int>, greater<int>> heap;
    for (int x : nums) {
        heap.push(x);
        if ((int)heap.size() > k) {
            heap.pop();  // @6
        }
    }
    return heap.top();  // @9
}
//@end

//@code lastStoneWeight
int lastStoneWeight(vector<int>& stones) {
    priority_queue<int> heap(stones.begin(), stones.end());  // @3
    while (heap.size() > 1) {
        int y = heap.top(); heap.pop();
        int x = heap.top(); heap.pop();
        if (y != x) heap.push(y - x);  // @6
    }
    return heap.empty() ? 0 : heap.top();
}
//@end

//@code kClosest
vector<vector<int>> kClosest(vector<vector<int>>& points, int k) {
    auto dist = [](const vector<int>& p) { return p[0] * p[0] + p[1] * p[1]; };
    auto cmp = [&](const vector<int>& a, const vector<int>& b) { return dist(a) < dist(b); };
    priority_queue<vector<int>, vector<vector<int>>, decltype(cmp)> heap(cmp);
    for (auto& p : points) {
        heap.push(p);
        if ((int)heap.size() > k) heap.pop();  // @5
    }
    vector<vector<int>> res;
    while (!heap.empty()) { res.push_back(heap.top()); heap.pop(); }
    return res;
}
//@end

//@code leastInterval
int leastInterval(vector<char>& tasks, int n) {
    int count[26] = {0};
    for (char t : tasks) count[t - 'A']++;
    int maxFreq = *max_element(count, count + 26);
    int countMax = 0;
    for (int c : count) if (c == maxFreq) countMax++;  // @7
    int frames = (maxFreq - 1) * (n + 1) + countMax;  // @8
    return max((int)tasks.size(), frames);  // @9
}
//@end

//@code topKFrequentWords
vector<string> topKFrequent(vector<string>& words, int k) {
    unordered_map<string, int> count;
    for (auto& w : words) count[w]++;  // @3
    // top of the heap = the word to evict first: lower count, then alphabetically later
    auto cmp = [&](const string& a, const string& b) {
        return count[a] != count[b] ? count[a] > count[b] : a < b;
    };
    priority_queue<string, vector<string>, decltype(cmp)> heap(cmp);
    for (auto& [w, c] : count) {
        heap.push(w);
        if ((int)heap.size() > k) {
            heap.pop();  // @8
        }
    }
    vector<string> res;
    while (!heap.empty()) { res.push_back(heap.top()); heap.pop(); }  // @12
    reverse(res.begin(), res.end());
    return res;
}
//@end

//@code reorganizeString
string reorganizeString(string s) {
    int count[26] = {0};
    for (char c : s) count[c - 'a']++;
    priority_queue<pair<int, char>> heap;
    for (int i = 0; i < 26; i++) {
        if (count[i] > ((int)s.size() + 1) / 2) {
            return "";  // @7
        }
        if (count[i] > 0) heap.push({count[i], char('a' + i)});
    }
    string out;  // @11
    while (heap.size() >= 2) {
        auto [n1, a] = heap.top(); heap.pop();
        auto [n2, b] = heap.top(); heap.pop();
        out += a; out += b;
        if (--n1 > 0) heap.push({n1, a});
        if (--n2 > 0) heap.push({n2, b});  // @16
    }
    if (!heap.empty()) {
        out += heap.top().second;  // @19
    }
    return out;
}
//@end

//@code MedianFinder
class MedianFinder {
    priority_queue<int> low;                             // smaller half, max on top
    priority_queue<int, vector<int>, greater<int>> high;  // larger half, min on top

public:
    void addNum(int num) {
        low.push(num);
        high.push(low.top()); low.pop();
        if (high.size() > low.size()) { low.push(high.top()); high.pop(); }  // @8
    }

    double findMedian() {
        if (low.size() > high.size()) return low.top();  // @12
        return (low.top() + high.top()) / 2.0;
    }
};
//@end
