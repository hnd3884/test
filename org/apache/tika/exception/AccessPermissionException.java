package org.apache.tika.exception;

public class AccessPermissionException extends TikaException
{
    public AccessPermissionException() {
        super("Unable to process: content extraction is not allowed");
    }
    
    public AccessPermissionException(final Throwable th) {
        super("Unable to process: content extraction is not allowed", th);
    }
    
    public AccessPermissionException(final String info) {
        super(info);
    }
    
    public AccessPermissionException(final String info, final Throwable th) {
        super(info, th);
    }
}
