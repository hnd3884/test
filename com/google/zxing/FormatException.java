package com.google.zxing;

public final class FormatException extends ReaderException
{
    private static final FormatException instance;
    
    private FormatException() {
    }
    
    public static FormatException getFormatInstance() {
        return FormatException.instance;
    }
    
    static {
        instance = new FormatException();
    }
}
