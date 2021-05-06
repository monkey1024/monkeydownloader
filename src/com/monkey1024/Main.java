package com.monkey1024;

import com.monkey1024.core.Downloader;
import com.monkey1024.util.LogUtil;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //下载地址
        String url = null;
        if (args == null || args.length == 0) {
            for (; ; ){
                LogUtil.info("请输入下载连接");

                Scanner scanner = new Scanner(System.in);
                url = scanner.next();
                if (url != null)
                    break;
            }
        }else {
            url = args[0];
        }
        //通过下载器下载
        Downloader.url = url;
        Downloader downloader = new Downloader();
        downloader.download();
    }
}
