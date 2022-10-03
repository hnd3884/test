package org.apache.poi.ss.usermodel;

public enum VerticalAlignment
{
    TOP, 
    CENTER, 
    BOTTOM, 
    JUSTIFY, 
    DISTRIBUTED;
    
    public short getCode() {
        return (short)this.ordinal();
    }
    
    public static VerticalAlignment forInt(final int code) {
        if (code < 0 || code >= values().length) {
            throw new IllegalArgumentException("Invalid VerticalAlignment code: " + code);
        }
        return values()[code];
    }
}
