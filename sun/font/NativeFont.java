package sun.font;

import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.font.FontRenderContext;
import java.awt.FontFormatException;

public class NativeFont extends PhysicalFont
{
    public NativeFont(final String s, final boolean b) throws FontFormatException {
        throw new FontFormatException("NativeFont not used on Windows");
    }
    
    static boolean hasExternalBitmaps(final String s) {
        return false;
    }
    
    public CharToGlyphMapper getMapper() {
        return null;
    }
    
    PhysicalFont getDelegateFont() {
        return null;
    }
    
    @Override
    FontStrike createStrike(final FontStrikeDesc fontStrikeDesc) {
        return null;
    }
    
    public Rectangle2D getMaxCharBounds(final FontRenderContext fontRenderContext) {
        return null;
    }
    
    @Override
    StrikeMetrics getFontMetrics(final long n) {
        return null;
    }
    
    public GeneralPath getGlyphOutline(final long n, final int n2, final float n3, final float n4) {
        return null;
    }
    
    public GeneralPath getGlyphVectorOutline(final long n, final int[] array, final int n2, final float n3, final float n4) {
        return null;
    }
    
    @Override
    long getGlyphImage(final long n, final int n2) {
        return 0L;
    }
    
    @Override
    void getGlyphMetrics(final long n, final int n2, final Point2D.Float float1) {
    }
    
    @Override
    float getGlyphAdvance(final long n, final int n2) {
        return 0.0f;
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final long n, final int n2) {
        return new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
    }
}
