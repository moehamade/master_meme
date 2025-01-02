# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep enum classes and their values
-keepclassmembers enum * { *; }

# Keep all model classes in the domain.model package
-keep class com.mobilecampus.mastermeme.meme.domain.model.** { *; }

# Keep all entity classes in the data.local.entity package
-keep class com.mobilecampus.mastermeme.meme.data.local.entity.** { *; }

# Keep serialization-related information
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Keep Room database classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# Preserve line numbers for debugging
-keepattributes SourceFile,LineNumberTable

# Keep the MemeRenderer and MemeTextPainter classes
-keep class com.mobilecampus.mastermeme.meme.presentation.screens.editor.util.MemeRenderer { *; }
-keep class com.mobilecampus.mastermeme.meme.presentation.screens.editor.util.MemeTextPainter { *; }