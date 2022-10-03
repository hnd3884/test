package jdk.jfr.internal;

import java.util.function.Supplier;

public final class Logger
{
    private static final int MAX_SIZE = 10000;
    
    public static void log(final LogTag logTag, final LogLevel logLevel, final String s) {
        if (shouldLog(logTag, logLevel)) {
            logInternal(logTag, logLevel, s);
        }
    }
    
    public static void log(final LogTag logTag, final LogLevel logLevel, final Supplier<String> supplier) {
        if (shouldLog(logTag, logLevel)) {
            logInternal(logTag, logLevel, supplier.get());
        }
    }
    
    private static void logInternal(final LogTag logTag, final LogLevel logLevel, final String s) {
        if (s == null || s.length() < 10000) {
            JVM.log(logTag.id, logLevel.level, s);
        }
        else {
            JVM.log(logTag.id, logLevel.level, s.substring(0, 10000));
        }
    }
    
    public static boolean shouldLog(final LogTag logTag, final LogLevel logLevel) {
        return true;
    }
    
    static {
        JVMSupport.tryToInitializeJVM();
    }
}
