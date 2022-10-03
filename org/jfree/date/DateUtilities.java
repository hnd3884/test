package org.jfree.date;

import java.util.Date;
import java.util.Calendar;

public class DateUtilities
{
    private static final Calendar CALENDAR;
    
    static {
        CALENDAR = Calendar.getInstance();
    }
    
    private DateUtilities() {
    }
    
    public static synchronized Date createDate(final int yyyy, final int month, final int day) {
        DateUtilities.CALENDAR.clear();
        DateUtilities.CALENDAR.set(yyyy, month - 1, day);
        return DateUtilities.CALENDAR.getTime();
    }
    
    public static synchronized Date createDate(final int yyyy, final int month, final int day, final int hour, final int min) {
        DateUtilities.CALENDAR.clear();
        DateUtilities.CALENDAR.set(yyyy, month - 1, day, hour, min);
        return DateUtilities.CALENDAR.getTime();
    }
}
