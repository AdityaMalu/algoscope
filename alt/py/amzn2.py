from common import *


def build_r(spec):
    nodes = [Node(v) for v, _ in spec]
    for i, (_, r) in enumerate(spec):
        if i + 1 < len(nodes): nodes[i].next = nodes[i + 1]
        if r is not None: nodes[i].random = nodes[r]
    return nodes[0] if nodes else None


def show_r(head):
    order, c = [], head
    while c: order.append(c); c = c.next
    pos = {id(n): i for i, n in enumerate(order)}
    return Raw(fmt([Raw(f"[{n.val}, {'null' if n.random is None else pos[id(n.random)]}]") for n in order]))


#@code copyRandomList
class Node:
    def __init__(self, val: int, next: "Node" = None, random: "Node" = None):
        self.val, self.next, self.random = val, next, random


def copyRandomList(head: Optional[Node]) -> Optional[Node]:
    copies = {None: None}
    cur = head
    while cur:
        copies[cur] = Node(cur.val)  # @10
        cur = cur.next
    cur = head
    while cur:
        copies[cur].next = copies[cur.next]
        copies[cur].random = copies[cur.random]  # @14
        cur = cur.next
    return copies[head]
#@end

#@code decodeString
def decodeString(s: str) -> str:
    stack, cur, k = [], "", 0
    for c in s:
        if c.isdigit():
            k = k * 10 + int(c)
        elif c == '[':
            stack.append((cur, k))
            cur, k = "", 0  # @14
        elif c == ']':
            outer, times = stack.pop()
            cur = outer + cur * times  # @19
        else:
            cur += c  # @21
    return cur
#@end

#@code findDuplicate
def findDuplicate(nums: List[int]) -> int:
    slow = fast = 0
    while True:
        slow = nums[slow]
        fast = nums[nums[fast]]  # @5
        if slow == fast: break  # @6
    slow = 0
    while slow != fast:
        slow = nums[slow]
        fast = nums[fast]  # @10
    return slow  # @12
#@end

#@code isValidSudoku
def isValidSudoku(board: List[List[str]]) -> bool:
    seen = set()
    for r in range(9):
        for c in range(9):
            d = board[r][c]
            if d == '.': continue
            keys = (f"{d} in row {r}", f"{d} in col {c}", f"{d} in box {(r // 3) * 3 + c // 3}")
            if any(k in seen for k in keys):  # @8
                return False  # @10
            seen.update(keys)
    return True  # @13
#@end

#@code calculate
def calculate(s: str) -> int:
    stack, num, op = [], 0, '+'
    for i, c in enumerate(s):
        if c.isdigit(): num = num * 10 + int(c)
        if (not c.isdigit() and c != ' ') or i == len(s) - 1:
            if op == '+': stack.append(num)  # @9
            elif op == '-': stack.append(-num)
            elif op == '*': stack.append(stack.pop() * num)
            else: stack.append(int(stack.pop() / num))  # truncate toward zero
            op, num = c, 0
    return sum(stack)  # @20
#@end

#@code maxSlidingWindow
def maxSlidingWindow(nums: List[int], k: int) -> List[int]:
    res, dq = [], deque()
    for i, x in enumerate(nums):
        if dq and dq[0] <= i - k: dq.popleft()
        while dq and nums[dq[-1]] < x: dq.pop()
        dq.append(i)
        if i >= k - 1: res.append(nums[dq[0]])  # @8
    return res
#@end

#@code firstMissingPositive
def firstMissingPositive(nums: List[int]) -> int:
    n = len(nums)
    for i in range(n):
        while 1 <= nums[i] <= n and nums[nums[i] - 1] != nums[i]:
            v = nums[i]
            nums[i], nums[v - 1] = nums[v - 1], v  # @7
    for i in range(n):
        if nums[i] != i + 1:
            return i + 1  # @12
    return n + 1  # @15
#@end
