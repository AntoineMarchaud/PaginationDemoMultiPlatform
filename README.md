# PaginationDemoMultiPlatform

## Tech Stack (main Branch)
- Kotlin 2
- Compose Multiplatform
- Koin
- SqlDelight
- Ktor with Kotlinx Serialization
- Paging 3 (appcash)
- Clean Architecture
- No KSP
- TOML (Version Catalogs)

## Tech Stack (second Branch)
- SqlDelight --> Room
- KSP

## Project Structure

PaginationDemo/
- composeApp/ # CMP application module and Android executable
- iosApp/ # executable for iOS which links to composeApp/iosMain
- data/ # Data layer: repositories, data sources, models
- domain/ # Domain layer: use cases, business logic
- ui/ # UI module with Compose screens and previews
- build.gradle.kts
- settings.gradle.kts

## Features

- Displays a list of items from a api using an infinite scroll
- Caches data in a local Room database for offline access
- Uses Kotlin Flows for reactive data streams
- Modern Android development stack using Jetpack Compose
- Dependency Injection with Hilt (or Koin)
- Pagination logic with UI loading indicators
- Modularized codebase for maintainability and testability
- Swipe to refresh
- Error cases

## What missing

- Add code quality : KtLint, Detekt
- Add Ui tests 
