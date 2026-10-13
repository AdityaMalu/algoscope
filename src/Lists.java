import java.util.*;

/** Linked List */
class Lists {
    static ListNode cyc(int pos, int... v) {
        ListNode h = F.L(v), t = h, at = null;
        for (int i = 0; t != null; i++) { if (i == pos) at = t; if (t.next == null) break; t = t.next; }
        if (t != null) t.next = at;
        return h;
    }

    static void run() {
        P.begin("Reverse Linked List", "reverseList");
        P.category("Linked List · Pointer reversal");
        P.summary("Reverse a singly linked list in place. Walk the list with three pointers: save `next`, point `curr.next` back at `prev`, then step both forward. When `curr` falls off the end, `prev` is the new head.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Each node's pointer is flipped once.");
        P.ideas("Everything before `curr` is already reversed and starts at `prev`.",
            "Save `curr.next` before overwriting it, or the rest of the list is lost.",
            "The loop ends with `curr == null`; return `prev`.",
            "The recursive version is elegant but uses O(n) stack.");
        P.pits("Overwriting `curr.next` before saving it.",
            "Returning `head` (now the tail) instead of `prev`.",
            "Forgetting that the old head's `next` must become null.");
        P.trace("head = [1, 2, 3, 4, 5]", () -> reverseList(F.L(1, 2, 3, 4, 5)));
        P.edge("Empty list", "head = []", "[]", () -> reverseList(null), "The loop never runs; return null.");
        P.edge("Single node", "head = [7]", "[7]", () -> reverseList(F.L(7)), "One flip to null.");
        P.edge("Two nodes", "head = [1, 2]", "[2, 1]", () -> reverseList(F.L(1, 2)), "The smallest case where links actually change direction.");
        P.done();

        P.begin("Merge Two Sorted Lists", "mergeTwoLists");
        P.category("Linked List · Dummy head merge");
        P.summary("Merge two sorted lists into one sorted list by splicing nodes. A dummy head avoids special cases: repeatedly attach the smaller front node to the tail, then attach whatever remains.");
        P.cx("O(n + m)", "O(n + m)", "O(n + m)", "O(1)", "Nodes are relinked, not copied.");
        P.ideas("A dummy node means the result's head is always `dummy.next`.",
            "Compare the two fronts, attach the smaller one, advance that list.",
            "When one list runs out, attach the other in one step; it's already sorted.");
        P.pits("Losing the head by not using a dummy node.",
            "Forgetting to attach the leftover list.",
            "Using `<` vs `<=` doesn't matter for correctness, but `<=` keeps it stable.");
        P.trace("list1 = [1, 2, 4], list2 = [1, 3, 4]", () -> mergeTwoLists(F.L(1, 2, 4), F.L(1, 3, 4)));
        P.edge("Both empty", "list1 = [], list2 = []", "[]", () -> mergeTwoLists(null, null), "dummy.next stays null.");
        P.edge("One empty", "list1 = [], list2 = [0]", "[0]", () -> mergeTwoLists(null, F.L(0)), "The other list is attached whole.");
        P.edge("No overlap", "list1 = [1, 2], list2 = [5, 6]", "[1, 2, 5, 6]", () -> mergeTwoLists(F.L(1, 2), F.L(5, 6)), "list1 runs out first; list2 is attached in one step.");
        P.edge("Negatives and duplicates", "list1 = [-3, 0, 0], list2 = [-3, 1]", "[-3, -3, 0, 0, 1]", () -> mergeTwoLists(F.L(-3, 0, 0), F.L(-3, 1)), "Ties take from list1 first.");
        P.done();

        P.begin("Linked List Cycle", "hasCycle");
        P.category("Linked List · Fast and slow pointers");
        P.summary("Detect whether a list loops back on itself, in O(1) space. Move `slow` one step and `fast` two steps. If there's a cycle, fast laps slow and they meet; otherwise fast reaches null.");
        P.cx("O(1)", "O(n)", "O(n)", "O(1)", "Once both are in the cycle, the gap closes by one each step.");
        P.ideas("In a cycle, the faster runner must eventually catch the slower one.",
            "Each step, the distance between them shrinks by exactly one, so fast can't jump over slow.",
            "Check `fast != null && fast.next != null` before moving fast.");
        P.pits("A HashSet of visited nodes works but uses O(n) space.",
            "Checking `slow == fast` before the first move (they start equal).",
            "Forgetting `fast.next != null` and crashing on even-length lists.");
        P.trace("head = [3, 2, 0, -4], tail links to index 1", () -> hasCycle(cyc(1, 3, 2, 0, -4)));
        P.edge("No cycle", "head = [1, 2, 3]", "false", () -> hasCycle(F.L(1, 2, 3)), "fast reaches null.");
        P.edge("Empty list", "head = []", "false", () -> hasCycle(null), "No nodes at all.");
        P.edge("Single node pointing to itself", "head = [1], tail links to index 0", "true", () -> hasCycle(cyc(0, 1)), "fast and slow meet on the only node.");
        P.edge("Single node, no cycle", "head = [1]", "false", () -> hasCycle(F.L(1)), "`fast.next` is null immediately.");
        P.done();

        P.begin("Middle of the Linked List", "middleNode");
        P.category("Linked List · Fast and slow pointers");
        P.summary("Return the middle node (the second middle for even lengths). Move `slow` one step and `fast` two steps; when fast reaches the end, slow is in the middle.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "fast walks the list once.");
        P.ideas("fast covers twice the distance, so slow is halfway when fast finishes.",
            "Loop while `fast != null && fast.next != null`.",
            "For even lengths this lands on the second middle, which the problem wants.");
        P.pits("Looping on `fast.next.next != null` returns the first middle instead.",
            "Counting the length first works but takes two passes.");
        P.trace("head = [1, 2, 3, 4, 5, 6]", () -> middleNode(F.L(1, 2, 3, 4, 5, 6)));
        P.edge("Odd length", "head = [1, 2, 3, 4, 5]", "[3, 4, 5]", () -> middleNode(F.L(1, 2, 3, 4, 5)), "The exact middle.");
        P.edge("Single node", "head = [1]", "[1]", () -> middleNode(F.L(1)), "It is its own middle.");
        P.edge("Two nodes", "head = [1, 2]", "[2]", () -> middleNode(F.L(1, 2)), "The second middle.");
        P.done();

        P.begin("Remove Nth Node From End of List", "removeNthFromEnd");
        P.category("Linked List · Gap pointers");
        P.summary("Remove the n-th node from the end in one pass. Move `fast` n steps ahead of `slow` (both starting at a dummy node), then move both until fast is at the last node. slow is now just before the node to delete.");
        P.cx("O(L)", "O(L)", "O(L)", "O(1)", "One pass over the list of length L.");
        P.ideas("Keep a gap of exactly n nodes between `fast` and `slow`.",
            "When fast is on the last node, slow is right before the target.",
            "A dummy head makes removing the first node work the same way.");
        P.pits("Without a dummy node, removing the head needs a special case.",
            "Off-by-one in the gap: stopping slow on the target instead of before it.");
        P.trace("head = [1, 2, 3, 4, 5], n = 2", () -> removeNthFromEnd(F.L(1, 2, 3, 4, 5), 2));
        P.edge("Remove the head", "head = [1, 2], n = 2", "[2]", () -> removeNthFromEnd(F.L(1, 2), 2), "slow stays on the dummy, so dummy.next is skipped.");
        P.edge("Only node", "head = [1], n = 1", "[]", () -> removeNthFromEnd(F.L(1), 1), "The result is empty.");
        P.edge("Remove the tail", "head = [1, 2, 3], n = 1", "[1, 2]", () -> removeNthFromEnd(F.L(1, 2, 3), 1), "slow ends on the second-to-last node.");
        P.done();

        P.begin("Reorder List", "reorderList");
        P.category("Linked List · Split, reverse, weave");
        P.summary("Reorder L0 → L1 → … → Ln into L0 → Ln → L1 → Ln−1 → …, in place. Find the middle, reverse the second half, then weave the two halves together one node at a time.");
        P.cx("O(n)", "O(n)", "O(n)", "O(1)", "Three linear passes, no extra list.");
        P.ideas("The pattern alternates from the front and the back, so reverse the back half to walk it forward.",
            "Fast/slow pointers find the middle; cut the list there.",
            "Weave: take one from the first half, one from the reversed second half.");
        P.pits("Forgetting to cut the first half (`slow.next = null`) creates a cycle.",
            "Copying nodes into an array works but uses O(n) space.");
        P.trace("head = [1, 2, 3, 4, 5]", () -> { ListNode h = F.L(1, 2, 3, 4, 5); reorderList(h); return h; });
        P.edge("Even length", "head = [1, 2, 3, 4]", "[1, 4, 2, 3]", () -> { ListNode h = F.L(1, 2, 3, 4); reorderList(h); return h; }, "Halves [1, 2] and [4, 3] weave together.");
        P.edge("Single node", "head = [1]", "[1]", () -> { ListNode h = F.L(1); reorderList(h); return h; }, "Nothing to move.");
        P.edge("Two nodes", "head = [1, 2]", "[1, 2]", () -> { ListNode h = F.L(1, 2); reorderList(h); return h; }, "Already in order.");
        P.done();

        P.begin("LRU Cache", "LRUCache");
        P.category("Linked List · Hash map + doubly linked list");
        P.summary("A cache with O(1) get and put that evicts the least recently used key when full. A hash map finds nodes by key; a doubly linked list keeps them in usage order, most recent at the front. Every access moves the node to the front; eviction removes the node before the tail.");
        P.cx("O(1)", "O(1)", "O(1)", "O(capacity)", "Map lookups and list relinking are both O(1).");
        P.ideas("Map: key → node, for O(1) lookup.",
            "Doubly linked list: O(1) removal from the middle and insertion at the front.",
            "Dummy head and tail nodes remove all null checks.",
            "On put, if the key is new and the cache is full, evict `tail.prev`.");
        P.pits("Forgetting to update recency on get.",
            "Evicting before checking whether the key already exists.",
            "Not removing the evicted key from the map.",
            "LinkedHashMap(cap, 0.75f, true) with removeEldestEntry is a valid shortcut; know both.");
        P.trace("LRUCache(2): put(1,1), put(2,2), get(1), put(3,3), get(2), put(4,4), get(1), get(3), get(4)", () -> {
            LRUCache c = new LRUCache(2); List<Object> out = new ArrayList<>(); out.add(null);
            c.put(1, 1); out.add(null); c.put(2, 2); out.add(null); out.add(c.get(1)); c.put(3, 3); out.add(null);
            out.add(c.get(2)); c.put(4, 4); out.add(null); out.add(c.get(1)); out.add(c.get(3)); out.add(c.get(4));
            return out;
        });
        P.edge("Update an existing key", "LRUCache(2): put(1,1), put(2,2), put(1,10), put(3,3), get(1), get(2)", "[null, null, null, null, null, 10, -1]", () -> {
            LRUCache c = new LRUCache(2); List<Object> out = new ArrayList<>(); out.add(null);
            c.put(1, 1); out.add(null); c.put(2, 2); out.add(null); c.put(1, 10); out.add(null); c.put(3, 3); out.add(null); out.add(c.get(1)); out.add(c.get(2)); return out;
        }, "Updating 1 makes it most recent, so 2 is evicted.");
        P.edge("Capacity 1", "LRUCache(1): put(1,1), put(2,2), get(1), get(2)", "[null, null, null, -1, 2]", () -> {
            LRUCache c = new LRUCache(1); List<Object> out = new ArrayList<>(); out.add(null);
            c.put(1, 1); out.add(null); c.put(2, 2); out.add(null); out.add(c.get(1)); out.add(c.get(2)); return out;
        }, "Each new key evicts the previous one.");
        P.edge("Get a missing key", "LRUCache(2): get(5)", "[null, -1]", () -> {
            LRUCache c = new LRUCache(2); List<Object> out = new ArrayList<>(); out.add(null); out.add(c.get(5)); return out;
        }, "Missing keys return -1 without changing anything.");
        P.done();

        P.begin("Merge k Sorted Lists", "mergeKLists");
        P.category("Linked List · Min-heap");
        P.summary("Merge k sorted lists into one. Put the head of every list in a min-heap keyed by value. Repeatedly pop the smallest node, attach it to the result, and push its successor.");
        P.cx("O(N log k)", "O(N log k)", "O(N log k)", "O(k)", "N total nodes; the heap never holds more than k nodes.");
        P.ideas("The next node of the answer is always one of the k current heads.",
            "A min-heap returns the smallest head in O(log k).",
            "After taking a node, its `next` becomes that list's new head.");
        P.pits("Pushing null heads into the heap throws an exception.",
            "Merging lists one by one is O(N·k).",
            "Divide and conquer (pairwise merging) is the other O(N log k) solution.");
        P.trace("lists = [[1, 4, 5], [1, 3, 4], [2, 6]]", () -> mergeKLists(new ListNode[]{F.L(1, 4, 5), F.L(1, 3, 4), F.L(2, 6)}));
        P.edge("No lists", "lists = []", "[]", () -> mergeKLists(new ListNode[]{}), "The heap starts empty.");
        P.edge("Only empty lists", "lists = [[], []]", "[]", () -> mergeKLists(new ListNode[]{null, null}), "Null heads are skipped.");
        P.edge("One list", "lists = [[1, 2]]", "[1, 2]", () -> mergeKLists(new ListNode[]{F.L(1, 2)}), "Nodes come out in their original order.");
        P.done();
    }

    //@code reverseList
    static ListNode reverseList(ListNode head) {
        ListNode prev = null, curr = head;
        T.s("Start", "`prev` is null (the reversed part is empty); `curr` is the head.", T.list("list", prev, curr).p("prev", prev).p("curr", curr));
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            T.s("Flip curr.next", "Node " + curr.val + " now points back to " + (prev == null ? "null" : prev.val) + ". `next` saved the rest of the list.",
                T.list("list", curr, next).p("prev", prev).p("curr", curr).p("next", next).m(curr, "swap"));
            prev = curr;
            curr = next;
            T.s("Step forward", "`prev` moves to " + prev.val + ", `curr` to " + (curr == null ? "null" : curr.val) + ".",
                T.list("list", prev, curr).p("prev", prev).p("curr", curr).m(prev, "done"));
        }
        T.n("Done", "`curr` is null, so `prev` is the new head.", T.list("list", prev).p("head", prev).m(prev, "found"));
        return prev;
    }
    //@end

