import java.util.*;

/** Tracer: records snapshots of program state while a solution runs. */
final class T {
    static final int CAP = Boolean.getBoolean("algoscope.deep") ? 20000 : 3000;
    static List<Object> steps = new ArrayList<>();
    static boolean on = false, capped = false;
    static final List<String> calls = new ArrayList<>();
    static final IdentityHashMap<Object, String> ids = new IdentityHashMap<>();

    static void begin() { steps = new ArrayList<>(); on = true; capped = false; calls.clear(); ids.clear(); frames.clear(); nodes = new ArrayList<>(); roots.clear(); back = -1; quiet = 0; }
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
    /** Hand-written call frames. Deep traces build the call stack from T.enter/T.exit instead. */
    static void call(String frame) { if (on && !DEEP) calls.add(frame); }
    static void ret() { if (on && !DEEP && !calls.isEmpty()) calls.remove(calls.size() - 1); }

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
        if (DEEP) { m.put("hand", true); m.put("st", stackIds()); m.put("up", nodes.size()); }
        steps.add(m);
    }

    // ---------- deep traces: calls inserted by tools/Instr.java ----------
    // Step kinds: 0 statement ran, 1 if (no else), 2 if/else, 3 loop check, 4 do-while check, 5 for-each item,
    // 6 return value, 7 return, 8 continue, 9 break, 10 throw, 11 method entered.
    static final boolean DEEP = Boolean.getBoolean("algoscope.deep");

    record Spec(Map<String, List<String>> ptr, Map<String, List<String[]>> cell, Map<String, String> use, Set<Integer> label, int params, boolean quiet) {}
    record Fr(int node, Spec spec) {}
    /** Depth of quiet calls (node-class constructors); nothing is recorded inside them. */
    static int quiet = 0;
    static final Map<String, Spec> specs = new HashMap<>();
    static final List<Fr> frames = new ArrayList<>();
    /** Recursion tree: [parent id or -1, compact label, label with names, result]. */
    static List<Object[]> nodes = new ArrayList<>();
    static final List<Object> roots = new ArrayList<>();
    static int back = -1, backDepth;

    static Spec spec(String s) {
        return specs.computeIfAbsent(s, k -> {
            Map<String, List<String>> ptr = new LinkedHashMap<>(); Map<String, List<String[]>> cell = new LinkedHashMap<>(); Map<String, String> use = new LinkedHashMap<>();
            Set<Integer> label = new HashSet<>(); int params = -1; boolean q = false;
            for (String part : k.split(";")) {
                if (part.isEmpty()) continue;
                int eq = part.indexOf('=');
                String name = part.substring(0, eq), val = part.substring(eq + 1);
                if (name.equals("#p")) params = Integer.parseInt(val);
                else if (name.equals("#q")) q = true;
                else if (name.equals("#")) { for (String x : val.split(",")) if (!x.isEmpty()) label.add(Integer.parseInt(x)); }
                else if (name.startsWith("@")) use.put(name.substring(1), val);
                else if (val.contains("|")) { List<String[]> l = new ArrayList<>(); for (String x : val.split(",")) l.add(x.split("\\|")); cell.put(name, l); }
                else ptr.put(name, Arrays.asList(val.split(",")));
            }
            return new Spec(ptr, cell, use, label, params, q);
        });
    }

    static List<Object> stackIds() { List<Object> l = new ArrayList<>(); for (Fr f : frames) if (f.node >= 0) l.add(f.node); return l; }

    static void enter(int line, String name, String[] n, Object[] v, String spec) {
        int id = -1;
        Spec sq = spec(spec);
        if (sq.quiet || quiet > 0) { quiet++; frames.add(new Fr(-1, sq)); return; }
        if (on && steps.size() < CAP) {
            id = nodes.size();
            int parent = -1;
            for (int i = frames.size() - 1; i >= 0 && parent < 0; i--) parent = frames.get(i).node;
            Spec sp = spec(spec);
            StringJoiner a = new StringJoiner(", "), b = new StringJoiner(", ");
            for (int i = 0; i < (sp.params >= 0 ? sp.params : n.length); i++) {
                if (sp.label.contains(i)) { String s = argText(v[i]); if (s != null) a.add(s); }
                // the call stack names every argument of the first call; deeper calls only their small ones
                if ((sp.label.contains(i) || frames.isEmpty()) && !skip(v[i])) { String s = shortText(v[i]); b.add(n[i] + "=" + (s.length() > 30 ? s.substring(0, 29) + "…" : s)); }
            }
            nodes.add(new Object[]{parent, name + "(" + a + ")", name + "(" + b + ")", null});
        }
        frames.add(new Fr(id, spec(spec)));
        if (frames.size() == 1) for (Object x : v) if (x instanceof TreeNode || x instanceof ListNode) roots.add(x);
        record(line, 11, n, v, null, null);
    }

    /** A branch the code rules out without calling into it (drawn red and dashed in the recursion tree). */
    static void prune(String label) {
        if (!on || steps.size() >= CAP) return;
        int parent = -1;
        for (int i = frames.size() - 1; i >= 0 && parent < 0; i--) parent = frames.get(i).node;
        nodes.add(new Object[]{parent, label, label, "✕"});
    }

    static void exit() {
        if (frames.isEmpty()) return;
        Fr f = frames.remove(frames.size() - 1);
        if (quiet > 0) { quiet--; return; }
        if (on && f.node >= 0) { back = f.node; backDepth = frames.size(); }
    }

    static void at(int line, int kind, String[] n, Object[] v, Object[] acc) { record(line, kind, n, v, acc, null); }
    static boolean c(int line, int kind, String[] n, Object[] v, Object[] acc, boolean cond) { record(line, kind, n, v, acc, cond); return cond; }
    static int r(int line, String[] n, Object[] v, Object[] acc, int x) { ret(line, n, v, acc, x); return x; }
    static long r(int line, String[] n, Object[] v, Object[] acc, long x) { ret(line, n, v, acc, x); return x; }
    static double r(int line, String[] n, Object[] v, Object[] acc, double x) { ret(line, n, v, acc, x); return x; }
    static boolean r(int line, String[] n, Object[] v, Object[] acc, boolean x) { ret(line, n, v, acc, x); return x; }
    static char r(int line, String[] n, Object[] v, Object[] acc, char x) { ret(line, n, v, acc, x); return x; }
    static <X> X r(int line, String[] n, Object[] v, Object[] acc, X x) { ret(line, n, v, acc, x); return x; }

    private static void ret(int line, String[] n, Object[] v, Object[] acc, Object x) {
        String s = shortText(x);
        if (on && !frames.isEmpty()) { int id = frames.get(frames.size() - 1).node; if (id >= 0) nodes.get(id)[3] = s; }
        record(line, 6, n, v, acc, s);
    }

    /** Short text of a call argument for the recursion tree; null to leave it out. */
    static String argText(Object o) {
        if (o == null) return "null";
        if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
        if (o instanceof Character c) return "'" + c + "'";
        if (o instanceof String s) return s.length() <= 16 ? "\"" + s + "\"" : null;
        if (o instanceof TreeNode t) return String.valueOf(t.val);
        if (o instanceof ListNode l) return String.valueOf(l.val);
        if (o instanceof StringBuilder sb) return sb.length() <= 16 ? "\"" + sb + "\"" : null;
        if (o instanceof List<?> l && l.size() <= 8 && (l.isEmpty() || l.get(0) instanceof Number || l.get(0) instanceof Character || l.get(0) instanceof String)) {
            String s = F.s(l); return s.length() <= 28 ? s : null;
        }
        return null;
    }
    static String shortText(Object o) {
        String s = o instanceof String str ? "\"" + str + "\"" : F.s(o);
        return s.length() > 40 ? s.substring(0, 37) + "…" : s;
    }

    static boolean skip(Object o) {
        if (o == null) return false;
        Class<?> c = o.getClass();
        return o instanceof Random || o instanceof Iterator || o instanceof Comparator || c == Object.class
            || c.isSynthetic() || c.getName().contains("$$Lambda") || o instanceof Thread;
    }

    private static void record(int line, int kind, String[] n, Object[] v, Object[] acc, Object extra) {
        if (!on || quiet > 0) return;
        if (steps.size() >= CAP) { capped = true; return; }
        StackWalker.StackFrame f = StackWalker.getInstance().walk(st -> st.dropWhile(x -> x.getClassName().equals("T")).findFirst().orElse(null));
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("file", f == null ? "" : f.getFileName());
        m.put("raw", line);
        m.put("dir", 0);
        m.put("kind", kind);
        m.put("title", "");
        m.put("explain", "");
        if (kind == 6) m.put("ret", extra);
        else if (extra instanceof Boolean b) m.put("cond", b);
        if (back >= 0 && kind != 11 && frames.size() == backDepth) { m.put("back", back); back = -1; }
        Spec sp = frames.isEmpty() ? spec("") : frames.get(frames.size() - 1).spec;
        Map<String, Object> vars = new LinkedHashMap<>();
        List<Object> structs = new ArrayList<>();
        snapshot(n, v, acc, sp, vars, structs);
        m.put("vars", vars);
        m.put("structures", structs);
        m.put("st", stackIds());
        m.put("up", nodes.size());
        steps.add(m);
    }

    static boolean scalar(Object o) { return o instanceof Number || o instanceof Boolean || o instanceof Character; }

    private static void snapshot(String[] n, Object[] v, Object[] acc, Spec sp, Map<String, Object> vars, List<Object> structs) {
        Map<String, Object> ints = new HashMap<>();
        for (int i = 0; i < n.length; i++) if (v[i] instanceof Integer || v[i] instanceof Long || v[i] instanceof Short || v[i] instanceof Byte) ints.put(n[i], ((Number) v[i]).longValue());
        Map<String, List<Object[]>> touched = new HashMap<>();
        if (acc != null) for (int i = 0; i + 3 < acc.length; i += 4) touched.computeIfAbsent((String) acc[i], k -> new ArrayList<>()).add(new Object[]{acc[i + 1], acc[i + 2], acc[i + 3]});
        List<ListNode> heads = new ArrayList<>(); Map<String, Object> lptr = new LinkedHashMap<>();
        List<TreeNode> troots = new ArrayList<>(); Map<String, Object> tptr = new LinkedHashMap<>();
        for (Object r : roots) { if (r instanceof ListNode l) heads.add(l); else if (r instanceof TreeNode t) troots.add(t); }
        boolean anyList = !heads.isEmpty(), anyTree = !troots.isEmpty();
        Set<String> names = new HashSet<>();
        Map<String, Object> objs = new LinkedHashMap<>();
        for (int i = 0; i < n.length; i++) {
            String name = n[i]; Object o = v[i];
            if (!names.add(name) || skip(o)) continue;
            if (o == null) { vars.put(name, null); continue; }
            if (o instanceof ListNode l) { heads.add(l); lptr.put(name, l); anyList = true; continue; }
            if (o instanceof TreeNode t) { troots.add(t); tptr.put(name, t); anyTree = true; continue; }
            if (scalar(o)) { vars.put(name, conv(o)); continue; }
            if (shapeOf(o) != null) { objs.put(name, o); continue; }
            boolean indexed = sp.ptr.containsKey(name) || touched.containsKey(name);
            if (o instanceof String || o instanceof StringBuilder) {
                if (!indexed) { vars.put(name, o.toString()); continue; }
                S s = str(name, o.toString()); point(s, name, o.toString().length(), sp, ints, touched); structs.add(s); continue;
            }
            S s = shape(name, o, sp);
            if (s == null) { vars.put(name, objText(o)); continue; }
            String kind = (String) s.get("kind");
            if (kind.equals("array")) point(s, name, ((List<?>) s.get("values")).size(), sp, ints, touched);
            else if (kind.equals("matrix")) cells(s, name, sp, ints, touched);
            structs.add(s);
        }
        if (!objs.isEmpty()) structs.addAll(0, objectGraphs(objs));
        if (anyList) { S s = list("list", heads.toArray(new ListNode[0])); lptr.forEach(s::p); structs.add(0, s); }
        if (anyTree) {
            S s = tree("tree", troots.toArray(new TreeNode[0]));
            tptr.forEach((k, x) -> s.p(k, x));
            structs.add(0, s);
        }
    }

    /** The structure that shows a value, or null to show it as text. */
    static S shape(String name, Object o, Spec sp) {
        if (o instanceof int[][] || o instanceof char[][] || o instanceof boolean[][] || o instanceof long[][] || o instanceof double[][]
            || (o instanceof Object[] a && a.length > 0 && a[0] != null && a[0].getClass().isArray())) return mat(name, o);
        if (o.getClass().isArray()) return arr(name, o);
        if (o instanceof PriorityQueue<?> pq) return heap(name, pq);
        String use = sp.use.get(name);
        if (o instanceof Deque<?> d && !(o instanceof List<?> && use == null)) {
            if ("Q".equals(use)) return queue(name, d);
            return stack(name, d);
        }
        if (o instanceof Queue<?> q && !(o instanceof List<?> && use == null)) return queue(name, q);
        if (o instanceof List<?> l) return arr(name, l);
        if (o instanceof Map<?, ?> mp) return map(name, mp);
        if (o instanceof Set<?> st) return set(name, st);
        if (o instanceof Collection<?> c) return arr(name, c);
        return null;
    }

    /** Names that are almost always positions: shown on every indexed array/string they fit, even when not used as an index. */
    static final Set<String> POS = Set.of("l", "r", "left", "right", "lo", "hi", "low", "high", "mid", "start", "end", "begin", "slow", "fast", "i", "j", "p1", "p2", "ind", "idx", "pos");

    static void point(S s, String name, int len, Spec sp, Map<String, Object> ints, Map<String, List<Object[]>> touched) {
        Set<String> names = new LinkedHashSet<>(sp.ptr.getOrDefault(name, List.of()));
        if (!names.isEmpty()) for (String p : POS) if (ints.containsKey(p)) names.add(p);
        for (String p : names) {
            Object x = ints.get(p);
            if (x instanceof Long l && l >= -1 && l <= len) s.p(p, (int) (long) l);
        }
        for (boolean writes : new boolean[]{false, true})   // a cell both read and written on one line shows as written
            for (Object[] t : touched.getOrDefault(name, List.of()))
                if (Boolean.TRUE.equals(t[2]) == writes && t[0] instanceof Number i && i.longValue() >= 0 && i.longValue() < len) s.m(i.intValue(), writes ? "swap" : "compare");
    }

    @SuppressWarnings("unchecked")
    static void cells(S s, String name, Spec sp, Map<String, Object> ints, Map<String, List<Object[]>> touched) {
        List<Object> rows = (List<Object>) s.get("rows");
        java.util.function.BiPredicate<Long, Long> in = (r, c) -> r >= 0 && r < rows.size() && rows.get((int) (long) r) instanceof List<?> row && c >= 0 && c < row.size();
        for (String[] rc : sp.cell.getOrDefault(name, List.of())) {
            if (ints.get(rc[0]) instanceof Long r && ints.get(rc[1]) instanceof Long c && in.test(r, c)) s.m(r.intValue(), c.intValue(), "active");
        }
        for (boolean writes : new boolean[]{false, true})
            for (Object[] t : touched.getOrDefault(name, List.of()))
                if (Boolean.TRUE.equals(t[2]) == writes && t[0] instanceof Number r && t[1] instanceof Number c && in.test(r.longValue(), c.longValue())) s.m(r.intValue(), c.intValue(), writes ? "swap" : "compare");
    }

    // ---------- custom node classes (LRU list nodes, trie nodes, graph nodes, ...) ----------
    /** How a class links to others of its kind: through next (list), left/right (binary tree), children (n-ary tree) or neighbors (graph). */
    record Shape(String kind, java.lang.reflect.Field next, java.lang.reflect.Field left, java.lang.reflect.Field right,
                 java.lang.reflect.Field kids, java.lang.reflect.Field nbrs, List<java.lang.reflect.Field> label, java.lang.reflect.Field end) {}
    static final Map<Class<?>, Optional<Shape>> shapes = new HashMap<>();

    static Shape shapeOf(Object o) {
        if (o == null) return null;
        Class<?> c = o.getClass();
        if (c.getName().startsWith("java.") || o instanceof ListNode || o instanceof TreeNode) return null;
        return shapes.computeIfAbsent(c, k -> {
            java.lang.reflect.Field next = null, left = null, right = null, kids = null, nbrs = null, end = null;
            Map<String, java.lang.reflect.Field> byName = new HashMap<>();
            for (java.lang.reflect.Field f : k.getDeclaredFields()) {
                if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) continue;
                f.setAccessible(true);
                byName.put(f.getName(), f);
                String g = f.getGenericType().getTypeName(), self = k.getTypeName();
                if (f.getType() == k && f.getName().equals("next")) next = f;
                else if (f.getType() == k && f.getName().equals("left")) left = f;
                else if (f.getType() == k && f.getName().equals("right")) right = f;
                else if ((f.getType().isArray() && f.getType().getComponentType() == k) || ((Map.class.isAssignableFrom(f.getType()) || List.class.isAssignableFrom(f.getType())) && g.contains(self) && f.getName().startsWith("child"))) kids = f;
                else if (List.class.isAssignableFrom(f.getType()) && g.contains(self)) nbrs = f;
                if ((f.getType() == boolean.class && (f.getName().startsWith("is") || f.getName().contains("end") || f.getName().contains("End") || f.getName().equals("word")))
                    || (f.getType() == String.class && f.getName().equals("word"))) end = f;
            }
            List<java.lang.reflect.Field> label = new ArrayList<>();
            if (byName.containsKey("key") && (byName.containsKey("val") || byName.containsKey("value"))) { label.add(byName.get("key")); label.add(byName.getOrDefault("val", byName.get("value"))); }
            else for (String nm : new String[]{"val", "value", "ch", "c", "label"}) if (byName.containsKey(nm)) { label.add(byName.get(nm)); break; }
            String kind = next != null ? "list" : (left != null || right != null) ? "tree" : kids != null ? "nary" : nbrs != null ? "graph" : null;
            return Optional.ofNullable(kind == null ? null : new Shape(kind, next, left, right, kids, nbrs, label, end));
        }).orElse(null);
    }

    static Object get(java.lang.reflect.Field f, Object o) { try { return f == null || o == null ? null : f.get(o); } catch (IllegalAccessException e) { return null; } }

    static String nodeLabel(Shape sh, Object o) {
        StringJoiner j = new StringJoiner(":");
        for (java.lang.reflect.Field f : sh.label) j.add(String.valueOf(get(f, o)));
        return j.toString();
    }

    /** One structure per node shape, built from the node variables in scope; null if there are none. */
    static List<S> objectGraphs(Map<String, Object> named) {
        Map<String, List<Object>> roots = new LinkedHashMap<>();
        for (Object o : named.values()) { Shape sh = shapeOf(o); if (sh != null) roots.computeIfAbsent(sh.kind, k -> new ArrayList<>()).add(o); }
        List<S> out = new ArrayList<>();
        for (Map.Entry<String, List<Object>> e : roots.entrySet()) {
            S s = switch (e.getKey()) {
                case "list" -> objList(e.getValue());
                case "graph" -> objGraph(e.getValue());
                default -> objTree(e.getValue());
            };
            named.forEach((k, o) -> { Shape sh = shapeOf(o); if (sh != null && sh.kind.equals(e.getKey())) s.p(k, o); });
            out.add(s);
        }
        return out;
    }

    static S objList(List<Object> heads) {
        S s = new S("linkedlist", "nodes");
        List<Object> nodes = new ArrayList<>();
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        // start from nodes nothing in scope points to first, so a list shows from its head
        for (Object h : heads)
            for (Object c = h; c != null && seen.add(c); c = get(shapeOf(c).next, c)) {
                Map<String, Object> n = new LinkedHashMap<>();
                Object nx = get(shapeOf(c).next, c);
                n.put("id", id(c)); n.put("value", nodeLabel(shapeOf(c), c)); n.put("next", nx == null ? null : id(nx));
                nodes.add(n);
            }
        s.put("nodes", nodes);
        return s;
    }

    static S objTree(List<Object> rootsIn) {
        S s = new S("tree", "nodes");
        List<Object> nodes = new ArrayList<>();
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Object[]> st = new ArrayDeque<>();
        for (int i = rootsIn.size() - 1; i >= 0; i--) st.push(new Object[]{rootsIn.get(i), null});
        String root = null;
        while (!st.isEmpty() && nodes.size() < 400) {
            Object[] top = st.pop(); Object c = top[0];
            if (c == null || !seen.add(c)) continue;
            Shape sh = shapeOf(c);
            Map<String, Object> n = new LinkedHashMap<>();
            String lbl = nodeLabel(sh, c);
            n.put("id", id(c)); n.put("value", !lbl.isEmpty() ? lbl : top[1] != null ? top[1] : "·");
            if (root == null) root = id(c);
            List<Object[]> kids = new ArrayList<>();
            if (sh.kind.equals("tree")) {
                Object l = get(sh.left, c), r = get(sh.right, c);
                n.put("left", l == null ? null : id(l)); n.put("right", r == null ? null : id(r));
                kids.add(new Object[]{l, null}); kids.add(new Object[]{r, null});
            } else {
                Object k = get(sh.kids, c);
                List<Object> ids = new ArrayList<>();
                if (k instanceof Object[] arr) {
                    for (int i = 0; i < arr.length; i++) if (arr[i] != null) kids.add(new Object[]{arr[i], String.valueOf(arr.length <= 26 ? (char) ('a' + i) : (char) i)});
                } else if (k instanceof Map<?, ?> m) {
                    for (Map.Entry<?, ?> x : entries(m)) if (x.getValue() != null) kids.add(new Object[]{x.getValue(), F.key(x.getKey())});
                } else if (k instanceof List<?> l) {
                    for (Object x : l) if (x != null) kids.add(new Object[]{x, null});
                }
                for (Object[] kd : kids) ids.add(id(kd[0]));
                n.put("children", ids);
            }
            nodes.add(n);
            if (sh.end != null) { Object e = get(sh.end, c); if (Boolean.TRUE.equals(e) || e instanceof String) s.m(c, "found"); }
            for (int i = kids.size() - 1; i >= 0; i--) st.push(kids.get(i));
        }
        s.put("root", root);
        s.put("nodes", nodes);
        return s;
    }

    static S objGraph(List<Object> starts) {
        S s = graph("graph", false);
        Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        Deque<Object> q = new ArrayDeque<>(starts);
        Set<String> edges = new HashSet<>();
        while (!q.isEmpty() && seen.size() < 200) {
            Object c = q.poll();
            if (c == null || !seen.add(c)) continue;
            Shape sh = shapeOf(c);
            s.node(id(c), nodeLabel(sh, c));
            if (get(sh.nbrs, c) instanceof List<?> l) for (Object x : l) {
                if (x == null) continue;
                String a = id(c), b = id(x), key = a.compareTo(b) < 0 ? a + "-" + b : b + "-" + a;
                if (edges.add(key)) s.edge(a, b, null);
                q.add(x);
            }
        }
        return s;
    }

    /** Text for objects without a structure: their own toString, or their simple fields. */
    static String objText(Object o) {
        try { if (o.getClass().getMethod("toString").getDeclaringClass() != Object.class) return String.valueOf(o); } catch (NoSuchMethodException e) { }
        StringJoiner j = new StringJoiner(", ", o.getClass().getSimpleName() + "{", "}");
        for (java.lang.reflect.Field fd : o.getClass().getDeclaredFields()) {
            if (java.lang.reflect.Modifier.isStatic(fd.getModifiers())) continue;
            try {
                fd.setAccessible(true); Object x = fd.get(o);
                if (x == null || scalar(x) || x instanceof String) j.add(fd.getName() + "=" + F.s(x));
            } catch (Exception e) { }
        }
        return j.toString();
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
            for (Map.Entry<?, ?> e : entries(mp)) r.put(F.key(e.getKey()), conv(e.getValue()));
            return r;
        }
        if (o instanceof Collection<?> c) { List<Object> l = new ArrayList<>(); for (Object x : items(c)) l.add(conv(x)); return l; }
        if (o instanceof ListNode n) return "node(" + n.val + ")";
        if (o instanceof TreeNode n) return "node(" + n.val + ")";
        Shape sh = shapeOf(o);
        if (sh != null) { String l = nodeLabel(sh, o); return "node(" + (l.isEmpty() ? id(o) : l) + ")"; }
        return objText(o);
    }

    // Hash containers keyed by objects without their own hashCode (tree/list/graph nodes) iterate in
    // identity-hash order, which changes from one JVM run to the next. Sort those so traces are reproducible.
    static boolean identityKeyed(Iterable<?> keys) {
        for (Object k : keys) {
            if (k == null) continue;
            try { if (k.getClass().getMethod("hashCode").getDeclaringClass() == Object.class) return true; }
            catch (NoSuchMethodException e) { return true; }
        }
        return false;
    }
    static boolean unordered(Object c) {
        return (c instanceof HashMap<?, ?> && !(c instanceof LinkedHashMap<?, ?>))
            || (c instanceof HashSet<?> && !(c instanceof LinkedHashSet<?>));
    }
    static final Comparator<String> NATURAL = Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder());
    static List<Map.Entry<?, ?>> entries(Map<?, ?> m) {
        List<Map.Entry<?, ?>> l = new ArrayList<>(m.entrySet());
        if (unordered(m) && identityKeyed(m.keySet())) l.sort((a, b) -> NATURAL.compare(F.key(a.getKey()), F.key(b.getKey())));
        return l;
    }
    static List<Object> items(Collection<?> c) {
        List<Object> l = new ArrayList<>(c);
        if (unordered(c) && identityKeyed(c)) l.sort((a, b) -> NATURAL.compare(F.key(a), F.key(b)));
        return l;
    }

    // ---------- structures ----------
    static S arr(String name, Object a) { S s = new S("array", name); s.put("values", conv(a)); return s; }
    static S str(String name, String str) { S s = new S("array", name); s.put("values", conv(str.toCharArray())); return s; }
    static S mat(String name, Object a) { S s = new S("matrix", name); s.put("rows", conv(a)); return s; }

    static S list(String name, ListNode... heads) {
        S s = new S("linkedlist", name);
        List<Object> nodes = new ArrayList<>();
        Set<ListNode> seen = Collections.newSetFromMap(new LinkedHashMap<>());
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
        Set<TreeNode> seen = Collections.newSetFromMap(new LinkedHashMap<>());
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
        for (Map.Entry<?, ?> x : entries(m)) e.add(Arrays.asList(conv(x.getKey()), conv(x.getValue())));
        s.put("entries", e);
        return s;
    }
    static S set(String name, Collection<?> c) {
        S s = new S("set", name);
        List<Object> e = new ArrayList<>();
        for (Object x : items(c)) e.add(Collections.singletonList(conv(x)));
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
