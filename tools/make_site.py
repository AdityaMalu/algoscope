"""Turn app.html (the claude.ai artifact body) into a standalone index.html for GitHub Pages.

Usage:
  python tools/make_site.py              writes index.html next to app.html
  python tools/make_site.py --out _site  also assembles the full deployable site in _site/
"""
import base64, hashlib, os, re, shutil, sys

import make_pages

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def csp(html):
    """A Content-Security-Policy allowing only this page's own inline script (by hash) and the hosts it needs.

    GitHub Pages can't send headers, so it goes in a <meta>. The artifact copy (app.html) runs under claude.ai's own CSP.
    """
    hashes = " ".join(
        "'sha256-" + base64.b64encode(hashlib.sha256(m.encode("utf-8")).digest()).decode() + "'"
        for m in re.findall(r"<script>(.*?)</script>", html, re.S))
    return ("default-src 'self'; "
            f"script-src 'self' {hashes} https://cdnjs.cloudflare.com; "
            "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com; "
            "font-src https://fonts.gstatic.com; img-src 'self' data:; "
            "connect-src 'self' https://openrouter.ai; worker-src 'self'; "
            "object-src 'none'; base-uri 'none'; form-action 'none'")


SITE = make_pages.SITE
DESC = "Step-by-step animated traces of 159 DSA interview problems in Java, Python and C++. Paste your own Python and watch it run."
FOOT = ('<footer class="site-foot"><a href="problems/">All 159 problems as plain pages</a> · '
        '<a href="https://github.com/AdityaMalu/algoscope">Source on GitHub</a> · MIT license</footer>\n')


def build_index():
    src = open(os.path.join(root, "app.html"), encoding="utf-8").read()
    cut = src.index('<div class="wrap">')
    head = src[:cut].replace('<meta charset="utf-8">\n', "")
    body = src[cut:]
    return (
        "<!doctype html>\n<html lang=\"en\">\n<head>\n<meta charset=\"utf-8\">\n"
        f"<meta http-equiv=\"Content-Security-Policy\" content=\"{csp(src)}\">\n"
        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, viewport-fit=cover\">\n"
        f"<meta name=\"description\" content=\"{DESC}\">\n"
        f"<link rel=\"canonical\" href=\"{SITE}\">\n"
        "<meta property=\"og:type\" content=\"website\">\n<meta property=\"og:site_name\" content=\"AlgoScope\">\n"
        "<meta property=\"og:title\" content=\"AlgoScope: watch DSA algorithms run, line by line\">\n"
        f"<meta property=\"og:description\" content=\"{DESC}\">\n<meta property=\"og:url\" content=\"{SITE}\">\n"
        f"<meta property=\"og:image\" content=\"{SITE}assets/og.png\">\n<meta name=\"twitter:card\" content=\"summary_large_image\">\n"
        "<link rel=\"icon\" href=\"assets/icon.svg\" type=\"image/svg+xml\">\n"
        "<style>html,body{margin:0}body{font:14px system-ui,-apple-system,'Segoe UI',sans-serif}"
        ".site-foot{max-width:1360px;margin:0 auto;padding:0 20px 32px;font-size:13px;color:var(--ink-3)}.site-foot a{color:inherit}</style>\n"
        + head + "</head>\n<body>\n" + body + "\n" + FOOT + "</body>\n</html>\n"
    )


def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8", newline="\n") as f:
        f.write(text)


def main():
    page = build_index()
    write(os.path.join(root, "index.html"), page)
    print("wrote index.html")
    if "--out" in sys.argv:
        out = os.path.abspath(sys.argv[sys.argv.index("--out") + 1])
        if os.path.isdir(out):
            shutil.rmtree(out)
        shutil.copytree(os.path.join(root, "data"), os.path.join(out, "data"))
        write(os.path.join(out, "index.html"), page)
        shutil.copy(os.path.join(root, "LICENSE"), os.path.join(out, "LICENSE"))
        shutil.copytree(os.path.join(root, "runner"), os.path.join(out, "runner"), ignore=shutil.ignore_patterns("__pycache__"))
        shutil.copytree(os.path.join(root, "assets"), os.path.join(out, "assets"))
        for f in os.listdir(root):  # Google Search Console ownership file(s)
            if re.fullmatch(r"google[0-9a-f]+\.html", f):
                shutil.copy(os.path.join(root, f), os.path.join(out, f))
        n = make_pages.build(out, write)
        print(f"assembled {out} with {n} problem pages")


if __name__ == "__main__":
    main()
