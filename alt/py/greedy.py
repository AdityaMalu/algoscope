from common import *

#@code maxSubArray
def maxSubArray(nums: List[int]) -> int:
    cur = best = nums[0]  # @2
    for x in nums[1:]:
        cur = max(x, cur + x)
        best = max(best, cur)  # @5
    return best
#@end

#@code canJump
def canJump(nums: List[int]) -> bool:
    reach = 0
    for i, x in enumerate(nums):
        if i > reach:
            return False  # @5
        reach = max(reach, i + x)  # @7
        if reach >= len(nums) - 1:
            return True  # @9
    return True
#@end

#@code merge
def merge(intervals: List[List[int]]) -> List[List[int]]:
    intervals.sort(key=lambda iv: iv[0])  # @2
    res = []
    for start, end in intervals:
        if res and start <= res[-1][1]:
            res[-1][1] = max(res[-1][1], end)  # @8
        else:
            res.append([start, end])  # @10
    return res
#@end

#@code insert
def insert(intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
    res, i, n = [], 0, len(intervals)
    lo, hi = newInterval
    while i < n and intervals[i][1] < lo:
        res.append(intervals[i])  # @6
        i += 1
    while i < n and intervals[i][0] <= hi:
        lo = min(lo, intervals[i][0])
        hi = max(hi, intervals[i][1])  # @11
        i += 1
    res.append([lo, hi])  # @14
    res.extend(intervals[i:])  # @17
    return res
#@end

#@code jump
def jump(nums: List[int]) -> int:
    jumps = end = far = 0
    for i in range(len(nums) - 1):
        far = max(far, i + nums[i])  # @4
        if i == end:
            jumps += 1
            end = far  # @7
    return jumps
#@end

#@code canCompleteCircuit
def canCompleteCircuit(gas: List[int], cost: List[int]) -> int:
    total = tank = start = 0
    for i in range(len(gas)):
        gain = gas[i] - cost[i]
        total += gain
        tank += gain
        if tank < 0:  # @7
            start = i + 1
            tank = 0
    return start if total >= 0 else -1  # @12
#@end

#@code eraseOverlapIntervals
def eraseOverlapIntervals(intervals: List[List[int]]) -> int:
    intervals.sort(key=lambda iv: iv[1])  # @2
    kept, last_end = 0, float("-inf")
    for start, end in intervals:
        if start >= last_end:  # @5
            kept += 1
            last_end = end
    return len(intervals) - kept
#@end

#@code findMinArrowShots
def findMinArrowShots(points: List[List[int]]) -> int:
    points.sort(key=lambda p: p[1])  # @2
    arrows = 1
    x = points[0][1]  # @4
    for start, end in points[1:]:
        if start > x:  # @6
            arrows += 1
            x = end
    return arrows
#@end
