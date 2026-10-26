# Oikos Grocery App Proguard / R8 Optimization Rules

# Preserve Kotlin Reflection / Serialization metadata
-keepattributes *Annotation*, InnerClasses, EnclosingMethod
-keepattributes Signature
-keepattributes SourceFile, LineNumberTable

# Keep domain models
-keep class com.example.oikos.model.** { *; }

# Keep AppWidget Provider
-keep class com.example.oikos.widget.OikosAppWidgetProvider { *; }

# SQLite Helper & Cursor reflection guards
-keepclassmembers class com.example.oikos.data.db.OikosDatabaseHelper { *; }

# Jetpack Compose R8 rules
-keepclassmembers class androidx.compose.ui.platform.AndroidComposeView { *; }
-dontwarn androidx.compose.**

# Coroutines
-dontwarn kotlinx.coroutines.**
