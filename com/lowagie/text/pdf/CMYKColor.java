package com.lowagie.text.pdf;

public class CMYKColor extends ExtendedColor
{
    private static final long serialVersionUID = 5940378778276468452L;
    float cyan;
    float magenta;
    float yellow;
    float black;
    
    public CMYKColor(final int intCyan, final int intMagenta, final int intYellow, final int intBlack) {
        this(intCyan / 255.0f, intMagenta / 255.0f, intYellow / 255.0f, intBlack / 255.0f);
    }
    
    public CMYKColor(final float floatCyan, final float floatMagenta, final float floatYellow, final float floatBlack) {
        super(2, 1.0f - floatCyan - floatBlack, 1.0f - floatMagenta - floatBlack, 1.0f - floatYellow - floatBlack);
        this.cyan = ExtendedColor.normalize(floatCyan);
        this.magenta = ExtendedColor.normalize(floatMagenta);
        this.yellow = ExtendedColor.normalize(floatYellow);
        this.black = ExtendedColor.normalize(floatBlack);
    }
    
    public float getCyan() {
        return this.cyan;
    }
    
    public float getMagenta() {
        return this.magenta;
    }
    
    public float getYellow() {
        return this.yellow;
    }
    
    public float getBlack() {
        return this.black;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof CMYKColor)) {
            return false;
        }
        final CMYKColor c2 = (CMYKColor)obj;
        return this.cyan == c2.cyan && this.magenta == c2.magenta && this.yellow == c2.yellow && this.black == c2.black;
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.cyan) ^ Float.floatToIntBits(this.magenta) ^ Float.floatToIntBits(this.yellow) ^ Float.floatToIntBits(this.black);
    }
}
