# Implementation Phases

Each phase = one PR. Each PR must build cleanly and pass its own tests before merging.

---

## Phase 1 — Project Setup & Dependencies
**Goal:** Multi-module scaffold compiles. No logic yet.

- Create modules: `:domain`, `:data`, `:feature:home`, `:feature:detail`
- Configure `settings.gradle.kts` to include all modules
- Configure each module's `build.gradle.kts` with correct dependencies
- Populate `libs.versions.toml` with all required versions:
  - Compose BOM, Navigation Compose, Koin, Kotlin Coroutines, kotlinx.serialization, MockK, Coroutines Test
- Set min SDK to API 29 across all modules
- ✅ Testable: `./gradlew build` succeeds with empty modules

---

## Phase 2 — Domain Layer
**Goal:** Core contracts defined. Pure Kotlin, no Android deps.

- `Request` data class (`id`, `title`, `description`) annotated `@Serializable`
- `RequestResult` sealed class (`Success`, `Error(message: String)`)
- `RequestService` interface (`suspend fun approve(request: Request): RequestResult`, `suspend fun reject(request: Request): RequestResult`)
- ✅ Testable: `:domain` unit tests compile and pass; models behave correctly

---

## Phase 3 — Data Layer
**Goal:** Mock service is fully implemented and tested.

- `MockRequestService` implementing `RequestService`:
  - `reject()`: returns immediately, always `RequestResult.Error("Request rejected")`
  - `approve()`: `delay(2000)`, then 50% `RequestResult.Success` / 50% `RequestResult.Error("Approval failed: <reason>")`
- Koin `:data` module binding `RequestService` → `MockRequestService`
- Unit tests for `MockRequestService` (reject always errors, approve succeeds/fails on seeded random)
- ✅ Testable: `:data` unit tests pass

---

## Phase 4 — ViewModels & Unit Tests
**Goal:** Business logic is complete and fully tested. No UI yet.

- `RequestDetailViewModel`:
  - Depends on `RequestService` interface
  - `StateFlow<Boolean> isLoading`
  - `StateFlow<String?> successMessage`
  - `StateFlow<String?> errorMessage`
  - `fun approve(request: Request)`
  - `fun reject(request: Request)`
  - On completion: delegates outcome to `RequestSharedViewModel` and sets navigate-back event
- `RequestSharedViewModel`:
  - `SharedFlow<RequestOutcome>` (`Approved`, `Rejected(message)`, `ApprovalFailed(message)`)
  - `fun emitOutcome(outcome: RequestOutcome)`
- Koin VM modules for both ViewModels
- Unit tests for `RequestDetailViewModel` using `MockK` + `TestDispatcher` + `runTest`:
  - ✅ Successful approve: `isLoading` true → false, `successMessage` set
  - ✅ Failed approve (service returns Error): `isLoading` true → false, `errorMessage` set
  - ✅ Reject: immediate, `errorMessage` set
- ✅ Testable: all VM unit tests pass

---

## Phase 5 — App Shell & Navigation
**Goal:** App launches and navigates between two stub screens.

- `Application` class with Koin init (loads all modules)
- `MainActivity` with `NavHost`
- Route definitions: `"home"`, `"detail"`
- `HomeScreen` stub (empty `Box` with route label)
- `RequestDetailScreen` stub (empty `Box` with route label)
- `RequestSharedViewModel` scoped to the nav graph
- ✅ Testable: app launches, button tap on stub navigates to detail, back returns to home

---

## Phase 6 — Home Screen
**Goal:** Home screen matches Figma and Snackbar feedback works end to end.

- Full `HomeScreen` UI: VIA logo in white circle, "Create new request" button
- Collect `RequestSharedViewModel.outcomeFlow` and show `Snackbar`:
  - Approve success → green, "Request approved"
  - Reject / approve failure → pink, descriptive message
- Snackbar is transient (one-shot via `LaunchedEffect` keyed on event)
- ✅ Testable: navigate to detail stub → trigger outcome → return to home → correct snackbar appears once

---

## Phase 7 — Detail Screen & Slider
**Goal:** Detail screen is fully functional. End-to-end flow works.

- Full `RequestDetailScreen` UI matching Figma:
  - Dark teal background
  - Request card ("Heading 1" + lorem ipsum body)
  - "Reject" button (outlined)
  - Custom `SlideToApprove` composable (draggable thumb with `>>`, fires on release at max)
- `CircularProgressIndicator` shown while `isLoading == true`
- All interactions disabled while loading
- On completion: emits to `RequestSharedViewModel`, navigates back
- ✅ Testable: full approve/reject flow runs, loading state visible, snackbar shown on home

---

## Phase 8 — Theming & Polish
**Goal:** UI matches Figma colours and typography. Code is clean.

- Define `Color` constants matching Figma (teal primary, light blue background, pink/green snackbar)
- Apply `MaterialTheme` with correct palette
- Typography for title, body, button labels
- Remove any placeholder/stub code
- Final review against `interview.md` checklist — all boxes ticked
- ✅ Testable: all unit tests pass, UI visually matches Figma
