package com.gs.baseupdate;

/**
 * @author husky
 * create on 2019-05-23-11:38
 */
public interface BaseDownLoadListener {

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
     void downComplete() ;

    /**
     * 下载中。。。
     *
     * @param progress 下载进度
     */
    void onDown(int progress);
}
