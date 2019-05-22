package com.gs.baseupdate;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

/**
 * @author husky
 * create on 2019-05-22-17:23
 * Android8.0的未知应用的权限申请
 */
public class AndroidORequestActivity extends Activity {
    /**
     * Android 8.0 打开未知权限的安装
     */
    public static final int REQUEST_O = 2000;


    private Uri fileUri;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fileUri = getIntent().getData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fileUri = getIntent().getData();
        if (null == fileUri) {
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean requestPackageInstalls = getPackageManager().canRequestPackageInstalls();
            if (!requestPackageInstalls) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, REQUEST_O);
            } else {
                //安装
                goInstall();
            }
        } else {
            //安装
            goInstall();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_O == requestCode) {//有注册权限且用户允许安装
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goInstall();
            } else {
                //将用户引导至安装未知应用界面。
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_O);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (getPackageManager().canRequestPackageInstalls()) {
                    //安装apk
                    goInstall();
                } else {
                    //提示
                    Toast.makeText(this, getString(R.string.install_permissions_tips), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void goInstall() {
        InstallTool.installApk(fileUri, this);
        finish();
    }
}
