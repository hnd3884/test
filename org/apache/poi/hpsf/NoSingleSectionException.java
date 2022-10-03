package org.apache.poi.hpsf;

public class NoSingleSectionException extends HPSFRuntimeException
{
    public NoSingleSectionException() {
    }
    
    public NoSingleSectionException(final String msg) {
        super(msg);
    }
    
    public NoSingleSectionException(final Throwable reason) {
        super(reason);
    }
    
    public NoSingleSectionException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}
