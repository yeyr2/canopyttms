package com.ttms.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil extends ch.qos.logback.core.util.TimeUtil {

    /**
     * 得到一天开始的时间,CST标准,快8小时
     * @param timestamp
     * @return
     */
    public static long getTodayStart(long timestamp) {
        return timestamp - (timestamp % (24 * 60 * 60 * 1000)) - 8 * 60 * 60 * 1000;
    }

    /**
     * 检查字符串时间格式是否为 yyyy-MM-dd HH:mm:ss
     * @param dateString
     * @return
     */
    public static boolean validationFormat(String dateString) {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            Date date = sdf.parse(dateString);
        } catch (ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * 时间戳转为字符串
     * @param timestamp
     * @return
     */
    public static String getFormatByDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(timestamp);
        String dateStr;
        dateStr = sdf.format(date);

        return  dateStr;
    }

    /**
     * 字符串转为时间戳
     * @param time
     * @return
     */
    public static long getDateByFormat(String time) throws ParseException {
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = sdf.parse(time);

        return date.getTime();
    }

    /**
     * 得到一天的开始时间,传入时间格式为 yyyy-MM-dd
     * @param time
     * @return
     */
    public static long getStartByDay(String time) {
        LocalDate date = LocalDate.parse(time, DateTimeFormatter.ISO_LOCAL_DATE);

        // 以当前系统时区设置开始今天的时间
        ZonedDateTime startOfDay = date.atStartOfDay(ZoneId.systemDefault());

        Instant instant = startOfDay.toInstant();

        return instant.toEpochMilli();
    }
}