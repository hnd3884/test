package org.apache.poi.hssf.eventusermodel;

public class HSSFUserException extends Exception
{
    private Throwable reason;
    
    public HSSFUserException() {
    }
    
    public HSSFUserException(final String msg) {
        super(msg);
    }
    
    public HSSFUserException(final Throwable reason) {
        this.reason = reason;
    }
    
    public HSSFUserException(final String msg, final Throwable reason) {
        super(msg);
        this.reason = reason;
    }
    
    public Throwable getReason() {
        return this.reason;
    }
}
