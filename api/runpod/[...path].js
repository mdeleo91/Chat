/* RunPod management-API relay.
   rest.runpod.io does not answer the CORS preflight a browser sends before
   any request that carries an Authorization header, so the web version of
   the app cannot call it directly (the Android app can: it loads from
   file:// with universal access). This function forwards the method, path,
   query, JSON body and the caller's own Authorization header to
   https://rest.runpod.io/v1/<path> and returns the upstream status and body
   untouched, with permissive CORS so the page can read error bodies.
   The API key is never stored or logged here — it rides each request and
   nothing else. */
const UPSTREAM = "https://rest.runpod.io/v1/";

function cors(res) {
  res.setHeader("Access-Control-Allow-Origin", "*");
  res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
  res.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
  res.setHeader("Access-Control-Max-Age", "86400");
}

module.exports = async function handler(req, res) {
  cors(res);
  if (req.method === "OPTIONS") { res.status(204).end(); return; }

  const auth = req.headers["authorization"];
  if (!auth) {
    res.status(401).json({error: "Missing Authorization header — the app sends your RunPod key with each request"});
    return;
  }
  const p = req.query && req.query.path;
  const path = Array.isArray(p) ? p.join("/") : String(p || "");
  if (!path || path.indexOf("..") >= 0) { res.status(400).json({error: "Bad path"}); return; }

  /* the query string minus the catch-all's own parameter */
  const u = new URL(req.url || "/", "http://x");
  u.searchParams.delete("path");
  const search = u.searchParams.toString();
  const target = UPSTREAM + path + (search ? "?" + search : "");

  let body;
  if (req.method !== "GET" && req.method !== "HEAD" && req.body !== undefined && req.body !== null) {
    body = typeof req.body === "string" ? req.body : JSON.stringify(req.body);
  }
  let up;
  try {
    up = await fetch(target, {
      method: req.method,
      headers: {"Authorization": auth, "Content-Type": "application/json", "Accept": "application/json"},
      body: body
    });
  } catch (e) {
    res.status(502).json({error: "Could not reach rest.runpod.io: " + String(e && e.message || e)});
    return;
  }
  const text = await up.text();
  res.status(up.status);
  res.setHeader("Content-Type", up.headers.get("content-type") || "application/json");
  res.send(text);
};
