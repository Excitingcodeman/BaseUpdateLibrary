package com.gs.baseupdate;

import java.io.File;

/**
 * @author husky
 * create on 2019-05-22-10:44
 * 下载监听器
 */
public interface DownLoadListener {
    /**
     * 下载开始的准备
     */
    void downStart();

    /**
     * 下载出错了
     */
    void downError();

    /**
     * 下载完成了
     */
    void downComplete(File file);

    /**
     * 下载中。。。
     *
     * @param progress 下载进度
     */
    void onDown(int progress);


}
