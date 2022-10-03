package sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;

public class NativeStrike extends PhysicalStrike
{
    NativeFont nativeFont;
    
    NativeStrike(final NativeFont nativeFont, final FontStrikeDesc fontStrikeDesc) {
        super(nativeFont, fontStrikeDesc);
        throw new RuntimeException("NativeFont not used on Windows");
    }
    
    NativeStrike(final NativeFont nativeFont, final FontStrikeDesc fontStrikeDesc, final boolean b) {
        super(nativeFont, fontStrikeDesc);
        throw new RuntimeException("NativeFont not used on Windows");
    }
    
    @Override
    void getGlyphImagePtrs(final int[] array, final long[] array2, final int n) {
    }
    
    @Override
    long getGlyphImagePtr(final int n) {
        return 0L;
    }
    
    long getGlyphImagePtrNoCache(final int n) {
        return 0L;
    }
    
    @Override
    void getGlyphImageBounds(final int n, final Point2D.Float float1, final Rectangle rectangle) {
    }
    
    @Override
    Point2D.Float getGlyphMetrics(final int n) {
        return null;
    }
    
    @Override
    float getGlyphAdvance(final int n) {
        return 0.0f;
    }
    
    @Override
    Rectangle2D.Float getGlyphOutlineBounds(final int n) {
        return null;
    }
    
    @Override
    GeneralPath getGlyphOutline(final int n, final float n2, final float n3) {
        return null;
    }
    
    @Override
    GeneralPath getGlyphVectorOutline(final int[] array, final float n, final float n2) {
        return null;
    }
}
