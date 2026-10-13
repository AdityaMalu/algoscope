//@code canFinish
bool canFinish(int n, vector<vector<int>>& prerequisites) {
    vector<vector<int>> adj(n);
    vector<int> indegree(n);
    for (auto& p : prerequisites) {
        adj[p[1]].push_back(p[0]);
        indegree[p[0]]++;
    }
    queue<int> q;
    for (int i = 0; i < n; i++) if (indegree[i] == 0) q.push(i);  // @10
    int taken = 0;
    while (!q.empty()) {
        int c = q.front(); q.pop();
        taken++;
        for (int next : adj[c]) {
            if (--indegree[next] == 0) q.push(next);  // @16
        }
    }
    return taken == n;  // @19
}
//@end

//@code findCircleNum
vector<int> parent;

int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x]);
    return parent[x];
}

int findCircleNum(vector<vector<int>>& isConnected) {
    int n = isConnected.size(), groups = n;
    parent.assign(n, 0);
    for (int i = 0; i < n; i++) parent[i] = i;  // @11
    for (int i = 0; i < n; i++)
        for (int j = i + 1; j < n; j++) {
            if (isConnected[i][j] == 0) continue;
            int a = find(i), b = find(j);
            if (a != b) {
                parent[a] = b;
                groups--;  // @18
            }
        }
    return groups;
}
//@end

//@code eventualSafeNodes
vector<int> eventualSafeNodes(vector<vector<int>>& graph) {
    int n = graph.size();
    vector<vector<int>> rev(n);
    vector<int> out(n);
    for (int u = 0; u < n; u++) {
        out[u] = graph[u].size();
        for (int v : graph[u]) rev[v].push_back(u);
    }
    queue<int> q;
    for (int i = 0; i < n; i++) if (out[i] == 0) q.push(i);  // @11
    vector<bool> safe(n);
    while (!q.empty()) {
        int v = q.front(); q.pop();
        safe[v] = true;
        for (int u : rev[v]) {
            if (--out[u] == 0) q.push(u);  // @17
        }
    }
    vector<int> res;
    for (int i = 0; i < n; i++) if (safe[i]) res.push_back(i);
    return res;
}
//@end

//@code findOrder
vector<int> findOrder(int n, vector<vector<int>>& prerequisites) {
    vector<vector<int>> adj(n);
    vector<int> indegree(n);
    for (auto& p : prerequisites) {
        adj[p[1]].push_back(p[0]);
        indegree[p[0]]++;
    }
    queue<int> q;
    for (int i = 0; i < n; i++) if (indegree[i] == 0) q.push(i);
    vector<int> order;  // @12
    while (!q.empty()) {
        int c = q.front(); q.pop();
        order.push_back(c);
        for (int next : adj[c]) {
            if (--indegree[next] == 0) q.push(next);  // @17
        }
    }
    return (int)order.size() == n ? order : vector<int>{};
}
//@end

//@code findRedundantConnection
int root(vector<int>& parent, int x) {
    while (parent[x] != x) {
        parent[x] = parent[parent[x]];
        x = parent[x];
    }
    return x;
}

vector<int> findRedundantConnection(vector<vector<int>>& edges) {
    vector<int> parent(edges.size() + 1);
    iota(parent.begin(), parent.end(), 0);
    for (auto& e : edges) {
        int a = root(parent, e[0]), b = root(parent, e[1]);
        if (a == b) {
            return e;  // @7
        }
        parent[a] = b;  // @9
    }
    return {};
}
//@end

//@code accountsMerge
unordered_map<string, string> par;

string findEmail(const string& x) {
    if (par[x] != x) par[x] = findEmail(par[x]);
    return par[x];
}

vector<vector<string>> accountsMerge(vector<vector<string>>& accounts) {
    par.clear();
    unordered_map<string, string> owner;
    for (auto& acc : accounts) {
        for (size_t i = 1; i < acc.size(); i++) {
            if (!par.count(acc[i])) par[acc[i]] = acc[i];
            owner[acc[i]] = acc[0];
            par[findEmail(acc[i])] = findEmail(acc[1]);  // @17
        }
    }
    map<string, set<string>> groups;
    for (auto& [email, p] : par) groups[findEmail(email)].insert(email);  // @21
    vector<vector<string>> res;
    for (auto& [root, emails] : groups) {
        vector<string> acc{owner[*emails.begin()]};
        acc.insert(acc.end(), emails.begin(), emails.end());
        res.push_back(acc);
    }
    sort(res.begin(), res.end(), [](auto& a, auto& b) { return a[1] < b[1]; });
    return res;
}
//@end

//@code ladderLength
int ladderLength(string beginWord, string endWord, vector<string>& wordList) {
    unordered_set<string> words(wordList.begin(), wordList.end());
    if (!words.count(endWord)) {
        return 0;  // @4
    }
    queue<string> q;
    q.push(beginWord);
    words.erase(beginWord);
    int level = 1;
    while (!q.empty()) {  // @10
        for (int size = q.size(); size > 0; size--) {
            string w = q.front(); q.pop();
            for (size_t i = 0; i < w.size(); i++) {
                char orig = w[i];
                for (char ch = 'a'; ch <= 'z'; ch++) {
                    w[i] = ch;
                    if (w == endWord) {
                        return level + 1;  // @19
                    }
                    if (words.erase(w)) {
                        q.push(w);  // @22
                    }
                }
                w[i] = orig;
            }
        }
        level++;
    }
    return 0;
}
//@end
