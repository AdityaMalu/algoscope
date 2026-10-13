"""Turn app.html (the claude.ai artifact body) into a standalone index.html for GitHub Pages.

Usage: python tools/make_site.py
"""
import os

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
src = open(os.path.join(root, "app.html"), encoding="utf-8").read()
cut = src.index('<div class="wrap">')
head = src[:cut].replace('<meta charset="utf-8">\n', "")
body = src[cut:]
page = (
    "<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n"
    "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\">\n"
    "<meta name=\"description\" content=\"Step-by-step animated traces of 159 DSA interview problems in Java, Python and C++.\">\n"
    "<style>html,body{margin:0}body{font:14px system-ui,-apple-system,'Segoe UI',sans-serif}</style>\n"
    + head + "</head>\n<body>\n" + body + "\n</body>\n</html>\n"
)
open(os.path.join(root, "index.html"), "w", encoding="utf-8").write(page)
print("wrote index.html")
