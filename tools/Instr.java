import com.sun.source.tree.*;
import com.sun.source.util.*;

import javax.lang.model.element.*;
import javax.tools.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Build-time instrumenter for deep traces.
 *
 * Rewrites every //@code region in src/ so that each executed line records a step: statements after they run,
 * if/loop conditions with their result, for-each iterations, returns with their value, and method entry/exit for the
 * call stack and recursion tree. Every step carries the locals in scope and the array cells the line touches.
 *
 * Edits are inserted inline and never add or remove a line break, so line numbers (and the hand-written T.s/T.n
 * narration, located by line) stay exactly as in src/.
 *
 * Usage: java Instr <srcDir> <outDir>
 */
public class Instr {
    public static void main(String[] args) throws Exception {
        Path src = Path.of(args[0]), out = Path.of(args[1]);
        Files.createDirectories(out);
        List<Path> files = new ArrayList<>();
        try (var s = Files.list(src)) { s.filter(p -> p.toString().endsWith(".java")).sorted().forEach(files::add); }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fm = jc.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        List<String> errors = new ArrayList<>();
        DiagnosticListener<JavaFileObject> dl = d -> { if (d.getKind() == Diagnostic.Kind.ERROR) errors.add(d.toString()); };
        JavacTask task = (JavacTask) jc.getTask(null, fm, dl, List.of("-proc:none", "-Xjcov", "-encoding", "UTF-8"), null,
            fm.getJavaFileObjectsFromPaths(files));
        List<CompilationUnitTree> cus = new ArrayList<>();
        task.parse().forEach(cus::add);
        task.analyze();
        if (!errors.isEmpty()) { errors.forEach(System.err::println); System.exit(1); }
        Trees trees = Trees.instance(task);
        int sites = 0;
        for (CompilationUnitTree cu : cus) {
            String text = cu.getSourceFile().getCharContent(true).toString();
            Unit u = new Unit(trees, cu, text);
            String res = u.run();
            sites += u.sites;
            Path name = Path.of(cu.getSourceFile().toUri()).getFileName();
            Files.writeString(out.resolve(name), res, StandardCharsets.UTF_8);
        }
        System.out.println("instrumented " + sites + " trace points");
    }

    static final class Unit {
        final Trees trees; final CompilationUnitTree cu; final String text; final SourcePositions sp; final LineMap lm;
        final List<int[]> regions = new ArrayList<>();
        final Set<Integer> hidden = new HashSet<>();
        /** position -> (priority -> text); texts at one position are joined by priority. */
        final TreeMap<Integer, TreeMap<Integer, String>> edits = new TreeMap<>();
        int sites = 0;
        Element method;          // method being instrumented
        List<String[]> fields;   // {display name, expression} of fields visible in it

        Unit(Trees trees, CompilationUnitTree cu, String text) {
            this.trees = trees; this.cu = cu; this.text = text;
            this.sp = trees.getSourcePositions(); this.lm = cu.getLineMap();
            String[] lines = text.split("\n", -1);
            for (int i = 0; i < lines.length; i++) {
                String s = lines[i].strip();
                if (s.startsWith("//@code ")) {
                    int j = i + 1;
                    while (j < lines.length && !lines[j].strip().equals("//@end")) j++;
                    regions.add(new int[]{i + 2, j});
                    for (int k = i + 1; k < j; k++) {
                        String t = lines[k].strip();
                        if (t.startsWith("T.") || t.endsWith("// @hide")) hidden.add(k + 1);
                    }
                    i = j;
                }
            }
        }

        long start(Tree t) { return sp.getStartPosition(cu, t); }
        long end(Tree t) { return sp.getEndPosition(cu, t); }
        int line(long pos) { return (int) lm.getLineNumber(pos); }
        String src(Tree t) { return text.substring((int) start(t), (int) end(t)); }
        boolean inRegion(int ln) { return regionOf(ln) >= 0; }
        int regionOf(int ln) { for (int i = 0; i < regions.size(); i++) if (ln >= regions.get(i)[0] && ln <= regions.get(i)[1]) return i; return -1; }

        /** Add text at a position; texts at one position are ordered by priority, then by insertion. */
        void put(long pos, int prio, String s) {
            TreeMap<Integer, String> at = edits.computeIfAbsent((int) pos, k -> new TreeMap<>());
            at.merge(prio, s, (a, b) -> a + b);
        }
        /** Like put, but a later call at the same position replaces the text (declarators of one statement). */
        void putReplace(long pos, int prio, String s) { edits.computeIfAbsent((int) pos, k -> new TreeMap<>()).put(prio, s); }

