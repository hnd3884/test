package com.lowagie.text;

import java.io.PrintWriter;
import java.io.PrintStream;

public class ExceptionConverter extends RuntimeException
{
    private static final long serialVersionUID = 8657630363395849399L;
    private Exception ex;
    private String prefix;
    
    public ExceptionConverter(final Exception ex) {
        this.ex = ex;
        this.prefix = ((ex instanceof RuntimeException) ? "" : "ExceptionConverter: ");
    }
    
    public static final RuntimeException convertException(final Exception ex) {
        if (ex instanceof RuntimeException) {
            return (RuntimeException)ex;
        }
        return new ExceptionConverter(ex);
    }
    
    public Exception getException() {
        return this.ex;
    }
    
    @Override
    public String getMessage() {
        return this.ex.getMessage();
    }
    
    @Override
    public String getLocalizedMessage() {
        return this.ex.getLocalizedMessage();
    }
    
    @Override
    public String toString() {
        return this.prefix + this.ex;
    }
    
    @Override
    public void printStackTrace() {
        this.printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(final PrintStream s) {
        synchronized (s) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }
    
    @Override
    public void printStackTrace(final PrintWriter s) {
        synchronized (s) {
            s.print(this.prefix);
            this.ex.printStackTrace(s);
        }
    }
    
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
