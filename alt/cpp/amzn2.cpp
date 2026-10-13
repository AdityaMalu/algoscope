//@code copyRandomList
class Node {
public:
    int val;
    Node *next = nullptr, *random = nullptr;
    Node(int v) : val(v) {}
};

Node* copyRandomList(Node* head) {
    unordered_map<Node*, Node*> copies{{nullptr, nullptr}};
    for (Node* cur = head; cur; cur = cur->next) {
        copies[cur] = new Node(cur->val);  // @10
    }
    for (Node* cur = head; cur; cur = cur->next) {
        copies[cur]->next = copies[cur->next];
        copies[cur]->random = copies[cur->random];  // @14
    }
    return copies[head];
}
//@end

Node* buildR(const vector<pair<int, int>>& spec) {
    vector<Node*> ns;
    for (auto& [v, r] : spec) ns.push_back(new Node(v));
    for (size_t i = 0; i < ns.size(); i++) {
        if (i + 1 < ns.size()) ns[i]->next = ns[i + 1];
        if (spec[i].second >= 0) ns[i]->random = ns[spec[i].second];
    }
    return ns.empty() ? nullptr : ns[0];
}

Raw showR(Node* h) {
    vector<Node*> order;
    for (Node* c = h; c; c = c->next) order.push_back(c);
    string s = "[";
    for (size_t i = 0; i < order.size(); i++) {
        int pos = -1;
        for (size_t j = 0; j < order.size(); j++) if (order[j] == order[i]->random) pos = j;
        s += (i ? ", [" : "[") + to_string(order[i]->val) + ", " + (pos < 0 ? "null" : to_string(pos)) + "]";
    }
    return {s + "]"};
}

//@code decodeString
string decodeString(string s) {
    stack<pair<string, int>> st;
    string cur;
    int k = 0;
    for (char c : s) {
        if (isdigit(c)) {
            k = k * 10 + (c - '0');
        } else if (c == '[') {
            st.push({cur, k});
            cur.clear();
            k = 0;  // @14
        } else if (c == ']') {
            auto [outer, times] = st.top(); st.pop();
            for (int t = 0; t < times; t++) outer += cur;
            cur = outer;  // @19
        } else {
            cur += c;  // @21
        }
    }
    return cur;
}
//@end

//@code findDuplicate
int findDuplicate(vector<int>& nums) {
    int slow = 0, fast = 0;
    do {
        slow = nums[slow];
        fast = nums[nums[fast]];  // @5
    } while (slow != fast);  // @6
    slow = 0;
    while (slow != fast) {
        slow = nums[slow];
        fast = nums[fast];  // @10
    }
    return slow;  // @12
}
//@end

//@code isValidSudoku
bool isValidSudoku(vector<vector<char>>& board) {
    unordered_set<string> seen;
    for (int r = 0; r < 9; r++)
        for (int c = 0; c < 9; c++) {
            char d = board[r][c];
            if (d == '.') continue;
            string dd(1, d), box = to_string((r / 3) * 3 + c / 3);
            bool fresh = seen.insert(dd + " in row " + to_string(r)).second && seen.insert(dd + " in col " + to_string(c)).second && seen.insert(dd + " in box " + box).second;  // @8
            if (!fresh) {
                return false;  // @10
            }
        }
    return true;  // @13
}
//@end

//@code calculate
int calculate(string s) {
    vector<int> st;
    int num = 0;
    char op = '+';
    for (int i = 0; i < (int)s.size(); i++) {
        char c = s[i];
        if (isdigit(c)) num = num * 10 + (c - '0');
        if ((!isdigit(c) && c != ' ') || i == (int)s.size() - 1) {
            switch (op) {  // @9
                case '+': st.push_back(num); break;
                case '-': st.push_back(-num); break;
                case '*': st.back() *= num; break;
                case '/': st.back() /= num; break;
            }
            op = c;
            num = 0;
        }
    }
    return accumulate(st.begin(), st.end(), 0);  // @20
}
//@end

//@code maxSlidingWindow
vector<int> maxSlidingWindow(vector<int>& nums, int k) {
    vector<int> res;
    deque<int> dq;
    for (int i = 0; i < (int)nums.size(); i++) {
        if (!dq.empty() && dq.front() <= i - k) dq.pop_front();
        while (!dq.empty() && nums[dq.back()] < nums[i]) dq.pop_back();
        dq.push_back(i);
        if (i >= k - 1) res.push_back(nums[dq.front()]);  // @8
    }
    return res;
}
//@end

//@code firstMissingPositive
int firstMissingPositive(vector<int>& nums) {
    int n = nums.size();
    for (int i = 0; i < n; i++) {
        while (nums[i] >= 1 && nums[i] <= n && nums[nums[i] - 1] != nums[i]) {
            swap(nums[i], nums[nums[i] - 1]);  // @7
        }
    }
    for (int i = 0; i < n; i++) {
        if (nums[i] != i + 1) {
            return i + 1;  // @12
        }
    }
    return n + 1;  // @15
}
//@end
