# via-interview

- Android technical interview task: a Request approval app built with MVVM + Jetpack Compose.
- Built with Claude CLI. Every prompt I sent was logged verbatim to
  [`BEN_PROCESS.md`](./BEN_PROCESS.md) via a `CLAUDE.md` instruction, so the
  transcript of what I actually asked — decisions, corrections, follow-ups —
  is auditable alongside the code.
- Claude is good but not perfect, it requires a close eye for proper polish and performance and so there was definitely some good old fashioned looking closely at spec, figma and actual app output and guide the landing to a nice final product.

## Key Documents

- [`REQUIREMENTS.md`](./REQUIREMENTS.md) — requirements spec distilled from the brief, with decisions
- [`PHASES.md`](./PHASES.md) — incremental PR plan, one phase per PR
- [`BEN_PROCESS.md`](./BEN_PROCESS.md) — running log of prompts/commands driving the work

## Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose (single Activity + `NavHost`)
- **Architecture:** MVVM with Kotlin Coroutines + Flow
- **DI:** Koin
- **Serialization:** kotlinx.serialization
- **Testing:** MockK, Coroutines Test
- **Min SDK:** API 29 (Android 10)

## Modules

| Module | Purpose |
|--------|---------|
| `:app` | Application entry, `MainActivity`, `NavHost` wiring, Koin bootstrap |
| `:domain` | Pure-Kotlin contracts: `Request`, `RequestResult`, `RequestService` |
| `:data` | `MockRequestService` and Koin data module |
| `:feature:home` | `HomeScreen` composable + ViewModel |
| `:feature:detail` | `RequestDetailScreen`, `RequestDetailViewModel`, shared outcome events |

