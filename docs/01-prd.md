# PocketAI — Product Requirements Document

**Status:** Draft v1.0 · **Owner:** Product · **Last updated:** 2026-07-31

---

## 1. Vision

PocketAI is a messaging app where every contact is an AI character that lives entirely on your device. The product bet is simple: **the interface people already love for talking to humans is the right interface for talking to AI.** No prompt boxes, no "regenerate" buttons front-and-center, no model names in your face — a conversation list, chat bubbles, voice notes, and group chats.

The second bet is **privacy as the moat**: everything — model inference, memories, documents, voice — runs locally. There is no account to create, no server to trust, and no subscription to maintain. You buy it (or unlock it) once and it is yours, offline, forever.

### Design philosophy

> An AI companion should feel like someone in your contacts list, not a website.

Every design decision is tested against one question: *does this feel like messaging a person, or like operating software?*

## 2. Problem Statement

1. **Cloud AI chat is a privacy trade nobody loves.** Journals, therapy-adjacent talks, roleplay, and work documents flow to third-party servers. A large audience self-censors or abstains entirely.
2. **Existing local-AI tools are built for enthusiasts.** Tools in this space expose models, quantizations, context windows, and sampler settings. They feel like dev tools, not products.
3. **Chatbot UX is transactional.** Session-based assistants forget you, reset context, and feel like a search engine with manners. There is no continuity, personality, or relationship.
4. **Subscriptions fatigue.** AI products default to $20/month. A one-time purchase for on-device AI is a genuinely differentiated offer: the marginal inference cost is zero because the user's hardware does the work.

## 3. Target Users & Personas

| Persona | Description | Primary jobs |
|---|---|---|
| **The Privacy-Conscious Journaler** (Maya, 29) | Keeps a diary, wants a reflective listener, refuses to send personal thoughts to a cloud | Daily check-in character; long-term memory; export/delete guarantees |
| **The Tabletop GM** (Devon, 34) | Runs D&D campaigns, drowning in lore notes | Dungeon Master character wired to a campaign Knowledge Vault; group chat with NPC characters |
| **The Writer** (Priya, 41) | Novelist who wants a critique partner that knows her manuscript | Writing-partner character; document import; brainstorm mode; long memory |
| **The Offline Traveler** (Sam, 26) | Flights, rural areas, countries with restricted internet | Fully offline chat, voice, and document Q&A |
| **The Companionship Seeker** (Alex, 52) | Wants friendly daily conversation without judgment or data harvesting | Warm persistent characters; voice calls; reactions; feels like a friend |
| **The Tinkerer** (Jordan, 31) | Local-LLM hobbyist who wants a *nice* front end | Model management, custom system prompts, import GGUF — but tucked behind Settings |

Primary launch personas: **Journaler, GM, Writer.** The Tinkerer is served but never designed *for* — their needs must not leak into the default UI.

## 4. Product Principles (non-negotiable)

1. **Everything runs locally.** No feature may require a network call after model download. Airplane mode is the reference environment.
2. **No accounts, no subscriptions, no telemetry.** Zero data leaves the device. Crash reporting is opt-in and scrubbed.
3. **Privacy-first.** Local database encrypted at rest; app lock (biometric/PIN); per-character memory deletion; full export.
4. **Mobile-first.** Design for a 6" phone screen; desktop is an adaptive expansion, not a separate product.
5. **Fast and responsive.** First token < 1.5 s on target hardware; UI never blocks on inference; 60 fps scrolling.
6. **It's a contact, not a bot.** Characters have names, avatars, statuses, moods, memories — never "Model: Qwen-2.5-7B-Instruct-Q4_K_M" in the chat UI.

## 5. Feature Requirements

Priorities: **P0** = MVP launch blocker · **P1** = v1.x fast-follow · **P2** = later.

### 5.1 Messaging Interface

| Req | Description | Priority |
|---|---|---|
| MSG-1 | Conversation list home screen: avatar, name, last message preview, timestamp, unread badge, pinned chats | P0 |
| MSG-2 | Chat screen with left/right bubbles, timestamps, day separators, read receipts styling | P0 |
| MSG-3 | Streaming responses rendered as a **typing indicator followed by the bubble filling in** (word-by-word, throttled to feel human — not raw token spray) | P0 |
| MSG-4 | Character "status" line (e.g. *"sharpening pencils ✏️"*) authored per character, shown in chat header & contact card | P0 |
| MSG-5 | Message reactions (long-press → emoji tapback) — user can react; characters occasionally react to user messages | P1 |
| MSG-6 | Image attachments: send a photo, character sees & discusses it (local vision model) | P0 |
| MSG-7 | Voice messages: push-to-talk record → local STT → character replies in text and/or TTS voice note | P1 |
| MSG-8 | In-conversation search (keyword, FTS) and global search across conversations | P1 |
| MSG-9 | Chat wallpapers & themes (per-chat wallpaper, global light/dark/system) | P1 |
| MSG-10 | Message actions: copy, delete, edit-my-message (regenerates reply), "rewind" conversation to a point | P0 |
| MSG-11 | Delightful human details: "last seen recently", occasional double-texting by characters, natural response delays proportional to message length (configurable off) | P2 |