        String run() {
            if (regions.isEmpty()) return text;
            // methods inside regions
            new TreePathScanner<Void, Void>() {
                @Override public Void visitMethod(MethodTree m, Void x) {
                    if (m.getBody() != null && end(m.getBody()) > 0 && inRegion(line(start(m.getBody())))) method(getCurrentPath());
                    return null;
                }
                @Override public Void visitLambdaExpression(LambdaExpressionTree l, Void x) { return null; }
                @Override public Void visitNewClass(NewClassTree n, Void x) { return null; }
            }.scan(cu, null);
            StringBuilder sb = new StringBuilder(text);
            for (var e : edits.descendingMap().entrySet()) sb.insert(e.getKey(), String.join("", e.getValue().values()));
            return sb.toString();
        }

        // ---------------- methods ----------------
        void method(TreePath mp) {
            MethodTree m = (MethodTree) mp.getLeaf();
            ExecutableElement me = (ExecutableElement) trees.getElement(mp);
            BlockTree body = m.getBody();
            int ln = line(start(body));
            if (hidden.contains(ln)) return;
            method = me;
            boolean isStatic = me.getModifiers().contains(Modifier.STATIC);
            fields = new ArrayList<>();
            TypeElement cls = (TypeElement) me.getEnclosingElement();
            for (Element f : cls.getEnclosedElements()) {
                if (f.getKind() != ElementKind.FIELD) continue;
                boolean st = f.getModifiers().contains(Modifier.STATIC);
                if (!st && isStatic) continue;
                Tree ft = trees.getTree(f);
                if (ft == null || regionOf(line(start(ft))) != regionOf(ln)) continue;   // only this problem's fields
                String nm = f.getSimpleName().toString();
                fields.add(new String[]{nm, st ? cls.getSimpleName() + "." + nm : "this." + nm});
            }
            String name = me.getKind() == ElementKind.CONSTRUCTOR ? cls.getSimpleName().toString() : me.getSimpleName().toString();
            List<String[]> params = new ArrayList<>();
            for (VariableTree p : m.getParameters()) params.add(new String[]{p.getName().toString(), p.getName().toString()});
            // Which parameters label a call in the recursion tree: small values, not inputs or accumulators.
            StringJoiner lbl = new StringJoiner(",", "#=", ";");
            for (int i = 0; i < m.getParameters().size(); i++) if (labelType(m.getParameters().get(i).getType())) lbl.add(String.valueOf(i));
            String spec = spec(body) + lbl + "#p=" + params.size() + ";" + (me.getKind() == ElementKind.CONSTRUCTOR && nodeClass(cls) ? "#q=1;" : "");
            List<String[]> entry = new ArrayList<>(params);
            Set<String> pn = new HashSet<>(); for (String[] x : params) pn.add(x[0]);
            if (me.getKind() != ElementKind.CONSTRUCTOR)   // a constructor can't read final fields before assigning them
                for (String[] f : fields) if (pn.add(f[0])) entry.add(f);
            long open = start(body) + 1;
            List<? extends StatementTree> stmts = body.getStatements();
            if (me.getKind() == ElementKind.CONSTRUCTOR && !stmts.isEmpty() && isCtorCall(stmts.get(0))) open = semi(stmts.get(0));
            put(open, 50, " T.enter(" + ln + ", \"" + name + "\", " + names(entry) + ", " + values(entry) + ", \"" + spec + "\"); try {");
            put(end(body) - 1, 50, " } finally { T.exit(); } ");
            sites++;
            TreePath bp = new TreePath(mp, body);
            for (StatementTree s : stmts) stmt(new TreePath(bp, s), false);
        }

        /** A class that links to its own kind (list/tree/trie/graph node): its constructor runs without steps. */
        static boolean nodeClass(TypeElement cls) {
            for (Element f : cls.getEnclosedElements()) {
                if (f.getKind() != ElementKind.FIELD) continue;
                String t = f.asType().toString(), self = cls.getQualifiedName().toString();
                if (t.equals(self) || t.equals(self + "[]") || t.contains("<" + self + ">") || t.contains(", " + self + ">")) return true;
            }
            return false;
        }

