# Ben's Process Log

All commands and inputs tracked with timestamps.

---

| Timestamp (UTC) | Input |
|-----------------|-------|
| 2026-04-20T00:00:00Z | `/Users/btseytlin/Downloads/Android\ Developer\ Interview\ Task\ \(1\).pdf internalize this PDF, it will be our source of truth, convert it to a interview.md spec document with each and every hard requirement` |
| 2026-04-20T00:00:00Z | `additionally, track within BEN_PROCESS.md every command that i am inputting with timestamp` |
| 2026-04-20T00:00:00Z | `use CLAUDE.md to ensure we're updating BEN_PROCESS.md` |
| 2026-04-20T00:00:00Z | `Run me through the document and ask me any decisions we need to make` |
| 2026-04-20T00:00:00Z | `1. MVVM, as that's more of my experience and fits nicely here / 2. Compose / 3. Koin / 4. Serializable with kotlinx serialization / 5. question about SharedFlow vs SavedStateHandle / 6. Show me where in spec / 7. Always error/negative / 8. MockK` |
| 2026-04-20T00:00:00Z | `5. Yes i would have recommended SharedFlow. 6. Fire on thumb release at max position launches it` |
| 2026-04-20T00:00:00Z | `Continue running through the spec so I can make more decisions` |
| 2026-04-20T00:00:00Z | `before we answer these, here are the designs: /Users/btseytlin/Downloads/via_figma_designs.pdf` |
| 2026-04-20T00:00:00Z | `12. Let's wrap exceptions into an Error sealed class / 13. Separate VM / 14. Let's do Android 10+` |
| 2026-04-20T00:00:00Z | `15. Either success or failure. How would there be a 50% failure? Sliders are on or off.` |
| 2026-04-20T00:00:00Z | `also pink unless it was specified in the requirements` |
| 2026-04-20T00:00:00Z | `Continue through the spec, any other open questions?` |
| 2026-04-20T00:00:00Z | `16. leave the package name / 17. let's decide what the UI setup will be, single activity, multi activity, fragment, etc.` |
| 2026-04-20T00:00:00Z | `Yes let's do this. Request List Screen and Request Detail Screen will need to be accommodated.` |
| 2026-04-20T00:00:00Z | `I want to design the modules so that we have proper separation, we'll have an app module, but then organize like network layer for making GET/POST requests, what else do we have to modularize?` |
| 2026-04-20T00:00:00Z | `yes this works. Let's start by creating a private github PR via-interview-tseytlin` |
| 2026-04-20T23:34Z | `Keep going` |
| 2026-04-20T23:36Z | `yes, also i've merged the earlier phases so lets stay up to date as we move forward on phases.` |
| 2026-04-20T23:36Z | `yes, also i've merged the earlier phases so lets stay up to date as we move forward on phases.` |
| 2026-04-20T23:41Z | `yes go` |
| 2026-04-20T23:46Z | `can we implement phase 6 or any of the other phases in parallel?` |
| 2026-04-20T23:46Z | `implement phase 6` |
| 2026-04-20T23:47Z | `continue` |
| 2026-04-20T23:47Z | `start a git work tree so we can in parallel : implement phase 6` |
| 2026-04-20T23:47Z | `implement phase 7` |
| 2026-04-20T23:47Z | `start a git work tree so we can in parallel : implement phase 7` |
| 2026-04-20T23:55Z | `continue the phase, pr creation` |
| 2026-04-20T23:55Z | `continue phase 7` |
| 2026-04-21T00:09Z | `are you able to look at the figma specs on the screens? https://www.figma.com/design/pouKokTVOWcW04FJJIu755/VIA-Interview?node-id=0-1&p=f&t=EhBUmkWXLE6OpUUY-0` |
| 2026-04-21T00:10Z | `can we do figma mcp through claude code terminal` |
| 2026-04-21T00:14Z | `pull down main` |
| 2026-04-21T00:13Z | `on a separate git worktree here, new pr for figma polish` |
| 2026-04-21T00:14Z | `/Users/btseytlin/Downloads/VIA Logo 1.svg here is the logo put it inside of a white circle with these dimens width: 238; height: 238; top: 231px; left: 81px; angle: 0 deg; opacity: 1;` |
| 2026-04-21T00:15Z | `create new request button should have these properties width: 331; height: 48; top: 537px; left: 41px; border-radius: 12px; border-width: 1px; background: #285976; border: 1px solid #87D6CD box-shadow: 0px 3px 3px 0px #00000029;` |
| 2026-04-21T00:14Z | `I think we are missing the concept in the original spec that gets use to Request Approved Request Rejected snackbar` |
| 2026-04-21T00:15Z | `snackbar dimens width: 370; height: 48; top: 822px; left: 21px; border-radius: 4px; animation-duration: 0ms;` |
| 2026-04-21T00:19Z | `pull main` |
| 2026-04-21T00:27Z | `work on top of fresh main` |
| 2026-04-21T00:29Z | `/Users/btseytlin/Downloads/slide_states.pdf carefully implement the sliders to match the ui specifications` |
| 2026-04-21T00:31Z | `/Users/btseytlin/Downloads/via_figma_designs.pdf review/look at the designs again - lets make sure we're implemented to look as closely as possible to the designs. Like the Reject button being next to the slide to accept button` |
| 2026-04-21T00:37Z | `/Users/btseytlin/Downloads/actual.pdf i never get the snackbars shown on the HomeScreen` |
| 2026-04-21T00:38Z | `review the code, are we implemented on these specs? Concurrency and Error Handling ● Use Kotlin Coroutines and Flow to call the mock service and manage the ViewModel's state. ● The ViewModel must expose state changes (e.g., isLoading, successMessage, errorMessage) using StateFlow to the View reactively. Code Quality & Testing ● Unit Tests: You must provide unit tests for the RequestDetailViewModel. ○ Test at least one successful approval path (verifying state transitions). ○ Test at least one failure path (rejection or random service failure). ○ Tests must use a TestDispatcher and runTest to ensure proper coroutine testing practices. ● Mocking: Use a mock or stub implementation of the RequestService interface to effectively isolate and test the ViewModel's logic. ● Code Quality: Write clean, modular, and maintainable code with clear naming conventions.` |
| 2026-04-21T00:41Z | `yes but do it on a separate branch` |
| 2026-04-21T00:43Z | `check out the branch` |
| 2026-04-21T00:42Z | `start a git worktree and do the work there` |
| 2026-04-21T00:46Z | `im still not seeing any snackbars` |