    //@code mergeTwoLists
    static ListNode mergeTwoLists(ListNode a, ListNode b) {
        ListNode dummy = new ListNode(0), tail = dummy;
        while (a != null && b != null) {
            if (a.val <= b.val) {
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
            T.s("Attach the smaller front", "Attached " + tail.val + ". Fronts are now " + (a == null ? "null" : a.val) + " and " + (b == null ? "null" : b.val) + ".",
                T.list("merged", dummy.next).p("tail", tail).m(tail, "swap"), T.list("list1", a).p("a", a), T.list("list2", b).p("b", b));
        }
        tail.next = (a != null) ? a : b;
        T.s("Attach the rest", "One list is empty, so the other is attached as is.", T.list("merged", dummy.next).m(tail.next, "visit"));
        return dummy.next;
    }
    //@end

    //@code hasCycle
    static boolean hasCycle(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            T.s("Move the runners", "slow moves 1 step to " + slow.val + ", fast moves 2 to " + (fast == null ? "null" : fast.val) + ".",
                T.list("list", head).p("slow", slow).p("fast", fast).m(slow, "visit").m(fast, "active"));
            if (slow == fast) {
                T.n("They meet", "fast caught up with slow at node " + slow.val + ", so there's a cycle.", T.list("list", head).p("slow", slow).p("fast", fast).m(slow, "found"));
                return true;
            }
        }
        T.n("fast reached the end", "A list with an end has no cycle.", T.list("list", head).p("slow", slow));
        return false;
    }
    //@end