**Deliberate anti-features in the chat surface:** no visible "regenerate" carousel (long-press → "ask to rephrase" instead), no token counters, no model badges, no system-prompt viewer inside the chat (all under character settings).

### 5.2 AI Characters

| Req | Description | Priority |
|---|---|---|
| CHR-1 | Character creation wizard: name, avatar (gallery/photo/generated monogram), personality description, speaking style, backstory, interests, greeting, status | P0 |
| CHR-2 | Advanced tab: raw system prompt override, memory rules (what to remember/forget), temperature-as-"creativity" slider with plain-language labels | P0 |
| CHR-3 | Starter cast: 8–12 pre-built characters shipped in-app (Dungeon Master, Writing Partner, Chef, Study Buddy, Fitness Coach, History Professor, Listener*, Trip Planner, …) | P0 |
| CHR-4 | Per-character isolated memory and chat history | P0 |
| CHR-5 | Character import/export as a shareable file (`.aichar` — JSON card, compatible superset of common character-card formats) | P1 |
| CHR-6 | Per-character voice selection (local TTS voice + pitch/speed) | P1 |
| CHR-7 | Per-character model override (defaults to global model; power users can pin a character to a specific model) — buried in Advanced | P2 |

\* *The "Listener" character is a supportive-conversation companion, explicitly framed in-app as not therapy and not a substitute for professional help (see PRD §9, Safety).*

### 5.3 Offline AI Capabilities

| Req | Description | Priority |
|---|---|---|
| AI-1 | Natural chat with persona consistency across sessions | P0 |
| AI-2 | Image understanding: describe/analyze photos sent in chat | P0 |
| AI-3 | Document summarization (via Knowledge Vault import or direct file share to a chat) | P1 |
| AI-4 | Long-term memory: recalls facts from prior conversations (see 5.4) | P0 |
| AI-5 | Roleplay, brainstorming, story generation, writing assistance — emergent from characters, validated by starter cast quality | P0 |
| AI-6 | Voice conversations: hands-free "call" mode with streaming STT → LLM → TTS | P2 |

### 5.4 Local Memory System

| Req | Description | Priority |
|---|---|---|
| MEM-1 | Automatic memory extraction: after conversations, a background pass distills durable facts ("User's dog is named Biscuit", "Campaign: party just entered Ironhold") into per-character memory entries | P0 |
| MEM-2 | Rolling conversation summaries so long chats stay coherent beyond the context window | P0 |
| MEM-3 | Memory browser per character: view, edit, delete, pin (pinned = always in context) | P0 |
| MEM-4 | Global "About You" profile: user-editable facts shared with all characters (opt-in per fact) | P1 |
| MEM-5 | Memory export (JSON/Markdown) and full wipe ("forget everything") per character and globally | P0 |
| MEM-6 | Memory rules per character: natural-language instructions ("Never remember spoilers past chapter 4") applied at extraction time | P1 |

### 5.5 Knowledge Vault

| Req | Description | Priority |
|---|---|---|
| KV-1 | Import PDF, TXT, Markdown, and images (OCR-lite via vision model captions); folder organization | P1 |
| KV-2 | Local chunking + embedding + vector index; retrieval during conversation (RAG) | P1 |
| KV-3 | Per-character vault access grants ("Dungeon Master can read *Campaign Notes* folder") | P1 |
| KV-4 | Source attribution: character can cite which document a fact came from (tap to open) | P2 |
| KV-5 | Vault search UI (semantic + keyword) | P2 |

### 5.6 Group Chats

| Req | Description | Priority |
|---|---|---|
| GRP-1 | Create group with 2–5 characters + user; group name & avatar | P1 |
| GRP-2 | Turn orchestration: a lightweight director decides who speaks next (addressed-by-name > topical relevance > round-robin); characters can respond to *each other*, capped per user turn to prevent runaway loops | P1 |
| GRP-3 | Sender name + avatar on each bubble (WhatsApp group style); distinct bubble tint per character | P1 |
| GRP-4 | @mention a character to force them to answer | P1 |
| GRP-5 | Characters share the group transcript but keep private per-character memories | P1 |
| GRP-6 | Group templates ("Fantasy Writing Team", "Business Advisors") in starter content | P2 |

### 5.7 Voice

| Req | Description | Priority |
|---|---|---|
| VOX-1 | Offline STT (Whisper-class) for dictation & voice messages | P1 |
| VOX-2 | Offline TTS with multiple voices; per-character voice | P1 |
| VOX-3 | Voice-note UX: hold-to-record, slide-to-cancel, waveform playback bubbles | P1 |
| VOX-4 | Call mode: full-screen voice conversation with barge-in (interrupt while character is speaking) | P2 |

### 5.8 Models & Device Management

