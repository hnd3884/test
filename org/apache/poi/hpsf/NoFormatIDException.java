package org.apache.poi.hpsf;

public class NoFormatIDException extends HPSFRuntimeException
{
    public NoFormatIDException() {
    }
    
    public NoFormatIDException(final String msg) {
        super(msg);
    }
    
    public NoFormatIDException(final Throwable reason) {
        super(reason);
    }
    
    public NoFormatIDException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}