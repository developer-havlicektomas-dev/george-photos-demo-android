# PhotosApp

Single-module Android app that browses Flickr's public photo feed. Compose +
Material3, Kotlin 2.2, AGP 9.2, minSdk 28, package `dev.havlicektomas.photosapp`.

## Where to find things

- **Requirements:** `specs/photos_app.txt` — read before planning any feature.
- **Inter font:** `specs/font/Inter/` (variable TTF + `README.txt` + `OFL.txt`).
  Copy the TTF into `app/src/main/res/font/` when wiring typography.
- **Design:** fetch on demand from
  `https://api.anthropic.com/v1/design/h/jZkwSIwCQE3WCcOLkXGwug?open_file=PhotosApp.html`.
  **Always read the design's README first**, then implement only the relevant
  aspects of the design for the screen you're building.
- **Flickr feed:**
  `https://api.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1`

## Architecture conventions

- Single Gradle module (`:app`), package `dev.havlicektomas.photosapp`.
- Layout is **feature-first**:
  - `feature/home/{data,domain,presentation}`
  - `feature/detail/{data,domain,presentation}`
  - `core/{di,ui,network}` for cross-feature plumbing (DI graph, theme/design
    system, Ktor client).
- MVI presentation: `State` / `Action` / `Event` + `ViewModel` + Root/Screen
  composable split.
- Typed errors via a `Result<T, E>` wrapper (`DataError`, `EmptyResult`).
- DI via Koin; one module per layer, assembled in the `Application` class.
- Type-safe Compose Navigation between Home and Detail.
- Network: Ktor `HttpClient`. **No local cache** — pull-to-refresh hits the feed
  directly.
- Images: **Coil 3**.
- Theme: Material3 with Inter typography.

## Skills to use

When the work matches one of these areas, invoke the corresponding skill:

- `android-module-structure` — module/package decisions.
- `android-presentation-mvi` — ViewModels, screens, State/Action/Event, UiText.
- `android-compose-ui` — composables, recomposition, previews, design system.
- `android-navigation` — type-safe routes (Home ↔ Detail).
- `android-data-layer` — Ktor client, repositories, DTOs, mappers.
- `android-di-koin` — Koin modules and `koinViewModel()` wiring.
- `android-error-handling` — `Result<T, E>` and `DataError`.
- `android-testing` — ViewModel/repository unit tests (JUnit5 + Turbine +
  AssertK + fake repos).

## Build & test

- `./gradlew assembleDebug` — build the debug APK.
- `./gradlew test` — run unit tests.
- `./gradlew lint` — Android lint.

## Out of scope

- No Room and no offline cache (network-only).
- No multi-module split — everything stays in `:app`.
- No instrumented or Compose UI tests unless explicitly requested.
