from common import *

#@code isValidBST
def isValidBST(root: Optional[TreeNode]) -> bool:
    return valid(root, float("-inf"), float("inf"))


def valid(node: Optional[TreeNode], low: float, high: float) -> bool:
    if not node: return True
    if not (low < node.val < high):
        return False  # @8
    return valid(node.left, low, node.val) and valid(node.right, node.val, high)  # @10
#@end

#@code kthSmallest
def kthSmallest(root: Optional[TreeNode], k: int) -> int:
    stack, curr, count = [], root, 0
    while curr or stack:
        while curr:
            stack.append(curr)
            curr = curr.left  # @8
        curr = stack.pop()
        count += 1
        if count == k:
            return curr.val  # @13
        curr = curr.right
    return -1
#@end

#@code lcaBST
def lowestCommonAncestorBST(root: TreeNode, p: TreeNode, q: TreeNode) -> Optional[TreeNode]:
    node = root
    while node:
        if p.val < node.val and q.val < node.val:  # @4
            node = node.left
        elif p.val > node.val and q.val > node.val:  # @6
            node = node.right
        else:
            return node  # @9
    return None
#@end

#@code lowestCommonAncestor
def lowestCommonAncestor(root: Optional[TreeNode], p: TreeNode, q: TreeNode) -> Optional[TreeNode]:
    if not root: return None
    if root is p or root is q:
        return root  # @4
    left = lowestCommonAncestor(root.left, p, q)  # @6
    right = lowestCommonAncestor(root.right, p, q)
    return root if left and right else (left or right)  # @8
#@end

#@code buildTree
def buildTree(preorder: List[int], inorder: List[int]) -> Optional[TreeNode]:
    index = {v: i for i, v in enumerate(inorder)}
    pre = 0

    def build(lo: int, hi: int) -> Optional[TreeNode]:
        nonlocal pre
        if lo > hi: return None
        val = preorder[pre]
        pre += 1
        node = TreeNode(val)
        mid = index[val]  # @14
        node.left = build(lo, mid - 1)
        node.right = build(mid + 1, hi)
        return node

    return build(0, len(inorder) - 1)
#@end

#@code maxPathSum
def maxPathSum(root: Optional[TreeNode]) -> int:
    best = float("-inf")

    def gain(node: Optional[TreeNode]) -> int:
        nonlocal best
        if not node: return 0
        left = max(0, gain(node.left))
        right = max(0, gain(node.right))
        best = max(best, node.val + left + right)  # @13
        return node.val + max(left, right)

    gain(root)
    return best
#@end

#@code Codec
class Codec:
    def serialize(self, root: Optional[TreeNode]) -> str:
        out = []
        self._write(root, out)  # @4
        return ",".join(out)

    def _write(self, node: Optional[TreeNode], out: List[str]) -> None:
        if not node:
            out.append("#")
            return
        out.append(str(node.val))  # @14
        self._write(node.left, out)
        self._write(node.right, out)

    def deserialize(self, data: str) -> Optional[TreeNode]:
        tokens = deque(data.split(","))
        return self._read(tokens)

    def _read(self, tokens: deque) -> Optional[TreeNode]:
        t = tokens.popleft()
        if t == "#": return None
        node = TreeNode(int(t))  # @27
        node.left = self._read(tokens)
        node.right = self._read(tokens)
        return node
#@end
