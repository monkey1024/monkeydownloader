package com.monkey1024.util;

import java.io.File;

public class FileUtil {

    /**
     * 获取文件内容长度
     *
     * @param name
     * @return
     */
    public static long getFileContentLength(String name) {
        File file = new File(name);
        return file.exists() && file.isFile() ? file.length() : 0;
    }

}