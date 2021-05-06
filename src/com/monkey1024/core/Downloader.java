package com.monkey1024.core;

import com.monkey1024.constant.Constant;
import com.monkey1024.util.FileUtil;
import com.monkey1024.util.HttpUtil;
import com.monkey1024.util.LogUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
    下载器
 */
public class Downloader {

    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static int contentLength;

    public static String httpFileName;

    public static String url;

    public static long size;

    public void download() {

        //获取文件名
        httpFileName = HttpUtil.getHttpFileName(url);
        //文件下载路径
        httpFileName = Constant.PATH + httpFileName;

        HttpURLConnection httpUrlConnection = null;
        DownloadInfoThread downloadInfoThread = null;
        try (RandomAccessFile oSavedFile = new RandomAccessFile(httpFileName, "rw")) {
            httpUrlConnection = HttpUtil.getHttpURLConnection(url);
            //获取本地文件大小
            long localFileLength = FileUtil.getFileContentLength(httpFileName);
            //下载文件的大小
            contentLength = httpUrlConnection.getContentLength();
            if (localFileLength >= contentLength) {
                LogUtil.info("{}已下载完毕，无需重新下载", httpFileName);
                return;
            }

            downloadInfoThread = new DownloadInfoThread(contentLength);
            //设置已下载的大小
            DownloadInfoThread.finishedSize.add(localFileLength);
            //延迟1秒且间隔1秒执行
            scheduledExecutorService.scheduleAtFixedRate(downloadInfoThread, 1, 1, TimeUnit.SECONDS);

            //计算切分后的每块下载大小
            size = contentLength / Constant.THREAD_NUM;


            DownloaderThread downloaderThread = new DownloaderThread(0, contentLength, oSavedFile);
            forkJoinPool.invoke(downloaderThread);

            System.out.print("\r");
            System.out.print("已下载完成");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭连接和线程池
            httpUrlConnection.disconnect();
            forkJoinPool.shutdown();
            scheduledExecutorService.shutdownNow();

        }
    }

}
