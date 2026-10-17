import java.util.*;
import java.util.function.Supplier;

/** Problem registry: metadata, main trace, and edge-case runs. */
final class P {
    static final List<Map<String, Object>> all = new ArrayList<>();
    static Map<String, Object> cur;
    static final List<String> warnings = new ArrayList<>();

    static void begin(String title, String marker) {
        StackWalker.StackFrame f = StackWalker.getInstance().walk(st -> st.skip(1).findFirst().orElse(null));
        cur = new LinkedHashMap<>();
        cur.put("title", title);
        cur.put("file", f == null ? "" : f.getFileName());
        cur.put("marker", marker);
        cur.put("edgeCases", new ArrayList<>());
    }
    static void category(String c) { cur.put("category", c); }
    static void summary(String s) { cur.put("summary", s); }
    static void cx(String best, String avg, String worst, String space, String note) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("best", best); m.put("average", avg); m.put("worst", worst); m.put("space", space); m.put("note", note);
        cur.put("complexity", m);
    }
    static void ideas(String... s) { cur.put("intuition", Arrays.asList(s)); }
    static void pits(String... s) { cur.put("pitfalls", Arrays.asList(s)); }

    static void trace(String input, Supplier<Object> run) {
        T.begin();
        String out;
        try { out = F.s(run.get()); } catch (Throwable e) { out = "throws " + e.getClass().getSimpleName(); }
        List<Object> steps = trim(T.end());
        cur.put("input", input);
        cur.put("output", out);
        cur.put("steps", steps);
        cur.put("capped", capped);
        if (T.DEEP) cur.put("calls", calls());
        if (steps.isEmpty()) warnings.add(cur.get("title") + ": main trace has no steps");
    }

    static void edge(String name, String input, String expected, Supplier<Object> run, String why) { add(name, input, expected, run, why, null); }
    static void risky(String name, String input, String expected, Supplier<Object> run, String why) { add(name, input, expected, run, why, "risky"); }

    @SuppressWarnings("unchecked")
    private static void add(String name, String input, String expected, Supplier<Object> run, String why, String status) {
        T.begin();
        String out;
        try { out = F.s(run.get()); } catch (Throwable e) { out = "throws " + e.getClass().getSimpleName(); }
        List<Object> steps = trim(T.end());
        if (out.equals("null") && expected.equals("[]")) out = "[]";
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("name", name); e.put("input", input); e.put("expected", expected); e.put("actual", out);
        boolean match = norm(out).equals(norm(expected));
        e.put("status", status != null ? status : (match ? "ok" : "bug"));
        e.put("why", why);
        e.put("steps", steps);
        e.put("capped", capped);
        if (T.DEEP) e.put("calls", calls());
        if (!match) warnings.add(cur.get("title") + " / " + name + ": expected " + expected + " but got " + out);
        ((List<Object>) cur.get("edgeCases")).add(e);
    }

    /** Deep traces are trimmed again in build.py after hand-written steps are merged in. */
    static final int KEEP_HEAD = T.DEEP ? 900 : 44, KEEP_TAIL = T.DEEP ? 100 : 8;
    static boolean capped;

    /** Long runs keep their beginning and end, with one marker step in between. */
    @SuppressWarnings("unchecked")
    static List<Object> trim(List<Object> steps) {
        capped = steps.size() > KEEP_HEAD + KEEP_TAIL + 1;
        if (!capped) return steps;
        int skipped = steps.size() - KEEP_HEAD - KEEP_TAIL;
        List<Object> out = new ArrayList<>(steps.subList(0, KEEP_HEAD));
        Map<String, Object> marker = new LinkedHashMap<>((Map<String, Object>) steps.get(KEEP_HEAD));
        marker.put("title", "… " + skipped + " similar steps skipped");
        marker.put("explain", "The run keeps repeating the same pattern. This is where it was; the next step jumps ahead to the end.");
        out.add(marker);
        out.addAll(steps.subList(steps.size() - KEEP_TAIL, steps.size()));
        return out;
    }

    /** Recursion-tree nodes of the last run: [parent, label, label with names, result]. */
    static List<Object> calls() {
        List<Object> l = new ArrayList<>();
        for (Object[] n : T.nodes) l.add(Arrays.asList(n));
        return l;
    }

    private static String norm(String s) { return s.replaceAll("\\s+", ""); }

    static void done() {
        for (String k : new String[]{"summary", "complexity", "intuition", "pitfalls", "steps"})
            if (!cur.containsKey(k)) warnings.add(cur.get("title") + ": missing " + k);
        all.add(cur);
    }
}
