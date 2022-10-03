package org.jfree.date;

import java.util.GregorianCalendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.io.Serializable;

public abstract class SerialDate implements Comparable, Serializable, MonthConstants
{
    private static final long serialVersionUID = -293716040467423637L;
    public static final DateFormatSymbols DATE_FORMAT_SYMBOLS;
    public static final int SERIAL_LOWER_BOUND = 2;
    public static final int SERIAL_UPPER_BOUND = 2958465;
    public static final int MINIMUM_YEAR_SUPPORTED = 1900;
    public static final int MAXIMUM_YEAR_SUPPORTED = 9999;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;
    public static final int SUNDAY = 1;
    static final int[] LAST_DAY_OF_MONTH;
    static final int[] AGGREGATE_DAYS_TO_END_OF_MONTH;
    static final int[] AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_MONTH;
    static final int[] LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
    public static final int FIRST_WEEK_IN_MONTH = 1;
    public static final int SECOND_WEEK_IN_MONTH = 2;
    public static final int THIRD_WEEK_IN_MONTH = 3;
    public static final int FOURTH_WEEK_IN_MONTH = 4;
    public static final int LAST_WEEK_IN_MONTH = 0;
    public static final int INCLUDE_NONE = 0;
    public static final int INCLUDE_FIRST = 1;
    public static final int INCLUDE_SECOND = 2;
    public static final int INCLUDE_BOTH = 3;
    public static final int PRECEDING = -1;
    public static final int NEAREST = 0;
    public static final int FOLLOWING = 1;
    private String description;
    
    static {
        DATE_FORMAT_SYMBOLS = new SimpleDateFormat().getDateFormatSymbols();
        LAST_DAY_OF_MONTH = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        AGGREGATE_DAYS_TO_END_OF_MONTH = new int[] { 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };
        AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = new int[] { 0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 };
        LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_MONTH = new int[] { 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };
        LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH = new int[] { 0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };
    }
    
    protected SerialDate() {
    }
    
    public static SerialDate addDays(final int days, final SerialDate base) {
        final int serialDayNumber = base.toSerial() + days;
        return createInstance(serialDayNumber);
    }
    
    public static SerialDate addMonths(final int months, final SerialDate base) {
        final int yy = (12 * base.getYYYY() + base.getMonth() + months - 1) / 12;
        final int mm = (12 * base.getYYYY() + base.getMonth() + months - 1) % 12 + 1;
        final int dd = Math.min(base.getDayOfMonth(), lastDayOfMonth(mm, yy));
        return createInstance(dd, mm, yy);
    }
    
    public static SerialDate addYears(final int years, final SerialDate base) {
        final int baseY = base.getYYYY();
        final int baseM = base.getMonth();
        final int baseD = base.getDayOfMonth();
        final int targetY = baseY + years;
        final int targetD = Math.min(baseD, lastDayOfMonth(baseM, targetY));
        return createInstance(targetD, baseM, targetY);
    }
    
    public abstract int compare(final SerialDate p0);
    
    public abstract int compareTo(final Object p0);
    
    public static SerialDate createInstance(final int serial) {
        return new SpreadsheetDate(serial);
    }
    
    public static SerialDate createInstance(final int day, final int month, final int yyyy) {
        return new SpreadsheetDate(day, month, yyyy);
    }
    
    public static SerialDate createInstance(final Date date) {
        final GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return new SpreadsheetDate(calendar.get(5), calendar.get(2) + 1, calendar.get(1));
    }
    
    public abstract int getDayOfMonth();
    
    public abstract int getDayOfWeek();
    
    public String getDescription() {
        return this.description;
    }
    
    public SerialDate getEndOfCurrentMonth(final SerialDate base) {
        final int last = lastDayOfMonth(base.getMonth(), base.getYYYY());
        return createInstance(last, base.getMonth(), base.getYYYY());
    }
    
    public SerialDate getFollowingDayOfWeek(final int targetDOW) {
        return getFollowingDayOfWeek(targetDOW, this);
    }
    
