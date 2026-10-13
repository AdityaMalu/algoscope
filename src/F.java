import java.util.*;
import java.util.function.Supplier;

class ListNode {
    int val; ListNode next;
    ListNode() {}
    ListNode(int v) { val = v; }
    ListNode(int v, ListNode n) { val = v; next = n; }
}

class TreeNode {
    int val; TreeNode left, right;
    TreeNode() {}
    TreeNode(int v) { val = v; }
    TreeNode(int v, TreeNode l, TreeNode r) { val = v; left = l; right = r; }
}

/** A string shown as-is (not quoted) in outputs. */
record R(String s) { public String toString() { return s; } }

/** Formatting of results and builders for inputs. */
final class F {
    static String key(Object k) {
        if (k instanceof String s) return s;
        if (k instanceof Character c) return String.valueOf(c);
        if (k == null) return "null";
        if (k.getClass().isArray() || k instanceof Collection) return s(k);
        return String.valueOf(k);
    }

    static String s(Object o) {
        if (o == null) return "null";
        if (o instanceof R r) return r.s();
        if (o instanceof String str) return "\"" + str + "\"";
        if (o instanceof Character c) return "'" + c + "'";
        if (o instanceof Double d) return (d == Math.rint(d) && !d.isInfinite()) ? String.format("%.1f", d) : String.format("%.5f", d).replaceAll("0+$", "");
        if (o instanceof Number || o instanceof Boolean) return String.valueOf(o);
        if (o instanceof int[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (int x : a) j.add(String.valueOf(x)); return j.toString(); }
        if (o instanceof long[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (long x : a) j.add(String.valueOf(x)); return j.toString(); }
        if (o instanceof double[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (double x : a) j.add(s(x)); return j.toString(); }
        if (o instanceof boolean[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (boolean x : a) j.add(String.valueOf(x)); return j.toString(); }
        if (o instanceof char[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (char x : a) j.add("\"" + x + "\""); return j.toString(); }
        if (o instanceof Object[] a) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (Object x : a) j.add(s(x)); return j.toString(); }
        if (o instanceof Map<?, ?> m) { StringJoiner j = new StringJoiner(", ", "{", "}"); for (Map.Entry<?, ?> e : m.entrySet()) j.add(key(e.getKey()) + "=" + s(e.getValue())); return j.toString(); }
        if (o instanceof Collection<?> c) { StringJoiner j = new StringJoiner(", ", "[", "]"); for (Object x : c) j.add(s(x)); return j.toString(); }
        if (o instanceof ListNode n) {
            StringJoiner j = new StringJoiner(", ", "[", "]");
            Set<ListNode> seen = Collections.newSetFromMap(new LinkedHashMap<>());
            for (ListNode c = n; c != null; c = c.next) { if (!seen.add(c)) { j.add("…cycle"); break; } j.add(String.valueOf(c.val)); }
            return j.toString();
        }
        if (o instanceof TreeNode t) return tree(t);
        return String.valueOf(o);
    }

    static String tree(TreeNode root) {
        if (root == null) return "[]";
        List<String> out = new ArrayList<>();
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        while (!q.isEmpty()) {
            TreeNode c = q.poll();
            if (c == null) { out.add("null"); continue; }
            out.add(String.valueOf(c.val));
            q.add(c.left); q.add(c.right);
        }
        int e = out.size();
        while (e > 0 && out.get(e - 1).equals("null")) e--;
        return "[" + String.join(",", out.subList(0, e)) + "]";
    }

    // ---------- input builders ----------
    static ListNode L(int... v) {
        ListNode d = new ListNode(0), c = d;
        for (int x : v) { c.next = new ListNode(x); c = c.next; }
        return d.next;
    }

    /** Level-order like LeetCode: "3,9,20,null,null,15,7". */
    static TreeNode Tr(String s) {
        s = s.replace("[", "").replace("]", "").trim();
        if (s.isEmpty()) return null;
        String[] p = s.split(",");
        if (p[0].trim().equals("null")) return null;
        TreeNode root = new TreeNode(Integer.parseInt(p[0].trim()));
        Queue<TreeNode> q = new LinkedList<>();
        q.add(root);
        int i = 1;
        while (!q.isEmpty() && i < p.length) {
            TreeNode c = q.poll();
            if (i < p.length && !p[i].trim().equals("null")) { c.left = new TreeNode(Integer.parseInt(p[i].trim())); q.add(c.left); }
            i++;
            if (i < p.length && !p[i].trim().equals("null")) { c.right = new TreeNode(Integer.parseInt(p[i].trim())); q.add(c.right); }
            i++;
        }
        return root;
    }

    static TreeNode find(TreeNode r, int v) {
        if (r == null) return null;
        if (r.val == v) return r;
        TreeNode l = find(r.left, v);
        return l != null ? l : find(r.right, v);
    }

    static char[][] grid(String... rows) { char[][] g = new char[rows.length][]; for (int i = 0; i < rows.length; i++) g[i] = rows[i].toCharArray(); return g; }
    static int[][] copy(int[][] a) { int[][] b = new int[a.length][]; for (int i = 0; i < a.length; i++) b[i] = a[i].clone(); return b; }
    static char[][] copy(char[][] a) { char[][] b = new char[a.length][]; for (int i = 0; i < a.length; i++) b[i] = a[i].clone(); return b; }
    static List<List<String>> lists(String[][] a) { List<List<String>> r = new ArrayList<>(); for (String[] x : a) r.add(new ArrayList<>(Arrays.asList(x))); return r; }
    static <X> X run(Supplier<X> s) { return s.get(); }
}
