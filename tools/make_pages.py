"""Static, crawlable pages for search engines and link previews.

The app itself is one page whose content is drawn by JavaScript, so search engines see almost nothing.
This writes a plain HTML page per problem (summary, complexity, key ideas, pitfalls, edge cases and the
solution in three languages) that links into the animated trace, plus a problem index, sitemap.xml
and 404.html. (A project site can't serve robots.txt; submit the sitemap in Google Search Console instead.) Called by `python tools/make_site.py --out DIR`.
"""
import html, json, os, re
from datetime import date

SITE = "https://adityamalu.github.io/algoscope/"
root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

CSS = """
:root{--paper:#eef2f5;--surface:#fff;--sunk:#f4f7f9;--ink:#15202b;--ink-2:#46535f;--ink-3:#7b8794;--line:#d8e0e7;--accent:#0b6aa8;--accent-ink:#fff;--ok:#14805e;--ok-bg:#ddf3ea;--warn:#9a5b00;--warn-bg:#fdf0d5;--bad:#b4233a;--bad-bg:#fbe1e5}
@media (prefers-color-scheme:dark){:root{--paper:#0f151b;--surface:#161e26;--sunk:#1b2530;--ink:#e3eaf0;--ink-2:#b3c0cb;--ink-3:#8797a6;--line:#2a3642;--accent:#5cb3ef;--accent-ink:#0f151b;--ok:#5fd7a8;--ok-bg:#163a30;--warn:#f3c26b;--warn-bg:#3d3018;--bad:#ff8b9b;--bad-bg:#43202a;color-scheme:dark}}
*{box-sizing:border-box}
body{margin:0;background:var(--paper);color:var(--ink);font:16px/1.6 "IBM Plex Sans","Segoe UI",system-ui,sans-serif}
main{max-width:860px;margin:0 auto;padding:20px 16px 64px}
a{color:var(--accent)}
nav.top{display:flex;gap:14px;align-items:center;font-size:14px;margin-bottom:18px}
nav.top .brand{font:700 18px "Bricolage Grotesque","Segoe UI",system-ui,sans-serif;color:var(--ink);text-decoration:none;margin-right:auto}
h1{font:700 clamp(26px,5vw,36px)/1.15 "Bricolage Grotesque","Segoe UI",system-ui,sans-serif;margin:0 0 8px}
h2{font:700 20px "Bricolage Grotesque","Segoe UI",system-ui,sans-serif;margin:28px 0 8px}
.meta{display:flex;flex-wrap:wrap;gap:8px;margin-bottom:14px}
.pill{font-size:12px;font-weight:600;letter-spacing:.04em;text-transform:uppercase;padding:3px 9px;border-radius:99px;background:var(--sunk);color:var(--ink-2)}
.easy{background:var(--ok-bg);color:var(--ok)} .medium{background:var(--warn-bg);color:var(--warn)} .hard{background:var(--bad-bg);color:var(--bad)}
.cta{display:inline-block;margin:6px 0 4px;padding:11px 18px;border-radius:10px;background:var(--accent);color:var(--accent-ink);font-weight:600;text-decoration:none}
.cx{display:grid;grid-template-columns:repeat(4,minmax(0,1fr));gap:8px}
.cx div{background:var(--surface);border:1px solid var(--line);border-radius:10px;padding:8px 10px}
.cx span{display:block;font-size:12px;color:var(--ink-3);text-transform:uppercase;letter-spacing:.04em}
.cx b{font-family:"JetBrains Mono",ui-monospace,Consolas,monospace;font-size:15px}
code,pre{font-family:"JetBrains Mono",ui-monospace,"Cascadia Code",Consolas,monospace;font-size:.88em}
p code,li code,td code{background:var(--sunk);padding:1px 5px;border-radius:5px}
pre{background:var(--surface);border:1px solid var(--line);border-radius:10px;padding:12px 14px;overflow-x:auto;line-height:1.5}
details{margin:8px 0} summary{cursor:pointer;font-weight:600}
table{width:100%;border-collapse:collapse;font-size:14px}
th,td{text-align:left;vertical-align:top;padding:8px;border-bottom:1px solid var(--line)}
.muted{color:var(--ink-3);font-size:14px}
.pager{display:flex;justify-content:space-between;gap:12px;margin-top:32px;font-size:14px}
ul.plist{list-style:none;padding:0;margin:0;columns:2 280px}
ul.plist li{break-inside:avoid;padding:3px 0}
@media (max-width:560px){.cx{grid-template-columns:repeat(2,minmax(0,1fr))}}
"""

