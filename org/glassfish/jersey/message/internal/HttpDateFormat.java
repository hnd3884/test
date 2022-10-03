package org.glassfish.jersey.message.internal;

import java.util.Iterator;
import java.text.ParseException;
import java.util.Date;
import java.util.Collections;
import java.util.Arrays;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public final class HttpDateFormat
{
    private static final String RFC1123_DATE_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String RFC1036_DATE_FORMAT_PATTERN = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String ANSI_C_ASCTIME_DATE_FORMAT_PATTERN = "EEE MMM d HH:mm:ss yyyy";
    private static final TimeZone GMT_TIME_ZONE;
    private static final ThreadLocal<List<SimpleDateFormat>> dateFormats;
    
    private HttpDateFormat() {
    }
    
    private static List<SimpleDateFormat> createDateFormats() {
        final SimpleDateFormat[] formats = { new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US), new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy", Locale.US) };
        formats[0].setTimeZone(HttpDateFormat.GMT_TIME_ZONE);
        formats[1].setTimeZone(HttpDateFormat.GMT_TIME_ZONE);
        formats[2].setTimeZone(HttpDateFormat.GMT_TIME_ZONE);
        return Collections.unmodifiableList((List<? extends SimpleDateFormat>)Arrays.asList((T[])formats));
    }
    
    private static List<SimpleDateFormat> getDateFormats() {
        return HttpDateFormat.dateFormats.get();
    }
    
    public static SimpleDateFormat getPreferredDateFormat() {
        return (SimpleDateFormat)HttpDateFormat.dateFormats.get().get(0).clone();
    }
    
    public static Date readDate(final String date) throws ParseException {
        ParseException pe = null;
        for (final SimpleDateFormat f : getDateFormats()) {
            try {
                final Date result = f.parse(date);
                f.setTimeZone(HttpDateFormat.GMT_TIME_ZONE);
                return result;
            }
            catch (final ParseException e) {
                pe = ((pe == null) ? e : pe);
                continue;
            }
            break;
        }
        throw pe;
    }
    
    static {
        GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");
        dateFormats = new ThreadLocal<List<SimpleDateFormat>>() {
            @Override
            protected synchronized List<SimpleDateFormat> initialValue() {
                return createDateFormats();
            }
        };
    }
}
