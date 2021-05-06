package com.monkey1024.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 *  Http工具类
 */
public class HttpUtil {

    /**
     *  获取Http连接对象
     * @param url
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getHttpURLConnection(String url) throws IOException {
        URL httpUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection)httpUrl.openConnection();
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.163 Safari/535.1");
        return httpURLConnection;
    }

    /**
     * 获取 HTTP 链接
     *
     * @param url
     * @param startPos
     * @param endPos
     * @return
     * @throws IOException
     */
    public static HttpURLConnection getHttpUrlConnection(String url, long startPos, long endPos) throws IOException {
        HttpURLConnection httpUrlConnection = getHttpURLConnection(url);
        LogUtil.debug("此线程下载内容区间 {}-{}", startPos, endPos);
        if (endPos != 0) {
            httpUrlConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-" + endPos);
        } else {
            httpUrlConnection.setRequestProperty("RANGE", "bytes=" + startPos + "-");
        }
        Map<String, List<String>> headerFields = httpUrlConnection.getHeaderFields();
        for (String s : headerFields.keySet()) {
            LogUtil.debug("此线程相应头{}:{}", s, headerFields.get(s));
        }
        return httpUrlConnection;
    }

    /**
     * 获取下载文件名
     *
     * @param url
     * @return
     */
    public static String getHttpFileName(String url) {
        int indexOf = url.lastIndexOf("/");
        return url.substring(indexOf + 1);
    }


}
