package com.zoho.security.dos;

import java.util.List;
import java.util.regex.Matcher;
import java.util.concurrent.TimeUnit;
import com.adventnet.iam.security.SecurityUtil;
import java.util.Date;
import java.util.Calendar;

public class Util
{
    public static Date getFloorSeconds(final Calendar calendar) {
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public static Date getFloorMinutes(final Calendar calendar) {
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public static Date getFloorHours(final Calendar calendar) {
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public static Date getFloorDay(final Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }
    
    public static long getTimeInMillis(final String timeStr) {
        if (!SecurityUtil.isValid(timeStr)) {
            return -1L;
        }
        long timeInMillis = 0L;
        final String[] times = timeStr.split(":");
        for (int i = 0; i < times.length; ++i) {
            final int time = (int)convertTimeStringToMillis(times[i]);
            if (time == -1) {
                return -1L;
            }
            timeInMillis += time;
        }
        return timeInMillis;
    }
    
    private static long convertTimeStringToMillis(final String timeStr) {
        TIME_UNIT unit = TIME_UNIT.M;
        int val = 0;
        final Matcher matcher = SecurityUtil.SINGLE_TIME_PATTERN.matcher(timeStr);
        if (matcher.matches()) {
            val = Integer.parseInt(matcher.group(1));
            final TimeUnit tu = TimeUnit.MILLISECONDS;
            switch (unit = ((matcher.group(2) != null) ? TIME_UNIT.valueOf(matcher.group(2).toUpperCase()) : unit)) {
                case MS: {
                    return val;
                }
                case S: {
                    return tu.convert(val, TimeUnit.SECONDS);
                }
                case M: {
                    return tu.convert(val, TimeUnit.MINUTES);
                }
                case H: {
                    return tu.convert(val, TimeUnit.HOURS);
                }
                case D: {
                    return tu.convert(val, TimeUnit.DAYS);
                }
            }
        }
        return -1L;
    }
    
    public static long getFloorTimeInMillis(final long throttleDuration, final Calendar currentCalendar) {
        if (throttleDuration >= TimeUnitInMillis.ONE_DAY.getTime()) {
            return getFloorDay(currentCalendar).getTime();
        }
        if (throttleDuration >= TimeUnitInMillis.ONE_HOUR.getTime()) {
            return getFloorHours(currentCalendar).getTime();
        }
        if (throttleDuration >= TimeUnitInMillis.ONE_MINUTE.getTime()) {
            return getFloorMinutes(currentCalendar).getTime();
        }
        return getFloorSeconds(currentCalendar).getTime();
    }
    
    public static String listJoiner(final List<Object> list, final String joiner) {
        if (list.size() > 1) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size() - 1; ++i) {
                builder.append(list.get(i));
                builder.append(joiner);
            }
            return builder.append(list.get(list.size() - 1)).toString();
        }
        return String.valueOf(list.get(0));
    }
    
    public static boolean isValidString(final String string) {
        return string != null && !"".equals(string);
    }
    
    public enum TIME_UNIT
    {
        D, 
        H, 
        M, 
        S, 
        MS;
    }
    
    public enum TimeUnitInMillis
    {
        ONE_SECOND(1000L), 
        ONE_MINUTE(60L * TimeUnitInMillis.ONE_SECOND.getTime()), 
        ONE_HOUR(60L * TimeUnitInMillis.ONE_MINUTE.getTime()), 
        ONE_DAY(24L * TimeUnitInMillis.ONE_HOUR.getTime());
        
        private long time;
        
        private TimeUnitInMillis(final long time) {
            this.time = -1L;
            this.time = time;
        }
        
        public long getTime() {
            return this.time;
        }
    }
}
