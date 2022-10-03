package org.apache.poi.hpsf;

public class NoPropertySetStreamException extends HPSFException
{
    public NoPropertySetStreamException() {
    }
    
    public NoPropertySetStreamException(final String msg) {
        super(msg);
    }
    
    public NoPropertySetStreamException(final Throwable reason) {
        super(reason);
    }
    
    public NoPropertySetStreamException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}