        static final Set<String> SMALL = Set.of("String", "Integer", "Long", "Character", "Boolean", "Double", "TreeNode", "ListNode", "StringBuilder");
        static boolean labelType(Tree t) {
            if (t instanceof PrimitiveTypeTree) return true;
            if (t instanceof IdentifierTree id) return SMALL.contains(id.getName().toString());
            if (t instanceof ParameterizedTypeTree pt && pt.getType() instanceof IdentifierTree id
                && Set.of("List", "ArrayList", "LinkedList", "Deque", "ArrayDeque").contains(id.getName().toString())
                && pt.getTypeArguments().size() == 1 && pt.getTypeArguments().get(0) instanceof IdentifierTree a)
                return Set.of("Integer", "Character", "String", "Long").contains(a.getName().toString());
            return false;
        }

        boolean isCtorCall(StatementTree s) {
            if (end(s) <= 0) return false;   // the implicit super() javac adds has no source position
            if (s instanceof ExpressionStatementTree es && es.getExpression() instanceof MethodInvocationTree mi) {
                String n = mi.getMethodSelect().toString();
                return n.equals("super") || n.equals("this");
            }
            return false;
        }

        /** Position just after the statement's terminating semicolon. */
        long semi(Tree s) {
            int e = (int) end(s);
            if (e > 0 && text.charAt(e - 1) == ';') return e;
            int k = e;
            while (k < text.length() && text.charAt(k) != ';') k++;
            return k + 1;
        }

        // ---------------- statements ----------------
        void stmt(TreePath p, boolean sole) {
            StatementTree s = (StatementTree) p.getLeaf();
            if (end(s) <= 0) return;   // generated by javac
            int ln = line(start(s));
            if (hidden.contains(ln)) return;
            switch (s.getKind()) {
                case BLOCK -> { for (StatementTree c : ((BlockTree) s).getStatements()) stmt(new TreePath(p, c), false); }
                case EXPRESSION_STATEMENT -> post(p, ((ExpressionStatementTree) s).getExpression(), sole);
                case VARIABLE -> post(p, ((VariableTree) s).getInitializer(), sole);
                case RETURN -> {
                    ExpressionTree e = ((ReturnTree) s).getExpression();
                    if (e == null) pre(p, 7, null, sole);
                    else {
                        put(start(e), 30, "T.r(" + ln + ", " + scope(p, null) + ", " + acc(e) + ", ");
                        put(end(e), 10, ")");
                        sites++;
                    }
                }
                case CONTINUE -> pre(p, 8, null, sole);
                case BREAK -> pre(p, 9, null, sole);
                case THROW -> pre(p, 10, ((ThrowTree) s).getExpression(), sole);
                case IF -> {
                    IfTree t = (IfTree) s;
                    cond(p, t.getCondition(), t.getElseStatement() == null ? 1 : 2, ln);
                    sub(p, t.getThenStatement());
                    if (t.getElseStatement() != null) sub(p, t.getElseStatement());
                }
                case WHILE_LOOP -> { WhileLoopTree t = (WhileLoopTree) s; cond(p, t.getCondition(), 3, ln); sub(p, t.getStatement()); }
                case DO_WHILE_LOOP -> { DoWhileLoopTree t = (DoWhileLoopTree) s; sub(p, t.getStatement()); cond(p, t.getCondition(), 4, line(start(t.getCondition()))); }
                case FOR_LOOP -> {
                    ForLoopTree t = (ForLoopTree) s;
                    if (t.getCondition() != null) cond(p, t.getCondition(), 3, ln);
                    sub(p, t.getStatement());
                }
                case ENHANCED_FOR_LOOP -> {
                    EnhancedForLoopTree t = (EnhancedForLoopTree) s;
                    StatementTree b = t.getStatement();
                    TreePath bp = new TreePath(p, b);
                    Element var = trees.getElement(new TreePath(p, t.getVariable()));
                    String call = "T.at(" + ln + ", 5, " + scope(bp, var) + ", " + acc(t.getExpression()) + ");";
                    sites++;
                    if (b instanceof BlockTree bt) {
                        put(start(bt) + 1, 50, " " + call);
                        for (StatementTree c : bt.getStatements()) stmt(new TreePath(bp, c), false);
                    } else {
                        put(start(b), 0, "{ " + call + " ");
                        put(semi(b), 90, "}");
                        stmt(bp, false);
                    }
                }
                case SWITCH -> {
                    for (CaseTree c : ((SwitchTree) s).getCases()) {
                        if (c.getStatements() != null) for (StatementTree x : c.getStatements()) stmt(new TreePath(new TreePath(p, c), x), false);
                        else if (c.getBody() instanceof BlockTree bt) stmt(new TreePath(new TreePath(p, c), bt), false);
                    }
                }
                case TRY -> {
                    TryTree t = (TryTree) s;
                    stmt(new TreePath(p, t.getBlock()), false);
                    for (CatchTree c : t.getCatches()) stmt(new TreePath(new TreePath(p, c), c.getBlock()), false);
                    if (t.getFinallyBlock() != null) stmt(new TreePath(p, t.getFinallyBlock()), false);
                }
                case SYNCHRONIZED -> stmt(new TreePath(p, ((SynchronizedTree) s).getBlock()), false);
                case LABELED_STATEMENT -> stmt(new TreePath(p, ((LabeledStatementTree) s).getStatement()), false);
                default -> { }
            }
        }

