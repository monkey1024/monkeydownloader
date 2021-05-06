package com.monkey1024.core;


import java.util.concurrent.atomic.LongAdder;

public class DownloadInfoThread implements Runnable {

    //本次累计下载的大小
    public static LongAdder downSize = new LongAdder();

    //本地已下载完成的内容大小，有可能是断点下载
    public static LongAdder finishedSize = new LongAdder();

    public double prevSize = 0;

    private long httpFileContentLength;

    public DownloadInfoThread(long httpFileContentLength) {
        this.httpFileContentLength = httpFileContentLength;
    }

    @Override
    public void run() {
        double mb = 1024d * 1024d;

        // 文件总大小
        String httpFileSize = String.format("%.2f", httpFileContentLength / mb);

            // 每秒速度
            int speed = (int) ((downSize.doubleValue() - prevSize) / 1024d);
            prevSize = downSize.doubleValue();

            // 剩余时间
            double remainingSize = httpFileContentLength - finishedSize.doubleValue() - downSize.doubleValue();
            String remainingTime = String.format("%.1f", remainingSize / 1024d / speed);
            if ("Infinity".equals(remainingTime)) {
                remainingTime = "-";
            }

            // 已下大小
            String currentFileSize = String.format("%.2f", downSize.doubleValue() / mb + finishedSize.doubleValue() / mb);
            String speedLog = String.format("> 已下载 %smb/%smb,速度 %skb/s,剩余时间 %ss", currentFileSize, httpFileSize, speed, remainingTime);
            System.out.print("\r");
            System.out.print(speedLog);
        }

}
