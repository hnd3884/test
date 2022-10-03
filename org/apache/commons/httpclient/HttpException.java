package org.apache.commons.httpclient;

import java.io.PrintWriter;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.io.IOException;

public class HttpException extends IOException
{
    private String reason;
    private int reasonCode;
    private final Throwable cause;
    
    public HttpException() {
        this.reasonCode = 200;
        this.cause = null;
    }
    
    public HttpException(final String message) {
        super(message);
        this.reasonCode = 200;
        this.cause = null;
    }
    
    public HttpException(final String message, final Throwable cause) {
        super(message);
        this.reasonCode = 200;
        this.cause = cause;
        try {
            final Class[] paramsClasses = { Throwable.class };
            final Method initCause = Throwable.class.getMethod("initCause", (Class[])paramsClasses);
            initCause.invoke(this, cause);
        }
        catch (final Exception ex) {}
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    public void printStackTrace(final PrintStream s) {
        try {
            final Class[] paramsClasses = new Class[0];
            this.getClass().getMethod("getStackTrace", (Class<?>[])paramsClasses);
            super.printStackTrace(s);
        }
        catch (final Exception ex) {
            super.printStackTrace(s);
            if (this.cause != null) {
                s.print("Caused by: ");
                this.cause.printStackTrace(s);
            }
        }
    }
    
    public void printStackTrace(final PrintWriter s) {
        try {
            final Class[] paramsClasses = new Class[0];
            this.getClass().getMethod("getStackTrace", (Class<?>[])paramsClasses);
            super.printStackTrace(s);
        }
        catch (final Exception ex) {
            super.printStackTrace(s);
            if (this.cause != null) {
                s.print("Caused by: ");
                this.cause.printStackTrace(s);
            }
        }
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReasonCode(final int code) {
        this.reasonCode = code;
    }
    
    public int getReasonCode() {
        return this.reasonCode;
    }
}
