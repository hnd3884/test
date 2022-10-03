package com.sun.org.apache.xml.internal.dtm;

public class DTMException extends RuntimeException
{
    static final long serialVersionUID = -775576419181334734L;
    
    public DTMException(final String message) {
        super(message);
    }
    
    public DTMException(final Throwable e) {
        super(e);
    }
    
    public DTMException(final String message, final Throwable e) {
        super(message, e);
    }
}
