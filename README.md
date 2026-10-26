<p align="center">
  <img src="Logo.png" alt="Oikos Logo" width="128" />
</p>

<h1 align="center">Oikos — Native Android Grocery App</h1>

<p align="center">
  <b>A 100% offline-first, private native Android grocery experience engineered with Jetpack Compose, Material 3, SQLite, and smart on-device natural language parsing. Zero bloat. Zero cloud latency. Zero trackers.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android_36-223E25?style=for-the-badge&logo=android" alt="Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin_2.0-7F52FF?style=for-the-badge&logo=kotlin" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack_Compose_Material_3-4285F4?style=for-the-badge&logo=jetpackcompose" alt="Compose" />
  <img src="https://img.shields.io/badge/Persistence-SQLite_Local--First-003B57?style=for-the-badge&logo=sqlite" alt="SQLite" />
  <img src="https://img.shields.io/badge/Privacy-Zero_Internet_Permission-2E7D32?style=for-the-badge" alt="Privacy" />
  <img src="https://img.shields.io/badge/Tests-100%25_Passing-success?style=for-the-badge" alt="Tests" />
</p>

---

## 💡 The Philosophy: *"Why is every grocery app not this good?"*

Most modern grocery and checklist apps are bloated, 150MB+ hybrid WebViews requiring cloud accounts, password resets, and endless sync spinners just to remember a carton of milk. When you walk into a concrete supermarket basement with zero mobile signal, they freeze, crash, or lose your edits.

**Oikos is engineered as the direct antidote:**
- **Zero Cloud Latency**: Pure local SQLite persistence. Edits, queries, and transitions execute in under 1 millisecond.
- **Pure Offline Privacy**: `android.permission.INTERNET` is not even requested in the manifest. Zero tracking, zero telemetry, zero accounts.
- **Real-World Supermarket UX**: Features designed specifically for pushing a shopping cart with one hand—screen wake-lock, aisle-based store walking order, 64dp+ tap targets, and multi-profile haptic feedback.

---

## 📸 Visual Showcase

### 🛒 1. Fluid Grocery Management & Instant Smart Add
Type naturally in a single input field (`"2L whole milk @ 60"`, `"6 organic eggs"`, or `"1/2 kg tomatoes"`). The on-device `SmartAddParser` extracts quantity, unit, price, and auto-classifies the item into 1 of 11 supermarket aisles in real time.

| Main Grocery List | Instant Item Search | Item Detail & Steppers |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_main_screen.png" width="270" alt="Main Grocery Screen" /> | <img src="docs/screenshots/oikos_search.png" width="270" alt="Search Dialog" /> | <img src="docs/screenshots/oikos_item_detail.png" width="270" alt="Item Detail Sheet" /> |
| *Categorized aisles & progress tracking* | *Real-time debounce search & filtering* | *Fluid steppers & custom price entry* |

<br/>

### 🏪 2. Store-Aware Shopping Mode & Aisle Navigation
Never backtrack across aisles again. Oikos sorts active items according to the physical layout of your supermarket, keeps your screen awake while shopping, and computes your live basket spend.

| Dedicated Shopping Mode | Supermarket Aisle Sequencer | Trip Finish Celebration |
| :---: | :---: | :---: |
| <img src="docs/screenshots/oikos_shopping_mode.png" width="270" alt="Shopping Mode Screen" /> | <img src="docs/screenshots/oikos_store_order.png" width="270" alt="Store Order Sheet" /> | <img src="docs/screenshots/oikos_finish_celebration.png" width="270" alt="Finish Celebration Modal" /> |
| *Screen wake-lock & running cart budget* | *Custom walking order per aisle* | *Trip stats & celebratory haptic wave* |

<br/>

### 🎨 3. True AMOLED Obsidian Dark Mode & Preferences
Switch seamlessly between **Organic Kitchen Light** (`#FAF9F5`) and **AMOLED Obsidian Slate** (`#0F1411`) with high-contrast category badges tailored for supermarket lighting.

