from common import *

#@code subsets
def subsets(nums: List[int]) -> List[List[int]]:
    res = []

    def backtrack(start: int, path: List[int]) -> None:
        res.append(path[:])  # @8
        for i in range(start, len(nums)):
            path.append(nums[i])
            backtrack(i + 1, path)
            path.pop()

    backtrack(0, [])
    return res
#@end

#@code permute
def permute(nums: List[int]) -> List[List[int]]:
    res, used = [], [False] * len(nums)

    def dfs(path: List[int]) -> None:
        if len(path) == len(nums):
            res.append(path[:])  # @9
            return
        for i in range(len(nums)):
            if used[i]: continue
            used[i] = True
            path.append(nums[i])  # @15
            dfs(path)
            path.pop()
            used[i] = False

    dfs([])
    return res
#@end

#@code combinationSum
def combinationSum(candidates: List[int], target: int) -> List[List[int]]:
    candidates.sort()
    res = []

    def search(start: int, remain: int, path: List[int]) -> None:
        if remain == 0:
            res.append(path[:])  # @10
            return
        for i in range(start, len(candidates)):
            if candidates[i] > remain:
                break  # @15
            path.append(candidates[i])  # @17
            search(i, remain - candidates[i], path)
            path.pop()

    search(0, target, [])
    return res
#@end

#@code subsetsWithDup
def subsetsWithDup(nums: List[int]) -> List[List[int]]:
    nums.sort()
    res = []

    def go(start: int, path: List[int]) -> None:
        res.append(path[:])  # @9
        for i in range(start, len(nums)):
            if i > start and nums[i] == nums[i - 1]:
                continue  # @12
            path.append(nums[i])
            go(i + 1, path)
            path.pop()

    go(0, [])
    return res
#@end

#@code combinationSum2
def combinationSum2(candidates: List[int], target: int) -> List[List[int]]:
    candidates.sort()
    res = []

    def find(start: int, remain: int, path: List[int]) -> None:
        if remain == 0:
            res.append(path[:])  # @10
            return
        for i in range(start, len(candidates)):
            if i > start and candidates[i] == candidates[i - 1]: continue
            if candidates[i] > remain: break
            path.append(candidates[i])  # @16
            find(i + 1, remain - candidates[i], path)
            path.pop()

    find(0, target, [])
    return res
#@end

#@code exist
def exist(board: List[List[str]], word: str) -> bool:
    m, n = len(board), len(board[0])

    def match(k: int, r: int, c: int) -> bool:
        if k == len(word): return True
        if r < 0 or c < 0 or r >= m or c >= n or board[r][c] != word[k]: return False
        saved = board[r][c]
        board[r][c] = '#'  # @12
        found = (match(k + 1, r + 1, c) or match(k + 1, r - 1, c)
                 or match(k + 1, r, c + 1) or match(k + 1, r, c - 1))
        board[r][c] = saved  # @15
        return found

    return any(match(0, r, c) for r in range(m) for c in range(n))
#@end

#@code partition
def partition(s: str) -> List[List[str]]:
    res = []

    def cut(start: int, path: List[str]) -> None:
        if start == len(s):
            res.append(path[:])  # @9
            return
        for i in range(start, len(s)):
            piece = s[start:i + 1]
            if piece != piece[::-1]:
                continue  # @14
            path.append(piece)  # @16
            cut(i + 1, path)
            path.pop()

    cut(0, [])
    return res
#@end

#@code solveNQueens
def solveNQueens(n: int) -> List[List[str]]:
    res, queens = [], [0] * n
    cols, diag, anti = set(), set(), set()

    def place(r: int) -> None:
        if r == n:
            res.append(["." * c + "Q" + "." * (n - c - 1) for c in queens])  # @16
            return
        for c in range(n):
            if c in cols or r - c in diag or r + c in anti: continue
            queens[r] = c
            cols.add(c); diag.add(r - c); anti.add(r + c)  # @24
            place(r + 1)
            cols.remove(c); diag.remove(r - c); anti.remove(r + c)

    place(0)
    return res
#@end
