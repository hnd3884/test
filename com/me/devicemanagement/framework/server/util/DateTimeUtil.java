package com.me.devicemanagement.framework.server.util;

import org.apache.commons.lang.StringUtils;
import java.util.StringTokenizer;
import javax.transaction.NotSupportedException;
import java.util.TimeZone;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateTimeUtil
{
    private static final Logger LOGGER;
    
    public static String dateString() {
        DateTimeUtil.LOGGER.log(Level.INFO, "date string withn no argument method is called... : ");
        final long dtl = System.currentTimeMillis();
        final String theDate = longdateToString(dtl, "yyyy-MM-dd");
        return theDate;
    }
    
    public static String dateString(final long i) {
        DateTimeUtil.LOGGER.log(Level.INFO, "date string with one argument method is called... : ");
        long dtl = System.currentTimeMillis();
        dtl += i * 86400000L;
        final String theDate = longdateToString(dtl, "yyyy-MM-dd");
        return theDate;
    }
    
    public static Hashtable determine_From_To_Times(final String period) throws Exception {
        DateTimeUtil.LOGGER.log(Level.INFO, "determine from to times method is called... : ");
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
            DateTimeUtil.LOGGER.log(Level.INFO, "last 24 hours");
        }
        else if (period.equals("this_week")) {
            final Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            DateTimeUtil.LOGGER.log(Level.INFO, "This Day " + thisDay);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay;
            DateTimeUtil.LOGGER.log(Level.INFO, "diff Day " + diffDay);
            cal.roll(7, diffDay);
            DateTimeUtil.LOGGER.log(Level.INFO, "Cal Date is " + cal.getTime());
            date1_inLong = formatter2.parse(formatter.format(cal.getTime()) + " 00:00:00").getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("last_week")) {
            DateTimeUtil.LOGGER.log(Level.INFO, "this is last weeek");
            final Calendar cal = Calendar.getInstance();
            cal.setFirstDayOfWeek(2);
            final int thisDay = cal.get(7);
            DateTimeUtil.LOGGER.log(Level.INFO, "This Day " + thisDay);
            final int diffDay = cal.getFirstDayOfWeek() - thisDay;
            DateTimeUtil.LOGGER.log(Level.INFO, "diff Day " + diffDay);
            cal.roll(7, diffDay);
            DateTimeUtil.LOGGER.log(Level.INFO, "Cal Date is (bef) : " + cal.getTime());
            cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "Cal Date is (after) : " + cal.getTime());
            date2_inLong = cal.getTime().getTime();
            --date2_inLong;
            DateTimeUtil.LOGGER.log(Level.INFO, "verify date2 : " + new Date(date2_inLong));
            final long diffd = 7L;
            final long diffh = 24L;
            final long diffm = 60L;
            final long diffs = 60L;
            final long milli = 1000L;
            final long diffTime = diffd * diffh * diffm * diffs * milli;
            date1_inLong = date2_inLong - diffTime;
            DateTimeUtil.LOGGER.log(Level.INFO, "verify date1 : " + new Date(date1_inLong));
        }
        else if (period.equals("last_7_days")) {
            DateTimeUtil.LOGGER.log(Level.INFO, "last 7 days");
        }
        else if (period.equals("this_month")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTimeUtil.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTimeUtil.LOGGER.log(Level.INFO, "year : " + year);
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month, 1, 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            date2_inLong = System.currentTimeMillis();
        }
        else if (period.equals("current_month")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTimeUtil.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTimeUtil.LOGGER.log(Level.INFO, "year : " + year);
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month, 1, 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            final Calendar cal_lastDay = Calendar.getInstance();
            final int lastDay = getLastDayOfMonth(year, month);
            cal_lastDay.set(year, month, lastDay, 23, 59, 59);
            DateTimeUtil.LOGGER.log(Level.INFO, "last day : " + cal_lastDay.getTime());
            date2_inLong = cal_lastDay.getTime().getTime();
        }
        else if (period.equals("last_month")) {
            final Calendar cal = Calendar.getInstance();
            int month = cal.get(2);
            DateTimeUtil.LOGGER.log(Level.INFO, "month : " + month);
            int year = cal.get(1);
            DateTimeUtil.LOGGER.log(Level.INFO, "year : " + year);
            if (month <= 0) {
                month = 12;
                --year;
            }
            final Calendar cal_firstDay = Calendar.getInstance();
            cal_firstDay.set(year, month - 1, 1, 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay.getTime());
            date1_inLong = cal_firstDay.getTime().getTime();
            final Calendar cal_lastDay = Calendar.getInstance();
            final int lastDay = getLastDayOfMonth(year, month - 1);
            cal_lastDay.set(year, month - 1, lastDay, 23, 59, 59);
            DateTimeUtil.LOGGER.log(Level.INFO, "last day : " + cal_lastDay.getTime());
            date2_inLong = cal_lastDay.getTime().getTime();
        }
        else if (period.equals("current_quarter")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTimeUtil.LOGGER.log(Level.INFO, "month : " + month);
            final int year = cal.get(1);
            DateTimeUtil.LOGGER.log(Level.INFO, "year : " + year);
            int startMonth = 0;
            int endMonth = 2;
            final int quot = month / 3;
            DateTimeUtil.LOGGER.log(Level.INFO, " quot " + quot);
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
            DateTimeUtil.LOGGER.log(Level.INFO, " startMonth " + startMonth);
            DateTimeUtil.LOGGER.log(Level.INFO, " endMonth " + endMonth);
            final Calendar cal_firstDay2 = Calendar.getInstance();
            cal_firstDay2.set(year, startMonth, 1, 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay2.getTime());
            date1_inLong = cal_firstDay2.getTime().getTime();
            final Calendar cal_lastDay2 = Calendar.getInstance();
            final int lastDay2 = getLastDayOfMonth(year, endMonth);
            cal_lastDay2.set(year, endMonth, lastDay2, 23, 59, 59);
            DateTimeUtil.LOGGER.log(Level.INFO, "last day : " + cal_lastDay2.getTime());
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equals("last_quarter")) {
            final Calendar cal = Calendar.getInstance();
            final int month = cal.get(2);
            DateTimeUtil.LOGGER.log(Level.INFO, "month : " + month);
            int year = cal.get(1);
            DateTimeUtil.LOGGER.log(Level.INFO, "year : " + year);
            int startMonth = 0;
            int endMonth = 2;
            final int quot = month / 3;
            DateTimeUtil.LOGGER.log(Level.INFO, " quot " + quot);
            switch (quot) {
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
            DateTimeUtil.LOGGER.log(Level.INFO, " startMonth " + startMonth);
            DateTimeUtil.LOGGER.log(Level.INFO, " endMonth " + endMonth);
            final Calendar cal_firstDay2 = Calendar.getInstance();
            cal_firstDay2.set(year, startMonth, 1, 0, 0, 0);
            DateTimeUtil.LOGGER.log(Level.INFO, "1st day : " + cal_firstDay2.getTime());
            date1_inLong = cal_firstDay2.getTime().getTime();
            final Calendar cal_lastDay2 = Calendar.getInstance();
            final int lastDay2 = getLastDayOfMonth(year, endMonth);
            cal_lastDay2.set(year, endMonth, lastDay2, 23, 59, 59);
            DateTimeUtil.LOGGER.log(Level.INFO, "last day : " + cal_lastDay2.getTime());
            date2_inLong = cal_lastDay2.getTime().getTime();
        }
        else if (period.equals("last_30_days")) {
            DateTimeUtil.LOGGER.log(Level.INFO, "last 30 days");
        }
        DateTimeUtil.LOGGER.log(Level.INFO, "date1_inLong show : " + longdateToString(date1_inLong, "yyyy-MM-dd HH:mm"));
        DateTimeUtil.LOGGER.log(Level.INFO, "date2_inLong show : " + longdateToString(date2_inLong, "yyyy-MM-dd HH:mm"));
        htab.put("date1", new Long(date1_inLong));
        htab.put("date2", new Long(date2_inLong));
        return htab;
    }
    
    public static String longdateToString(final long ld) {
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
    
    private static int getLastDayOfMonth(final int year, final int month) {
        DateTimeUtil.LOGGER.log(Level.INFO, "get last day of month method is called... : ");
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
    
    public static HashMap getDateRangeFromNoOfDaysPast(final int noOfDays, final boolean pastDays) {
        DateTimeUtil.LOGGER.log(Level.INFO, "get date range from noof days past method is called... : ");
        final HashMap<String, Long> dateRange = new HashMap<String, Long>();
        Long toDate = 0L;
        Long fromDate = 0L;
        final Long noOfDaysLong = (Long)noOfDays;
        if (pastDays) {
            toDate = System.currentTimeMillis();
            fromDate = toDate - 86400000L * noOfDaysLong;
        }
        else {
            fromDate = System.currentTimeMillis();
            toDate = fromDate + 86400000L * noOfDaysLong;
        }
        dateRange.put("fromDate", fromDate);
        dateRange.put("toDate", toDate);
        return dateRange;
    }
    
    public static Date getDateFromString(final String dateStr, final String formatStr) {
        DateTimeUtil.LOGGER.log(Level.INFO, "get date from string method is called... : ");
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
    
    public static String increment_Decrement_Dates(final String dateString, final long no_of_days, final String format) {
        DateTimeUtil.LOGGER.log(Level.INFO, "increment method is called... : ");
        final SimpleDateFormat f1 = new SimpleDateFormat(format);
        Date dte = null;
        try {
            dte = f1.parse(dateString);
        }
        catch (final Exception exx) {
            DateTimeUtil.LOGGER.log(Level.INFO, "Error in Parsing...");
            exx.printStackTrace();
            return null;
        }
        long timeInMills = dte.getTime();
        timeInMills += 86400000L * no_of_days;
        final Date dxt = new Date(timeInMills);
        return f1.format(dxt);
    }
    
    public static long dateInLong() {
        return System.currentTimeMillis();
    }
    
    public static String dateString(final String formatStr) {
        return longdateToString(System.currentTimeMillis(), formatStr);
    }
    
    public static long dateInLong(final String st) {
        DateTimeUtil.LOGGER.log(Level.INFO, "date in long with one argument method is called... : ");
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
        DateTimeUtil.LOGGER.log(Level.FINE, "date in long method is called... : ");
        long dateInLong = 0L;
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            if (st != null) {
                final Date mailDate = formatter.parse(st);
                dateInLong = mailDate.getTime();
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        return dateInLong;
    }
    
    public static HashMap<String, Long> getDateRangeInTimeStampBetweenNoOfDays(final int startInterval, final int endInterval) {
        final HashMap<String, Long> dateRange = new HashMap<String, Long>();
        Long toDate = 0L;
        Long fromDate = 0L;
        toDate = System.currentTimeMillis();
        final Calendar calObj = Calendar.getInstance();
        calObj.add(5, -startInterval);
        fromDate = calObj.getTimeInMillis();
        calObj.setTimeInMillis(fromDate);
        calObj.add(5, -endInterval);
        toDate = calObj.getTimeInMillis();
        dateRange.put("fromDate", fromDate);
        dateRange.put("toDate", toDate);
        return dateRange;
    }
    
    public static String getCurrentTimeZone() {
        final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
        final int rawOffset = timeZone.getRawOffset();
        final int hour = rawOffset / 3600000;
        final int min = Math.abs(rawOffset / 60000) % 60;
        String timeZoneStr = null;
        if (hour >= 0) {
            timeZoneStr = "( GMT+" + hour + ":" + min + " ) ";
        }
        else {
            timeZoneStr = "( GMT" + hour + ":" + min + " ) ";
        }
        return timeZoneStr;
    }
    
    public static long dateInLonginUserTimeZone(final String dateString, final String format) {
        DateTimeUtil.LOGGER.log(Level.INFO, "date in long method is called... : ");
        long dateInLong = -1L;
        if (dateString != null) {
            final SimpleDateFormat formatter = new SimpleDateFormat(format);
            final TimeZone timeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
            formatter.setTimeZone(timeZone);
            try {
                final Date date = formatter.parse(dateString);
                dateInLong = date.getTime();
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
        }
        return dateInLong;
    }
    
    public static String getCurrentTimeInUserTimeZone(final String format) {
        DateTimeUtil.LOGGER.log(Level.INFO, "date string withn no argument method is called... : ");
        final long currentTime = System.currentTimeMillis();
        final TimeZone userTimeZone = ApiFactoryProvider.getAuthUtilAccessAPI().getUserTimeZone();
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(userTimeZone);
        final Date currentDate = new Date(currentTime);
        final String dateFormat = formatter.format(currentDate);
        return dateFormat;
    }
    
    public static int compareTimeWithCurrentTime(final long timeToBeCompared) {
        final long currentTime = System.currentTimeMillis();
        if (currentTime > timeToBeCompared) {
            return -1;
        }
        if (currentTime == timeToBeCompared) {
            return 0;
        }
        return 1;
    }
    
    public static boolean isTimeExpired(final String time, final String format) throws NotSupportedException {
        Boolean isExpired = false;
        if (time != null) {
            final long timeIn_ms = dateInLong(time, format);
            if (compareTimeWithCurrentTime(timeIn_ms) == -1) {
                isExpired = true;
            }
            return isExpired;
        }
        throw new NotSupportedException();
    }
    
    public static String getMonthFromString(final String date, String format) throws NotSupportedException {
        if (format != null && date != null) {
            final Date dateObj = getDateFromString(date, format);
            format = format.split(" ")[0];
            final String[] formatArr = format.split("/");
            final String[] month = new String[3];
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            for (int i = 0; i < formatArr.length; ++i) {
                simpleDateFormat.applyPattern(formatArr[i]);
                month[i] = simpleDateFormat.format(dateObj);
            }
            return month[0] + "/" + month[1] + "/" + month[2];
        }
        throw new NotSupportedException("Cannot find date from null data");
    }
    
    public static String getTimeFromString(final String date, String format) throws NotSupportedException {
        if (date != null && format != null) {
            final Date dateObj = getDateFromString(date, format);
            format = format.split(" ")[1];
            final String[] formatArr = format.split(":");
            final String[] time = new String[3];
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            for (int i = 0; i < formatArr.length; ++i) {
                simpleDateFormat.applyPattern(formatArr[i]);
                time[i] = simpleDateFormat.format(dateObj);
            }
            return time[0] + ":" + time[1] + ":" + time[2];
        }
        throw new NotSupportedException("Cannot find date from null data");
    }
    
    private static Calendar getCalOfDate(final String expiryDate, final String stringTokenenizer) {
        final Calendar cal = Calendar.getInstance();
        final StringTokenizer stt = new StringTokenizer(expiryDate, stringTokenenizer);
        if (stt.countTokens() == 3) {
            final int yyyy = Integer.parseInt(stt.nextToken());
            final int mm = Integer.parseInt(stt.nextToken()) - 1;
            final int dd = Integer.parseInt(stt.nextToken());
            try {
                cal.set(yyyy, mm, dd);
            }
            catch (final Exception ex) {}
        }
        return cal;
    }
    
    public static long convertDateToLong(final String expiryDate, final String stringTokenizer) {
        final Calendar cal = getCalOfDate(expiryDate, stringTokenizer);
        final long expDate_long = cal.getTime().getTime();
        return expDate_long;
    }
    
    public static Long getUTCTimestampFromTextTime(final String time, final String dateFormat, final TimeZone timeZone) {
        if (StringUtils.isNotBlank(time)) {
            try {
                final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
                simpleDateFormat.setTimeZone(timeZone);
                return simpleDateFormat.parse(time).getTime();
            }
            catch (final Exception e) {
                DateTimeUtil.LOGGER.log(Level.SEVERE, "Exception while getting UTC timestamp from given string...", e);
            }
        }
        return null;
    }
    
    public static String getTextTimeFromUTCTimestamp(final Long time, final String dateFormat, final TimeZone timeZone) {
        final Date date = new Date(time);
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        simpleDateFormat.setTimeZone(timeZone);
        return simpleDateFormat.format(date);
    }
    
    static {
        LOGGER = Logger.getLogger(DateTimeUtil.class.getName());
    }
}
