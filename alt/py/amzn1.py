from common import *

#@code subarraySum
def subarraySum(nums: List[int], k: int) -> int:
    seen = Counter({0: 1})
    prefix = count = 0  # @4
    for x in nums:
        prefix += x
        count += seen[prefix - k]
        seen[prefix] += 1  # @9
    return count
#@end

#@code findAnagrams
def findAnagrams(s: str, p: str) -> List[int]:
    res = []
    if len(p) > len(s): return res
    need, win = Counter(p), Counter()
    for i, c in enumerate(s):
        win[c] += 1
        if i >= len(p):
            old = s[i - len(p)]
            win[old] -= 1
            if win[old] == 0: del win[old]
        if i >= len(p) - 1 and win == need: res.append(i - len(p) + 1)  # @10
    return res
#@end

#@code compress
def compress(chars: List[str]) -> int:
    write = read = 0
    while read < len(chars):
        c, start = chars[read], read
        while read < len(chars) and chars[read] == c: read += 1
        chars[write] = c
        write += 1
        if read - start > 1:
            for d in str(read - start):
                chars[write] = d
                write += 1  # @10
    return write
#@end

#@code nextPermutation
def nextPermutation(nums: List[int]) -> None:
    i = len(nums) - 2
    while i >= 0 and nums[i] >= nums[i + 1]: i -= 1  # @3
    if i >= 0:
        j = len(nums) - 1
        while nums[j] <= nums[i]: j -= 1
        nums[i], nums[j] = nums[j], nums[i]  # @7
    nums[i + 1:] = reversed(nums[i + 1:])  # @9
#@end

#@code spiralOrder
def spiralOrder(matrix: List[List[int]]) -> List[int]:
    res = []
    top, bottom, left, right = 0, len(matrix) - 1, 0, len(matrix[0]) - 1
    while top <= bottom and left <= right:
        res += [matrix[top][c] for c in range(left, right + 1)]  # @5
        top += 1
        res += [matrix[r][right] for r in range(top, bottom + 1)]  # @7
        right -= 1
        if top <= bottom:
            res += [matrix[bottom][c] for c in range(right, left - 1, -1)]  # @10
            bottom -= 1
        if left <= right:
            res += [matrix[r][left] for r in range(bottom, top - 1, -1)]  # @14
            left += 1
    return res
#@end

#@code rotate
def rotate(matrix: List[List[int]]) -> None:
    n = len(matrix)
    for i in range(n):
        for j in range(i + 1, n):
            matrix[i][j], matrix[j][i] = matrix[j][i], matrix[i][j]  # @7
    for row in matrix:
        row.reverse()  # @13
#@end

#@code setZeroes
def setZeroes(matrix: List[List[int]]) -> None:
    m, n = len(matrix), len(matrix[0])
    first_row = any(matrix[0][j] == 0 for j in range(n))
    first_col = any(matrix[i][0] == 0 for i in range(m))
    for i in range(1, m):
        for j in range(1, n):
            if matrix[i][j] == 0:
                matrix[i][0] = matrix[0][j] = 0  # @10
    for i in range(1, m):
        for j in range(1, n):
            if matrix[i][0] == 0 or matrix[0][j] == 0: matrix[i][j] = 0  # @14
    if first_row:
        for j in range(n): matrix[0][j] = 0
    if first_col:
        for i in range(m): matrix[i][0] = 0  # @16
#@end
