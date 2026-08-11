# The reply engine: Z.ai's GLM API

Every reply in the app comes from [Z.ai](https://z.ai)'s hosted GLM models over
their OpenAI-style API, authenticated with an API key pasted into Settings →
**Change model**. There is no on-device model, no local server and no scripted
fallback — if the API is unreachable the app says so instead of answering.
Pay-per-token; create a key and top up at the Z.AI Open Platform.

Three model slots, all editable in Settings so newer ids work without a code
change:

| Slot | Default | Used for |
|---|---|---|
| Text | `glm-5.2` | every ordinary reply, and the rolling summaries |
| Vision | `glm-5v-turbo` | any request while a photo you sent is still in the history window |
| Image | `glm-image` | pictures characters send back (billed per image) |

## What the API receives

Built in `sysPrompt()` and `buildMessages()`:

- the character's name, tagline, personality, speaking style, description and
  interests
- your own persona — name and description, from Settings → Your persona
  (the description can be withheld with the toggle there; the name always goes)
- that character's stored memories (pinned first, then most recent)
- group-chat context, when the conversation is a group
- the rolling summary plus the un-summarized tail of the conversation
- photos you've sent, as base64 `image_url` content parts — these switch the
  request to the vision model until they leave the history window

Sampling is `temperature 0.85`, `top_p 0.95`, `max_tokens 512`, with presence
and frequency penalties at `0.3` to stop the repetition loops that roleplay
fine-tunes fall into.

## Characters sending photos

The system prompt teaches the model two tags, emitted at the end of a reply
and stripped from the visible text: `[photo: description]` and
`[selfie: description]` (the model picks selfie only when it would be in
frame). The description is rendered by the image model and delivered as its
own picture bubble; the description — not the pixels — stays in history, so
the character remembers what it showed.

With mature mode on, characters may additionally mark an individual picture
`[selfie 18+: …]` / `[photo 18+: …]` when the moment is itself intimate —
only those photos lift fal's safety checker and match `18+`-gated LoRAs;
everyday pictures during a mature chat stay tame. With mature mode off the
marker is ignored.

- Generated shots come out portrait with a "throwaway phone snapshot" style
  baked into the prompt; a character's **Photo style** field replaces that
  default.
- Selfies use the character's **Selfie references** (up to three photos in
  the editor) as identity references, for the same face every time. No
  references → plain text-to-image, so put appearance in Photo style in that
  case. A rejected reference request falls back the same way and logs the
  API's error to Settings → Troubleshooting logs.
- The app downloads each generated image immediately and stores downscaled
  pixels in local storage — hosted URLs expire and would leave broken
  bubbles.

## FLUX via fal.ai (optional)

Add a [fal.ai](https://fal.ai) API key in Settings and photos switch from GLM
to FLUX — `fal-ai/flux-lora` for ordinary photos, `fal-ai/flux-kontext-lora`
for selfies with references (the first reference rides as `image_url`) — with
**LoRA support** on both paths:

- **LoRAs** (Settings and per character in the editor), one per line:
  `URL [strength] [when: keyword, keyword…] [add: trigger words]`. Without
  `when:` a LoRA applies to every photo — right for style LoRAs (an
  amateur-photography LoRA is the single biggest "looks like a real phone
  photo" upgrade), wrong for subject LoRAs, which would bleed into every
  shot. With `when:` the LoRA loads only when the photo's description
  mentions a keyword, and `add:` rides its trigger words along on exactly
  those shots. Character-editor LoRAs stack on top of the Settings ones —
  a likeness LoRA there gives better identity consistency than reference
  photos. A global trigger-words box covers the always-on style LoRAs.

Images are billed per generation on the fal account; text replies stay on the
Z.ai key. If a FLUX call fails, the photo falls back to GLM (when a Z.ai key
is present) and the error lands in the troubleshooting log.
