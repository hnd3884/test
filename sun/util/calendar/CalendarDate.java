package sun.util.calendar;

import java.util.Locale;
import java.util.TimeZone;

public abstract class CalendarDate implements Cloneable
{
    public static final int FIELD_UNDEFINED = Integer.MIN_VALUE;
    public static final long TIME_UNDEFINED = Long.MIN_VALUE;
    private Era era;
    private int year;
    private int month;
    private int dayOfMonth;
    private int dayOfWeek;
    private boolean leapYear;
    private int hours;
    private int minutes;
    private int seconds;
    private int millis;
    private long fraction;
    private boolean normalized;
    private TimeZone zoneinfo;
    private int zoneOffset;
    private int daylightSaving;
    private boolean forceStandardTime;
    private Locale locale;
    
    protected CalendarDate() {
        this(TimeZone.getDefault());
    }
    
    protected CalendarDate(final TimeZone zoneinfo) {
        this.dayOfWeek = Integer.MIN_VALUE;
        this.zoneinfo = zoneinfo;
    }
    
    public Era getEra() {
        return this.era;
    }
    
    public CalendarDate setEra(final Era era) {
        if (this.era == era) {
            return this;
        }
        this.era = era;
        this.normalized = false;
        return this;
    }
    
    public int getYear() {
        return this.year;
    }
    
