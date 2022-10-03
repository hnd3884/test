package org.apache.poi.util;

public class RecordFormatException extends RuntimeException
{
    public RecordFormatException(final String exception) {
        super(exception);
    }
    
    public RecordFormatException(final String exception, final Throwable thr) {
        super(exception, thr);
    }
    
    public RecordFormatException(final Throwable thr) {
        super(thr);
    }
    
    public static void check(final boolean assertTrue, final String message) {
        if (!assertTrue) {
            throw new RecordFormatException(message);
        }
    }
}
