from common import *

#@code maxProfit
def maxProfit(prices: List[int]) -> int:
    min_price, best = float("inf"), 0
    for p in prices:
        min_price = min(min_price, p)
        best = max(best, p - min_price)  # @5
    return best
#@end

#@code lengthOfLongestSubstring
def lengthOfLongestSubstring(s: str) -> int:
    last = {}
    l = best = 0
    for r, c in enumerate(s):
        if c in last and last[c] >= l:
            l = last[c] + 1  # @7
        last[c] = r
        best = max(best, r - l + 1)  # @10
    return best
#@end

#@code longestOnes
def longestOnes(nums: List[int], k: int) -> int:
    l = zeros = best = 0
    for r, x in enumerate(nums):
        if x == 0: zeros += 1
        while zeros > k:
            if nums[l] == 0: zeros -= 1
            l += 1  # @7
        best = max(best, r - l + 1)  # @9
    return best
#@end

#@code totalFruit
def totalFruit(fruits: List[int]) -> int:
    count = defaultdict(int)
    l = best = 0
    for r, f in enumerate(fruits):
        count[f] += 1
        while len(count) > 2:
            count[fruits[l]] -= 1
            if count[fruits[l]] == 0: del count[fruits[l]]
            l += 1  # @9
        best = max(best, r - l + 1)  # @11
    return best
#@end

#@code characterReplacement
def characterReplacement(s: str, k: int) -> int:
    count = [0] * 26
    l = max_count = best = 0
    for r, c in enumerate(s):
        count[ord(c) - ord('A')] += 1
        max_count = max(max_count, count[ord(c) - ord('A')])
        if r - l + 1 - max_count > k:
            count[ord(s[l]) - ord('A')] -= 1  # @7
            l += 1
        best = max(best, r - l + 1)  # @10
    return best
#@end

#@code checkInclusion
def checkInclusion(s1: str, s2: str) -> bool:
    if len(s1) > len(s2): return False
    need, win = [0] * 26, [0] * 26
    for i in range(len(s1)):
        need[ord(s1[i]) - 97] += 1
        win[ord(s2[i]) - 97] += 1
    matches = sum(need[i] == win[i] for i in range(26))
    m = len(s1)  # @10
    for r in range(m, len(s2)):
        if matches == 26:
            return True  # @13
        i, o = ord(s2[r]) - 97, ord(s2[r - m]) - 97
        win[i] += 1
        if win[i] == need[i]: matches += 1
        elif win[i] == need[i] + 1: matches -= 1
        win[o] -= 1
        if win[o] == need[o]: matches += 1
        elif win[o] == need[o] - 1: matches -= 1  # @21
    return matches == 26  # @23
#@end

#@code minWindow
def minWindow(s: str, t: str) -> str:
    need = Counter(t)
    missing, l, best_l, best_len = len(t), 0, 0, float("inf")  # @4
    for r, c in enumerate(s):
        if need[c] > 0:
            missing -= 1  # @7
        need[c] -= 1
        while missing == 0:
            if r - l + 1 < best_len:
                best_len = r - l + 1
                best_l = l  # @12
            need[s[l]] += 1
            if need[s[l]] > 0:
                missing += 1  # @15
            l += 1
    return "" if best_len == float("inf") else s[best_l:best_l + best_len]  # @20
#@end
