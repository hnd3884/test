package java.awt;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

final class RadialGradientPaintContext extends MultipleGradientPaintContext
{
    private boolean isSimpleFocus;
    private boolean isNonCyclic;
    private float radius;
    private float centerX;
    private float centerY;
    private float focusX;
    private float focusY;
    private float radiusSq;
    private float constA;
    private float constB;
    private float gDeltaDelta;
    private float trivial;
    private static final float SCALEBACK = 0.99f;
    private static final int SQRT_LUT_SIZE = 2048;
    private static float[] sqrtLut;
    
    RadialGradientPaintContext(final RadialGradientPaint radialGradientPaint, final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints, final float centerX, final float centerY, final float radius, final float focusX, final float focusY, final float[] array, final Color[] array2, final MultipleGradientPaint.CycleMethod cycleMethod, final MultipleGradientPaint.ColorSpaceType colorSpaceType) {
        super(radialGradientPaint, colorModel, rectangle, rectangle2D, affineTransform, renderingHints, array, array2, cycleMethod, colorSpaceType);
        this.isSimpleFocus = false;
        this.isNonCyclic = false;
        this.centerX = centerX;
        this.centerY = centerY;
        this.focusX = focusX;
        this.focusY = focusY;
        this.radius = radius;
        this.isSimpleFocus = (this.focusX == this.centerX && this.focusY == this.centerY);
        this.isNonCyclic = (cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE);
        this.radiusSq = this.radius * this.radius;
        float n = this.focusX - this.centerX;
        final float n2 = this.focusY - this.centerY;
        final double n3 = n * n + n2 * n2;
        if (n3 > this.radiusSq * 0.99f) {
            final float n4 = (float)Math.sqrt(this.radiusSq * 0.99f / n3);
            n *= n4;
            final float n5 = n2 * n4;
            this.focusX = this.centerX + n;
            this.focusY = this.centerY + n5;
        }
        this.trivial = (float)Math.sqrt(this.radiusSq - n * n);
        this.constA = this.a02 - this.centerX;
        this.constB = this.a12 - this.centerY;
        this.gDeltaDelta = 2.0f * (this.a00 * this.a00 + this.a10 * this.a10) / this.radiusSq;
    }
    
    @Override
    protected void fillRaster(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        if (this.isSimpleFocus && this.isNonCyclic && this.isSimpleLookup) {
            this.simpleNonCyclicFillRaster(array, n, n2, n3, n4, n5, n6);
        }
        else {
            this.cyclicCircularGradientFillRaster(array, n, n2, n3, n4, n5, n6);
        }
    }
    
    private void simpleNonCyclicFillRaster(final int[] array, int n, int n2, final int n3, final int n4, final int n5, final int n6) {
        float n7 = this.a00 * n3 + this.a01 * n4 + this.constA;
        float n8 = this.a10 * n3 + this.a11 * n4 + this.constB;
        final float gDeltaDelta = this.gDeltaDelta;
        n2 += n5;
        final int n9 = this.gradient[this.fastGradientArraySize];
        for (int i = 0; i < n6; ++i) {
            float n10;
            float n11;
            int j;
            for (n10 = (n7 * n7 + n8 * n8) / this.radiusSq, n11 = 2.0f * (this.a00 * n7 + this.a10 * n8) / this.radiusSq + gDeltaDelta / 2.0f, j = 0; j < n5 && n10 >= 1.0f; n10 += n11, n11 += gDeltaDelta, ++j) {
                array[n + j] = n9;
            }
            while (j < n5 && n10 < 1.0f) {
                int n12;
                if (n10 <= 0.0f) {
                    n12 = 0;
                }
                else {
                    final float n13 = n10 * 2048.0f;
                    final int n14 = (int)n13;
                    final float n15 = RadialGradientPaintContext.sqrtLut[n14];
                    n12 = (int)((n15 + (n13 - n14) * (RadialGradientPaintContext.sqrtLut[n14 + 1] - n15)) * this.fastGradientArraySize);
                }
                array[n + j] = this.gradient[n12];
                n10 += n11;
                n11 += gDeltaDelta;
                ++j;
            }
            while (j < n5) {
                array[n + j] = n9;
                ++j;
            }
            n += n2;
            n7 += this.a01;
            n8 += this.a11;
        }
    }
    
    private void cyclicCircularGradientFillRaster(final int[] array, final int n, final int n2, final int n3, final int n4, final int n5, final int n6) {
        final double n7 = -this.radiusSq + this.centerX * this.centerX + this.centerY * this.centerY;
        final float n8 = this.a00 * n3 + this.a01 * n4 + this.a02;
        final float n9 = this.a10 * n3 + this.a11 * n4 + this.a12;
        final float n10 = 2.0f * this.centerY;
        final float n11 = -2.0f * this.centerX;
        int n12 = n;
        final int n13 = n5 + n2;
        for (int i = 0; i < n6; ++i) {
            float n14 = this.a01 * i + n8;
            float n15 = this.a11 * i + n9;
            for (int j = 0; j < n5; ++j) {
                double n16;
                double n17;
                if (n14 == this.focusX) {
                    n16 = this.focusX;
                    n17 = this.centerY + ((n15 > this.focusY) ? this.trivial : ((double)(-this.trivial)));
                }
                else {
                    final double n18 = (n15 - this.focusY) / (n14 - this.focusX);
                    final double n19 = n15 - n18 * n14;
                    final double n20 = n18 * n18 + 1.0;
                    final double n21 = n11 + -2.0 * n18 * (this.centerY - n19);
                    final float n22 = (float)Math.sqrt(n21 * n21 - 4.0 * n20 * (n7 + n19 * (n19 - n10)));
                    n16 = (-n21 + ((n14 < this.focusX) ? (-n22) : ((double)n22))) / (2.0 * n20);
                    n17 = n18 * n16 + n19;
                }
                final float n23 = n14 - this.focusX;
                final float n24 = n23 * n23;
                final float n25 = n15 - this.focusY;
                final float n26 = n24 + n25 * n25;
                final float n27 = (float)n16 - this.focusX;
                final float n28 = n27 * n27;
                final float n29 = (float)n17 - this.focusY;
                array[n12 + j] = this.indexIntoGradientsArrays((float)Math.sqrt(n26 / (n28 + n29 * n29)));
                n14 += this.a00;
                n15 += this.a10;
            }
            n12 += n13;
        }
    }
    
    static {
        RadialGradientPaintContext.sqrtLut = new float[2049];
        for (int i = 0; i < RadialGradientPaintContext.sqrtLut.length; ++i) {
            RadialGradientPaintContext.sqrtLut[i] = (float)Math.sqrt(i / 2048.0f);
        }
    }
}
