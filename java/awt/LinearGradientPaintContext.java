package java.awt;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class LinearGradientPaintContext extends MultipleGradientPaintContext
{
    private float dgdX;
    private float dgdY;
    private float gc;
    
    LinearGradientPaintContext(final LinearGradientPaint linearGradientPaint, final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints, final Point2D point2D, final Point2D point2D2, final float[] array, final Color[] array2, final MultipleGradientPaint.CycleMethod cycleMethod, final MultipleGradientPaint.ColorSpaceType colorSpaceType) {
        super(linearGradientPaint, colorModel, rectangle, rectangle2D, affineTransform, renderingHints, array, array2, cycleMethod, colorSpaceType);
        final float n = (float)point2D.getX();
        final float n2 = (float)point2D.getY();
        final float n3 = (float)point2D2.getX();
        final float n4 = (float)point2D2.getY();
        final float n5 = n3 - n;
        final float n6 = n4 - n2;
        final float n7 = n5 * n5 + n6 * n6;
        final float n8 = n5 / n7;
        final float n9 = n6 / n7;
        this.dgdX = this.a00 * n8 + this.a10 * n9;
        this.dgdY = this.a01 * n8 + this.a11 * n9;
        this.gc = (this.a02 - n) * n8 + (this.a12 - n2) * n9;
    }
    
    @Override
    protected void fillRaster(final int[] array, int i, final int n, final int n2, final int n3, final int n4, final int n5) {
        int n6 = i + n4;
        final float n7 = this.dgdX * n2 + this.gc;
        for (int j = 0; j < n5; ++j) {
            for (float n8 = n7 + this.dgdY * (n3 + j); i < n6; array[i++] = this.indexIntoGradientsArrays(n8), n8 += this.dgdX) {}
            i += n;
            n6 = i + n4;
        }
    }
}