FONTS = ('<link rel="preconnect" href="https://fonts.googleapis.com">\n'
         '<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Bricolage+Grotesque:opsz,wght@12..96,700'
         '&family=IBM+Plex+Sans:wght@400;600&family=JetBrains+Mono:wght@400&display=swap">')
CSP = ("default-src 'none'; style-src 'unsafe-inline' https://fonts.googleapis.com; font-src https://fonts.gstatic.com; "
       "img-src 'self'; base-uri 'none'; form-action 'none'")


def e(s):
    return html.escape(str(s), quote=True)


def inline(s):
    """Escape, then turn `code` into <code>."""
    return re.sub(r"`([^`]+)`", r"<code>\1</code>", e(s))


def plain(s):
    return re.sub(r"\s+", " ", str(s).replace("`", "")).strip()


def clip(s, n):
    s = plain(s)
    return s if len(s) <= n else s[: n - 1].rsplit(" ", 1)[0] + "…"


def page(title, desc, path, body, extra_head="", base=None):
    url = SITE + path
    up = base or "../" * path.count("/")
    return f"""<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta http-equiv="Content-Security-Policy" content="{CSP}">
<title>{e(title)}</title>
<meta name="description" content="{e(desc)}">
<link rel="canonical" href="{e(url)}">
<meta property="og:type" content="article">
<meta property="og:site_name" content="AlgoScope">
<meta property="og:title" content="{e(title)}">
<meta property="og:description" content="{e(desc)}">
<meta property="og:url" content="{e(url)}">
<meta property="og:image" content="{SITE}assets/og.png">
<meta name="twitter:card" content="summary_large_image">
{FONTS}
<style>{CSS}</style>
{extra_head}</head>
<body>
<main>
<nav class="top"><a class="brand" href="{up}">AlgoScope</a><a href="{up}problems/">All problems</a></nav>
{body}
</main>
</body>
</html>
"""


def problem_page(p, prev, nxt):
    pid, title = p["id"], p["title"]
    path = f"problems/{pid}/"
    app = f"../../#{pid}"
    cx = p.get("complexity") or {}
    desc = clip(f"{title} ({p['level']}, {p['topic']}): {p.get('summary', '')}", 155)
    langs = [("Java", p["code"])] + [(k, v["code"]) for k, v in (p.get("alt") or {}).items()]
    code = "".join(f'<details{" open" if i == 0 else ""}><summary>{e(lang)}</summary><pre><code>{e(src)}</code></pre></details>'
                   for i, (lang, src) in enumerate(langs))
    edges = "".join(
        f"<tr><td><b>{e(x.get('name', ''))}</b><br><span class='muted'>{inline(x.get('why', ''))}</span></td>"
        f"<td><code>{e(x.get('input', ''))}</code></td><td><code>{e(x.get('expected', x.get('actual', '')))}</code></td></tr>"
        for x in p.get("edgeCases") or [])
    ld = {"@context": "https://schema.org", "@type": "LearningResource", "name": f"{title}: step-by-step trace",
          "description": desc, "url": SITE + path, "learningResourceType": "interactive visualization",
          "educationalLevel": p["level"], "about": p["topic"], "inLanguage": "en",
          "isPartOf": {"@type": "WebSite", "name": "AlgoScope", "url": SITE}}
    body = f"""<h1>{e(title)}</h1>
<div class="meta"><span class="pill {e(p['level'])}">{e(p['level'])}</span><span class="pill">{e(p['topic'])}</span><span class="pill">{len(p['steps'])} steps</span><span class="pill">Java · Python · C++</span></div>
<p>{inline(p.get('summary', ''))}</p>
<a class="cta" href="{e(app)}">▶ Watch the animated trace</a>
<p class="muted">Traced input: <code>{e(p.get('input', ''))}</code> → <code>{e(p.get('output', ''))}</code>. Original problem: <a href="{e(p.get('url', ''))}" rel="noopener">{e(p.get('url', '').split('//')[-1])}</a></p>
<h2>Complexity</h2>
<div class="cx"><div><span>Best</span><b>{e(cx.get('best', '–'))}</b></div><div><span>Average</span><b>{e(cx.get('average', '–'))}</b></div><div><span>Worst</span><b>{e(cx.get('worst', '–'))}</b></div><div><span>Space</span><b>{e(cx.get('space', '–'))}</b></div></div>
{f"<p class='muted'>{inline(cx['note'])}</p>" if cx.get('note') else ""}
<h2>Key idea</h2>
<ul>{"".join(f"<li>{inline(x)}</li>" for x in p.get('intuition') or [])}</ul>
<h2>Common mistakes</h2>
<ul>{"".join(f"<li>{inline(x)}</li>" for x in p.get('pitfalls') or [])}</ul>
<h2>Solution</h2>
{code}
<h2>Edge cases</h2>
<p class="muted">Each one has its own trace in the <a href="{e(app)}">interactive view</a>.</p>
<table><tr><th>Case</th><th>Input</th><th>Answer</th></tr>{edges}</table>
<div class="pager"><span>{f'← <a href="../{e(prev["id"])}/">{e(prev["title"])}</a>' if prev else ''}</span><span>{f'<a href="../{e(nxt["id"])}/">{e(nxt["title"])}</a> →' if nxt else ''}</span></div>
"""
    ld_json = json.dumps(ld, ensure_ascii=False).replace("</", "<\\/")
    head = f'<script type="application/ld+json">{ld_json}</script>\n'
    return page(f"{title}: step-by-step visual trace · AlgoScope", desc, path, body, head)


