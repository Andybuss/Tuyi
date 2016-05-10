# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\studio_sdk/tools/proguard/proguard-android.txt
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

-optimizations !code/allocation/variable
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**

-dontusemixedcaseclassnames
-dontshrink
-dontoptimize
-dontpreverify
-dontwarn com.umeng.comm.**
-dontwarn com.umeng.commm.**
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-keep class  activeandroid.** {*;}
-keep class com.umeng.** {*;}
-keep class android.** {*;}
-keepattributes *Annotation*


-ignorewarnings
-keep class org.apache.http.** {*;}
-dontwarn  org.apache.http.**
-keep class org.apache.http.* {*;}
-dontwarn  org.apache.http.*
-keep,allowshrinking class org.android.agoo.service.* {
    public <fields>;
    public <methods>;
}
-keep,allowshrinking class com.umeng.message.* {
    public <fields>;
    public <methods>;
}

-keep public class com.umeng.community.example.R$*{
    *;
}

-keep class com.umeng.comm.push.UmengPushImpl {
    public * ;
}
-keep class android.support.v4.** {*;}
-dontwarn android.webkit.WebView

-dontwarn com.tencent.weibo.sdk.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep public class com.tencent.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}

# adding push
-keep class com.umeng.message.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.protobuffer.* {
        public <fields>;
        public <methods>;
}

-keep class com.squareup.wire.* {
        public <fields>;
        public <methods>;
}

-keep class com.umeng.message.local.* {
        public <fields>;
        public <methods>;
}
-keep class org.android.agoo.impl.*{
        public <fields>;
        public <methods>;
}

-dontwarn com.xiaomi.**

-dontwarn com.ut.mini.**

-keep class org.android.agoo.service.* {*;}

-keep class org.android.spdy.**{*;}

-keep public class dong.lan.tuyi.R$*{
    public static final int *;
}
-keepattributes Exceptions,InnerClasses,Signature,EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*

#分享相关混淆
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes SourceFile,LineNumberTable
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public class com.umeng.socialize.* {*;}
-keep public class javax.**
-keep public class android.webkit.**
-keep public class com.tencent.** {*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}


-ignorewarnings
-keepattributes Signature
# 如果你使用了okhttp、okio的包，请添加以下混淆代码

-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep interface com.squareup.okhttp.** { *; }
-dontwarn okio.**

-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class * extends cn.bmob.v3.BmobObject {
    *;
}

-keep class * extends com.squareup.picasso.Transformation {
    *;
}

-keep class dong.lan.tuyi.PicassoHelper
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

-keep class com.lidroid.xutils.**{*;}
-dontwarn com.lidroid.xutils.**

-keep class com.nostra13.universaliamgeloader.**{*;}
-dontwarn com.nostra13.universaliamgeloader.**

-keep class com.squareup.picasso.**{*;}
-dontwarn com.squareup.picasso.**

