package org.apache.poi.ss.usermodel;

public enum HorizontalAlignment
{
    GENERAL, 
    LEFT, 
    CENTER, 
    RIGHT, 
    FILL, 
    JUSTIFY, 
    CENTER_SELECTION, 
    DISTRIBUTED;
    
    public short getCode() {
        return (short)this.ordinal();
    }
    
    public static HorizontalAlignment forInt(final int code) {
        if (code < 0 || code >= values().length) {
            throw new IllegalArgumentException("Invalid HorizontalAlignment code: " + code);
        }
        return values()[code];
    }
}
