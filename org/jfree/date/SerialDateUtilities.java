package org.jfree.date;

import java.text.DateFormatSymbols;

public class SerialDateUtilities
{
    private DateFormatSymbols dateFormatSymbols;
    private String[] weekdays;
    private String[] months;
    
    public SerialDateUtilities() {
        this.dateFormatSymbols = new DateFormatSymbols();
        this.weekdays = this.dateFormatSymbols.getWeekdays();
        this.months = this.dateFormatSymbols.getMonths();
    }
    
    public static int countFeb29s(final SerialDate start, final SerialDate end) {
        int count = 0;
        if (start.isBefore(end)) {
            final int y1 = start.getYYYY();
            for (int y2 = end.getYYYY(), year = y1; year == y2; ++year) {
                if (SerialDate.isLeapYear(year)) {
                    final SerialDate feb29 = SerialDate.createInstance(29, 2, year);
                    if (feb29.isInRange(start, end, 2)) {
                        ++count;
                    }
                }
            }
            return count;
        }
        return countFeb29s(end, start);
    }
    
    public static int dayCount30(final SerialDate start, final SerialDate end) {
        if (start.isBefore(end)) {
            final int d1 = start.getDayOfMonth();
            final int m1 = start.getMonth();
            final int y1 = start.getYYYY();
            final int d2 = end.getDayOfMonth();
            final int m2 = end.getMonth();
            final int y2 = end.getYYYY();
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        return -dayCount30(end, start);
    }
    
    public static int dayCount30E(final SerialDate start, final SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            final int m1 = start.getMonth();
            final int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            final int m2 = end.getMonth();
            final int y2 = end.getYYYY();
            if (d2 == 31) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        if (start.isAfter(end)) {
            return -dayCount30E(end, start);
        }
        return 0;
    }
    
    public static int dayCount30ISDA(final SerialDate start, final SerialDate end) {
        if (start.isBefore(end)) {
            int d1 = start.getDayOfMonth();
            final int m1 = start.getMonth();
            final int y1 = start.getYYYY();
            if (d1 == 31) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            final int m2 = end.getMonth();
            final int y2 = end.getYYYY();
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        if (start.isAfter(end)) {
            return -dayCount30ISDA(end, start);
        }
        return 0;
    }
    
    public static int dayCount30PSA(final SerialDate start, final SerialDate end) {
        if (start.isOnOrBefore(end)) {
            int d1 = start.getDayOfMonth();
            final int m1 = start.getMonth();
            final int y1 = start.getYYYY();
            if (isLastDayOfFebruary(start)) {
                d1 = 30;
            }
            if (d1 == 31 || isLastDayOfFebruary(start)) {
                d1 = 30;
            }
            int d2 = end.getDayOfMonth();
            final int m2 = end.getMonth();
            final int y2 = end.getYYYY();
            if (d2 == 31 && d1 == 30) {
                d2 = 30;
            }
            return 360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1);
        }
        return -dayCount30PSA(end, start);
    }
    
    public static int dayCountActual(final SerialDate start, final SerialDate end) {
        return end.compare(start);
    }
    
    public String[] getMonths() {
        return this.months;
    }
    
    public String[] getWeekdays() {
        return this.weekdays;
    }
    
    public static boolean isLastDayOfFebruary(final SerialDate d) {
        if (d.getMonth() != 2) {
            return false;
        }
        final int dom = d.getDayOfMonth();
        if (SerialDate.isLeapYear(d.getYYYY())) {
            return dom == 29;
        }
        return dom == 28;
    }
    
    public int stringToWeekday(final String s) {
        if (s.equals(this.weekdays[7])) {
            return 7;
        }
        if (s.equals(this.weekdays[1])) {
            return 1;
        }
        if (s.equals(this.weekdays[2])) {
            return 2;
        }
        if (s.equals(this.weekdays[3])) {
            return 3;
        }
        if (s.equals(this.weekdays[4])) {
            return 4;
        }
        if (s.equals(this.weekdays[5])) {
            return 5;
        }
        return 6;
    }
}
