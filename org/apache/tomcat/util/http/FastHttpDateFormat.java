package org.apache.tomcat.util.http;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.text.ParseException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public final class FastHttpDateFormat
{
    private static final int CACHE_SIZE;
    @Deprecated
    public static final String RFC1123_DATE = "EEE, dd MMM yyyy HH:mm:ss zzz";
    private static final String DATE_RFC5322 = "EEE, dd MMM yyyy HH:mm:ss z";
    private static final String DATE_OBSOLETE_RFC850 = "EEEEEE, dd-MMM-yy HH:mm:ss zzz";
    private static final String DATE_OBSOLETE_ASCTIME = "EEE MMMM d HH:mm:ss yyyy";
    private static final ConcurrentDateFormat FORMAT_RFC5322;
    private static final ConcurrentDateFormat FORMAT_OBSOLETE_RFC850;
    private static final ConcurrentDateFormat FORMAT_OBSOLETE_ASCTIME;
    private static final ConcurrentDateFormat[] httpParseFormats;
    private static volatile long currentDateGenerated;
    private static String currentDate;
    private static final Map<Long, String> formatCache;
    private static final Map<String, Long> parseCache;
    
    public static final String getCurrentDate() {
        final long now = System.currentTimeMillis();
        if (now - FastHttpDateFormat.currentDateGenerated > 1000L) {
            FastHttpDateFormat.currentDate = FastHttpDateFormat.FORMAT_RFC5322.format(new Date(now));
            FastHttpDateFormat.currentDateGenerated = now;
        }
        return FastHttpDateFormat.currentDate;
    }
    
    @Deprecated
    public static final String formatDate(final long value, final DateFormat threadLocalformat) {
        return formatDate(value);
    }
    
    public static final String formatDate(final long value) {
        final Long longValue = value;
        final String cachedDate = FastHttpDateFormat.formatCache.get(longValue);
        if (cachedDate != null) {
            return cachedDate;
        }
        final String newDate = FastHttpDateFormat.FORMAT_RFC5322.format(new Date(value));
        updateFormatCache(longValue, newDate);
        return newDate;
    }
    
    @Deprecated
    public static final long parseDate(final String value, final DateFormat[] threadLocalformats) {
        return parseDate(value);
    }
    
    public static final long parseDate(final String value) {
        final Long cachedDate = FastHttpDateFormat.parseCache.get(value);
        if (cachedDate != null) {
            return cachedDate;
        }
        long date = -1L;
        for (int i = 0; date == -1L && i < FastHttpDateFormat.httpParseFormats.length; ++i) {
            try {
                date = FastHttpDateFormat.httpParseFormats[i].parse(value).getTime();
                updateParseCache(value, date);
            }
            catch (final ParseException ex) {}
        }
        return date;
    }
    
    private static void updateFormatCache(final Long key, final String value) {
        if (value == null) {
            return;
        }
        if (FastHttpDateFormat.formatCache.size() > FastHttpDateFormat.CACHE_SIZE) {
            FastHttpDateFormat.formatCache.clear();
        }
        FastHttpDateFormat.formatCache.put(key, value);
    }
    
    private static void updateParseCache(final String key, final Long value) {
        if (value == null) {
            return;
        }
        if (FastHttpDateFormat.parseCache.size() > FastHttpDateFormat.CACHE_SIZE) {
            FastHttpDateFormat.parseCache.clear();
        }
        FastHttpDateFormat.parseCache.put(key, value);
    }
    
    static {
        CACHE_SIZE = Integer.parseInt(System.getProperty("org.apache.tomcat.util.http.FastHttpDateFormat.CACHE_SIZE", "1000"));
        final TimeZone tz = TimeZone.getTimeZone("GMT");
        FORMAT_RFC5322 = new ConcurrentDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US, tz);
        FORMAT_OBSOLETE_RFC850 = new ConcurrentDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss zzz", Locale.US, tz);
        FORMAT_OBSOLETE_ASCTIME = new ConcurrentDateFormat("EEE MMMM d HH:mm:ss yyyy", Locale.US, tz);
        httpParseFormats = new ConcurrentDateFormat[] { FastHttpDateFormat.FORMAT_RFC5322, FastHttpDateFormat.FORMAT_OBSOLETE_RFC850, FastHttpDateFormat.FORMAT_OBSOLETE_ASCTIME };
        FastHttpDateFormat.currentDateGenerated = 0L;
        FastHttpDateFormat.currentDate = null;
        formatCache = new ConcurrentHashMap<Long, String>(FastHttpDateFormat.CACHE_SIZE);
        parseCache = new ConcurrentHashMap<String, Long>(FastHttpDateFormat.CACHE_SIZE);
    }
}
