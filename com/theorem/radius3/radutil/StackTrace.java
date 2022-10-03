package com.theorem.radius3.radutil;

public class StackTrace
{
    public static String getStackTrace(final Throwable t) {
        Throwable cause = t;
        final StringBuffer sb = new StringBuffer();
        int n = 1;
        do {
            if (n == 1) {
                sb.append(cause.getClass().getName()).append(' ');
                n = 0;
            }
            else {
                sb.append("Caused by ").append(cause.getClass().getName());
            }
            final String message = cause.getMessage();
            if (message != null) {
                sb.append(": ").append(message);
            }
            else {
                sb.append(" No message");
            }
            sb.append('\n');
            final StackTraceElement[] stackTrace = cause.getStackTrace();
            for (int i = 0; i < stackTrace.length; ++i) {
                final StackTraceElement stackTraceElement = stackTrace[i];
                sb.append("      at ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(")");
                if (stackTraceElement.isNativeMethod()) {
                    sb.append(" -  Native method");
                }
                sb.append("\n");
            }
        } while ((cause = cause.getCause()) != null);
        return sb.toString();
    }
    
    public static String getStackTrace() {
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        final StringBuffer sb = new StringBuffer();
        for (int i = 1; i < stackTrace.length; ++i) {
            final StackTraceElement stackTraceElement = stackTrace[i];
            sb.append("      at ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(")");
            if (stackTraceElement.isNativeMethod()) {
                sb.append(" -  Native method");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public static boolean isRecursing(final String s) {
        final String stackTrace = getStackTrace();
        final int index = stackTrace.indexOf(s);
        return index >= 0 && stackTrace.indexOf(s, index + s.length()) >= 0;
    }
    
    public final String getFrom(final int n) {
        final Throwable t = new Throwable();
        final int n2 = n + 2;
        final StackTraceElement[] stackTrace = t.getStackTrace();
        final StringBuffer sb = new StringBuffer();
        final StackTraceElement stackTraceElement = stackTrace[n2];
        sb.append("From ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("(").append(stackTraceElement.getFileName()).append(":").append(stackTraceElement.getLineNumber()).append(")");
        if (stackTraceElement.isNativeMethod()) {
            sb.append(" -  Native method");
        }
        return sb.toString();
    }
    
    public final String toString() {
        return getStackTrace();
    }
    
    public static final void main(final String[] array) {
        try {
            final StackTrace stackTrace = new StackTrace();
            System.out.println(getStackTrace(new Exception("can't catch fish")));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