def index_page(items):
    topics = {}
    for p in items:
        topics.setdefault(p["topic"], []).append(p)
    body = "<h1>All problems</h1>\n<p>Every problem has an animated, line-by-line trace in Java, Python and C++, with complexity, the key idea, common mistakes and edge cases.</p>\n"
    for t, ps in topics.items():
        body += f"<h2>{e(t)}</h2>\n<ul class='plist'>" + "".join(
            f"<li><a href='{e(p['id'])}/'>{e(p['title'])}</a> <span class='pill {e(p['level'])}'>{e(p['level'])}</span></li>" for p in ps) + "</ul>\n"
    desc = f"{len(items)} DSA interview problems with step-by-step animated traces in Java, Python and C++."
    return page("All problems · AlgoScope", desc, "problems/", body)


def not_found():
    body = '<h1>Page not found</h1>\n<p>That page doesn\'t exist. <a href="/algoscope/">Go to AlgoScope</a> or <a href="/algoscope/problems/">see all problems</a>.</p>'
    return page("Not found · AlgoScope", "Page not found.", "404.html", body, base="/algoscope/")


def build(out, write):
    data = os.path.join(root, "data")
    index = [x for x in json.load(open(os.path.join(data, "index.json"), encoding="utf-8")) if x.get("file")]
    files = {}
    items = []
    for x in index:
        if x["file"] not in files:
            files[x["file"]] = {p["id"]: p for p in json.load(open(os.path.join(data, x["file"]), encoding="utf-8"))}
        p = dict(files[x["file"]][x["id"]])
        p.setdefault("url", x.get("url", ""))
        items.append(p)
    for i, p in enumerate(items):
        write(os.path.join(out, "problems", p["id"], "index.html"), problem_page(p, items[i - 1] if i else None, items[i + 1] if i + 1 < len(items) else None))
    write(os.path.join(out, "problems", "index.html"), index_page(items))
    write(os.path.join(out, "404.html"), not_found())
    today = date.today().isoformat()
    urls = [SITE, SITE + "problems/"] + [SITE + f"problems/{p['id']}/" for p in items]
    write(os.path.join(out, "sitemap.xml"), '<?xml version="1.0" encoding="UTF-8"?>\n<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n'
          + "".join(f"  <url><loc>{u}</loc><lastmod>{today}</lastmod></url>\n" for u in urls) + "</urlset>\n")
    return len(items)
