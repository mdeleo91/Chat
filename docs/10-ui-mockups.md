# PocketAI — UI Mockups & Design Language

Interactive high-fidelity mockups: **[`mockups/pocketai-mockups.html`](../mockups/pocketai-mockups.html)** — a self-contained page (no dependencies, works offline, naturally) rendering five key screens in light and dark themes. Open it in any browser.

## Design Language: **"Dusk"**

The familiar grammar of iMessage/WhatsApp — bubbles, tails, avatars, list rows — with an identity that is unmistakably not either of them. Where WhatsApp is daylight-green utility and iMessage is corporate blue, Dusk is **the hour you text your closest people**: deep ink surfaces, warm violet-to-ember light.

### Color

| Token | Light | Dark | Use |
|---|---|---|---|
| `ink` | `#14121F` | `#EDEBF6` | Primary text |
| `surface` | `#F7F5FB` | `#0E0D16` | App background |
| `card` | `#FFFFFF` | `#1A1826` | Bubbles (incoming), rows, sheets |
| `dusk-500` | `#7C5CFF` | `#8F73FF` | Primary accent (violet) |
| `ember-400` | `#FF7A59` | `#FF8A6B` | Secondary accent (warm coral) |
| `dusk-gradient` | `linear(135°, #7C5CFF → #B15CFF → #FF7A59)` | same | **User bubbles**, primary buttons, avatar rings |
| `mist` | `#8B87A0` | `#7E7A96` | Timestamps, previews, metadata |
| `alive` | `#3DD68C` | `#3DD68C` | "online"/ready dots, success |

- **Signature element — the Dusk gradient user bubble.** Outgoing messages carry the violet→ember gradient; it is the brand, visible in every screenshot anyone ever posts.
- Each character owns an **accent hue** (from a curated 12-hue wheel) used on their avatar ring, group-bubble edge, and contact card — characters feel like individuals at a glance.
- Dark theme is the flagship (the app is called *Pocket*AI; it lives in evening hands). Light theme is a first-class sibling, not an afterthought.

### Shape & depth
- Bubbles: 20 px radius, 6 px on the tail corner; max width 78% (mobile) / 60% (desktop).
- Cards/sheets: 16 px radius; soft single-layer shadows (no material stacking).
- Avatars: circles with a 2 px character-accent ring; groups show a stacked pair.

### Type
- System stack (SF Pro / Roboto / Segoe) — messaging apps should feel native, not branded-by-font. Wordmark only in a rounded semibold.
- Sizes: 17/16 message text (platform-respecting), 13 metadata, dynamic-type compliant.

### Motion & feel
- Streaming text fills word-by-word at a paced, human cadence with a soft caret shimmer — never raw token stutter.
- Typing indicator: three dots breathing in the character's accent hue.
- Send: bubble springs from the composer (280 ms, overshoot 1.02); tapbacks pop with haptic.
- Voice call screen: avatar wears a slow "breathing" gradient ring — listening (cool violet) vs speaking (warm ember).

### Voice & tone (microcopy)
- The app never says "model," "tokens," "context," "generate," or "AI response."
- Downloads are "your companion's brain." Throttling is "Nova is taking a breather." Regeneration is "Ask to rephrase."
- Honest where it matters: every contact card quietly states *"AI character · runs on your device."* Charm never crosses into deception.

## Screens included in the mockup file

1. **Conversation list** — pinned chats, unread dots, group row, voice-note preview, bottom tabs.
2. **1:1 chat (Nova)** — gradient user bubbles, incoming cards, reaction chip, typing indicator, composer with mic/attach.
3. **Group chat (Fantasy Writing Team)** — per-character accent edges, sender labels, @mention pill.
4. **Character creation (Step 2)** — trait chips, speaking-style field, progressive disclosure of Advanced.
5. **Voice call (Nova)** — breathing avatar ring, live transcript, mute/end/text controls.

Each screen renders in a device frame, side by side in dark and light, with annotation captions. The page is itself written in the Dusk system, so it doubles as a living style sheet.
