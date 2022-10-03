package com.lowagie.text.pdf;

public class GrayColor extends ExtendedColor
{
    private static final long serialVersionUID = -6571835680819282746L;
    private float gray;
    public static final GrayColor GRAYBLACK;
    public static final GrayColor GRAYWHITE;
    
    public GrayColor(final int intGray) {
        this(intGray / 255.0f);
    }
    
    public GrayColor(final float floatGray) {
        super(1, floatGray, floatGray, floatGray);
        this.gray = ExtendedColor.normalize(floatGray);
    }
    
    public float getGray() {
        return this.gray;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof GrayColor && ((GrayColor)obj).gray == this.gray;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.gray);
    }
    
    static {
        GRAYBLACK = new GrayColor(0.0f);
        GRAYWHITE = new GrayColor(1.0f);
    }
}