    public CalendarDate setYear(final int year) {
        if (this.year != year) {
            this.year = year;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addYear(final int n) {
        if (n != 0) {
            this.year += n;
            this.normalized = false;
        }
        return this;
    }
    
    public boolean isLeapYear() {
        return this.leapYear;
    }
    
    void setLeapYear(final boolean leapYear) {
        this.leapYear = leapYear;
    }
    
    public int getMonth() {
        return this.month;
    }
    
    public CalendarDate setMonth(final int month) {
        if (this.month != month) {
            this.month = month;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addMonth(final int n) {
        if (n != 0) {
            this.month += n;
            this.normalized = false;
        }
        return this;
    }
    
    public int getDayOfMonth() {
        return this.dayOfMonth;
    }
    
    public CalendarDate setDayOfMonth(final int dayOfMonth) {
        if (this.dayOfMonth != dayOfMonth) {
            this.dayOfMonth = dayOfMonth;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addDayOfMonth(final int n) {
        if (n != 0) {
            this.dayOfMonth += n;
            this.normalized = false;
        }
        return this;
    }
    
    public int getDayOfWeek() {
        if (!this.isNormalized()) {
            this.dayOfWeek = Integer.MIN_VALUE;
        }
        return this.dayOfWeek;
    }
    
    public int getHours() {
        return this.hours;
    }
    
    public CalendarDate setHours(final int hours) {
        if (this.hours != hours) {
            this.hours = hours;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addHours(final int n) {
        if (n != 0) {
            this.hours += n;
            this.normalized = false;
        }
        return this;
    }
    
    public int getMinutes() {
        return this.minutes;
    }
    
    public CalendarDate setMinutes(final int minutes) {
        if (this.minutes != minutes) {
            this.minutes = minutes;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addMinutes(final int n) {
        if (n != 0) {
            this.minutes += n;
            this.normalized = false;
        }
        return this;
    }
    
    public int getSeconds() {
        return this.seconds;
    }
    
    public CalendarDate setSeconds(final int seconds) {
        if (this.seconds != seconds) {
            this.seconds = seconds;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addSeconds(final int n) {
        if (n != 0) {
            this.seconds += n;
            this.normalized = false;
        }
        return this;
    }
    
    public int getMillis() {
        return this.millis;
    }
    
    public CalendarDate setMillis(final int millis) {
        if (this.millis != millis) {
            this.millis = millis;
            this.normalized = false;
        }
        return this;
    }
    
    public CalendarDate addMillis(final int n) {
        if (n != 0) {
            this.millis += n;
            this.normalized = false;
        }
        return this;
    }
    
    public long getTimeOfDay() {
        if (!this.isNormalized()) {
            return this.fraction = Long.MIN_VALUE;
        }
        return this.fraction;
    }
    
    public CalendarDate setDate(final int year, final int month, final int dayOfMonth) {
        this.setYear(year);
        this.setMonth(month);
        this.setDayOfMonth(dayOfMonth);
        return this;
    }
    
    public CalendarDate addDate(final int n, final int n2, final int n3) {
        this.addYear(n);
        this.addMonth(n2);
        this.addDayOfMonth(n3);
        return this;
    }
    
    public CalendarDate setTimeOfDay(final int hours, final int minutes, final int seconds, final int millis) {
        this.setHours(hours);
        this.setMinutes(minutes);
        this.setSeconds(seconds);
        this.setMillis(millis);
        return this;
    }
    
    public CalendarDate addTimeOfDay(final int n, final int n2, final int n3, final int n4) {
        this.addHours(n);
        this.addMinutes(n2);
        this.addSeconds(n3);
        this.addMillis(n4);
        return this;
    }
    
    protected void setTimeOfDay(final long fraction) {
        this.fraction = fraction;
    }
    
    public boolean isNormalized() {
        return this.normalized;
    }
    
    public boolean isStandardTime() {
        return this.forceStandardTime;
    }
    
    public void setStandardTime(final boolean forceStandardTime) {
        this.forceStandardTime = forceStandardTime;
    }
    
    public boolean isDaylightTime() {
        return !this.isStandardTime() && this.daylightSaving != 0;
    }
    
    protected void setLocale(final Locale locale) {
        this.locale = locale;
    }
    
    public TimeZone getZone() {
        return this.zoneinfo;
    }
    
    public CalendarDate setZone(final TimeZone zoneinfo) {
        this.zoneinfo = zoneinfo;
        return this;
    }
    
    public boolean isSameDate(final CalendarDate calendarDate) {
        return this.getDayOfWeek() == calendarDate.getDayOfWeek() && this.getMonth() == calendarDate.getMonth() && this.getYear() == calendarDate.getYear() && this.getEra() == calendarDate.getEra();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof CalendarDate)) {
            return false;
        }
        final CalendarDate calendarDate = (CalendarDate)o;
        if (this.isNormalized() != calendarDate.isNormalized()) {
            return false;
        }
        final boolean b = this.zoneinfo != null;
        return b == (calendarDate.zoneinfo != null) && (!b || this.zoneinfo.equals(calendarDate.zoneinfo)) && this.getEra() == calendarDate.getEra() && this.year == calendarDate.year && this.month == calendarDate.month && this.dayOfMonth == calendarDate.dayOfMonth && this.hours == calendarDate.hours && this.minutes == calendarDate.minutes && this.seconds == calendarDate.seconds && this.millis == calendarDate.millis && this.zoneOffset == calendarDate.zoneOffset;
    }
    
    @Override
    public int hashCode() {
        final long n = ((((((this.year - 1970L) * 12L + (this.month - 1)) * 30L + this.dayOfMonth) * 24L + this.hours) * 60L + this.minutes) * 60L + this.seconds) * 1000L + this.millis - this.zoneOffset;
        final boolean normalized = this.isNormalized();
        int hashCode = 0;
        final Era era = this.getEra();
        if (era != null) {
            hashCode = era.hashCode();
        }
        return (int)n * (int)(n >> 32) ^ hashCode ^ (normalized ? 1 : 0) ^ ((this.zoneinfo != null) ? this.zoneinfo.hashCode() : 0);
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        CalendarUtils.sprintf0d(sb, this.year, 4).append('-');
        CalendarUtils.sprintf0d(sb, this.month, 2).append('-');
        CalendarUtils.sprintf0d(sb, this.dayOfMonth, 2).append('T');
        CalendarUtils.sprintf0d(sb, this.hours, 2).append(':');
        CalendarUtils.sprintf0d(sb, this.minutes, 2).append(':');
        CalendarUtils.sprintf0d(sb, this.seconds, 2).append('.');
        CalendarUtils.sprintf0d(sb, this.millis, 3);
        if (this.zoneOffset == 0) {
            sb.append('Z');
        }
        else if (this.zoneOffset != Integer.MIN_VALUE) {
            int zoneOffset;
            char c;
            if (this.zoneOffset > 0) {
                zoneOffset = this.zoneOffset;
                c = '+';
            }
            else {
                zoneOffset = -this.zoneOffset;
                c = '-';
            }
            final int n = zoneOffset / 60000;
            sb.append(c);
            CalendarUtils.sprintf0d(sb, n / 60, 2);
            CalendarUtils.sprintf0d(sb, n % 60, 2);
        }
        else {
            sb.append(" local time");
        }
        return sb.toString();
    }
    
    protected void setDayOfWeek(final int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    protected void setNormalized(final boolean normalized) {
        this.normalized = normalized;
    }
    
    public int getZoneOffset() {
        return this.zoneOffset;
    }
    
    protected void setZoneOffset(final int zoneOffset) {
        this.zoneOffset = zoneOffset;
    }
    
    public int getDaylightSaving() {
        return this.daylightSaving;
    }
    
    protected void setDaylightSaving(final int daylightSaving) {
        this.daylightSaving = daylightSaving;
    }
}
