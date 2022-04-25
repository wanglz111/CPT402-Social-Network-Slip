package com.xjtlu.slip;

import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import static com.xjtlu.slip.utils.TimeToUnix.getCurrentTime;
import static com.xjtlu.slip.utils.TimeToUnix.getTime;

public class TestUtils {
    @Test
    public void testTimeUtils() throws Exception {
        System.out.println(getCurrentTime());
        System.out.println(getTime(getCurrentTime()));
    }

    @Test
    public void testConvertTime() throws Exception {
        Date sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-04-24 23:38:58");
        //data to unix timestamp


    }
}
