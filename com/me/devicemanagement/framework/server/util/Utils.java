package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Locale;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Utils
{
    private static Logger logger;
    
    public static String getConvertedTime(final String timeStr) {
        String[] time = new String[4];
        time = timeStr.split(":");
        for (int i = 0; i < 2; ++i) {
            if (time[i].length() == 1) {
                time[i] = "0" + time[i];
            }
        }
        final int hr = new Integer(time[0]);
        final float mnt = new Integer(time[1]);
        final int sec = new Integer(time[2]);
        final float fmnt = mnt / 60.0f;
        final float ftime = hr + fmnt;
        return String.valueOf(ftime);
    }
    
    public static String getDayOfDate(final String date) {
        final int day = new Integer(date.substring(3, 5));
        return String.valueOf(day);
    }
    
    public static String getMonthOfDate(final String date) {
        final int month = new Integer(date.substring(0, 2)) - 1;
        return String.valueOf(month);
    }
    
    public static String getYearOfDate(final String date) {
        final int year = new Integer(date.substring(6));
        return String.valueOf(year);
    }
    
    public static ArrayList convertAsLongList(final String str, final String delim) {
        final StringTokenizer stt = new StringTokenizer(str, delim);
        final ArrayList list = new ArrayList();
        while (stt.hasMoreTokens()) {
            final String temp = stt.nextToken();
            list.add(new Long(temp));
        }
        return list;
    }
    
    public static String getTime(final Long time) {
        String time2 = "--";
        try {
            final String dateFormat = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeFormat();
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            time2 = getTime(time, dateFormat, locale, timeZone);
        }
        catch (final Exception e) {
            Utils.logger.log(Level.SEVERE, "Exception while getting time...", e);
        }
        return time2;
    }
    
    public static String getTime(final Long time, final String dateFormat) {
        String formattedDate = null;
        final Date date = new Date(time);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        formattedDate = simpleDateFormat.format(date);
        return formattedDate;
    }
    
    public static String getTime(final Long time, final String dateFormat, final Locale locale, final TimeZone timeZone) {
        String formattedDate = "--";
        if (time > 0L && time != null) {
            try {
                final Date date = new Date(time);
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
                simpleDateFormat.setTimeZone(timeZone);
                formattedDate = simpleDateFormat.format(date);
            }
            catch (final Exception e) {
                Utils.logger.log(Level.SEVERE, "Exception while getting time...", e);
            }
        }
        return formattedDate;
    }
    
    public static String getEventTime(final Long time) {
        Utils.logger.log(Level.FINE, "getEventTime method is called...");
        final String formattedDate = getTime(time);
        Utils.logger.log(Level.FINE, "formattedDate val: " + formattedDate);
        return formattedDate;
    }
    
    public static String getEventDate(final Long time) {
        Utils.logger.log(Level.FINE, "getEventDate method is called..." + time);
        final String formattedDate = getDate(time);
        Utils.logger.log(Level.FINE, "formattedDate val: " + formattedDate);
        return formattedDate;
    }
    
    public static Long getTime(final String dateStr) {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            if (dateStr != null) {
                final long datetime = new Long(format.parse(dateStr).getTime());
                return datetime;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Hashtable getStartAndEndDates(final String period) throws Exception {
        return getStartAndEndDates(period, false);
    }
    
    public static Hashtable getStartAndEndDates(final String period, final Boolean withTimeZone) throws Exception {
        if (period == null) {
            return null;
        }
        Calendar gregorianCalendar = new GregorianCalendar();
        Calendar calendar = Calendar.getInstance();
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (withTimeZone) {
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            gregorianCalendar = new GregorianCalendar(timeZone);
            calendar = Calendar.getInstance(timeZone);
            formatter.setTimeZone(timeZone);
            formatter2.setTimeZone(timeZone);
        }
        final Hashtable htab = new Hashtable();
        long date1_inLong = 0L;
        long date2_inLong = 0L;
        if (period.equalsIgnoreCase("today")) {
            final Calendar cal = gregorianCalendar;
            date2_inLong = System.currentTimeMillis();
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            date1_inLong = cal.getTimeInMillis();
        }
        else if (period.equalsIgnoreCase("yesterday")) {
            final Calendar cal = gregorianCalendar;
            cal.add(5, -1);
            cal.set(11, 0);
            cal.set(12, 0);
            cal.set(13, 0);
            cal.set(14, 0);
            date1_inLong = cal.getTimeInMillis();
            cal.set(11, 23);
            cal.set(12, 59);
            cal.set(13, 59);
            date2_inLong = cal.getTimeInMillis();
        }
        else if (period.equalsIgnoreCase("this_week") || period.equalsIgnoreCase("this week")) {
            final Calendar cal = gregorianCalendar;
            date2_inLong = System.currentTimeMillis();
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay;
            cal.add(7, diffDay);
            date1_inLong = formatter2.parse(formatter.format(cal.getTime()) + " 00:00:00").getTime();
        }
        else if (period.equalsIgnoreCase("last_week") || period.equalsIgnoreCase("last week")) {
            final Calendar cal = gregorianCalendar;
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay - 1;
            cal.add(7, diffDay);
            cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            date2_inLong = cal.getTime().getTime();
            --date2_inLong;
            final long diffd = 6L;
            final long diffh = 24L;
            final long diffm = 60L;
            final long diffs = 60L;
            final long milli = 1000L;
            final long diffTime = diffd * diffh * diffm * diffs * milli;
            date1_inLong = date2_inLong - diffTime;
        }
        else if (period.equalsIgnoreCase("this_month") || period.equalsIgnoreCase("this month")) {
            final Calendar cal = calendar;
            final Calendar cal_firstDay = calendar;
            date2_inLong = System.currentTimeMillis();
            final int month = cal.get(2);
            final int year = cal.get(1);
            cal_firstDay.set(year, month, 1);
            date1_inLong = cal_firstDay.getTime().getTime();
        }
        else if (period.equalsIgnoreCase("current_month") || period.equalsIgnoreCase("current month")) {
            final Calendar cal = calendar;
            final int month2 = cal.get(2);
            final int year2 = cal.get(1);
            final Calendar cal_firstDay2 = calendar;
            cal_firstDay2.set(year2, month2, 1, 0, 0, 0);
            date1_inLong = cal_firstDay2.getTime().getTime();
            final Calendar cal_lastDay = calendar;
            final int lastDay = getLastDayOfMonth(year2, month2);
            cal_lastDay.set(year2, month2, lastDay, 23, 59, 59);
            date2_inLong = cal_lastDay.getTime().getTime();
        }
        else if (period.equalsIgnoreCase("last_month") || period.equalsIgnoreCase("last month")) {
            final Calendar cal = calendar;
            final Calendar cal_firstDay = calendar;
            final Calendar cal_lastDay2 = calendar;
            int month3 = cal.get(2);
            int year3 = cal.get(1);
            if (month3 <= 0) {
                month3 = 12;
                --year3;
            }
            cal_firstDay.set(year3, month3 - 1, 1, 0, 0, 0);
            date1_inLong = cal_firstDay.getTime().getTime();
            final int lastDay = getLastDayOfMonth(year3, month3 - 1);
            cal_lastDay2.set(year3, month3 - 1, lastDay, 23, 59, 59);
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equalsIgnoreCase("current_quarter") || period.equalsIgnoreCase("current quarter")) {
            final Calendar cal = calendar;
            final Calendar cal_firstDay = calendar;
            final Calendar cal_lastDay2 = calendar;
            final int month3 = cal.get(2);
            final int year3 = cal.get(1);
            int startMonth = 0;
            int endMonth = 2;
            final int quot = month3 / 3;
            switch (quot) {
                case 0: {
                    startMonth = 0;
                    endMonth = 2;
                    break;
                }
                case 1: {
                    startMonth = 3;
                    endMonth = 5;
                    break;
                }
                case 2: {
                    startMonth = 6;
                    endMonth = 8;
                    break;
                }
                case 3: {
                    startMonth = 9;
                    endMonth = 11;
                    break;
                }
            }
            cal_firstDay.set(year3, startMonth, 1, 0, 0, 0);
            date1_inLong = cal_firstDay.getTime().getTime();
            final int lastDay2 = getLastDayOfMonth(year3, endMonth);
            cal_lastDay2.set(year3, endMonth, lastDay2, 23, 59, 59);
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equalsIgnoreCase("last_quarter") || period.equalsIgnoreCase("last quarter")) {
            final Calendar cal = calendar;
            final Calendar cal_firstDay = calendar;
            final Calendar cal_lastDay2 = calendar;
            final int month3 = cal.get(2);
            int year3 = cal.get(1);
            int startMonth = 0;
            int endMonth = 2;
            final int quot = month3 / 3;
            switch (quot) {
                case 0: {
                    startMonth = 9;
                    endMonth = 11;
                    --year3;
                    break;
                }
                case 1: {
                    startMonth = 0;
                    endMonth = 2;
                    break;
                }
                case 2: {
                    startMonth = 3;
                    endMonth = 5;
                    break;
                }
                case 3: {
                    startMonth = 6;
                    endMonth = 8;
                    break;
                }
            }
            cal_firstDay.set(year3, startMonth, 1, 0, 0, 0);
            date1_inLong = cal_firstDay.getTime().getTime();
            final int lastDay2 = getLastDayOfMonth(year3, endMonth);
            cal_lastDay2.set(year3, endMonth, lastDay2, 23, 59, 59);
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equalsIgnoreCase("last_30_days") || period.equalsIgnoreCase("last 30 days")) {
            Utils.logger.log(Level.FINEST, "empty if");
        }
        htab.put("startDate", new Long(date1_inLong));
        htab.put("endDate", new Long(date2_inLong));
        return htab;
    }
    
    private static int getLastDayOfMonth(final int year, final int month) {
        int days = 0;
        switch (month) {
            case 0: {
                days = 31;
                break;
            }
            case 1: {
                if (year % 4 == 0) {
                    days = 29;
                    break;
                }
                days = 28;
                break;
            }
            case 2: {
                days = 31;
                break;
            }
            case 3: {
                days = 30;
                break;
            }
            case 4: {
                days = 31;
                break;
            }
            case 5: {
                days = 30;
                break;
            }
            case 6: {
                days = 31;
                break;
            }
            case 7: {
                days = 31;
                break;
            }
            case 8: {
                days = 30;
                break;
            }
            case 9: {
                days = 31;
                break;
            }
            case 10: {
                days = 30;
                break;
            }
            case 11: {
                days = 31;
                break;
            }
        }
        return days;
    }
    
    public static int getHourOfTime(final String timeStr) {
        String[] time = new String[4];
        time = timeStr.split(":");
        if (time[0].length() == 1) {
            time[0] = "0" + time[0];
        }
        final int hour = new Integer(time[0]);
        return hour;
    }
    
    public static int getMinuteOfTime(final String timeStr) {
        String[] time = new String[4];
        time = timeStr.split(":");
        if (time[1].length() == 1) {
            time[1] = "0" + time[1];
        }
        final int minute = new Integer(time[1]);
        return minute;
    }
    
    public static int getSecondOfTime(final String timeStr) {
        String[] time = new String[4];
        time = timeStr.split(":");
        if (time[2].length() == 1) {
            time[2] = "0" + time[2];
        }
        final int seconds = new Integer(time[2]);
        return seconds;
    }
    
    public static String longdateToString(final long currentTimeInMilli, final String timeFormat) {
        String time = "";
        try {
            final TimeZone userTimeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            time = getTime(currentTimeInMilli, timeFormat, locale, userTimeZone);
        }
        catch (final Exception ex) {}
        return time;
    }
    
    public static String getEventTime(final Timestamp time) {
        if (time != null) {
            return getEventTime(time.getTime());
        }
        return "---";
    }
    
    public static String getDate(final Long time) {
        String date = "";
        try {
            final String dateFormat = DMUserHandler.getUserDateFormat();
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            date = getTime(time, dateFormat, locale, timeZone);
        }
        catch (final Exception ex) {}
        return date;
    }
    
    public static String getDate(final Long time, final TimeZone timezone) {
        String date = "";
        try {
            final String dateFormat = DMUserHandler.getUserDateFormat();
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            date = getTime(time, dateFormat, locale, timezone);
        }
        catch (final Exception e) {
            Utils.logger.log(Level.WARNING, "Exception Utils.getDate() : ", e);
        }
        return date;
    }
    
    public static String getDate(final Long time, final String dateFormat) {
        final Date date = new Date(time);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }
    
    public static String getServerTimeInString() {
        return getServerTimeInString(System.currentTimeMillis());
    }
    
    public static String getServerTimeInString(final Long time) {
        String serverTime = "";
        try {
            final String dateFormat = "dd MMM yyyy HH:mm:ss";
            final Locale locale = ApiFactoryProvider.getAuthUtilAccessAPI().getUserLocale();
            serverTime = getServerTimeInString(time, dateFormat, locale);
        }
        catch (final Exception e) {
            Utils.logger.log(Level.SEVERE, "Exception while getting server time....", e);
        }
        return serverTime;
    }
    
    public static String getServerTimeInString(final Long time1, final String dateFormat, final Locale userlocale) {
        String serverTimeValue = "";
        try {
            final Date date = new Date(time1);
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, userlocale);
            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            serverTimeValue = simpleDateFormat.format(date);
        }
        catch (final Exception e) {
            Utils.logger.log(Level.SEVERE, "Exception while getting server time in string...", e);
        }
        return serverTimeValue;
    }
    
    public static String getDateInUserFormat(final String date, final String inputFormat) {
        final String userDateFormat = DMUserHandler.getUserDateFormat();
        return getDateInUserFormat(date, inputFormat, userDateFormat);
    }
    
    public static String getDateInUserFormat(final String date, final String inputFormat, final String outputFormat) {
        String value = "";
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
            final Date d = sdf.parse(date);
            final SimpleDateFormat usdf = new SimpleDateFormat(outputFormat);
            final Date d2 = new Date(d.getTime());
            value = usdf.format(d2);
        }
        catch (final Exception e) {
            Utils.logger.log(Level.SEVERE, "Exception while getting date in string...", e);
        }
        return value;
    }
    
    static {
        Utils.logger = Logger.getLogger(Utils.class.getName());
    }
}
