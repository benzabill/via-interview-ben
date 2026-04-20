# via-interview

Android technical interview task: a Request approval app built with MVVM + Jetpack Compose.

**Repository:** https://github.com/benzabill/via-interview-tseytlin/tree/main

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

## Build

```
./gradlew build
```

## Key Documents

- [`interview.md`](./interview.md) — requirements spec distilled from the brief, with decisions locked in
- [`PHASES.md`](./PHASES.md) — incremental PR plan, one phase per PR
- [`BEN_PROCESS.md`](./BEN_PROCESS.md) — running log of prompts/commands driving the work
