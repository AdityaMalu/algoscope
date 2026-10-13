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
