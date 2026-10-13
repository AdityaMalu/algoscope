//@code Trie
class Trie {
    struct Node {
        Node* next[26] = {};
        bool isEnd = false;
    };

    Node* root = new Node();

    Node* walk(const string& s) {
        Node* cur = root;
        for (char c : s) {
            cur = cur->next[c - 'a'];
            if (!cur) return nullptr;
        }
        return cur;
    }

public:
    void insert(string word) {
        Node* cur = root;
        for (char c : word) {
            if (!cur->next[c - 'a']) cur->next[c - 'a'] = new Node();
            cur = cur->next[c - 'a'];  // @13
        }
        cur->isEnd = true;  // @15
    }

    bool search(string word) {
        Node* node = walk(word);
        return node && node->isEnd;  // @20
    }

    bool startsWith(string prefix) {
        return walk(prefix) != nullptr;  // @25
    }
};
//@end

//@code longestCommonPrefix
string longestCommonPrefix(vector<string>& strs) {
    const string& first = strs[0];
    for (size_t i = 0; i < first.size(); i++) {
        for (size_t k = 1; k < strs.size(); k++) {
            if (i == strs[k].size() || strs[k][i] != first[i]) {
                return first.substr(0, i);  // @7
            }
        }
    }
    return first;
}
//@end

//@code countSubstrings
int countSubstrings(string s) {
    int count = 0, n = s.size();
    for (int center = 0; center < 2 * n - 1; center++) {
        int l = center / 2, r = l + center % 2;
        while (l >= 0 && r < n && s[l] == s[r]) {
            count++;  // @6
            l--;
            r++;
        }
    }
    return count;
}
//@end

//@code WordDictionary
class WordDictionary {
    struct Node {
        Node* next[26] = {};
        bool isEnd = false;
    };

    Node* root = new Node();

    bool match(const string& word, size_t i, Node* node) {
        if (i == word.size()) return node->isEnd;
        char c = word[i];
        if (c != '.') {
            Node* next = node->next[c - 'a'];
            return next && match(word, i + 1, next);
        }
        for (Node* child : node->next) {
            if (!child) continue;  // @31
            if (match(word, i + 1, child)) return true;
        }
        return false;
    }

public:
    void addWord(string word) {
        Node* cur = root;
        for (char c : word) {
            if (!cur->next[c - 'a']) cur->next[c - 'a'] = new Node();
            cur = cur->next[c - 'a'];
        }
        cur->isEnd = true;  // @15
    }

    bool search(string word) {
        return match(word, 0, root);  // @20
    }
};
//@end

//@code suggestedProducts
vector<vector<string>> suggestedProducts(vector<string>& products, string searchWord) {
    sort(products.begin(), products.end());
    vector<vector<string>> res;
    string prefix;
    for (char c : searchWord) {
        prefix += c;
        auto it = lower_bound(products.begin(), products.end(), prefix);
        vector<string> top;
        for (int i = 0; i < 3 && it != products.end() && it->compare(0, prefix.size(), prefix) == 0; i++, it++) top.push_back(*it);
        res.push_back(top);  // @15
    }
    return res;
}
//@end

//@code longestPalindrome
string longestPalindrome(string s) {
    int bestStart = 0, bestLen = 1, n = s.size();
    for (int center = 0; center < 2 * n - 1; center++) {
        int l = center / 2, r = l + center % 2;
        while (l >= 0 && r < n && s[l] == s[r]) {
            l--;
            r++;
        }
        int len = r - l - 1;
        if (len > bestLen) {
            bestLen = len;
            bestStart = l + 1;  // @12
        }
    }
    return s.substr(bestStart, bestLen);
}
//@end

//@code findWords
struct Node {
    Node* next[26] = {};
    string word;
};

void dfs(vector<vector<char>>& b, int r, int c, Node* node, vector<string>& res) {
    if (r < 0 || c < 0 || r >= (int)b.size() || c >= (int)b[0].size() || b[r][c] == '#') return;
    Node* next = node->next[b[r][c] - 'a'];
    if (!next) return;
    if (!next->word.empty()) {
        res.push_back(next->word);  // @28
        next->word.clear();
    }
    char ch = b[r][c];
    b[r][c] = '#';  // @32
    dfs(b, r + 1, c, next, res);
    dfs(b, r - 1, c, next, res);
    dfs(b, r, c + 1, next, res);
    dfs(b, r, c - 1, next, res);
    b[r][c] = ch;
}

vector<string> findWords(vector<vector<char>>& board, vector<string>& words) {
    Node* root = new Node();
    for (auto& w : words) {
        Node* cur = root;
        for (char c : w) {
            if (!cur->next[c - 'a']) cur->next[c - 'a'] = new Node();
            cur = cur->next[c - 'a'];
        }
        cur->word = w;  // @14
    }
    vector<string> res;
    for (int r = 0; r < (int)board.size(); r++)
        for (int c = 0; c < (int)board[0].size(); c++)
            dfs(board, r, c, root, res);
    return res;
}
//@end
