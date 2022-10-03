package sun.util.calendar;

import java.util.TimeZone;

public abstract class AbstractCalendar extends CalendarSystem
{
    static final int SECOND_IN_MILLIS = 1000;
    static final int MINUTE_IN_MILLIS = 60000;
    static final int HOUR_IN_MILLIS = 3600000;
    static final int DAY_IN_MILLIS = 86400000;
    static final int EPOCH_OFFSET = 719163;
    private Era[] eras;
    
    protected AbstractCalendar() {
    }
    
    @Override
    public Era getEra(final String s) {
        if (this.eras != null) {
            for (int i = 0; i < this.eras.length; ++i) {
                if (this.eras[i].equals(s)) {
                    return this.eras[i];
                }
            }
        }
        return null;
    }
    
    @Override
    public Era[] getEras() {
        Object o = null;
        if (this.eras != null) {
            o = new Era[this.eras.length];
            System.arraycopy(this.eras, 0, o, 0, this.eras.length);
        }
        return (Era[])o;
    }
    
    @Override
    public void setEra(final CalendarDate calendarDate, final String s) {
        if (this.eras == null) {
            return;
        }
        for (int i = 0; i < this.eras.length; ++i) {
            final Era era = this.eras[i];
            if (era != null && era.getName().equals(s)) {
                calendarDate.setEra(era);
                return;
            }
        }
        throw new IllegalArgumentException("unknown era name: " + s);
    }
    
    protected void setEras(final Era[] eras) {
        this.eras = eras;
    }
    
    @Override
    public CalendarDate getCalendarDate() {
        return this.getCalendarDate(System.currentTimeMillis(), this.newCalendarDate());
    }
    
    @Override
    public CalendarDate getCalendarDate(final long n) {
        return this.getCalendarDate(n, this.newCalendarDate());
    }
    
    @Override
    public CalendarDate getCalendarDate(final long n, final TimeZone timeZone) {
        return this.getCalendarDate(n, this.newCalendarDate(timeZone));
    }
    
    @Override
    public CalendarDate getCalendarDate(final long n, final CalendarDate calendarDate) {
        int n2 = 0;
        int zoneOffset = 0;
        int daylightSaving = 0;
        long n3 = 0L;
        final TimeZone zone = calendarDate.getZone();
        if (zone != null) {
            final int[] array = new int[2];
            if (zone instanceof ZoneInfo) {
                zoneOffset = ((ZoneInfo)zone).getOffsets(n, array);
            }
            else {
                zoneOffset = zone.getOffset(n);
                array[0] = zone.getRawOffset();
                array[1] = zoneOffset - array[0];
            }
            n3 = zoneOffset / 86400000;
            n2 = zoneOffset % 86400000;
            daylightSaving = array[1];
        }
        calendarDate.setZoneOffset(zoneOffset);
        calendarDate.setDaylightSaving(daylightSaving);
        long n4 = n3 + n / 86400000L;
        int i = n2 + (int)(n % 86400000L);
        if (i >= 86400000) {
            i -= 86400000;
            ++n4;
        }
        else {
            while (i < 0) {
                i += 86400000;
                --n4;
            }
        }
        this.getCalendarDateFromFixedDate(calendarDate, n4 + 719163L);
        this.setTimeOfDay(calendarDate, i);
        calendarDate.setLeapYear(this.isLeapYear(calendarDate));
        calendarDate.setNormalized(true);
        return calendarDate;
    }
    
    @Override
    public long getTime(final CalendarDate calendarDate) {
        final long n = (this.getFixedDate(calendarDate) - 719163L) * 86400000L + this.getTimeOfDay(calendarDate);
        int n2 = 0;
        final TimeZone zone = calendarDate.getZone();
        if (zone != null) {
            if (calendarDate.isNormalized()) {
                return n - calendarDate.getZoneOffset();
            }
            final int[] array = new int[2];
            if (calendarDate.isStandardTime()) {
                if (zone instanceof ZoneInfo) {
                    ((ZoneInfo)zone).getOffsetsByStandard(n, array);
                    n2 = array[0];
                }
                else {
                    n2 = zone.getOffset(n - zone.getRawOffset());
                }
            }
            else if (zone instanceof ZoneInfo) {
                n2 = ((ZoneInfo)zone).getOffsetsByWall(n, array);
            }
            else {
                n2 = zone.getOffset(n - zone.getRawOffset());
            }
        }
        final long n3 = n - n2;
        this.getCalendarDate(n3, calendarDate);
        return n3;
    }
    
