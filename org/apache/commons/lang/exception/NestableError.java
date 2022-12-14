package org.apache.commons.lang.exception;

import java.io.PrintWriter;
import java.io.PrintStream;

public class NestableError extends Error implements Nestable
{
    protected NestableDelegate delegate;
    private Throwable cause;
    
    public NestableError() {
        this.delegate = new NestableDelegate(this);
        this.cause = null;
    }
    
    public NestableError(final String msg) {
        super(msg);
        this.delegate = new NestableDelegate(this);
        this.cause = null;
    }
    
    public NestableError(final Throwable cause) {
        this.delegate = new NestableDelegate(this);
        this.cause = null;
        this.cause = cause;
    }
    
    public NestableError(final String msg, final Throwable cause) {
        super(msg);
        this.delegate = new NestableDelegate(this);
        this.cause = null;
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
    
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        if (this.cause != null) {
            return this.cause.toString();
        }
        return null;
    }
    
    public String getMessage(final int index) {
        if (index == 0) {
            return super.getMessage();
        }
        return this.delegate.getMessage(index);
    }
    
    public String[] getMessages() {
        return this.delegate.getMessages();
    }
    
    public Throwable getThrowable(final int index) {
        return this.delegate.getThrowable(index);
    }
    
    public int getThrowableCount() {
        return this.delegate.getThrowableCount();
    }
    
    public Throwable[] getThrowables() {
        return this.delegate.getThrowables();
    }
    
    public int indexOfThrowable(final Class type) {
        return this.delegate.indexOfThrowable(type, 0);
    }
    
    public int indexOfThrowable(final Class type, final int fromIndex) {
        return this.delegate.indexOfThrowable(type, fromIndex);
    }
    
    public void printStackTrace() {
        this.delegate.printStackTrace();
    }
    
    public void printStackTrace(final PrintStream out) {
        this.delegate.printStackTrace(out);
    }
    
    public void printStackTrace(final PrintWriter out) {
        this.delegate.printStackTrace(out);
    }
    
    public final void printPartialStackTrace(final PrintWriter out) {
        super.printStackTrace(out);
    }
}
