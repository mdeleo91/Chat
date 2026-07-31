# PocketAI

> *"An AI companion should feel like someone in your contacts list, not a website."*

**PocketAI** is a privacy-first, fully offline AI messaging application. It looks and feels like iMessage, WhatsApp, or Signal — a conversation list, chat bubbles, typing indicators, voice notes, group chats — except every contact is a local AI character running entirely on your device.

- **100% local.** No accounts, no cloud, no telemetry, no internet after install.
- **No subscriptions.** One-time purchases only.
- **Feels like texting.** Not "using AI."

## Design Documentation

| # | Document | Contents |
|---|----------|----------|
| 1 | [Product Requirements Document](docs/01-prd.md) | Vision, personas, feature specs, non-goals, success metrics |
| 2 | [Wireframes](docs/02-wireframes.md) | Annotated wireframes for every main screen |
| 3 | [Tech Stack](docs/03-tech-stack.md) | Recommended stack with alternatives considered |
| 4 | [Database Schema](docs/04-database-schema.md) | Full SQLite + sqlite-vec schema with DDL |
| 5 | [System Architecture](docs/05-architecture.md) | Layer diagram, data flows, pipelines (Mermaid) |
| 6 | [MVP Roadmap](docs/06-mvp-roadmap.md) | Milestone-by-milestone scope from M0 to launch |
| 7 | [Monetization Strategy](docs/07-monetization.md) | Revenue without subscriptions or ads |
| 8 | [Risks & Technical Challenges](docs/08-risks.md) | Ranked risks with mitigations |
| 9 | [Six-Month Development Plan](docs/09-six-month-plan.md) | Week-by-week plan, team shape, exit criteria |
| 10 | [UI Mockups & Design Language](docs/10-ui-mockups.md) | Visual identity spec + interactive mockups |

## Interactive Mockups

Open [`mockups/pocketai-mockups.html`](mockups/pocketai-mockups.html) in any browser — a self-contained, dependency-free page showing the conversation list, chat screen, group chat, character editor, and voice call screens in the PocketAI design language ("Dusk"), in both light and dark themes.

## The One-Paragraph Pitch

People want AI companionship and assistance without surrendering their private conversations to a server. PocketAI packages open local models (Gemma, Llama, Qwen, Phi, Mistral) inside a messaging app so polished that using it feels like texting friends: a Dungeon Master who remembers your campaign, a writing partner who knows your novel, a chef who knows your pantry — all with their own memories, voices, and personalities, all living entirely in your pocket.
