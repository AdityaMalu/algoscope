//@code search
int search(vector<int>& nums, int target) {
    int lo = 0, hi = (int)nums.size() - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;  // @4
        if (nums[mid] == target) {
            return mid;  // @6
        }
        if (nums[mid] < target) {
            lo = mid + 1;  // @9
        } else {
            hi = mid - 1;  // @11
        }
    }
    return -1;  // @14
}
//@end

//@code searchInsert
int searchInsert(vector<int>& nums, int target) {
    int lo = 0, hi = nums.size();
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] < target) {
            lo = mid + 1;  // @6
        } else {
            hi = mid;  // @8
        }
    }
    return lo;  // @11
}
//@end

//@code findMin
int findMin(vector<int>& nums) {
    int lo = 0, hi = (int)nums.size() - 1;
    while (lo < hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] > nums[hi]) {
            lo = mid + 1;  // @6
        } else {
            hi = mid;  // @8
        }
    }
    return nums[lo];  // @11
}
//@end

//@code searchMatrix
bool searchMatrix(vector<vector<int>>& matrix, int target) {
    int m = matrix.size(), n = matrix[0].size();
    int lo = 0, hi = m * n - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        int val = matrix[mid / n][mid % n];  // @6
        if (val == target) {
            return true;  // @8
        }
        if (val < target) lo = mid + 1;
        else hi = mid - 1;
    }
    return false;  // @13
}
//@end

//@code searchRotated
int searchRotated(vector<int>& nums, int target) {
    int lo = 0, hi = (int)nums.size() - 1;
    while (lo <= hi) {
        int mid = lo + (hi - lo) / 2;
        if (nums[mid] == target) {
            return mid;  // @6
        }
        if (nums[lo] <= nums[mid]) {
            if (nums[lo] <= target && target < nums[mid]) hi = mid - 1;  // @9
            else lo = mid + 1;
        } else {
            if (nums[mid] < target && target <= nums[hi]) lo = mid + 1;  // @12
            else hi = mid - 1;
        }
    }
    return -1;  // @16
}
//@end

//@code minEatingSpeed
int minEatingSpeed(vector<int>& piles, int h) {
    int lo = 1, hi = *max_element(piles.begin(), piles.end());  // @3
    while (lo < hi) {
        int k = lo + (hi - lo) / 2;
        long long hours = 0;
        for (int p : piles) hours += (p + k - 1) / k;  // @7
        if (hours <= h) hi = k;
        else lo = k + 1;
    }
    return lo;  // @11
}
//@end

//@code shipWithinDays
int daysNeeded(vector<int>& weights, int cap);

int shipWithinDays(vector<int>& weights, int days) {
    int lo = 0, hi = 0;
    for (int w : weights) { lo = max(lo, w); hi += w; }  // @3
    while (lo < hi) {
        int cap = lo + (hi - lo) / 2;
        int need = daysNeeded(weights, cap);  // @6
        if (need <= days) hi = cap;
        else lo = cap + 1;
    }
    return lo;  // @10
}

int daysNeeded(vector<int>& weights, int cap) {
    int d = 1, load = 0;
    for (int w : weights) {
        if (load + w > cap) { d++; load = 0; }
        load += w;
    }
    return d;
}
//@end
