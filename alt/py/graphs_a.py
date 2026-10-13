from common import *

DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]

#@code floodFill
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def floodFill(image: List[List[int]], sr: int, sc: int, color: int) -> List[List[int]]:
    original = image[sr][sc]
    if original == color:
        return image  # @5

    def fill(r: int, c: int) -> None:
        if r < 0 or c < 0 or r >= len(image) or c >= len(image[0]) or image[r][c] != original: return
        image[r][c] = color  # @13
        for dr, dc in DIRS: fill(r + dr, c + dc)

    fill(sr, sc)
    return image
#@end

#@code numIslands
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def numIslands(grid: List[List[str]]) -> int:
    def sink(r: int, c: int) -> None:
        if r < 0 or c < 0 or r >= len(grid) or c >= len(grid[0]) or grid[r][c] != '1': return
        grid[r][c] = '0'  # @17
        for dr, dc in DIRS: sink(r + dr, c + dc)

    count = 0
    for r in range(len(grid)):
        for c in range(len(grid[0])):
            if grid[r][c] == '1':
                count += 1  # @7
                sink(r, c)
    return count  # @12
#@end

#@code maxAreaOfIsland
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def maxAreaOfIsland(grid: List[List[int]]) -> int:
    def area(r: int, c: int) -> int:
        if r < 0 or c < 0 or r >= len(grid) or c >= len(grid[0]) or grid[r][c] != 1: return 0
        grid[r][c] = 0  # @17
        return 1 + sum(area(r + dr, c + dc) for dr, dc in DIRS)

    best = 0
    for r in range(len(grid)):
        for c in range(len(grid[0])):
            if grid[r][c] == 1:
                best = max(best, area(r, c))  # @8
    return best
#@end

class GNode:
    def __init__(self, val=0, neighbors=None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []


def build_graph(adj):
    if not adj: return None
    ns = [GNode(i + 1) for i in range(len(adj))]
    for i, nb in enumerate(adj): ns[i].neighbors = [ns[j - 1] for j in nb]
    return ns[0]


def adj_of(node):
    if node is None: return Raw("[]")
    seen, st = {}, [node]
    while st:
        c = st.pop()
        if c.val in seen: continue
        seen[c.val] = c
        st.extend(c.neighbors)
    return Raw(fmt([[x.val for x in seen[v].neighbors] for v in sorted(seen)]))


#@code cloneGraph
class Node:
    def __init__(self, val=0, neighbors=None):
        self.val = val
        self.neighbors = neighbors if neighbors is not None else []


def cloneGraph(node: Optional["Node"]) -> Optional["Node"]:
    copies = {}

    def clone(n):
        if n in copies: return copies[n]
        copy = Node(n.val)
        copies[n] = copy  # @15
        for nb in n.neighbors:
            copy.neighbors.append(clone(nb))  # @17
        return copy

    return clone(node) if node else None
#@end

#@code orangesRotting
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def orangesRotting(grid: List[List[int]]) -> int:
    m, n = len(grid), len(grid[0])
    queue = deque((r, c) for r in range(m) for c in range(n) if grid[r][c] == 2)
    fresh = sum(row.count(1) for row in grid)  # @8
    minutes = 0
    while queue and fresh > 0:
        minutes += 1
        for _ in range(len(queue)):
            r, c = queue.popleft()
            for dr, dc in DIRS:
                nr, nc = r + dr, c + dc
                if 0 <= nr < m and 0 <= nc < n and grid[nr][nc] == 1:
                    grid[nr][nc] = 2
                    fresh -= 1
                    queue.append((nr, nc))  # @20
    return minutes if fresh == 0 else -1  # @24
#@end

#@code updateMatrix
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def updateMatrix(mat: List[List[int]]) -> List[List[int]]:
    m, n = len(mat), len(mat[0])
    queue = deque()
    for r in range(m):
        for c in range(n):
            if mat[r][c] == 0: queue.append((r, c))
            else: mat[r][c] = -1  # @8
    while queue:
        r, c = queue.popleft()
        for dr, dc in DIRS:
            nr, nc = r + dr, c + dc
            if 0 <= nr < m and 0 <= nc < n and mat[nr][nc] == -1:
                mat[nr][nc] = mat[r][c] + 1
                queue.append((nr, nc))  # @16
    return mat
#@end

#@code solve
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def solve(board: List[List[str]]) -> None:
    m, n = len(board), len(board[0])

    def mark(r: int, c: int) -> None:
        if r < 0 or c < 0 or r >= m or c >= n or board[r][c] != 'O': return
        board[r][c] = '#'  # @21
        for dr, dc in DIRS: mark(r + dr, c + dc)

    for r in range(m):
        mark(r, 0)
        mark(r, n - 1)
    for c in range(n):
        mark(0, c)
        mark(m - 1, c)  # @10
    for r in range(m):
        for c in range(n):
            if board[r][c] == 'O': board[r][c] = 'X'
            elif board[r][c] == '#': board[r][c] = 'O'  # @15
#@end

#@code pacificAtlantic
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def pacificAtlantic(heights: List[List[int]]) -> List[List[int]]:
    m, n = len(heights), len(heights[0])
    pac, atl = set(), set()

    def climb(r: int, c: int, seen: set) -> None:
        if (r, c) in seen: return
        seen.add((r, c))  # @22
        for dr, dc in DIRS:
            nr, nc = r + dr, c + dc
            if 0 <= nr < m and 0 <= nc < n and heights[nr][nc] >= heights[r][c]: climb(nr, nc, seen)

    for r in range(m):
        climb(r, 0, pac)
        climb(r, n - 1, atl)
    for c in range(n):
        climb(0, c, pac)
        climb(m - 1, c, atl)  # @11
    return [[r, c] for r in range(m) for c in range(n) if (r, c) in pac and (r, c) in atl]  # @16
#@end
