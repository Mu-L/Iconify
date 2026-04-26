# AGENTS.md

## Architecture
- Single-module Android app (`app/`), root package `com.drdisagree.iconify`, targets **SDK 36 (Android 16)** on rooted Pixel/AOSP devices.
- Three runtime surfaces: **Compose UI** (`features/**`), **root/service glue** (`services/**`, `core/utils/**`), and **Xposed hooks** (`xposed/**`).
- `MainActivity` boots a libsu root shell, runs `AppProviders` which wires composition locals (`LocalNavController`, `LocalSettings`, `LocalColorScheme`, haptics, haze, scaled `Density`), then `NavGraph` renders screens. Most screens read state from locals, not passed parameters.
- `MainScreen` redirects to `NavRoutes.Home.Root` or `NavRoutes.Xposed.Root` based on `SettingsKey.XPOSED_ONLY_MODE`.
- DI is Hilt (`core/di/`). `PreferenceModule` provides `PreferenceController` backed by `SharedPreferencesStorage` (device-protected storage, cross-process via `RemotePrefProvider`). A `DataStoreStorage` alternative exists but is not the default.

## Preference / data flow
- Preference keys are enums implementing `Key` interface (`data/keys/`): `SettingsKey`, `XposedKey`, `TweaksKey`, `CustomizationKey`. Each entry carries a typed `default` value.
- `PreferenceController` (`core/preferences/`) manages in-memory state backed by `PreferenceStorage`; exposes typed flows consumed by `SettingsViewModel`.
- Values are wrapped in `PrefValue` sealed class (`BoolValue`, `IntValue`, `FloatValue`, `StringValue`, etc.). Color prefs are stored as **hex strings** (e.g. `"#8a51f5"`), not ints — parse with `Color.parseColor()` or `.toLong()`.
- Dynamic overlays persist in Room (`data/database/ResourceDatabase.kt`, DAOs under `data/dao/`).

## Xposed hooks
- Entry point: `InitHook` → `HookEntry.handleLoadPackage()`. Hooks target **`android` (framework)** and **`com.android.systemui`** packages only; child processes are skipped.
- All hook modules extend `ModPack` (`xposed/ModPack.kt`): implement `updatePrefs()` and `handleLoadPackage()`.
- `EntryList.kt` is the authoritative hook registry. `topPriorityCommonModPacks` (callbacks, utilities) run first; `systemUIModPacks` run after. Adding a hook = add class to the appropriate list + register in `EntryList`.
- Xposed reads prefs via `XPrefs` (SharedPreferences from `RemotePrefProvider`), NOT DataStore. Resource files (fonts, images) are shared via `XposedConst.XPOSED_RESOURCE_DIR` → `Downloads/Iconify/`.
- `RootProviderProxy` is a bound AIDL service (`IRootProviderProxy.aidl`) called from Xposed via `HookEntry.enqueueProxyCommand()`. The caller allowlist is in `R.array.root_requirement`.

## UI patterns
- Most screens use the `PreferenceScreen` / `preferenceScreen { ... }` DSL (`core/preferences/`), not plain Compose layouts.
- Activity-scoped VMs: use `sharedHiltViewModel()` (`core/ui/utils/ComposeUtils.kt`) for state that must survive navigation (e.g. `BottomNavViewModel`).
- Navigation is typed: routes in `app/navigation/NavRoutes.kt`, screens in `app/navigation/NavGraph.kt`.
- Feature packages follow `features/{home,xposed,settings,onboarding,main}/` with `screens/`, `components/`, `viewmodels/` sub-packages.

## Build & CI
- Debug: `.\gradlew.bat assembleDebug` · Release: `.\gradlew.bat assembleRelease` then `.\gradlew.bat renameApks`
- Debug build appends `.debug` to applicationId; `ModuleBase/customize.sh` references this as `PKGNAME`. If you change package IDs or the launcher activity, update `customize.sh`.
- CI (`.github/workflows/build_debug.yml`): JDK 21, sets `CI=true` (enables minify/shrink on debug), bumps version, zips APK + `ModuleBase/` for Magisk flashing, posts to Telegram.
- No checked-in test sources; validation = successful build + tracing root/Xposed flows.
- Translations: Crowdin (`crowdin.yml`); user-facing strings go in `res/values/strings.xml`.
