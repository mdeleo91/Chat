# Running a model server

The phone can only hold a 1–3B model. A machine on your network can run a
12–32B one, which is the difference between "coherent sentences" and "a
conversation you want to continue." Settings → **Change model** → **My server**
points the app at that machine.

Anything speaking the OpenAI `/v1/chat/completions` API works: **Ollama**,
**llama.cpp** (`llama-server`), **LM Studio**, **vLLM**. The app discovers what
you have loaded via `GET /v1/models` and streams replies over SSE. If the box
is asleep, off, or off-network, the app says so instead of answering — keep the
server reachable.

## The other engine: GLM cloud

The same settings screen has a **GLM cloud** toggle: instead of your server,
replies come from Z.ai's hosted GLM (default `glm-5.2`) over the same
OpenAI-style API at `api.z.ai`, authenticated with an API key you paste in.
It works from anywhere with internet — no home server, no Tailscale — but it
inverts the privacy story: prompts (character card, persona, memories, recent
history) are sent to Z.ai, and you pay per token. Only one engine answers at
a time; whichever the toggle selects is the only reply path, and the model
input accepts newer ids (e.g. a future `glm-5.5`) without a code change.

## Ollama, the short version

```bash
# on the Mac mini / PC
ollama pull hermes3:8b            # or mistral-nemo:12b, qwen2.5:14b …
OLLAMA_HOST=0.0.0.0:11434 \
OLLAMA_ORIGINS='*' \
  ollama serve
```

Both variables matter:

- `OLLAMA_HOST=0.0.0.0` — otherwise it binds to localhost and the phone cannot
  reach it at all.
- `OLLAMA_ORIGINS='*'` — the app runs from `file://` inside the APK, which sends
  `Origin: null`. Without this the browser blocks the response and it looks like
  the server is down.

Then in the app enter `192.168.x.x:11434`, tap **Connect**, pick a model.

`llama-server` needs neither variable — it listens on all interfaces and sends
permissive CORS headers by default:

```bash
llama-server -m model.gguf --host 0.0.0.0 --port 8080
```

…and you enter `192.168.x.x:8080`.

## Sizing the machine

| Hardware | Comfortable model |
|---|---|
| Mac mini M4, 16 GB | 8B q4 |
| Mac mini M4 Pro, 24–32 GB | 12–14B q4 |
| 48–64 GB unified, or a 24 GB GPU | 27–32B q4 |

Unified memory is the reason a Mac mini punches above a same-priced GPU box
here: the model has to fit in memory the accelerator can address, and 32 GB of
that is cheaper on Apple silicon than in VRAM.

## Reaching it from outside the house

Plain HTTP only works on your own LAN, and only because the APK now sets
`android:usesCleartextTraffic="true"` (see `android/axml.py`). Two constraints
follow:

1. **The web build on Vercel cannot talk to a plain-HTTP server at all** — an
   HTTPS page may not issue `http://` requests, and no flag turns that off. Use
   the APK on the LAN, or give the server real TLS.
2. **Away from home you want TLS anyway**, and you do not want to forward a port.

[Tailscale](https://tailscale.com) solves both: install it on the server and the
phone, run `tailscale serve https / http://localhost:11434`, and use the
resulting `https://<machine>.<tailnet>.ts.net` address in the app. Real
certificate, no open ports, works from anywhere, and the traffic still only goes
between your own devices.

## What the server receives

Exactly what the on-device path builds — same code, in `sysPrompt()` and
`buildMessages()`:

- the character's name, tagline, personality, speaking style, description and
  interests
- your own persona — name and description, from Settings → Your persona
  (the description can be withheld with the toggle there; the name always goes)
- that character's stored memories (most recent 12)
- group-chat context, when the conversation is a group
- the last 16 messages of history
- photos you've sent, as base64 `image_url` content parts — on GLM cloud these
  automatically switch the request to the vision model (`glm-5v-turbo` by
  default) until the photo leaves the history window; on your own server they
  go to whatever model is loaded, which needs to be a vision model (qwen-VL,
  llava…) to see them

Sampling is `temperature 0.85`, `top_p 0.95`, `max_tokens 512`, with presence and
frequency penalties at `0.3` to stop the repetition loops that roleplay
fine-tunes fall into.

## Still worth doing

The context window is a flat last-16-messages slice with no summarization, and
memories are injected newest-first rather than by relevance. A bigger model
makes both of those limits *more* visible, not less — they are the next thing to
fix after you have the server running.