        /** A statement nested directly in if/loop: wrap it in braces if we add code around it. */
        void sub(TreePath p, StatementTree child) { stmt(new TreePath(p, child), !(child instanceof BlockTree)); }

        void post(TreePath p, Tree expr, boolean sole) {
            StatementTree s = (StatementTree) p.getLeaf();
            Element self = s instanceof VariableTree ? trees.getElement(p) : null;
            String call = " T.at(" + line(start(s)) + ", 0, " + scope(p, self) + ", " + acc(expr) + ");";
            long e = semi(s);
            putReplace(e, 50, call);   // a later declarator of "int a = 0, b = 1;" replaces the earlier one
            if (sole) { put(start(s), 0, "{"); put(e, 90, "}"); }
            sites++;
        }

        /** "//@prune <label expression>" at the end of a line adds that branch to the recursion tree as ruled out. */
        String pruneCall(int ln) {
            String l = text.split("\n", -1)[ln - 1];
            int k = l.indexOf("//@prune ");
            return k < 0 ? "" : "T.prune(" + l.substring(k + 9).strip() + "); ";
        }

        void pre(TreePath p, int kind, Tree expr, boolean sole) {
            StatementTree s = (StatementTree) p.getLeaf();
            String call = pruneCall(line(start(s))) + "T.at(" + line(start(s)) + ", " + kind + ", " + scope(p, null) + ", " + acc(expr) + ");";
            if (sole) { put(start(s), 0, "{ " + call + " "); put(semi(s), 90, "}"); }
            else put(start(s), 40, call + " ");
            sites++;
        }

        void cond(TreePath p, ExpressionTree c, int kind, int ln) {
            ExpressionTree e = c instanceof ParenthesizedTree pt ? pt.getExpression() : c;
            put(start(e), 30, "T.c(" + ln + ", " + kind + ", " + scope(new TreePath(p, c), null) + ", " + acc(e) + ", ");
            put(end(e), 10, ")");
            sites++;
        }

        // ---------------- locals in scope ----------------
        /** Locals visible (and definitely assigned) at a statement, found by walking up the tree. */
        String scope(TreePath p, Element extra) {
            List<VariableTree> vs = new ArrayList<>();
            Tree child = p.getLeaf();
            for (TreePath q = p.getParentPath(); q != null; child = q.getLeaf(), q = q.getParentPath()) {
                Tree t = q.getLeaf();
                switch (t.getKind()) {
                    case BLOCK -> before(((BlockTree) t).getStatements(), child, vs);
                    case CASE -> { if (((CaseTree) t).getStatements() != null) before(((CaseTree) t).getStatements(), child, vs); }
                    case FOR_LOOP -> before(((ForLoopTree) t).getInitializer(), child, vs);
                    case ENHANCED_FOR_LOOP -> { EnhancedForLoopTree f = (EnhancedForLoopTree) t; if (child == f.getStatement()) vs.add(f.getVariable()); }
                    case CATCH -> vs.add(((CatchTree) t).getParameter());
                    case TRY -> { for (Tree r : ((TryTree) t).getResources()) { if (r == child) break; if (r instanceof VariableTree v) vs.add(v); } }
                    case METHOD -> { vs.addAll(((MethodTree) t).getParameters()); q = null; }
                    case LAMBDA_EXPRESSION, CLASS, NEW_CLASS -> q = null;
                    default -> { }
                }
                if (q == null) break;
            }
            if (extra != null && p.getLeaf() instanceof VariableTree v && v.getInitializer() != null) vs.add(v);
            vs.sort(Comparator.comparingLong(this::start));
            Set<String> seen = new HashSet<>();
            List<String[]> nv = new ArrayList<>();
            for (VariableTree v : vs) { String n = v.getName().toString(); if (seen.add(n)) nv.add(new String[]{n, n}); }
            for (String[] f : fields) if (seen.add(f[0])) nv.add(f);
            return names(nv) + ", " + values(nv);
        }

