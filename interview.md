# Android Development Technical Interview — Requirements Spec

## Decisions

| # | Decision | Choice | Rationale |
|---|----------|--------|-----------|
| 1 | Architecture pattern | **MVVM** | Fits the flow naturally, aligns with experience |
| 2 | UI framework | **Jetpack Compose** | Modern Android standard |
| 3 | DI framework | **Koin** | Familiar with Hilt, using Koin to learn new approach |
| 4 | Request serialization | **kotlinx.serialization (`@Serializable`)** | Native Kotlin, well-documented, avoids Parcelable boilerplate |
| 5 | Outcome event mechanism | **SharedFlow on nav-graph-scoped ViewModel** | Idiomatic Compose/MVVM, matches spec language |
| 6 | Approve slider trigger | **Thumb-release at max position** | Standard swipe-to-confirm UX, no extra confirm button |
| 7 | Reject outcome | **Always error/negative** | Navigates back with error message |
| 8 | Test mocking library | **MockK** | Native Kotlin, idiomatic |
| 15 | Approve service failure snackbar color | **Pink** (same as reject) | Not specified in designs or spec; consistent with other failure states |
| 9 | Request model fields | **`id`, `title`, `description`** | Derived from Figma detail card |
| 10 | Request List content | **Logo + button only, no list** | Confirmed by Figma Home screen |
| 11 | Navigation after action | **Always navigate back** | Both outcomes return to Home and show snackbar |
| 12 | Service return type | **Sealed class (`RequestResult.Success` / `RequestResult.Error`)** | Wraps exceptions, no leaking across layers |
| 13 | ViewModel structure | **Two VMs: `RequestDetailViewModel` + `RequestSharedViewModel`** | Spec names `RequestDetailViewModel` explicitly; shared events separate |
| 14 | Min SDK | **API 29 (Android 10)** | Modern baseline, clean API access |
| 16 | Package name | **Default (as generated)** | Left as-is |
| 17 | UI setup | **Single Activity + `NavHost`** | No Fragments; `HomeScreen` and `RequestDetailScreen` are `@Composable` destinations in the `NavHost` inside `MainActivity` |

---

## Architecture

- [ ] Use **MVVM** pattern
- [ ] Use **Jetpack Compose**
- [ ] Use **Koin** for dependency injection
- [ ] Use Kotlin Coroutines and Flow for async operations and state management
- [ ] **Single Activity** (`MainActivity`) hosting a `NavHost`
- [ ] `HomeScreen` and `RequestDetailScreen` are `@Composable` destinations — no Fragments

---

## Data Model

- [ ] Define a `Request` data structure with fields: `id: String`, `title: String`, `description: String`
- [ ] `Request` must be annotated with `@Serializable` (kotlinx.serialization)

---

## Service Layer

- [ ] Define a `RequestService` interface with methods returning `RequestResult`
- [ ] Define `RequestResult` as a sealed class: `RequestResult.Success` and `RequestResult.Error(message: String)`
- [ ] Implement a concrete `MockRequestService` class
- [ ] `MockRequestService` methods must be `suspend` functions
- [ ] Use coroutine `delay()` to simulate network latency
- [ ] Use coroutine `delay()` to simulate random failure; wrap exceptions into `RequestResult.Error`

### Action behavior

| Action  | Simulation                              | Outcome                                                      |
|---------|-----------------------------------------|--------------------------------------------------------------|
| Reject  | Immediate return with success/error     | Navigate back with an appropriate error message              |
| Approve | Async call with 2-second `delay()`      | 50% random chance of failure with a descriptive error message |

---

## ViewModel

- [ ] Implement `RequestDetailViewModel` to host detail business logic; depends on `RequestService` interface
- [ ] Implement `RequestSharedViewModel` scoped to the nav graph to pass outcome events to the List screen
- [ ] `RequestDetailViewModel` must expose `isLoading` via `StateFlow`
- [ ] `RequestDetailViewModel` must expose `successMessage` via `StateFlow`
- [ ] `RequestDetailViewModel` must expose `errorMessage` via `StateFlow`
- [ ] `RequestSharedViewModel` must expose outcome events via `SharedFlow`
- [ ] Use Kotlin Coroutines and Flow internally for all async service calls
- [ ] Min SDK: API 29 (Android 10)

---

## Request List Screen

- [ ] Display a button to initiate a new request
- [ ] Button tap must use the Jetpack Navigation Component to navigate to the Detail screen
- [ ] Observe outcome events via **`SharedFlow` on a nav-graph-scoped ViewModel**
- [ ] Display a `Snackbar` after returning from the Detail screen:
  - Approve success → green, "Request approved"
  - Reject → pink, "Request rejected"
  - Approve service failure → pink, descriptive error message from service
- [ ] Snackbar display must be transient (one-shot, not re-shown on recomposition/re-entry)

---

## Request Detail Screen

- [ ] Display request details (mock data)
- [ ] Include a "Reject" button
- [ ] Include an "Approve" slider
- [ ] "Reject" button tap must call the ViewModel to process the rejection
- [ ] "Approve" slider fires on **thumb-release at max position** and calls the ViewModel to process the approval
- [ ] Show a `CircularProgressIndicator` while the async action is in progress
- [ ] Disable ALL interactions while the async action is in progress

---

## Navigation

- [ ] All navigation must use the Jetpack Navigation Component (no manual back-stack manipulation)

---

## Unit Tests

- [ ] Provide unit tests for `RequestDetailViewModel`
- [ ] Test at least one **successful approval path**, verifying state transitions
- [ ] Test at least one **failure path** (rejection or random service failure)
- [ ] Tests must use `TestDispatcher`
- [ ] Tests must use `runTest`
- [ ] Use **MockK** to mock `RequestService` and isolate ViewModel logic (no real service in tests)

---

## Code Quality

- [ ] Clean, modular, maintainable code
- [ ] Clear naming conventions
- [ ] Clear separation of layers (UI / ViewModel / Service)

---

## Submission

- [ ] Provide project as a zipped archive **or** a link to a Git repository
- [ ] Test targets must be included in the submission