    public static SerialDate getFollowingDayOfWeek(final int targetWeekday, final SerialDate base) {
        if (!isValidWeekdayCode(targetWeekday)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        final int baseDOW = base.getDayOfWeek();
        int adjust;
        if (baseDOW > targetWeekday) {
            adjust = 7 + Math.min(0, targetWeekday - baseDOW);
        }
        else {
            adjust = Math.max(0, targetWeekday - baseDOW);
        }
        return addDays(adjust, base);
    }
    
    public abstract int getMonth();
    
    public static String[] getMonths() {
        return getMonths(false);
    }
    
    public static String[] getMonths(final boolean shortened) {
        if (shortened) {
            return SerialDate.DATE_FORMAT_SYMBOLS.getShortMonths();
        }
        return SerialDate.DATE_FORMAT_SYMBOLS.getMonths();
    }
    
    public SerialDate getNearestDayOfWeek(final int targetDOW) {
        return getNearestDayOfWeek(targetDOW, this);
    }
    
    public static SerialDate getNearestDayOfWeek(final int targetDOW, final SerialDate base) {
        if (!isValidWeekdayCode(targetDOW)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        final int baseDOW = base.getDayOfWeek();
        int adjust = -Math.abs(targetDOW - baseDOW);
        if (adjust >= 4) {
            adjust = 7 - adjust;
        }
        if (adjust <= -4) {
            adjust += 7;
        }
        return addDays(adjust, base);
    }
    
    public SerialDate getPreviousDayOfWeek(final int targetDOW) {
        return getPreviousDayOfWeek(targetDOW, this);
    }
    
    public static SerialDate getPreviousDayOfWeek(final int targetWeekday, final SerialDate base) {
        if (!isValidWeekdayCode(targetWeekday)) {
            throw new IllegalArgumentException("Invalid day-of-the-week code.");
        }
        final int baseDOW = base.getDayOfWeek();
        int adjust;
        if (baseDOW > targetWeekday) {
            adjust = Math.min(0, targetWeekday - baseDOW);
        }
        else {
            adjust = -7 + Math.max(0, targetWeekday - baseDOW);
        }
        return addDays(adjust, base);
    }
    
    public abstract int getYYYY();
    
    public abstract boolean isAfter(final SerialDate p0);
    
    public abstract boolean isBefore(final SerialDate p0);
    
    public abstract boolean isInRange(final SerialDate p0, final SerialDate p1);
    
    public abstract boolean isInRange(final SerialDate p0, final SerialDate p1, final int p2);
    
    public static boolean isLeapYear(final int yyyy) {
        return yyyy % 4 == 0 && (yyyy % 400 == 0 || yyyy % 100 != 0);
    }
    
    public abstract boolean isOn(final SerialDate p0);
    
    public abstract boolean isOnOrAfter(final SerialDate p0);
    
    public abstract boolean isOnOrBefore(final SerialDate p0);
    
    public static boolean isValidMonthCode(final int code) {
        switch (code) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isValidWeekInMonthCode(final int code) {
        switch (code) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isValidWeekdayCode(final int code) {
        switch (code) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static int lastDayOfMonth(final int month, final int yyyy) {
        final int result = SerialDate.LAST_DAY_OF_MONTH[month];
        if (month != 2) {
            return result;
        }
        if (isLeapYear(yyyy)) {
            return result + 1;
        }
        return result;
    }
    
    public static int leapYearCount(final int yyyy) {
        final int leap4 = (yyyy - 1896) / 4;
        final int leap5 = (yyyy - 1800) / 100;
        final int leap6 = (yyyy - 1600) / 400;
        return leap4 - leap5 + leap6;
    }
    
    public static int monthCodeToQuarter(final int code) {
        switch (code) {
            case 1:
            case 2:
            case 3: {
                return 1;
            }
            case 4:
            case 5:
            case 6: {
                return 2;
            }
            case 7:
            case 8:
            case 9: {
                return 3;
            }
            case 10:
            case 11:
            case 12: {
                return 4;
            }
            default: {
                throw new IllegalArgumentException("SerialDate.monthCodeToQuarter: invalid month code.");
            }
        }
    }
    
    public static String monthCodeToString(final int month) {
        return monthCodeToString(month, false);
    }
    
    public static String monthCodeToString(final int month, final boolean shortened) {
        if (!isValidMonthCode(month)) {
            throw new IllegalArgumentException("SerialDate.monthCodeToString: month outside valid range.");
        }
        String[] months;
        if (shortened) {
            months = SerialDate.DATE_FORMAT_SYMBOLS.getShortMonths();
        }
        else {
            months = SerialDate.DATE_FORMAT_SYMBOLS.getMonths();
        }
        return months[month - 1];
    }
    
    public static String relativeToString(final int relative) {
        switch (relative) {
            case -1: {
                return "Preceding";
            }
            case 0: {
                return "Nearest";
            }
            case 1: {
                return "Following";
            }
            default: {
                return "ERROR : Relative To String";
            }
        }
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public static int stringToMonthCode(String s) {
        final String[] shortMonthNames = SerialDate.DATE_FORMAT_SYMBOLS.getShortMonths();
        final String[] monthNames = SerialDate.DATE_FORMAT_SYMBOLS.getMonths();
        int result = -1;
        s = s.trim();
        try {
            result = Integer.parseInt(s);
        }
        catch (final NumberFormatException ex) {}
        if (result < 1 || result > 12) {
            for (int i = 0; i < monthNames.length; ++i) {
                if (s.equals(shortMonthNames[i])) {
                    result = i + 1;
                    break;
                }
                if (s.equals(monthNames[i])) {
                    result = i + 1;
                    break;
                }
            }
        }
        return result;
    }
    
    public static int stringToWeekdayCode(String s) {
        final String[] shortWeekdayNames = SerialDate.DATE_FORMAT_SYMBOLS.getShortWeekdays();
        final String[] weekDayNames = SerialDate.DATE_FORMAT_SYMBOLS.getWeekdays();
        int result = -1;
        s = s.trim();
        for (int i = 0; i < weekDayNames.length; ++i) {
            if (s.equals(shortWeekdayNames[i])) {
                result = i;
                break;
            }
            if (s.equals(weekDayNames[i])) {
                result = i;
                break;
            }
        }
        return result;
    }
    
    public abstract Date toDate();
    
    public abstract int toSerial();
    
    public String toString() {
        return String.valueOf(this.getDayOfMonth()) + "-" + monthCodeToString(this.getMonth()) + "-" + this.getYYYY();
    }
    
    public static String weekInMonthToString(final int count) {
        switch (count) {
            case 1: {
                return "First";
            }
            case 2: {
                return "Second";
            }
            case 3: {
                return "Third";
            }
            case 4: {
                return "Fourth";
            }
            case 0: {
                return "Last";
            }
            default: {
                return "SerialDate.weekInMonthToString(): invalid code.";
            }
        }
    }
    
    public static String weekdayCodeToString(final int weekday) {
        final String[] weekdays = SerialDate.DATE_FORMAT_SYMBOLS.getWeekdays();
        return weekdays[weekday];
    }
}
