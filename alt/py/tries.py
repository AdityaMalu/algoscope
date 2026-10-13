from common import *

#@code Trie
class TrieNode:
    def __init__(self):
        self.next = {}
        self.is_end = False


class Trie:
    def __init__(self):
        self.root = TrieNode()

    def insert(self, word: str) -> None:
        cur = self.root
        for c in word:
            cur = cur.next.setdefault(c, TrieNode())  # @13
        cur.is_end = True  # @15

    def search(self, word: str) -> bool:
        node = self._walk(word)
        return node is not None and node.is_end  # @20

    def startsWith(self, prefix: str) -> bool:
        return self._walk(prefix) is not None  # @25

    def _walk(self, s: str) -> Optional[TrieNode]:
        cur = self.root
        for c in s:
            cur = cur.next.get(c)
            if cur is None: return None
        return cur
#@end

#@code longestCommonPrefix
def longestCommonPrefix(strs: List[str]) -> str:
    first = strs[0]
    for i, c in enumerate(first):
        for s in strs[1:]:
            if i == len(s) or s[i] != c:
                return first[:i]  # @7
    return first
#@end

#@code countSubstrings
def countSubstrings(s: str) -> int:
    count = 0
    for center in range(2 * len(s) - 1):
        l, r = center // 2, center // 2 + center % 2
        while l >= 0 and r < len(s) and s[l] == s[r]:
            count += 1  # @6
            l -= 1
            r += 1
    return count
#@end

#@code WordDictionary
class WordNode:
    def __init__(self):
        self.next = {}
        self.is_end = False


class WordDictionary:
    def __init__(self):
        self.root = WordNode()

    def addWord(self, word: str) -> None:
        cur = self.root
        for c in word:
            cur = cur.next.setdefault(c, WordNode())
        cur.is_end = True  # @15

    def search(self, word: str) -> bool:
        return self._match(word, 0, self.root)  # @20

    def _match(self, word: str, i: int, node: WordNode) -> bool:
        if i == len(word): return node.is_end
        c = word[i]
        if c != '.':
            nxt = node.next.get(c)
            return nxt is not None and self._match(word, i + 1, nxt)
        for child in node.next.values():  # @31
            if self._match(word, i + 1, child): return True
        return False
#@end

#@code suggestedProducts
def suggestedProducts(products: List[str], searchWord: str) -> List[List[str]]:
    products.sort()
    res, prefix = [], ""
    for c in searchWord:
        prefix += c
        lo = bisect.bisect_left(products, prefix)
        res.append([p for p in products[lo:lo + 3] if p.startswith(prefix)])  # @15
    return res
#@end

#@code longestPalindrome
def longestPalindrome(s: str) -> str:
    best_start, best_len = 0, 1
    for center in range(2 * len(s) - 1):
        l, r = center // 2, center // 2 + center % 2
        while l >= 0 and r < len(s) and s[l] == s[r]:
            l -= 1
            r += 1
        if r - l - 1 > best_len:
            best_len = r - l - 1
            best_start = l + 1  # @12
    return s[best_start:best_start + best_len]
#@end

#@code findWords
class Node:
    def __init__(self):
        self.next = {}
        self.word = None


def findWords(board: List[List[str]], words: List[str]) -> List[str]:
    root = Node()
    for w in words:
        cur = root
        for c in w:
            cur = cur.next.setdefault(c, Node())
        cur.word = w  # @14
    res = []
    m, n = len(board), len(board[0])

    def dfs(r: int, c: int, node: Node) -> None:
        if r < 0 or c < 0 or r >= m or c >= n or board[r][c] == '#': return
        nxt = node.next.get(board[r][c])
        if nxt is None: return
        if nxt.word is not None:
            res.append(nxt.word)  # @28
            nxt.word = None
        ch = board[r][c]
        board[r][c] = '#'  # @32
        dfs(r + 1, c, nxt)
        dfs(r - 1, c, nxt)
        dfs(r, c + 1, nxt)
        dfs(r, c - 1, nxt)
        board[r][c] = ch

    for r in range(m):
        for c in range(n):
            dfs(r, c, root)
    return res
#@end
