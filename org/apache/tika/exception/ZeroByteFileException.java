package org.apache.tika.exception;

public class ZeroByteFileException extends TikaException
{
    public static IgnoreZeroByteFileException IGNORE_ZERO_BYTE_FILE_EXCEPTION;
    
    public ZeroByteFileException(final String msg) {
        super(msg);
    }
    
    static {
        ZeroByteFileException.IGNORE_ZERO_BYTE_FILE_EXCEPTION = new IgnoreZeroByteFileException();
    }
    
    public static class IgnoreZeroByteFileException
    {
    }
}
