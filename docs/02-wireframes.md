# PocketAI — Wireframes

Low-fidelity, annotated wireframes for the main screens. Mobile-first (390×844 reference); the desktop adaptation is described at the end. High-fidelity versions live in [`mockups/pocketai-mockups.html`](../mockups/pocketai-mockups.html).

---

## 1. Home — Conversation List

```
┌─────────────────────────────────────┐
│  PocketAI                    ⊕  ⚙   │  ← wordmark left; New Chat (⊕), Settings (⚙)
│  ┌───────────────────────────────┐  │
│  │ 🔍  Search chats & messages   │  │  ← global FTS search
│  └───────────────────────────────┘  │
│                                     │
│  📌 PINNED                          │
│  ┌───┐  Nova ✍️              21:42  │
│  │ N │  Sure — want me to punch    │  ← avatar, name, last-msg preview,
│  └───┘  up the ending?          ●  │     timestamp, unread dot
│                                     │
│  ALL CHATS                          │
│  ┌───┐  Dungeon Master       19:03 │
│  │ 🐉│  The gates of Ironhold      │
│  └───┘  creak open...              │
│  ┌───┐  Fantasy Writing Team 18:11 │
│  │⚔️👥│  Elias: The siege engine…  │  ← group: stacked avatars,
│  └───┘                             │     "Sender: preview" format
│  ┌───┐  Chef Rosa            Tue   │
│  │ 🍳│  🎤 0:23                    │  ← voice note preview
│  └───┘                             │
│  ┌───┐  Coach Kai            Mon   │
│  │ 💪│  Rest day today. You        │
│  └───┘  earned it 👊               │
│                                     │
│         ( ✉️  Chats | 👥 Cast |     │  ← bottom tabs: Chats,
│           📚 Vault  | ⚙ Settings )  │     Cast (characters), Vault, Settings
└─────────────────────────────────────┘
```

**Notes**
- Swipe actions on rows: left = mute/pin, right = archive/delete (destructive confirm).
- Empty state (first run, post-download): warm illustration + "Say hi to your starter cast" with 3 suggested characters.
- No model names, token counts, or "AI" framing anywhere on this screen.

---

## 2. Chat Screen (1:1)

```
┌─────────────────────────────────────┐
│ ‹  ┌──┐  Nova                  📞 ⋮ │ ← back; avatar+name tap → contact card;
│    └──┘  brewing metaphors ☕       │    call (voice mode); overflow menu
│─────────────────────────────────────│
│        ── Today ──                  │ ← day separator
│  ┌─────────────────────────┐        │
│  │ Morning! Did you get a  │        │ ← character bubble, left,
│  │ chance to read ch. 4?   │        │    surface-tint, no avatar in 1:1
│  └─────────────────────────┘        │
│         ┌────────────────────────┐  │
│         │ Yes — the pacing drags │  │ ← user bubble, right,
│         │ in the middle though   │  │    accent gradient fill
│         └────────────────────────┘  │
│  ┌─────────────────────────┐        │
│  │ Agreed. The dinner scene│        │
│  │ runs 3 pages before any │        │
│  │ tension lands. Options: │        │
│  │ cut, or move the phone  │        │
│  │ call earlier. 🎯        │❤️      │ ← reaction chip (long-press → tapback)
│  └─────────────────────────┘        │
│  ● ● ●                              │ ← typing indicator (pre-stream)
│─────────────────────────────────────│
│  ⊕  ┌────────────────────┐  🎤 │➤│ │ ← attach (photos/files), input,
│     │ Message…           │        │    mic (hold = voice note), send
│     └────────────────────┘        │
└─────────────────────────────────────┘
```

**Notes**
- Streaming: typing indicator shows during prefill; bubble then fills word-by-word at a throttled, human cadence. A subtle "…" tail marks in-progress.
- Long-press a character message: React · Copy · Ask to rephrase · Rewind here · Delete. ("Ask to rephrase" is regeneration in human clothes.)
- Long-press own message: Edit (regenerates the reply) · Copy · Delete.
- Attachments sheet: Photo library · Camera · Document (→ summarize/discuss) · Vault item.
- Overflow ⋮: Search in chat · Wallpaper · Mute · View memories · Clear chat.

---

## 3. Contact Card (Character Profile)

```
┌─────────────────────────────────────┐
│ ‹                              Edit │
│            ┌────────┐               │
│            │ avatar │               │
│            └────────┘               │
│              Nova                   │
│      ✍️ Writing partner             │ ← role line
│   "brewing metaphors ☕"            │ ← status
│   AI character · runs on-device    │ ← honest, quiet disclosure
│                                     │
│  ┌─────────┐ ┌─────────┐ ┌────────┐ │
│  │ 💬 Chat │ │ 📞 Call │ │ 🔍 Srch│ │
│  └─────────┘ └─────────┘ └────────┘ │
│                                     │
│  ABOUT                              │
│  Sharp-eyed editor with a warm     │
│  streak. Loves structure talk,     │
│  hates adverbs.                     │
│                                     │
│  🧠 Memories                   47 › │ ← memory browser entry
│  📚 Vault access        Novel dir › │
│  🎙 Voice               "Ember"   › │
│  🎨 Chat wallpaper                › │
│                                     │
│  Export character  ·  Clear chat    │
│  Forget everything  ·  Delete       │ ← destructive zone
└─────────────────────────────────────┘
```

