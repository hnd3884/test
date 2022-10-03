package org.apache.poi.util;

import java.util.Calendar;
import java.util.Locale;
import java.nio.charset.Charset;
import java.util.TimeZone;

public final class LocaleUtil
{
    public static final TimeZone TIMEZONE_UTC;
    public static final Charset CHARSET_1252;
    private static final ThreadLocal<TimeZone> userTimeZone;
    private static final ThreadLocal<Locale> userLocale;
    
    private LocaleUtil() {
    }
    
    public static void setUserTimeZone(final TimeZone timezone) {
        LocaleUtil.userTimeZone.set(timezone);
    }
    
    @SuppressForbidden("implementation around default locales in POI")
    public static TimeZone getUserTimeZone() {
        final TimeZone timeZone = LocaleUtil.userTimeZone.get();
        return (timeZone != null) ? timeZone : TimeZone.getDefault();
    }
    
    public static void resetUserTimeZone() {
        LocaleUtil.userTimeZone.remove();
    }
    
    public static void setUserLocale(final Locale locale) {
        LocaleUtil.userLocale.set(locale);
    }
    
    @SuppressForbidden("implementation around default locales in POI")
    public static Locale getUserLocale() {
        final Locale locale = LocaleUtil.userLocale.get();
        return (locale != null) ? locale : Locale.getDefault();
    }
    
    public static void resetUserLocale() {
        LocaleUtil.userLocale.remove();
    }
    
    public static Calendar getLocaleCalendar() {
        return getLocaleCalendar(getUserTimeZone());
    }
    
    public static Calendar getLocaleCalendar(final int year, final int month, final int day) {
        return getLocaleCalendar(year, month, day, 0, 0, 0);
    }
    
    public static Calendar getLocaleCalendar(final int year, final int month, final int day, final int hour, final int minute, final int second) {
        final Calendar cal = getLocaleCalendar();
        cal.set(year, month, day, hour, minute, second);
        cal.clear(14);
        return cal;
    }
    
    public static Calendar getLocaleCalendar(final TimeZone timeZone) {
        return Calendar.getInstance(timeZone, getUserLocale());
    }
    
    public static String getLocaleFromLCID(final int lcid) {
        final LocaleID lid = LocaleID.lookupByLcid(lcid & 0xFFFF);
        return (lid == null) ? "invalid" : lid.getLanguageTag();
    }
    
    public static int getDefaultCodePageFromLCID(final int lcid) {
        final LocaleID lid = LocaleID.lookupByLcid(lcid & 0xFFFF);
        return (lid == null) ? 0 : lid.getDefaultCodepage();
    }
    
    static {
        TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
        CHARSET_1252 = Charset.forName("CP1252");
        userTimeZone = new ThreadLocal<TimeZone>();
        userLocale = new ThreadLocal<Locale>();
    }
}
