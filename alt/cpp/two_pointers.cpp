//@code isPalindrome
bool isPalindrome(string s) {
    int l = 0, r = (int)s.size() - 1;
    while (l < r) {
        while (l < r && !isalnum((unsigned char)s[l])) l++;
        while (l < r && !isalnum((unsigned char)s[r])) r--;
        if (tolower(s[l]) != tolower(s[r])) {  // @6
            return false;  // @7
        }
        l++;
        r--;
    }
    return true;  // @12
}
//@end

//@code moveZeroes
void moveZeroes(vector<int>& nums) {
    int w = 0;
    for (int r = 0; r < (int)nums.size(); r++) {
        if (nums[r] == 0) {
            continue;  // @5
        }
        swap(nums[w], nums[r]);  // @7
        w++;  // @8
    }
}
//@end

//@code twoSumSorted
vector<int> twoSum(vector<int>& numbers, int target) {
    int l = 0, r = (int)numbers.size() - 1;
    while (l < r) {
        int sum = numbers[l] + numbers[r];  // @4
        if (sum == target) {
            return {l + 1, r + 1};  // @6
        }
        if (sum < target) l++;
        else r--;
    }
    return {-1, -1};
}
//@end

//@code sortColors
void sortColors(vector<int>& nums) {
    int lo = 0, mid = 0, hi = (int)nums.size() - 1;  // @2
    while (mid <= hi) {
        if (nums[mid] == 0) {
            swap(nums[lo], nums[mid]);  // @5
            lo++;
            mid++;
        } else if (nums[mid] == 1) {
            mid++;  // @9
        } else {
            swap(nums[mid], nums[hi]);  // @11
            hi--;  // @17
        }
    }
}
//@end

//@code threeSum
vector<vector<int>> threeSum(vector<int>& nums) {
    sort(nums.begin(), nums.end());  // @2
    vector<vector<int>> res;
    for (int i = 0; i + 2 < (int)nums.size(); i++) {
        if (nums[i] > 0) {
            break;  // @6
        }
        if (i > 0 && nums[i] == nums[i - 1]) {
            continue;  // @9
        }
        int l = i + 1, r = (int)nums.size() - 1;
        while (l < r) {
            int sum = nums[i] + nums[l] + nums[r];  // @13
            if (sum < 0) l++;
            else if (sum > 0) r--;
            else {
                res.push_back({nums[i], nums[l], nums[r]});  // @17
                while (l < r && nums[l] == nums[l + 1]) l++;
                while (l < r && nums[r] == nums[r - 1]) r--;
                l++;
                r--;
            }
        }
    }
    return res;
}
//@end

//@code maxArea
int maxArea(vector<int>& height) {
    int l = 0, r = (int)height.size() - 1, best = 0;
    while (l < r) {
        int area = min(height[l], height[r]) * (r - l);
        best = max(best, area);  // @5
        if (height[l] < height[r]) l++;
        else r--;
    }
    return best;
}
//@end

//@code trap
int trap(vector<int>& height) {
    int l = 0, r = (int)height.size() - 1, leftMax = 0, rightMax = 0, total = 0;
    while (l < r) {
        if (height[l] < height[r]) {
            leftMax = max(leftMax, height[l]);
            total += leftMax - height[l];  // @6
            l++;
        } else {
            rightMax = max(rightMax, height[r]);
            total += rightMax - height[r];  // @10
            r--;
        }
    }
    return total;  // @14
}
//@end
