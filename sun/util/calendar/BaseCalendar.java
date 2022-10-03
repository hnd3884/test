package sun.util.calendar;

import java.util.TimeZone;

public abstract class BaseCalendar extends AbstractCalendar
{
    public static final int JANUARY = 1;
    public static final int FEBRUARY = 2;
    public static final int MARCH = 3;
    public static final int APRIL = 4;
    public static final int MAY = 5;
    public static final int JUNE = 6;
    public static final int JULY = 7;
    public static final int AUGUST = 8;
    public static final int SEPTEMBER = 9;
    public static final int OCTOBER = 10;
    public static final int NOVEMBER = 11;
    public static final int DECEMBER = 12;
    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;
    private static final int BASE_YEAR = 1970;
    private static final int[] FIXED_DATES;
    static final int[] DAYS_IN_MONTH;
    static final int[] ACCUMULATED_DAYS_IN_MONTH;
    static final int[] ACCUMULATED_DAYS_IN_MONTH_LEAP;
    
    @Override
    public boolean validate(final CalendarDate calendarDate) {
        final Date date = (Date)calendarDate;
        if (date.isNormalized()) {
            return true;
        }
        final int month = date.getMonth();
        if (month < 1 || month > 12) {
            return false;
        }
        final int dayOfMonth = date.getDayOfMonth();
        if (dayOfMonth <= 0 || dayOfMonth > this.getMonthLength(date.getNormalizedYear(), month)) {
            return false;
        }
        final int dayOfWeek = date.getDayOfWeek();
        if (dayOfWeek != Integer.MIN_VALUE && dayOfWeek != this.getDayOfWeek(date)) {
            return false;
        }
        if (!this.validateTime(calendarDate)) {
            return false;
        }
        date.setNormalized(true);
        return true;
    }
    
    @Override
    public boolean normalize(final CalendarDate calendarDate) {
        if (calendarDate.isNormalized()) {
            return true;
        }
        final Date date = (Date)calendarDate;
        if (date.getZone() != null) {
            this.getTime(calendarDate);
            return true;
        }
        final int normalizeTime = this.normalizeTime(date);
        this.normalizeMonth(date);
        final long n = date.getDayOfMonth() + (long)normalizeTime;
        int month = date.getMonth();
        final int normalizedYear = date.getNormalizedYear();
        final int monthLength = this.getMonthLength(normalizedYear, month);
        if (n <= 0L || n > monthLength) {
            if (n <= 0L && n > -28L) {
                date.setDayOfMonth((int)(n + this.getMonthLength(normalizedYear, --month)));
                if (month == 0) {
                    month = 12;
                    date.setNormalizedYear(normalizedYear - 1);
                }
                date.setMonth(month);
            }
            else if (n > monthLength && n < monthLength + 28) {
                final long n2 = n - monthLength;
                ++month;
                date.setDayOfMonth((int)n2);
                if (month > 12) {
                    date.setNormalizedYear(normalizedYear + 1);
                    month = 1;
                }
                date.setMonth(month);
            }
            else {
                this.getCalendarDateFromFixedDate(date, n + this.getFixedDate(normalizedYear, month, 1, date) - 1L);
            }
        }
        else {
            date.setDayOfWeek(this.getDayOfWeek(date));
        }
        calendarDate.setLeapYear(this.isLeapYear(date.getNormalizedYear()));
        calendarDate.setZoneOffset(0);
        calendarDate.setDaylightSaving(0);
        date.setNormalized(true);
        return true;
    }
    
    void normalizeMonth(final CalendarDate calendarDate) {
        final Date date = (Date)calendarDate;
        final int normalizedYear = date.getNormalizedYear();
        final long n = date.getMonth();
        if (n <= 0L) {
            final long n2 = 1L - n;
            final int normalizedYear2 = normalizedYear - (int)(n2 / 12L + 1L);
            final long n3 = 13L - n2 % 12L;
            date.setNormalizedYear(normalizedYear2);
            date.setMonth((int)n3);
        }
        else if (n > 12L) {
            final int normalizedYear3 = normalizedYear + (int)((n - 1L) / 12L);
            final long n4 = (n - 1L) % 12L + 1L;
            date.setNormalizedYear(normalizedYear3);
            date.setMonth((int)n4);
        }
    }
    
    @Override
    public int getYearLength(final CalendarDate calendarDate) {
        return this.isLeapYear(((Date)calendarDate).getNormalizedYear()) ? 366 : 365;
    }
    
    @Override
    public int getYearLengthInMonths(final CalendarDate calendarDate) {
        return 12;
    }
    
