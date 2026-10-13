//@code maxSubArray
int maxSubArray(vector<int>& nums) {
    int cur = nums[0], best = nums[0];  // @2
    for (size_t i = 1; i < nums.size(); i++) {
        cur = max(nums[i], cur + nums[i]);
        best = max(best, cur);  // @5
    }
    return best;
}
//@end

//@code canJump
bool canJump(vector<int>& nums) {
    int reach = 0;
    for (int i = 0; i < (int)nums.size(); i++) {
        if (i > reach) {
            return false;  // @5
        }
        reach = max(reach, i + nums[i]);  // @7
        if (reach >= (int)nums.size() - 1) {
            return true;  // @9
        }
    }
    return true;
}
//@end

//@code merge
vector<vector<int>> merge(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end());  // @2
    vector<vector<int>> res;
    for (auto& cur : intervals) {
        if (!res.empty() && cur[0] <= res.back()[1]) {
            res.back()[1] = max(res.back()[1], cur[1]);  // @8
        } else {
            res.push_back(cur);  // @10
        }
    }
    return res;
}
//@end

//@code insert
vector<vector<int>> insert(vector<vector<int>>& intervals, vector<int>& newInterval) {
    vector<vector<int>> res;
    int i = 0, n = intervals.size();
    vector<int> add = newInterval;
    while (i < n && intervals[i][1] < add[0]) {
        res.push_back(intervals[i]);  // @6
        i++;
    }
    while (i < n && intervals[i][0] <= add[1]) {
        add[0] = min(add[0], intervals[i][0]);
        add[1] = max(add[1], intervals[i][1]);  // @11
        i++;
    }
    res.push_back(add);  // @14
    while (i < n) {
        res.push_back(intervals[i]);
        i++;  // @17
    }
    return res;
}
//@end

//@code jump
int jump(vector<int>& nums) {
    int jumps = 0, end = 0, far = 0;
    for (int i = 0; i + 1 < (int)nums.size(); i++) {
        far = max(far, i + nums[i]);  // @4
        if (i == end) {
            jumps++;
            end = far;  // @7
        }
    }
    return jumps;
}
//@end

//@code canCompleteCircuit
int canCompleteCircuit(vector<int>& gas, vector<int>& cost) {
    int total = 0, tank = 0, start = 0;
    for (int i = 0; i < (int)gas.size(); i++) {
        int gain = gas[i] - cost[i];
        total += gain;
        tank += gain;
        if (tank < 0) {  // @7
            start = i + 1;
            tank = 0;
        }
    }
    return total >= 0 ? start : -1;  // @12
}
//@end

//@code eraseOverlapIntervals
int eraseOverlapIntervals(vector<vector<int>>& intervals) {
    sort(intervals.begin(), intervals.end(), [](auto& a, auto& b) { return a[1] < b[1]; });  // @2
    int kept = 0;
    long long lastEnd = LLONG_MIN;
    for (auto& iv : intervals) {
        if (iv[0] >= lastEnd) {  // @5
            kept++;
            lastEnd = iv[1];
        }
    }
    return intervals.size() - kept;
}
//@end

//@code findMinArrowShots
int findMinArrowShots(vector<vector<int>>& points) {
    sort(points.begin(), points.end(), [](auto& a, auto& b) { return a[1] < b[1]; });  // @2
    int arrows = 1;
    long long x = points[0][1];  // @4
    for (size_t i = 1; i < points.size(); i++) {
        if (points[i][0] > x) {  // @6
            arrows++;
            x = points[i][1];
        }
    }
    return arrows;
}
//@end
