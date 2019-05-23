package com.gs.baseupdate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author husky
 * create on 2019-05-22-10:47
 */
public class DownLoadTool {
    /**
     * 下载开始
     */
    private static final int DOWN_START = 1000;
    /**
     * 下载中
     */
    private static final int DOWNING = 1001;

    /**
     * 下载完成
     */
    private static final int DOWN_COMPLETE = 1002;
    /**
     * 下载失败
     */
    private static final int DOWN_ERROR = 1003;

    private static final BigDecimal hundred = new BigDecimal(100);

    /**
     * 读写的权限
     */
    private static final String[] permissionsArray = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWN_START:
                    if (null != builder.downLoadListener) {
                        builder.downLoadListener.downStart();
                    }
                    break;
                case DOWNING:

                    if (null != builder.downLoadListener) {
                        builder.downLoadListener.onDown(msg.arg1);
                    }
                    break;
                case DOWN_ERROR:
                    if (null != builder.downLoadListener) {
                        builder.downLoadListener.downError();
                    }
                    break;

                case DOWN_COMPLETE:
                    if (null != builder.downLoadListener) {
                        builder.downLoadListener.downComplete((File) msg.obj);
                    }
                    break;
            }
        }
    };
    private Builder builder;

    private DownLoadTool(@NonNull Builder builder) {
        this.builder = builder;
        ActivityCompat.requestPermissions(builder.activity, permissionsArray, 1);
    }

    /**
     * 使用自己的下载
     */
    public void downLoadFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File apkFile;
                if (null != builder.downLoadFile) {
                    apkFile = builder.downLoadFile;
                } else if (!TextUtils.isEmpty(builder.downLoadFilePath)) {
                    apkFile = new File(builder.downLoadFilePath);
                } else {
                    apkFile = new File(
                            builder.activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            builder.downUrl.substring(builder.downUrl.lastIndexOf("/") + 1));
                }
                if (apkFile.exists()) {
                    apkFile.delete();
                }

                InputStream in = null;
                FileOutputStream out = null;
                try {
                    URL url = new URL(builder.downUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(false);
                    urlConnection.setConnectTimeout(20 * 1000);
                    urlConnection.setReadTimeout(20 * 1000);
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("Charset", "UTF-8");
                    urlConnection.setRequestProperty("Accept-Encoding", "gzip, deflate");
                    urlConnection.connect();
                    //文件总大小
                    long bytetotal = urlConnection.getContentLength();
                    //下载的大小
                    long byteSum = 0;
                    int byteRead;
                    in = urlConnection.getInputStream();
                    out = new FileOutputStream(apkFile);
                    byte[] buffer = new byte[1024];
                    int oldProgress = 0;
                    Message message = Message.obtain();
                    message.arg1 = oldProgress;
                    message.what = DOWN_START;
                    handler.sendMessage(message);
                    while ((byteRead = in.read(buffer)) != -1) {
                        byteSum += byteRead;
                        int newProgress = new BigDecimal(byteSum).divide(new BigDecimal(bytetotal), 2, RoundingMode.HALF_UP).multiply(hundred).intValue();
                        if (newProgress > oldProgress) {
                            oldProgress = newProgress;
                        }
                        message = Message.obtain();
                        message.arg1 = oldProgress;
                        message.what = DOWNING;
                        handler.sendMessage(message);
                        out.write(buffer, 0, byteRead);

                    }
                    out.flush();
                    out.close();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    Message message = Message.obtain();
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message message = Message.obtain();
                    message.what = DOWN_COMPLETE;
                    message.obj = apkFile;
                    handler.sendMessage(message);
                }
            }
        }).start();
    }

    /**
     * 使用系统的downloadManage
     *
     * @return 下载的id
     */
    public long downLoadManager() {
        DownloadManager mDownloadManager = (DownloadManager) builder.activity.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri resource = Uri.parse(builder.downUrl);
        DownloadManager.Request request = new DownloadManager.Request(resource);
        //start 一些非必要的设置
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setTitle(builder.title);
        request.setDescription(builder.contentMessage);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, builder.newVersion + builder.downUrl.substring(builder.downUrl.lastIndexOf("/") + 1));
        //end 一些非必要的设置
        return mDownloadManager.enqueue(request);


    }

    public static class Builder {
        //下载的监听器
        private DownLoadListener downLoadListener;
        //下载后的保存文件
        private File downLoadFile;
        //下载后的保存文件路径
        private String downLoadFilePath;
        //下载的地址
        private String downUrl;
        //用于申请权限和创建文件使用
        private Activity activity;
        //提示头
        private String title;
        //提示内容
        private String contentMessage;
        //使用downLoadManager时，增加一个标识
        private String newVersion = "";


        public Builder(@NonNull String downUrl, @NonNull Activity activity) {
            this.downUrl = downUrl;
            this.activity = activity;
        }

        public Builder setNewVersion(String newVersion) {
            this.newVersion = newVersion;
            return this;
        }

        public Builder setDownLoadListener(@NonNull DownLoadListener downLoadListener) {
            this.downLoadListener = downLoadListener;
            return this;
        }

        public Builder setDownLoadFile(@NonNull File downLoadFile) {
            this.downLoadFile = downLoadFile;
            return this;
        }

        public Builder setDownLoadFilePath(@NonNull String downLoadFilePath) {
            this.downLoadFilePath = downLoadFilePath;
            return this;
        }


        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentMessage(String contentMessage) {
            this.contentMessage = contentMessage;
            return this;
        }

        public DownLoadTool build() {
            return new DownLoadTool(this);
        }
    }

}
