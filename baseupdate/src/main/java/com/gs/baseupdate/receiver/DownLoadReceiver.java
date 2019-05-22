package com.gs.baseupdate.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.gs.baseupdate.InstallTool;

/**
 * @author husky
 * create on 2019-05-22-16:31
 */
public class DownLoadReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
            //点击了通知栏
            Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(viewDownloadIntent);

        } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            //下载完成后的广播
            long downLoadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadedFile = mDownloadManager.getUriForDownloadedFile(downLoadId);
            if (null != downloadedFile) {
                InstallTool.installApk(downloadedFile, context);
            }
        }
    }
}
