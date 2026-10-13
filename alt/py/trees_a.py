from common import *

#@code maxDepth
def maxDepth(root: Optional[TreeNode]) -> int:
    if not root: return 0  # @2
    left = maxDepth(root.left)
    right = maxDepth(root.right)
    return 1 + max(left, right)  # @5
#@end

#@code invertTree
def invertTree(root: Optional[TreeNode]) -> Optional[TreeNode]:
    if not root: return None
    root.left, root.right = root.right, root.left  # @5
    invertTree(root.left)
    invertTree(root.right)
    return root
#@end

#@code isSameTree
def isSameTree(p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:
    if not p and not q: return True
    if not p or not q:
        return False  # @4
    return p.val == q.val and isSameTree(p.left, q.left) and isSameTree(p.right, q.right)  # @6
#@end

#@code isBalanced
def isBalanced(root: Optional[TreeNode]) -> bool:
    return height(root) != -1


def height(node: Optional[TreeNode]) -> int:
    if not node: return 0
    left = height(node.left)
    right = -1 if left == -1 else height(node.right)
    if left == -1 or right == -1 or abs(left - right) > 1: return -1  # @9
    return 1 + max(left, right)
#@end

#@code diameterOfBinaryTree
def diameterOfBinaryTree(root: Optional[TreeNode]) -> int:
    best = 0

    def depth(node: Optional[TreeNode]) -> int:
        nonlocal best
        if not node: return 0
        left = depth(node.left)
        right = depth(node.right)
        best = max(best, left + right)  # @13
        return 1 + max(left, right)

    depth(root)
    return best
#@end

#@code levelOrder
def levelOrder(root: Optional[TreeNode]) -> List[List[int]]:
    res = []
    if not root: return res
    queue = deque([root])
    while queue:
        level = []  # @8
        for _ in range(len(queue)):
            node = queue.popleft()
            level.append(node.val)
            if node.left: queue.append(node.left)
            if node.right: queue.append(node.right)  # @13
        res.append(level)
    return res  # @17
#@end

#@code rightSideView
def rightSideView(root: Optional[TreeNode]) -> List[int]:
    res = []
    if not root: return res
    queue = deque([root])
    while queue:
        size = len(queue)
        for i in range(size):
            node = queue.popleft()
            if i == size - 1: res.append(node.val)
            if node.left: queue.append(node.left)
            if node.right: queue.append(node.right)  # @12
    return res
#@end

#@code zigzagLevelOrder
def zigzagLevelOrder(root: Optional[TreeNode]) -> List[List[int]]:
    res = []
    if not root: return res
    queue = deque([root])
    left_to_right = True
    while queue:
        level = deque()
        for _ in range(len(queue)):
            node = queue.popleft()
            if left_to_right: level.append(node.val)
            else: level.appendleft(node.val)
            if node.left: queue.append(node.left)
            if node.right: queue.append(node.right)  # @15
        res.append(list(level))
        left_to_right = not left_to_right
    return res  # @20
#@end
