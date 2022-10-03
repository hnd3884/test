package org.apache.commons.digester.xmlrules;

public class DigesterLoadingException extends Exception
{
    private Throwable cause;
    
    public DigesterLoadingException(final String msg) {
        super(msg);
        this.cause = null;
    }
    
    public DigesterLoadingException(final Throwable cause) {
        this(cause.getMessage());
        this.cause = cause;
    }
    
    public DigesterLoadingException(final String msg, final Throwable cause) {
        this(msg);
        this.cause = cause;
    }
    
    public Throwable getCause() {
        return this.cause;
    }
}
