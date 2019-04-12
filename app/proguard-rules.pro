-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class org.ocpsoft.prettytime.i18n.**
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class android.net.http.** { *; }
-dontwarn android.net.http.**


-keep class android.support.v7.widget.LinearLayoutManager { *; }
-keep public class * extends android.support.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}


-keep public class org.jsoup.** {
    public *;
}
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keepattributes *Annotation*,EnclosingMethod,Signature
-keepnames class com.fasterxml.jackson.** { *; }
 -dontwarn com.fasterxml.jackson.databind.**
 -keep class org.codehaus.** { *; }
 -keepclassmembers public final enum org.codehaus.jackson.annotate.JsonAutoDetect$Visibility {
 public static final org.codehaus.jackson.annotate.JsonAutoDetect$Visibility *; }


-keep public class Model.class.** {
  public void set*(***);
  public *** get*();
}
-keep public class CallData.class.** {
  public void set*(***);
  public *** get*();
}

-dontwarn com.squareup.okhttp.**

-dontskipnonpubliclibraryclasses
-dontobfuscate
-forceprocessing
-optimizationpasses 5

-keep class * extends android.app.Activity
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}