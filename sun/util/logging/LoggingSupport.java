package sun.util.logging;

import java.lang.reflect.Field;
import java.util.Date;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

public class LoggingSupport
{
    private static final LoggingProxy proxy;
    private static final String DEFAULT_FORMAT = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
    private static final String FORMAT_PROP_KEY = "java.util.logging.SimpleFormatter.format";
    
    private LoggingSupport() {
    }
    
    public static boolean isAvailable() {
        return LoggingSupport.proxy != null;
    }
    
    private static void ensureAvailable() {
        if (LoggingSupport.proxy == null) {
            throw new AssertionError((Object)"Should not here");
        }
    }
    
    public static List<String> getLoggerNames() {
        ensureAvailable();
        return LoggingSupport.proxy.getLoggerNames();
    }
    
    public static String getLoggerLevel(final String s) {
        ensureAvailable();
        return LoggingSupport.proxy.getLoggerLevel(s);
    }
    
    public static void setLoggerLevel(final String s, final String s2) {
        ensureAvailable();
        LoggingSupport.proxy.setLoggerLevel(s, s2);
    }
    
    public static String getParentLoggerName(final String s) {
        ensureAvailable();
        return LoggingSupport.proxy.getParentLoggerName(s);
    }
    
    public static Object getLogger(final String s) {
        ensureAvailable();
        return LoggingSupport.proxy.getLogger(s);
    }
    
    public static Object getLevel(final Object o) {
        ensureAvailable();
        return LoggingSupport.proxy.getLevel(o);
    }
    
    public static void setLevel(final Object o, final Object o2) {
        ensureAvailable();
        LoggingSupport.proxy.setLevel(o, o2);
    }
    
    public static boolean isLoggable(final Object o, final Object o2) {
        ensureAvailable();
        return LoggingSupport.proxy.isLoggable(o, o2);
    }
    
    public static void log(final Object o, final Object o2, final String s) {
        ensureAvailable();
        LoggingSupport.proxy.log(o, o2, s);
    }
    
    public static void log(final Object o, final Object o2, final String s, final Throwable t) {
        ensureAvailable();
        LoggingSupport.proxy.log(o, o2, s, t);
    }
    
    public static void log(final Object o, final Object o2, final String s, final Object... array) {
        ensureAvailable();
        LoggingSupport.proxy.log(o, o2, s, array);
    }
    
    public static Object parseLevel(final String s) {
        ensureAvailable();
        return LoggingSupport.proxy.parseLevel(s);
    }
    
    public static String getLevelName(final Object o) {
        ensureAvailable();
        return LoggingSupport.proxy.getLevelName(o);
    }
    
    public static int getLevelValue(final Object o) {
        ensureAvailable();
        return LoggingSupport.proxy.getLevelValue(o);
    }
    
    public static String getSimpleFormat() {
        return getSimpleFormat(true);
    }
    
    static String getSimpleFormat(final boolean b) {
        String property = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty("java.util.logging.SimpleFormatter.format");
            }
        });
        if (b && LoggingSupport.proxy != null && property == null) {
            property = LoggingSupport.proxy.getProperty("java.util.logging.SimpleFormatter.format");
        }
        if (property != null) {
            try {
                String.format(property, new Date(), "", "", "", "", "");
            }
            catch (final IllegalArgumentException ex) {
                property = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
            }
        }
        else {
            property = "%1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS %1$Tp %2$s%n%4$s: %5$s%6$s%n";
        }
        return property;
    }
    
    static {
        proxy = AccessController.doPrivileged((PrivilegedAction<LoggingProxy>)new PrivilegedAction<LoggingProxy>() {
            @Override
            public LoggingProxy run() {
                try {
                    final Field declaredField = Class.forName("java.util.logging.LoggingProxyImpl", true, null).getDeclaredField("INSTANCE");
                    declaredField.setAccessible(true);
                    return (LoggingProxy)declaredField.get(null);
                }
                catch (final ClassNotFoundException ex) {
                    return null;
                }
                catch (final NoSuchFieldException ex2) {
                    throw new AssertionError((Object)ex2);
                }
                catch (final IllegalAccessException ex3) {
                    throw new AssertionError((Object)ex3);
                }
            }
        });
    }
}
