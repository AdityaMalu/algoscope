import java.util.*;

/** Trie & Strings */
class Tries {
    /** Generic trie view: nodes expose children[26] / isEnd through the TN interface. */
    interface TN { TN child(int i); boolean end(); }

    static S trieView(String name, TN root, Collection<TN> path, TN cur, String mark) {
        S s = new S("tree", name);
        List<Object> nodes = new ArrayList<>();
        Deque<Object[]> st = new ArrayDeque<>();
        st.push(new Object[]{root, "•"});
        while (!st.isEmpty()) {
            Object[] e = st.pop();
            TN n = (TN) e[0];
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", T.id(n)); m.put("value", e[1]);
            List<Object> kids = new ArrayList<>();
            for (int i = 0; i < 26; i++) if (n.child(i) != null) kids.add(T.id(n.child(i)));
            m.put("children", kids);
            nodes.add(m);
            for (int i = 25; i >= 0; i--) if (n.child(i) != null) st.push(new Object[]{n.child(i), String.valueOf((char) ('a' + i))});
        }
        s.put("root", T.id(root));
        s.put("nodes", nodes);
        Deque<TN> q = new ArrayDeque<>(); q.push(root);
        while (!q.isEmpty()) { TN n = q.pop(); if (n.end()) s.m(n, "done"); for (int i = 0; i < 26; i++) if (n.child(i) != null) q.push(n.child(i)); }
        if (path != null) for (TN p : path) s.m(p, "visit");
        if (cur != null) s.p("node", cur).m(cur, mark);
        return s;
    }

