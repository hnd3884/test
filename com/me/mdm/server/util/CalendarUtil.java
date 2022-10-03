package com.me.mdm.server.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil
{
    private static CalendarUtil calendarUtil;
    
    public Date getStartTimeOfTheDay(final Long timeInMilli) {
        final Date dt = new Date(timeInMilli);
        final Calendar c = Calendar.getInstance();
        c.setTime(dt);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public Date getEndTimeOfTheDay(final Long timeInMilli) {
        Date dt = new Date(timeInMilli);
        dt = this.addDays(dt, 1);
        return this.getStartTimeOfTheDay(dt.getTime());
    }
    
    public int diffBetweenDays(Long dateInMilli1, Long dateInMilli2) {
        int diffDays = -1;
        if (dateInMilli1 > dateInMilli2) {
            final Long tempDateMilli = dateInMilli2;
            dateInMilli2 = dateInMilli1;
            dateInMilli1 = tempDateMilli;
        }
        diffDays = (int)((dateInMilli2 - dateInMilli1) / 86400000L);
        return diffDays;
    }
    
    public Date addDays(final Date baseDate, final int daysToAdd) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(baseDate);
        calendar.add(6, daysToAdd);
        return calendar.getTime();
    }
    
    public String getDateAsString(final Long timeInMilli, final DateFormat formatter) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilli);
        return formatter.format(calendar.getTime());
    }
    
    public static CalendarUtil getInstance() {
        return (CalendarUtil.calendarUtil == null) ? (CalendarUtil.calendarUtil = new CalendarUtil()) : CalendarUtil.calendarUtil;
    }
}
