package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;

class NullFontScaler extends FontScaler
{
    NullFontScaler() {
    }
    
    public NullFontScaler(final Font2D font2D, final int n, final boolean b, final int n2) {
    }
    
    @Override
    StrikeMetrics getFontMetrics(final long n) {
        return new StrikeMetrics(240.0f, 240.0f, 240.0f, 240.0f, 240.0f, 240.0f, 240.0f, 240.0f, 240.0f, 240.0f);
    }
    
    @Override
    float getGlyphAdvance(final long n, final int n2) {
        return 0.0f;
    }
    
    @Override
    void getGlyphMetrics(final long n, final int n2, final Point2D.Float float1) {
        float1.x = 0.0f;
        float1.y = 0.0f;
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final long n, final int n2) {
        return new Rectangle2D.Float(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    GeneralPath getGlyphOutline(final long n, final int n2, final float n3, final float n4) {
        return new GeneralPath();
    }
    
    @Override
    GeneralPath getGlyphVectorOutline(final long n, final int[] array, final int n2, final float n3, final float n4) {
        return new GeneralPath();
    }
    
    @Override
    long getLayoutTableCache() {
        return 0L;
    }
    
    @Override
    long createScalerContext(final double[] array, final int n, final int n2, final float n3, final float n4, final boolean b) {
        return getNullScalerContext();
    }
    
    @Override
    void invalidateScalerContext(final long n) {
    }
    
    @Override
    int getNumGlyphs() throws FontScalerException {
        return 1;
    }
    
    @Override
    int getMissingGlyphCode() throws FontScalerException {
        return 0;
    }
    
    @Override
    int getGlyphCode(final char c) throws FontScalerException {
        return 0;
    }
    
    @Override
    long getUnitsPerEm() {
        return 2048L;
    }
    
    @Override
    Point2D.Float getGlyphPoint(final long n, final int n2, final int n3) {
        return null;
    }
    
    static native long getNullScalerContext();
    
    @Override
    native long getGlyphImage(final long p0, final int p1);
}
