from common import *

#@code reverseList
def reverseList(head: Optional[ListNode]) -> Optional[ListNode]:
    prev, curr = None, head  # @2
    while curr:
        nxt = curr.next
        curr.next = prev  # @5
        prev = curr
        curr = nxt  # @7
    return prev  # @9
#@end

#@code mergeTwoLists
def mergeTwoLists(a: Optional[ListNode], b: Optional[ListNode]) -> Optional[ListNode]:
    dummy = tail = ListNode(0)
    while a and b:
        if a.val <= b.val:
            tail.next = a
            a = a.next
        else:
            tail.next = b
            b = b.next
        tail = tail.next  # @11
    tail.next = a if a else b  # @13
    return dummy.next
#@end

#@code hasCycle
def hasCycle(head: Optional[ListNode]) -> bool:
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next  # @5
        if slow is fast:
            return True  # @7
    return False  # @10
#@end

#@code middleNode
def middleNode(head: Optional[ListNode]) -> Optional[ListNode]:
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next  # @5
    return slow  # @7
#@end

#@code removeNthFromEnd
def removeNthFromEnd(head: Optional[ListNode], n: int) -> Optional[ListNode]:
    dummy = ListNode(0, head)
    slow = fast = dummy
    for _ in range(n):
        fast = fast.next  # @4
    while fast.next:
        slow = slow.next
        fast = fast.next  # @8
    slow.next = slow.next.next  # @10
    return dummy.next
#@end

#@code reorderList
def reorderList(head: Optional[ListNode]) -> None:
    if not head or not head.next:
        return  # @3
    slow = fast = head
    while fast.next and fast.next.next:
        slow = slow.next
        fast = fast.next.next  # @8
    second, prev = slow.next, None
    slow.next = None
    while second:
        nxt = second.next
        second.next = prev
        prev = second
        second = nxt  # @16
    first, second = head, prev
    while second:
        n1, n2 = first.next, second.next
        first.next = second
        second.next = n1  # @23
        first, second = n1, n2
#@end

#@code LRUCache
class Node:
    def __init__(self, key=0, val=0):
        self.key, self.val = key, val
        self.prev = self.next = None


class LRUCache:
    def __init__(self, capacity: int):
        self.capacity = capacity
        self.map = {}
        self.head, self.tail = Node(), Node()
        self.head.next, self.tail.prev = self.tail, self.head

    def get(self, key: int) -> int:
        node = self.map.get(key)
        if node is None:
            return -1  # @21
        self._remove(node)
        self._add_front(node)  # @24
        return node.val

    def put(self, key: int, value: int) -> None:
        node = self.map.get(key)
        if node:
            node.val = value
            self._remove(node)
            self._add_front(node)  # @33
            return
        if len(self.map) == self.capacity:
            lru = self.tail.prev
            self._remove(lru)  # @38
            del self.map[lru.key]
        node = Node(key, value)
        self.map[key] = node
        self._add_front(node)  # @43

    def _remove(self, n: Node) -> None:
        n.prev.next, n.next.prev = n.next, n.prev

    def _add_front(self, n: Node) -> None:
        n.next, n.prev = self.head.next, self.head
        self.head.next.prev = n
        self.head.next = n
#@end

#@code mergeKLists
def mergeKLists(lists: List[Optional[ListNode]]) -> Optional[ListNode]:
    heap = [(l.val, i, l) for i, l in enumerate(lists) if l]  # @3
    heapq.heapify(heap)
    dummy = tail = ListNode(0)
    while heap:
        _, i, node = heapq.heappop(heap)
        tail.next = node
        tail = node
        if node.next: heapq.heappush(heap, (node.next.val, i, node.next))  # @9
    return dummy.next
#@end
