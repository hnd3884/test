package com.sun.org.apache.xalan.internal.lib;

import java.util.TimeZone;
import java.util.Locale;
import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.sun.org.apache.xpath.internal.objects.XNumber;
import com.sun.org.apache.xpath.internal.objects.XObject;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExsltDatetime
{
    static final String dt = "yyyy-MM-dd'T'HH:mm:ss";
    static final String d = "yyyy-MM-dd";
    static final String gym = "yyyy-MM";
    static final String gy = "yyyy";
    static final String gmd = "--MM-dd";
    static final String gm = "--MM--";
    static final String gd = "---dd";
    static final String t = "HH:mm:ss";
    static final String EMPTY_STR = "";
    
    public static String dateTime() {
        final Calendar cal = Calendar.getInstance();
        final Date datetime = cal.getTime();
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final StringBuffer buff = new StringBuffer(dateFormat.format(datetime));
        final int offset = cal.get(15) + cal.get(16);
        if (offset == 0) {
            buff.append("Z");
        }
        else {
            final int hrs = offset / 3600000;
            final int min = offset % 3600000;
            final char posneg = (hrs < 0) ? '-' : '+';
            buff.append(posneg).append(formatDigits(hrs)).append(':').append(formatDigits(min));
        }
        return buff.toString();
    }
    
    private static String formatDigits(final int q) {
        final String dd = String.valueOf(Math.abs(q));
        return (dd.length() == 1) ? ('0' + dd) : dd;
    }
    
    public static String date(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String leader = edz[0];
        final String datetime = edz[1];
        final String zone = edz[2];
        if (datetime == null || zone == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        final String formatOut = "yyyy-MM-dd";
        final Date date = testFormats(datetime, formatsIn);
        if (date == null) {
            return "";
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(formatOut);
        dateFormat.setLenient(false);
        final String dateOut = dateFormat.format(date);
        if (dateOut.length() == 0) {
            return "";
        }
        return leader + dateOut + zone;
    }
    
    public static String date() {
        final String datetime = dateTime().toString();
        final String date = datetime.substring(0, datetime.indexOf("T"));
        final String zone = datetime.substring(getZoneStart(datetime));
        return date + zone;
    }
    
    public static String time(final String timeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(timeIn);
        final String time = edz[1];
        final String zone = edz[2];
        if (time == null || zone == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "HH:mm:ss" };
        final String formatOut = "HH:mm:ss";
        final Date date = testFormats(time, formatsIn);
        if (date == null) {
            return "";
        }
        final SimpleDateFormat dateFormat = new SimpleDateFormat(formatOut);
        final String out = dateFormat.format(date);
        return out + zone;
    }
    
    public static String time() {
        final String datetime = dateTime().toString();
        final String time = datetime.substring(datetime.indexOf("T") + 1);
        return time;
    }
    
    public static double year(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final boolean ad = edz[0].length() == 0;
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
        final double yr = getNumber(datetime, formats, 1);
        if (ad || yr == Double.NaN) {
            return yr;
        }
        return -yr;
    }
    
    public static double year() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(1);
    }
    
    public static double monthInYear(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--", "--MM-dd" };
        return getNumber(datetime, formats, 2) + 1.0;
    }
    
    public static double monthInYear() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(2) + 1;
    }
    
    public static double weekInYear(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        return getNumber(datetime, formats, 3);
    }
    
    public static double weekInYear() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(3);
    }
    
    public static double dayInYear(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        return getNumber(datetime, formats, 6);
    }
    
    public static double dayInYear() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(6);
    }
    
    public static double dayInMonth(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "--MM-dd", "---dd" };
        final double day = getNumber(datetime, formats, 5);
        return day;
    }
    
    public static double dayInMonth() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(5);
    }
    
    public static double dayOfWeekInMonth(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        return getNumber(datetime, formats, 8);
    }
    
    public static double dayOfWeekInMonth() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(8);
    }
    
    public static double dayInWeek(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        return getNumber(datetime, formats, 7);
    }
    
    public static double dayInWeek() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(7);
    }
    
    public static double hourInDay(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
        return getNumber(datetime, formats, 11);
    }
    
    public static double hourInDay() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(11);
    }
    
    public static double minuteInHour(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
        return getNumber(datetime, formats, 12);
    }
    
    public static double minuteInHour() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(12);
    }
    
    public static double secondInMinute(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return Double.NaN;
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss" };
        return getNumber(datetime, formats, 13);
    }
    
    public static double secondInMinute() {
        final Calendar cal = Calendar.getInstance();
        return cal.get(13);
    }
    
    public static XObject leapYear(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return new XNumber(Double.NaN);
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "yyyy" };
        final double dbl = getNumber(datetime, formats, 1);
        if (dbl == Double.NaN) {
            return new XNumber(Double.NaN);
        }
        final int yr = (int)dbl;
        return new XBoolean(yr % 400 == 0 || (yr % 100 != 0 && yr % 4 == 0));
    }
    
    public static boolean leapYear() {
        final Calendar cal = Calendar.getInstance();
        final int yr = cal.get(1);
        return yr % 400 == 0 || (yr % 100 != 0 && yr % 4 == 0);
    }
    
    public static String monthName(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
        final String formatOut = "MMMM";
        return getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }
    
    public static String monthName() {
        final Calendar cal = Calendar.getInstance();
        final String format = "MMMM";
        return getNameOrAbbrev(format);
    }
    
    public static String monthAbbreviation(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd", "yyyy-MM", "--MM--" };
        final String formatOut = "MMM";
        return getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }
    
    public static String monthAbbreviation() {
        final String format = "MMM";
        return getNameOrAbbrev(format);
    }
    
    public static String dayName(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        final String formatOut = "EEEE";
        return getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }
    
    public static String dayName() {
        final String format = "EEEE";
        return getNameOrAbbrev(format);
    }
    
    public static String dayAbbreviation(final String datetimeIn) throws ParseException {
        final String[] edz = getEraDatetimeZone(datetimeIn);
        final String datetime = edz[1];
        if (datetime == null) {
            return "";
        }
        final String[] formatsIn = { "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd" };
        final String formatOut = "EEE";
        return getNameOrAbbrev(datetimeIn, formatsIn, formatOut);
    }
    
    public static String dayAbbreviation() {
        final String format = "EEE";
        return getNameOrAbbrev(format);
    }
    
    private static String[] getEraDatetimeZone(final String in) {
        String leader = "";
        String datetime = in;
        String zone = "";
        if (in.charAt(0) == '-' && !in.startsWith("--")) {
            leader = "-";
            datetime = in.substring(1);
        }
        final int z = getZoneStart(datetime);
        if (z > 0) {
            zone = datetime.substring(z);
            datetime = datetime.substring(0, z);
        }
        else if (z == -2) {
            zone = null;
        }
        return new String[] { leader, datetime, zone };
    }
    
    private static int getZoneStart(final String datetime) {
        if (datetime.indexOf("Z") == datetime.length() - 1) {
            return datetime.length() - 1;
        }
        if (datetime.length() >= 6 && datetime.charAt(datetime.length() - 3) == ':') {
            if (datetime.charAt(datetime.length() - 6) != '+') {
                if (datetime.charAt(datetime.length() - 6) != '-') {
                    return -1;
                }
            }
            try {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                dateFormat.setLenient(false);
                final Date d = dateFormat.parse(datetime.substring(datetime.length() - 5));
                return datetime.length() - 6;
            }
            catch (final ParseException pe) {
                System.out.println("ParseException " + pe.getErrorOffset());
                return -2;
            }
        }
        return -1;
    }
    
    private static Date testFormats(final String in, final String[] formats) throws ParseException {
        int i = 0;
        while (i < formats.length) {
            try {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(formats[i]);
                dateFormat.setLenient(false);
                return dateFormat.parse(in);
            }
            catch (final ParseException ex) {
                ++i;
                continue;
            }
            break;
        }
        return null;
    }
    
    private static double getNumber(final String in, final String[] formats, final int calField) throws ParseException {
        final Calendar cal = Calendar.getInstance();
        cal.setLenient(false);
        final Date date = testFormats(in, formats);
        if (date == null) {
            return Double.NaN;
        }
        cal.setTime(date);
        return cal.get(calField);
    }
    
    private static String getNameOrAbbrev(final String in, final String[] formatsIn, final String formatOut) throws ParseException {
        int i = 0;
        while (i < formatsIn.length) {
            try {
                final SimpleDateFormat dateFormat = new SimpleDateFormat(formatsIn[i], Locale.ENGLISH);
                dateFormat.setLenient(false);
                final Date dt = dateFormat.parse(in);
                dateFormat.applyPattern(formatOut);
                return dateFormat.format(dt);
            }
            catch (final ParseException ex) {
                ++i;
                continue;
            }
            break;
        }
        return "";
    }
    
    private static String getNameOrAbbrev(final String format) {
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        return dateFormat.format(cal.getTime());
    }
    
    public static String formatDate(String dateTime, final String pattern) {
        final String yearSymbols = "Gy";
        final String monthSymbols = "M";
        final String daySymbols = "dDEFwW";
        TimeZone timeZone;
        String zone;
        if (dateTime.endsWith("Z") || dateTime.endsWith("z")) {
            timeZone = TimeZone.getTimeZone("GMT");
            dateTime = dateTime.substring(0, dateTime.length() - 1) + "GMT";
            zone = "z";
        }
        else if (dateTime.length() >= 6 && dateTime.charAt(dateTime.length() - 3) == ':' && (dateTime.charAt(dateTime.length() - 6) == '+' || dateTime.charAt(dateTime.length() - 6) == '-')) {
            final String offset = dateTime.substring(dateTime.length() - 6);
            if ("+00:00".equals(offset) || "-00:00".equals(offset)) {
                timeZone = TimeZone.getTimeZone("GMT");
            }
            else {
                timeZone = TimeZone.getTimeZone("GMT" + offset);
            }
            zone = "z";
            dateTime = dateTime.substring(0, dateTime.length() - 6) + "GMT" + offset;
        }
        else {
            timeZone = TimeZone.getDefault();
            zone = "";
        }
        final String[] formats = { "yyyy-MM-dd'T'HH:mm:ss" + zone, "yyyy-MM-dd", "yyyy-MM", "yyyy" };
        try {
            final SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm:ss" + zone);
            inFormat.setLenient(false);
            final Date d = inFormat.parse(dateTime);
            final SimpleDateFormat outFormat = new SimpleDateFormat(strip("GyMdDEFwW", pattern));
            outFormat.setTimeZone(timeZone);
            return outFormat.format(d);
        }
        catch (final ParseException ex) {
            int i = 0;
            while (i < formats.length) {
                try {
                    final SimpleDateFormat inFormat2 = new SimpleDateFormat(formats[i]);
                    inFormat2.setLenient(false);
                    final Date d2 = inFormat2.parse(dateTime);
                    final SimpleDateFormat outFormat2 = new SimpleDateFormat(pattern);
                    outFormat2.setTimeZone(timeZone);
                    return outFormat2.format(d2);
                }
                catch (final ParseException ex2) {
                    ++i;
                    continue;
                }
                break;
            }
            try {
                final SimpleDateFormat inFormat = new SimpleDateFormat("--MM-dd");
                inFormat.setLenient(false);
                final Date d = inFormat.parse(dateTime);
                final SimpleDateFormat outFormat = new SimpleDateFormat(strip("Gy", pattern));
                outFormat.setTimeZone(timeZone);
                return outFormat.format(d);
            }
            catch (final ParseException ex3) {
                try {
                    final SimpleDateFormat inFormat = new SimpleDateFormat("--MM--");
                    inFormat.setLenient(false);
                    final Date d = inFormat.parse(dateTime);
                    final SimpleDateFormat outFormat = new SimpleDateFormat(strip("Gy", pattern));
                    outFormat.setTimeZone(timeZone);
                    return outFormat.format(d);
                }
                catch (final ParseException ex4) {
                    try {
                        final SimpleDateFormat inFormat = new SimpleDateFormat("---dd");
                        inFormat.setLenient(false);
                        final Date d = inFormat.parse(dateTime);
                        final SimpleDateFormat outFormat = new SimpleDateFormat(strip("GyM", pattern));
                        outFormat.setTimeZone(timeZone);
                        return outFormat.format(d);
                    }
                    catch (final ParseException ex5) {
                        return "";
                    }
                }
            }
        }
    }
    
    private static String strip(final String symbols, final String pattern) {
        final int quoteSemaphore = 0;
        int i = 0;
        final StringBuffer result = new StringBuffer(pattern.length());
        while (i < pattern.length()) {
            final char ch = pattern.charAt(i);
            if (ch == '\'') {
                int endQuote = pattern.indexOf(39, i + 1);
                if (endQuote == -1) {
                    endQuote = pattern.length();
                }
                result.append(pattern.substring(i, endQuote));
                i = endQuote++;
            }
            else if (symbols.indexOf(ch) > -1) {
                ++i;
            }
            else {
                result.append(ch);
                ++i;
            }
        }
        return result.toString();
    }
}
