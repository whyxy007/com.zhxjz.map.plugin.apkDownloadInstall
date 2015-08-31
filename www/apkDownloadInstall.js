var cordova = require('cordova');

var apkDownloadInstall = {

	"startup" : function(url, onSuccessFn, onErrorFn) {
		cordova.exec(onSuccessFn, onErrorFn, 'ApkDownloadInstallPlugin',
				'startup', [url]);
	}

};

module.exports = apkDownloadInstall;

