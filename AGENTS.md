# Android Development Agents - Rules & Skill Set

This document defines the rules and standards for AI Agents and developers working on the **BasePhamThang** project.

## 1. Core Architecture & Tech Stack
- **Architecture**: Strict MVVM (Model-View-ViewModel) + Clean Architecture.
- **Language**: Kotlin 100%. Follow idiomatic Kotlin (immutability, data classes, extension functions).
- **Dependency Injection**: Hilt (`@HiltViewModel`, `@Inject`, `@Module`, `@InstallIn`).
- **Asynchronous / Stream**: Kotlin Coroutines & StateFlow/SharedFlow. **DO NOT** use LiveData, RxJava, or Callbacks.
- **UI Framework**:  XML with ViewBinding if explicitly requested.

---

## 2. ViewModel & State Management Guidelines
- Every ViewModel MUST use `@HiltViewModel` and inject dependencies via constructor `@Inject`.
- State management strictly follows **Unidirectional Data Flow (UDF)**:
  - Expose UI State as a single `StateFlow<UiState>` (using `.asStateFlow()`).
  - Keep mutable state private (`private val _uiState = MutableStateFlow(...)`).
  - Represent UI states using `sealed interface` or `sealed class` (e.g., `Idle`, `Loading`, `Success`, `Error`).
- Handle Coroutines using `viewModelScope.launch` and appropriate Dispatchers (`Dispatchers.IO` for background, `Dispatchers.Main` for UI).

---

## 3. Data & Repository Layer Rules
- Repositories must be defined as `interface` with concrete implementations (e.g., `FeatureRepository` and `FeatureRepositoryImpl`).
- Bind implementations using `@Binds` in a Hilt `@Module` installed in `SingletonComponent` (or `ViewModelComponent`).
- Always wrap API or Database operations in Kotlin `Result<T>` or a custom `NetworkResult<T>` to safely catch and handle Exceptions.
- Never expose raw DTOs / Network Entities directly to the UI layer; always map them to Domain Models first.

---

## 4. Coding Standards & Best Practices
- **Null Safety**: Strictly avoid non-null assertions (`!!`). Use safe calls (`?.`), Elvis operator (`?:`), or `let`.
- **String Resources**: Do not hardcode strings in UI / Compose components. Always use `stringResource(id)` or Context string resources.
- **Unit Testing**:
  - Write testable code without static calls or direct object instantiations inside ViewModels.
  - When generating Unit Tests, use `MockK` for mocking and `Turbine` for testing `Flow` / `StateFlow`.
- **Code Style**: Keep functions concise and single-responsibility. Follow official Kotlin Coding Conventions.


---

## 5. Specialized Agent Skill Files
- **Ads Integration (`AGENT_ADS.md`)**: Whenever the task involves integrating ads, reading an Ads Google Sheet, updating `AdsKey` / `RemoteKeys`, or using `AdsHelper`, you MUST read and follow `AGENT_ADS.md`.
- **Figma to Android XML UI (`AGENT_FIGMA_UI.md`)**: Whenever the task involves building or updating Android XML layouts, drawables, colors, or assets from a Figma URL/design, you MUST read and follow `AGENT_FIGMA_UI.md`.