    static void run() {
        P.begin("Implement Trie (Prefix Tree)", "Trie");
        P.category("Trie · Design");
        P.summary("A trie stores strings character by character along a tree, so words with a common prefix share nodes. insert walks down creating missing children and marks the last node as a word end; search walks down and checks that mark; startsWith only needs the walk to succeed.");
        P.cx("O(L)", "O(L)", "O(L)", "O(total chars)", "Each operation is linear in the word length L.");
        P.ideas("Each node has up to 26 children, one per letter.",
            "`isEnd` distinguishes a full word from a prefix (\"app\" vs \"apple\").",
            "search and startsWith share the same walk; they differ only in the final check.");
        P.pits("Forgetting `isEnd`: then search(\"app\") would be true after inserting only \"apple\".",
            "Using a HashMap per node is fine for large alphabets but slower for a–z.");
        P.trace("insert(apple), search(apple), search(app), startsWith(app), insert(app), search(app)", () -> {
            Trie t = new Trie(); List<Object> out = new ArrayList<>(); out.add(null);
            t.insert("apple"); out.add(null); out.add(t.search("apple")); out.add(t.search("app")); out.add(t.startsWith("app")); t.insert("app"); out.add(null); out.add(t.search("app"));
            return out;
        });
        P.edge("Search on an empty trie", "search(a)", "[null, false]", () -> { Trie t = new Trie(); List<Object> out = new ArrayList<>(); out.add(null); out.add(t.search("a")); return out; }, "The root has no children.");
        P.edge("Prefix is not a word", "insert(car), search(ca), startsWith(ca)", "[null, null, false, true]", () -> {
            Trie t = new Trie(); List<Object> out = new ArrayList<>(); out.add(null); t.insert("car"); out.add(null); out.add(t.search("ca")); out.add(t.startsWith("ca")); return out;
        }, "The 'a' node exists but isn't marked as a word end.");
        P.edge("Word longer than any stored", "insert(go), search(gone)", "[null, null, false]", () -> {
            Trie t = new Trie(); List<Object> out = new ArrayList<>(); out.add(null); t.insert("go"); out.add(null); out.add(t.search("gone")); return out;
        }, "The walk falls off the trie after \"go\".");
        P.done();

        P.begin("Longest Common Prefix", "longestCommonPrefix");
        P.category("Strings · Vertical scan");
        P.summary("Find the longest prefix shared by every string. Compare column by column: take each character of the first string and check that every other string has the same character at that position. Stop at the first mismatch or when a string runs out.");
        P.cx("O(n)", "O(S)", "O(S)", "O(1)", "S = total characters; stops at the first mismatching column.");
        P.ideas("The answer can't be longer than the shortest string.",
            "Check column i across all strings before moving to column i + 1.",
            "Sorting and comparing only the first and last strings is a neat alternative.");
        P.pits("Not handling an empty string in the list (answer \"\").",
            "Index out of range when a later string is shorter than the first.");
        P.trace("strs = [flower, flow, flight]", () -> longestCommonPrefix(new String[]{"flower", "flow", "flight"}));
        P.edge("No common prefix", "strs = [dog, racecar, car]", "\"\"", () -> longestCommonPrefix(new String[]{"dog", "racecar", "car"}), "First letters already differ.");
        P.edge("One string", "strs = [alone]", "\"alone\"", () -> longestCommonPrefix(new String[]{"alone"}), "It's its own prefix.");
        P.edge("Contains an empty string", "strs = [abc, \"\"]", "\"\"", () -> longestCommonPrefix(new String[]{"abc", ""}), "The empty string ends the scan immediately.");
        P.edge("One is a prefix of another", "strs = [ab, abc]", "\"ab\"", () -> longestCommonPrefix(new String[]{"ab", "abc"}), "The shorter string runs out.");
        P.done();

        P.begin("Palindromic Substrings", "countSubstrings");
        P.category("Strings · Expand around centre");
        P.summary("Count every palindromic substring. Each palindrome has a centre: a character (odd length) or a gap between two characters (even length). Expand outward from all 2n−1 centres, counting each step while the ends match.");
        P.cx("O(n)", "O(n²)", "O(n²)", "O(1)", "Expansion stops at the first mismatch.");
        P.ideas("2n − 1 centres: n single characters and n − 1 gaps.",
            "Expand while `s[l] == s[r]`; each successful expansion is one more palindrome.",
            "No extra space, unlike the DP table.");
        P.pits("Only checking odd-length centres misses \"aa\".",
            "Counting distinct palindromes instead of all occurrences.");
        P.trace("s = \"aaa\"", () -> countSubstrings("aaa"));
        P.edge("All distinct", "s = \"abc\"", "3", () -> countSubstrings("abc"), "Only the single letters.");
        P.edge("Single character", "s = \"z\"", "1", () -> countSubstrings("z"), "One palindrome.");
        P.edge("Even palindrome", "s = \"abba\"", "6", () -> countSubstrings("abba"), "a, b, b, a, bb, abba.");
        P.done();

        P.begin("Design Add and Search Words Data Structure", "WordDictionary");
        P.category("Trie · Wildcard search");
        P.summary("Add words, and search where '.' matches any letter. Store words in a trie. Searching follows the exact child for a letter, and tries every child for a '.', using recursion.");
        P.cx("O(L)", "O(L)", "O(26^L)", "O(total chars)", "Searches with many dots can branch widely.");
        P.ideas("Plain letters follow one path, exactly like a normal trie.",
            "A '.' branches into every existing child.",
            "Return true as soon as any branch finds a word end.");
        P.pits("Treating '.' as a literal character.",
            "Forgetting to check `isEnd` at the last character.");
        P.trace("addWord(bad), addWord(dad), addWord(mad), search(pad), search(bad), search(.ad), search(b..)", () -> {
            WordDictionary w = new WordDictionary(); List<Object> out = new ArrayList<>(); out.add(null);
            w.addWord("bad"); out.add(null); w.addWord("dad"); out.add(null); w.addWord("mad"); out.add(null);
            out.add(w.search("pad")); out.add(w.search("bad")); out.add(w.search(".ad")); out.add(w.search("b.."));
            return out;
        });
        P.edge("All dots", "addWord(ab), search(..), search(...)", "[null, null, true, false]", () -> {
            WordDictionary w = new WordDictionary(); List<Object> out = new ArrayList<>(); out.add(null); w.addWord("ab"); out.add(null); out.add(w.search("..")); out.add(w.search("...")); return out;
        }, "Length still has to match.");
        P.edge("Dot as the last letter", "addWord(at), search(a.)", "[null, null, true]", () -> {
            WordDictionary w = new WordDictionary(); List<Object> out = new ArrayList<>(); out.add(null); w.addWord("at"); out.add(null); out.add(w.search("a.")); return out;
        }, "The '.' matches 't', which is a word end.");
        P.done();

        P.begin("Search Suggestions System", "suggestedProducts");
        P.category("Strings · Sort + binary search");
        P.summary("After each typed character, suggest up to three products with that prefix, in alphabetical order. Sort the products once. For each prefix, binary-search the first product ≥ the prefix; the next three products that still start with it are the suggestions.");
        P.cx("O(n log n)", "O(n log n + L log n)", "O(n log n + L log n)", "O(1)", "Sorting plus one binary search per typed character.");
        P.ideas("In sorted order, all products sharing a prefix are contiguous.",
            "The first one is found by binary search (lower bound).",
            "Take up to three from there while they match the prefix.",
            "A trie storing the top three at each node is the other common solution.");
        P.pits("Forgetting to sort first.",
            "Taking three products without checking they still match the prefix.");
        P.trace("products = [mobile, mouse, moneypot, monitor, mousepad], searchWord = mouse", () -> suggestedProducts(new String[]{"mobile", "mouse", "moneypot", "monitor", "mousepad"}, "mouse"));
        P.edge("No matches", "products = [havana], searchWord = tatiana", "[[], [], [], [], [], [], []]", () -> suggestedProducts(new String[]{"havana"}, "tatiana"), "No product starts with 't'.");
        P.edge("Fewer than three", "products = [havana], searchWord = havana", "[[\"havana\"], [\"havana\"], [\"havana\"], [\"havana\"], [\"havana\"], [\"havana\"]]", () -> suggestedProducts(new String[]{"havana"}, "havana"), "Only one product ever matches.");
        P.done();

        P.begin("Longest Palindromic Substring", "longestPalindrome");
        P.category("Strings · Expand around centre");
        P.summary("Find the longest palindromic substring. Expand from each of the 2n−1 centres while the ends match, and remember the widest expansion seen.");
        P.cx("O(n)", "O(n²)", "O(n²)", "O(1)", "Manacher's algorithm does O(n), but it's rarely expected.");
        P.ideas("Odd palindromes centre on a character; even ones on a gap.",
            "Expansion returns the length; convert it back to a start index.",
            "Keep only the best start and length, not the substring.");
        P.pits("Forgetting even-length centres.",
            "Off-by-one when converting the expanded bounds back to a substring.");
        P.trace("s = \"babad\"", () -> longestPalindrome("babad"));
        P.edge("Even palindrome", "s = \"cbbd\"", "\"bb\"", () -> longestPalindrome("cbbd"), "Only the gap centre finds it.");
        P.edge("Single character", "s = \"a\"", "\"a\"", () -> longestPalindrome("a"), "Itself.");
        P.edge("Whole string", "s = \"racecar\"", "\"racecar\"", () -> longestPalindrome("racecar"), "The centre 'e' expands to both ends.");
        P.done();

        P.begin("Word Search II", "findWords");
        P.category("Trie · Backtracking on a grid");
        P.summary("Find every dictionary word on the grid. Put all words in a trie and DFS from every cell, walking the trie alongside the grid. A path that leaves the trie is abandoned immediately, so all words are searched at once.");
        P.cx("O(m·n)", "O(m·n·4^L)", "O(m·n·4^L)", "O(total chars)", "The trie prunes most paths early.");
        P.ideas("Searching each word separately repeats work; the trie shares prefixes.",
            "Store the full word at its end node to collect it directly.",
            "Clear the stored word once found so it's not reported twice.",
            "Mark cells with '#' during the DFS and restore them after.");
        P.pits("Running Word Search I for every word times out.",
            "Reporting duplicates when a word can be found along two paths.");
        P.trace("board = [[o,a,a,n],[e,t,a,e],[i,h,k,r],[i,f,l,v]], words = [oath, pea, eat, rain]", () -> findWords(F.grid("oaan", "etae", "ihkr", "iflv"), new String[]{"oath", "pea", "eat", "rain"}));
        P.edge("No words found", "board = [[a,b],[c,d]], words = [abcb]", "[]", () -> findWords(F.grid("ab", "cd"), new String[]{"abcb"}), "'b' can't be reused.");
        P.edge("Same word twice in the list", "board = [[a]], words = [a, a]", "[\"a\"]", () -> findWords(F.grid("a"), new String[]{"a", "a"}), "Reported once.");
        P.done();
    }

