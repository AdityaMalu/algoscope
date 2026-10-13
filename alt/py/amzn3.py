from common import *

#@code distanceK
def distanceK(root: TreeNode, target: TreeNode, k: int) -> List[int]:
    parent = {}
    stack = [root]
    while stack:
        n = stack.pop()
        for child in (n.left, n.right):
            if child: parent[child] = n; stack.append(child)  # @8
    queue, seen = deque([target]), {target}
    for _ in range(k):
        if not queue: break
        for _ in range(len(queue)):
            n = queue.popleft()
            for nxt in (n.left, n.right, parent.get(n)):
                if nxt and nxt not in seen: seen.add(nxt); queue.append(nxt)  # @18
    return [n.val for n in queue]
#@end

#@code pathSum
def pathSum(root: Optional[TreeNode], targetSum: int) -> List[List[int]]:
    res, path = [], []

    def dfs(node: Optional[TreeNode], remain: int) -> None:
        if not node: return
        path.append(node.val)
        remain -= node.val
        if not node.left and not node.right and remain == 0: res.append(path[:])  # @12
        dfs(node.left, remain)
        dfs(node.right, remain)
        path.pop()

    dfs(root, targetSum)
    return res
#@end

#@code flatten
def flatten(root: Optional[TreeNode]) -> None:
    node = root
    while node:
        if node.left:
            rightmost = node.left
            while rightmost.right: rightmost = rightmost.right  # @6
            rightmost.right = node.right
            node.right = node.left
            node.left = None  # @9
        node = node.right  # @11
#@end

#@code maximalSquare
def maximalSquare(matrix: List[List[str]]) -> int:
    m, n, best = len(matrix), len(matrix[0]), 0
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for r in range(1, m + 1):
        for c in range(1, n + 1):
            if matrix[r - 1][c - 1] == '1':
                dp[r][c] = 1 + min(dp[r - 1][c - 1], dp[r - 1][c], dp[r][c - 1])
                best = max(best, dp[r][c])  # @8
    return best * best
#@end

#@code verticalTraversal
def verticalTraversal(root: Optional[TreeNode]) -> List[List[int]]:
    nodes = []

    def collect(node: Optional[TreeNode], col: int, row: int) -> None:
        if not node: return
        nodes.append((col, row, node.val))  # @19
        collect(node.left, col - 1, row + 1)
        collect(node.right, col + 1, row + 1)

    collect(root, 0, 0)
    nodes.sort()  # @4
    res, prev_col = [], None
    for col, _, val in nodes:
        if col != prev_col:
            res.append([])
            prev_col = col
        res[-1].append(val)  # @12
    return res
#@end

#@code longestIncreasingPath
def longestIncreasingPath(matrix: List[List[int]]) -> int:
    m, n = len(matrix), len(matrix[0])
    memo = [[0] * n for _ in range(m)]

    def longest(r: int, c: int) -> int:
        if memo[r][c]: return memo[r][c]
        best = 1
        for dr, dc in ((1, 0), (-1, 0), (0, 1), (0, -1)):
            nr, nc = r + dr, c + dc
            if 0 <= nr < m and 0 <= nc < n and matrix[nr][nc] > matrix[r][c]:
                best = max(best, 1 + longest(nr, nc))
        memo[r][c] = best  # @19
        return best

    return max(longest(r, c) for r in range(m) for c in range(n))  # @7
#@end
