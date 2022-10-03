package sun.font;

import java.awt.font.FontRenderContext;
import java.awt.Font;
import sun.awt.SunHints;
import java.awt.geom.AffineTransform;

public class FontStrikeDesc
{
    static final int AA_ON = 16;
    static final int AA_LCD_H = 32;
    static final int AA_LCD_V = 64;
    static final int FRAC_METRICS_ON = 256;
    static final int FRAC_METRICS_SP = 512;
    AffineTransform devTx;
    AffineTransform glyphTx;
    int style;
    int aaHint;
    int fmHint;
    private int hashCode;
    private int valuemask;
    
    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            this.hashCode = this.glyphTx.hashCode() + this.devTx.hashCode() + this.valuemask;
        }
        return this.hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final FontStrikeDesc fontStrikeDesc = (FontStrikeDesc)o;
            return fontStrikeDesc.valuemask == this.valuemask && fontStrikeDesc.glyphTx.equals(this.glyphTx) && fontStrikeDesc.devTx.equals(this.devTx);
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    FontStrikeDesc() {
    }
    
    public static int getAAHintIntVal(final Object o, final Font2D font2D, final int n) {
        if (FontUtilities.isMacOSX14 && (o == SunHints.VALUE_TEXT_ANTIALIAS_OFF || o == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT || o == SunHints.VALUE_TEXT_ANTIALIAS_ON || o == SunHints.VALUE_TEXT_ANTIALIAS_GASP)) {
            return 2;
        }
        if (o == SunHints.VALUE_TEXT_ANTIALIAS_OFF || o == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            return 1;
        }
        if (o == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
            return 2;
        }
        if (o == SunHints.VALUE_TEXT_ANTIALIAS_GASP) {
            if (font2D.useAAForPtSize(n)) {
                return 2;
            }
            return 1;
        }
        else {
            if (o == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || o == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
                return 4;
            }
            if (o == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB || o == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR) {
                return 6;
            }
            return 1;
        }
    }
    
    public static int getAAHintIntVal(final Font2D font2D, final Font font, final FontRenderContext fontRenderContext) {
        final Object antiAliasingHint = fontRenderContext.getAntiAliasingHint();
        if (FontUtilities.isMacOSX14 && (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_OFF || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_ON || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_GASP)) {
            return 2;
        }
        if (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_OFF || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            return 1;
        }
        if (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_ON) {
            return 2;
        }
        if (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_GASP) {
            AffineTransform affineTransform = fontRenderContext.getTransform();
            int size;
            if (affineTransform.isIdentity() && !font.isTransformed()) {
                size = font.getSize();
            }
            else {
                final float size2D = font.getSize2D();
                if (affineTransform.isIdentity()) {
                    affineTransform = font.getTransform();
                    affineTransform.scale(size2D, size2D);
                }
                else {
                    affineTransform.scale(size2D, size2D);
                    if (font.isTransformed()) {
                        affineTransform.concatenate(font.getTransform());
                    }
                }
                final double shearX = affineTransform.getShearX();
                double n = affineTransform.getScaleY();
                if (shearX != 0.0) {
                    n = Math.sqrt(shearX * shearX + n * n);
                }
                size = (int)(Math.abs(n) + 0.5);
            }
            if (font2D.useAAForPtSize(size)) {
                return 2;
            }
            return 1;
        }
        else {
            if (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR) {
                return 4;
            }
            if (antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB || antiAliasingHint == SunHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR) {
                return 6;
            }
            return 1;
        }
    }
    
    public static int getFMHintIntVal(final Object o) {
        if (o == SunHints.VALUE_FRACTIONALMETRICS_OFF || o == SunHints.VALUE_FRACTIONALMETRICS_DEFAULT) {
            return 1;
        }
        return 2;
    }
    
    public FontStrikeDesc(final AffineTransform devTx, final AffineTransform glyphTx, final int n, final int aaHint, final int fmHint) {
        this.devTx = devTx;
        this.glyphTx = glyphTx;
        this.style = n;
        this.aaHint = aaHint;
        this.fmHint = fmHint;
        this.valuemask = n;
        switch (aaHint) {
            case 2: {
                this.valuemask |= 0x10;
                break;
            }
            case 4:
            case 5: {
                this.valuemask |= 0x20;
                break;
            }
            case 6:
            case 7: {
                this.valuemask |= 0x40;
                break;
            }
        }
        if (fmHint == 2) {
            this.valuemask |= 0x100;
        }
    }
    
    FontStrikeDesc(final FontStrikeDesc fontStrikeDesc) {
        this.devTx = fontStrikeDesc.devTx;
        this.glyphTx = (AffineTransform)fontStrikeDesc.glyphTx.clone();
        this.style = fontStrikeDesc.style;
        this.aaHint = fontStrikeDesc.aaHint;
        this.fmHint = fontStrikeDesc.fmHint;
        this.hashCode = fontStrikeDesc.hashCode;
        this.valuemask = fontStrikeDesc.valuemask;
    }
    
    @Override
    public String toString() {
        return "FontStrikeDesc: Style=" + this.style + " AA=" + this.aaHint + " FM=" + this.fmHint + " devTx=" + this.devTx + " devTx.FontTx.ptSize=" + this.glyphTx;
    }
}