---

## 4. Memory Browser

```
┌─────────────────────────────────────┐
│ ‹  Nova's memories            + Add │
│  ┌───────────────────────────────┐  │
│  │ 🔍 Search memories            │  │
│  └───────────────────────────────┘  │
│  📌 PINNED                          │
│  ┌───────────────────────────────┐  │
│  │ Working on novel "Salt &     │  │
│  │ Static", literary thriller   │  │
│  │ set in Lisbon        📌 ✎ 🗑 │  │
│  └───────────────────────────────┘  │
│  RECENT                             │
│  ┌───────────────────────────────┐  │
│  │ Prefers blunt feedback over  │  │
│  │ praise sandwiches     ✎ 🗑   │  │
│  │ from chat · Jul 28            │  │ ← provenance + date
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │ Ch. 4 dinner scene needs     │  │
│  │ trimming              ✎ 🗑   │  │
│  └───────────────────────────────┘  │
│                                     │
│  Export memories   ·   Forget all   │
└─────────────────────────────────────┘
```

Each card: pin toggle, inline edit, delete. Tap provenance → jumps to source message.

---

## 5. Character Creation Wizard (Cast tab → ⊕)

Three steps + optional Advanced. Progressive disclosure keeps it non-technical.

```
 Step 1 · Identity            Step 2 · Personality          Step 3 · Voice & Look
┌───────────────────┐        ┌───────────────────┐        ┌───────────────────┐
│ ‹ New character   │        │ ‹ Personality     │        │ ‹ Finishing touch │
│   ┌──────┐        │        │ Describe them     │        │ Voice   ◉ Ember   │
│   │avatar│  📷 🎲 │        │ ┌───────────────┐ │        │         ○ Brook   │
│   └──────┘        │        │ │free text or   │ │        │         ▷ preview │
│ Name              │        │ │pick traits:   │ │        │ Creativity        │
│ ┌───────────────┐ │        │ │[warm][blunt]  │ │        │ ├────●──────┤     │
│ │               │ │        │ │[funny][formal]│ │        │ grounded↔dreamy   │
│ └───────────────┘ │        │ └───────────────┘ │        │ First message     │
│ Role/tagline      │        │ Speaking style    │        │ ┌───────────────┐ │
│ ┌───────────────┐ │        │ ┌───────────────┐ │        │ │auto-suggested │ │
│ │e.g. Chef      │ │        │ │short, dry,    │ │        │ └───────────────┘ │
│ └───────────────┘ │        │ │uses emoji     │ │        │                   │
│ Status message    │        │ └───────────────┘ │        │ ▸ Advanced        │
│ ┌───────────────┐ │        │ Backstory &       │        │  (system prompt,  │
│ │               │ │        │ interests (opt.)  │        │   memory rules,   │
│ └───────────────┘ │        │                   │        │   model override) │
│        Next ›     │        │        Next ›     │        │  Create & say hi  │
└───────────────────┘        └───────────────────┘        └───────────────────┘
```

**Notes**
- 🎲 on avatar = generated monogram/gradient; personality traits are chips that compose into the prompt.
- "Create & say hi" drops you straight into the chat with the character's greeting — instant payoff.
- Advanced sheet is where the Tinkerer lives: raw system prompt, memory rules, per-character model.

---

## 6. Group Chat

```
┌─────────────────────────────────────┐
│ ‹ ⚔️ Fantasy Writing Team        ⋮ │
│    Elias, Mira, DM & You            │
│─────────────────────────────────────│
│ ┌──┐ Elias · Editor                 │ ← sender name + role, tinted
│ └──┘┌───────────────────────┐       │    bubble edge per character
│     │ The siege chapter     │       │
│     │ needs a ticking clock.│       │
│     └───────────────────────┘       │
│ ┌──┐ Mira · Historian               │
│ └──┘┌───────────────────────┐       │
│     │ Medieval sieges took  │       │
│     │ months — your 3-day   │       │
│     │ deadline is generous! │       │
│     └───────────────────────┘       │
│          ┌────────────────────────┐ │
│          │ @DM what would the     │ │ ← @mention forces
│          │ defenders actually do? │ │    that character to answer
│          └────────────────────────┘ │
│ ┌──┐ DM is typing…                  │
│─────────────────────────────────────│
│  ⊕  ┌──────────────────┐  🎤  │➤│  │
└─────────────────────────────────────┘
```

**Notes**
- Director caps character-to-character exchanges (default: max 2 AI turns after a user message, then wait). "Let them keep talking" affordance appears when capped.
- Group info sheet: members (tap → contact card), add/remove characters, group memory toggle.

