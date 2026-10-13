//@code maxDepth
int maxDepth(TreeNode* root) {
    if (!root) return 0;  // @2
    int left = maxDepth(root->left);
    int right = maxDepth(root->right);
    return 1 + max(left, right);  // @5
}
//@end

//@code invertTree
TreeNode* invertTree(TreeNode* root) {
    if (!root) return nullptr;
    swap(root->left, root->right);  // @5
    invertTree(root->left);
    invertTree(root->right);
    return root;
}
//@end

//@code isSameTree
bool isSameTree(TreeNode* p, TreeNode* q) {
    if (!p && !q) return true;
    if (!p || !q) {
        return false;  // @4
    }
    return p->val == q->val && isSameTree(p->left, q->left) && isSameTree(p->right, q->right);  // @6
}
//@end

//@code isBalanced
int height(TreeNode* node) {
    if (!node) return 0;
    int left = height(node->left);
    int right = left == -1 ? -1 : height(node->right);
    if (left == -1 || right == -1 || abs(left - right) > 1) return -1;  // @9
    return 1 + max(left, right);
}

bool isBalanced(TreeNode* root) {
    return height(root) != -1;
}
//@end

//@code diameterOfBinaryTree
int best;

int depth(TreeNode* node) {
    if (!node) return 0;
    int left = depth(node->left);
    int right = depth(node->right);
    best = max(best, left + right);  // @13
    return 1 + max(left, right);
}

int diameterOfBinaryTree(TreeNode* root) {
    best = 0;
    depth(root);
    return best;
}
//@end

//@code levelOrder
vector<vector<int>> levelOrder(TreeNode* root) {
    vector<vector<int>> res;
    if (!root) return res;
    queue<TreeNode*> q;
    q.push(root);
    while (!q.empty()) {
        vector<int> level;  // @8
        for (int i = q.size(); i > 0; i--) {
            TreeNode* node = q.front(); q.pop();
            level.push_back(node->val);
            if (node->left) q.push(node->left);
            if (node->right) q.push(node->right);  // @13
        }
        res.push_back(level);
    }
    return res;  // @17
}
//@end

//@code rightSideView
vector<int> rightSideView(TreeNode* root) {
    vector<int> res;
    if (!root) return res;
    queue<TreeNode*> q;
    q.push(root);
    while (!q.empty()) {
        int size = q.size();
        for (int i = 0; i < size; i++) {
            TreeNode* node = q.front(); q.pop();
            if (i == size - 1) res.push_back(node->val);
            if (node->left) q.push(node->left);
            if (node->right) q.push(node->right);  // @12
        }
    }
    return res;
}
//@end

//@code zigzagLevelOrder
vector<vector<int>> zigzagLevelOrder(TreeNode* root) {
    vector<vector<int>> res;
    if (!root) return res;
    queue<TreeNode*> q;
    q.push(root);
    bool leftToRight = true;
    while (!q.empty()) {
        deque<int> level;
        for (int i = q.size(); i > 0; i--) {
            TreeNode* node = q.front(); q.pop();
            if (leftToRight) level.push_back(node->val);
            else level.push_front(node->val);
            if (node->left) q.push(node->left);
            if (node->right) q.push(node->right);  // @15
        }
        res.emplace_back(level.begin(), level.end());
        leftToRight = !leftToRight;
    }
    return res;  // @20
}
//@end
