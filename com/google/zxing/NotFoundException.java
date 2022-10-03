package com.google.zxing;

public final class NotFoundException extends ReaderException
{
    private static final NotFoundException instance;
    
    private NotFoundException() {
    }
    
    public static NotFoundException getNotFoundInstance() {
        return NotFoundException.instance;
    }
    
    static {
        instance = new NotFoundException();
    }
}
