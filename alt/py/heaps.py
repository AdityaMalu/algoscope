from common import *

#@code findKthLargest
def findKthLargest(nums: List[int], k: int) -> int:
    heap = []
    for x in nums:
        heapq.heappush(heap, x)
        if len(heap) > k:
            heapq.heappop(heap)  # @6
    return heap[0]  # @9
#@end

#@code lastStoneWeight
def lastStoneWeight(stones: List[int]) -> int:
    heap = [-s for s in stones]  # @3
    heapq.heapify(heap)
    while len(heap) > 1:
        y, x = -heapq.heappop(heap), -heapq.heappop(heap)
        if y != x: heapq.heappush(heap, -(y - x))  # @6
    return -heap[0] if heap else 0
#@end

#@code kClosest
def kClosest(points: List[List[int]], k: int) -> List[List[int]]:
    heap = []
    for x, y in points:
        heapq.heappush(heap, (-(x * x + y * y), x, y))
        if len(heap) > k: heapq.heappop(heap)  # @5
    return [[x, y] for _, x, y in heap]
#@end

#@code leastInterval
def leastInterval(tasks: List[str], n: int) -> int:
    count = Counter(tasks)
    max_freq = max(count.values())
    count_max = sum(1 for c in count.values() if c == max_freq)  # @7
    frames = (max_freq - 1) * (n + 1) + count_max  # @8
    return max(len(tasks), frames)  # @9
#@end

#@code topKFrequentWords
class Word:
    def __init__(self, word: str, count: int):
        self.word, self.count = word, count

    def __lt__(self, other: "Word") -> bool:
        # the heap's top is the word to evict first: lower count, then alphabetically later
        if self.count != other.count: return self.count < other.count
        return self.word > other.word


def topKFrequent(words: List[str], k: int) -> List[str]:
    count = Counter(words)  # @3
    heap = []
    for w, c in count.items():
        heapq.heappush(heap, Word(w, c))
        if len(heap) > k:
            heapq.heappop(heap)  # @8
    res = []
    while heap: res.append(heapq.heappop(heap).word)  # @12
    return res[::-1]
#@end

#@code reorganizeString
def reorganizeString(s: str) -> str:
    count = Counter(s)
    heap = []
    for c, n in count.items():
        if n > (len(s) + 1) // 2:
            return ""  # @7
        heapq.heappush(heap, (-n, c))
    out = []  # @11
    while len(heap) >= 2:
        n1, a = heapq.heappop(heap)
        n2, b = heapq.heappop(heap)
        out += [a, b]
        if n1 + 1 < 0: heapq.heappush(heap, (n1 + 1, a))
        if n2 + 1 < 0: heapq.heappush(heap, (n2 + 1, b))  # @16
    if heap:
        out.append(heapq.heappop(heap)[1])  # @19
    return "".join(out)
#@end

#@code MedianFinder
class MedianFinder:
    def __init__(self):
        self.low = []   # max-heap (stored negated): smaller half
        self.high = []  # min-heap: larger half

    def addNum(self, num: int) -> None:
        heapq.heappush(self.low, -num)
        heapq.heappush(self.high, -heapq.heappop(self.low))
        if len(self.high) > len(self.low): heapq.heappush(self.low, -heapq.heappop(self.high))  # @8

    def findMedian(self) -> float:
        if len(self.low) > len(self.high): return float(-self.low[0])  # @12
        return (-self.low[0] + self.high[0]) / 2.0
#@end
