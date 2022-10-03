package java.awt;

import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.DataBufferInt;
import java.lang.ref.SoftReference;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import java.awt.image.ColorModel;

abstract class MultipleGradientPaintContext implements PaintContext
{
    protected ColorModel model;
    private static ColorModel xrgbmodel;
    protected static ColorModel cachedModel;
    protected static WeakReference<Raster> cached;
    protected Raster saved;
    protected MultipleGradientPaint.CycleMethod cycleMethod;
    protected MultipleGradientPaint.ColorSpaceType colorSpace;
    protected float a00;
    protected float a01;
    protected float a10;
    protected float a11;
    protected float a02;
    protected float a12;
    protected boolean isSimpleLookup;
    protected int fastGradientArraySize;
    protected int[] gradient;
    private int[][] gradients;
    private float[] normalizedIntervals;
    private float[] fractions;
    private int transparencyTest;
    private static final int[] SRGBtoLinearRGB;
    private static final int[] LinearRGBtoSRGB;
    protected static final int GRADIENT_SIZE = 256;
    protected static final int GRADIENT_SIZE_INDEX = 255;
    private static final int MAX_GRADIENT_ARRAY_SIZE = 5000;
    
    protected MultipleGradientPaintContext(final MultipleGradientPaint multipleGradientPaint, final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints, final float[] fractions, final Color[] array, final MultipleGradientPaint.CycleMethod cycleMethod, final MultipleGradientPaint.ColorSpaceType colorSpace) {
        if (rectangle == null) {
            throw new NullPointerException("Device bounds cannot be null");
        }
        if (rectangle2D == null) {
            throw new NullPointerException("User bounds cannot be null");
        }
        if (affineTransform == null) {
            throw new NullPointerException("Transform cannot be null");
        }
        if (renderingHints == null) {
            throw new NullPointerException("RenderingHints cannot be null");
        }
        AffineTransform affineTransform2;
        try {
            affineTransform.invert();
            affineTransform2 = affineTransform;
        }
        catch (final NoninvertibleTransformException ex) {
            affineTransform2 = new AffineTransform();
        }
        final double[] array2 = new double[6];
        affineTransform2.getMatrix(array2);
        this.a00 = (float)array2[0];
        this.a10 = (float)array2[1];
        this.a01 = (float)array2[2];
        this.a11 = (float)array2[3];
        this.a02 = (float)array2[4];
        this.a12 = (float)array2[5];
        this.cycleMethod = cycleMethod;
        this.colorSpace = colorSpace;
        this.fractions = fractions;
        final int[] gradient = (int[])((multipleGradientPaint.gradient != null) ? ((int[])multipleGradientPaint.gradient.get()) : null);
        final int[][] gradients = (int[][])((multipleGradientPaint.gradients != null) ? multipleGradientPaint.gradients.get() : null);
        if (gradient == null && gradients == null) {
            this.calculateLookupData(array);
            multipleGradientPaint.model = this.model;
            multipleGradientPaint.normalizedIntervals = this.normalizedIntervals;
            multipleGradientPaint.isSimpleLookup = this.isSimpleLookup;
            if (this.isSimpleLookup) {
                multipleGradientPaint.fastGradientArraySize = this.fastGradientArraySize;
                multipleGradientPaint.gradient = new SoftReference<int[]>(this.gradient);
            }
            else {
                multipleGradientPaint.gradients = new SoftReference<int[][]>(this.gradients);
            }
        }
        else {
            this.model = multipleGradientPaint.model;
            this.normalizedIntervals = multipleGradientPaint.normalizedIntervals;
            this.isSimpleLookup = multipleGradientPaint.isSimpleLookup;
            this.gradient = gradient;
            this.fastGradientArraySize = multipleGradientPaint.fastGradientArraySize;
            this.gradients = gradients;
        }
    }
    
