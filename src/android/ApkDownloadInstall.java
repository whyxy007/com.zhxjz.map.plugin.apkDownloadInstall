package com.zhxjz.map.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

/**
 * 下载更新安装包并自动安装工具
 * 
 * @author yangxy
 * 
 * 使用方法：
 * apkDownloadInstall('http://210.10.3.61:8080/server/installPackage/V3.9-28/V3.9-28.apk', function(msg){alert(msg)}, function(){});
 * 如果下载成功则alert出来的是true，否则为false
 * 
 * 注意：
 * 需要在AndroidManifest.xml里面增加权限访问网络和写存储卡，
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */

public class ApkDownloadInstall extends CordovaPlugin {

	private ProgressBar mProgress;
	private Dialog downloadDialog;

	private boolean cancelUpdate; 	// 设置取消状态
	private boolean success; 		// 更新成功状态
	private String downloadUrl; 	// 下载APK地址
	private String apkName; 		// APK名字
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
			if(action.equals("startup")) {
				startup(cordova.getActivity(), args.getString(0), callbackContext);
			}
			return true;
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
			return false;
		}
	}

	private void startup(Activity activity, String url, CallbackContext callbackContext) {
		this.cancelUpdate = false;
		this.success = false;
		this.downloadUrl = url;
		this.apkName = url.substring(url.lastIndexOf("/")+1);
		initWidget(activity);
		startDownload(activity, callbackContext);
	}
	
	private void initWidget(Activity activity) {
		Builder builder = new Builder(activity);
		builder.setTitle("正在更新");

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LinearLayout mainLayout = new LinearLayout(activity);
		mainLayout.setLayoutParams(params);
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		mProgress = new ProgressBar(activity, null,
				android.R.attr.progressBarStyleHorizontal);
		mProgress.setLayoutParams(params);
		mProgress.setMax(100);
		mProgress.setProgress(50);
		mainLayout.addView(mProgress);
		builder.setView(mainLayout);

		builder.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelUpdate = true;
			}
		});

		downloadDialog = builder.create();
		downloadDialog.show();
	}
	
	private void startDownload(final Activity activity, final CallbackContext callbackContext) {
		new Thread() {
			public void run() {
				InputStream is = null;
				FileOutputStream fos = null;
				try {
					if (Environment.getExternalStorageState().equals(
							Environment.MEDIA_MOUNTED)) {
						// 获得存储卡的路径
						String apkSavePath = Environment
								.getExternalStorageDirectory()
								+ File.separator
								+ "DOWNLOAD";
						if (!new File(apkSavePath).exists()) {
							new File(apkSavePath).mkdirs();
						}
						HttpURLConnection conn = (HttpURLConnection) new URL(downloadUrl)
								.openConnection();
						conn.connect();
						// 获取文件大小
						int length = conn.getContentLength();

						File apkFile = new File(apkSavePath, apkName);
						is = conn.getInputStream();
						fos = new FileOutputStream(apkFile);
						int count = 0;
						byte buf[] = new byte[1024];
						do {
							int numread = is.read(buf);
							count += numread;
							mProgress.setProgress((int) (((float) count / length) * 100));
							// 更新进度
							if (numread <= 0) {
								mProgress.setProgress(100);
								success = true;
								break;
							}
							// 写入文件
							fos.write(buf, 0, numread);
						} while (!cancelUpdate);// 点击取消就停止下载.
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					try {
						fos.close();
						is.close();
						downloadDialog.dismiss();
						installApk(activity, callbackContext);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void installApk(Activity activity, CallbackContext callbackContext) throws JSONException {
		File apkfile = new File(Environment
				.getExternalStorageDirectory()
				+ File.separator
				+ "DOWNLOAD", apkName);
		if(success) {
			if (!apkfile.exists()) 
				return;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + apkfile.toString()), 
					"application/vnd.android.package-archive");
			activity.startActivity(i);
		} else {
			apkfile.delete();
		}
		callbackContext.success(String.valueOf(success));
	}
}
