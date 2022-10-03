package com.me.devicemanagement.framework.start.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.text.DateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateTime
{
    private static final Logger LOGGER;
    
    public static String dateString() {
        DateTime.LOGGER.log(Level.INFO, "date string withn no argument method is called... : ");
        final long dtl = System.currentTimeMillis();
        final String theDate = longdateToString(dtl, "yyyy-MM-dd");
        return theDate;
    }
    
    public static String dateString(final long i) {
        DateTime.LOGGER.log(Level.INFO, "date string with one argument method is called... : ");
        long dtl = System.currentTimeMillis();
        dtl += i * 86400000L;
        final String theDate = longdateToString(dtl, "yyyy-MM-dd");
        return theDate;
    }
    
    public static String timeString() {
        DateTime.LOGGER.log(Level.INFO, "time string method is called... : ");
        final Calendar calendar = new GregorianCalendar();
        final String theTime = calendar.get(10) + ":" + calendar.get(12) + ":" + calendar.get(13) + "";
        return theTime;
    }
    
    public static String timeStringWithMillies() {
        DateTime.LOGGER.log(Level.INFO, "time string with mili method is called... : ");
        final Calendar calendar = new GregorianCalendar();
        final String theTime = calendar.get(10) + ":" + calendar.get(12) + ":" + calendar.get(13) + " " + calendar.get(14);
        return theTime;
    }
    
    public static long dateInLong() {
        return System.currentTimeMillis();
    }
    
    public static String dateString(final String formatStr) {
        return longdateToString(System.currentTimeMillis(), formatStr);
    }
    
    public static long dateInLong(final String st) {
        DateTime.LOGGER.log(Level.INFO, "date in long with one argument method is called... : ");
        long dateInLong = 0L;
        try {
            dateInLong = System.currentTimeMillis();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateInLong;
    }
    
    public static long dateInLong(final String st, final String format) {
        DateTime.LOGGER.log(Level.INFO, "date in long method is called... : ");
        long dateInLong = 0L;
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            final Date mailDate = formatter.parse(st);
            dateInLong = mailDate.getTime();
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateInLong;
    }
    
    public static String longdateToString(final long ld) {
        DateTime.LOGGER.log(Level.INFO, "longdateToString method is called with one id... : ");
        final long ldate = ld;
        String dateFormat = null;
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy hh:mm:ss  zzz");
            final Date theCreatedDate = new Date(ldate);
            dateFormat = formatter.format(theCreatedDate);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }
    
    public static String longdateToString(final long ld, final String fmt) {
        final long ldate = ld;
        String dateFormat = null;
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(fmt);
            final Date theCreatedDate = new Date(ldate);
            dateFormat = formatter.format(theCreatedDate);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateFormat;
    }
    
    public static long getDifferenceInDate(final String reminderDate) {
        DateTime.LOGGER.log(Level.INFO, "get difference in date method is called... : ");
        long difference = 0L;
        try {
            final long today = dateInLong();
            final long reminder = dateInLong(reminderDate, "yyyy-MM-dd");
            difference = reminder - today;
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return difference;
    }
    
    public static String getCurrentDate_Time(String format, String timeZone) {
        DateTime.LOGGER.log(Level.INFO, "get current date and time method is called... : ");
        if (format == null || format.equals("")) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        final SimpleDateFormat f1 = new SimpleDateFormat(format);
        if (timeZone == null || timeZone.equals("")) {
            timeZone = "GMT";
        }
        final TimeZone tz = TimeZone.getTimeZone(timeZone);
        final Calendar cal = Calendar.getInstance(tz);
        f1.setTimeZone(tz);
        return f1.format(cal.getTime());
    }
    
    public static String getDate_Time(final String dateInGMT, final String format) {
        DateTime.LOGGER.log(Level.INFO, "get date and timee method is called... : ");
        final SimpleDateFormat f1 = new SimpleDateFormat(format);
        final TimeZone tarTz = TimeZone.getTimeZone("GMT");
        final Calendar cal = Calendar.getInstance(tarTz);
        f1.setTimeZone(tarTz);
        Date dte = null;
        try {
            dte = f1.parse(dateInGMT);
        }
        catch (final Exception exx) {
            DateTime.LOGGER.log(Level.INFO, "Error in Parsing...");
            exx.printStackTrace();
            return null;
        }
        final SimpleDateFormat sdf = (SimpleDateFormat)DateFormat.getDateTimeInstance();
        sdf.applyPattern(format);
        return sdf.format(dte);
    }
    
    public static String getDate_TimeInGMT(final String dateStr, String format) {
        DateTime.LOGGER.log(Level.INFO, "get date and time in GMT  method is called... : ");
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        final SimpleDateFormat f1 = new SimpleDateFormat(format);
        final TimeZone tarTz = TimeZone.getTimeZone("GMT");
        final Calendar cal = Calendar.getInstance(tarTz);
        f1.setTimeZone(tarTz);
        Date dte = null;
        try {
            dte = f1.parse(dateStr);
        }
        catch (final Exception exx) {
            DateTime.LOGGER.log(Level.INFO, "Error in Parsing...");
            exx.printStackTrace();
            return null;
        }
        cal.setTime(dte);
        return f1.format(cal.getTime());
    }
    
    public static String increment_Decrement_Dates(final String dateString, final long no_of_days) {
        DateTime.LOGGER.log(Level.INFO, "increment and decrement date method is called... : ");
        final SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dte = null;
        try {
            dte = f1.parse(dateString);
        }
        catch (final Exception exx) {
            DateTime.LOGGER.log(Level.INFO, "Error in Parsing...");
            exx.printStackTrace();
            return null;
        }
        long timeInMills = dte.getTime();
        timeInMills += 86400000L * no_of_days;
        final Date dxt = new Date(timeInMills);
        return f1.format(dxt);
    }
    
    public static String increment_Decrement_Dates(final String dateString, final long no_of_days, final String format) {
        DateTime.LOGGER.log(Level.INFO, "increment method is called... : ");
        final SimpleDateFormat f1 = new SimpleDateFormat(format);
        Date dte = null;
        try {
            dte = f1.parse(dateString);
        }
        catch (final Exception exx) {
            DateTime.LOGGER.log(Level.INFO, "Error in Parsing...");
            exx.printStackTrace();
            return null;
        }
        long timeInMills = dte.getTime();
        timeInMills += 86400000L * no_of_days;
        final Date dxt = new Date(timeInMills);
        return f1.format(dxt);
    }
    
    public Date getDateFromString(final String dateStr, final String formatStr) {
        DateTime.LOGGER.log(Level.INFO, "get date from string method is called... : ");
        Date dt = null;
        try {
            final SimpleDateFormat simpledateformat = new SimpleDateFormat(formatStr);
            dt = simpledateformat.parse(dateStr);
        }
        catch (final Exception exception) {
            exception.printStackTrace();
        }
        return dt;
    }
    
    public String getTimeOffset(final String timeZone) {
        String timeStr = null;
        final String val = getCurrentDate_Time("z", timeZone);
        int signIndex = val.indexOf("-");
        if (signIndex == -1) {
            signIndex = val.indexOf("+");
        }
        if (signIndex != -1) {
            timeStr = val.substring(signIndex);
            final int colonIndex = timeStr.indexOf(":");
            if (colonIndex != -1) {
                timeStr = timeStr.substring(0, colonIndex) + timeStr.substring(colonIndex + 1);
            }
        }
        return timeStr;
    }
    
    public String getCurrentDate_TimeForMail(final String format, final String timeZone) {
        DateTime.LOGGER.log(Level.INFO, "get currnet date time for mail method is called... : ");
        String date = getCurrentDate_Time(format, timeZone);
        final String time = this.getTimeOffset(timeZone);
        if (time != null) {
            date = date + " " + time;
        }
        else {
            date = getCurrentDate_Time(format + " zzz", timeZone);
        }
        return date;
    }
    
    public String getDateString(final Properties p) {
        DateTime.LOGGER.log(Level.INFO, "get date properties method is called... : ");
        final String date = ((Hashtable<K, String>)p).get("time");
        String str = null;
        if (date != null) {
            final String fmt = p.getProperty("format");
            if (fmt == null) {
                str = date.substring(0, date.indexOf(32));
            }
            else {
                str = longdateToString(Long.parseLong(date), fmt);
            }
        }
        return str;
    }
    
    public static Hashtable determine_From_To_Times(final String period) throws Exception {
        DateTime.LOGGER.log(Level.INFO, "determine from to times method is called... : ");
        if (period == null) {
            return null;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final Hashtable htab = new Hashtable();
        long date1_inLong = 0L;
        long date2_inLong = 0L;
        if (period.equals("ever_opened")) {
            date1_inLong = 0L;
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("today")) {
            final Calendar cal = new GregorianCalendar();
            final Calendar cal2 = new GregorianCalendar();
            cal2.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            date1_inLong = cal2.getTime().getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("yesterday")) {
            final Calendar cal = new GregorianCalendar();
            final Calendar cal2 = new GregorianCalendar();
            cal2.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            date1_inLong = cal2.getTime().getTime();
            date1_inLong -= 86400000L;
            date2_inLong = cal2.getTime().getTime();
        }
        else if (period.equals("last_24_hrs")) {
            DateTime.LOGGER.log(Level.INFO, "last 24 hrs");
        }
        else if (period.equals("this_week")) {
            final Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            DateTime.LOGGER.log(Level.INFO, "This Day " + thisDay);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay;
            DateTime.LOGGER.log(Level.INFO, "diff Day " + diffDay);
            cal.roll(7, diffDay);
            DateTime.LOGGER.log(Level.INFO, "Cal Date is " + cal.getTime());
            date1_inLong = formatter2.parse(formatter.format(cal.getTime()) + " 00:00:00").getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("last_week")) {
            DateTime.LOGGER.log(Level.INFO, "this is last weeek");
            final Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            DateTime.LOGGER.log(Level.INFO, "This Day " + thisDay);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay;
            DateTime.LOGGER.log(Level.INFO, "diff Day " + diffDay);
            cal.roll(7, diffDay);
            DateTime.LOGGER.log(Level.INFO, "Cal Date is (bef) : " + cal.getTime());
            cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "Cal Date is (after) : " + cal.getTime());
            date2_inLong = cal.getTime().getTime();
            --date2_inLong;
            DateTime.LOGGER.log(Level.INFO, "verify date2 : " + new Date(date2_inLong));
            final long diffd = 7L;
            final long diffh = 24L;
            final long diffm = 60L;
            final long diffs = 60L;
            final long milli = 1000L;
            final long diffTime = diffd * diffh * diffm * diffs * milli;
            date1_inLong = date2_inLong - diffTime;
            DateTime.LOGGER.log(Level.INFO, "verify date1 : " + new Date(date1_inLong));
        }
        else if (period.equals("last_7_days")) {
            DateTime.LOGGER.log(Level.INFO, "last 7 days");
        }
        else if (period.equals("this_month")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTime.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTime.LOGGER.log(Level.INFO, "year : " + year);
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month, 1, 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("current_month")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTime.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTime.LOGGER.log(Level.INFO, "year : " + year);
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month, 1, 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            final Calendar cal_lastDay = Calendar.getInstance();
            final int lastDay = getLastDayOfMonth(year, month);
            cal_lastDay.set(year, month, lastDay, 23, 59, 59);
            DateTime.LOGGER.log(Level.INFO, "last day : " + cal_lastDay.getTime());
            date2_inLong = cal_lastDay.getTime().getTime();
        }
        else if (period.equals("last_month")) {
            final Calendar cal = Calendar.getInstance();
            int month = cal.get(2);
            DateTime.LOGGER.log(Level.INFO, "month : " + month);
            int year = cal.get(1);
            DateTime.LOGGER.log(Level.INFO, "year : " + year);
            if (month <= 0) {
                month = 12;
                --year;
            }
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month - 1, 1, 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            final Calendar cal_lastDay = Calendar.getInstance();
            final int lastDay = getLastDayOfMonth(year, month - 1);
            cal_lastDay.set(year, month - 1, lastDay, 23, 59, 59);
            DateTime.LOGGER.log(Level.INFO, "last day : " + cal_lastDay.getTime());
            date2_inLong = cal_lastDay.getTime().getTime();
        }
        else if (period.equals("current_quarter")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTime.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTime.LOGGER.log(Level.INFO, "year : " + year);
            int startMonth = 0;
            final int quot = month / 3;
            DateTime.LOGGER.log(Level.INFO, " quot " + quot);
            switch (quot) {
                case 0: {
                    startMonth = 0;
                    break;
                }
                case 1: {
                    startMonth = 3;
                    break;
                }
                case 2: {
                    startMonth = 6;
                    break;
                }
                case 3: {
                    startMonth = 9;
                    break;
                }
            }
            DateTime.LOGGER.log(Level.INFO, " startMonth " + startMonth);
            final Calendar cal_firstDay2 = Calendar.getInstance();
            cal_firstDay2.set(year, startMonth, 1, 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay2.getTime());
            date1_inLong = cal_firstDay2.getTime().getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("last_quarter")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTime.LOGGER.log(Level.INFO, "month : " + month);
            int year = cal.get(1);
            DateTime.LOGGER.log(Level.INFO, "year : " + year);
            int startMonth = 0;
            int endMonth = 2;
            final int quot2 = month / 3;
            DateTime.LOGGER.log(Level.INFO, " quot " + quot2);
            switch (quot2) {
                case 0: {
                    startMonth = 9;
                    endMonth = 11;
                    --year;
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
            DateTime.LOGGER.log(Level.INFO, " startMonth " + startMonth);
            DateTime.LOGGER.log(Level.INFO, " endMonth " + endMonth);
            final Calendar cal_firstDay3 = Calendar.getInstance();
            cal_firstDay3.set(year, startMonth, 1, 0, 0, 0);
            DateTime.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay3.getTime());
            date1_inLong = cal_firstDay3.getTime().getTime();
            final Calendar cal_lastDay2 = Calendar.getInstance();
            final int lastDay2 = getLastDayOfMonth(year, endMonth);
            cal_lastDay2.set(year, endMonth, lastDay2, 23, 59, 59);
            DateTime.LOGGER.log(Level.INFO, "last day : " + cal_lastDay2.getTime());
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equals("last_30_days")) {
            DateTime.LOGGER.log(Level.INFO, "last 30 days");
        }
        DateTime.LOGGER.log(Level.INFO, "date1_inLong show : " + longdateToString(date1_inLong, "yyyy-MM-dd HH:mm"));
        DateTime.LOGGER.log(Level.INFO, "date2_inLong show : " + longdateToString(date2_inLong, "yyyy-MM-dd HH:mm"));
        htab.put("date1", new Long(date1_inLong));
        htab.put("date2", new Long(date2_inLong));
        return htab;
    }
    
    private static int getLastDayOfMonth(final int year, final int month) {
        DateTime.LOGGER.log(Level.INFO, "get last day of month method is called... : ");
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
    
    public static String getDiffToDateFormat(final String timeInMin) {
        DateTime.LOGGER.log(Level.INFO, "get diff to date format method is called... : ");
        int days = 0;
        int hrs = 0;
        int mins = Integer.parseInt(timeInMin);
        String formattedStr = "";
        while (mins >= 60) {
            ++hrs;
            mins -= 60;
        }
        while (hrs >= 24) {
            ++days;
            hrs -= 24;
        }
        if (days > 0) {
            formattedStr = days + " days";
        }
        if (hrs > 0) {
            formattedStr = formattedStr + " " + hrs + " hrs";
        }
        if (mins > 0) {
            formattedStr = formattedStr + " " + mins + " mins";
        }
        return formattedStr;
    }
    
    public static HashMap getDateRangeFromNoOfDaysPast(final int noOfDays, final boolean pastDays) {
        DateTime.LOGGER.log(Level.INFO, "get date range from noof days past method is called... : ");
        final HashMap<String, Long> dateRange = new HashMap<String, Long>();
        Long toDate = 0L;
        Long fromDate = 0L;
        if (pastDays) {
            toDate = System.currentTimeMillis();
            fromDate = toDate - 86400000 * noOfDays;
        }
        else {
            fromDate = System.currentTimeMillis();
            toDate = fromDate + 86400000 * noOfDays;
        }
        dateRange.put("fromDate", fromDate);
        dateRange.put("toDate", toDate);
        return dateRange;
    }
    
    static {
        LOGGER = Logger.getLogger(DateTime.class.getName());
    }
}
