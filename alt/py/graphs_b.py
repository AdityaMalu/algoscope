from common import *

#@code canFinish
def canFinish(n: int, prerequisites: List[List[int]]) -> bool:
    adj = [[] for _ in range(n)]
    indegree = [0] * n
    for a, b in prerequisites:
        adj[b].append(a)
        indegree[a] += 1
    queue = deque(i for i in range(n) if indegree[i] == 0)  # @10
    taken = 0
    while queue:
        c = queue.popleft()
        taken += 1
        for nxt in adj[c]:
            indegree[nxt] -= 1
            if indegree[nxt] == 0: queue.append(nxt)  # @16
    return taken == n  # @19
#@end

#@code findCircleNum
def findCircleNum(isConnected: List[List[int]]) -> int:
    n = len(isConnected)
    parent = list(range(n))  # @11

    def find(x: int) -> int:
        if parent[x] != x: parent[x] = find(parent[x])
        return parent[x]

    groups = n
    for i in range(n):
        for j in range(i + 1, n):
            if isConnected[i][j] == 0: continue
            a, b = find(i), find(j)
            if a != b:
                parent[a] = b
                groups -= 1  # @18
    return groups
#@end

#@code eventualSafeNodes
def eventualSafeNodes(graph: List[List[int]]) -> List[int]:
    n = len(graph)
    rev = [[] for _ in range(n)]
    out = [len(g) for g in graph]
    for u in range(n):
        for v in graph[u]: rev[v].append(u)
    queue = deque(i for i in range(n) if out[i] == 0)  # @11
    safe = [False] * n
    while queue:
        v = queue.popleft()
        safe[v] = True
        for u in rev[v]:
            out[u] -= 1
            if out[u] == 0: queue.append(u)  # @17
    return [i for i in range(n) if safe[i]]
#@end

#@code findOrder
def findOrder(n: int, prerequisites: List[List[int]]) -> List[int]:
    adj = [[] for _ in range(n)]
    indegree = [0] * n
    for a, b in prerequisites:
        adj[b].append(a)
        indegree[a] += 1
    queue = deque(i for i in range(n) if indegree[i] == 0)
    order = []  # @12
    while queue:
        c = queue.popleft()
        order.append(c)
        for nxt in adj[c]:
            indegree[nxt] -= 1
            if indegree[nxt] == 0: queue.append(nxt)  # @17
    return order if len(order) == n else []
#@end

#@code findRedundantConnection
def findRedundantConnection(edges: List[List[int]]) -> List[int]:
    parent = list(range(len(edges) + 1))

    def root(x: int) -> int:
        while parent[x] != x:
            parent[x] = parent[parent[x]]
            x = parent[x]
        return x

    for u, v in edges:
        a, b = root(u), root(v)
        if a == b:
            return [u, v]  # @7
        parent[a] = b  # @9
    return []
#@end

#@code accountsMerge
def accountsMerge(accounts: List[List[str]]) -> List[List[str]]:
    parent, owner = {}, {}

    def find(x: str) -> str:
        if parent[x] != x: parent[x] = find(parent[x])
        return parent[x]

    for acc in accounts:
        first = acc[1]
        for email in acc[1:]:
            parent.setdefault(email, email)
            owner[email] = acc[0]
            parent[find(email)] = find(first)  # @17
    groups = defaultdict(list)
    for email in parent: groups[find(email)].append(email)  # @21
    res = [[owner[emails[0]]] + sorted(emails) for emails in groups.values()]
    return sorted(res, key=lambda a: a[1])
#@end

#@code ladderLength
def ladderLength(beginWord: str, endWord: str, wordList: List[str]) -> int:
    words = set(wordList)
    if endWord not in words:
        return 0  # @4
    queue = deque([beginWord])
    words.discard(beginWord)
    level = 1
    while queue:  # @10
        for _ in range(len(queue)):
            w = queue.popleft()
            for i in range(len(w)):
                for ch in "abcdefghijklmnopqrstuvwxyz":
                    nxt = w[:i] + ch + w[i + 1:]
                    if nxt == endWord:
                        return level + 1  # @19
                    if nxt in words:
                        words.remove(nxt)
                        queue.append(nxt)  # @22
        level += 1
    return 0
#@end