| Completed Items Archive | Settings & Appearance |
| :---: | :---: |
| <img src="docs/screenshots/oikos_completed_items.png" width="270" alt="Completed Items List" /> | <img src="docs/screenshots/oikos_settings.png" width="270" alt="Settings Sheet" /> |
| *One-tap restore or clearing* | *Theme selector, haptics & clipboard backup* |

---

## 🌿 God-Tier Features & Engineering Craft

### 🧠 1. On-Device Natural Language Parser (`SmartAddParser`)
- **Single-Line Conversational Input**: Parse natural phrases like `"2L whole milk @ 120"`, `"3 dozen eggs"`, `"1/2 kg baby spinach"`, or `"artisan sourdough"`.
- **Intelligent Tokenizer**: Automatically strips and normalizes fractional quantities, units of measure (`kg`, `g`, `L`, `ml`, `pack`, `box`, `bunch`, `pcs`), and pricing tokens (`@`, `₹`, `$`, `€`).
- **11-Aisle Supermarket Taxonomy**: Auto-classifies items into Produce, Bakery, Dairy & Eggs, Meat & Seafood, Pantry, Beverages, Frozen, Household, Personal Care, Snacks, and Baby Care via a built-in 150+ staple grocery dictionary.
- **Inline Duplicate Detection**: Warns in real-time when an item is already on your list with a one-tap increment action (`"Already on list · Tap + to increment quantity"`).

### 🚶 2. Supermarket Aisle Walking Order
- **Backtrack-Free Shopping**: Active cart items automatically group and order themselves by your store's physical sequence.
- **Interactive Store Order Customizer (`StoreOrderSheet`)**: Reorder supermarket aisles to mirror your specific local grocery store (e.g. Produce first, Dairy last).

### 🛒 3. In-Store Shopping Mode (`ShoppingModeScreen`)
- **Screen Wake-Lock**: Keeps your display on while pushing the grocery cart, preventing frustrating device locks when hands are busy.
- **64dp+ Giant Tap Targets**: Engineered specifically for effortless one-handed thumb interaction while on the move.
- **Live Cart Spend Total**: Running budget counter that computes the exact cost of checked-off items in real-time.
- **Quick Settings Tile (`OikosShoppingTileService`)**: Launch directly into Store Shopping Mode from Android's notification shade with a single swipe and tap.

### 📳 4. Hardware-Tailored Haptic Engine (`HapticFeedbackHelper`)
Crafted with 4 distinct tactile vibration profiles:
- **`tick`**: Subtle tactile click for steppers and aisle switches (`VibrationEffect.EFFECT_TICK`).
- **`confirm`**: Satisfying mechanical click when checking an item into your cart (`VibrationEffect.EFFECT_CLICK`).
- **`warning`**: Double reject vibration for duplicates or destructive thresholds (`VibrationEffect.createOneShot(40)`).
- **`milestone`**: Harmonic multi-stage waveform pulse (`timings = [0, 30, 60, 30, 60, 50]` ms @ increasing amplitudes) triggering when you finish your shopping trip.

### 📱 5. Native Home Screen Glance Widget (`OikosAppWidgetProvider`)
- Direct SQLite-backed `RemoteViews` widget requiring zero background sync services.
- Real-time completion progress bar, active list title, pending count badge, and 5-slot item checklist preview with instant auto-update.

### 🛡️ 6. Zero-Network Privacy & Instant Data Portability
- **100% Offline-First**: No servers, no APIs, no analytics, no third-party trackers.
- **Multi-Step LIFO Undo Stack**: Accidental swipe completions or deletions can be undone instantly via the reactive Undo snackbar.
- **Clipboard JSON Export & Import**: Zero-cloud migration between devices using clean JSON data bundles.

---

## ⚡ The Benchmark: Oikos vs Typical Grocery Apps

