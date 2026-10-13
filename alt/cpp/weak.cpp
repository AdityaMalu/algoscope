//@code gameOfLife
void gameOfLife(vector<vector<int>>& board) {
    int m = board.size(), n = board[0].size();
    for (int r = 0; r < m; r++) {
        for (int c = 0; c < n; c++) {
            int live = 0;
            for (int dr = -1; dr <= 1; dr++)
                for (int dc = -1; dc <= 1; dc++) {
                    if (dr == 0 && dc == 0) continue;
                    int nr = r + dr, nc = c + dc;
                    if (nr >= 0 && nc >= 0 && nr < m && nc < n) live += board[nr][nc] & 1;
                }
            bool alive = board[r][c] & 1;
            if ((alive && (live == 2 || live == 3)) || (!alive && live == 3)) board[r][c] |= 2;  // @13
        }
    }
    for (int r = 0; r < m; r++)
        for (int c = 0; c < n; c++) board[r][c] >>= 1;  // @17
}
//@end

//@code threeSumClosest
int threeSumClosest(vector<int>& nums, int target) {
    sort(nums.begin(), nums.end());
    int closest = nums[0] + nums[1] + nums[2];
    for (int i = 0; i + 2 < (int)nums.size(); i++) {
        int l = i + 1, r = (int)nums.size() - 1;
        while (l < r) {
            int sum = nums[i] + nums[l] + nums[r];
            if (abs(sum - target) < abs(closest - target)) closest = sum;  // @8
            if (sum == target) return sum;
            if (sum < target) l++;
            else r--;
        }
    }
    return closest;
}
//@end

//@code numberToWords
const vector<string> BELOW_20 = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
const vector<string> TENS = {"", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};
const vector<string> SCALES = {"", "Thousand", "Million", "Billion"};

string three(int n) {
    string s;
    if (n >= 100) {
        s += BELOW_20[n / 100] + " Hundred ";
        n %= 100;
    }
    if (n >= 20) {
        s += TENS[n / 10] + " ";
        n %= 10;
    }
    if (n > 0) s += BELOW_20[n] + " ";
    return s;
}

string numberToWords(int num) {
    if (num == 0) return "Zero";
    string res;
    int scale = 0;
    while (num > 0) {
        int chunk = num % 1000;
        if (chunk != 0) res = three(chunk) + SCALES[scale] + " " + res;  // @11
        num /= 1000;
        scale++;
    }
    // collapse the extra spaces left by empty scale words
    string out;
    istringstream in(res);
    for (string w; in >> w;) out += (out.empty() ? "" : " ") + w;
    return out;
}
//@end
