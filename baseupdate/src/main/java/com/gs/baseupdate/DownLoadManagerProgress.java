package com.gs.baseupdate;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static android.app.DownloadManager.*;
import static com.gs.baseupdate.DownLoadTool.*;

/**
 * @author husky
 * create on 2019-05-23-11:17
 */
public class DownLoadManagerProgress {

    private Context context;
    private long downloadId;

    private int oldProgress = 0;

    private BaseDownLoadListener downLoadListener;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWN_START:
                    if (downLoadListener != null) {
                        downLoadListener.downStart();
                    }
                    break;
                case DOWNING:
                    if (downLoadListener != null) {
                        downLoadListener.onDown(msg.arg1);
                    }
                    start();
                    break;
                case DOWN_ERROR:
                    if (downLoadListener != null) {
                        downLoadListener.downError();
                    }
                    break;
                case DOWN_COMPLETE:
                    if (downLoadListener != null) {
                        downLoadListener.downComplete();
                    }
                    break;
            }
        }
    };

    public DownLoadManagerProgress(Context context, long downloadId) {
        this.context = context;
        this.downloadId = downloadId;
    }

    public void setDownLoadListener(BaseDownLoadListener downLoadListener) {
        this.downLoadListener = downLoadListener;
    }

    public void start() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBytesAndStatus();

            }
        }, 500);
    }


    void getBytesAndStatus() {
        int[] bytesAndStatus = new int[]{
                -1, -1, 0
        };
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                Message message = Message.obtain();
                //已经下载文件大小
                bytesAndStatus[0] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                //下载文件的总大小
                bytesAndStatus[1] = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                //下载状态
                bytesAndStatus[2] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                if (STATUS_SUCCESSFUL == bytesAndStatus[2]) {
                    //下载成功
                    message.what = DOWN_COMPLETE;
                    handler.sendMessage(message);
                } else if (STATUS_FAILED == bytesAndStatus[2]) {
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                } else if (STATUS_RUNNING == bytesAndStatus[2]) {
                    int progress = new BigDecimal(bytesAndStatus[0]).divide(new BigDecimal(bytesAndStatus[1]), 2, RoundingMode.HALF_UP).multiply(hundred).intValue();
                    Log.d("TAG", "当前进度" + progress);
                    Log.d("TAG", "已经下载文件大小" + bytesAndStatus[0]);
                    Log.d("TAG", "下载文件的总大小" + bytesAndStatus[1]);
                    if (progress < oldProgress) {
                        progress = oldProgress;
                    }

                    message.arg1 = progress;
                    message.what = DOWNING;
                    handler.sendMessage(message);
                    oldProgress = progress;

                } else if (STATUS_PENDING == bytesAndStatus[2]) {
                    oldProgress = 0;
                    message.what = DOWNING;
                    handler.sendMessage(message);
                }
            }
        } catch (Exception e) {
            Message message = Message.obtain();
            message.what = DOWN_START;
            handler.sendMessage(message);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

    }


}