    //@code Trie
    static class Trie {
        static class Node implements TN {
            Node[] next = new Node[26];
            boolean isEnd;
            public TN child(int i) { return next[i]; } // @hide
            public boolean end() { return isEnd; } // @hide
        }

        private final Node root = new Node();

        void insert(String word) {
            Node cur = root;
            List<TN> path = new ArrayList<>(); // @hide
            for (char c : word.toCharArray()) {
                if (cur.next[c - 'a'] == null) cur.next[c - 'a'] = new Node();
                cur = cur.next[c - 'a'];
                path.add(cur); // @hide
                T.s("insert: '" + c + "'", "Walk to (or create) the child for '" + c + "'.", trieView("trie", root, path, cur, "active"), T.str("word", word).range(0, path.size() - 1, "done"));
            }
            cur.isEnd = true;
            T.s("Mark the word end", "\"" + word + "\" ends here.", trieView("trie", root, path, cur, "found"));
        }

        boolean search(String word) {
            Node node = walk(word);
            T.n("search(\"" + word + "\")", node == null ? "The path breaks off, so it's not stored." : node.isEnd ? "The path exists and ends on a word mark." : "The path exists, but it's only a prefix (no word mark).", trieView("trie", root, null, node, node != null && node.isEnd ? "found" : "error"));
            return node != null && node.isEnd;
        }

