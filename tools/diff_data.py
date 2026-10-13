"""Show where rebuilt data/*.json differs from the committed copy, as JSON paths.

Usage: python tools/diff_data.py [max-per-file]
"""
import json, os, subprocess, sys

root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def walk(a, b, path, out):
    if type(a) is not type(b):
        out.append(f"{path}: {str(a)[:80]} -> {str(b)[:80]}")
    elif isinstance(a, dict):
        for k in sorted(set(a) | set(b), key=str):
            walk(a.get(k), b.get(k), f"{path}.{k}", out)
    elif isinstance(a, list):
        if len(a) != len(b):
            out.append(f"{path}: length {len(a)} -> {len(b)}")
        for i, (x, y) in enumerate(zip(a, b)):
            walk(x, y, f"{path}[{i}]", out)
    elif a != b:
        out.append(f"{path}: {str(a)[:80]} -> {str(b)[:80]}")


def main():
    cap = int(sys.argv[1]) if len(sys.argv) > 1 else 10
    names = subprocess.run(["git", "diff", "--name-only", "--", "data"], cwd=root, capture_output=True, text=True).stdout.split()
    for name in names:
        old = json.loads(subprocess.run(["git", "show", "HEAD:" + name], cwd=root, capture_output=True, text=True, encoding="utf-8").stdout)
        new = json.load(open(os.path.join(root, name), encoding="utf-8"))
        out = []
        walk(old, new, "", out)
        print(f"{name}: {len(out)} difference(s)")
        for line in out[:cap]:
            print("  " + line)


if __name__ == "__main__":
    main()
