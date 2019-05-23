package com.gs.baseupdate;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.webkit.URLUtil;
import androidx.annotation.NonNull;

/**
 * @author husky
 * create on 2019-05-23-09:21
 */
public class DownLoadManagerHelper {

    private DownLoadManagerHelper() {
        throw new RuntimeException("Can't create another instance");
    }

    /**
     * 系统下载  提供给webview拦截的下载事件
     * 注：需要先申请读写权限  不然会失败
     *
     * @param activity           当前的activity
     * @param url                地址
     * @param contentDisposition 描述
     * @param mimeType           类型
     * @return 下载的任务id
     */
    public static long downLoadMangerUrl(Activity activity, String url, String contentDisposition, String mimeType) {
        DownloadManager mDownloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(resource);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setDescription(contentDisposition);
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        return mDownloadManager.enqueue(request);
    }

    /**
     * 判断系统下载是否可用
     *
     * @param context 上下文
     * @return
     */
    public static boolean downLoadMangerIsEnable(@NonNull Context context) {
        int state = context.getApplicationContext().getPackageManager()
                .getApplicationEnabledSetting("com.android.providers.downloads");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } else {
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
        }
    }

    /**
     * 去设置界面打开 系统下载
     *
     * @param context 上下文
     */
    public static void openDownLoad(@NonNull Context context) {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
