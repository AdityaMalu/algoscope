"""Render assets/og.png, the 1200x630 picture shown when an AlgoScope link is shared.

Needs Playwright (pip install -r requirements-dev.txt && playwright install chromium).
Usage: python tools/og_image.py
"""
import os

from playwright.sync_api import sync_playwright

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

CELLS = [(1, ""), (3, ""), (5, "lo"), (8, ""), (13, "mid"), (21, ""), (34, "hi")]
cells = "".join(
    f'<div class="c"><div class="b{" on" if p == "mid" else " in" if p else ""}">{v}</div><div class="p">{p}</div></div>' for v, p in CELLS)

HTML = f"""<!doctype html><html><head><meta charset="utf-8">
<link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Familjen+Grotesk:wght@700&family=IBM+Plex+Sans:wght@400;600&family=JetBrains+Mono:wght@500;700&display=swap">
<style>
body{{margin:0;width:1200px;height:630px;background:#0f1424;color:#e6eaf5;font-family:"IBM Plex Sans",sans-serif;display:flex;flex-direction:column;justify-content:center;padding:0 90px;box-sizing:border-box;
background-image:linear-gradient(#1d2642 1px,transparent 1px),linear-gradient(90deg,#1d2642 1px,transparent 1px);background-size:32px 32px}}
h1{{font:700 92px "Familjen Grotesk",sans-serif;margin:0;letter-spacing:-1px}} h1 span{{color:#7d96ff}}
p{{font-size:34px;margin:14px 0 44px;color:#aeb7d0;max-width:960px;line-height:1.3}}
.row{{display:flex;gap:14px}} .c{{display:flex;flex-direction:column;align-items:center;gap:8px}}
.b{{width:86px;height:86px;border-radius:14px;background:#161d31;border:3px solid #2a3454;display:flex;align-items:center;justify-content:center;font:700 34px "JetBrains Mono",monospace}}
.b.in{{border-color:#7d96ff}} .b.on{{background:#f5b335;border-color:#f5b335;color:#0f1424}}
.p{{height:30px;font:600 24px "JetBrains Mono",monospace;color:#f5b335}}
.foot{{margin-top:34px;font-size:26px;color:#7a84a3}}
</style></head><body>
<h1>Algo<span>Scope</span></h1>
<p>Watch DSA algorithms run, line by line. 159 interview problems in Java, Python and C++.</p>
<div class="row">{cells}</div>
<div class="foot">adityamalu.github.io/algoscope</div>
</body></html>"""

with sync_playwright() as pw:
    b = pw.chromium.launch()
    pg = b.new_page(viewport={"width": 1200, "height": 630})
    pg.set_content(HTML, wait_until="networkidle")
    os.makedirs(os.path.join(root, "assets"), exist_ok=True)
    pg.screenshot(path=os.path.join(root, "assets", "og.png"))
    b.close()
print("wrote assets/og.png")
