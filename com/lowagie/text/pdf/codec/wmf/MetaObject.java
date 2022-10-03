package com.lowagie.text.pdf.codec.wmf;

public class MetaObject
{
    public static final int META_NOT_SUPPORTED = 0;
    public static final int META_PEN = 1;
    public static final int META_BRUSH = 2;
    public static final int META_FONT = 3;
    public int type;
    
    public MetaObject() {
        this.type = 0;
    }
    
    public MetaObject(final int type) {
        this.type = 0;
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
}
