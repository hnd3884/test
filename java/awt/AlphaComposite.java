package java.awt;

import sun.java2d.SunCompositeContext;
import java.awt.image.ColorModel;

public final class AlphaComposite implements Composite
{
    public static final int CLEAR = 1;
    public static final int SRC = 2;
    public static final int DST = 9;
    public static final int SRC_OVER = 3;
    public static final int DST_OVER = 4;
    public static final int SRC_IN = 5;
    public static final int DST_IN = 6;
    public static final int SRC_OUT = 7;
    public static final int DST_OUT = 8;
    public static final int SRC_ATOP = 10;
    public static final int DST_ATOP = 11;
    public static final int XOR = 12;
    public static final AlphaComposite Clear;
    public static final AlphaComposite Src;
    public static final AlphaComposite Dst;
    public static final AlphaComposite SrcOver;
    public static final AlphaComposite DstOver;
    public static final AlphaComposite SrcIn;
    public static final AlphaComposite DstIn;
    public static final AlphaComposite SrcOut;
    public static final AlphaComposite DstOut;
    public static final AlphaComposite SrcAtop;
    public static final AlphaComposite DstAtop;
    public static final AlphaComposite Xor;
    private static final int MIN_RULE = 1;
    private static final int MAX_RULE = 12;
    float extraAlpha;
    int rule;
    
    private AlphaComposite(final int n) {
        this(n, 1.0f);
    }
    
    private AlphaComposite(final int rule, final float extraAlpha) {
        if (rule < 1 || rule > 12) {
            throw new IllegalArgumentException("unknown composite rule");
        }
        if (extraAlpha >= 0.0f && extraAlpha <= 1.0f) {
            this.rule = rule;
            this.extraAlpha = extraAlpha;
            return;
        }
        throw new IllegalArgumentException("alpha value out of range");
    }
    
    public static AlphaComposite getInstance(final int n) {
        switch (n) {
            case 1: {
                return AlphaComposite.Clear;
            }
            case 2: {
                return AlphaComposite.Src;
            }
            case 9: {
                return AlphaComposite.Dst;
            }
            case 3: {
                return AlphaComposite.SrcOver;
            }
            case 4: {
                return AlphaComposite.DstOver;
            }
            case 5: {
                return AlphaComposite.SrcIn;
            }
            case 6: {
                return AlphaComposite.DstIn;
            }
            case 7: {
                return AlphaComposite.SrcOut;
            }
            case 8: {
                return AlphaComposite.DstOut;
            }
            case 10: {
                return AlphaComposite.SrcAtop;
            }
            case 11: {
                return AlphaComposite.DstAtop;
            }
            case 12: {
                return AlphaComposite.Xor;
            }
            default: {
                throw new IllegalArgumentException("unknown composite rule");
            }
        }
    }
    
    public static AlphaComposite getInstance(final int n, final float n2) {
        if (n2 == 1.0f) {
            return getInstance(n);
        }
        return new AlphaComposite(n, n2);
    }
    
    @Override
    public CompositeContext createContext(final ColorModel colorModel, final ColorModel colorModel2, final RenderingHints renderingHints) {
        return new SunCompositeContext(this, colorModel, colorModel2);
    }
    
    public float getAlpha() {
        return this.extraAlpha;
    }
    
    public int getRule() {
        return this.rule;
    }
    
    public AlphaComposite derive(final int n) {
        return (this.rule == n) ? this : getInstance(n, this.extraAlpha);
    }
    
    public AlphaComposite derive(final float n) {
        return (this.extraAlpha == n) ? this : getInstance(this.rule, n);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.extraAlpha) * 31 + this.rule;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof AlphaComposite)) {
            return false;
        }
        final AlphaComposite alphaComposite = (AlphaComposite)o;
        return this.rule == alphaComposite.rule && this.extraAlpha == alphaComposite.extraAlpha;
    }
    
    static {
        Clear = new AlphaComposite(1);
        Src = new AlphaComposite(2);
        Dst = new AlphaComposite(9);
        SrcOver = new AlphaComposite(3);
        DstOver = new AlphaComposite(4);
        SrcIn = new AlphaComposite(5);
        DstIn = new AlphaComposite(6);
        SrcOut = new AlphaComposite(7);
        DstOut = new AlphaComposite(8);
        SrcAtop = new AlphaComposite(10);
        DstAtop = new AlphaComposite(11);
        Xor = new AlphaComposite(12);
    }
}
