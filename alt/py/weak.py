from common import *

#@code gameOfLife
def gameOfLife(board: List[List[int]]) -> None:
    m, n = len(board), len(board[0])
    for r in range(m):
        for c in range(n):
            live = sum(board[nr][nc] & 1
                       for nr in range(r - 1, r + 2) for nc in range(c - 1, c + 2)
                       if (nr, nc) != (r, c) and 0 <= nr < m and 0 <= nc < n)
            alive = board[r][c] & 1
            if (alive and live in (2, 3)) or (not alive and live == 3): board[r][c] |= 2  # @13
    for r in range(m):
        for c in range(n): board[r][c] >>= 1  # @17
#@end

#@code threeSumClosest
def threeSumClosest(nums: List[int], target: int) -> int:
    nums.sort()
    closest = nums[0] + nums[1] + nums[2]
    for i in range(len(nums) - 2):
        l, r = i + 1, len(nums) - 1
        while l < r:
            total = nums[i] + nums[l] + nums[r]
            if abs(total - target) < abs(closest - target): closest = total  # @8
            if total == target: return total
            if total < target: l += 1
            else: r -= 1
    return closest
#@end

#@code numberToWords
BELOW_20 = ["", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten", "Eleven", "Twelve",
            "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"]
TENS = ["", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"]
SCALES = ["", "Thousand", "Million", "Billion"]


def numberToWords(num: int) -> str:
    if num == 0: return "Zero"
    res, scale = "", 0
    while num > 0:
        chunk = num % 1000
        if chunk: res = three(chunk) + SCALES[scale] + " " + res  # @11
        num //= 1000
        scale += 1
    return " ".join(res.split())


def three(n: int) -> str:
    s = ""
    if n >= 100:
        s += BELOW_20[n // 100] + " Hundred "
        n %= 100
    if n >= 20:
        s += TENS[n // 10] + " "
        n %= 10
    if n > 0: s += BELOW_20[n] + " "
    return s
#@end