| Req | Description | Priority |
|---|---|---|
| MOD-1 | First-run flow: detect device RAM/chipset → recommend a model bundle ("Best for your phone") → download with resume | P0 |
| MOD-2 | Curated model catalog (Gemma, Llama, Qwen, Phi, Mistral families in GGUF) with plain-language descriptions ("Quick & light" / "Smart & thorough"), sizes, and RAM requirements | P0 |
| MOD-3 | Model manager in Settings: download/delete, storage usage, active model switch | P0 |
| MOD-4 | Advanced: import your own GGUF file | P1 |
| MOD-5 | Auxiliary model management (vision projector, embedder, STT, TTS voices) presented as "capability packs": *Vision*, *Voice*, *Reading (documents)* | P0/P1 per pack |
| MOD-6 | Thermal/battery guardrails: throttle or pause generation politely ("Nova is taking a breather — phone getting warm") | P1 |

### 5.9 Privacy & Data Control

| Req | Description | Priority |
|---|---|---|
| PRV-1 | SQLCipher-encrypted database; key in OS keystore | P0 |
| PRV-2 | App lock: biometric / PIN | P1 |
| PRV-3 | Full local backup/export (single encrypted archive) and restore/import | P1 |
| PRV-4 | Per-conversation and per-character delete = actual data destruction (VACUUM) | P0 |
| PRV-5 | Network permission used **only** by the model downloader; visible network activity indicator; "Airplane badge" in Settings proving zero connections | P0 |

## 6. Platforms

| Platform | Tier | Notes |
|---|---|---|
| Android | Launch | Min: 6 GB RAM, Android 10+; ARMv8.2+. Vulkan/OpenCL acceleration where available |
| iOS | Launch | iPhone 12+ (A14, 4 GB) minimum; iPhone 15 Pro+ recommended. Metal acceleration |
| macOS | Launch (fast-follow) | Apple Silicon; Metal. Same Flutter codebase, adaptive layout |
| Windows | v1.x | x64 + ARM64; CPU (AVX2) baseline, Vulkan/CUDA acceleration |
| Linux | v1.x | x64; CPU baseline, Vulkan/CUDA acceleration |

Desktop layout: two-pane (conversation list + chat) above 700 px width; global hotkey quick-chat later.

## 7. Non-Goals (v1)

- No cloud model fallback, even optional. (Revisit only with an explicit, separate, clearly-labeled toggle — default stays local-only.)
- No user-to-user messaging or federation. This is not a real messenger.
- No image *generation* (v1 is image understanding only; generation is a P2 capability pack).
- No plugin/tool ecosystem, no web browsing (there is no web).
- No character marketplace at launch (file-based sharing only; marketplace is a monetization phase-2 item).
- No Android < 10 / 4 GB devices — a bad first-token experience is worse than no app.

## 8. Success Metrics (all measured locally, surfaced to the user, never transmitted)

Because there is no telemetry, success is measured by **store metrics + opt-in anonymous surveys + local-only stats the user can see** ("Your year with Nova").

- **Activation:** ≥ 70% of installs complete model download and send ≥ 3 messages on day 1 (proxy: store funnel + reviews; internal: beta cohort with consented analytics builds).
- **Retention proxy:** ≥ 35% of beta cohort active on day 30.
- **Depth:** median beta user has ≥ 2 characters and ≥ 1 conversation exceeding 50 messages by day 14.
- **Performance floor:** p90 first token < 2.5 s, p90 sustained ≥ 8 tok/s on min-spec devices.
- **Business:** ≥ 4.5★ store rating; Pro unlock conversion ≥ 5% of MAU proxy by month 3 post-launch.

## 9. Safety & Content Considerations

- Characters presented as companions must include honest framing: an unobtrusive but persistent disclosure that they are AI (contact card: "AI character · runs on your device").
- The "Listener" starter character explicitly disclaims therapy/medical/crisis capability and surfaces regional crisis-line information (bundled offline) when self-harm topics are detected via a local classifier prompt.
- Age rating targets 17+/Mature where required; local content controls (a "content preferences" setting that adjusts system-prompt guardrails) — acknowledging that with open local models, guardrails are best-effort steering, not enforcement (see Risks doc §Safety).
- Character sharing files are inert data (JSON), never code.

## 10. Open Questions

1. iOS memory ceilings: which model tier is the *default* recommendation on 6 GB iPhones — 3B-class for speed or 4B/7B-class with extended-memory entitlement? (Prototype in M1; see Risks R-1.)
2. Do characters double-text / initiate? Local notifications from a scheduled "character check-in" are charming for companionship personas but risk feeling gimmicky. (P2 experiment, strictly opt-in.)
3. `.aichar` format: adopt/extend an existing community character-card format for import compatibility vs. clean-room schema. (Leaning: import common formats, export our superset.)
4. Whether Knowledge Vault ships in MVP or fast-follow — current plan: fast-follow (see MVP roadmap M4 vs v1.1).
