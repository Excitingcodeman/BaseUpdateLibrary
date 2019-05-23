package com.gs.baseupdatelibrary;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.gs.baseupdate.*;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    DownLoadTool downLoadTool;
    Button button;

    String downLoadPath = "http://test-download.antrice.cn/000/file/yld/yld.apk";

    DownLoadManagerProgress downLoadManagerProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.downLoadButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downLoad();
            }
        });

        progressBar = findViewById(R.id.progress_horizontal);
        downLoadTool = new DownLoadTool.Builder(downLoadPath, this)
                .setDownLoadListener(new DownLoadListener() {
                    @Override
                    public void downStart() {
                        button.setEnabled(false);
                    }

                    @Override
                    public void downError() {
                        button.setEnabled(true);
                    }

                    @Override
                    public void downComplete(File file) {
                        button.setEnabled(true);
                        InstallTool.installApk(file, MainActivity.this);
                    }

                    @Override
                    public void onDown(int progress) {
                        progressBar.setProgress(progress);
                    }
                })
                .build();

    }


    void downLoad() {
        if (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        ) {
            listener(downLoadTool.downLoadManager());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (1 == requestCode) {
            boolean refresh = false;
            if (grantResults.length > 0) {
                for (int i : grantResults) {
                    if (i != PackageManager.PERMISSION_GRANTED) {
                        refresh = true;
                        break;
                    }
                }
            }
            if (!refresh) {
//                listener(downLoadTool.downLoadManager());
            }
        }
    }


    private void listener(long id) {
        downLoadManagerProgress = new DownLoadManagerProgress(this, id);
        downLoadManagerProgress.setDownLoadListener(new BaseDownLoadListener() {
            @Override
            public void downStart() {

            }

            @Override
            public void downError() {

            }

            @Override
            public void downComplete() {

            }

            @Override
            public void onDown(int progress) {
                Log.d("TAG", "当前进度" + progress);
                progressBar.setProgress(progress);
            }
        });
        downLoadManagerProgress.start();
    }
}
