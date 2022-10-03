package com.google.zxing;

public final class ChecksumException extends ReaderException
{
    private static final ChecksumException instance;
    
    private ChecksumException() {
    }
    
    public static ChecksumException getChecksumInstance() {
        return ChecksumException.instance;
    }
    
    static {
        instance = new ChecksumException();
    }
}
