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
</pre>
<h1>enjoy it :)</h1>
