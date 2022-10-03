package jdk.management.jfr;

import java.lang.management.ManagementPermission;
import java.time.format.DateTimeParseException;
import java.time.DateTimeException;
import java.time.Instant;
import jdk.jfr.internal.management.ManagementSupport;
import java.time.Duration;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.List;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.security.Permission;

final class MBeanUtils
{
    private static final Permission monitor;
    private static final Permission control;
    
    static ObjectName createObjectName() {
        try {
            return new ObjectName("jdk.management.jfr:type=FlightRecorder");
        }
        catch (final MalformedObjectNameException ex) {
            throw new Error("Can't happen", ex);
        }
    }
    
    static void checkControl() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(MBeanUtils.control);
        }
    }
    
    static void checkMonitor() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(MBeanUtils.monitor);
        }
    }
    
    static <T, R> List<R> transformList(final List<T> list, final Function<T, R> function) {
        return list.stream().map((Function<? super Object, ?>)function).collect((Collector<? super Object, ?, List<R>>)Collectors.toList());
    }
    
    static boolean booleanValue(final String s) {
        if ("true".equals(s)) {
            return true;
        }
        if ("false".equals(s)) {
            return false;
        }
        throw new IllegalArgumentException("Value must be true or false.");
    }
    
    static Duration duration(final String s) throws NumberFormatException {
        if (s == null) {
            return null;
        }
        final long timespan = ManagementSupport.parseTimespan(s);
        if (timespan == 0L) {
            return null;
        }
        return Duration.ofNanos(timespan);
    }
    
    public static Instant parseTimestamp(final String s, final Instant instant) {
        if (s == null) {
            return instant;
        }
        try {
            return Instant.parse(s);
        }
        catch (final DateTimeParseException ex) {
            try {
                return Instant.ofEpochMilli(Long.parseLong(s));
            }
            catch (final NumberFormatException | DateTimeException ex2) {
                throw new IllegalArgumentException("Not a valid timestamp " + s);
            }
        }
    }
    
    static Long size(final String s) throws NumberFormatException {
        final long long1 = Long.parseLong(s);
        if (long1 < 0L) {
            throw new IllegalArgumentException("Negative size not allowed");
        }
        return long1;
    }
    
    public static int parseBlockSize(final String s, final int n) {
        if (s == null) {
            return n;
        }
        final int int1 = Integer.parseInt(s);
        if (int1 < 1) {
            throw new IllegalArgumentException("Block size must be at least 1 byte");
        }
        return int1;
    }
    
    static {
        monitor = new ManagementPermission("monitor");
        control = new ManagementPermission("control");
    }
}
