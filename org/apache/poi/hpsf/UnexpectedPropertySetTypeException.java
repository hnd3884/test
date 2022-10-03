package org.apache.poi.hpsf;

public class UnexpectedPropertySetTypeException extends HPSFException
{
    public UnexpectedPropertySetTypeException() {
    }
    
    public UnexpectedPropertySetTypeException(final String msg) {
        super(msg);
    }
    
    public UnexpectedPropertySetTypeException(final Throwable reason) {
        super(reason);
    }
    
    public UnexpectedPropertySetTypeException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}