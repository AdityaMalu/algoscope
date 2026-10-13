from common import *

#@code isValid
def isValid(s: str) -> bool:
    pairs = {')': '(', ']': '[', '}': '{'}
    stack = []
    for c in s:
        if c in '([{':
            stack.append(c)  # @6
            continue
        if not stack or stack[-1] != pairs[c]:
            return False  # @11
        stack.pop()  # @13
    return not stack  # @15
#@end

#@code MinStack
class MinStack:
    def __init__(self):
        self.stack = []
        self.mins = []

    def push(self, x: int) -> None:
        self.stack.append(x)
        self.mins.append(x if not self.mins else min(x, self.mins[-1]))  # @7

    def pop(self) -> None:
        self.stack.pop()  # @11
        self.mins.pop()

    def top(self) -> int:
        return self.stack[-1]  # @16

    def getMin(self) -> int:
        return self.mins[-1]  # @20
#@end

#@code nextGreaterElement
def nextGreaterElement(nums1: List[int], nums2: List[int]) -> List[int]:
    nxt = {}
    stack = []
    for x in nums2:
        while stack and stack[-1] < x:
            nxt[stack.pop()] = x  # @7
        stack.append(x)  # @9
    return [nxt.get(x, -1) for x in nums1]  # @12
#@end

#@code evalRPN
def evalRPN(tokens: List[str]) -> int:
    stack = []
    for t in tokens:
        if t in ("+", "-", "*", "/"):
            b, a = stack.pop(), stack.pop()
            if t == "+": r = a + b
            elif t == "-": r = a - b
            elif t == "*": r = a * b
            else: r = int(a / b)  # truncate toward zero
            stack.append(r)  # @13
        else:
            stack.append(int(t))  # @15
    return stack.pop()  # @18
#@end

#@code dailyTemperatures
def dailyTemperatures(temps: List[int]) -> List[int]:
    ans = [0] * len(temps)
    stack = []
    for i, t in enumerate(temps):
        while stack and temps[stack[-1]] < t:
            j = stack.pop()
            ans[j] = i - j  # @7
        stack.append(i)  # @9
    return ans
#@end

#@code asteroidCollision
def asteroidCollision(asteroids: List[int]) -> List[int]:
    stack = []
    for a in asteroids:
        alive = True
        while alive and a < 0 and stack and stack[-1] > 0:
            top = stack[-1]
            if top < -a:
                stack.pop()  # @9
                continue
            if top == -a:
                stack.pop()  # @13
            alive = False  # @15
        if alive:
            stack.append(a)  # @18
    return stack
#@end

#@code largestRectangleArea
def largestRectangleArea(heights: List[int]) -> int:
    stack = []
    best, n = 0, len(heights)
    for i in range(n + 1):
        h = 0 if i == n else heights[i]
        while stack and heights[stack[-1]] > h:
            height = heights[stack.pop()]
            left = stack[-1] if stack else -1
            width = i - left - 1
            best = max(best, height * width)  # @10
        stack.append(i)  # @12
    return best
#@end
