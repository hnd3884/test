package java.awt;

import sun.awt.image.IntegerComponentRaster;
import java.awt.image.DirectColorModel;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import java.awt.image.ColorModel;

class GradientPaintContext implements PaintContext
{
    static ColorModel xrgbmodel;
    static ColorModel xbgrmodel;
    static ColorModel cachedModel;
    static WeakReference<Raster> cached;
    double x1;
    double y1;
    double dx;
    double dy;
    boolean cyclic;
    int[] interp;
    Raster saved;
    ColorModel model;
    
    static synchronized Raster getCachedRaster(final ColorModel colorModel, final int n, final int n2) {
        if (colorModel == GradientPaintContext.cachedModel && GradientPaintContext.cached != null) {
            final Raster raster = GradientPaintContext.cached.get();
            if (raster != null && raster.getWidth() >= n && raster.getHeight() >= n2) {
                GradientPaintContext.cached = null;
                return raster;
            }
        }
        return colorModel.createCompatibleWritableRaster(n, n2);
    }
    
    static synchronized void putCachedRaster(final ColorModel cachedModel, final Raster raster) {
        if (GradientPaintContext.cached != null) {
            final Raster raster2 = GradientPaintContext.cached.get();
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
        GradientPaintContext.cachedModel = cachedModel;
        GradientPaintContext.cached = new WeakReference<Raster>(raster);
    }
    
    public GradientPaintContext(final ColorModel colorModel, Point2D point2D, Point2D point2D2, final AffineTransform affineTransform, Color color, Color color2, final boolean cyclic) {
        final Point2D.Double double1 = new Point2D.Double(1.0, 0.0);
        final Point2D.Double double2 = new Point2D.Double(0.0, 1.0);
        try {
            final AffineTransform inverse = affineTransform.createInverse();
            inverse.deltaTransform(double1, double1);
            inverse.deltaTransform(double2, double2);
        }
        catch (final NoninvertibleTransformException ex) {
            double1.setLocation(0.0, 0.0);
            double2.setLocation(0.0, 0.0);
        }
        final double n = point2D2.getX() - point2D.getX();
        final double n2 = point2D2.getY() - point2D.getY();
        final double n3 = n * n + n2 * n2;
        if (n3 <= Double.MIN_VALUE) {
            this.dx = 0.0;
            this.dy = 0.0;
        }
        else {
            this.dx = (double1.getX() * n + double1.getY() * n2) / n3;
            this.dy = (double2.getX() * n + double2.getY() * n2) / n3;
            if (cyclic) {
                this.dx %= 1.0;
                this.dy %= 1.0;
            }
            else if (this.dx < 0.0) {
                final Point2D point2D3 = point2D;
                point2D = point2D2;
                point2D2 = point2D3;
                final Color color3 = color;
                color = color2;
                color2 = color3;
                this.dx = -this.dx;
                this.dy = -this.dy;
            }
        }
        final Point2D transform = affineTransform.transform(point2D, null);
        this.x1 = transform.getX();
        this.y1 = transform.getY();
        this.cyclic = cyclic;
        final int rgb = color.getRGB();
        final int rgb2 = color2.getRGB();
        final int n4 = rgb >> 24 & 0xFF;
        int n5 = rgb >> 16 & 0xFF;
        final int n6 = rgb >> 8 & 0xFF;
        int n7 = rgb & 0xFF;
        final int n8 = (rgb2 >> 24 & 0xFF) - n4;
        int n9 = (rgb2 >> 16 & 0xFF) - n5;
        final int n10 = (rgb2 >> 8 & 0xFF) - n6;
        int n11 = (rgb2 & 0xFF) - n7;
        if (n4 == 255 && n8 == 0) {
            this.model = GradientPaintContext.xrgbmodel;
            if (colorModel instanceof DirectColorModel) {
                final DirectColorModel directColorModel = (DirectColorModel)colorModel;
                final int alphaMask = directColorModel.getAlphaMask();
                if ((alphaMask == 0 || alphaMask == 255) && directColorModel.getRedMask() == 255 && directColorModel.getGreenMask() == 65280 && directColorModel.getBlueMask() == 16711680) {
                    this.model = GradientPaintContext.xbgrmodel;
                    final int n12 = n5;
                    n5 = n7;
                    n7 = n12;
                    final int n13 = n9;
                    n9 = n11;
                    n11 = n13;
                }
            }
        }
        else {
            this.model = ColorModel.getRGBdefault();
        }
        this.interp = new int[cyclic ? 513 : 257];
        for (int i = 0; i <= 256; ++i) {
            final float n14 = i / 256.0f;
            final int n15 = (int)(n4 + n8 * n14) << 24 | (int)(n5 + n9 * n14) << 16 | (int)(n6 + n10 * n14) << 8 | (int)(n7 + n11 * n14);
            this.interp[i] = n15;
            if (cyclic) {
                this.interp[512 - i] = n15;
            }
        }
    }
    
    @Override
    public void dispose() {
        if (this.saved != null) {
            putCachedRaster(this.model, this.saved);
            this.saved = null;
        }
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.model;
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        final double n5 = (n - this.x1) * this.dx + (n2 - this.y1) * this.dy;
        Raster saved = this.saved;
        if (saved == null || saved.getWidth() < n3 || saved.getHeight() < n4) {
            saved = getCachedRaster(this.model, n3, n4);
            this.saved = saved;
        }
        final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)saved;
        final int dataOffset = integerComponentRaster.getDataOffset(0);
        final int n6 = integerComponentRaster.getScanlineStride() - n3;
        final int[] dataStorage = integerComponentRaster.getDataStorage();
        if (this.cyclic) {
            this.cycleFillRaster(dataStorage, dataOffset, n6, n3, n4, n5, this.dx, this.dy);
        }
        else {
            this.clipFillRaster(dataStorage, dataOffset, n6, n3, n4, n5, this.dx, this.dy);
        }
        integerComponentRaster.markDirty();
        return saved;
    }
    
    void cycleFillRaster(final int[] array, int n, final int n2, final int n3, int n4, double n5, final double n6, final double n7) {
        n5 %= 2.0;
        int n8 = (int)(n5 * 1.073741824E9) << 1;
        final int n9 = (int)(-n6 * -2.147483648E9);
        final int n10 = (int)(-n7 * -2.147483648E9);
        while (--n4 >= 0) {
            int n11 = n8;
            for (int i = n3; i > 0; --i) {
                array[n++] = this.interp[n11 >>> 23];
                n11 += n9;
            }
            n += n2;
            n8 += n10;
        }
    }
    
    void clipFillRaster(final int[] array, int n, final int n2, final int n3, int n4, double n5, final double n6, final double n7) {
        while (--n4 >= 0) {
            double n8 = n5;
            int n9 = n3;
            if (n8 <= 0.0) {
                final int n10 = this.interp[0];
                do {
                    array[n++] = n10;
                    n8 += n6;
                } while (--n9 > 0 && n8 <= 0.0);
            }
            while (n8 < 1.0 && --n9 >= 0) {
                array[n++] = this.interp[(int)(n8 * 256.0)];
                n8 += n6;
            }
            if (n9 > 0) {
                final int n11 = this.interp[256];
                do {
                    array[n++] = n11;
                } while (--n9 > 0);
            }
            n += n2;
            n5 += n7;
        }
    }
    
    static {
        GradientPaintContext.xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
        GradientPaintContext.xbgrmodel = new DirectColorModel(24, 255, 65280, 16711680);
    }
}
