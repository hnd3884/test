package sun.util.calendar;

import java.util.TimeZone;
import java.util.Locale;

class ImmutableGregorianDate extends BaseCalendar.Date
{
    private final BaseCalendar.Date date;
    
    ImmutableGregorianDate(final BaseCalendar.Date date) {
        if (date == null) {
            throw new NullPointerException();
        }
        this.date = date;
    }
    
    @Override
    public Era getEra() {
        return this.date.getEra();
    }
    
    @Override
    public CalendarDate setEra(final Era era) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getYear() {
        return this.date.getYear();
    }
    
    @Override
    public CalendarDate setYear(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addYear(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public boolean isLeapYear() {
        return this.date.isLeapYear();
    }
    
    @Override
    void setLeapYear(final boolean b) {
        this.unsupported();
    }
    
    @Override
    public int getMonth() {
        return this.date.getMonth();
    }
    
    @Override
    public CalendarDate setMonth(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addMonth(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getDayOfMonth() {
        return this.date.getDayOfMonth();
    }
    
    @Override
    public CalendarDate setDayOfMonth(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addDayOfMonth(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getDayOfWeek() {
        return this.date.getDayOfWeek();
    }
    
    @Override
    public int getHours() {
        return this.date.getHours();
    }
    
    @Override
    public CalendarDate setHours(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addHours(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getMinutes() {
        return this.date.getMinutes();
    }
    
    @Override
    public CalendarDate setMinutes(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addMinutes(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getSeconds() {
        return this.date.getSeconds();
    }
    
    @Override
    public CalendarDate setSeconds(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addSeconds(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public int getMillis() {
        return this.date.getMillis();
    }
    
    @Override
    public CalendarDate setMillis(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addMillis(final int n) {
        this.unsupported();
        return this;
    }
    
    @Override
    public long getTimeOfDay() {
        return this.date.getTimeOfDay();
    }
    
    @Override
    public CalendarDate setDate(final int n, final int n2, final int n3) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addDate(final int n, final int n2, final int n3) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate setTimeOfDay(final int n, final int n2, final int n3, final int n4) {
        this.unsupported();
        return this;
    }
    
    @Override
    public CalendarDate addTimeOfDay(final int n, final int n2, final int n3, final int n4) {
        this.unsupported();
        return this;
    }
    
    @Override
    protected void setTimeOfDay(final long n) {
        this.unsupported();
    }
    
    @Override
    public boolean isNormalized() {
        return this.date.isNormalized();
    }
    
    @Override
    public boolean isStandardTime() {
        return this.date.isStandardTime();
    }
    
    @Override
    public void setStandardTime(final boolean b) {
        this.unsupported();
    }
    
    @Override
    public boolean isDaylightTime() {
        return this.date.isDaylightTime();
    }
    
    @Override
    protected void setLocale(final Locale locale) {
        this.unsupported();
    }
    
    @Override
    public TimeZone getZone() {
        return this.date.getZone();
    }
    
    @Override
    public CalendarDate setZone(final TimeZone timeZone) {
        this.unsupported();
        return this;
    }
    
    @Override
    public boolean isSameDate(final CalendarDate calendarDate) {
        return calendarDate.isSameDate(calendarDate);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ImmutableGregorianDate && this.date.equals(((ImmutableGregorianDate)o).date));
    }
    
    @Override
    public int hashCode() {
        return this.date.hashCode();
    }
    
    @Override
    public Object clone() {
        return super.clone();
    }
    
    @Override
    public String toString() {
        return this.date.toString();
    }
    
    @Override
    protected void setDayOfWeek(final int n) {
        this.unsupported();
    }
    
    @Override
    protected void setNormalized(final boolean b) {
        this.unsupported();
    }
    
    @Override
    public int getZoneOffset() {
        return this.date.getZoneOffset();
    }
    
    @Override
    protected void setZoneOffset(final int n) {
        this.unsupported();
    }
    
    @Override
    public int getDaylightSaving() {
        return this.date.getDaylightSaving();
    }
    
    @Override
    protected void setDaylightSaving(final int n) {
        this.unsupported();
    }
    
    @Override
    public int getNormalizedYear() {
        return this.date.getNormalizedYear();
    }
    
    @Override
    public void setNormalizedYear(final int n) {
        this.unsupported();
    }
    
    private void unsupported() {
        throw new UnsupportedOperationException();
    }
}
