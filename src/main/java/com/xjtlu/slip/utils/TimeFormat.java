package com.xjtlu.slip.utils;

import java.util.Date;

public class TimeFormat {
    private static final long ONE_MINUTE = 60000L;
    private static final long ONE_HOUR = 3600000L;
    private static final long ONE_DAY = 86400000L;
    private static final long ONE_WEEK = 604800000L;
    private static final String ONE_SECOND_AGO = " second ago";
    private static final String MORE_SECOND_AGO = " seconds ago";
    private static final String ONE_MINUTE_AGO = " minute ago";
    private static final String MORE_MINUTE_AGO = " minutes ago";
    private static final String ONE_HOUR_AGO = " hour ago";
    private static final String MORE_HOUR_AGO = " hours ago";
    private static final String ONE_DAY_AGO = " day ago";
    private static final String MORE_DAY_AGO = " days ago";
    private static final String ONE_MONTH_AGO = " month ago";
    private static final String MORE_MONTH_AGO = " months ago";
    private static final String ONE_YEAR_AGO = " year ago";
    private static final String MORE_YEAR_AGO = " years ago";

    //时间转换
    public static String format(long timeMillis) {
        long delta = new Date().getTime() - timeMillis * 1000;
        if (delta < ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return ((seconds <= 0 ? 1 : seconds) == 1)? 1 + ONE_SECOND_AGO: seconds + MORE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return ((minutes <= 0 ? 1 : minutes) == 1)? 1 + ONE_MINUTE_AGO: minutes + MORE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return ((hours <= 0 ? 1 : hours) == 1)? 1 + ONE_HOUR_AGO: hours + MORE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "yesterday";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return ((days <= 0 ? 1 : days) == 1)? 1 + ONE_DAY_AGO: days + MORE_DAY_AGO;
        }
        if (delta < 12L * 4L * ONE_WEEK) {
            long months = toMonths(delta);
            return ((months <= 0 ? 1 : months) == 1)? 1 + ONE_MONTH_AGO: months + MORE_MONTH_AGO;
        } else {
            long years = toYears(delta);
            return ((years <= 0 ? 1 : years) == 1)? 1 + ONE_YEAR_AGO: years + MORE_YEAR_AGO;
        }
    }

    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }

    private static long toYears(long date) {
        return toMonths(date) / 365L;
    }
}
