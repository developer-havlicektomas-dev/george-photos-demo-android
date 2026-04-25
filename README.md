# PhotosApp

A small Android app that browses Flickr's public photo feed. The home screen
shows a staggered, masonry-style grid of photos with title and tags; tapping a
photo opens a full-bleed detail screen. Tag filtering is driven by a modal
bottom sheet — pick any combination of tags from the feed and the grid (and the
photo count in the app bar) updates accordingly. Pull-to-refresh hits the feed
directly; there is no local cache.

## Tech stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.3 |
| UI | Jetpack Compose + Material3 |
| Architecture | Feature-first MVI (`State` / `Action` / `Event` + ViewModel) |
| Navigation | Compose Navigation with type-safe `@Serializable` routes |
| DI | Koin (one module per layer, assembled in `:app`) |
| Networking | Ktor (CIO engine) + kotlinx.serialization |
| Image loading | Coil 3 (with the Ktor network fetcher) |
| Error handling | Typed `Result<T, E>` + `DataError` mapped to `UiText` |
| Logging | Timber |
| Testing | JUnit 5, Turbine, AssertK, `kotlinx-coroutines-test` |
| Build | AGP 9.2, single Gradle module (`:app`) |

## Project layout

Single Gradle module, package `dev.havlicektomas.photosapp`:

```
app/src/main/java/dev/havlicektomas/photosapp/
├── core/
│   ├── di/             — Koin modules for cross-feature plumbing
│   ├── domain/util/    — Result, DataError, Error
│   ├── navigation/     — Routes, custom NavType, RootNavGraph
│   ├── network/        — Ktor HttpClient factory + safe-call helpers
│   ├── presentation/   — UiText, ObserveAsEvents, DataError → UiText
│   └── ui/             — theme tokens + reusable design-system components
└── feature/
    ├── home/           — data, domain, presentation, di
    └── detail/         — presentation, di
```

## Build & test

```bash
./gradlew assembleDebug      # build the debug APK
./gradlew installDebug       # install on a connected device/emulator
./gradlew test               # run unit tests
./gradlew lint               # Android lint
```

## Out of scope

- No Room / persistent cache — pull-to-refresh always hits the network.
- No multi-module split — everything lives in `:app`.
- No instrumented or Compose UI tests.