        static void before(List<? extends Tree> stmts, Tree child, List<VariableTree> out) {
            for (Tree s : stmts) {
                if (s == child) break;
                if (s instanceof VariableTree v && v.getInitializer() != null) out.add(v);
            }
        }

        static String names(List<String[]> nv) {
            StringJoiner j = new StringJoiner(", ", "new String[]{", "}");
            for (String[] x : nv) j.add("\"" + x[0] + "\"");
            return j.toString();
        }
        static String values(List<String[]> nv) {
            StringJoiner j = new StringJoiner(", ", "new Object[]{", "}");
            for (String[] x : nv) j.add(x[1]);
            return j.toString();
        }

        // ---------------- cells a line reads or writes ----------------
        static String base(ExpressionTree e) {
            if (e instanceof IdentifierTree id) return id.getName().toString();
            if (e instanceof MemberSelectTree ms && ms.getExpression() instanceof IdentifierTree q && q.getName().contentEquals("this"))
                return ms.getIdentifier().toString();
            return null;
        }

        /** Index expressions we may evaluate early: no calls, array reads, assignments, or division by a variable. */
        boolean pure(ExpressionTree e) {
            boolean[] ok = {true};
            new TreeScanner<Void, Void>() {
                @Override public Void scan(Tree t, Void x) {
                    if (t == null || !ok[0]) return null;
                    switch (t.getKind()) {
                        case IDENTIFIER, INT_LITERAL, LONG_LITERAL, CHAR_LITERAL, PARENTHESIZED, TYPE_CAST, PRIMITIVE_TYPE,
                             PLUS, MINUS, MULTIPLY, UNARY_MINUS, UNARY_PLUS, CONDITIONAL_EXPRESSION, LEFT_SHIFT, RIGHT_SHIFT,
                             UNSIGNED_RIGHT_SHIFT, AND, OR, XOR, EQUAL_TO, NOT_EQUAL_TO, LESS_THAN, GREATER_THAN,
                             LESS_THAN_EQUAL, GREATER_THAN_EQUAL, CONDITIONAL_AND, CONDITIONAL_OR, LOGICAL_COMPLEMENT -> { }
                        case DIVIDE, REMAINDER -> { if (!(((BinaryTree) t).getRightOperand() instanceof LiteralTree)) ok[0] = false; }
                        case MEMBER_SELECT -> { if (!((MemberSelectTree) t).getIdentifier().contentEquals("length")) ok[0] = false; }
                        default -> ok[0] = false;
                    }
                    return super.scan(t, x);
                }
            }.scan(e, null);
            return ok[0];
        }

