package org.apache.commons.httpclient.util;

import java.util.Arrays;
import java.util.Iterator;
import java.text.ParseException;
import java.util.TimeZone;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;
import java.util.Collection;

public class DateParser
{
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    private static final Collection DEFAULT_PATTERNS;
    
    public static Date parseDate(final String dateValue) throws DateParseException {
        return parseDate(dateValue, null);
    }
    
    public static Date parseDate(String dateValue, Collection dateFormats) throws DateParseException {
        if (dateValue == null) {
            throw new IllegalArgumentException("dateValue is null");
        }
        if (dateFormats == null) {
            dateFormats = DateParser.DEFAULT_PATTERNS;
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
    
    private DateParser() {
    }
    
    static {
        DEFAULT_PATTERNS = Arrays.asList("EEE MMM d HH:mm:ss yyyy", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE, dd MMM yyyy HH:mm:ss zzz");
    }
}
