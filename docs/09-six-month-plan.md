# PocketAI — Six-Month Development Plan

**Team shape (5 FTE + fractional):**
- **E1 — Engine lead** (C++/FFI: llama.cpp, whisper, Piper, builds/CI)
- **E2 — Flutter lead** (UI/UX implementation, design system)
- **E3 — Product engineer** (Dart services: conversation, memory, RAG, data layer)
- **D1 — Product designer** (also owns starter-character writing with founder)
- **P1 — Founder/PM** (product, prompts/personas, beta ops, store/legal)
- Fractional: QA contractor from month 3; legal/licensing counsel ~month 1 and pre-launch.

Cadence: 2-week sprints; every sprint ends with a build on real min-spec devices. Milestones M0–M6 refer to the [MVP Roadmap](06-mvp-roadmap.md).

---

## Month 1 — Foundations & the Gate (M0 → M1 start)

**Weeks 1–2 (Sprint 1): the spike that decides everything**
- E1: llama.cpp via FFI on Android + iOS; benchmark harness; candidate-model matrix (Gemma/Qwen/Llama/Phi, 1.5B–8B, Q4) on 4 reference devices.
- E3: repo, CI skeleton, drift + SQLCipher + sqlite-vec proof; schema v1 from the design doc.
- E2: Flutter app shell, theming tokens, navigation; chat-bubble list perf prototype (10k messages, 60fps).
- D1/P1: design system ("Dusk"), starter-cast casting doc; legal review of model licenses kicked off.
- **Gate (end W2): M0 exit criteria met on min-spec, or re-scope min-spec/default model now.**

**Weeks 3–4 (Sprint 2): first real conversation**
- Streaming pipeline engine→isolate→UI with pacing; typing indicator; persistence transactions; stop/continue.
- KV-cache session save/restore wired to conversations.
- Deliverable: **kill-and-reopen-proof chat with one hardcoded character on both platforms.**

## Month 2 — Chat Core Complete + Characters (M1 → M2)

**Sprint 3:** conversation list (denormalized query, pins, swipe actions), day separators, edit-my-message → regenerate, message actions, empty/error states, dark mode complete.
**Sprint 4:** character schema + PromptComposer v1 (persona compilation w/ token budget); creation wizard (3 steps + Advanced); contact card; first 4 starter characters written & tuned (P1/D1 own persona quality as a *product* workstream, not config).
- **Month-2 exit = M2:** blind persona test — 5 testers chat with 3 characters; if they read as "same bot, different name," Sprint 5 becomes a persona sprint.

## Month 3 — Memory (M3) — the moat month

**Sprint 5:** rolling summaries (segment + compaction) with deterministic token budgeting; embedder integration; hybrid recall (vec + FTS fusion) injected via PromptComposer; recall tuning harness with fixture conversations.
**Sprint 6:** background extraction pass (dedup, importance, provenance); Memory Browser UI (view/edit/delete/pin, search); "forget everything"; About-You profile; QA contractor onboards; **internal dogfood build — whole team daily-drives.**
- **Month-3 exit = M3:** the "remember test" (3 facts, 200+ mixed messages, restart, ≥2/3 recalled naturally) passes on min-spec.

## Month 4 — Images, Models, Onboarding (M4) + remaining cast

**Sprint 7:** vision pack (mmproj) — send photo → discuss; caption cache; attachment UX (camera/library, previews). Model catalog + downloader (resumable, sha-verified, background).
**Sprint 8:** first-run flow (device detect → recommendation → download-while-meeting-the-cast); model manager + storage dashboard; remaining starter cast (8 total) written/tuned; **early TestFlight submission to surface App Review feedback (R-4).**
- **Month-4 exit = M4:** fresh install → first AI reply < 10 min on Wi-Fi; TestFlight approved.

## Month 5 — Polish & Hardening (M5)

**Sprint 9:** reactions, wallpapers/themes, in-chat + global search (FTS), haptics, animation pass, app lock, encrypted export/backup + restore.
**Sprint 10:** performance/stability blitz — p90 latency targets on device matrix, soak tests, thermal/battery guardrails, OOM preflight, migration safety net; accessibility pass (screen readers, dynamic type); store assets, privacy labels ("Data Not Collected" — earned literally), licensing/attribution screen.
- **Month-5 exit = M5:** beta testers describe it as "texting a person"; crash-free sessions ≥ 99.5% in dogfood.

## Month 6 — Beta, Stabilize, Launch (M6)

**Weeks 21–22:** closed beta, 200–500 users (TestFlight + Play closed track, consented-analytics flavor). Instrument the four launch metrics (activation, D7, depth, perf). Triage cadence: daily. One feature-freeze exception allowed, chosen from beta data.
**Weeks 23–24:** RC builds; Pro unlock IAP live in sandbox → production; final store review submissions with buffer; press/launch kit (the "no subscription, no cloud, own your AI" story); launch-day catalog config (models, sale pricing).
- **Ship:** Android + iOS GA. Post-launch war room for two weeks (hotfix pipeline rehearsed in beta).

---

## What months 7–9 already look like (pre-committed fast-follows)
v1.1 Voice (engine work E1 starts during month 6 beta idle time) → v1.2 Vault → v1.3 Groups → v1.4 Desktop. See [MVP Roadmap](06-mvp-roadmap.md).

## Slip policy (decided now, not under pressure)
1. **Cut first:** reactions/wallpapers/global search (cosmetic polish) → v1.0.x patch.
2. **Cut second:** vision pack → v1.1 (painful — it's a demo darling — but memory is the moat, images are a feature).
3. **Never cut:** memory quality, streaming feel, onboarding reliability, encryption, persona quality.
4. A milestone slipping > 2 weeks triggers scope review, not schedule compression.

## Standing risk rituals
- Every sprint demo runs on the **worst** supported device, not the best.
- Persona quality review is a named agenda item every sprint (R-1 is the product risk).
- Store-policy watch (R-4) monthly; safety red-team session (R-3) in months 3 and 5.
