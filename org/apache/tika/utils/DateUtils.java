package org.apache.tika.utils;

import java.util.Iterator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;
import java.util.Locale;
import java.text.DateFormat;
import java.util.List;
import java.util.TimeZone;

public class DateUtils
{
    public static final TimeZone UTC;
    public static final TimeZone MIDDAY;
    private final List<DateFormat> iso8601InputFormats;
    
    public DateUtils() {
        this.iso8601InputFormats = this.loadDateFormats();
    }
    
    private static DateFormat createDateFormat(final String format, final TimeZone timezone) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format, new DateFormatSymbols(Locale.US));
        if (timezone != null) {
            sdf.setTimeZone(timezone);
        }
        return sdf;
    }
    
    public static String formatDate(final Date date) {
        final Calendar calendar = Calendar.getInstance(DateUtils.UTC, Locale.US);
        calendar.setTime(date);
        return doFormatDate(calendar);
    }
    
    public static String formatDate(final Calendar date) {
        date.setTimeZone(DateUtils.UTC);
        return doFormatDate(date);
    }
    
    public static String formatDateUnknownTimezone(final Date date) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.US);
        calendar.setTime(date);
        final String formatted = formatDate(calendar);
        return formatted.substring(0, formatted.length() - 1);
    }
    
    private static String doFormatDate(final Calendar calendar) {
        return String.format(Locale.ROOT, "%04d-%02d-%02dT%02d:%02d:%02dZ", calendar.get(1), calendar.get(2) + 1, calendar.get(5), calendar.get(11), calendar.get(12), calendar.get(13));
    }
    
    private List<DateFormat> loadDateFormats() {
        final List<DateFormat> dateFormats = new ArrayList<DateFormat>();
        dateFormats.add(createDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", DateUtils.UTC));
        dateFormats.add(createDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", null));
        dateFormats.add(createDateFormat("yyyy-MM-dd'T'HH:mm:ss", null));
        dateFormats.add(createDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'", DateUtils.UTC));
        dateFormats.add(createDateFormat("yyyy-MM-dd' 'HH:mm:ssZ", null));
        dateFormats.add(createDateFormat("yyyy-MM-dd' 'HH:mm:ss", null));
        dateFormats.add(createDateFormat("yyyy-MM-dd", DateUtils.MIDDAY));
        dateFormats.add(createDateFormat("yyyy:MM:dd", DateUtils.MIDDAY));
        return dateFormats;
    }
    
    public Date tryToParse(String dateString) {
        final int n = dateString.length();
        if (dateString.charAt(n - 3) == ':' && (dateString.charAt(n - 6) == '+' || dateString.charAt(n - 6) == '-')) {
            dateString = dateString.substring(0, n - 3) + dateString.substring(n - 2);
        }
        for (final DateFormat df : this.iso8601InputFormats) {
            try {
                return df.parse(dateString);
            }
            catch (final ParseException ex) {
                continue;
            }
            break;
        }
        return null;
    }
    
    static {
        UTC = TimeZone.getTimeZone("UTC");
        MIDDAY = TimeZone.getTimeZone("GMT-12:00");
    }
}
