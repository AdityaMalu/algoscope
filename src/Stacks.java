import java.util.*;

/** Stack */
class Stacks {
    static void run() {
        P.begin("Valid Parentheses", "isValid");
        P.category("Stack · Matching pairs");
        P.summary("Check that every bracket is closed by the same type in the right order. Push opening brackets; on a closing bracket the top of the stack must be its partner. The string is valid only if the stack ends empty.");
        P.cx("O(1)", "O(n)", "O(n)", "O(n)", "Best case is an immediate mismatch. The stack can hold n/2 brackets.");
        P.ideas("The most recent unclosed bracket must be closed first: last in, first out.",
            "On `)`, `]`, `}`, pop and check it matches.",
            "A closing bracket with an empty stack is invalid.",
            "Leftover openers at the end are invalid too.");
        P.pits("Only counting brackets misses order errors like \"([)]\".",
            "Popping an empty stack throws; check `isEmpty()` first.",
            "Forgetting the final `isEmpty()` check (\"((\" would pass).");
        P.trace("s = \"{[()]}()\"", () -> isValid("{[()]}()"));
        P.edge("Wrong order", "s = \"([)]\"", "false", () -> isValid("([)]"), "The `)` meets `[` on top of the stack.");
        P.edge("Only openers", "s = \"((\"", "false", () -> isValid("(("), "The stack isn't empty at the end.");
        P.edge("Starts with a closer", "s = \"]\"", "false", () -> isValid("]"), "Nothing to match against.");
        P.edge("Empty string", "s = \"\"", "true", () -> isValid(""), "No brackets, nothing unbalanced.");
        P.edge("Simple pairs", "s = \"()[]{}\"", "true", () -> isValid("()[]{}"), "Each pair opens and closes immediately.");
        P.done();

        P.begin("Min Stack", "MinStack");
        P.category("Stack · Design");
        P.summary("Support push, pop, top and getMin in O(1). Keep a second stack where each entry is the minimum of everything at or below that level. Pushing records `min(x, currentMin)`; popping removes both tops together.");
        P.cx("O(1)", "O(1)", "O(1)", "O(n)", "Every operation is O(1); the extra stack doubles the space.");
        P.ideas("The minimum only changes when you push or pop, so remember it per level.",
            "`mins.peek()` is always the minimum of the whole stack.",
            "Push `min(x, mins.peek())` so both stacks stay the same height.");
        P.pits("Storing a single `min` variable fails after popping the minimum.",
            "Only pushing onto `mins` when x is smaller works, but then pops must compare values; duplicates make that fragile.",
            "Using `Integer ==` instead of `.equals` on boxed values above 127.");
        P.trace("push(-2), push(0), push(-3), getMin(), pop(), top(), getMin()", () -> {
            MinStack s = new MinStack(); List<Object> out = new ArrayList<>();
            s.push(-2); out.add(null); s.push(0); out.add(null); s.push(-3); out.add(null);
            out.add(s.getMin()); s.pop(); out.add(null); out.add(s.top()); out.add(s.getMin());
            return out;
        });
        P.edge("Duplicate minimums", "push(1), push(1), pop(), getMin()", "[null, null, null, 1]", () -> {
            MinStack s = new MinStack(); List<Object> out = new ArrayList<>();
            s.push(1); out.add(null); s.push(1); out.add(null); s.pop(); out.add(null); out.add(s.getMin()); return out;
        }, "Each level stores its own minimum, so popping one 1 leaves the other.");
        P.edge("Increasing pushes", "push(1), push(2), push(3), getMin()", "[null, null, null, 1]", () -> {
            MinStack s = new MinStack(); List<Object> out = new ArrayList<>();
            s.push(1); out.add(null); s.push(2); out.add(null); s.push(3); out.add(null); out.add(s.getMin()); return out;
        }, "The minimum stays 1 at every level.");
        P.edge("Extreme values", "push(2147483647), push(-2147483648), getMin()", "[null, null, -2147483648]", () -> {
            MinStack s = new MinStack(); List<Object> out = new ArrayList<>();
            s.push(Integer.MAX_VALUE); out.add(null); s.push(Integer.MIN_VALUE); out.add(null); out.add(s.getMin()); return out;
        }, "No arithmetic on values, so no overflow (unlike the \"store differences\" trick).");
        P.done();

        P.begin("Next Greater Element I", "nextGreaterElement");
        P.category("Stack · Monotonic stack");
        P.summary("For each value in `nums1`, find the first larger value to its right in `nums2`. Scan `nums2` with a decreasing stack: when a bigger value arrives, it's the next greater element for everything smaller it pops. Store answers in a map, then look up `nums1`.");
        P.cx("O(n + m)", "O(n + m)", "O(n + m)", "O(m)", "Each element of nums2 is pushed and popped at most once.");
        P.ideas("Keep the stack decreasing from bottom to top.",
            "A new value pops every smaller value; for each, the new value is its next greater.",
            "Whatever stays on the stack has no greater element: answer -1.",
            "A map from value to answer handles `nums1` lookups in O(1).");
        P.pits("Nested loops are O(n·m).",
            "Values are distinct here; with duplicates you'd store indices, not values.");
        P.trace("nums1 = [4, 1, 2], nums2 = [1, 3, 4, 2]", () -> nextGreaterElement(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2}));
        P.edge("All decreasing", "nums1 = [3, 1], nums2 = [3, 2, 1]", "[-1, -1]", () -> nextGreaterElement(new int[]{3, 1}, new int[]{3, 2, 1}), "Nothing is ever popped.");
        P.edge("All increasing", "nums1 = [1, 2], nums2 = [1, 2, 3]", "[2, 3]", () -> nextGreaterElement(new int[]{1, 2}, new int[]{1, 2, 3}), "Each value is popped by the next one.");
        P.edge("Single element", "nums1 = [5], nums2 = [5]", "[-1]", () -> nextGreaterElement(new int[]{5}, new int[]{5}), "Nothing to its right.");
        P.edge("Answer is far away", "nums1 = [2], nums2 = [2, 1, 0, 0, 9]", "[9]", () -> nextGreaterElement(new int[]{2}, new int[]{2, 1, 0, 0, 9}), "2 waits on the stack until 9 arrives.");
        P.done();

