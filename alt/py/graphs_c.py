from common import *

DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]

#@code networkDelayTime
def networkDelayTime(times: List[List[int]], n: int, k: int) -> int:
    adj = defaultdict(list)
    for u, v, w in times: adj[u].append((v, w))
    dist = {k: 0}
    heap = [(0, k)]  # @9
    while heap:
        d, u = heapq.heappop(heap)
        if d > dist.get(u, float("inf")): continue
        for v, w in adj[u]:
            if d + w < dist.get(v, float("inf")):
                dist[v] = d + w
                heapq.heappush(heap, (dist[v], v))  # @18
    return max(dist.values()) if len(dist) == n else -1  # @24
#@end

#@code shortestPathBinaryMatrix
def shortestPathBinaryMatrix(grid: List[List[int]]) -> int:
    n = len(grid)
    if grid[0][0] == 1 or grid[n - 1][n - 1] == 1:
        return -1  # @4
    dist = [[0] * n for _ in range(n)]
    dist[0][0] = 1
    queue = deque([(0, 0)])
    while queue:
        r, c = queue.popleft()
        if r == n - 1 and c == n - 1:
            return dist[r][c]  # @14
        for dr in (-1, 0, 1):
            for dc in (-1, 0, 1):
                nr, nc = r + dr, c + dc
                if 0 <= nr < n and 0 <= nc < n and grid[nr][nc] == 0 and dist[nr][nc] == 0:
                    dist[nr][nc] = dist[r][c] + 1
                    queue.append((nr, nc))  # @21
    return -1
#@end

#@code minCostConnectPoints
def minCostConnectPoints(points: List[List[int]]) -> int:
    n, total = len(points), 0
    dist = [float("inf")] * n
    in_tree = [False] * n
    dist[0] = 0
    for _ in range(n):
        u = min((i for i in range(n) if not in_tree[i]), key=lambda i: dist[i])
        in_tree[u] = True
        total += dist[u]
        for v in range(n):
            d = abs(points[u][0] - points[v][0]) + abs(points[u][1] - points[v][1])
            if not in_tree[v] and d < dist[v]:
                dist[v] = d  # @16
    return total
#@end

#@code findCheapestPrice
def findCheapestPrice(n: int, flights: List[List[int]], src: int, dst: int, k: int) -> int:
    price = [float("inf")] * n
    price[src] = 0  # @4
    for _ in range(k + 1):
        nxt = price[:]
        for u, v, w in flights:
            if price[u] + w < nxt[v]: nxt[v] = price[u] + w
        price = nxt  # @11
    return -1 if price[dst] == float("inf") else price[dst]
#@end

#@code minimumEffortPath
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def minimumEffortPath(heights: List[List[int]]) -> int:
    m, n = len(heights), len(heights[0])
    effort = [[float("inf")] * n for _ in range(m)]
    effort[0][0] = 0
    heap = [(0, 0, 0)]
    while heap:
        e, r, c = heapq.heappop(heap)
        if e > effort[r][c]: continue
        if r == m - 1 and c == n - 1:
            return e  # @14
        for dr, dc in DIRS:
            nr, nc = r + dr, c + dc
            if 0 <= nr < m and 0 <= nc < n:
                ne = max(e, abs(heights[nr][nc] - heights[r][c]))
                if ne < effort[nr][nc]:
                    effort[nr][nc] = ne
                    heapq.heappush(heap, (ne, nr, nc))  # @22
    return 0
#@end

#@code swimInWater
# DIRS = [(1, 0), (-1, 0), (0, 1), (0, -1)]: down, up, right, left
def swimInWater(grid: List[List[int]]) -> int:
    n, time = len(grid), 0
    seen = {(0, 0)}
    heap = [(grid[0][0], 0, 0)]
    while heap:
        h, r, c = heapq.heappop(heap)
        time = max(time, h)  # @11
        if r == n - 1 and c == n - 1: return time
        for dr, dc in DIRS:
            nr, nc = r + dr, c + dc
            if 0 <= nr < n and 0 <= nc < n and (nr, nc) not in seen:
                seen.add((nr, nc))
                heapq.heappush(heap, (grid[nr][nc], nr, nc))
    return time
#@end