    //@code middleNode
    static ListNode middleNode(ListNode head) {
        ListNode slow = head, fast = head;
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            T.s("Move the runners", "slow: " + slow.val + ", fast: " + (fast == null ? "past the end" : String.valueOf(fast.val)) + ".", T.list("list", head).p("slow", slow).p("fast", fast).m(slow, "visit").m(fast, "active"));
        }
        T.n("Middle", "fast can't take two more steps, so slow (" + slow.val + ") is the middle.", T.list("list", head).p("slow", slow).m(slow, "found"));
        return slow;
    }
    //@end

    //@code removeNthFromEnd
    static ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head), slow = dummy, fast = dummy;
        for (int i = 0; i < n; i++) {
            fast = fast.next;
            T.s("Open the gap", "fast moves ahead (" + (i + 1) + " of " + n + ").", T.list("list", dummy).p("slow", slow).p("fast", fast).m(dummy, "dim").m(fast, "active"));
        }
        while (fast.next != null) {
            slow = slow.next;
            fast = fast.next;
            T.s("Move both", "The gap stays " + n + ".", T.list("list", dummy).p("slow", slow).p("fast", fast).m(dummy, "dim").m(slow, "visit").m(fast, "active"));
        }
        T.n("Unlink the target", "fast is on the last node, so `slow.next` (" + slow.next.val + ") is the " + n + "th from the end.",
            T.list("list", dummy).p("slow", slow).p("fast", fast).m(dummy, "dim").m(slow.next, "error"));
        slow.next = slow.next.next;
        T.s("Removed", "`slow.next` now skips over it.", T.list("list", dummy).p("slow", slow).m(dummy, "dim"));
        return dummy.next;
    }
    //@end

    //@code reorderList
    static void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            T.n("Nothing to reorder", "A list with fewer than two nodes stays as it is.", T.list("list", head));
            return;
        }
        ListNode slow = head, fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        T.s("Find the middle", "slow stops at " + slow.val + ", the end of the first half.", T.list("list", head).p("slow", slow).p("fast", fast).m(slow, "active"));
        ListNode second = slow.next, prev = null;
        slow.next = null;
        while (second != null) {
            ListNode next = second.next;
            second.next = prev;
            prev = second;
            second = next;
        }
        T.s("Reverse the second half", "Cut after the middle and reverse the back half.", T.list("first", head), T.list("second (reversed)", prev).p("prev", prev));
        ListNode first = head;
        second = prev;
        while (second != null) {
            ListNode n1 = first.next, n2 = second.next;
            first.next = second;
            second.next = n1;
            T.s("Weave one pair", first.val + " → " + second.val + " → " + (n1 == null ? "null" : n1.val) + ".", T.list("list", head).m(first, "done").m(second, "swap"), T.list("second (rest)", n2));
            first = n1;
            second = n2;
        }
    }
    //@end

    static S lru(Map<Integer, LRUCache.Node> map, LRUCache.Node head, LRUCache.Node tail, Integer hot, String mark) {
        S s = T.graph("recency list (most recent first)", true);
        int i = 0, n = map.size() + 2;
        List<LRUCache.Node> order = new ArrayList<>();
        for (LRUCache.Node c = head; c != null; c = c.next) order.add(c);
        for (LRUCache.Node c : order) {
            String label = c == head ? "head" : c == tail ? "tail" : c.key + ":" + c.val;
            s.nodeAt(T.id(c), label, n == 1 ? 50 : 100.0 * i / (n - 1), 50);
            i++;
        }
        for (LRUCache.Node c : order) if (c.next != null) s.edge(T.id(c), T.id(c.next), null);
        for (LRUCache.Node c : order) if (c != head && c != tail && hot != null && c.key == hot) s.m(T.id(c), mark);
        s.m(T.id(head), "dim").m(T.id(tail), "dim");
        return s;
    }

    //@code LRUCache
    static class LRUCache {
        static class Node {
            int key, val;
            Node prev, next;
            Node(int k, int v) { key = k; val = v; }
        }

        private final int capacity;
        private final Map<Integer, Node> map = new HashMap<>();
        private final Node head = new Node(0, 0), tail = new Node(0, 0);

        LRUCache(int capacity) {
            this.capacity = capacity;
            head.next = tail;
            tail.prev = head;
        }

        int get(int key) {
            Node node = map.get(key);
            if (node == null) {
                T.n("get(" + key + "): miss", key + " is not in the cache.", lru(map, head, tail, null, ""), T.map("map", keys(map)));
                return -1;
            }
            remove(node);
            addFront(node);
            T.s("get(" + key + "): hit", "Return " + node.val + " and move it to the front (most recently used).", lru(map, head, tail, key, "found"), T.map("map", keys(map)).m(key, "found"));
            return node.val;
        }

        void put(int key, int value) {
            Node node = map.get(key);
            if (node != null) {
                node.val = value;
                remove(node);
                addFront(node);
                T.s("put(" + key + ", " + value + "): update", "The key exists. Update its value and move it to the front.", lru(map, head, tail, key, "swap"), T.map("map", keys(map)).m(key, "swap"));
                return;
            }
            if (map.size() == capacity) {
                Node lru = tail.prev;
                T.n("Cache full: evict", "Key " + lru.key + " is least recently used (just before tail). Remove it.", lru(map, head, tail, lru.key, "error"), T.map("map", keys(map)).m(lru.key, "error"));
                remove(lru);
                map.remove(lru.key);
            }
            node = new Node(key, value);
            map.put(key, node);
            addFront(node);
            T.s("put(" + key + ", " + value + "): insert", "Add the new node at the front.", lru(map, head, tail, key, "swap"), T.map("map", keys(map)).m(key, "swap"));
        }

        private void remove(Node n) {
            n.prev.next = n.next;
            n.next.prev = n.prev;
        }

        private void addFront(Node n) {
            n.next = head.next;
            n.prev = head;
            head.next.prev = n;
            head.next = n;
        }
    }
    //@end
    static Map<Integer, String> keys(Map<Integer, LRUCache.Node> m) { Map<Integer, String> r = new LinkedHashMap<>(); for (Map.Entry<Integer, LRUCache.Node> e : new TreeMap<>(m).entrySet()) r.put(e.getKey(), "node(" + e.getKey() + ":" + e.getValue().val + ")"); return r; }

    static S heapView(PriorityQueue<ListNode> pq) {
        S s = new S("heap", "heap");
        List<Object> v = new ArrayList<>();
        for (Object o : pq.toArray()) v.add(((ListNode) o).val);
        s.put("values", v);
        return s;
    }

    //@code mergeKLists
    static ListNode mergeKLists(ListNode[] lists) {
        PriorityQueue<ListNode> pq = new PriorityQueue<>((x, y) -> Integer.compare(x.val, y.val));
        for (ListNode l : lists) if (l != null) pq.offer(l);
        T.s("Heap of heads", "Every non-empty list contributes its first node.", heapView(pq));
        ListNode dummy = new ListNode(0), tail = dummy;
        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            tail.next = node;
            tail = node;
            if (node.next != null) pq.offer(node.next);
            T.s("Take the smallest", "Pop " + node.val + " and attach it. " + (node.next != null ? "Push its successor " + node.next.val + "." : "Its list is now empty."),
                heapView(pq), T.list("merged", dummy.next).m(tail, "swap"));
        }
        return dummy.next;
    }
    //@end
}
