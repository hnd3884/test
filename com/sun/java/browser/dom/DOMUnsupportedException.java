package com.sun.java.browser.dom;

public class DOMUnsupportedException extends Exception
{
    private Throwable ex;
    private String msg;
    
    public DOMUnsupportedException() {
        this(null, (String)null);
    }
    
    public DOMUnsupportedException(final String s) {
        this(null, s);
    }
    
    public DOMUnsupportedException(final Exception ex) {
        this(ex, null);
    }
    
    public DOMUnsupportedException(final Exception ex, final String msg) {
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
