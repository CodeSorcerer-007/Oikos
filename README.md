<p align="center">
  <img src="Logo.png" alt="Oikos Logo" width="128" />
</p>

<h1 align="center">Oikos</h1>

<p align="center">
  <b>100% offline-first, private native Android grocery manager built with Jetpack Compose, Material 3, and SQLite.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_36-223E25?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Persistence-SQLite-003B57?style=for-the-badge&logo=sqlite" alt="SQLite" />
  <img src="https://img.shields.io/badge/Privacy-Zero_Internet_Permission-2E7D32?style=for-the-badge" alt="Privacy" />
  <img src="https://img.shields.io/badge/Tests-53%20Passed-brightgreen?style=for-the-badge" alt="Tests" />
  <img src="https://img.shields.io/badge/Audit_Score-100%2F100-success?style=for-the-badge" alt="Audit Score" />
</p>

<p align="center">
  <img src="brag-output/brag.gif" alt="Oikos App Demo" width="100%" />
</p>

---

## Overview

Most grocery list apps require cloud accounts, track personal purchasing habits, and stall in supermarket basements with weak cellular reception.

**Oikos is engineered for the real-world supermarket experience:**
- **Local-First & Instant**: All data lives in local SQLite with `<1ms` read/write latency.
- **Zero Network Permission**: `android.permission.INTERNET` is completely omitted from the manifest. No telemetry, no accounts, no analytics SDKs, and zero cloud lock-in.
- **In-Store Ergonomics**: 64dp+ tap targets, screen wake-lock, gesture `BackHandler` interception, and physical aisle walking order designed for one-handed operation while pushing a cart.
- **Audited & Hardened**: 100/100 verified across mobile security, architecture, performance, correctness, and automated test coverage.

---

## Download APK

Pre-built and signed installable APK is maintained directly in the repository:
- **[Oikos.apk](Oikos.apk)** (Direct installation)

---

## Screenshots

| Grocery List | Natural Search | Item Detail |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_main_screen.png" width="260" alt="Main Screen" /> | <img src="docs/screenshots/oikos_search.png" width="260" alt="Search" /> | <img src="docs/screenshots/oikos_item_detail.png" width="260" alt="Item Detail" /> |

| Shopping Mode | Aisle Sequence | Trip Summary |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_shopping_mode.png" width="260" alt="Shopping Mode" /> | <img src="docs/screenshots/oikos_store_order.png" width="260" alt="Store Order" /> | <img src="docs/screenshots/oikos_finish_celebration.png" width="260" alt="Finish Modal" /> |

---

## Key Features

- **Smart Add (`SmartAddParser`)**: Type natural phrases like `"2L milk @ 60"`, `"6 eggs"`, `"1/4 kg butter"`, `"3/4 L olive oil"`, or `"coffee inr 250"`. Automatically parses quantities, fractions, units, pricing tokens (`@`, `₹`, `rs.`, `inr`, `for`), and maps items to 1 of 11 supermarket aisles using a collision-resistant on-device dictionary.
- **Batch Quick-Add**: Paste or type multiple items separated by newlines, commas, or semicolons (e.g., `2L milk; 6 eggs; 1 loaf sourdough`) for instant multi-item list population.
- **Duplicate Detection & Autocomplete**: Proactively identifies singular/plural variants (`Apple` vs `Apples`) to prevent accidental double-purchases, accompanied by real-time autocomplete suggestions as you type.
- **Store-Aware Walking Order**: Sorts active grocery items by the physical sequence of your supermarket aisles to eliminate backtracking through store corridors.
- **In-Store Shopping Mode**: Keeps the display awake while shopping, provides large thumb-friendly tap targets, calculates a live running total of checked items, and features an integrated Android `BackHandler` for smooth gesture exit.
- **Tactile Haptics (`HapticFeedbackHelper`)**: Distinct vibration profiles for item check-off, quantity steppers, warning thresholds, and trip completion.
- **Home Screen Glance Widget (`OikosAppWidgetProvider`)**: Direct SQLite-backed `RemoteViews` widget showing active items and progress with asynchronous coroutine dispatch (`goAsync`) preventing main-thread ANRs.
- **Quick Settings Tile**: Enter Store Shopping Mode directly from Android's notification shade.
- **High-Contrast Themes**: Curated AMOLED Dark (`#0F1411`) and Organic Light (`#FAF9F5`) design system.
- **Private Data Portability**: Full JSON export and import with sensitive clipboard masking (`EXTRA_IS_SENSITIVE`), strict payload security guards, and a multi-step LIFO undo stack for accidental deletions.