    protected long getTimeOfDay(final CalendarDate calendarDate) {
        final long timeOfDay = calendarDate.getTimeOfDay();
        if (timeOfDay != Long.MIN_VALUE) {
            return timeOfDay;
        }
        final long timeOfDayValue = this.getTimeOfDayValue(calendarDate);
        calendarDate.setTimeOfDay(timeOfDayValue);
        return timeOfDayValue;
    }
    
    public long getTimeOfDayValue(final CalendarDate calendarDate) {
        return ((calendarDate.getHours() * 60L + calendarDate.getMinutes()) * 60L + calendarDate.getSeconds()) * 1000L + calendarDate.getMillis();
    }
    
    @Override
    public CalendarDate setTimeOfDay(final CalendarDate calendarDate, final int n) {
        if (n < 0) {
            throw new IllegalArgumentException();
        }
        final boolean normalized = calendarDate.isNormalized();
        final int hours = n / 3600000;
        final int n2 = n % 3600000;
        final int minutes = n2 / 60000;
        final int n3 = n2 % 60000;
        final int seconds = n3 / 1000;
        final int millis = n3 % 1000;
        calendarDate.setHours(hours);
        calendarDate.setMinutes(minutes);
        calendarDate.setSeconds(seconds);
        calendarDate.setMillis(millis);
        calendarDate.setTimeOfDay(n);
        if (hours < 24 && normalized) {
            calendarDate.setNormalized(normalized);
        }
        return calendarDate;
    }
    
    @Override
    public int getWeekLength() {
        return 7;
    }
    
    protected abstract boolean isLeapYear(final CalendarDate p0);
    
    @Override
    public CalendarDate getNthDayOfWeek(final int n, final int n2, final CalendarDate calendarDate) {
        final CalendarDate calendarDate2 = (CalendarDate)calendarDate.clone();
        this.normalize(calendarDate2);
        final long fixedDate = this.getFixedDate(calendarDate2);
        long n3;
        if (n > 0) {
            n3 = 7 * n + getDayOfWeekDateBefore(fixedDate, n2);
        }
        else {
            n3 = 7 * n + getDayOfWeekDateAfter(fixedDate, n2);
        }
        this.getCalendarDateFromFixedDate(calendarDate2, n3);
        return calendarDate2;
    }
    
    static long getDayOfWeekDateBefore(final long n, final int n2) {
        return getDayOfWeekDateOnOrBefore(n - 1L, n2);
    }
    
    static long getDayOfWeekDateAfter(final long n, final int n2) {
        return getDayOfWeekDateOnOrBefore(n + 7L, n2);
    }
    
    public static long getDayOfWeekDateOnOrBefore(final long n, final int n2) {
        final long n3 = n - (n2 - 1);
        if (n3 >= 0L) {
            return n - n3 % 7L;
        }
        return n - CalendarUtils.mod(n3, 7L);
    }
    
    protected abstract long getFixedDate(final CalendarDate p0);
    
    protected abstract void getCalendarDateFromFixedDate(final CalendarDate p0, final long p1);
    
    public boolean validateTime(final CalendarDate calendarDate) {
        final int hours = calendarDate.getHours();
        if (hours < 0 || hours >= 24) {
            return false;
        }
        final int minutes = calendarDate.getMinutes();
        if (minutes < 0 || minutes >= 60) {
            return false;
        }
        final int seconds = calendarDate.getSeconds();
        if (seconds < 0 || seconds >= 60) {
            return false;
        }
        final int millis = calendarDate.getMillis();
        return millis >= 0 && millis < 1000;
    }
    
    int normalizeTime(final CalendarDate calendarDate) {
        long timeOfDay = this.getTimeOfDay(calendarDate);
        long floorDivide = 0L;
        if (timeOfDay >= 86400000L) {
            floorDivide = timeOfDay / 86400000L;
            timeOfDay %= 86400000L;
        }
        else if (timeOfDay < 0L) {
            floorDivide = CalendarUtils.floorDivide(timeOfDay, 86400000L);
            if (floorDivide != 0L) {
                timeOfDay -= 86400000L * floorDivide;
            }
        }
        if (floorDivide != 0L) {
            calendarDate.setTimeOfDay(timeOfDay);
        }
        calendarDate.setMillis((int)(timeOfDay % 1000L));
        final long n = timeOfDay / 1000L;
        calendarDate.setSeconds((int)(n % 60L));
        final long n2 = n / 60L;
        calendarDate.setMinutes((int)(n2 % 60L));
        calendarDate.setHours((int)(n2 / 60L));
        return (int)floorDivide;
    }
}
