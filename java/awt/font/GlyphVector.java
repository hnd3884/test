package java.awt.font;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.Shape;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.Font;

public abstract class GlyphVector implements Cloneable
{
    public static final int FLAG_HAS_TRANSFORMS = 1;
    public static final int FLAG_HAS_POSITION_ADJUSTMENTS = 2;
    public static final int FLAG_RUN_RTL = 4;
    public static final int FLAG_COMPLEX_GLYPHS = 8;
    public static final int FLAG_MASK = 15;
    
    public abstract Font getFont();
    
    public abstract FontRenderContext getFontRenderContext();
    
    public abstract void performDefaultLayout();
    
    public abstract int getNumGlyphs();
    
    public abstract int getGlyphCode(final int p0);
    
    public abstract int[] getGlyphCodes(final int p0, final int p1, final int[] p2);
    
    public int getGlyphCharIndex(final int n) {
        return n;
    }
    
    public int[] getGlyphCharIndices(final int n, final int n2, int[] array) {
        if (array == null) {
            array = new int[n2];
        }
        for (int i = 0, n3 = n; i < n2; ++i, ++n3) {
            array[i] = this.getGlyphCharIndex(n3);
        }
        return array;
    }
    
    public abstract Rectangle2D getLogicalBounds();
    
    public abstract Rectangle2D getVisualBounds();
    
    public Rectangle getPixelBounds(final FontRenderContext fontRenderContext, final float n, final float n2) {
        final Rectangle2D visualBounds = this.getVisualBounds();
        final int n3 = (int)Math.floor(visualBounds.getX() + n);
        final int n4 = (int)Math.floor(visualBounds.getY() + n2);
        return new Rectangle(n3, n4, (int)Math.ceil(visualBounds.getMaxX() + n) - n3, (int)Math.ceil(visualBounds.getMaxY() + n2) - n4);
    }
    
    public abstract Shape getOutline();
    
    public abstract Shape getOutline(final float p0, final float p1);
    
    public abstract Shape getGlyphOutline(final int p0);
    
    public Shape getGlyphOutline(final int n, final float n2, final float n3) {
        return AffineTransform.getTranslateInstance(n2, n3).createTransformedShape(this.getGlyphOutline(n));
    }
    
    public abstract Point2D getGlyphPosition(final int p0);
    
    public abstract void setGlyphPosition(final int p0, final Point2D p1);
    
    public abstract AffineTransform getGlyphTransform(final int p0);
    
    public abstract void setGlyphTransform(final int p0, final AffineTransform p1);
    
    public int getLayoutFlags() {
        return 0;
    }
    
    public abstract float[] getGlyphPositions(final int p0, final int p1, final float[] p2);
    
    public abstract Shape getGlyphLogicalBounds(final int p0);
    
    public abstract Shape getGlyphVisualBounds(final int p0);
    
    public Rectangle getGlyphPixelBounds(final int n, final FontRenderContext fontRenderContext, final float n2, final float n3) {
        final Rectangle2D bounds2D = this.getGlyphVisualBounds(n).getBounds2D();
        final int n4 = (int)Math.floor(bounds2D.getX() + n2);
        final int n5 = (int)Math.floor(bounds2D.getY() + n3);
        return new Rectangle(n4, n5, (int)Math.ceil(bounds2D.getMaxX() + n2) - n4, (int)Math.ceil(bounds2D.getMaxY() + n3) - n5);
    }
    
    public abstract GlyphMetrics getGlyphMetrics(final int p0);
    
    public abstract GlyphJustificationInfo getGlyphJustificationInfo(final int p0);
    
    public abstract boolean equals(final GlyphVector p0);
}
