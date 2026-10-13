from common import *

#@code maximumUnits
def maximumUnits(boxTypes: List[List[int]], truckSize: int) -> int:
    boxTypes.sort(key=lambda b: -b[1])  # @2
    total = 0
    for count, units in boxTypes:
        if truckSize == 0: break
        take = min(truckSize, count)
        total += take * units
        truckSize -= take  # @7
    return total
#@end

#@code numTeams
def numTeams(rating: List[int]) -> int:
    n, teams = len(rating), 0
    for j in range(1, n - 1):
        less_l = sum(1 for i in range(j) if rating[i] < rating[j])
        more_l = j - less_l
        less_r = sum(1 for k in range(j + 1, n) if rating[k] < rating[j])
        more_r = n - 1 - j - less_r
        teams += less_l * more_r + more_l * less_r  # @13
    return teams
#@end

#@code numberOfWays
def numberOfWays(s: str) -> int:
    total0, total1 = s.count('0'), s.count('1')
    left0 = left1 = ways = 0
    for ch in s:
        if ch == '0':
            ways += left1 * (total1 - left1)  # @11
            left0 += 1
        else:
            ways += left0 * (total0 - left0)  # @15
            left1 += 1
    return ways
#@end

#@code totalStrength
def totalStrength(strength: List[int]) -> int:
    MOD = 10**9 + 7
    n = len(strength)
    left, right, st = [-1] * n, [n] * n, []
    for i in range(n):
        while st and strength[st[-1]] >= strength[i]: st.pop()
        left[i] = st[-1] if st else -1
        st.append(i)
    st = []
    for i in range(n - 1, -1, -1):
        while st and strength[st[-1]] > strength[i]: st.pop()
        right[i] = st[-1] if st else n
        st.append(i)  # @15
    prefix = [0] * (n + 1)
    for i in range(n): prefix[i + 1] = (prefix[i] + strength[i]) % MOD
    pp = [0] * (n + 2)
    for i in range(n + 1): pp[i + 1] = (pp[i] + prefix[i]) % MOD  # @19
    ans = 0
    for i in range(n):
        l, r = left[i], right[i]
        pos = (pp[r + 1] - pp[i + 1]) * (i - l)
        neg = (pp[i + 1] - pp[l + 1]) * (r - i)
        ans = (ans + strength[i] * (pos - neg)) % MOD  # @26
    return ans
#@end
