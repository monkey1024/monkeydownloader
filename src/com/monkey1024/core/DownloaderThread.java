package com.monkey1024.core;

import com.monkey1024.constant.Constant;
import com.monkey1024.util.FileUtil;
import com.monkey1024.util.HttpUtil;
import com.monkey1024.util.LogUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.util.concurrent.RecursiveTask;

/*
    分块下载线程
 */
public class DownloaderThread extends RecursiveTask<String> {


    private long startPos;

    private long endPos;

    private RandomAccessFile accessFile;


    public DownloaderThread(long startPos, long endPos, RandomAccessFile accessFile) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.accessFile = accessFile;
    }

    @Override
    protected String compute() {
        if (endPos - startPos <= Downloader.size) {
            //获取文件名
            String httpFileName = Downloader.httpFileName;
            //分块文件的名字
            httpFileName = httpFileName + Constant.FILE_TEMP_SUFFIX + startPos;

            //获取本地文件大小
            long localFileContentLength = FileUtil.getFileContentLength(httpFileName);

            //获取分块下载连接
            HttpURLConnection httpUrlConnection = null;
            try {
                httpUrlConnection = HttpUtil.getHttpUrlConnection(Downloader.url, startPos, endPos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (InputStream input = httpUrlConnection.getInputStream();
                 BufferedInputStream bis = new BufferedInputStream(input);
                 RandomAccessFile accessFile = new RandomAccessFile(httpFileName, "rw")) {
                accessFile.seek(localFileContentLength);


                byte[] buffer = new byte[Constant.BYTE_SIZE];
                int len = -1;
                // 读到文件末尾则返回-1
                while ((len = bis.read(buffer)) != -1) {
                    DownloadInfoThread.downSize.add(len);
                    accessFile.write(buffer, 0, len);
                }
                return httpFileName;
            } catch (FileNotFoundException e) {
                LogUtil.error("ERROR! 要下载的文件路径不存在 {} ", Downloader.url);
                e.printStackTrace();
            } catch (Exception e) {
                LogUtil.error("下载出现异常");
                e.printStackTrace();
            } finally {
                httpUrlConnection.disconnect();
            }
        } else {
            long middle = (startPos + endPos) / 2;
            DownloaderThread leftTask = new DownloaderThread(startPos, middle, accessFile);
            DownloaderThread rightTask = new DownloaderThread(middle + 1, endPos, accessFile);

            //执行任务
            invokeAll(leftTask, rightTask);

            String left = leftTask.join();
            String right = rightTask.join();

            mergeAndClear(left);
            mergeAndClear(right);

        }
        return null;
    }

    private void mergeAndClear(String fileName) {
        if (fileName == null) {
            return;
        }
        byte[] buffer = new byte[Constant.BYTE_SIZE];
        int len;
        String[] temps = fileName.split(Constant.FILE_TEMP_SUFFIX);
        long index = Long.parseLong(temps[1]);
        synchronized (DownloaderThread.class) {
            try {
                accessFile.seek(index);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try (BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(fileName))) {
                while ((len = bis.read(buffer)) != -1) { // 读到文件末尾则返回-1
                    accessFile.write(buffer, 0, len);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //清理文件
        File file = new File(fileName);
        file.delete();

    }
}
