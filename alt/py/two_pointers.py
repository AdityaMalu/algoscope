from common import *

#@code isPalindrome
def isPalindrome(s: str) -> bool:
    l, r = 0, len(s) - 1
    while l < r:
        while l < r and not s[l].isalnum(): l += 1
        while l < r and not s[r].isalnum(): r -= 1
        if s[l].lower() != s[r].lower():  # @6
            return False  # @7
        l += 1
        r -= 1
    return True  # @12
#@end

#@code moveZeroes
def moveZeroes(nums: List[int]) -> None:
    w = 0
    for r in range(len(nums)):
        if nums[r] == 0:
            continue  # @5
        nums[w], nums[r] = nums[r], nums[w]  # @7
        w += 1  # @8
#@end

#@code twoSumSorted
def twoSum(numbers: List[int], target: int) -> List[int]:
    l, r = 0, len(numbers) - 1
    while l < r:
        total = numbers[l] + numbers[r]  # @4
        if total == target:
            return [l + 1, r + 1]  # @6
        if total < target: l += 1
        else: r -= 1
    return [-1, -1]
#@end

#@code sortColors
def sortColors(nums: List[int]) -> None:
    lo, mid, hi = 0, 0, len(nums) - 1  # @2
    while mid <= hi:
        if nums[mid] == 0:
            nums[lo], nums[mid] = nums[mid], nums[lo]  # @5
            lo += 1
            mid += 1
        elif nums[mid] == 1:
            mid += 1  # @9
        else:
            nums[mid], nums[hi] = nums[hi], nums[mid]  # @11
            hi -= 1  # @17
#@end

#@code threeSum
def threeSum(nums: List[int]) -> List[List[int]]:
    nums.sort()  # @2
    res = []
    for i in range(len(nums) - 2):
        if nums[i] > 0:
            break  # @6
        if i > 0 and nums[i] == nums[i - 1]:
            continue  # @9
        l, r = i + 1, len(nums) - 1
        while l < r:
            total = nums[i] + nums[l] + nums[r]  # @13
            if total < 0: l += 1
            elif total > 0: r -= 1
            else:
                res.append([nums[i], nums[l], nums[r]])  # @17
                while l < r and nums[l] == nums[l + 1]: l += 1
                while l < r and nums[r] == nums[r - 1]: r -= 1
                l += 1
                r -= 1
    return res
#@end

#@code maxArea
def maxArea(height: List[int]) -> int:
    l, r, best = 0, len(height) - 1, 0
    while l < r:
        area = min(height[l], height[r]) * (r - l)
        best = max(best, area)  # @5
        if height[l] < height[r]: l += 1
        else: r -= 1
    return best
#@end

#@code trap
def trap(height: List[int]) -> int:
    l, r = 0, len(height) - 1
    left_max = right_max = total = 0
    while l < r:
        if height[l] < height[r]:
            left_max = max(left_max, height[l])
            total += left_max - height[l]  # @6
            l += 1
        else:
            right_max = max(right_max, height[r])
            total += right_max - height[r]  # @10
            r -= 1
    return total  # @14
#@end
