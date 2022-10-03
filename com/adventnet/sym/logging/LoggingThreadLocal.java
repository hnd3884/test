package com.adventnet.sym.logging;

import java.util.UUID;

public class LoggingThreadLocal
{
    static ThreadLocal<String> loggingId;
    
    public static void setLoggingId(final String logId) {
        LoggingThreadLocal.loggingId.set(logId);
    }
    
    public static String getLoggingId() {
        return LoggingThreadLocal.loggingId.get();
    }
    
    public static void clearLoggingId() {
        LoggingThreadLocal.loggingId.remove();
    }
    
    static {
        LoggingThreadLocal.loggingId = new ThreadLocal<String>() {
            @Override
            protected String initialValue() {
                return String.valueOf("SERVER-" + UUID.randomUUID());
            }
        };
    }
}
