package com.me.mdm.framework.syncml.xml;

public class SyncMLMessage2XMLConverterException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    public SyncMLMessage2XMLConverterException() {
    }
    
    public SyncMLMessage2XMLConverterException(final String message) {
        super(message);
    }
    
    public SyncMLMessage2XMLConverterException(final Throwable cause) {
        super(cause);
    }
    
    public SyncMLMessage2XMLConverterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
