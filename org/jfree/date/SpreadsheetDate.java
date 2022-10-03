package org.jfree.date;

import java.util.Calendar;
import java.util.Date;

public class SpreadsheetDate extends SerialDate
{
    private static final long serialVersionUID = -2039586705374454461L;
    private int serial;
    private int day;
    private int month;
    private int year;
    private String description;
    
    public SpreadsheetDate(final int serial) {
        if (serial >= 2 && serial <= 2958465) {
            this.serial = serial;
            this.calcDayMonthYear();
            return;
        }
        throw new IllegalArgumentException("SpreadsheetDate: Serial must be in range 2 to 2958465.");
    }
    
    public SpreadsheetDate(final int day, final int month, final int year) {
        if (year < 1900 || year > 9999) {
            throw new IllegalArgumentException("The 'year' argument must be in range 1900 to 9999.");
        }
        this.year = year;
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("The 'month' argument must be in the range 1 to 12.");
        }
        this.month = month;
        if (day >= 1 && day <= SerialDate.lastDayOfMonth(month, year)) {
            this.day = day;
            this.serial = this.calcSerial(day, month, year);
            this.description = null;
            return;
        }
        throw new IllegalArgumentException("Invalid 'day' argument.");
    }
    
    private void calcDayMonthYear() {
        final int days = this.serial - 2;
        final int overestimatedYYYY = 1900 + days / 365;
        final int leaps = SerialDate.leapYearCount(overestimatedYYYY);
        final int nonleapdays = days - leaps;
        int underestimatedYYYY = 1900 + nonleapdays / 365;
        if (underestimatedYYYY == overestimatedYYYY) {
            this.year = underestimatedYYYY;
        }
        else {
            for (int ss1 = this.calcSerial(1, 1, underestimatedYYYY); ss1 <= this.serial; ss1 = this.calcSerial(1, 1, underestimatedYYYY)) {
                ++underestimatedYYYY;
            }
            this.year = underestimatedYYYY - 1;
        }
        final int ss2 = this.calcSerial(1, 1, this.year);
        int[] daysToEndOfPrecedingMonth = SerialDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
        if (SerialDate.isLeapYear(this.year)) {
            daysToEndOfPrecedingMonth = SerialDate.LEAP_YEAR_AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH;
        }
        int mm = 1;
        for (int sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1; sss < this.serial; sss = ss2 + daysToEndOfPrecedingMonth[mm] - 1) {
            ++mm;
        }
        this.month = mm - 1;
        this.day = this.serial - ss2 - daysToEndOfPrecedingMonth[this.month] + 1;
    }
    
    private int calcSerial(final int d, final int m, final int y) {
        final int yy = (y - 1900) * 365 + SerialDate.leapYearCount(y - 1);
        int mm = SerialDate.AGGREGATE_DAYS_TO_END_OF_PRECEDING_MONTH[m];
        if (m > 2 && SerialDate.isLeapYear(y)) {
            ++mm;
        }
        final int dd = d;
        return yy + mm + dd + 1;
    }
    
    public int compare(final SerialDate other) {
        return this.serial - other.toSerial();
    }
    
    public int compareTo(final Object other) {
        return this.compare((SerialDate)other);
    }
    
    public boolean equals(final Object object) {
        if (object instanceof SerialDate) {
            final SerialDate s = (SerialDate)object;
            return s.toSerial() == this.toSerial();
        }
        return false;
    }
    
    public int getDayOfMonth() {
        return this.day;
    }
    
    public int getDayOfWeek() {
        return (this.serial + 6) % 7 + 1;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public int getYYYY() {
        return this.year;
    }
    
    public int hashCode() {
        return this.toSerial();
    }
    
    public boolean isAfter(final SerialDate other) {
        return this.serial > other.toSerial();
    }
    
    public boolean isBefore(final SerialDate other) {
        return this.serial < other.toSerial();
    }
    
    public boolean isInRange(final SerialDate d1, final SerialDate d2) {
        return this.isInRange(d1, d2, 3);
    }
    
    public boolean isInRange(final SerialDate d1, final SerialDate d2, final int include) {
        final int s1 = d1.toSerial();
        final int s2 = d2.toSerial();
        final int start = Math.min(s1, s2);
        final int end = Math.max(s1, s2);
        final int s3 = this.toSerial();
        if (include == 3) {
            return s3 >= start && s3 <= end;
        }
        if (include == 1) {
            return s3 >= start && s3 < end;
        }
        if (include == 2) {
            return s3 > start && s3 <= end;
        }
        return s3 > start && s3 < end;
    }
    
    public boolean isOn(final SerialDate other) {
        return this.serial == other.toSerial();
    }
    
    public boolean isOnOrAfter(final SerialDate other) {
        return this.serial >= other.toSerial();
    }
    
    public boolean isOnOrBefore(final SerialDate other) {
        return this.serial <= other.toSerial();
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public Date toDate() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(this.getYYYY(), this.getMonth() - 1, this.getDayOfMonth(), 0, 0, 0);
        return calendar.getTime();
    }
    
    public int toSerial() {
        return this.serial;
    }
}