    @Override
    public int getMonthLength(final CalendarDate calendarDate) {
        final Date date = (Date)calendarDate;
        final int month = date.getMonth();
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Illegal month value: " + month);
        }
        return this.getMonthLength(date.getNormalizedYear(), month);
    }
    
    private int getMonthLength(final int n, final int n2) {
        int n3 = BaseCalendar.DAYS_IN_MONTH[n2];
        if (n2 == 2 && this.isLeapYear(n)) {
            ++n3;
        }
        return n3;
    }
    
    public long getDayOfYear(final CalendarDate calendarDate) {
        return this.getDayOfYear(((Date)calendarDate).getNormalizedYear(), calendarDate.getMonth(), calendarDate.getDayOfMonth());
    }
    
    final long getDayOfYear(final int n, final int n2, final int n3) {
        return n3 + (long)(this.isLeapYear(n) ? BaseCalendar.ACCUMULATED_DAYS_IN_MONTH_LEAP[n2] : BaseCalendar.ACCUMULATED_DAYS_IN_MONTH[n2]);
    }
    
    public long getFixedDate(final CalendarDate calendarDate) {
        if (!calendarDate.isNormalized()) {
            this.normalizeMonth(calendarDate);
        }
        return this.getFixedDate(((Date)calendarDate).getNormalizedYear(), calendarDate.getMonth(), calendarDate.getDayOfMonth(), (Date)calendarDate);
    }
    
    public long getFixedDate(final int n, final int n2, final int n3, final Date date) {
        final boolean b = n2 == 1 && n3 == 1;
        if (date != null && date.hit(n)) {
            if (b) {
                return date.getCachedJan1();
            }
            return date.getCachedJan1() + this.getDayOfYear(n, n2, n3) - 1L;
        }
        else {
            final int n4 = n - 1970;
            if (n4 >= 0 && n4 < BaseCalendar.FIXED_DATES.length) {
                final long n5 = BaseCalendar.FIXED_DATES[n4];
                if (date != null) {
                    date.setCache(n, n5, this.isLeapYear(n) ? 366 : 365);
                }
                return b ? n5 : (n5 + this.getDayOfYear(n, n2, n3) - 1L);
            }
            final long n6 = n - 1L;
            final long n7 = n3;
            long n8;
            if (n6 >= 0L) {
                n8 = n7 + (365L * n6 + n6 / 4L - n6 / 100L + n6 / 400L + (367 * n2 - 362) / 12);
            }
            else {
                n8 = n7 + (365L * n6 + CalendarUtils.floorDivide(n6, 4L) - CalendarUtils.floorDivide(n6, 100L) + CalendarUtils.floorDivide(n6, 400L) + CalendarUtils.floorDivide(367 * n2 - 362, 12));
            }
            if (n2 > 2) {
                n8 -= (this.isLeapYear(n) ? 1L : 2L);
            }
            if (date != null && b) {
                date.setCache(n, n8, this.isLeapYear(n) ? 366 : 365);
            }
            return n8;
        }
    }
    
    public void getCalendarDateFromFixedDate(final CalendarDate calendarDate, final long n) {
        final Date date = (Date)calendarDate;
        int normalizedYear;
        long n2;
        boolean leapYear;
        if (date.hit(n)) {
            normalizedYear = date.getCachedYear();
            n2 = date.getCachedJan1();
            leapYear = this.isLeapYear(normalizedYear);
        }
        else {
            normalizedYear = this.getGregorianYearFromFixedDate(n);
            n2 = this.getFixedDate(normalizedYear, 1, 1, null);
            leapYear = this.isLeapYear(normalizedYear);
            date.setCache(normalizedYear, n2, leapYear ? 366 : 365);
        }
        int n3 = (int)(n - n2);
        long n4 = n2 + 31L + 28L;
        if (leapYear) {
            ++n4;
        }
        if (n >= n4) {
            n3 += (leapYear ? 1 : 2);
        }
        final int n5 = 12 * n3 + 373;
        int floorDivide;
        if (n5 > 0) {
            floorDivide = n5 / 367;
        }
        else {
            floorDivide = CalendarUtils.floorDivide(n5, 367);
        }
        long n6 = n2 + BaseCalendar.ACCUMULATED_DAYS_IN_MONTH[floorDivide];
        if (leapYear && floorDivide >= 3) {
            ++n6;
        }
        final int dayOfMonth = (int)(n - n6) + 1;
        final int dayOfWeekFromFixedDate = getDayOfWeekFromFixedDate(n);
        assert dayOfWeekFromFixedDate > 0 : "negative day of week " + dayOfWeekFromFixedDate;
        date.setNormalizedYear(normalizedYear);
        date.setMonth(floorDivide);
        date.setDayOfMonth(dayOfMonth);
        date.setDayOfWeek(dayOfWeekFromFixedDate);
        date.setLeapYear(leapYear);
        date.setNormalized(true);
    }
    
    public int getDayOfWeek(final CalendarDate calendarDate) {
        return getDayOfWeekFromFixedDate(this.getFixedDate(calendarDate));
    }
    
    public static final int getDayOfWeekFromFixedDate(final long n) {
        if (n >= 0L) {
            return (int)(n % 7L) + 1;
        }
        return (int)CalendarUtils.mod(n, 7L) + 1;
    }
    
    public int getYearFromFixedDate(final long n) {
        return this.getGregorianYearFromFixedDate(n);
    }
    
    final int getGregorianYearFromFixedDate(final long n) {
        int n3;
        int floorDivide;
        int floorDivide2;
        int floorDivide3;
        if (n > 0L) {
            final long n2 = n - 1L;
            n3 = (int)(n2 / 146097L);
            final int n4 = (int)(n2 % 146097L);
            floorDivide = n4 / 36524;
            final int n5 = n4 % 36524;
            floorDivide2 = n5 / 1461;
            floorDivide3 = n5 % 1461 / 365;
        }
        else {
            final long n6 = n - 1L;
            n3 = (int)CalendarUtils.floorDivide(n6, 146097L);
            final int n7 = (int)CalendarUtils.mod(n6, 146097L);
            floorDivide = CalendarUtils.floorDivide(n7, 36524);
            final int mod = CalendarUtils.mod(n7, 36524);
            floorDivide2 = CalendarUtils.floorDivide(mod, 1461);
            final int mod2 = CalendarUtils.mod(mod, 1461);
            floorDivide3 = CalendarUtils.floorDivide(mod2, 365);
            final int n8 = CalendarUtils.mod(mod2, 365) + 1;
        }
        int n9 = 400 * n3 + 100 * floorDivide + 4 * floorDivide2 + floorDivide3;
        if (floorDivide != 4 && floorDivide3 != 4) {
            ++n9;
        }
        return n9;
    }
    
    @Override
    protected boolean isLeapYear(final CalendarDate calendarDate) {
        return this.isLeapYear(((Date)calendarDate).getNormalizedYear());
    }
    
    boolean isLeapYear(final int n) {
        return CalendarUtils.isGregorianLeapYear(n);
    }
    
    static {
        FIXED_DATES = new int[] { 719163, 719528, 719893, 720259, 720624, 720989, 721354, 721720, 722085, 722450, 722815, 723181, 723546, 723911, 724276, 724642, 725007, 725372, 725737, 726103, 726468, 726833, 727198, 727564, 727929, 728294, 728659, 729025, 729390, 729755, 730120, 730486, 730851, 731216, 731581, 731947, 732312, 732677, 733042, 733408, 733773, 734138, 734503, 734869, 735234, 735599, 735964, 736330, 736695, 737060, 737425, 737791, 738156, 738521, 738886, 739252, 739617, 739982, 740347, 740713, 741078, 741443, 741808, 742174, 742539, 742904, 743269, 743635, 744000, 744365 };
        DAYS_IN_MONTH = new int[] { 31, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        ACCUMULATED_DAYS_IN_MONTH = new int[] { -30, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334 };
        ACCUMULATED_DAYS_IN_MONTH_LEAP = new int[] { -30, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335 };
    }
    
    public abstract static class Date extends CalendarDate
    {
        int cachedYear;
        long cachedFixedDateJan1;
        long cachedFixedDateNextJan1;
        
        protected Date() {
            this.cachedYear = 2004;
            this.cachedFixedDateJan1 = 731581L;
            this.cachedFixedDateNextJan1 = this.cachedFixedDateJan1 + 366L;
        }
        
        protected Date(final TimeZone timeZone) {
            super(timeZone);
            this.cachedYear = 2004;
            this.cachedFixedDateJan1 = 731581L;
            this.cachedFixedDateNextJan1 = this.cachedFixedDateJan1 + 366L;
        }
        
        public Date setNormalizedDate(final int normalizedYear, final int month, final int dayOfMonth) {
            this.setNormalizedYear(normalizedYear);
            this.setMonth(month).setDayOfMonth(dayOfMonth);
            return this;
        }
        
        public abstract int getNormalizedYear();
        
        public abstract void setNormalizedYear(final int p0);
        
        protected final boolean hit(final int n) {
            return n == this.cachedYear;
        }
        
        protected final boolean hit(final long n) {
            return n >= this.cachedFixedDateJan1 && n < this.cachedFixedDateNextJan1;
        }
        
        protected int getCachedYear() {
            return this.cachedYear;
        }
        
        protected long getCachedJan1() {
            return this.cachedFixedDateJan1;
        }
        
        protected void setCache(final int cachedYear, final long cachedFixedDateJan1, final int n) {
            this.cachedYear = cachedYear;
            this.cachedFixedDateJan1 = cachedFixedDateJan1;
            this.cachedFixedDateNextJan1 = cachedFixedDateJan1 + n;
        }
    }
}
