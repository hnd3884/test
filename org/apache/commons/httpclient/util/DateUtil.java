package org.apache.commons.httpclient.util;

import java.util.Calendar;
import java.util.Arrays;
import java.util.Iterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;
import java.util.Collection;

public class DateUtil
{
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    private static final Collection DEFAULT_PATTERNS;
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    private static final TimeZone GMT;
    
    public static Date parseDate(final String dateValue) throws DateParseException {
        return parseDate(dateValue, null, null);
    }
    
    public static Date parseDate(final String dateValue, final Collection dateFormats) throws DateParseException {
        return parseDate(dateValue, dateFormats, null);
    }
    
    public static Date parseDate(String dateValue, Collection dateFormats, Date startDate) throws DateParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DateUtil.DEFAULT_PATTERNS;
        }
        if (startDate == null) {
            startDate = DateUtil.DEFAULT_TWO_DIGIT_YEAR_START;
        }
        if (dateValue.length() > 1 && dateValue.startsWith("'") && dateValue.endsWith("'")) {
            dateValue = dateValue.substring(1, dateValue.length() - 1);
        }
        SimpleDateFormat dateParser = null;
        final Iterator formatIter = dateFormats.iterator();
        while (formatIter.hasNext()) {
            final String format = formatIter.next();
            if (dateParser == null) {
                dateParser = new SimpleDateFormat(format, Locale.US);
                dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
                dateParser.set2DigitYearStart(startDate);
            }
            else {
                dateParser.applyPattern(format);
            }
            try {
                return dateParser.parse(dateValue);
            }
            catch (final ParseException pe) {
                continue;
            }
            break;
        }
        throw new DateParseException("Unable to parse the date " + dateValue);
    }
    
    public static String formatDate(final Date date) {
        return formatDate(date, "EEE, dd MMM yyyy HH:mm:ss zzz");
    }
    
    public static String formatDate(final Date date, final String pattern) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }
        final SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.US);
        formatter.setTimeZone(DateUtil.GMT);
        return formatter.format(date);
    }
    
    private DateUtil() {
    }
    
    static {
        DEFAULT_PATTERNS = Arrays.asList("EEE MMM d HH:mm:ss yyyy", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE, dd MMM yyyy HH:mm:ss zzz");
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2000, 0, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
        GMT = TimeZone.getTimeZone("GMT");
    }
}