        String acc(Tree t) {
            if (t == null) return "null";
            List<String> out = new ArrayList<>();
            Set<Tree> writes = new HashSet<>(), inner = new HashSet<>();
            new TreeScanner<Void, Void>() {
                @Override public Void visitLambdaExpression(LambdaExpressionTree l, Void x) { return null; }
                @Override public Void visitNewClass(NewClassTree n, Void x) { if (n.getClassBody() == null) return super.visitNewClass(n, x); return null; }
                @Override public Void visitAssignment(AssignmentTree a, Void x) { writes.add(a.getVariable()); return super.visitAssignment(a, x); }
                @Override public Void visitCompoundAssignment(CompoundAssignmentTree a, Void x) { writes.add(a.getVariable()); return super.visitCompoundAssignment(a, x); }
                @Override public Void visitUnary(UnaryTree u, Void x) {
                    switch (u.getKind()) { case PREFIX_INCREMENT, PREFIX_DECREMENT, POSTFIX_INCREMENT, POSTFIX_DECREMENT -> writes.add(u.getExpression()); default -> { } }
                    return super.visitUnary(u, x);
                }
                @Override public Void visitArrayAccess(ArrayAccessTree a, Void x) {
                    if (!inner.contains(a)) {
                        boolean w = writes.contains(a);
                        if (a.getExpression() instanceof ArrayAccessTree in) {
                            String b = base(in.getExpression());
                            inner.add(in);
                            if (b != null && pure(in.getIndex()) && pure(a.getIndex()))
                                out.add("\"" + b + "\", (" + src(in.getIndex()) + "), (" + src(a.getIndex()) + "), " + w);
                        } else {
                            String b = base(a.getExpression());
                            if (b != null && pure(a.getIndex())) out.add("\"" + b + "\", (" + src(a.getIndex()) + "), null, " + w);
                        }
                    }
                    return super.visitArrayAccess(a, x);
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree mi, Void x) {
                    if (mi.getMethodSelect() instanceof MemberSelectTree ms && !mi.getArguments().isEmpty()) {
                        String n = ms.getIdentifier().toString(), b = base(ms.getExpression());
                        boolean w = n.equals("set") || n.equals("setCharAt");
                        if (b != null && (w || n.equals("get") || n.equals("charAt")) && pure(mi.getArguments().get(0))) {
                            ExpressionTree a0 = mi.getArguments().get(0);
                            out.add("\"" + b + "\", (" + src(a0) + "), null, " + w);
                        }
                    }
                    return super.visitMethodInvocation(mi, x);
                }
            }.scan(t, null);
            if (out.isEmpty()) return "null";
            return "new Object[]{" + String.join(", ", out) + "}";
        }

        // ---------------- per-method spec: which variables index which arrays, and how deques are used ----------------
        String spec(BlockTree body) {
            Map<String, Set<String>> ptr = new TreeMap<>(), cell = new TreeMap<>();
            Map<String, String> use = new TreeMap<>();
            new TreeScanner<Void, Void>() {
                @Override public Void visitLambdaExpression(LambdaExpressionTree l, Void x) { return null; }
                @Override public Void visitArrayAccess(ArrayAccessTree a, Void x) {
                    if (a.getExpression() instanceof ArrayAccessTree in) {
                        String b = base(in.getExpression()), r = ident(in.getIndex()), c = ident(a.getIndex());
                        if (b != null && r != null && c != null) cell.computeIfAbsent(b, k -> new TreeSet<>()).add(r + "|" + c);
                    } else {
                        String b = base(a.getExpression()), i = ident(a.getIndex());
                        if (b != null && i != null) ptr.computeIfAbsent(b, k -> new TreeSet<>()).add(i);
                    }
                    return super.visitArrayAccess(a, x);
                }
                @Override public Void visitMethodInvocation(MethodInvocationTree mi, Void x) {
                    if (mi.getMethodSelect() instanceof MemberSelectTree ms) {
                        String n = ms.getIdentifier().toString(), b = base(ms.getExpression());
                        if (b != null) {
                            String i = mi.getArguments().isEmpty() ? null : ident(mi.getArguments().get(0));
                            if ((n.equals("get") || n.equals("charAt") || n.equals("set") || n.equals("setCharAt")) && i != null)
                                ptr.computeIfAbsent(b, k -> new TreeSet<>()).add(i);
                            switch (n) {
                                case "push", "pop" -> use.putIfAbsent(b, "S");
                                case "offer", "poll", "pollFirst", "pollLast", "offerLast", "offerFirst", "peekFirst", "peekLast",
                                     "addLast", "addFirst", "removeFirst", "removeLast" -> use.put(b, "Q");
                                default -> { }
                            }
                        }
                    }
                    return super.visitMethodInvocation(mi, x);
                }
            }.scan(body, null);
            StringBuilder sb = new StringBuilder();
            ptr.forEach((k, v) -> { if (!v.isEmpty()) sb.append(k).append('=').append(String.join(",", v)).append(';'); });
            cell.forEach((k, v) -> sb.append(k).append('=').append(String.join(",", v)).append(';'));
            use.forEach((k, v) -> sb.append('@').append(k).append('=').append(v).append(';'));
            return sb.toString();
        }

        static String ident(ExpressionTree e) {
            while (e instanceof ParenthesizedTree p) e = p.getExpression();
            if (e instanceof IdentifierTree id) return id.getName().toString();
            if (e instanceof BinaryTree b && (b.getKind() == Tree.Kind.PLUS || b.getKind() == Tree.Kind.MINUS)
                && b.getRightOperand() instanceof LiteralTree) return ident(b.getLeftOperand());
            return null;
        }
    }
}
