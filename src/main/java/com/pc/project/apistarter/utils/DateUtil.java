package com.pc.project.apistarter.utils;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期工具类
 */
public class DateUtil {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_ORDER_PATTERN = "yyyy/MM/dd HH:mm";
    public static final FastDateFormat DATE_TIME_FORMATTER_SPLIT = FastDateFormat.getInstance(DATE_PATTERN);
    //存个date不能保证线程安全，应该存SimpleDateFormater比较
    public static final ThreadLocal<Date> THREAD_LOCAL = new ThreadLocal<Date>();

    private static Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {
    }

    /**
     * 将dateStr类型转换成为Date类型
     */
    public static Date parseToDate(String dateStr) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将dateStr类型转换成为Date类型
     *
     * @param dateStr
     * @param pattern 日期格式
     */
    public static Date parseToDate(String dateStr, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将Date转换成为'yyyy-MM-dd hh:mm:ss'类型的字符串
     */
    public static String formatDateTime(Date date) {
        if (null == date) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        return dateFormat.format(date);
    }

    /**
     * 将Date转换成为'yyyy-MM-dd hh:mm:ss'类型的字符串
     */
    public static String formatDate(Date date, String formatPattern) {
        if (null == date) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(formatPattern);
        return dateFormat.format(date);
    }

    /**
     * 获取当前时间对象
     */
    public static Date getCurrentDate() {
        THREAD_LOCAL.set(new Date());
        return THREAD_LOCAL.get();
    }


    public static Date longToDate(long currentTime, String formatType) {
        Date date = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(formatType);
            Date dateOld = new Date(currentTime);
            String sDateTime = formatter.format(dateOld);
            date = formatter.parse(sDateTime);
        } catch (Exception e) {
            logger.error("longToDate longTime:{}, formatType: {} error : {}", currentTime, formatType, e);
        }
        return date;
    }

    /**
     * @param date
     * @param year
     * @return
     */
    public static Date addYear(Date date, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * @param date
     * @param day
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, day);
        return calendar.getTime();
    }

    /**
     * 有效期一年，left=today,right=nextyear
     */
    public static Pair<Date, Date> validForOneYear() {
        Date today = new Date();
        Date nextYear = addDay(addYear(today, 1), -1);

        return Pair.of(today, nextYear);
    }

    /**
     * 根据单位字段比较两个日期
     *
     * @param date      日期1
     * @param otherDate 日期2
     * @param withUnit  单位字段，从Calendar field取值
     * @return 等于返回0值, 大于返回大于0的值 小于返回小于0的值
     */
    public static int compareDate(Date date, Date otherDate, int withUnit) {
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(date);
        Calendar otherDateCal = Calendar.getInstance();
        otherDateCal.setTime(otherDate);

        switch (withUnit) {
            case Calendar.YEAR:
                dateCal.clear(Calendar.MONTH);
                otherDateCal.clear(Calendar.MONTH);
                break;
            case Calendar.MONTH:
                dateCal.set(Calendar.DATE, 1);
                otherDateCal.set(Calendar.DATE, 1);
                break;
            case Calendar.DATE:
                dateCal.set(Calendar.HOUR_OF_DAY, 0);
                otherDateCal.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case Calendar.HOUR:
                dateCal.clear(Calendar.MINUTE);
                otherDateCal.clear(Calendar.MINUTE);
                break;
            case Calendar.MINUTE:
                dateCal.clear(Calendar.SECOND);
                otherDateCal.clear(Calendar.SECOND);
                break;
            case Calendar.SECOND:
                dateCal.clear(Calendar.MILLISECOND);
                otherDateCal.clear(Calendar.MILLISECOND);
                break;
            case Calendar.MILLISECOND:
                break;
            default:
                throw new IllegalArgumentException("withUnit 单位字段 " + withUnit + " 不合法！！");
        }
        return dateCal.compareTo(otherDateCal);
    }

    /**
     * 获取一年前的毫秒时间戳
     */
    public static long getMillSecondsOfYearBefore() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -365);
        return calendar.getTimeInMillis();
    }

    /**
     * 获取2个小时前Date
     *
     * @return
     */
    public static Date getLastTwoHourDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 2);
        return calendar.getTime();
    }

    /**
     * 获取前一小时整点Date
     * 例如 13:00:00 14:00:00
     *
     * @return
     */
    public static Date getLastHourSharpDate() {
        LocalDateTime localDateTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(-1L);
        Instant startInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(startInstant);
    }

    /**
     * 获取当前小时整点Date
     * 例如 13:00:00 14:00:00
     */
    public static Date getNowHourSharpDate() {
        LocalDateTime localDateTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        Instant startInstant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(startInstant);
    }

    /**
     * @param num
     * @param unit
     * @return
     */
    public static Date getDateTimeByTerm(int num, int unit) {

        Calendar date = Calendar.getInstance();
        date.setTime(new Date());
        date.set(unit, date.get(unit) + num);

        return date.getTime();
    }

    public static Date getDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance(TimeZone.getDefault());
        c.clear();
        c.set(year, month, day);
        return c.getTime();
    }

    /**
     * 获取日期的年份。失败返回0。
     *
     * @param date 日期
     * @return 年份
     */
    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }

    /**
     * 获取日期的月份。失败返回0。
     *
     * @param date 日期
     * @return 月份
     */
    public static int getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }

    /**
     * 获取日期的天数。失败返回0。
     *
     * @param date 日期
     * @return 天
     */
    public static int getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }


    /**
     * 获取日期中的某数值。如获取月份
     *
     * @param date     日期
     * @param dateType 日期格式
     * @return 数值
     */
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(dateType);
    }

}
