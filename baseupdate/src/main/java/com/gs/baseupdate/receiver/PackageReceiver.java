package com.gs.baseupdate.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author husky
 * create on 2019-05-22-15:06
 */
public class PackageReceiver extends BroadcastReceiver {

    static String TAG = PackageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String packageName = intent.getDataString();
        // 安装
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            Log.d(TAG, "新安装了---》" + packageName);

        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            // 覆盖安装
            Log.d(TAG, "重新安装了---》" + packageName);

        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
            // 移除
            Log.d(TAG, "卸载了---》" + packageName);

        } else if (Intent.ACTION_PACKAGE_FIRST_LAUNCH.equals(intent.getAction())) {
            //第一次打开
        }

    }
}
