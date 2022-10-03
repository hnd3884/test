package sun.util.calendar;

import java.util.TimeZone;
import java.security.PrivilegedActionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public abstract class CalendarSystem
{
    private static volatile boolean initialized;
    private static ConcurrentMap<String, String> names;
    private static ConcurrentMap<String, CalendarSystem> calendars;
    private static final String PACKAGE_NAME = "sun.util.calendar.";
    private static final String[] namePairs;
    private static final Gregorian GREGORIAN_INSTANCE;
    
    private static void initNames() {
        final ConcurrentHashMap names = new ConcurrentHashMap();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CalendarSystem.namePairs.length; i += 2) {
            sb.setLength(0);
            names.put(CalendarSystem.namePairs[i], sb.append("sun.util.calendar.").append(CalendarSystem.namePairs[i + 1]).toString());
        }
        synchronized (CalendarSystem.class) {
            if (!CalendarSystem.initialized) {
                CalendarSystem.names = names;
                CalendarSystem.calendars = new ConcurrentHashMap<String, CalendarSystem>();
                CalendarSystem.initialized = true;
            }
        }
    }
    
    public static Gregorian getGregorianCalendar() {
        return CalendarSystem.GREGORIAN_INSTANCE;
    }
    
    public static CalendarSystem forName(final String s) {
        if ("gregorian".equals(s)) {
            return CalendarSystem.GREGORIAN_INSTANCE;
        }
        if (!CalendarSystem.initialized) {
            initNames();
        }
        final CalendarSystem calendarSystem = CalendarSystem.calendars.get(s);
        if (calendarSystem != null) {
            return calendarSystem;
        }
        final String s2 = CalendarSystem.names.get(s);
        if (s2 == null) {
            return null;
        }
        CalendarSystem localGregorianCalendar;
        if (s2.endsWith("LocalGregorianCalendar")) {
            localGregorianCalendar = LocalGregorianCalendar.getLocalGregorianCalendar(s);
        }
        else {
            try {
                localGregorianCalendar = (CalendarSystem)Class.forName(s2).newInstance();
            }
            catch (final Exception ex) {
                throw new InternalError(ex);
            }
        }
        if (localGregorianCalendar == null) {
            return null;
        }
        final CalendarSystem calendarSystem2 = CalendarSystem.calendars.putIfAbsent(s, localGregorianCalendar);
        return (calendarSystem2 == null) ? localGregorianCalendar : calendarSystem2;
    }
    
    public static Properties getCalendarProperties() throws IOException {
        Properties properties;
        try {
            properties = AccessController.doPrivileged((PrivilegedExceptionAction<Properties>)new PrivilegedExceptionAction<Properties>() {
                final /* synthetic */ String val$fname = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.home")) + File.separator + "lib" + File.separator + "calendars.properties";
                
                @Override
                public Properties run() throws IOException {
                    final Properties properties = new Properties();
                    try (final FileInputStream fileInputStream = new FileInputStream(this.val$fname)) {
                        properties.load(fileInputStream);
                    }
                    return properties;
                }
            });
        }
        catch (final PrivilegedActionException ex) {
            final Throwable cause = ex.getCause();
            if (cause instanceof IOException) {
                throw (IOException)cause;
            }
            if (cause instanceof IllegalArgumentException) {
                throw (IllegalArgumentException)cause;
            }
            throw new InternalError(cause);
        }
        return properties;
    }
    
    public abstract String getName();
    
    public abstract CalendarDate getCalendarDate();
    
    public abstract CalendarDate getCalendarDate(final long p0);
    
    public abstract CalendarDate getCalendarDate(final long p0, final CalendarDate p1);
    
    public abstract CalendarDate getCalendarDate(final long p0, final TimeZone p1);
    
    public abstract CalendarDate newCalendarDate();
    
    public abstract CalendarDate newCalendarDate(final TimeZone p0);
    
    public abstract long getTime(final CalendarDate p0);
    
    public abstract int getYearLength(final CalendarDate p0);
    
    public abstract int getYearLengthInMonths(final CalendarDate p0);
    
    public abstract int getMonthLength(final CalendarDate p0);
    
    public abstract int getWeekLength();
    
    public abstract Era getEra(final String p0);
    
    public abstract Era[] getEras();
    
    public abstract void setEra(final CalendarDate p0, final String p1);
    
    public abstract CalendarDate getNthDayOfWeek(final int p0, final int p1, final CalendarDate p2);
    
    public abstract CalendarDate setTimeOfDay(final CalendarDate p0, final int p1);
    
    public abstract boolean validate(final CalendarDate p0);
    
    public abstract boolean normalize(final CalendarDate p0);
    
    static {
        CalendarSystem.initialized = false;
        namePairs = new String[] { "gregorian", "Gregorian", "japanese", "LocalGregorianCalendar", "julian", "JulianCalendar" };
        GREGORIAN_INSTANCE = new Gregorian();
    }
}