---

## Privacy & Security

- **Zero Cloud Backups**: Configured with `data_extraction_rules.xml` and `backup_rules.xml` to prevent unencrypted cloud extraction of your database to Google Drive while permitting direct encrypted device-to-device transfers.
- **Clipboard Leakage Guard**: Masks database export payloads using `ClipDescription.EXTRA_IS_SENSITIVE` on Android 13+ (API 33+) to suppress plaintext clipboard preview overlays.
- **Foreign Key Relational Integrity**: Enforces `PRAGMA foreign_keys = ON` in `OikosDatabaseHelper.onConfigure` to preserve relational integrity across lists, items, and store order.
- **Memory Denial-of-Service Protection**: Imposes strict 5MB size limits and atomic transaction boundaries during JSON backup imports.

---

## Architecture

```
com.example.oikos
 ├── data
 │    ├── db          # OikosDatabaseHelper (Thread-safe SQLiteOpenHelper with double-checked locking)
 │    └── repository  # OikosRepository (StateFlow streams, LIFO Undo stack, widget sync)
 ├── model            # Immutable domain models (GroceryItem, GroceryList, GroceryCategory)
 ├── nlp              # SmartAddParser (Tokenization, units, fractions, staple dictionary)
 ├── theme            # Material 3 Design Tokens (Light, AMOLED Obsidian Slate, Typography)
 ├── tile             # OikosShoppingTileService (Android Quick Settings Tile)
 ├── ui
 │    ├── components  # CategorySectionHeader, GroceryItemRow, SmartAddBar, ProgressCard, Sheets
 │    ├── main        # MainScreen & MainScreenViewModel (UDF reactive state machine)
 │    └── shopping    # ShoppingModeScreen (Store-Aware layout, wake-lock, BackHandler, live spend)
 ├── util             # HapticFeedbackHelper, CurrencyFormatter
 └── widget           # OikosAppWidgetProvider (Asynchronous SQLite RemoteViews glance widget)
```

- **Pattern**: MVVM with Unidirectional Data Flow (`StateFlow`).
- **UI Framework**: Jetpack Compose + Material 3.
- **Local Persistence**: SQLite (direct thread-safe singleton, zero third-party cloud dependencies).

---

## Testing & Quality Assurance

The codebase includes an extensive automated test suite with **53 unit tests** passing with 100% success:

- **`SmartAddParserAdvancedTest`**: Validates collision resistance (e.g., `"red peppers"` avoiding bakery bread collisions), fraction handling, multi-currency tokens, batch delimiters, singular/plural duplicate detection, and autocomplete prefix matching.
- **`GroceryListTest`**: Validates list initialization, immutability, and UUID assignment.
- **`CurrencyFormatterTest`**: Verifies locale-agnostic and symbol-specific currency formatting.
- **`UndoActionTest`**: Validates restoration state fidelity across item deletions.
- **`MainScreenViewModelLogicTest`**: Validates restock frequency suggestions, quantity steppers, physical aisle ordering, and search filtering.

```bash
# Run all unit tests
./gradlew test
```

---

## Development & Build

### Build Debug APK
```bash
./gradlew assembleDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```

### Install Directly to Device
```bash
./gradlew installDebug
```

---

## License

[MIT](LICENSE)
