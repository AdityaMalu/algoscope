from common import *

#@code RandomizedSet
class RandomizedSet:
    def __init__(self):
        self.items = []
        self.index = {}

    def insert(self, val: int) -> bool:
        if val in self.index:
            return False  # @8
        self.index[val] = len(self.items)
        self.items.append(val)  # @11
        return True

    def remove(self, val: int) -> bool:
        if val not in self.index:
            return False  # @18
        i, last = self.index[val], self.items[-1]
        self.items[i] = last
        self.index[last] = i
        self.items.pop()
        del self.index[val]  # @24
        return True

    def getRandom(self) -> int:
        return random.choice(self.items)  # @30
#@end

#@code TimeMap
class TimeMap:
    def __init__(self):
        self.times = defaultdict(list)
        self.values = defaultdict(list)

    def set(self, key: str, value: str, timestamp: int) -> None:
        self.times[key].append(timestamp)
        self.values[key].append(value)  # @7

    def get(self, key: str, timestamp: int) -> str:
        if key not in self.times:
            return ""  # @13
        i = bisect.bisect_right(self.times[key], timestamp)
        return self.values[key][i - 1] if i else ""  # @21
#@end

#@code UndergroundSystem
class UndergroundSystem:
    def __init__(self):
        self.check_ins = {}
        self.routes = defaultdict(lambda: [0, 0])

    def checkIn(self, id: int, station: str, t: int) -> None:
        self.check_ins[id] = (station, t)  # @8

    def checkOut(self, id: int, station: str, t: int) -> None:
        start, t0 = self.check_ins.pop(id)
        route = self.routes[(start, station)]
        route[0] += t - t0
        route[1] += 1  # @16

    def getAverageTime(self, startStation: str, endStation: str) -> float:
        total, count = self.routes[(startStation, endStation)]
        return total / count  # @21
#@end

#@code SnapshotArray
class SnapshotArray:
    def __init__(self, length: int):
        self.history = [[(0, 0)] for _ in range(length)]
        self.snap_id = 0

    def set(self, index: int, val: int) -> None:
        h = self.history[index]
        if h[-1][0] == self.snap_id: h[-1] = (self.snap_id, val)
        else: h.append((self.snap_id, val))  # @16

    def snap(self) -> int:
        self.snap_id += 1
        return self.snap_id - 1  # @20

    def get(self, index: int, snap_id: int) -> int:
        h = self.history[index]
        i = bisect.bisect_right(h, (snap_id, float("inf"))) - 1  # @29
        return h[i][1]
#@end

#@code Twitter
class Twitter:
    def __init__(self):
        self.time = 0
        self.tweets = defaultdict(list)
        self.follows = defaultdict(set)

    def postTweet(self, userId: int, tweetId: int) -> None:
        self.tweets[userId].append((self.time, tweetId))  # @7
        self.time += 1

    def getNewsFeed(self, userId: int) -> List[int]:
        heap = []
        for u in self.follows[userId] | {userId}:
            if self.tweets[u]:
                i = len(self.tweets[u]) - 1
                t, tid = self.tweets[u][i]
                heap.append((-t, tid, u, i))
        heapq.heapify(heap)
        feed = []
        while heap and len(feed) < 10:
            _, tid, u, i = heapq.heappop(heap)
            feed.append(tid)
            if i > 0:
                t, prev = self.tweets[u][i - 1]
                heapq.heappush(heap, (-t, prev, u, i - 1))  # @24
        return feed

    def follow(self, followerId: int, followeeId: int) -> None:
        if followerId != followeeId: self.follows[followerId].add(followeeId)  # @31

    def unfollow(self, followerId: int, followeeId: int) -> None:
        self.follows[followerId].discard(followeeId)  # @35
#@end

#@code FreqStack
class FreqStack:
    def __init__(self):
        self.freq = Counter()
        self.group = defaultdict(list)
        self.max_freq = 0

    def push(self, x: int) -> None:
        self.freq[x] += 1
        f = self.freq[x]
        self.max_freq = max(self.max_freq, f)
        self.group[f].append(x)  # @9

    def pop(self) -> int:
        x = self.group[self.max_freq].pop()
        self.freq[x] -= 1
        if not self.group[self.max_freq]: self.max_freq -= 1  # @15
        return x
#@end
