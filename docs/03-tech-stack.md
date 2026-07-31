# PocketAI — Recommended Tech Stack

**Guiding constraints:** five platforms from one team, heavy native FFI (inference), 60 fps chat UI, fully offline, small team. Every choice below optimizes for *one codebase, native performance where it counts*.

## Summary Table

| Layer | Choice | Why |
|---|---|---|
| App framework | **Flutter (Dart)** | One codebase for Android/iOS/macOS/Windows/Linux with real 60fps UI and first-class FFI |
| LLM runtime | **llama.cpp** (vendored, via `dart:ffi`) | GGUF runs every target model family; Metal/Vulkan/CPU backends; battle-tested mobile support |
| Vision | **llama.cpp multimodal** (Qwen-VL / Gemma-vision class GGUF + mmproj) | Same runtime, no second engine |
| Embeddings | **Small GGUF embedder** (bge-small / nomic-embed / embeddinggemma-class) via llama.cpp embedding API | Same runtime again; ~100–300 MB |
| Speech-to-text | **whisper.cpp** (small/base quantized) | The proven offline STT; streaming-capable; same build toolchain |
| Text-to-speech | **Piper** (ONNX voices, ~20–60 MB each) | Fast CPU real-time TTS, many voices/languages, permissive licenses |
| Database | **SQLite + SQLCipher** via `drift` | Encrypted, transactional, portable, single-file backup |
| Vector search | **sqlite-vec** extension | Vectors live *inside* the same encrypted DB; no second store to sync/secure |
| Keyword search | **SQLite FTS5** | Message & vault full-text search for free |
| Doc parsing | `pdfium` (via FFI) for PDF text; Markdown/TXT native; vision-model captions for images | Offline, lightweight |
| State mgmt | Riverpod | Testable, compile-safe DI for services (inference, memory, audio) |
| Background work | Dart isolates + native worker threads | Inference/indexing never touch the UI thread |
| Model delivery | Direct HTTPS from a curated manifest (Hugging Face-hosted GGUFs + own CDN mirror), resumable | The app's only network feature |

Target model families (all GGUF, Q4_K_M default): **Gemma** (2B/4B-class), **Llama** (3B/8B-class), **Qwen** (1.5B/4B/7B-class, incl. VL), **Phi** (mini-class), **Mistral** (7B-class, desktop tier).

---

## Framework: why Flutter

The framework decision is the biggest one; it was evaluated against three alternatives.

| Option | Verdict | Reasoning |
|---|---|---|
| **Flutter** | ✅ **Chosen** | True single codebase across all 5 targets. Skia/Impeller rendering gives the fluid, custom chat UI the product demands (bubbles, streaming text, waveforms) without fighting platform widgets. `dart:ffi` calls into llama.cpp/whisper.cpp with near-zero overhead. Mature desktop support. One team ships everything. |
| React Native + Tauri/Electron | ❌ | Two codebases in practice (RN mobile + web-wrapped desktop); JS bridge is a liability for streaming tokens and audio buffers; Electron contradicts "fast and light". |
| Kotlin Multiplatform + Compose | ❌ (for now) | Excellent on Android, but iOS Compose and desktop (esp. Linux) maturity adds risk; smaller hiring pool for the UI layer. Revisit if Flutter desktop disappoints. |
| Fully native ×5 | ❌ | 3–5× engineering cost; impossible for a small team in 6 months. |

**Risk hedge:** all AI functionality lives behind a platform-agnostic Dart service interface (`InferenceService`, `SpeechService`, …) backed by a C ABI. If the UI framework ever changes, the engine layer survives intact.

## Inference: why llama.cpp (and how)

- **Coverage:** every required family (Gemma, Llama, Qwen, Phi, Mistral) ships GGUF weights; quantization tiers (Q4_K_M default, Q8/F16 desktop) map cleanly to device tiers.
- **Acceleration:** Metal (iOS/macOS), Vulkan (Android/Windows/Linux), CUDA (desktop NVIDIA), plus strong NEON/AVX2 CPU paths. One runtime, per-platform build flags.
- **Multimodal:** vision via mmproj projectors on the same runtime — image understanding without a second engine.
- **Integration shape:** vendor llama.cpp as a git submodule, compile per-platform via CMake into `libpocket_engine` — a thin **C wrapper owning: model lifecycle, one inference queue (single active generation), KV-cache/session save-restore per conversation, token callback streaming into Dart, and cancellation.** The wrapper is our stable ABI; llama.cpp can be upgraded (or swapped) behind it.
- **KV-cache sessions:** persist per-conversation prompt caches to disk so reopening a chat doesn't re-prefill the whole history — critical for "first token < 1.5 s" on long chats.

**Alternatives considered:** MLC-LLM (great perf, heavier toolchain, smaller model coverage); Apple MLX (macOS/iOS-only — possible later *optimization backend* behind the same ABI); MediaPipe LLM Inference (Gemma-centric, less flexible); ONNX Runtime GenAI (weaker mobile GPU story for LLMs). llama.cpp wins on *breadth × maturity × one-runtime simplicity*.

## Voice pipeline

```
mic → 16 kHz PCM → VAD (Silero, ONNX) → whisper.cpp (streaming) → text
LLM reply (sentence-chunked as it streams) → Piper TTS → audio out
```

- **whisper.cpp**: `base`/`small` quantized (~60–200 MB) for messages; streaming mode for call UX.
- **Piper**: real-time-plus on mobile CPU; voices are small ONNX files → "voice packs" per character. Barge-in = VAD event → fade TTS, cancel generation.
- Sentence-chunked TTS (speak sentence 1 while sentence 2 generates) keeps call latency conversational (~1–2 s to first audio).

## Data layer

- **One encrypted SQLite file** holds everything: messages, characters, memories, vault chunks, vectors (sqlite-vec virtual tables), FTS5 indexes. Single-file design makes encrypted backup/export trivial and honors "delete means delete" (`secure_delete`, VACUUM after purges).
- **drift** (Dart) for typed schema + migrations; raw SQL escape hatch for vec/FTS queries.
- Model weights & voice packs live in app-support storage outside the DB, content-addressed (SHA-256), with a `models` table as the catalog of what's installed.

See [Database Schema](04-database-schema.md) for full DDL.

## Retrieval (memory + vault)

- Embedder: small GGUF embedding model (~384–768 dims). Chunking: ~350 tokens, 15% overlap, heading-aware for Markdown/PDF.
- Query path: hybrid — sqlite-vec cosine top-k **+** FTS5 BM25, merged via reciprocal-rank fusion, then a cheap recency/pin boost. No reranker model in v1 (latency budget).

## Packaging & platform notes

| Platform | Notes |
|---|---|
| Android | NDK build (arm64-v8a only), Vulkan with CPU fallback; Play "install-time asset" excluded — models always post-install download to keep APK < 200 MB |
| iOS | Metal; models downloaded post-install (App Store cellular limits); `increased-memory-limit` entitlement for 4B+ models; background download via `URLSession` |
| macOS | Metal; notarized DMG + App Store build |
| Windows | AVX2 CPU baseline, Vulkan/CUDA optional DLLs; MSIX + portable zip |
| Linux | Flathub Flatpak primary; AppImage secondary |

## Licensing diligence

llama.cpp / whisper.cpp (MIT), Piper (MIT, per-voice license check), sqlite-vec (Apache-2.0/MIT), SQLCipher (BSD-style community edition), Flutter (BSD). Model weights each carry their own license (Gemma Terms, Llama Community License, Apache-2.0 for Qwen/Mistral/Phi variants) — the in-app catalog stores and displays per-model license and attribution, and the curated list includes only redistributable weights.
