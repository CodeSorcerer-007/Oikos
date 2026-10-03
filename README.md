<p align="center">
  <img src="Logo.png" alt="Oikos Logo" width="120" />
</p>

<h1 align="center">Oikos — Native Android Grocery App</h1>

<p align="center">
  <b>A 100% offline-first, private native Android grocery experience engineered with Jetpack Compose, Material 3, SQLite, and smart on-device natural language parsing. Zero bloat. Zero cloud latency. Zero trackers.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_36-223E25?style=flat-square&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF?style=flat-square&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=flat-square&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Persistence-SQLite_Local--First-003B57?style=flat-square&logo=sqlite" alt="SQLite" />
  <img src="https://img.shields.io/badge/Tests-100%25_Passing-success?style=flat-square" alt="Tests" />
</p>

---

## 🌿 Highlights & Key Features

- **Smart Natural Language Item Entry (`SmartAddParser`)**
  - Type naturally in a single input field: `"2L whole milk"`, `"6 organic eggs"`, `"artisan sourdough @ 150"`, or `"1/2 kg tomatoes"`.
  - Automatic extraction of quantities, fractional measurements, unit tokens, and item prices.
  - Automatic category classification across 11 supermarket aisles backed by a 150+ food dictionary.
  - Real-time token preview chips and inline duplicate warnings (`"Already on list · Tap + to increment quantity"`).

- **Store-Aware Shopping Mode (`ShoppingModeScreen`)**
  - **Screen Wake-Lock**: Keeps the display alive while pushing a cart.
  - **Aisle-Based Walking Order**: Automatically sorts active items according to your supermarket layout so you never backtrack across aisles.
  - **Large Tap Targets**: 64dp+ touch areas optimized for one-handed in-store use.
  - **Live Cart Total**: Running budget spend counter that calculates in real-time as items are checked into your cart.
  - **Trip Completion Celebration**: Summarizes purchased items, total spend, and offers one-tap clearing or archiving.

- **Store Layout Customizer (`StoreOrderSheet`)**
  - Reorder supermarket aisles to match your specific grocery store (Produce, Bakery, Dairy & Eggs, Meat & Seafood, Pantry, Beverages, Frozen, etc.).

- **Native Home Screen Glance Widget (`OikosAppWidgetProvider`)**
  - Instant home screen widget querying SQLite directly via `RemoteViews`.
  - Displays active list name, total pending item count, and item checklist.
  - Automatically synchronizes on every item add, toggle, or modification.

- **100% Offline-First Architecture & Privacy**
  - Entirely local SQLite database (`SQLiteOpenHelper`) storing lists, items, and purchase frequencies.
  - Multi-step LIFO Undo system for immediate recovery of accidental item actions.
  - Instant JSON backup export and import to clipboard for easy phone migrations.
  - Zero cloud dependencies, zero analytics trackers, zero account requirements.

---

## 📸 Screenshots

| Main Grocery List | Dedicated Shopping Mode | Edit Item Sheet |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_main_screen.png" width="260" /> | <img src="docs/screenshots/oikos_shopping_mode.png" width="260" /> | <img src="docs/screenshots/oikos_item_detail.png" width="260" /> |

| Instant Search | Store Walking Order | Preferences & Backup |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_search.png" width="260" /> | <img src="docs/screenshots/oikos_store_order.png" width="260" /> | <img src="docs/screenshots/oikos_settings.png" width="260" /> |

---

## 🏗️ Architecture & Technology Stack

- **Architecture**: MVVM with reactive StateFlow streams and single unidirectional data flow (UDF).
- **UI Framework**: Modern Jetpack Compose with Material 3 theming.
- **Design System**:
  - Forest Green: `#223E25`
  - Warm Honey Accent: `#D4822B`
  - Eggshell Canvas: `#FAF9F5`
  - High-contrast accessibility tokens and subtle elevation surfaces.
- **Data Layer**:
  - `OikosDatabaseHelper`: SQLiteOpenHelper managing `lists`, `items`, `frequent_items`, and `store_order`.
  - `OikosRepository`: Reactive repository handling SQLite queries, StateFlow broadcasts, Undo stack, and AppWidget synchronization.

---

## 🧪 Testing & Verification

Unit tests cover the NLP parser, domain models, and UI state machine:
- `SmartAddParserTest`: Fractions (`1/2 kg`), unit normalization, price extraction, duplicate detection, and 11-category classification.
- `GroceryItemTest`: Quantity formatting, fallback calculations, model semantics.
- `GroceryCategoryTest`: Default supermarket aisle sequence, display attributes.
- `MainScreenViewModelTest`: Active modal transitions and dialog dismiss flows.

To run the unit test suite:
```bash
./gradlew test
```

To build and install the debug APK onto a connected Android device or emulator:
```bash
./gradlew installDebug
```

---

## 📄 License

This project is licensed under the MIT License.
