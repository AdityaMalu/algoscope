"""Serve the built site on a local port for the browser tests.

SITE_DIR picks the folder to serve (default: _site, falling back to the repo root).
"""
import functools, http.server, os, socketserver, threading

import pytest

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


class Quiet(http.server.SimpleHTTPRequestHandler):
    def log_message(self, *a):
        pass


@pytest.fixture(scope="session")
def site():
    d = os.environ.get("SITE_DIR") or os.path.join(ROOT, "_site")
    if not os.path.isdir(d):
        d = ROOT
    srv = socketserver.ThreadingTCPServer(("127.0.0.1", 0), functools.partial(Quiet, directory=d))
    threading.Thread(target=srv.serve_forever, daemon=True).start()
    yield f"http://127.0.0.1:{srv.server_address[1]}/"
    srv.shutdown()


@pytest.fixture
def app(page, site):
    """A page with the site open on the home view; fails the test on any uncaught script error."""
    errors = []
    page.on("pageerror", lambda e: errors.append(str(e)))
    page.goto(site + "#home")
    page.wait_for_selector("#homeTopics .prow")
    yield page
    assert not errors, errors
