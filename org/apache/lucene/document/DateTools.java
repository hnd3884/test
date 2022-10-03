package org.apache.lucene.document;

import java.util.Locale;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class DateTools
{
    static final TimeZone GMT;
    private static final ThreadLocal<Calendar> TL_CAL;
    private static final ThreadLocal<SimpleDateFormat[]> TL_FORMATS;
    
    private DateTools() {
    }
    
    public static String dateToString(final Date date, final Resolution resolution) {
        return timeToString(date.getTime(), resolution);
    }
    
    public static String timeToString(final long time, final Resolution resolution) {
        final Date date = new Date(round(time, resolution));
        return DateTools.TL_FORMATS.get()[resolution.formatLen].format(date);
    }
    
    public static long stringToTime(final String dateString) throws ParseException {
        return stringToDate(dateString).getTime();
    }
    
    public static Date stringToDate(final String dateString) throws ParseException {
        try {
            return DateTools.TL_FORMATS.get()[dateString.length()].parse(dateString);
        }
        catch (final Exception e) {
            throw new ParseException("Input is not a valid date string: " + dateString, 0);
        }
    }
    
    public static Date round(final Date date, final Resolution resolution) {
        return new Date(round(date.getTime(), resolution));
    }
    
    public static long round(final long time, final Resolution resolution) {
        final Calendar calInstance = DateTools.TL_CAL.get();
        calInstance.setTimeInMillis(time);
        switch (resolution) {
            case YEAR: {
                calInstance.set(2, 0);
            }
            case MONTH: {
                calInstance.set(5, 1);
            }
            case DAY: {
                calInstance.set(11, 0);
            }
            case HOUR: {
                calInstance.set(12, 0);
            }
            case MINUTE: {
                calInstance.set(13, 0);
            }
            case SECOND: {
                calInstance.set(14, 0);
            }
            case MILLISECOND: {
                return calInstance.getTimeInMillis();
            }
            default: {
                throw new IllegalArgumentException("unknown resolution " + resolution);
            }
        }
    }
    
    static {
        GMT = TimeZone.getTimeZone("GMT");
        TL_CAL = new ThreadLocal<Calendar>() {
            @Override
            protected Calendar initialValue() {
                return Calendar.getInstance(DateTools.GMT, Locale.ROOT);
            }
        };
        TL_FORMATS = new ThreadLocal<SimpleDateFormat[]>() {
            @Override
            protected SimpleDateFormat[] initialValue() {
                final SimpleDateFormat[] arr = new SimpleDateFormat[Resolution.MILLISECOND.formatLen + 1];
                for (final Resolution resolution : Resolution.values()) {
                    arr[resolution.formatLen] = (SimpleDateFormat)resolution.format.clone();
                }
                return arr;
            }
        };
    }
    
    public enum Resolution
    {
        YEAR(4), 
        MONTH(6), 
        DAY(8), 
        HOUR(10), 
        MINUTE(12), 
        SECOND(14), 
        MILLISECOND(17);
        
        final int formatLen;
        final SimpleDateFormat format;
        
        private Resolution(final int formatLen) {
            this.formatLen = formatLen;
            (this.format = new SimpleDateFormat("yyyyMMddHHmmssSSS".substring(0, formatLen), Locale.ROOT)).setTimeZone(DateTools.GMT);
        }
        
        @Override
        public String toString() {
            return super.toString().toLowerCase(Locale.ROOT);
        }
    }
}
