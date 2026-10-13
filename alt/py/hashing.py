from common import *

#@code twoSum
def twoSum(nums: List[int], target: int) -> List[int]:
    seen = {}  # @2
    for i, x in enumerate(nums):
        need = target - x  # @4
        if need in seen:
            return [seen[need], i]  # @7
        seen[x] = i  # @9
    return []  # @11
#@end

#@code isAnagram
def isAnagram(s: str, t: str) -> bool:
    if len(s) != len(t):
        return False  # @3
    count = [0] * 26  # @5
    for a, b in zip(s, t):
        count[ord(a) - ord('a')] += 1
        count[ord(b) - ord('a')] -= 1  # @8
    for c in count:
        if c != 0:
            return False  # @12
    return True  # @15
#@end

#@code containsDuplicate
def containsDuplicate(nums: List[int]) -> bool:
    seen = set()
    for x in nums:
        if x in seen:  # @4
            return True  # @5
        seen.add(x)
    return False  # @8
#@end

#@code majorityElement
def majorityElement(nums: List[int]) -> int:
    candidate, count = 0, 0
    for x in nums:
        if count == 0:
            candidate = x  # @5
        count += 1 if x == candidate else -1  # @7
    return candidate  # @9
#@end

#@code groupAnagrams
def groupAnagrams(strs: List[str]) -> List[List[str]]:
    groups = defaultdict(list)
    for s in strs:
        key = "".join(sorted(s))  # @6
        groups[key].append(s)  # @7
    return list(groups.values())  # @9
#@end

#@code topKFrequent
def topKFrequent(nums: List[int], k: int) -> List[int]:
    freq = Counter(nums)  # @3
    bucket = [[] for _ in range(len(nums) + 1)]
    for x, f in freq.items():
        bucket[f].append(x)  # @8
    res = []
    for f in range(len(bucket) - 1, 0, -1):
        for x in bucket[f]:
            if len(res) == k:
                break
            res.append(x)  # @16
    return res  # @19
#@end

#@code productExceptSelf
def productExceptSelf(nums: List[int]) -> List[int]:
    n = len(nums)
    res = [1] * n  # @4
    for i in range(1, n):
        res[i] = res[i - 1] * nums[i - 1]  # @6
    suffix = 1
    for i in range(n - 1, -1, -1):
        res[i] *= suffix  # @10
        suffix *= nums[i]
    return res  # @13
#@end

#@code longestConsecutive
def longestConsecutive(nums: List[int]) -> int:
    num_set = set(nums)  # @3
    best = 0
    for x in num_set:
        if x - 1 in num_set:
            continue  # @7
        length = 1  # @9
        while x + length in num_set:
            length += 1  # @11
        best = max(best, length)  # @13
    return best
#@end
