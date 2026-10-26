<p align="center">
  <img src="Logo.png" alt="Oikos Logo" width="128" />
</p>

<h1 align="center">Oikos</h1>

<p align="center">
  <b>100% offline-first, private native Android grocery app built with Jetpack Compose, Material 3, and SQLite.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_36-223E25?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Persistence-SQLite-003B57?style=for-the-badge&logo=sqlite" alt="SQLite" />
  <img src="https://img.shields.io/badge/Privacy-No_Internet_Permission-2E7D32?style=for-the-badge" alt="Privacy" />
</p>

<p align="center">
  <img src="brag-output/brag.gif" alt="Oikos App Demo" width="100%" />
</p>

---

## Overview

Most grocery list apps require cloud accounts, track personal habits, and fail in supermarket basements with weak reception.

**Oikos is built for the real supermarket experience:**
- **Local-First & Instant**: All data lives in local SQLite with `<1ms` read/write latency.
- **Zero Network Permission**: `android.permission.INTERNET` is omitted from the manifest. No telemetry, no accounts, no sync delays.
- **In-Store Usability**: 64dp+ tap targets, screen wake-lock, and aisle walking order designed for one-handed use while pushing a cart.

---

## Screenshots

| Grocery List | Natural Search | Item Detail |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_main_screen.png" width="260" alt="Main Screen" /> | <img src="docs/screenshots/oikos_search.png" width="260" alt="Search" /> | <img src="docs/screenshots/oikos_item_detail.png" width="260" alt="Item Detail" /> |

| Shopping Mode | Aisle Sequence | Trip Summary |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_shopping_mode.png" width="260" alt="Shopping Mode" /> | <img src="docs/screenshots/oikos_store_order.png" width="260" alt="Store Order" /> | <img src="docs/screenshots/oikos_finish_celebration.png" width="260" alt="Finish Modal" /> |

---

## Features

- **Smart Add (`SmartAddParser`)**: Type natural phrases like `"2L milk @ 60"`, `"6 eggs"`, or `"1/2 kg spinach"`. Automatically extracts quantity, units, price, and maps to 1 of 11 supermarket aisles using an on-device dictionary.
- **Store-Aware Walking Order**: Sorts active grocery items by the physical sequence of your store aisles to eliminate backtracking.
- **In-Store Shopping Mode**: Keeps the display awake while shopping, provides large thumb-friendly tap targets, and calculates a running total of checked items.
- **Tactile Haptics (`HapticFeedbackHelper`)**: Distinct vibration profiles for item check-off, quantity steppers, warnings, and trip completion.
- **Home Screen Glance Widget**: Direct SQLite-backed `RemoteViews` widget showing active items and progress without background services.
- **Quick Settings Tile**: Enter Store Shopping Mode directly from Android's notification shade.
- **Themes**: High-contrast AMOLED Dark (`#0F1411`) and Organic Light (`#FAF9F5`) themes.
- **Data Portability**: Full JSON export and import to clipboard with multi-step undo for accidental deletions.

---

## Architecture

```
com.example.oikos
 ├── data
 │    ├── db          # OikosDatabaseHelper (SQLiteOpenHelper: lists, items, frequent, store_order)
 │    └── repository  # OikosRepository (StateFlow streams, LIFO Undo stack, widget sync)
 ├── model            # Immutable domain models (GroceryItem, GroceryList, GroceryCategory)
 ├── nlp              # SmartAddParser (Tokenization, units, fractions, staple dictionary)
 ├── theme            # Material 3 Design Tokens (Light, AMOLED Obsidian Slate, Typography)
 ├── tile             # OikosShoppingTileService (Android Quick Settings Tile)
 ├── ui
 │    ├── components  # CategorySectionHeader, GroceryItemRow, SmartAddBar, ProgressCard, Sheets
 │    ├── main        # MainScreen & MainScreenViewModel (UDF reactive state machine)
 │    └── shopping    # ShoppingModeScreen (Store-Aware layout, wake-lock, live spend)
 ├── util             # HapticFeedbackHelper, CurrencyFormatter
 └── widget           # OikosAppWidgetProvider (Direct SQLite RemoteViews glance widget)
```

- **Pattern**: MVVM with Unidirectional Data Flow (`StateFlow`).
- **UI**: Jetpack Compose + Material 3.
- **Database**: SQLite (direct local helper, zero network dependencies).

---

## Development

### Run Tests
```bash
./gradlew test
```

### Install Debug APK
```bash
./gradlew installDebug
```

### Build Release APK
```bash
./gradlew assembleRelease
```

---

## License

[MIT](LICENSE)
