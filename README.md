# AlgoScope

Step-by-step, animated traces of 159 DSA interview problems, in **Java, Python and C++**. Each trace shows the array, pointers, tree, graph, stack or DP table changing line by line, with complexity, the key idea, common pitfalls, and edge cases you can trace too.

**Live site:** https://adityamalu.github.io/algoscope/ · [all problems as plain pages](https://adityamalu.github.io/algoscope/problems/)

- The 159 problems (an Amazon prep sheet: arrays, two pointers, sliding window, binary search, stacks, linked lists, trees, heaps, graphs, backtracking, DP, greedy, tries, design) are **pre-computed**. They need no account, no API key and no AI.
- Traces come from **actually running** the Java solutions with a small tracer, so every step is real program state, not a guess.
- The Python and C++ solutions were run on the same inputs and checked against Java: 693 of 693 test cases match in each language.
- Share a moment of a trace: links like `#two-sum/4` open a problem at step 4 (**Copy link to this step** under the step counter).
- Every problem also has a plain page (`/problems/two-sum/`) with the summary, complexity, key idea, mistakes, edge cases and code, so search engines and link previews can see it.
- **Paste your own Python** and it is actually run in your browser with [Pyodide](https://pyodide.org/) (Python compiled to WebAssembly). Every line is recorded, arrays, maps, linked lists, trees and the call stack are drawn from the real values, and common edge cases (empty input, one element, duplicates, …) are run too. No key or account needed.
- **Paste code in any other language** and get an AI-generated trace. That part needs a free OpenRouter key (see below). AI traces are checked before drawing and labelled *AI-generated*, because the model writes them without running the code. With a key, Python runs also get an AI overview and AI-picked edge cases, whose results come from running the code.

## Use AI for your own code (free OpenRouter key)

The site never ships with a key. Each person adds their own, and it stays in their browser.

### 1. Get a free key (about a minute, no card)

1. Go to [openrouter.ai](https://openrouter.ai/) and sign in with Google or GitHub.
2. Open **[Settings → API Keys](https://openrouter.ai/settings/keys)** and press **Create key**. Name it something like `AlgoScope`.
3. Copy the key. It starts with `sk-or-v1-` and is shown only once.

### 2. Put it in AlgoScope

1. Open the [live site](https://adityamalu.github.io/algoscope/).
2. Press **⚙ AI settings** in the top-right corner.
3. Paste the key into **OpenRouter API key**.
4. Pick a model from **Free model**. The list is loaded live from OpenRouter and only shows `:free` models, largest first.
5. Press **Save**. The status in the corner changes to `OpenRouter · <model>`.

Now paste code in the editor and press **Visualize**, or type a question in **Ask about this code**.

### Where the key is stored

- Only in your browser: for this tab only (`sessionStorage`), or on this device (`localStorage`) if you tick **Remember the key on this device**.
- To limit damage if a key ever leaks, set a credit limit of $0 on it at OpenRouter; free models still work.
- It is sent only to `https://openrouter.ai/api/v1/chat/completions`, straight from your browser.
- It is **never** written to this repository or sent anywhere else. Press **Remove key** in AI settings to delete it.
- Don't paste your key into a GitHub issue, a commit or a shared screenshot. If that happens, delete the key on OpenRouter and create a new one.

### Free limits

| Limit | Free OpenRouter models |
|---|---|
| Requests per minute | about 20 |
| Requests per day | about 50 (about 1,000 once the account has ever bought $10 of credits) |

One **Visualize** is one request (roughly 4,000–8,000 tokens). One **Ask** is one request (about 1,000 tokens). Limits are per account, so extra keys don't add capacity. Free models are less accurate than paid ones, so double-check a trace that looks surprising.

## How it's checked

Every push runs [CI](.github/workflows/ci.yml): it rebuilds all traces from the Java sources, checks them, cross-checks the Python and C++ solutions against Java, runs the tracer and browser tests, and fails if the committed `data/` differs from a fresh build. Only a green run deploys to GitHub Pages.

The page ships with a Content-Security-Policy (scripts only from itself, by hash, and cdnjs with an integrity hash; network calls only to openrouter.ai), so a script injected through an AI answer can't run or read your key.

To get the problem pages into Google, add the site in [Search Console](https://search.google.com/search-console) and submit `https://adityamalu.github.io/algoscope/sitemap.xml`.

## Run it locally

No build is needed to view it:

```bash
python -m http.server 8765
```

Then open http://localhost:8765.

## Rebuild the traces

Requirements: Java 21+, Python 3.10+, and g++ (C++17) for the cross-language check.

| Command | What it does |
|---|---|
| `python build.py` | Compiles and runs every Java solution with the tracer and writes `data/*.json` |
| `python build.py Hashing` | Rebuilds one topic (class names are in `src/Main.java`) |
| `python tools/verify.py` | Runs the Python and C++ solutions on the same inputs and compares them with Java |
| `python tools/listing.py arrays-hashing` | Prints a topic's Java code with the lines that trace steps point at |
| `python tools/check_data.py` | Checks every trace in `data/` (lines inside the code, known shapes, language maps) |
| `python tools/make_site.py` | Regenerates `index.html` from `app.html` (adds the Content-Security-Policy) |
| `python tools/make_site.py --out _site` | Also assembles the full site that GitHub Pages serves: the app, a page per problem, `sitemap.xml` and `404.html` |
| `python tools/og_image.py` | Re-renders `assets/og.png`, the link-preview image (needs Playwright) |
| `pytest` | Tracer tests and browser tests (`pip install -r requirements-dev.txt`, then `playwright install chromium`) |

### Layout

```
app.html            page source (also published as a claude.ai artifact)
index.html          generated full page for GitHub Pages
data/               pre-computed traces, one JSON file per topic, plus index.json
src/                Java solutions + tracer (T.java), registry (P.java), formatting (F.java)
alt/py, alt/cpp     Python and C++ solutions; "# @12" / "// @12" tags map a line to Java line 12
alt/spec.py         how to call each solution when cross-checking
build.py            runs the Java tracer and writes data/
runner/             pytrace.py (records real Python runs) and py-worker.js (runs it with Pyodide in a Web Worker)
tests/              pytest: tracer tests and Playwright browser tests
tools/              verify.py, check_data.py, diff_data.py, listing.py, make_site.py, make_pages.py, og_image.py
assets/             site icon and link-preview image
.github/workflows/  CI: rebuild traces, cross-check languages, run tests, deploy to Pages
sheet.json          the problem list (names, levels, topics, links)
```

Inside a Java solution, code between `//@code <name>` and `//@end` is what the page shows. Tracer calls (`T.s(...)`, `T.n(...)`) and lines ending in `// @hide` are stripped from the display.

## Credits

Problem names and links point to [NeetCode](https://neetcode.io/) and [LeetCode](https://leetcode.com/). Problem statements are not copied here; solutions, explanations and traces are original.

## License

[MIT](LICENSE)
