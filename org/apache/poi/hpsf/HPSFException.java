package org.apache.poi.hpsf;

public class HPSFException extends Exception
{
    private Throwable reason;
    
    public HPSFException() {
    }
    
    public HPSFException(final String msg) {
        super(msg);
    }
    
    public HPSFException(final Throwable reason) {
        this.reason = reason;
    }
    
    public HPSFException(final String msg, final Throwable reason) {
        super(msg);
        this.reason = reason;
    }
    
    public Throwable getReason() {
        return this.reason;
    }
}
