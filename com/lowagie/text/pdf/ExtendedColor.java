package com.lowagie.text.pdf;

import java.awt.Color;

public abstract class ExtendedColor extends Color
{
    private static final long serialVersionUID = 2722660170712380080L;
    public static final int TYPE_RGB = 0;
    public static final int TYPE_GRAY = 1;
    public static final int TYPE_CMYK = 2;
    public static final int TYPE_SEPARATION = 3;
    public static final int TYPE_PATTERN = 4;
    public static final int TYPE_SHADING = 5;
    protected int type;
    
    public ExtendedColor(final int type) {
        super(0, 0, 0);
        this.type = type;
    }
    
    public ExtendedColor(final int type, final float red, final float green, final float blue) {
        super(normalize(red), normalize(green), normalize(blue));
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public static int getType(final Color color) {
        if (color instanceof ExtendedColor) {
            return ((ExtendedColor)color).getType();
        }
        return 0;
    }
    
    static final float normalize(final float value) {
        if (value < 0.0f) {
            return 0.0f;
        }
        if (value > 1.0f) {
            return 1.0f;
        }
        return value;
    }
}
