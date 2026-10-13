/* Runs pasted Python for real, off the main thread, with Pyodide (CPython compiled to WebAssembly).
   The page sends {id, job} and gets back {id, ok, result | error}. The tracer itself is pytrace.py. */
const PYODIDE = "https://cdn.jsdelivr.net/pyodide/v0.26.4/full/";
let boot = null;

function start(){
  if (!boot) boot = (async () => {
    importScripts(PYODIDE + "pyodide.js");
    const py = await loadPyodide({ indexURL: PYODIDE, stdout: () => {}, stderr: () => {} });
    const src = await (await fetch(new URL("pytrace.py", self.location.href))).text();
    py.FS.writeFile("/home/pyodide/pytrace.py", src);
    py.runPython("import sys; sys.path.insert(0, '/home/pyodide'); import pytrace; sys.setrecursionlimit(900)");
    return py;
  })();
  return boot;
}

self.onmessage = async ({ data }) => {
  const { id, job } = data;
  try {
    const py = await start();
    if (!job){ self.postMessage({ id, ok: true }); return; }  // warm-up only
    py.globals.set("__job", JSON.stringify(job));
    const out = py.runPython("pytrace.run_job(__job)");
    self.postMessage({ id, ok: true, result: JSON.parse(out) });
  } catch (e) {
    self.postMessage({ id, ok: false, error: String((e && e.message) || e) });
  }
};
