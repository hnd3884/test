package org.apache.poi.util;

public class DocumentFormatException extends RuntimeException
{
    public DocumentFormatException(final String exception) {
        super(exception);
    }
    
    public DocumentFormatException(final String exception, final Throwable thr) {
        super(exception, thr);
    }
    
    public DocumentFormatException(final Throwable thr) {
        super(thr);
    }
    
    public static void check(final boolean assertTrue, final String message) {
        if (!assertTrue) {
            throw new DocumentFormatException(message);
        }
    }
}
