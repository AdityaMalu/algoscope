"""Browser tests for the built site (pytest + Playwright).

Run: pip install -r requirements-dev.txt && playwright install chromium
     python tools/make_site.py --out _site && pytest
"""
import json, re

from playwright.sync_api import expect


def test_home_lists_every_problem(app):
    expect(app.locator("#factCount")).to_have_text("159")
    assert app.locator("#homeTopics .prow").count() == 159
    assert not app.locator("#homeView").is_hidden()


def test_home_search_filters(app):
    app.fill("#homeSearch", "two sum")
    expect(app.locator("#homeTopics .prow").first).to_contain_text("Two Sum")
    assert app.locator("#homeTopics .prow").count() < 10


def test_open_problem_and_step(app):
    app.click("#homeTopics .prow >> text=Two Sum")
    expect(app).to_have_url(re.compile(r"#two-sum$"))
    expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of \d+"))
    app.click("#bNext")
    expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 2 of \d+"))
    expect(app.locator("#stage .ds").first).to_be_visible()
    app.click("#bLast")
    total = app.locator("#stepCount").inner_text().split()[-1]
    expect(app.locator("#stepCount")).to_have_text(f"Step {total} of {total}")


def test_back_to_home(app):
    app.goto(app.url.split("#")[0] + "#valid-anagram")
    expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of"))
    app.click(".home-btn")
    expect(app.locator("#homeTopics .prow").first).to_be_visible()
    expect(app.locator("#homeTopics .prow.seen")).to_have_count(1)


def test_language_switch(app):
    app.goto(app.url.split("#")[0] + "#two-sum")
    expect(app.locator("#trace")).to_contain_text("HashMap")
    app.select_option("#lang", "Python")
    expect(app.locator("#trace")).to_contain_text("def ")
    expect(app.locator("#stepLine")).to_have_text(re.compile(r"line \d+"))
    app.select_option("#lang", "C++")
    expect(app.locator("#trace")).to_contain_text("unordered_map")


def test_edge_case_trace(app):
    app.goto(app.url.split("#")[0] + "#two-sum")
    btn = app.locator("#ecs [data-ec]").first
    expect(btn).to_be_visible()
    btn.click()
    expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of \d+"))


def test_every_problem_opens(app):
    """Open each problem once and check it renders a first step."""
    base = app.url.split("#")[0]
    ids = app.evaluate("() => [...document.querySelectorAll('#homeTopics .prow')].map(a => a.getAttribute('href').slice(1))")
    assert len(ids) == 159
    for pid in ids:
        app.goto(base + "#" + pid)
        expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of [1-9]"), timeout=5000)


def test_saved_trace_cannot_inject_html(page, site):
    """AI output is untrusted: a saved trace full of markup must render as text."""
    bad = "<img src=x onerror=\"window.__pwned=1\">"
    analysis = {
        "title": bad, "summary": bad, "category": bad, "language": "Python", "code": "x = 1\n" + bad,
        "complexity": {"best": bad, "average": bad, "worst": bad, "space": bad, "note": bad},
        "intuition": [bad], "pitfalls": [bad],
        "edgeCases": [{"name": bad, "input": bad, "expected": bad, "actual": bad, "status": bad, "why": bad}],
        "steps": [{"line": 1, "title": bad, "explain": bad, "vars": {bad: bad},
                   "structures": [{"kind": "array", "name": bad, "values": [bad], "pointers": {bad: 0}},
                                  {"kind": "map", "name": bad, "entries": [[bad, bad]]},
                                  {"kind": "tree", "name": bad, "nodes": [{"id": bad, "value": bad}]},
                                  {"kind": bad, "name": bad, "values": [bad]}]}],
    }
    page.goto(site + "#home")
    page.evaluate("v => localStorage.setItem('algoscope:v1', v)", json.dumps({"analysis": analysis, "code": analysis["code"]}))
    page.goto(site + "#code")
    page.reload()
    expect(page.locator("#stepTitle")).to_contain_text("<img")
    page.wait_for_timeout(300)
    assert page.evaluate("() => window.__pwned") is None
    assert page.locator("img[src=x]").count() == 0