---

## 7. Knowledge Vault

```
┌─────────────────────────────────────┐
│  Vault                       + Add  │
│  ┌───────────────────────────────┐  │
│  │ 🔍 Ask your documents…        │  │ ← semantic search
│  └───────────────────────────────┘  │
│  FOLDERS                            │
│  📁 Campaign Notes      12 items › │
│  📁 Novel — Salt & Static 8 items ›│
│  📁 Recipes              23 items ›│
│  RECENT                             │
│  📄 ironhold-map-notes.md          │
│      indexed ✓ · DM has access     │ ← index status + access grants
│  📄 chapter-04.pdf                 │
│      indexing… ▓▓▓░░               │
│─────────────────────────────────────│
│  ( ✉️ | 👥 | 📚 | ⚙ )              │
└─────────────────────────────────────┘
```

Item detail: preview, which characters have access (toggle list), re-index, delete.

---

## 8. Voice Call Mode

```
┌─────────────────────────────────────┐
│                                     │
│            ┌────────┐               │
│            │ avatar │  ← breathing  │
│            └────────┘     glow ring │
│              Nova                   │
│           listening…                │ ← state: listening / thinking
│                                     │    / speaking
│        ~ ~ ▂▄▆▄▂ ~ ~                │ ← live waveform
│                                     │
│   "…so if the phone call moves     │
│    to page two, the dinner scene   │ ← live transcript (collapsible)
│    becomes the payoff…"            │
│                                     │
│   ┌────┐    ┌────┐    ┌────┐        │
│   │ 🔇 │    │ ⏹  │    │ 💬 │        │ ← mute · end · switch to text
│   └────┘    └────┘    └────┘        │
└─────────────────────────────────────┘
```

Barge-in: user speaking while character talks fades TTS out and yields the turn.

---

## 9. First-Run / Model Onboarding

```
 Screen 1 · Welcome           Screen 2 · Pick brain          Screen 3 · Download
┌───────────────────┐        ┌───────────────────┐        ┌───────────────────┐
│                   │        │ Choose how your   │        │ Setting things up │
│   (illustration)  │        │ companions think  │        │                   │
│  Your AI friends, │        │ ┌───────────────┐ │        │   ▓▓▓▓▓▓░░░ 64%   │
│  entirely on your │        │ │◉ Recommended  │ │        │  2.1 of 3.2 GB    │
│  phone.           │        │ │  for this     │ │        │                   │
│                   │        │ │  phone (3.2GB)│ │        │  Meanwhile, meet  │
│  No account.      │        │ │  Quick & smart│ │        │  your starter     │
│  No cloud.        │        │ ├───────────────┤ │        │  cast:            │
│  No subscription. │        │ │○ Light (1.8GB)│ │        │  🐉 Dungeon Master│
│                   │        │ │  Fastest      │ │        │  ✍️ Nova          │
│  [ Get started ]  │        │ ├───────────────┤ │        │  🍳 Chef Rosa …   │
│                   │        │ │○ Max (4.7GB)  │ │        │                   │
│  100% offline ✓   │        │ │  Deepest      │ │        │ (download resumes │
│                   │        │ └───────────────┘ │        │  in background)   │
└───────────────────┘        └───────────────────┘        └───────────────────┘
```

Only network-touching flow in the app. Wi-Fi recommended banner; resumable; cast browsing while downloading converts wait time into anticipation.

---

## 10. Settings (top level)

```
┌─────────────────────────────────────┐
│  Settings                           │
│  🎨 Appearance      theme, wallpapers│
│  🧠 AI Brain        models, storage  │ ← the ONLY tech-flavored area
│  🎙 Voice           STT/TTS packs    │
│  🔒 Privacy         app lock, export,│
│                     wipe, network log│
│  👤 About You       shared profile   │
│  💜 PocketAI Pro    one-time unlock  │
│  ⓘ  About          licenses, version │
└─────────────────────────────────────┘
```

---

## Desktop Adaptation (macOS / Windows / Linux)

```
┌──────────────┬──────────────────────────────────────────────┐
│ PocketAI  ⊕  │  Nova — brewing metaphors ☕            📞 ⋮ │
│ ┌──────────┐ │ ──────────────────────────────────────────── │
│ │🔍 Search │ │                    (chat as mobile, wider    │
│ └──────────┘ │                     bubbles max ~60% width)  │
│ ● Nova 21:42 │                                              │
│   DM   19:03 │                                              │
│   Team 18:11 │                                              │
│   Rosa  Tue  │ ──────────────────────────────────────────── │
│   Kai   Mon  │  ⊕  [ Message…                    ]  🎤  ➤  │
└──────────────┴──────────────────────────────────────────────┘
```

- Two-pane ≥ 700 px; optional third pane (contact card / memories) ≥ 1100 px.
- Keyboard: ↑/↓ switch chats, ⌘K quick-switcher, Enter send / Shift-Enter newline.
- Same codebase, adaptive layout — not a separate app.
