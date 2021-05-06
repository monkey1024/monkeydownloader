package com.monkey1024.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *  日志工具类
 */
public class LogUtil {

    public static boolean DEBUG = false;


    public static void info(String msg, Object... arg) {
        print(msg, " -INFO- ", arg);
    }

    public static void error(String msg, Object... arg) {
        print(msg, " -ERROR-", arg);
    }

    public static void debug(String msg, Object... arg) {
        if (DEBUG) { print(msg, " -DEBUG-", arg); }
    }

    private static void print(String msg, String level, Object... arg) {
        if (arg != null && arg.length > 0) {
            msg = String.format(msg.replace("{}", "%s"), arg);
        }
        String thread = Thread.currentThread().getName();
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")) + " " + thread + level + msg);
    }
}
