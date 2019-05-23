package com.gs.baseupdate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * @author husky
 * create on 2019-05-22-17:39
 */
public class InstallTool {

    /**
     * @param uri     Uri
     * @param context Context
     */
    public static void installApk(@NonNull Uri uri, @NonNull Context context) {
        int sdkVersion = context.getApplicationInfo().targetSdkVersion;
        if (sdkVersion >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean requestPackageInstalls = context.getPackageManager().canRequestPackageInstalls();
            if (!requestPackageInstalls) {
                Intent intent = new Intent(context, AndroidORequestActivity.class);
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } else {
                doInstall(uri, context);
            }
        } else {
            doInstall(uri, context);
        }
    }


    private static void doInstall(@NonNull Uri uri, @NonNull Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 走.FileProvider
     *
     * @param file    文件
     * @param context Context
     */
    public static void installApk(@NonNull File file, @NonNull Context context) {
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, context.getPackageName() + ".FileProvider", file);
        } else {
            data = Uri.fromFile(file);
        }

        if (null != data) {
            installApk(data, context);
        }

    }

    /**
     * @param file         文件
     * @param context      Context
     * @param fileProvider 内容提供者
     */
    public static void installApk(@NonNull File file, @NonNull Context context, @NonNull String fileProvider) {
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, fileProvider, file);
        } else {
            data = Uri.fromFile(file);
        }

        if (null != data) {
            installApk(data, context);
        }

    }

    public static void openFile(@NonNull Uri uri, @NonNull Context context, String mineType) {
        if ("application/vnd.android.package-archive".equals(mineType)
                || "application/octet-stream".equals(mineType)) {
            //兼容部分手机下载的.apk文件的类型是application/octet-stream
            installApk(uri, context);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.setDataAndType(uri, mineType);
            context.startActivity(intent);
        }

    }
}
