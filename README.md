# com.zhxjz.map.plugin.apkDownloadInstall
安装包下载并自动安装，主要用于安装包更新<br/>
用法：<br/>
<pre>
var downloadURL = "http://210.10.3.61:8080/server/installPackage/V3.9-28/V3.9-28.apk";
apkDownloadInstall.startup(downloadURL,
        function(isSuccess){
                alert(isSuccess);
        },
        function(){
                alert('error');
        }
);
<br/>
注意：
需要在AndroidManifest.xml里面增加权限访问网络和写存储卡，
《uses-permission android:name="android.permission.INTERNET" /》 
《uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /》
<h1>enjoy it :)</h1>

</pre>
