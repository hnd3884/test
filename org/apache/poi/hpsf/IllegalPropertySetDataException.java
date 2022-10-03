package org.apache.poi.hpsf;

public class IllegalPropertySetDataException extends HPSFRuntimeException
{
    public IllegalPropertySetDataException() {
    }
    
    public IllegalPropertySetDataException(final String msg) {
        super(msg);
    }
    
    public IllegalPropertySetDataException(final Throwable reason) {
        super(reason);
    }
    
    public IllegalPropertySetDataException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}
