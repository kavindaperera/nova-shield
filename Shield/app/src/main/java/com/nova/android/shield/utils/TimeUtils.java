package com.nova.android.shield.utils;

import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;

public class TimeUtils {
    public static final String ZERO_LEADING_NUMBER_FORMAT = "%02d";
    public static final String NUMBER_FORMAT = "%d";

    public static long getTime() {
        return System.currentTimeMillis();
    }

    public static String getMessageDate(String str) {
        LocalDateTime localDateTime = new DateTime().toLocalDateTime();
        try {
            LocalDateTime localDateTime2 = new DateTime(Long.parseLong(str)).toLocalDateTime();
            if (localDateTime.getDayOfMonth() == localDateTime2.getDayOfMonth()) {
                return String.format(ZERO_LEADING_NUMBER_FORMAT, Integer.valueOf(localDateTime2.getHourOfDay())) + ":" + String.format(ZERO_LEADING_NUMBER_FORMAT, Integer.valueOf(localDateTime2.getMinuteOfHour()));
            } else if (localDateTime2.getDayOfMonth() == localDateTime.minusDays(1).getDayOfMonth()) {
                return Constants.MSG_DATE_YESTERDAY;
            } else {
                if (localDateTime.minusDays(6).isBefore(localDateTime2)) {
                    return Constants.DAYS_OF_WEEK[localDateTime2.getDayOfWeek() - 1];
                }
                return String.format(ZERO_LEADING_NUMBER_FORMAT, Integer.valueOf(localDateTime2.getDayOfMonth())) + "/" + String.format(ZERO_LEADING_NUMBER_FORMAT, Integer.valueOf(localDateTime2.getMonthOfYear())) + "/" + String.format(ZERO_LEADING_NUMBER_FORMAT, Integer.valueOf(localDateTime2.getYear()));
            }
        } catch (IllegalArgumentException unused) {
            return "";
        }
    }
}