    private void calculateLookupData(final Color[] array) {
        Color[] array2;
        if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
            array2 = new Color[array.length];
            for (int i = 0; i < array.length; ++i) {
                final int rgb = array[i].getRGB();
                array2[i] = new Color(MultipleGradientPaintContext.SRGBtoLinearRGB[rgb >> 16 & 0xFF], MultipleGradientPaintContext.SRGBtoLinearRGB[rgb >> 8 & 0xFF], MultipleGradientPaintContext.SRGBtoLinearRGB[rgb & 0xFF], rgb >>> 24);
            }
        }
        else {
            array2 = array;
        }
        this.normalizedIntervals = new float[this.fractions.length - 1];
        for (int j = 0; j < this.normalizedIntervals.length; ++j) {
            this.normalizedIntervals[j] = this.fractions[j + 1] - this.fractions[j];
        }
        this.transparencyTest = -16777216;
        this.gradients = new int[this.normalizedIntervals.length][];
        float n = 1.0f;
        for (int k = 0; k < this.normalizedIntervals.length; ++k) {
            n = ((n > this.normalizedIntervals[k]) ? this.normalizedIntervals[k] : n);
        }
        int n2 = 0;
        for (int l = 0; l < this.normalizedIntervals.length; ++l) {
            n2 += (int)(this.normalizedIntervals[l] / n * 256.0f);
        }
        if (n2 > 5000) {
            this.calculateMultipleArrayGradient(array2);
        }
        else {
            this.calculateSingleArrayGradient(array2, n);
        }
        if (this.transparencyTest >>> 24 == 255) {
            this.model = MultipleGradientPaintContext.xrgbmodel;
        }
        else {
            this.model = ColorModel.getRGBdefault();
        }
    }
    
    private void calculateSingleArrayGradient(final Color[] array, final float n) {
        this.isSimpleLookup = true;
        int n2 = 1;
        for (int i = 0; i < this.gradients.length; ++i) {
            final int n3 = (int)(this.normalizedIntervals[i] / n * 255.0f);
            n2 += n3;
            this.gradients[i] = new int[n3];
            final int rgb = array[i].getRGB();
            final int rgb2 = array[i + 1].getRGB();
            this.interpolate(rgb, rgb2, this.gradients[i]);
            this.transparencyTest &= rgb;
            this.transparencyTest &= rgb2;
        }
        this.gradient = new int[n2];
        int n4 = 0;
        for (int j = 0; j < this.gradients.length; ++j) {
            System.arraycopy(this.gradients[j], 0, this.gradient, n4, this.gradients[j].length);
            n4 += this.gradients[j].length;
        }
        this.gradient[this.gradient.length - 1] = array[array.length - 1].getRGB();
        if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
            for (int k = 0; k < this.gradient.length; ++k) {
                this.gradient[k] = this.convertEntireColorLinearRGBtoSRGB(this.gradient[k]);
            }
        }
        this.fastGradientArraySize = this.gradient.length - 1;
    }
    
    private void calculateMultipleArrayGradient(final Color[] array) {
        this.isSimpleLookup = false;
        for (int i = 0; i < this.gradients.length; ++i) {
            this.gradients[i] = new int[256];
            final int rgb = array[i].getRGB();
            final int rgb2 = array[i + 1].getRGB();
            this.interpolate(rgb, rgb2, this.gradients[i]);
            this.transparencyTest &= rgb;
            this.transparencyTest &= rgb2;
        }
        if (this.colorSpace == MultipleGradientPaint.ColorSpaceType.LINEAR_RGB) {
            for (int j = 0; j < this.gradients.length; ++j) {
                for (int k = 0; k < this.gradients[j].length; ++k) {
                    this.gradients[j][k] = this.convertEntireColorLinearRGBtoSRGB(this.gradients[j][k]);
                }
            }
        }
    }
    
    private void interpolate(final int n, final int n2, final int[] array) {
        final float n3 = 1.0f / array.length;
        final int n4 = n >> 24 & 0xFF;
        final int n5 = n >> 16 & 0xFF;
        final int n6 = n >> 8 & 0xFF;
        final int n7 = n & 0xFF;
        final int n8 = (n2 >> 24 & 0xFF) - n4;
        final int n9 = (n2 >> 16 & 0xFF) - n5;
        final int n10 = (n2 >> 8 & 0xFF) - n6;
        final int n11 = (n2 & 0xFF) - n7;
        for (int i = 0; i < array.length; ++i) {
            array[i] = ((int)(n4 + i * n8 * n3 + 0.5) << 24 | (int)(n5 + i * n9 * n3 + 0.5) << 16 | (int)(n6 + i * n10 * n3 + 0.5) << 8 | (int)(n7 + i * n11 * n3 + 0.5));
        }
    }
    
    private int convertEntireColorLinearRGBtoSRGB(final int n) {
        return (n >> 24 & 0xFF) << 24 | MultipleGradientPaintContext.LinearRGBtoSRGB[n >> 16 & 0xFF] << 16 | MultipleGradientPaintContext.LinearRGBtoSRGB[n >> 8 & 0xFF] << 8 | MultipleGradientPaintContext.LinearRGBtoSRGB[n & 0xFF];
    }
    
    protected final int indexIntoGradientsArrays(float n) {
        if (this.cycleMethod == MultipleGradientPaint.CycleMethod.NO_CYCLE) {
            if (n > 1.0f) {
                n = 1.0f;
            }
            else if (n < 0.0f) {
                n = 0.0f;
            }
        }
        else if (this.cycleMethod == MultipleGradientPaint.CycleMethod.REPEAT) {
            n -= (int)n;
            if (n < 0.0f) {
                ++n;
            }
        }
        else {
            if (n < 0.0f) {
                n = -n;
            }
            final int n2 = (int)n;
            n -= n2;
            if ((n2 & 0x1) == 0x1) {
                n = 1.0f - n;
            }
        }
        if (this.isSimpleLookup) {
            return this.gradient[(int)(n * this.fastGradientArraySize)];
        }
        for (int i = 0; i < this.gradients.length; ++i) {
            if (n < this.fractions[i + 1]) {
                return this.gradients[i][(int)((n - this.fractions[i]) / this.normalizedIntervals[i] * 255.0f)];
            }
        }
        return this.gradients[this.gradients.length - 1][255];
    }
    
    private static int convertSRGBtoLinearRGB(final int n) {
        final float n2 = n / 255.0f;
        float n3;
        if (n2 <= 0.04045f) {
            n3 = n2 / 12.92f;
        }
        else {
            n3 = (float)Math.pow((n2 + 0.055) / 1.055, 2.4);
        }
        return Math.round(n3 * 255.0f);
    }
    
    private static int convertLinearRGBtoSRGB(final int n) {
        final float n2 = n / 255.0f;
        float n3;
        if (n2 <= 0.0031308) {
            n3 = n2 * 12.92f;
        }
        else {
            n3 = 1.055f * (float)Math.pow(n2, 0.4166666666666667) - 0.055f;
        }
        return Math.round(n3 * 255.0f);
    }
    
    @Override
    public final Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        Raster saved = this.saved;
        if (saved == null || saved.getWidth() < n3 || saved.getHeight() < n4) {
            saved = getCachedRaster(this.model, n3, n4);
            this.saved = saved;
        }
        final DataBufferInt dataBufferInt = (DataBufferInt)saved.getDataBuffer();
        this.fillRaster(dataBufferInt.getData(0), dataBufferInt.getOffset(), ((SinglePixelPackedSampleModel)saved.getSampleModel()).getScanlineStride() - n3, n, n2, n3, n4);
        return saved;
    }
    
    protected abstract void fillRaster(final int[] p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6);
    
    private static synchronized Raster getCachedRaster(final ColorModel colorModel, final int n, final int n2) {
        if (colorModel == MultipleGradientPaintContext.cachedModel && MultipleGradientPaintContext.cached != null) {
            final Raster raster = MultipleGradientPaintContext.cached.get();
            if (raster != null && raster.getWidth() >= n && raster.getHeight() >= n2) {
                MultipleGradientPaintContext.cached = null;
                return raster;
            }
        }
        return colorModel.createCompatibleWritableRaster(n, n2);
    }
    
    private static synchronized void putCachedRaster(final ColorModel cachedModel, final Raster raster) {
        if (MultipleGradientPaintContext.cached != null) {
            final Raster raster2 = MultipleGradientPaintContext.cached.get();
            if (raster2 != null) {
                final int width = raster2.getWidth();
                final int height = raster2.getHeight();
                final int width2 = raster.getWidth();
                final int height2 = raster.getHeight();
                if (width >= width2 && height >= height2) {
                    return;
                }
                if (width * height >= width2 * height2) {
                    return;
                }
            }
        }
        MultipleGradientPaintContext.cachedModel = cachedModel;
        MultipleGradientPaintContext.cached = new WeakReference<Raster>(raster);
    }
    
    @Override
    public final void dispose() {
        if (this.saved != null) {
            putCachedRaster(this.model, this.saved);
            this.saved = null;
        }
    }
    
    @Override
    public final ColorModel getColorModel() {
        return this.model;
    }
    
    static {
        MultipleGradientPaintContext.xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
        SRGBtoLinearRGB = new int[256];
        LinearRGBtoSRGB = new int[256];
        for (int i = 0; i < 256; ++i) {
            MultipleGradientPaintContext.SRGBtoLinearRGB[i] = convertSRGBtoLinearRGB(i);
            MultipleGradientPaintContext.LinearRGBtoSRGB[i] = convertLinearRGBtoSRGB(i);
        }
    }
}
