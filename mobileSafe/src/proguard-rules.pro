-keepattributes *Annotation*//使用注解需要添加
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {#指定不混淆所有的JNI方法
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {#所有View的子类及其子类的get、set方法都不进行混淆
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {#不混淆Activity中参数类型为View的所有方法
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {#不混淆Enum类型的指定方法
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#不混淆Parcelable和它的子类，还有Creator成员变量
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#不混淆R类里及其所有内部static类中的所有static变量字段
-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**//不提示兼容库的错误警告

-keepattributes Signature  #反射不混淆
-keepattributes EnclosingMethod

#JavaBean不混淆
-keep class com.boyzhang.projectmobilesafe.bean.**{*;}#不混淆所有的com.boyzhang.projectmobilesafe.bean包下的类和这些类的所有成员变量

#不混淆Serializable接口的子类中指定的某些成员变量和方法,在反序列画的时候, 需要正确的类名等, 在Android 中大多是实现 Parcelable来序列化的
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}