def test_no_csp_violations(page, site):
    """The Content-Security-Policy must not block anything the page itself needs."""
    page.add_init_script("window.__csp = []; document.addEventListener('securitypolicyviolation', e => window.__csp.push(e.violatedDirective + ' ' + e.blockedURI))")
    page.goto(site + "#home")
    page.wait_for_selector("#homeTopics .prow")
    page.goto(site + "#binary-search")
    expect(page.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of"))
    page.click("#bPlay")
    page.wait_for_timeout(800)
    assert page.evaluate("() => window.__csp") == []
    assert page.evaluate("() => !!window.hljs"), "highlight.js failed to load (SRI or CSP)"


def test_ai_trace_is_checked(app):
    """Steps pointing outside the code lose their line; unknown shapes and junk steps are counted."""
    r = app.evaluate(r"""() => AlgoScope.normalize({steps: [{line: 99, title: "a"}, {line: 1, structures: [{kind: "weird"}, 7]}, 5]}, "x = 1\ny = 2", "auto")""")
    assert [s["line"] for s in r["steps"]] == [0, 1]
    assert r["issues"] == {"lines": 1, "shapes": 2, "dropped": 1}
    assert r["ai"] is True


def test_ai_badge_shown(page, site):
    analysis = {"title": "Mine", "summary": "", "code": "x = 1", "ai": True, "issues": {"lines": 2, "shapes": 0, "dropped": 0},
                "complexity": {"best": "-", "average": "-", "worst": "-", "space": "-"}, "intuition": [], "pitfalls": [],
                "edgeCases": [], "steps": [{"line": 1, "title": "t", "explain": "", "vars": {}, "structures": []}]}
    page.goto(site + "#home")
    page.evaluate("v => localStorage.setItem('algoscope:v1', v)", json.dumps({"analysis": analysis, "code": "x = 1"}))
    page.goto(site + "#code")
    page.reload()
    expect(page.locator("#overview .pill.warn")).to_contain_text("AI-generated")
    expect(page.locator("#overview")).to_contain_text("2 steps pointed at a line")


PY_CODE = """def two_sum(nums, target):
    seen = {}
    for i, x in enumerate(nums):
        if target - x in seen:
            return [seen[target - x], i]
        seen[x] = i
    return []
"""


def test_python_runs_for_real(app):
    """Pasted Python is executed with Pyodide; no AI key is needed."""
    app.goto(app.url.split("#")[0] + "#code")
    app.wait_for_selector("#editor", state="visible")
    app.fill("#editor", PY_CODE)
    app.fill("#inputBox", "[2, 7, 11, 15], 9")
    app.click("#runBtn")
    expect(app.locator("#overview .pill.free")).to_contain_text("Ran your code", timeout=120000)
    expect(app.locator("#overview")).to_contain_text("[0, 1]")
    expect(app.locator("#stepCount")).to_have_text("Step 1 of 8")
    app.click("#bLast")
    expect(app.locator("#stepTitle")).to_have_text("Return [0, 1]")
    # auto edge cases ran too; the empty list returns [] and has its own trace
    expect(app.locator("#ecs .ec")).to_have_count(5)
    app.locator("#ecs .ec", has_text="Empty `nums`").locator("[data-ec]").click()
    expect(app.locator("#edgeBar")).to_be_visible()
    app.click("#edgeBack")
    expect(app.locator("#stepCount")).to_have_text("Step 1 of 8")


def test_python_error_is_shown(app):
    app.goto(app.url.split("#")[0] + "#code")
    app.wait_for_selector("#editor", state="visible")
    app.fill("#editor", "def first(nums):\n    total = 0\n    return nums[5]\n")
    app.fill("#inputBox", "[1, 2]")
    app.click("#runBtn")
    expect(app.locator("#overview")).to_contain_text("IndexError", timeout=120000)
    app.click("#bLast")
    expect(app.locator("#stepLine")).to_have_text("line 3")


def test_step_links(app):
    app.goto(app.url.split("#")[0] + "#binary-search/3")
    expect(app.locator("#stepCount")).to_have_text(re.compile(r"Step 3 of"))
    expect(app.locator("#bLink")).to_be_visible()


def test_problem_pages(page, site):
    """The plain pages for search engines (built by `make_site.py --out`) exist and link into the app."""
    page.goto(site + "problems/")
    assert page.locator("ul.plist a").count() == 159
    page.click("text=Two Sum")
    expect(page.locator("h1")).to_have_text("Two Sum")
    assert page.locator("meta[property='og:title']").get_attribute("content").startswith("Two Sum")
    assert page.locator("pre code").count() == 3
    page.click("text=Watch the animated trace")
    expect(page.locator("#stepCount")).to_have_text(re.compile(r"Step 1 of"))


def test_sitemap(page, site):
    assert page.goto(site + "sitemap.xml").text().count("<loc>") == 161


# ---------- bring-your-own-key providers (requests are intercepted; no real keys or network) ----------

FAKE_TRACE = {"title": "Sum", "category": "Loop", "language": "Java", "summary": "Adds numbers.", "input": "[1, 2]", "output": "3",
              "complexity": {"best": "O(n)", "average": "O(n)", "worst": "O(n)", "space": "O(1)"},
              "intuition": ["Running total."], "pitfalls": ["Overflow."], "edgeCases": [],
              "steps": [{"line": 2, "title": "Start", "explain": "s = 0", "vars": {"s": 0}, "structures": []},
                        {"line": 3, "title": "Add", "explain": "s = 3", "vars": {"s": 3}, "structures": []}]}
JAVA = "int sum(int[] a) {\n    int s = 0;\n    for (int x : a) s += x;\n    return s;\n}\n"


def sse(events):
    return "".join(f"data: {json.dumps(e)}\n\n" for e in events)


def chunks(text, n=3):
    k = max(1, len(text) // n)
    return [text[i:i + k] for i in range(0, len(text), k)]


def setup_provider(page, site, provider, key, model):
    page.goto(site + "#home")
    page.wait_for_selector("#homeTopics .prow")
    page.click("#aiOpen")
    page.select_option("#aiProvider", provider)
    page.fill("#orKey", key)
    page.fill("#orModel", model)
    page.click("#aiSave")
    expect(page.locator("#aiMsg")).to_contain_text("Using")
    page.click("#aiClose")


def visualize_java(page, site):
    page.goto(site + "#code")
    page.wait_for_selector("#editor", state="visible")
    page.fill("#editor", JAVA)
    page.select_option("#lang", "Java")
    page.click("#runBtn")
    expect(page.locator("#overview h2")).to_have_text("Sum", timeout=15000)
    expect(page.locator("#overview .pill.warn")).to_contain_text("AI-generated")
    expect(page.locator("#stepCount")).to_have_text("Step 1 of 2")


def test_provider_list(app):
    app.click("#aiOpen")
    names = app.locator("#aiProvider option").all_inner_texts()
    for n in ["OpenRouter", "Anthropic (Claude)", "OpenAI", "Google Gemini", "Groq", "Mistral", "DeepSeek", "xAI (Grok)"]:
        assert n in names
    app.select_option("#aiProvider", "anthropic")
    expect(app.locator("#orKey")).to_have_attribute("placeholder", "sk-ant-…")
    expect(app.locator("#aiSteps")).to_contain_text("console.anthropic.com")
    app.fill("#orKey", "sk-wrong")
    app.fill("#orModel", "claude-opus-5-5")
    app.click("#aiSave")
    expect(app.locator("#aiMsg")).to_contain_text("usually start with sk-ant-")
    app.click("#aiSave")  # a second Save uses the key anyway
    expect(app.locator("#aiMsg")).to_contain_text("Using Anthropic")


def test_gemini_accepts_both_key_formats(app):
    app.click("#aiOpen")
    app.select_option("#aiProvider", "gemini")
    for key in ["AIzaTestKey", "AQ.TestKey"]:
        app.fill("#orKey", key)
        app.fill("#orModel", "gemini-flash-latest")
        app.click("#aiSave")
        expect(app.locator("#aiMsg")).to_contain_text("Using Google Gemini")


def test_anthropic_request(page, site):
    seen = {}

    def messages(route):
        seen["headers"] = route.request.headers
        seen["body"] = json.loads(route.request.post_data)
        text = json.dumps(FAKE_TRACE)
        events = [{"type": "message_start", "message": {"id": "m"}}, {"type": "content_block_start", "index": 0, "content_block": {"type": "text", "text": ""}}]
        events += [{"type": "content_block_delta", "index": 0, "delta": {"type": "text_delta", "text": c}} for c in chunks(text)]
        events += [{"type": "message_delta", "delta": {"stop_reason": "end_turn"}}, {"type": "message_stop"}]
        route.fulfill(status=200, headers={"content-type": "text/event-stream", "access-control-allow-origin": "*"}, body=sse(events))

    page.route("https://api.anthropic.com/v1/models**", lambda r: r.fulfill(json={"data": [{"id": "claude-opus-5-5"}, {"id": "claude-sonnet-5-5"}]}, headers={"access-control-allow-origin": "*"}))
    page.route("https://api.anthropic.com/v1/messages", messages)
    setup_provider(page, site, "anthropic", "sk-ant-test-key", "claude-opus-5-5")
    visualize_java(page, site)
    h, b = seen["headers"], seen["body"]
    assert h["x-api-key"] == "sk-ant-test-key" and h["anthropic-version"] == "2023-06-01"
    assert h["anthropic-dangerous-direct-browser-access"] == "true"
    assert h["anthropic-beta"] == "server-side-fallback-2026-07-01" and b["fallbacks"] == "default"
    assert b["model"] == "claude-opus-5-5" and b["stream"] is True and b["output_config"]["effort"] == "medium"


def test_gemini_request(page, site):
    seen = {}

    def gen(route):
        seen["url"], seen["headers"] = route.request.url, route.request.headers
        events = [{"candidates": [{"content": {"parts": [{"text": c}]}}]} for c in chunks(json.dumps(FAKE_TRACE))]
        route.fulfill(status=200, headers={"content-type": "text/event-stream", "access-control-allow-origin": "*"}, body=sse(events))

    page.route("https://generativelanguage.googleapis.com/v1beta/models?**", lambda r: r.fulfill(json={"models": [
        {"name": "models/gemini-test-flash", "supportedGenerationMethods": ["generateContent"]}]}, headers={"access-control-allow-origin": "*"}))
    page.route("https://generativelanguage.googleapis.com/v1beta/models/*:streamGenerateContent*", gen)
    setup_provider(page, site, "gemini", "AIzaTestKey", "gemini-test-flash")
    visualize_java(page, site)
    assert "gemini-test-flash:streamGenerateContent?alt=sse" in seen["url"] and seen["headers"]["x-goog-api-key"] == "AIzaTestKey"


def test_openai_compatible_request(page, site):
    seen = {}

    def chat(route):
        seen["headers"], seen["body"] = route.request.headers, json.loads(route.request.post_data)
        events = [{"choices": [{"delta": {"content": c}}]} for c in chunks(json.dumps(FAKE_TRACE))]
        route.fulfill(status=200, headers={"content-type": "text/event-stream", "access-control-allow-origin": "*"}, body=sse(events) + "data: [DONE]\n\n")

    page.route("https://api.openai.com/v1/models", lambda r: r.fulfill(json={"data": [{"id": "gpt-test"}, {"id": "text-embedding-3"}]}, headers={"access-control-allow-origin": "*"}))
    page.route("https://api.openai.com/v1/chat/completions", chat)
    setup_provider(page, site, "openai", "sk-test-openai", "gpt-test")
    visualize_java(page, site)
    assert seen["headers"]["authorization"] == "Bearer sk-test-openai"
    assert seen["body"]["model"] == "gpt-test" and "max_completion_tokens" in seen["body"]


def test_bad_key_message(page, site):
    page.route("https://api.groq.com/openai/v1/models", lambda r: r.fulfill(status=401, json={"error": {"message": "Invalid API Key"}}, headers={"access-control-allow-origin": "*"}))
    page.route("https://api.groq.com/openai/v1/chat/completions", lambda r: r.fulfill(status=401, json={"error": {"message": "Invalid API Key"}}, headers={"access-control-allow-origin": "*"}))
    setup_provider(page, site, "groq", "gsk_bad", "some-model")
    page.goto(site + "#code")
    page.wait_for_selector("#editor", state="visible")
    page.fill("#editor", JAVA)
    page.select_option("#lang", "Java")
    page.click("#runBtn")
    expect(page.locator("#err")).to_contain_text("rejected the key")


def test_old_openrouter_key_is_migrated(page, site):
    page.goto(site + "#home")
    page.evaluate("() => { localStorage.setItem('algoscope:openrouterKey', 'sk-or-v1-old'); localStorage.setItem('algoscope:openrouterModel', 'x/y:free'); }")
    page.reload()
    page.wait_for_selector("#homeTopics .prow")
    assert page.evaluate("() => localStorage.getItem('algoscope:key:openrouter')") == "sk-or-v1-old"
    expect(page.locator("#statusText")).to_contain_text("OpenRouter · x/y")


def test_switching_provider_ignores_stale_model_list(page, site):
    def slow(route):
        page.wait_for_timeout(700)
        route.fulfill(json={"data": [{"id": "vendor/slow:free"}]}, headers={"access-control-allow-origin": "*"})
    page.route("https://openrouter.ai/api/v1/models", slow)
    page.goto(site + "#home")
    page.wait_for_selector("#homeTopics .prow")
    page.click("#aiOpen")
    page.select_option("#aiProvider", "gemini")
    page.wait_for_timeout(1200)
    assert "slow" not in page.input_value("#orModel")
    expect(page.locator("#orModelHint")).to_contain_text("Add a key")


def test_long_complexity_wraps(page, site):
    analysis = dict(FAKE_TRACE, code=JAVA, ai=True, complexity={"best": "O(n)", "average": "O(n log(n)) (but non-terminating for many inputs)",
                                                              "worst": "O(∞) - loops forever", "space": "O(1)"})
    page.goto(site + "#home")
    page.evaluate("v => localStorage.setItem('algoscope:v1', v)", json.dumps({"analysis": analysis, "code": JAVA}))
    page.goto(site + "#code")
    page.reload()
    cells = page.locator("#overview .cx > div")
    expect(cells.nth(1).locator("b")).to_have_text("O(n log(n))")
    expect(cells.nth(1).locator("small")).to_have_text("but non-terminating for many inputs")
    expect(cells.nth(2).locator("small")).to_have_text("loops forever")
    for i in range(4):  # nothing spills out of its box
        assert page.evaluate("el => el.scrollWidth <= el.clientWidth + 1", cells.nth(i).element_handle())


def test_retired_model_switches_to_suggestion(page, site):
    calls = []

    def gen(route):
        calls.append(route.request.url)
        if "gemini-old" in route.request.url:
            route.fulfill(status=404, json={"error": {"message": "This model models/gemini-old is no longer available to new users. Please update your code to use models/gemini-new-lite for the latest features."}},
                          headers={"access-control-allow-origin": "*"})
        else:
            events = [{"candidates": [{"content": {"parts": [{"text": c}]}}]} for c in chunks(json.dumps(FAKE_TRACE))]
            route.fulfill(status=200, headers={"content-type": "text/event-stream", "access-control-allow-origin": "*"}, body=sse(events))

    page.route("https://generativelanguage.googleapis.com/v1beta/models?**", lambda r: r.fulfill(json={"models": [
        {"name": "models/gemini-old", "supportedGenerationMethods": ["generateContent"]}]}, headers={"access-control-allow-origin": "*"}))
    page.route("https://generativelanguage.googleapis.com/v1beta/models/*:streamGenerateContent*", gen)
    setup_provider(page, site, "gemini", "AQ.test", "gemini-old")
    visualize_java(page, site)
    assert "gemini-new-lite" in calls[-1]
    expect(page.locator("#err")).to_contain_text("switched to gemini-new-lite")
    expect(page.locator("#statusText")).to_contain_text("gemini-new-lite")


def test_gemini_models_ranked_newest_first(page, site):
    page.route("https://generativelanguage.googleapis.com/v1beta/models?**", lambda r: r.fulfill(json={"models": [
        {"name": f"models/{n}", "supportedGenerationMethods": ["generateContent"]} for n in
        ["gemini-2.5-flash-lite", "gemini-3.5-flash", "gemini-flash-latest", "gemini-2.0-flash", "gemini-3.5-pro-preview"]]},
        headers={"access-control-allow-origin": "*"}))
    page.goto(site + "#home")
    page.wait_for_selector("#homeTopics .prow")
    page.click("#aiOpen")
    page.select_option("#aiProvider", "gemini")
    page.fill("#orKey", "AQ.test")
    page.dispatch_event("#orKey", "change")
    expect(page.locator("#orModel")).to_have_value("gemini-flash-latest")
    order = page.evaluate("() => [...document.querySelectorAll('#orModels option')].map(o => o.value)")
    assert order.index("gemini-3.5-flash") < order.index("gemini-2.5-flash-lite") < order.index("gemini-2.0-flash")
