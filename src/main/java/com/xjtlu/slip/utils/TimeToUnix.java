package com.xjtlu.slip.utils;

import java.text.SimpleDateFormat;

public class TimeToUnix {

    //获取当前时间戳
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    //时间戳转换成时间
    public static String getTime(long time) {
        String timeStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeStr = sdf.format(time * 1000);
        return timeStr;
    }
}