        boolean startsWith(String prefix) {
            Node node = walk(prefix);
            T.n("startsWith(\"" + prefix + "\")", node == null ? "The path breaks off." : "The whole prefix path exists.", trieView("trie", root, null, node, node != null ? "found" : "error"));
            return node != null;
        }

        private Node walk(String s) {
            Node cur = root;
            for (char c : s.toCharArray()) {
                cur = cur.next[c - 'a'];
                if (cur == null) return null;
            }
            return cur;
        }
    }
    //@end

    //@code longestCommonPrefix
    static String longestCommonPrefix(String[] strs) {
        String first = strs[0];
        for (int i = 0; i < first.length(); i++) {
            char c = first.charAt(i);
            for (int k = 1; k < strs.length; k++) {
                if (i == strs[k].length() || strs[k].charAt(i) != c) {
                    T.n("Column " + i + " fails", i == strs[k].length() ? "\"" + strs[k] + "\" has no character at position " + i + "." : "\"" + strs[k] + "\" has '" + strs[k].charAt(i) + "' instead of '" + c + "'.", T.arr("strs", strs).m(k, "error"), T.str("prefix", first.substring(0, i)).range(0, i - 1, "found"));
                    return first.substring(0, i);
                }
            }
            T.s("Column " + i + " matches", "Every string has '" + c + "' at position " + i + ".", T.arr("strs", strs), T.str("prefix", first.substring(0, i + 1)).range(0, i, "found"));
        }
        return first;
    }
    //@end