| Feature | Standard Grocery / To-Do App | Oikos Native Android |
| :--- | :--- | :--- |
| **Network Architecture** | Mandatory cloud servers & continuous polling | **100% Offline-First SQLite** (Zero cloud latency) |
| **Permissions Required** | Internet, Location, Accounts, Analytics | **Zero Internet Permission** (`android.permission.INTERNET` omitted) |
| **Cold Start Launch Time** | 1,500ms – 4,000ms (Splash & Auth sync) | **< 50ms** (Instant local UI rendering) |
| **Item Entry Experience** | Multi-step dialogs, pickers, and category trees | **Single-Line Natural Language Parser** (`2L milk @ 60`) |
| **In-Store Navigation** | Random entry order or alphabetical list | **Store-Aware Walking Order** mapped to physical aisles |
| **Screen Management** | Device display dims and locks in-store | **Screen Wake-Lock** keeping display alive while shopping |
| **Tactile Feedback** | Generic system vibration or none | **4 Custom Haptic Waveforms** (Tick, Confirm, Warning, Milestone) |
| **Android Integration** | Standard app icon only | **Quick Settings Tile + Home Screen Glance Widget** |
| **Bundle & Performance** | Bloated hybrid WebViews / cross-platform runtimes | **R8 / ProGuard Minified Kotlin & Jetpack Compose** |

---

## 🏗️ Architecture & Technology Stack

```
com.example.oikos
 ├── data
 │    ├── db          # OikosDatabaseHelper (SQLiteOpenHelper, tables: lists, items, frequent, store_order)
 │    └── repository  # OikosRepository (StateFlow streams, LIFO Undo stack, widget sync)
 ├── model            # Immutable domain models (GroceryItem, GroceryList, GroceryCategory)
 ├── nlp              # SmartAddParser (Tokenization, units, fractions, 150+ staple dictionary)
 ├── theme            # Material 3 Design Tokens (Light, AMOLED Obsidian Slate, Typography)
 ├── tile             # OikosShoppingTileService (Android Quick Settings Tile)
 ├── ui
 │    ├── components  # CategorySectionHeader, GroceryItemRow, SmartAddBar, ProgressCard, Sheets
 │    ├── main        # MainScreen & MainScreenViewModel (UDF reactive state machine)
 │    └── shopping    # ShoppingModeScreen (Store-Aware layout, wake-lock, live spend)
 ├── util             # HapticFeedbackHelper, CurrencyFormatter
 └── widget           # OikosAppWidgetProvider (Direct SQLite RemoteViews glance widget)
```

- **Architecture**: MVVM with reactive `StateFlow` streams and Unidirectional Data Flow (UDF).
- **UI Framework**: Modern Jetpack Compose with Material 3.
- **Design Tokens**:
  - **Organic Kitchen Light**: Forest Green (`#223E25`), Warm Honey Accent (`#D4822B`), Eggshell Canvas (`#FAF9F5`).
  - **AMOLED Obsidian Dark**: Vivid Leaf Accent (`#4EA157`), Amber Accent (`#E5953E`), Obsidian Slate (`#0F1411`).
- **Optimization**: ProGuard / R8 code shrinking and resource optimization enabled for release builds (`isMinifyEnabled = true`, `isShrinkResources = true`).

---

## 🧪 Testing & Verification

Oikos maintains a comprehensive unit test suite covering NLP parsing, domain models, theme contrasts, Quick Settings tiles, and UI state machines:

- **`SmartAddParserTest`**: Validates fractional quantities (`1/2 kg`), unit normalization (`L`, `litres`, `ltr`), price extraction (`@ 150`, `₹60`), duplicate warnings, and 11-category classification.
- **`GroceryItemTest`**: Formatted quantity labels, fallback math, and model immutability.
- **`GroceryCategoryTest`**: Default supermarket aisle sequence and display metadata.
- **`ThemeTest`**: Verifies light vs. dark color token contrasts and adaptive category badges.
- **`TileServiceTest`**: Validates Quick Settings Tile lifecycle, intent extras, and state updates.
- **`MainScreenViewModelTest`**: Modal transitions, sheet dismiss flows, and UDF state emissions.

To run the full test suite:
```bash
./gradlew test
```

To build and install the debug APK on a connected device or emulator:
```bash
./gradlew installDebug
```

To build an optimized, minified release bundle:
```bash
./gradlew assembleRelease
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
