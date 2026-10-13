//@code reverseList
ListNode* reverseList(ListNode* head) {
    ListNode *prev = nullptr, *curr = head;  // @2
    while (curr) {
        ListNode* next = curr->next;
        curr->next = prev;  // @5
        prev = curr;
        curr = next;  // @7
    }
    return prev;  // @9
}
//@end

//@code mergeTwoLists
ListNode* mergeTwoLists(ListNode* a, ListNode* b) {
    ListNode dummy(0);
    ListNode* tail = &dummy;
    while (a && b) {
        if (a->val <= b->val) {
            tail->next = a;
            a = a->next;
        } else {
            tail->next = b;
            b = b->next;
        }
        tail = tail->next;  // @11
    }
    tail->next = a ? a : b;  // @13
    return dummy.next;
}
//@end

//@code hasCycle
bool hasCycle(ListNode* head) {
    ListNode *slow = head, *fast = head;
    while (fast && fast->next) {
        slow = slow->next;
        fast = fast->next->next;  // @5
        if (slow == fast) {
            return true;  // @7
        }
    }
    return false;  // @10
}
//@end

//@code middleNode
ListNode* middleNode(ListNode* head) {
    ListNode *slow = head, *fast = head;
    while (fast && fast->next) {
        slow = slow->next;
        fast = fast->next->next;  // @5
    }
    return slow;  // @7
}
//@end

//@code removeNthFromEnd
ListNode* removeNthFromEnd(ListNode* head, int n) {
    ListNode dummy(0, head);
    ListNode *slow = &dummy, *fast = &dummy;
    for (int i = 0; i < n; i++) {
        fast = fast->next;  // @4
    }
    while (fast->next) {
        slow = slow->next;
        fast = fast->next;  // @8
    }
    slow->next = slow->next->next;  // @10
    return dummy.next;
}
//@end

//@code reorderList
void reorderList(ListNode* head) {
    if (!head || !head->next) {
        return;  // @3
    }
    ListNode *slow = head, *fast = head;
    while (fast->next && fast->next->next) {
        slow = slow->next;
        fast = fast->next->next;  // @8
    }
    ListNode *second = slow->next, *prev = nullptr;
    slow->next = nullptr;
    while (second) {
        ListNode* next = second->next;
        second->next = prev;
        prev = second;
        second = next;  // @16
    }
    ListNode* first = head;
    second = prev;
    while (second) {
        ListNode *n1 = first->next, *n2 = second->next;
        first->next = second;
        second->next = n1;  // @23
        first = n1;
        second = n2;
    }
}
//@end

//@code LRUCache
class LRUCache {
    struct Node {
        int key, val;
        Node *prev = nullptr, *next = nullptr;
        Node(int k, int v) : key(k), val(v) {}
    };

    int capacity;
    unordered_map<int, Node*> map;
    Node *head = new Node(0, 0), *tail = new Node(0, 0);

    void remove(Node* n) {
        n->prev->next = n->next;
        n->next->prev = n->prev;
    }

    void addFront(Node* n) {
        n->next = head->next;
        n->prev = head;
        head->next->prev = n;
        head->next = n;
    }

public:
    LRUCache(int capacity) : capacity(capacity) {
        head->next = tail;
        tail->prev = head;
    }

    int get(int key) {
        auto it = map.find(key);
        if (it == map.end()) {
            return -1;  // @21
        }
        remove(it->second);
        addFront(it->second);  // @24
        return it->second->val;
    }

    void put(int key, int value) {
        auto it = map.find(key);
        if (it != map.end()) {
            it->second->val = value;
            remove(it->second);
            addFront(it->second);  // @33
            return;
        }
        if ((int)map.size() == capacity) {
            Node* lru = tail->prev;
            remove(lru);  // @38
            map.erase(lru->key);
            delete lru;
        }
        Node* node = new Node(key, value);
        map[key] = node;
        addFront(node);  // @43
    }
};
//@end

//@code mergeKLists
ListNode* mergeKLists(vector<ListNode*>& lists) {
    auto cmp = [](ListNode* a, ListNode* b) { return a->val > b->val; };
    priority_queue<ListNode*, vector<ListNode*>, decltype(cmp)> pq(cmp);
    for (ListNode* l : lists) if (l) pq.push(l);  // @3
    ListNode dummy(0);
    ListNode* tail = &dummy;
    while (!pq.empty()) {
        ListNode* node = pq.top(); pq.pop();
        tail->next = node;
        tail = node;
        if (node->next) pq.push(node->next);  // @9
    }
    return dummy.next;
}
//@end
