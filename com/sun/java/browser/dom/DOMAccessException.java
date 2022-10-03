package com.sun.java.browser.dom;

public class DOMAccessException extends Exception
{
    private Throwable ex;
    private String msg;
    
    public DOMAccessException() {
        this(null, (String)null);
    }
    
    public DOMAccessException(final String s) {
        this(null, s);
    }
    
    public DOMAccessException(final Exception ex) {
        this(ex, null);
    }
    
    public DOMAccessException(final Exception ex, final String msg) {
        this.ex = ex;
        this.msg = msg;
    }
    
    @Override
    public String getMessage() {
        return this.msg;
    }
    
    @Override
    public Throwable getCause() {
        return this.ex;
    }
}