        P.begin("Evaluate Reverse Polish Notation", "evalRPN");
        P.category("Stack · Expression evaluation");
        P.summary("Evaluate an expression written in postfix, like `2 1 + 3 *`. Push numbers; on an operator, pop the right operand, then the left one, apply the operator, and push the result. The last value on the stack is the answer.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Each token is pushed or popped a constant number of times.");
        P.ideas("Postfix needs no parentheses: an operator always applies to the two most recent values.",
            "Pop `b` first, then `a`, and compute `a op b`; order matters for `-` and `/`.",
            "Division truncates toward zero, which is exactly what Java's `/` does.");
        P.pits("Swapping operands: `4 13 5 / +` needs 13 / 5, not 5 / 13.",
            "Checking for operators with `token.charAt(0) == '-'` breaks on negative numbers like \"-11\".",
            "Comparing strings with `==` instead of `.equals`.");
        P.trace("tokens = [\"4\", \"13\", \"5\", \"/\", \"+\"]", () -> evalRPN(new String[]{"4", "13", "5", "/", "+"}));
        P.edge("Single number", "tokens = [\"42\"]", "42", () -> evalRPN(new String[]{"42"}), "Just pushed and returned.");
        P.edge("Negative numbers", "tokens = [\"-3\", \"4\", \"*\"]", "-12", () -> evalRPN(new String[]{"-3", "4", "*"}), "\"-3\" is a number, not the minus operator.");
        P.edge("Division truncates toward zero", "tokens = [\"-7\", \"2\", \"/\"]", "-3", () -> evalRPN(new String[]{"-7", "2", "/"}), "-3.5 truncates to -3, not -4.");
        P.edge("Long expression", "tokens = [\"10\", \"6\", \"9\", \"3\", \"+\", \"-11\", \"*\", \"/\", \"*\", \"17\", \"+\", \"5\", \"+\"]", "22", () -> evalRPN(new String[]{"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"}), "The LeetCode example.");
        P.done();

