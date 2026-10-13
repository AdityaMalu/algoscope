from common import *

#@code uniquePaths
def uniquePaths(m: int, n: int) -> int:
    dp = [[1] * n for _ in range(m)]
    for r in range(1, m):
        for c in range(1, n):
            dp[r][c] = dp[r - 1][c] + dp[r][c - 1]  # @6
    return dp[m - 1][n - 1]
#@end

#@code minPathSum
def minPathSum(grid: List[List[int]]) -> int:
    m, n = len(grid), len(grid[0])
    for r in range(m):
        for c in range(n):
            if r == 0 and c == 0: continue
            top = grid[r - 1][c] if r > 0 else float("inf")
            left = grid[r][c - 1] if c > 0 else float("inf")
            grid[r][c] += min(top, left)  # @8
    return grid[m - 1][n - 1]
#@end

#@code longestCommonSubsequence
def longestCommonSubsequence(a: str, b: str) -> int:
    m, n = len(a), len(b)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if a[i - 1] == b[j - 1]:
                dp[i][j] = dp[i - 1][j - 1] + 1  # @7
            else:
                dp[i][j] = max(dp[i - 1][j], dp[i][j - 1])  # @9
    return dp[m][n]
#@end

#@code canPartition
def canPartition(nums: List[int]) -> bool:
    total = sum(nums)
    if total % 2:
        return False  # @5
    target = total // 2
    dp = [True] + [False] * target  # @9
    for x in nums:
        for s in range(target, x - 1, -1):
            dp[s] = dp[s] or dp[s - x]  # @12
        if dp[target]:
            return True  # @15
    return False
#@end

#@code change
def change(amount: int, coins: List[int]) -> int:
    dp = [1] + [0] * amount
    for coin in coins:
        for a in range(coin, amount + 1):
            dp[a] += dp[a - coin]  # @6
    return dp[amount]
#@end

#@code findTargetSumWays
def findTargetSumWays(nums: List[int], target: int) -> int:
    total = sum(nums)
    if abs(target) > total or (total + target) % 2:
        return 0  # @5
    want = (total + target) // 2
    dp = [1] + [0] * want  # @9
    for x in nums:
        for s in range(want, x - 1, -1):
            dp[s] += dp[s - x]  # @12
    return dp[want]
#@end

#@code minDistance
def minDistance(a: str, b: str) -> int:
    m, n = len(a), len(b)
    dp = [[0] * (n + 1) for _ in range(m + 1)]
    for i in range(m + 1): dp[i][0] = i
    for j in range(n + 1): dp[0][j] = j  # @5
    for i in range(1, m + 1):
        for j in range(1, n + 1):
            if a[i - 1] == b[j - 1]:
                dp[i][j] = dp[i - 1][j - 1]  # @9
            else:
                dp[i][j] = 1 + min(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])  # @11
    return dp[m][n]
#@end

#@code maxProfitCooldown
def maxProfit(prices: List[int]) -> int:
    hold, sold, rest = float("-inf"), 0, 0
    for p in prices:
        prev_sold = sold
        sold = hold + p
        hold = max(hold, rest - p)
        rest = max(rest, prev_sold)  # @8
    return max(sold, rest)
#@end
