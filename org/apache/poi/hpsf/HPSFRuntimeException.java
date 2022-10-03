package org.apache.poi.hpsf;

public class HPSFRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = -7804271670232727159L;
    private Throwable reason;
    
    public HPSFRuntimeException() {
    }
    
    public HPSFRuntimeException(final String msg) {
        super(msg);
    }
    
    public HPSFRuntimeException(final Throwable reason) {
        this.reason = reason;
    }
    
    public HPSFRuntimeException(final String msg, final Throwable reason) {
        super(msg);
        this.reason = reason;
    }
    
    public Throwable getReason() {
        return this.reason;
    }
}
