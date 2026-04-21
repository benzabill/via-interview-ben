# FOLLOWUP

Work that's out of scope for this branch but would be the natural next steps.
Grouped by theme, roughly ordered by value.

## Lifecycle & process death

- **Persist in-flight state across process death.** `RequestDetailViewModel` currently
  loses `isLoading` / staged messages if the OS kills the process during a service
  call. Wire `SavedStateHandle` into the VM so the screen restores to the same
  in-flight state after recreation. The pending request id is the key piece of
  state to save; the actual RPC would need to be re-issued (or the service would
  need an idempotency token so we can poll for the prior call's result).
- **Don't cancel the request on backgrounding, but do cancel on navigate-away.**
  `viewModelScope` already ties the coroutine to VM lifetime, so config changes
  and backgrounding are safe. The concern is: if the user pops the detail screen
  while a call is in flight, today the VM is cleared and the call is silently
  cancelled. The current behavior is acceptable for this flow (we block system
  back while loading), but if the product later wants "fire-and-forget" approvals
  we'd hoist the call out of VM scope into an app-scoped coroutine plus a
  small operation tracker.
- **Reproduce & test process death.** Add a developer-mode setting or a Gradle
  `run --kill-after` recipe so we can deterministically exercise the save/restore
  path in QA. A Compose UI test with `StateRestorationTester` covers the
  configuration-change case but not full process death.

## Network & error handling

- **Replace `MockRequestService` with a real client.** Retrofit or Ktor, with a
  dedicated `:network` module that owns the HTTP client, interceptors, and a
  mapping layer between transport DTOs and domain models. Keep the
  `RequestService` interface in `:domain` so swapping implementations stays
  trivial.
- **Model failure modes properly.** `RequestResult.Error` currently carries a
  single string. Split into a sealed hierarchy: `Network`, `Timeout`, `Server(code)`,
  `Unknown(throwable)`. This lets the UI decide "show a retry button" vs "log the
  user out" vs "show a generic error."
- **Retry policy at the repository layer.** Exponential backoff with jitter for
  transient errors; no retry for 4xx. Implemented inside `DefaultRequestRepository`
  (or a `RetryingRequestRepository` decorator) so VMs stay ignorant of retry logic.
- **Connectivity awareness.** Observe `ConnectivityManager` as a flow; when
  offline, short-circuit to a friendly error without making the call. Useful
  once the service is real, not a mock.
- **Timeouts and cancellation budgets.** Wrap every service call in `withTimeout`
  so a hung request can't pin `isLoading=true` forever. Today the 2s `delay` in
  the mock masks this gap.

## Dependency injection

- **Inject `CoroutineDispatcher` via Koin.** Bind `Dispatchers.IO` and `.Default`
  as named singles; have the repository/service take the dispatcher as a
  constructor arg. Makes VM tests deterministic without relying on the service's
  internal `delay` and unifies dispatcher selection across the codebase.
- **Scope `RequestSharedViewModel` to the nav graph, not the Activity.** Today
  it's obtained in `NavGraph` via `koinViewModel()`, which binds it to the
  Activity's `ViewModelStoreOwner`. When the outcome bus grows (or if we ever
  host a second nav graph), a Koin scope keyed to the nav entry keeps lifetimes
  tight.
- **Module-graph verification test.** Add a `koin-test` `checkModules()` run in
  the `:app` module so a missing binding fails unit tests instead of crashing on
  launch.
- **Compose previews hitting Koin.** Today previews of `HomeScreen` / detail
  would fail because Koin isn't started. Create a `@Preview`-only Koin
  initialization (or pass VMs in explicitly for previews) so the design-time
  tooling isn't broken.

## Architecture & navigation

- **Request list screen and typed nav routes.** The single hardcoded sample
  Request currently lives in `DefaultRequestRepository`. Real flow: Home shows
  a list from the repo, tap -> Detail by id. Use `kotlinx.serialization`-backed
  typed routes (`@Serializable object Home`, `@Serializable data class Detail(val id: String)`)
  instead of string destinations.
- **Promote the repository's data model.** Once Detail takes an id, the VM
  needs `repository.requestById(id): Flow<Request>` — and we'll want caching,
  likely a `Room` database as the source of truth, with the network as a
  write-through.
- **Outcome bus rework.** `RequestSharedViewModel` with a `SharedFlow(replay=1)`
  + `resetReplayCache()` is working but brittle. Compose-Nav 2.8 supports
  `savedStateHandle` on the previous back-stack entry; passing the outcome
  that way removes the need for a shared VM entirely.
- **Use cases / interactors.** At current scale the repo is thin enough that VMs
  calling it directly is fine. If business rules grow (e.g., "rejection requires
  a reason over 20 chars"), extract `ApproveRequestUseCase` / `RejectRequestUseCase`
  in `:domain` so VMs stay presentation-only.

## Testing

- **Compose UI tests with Koin overrides.** `KoinTestRule` + a test module that
  binds a fake `RequestRepository` → drives the Detail screen through approve /
  reject / error paths without launching the real service. The Koin-swap
  pattern already shown in `RequestDetailViewModelKoinTest` extends naturally.
- **Screenshot tests for the Figma-matched surfaces.** Paparazzi or Roborazzi on
  the snackbar, slide-to-approve, and detail card so visual regressions
  (padding, colors, spacing) are caught in CI instead of by eye.
- **Concurrency stress test.** Property-based test that fires N taps with
  random delays against the VM to confirm the in-flight guard holds under
  arbitrary interleavings.

## Code quality & tooling

- **Formatter + linter.** ktlint or Spotless on pre-commit, detekt for static
  analysis. Keeps the style we've been landing consistent without manual review.
- **Strings resource.** Hardcoded `"Request approved"`, `"Slide to approve"`,
  etc. move to `strings.xml` in the feature modules so they're localizable and
  swappable by product.
- **Accessibility.** Explicit `contentDescription` on the VIA logo and slider
  thumb; dynamic type support; confirm TalkBack reads the outcome snackbar.
- **Timber + crash reporting.** Route all logs through Timber; plant a
  Crashlytics (or similar) tree in release builds with breadcrumbs for the
  approve/reject flow.
- **CI pipeline.** GitHub Actions workflow running `./gradlew check` plus
  lint/detekt on every PR. Assemble a debug APK as an artifact for manual QA.

## Koin-specific polish

- **Annotations + KSP.** If the module list grows past ~5, `koin-annotations`
  pays for itself: `@Single`, `@Factory`, `@KoinViewModel` replace the hand-written
  DSL. At current size it's not worth the build-time cost.
- **`androidLogger()` in debug only.** Adding `androidLogger(Level.DEBUG)` inside
  `startKoin` surfaces binding issues faster during development. Guard with
  `BuildConfig.DEBUG` so release builds stay quiet.
