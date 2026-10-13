from common import *

#@code search
def search(nums: List[int], target: int) -> int:
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        mid = (lo + hi) // 2  # @4
        if nums[mid] == target:
            return mid  # @6
        if nums[mid] < target:
            lo = mid + 1  # @9
        else:
            hi = mid - 1  # @11
    return -1  # @14
#@end

#@code searchInsert
def searchInsert(nums: List[int], target: int) -> int:
    lo, hi = 0, len(nums)
    while lo < hi:
        mid = (lo + hi) // 2
        if nums[mid] < target:
            lo = mid + 1  # @6
        else:
            hi = mid  # @8
    return lo  # @11
#@end

#@code findMin
def findMin(nums: List[int]) -> int:
    lo, hi = 0, len(nums) - 1
    while lo < hi:
        mid = (lo + hi) // 2
        if nums[mid] > nums[hi]:
            lo = mid + 1  # @6
        else:
            hi = mid  # @8
    return nums[lo]  # @11
#@end

#@code searchMatrix
def searchMatrix(matrix: List[List[int]], target: int) -> bool:
    m, n = len(matrix), len(matrix[0])
    lo, hi = 0, m * n - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        val = matrix[mid // n][mid % n]  # @6
        if val == target:
            return True  # @8
        if val < target: lo = mid + 1
        else: hi = mid - 1
    return False  # @13
#@end

#@code searchRotated
def searchRotated(nums: List[int], target: int) -> int:
    lo, hi = 0, len(nums) - 1
    while lo <= hi:
        mid = (lo + hi) // 2
        if nums[mid] == target:
            return mid  # @6
        if nums[lo] <= nums[mid]:
            if nums[lo] <= target < nums[mid]: hi = mid - 1  # @9
            else: lo = mid + 1
        else:
            if nums[mid] < target <= nums[hi]: lo = mid + 1  # @12
            else: hi = mid - 1
    return -1  # @16
#@end

#@code minEatingSpeed
def minEatingSpeed(piles: List[int], h: int) -> int:
    lo, hi = 1, max(piles)  # @3
    while lo < hi:
        k = (lo + hi) // 2
        hours = sum((p + k - 1) // k for p in piles)  # @7
        if hours <= h: hi = k
        else: lo = k + 1
    return lo  # @11
#@end

#@code shipWithinDays
def shipWithinDays(weights: List[int], days: int) -> int:
    lo, hi = max(weights), sum(weights)  # @3
    while lo < hi:
        cap = (lo + hi) // 2
        need = daysNeeded(weights, cap)  # @6
        if need <= days: hi = cap
        else: lo = cap + 1
    return lo  # @10


def daysNeeded(weights: List[int], cap: int) -> int:
    d, load = 1, 0
    for w in weights:
        if load + w > cap:
            d += 1
            load = 0
        load += w
    return d
#@end
