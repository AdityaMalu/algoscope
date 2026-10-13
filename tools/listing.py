"""Print each problem's Java display code with line numbers; '*' marks lines that trace steps point at.

Usage: python tools/listing.py <topic-json-name>   e.g. arrays-hashing
"""
import json, os, sys

sys.stdout.reconfigure(encoding="utf-8")
root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
recs = json.load(open(os.path.join(root, "data", sys.argv[1] + ".json"), encoding="utf-8"))
for p in recs:
    used = {s["line"] for s in p["steps"]}
    for e in p["edgeCases"]:
        used |= {s["line"] for s in e["steps"]}
    print(f"=== {p['id']}")
    for i, line in enumerate(p["code"].split("\n"), 1):
        print(f"{'*' if i in used else ' '}{i:3} {line}")
