// Shared helpers for the C++ solutions (not shown on the page).
#pragma once
#include <bits/stdc++.h>
using namespace std;

struct ListNode {
    int val; ListNode* next;
    ListNode(int v = 0, ListNode* n = nullptr) : val(v), next(n) {}
};

struct TreeNode {
    int val; TreeNode *left, *right;
    TreeNode(int v = 0, TreeNode* l = nullptr, TreeNode* r = nullptr) : val(v), left(l), right(r) {}
};

inline ListNode* mkList(const vector<int>& v) {
    ListNode d; ListNode* c = &d;
    for (int x : v) { c->next = new ListNode(x); c = c->next; }
    return d.next;
}

inline TreeNode* mkTree(const vector<string>& v) {
    if (v.empty() || v[0] == "null") return nullptr;
    TreeNode* root = new TreeNode(stoi(v[0]));
    queue<TreeNode*> q; q.push(root); size_t i = 1;
    while (!q.empty() && i < v.size()) {
        TreeNode* n = q.front(); q.pop();
        if (i < v.size() && v[i] != "null") { n->left = new TreeNode(stoi(v[i])); q.push(n->left); }
        i++;
        if (i < v.size() && v[i] != "null") { n->right = new TreeNode(stoi(v[i])); q.push(n->right); }
        i++;
    }
    return root;
}

inline TreeNode* findNode(TreeNode* r, int v) {
    if (!r) return nullptr;
    if (r->val == v) return r;
    TreeNode* l = findNode(r->left, v);
    return l ? l : findNode(r->right, v);
}

struct Raw { string s; };

inline string fmt(const Raw& r) { return r.s; }
inline string fmt(int x) { return to_string(x); }
inline string fmt(long x) { return to_string(x); }
inline string fmt(long long x) { return to_string(x); }
inline string fmt(bool x) { return x ? "true" : "false"; }
inline string fmt(double x) {
    char b[64];
    if (x == floor(x)) { snprintf(b, sizeof b, "%.1f", x); return b; }
    snprintf(b, sizeof b, "%.5f", x); string s = b;
    while (!s.empty() && s.back() == '0') s.pop_back();
    return s;
}
inline string fmt(const string& s) { return "\"" + s + "\""; }
inline string fmt(const char* s) { return fmt(string(s)); }
inline string fmt(char c) { return string("'") + c + "'"; }
inline string fmt(ListNode* h) {
    string out = "["; unordered_set<ListNode*> seen; bool first = true;
    for (ListNode* c = h; c; c = c->next) {
        if (!first) out += ", ";
        first = false;
        if (seen.count(c)) { out += "…cycle"; break; }
        seen.insert(c); out += to_string(c->val);
    }
    return out + "]";
}
inline string fmt(TreeNode* r) {
    if (!r) return "[]";
    vector<string> out; queue<TreeNode*> q; q.push(r);
    while (!q.empty()) {
        TreeNode* n = q.front(); q.pop();
        if (!n) { out.push_back("null"); continue; }
        out.push_back(to_string(n->val)); q.push(n->left); q.push(n->right);
    }
    while (!out.empty() && out.back() == "null") out.pop_back();
    string s = "[";
    for (size_t i = 0; i < out.size(); i++) s += (i ? "," : "") + out[i];
    return s + "]";
}
inline string fmt(const vector<char>& v) {
    string s = "[";
    for (size_t i = 0; i < v.size(); i++) s += (i ? ", \"" : "\"") + string(1, v[i]) + "\"";
    return s + "]";
}
template <class T> string fmt(const vector<T>& v) {
    string s = "[";
    for (size_t i = 0; i < v.size(); i++) s += (i ? ", " : "") + fmt(v[i]);
    return s + "]";
}

struct Out {
    vector<string> v;
    void nul() { v.push_back("null"); }
    template <class T> void add(const T& x) { v.push_back(fmt(x)); }
    string str() const { string s = "["; for (size_t i = 0; i < v.size(); i++) s += (i ? ", " : "") + v[i]; return s + "]"; }
};
inline string fmt(const Out& o) { return o.str(); }

inline ListNode* mkCycle(const vector<int>& v, int pos) {
    ListNode* h = mkList(v); ListNode *t = nullptr, *at = nullptr; int i = 0;
    for (ListNode* c = h; c; c = c->next, i++) { if (i == pos) at = c; t = c; }
    if (t) t->next = at;
    return h;
}
