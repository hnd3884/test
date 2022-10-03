package sun.java2d.pipe;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.font.TextLayout;
import sun.java2d.SunGraphics2D;

public class OutlineTextRenderer implements TextPipe
{
    public static final int THRESHHOLD = 100;
    
    @Override
    public void drawChars(final SunGraphics2D sunGraphics2D, final char[] array, final int n, final int n2, final int n3, final int n4) {
        this.drawString(sunGraphics2D, new String(array, n, n2), n3, n4);
    }
    
    @Override
    public void drawString(final SunGraphics2D sunGraphics2D, final String s, final double n, final double n2) {
        if ("".equals(s)) {
            return;
        }
        final Shape outline = new TextLayout(s, sunGraphics2D.getFont(), sunGraphics2D.getFontRenderContext()).getOutline(AffineTransform.getTranslateInstance(n, n2));
        final int aaHint = sunGraphics2D.getFontInfo().aaHint;
        int antialiasHint = -1;
        if (aaHint != 1 && sunGraphics2D.antialiasHint != 2) {
            antialiasHint = sunGraphics2D.antialiasHint;
            sunGraphics2D.antialiasHint = 2;
            sunGraphics2D.validatePipe();
        }
        else if (aaHint == 1 && sunGraphics2D.antialiasHint != 1) {
            antialiasHint = sunGraphics2D.antialiasHint;
            sunGraphics2D.antialiasHint = 1;
            sunGraphics2D.validatePipe();
        }
        sunGraphics2D.fill(outline);
        if (antialiasHint != -1) {
            sunGraphics2D.antialiasHint = antialiasHint;
            sunGraphics2D.validatePipe();
        }
    }
    
    @Override
    public void drawGlyphVector(final SunGraphics2D sunGraphics2D, final GlyphVector glyphVector, final float n, final float n2) {
        final Shape outline = glyphVector.getOutline(n, n2);
        int antialiasHint = -1;
        final FontRenderContext fontRenderContext = glyphVector.getFontRenderContext();
        int antiAliased = fontRenderContext.isAntiAliased() ? 1 : 0;
        if (antiAliased != 0 && sunGraphics2D.getGVFontInfo(glyphVector.getFont(), fontRenderContext).aaHint == 1) {
            antiAliased = 0;
        }
        if (antiAliased != 0 && sunGraphics2D.antialiasHint != 2) {
            antialiasHint = sunGraphics2D.antialiasHint;
            sunGraphics2D.antialiasHint = 2;
            sunGraphics2D.validatePipe();
        }
        else if (antiAliased == 0 && sunGraphics2D.antialiasHint != 1) {
            antialiasHint = sunGraphics2D.antialiasHint;
            sunGraphics2D.antialiasHint = 1;
            sunGraphics2D.validatePipe();
        }
        sunGraphics2D.fill(outline);
        if (antialiasHint != -1) {
            sunGraphics2D.antialiasHint = antialiasHint;
            sunGraphics2D.validatePipe();
        }
    }
}
