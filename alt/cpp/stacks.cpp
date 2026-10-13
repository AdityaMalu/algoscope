//@code isValid
bool isValid(string s) {
    stack<char> st;
    for (char c : s) {
        if (c == '(' || c == '[' || c == '{') {
            st.push(c);  // @6
            continue;
        }
        char open = c == ')' ? '(' : c == ']' ? '[' : '{';
        if (st.empty() || st.top() != open) {
            return false;  // @11
        }
        st.pop();  // @13
    }
    return st.empty();  // @15
}
//@end

//@code MinStack
class MinStack {
    stack<int> st, mins;

public:
    void push(int x) {
        st.push(x);
        mins.push(mins.empty() ? x : min(x, mins.top()));  // @7
    }

    void pop() {
        st.pop();  // @11
        mins.pop();
    }

    int top() {
        return st.top();  // @16
    }

    int getMin() {
        return mins.top();  // @20
    }
};
//@end

//@code nextGreaterElement
vector<int> nextGreaterElement(vector<int>& nums1, vector<int>& nums2) {
    unordered_map<int, int> next;
    stack<int> st;
    for (int x : nums2) {
        while (!st.empty() && st.top() < x) {
            next[st.top()] = x;  // @7
            st.pop();
        }
        st.push(x);  // @9
    }
    vector<int> res;
    for (int x : nums1) res.push_back(next.count(x) ? next[x] : -1);  // @12
    return res;
}
//@end

//@code evalRPN
int evalRPN(vector<string>& tokens) {
    stack<int> st;
    for (const string& t : tokens) {
        if (t == "+" || t == "-" || t == "*" || t == "/") {
            int b = st.top(); st.pop();
            int a = st.top(); st.pop();
            int r;
            if (t == "+") r = a + b;
            else if (t == "-") r = a - b;
            else if (t == "*") r = a * b;
            else r = a / b;
            st.push(r);  // @13
        } else {
            st.push(stoi(t));  // @15
        }
    }
    return st.top();  // @18
}
//@end

//@code dailyTemperatures
vector<int> dailyTemperatures(vector<int>& temps) {
    vector<int> ans(temps.size());
    stack<int> st;
    for (int i = 0; i < (int)temps.size(); i++) {
        while (!st.empty() && temps[st.top()] < temps[i]) {
            int j = st.top(); st.pop();
            ans[j] = i - j;  // @7
        }
        st.push(i);  // @9
    }
    return ans;
}
//@end

//@code asteroidCollision
vector<int> asteroidCollision(vector<int>& asteroids) {
    vector<int> st;
    for (int a : asteroids) {
        bool alive = true;
        while (alive && a < 0 && !st.empty() && st.back() > 0) {
            int top = st.back();
            if (top < -a) {
                st.pop_back();  // @9
                continue;
            }
            if (top == -a) {
                st.pop_back();  // @13
            }
            alive = false;  // @15
        }
        if (alive) {
            st.push_back(a);  // @18
        }
    }
    return st;
}
//@end

//@code largestRectangleArea
int largestRectangleArea(vector<int>& heights) {
    stack<int> st;
    int best = 0, n = heights.size();
    for (int i = 0; i <= n; i++) {
        int h = i == n ? 0 : heights[i];
        while (!st.empty() && heights[st.top()] > h) {
            int height = heights[st.top()]; st.pop();
            int left = st.empty() ? -1 : st.top();
            int width = i - left - 1;
            best = max(best, height * width);  // @10
        }
        st.push(i);  // @12
    }
    return best;
}
//@end
