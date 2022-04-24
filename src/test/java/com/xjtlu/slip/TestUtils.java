package com.xjtlu.slip;

import org.junit.jupiter.api.Test;

import static com.xjtlu.slip.utils.TimeToUnix.getCurrentTime;
import static com.xjtlu.slip.utils.TimeToUnix.getTime;

public class TestUtils {
    @Test
    public void testTimeUtils() throws Exception {
        System.out.println(getCurrentTime());
        System.out.println(getTime(getCurrentTime()));
    }
}
