package sun.util.calendar;

import java.util.TimeZone;

public class Gregorian extends BaseCalendar
{
    Gregorian() {
    }
    
    @Override
    public String getName() {
        return "gregorian";
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
    
    static class Date extends BaseCalendar.Date
    {
        protected Date() {
        }
        
        protected Date(final TimeZone timeZone) {
            super(timeZone);
        }
        
        @Override
        public int getNormalizedYear() {
            return this.getYear();
        }
        
        @Override
        public void setNormalizedYear(final int year) {
            this.setYear(year);
        }
    }
}
