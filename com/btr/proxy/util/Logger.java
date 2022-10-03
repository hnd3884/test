package com.btr.proxy.util;

public class Logger
{
    private static LogBackEnd backend;
    
    public static LogBackEnd getBackend() {
        return Logger.backend;
    }
    
    public static void setBackend(final LogBackEnd backend) {
        Logger.backend = backend;
    }
    
    public static void log(final Class<?> clazz, final LogLevel loglevel, final String msg, final Object... params) {
        if (Logger.backend != null) {
            Logger.backend.log(clazz, loglevel, msg, params);
        }
    }
    
    public static boolean isLogginEnabled(final LogLevel logLevel) {
        return Logger.backend != null && Logger.backend.isLogginEnabled(logLevel);
    }
    
    public enum LogLevel
    {
        ERROR, 
        WARNING, 
        INFO, 
        TRACE, 
        DEBUG;
    }
    
    public interface LogBackEnd
    {
        void log(final Class<?> p0, final LogLevel p1, final String p2, final Object... p3);
        
        boolean isLogginEnabled(final LogLevel p0);
    }
}
