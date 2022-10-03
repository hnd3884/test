package org.apache.poi.ss.usermodel;

public enum ReadingOrder
{
    CONTEXT, 
    LEFT_TO_RIGHT, 
    RIGHT_TO_LEFT;
    
    public short getCode() {
        return (short)this.ordinal();
    }
    
    public static ReadingOrder forLong(final long code) {
        if (code < 0L || code >= values().length) {
            throw new IllegalArgumentException("Invalid ReadingOrder code: " + code);
        }
        return values()[(int)code];
    }
}
