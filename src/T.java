import java.util.*;

/** Tracer: records snapshots of program state while a solution runs. */
final class T {
    static final int CAP = 3000;
    static List<Object> steps = new ArrayList<>();
    static boolean on = false, capped = false;
    static final List<String> calls = new ArrayList<>();
    static final IdentityHashMap<Object, String> ids = new IdentityHashMap<>();

    static void begin() { steps = new ArrayList<>(); on = true; capped = false; calls.clear(); ids.clear(); }
    static List<Object> end() { on = false; calls.clear(); return steps; }
    static boolean full() { return steps.size() >= CAP; }

    static String id(Object o) {
        if (o == null) return null;
        String s = ids.get(o);
        if (s == null) { s = "n" + (ids.size() + 1); ids.put(o, s); }
        return s;
    }

    /** Step pointing at the previous code line. */
    static void s(String title, String explain, Object... parts) { add(0, title, explain, parts); }
    /** Step pointing at the next code line. */
    static void n(String title, String explain, Object... parts) { add(1, title, explain, parts); }
    static void call(String frame) { if (on) calls.add(frame); }
    static void ret() { if (on && !calls.isEmpty()) calls.remove(calls.size() - 1); }

    private static void add(int dir, String title, String explain, Object[] parts) {
        if (!on) return;
        if (steps.size() >= CAP) { capped = true; return; }
        StackWalker.StackFrame f = StackWalker.getInstance().walk(st -> st.skip(2).findFirst().orElse(null));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("file", f == null ? "" : f.getFileName());
        m.put("raw", f == null ? 0 : f.getLineNumber());
        m.put("dir", dir);
        m.put("title", title);
        m.put("explain", explain);
        Map<String, Object> vars = new LinkedHashMap<>();
        List<Object> structs = new ArrayList<>();
        for (Object p : parts) {
            if (p instanceof V v) vars.putAll(v);
            else if (p instanceof S s) structs.add(s);
        }
        if (!calls.isEmpty()) {
            S cs = new S("stack", "call stack");
            cs.put("values", new ArrayList<>(calls));
            structs.add(cs);
        }
        m.put("vars", vars);
        m.put("structures", structs);
        steps.add(m);
    }

    // ---------- values ----------
    static V v(Object... kv) {
        V v = new V();
        for (int i = 0; i + 1 < kv.length; i += 2) v.put(String.valueOf(kv[i]), conv(kv[i + 1]));
        return v;
    }

    static Object conv(Object o) {
        if (o == null) return null;
        if (o instanceof Double d && (d.isInfinite() || d.isNaN())) return d.toString();
        if (o instanceof Number || o instanceof Boolean || o instanceof String) return o;
        if (o instanceof Character c) return String.valueOf(c);
        if (o instanceof int[] a) { List<Object> l = new ArrayList<>(); for (int x : a) l.add(x); return l; }
        if (o instanceof long[] a) { List<Object> l = new ArrayList<>(); for (long x : a) l.add(x); return l; }
        if (o instanceof double[] a) { List<Object> l = new ArrayList<>(); for (double x : a) l.add(conv(x)); return l; }
        if (o instanceof char[] a) { List<Object> l = new ArrayList<>(); for (char x : a) l.add(String.valueOf(x)); return l; }
        if (o instanceof boolean[] a) { List<Object> l = new ArrayList<>(); for (boolean x : a) l.add(x); return l; }
        if (o instanceof Object[] a) { List<Object> l = new ArrayList<>(); for (Object x : a) l.add(conv(x)); return l; }
        if (o instanceof Map<?, ?> mp) {
            Map<String, Object> r = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : mp.entrySet()) r.put(F.key(e.getKey()), conv(e.getValue()));
            return r;
        }
        if (o instanceof Collection<?> c) { List<Object> l = new ArrayList<>(); for (Object x : c) l.add(conv(x)); return l; }
        if (o instanceof ListNode n) return "node(" + n.val + ")";
        if (o instanceof TreeNode n) return "node(" + n.val + ")";
        return String.valueOf(o);
    }

    // ---------- structures ----------
    static S arr(String name, Object a) { S s = new S("array", name); s.put("values", conv(a)); return s; }
    static S str(String name, String str) { S s = new S("array", name); s.put("values", conv(str.toCharArray())); return s; }
    static S mat(String name, Object a) { S s = new S("matrix", name); s.put("rows", conv(a)); return s; }

    static S list(String name, ListNode... heads) {
        S s = new S("linkedlist", name);
        List<Object> nodes = new ArrayList<>();
        Set<ListNode> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        for (ListNode h : heads)
            for (ListNode c = h; c != null && seen.add(c); c = c.next) {
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("id", id(c)); n.put("value", c.val); n.put("next", c.next == null ? null : id(c.next));
                nodes.add(n);
            }
        s.put("nodes", nodes);
        return s;
    }

    static S tree(String name, TreeNode... roots) {
        S s = new S("tree", name);
        List<Object> nodes = new ArrayList<>();
        Set<TreeNode> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        String rootId = null;
        for (TreeNode r : roots) {
            if (r == null) continue;
            if (rootId == null) rootId = id(r);
            Deque<TreeNode> st = new ArrayDeque<>();
            st.push(r);
            while (!st.isEmpty()) {
                TreeNode c = st.pop();
                if (!seen.add(c)) continue;
                Map<String, Object> n = new LinkedHashMap<>();
                n.put("id", id(c)); n.put("value", c.val);
                n.put("left", c.left == null ? null : id(c.left));
                n.put("right", c.right == null ? null : id(c.right));
                nodes.add(n);
                if (c.right != null) st.push(c.right);
                if (c.left != null) st.push(c.left);
            }
        }
        s.put("root", rootId);
        s.put("nodes", nodes);
        return s;
    }

    /** Deques are assumed to be used with push/pop (top first); lists keep the top at the end. */
    static S stack(String name, Object c) {
        S s = new S("stack", name);
        List<Object> l = new ArrayList<>();
        if (c instanceof Deque<?> d) { for (Object x : d) l.add(0, conv(x)); }
        else if (c instanceof Collection<?> col) { for (Object x : col) l.add(conv(x)); }
        else { Object v = conv(c); if (v instanceof List<?> lv) l.addAll(lv); }
        s.put("values", l);
        return s;
    }
    static S queue(String name, Collection<?> c) { S s = new S("queue", name); s.put("values", conv(c)); return s; }
    static S heap(String name, PriorityQueue<?> pq) { S s = new S("heap", name); s.put("values", conv(pq.toArray())); return s; }
    static S map(String name, Map<?, ?> m) {
        S s = new S("map", name);
        List<Object> e = new ArrayList<>();
        for (Map.Entry<?, ?> x : m.entrySet()) e.add(Arrays.asList(conv(x.getKey()), conv(x.getValue())));
        s.put("entries", e);
        return s;
    }
    static S set(String name, Collection<?> c) {
        S s = new S("set", name);
        List<Object> e = new ArrayList<>();
        for (Object x : c) e.add(Collections.singletonList(conv(x)));
        s.put("entries", e);
        return s;
    }
    static S graph(String name, boolean directed) { S s = new S("graph", name); s.put("directed", directed); s.put("nodes", new ArrayList<>()); s.put("edges", new ArrayList<>()); return s; }
}