        P.begin("Daily Temperatures", "dailyTemperatures");
        P.category("Stack · Monotonic stack");
        P.summary("For each day, how many days until a warmer one? Keep a stack of day indices with decreasing temperatures. When a warmer day arrives, pop every cooler day: its answer is the distance to today.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Each index is pushed and popped once.");
        P.ideas("Store indices, not temperatures, so you can compute the distance.",
            "Days on the stack are still waiting for a warmer day.",
            "`ans[j] = i - j` when day i pops day j.",
            "Days left on the stack at the end keep the default 0.");
        P.pits("Popping on `>=` instead of `>`: equal temperatures are not warmer.",
            "Brute force O(n²) times out on 10⁵ days.");
        P.trace("temperatures = [73, 74, 75, 71, 69, 72, 76, 73]", () -> dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73}));
        P.edge("Strictly decreasing", "temperatures = [5, 4, 3]", "[0, 0, 0]", () -> dailyTemperatures(new int[]{5, 4, 3}), "No day ever gets warmer.");
        P.edge("Strictly increasing", "temperatures = [30, 40, 50, 60]", "[1, 1, 1, 0]", () -> dailyTemperatures(new int[]{30, 40, 50, 60}), "Every day pops the previous one.");
        P.edge("Equal temperatures", "temperatures = [70, 70, 71]", "[2, 1, 0]", () -> dailyTemperatures(new int[]{70, 70, 71}), "70 doesn't pop 70; both wait for 71.");
        P.edge("Single day", "temperatures = [50]", "[0]", () -> dailyTemperatures(new int[]{50}), "No future days.");
        P.done();

        P.begin("Asteroid Collision", "asteroidCollision");
        P.category("Stack · Simulation");
        P.summary("Positive asteroids move right, negative ones left; when they meet, the smaller explodes (both if equal). Use a stack of survivors: a left-moving asteroid fights right-movers on top of the stack until it dies, they all die, or no right-mover is left.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Each asteroid is pushed and popped at most once.");
        P.ideas("Only a right-mover (top, positive) followed by a left-mover (current, negative) can collide.",
            "While the top is smaller, it explodes: pop and keep fighting.",
            "Equal sizes: pop the top and the current one dies too.",
            "If the current asteroid survives, push it.");
        P.pits("Treating `[-2, 2]` as a collision; they move apart.",
            "Forgetting the equal-size case.",
            "Using an ArrayList and removing from the middle is O(n²).");
        P.trace("asteroids = [5, 10, -5, 3, -12, 4]", () -> asteroidCollision(new int[]{5, 10, -5, 3, -12, 4}));
        P.edge("Equal sizes", "asteroids = [8, -8]", "[]", () -> asteroidCollision(new int[]{8, -8}), "Both explode.");
        P.edge("Moving apart", "asteroids = [-2, -1, 1, 2]", "[-2, -1, 1, 2]", () -> asteroidCollision(new int[]{-2, -1, 1, 2}), "Left-movers on the left and right-movers on the right never meet.");
        P.edge("One big left-mover", "asteroids = [1, 2, 3, -10]", "[-10]", () -> asteroidCollision(new int[]{1, 2, 3, -10}), "-10 destroys all three.");
        P.edge("Left-mover dies", "asteroids = [10, 2, -5]", "[10]", () -> asteroidCollision(new int[]{10, 2, -5}), "-5 beats 2 but loses to 10.");
        P.done();

        P.begin("Largest Rectangle in Histogram", "largestRectangleArea");
        P.category("Stack · Monotonic stack");
        P.summary("Find the largest rectangle under a histogram. Keep a stack of bar indices with increasing heights. When a shorter bar arrives, each taller bar popped can extend no further right; its width runs from the new stack top + 1 to the current index − 1.");
        P.cx("O(n)", "O(n)", "O(n)", "O(n)", "Each bar is pushed and popped once.");
        P.ideas("For each bar, the best rectangle using its full height stretches until a shorter bar on each side.",
            "An increasing stack gives the left boundary (the element below it) for free.",
            "The bar that pops it is the right boundary.",
            "A sentinel height 0 at the end flushes the stack.");
        P.pits("Computing width as `i - popped` instead of `i - stack.peek() - 1`.",
            "Forgetting to flush the stack at the end.",
            "O(n²) expansion from each bar times out.");
        P.trace("heights = [2, 1, 5, 6, 2, 3]", () -> largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}));
        P.edge("Single bar", "heights = [4]", "4", () -> largestRectangleArea(new int[]{4}), "The sentinel pops it with width 1.");
        P.edge("All equal", "heights = [3, 3, 3]", "9", () -> largestRectangleArea(new int[]{3, 3, 3}), "Equal bars stay on the stack; the sentinel pops the first 3 last, and it spans all three bars.");
        P.edge("Increasing", "heights = [1, 2, 3, 4, 5]", "9", () -> largestRectangleArea(new int[]{1, 2, 3, 4, 5}), "3 × 3 (heights 3, 4, 5).");
        P.edge("Zero height bar", "heights = [2, 0, 2]", "2", () -> largestRectangleArea(new int[]{2, 0, 2}), "The 0 splits the histogram.");
        P.done();
    }

    //@code isValid
    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
                T.s("Push the opener", "'" + c + "' waits on the stack for its partner.", T.str("s", s).p("i", i).range(0, i - 1, "done").m(i, "active"), T.stack("stack", stack).m(stack.size() - 1, "swap"));
                continue;
            }
            char open = c == ')' ? '(' : c == ']' ? '[' : '{';
            if (stack.isEmpty() || stack.peek() != open) {
                T.n("Mismatch", stack.isEmpty() ? "'" + c + "' has nothing to close." : "'" + c + "' needs '" + open + "' but the top is '" + stack.peek() + "'.",
                    T.str("s", s).p("i", i).range(0, i - 1, "done").m(i, "error"), T.stack("stack", stack).m(stack.size() - 1, "error"));
                return false;
            }
            stack.pop();
            T.s("Matched, pop", "'" + c + "' closes the '" + open + "' on top.", T.str("s", s).p("i", i).range(0, i, "done"), T.stack("stack", stack));
        }
        T.n("End of string", stack.isEmpty() ? "The stack is empty: every bracket was matched." : stack.size() + " opener(s) were never closed.",
            T.str("s", s).range(0, s.length() - 1, stack.isEmpty() ? "found" : "dim"), T.stack("stack", stack));
        return stack.isEmpty();
    }
    //@end

    //@code MinStack
    static class MinStack {
        private final Deque<Integer> stack = new ArrayDeque<>();
        private final Deque<Integer> mins = new ArrayDeque<>();

        void push(int x) {
            stack.push(x);
            mins.push(mins.isEmpty() ? x : Math.min(x, mins.peek()));
            T.s("push(" + x + ")", "Push " + x + ". The minimum at this level is " + mins.peek() + ".", T.stack("stack", stack).m(stack.size() - 1, "swap"), T.stack("mins", mins).m(mins.size() - 1, "swap"));
        }

        void pop() {
            T.n("pop()", "Remove " + stack.peek() + " and its minimum together.", T.stack("stack", stack).m(stack.size() - 1, "dim"), T.stack("mins", mins).m(mins.size() - 1, "dim"));
            stack.pop();
            mins.pop();
        }

        int top() {
            T.n("top()", "The top is " + stack.peek() + ".", T.stack("stack", stack).m(stack.size() - 1, "found"), T.stack("mins", mins));
            return stack.peek();
        }

        int getMin() {
            T.n("getMin()", "The top of `mins` is the minimum of the whole stack: " + mins.peek() + ".", T.stack("stack", stack), T.stack("mins", mins).m(mins.size() - 1, "found"));
            return mins.peek();
        }
    }
    //@end

    //@code nextGreaterElement
    static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> next = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < nums2.length; i++) {
            int x = nums2[i];
            while (!stack.isEmpty() && stack.peek() < x) {
                next.put(stack.pop(), x);
                T.s("Pop a smaller value", x + " is the next greater element for the value just popped.", T.arr("nums2", nums2).p("i", i).m(i, "active"), T.stack("stack", stack), T.map("next", next).m(nextKey(nums2, next, x), "swap"));
            }
            stack.push(x);
            T.s("Push " + x, x + " waits for something bigger. The stack stays decreasing.", T.arr("nums2", nums2).p("i", i).range(0, i - 1, "visit").m(i, "active"), T.stack("stack", stack).m(stack.size() - 1, "swap"), T.map("next", next));
        }
        int[] res = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) res[i] = next.getOrDefault(nums1[i], -1);
        T.s("Look up nums1", "Values still on the stack have no greater element: -1.", T.arr("nums1", nums1), T.map("next", next), T.arr("res", res).range(0, res.length - 1, "found"));
        return res;
    }
    //@end
    static Integer nextKey(int[] a, Map<Integer, Integer> m, int x) { Integer k = null; for (Map.Entry<Integer, Integer> e : m.entrySet()) if (e.getValue() == x) k = e.getKey(); return k; }

    //@code evalRPN
    static int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < tokens.length; i++) {
            String t = tokens[i];
            if (t.equals("+") || t.equals("-") || t.equals("*") || t.equals("/")) {
                int b = stack.pop(), a = stack.pop();
                int r = switch (t) {
                    case "+" -> a + b;
                    case "-" -> a - b;
                    case "*" -> a * b;
                    default -> a / b;
                };
                stack.push(r);
                T.s("Apply " + t, "Pop " + b + " then " + a + ", compute `" + a + " " + t + " " + b + " = " + r + "`, push it.", T.arr("tokens", tokens).p("i", i).range(0, i - 1, "done").m(i, "active"), T.stack("stack", stack).m(stack.size() - 1, "swap"), T.v("a", a, "b", b, "result", r));
            } else {
                stack.push(Integer.parseInt(t));
                T.s("Push the number", t + " is an operand.", T.arr("tokens", tokens).p("i", i).range(0, i - 1, "done").m(i, "active"), T.stack("stack", stack).m(stack.size() - 1, "swap"));
            }
        }
        T.n("Result", "One value is left: " + stack.peek() + ".", T.stack("stack", stack).m(0, "found"));
        return stack.pop();
    }
    //@end

    //@code dailyTemperatures
    static int[] dailyTemperatures(int[] temps) {
        int[] ans = new int[temps.length];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < temps.length; i++) {
            while (!stack.isEmpty() && temps[stack.peek()] < temps[i]) {
                int j = stack.pop();
                ans[j] = i - j;
                T.s("Warmer day found", "Day " + i + " (" + temps[i] + "°) is warmer than day " + j + " (" + temps[j] + "°): wait " + ans[j] + " day(s).",
                    T.arr("temps", temps).p("i", i).p("j", j).m(i, "active").m(j, "done"), T.stack("stack (indices)", stack), T.arr("ans", ans).m(j, "swap"));
            }
            stack.push(i);
            T.s("Day " + i + " waits", "Push index " + i + ". Temperatures on the stack are decreasing.", T.arr("temps", temps).p("i", i).m(i, "visit"), T.stack("stack (indices)", stack).m(stack.size() - 1, "swap"), T.arr("ans", ans));
        }
        return ans;
    }
    //@end

    //@code asteroidCollision
    static int[] asteroidCollision(int[] asteroids) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < asteroids.length; i++) {
            int a = asteroids[i];
            boolean alive = true;
            while (alive && a < 0 && !stack.isEmpty() && stack.peek() > 0) {
                int top = stack.peek();
                if (top < -a) {
                    stack.pop();
                    T.s("Top explodes", top + " meets " + a + ": " + top + " is smaller and explodes. " + a + " keeps going.", T.arr("asteroids", asteroids).p("i", i).m(i, "active"), T.stack("stack", stack), T.v("a", a, "top", top));
                    continue;
                }
                if (top == -a) {
                    stack.pop();
                    T.s("Both explode", top + " and " + a + " are the same size.", T.arr("asteroids", asteroids).p("i", i).m(i, "error"), T.stack("stack", stack), T.v("a", a, "top", top));
                }
                alive = false;
                T.s(a + " is destroyed", top == -a ? "Neither survives." : top + " is bigger, so " + a + " explodes.", T.arr("asteroids", asteroids).p("i", i).m(i, "dim"), T.stack("stack", stack));
            }
            if (alive) {
                stack.push(a);
                T.s("Survives", a + (a > 0 ? " moves right; push it." : " meets no right-mover; push it."), T.arr("asteroids", asteroids).p("i", i).m(i, "visit"), T.stack("stack", stack).m(stack.size() - 1, "swap"));
            }
        }
        int[] res = new int[stack.size()];
        for (int i = res.length - 1; i >= 0; i--) res[i] = stack.pop();
        return res;
    }
    //@end

    //@code largestRectangleArea
    static int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0, n = heights.length;
        for (int i = 0; i <= n; i++) {
            int h = i == n ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] > h) {
                int height = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                int width = i - left - 1;
                best = Math.max(best, height * width);
                T.s("Pop and measure", "Bar of height " + height + " can't extend past index " + i + ". It spans " + (left + 1) + ".." + (i - 1) + ": area " + height + " × " + width + " = " + (height * width) + ". Best: " + best + ".",
                    T.arr("heights", heights).bars().p("i", Math.min(i, n)).range(left + 1, i - 1, "found"), T.stack("stack (indices)", stack), T.v("i", i, "height", height, "width", width, "best", best));
            }
            stack.push(i);
            T.s(i < n ? "Push bar " + i : "Sentinel flushes the stack", i < n ? "Heights on the stack stay increasing." : "The extra height-0 bar at index n popped everything that was left.", T.arr("heights", heights).bars().p("i", i).m(i, "active"), T.stack("stack (indices)", stack).m(stack.size() - 1, "swap"), T.v("i", i, "best", best));
        }
        return best;
    }
    //@end
}
