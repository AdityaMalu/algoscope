//@code RandomizedSet
class RandomizedSet {
    vector<int> items;
    unordered_map<int, int> index;

public:
    bool insert(int val) {
        if (index.count(val)) {
            return false;  // @8
        }
        index[val] = items.size();
        items.push_back(val);  // @11
        return true;
    }

    bool remove(int val) {
        auto it = index.find(val);
        if (it == index.end()) {
            return false;  // @18
        }
        int i = it->second, last = items.back();
        items[i] = last;
        index[last] = i;
        items.pop_back();
        index.erase(val);  // @24
        return true;
    }

    int getRandom() {
        return items[rand() % items.size()];  // @30
    }
};
//@end

//@code TimeMap
class TimeMap {
    unordered_map<string, vector<pair<int, string>>> data;

public:
    void set(string key, string value, int timestamp) {
        data[key].push_back({timestamp, value});  // @7
    }

    string get(string key, int timestamp) {
        auto it = data.find(key);
        if (it == data.end()) {
            return "";  // @13
        }
        auto& h = it->second;
        int lo = 0, hi = h.size();
        while (lo < hi) {
            int mid = (lo + hi) / 2;
            if (h[mid].first <= timestamp) lo = mid + 1;
            else hi = mid;
        }
        return lo == 0 ? "" : h[lo - 1].second;  // @21
    }
};
//@end

//@code UndergroundSystem
class UndergroundSystem {
    unordered_map<int, pair<string, int>> checkIns;
    map<pair<string, string>, pair<double, int>> routes;

public:
    void checkIn(int id, string station, int t) {
        checkIns[id] = {station, t};  // @8
    }

    void checkOut(int id, string station, int t) {
        auto [start, t0] = checkIns[id];
        checkIns.erase(id);
        auto& r = routes[{start, station}];
        r.first += t - t0;
        r.second++;  // @16
    }

    double getAverageTime(string startStation, string endStation) {
        auto& r = routes[{startStation, endStation}];
        return r.first / r.second;  // @21
    }
};
//@end

//@code SnapshotArray
class SnapshotArray {
    vector<vector<pair<int, int>>> history;
    int snapId = 0;

public:
    SnapshotArray(int length) : history(length, {{0, 0}}) {}

    void set(int index, int val) {
        auto& h = history[index];
        if (h.back().first == snapId) h.back().second = val;
        else h.push_back({snapId, val});  // @16
    }

    int snap() {
        return snapId++;  // @20
    }

    int get(int index, int id) {
        auto& h = history[index];
        auto it = upper_bound(h.begin(), h.end(), make_pair(id, INT_MAX));  // @29
        return prev(it)->second;
    }
};
//@end

//@code Twitter
class Twitter {
    int time = 0;
    unordered_map<int, vector<pair<int, int>>> tweets;
    unordered_map<int, unordered_set<int>> follows;

public:
    void postTweet(int userId, int tweetId) {
        tweets[userId].push_back({time++, tweetId});  // @7
    }

    vector<int> getNewsFeed(int userId) {
        priority_queue<array<int, 4>> pq;  // {time, tweetId, user, index}
        unordered_set<int> users = follows[userId];
        users.insert(userId);
        for (int u : users) {
            auto& list = tweets[u];
            if (!list.empty()) pq.push({list.back().first, list.back().second, u, (int)list.size() - 1});
        }
        vector<int> feed;
        while (!pq.empty() && feed.size() < 10) {
            auto [t, id, u, i] = pq.top(); pq.pop();
            feed.push_back(id);
            if (i > 0) {
                auto& prev = tweets[u][i - 1];
                pq.push({prev.first, prev.second, u, i - 1});  // @24
            }
        }
        return feed;
    }

    void follow(int followerId, int followeeId) {
        if (followerId != followeeId) follows[followerId].insert(followeeId);  // @31
    }

    void unfollow(int followerId, int followeeId) {
        follows[followerId].erase(followeeId);  // @35
    }
};
//@end

//@code FreqStack
class FreqStack {
    unordered_map<int, int> freq;
    unordered_map<int, vector<int>> group;
    int maxFreq = 0;

public:
    void push(int x) {
        int f = ++freq[x];
        maxFreq = max(maxFreq, f);
        group[f].push_back(x);  // @9
    }

    int pop() {
        int x = group[maxFreq].back();
        group[maxFreq].pop_back();
        freq[x]--;
        if (group[maxFreq].empty()) maxFreq--;  // @15
        return x;
    }
};
//@end
