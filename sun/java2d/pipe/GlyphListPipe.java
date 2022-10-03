package sun.java2d.pipe;

import java.awt.font.GlyphVector;
import sun.java2d.loops.FontInfo;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import sun.font.GlyphList;
import sun.java2d.SurfaceData;
import sun.java2d.SunGraphics2D;

public abstract class GlyphListPipe implements TextPipe
{
    @Override
    public void drawString(final SunGraphics2D sunGraphics2D, final String s, final double n, final double n2) {
        final FontInfo fontInfo = sunGraphics2D.getFontInfo();
        if (fontInfo.pixelHeight > 100) {
            SurfaceData.outlineTextRenderer.drawString(sunGraphics2D, s, n, n2);
            return;
        }
        float n3;
        float n4;
        if (sunGraphics2D.transformState >= 3) {
            final double[] array = { n + fontInfo.originX, n2 + fontInfo.originY };
            sunGraphics2D.transform.transform(array, 0, array, 0, 1);
            n3 = (float)array[0];
            n4 = (float)array[1];
        }
        else {
            n3 = (float)(n + fontInfo.originX + sunGraphics2D.transX);
            n4 = (float)(n2 + fontInfo.originY + sunGraphics2D.transY);
        }
        final GlyphList instance = GlyphList.getInstance();
        if (instance.setFromString(fontInfo, s, n3, n4)) {
            this.drawGlyphList(sunGraphics2D, instance);
            instance.dispose();
        }
        else {
            instance.dispose();
            new TextLayout(s, sunGraphics2D.getFont(), sunGraphics2D.getFontRenderContext()).draw(sunGraphics2D, (float)n, (float)n2);
        }
    }
    
    @Override
    public void drawChars(final SunGraphics2D sunGraphics2D, final char[] array, final int n, final int n2, final int n3, final int n4) {
        final FontInfo fontInfo = sunGraphics2D.getFontInfo();
        if (fontInfo.pixelHeight > 100) {
            SurfaceData.outlineTextRenderer.drawChars(sunGraphics2D, array, n, n2, n3, n4);
            return;
        }
        float n5;
        float n6;
        if (sunGraphics2D.transformState >= 3) {
            final double[] array2 = { n3 + fontInfo.originX, n4 + fontInfo.originY };
            sunGraphics2D.transform.transform(array2, 0, array2, 0, 1);
            n5 = (float)array2[0];
            n6 = (float)array2[1];
        }
        else {
            n5 = n3 + fontInfo.originX + sunGraphics2D.transX;
            n6 = n4 + fontInfo.originY + sunGraphics2D.transY;
        }
        final GlyphList instance = GlyphList.getInstance();
        if (instance.setFromChars(fontInfo, array, n, n2, n5, n6)) {
            this.drawGlyphList(sunGraphics2D, instance);
            instance.dispose();
        }
        else {
            instance.dispose();
            new TextLayout(new String(array, n, n2), sunGraphics2D.getFont(), sunGraphics2D.getFontRenderContext()).draw(sunGraphics2D, (float)n3, (float)n4);
        }
    }
    
    @Override
    public void drawGlyphVector(final SunGraphics2D sunGraphics2D, final GlyphVector glyphVector, float n, float n2) {
        final FontInfo gvFontInfo = sunGraphics2D.getGVFontInfo(glyphVector.getFont(), glyphVector.getFontRenderContext());
        if (gvFontInfo.pixelHeight > 100) {
            SurfaceData.outlineTextRenderer.drawGlyphVector(sunGraphics2D, glyphVector, n, n2);
            return;
        }
        if (sunGraphics2D.transformState >= 3) {
            final double[] array = { n, n2 };
            sunGraphics2D.transform.transform(array, 0, array, 0, 1);
            n = (float)array[0];
            n2 = (float)array[1];
        }
        else {
            n += sunGraphics2D.transX;
            n2 += sunGraphics2D.transY;
        }
        final GlyphList instance = GlyphList.getInstance();
        instance.setFromGlyphVector(gvFontInfo, glyphVector, n, n2);
        this.drawGlyphList(sunGraphics2D, instance, gvFontInfo.aaHint);
        instance.dispose();
    }
    
    protected abstract void drawGlyphList(final SunGraphics2D p0, final GlyphList p1);
    
    protected void drawGlyphList(final SunGraphics2D sunGraphics2D, final GlyphList list, final int n) {
        this.drawGlyphList(sunGraphics2D, list);
    }
}
