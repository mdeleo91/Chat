# PocketAI — Risks & Technical Challenges

Ranked by (likelihood × impact). Each has an owner-able mitigation and, where possible, an early tripwire.

## Critical

### R-1 · Small-model quality vs. the "feels like a person" bar
**The product promises personality, memory, and wit from 2–8B quantized models.** Users calibrated on frontier cloud models may find replies repetitive, forgetful, or dull — and the whole thesis collapses if characters feel like bots.
- **Mitigations:** invest disproportionately in prompt engineering per starter character (persona compilation is a core competency, not config); memory injection does heavy lifting — *continuity* reads as intelligence; response pacing and reactions add perceived humanity for free; set expectations by archetype (a Chef or DM is judged on charm, not PhD reasoning); default to the best 3–4B-class instruct models per device tier and refresh the catalog as open models improve (catalog updates need no app release).
- **Tripwire:** M2 blind persona testing; if testers say "chatbot" not "character," stop and fix before M3.

### R-2 · Device performance, memory ceilings, and thermals (esp. iOS)
6 GB iPhones cap usable model size; Android fragmentation spans Vulkan-broken drivers to flagship NPUs; sustained generation heats phones and drains batteries; OOM kills feel like app crashes.
- **Mitigations:** M0 spike gates the whole plan on measured min-spec numbers; strict device-tier matrix mapping RAM/chipset → recommended model; KV-cache persistence to avoid re-prefill; preflight RAM checks before load; thermal monitor with graceful, in-fiction degradation ("taking a breather"); CPU fallback everywhere GPU is flaky; iOS `increased-memory-limit` entitlement + conservative default tier.
- **Tripwire:** any min-spec device under 8 tok/s sustained in M0 → raise min-spec or shrink default model *then*.

### R-3 · Safety with uncensorable local models
Open-weight local models can be steered past guardrails; the app hosts companionship and "Listener"-type personas; there is no server-side moderation by design. Reputational, store-review, and regulatory exposure (esp. minors, self-harm, parasocial harm).
- **Mitigations:** 17+/Mature age rating from day one; system-prompt-level guardrails on all built-in characters; local lightweight classifier for self-harm/crisis topics → bundled offline crisis resources surfaced non-judgmentally; persistent "AI character" disclosure on every contact card; no romantic-companion *marketing* even if users create such characters; content-preference settings framed honestly as steering, not enforcement; legal review of store policies (Apple 1.2/4.x, Google AI-generated-content rules) before submission.
- **Tripwire:** store review rejection or beta reports of harmful outputs → dedicated safety sprint before launch.

## High

### R-4 · App review risk (Apple especially)
On-device LLMs, user-authored characters, and a marketplace-later plan all touch evolving store policies; a rejection at M6 is a schedule crater.
- **Mitigations:** submit an early TestFlight build in M4 (review feedback surfaces early); precedent research (several local-LLM apps are live on the App Store); keep launch scope free of user-generated *sharing* (import only); direct-distribution hedge on desktop.

### R-5 · Model download & first-run funnel
The magic is gated behind a 2–4 GB download; every abandonment there is a lost user and a likely 1-star "doesn't work" review.
- **Mitigations:** resumable, background-capable downloads; "meet your cast" content during the wait; a tiny bundled fallback model (~700 MB-class) so *something* works within minutes on Wi-Fi-poor installs; clear size/Wi-Fi messaging pre-download; store listing sets expectations ("downloads a 3 GB AI brain").

### R-6 · Memory/RAG quality: creepy, wrong, or absent
Bad extraction pollutes context ("who is Biscuit?"), over-recall feels surveillance-y, under-recall breaks the core promise.
- **Mitigations:** memories are always user-visible/editable (trust through transparency); similarity-dedup before insert; importance scoring + recency decay in recall ranking; per-character memory rules honored at extraction; "why do you know that?" — provenance links every memory to its source message; conservative recall thresholds (silently missing a memory is safer than confidently misusing one).

### R-7 · Group-chat runaway or flat dynamics
Characters talking past each other, infinite AI-AI loops, or all replies collapsing into one voice (same base model!).
- **Mitigations:** hard AI-turn caps with user-controlled continuation; director heuristics before LLM routing; strong per-character speaking-style prompts + distinct sampler presets; ship groups in v1.3 only after 1:1 personas are proven; template groups tuned by hand.

## Medium

### R-8 · Cross-platform native build complexity
llama.cpp + whisper.cpp + Piper + SQLCipher + sqlite-vec across 5 OSes × multiple ISAs is a CI/CD project of its own (Vulkan driver bugs, notarization, Play 16KB-page requirements, MSIX signing).
- **Mitigations:** engine behind one C ABI with a golden-output test suite per platform; CI device farm (physical low-end Androids + iPhones) from M1; desktop deferred to v1.4 so mobile matrices harden first.

### R-9 · Storage pressure
Models (2–5 GB) + voices + vault + attachments can eat 10 GB+; users on 64 GB phones will hit walls and blame the app.
- **Mitigations:** storage dashboard in model manager; one-chat-model-at-a-time default; easy re-downloadable deletes; vault stores text + embeddings, not necessarily original files (user choice).

### R-10 · License compliance for models & voices
Model weights carry heterogeneous licenses (Gemma Terms, Llama Community License, etc.); redistribution vs. user-initiated download matters legally; voice datasets have provenance questions.
- **Mitigations:** catalog links to each license and downloads from official/approved mirrors (user-initiated download, not bundled redistribution, for restrictive licenses); ship-bundle only Apache/MIT-licensed weights; per-voice license audit for Piper voices; attribution screen.

### R-11 · One-time-purchase revenue sustainability
Covered in the [Monetization doc](07-monetization.md) — packs, marketplace, desktop direct sales, and honest major-version upgrades; tripwire is Pro conversion < 2% at month 3 post-launch → revisit pricing/paywall placement, not the no-subscription promise.

### R-12 · Competitive squeeze
Platform vendors are shipping free on-device AI (Apple Intelligence, Gemini Nano); a "good enough" OS-level companion could commoditize the category.
- **Mitigations:** the moat is the *product* (characters, memory, relationships, groups, vault — a messaging experience, not an assistant API), cross-platform reach, and user ownership; OS on-device APIs are an *opportunity* too — they can become another engine backend behind the same ABI, shrinking our download sizes.

## Watchlist (low today, monitor)
- **Flutter desktop maturity** on Linux (fallback: delay Linux, or Tauri shell for Linux only).
- **sqlite-vec scale limits** (vault > ~500k chunks) — fallback path to a dedicated index (HNSW) behind the RetrievalService interface.
- **Whisper streaming latency** on min-spec Androids — fallback: push-to-talk-only on low tier, no call mode.
- **EU AI Act / transparency rules** for AI companions — disclosure requirements already exceeded by design; keep counsel review annual.
