package org.apache.poi.hpsf;

public class MarkUnsupportedException extends HPSFException
{
    public MarkUnsupportedException() {
    }
    
    public MarkUnsupportedException(final String msg) {
        super(msg);
    }
    
    public MarkUnsupportedException(final Throwable reason) {
        super(reason);
    }
    
    public MarkUnsupportedException(final String msg, final Throwable reason) {
        super(msg, reason);
    }
}
