//@code isValidBST
bool valid(TreeNode* node, long long low, long long high) {
    if (!node) return true;
    if (node->val <= low || node->val >= high) {
        return false;  // @8
    }
    return valid(node->left, low, node->val) && valid(node->right, node->val, high);  // @10
}

bool isValidBST(TreeNode* root) {
    return valid(root, LLONG_MIN, LLONG_MAX);
}
//@end

//@code kthSmallest
int kthSmallest(TreeNode* root, int k) {
    stack<TreeNode*> st;
    TreeNode* curr = root;
    int count = 0;
    while (curr || !st.empty()) {
        while (curr) {
            st.push(curr);
            curr = curr->left;  // @8
        }
        curr = st.top(); st.pop();
        count++;
        if (count == k) {
            return curr->val;  // @13
        }
        curr = curr->right;
    }
    return -1;
}
//@end

//@code lcaBST
TreeNode* lowestCommonAncestorBST(TreeNode* root, TreeNode* p, TreeNode* q) {
    TreeNode* node = root;
    while (node) {
        if (p->val < node->val && q->val < node->val) {  // @4
            node = node->left;
        } else if (p->val > node->val && q->val > node->val) {  // @6
            node = node->right;
        } else {
            return node;  // @9
        }
    }
    return nullptr;
}
//@end

//@code lowestCommonAncestor
TreeNode* lowestCommonAncestor(TreeNode* root, TreeNode* p, TreeNode* q) {
    if (!root) return nullptr;
    if (root == p || root == q) {
        return root;  // @4
    }
    TreeNode* left = lowestCommonAncestor(root->left, p, q);  // @6
    TreeNode* right = lowestCommonAncestor(root->right, p, q);
    return (left && right) ? root : (left ? left : right);  // @8
}
//@end

//@code buildTree
int pre;

TreeNode* build(vector<int>& preorder, unordered_map<int, int>& index, int lo, int hi) {
    if (lo > hi) return nullptr;
    int val = preorder[pre++];
    TreeNode* node = new TreeNode(val);
    int mid = index[val];  // @14
    node->left = build(preorder, index, lo, mid - 1);
    node->right = build(preorder, index, mid + 1, hi);
    return node;
}

TreeNode* buildTree(vector<int>& preorder, vector<int>& inorder) {
    unordered_map<int, int> index;
    for (int i = 0; i < (int)inorder.size(); i++) index[inorder[i]] = i;
    pre = 0;
    return build(preorder, index, 0, (int)inorder.size() - 1);
}
//@end

//@code maxPathSum
int bestSum;

int gain(TreeNode* node) {
    if (!node) return 0;
    int left = max(0, gain(node->left));
    int right = max(0, gain(node->right));
    bestSum = max(bestSum, node->val + left + right);  // @13
    return node->val + max(left, right);
}

int maxPathSum(TreeNode* root) {
    bestSum = INT_MIN;
    gain(root);
    return bestSum;
}
//@end

//@code Codec
class Codec {
    void write(TreeNode* node, string& out) {
        if (!out.empty()) out += ',';
        if (!node) {
            out += '#';
            return;
        }
        out += to_string(node->val);  // @14
        write(node->left, out);
        write(node->right, out);
    }

    TreeNode* read(istringstream& in) {
        string t;
        getline(in, t, ',');
        if (t == "#") return nullptr;
        TreeNode* node = new TreeNode(stoi(t));  // @27
        node->left = read(in);
        node->right = read(in);
        return node;
    }

public:
    string serialize(TreeNode* root) {
        string out;
        write(root, out);  // @4
        return out;
    }

    TreeNode* deserialize(string data) {
        istringstream in(data);
        return read(in);
    }
};
//@end