final class V extends LinkedHashMap<String, Object> {}

final class S extends LinkedHashMap<String, Object> {
    S(String kind, String name) { put("kind", kind); put("name", name); }

    @SuppressWarnings("unchecked")
    private Map<String, Object> sub(String k) { return (Map<String, Object>) computeIfAbsent(k, x -> new LinkedHashMap<String, Object>()); }
    @SuppressWarnings("unchecked")
    private List<Object> lst(String k) { return (List<Object>) computeIfAbsent(k, x -> new ArrayList<Object>()); }

    private static Object key(Object at) {
        if (at instanceof Number || at instanceof String) return at;
        if (at instanceof Character c) return String.valueOf(c);
        return T.id(at);
    }

    S p(String name, Object at) { if (at != null) sub("pointers").put(name, key(at)); return this; }
    S m(Object at, String mark) { if (at != null) sub("marks").put(String.valueOf(key(at)), mark); return this; }
    S m(int r, int c, String mark) { sub("marks").put(r + "," + c, mark); return this; }
    S range(int a, int b, String mark) { for (int i = a; i <= b; i++) sub("marks").put(String.valueOf(i), mark); return this; }
    S mark(Map<?, String> marks) { if (marks != null) for (Map.Entry<?, String> e : marks.entrySet()) m(e.getKey(), e.getValue()); return this; }
    S bars() { put("view", "bars"); return this; }
    S note(String n) { put("note", n); return this; }
    S labels(Object rows, Object cols) { if (rows != null) put("rowLabels", T.conv(rows)); if (cols != null) put("colLabels", T.conv(cols)); return this; }

    S node(Object id, Object label) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("id", String.valueOf(id)); n.put("label", T.conv(label));
        lst("nodes").add(n);
        return this;
    }
    S nodeAt(Object id, Object label, double x, double y) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("id", String.valueOf(id)); n.put("label", T.conv(label)); n.put("x", x); n.put("y", y);
        lst("nodes").add(n);
        return this;
    }
    S edge(Object a, Object b, Object w) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("from", String.valueOf(a)); e.put("to", String.valueOf(b));
        if (w != null) e.put("weight", T.conv(w));
        lst("edges").add(e);
        return this;
    }
    @SuppressWarnings("unchecked")
    S em(Object a, Object b, String mark) {
        boolean dir = Boolean.TRUE.equals(get("directed"));
        String sa = String.valueOf(a), sb = String.valueOf(b);
        for (Object o : lst("edges")) {
            Map<String, Object> e = (Map<String, Object>) o;
            if ((e.get("from").equals(sa) && e.get("to").equals(sb)) || (!dir && e.get("from").equals(sb) && e.get("to").equals(sa))) e.put("mark", mark);
        }
        return this;
    }
}
