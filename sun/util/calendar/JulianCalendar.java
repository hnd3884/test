package sun.util.calendar;

import java.util.TimeZone;

public class JulianCalendar extends BaseCalendar
{
    private static final int BCE = 0;
    private static final int CE = 1;
    private static final Era[] eras;
    private static final int JULIAN_EPOCH = -1;
    
    JulianCalendar() {
        this.setEras(JulianCalendar.eras);
    }
    
    @Override
    public String getName() {
        return "julian";
    }
    
    @Override
    public Date getCalendarDate() {
        return this.getCalendarDate(System.currentTimeMillis(), this.newCalendarDate());
    }
    
    @Override
    public Date getCalendarDate(final long n) {
        return this.getCalendarDate(n, this.newCalendarDate());
    }
    
    @Override
    public Date getCalendarDate(final long n, final CalendarDate calendarDate) {
        return (Date)super.getCalendarDate(n, calendarDate);
    }
    
    @Override
    public Date getCalendarDate(final long n, final TimeZone timeZone) {
        return this.getCalendarDate(n, this.newCalendarDate(timeZone));
    }
    
    @Override
    public Date newCalendarDate() {
        return new Date();
    }
    
    @Override
    public Date newCalendarDate(final TimeZone timeZone) {
        return new Date(timeZone);
    }
    
    @Override
    public long getFixedDate(final int n, final int n2, final int n3, final BaseCalendar.Date date) {
        final boolean b = n2 == 1 && n3 == 1;
        if (date == null || !date.hit(n)) {
            final long n4 = n;
            final long n5 = -2L + 365L * (n4 - 1L) + n3;
            long n6;
            if (n4 > 0L) {
                n6 = n5 + (n4 - 1L) / 4L;
            }
            else {
                n6 = n5 + CalendarUtils.floorDivide(n4 - 1L, 4L);
            }
            long n7;
            if (n2 > 0) {
                n7 = n6 + (367L * n2 - 362L) / 12L;
            }
            else {
                n7 = n6 + CalendarUtils.floorDivide(367L * n2 - 362L, 12L);
            }
            if (n2 > 2) {
                n7 -= (CalendarUtils.isJulianLeapYear(n) ? 1L : 2L);
            }
            if (date != null && b) {
                date.setCache(n, n7, CalendarUtils.isJulianLeapYear(n) ? 366 : 365);
            }
            return n7;
        }
        if (b) {
            return date.getCachedJan1();
        }
        return date.getCachedJan1() + this.getDayOfYear(n, n2, n3) - 1L;
    }
    
    @Override
    public void getCalendarDateFromFixedDate(final CalendarDate calendarDate, final long n) {
        final Date date = (Date)calendarDate;
        final long n2 = 4L * (n + 1L) + 1464L;
        int normalizedYear;
        if (n2 >= 0L) {
            normalizedYear = (int)(n2 / 1461L);
        }
        else {
            normalizedYear = (int)CalendarUtils.floorDivide(n2, 1461L);
        }
        int n3 = (int)(n - this.getFixedDate(normalizedYear, 1, 1, date));
        final boolean julianLeapYear = CalendarUtils.isJulianLeapYear(normalizedYear);
        if (n >= this.getFixedDate(normalizedYear, 3, 1, date)) {
            n3 += (julianLeapYear ? 1 : 2);
        }
        final int n4 = 12 * n3 + 373;
        int floorDivide;
        if (n4 > 0) {
            floorDivide = n4 / 367;
        }
        else {
            floorDivide = CalendarUtils.floorDivide(n4, 367);
        }
        final int dayOfMonth = (int)(n - this.getFixedDate(normalizedYear, floorDivide, 1, date)) + 1;
        final int dayOfWeekFromFixedDate = BaseCalendar.getDayOfWeekFromFixedDate(n);
        assert dayOfWeekFromFixedDate > 0 : "negative day of week " + dayOfWeekFromFixedDate;
        date.setNormalizedYear(normalizedYear);
        date.setMonth(floorDivide);
        date.setDayOfMonth(dayOfMonth);
        date.setDayOfWeek(dayOfWeekFromFixedDate);
        date.setLeapYear(julianLeapYear);
        date.setNormalized(true);
    }
    
    @Override
    public int getYearFromFixedDate(final long n) {
        return (int)CalendarUtils.floorDivide(4L * (n + 1L) + 1464L, 1461L);
    }
    
    @Override
    public int getDayOfWeek(final CalendarDate calendarDate) {
        return BaseCalendar.getDayOfWeekFromFixedDate(this.getFixedDate(calendarDate));
    }
    
    @Override
    boolean isLeapYear(final int n) {
        return CalendarUtils.isJulianLeapYear(n);
    }
    
    static {
        eras = new Era[] { new Era("BeforeCommonEra", "B.C.E.", Long.MIN_VALUE, false), new Era("CommonEra", "C.E.", -62135709175808L, true) };
    }
    
    private static class Date extends BaseCalendar.Date
    {
        protected Date() {
            this.setCache(1, -1L, 365);
        }
        
        protected Date(final TimeZone timeZone) {
            super(timeZone);
            this.setCache(1, -1L, 365);
        }
        
        @Override
        public Date setEra(final Era era) {
            if (era == null) {
                throw new NullPointerException();
            }
            if (era != JulianCalendar.eras[0] || era != JulianCalendar.eras[1]) {
                throw new IllegalArgumentException("unknown era: " + era);
            }
            super.setEra(era);
            return this;
        }
        
        protected void setKnownEra(final Era era) {
            super.setEra(era);
        }
        
        @Override
        public int getNormalizedYear() {
            if (this.getEra() == JulianCalendar.eras[0]) {
                return 1 - this.getYear();
            }
            return this.getYear();
        }
        
        @Override
        public void setNormalizedYear(final int year) {
            if (year <= 0) {
                this.setYear(1 - year);
                this.setKnownEra(JulianCalendar.eras[0]);
            }
            else {
                this.setYear(year);
                this.setKnownEra(JulianCalendar.eras[1]);
            }
        }
        
        @Override
        public String toString() {
            final String string = super.toString();
            final String substring = string.substring(string.indexOf(84));
            final StringBuffer sb = new StringBuffer();
            final Era era = this.getEra();
            if (era != null) {
                final String abbreviation = era.getAbbreviation();
                if (abbreviation != null) {
                    sb.append(abbreviation).append(' ');
                }
            }
            sb.append(this.getYear()).append('-');
            CalendarUtils.sprintf0d(sb, this.getMonth(), 2).append('-');
            CalendarUtils.sprintf0d(sb, this.getDayOfMonth(), 2);
            sb.append(substring);
            return sb.toString();
        }
    }
}
