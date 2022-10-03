package com.zoho.mickey;

import java.util.StringJoiner;

public class ExceptionUtils
{
    public static String getStackTraceException() {
        return getStackTrace(new Exception("StackTrace"));
    }
    
    public static String getStackTraceForCurrentThread() {
        return getStackTrace(Thread.currentThread().getStackTrace());
    }
    
    public static String getStackTrace(final Exception exception) {
        if (exception == null) {
            return "";
        }
        return String.join(System.lineSeparator() + "\t at ", exception.toString(), getStackTrace(exception.getStackTrace()));
    }
    
    public static String getStackTrace(final StackTraceElement[] stackTraceElements) {
        if (stackTraceElements == null || stackTraceElements.length == 0) {
            return "";
        }
        final StringJoiner stringJoiner = new StringJoiner(System.lineSeparator() + "\t at ");
        for (final StackTraceElement element : stackTraceElements) {
            stringJoiner.add(element.toString());
        }
        return stringJoiner.toString();
    }
    
    public static String getStackTrace(final Thread thread) {
        return getStackTrace(thread.getStackTrace());
    }
}
