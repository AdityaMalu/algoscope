from common import *

#@code climbStairs
def climbStairs(n: int) -> int:
    prev2, prev1 = 1, 1  # @2
    for _ in range(2, n + 1):
        prev2, prev1 = prev1, prev1 + prev2  # @4
    return prev1
#@end

#@code minCostClimbingStairs
def minCostClimbingStairs(cost: List[int]) -> int:
    n = len(cost)
    dp = [0] * (n + 1)
    for i in range(2, n + 1):
        dp[i] = min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2])  # @5
    return dp[n]  # @7
#@end

#@code rob
def rob(nums: List[int]) -> int:
    prev2 = prev1 = 0
    for x in nums:
        prev2, prev1 = prev1, max(prev1, prev2 + x)  # @4
    return prev1
#@end

#@code robCircle
def robCircle(nums: List[int]) -> int:
    if len(nums) == 1: return nums[0]
    a = robRange(nums, 0, len(nums) - 2)  # @3
    b = robRange(nums, 1, len(nums) - 1)  # @4
    return max(a, b)


def robRange(nums: List[int], lo: int, hi: int) -> int:
    prev2 = prev1 = 0
    for i in range(lo, hi + 1):
        prev2, prev1 = prev1, max(prev1, prev2 + nums[i])  # @11
    return prev1
#@end

#@code coinChange
def coinChange(coins: List[int], amount: int) -> int:
    dp = [amount + 1] * (amount + 1)
    dp[0] = 0  # @4
    for a in range(1, amount + 1):
        for c in coins:
            if c <= a and dp[a - c] + 1 < dp[a]:
                dp[a] = dp[a - c] + 1  # @8
    return -1 if dp[amount] > amount else dp[amount]  # @13
#@end

#@code lengthOfLIS
def lengthOfLIS(nums: List[int]) -> int:
    tails = []
    for x in nums:
        i = bisect.bisect_left(tails, x)
        if i == len(tails): tails.append(x)
        else: tails[i] = x  # @13
    return len(tails)
#@end

#@code wordBreak
def wordBreak(s: str, wordDict: List[str]) -> bool:
    words = set(wordDict)
    dp = [True] + [False] * len(s)
    for i in range(1, len(s) + 1):
        for j in range(i):
            if dp[j] and s[j:i] in words:
                dp[i] = True  # @8
                break
    return dp[len(s)]  # @13
#@end

#@code numDecodings
def numDecodings(s: str) -> int:
    n = len(s)
    dp = [1] + [0] * n
    for i in range(1, n + 1):
        if s[i - 1] != '0': dp[i] += dp[i - 1]
        if i >= 2 and 10 <= int(s[i - 2:i]) <= 26: dp[i] += dp[i - 2]  # @10
    return dp[n]
#@end
