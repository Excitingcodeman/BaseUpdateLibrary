package com.gs.baseupdatelibrary;

import android.app.Application;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.multidex.MultiDex;
import com.gs.baseupdate.receiver.DownLoadReceiver;
import com.gs.baseupdate.receiver.PackageReceiver;

/**
 * @author husky
 * create on 2019-05-22-15:12
 */
public class MyApp extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_FIRST_LAUNCH);
        filter.addDataScheme("package");
        PackageReceiver receiver = new PackageReceiver();
        registerReceiver(receiver, filter);

        IntentFilter downFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        DownLoadReceiver downLoadReceiver = new DownLoadReceiver();
        registerReceiver(downLoadReceiver, downFilter);

    }
}