    //@code countSubstrings
    static int countSubstrings(String s) {
        int count = 0;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            int l = center / 2, r = l + center % 2;
            while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                count++;
                T.s("Palindrome \"" + s.substring(l, r + 1) + "\"", "Centre " + center + (center % 2 == 0 ? " (a character)" : " (a gap)") + ": the ends match. Count: " + count + ".", T.str("s", s).p("l", l).p("r", r).range(l, r, "found"), T.v("center", center, "count", count));
                l--;
                r++;
            }
        }
        return count;
    }
    //@end

    //@code WordDictionary
    static class WordDictionary {
        static class Node implements TN {
            Node[] next = new Node[26];
            boolean isEnd;
            public TN child(int i) { return next[i]; } // @hide
            public boolean end() { return isEnd; } // @hide
        }

        private final Node root = new Node();
        private final List<TN> seen = new ArrayList<>(); // @hide

        void addWord(String word) {
            Node cur = root;
            for (char c : word.toCharArray()) {
                if (cur.next[c - 'a'] == null) cur.next[c - 'a'] = new Node();
                cur = cur.next[c - 'a'];
            }
            cur.isEnd = true;
            T.s("addWord(\"" + word + "\")", "Insert it like a normal trie.", trieView("trie", root, null, cur, "found"));
        }

        boolean search(String word) {
            seen.clear(); // @hide
            boolean found = match(word, 0, root);
            T.n("search(\"" + word + "\")", found ? "Some branch reached a word end." : "No branch matched.", trieView("trie", root, seen, null, ""));
            return found;
        }

        private boolean match(String word, int i, Node node) {
            if (i == word.length()) return node.isEnd;
            char c = word.charAt(i);
            if (c != '.') {
                Node next = node.next[c - 'a'];
                if (next != null) seen.add(next); // @hide
                return next != null && match(word, i + 1, next);
            }
            for (Node child : node.next) {
                if (child == null) continue;
                seen.add(child); // @hide
                T.s("'.' tries a branch", "Position " + i + " is a wildcard, so try every child.", trieView("trie", root, seen, child, "active"), T.str("pattern", word).m(i, "active"));
                if (match(word, i + 1, child)) return true;
            }
            return false;
        }
    }
    //@end

    //@code suggestedProducts
    static List<List<String>> suggestedProducts(String[] products, String searchWord) {
        Arrays.sort(products);
        List<List<String>> res = new ArrayList<>();
        String prefix = "";
        for (char c : searchWord.toCharArray()) {
            prefix += c;
            int lo = 0, hi = products.length;
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (products[mid].compareTo(prefix) < 0) lo = mid + 1;
                else hi = mid;
            }
            List<String> top = new ArrayList<>();
            for (int i = lo; i < Math.min(lo + 3, products.length) && products[i].startsWith(prefix); i++) top.add(products[i]);
            res.add(top);
            T.s("Typed \"" + prefix + "\"", "Binary search lands on index " + lo + ". " + (top.isEmpty() ? "Nothing there starts with the prefix." : "Take up to three matches: " + top + "."), T.arr("products (sorted)", products).p("lo", Math.min(lo, products.length - 1)).range(lo, lo + top.size() - 1, "found"), T.arr("suggestions", top));
        }
        return res;
    }
    //@end

    //@code longestPalindrome
    static String longestPalindrome(String s) {
        int bestStart = 0, bestLen = 1;
        for (int center = 0; center < 2 * s.length() - 1; center++) {
            int l = center / 2, r = l + center % 2;
            while (l >= 0 && r < s.length() && s.charAt(l) == s.charAt(r)) {
                l--;
                r++;
            }
            int len = r - l - 1;
            if (len > bestLen) {
                bestLen = len;
                bestStart = l + 1;
            }
            T.s("Centre " + center, (len > 0 ? "Expands to \"" + s.substring(l + 1, r) + "\" (length " + len + ")." : "The two sides of this gap differ.") + " Best: \"" + s.substring(bestStart, bestStart + bestLen) + "\".", T.str("s", s).range(l + 1, r - 1, "visit").range(bestStart, bestStart + bestLen - 1, "found"), T.v("center", center, "len", len, "bestLen", bestLen));
        }
        return s.substring(bestStart, bestStart + bestLen);
    }
    //@end

    static char[][] B;
    static S wsBoard(char[][] b, int r, int c) {
        S s = T.mat("board", b);
        for (int i = 0; i < b.length; i++) for (int j = 0; j < b[0].length; j++) if (b[i][j] == '#') s.m(i, j, "visit");
        if (r >= 0) s.m(r, c, "active");
        return s;
    }

    //@code findWords
    static class Node implements TN {
        Node[] next = new Node[26];
        String word;
        public TN child(int i) { return next[i]; } // @hide
        public boolean end() { return word != null; } // @hide
    }

    static Node ROOT; // @hide

    static List<String> findWords(char[][] board, String[] words) {
        Node root = new Node();
        for (String w : words) {
            Node cur = root;
            for (char c : w.toCharArray()) {
                if (cur.next[c - 'a'] == null) cur.next[c - 'a'] = new Node();
                cur = cur.next[c - 'a'];
            }
            cur.word = w;
        }
        ROOT = root; // @hide
        T.s("Build a trie of the words", "All words share one trie, so one DFS checks them all.", trieView("trie", root, null, null, ""), T.mat("board", board));
        List<String> res = new ArrayList<>();
        for (int r = 0; r < board.length; r++)
            for (int c = 0; c < board[0].length; c++)
                dfs(board, r, c, root, res);
        return res;
    }

    static void dfs(char[][] b, int r, int c, Node node, List<String> res) {
        if (r < 0 || c < 0 || r >= b.length || c >= b[0].length || b[r][c] == '#') return;
        Node next = node.next[b[r][c] - 'a'];
        if (next == null) return;
        if (next.word != null) {
            res.add(next.word);
            T.s("Found \"" + next.word + "\"", "The trie node here holds a word. Clear it so it isn't reported twice.", wsBoard(b, r, c), trieView("trie", ROOT, null, next, "found"), T.arr("res", res));
            next.word = null;
        }
        char ch = b[r][c];
        b[r][c] = '#';
        T.call("dfs(" + r + ", " + c + ")");
        T.s("Step onto '" + ch + "' at (" + r + ", " + c + ")", "The trie has a child for '" + ch + "', so the path is still a prefix of some word.", wsBoard(b, r, c), trieView("trie", ROOT, null, next, "active"));
        dfs(b, r + 1, c, next, res);
        dfs(b, r - 1, c, next, res);
        dfs(b, r, c + 1, next, res);
        dfs(b, r, c - 1, next, res);
        b[r][c] = ch;
        T.ret();
    }
    //@end
}
