import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        List<String> only = Arrays.asList(args).subList(Math.min(1, args.length), args.length);
        String[] topics = {"Hashing", "TwoPointers", "Sliding", "BinSearch", "Stacks", "Lists", "TreesA", "TreesB", "Heaps",
            "GraphsA", "GraphsB", "Backtrack", "Dp1", "Dp2", "Greedy", "Tries", "GraphsC", "Oa", "Amzn1", "Amzn2", "Design", "Amzn3", "Weak"};
        for (String t : topics) {
            if (!only.isEmpty() && !only.contains(t)) continue;
            Class<?> c;
            try { c = Class.forName(t); } catch (ClassNotFoundException e) { continue; }
            c.getDeclaredMethod("run").invoke(null);
        }
        String out = args.length > 0 ? args[0] : "out.json";
        try (Writer w = new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8)) {
            J.write(P.all, w);
        }
        System.out.println("problems: " + P.all.size());
        for (String s : P.warnings) System.out.println("WARN " + s);
    }
}

final class J {
    static void write(Object o, Writer w) throws IOException {
        if (o == null) w.write("null");
        else if (o instanceof String s) str(s, w);
        else if (o instanceof Boolean || o instanceof Integer || o instanceof Long || o instanceof Short || o instanceof Byte) w.write(o.toString());
        else if (o instanceof Number n) { double d = n.doubleValue(); if (Double.isFinite(d)) w.write(d == Math.rint(d) && Math.abs(d) < 1e15 ? Long.toString((long) d) : Double.toString(d)); else str(String.valueOf(d), w); }
        else if (o instanceof Map<?, ?> m) {
            w.write('{'); boolean first = true;
            for (Map.Entry<?, ?> e : m.entrySet()) { if (!first) w.write(','); first = false; str(String.valueOf(e.getKey()), w); w.write(':'); write(e.getValue(), w); }
            w.write('}');
        } else if (o instanceof Collection<?> c) {
            w.write('['); boolean first = true;
            for (Object x : c) { if (!first) w.write(','); first = false; write(x, w); }
            w.write(']');
        } else str(String.valueOf(o), w);
    }
    static void str(String s, Writer w) throws IOException {
        w.write('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> w.write("\\\"");
                case '\\' -> w.write("\\\\");
                case '\n' -> w.write("\\n");
                case '\r' -> w.write("\\r");
                case '\t' -> w.write("\\t");
                default -> { if (c < 0x20) w.write(String.format("\\u%04x", (int) c)); else w.write(c); }
            }
        }
        w.write('"');
    }
}
