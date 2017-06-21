# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/santhosh/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn javax.xml.stream**
-dontwarn javax.lang.invoke**
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**

-dontwarn retrofit2.**
-keep class retrofit2.**
-keepattributes Signature
-keepattributes Exceptions
