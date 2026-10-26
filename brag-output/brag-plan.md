# Brag Plan: Oikos (Native Android Grocery App)

## 1. What is this app?
Oikos is a 100% offline-first, zero-bloat native Android grocery shopping experience engineered with Jetpack Compose, Material 3, and local SQLite, featuring on-device natural language parsing, aisle-optimized store walking order, custom multi-stage haptic waveforms, and zero cloud permissions.

## 2. The angle
"Why is every grocery app not this good?"
Most grocery apps are bloated, ad-infested, slow WebViews demanding an account and constant cloud sync just to remember a carton of milk. Oikos flips the script: pure native Android craftsmanship, instantaneous offline SQLite persistence, zero network permissions, and intelligent features that make in-store shopping effortless.

## 3. Hook (first 2-3 seconds)
"You don't need cloud sync to buy bananas."
Opening on a pitch-black AMOLED Obsidian canvas revealing the forest-green Oikos emblem and rapid natural language input: `2L whole milk @ 60` tokenizing instantaneously into quantity, unit, price, and aisle.

## 4. Key moments (the middle)
- **Instant NLP Parsing (`SmartAddParser`)**: Token preview chips (`2`, `L`, `Dairy`, `₹60`) resolving in real-time with duplicate detection.
- **Aisle-Aware Walking Order**: Auto-sorting active cart items according to physical supermarket aisle layout so you never backtrack across the store.
- **Tactile Store Shopping Mode**: Screen wake-lock enabled, giant 64dp+ tap targets, running cart spend counter, and tailored haptic feedback pulses (`tick`, `confirm`, `milestone`).
- **Home Screen Widget & Quick Settings Tile**: Direct SQLite-backed `RemoteViews` widget and notification shade Quick Tile for instant shopping access.

## 5. Outro / punchline
"Zero accounts. Zero cloud latency. Zero trackers. Just grocery shopping, perfected."
Oikos — Built native for Android.

## 6. User flow worth showing
1. **Entry**: Single-field natural language entry (`"6 organic eggs"`, `"artisan sourdough @ 150"`).
2. **Key Action**: Walking into the supermarket, tapping the Quick Settings Tile to enter Wake-Locked Shopping Mode, checking off items along the optimized aisle path.
3. **Result**: Reaching the checkout with exact running cart total, followed by the celebratory trip summary and multi-pulse haptic milestone.

## 7. Tone
- **Preset**: `polished` (with high-energy product confidence)
- **Creative direction**: "Quiet premium engineering showcase"
- **Interpretation**: Confident, technically precise, highlighting deliberate craft over generic marketing buzzwords.

## 8. Format & Duration
- **Format**: Landscape (1920x1080) / Mobile Showcase
- **Target Duration**: 20 seconds

## 9. Visual Identity (from the project)
- **Primary / Brand**: Forest Green (`#223E25`)
- **Accent**: Warm Honey (`#D4822B`) / Vivid Leaf (`#4EA157`)
- **Light Background**: Organic Kitchen Canvas (`#FAF9F5`)
- **Dark Background**: True AMOLED Obsidian Slate (`#0F1411`)
- **Typography**: Helvetica / Modern Sans-Serif
- **Strongest Visuals**: 
  - Store Walking Order aisle visualization
  - Running cart total progress card
  - Celebration trip completion screen
  - Home screen Glance widget

## 10. Share Copy (draft)
"Built Oikos: a 100% offline-first native Android grocery app with on-device NLP parsing, supermarket aisle walking order, and zero network permissions. Because you don't need cloud servers to buy eggs."

## 11. Audio Direction
- **Role**: Clean modern electronic bed with subtle acoustic warmth.
- **SFX Posture**: Motion-matched tactile ticks, confirmation chime on cart checkout, and gentle milestone fanfare on trip completion.
