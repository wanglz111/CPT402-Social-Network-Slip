package com.xjtlu.slip.utils;

import java.text.SimpleDateFormat;

public class TimeToUnix {

    //Get current timestamp
    public static long getCurrentTime() {
        return System.currentTimeMillis() / 1000;
    }

    //Timestamp to time
    public static String getTime(long time) {
        String timeStr = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timeStr = sdf.format(time * 1000);
        return timeStr;
    }

}
