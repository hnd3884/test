package java.awt;

import sun.awt.image.SunWritableRaster;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import sun.awt.image.ByteInterleavedRaster;
import sun.awt.image.IntegerInterleavedRaster;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.lang.ref.WeakReference;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;

abstract class TexturePaintContext implements PaintContext
{
    public static ColorModel xrgbmodel;
    public static ColorModel argbmodel;
    ColorModel colorModel;
    int bWidth;
    int bHeight;
    int maxWidth;
    WritableRaster outRas;
    double xOrg;
    double yOrg;
    double incXAcross;
    double incYAcross;
    double incXDown;
    double incYDown;
    int colincx;
    int colincy;
    int colincxerr;
    int colincyerr;
    int rowincx;
    int rowincy;
    int rowincxerr;
    int rowincyerr;
    private static WeakReference<Raster> xrgbRasRef;
    private static WeakReference<Raster> argbRasRef;
    private static WeakReference<Raster> byteRasRef;
    
    public static PaintContext getContext(final BufferedImage bufferedImage, final AffineTransform affineTransform, final RenderingHints renderingHints, final Rectangle rectangle) {
        final WritableRaster raster = bufferedImage.getRaster();
        final ColorModel colorModel = bufferedImage.getColorModel();
        final int width = rectangle.width;
        final Object value = renderingHints.get(RenderingHints.KEY_INTERPOLATION);
        final boolean b = (value == null) ? (renderingHints.get(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY) : (value != RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        if (raster instanceof IntegerInterleavedRaster && (!b || isFilterableDCM(colorModel))) {
            final IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)raster;
            if (integerInterleavedRaster.getNumDataElements() == 1 && integerInterleavedRaster.getPixelStride() == 1) {
                return new Int(integerInterleavedRaster, colorModel, affineTransform, width, b);
            }
        }
        else if (raster instanceof ByteInterleavedRaster) {
            final ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)raster;
            if (byteInterleavedRaster.getNumDataElements() == 1 && byteInterleavedRaster.getPixelStride() == 1) {
                if (!b) {
                    return new Byte(byteInterleavedRaster, colorModel, affineTransform, width);
                }
                if (isFilterableICM(colorModel)) {
                    return new ByteFilter(byteInterleavedRaster, colorModel, affineTransform, width);
                }
            }
        }
        return new Any(raster, colorModel, affineTransform, width, b);
    }
    
    public static boolean isFilterableICM(final ColorModel colorModel) {
        return colorModel instanceof IndexColorModel && ((IndexColorModel)colorModel).getMapSize() <= 256;
    }
    
    public static boolean isFilterableDCM(final ColorModel colorModel) {
        if (colorModel instanceof DirectColorModel) {
            final DirectColorModel directColorModel = (DirectColorModel)colorModel;
            return isMaskOK(directColorModel.getAlphaMask(), true) && isMaskOK(directColorModel.getRedMask(), false) && isMaskOK(directColorModel.getGreenMask(), false) && isMaskOK(directColorModel.getBlueMask(), false);
        }
        return false;
    }
    
    public static boolean isMaskOK(final int n, final boolean b) {
        return (b && n == 0) || n == 255 || n == 65280 || n == 16711680 || n == -16777216;
    }
    
    public static ColorModel getInternedColorModel(final ColorModel colorModel) {
        if (TexturePaintContext.xrgbmodel == colorModel || TexturePaintContext.xrgbmodel.equals(colorModel)) {
            return TexturePaintContext.xrgbmodel;
        }
        if (TexturePaintContext.argbmodel == colorModel || TexturePaintContext.argbmodel.equals(colorModel)) {
            return TexturePaintContext.argbmodel;
        }
        return colorModel;
    }
    
    TexturePaintContext(final ColorModel colorModel, AffineTransform inverse, final int bWidth, final int bHeight, final int maxWidth) {
        this.colorModel = getInternedColorModel(colorModel);
        this.bWidth = bWidth;
        this.bHeight = bHeight;
        this.maxWidth = maxWidth;
        try {
            inverse = inverse.createInverse();
        }
        catch (final NoninvertibleTransformException ex) {
            inverse.setToScale(0.0, 0.0);
        }
        this.incXAcross = mod(inverse.getScaleX(), bWidth);
        this.incYAcross = mod(inverse.getShearY(), bHeight);
        this.incXDown = mod(inverse.getShearX(), bWidth);
        this.incYDown = mod(inverse.getScaleY(), bHeight);
        this.xOrg = inverse.getTranslateX();
        this.yOrg = inverse.getTranslateY();
        this.colincx = (int)this.incXAcross;
        this.colincy = (int)this.incYAcross;
        this.colincxerr = fractAsInt(this.incXAcross);
        this.colincyerr = fractAsInt(this.incYAcross);
        this.rowincx = (int)this.incXDown;
        this.rowincy = (int)this.incYDown;
        this.rowincxerr = fractAsInt(this.incXDown);
        this.rowincyerr = fractAsInt(this.incYDown);
    }
    
    static int fractAsInt(final double n) {
        return (int)(n % 1.0 * 2.147483647E9);
    }
    
    static double mod(double n, final double n2) {
        n %= n2;
        if (n < 0.0) {
            n += n2;
            if (n >= n2) {
                n = 0.0;
            }
        }
        return n;
    }
    
    @Override
    public void dispose() {
        dropRaster(this.colorModel, this.outRas);
    }
    
    @Override
    public ColorModel getColorModel() {
        return this.colorModel;
    }
    
    @Override
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        if (this.outRas == null || this.outRas.getWidth() < n3 || this.outRas.getHeight() < n4) {
            this.outRas = this.makeRaster((n4 == 1) ? Math.max(n3, this.maxWidth) : n3, n4);
        }
        final double mod = mod(this.xOrg + n * this.incXAcross + n2 * this.incXDown, this.bWidth);
        final double mod2 = mod(this.yOrg + n * this.incYAcross + n2 * this.incYDown, this.bHeight);
        this.setRaster((int)mod, (int)mod2, fractAsInt(mod), fractAsInt(mod2), n3, n4, this.bWidth, this.bHeight, this.colincx, this.colincxerr, this.colincy, this.colincyerr, this.rowincx, this.rowincxerr, this.rowincy, this.rowincyerr);
        SunWritableRaster.markDirty(this.outRas);
        return this.outRas;
    }
    
    static synchronized WritableRaster makeRaster(final ColorModel colorModel, final Raster raster, int n, int n2) {
        if (TexturePaintContext.xrgbmodel == colorModel) {
            if (TexturePaintContext.xrgbRasRef != null) {
                final WritableRaster writableRaster = TexturePaintContext.xrgbRasRef.get();
                if (writableRaster != null && writableRaster.getWidth() >= n && writableRaster.getHeight() >= n2) {
                    TexturePaintContext.xrgbRasRef = null;
                    return writableRaster;
                }
            }
            if (n <= 32 && n2 <= 32) {
                n2 = (n = 32);
            }
        }
        else if (TexturePaintContext.argbmodel == colorModel) {
            if (TexturePaintContext.argbRasRef != null) {
                final WritableRaster writableRaster2 = TexturePaintContext.argbRasRef.get();
                if (writableRaster2 != null && writableRaster2.getWidth() >= n && writableRaster2.getHeight() >= n2) {
                    TexturePaintContext.argbRasRef = null;
                    return writableRaster2;
                }
            }
            if (n <= 32 && n2 <= 32) {
                n2 = (n = 32);
            }
        }
        if (raster != null) {
            return raster.createCompatibleWritableRaster(n, n2);
        }
        return colorModel.createCompatibleWritableRaster(n, n2);
    }
    
    static synchronized void dropRaster(final ColorModel colorModel, final Raster raster) {
        if (raster == null) {
            return;
        }
        if (TexturePaintContext.xrgbmodel == colorModel) {
            TexturePaintContext.xrgbRasRef = new WeakReference<Raster>(raster);
        }
        else if (TexturePaintContext.argbmodel == colorModel) {
            TexturePaintContext.argbRasRef = new WeakReference<Raster>(raster);
        }
    }
    
    static synchronized WritableRaster makeByteRaster(final Raster raster, int n, int n2) {
        if (TexturePaintContext.byteRasRef != null) {
            final WritableRaster writableRaster = TexturePaintContext.byteRasRef.get();
            if (writableRaster != null && writableRaster.getWidth() >= n && writableRaster.getHeight() >= n2) {
                TexturePaintContext.byteRasRef = null;
                return writableRaster;
            }
        }
        if (n <= 32 && n2 <= 32) {
            n2 = (n = 32);
        }
        return raster.createCompatibleWritableRaster(n, n2);
    }
    
    static synchronized void dropByteRaster(final Raster raster) {
        if (raster == null) {
            return;
        }
        TexturePaintContext.byteRasRef = new WeakReference<Raster>(raster);
    }
    
    public abstract WritableRaster makeRaster(final int p0, final int p1);
    
    public abstract void setRaster(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final int p12, final int p13, final int p14, final int p15);
    
    public static int blend(final int[] array, int n, int n2) {
        n >>>= 19;
        n2 >>>= 19;
        int n6;
        int n5;
        int n4;
        int n3 = n4 = (n5 = (n6 = 0));
        for (int i = 0; i < 4; ++i) {
            final int n7 = array[i];
            n = 4096 - n;
            if ((i & 0x1) == 0x0) {
                n2 = 4096 - n2;
            }
            final int n8 = n * n2;
            if (n8 != 0) {
                n4 += (n7 >>> 24) * n8;
                n3 += (n7 >>> 16 & 0xFF) * n8;
                n5 += (n7 >>> 8 & 0xFF) * n8;
                n6 += (n7 & 0xFF) * n8;
            }
        }
        return n4 + 8388608 >>> 24 << 24 | n3 + 8388608 >>> 24 << 16 | n5 + 8388608 >>> 24 << 8 | n6 + 8388608 >>> 24;
    }
    
    static {
        TexturePaintContext.xrgbmodel = new DirectColorModel(24, 16711680, 65280, 255);
        TexturePaintContext.argbmodel = ColorModel.getRGBdefault();
    }
    
    static class Int extends TexturePaintContext
    {
        IntegerInterleavedRaster srcRas;
        int[] inData;
        int inOff;
        int inSpan;
        int[] outData;
        int outOff;
        int outSpan;
        boolean filter;
        
        public Int(final IntegerInterleavedRaster srcRas, final ColorModel colorModel, final AffineTransform affineTransform, final int n, final boolean filter) {
            super(colorModel, affineTransform, srcRas.getWidth(), srcRas.getHeight(), n);
            this.srcRas = srcRas;
            this.inData = srcRas.getDataStorage();
            this.inSpan = srcRas.getScanlineStride();
            this.inOff = srcRas.getDataOffset(0);
            this.filter = filter;
        }
        
        @Override
        public WritableRaster makeRaster(final int n, final int n2) {
            final WritableRaster raster = TexturePaintContext.makeRaster(this.colorModel, this.srcRas, n, n2);
            final IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)raster;
            this.outData = integerInterleavedRaster.getDataStorage();
            this.outSpan = integerInterleavedRaster.getScanlineStride();
            this.outOff = integerInterleavedRaster.getDataOffset(0);
            return raster;
        }
        
        @Override
        public void setRaster(int n, int n2, int n3, int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final int n11, final int n12, final int n13, final int n14, final int n15, final int n16) {
            final int[] inData = this.inData;
            final int[] outData = this.outData;
            int outOff = this.outOff;
            final int inSpan = this.inSpan;
            final int inOff = this.inOff;
            int outSpan = this.outSpan;
            final boolean filter = this.filter;
            final boolean b = n9 == 1 && n10 == 0 && n11 == 0 && n12 == 0 && !filter;
            int n17 = n;
            int n18 = n2;
            int n19 = n3;
            int n20 = n4;
            if (b) {
                outSpan -= n5;
            }
            final int[] array = (int[])(filter ? new int[4] : null);
            for (int i = 0; i < n6; ++i) {
                if (b) {
                    final int n21 = inOff + n18 * inSpan + n7;
                    n = n7 - n17;
                    outOff += n5;
                    if (n7 >= 32) {
                        int j = n5;
                        while (j > 0) {
                            final int n22 = (j < n) ? j : n;
                            System.arraycopy(inData, n21 - n, outData, outOff - j, n22);
                            j -= n22;
                            if ((n -= n22) == 0) {
                                n = n7;
                            }
                        }
                    }
                    else {
                        for (int k = n5; k > 0; --k) {
                            outData[outOff - k] = inData[n21 - n];
                            if (--n == 0) {
                                n = n7;
                            }
                        }
                    }
                }
                else {
                    n = n17;
                    n2 = n18;
                    n3 = n19;
                    n4 = n20;
                    for (int l = 0; l < n5; ++l) {
                        if (filter) {
                            int n23;
                            if ((n23 = n + 1) >= n7) {
                                n23 = 0;
                            }
                            int n24;
                            if ((n24 = n2 + 1) >= n8) {
                                n24 = 0;
                            }
                            array[0] = inData[inOff + n2 * inSpan + n];
                            array[1] = inData[inOff + n2 * inSpan + n23];
                            array[2] = inData[inOff + n24 * inSpan + n];
                            array[3] = inData[inOff + n24 * inSpan + n23];
                            outData[outOff + l] = TexturePaintContext.blend(array, n3, n4);
                        }
                        else {
                            outData[outOff + l] = inData[inOff + n2 * inSpan + n];
                        }
                        if ((n3 += n10) < 0) {
                            n3 &= Integer.MAX_VALUE;
                            ++n;
                        }
                        if ((n += n9) >= n7) {
                            n -= n7;
                        }
                        if ((n4 += n12) < 0) {
                            n4 &= Integer.MAX_VALUE;
                            ++n2;
                        }
                        if ((n2 += n11) >= n8) {
                            n2 -= n8;
                        }
                    }
                }
                if ((n19 += n14) < 0) {
                    n19 &= Integer.MAX_VALUE;
                    ++n17;
                }
                if ((n17 += n13) >= n7) {
                    n17 -= n7;
                }
                if ((n20 += n16) < 0) {
                    n20 &= Integer.MAX_VALUE;
                    ++n18;
                }
                if ((n18 += n15) >= n8) {
                    n18 -= n8;
                }
                outOff += outSpan;
            }
        }
    }
    
    static class Byte extends TexturePaintContext
    {
        ByteInterleavedRaster srcRas;
        byte[] inData;
        int inOff;
        int inSpan;
        byte[] outData;
        int outOff;
        int outSpan;
        
        public Byte(final ByteInterleavedRaster srcRas, final ColorModel colorModel, final AffineTransform affineTransform, final int n) {
            super(colorModel, affineTransform, srcRas.getWidth(), srcRas.getHeight(), n);
            this.srcRas = srcRas;
            this.inData = srcRas.getDataStorage();
            this.inSpan = srcRas.getScanlineStride();
            this.inOff = srcRas.getDataOffset(0);
        }
        
        @Override
        public WritableRaster makeRaster(final int n, final int n2) {
            final WritableRaster byteRaster = TexturePaintContext.makeByteRaster(this.srcRas, n, n2);
            final ByteInterleavedRaster byteInterleavedRaster = (ByteInterleavedRaster)byteRaster;
            this.outData = byteInterleavedRaster.getDataStorage();
            this.outSpan = byteInterleavedRaster.getScanlineStride();
            this.outOff = byteInterleavedRaster.getDataOffset(0);
            return byteRaster;
        }
        
        @Override
        public void dispose() {
            TexturePaintContext.dropByteRaster(this.outRas);
        }
        
        @Override
        public void setRaster(int n, int n2, int n3, int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final int n11, final int n12, final int n13, final int n14, final int n15, final int n16) {
            final byte[] inData = this.inData;
            final byte[] outData = this.outData;
            int outOff = this.outOff;
            final int inSpan = this.inSpan;
            final int inOff = this.inOff;
            int outSpan = this.outSpan;
            final boolean b = n9 == 1 && n10 == 0 && n11 == 0 && n12 == 0;
            int n17 = n;
            int n18 = n2;
            int n19 = n3;
            int n20 = n4;
            if (b) {
                outSpan -= n5;
            }
            for (int i = 0; i < n6; ++i) {
                if (b) {
                    final int n21 = inOff + n18 * inSpan + n7;
                    n = n7 - n17;
                    outOff += n5;
                    if (n7 >= 32) {
                        int j = n5;
                        while (j > 0) {
                            final int n22 = (j < n) ? j : n;
                            System.arraycopy(inData, n21 - n, outData, outOff - j, n22);
                            j -= n22;
                            if ((n -= n22) == 0) {
                                n = n7;
                            }
                        }
                    }
                    else {
                        for (int k = n5; k > 0; --k) {
                            outData[outOff - k] = inData[n21 - n];
                            if (--n == 0) {
                                n = n7;
                            }
                        }
                    }
                }
                else {
                    n = n17;
                    n2 = n18;
                    n3 = n19;
                    n4 = n20;
                    for (int l = 0; l < n5; ++l) {
                        outData[outOff + l] = inData[inOff + n2 * inSpan + n];
                        if ((n3 += n10) < 0) {
                            n3 &= Integer.MAX_VALUE;
                            ++n;
                        }
                        if ((n += n9) >= n7) {
                            n -= n7;
                        }
                        if ((n4 += n12) < 0) {
                            n4 &= Integer.MAX_VALUE;
                            ++n2;
                        }
                        if ((n2 += n11) >= n8) {
                            n2 -= n8;
                        }
                    }
                }
                if ((n19 += n14) < 0) {
                    n19 &= Integer.MAX_VALUE;
                    ++n17;
                }
                if ((n17 += n13) >= n7) {
                    n17 -= n7;
                }
                if ((n20 += n16) < 0) {
                    n20 &= Integer.MAX_VALUE;
                    ++n18;
                }
                if ((n18 += n15) >= n8) {
                    n18 -= n8;
                }
                outOff += outSpan;
            }
        }
    }
    
    static class ByteFilter extends TexturePaintContext
    {
        ByteInterleavedRaster srcRas;
        int[] inPalette;
        byte[] inData;
        int inOff;
        int inSpan;
        int[] outData;
        int outOff;
        int outSpan;
        
        public ByteFilter(final ByteInterleavedRaster srcRas, final ColorModel colorModel, final AffineTransform affineTransform, final int n) {
            super((colorModel.getTransparency() == 1) ? ByteFilter.xrgbmodel : ByteFilter.argbmodel, affineTransform, srcRas.getWidth(), srcRas.getHeight(), n);
            this.inPalette = new int[256];
            ((IndexColorModel)colorModel).getRGBs(this.inPalette);
            this.srcRas = srcRas;
            this.inData = srcRas.getDataStorage();
            this.inSpan = srcRas.getScanlineStride();
            this.inOff = srcRas.getDataOffset(0);
        }
        
        @Override
        public WritableRaster makeRaster(final int n, final int n2) {
            final WritableRaster raster = TexturePaintContext.makeRaster(this.colorModel, null, n, n2);
            final IntegerInterleavedRaster integerInterleavedRaster = (IntegerInterleavedRaster)raster;
            this.outData = integerInterleavedRaster.getDataStorage();
            this.outSpan = integerInterleavedRaster.getScanlineStride();
            this.outOff = integerInterleavedRaster.getDataOffset(0);
            return raster;
        }
        
        @Override
        public void setRaster(int n, int n2, int n3, int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final int n11, final int n12, final int n13, final int n14, final int n15, final int n16) {
            final byte[] inData = this.inData;
            final int[] outData = this.outData;
            int outOff = this.outOff;
            final int inSpan = this.inSpan;
            final int inOff = this.inOff;
            final int outSpan = this.outSpan;
            int n17 = n;
            int n18 = n2;
            int n19 = n3;
            int n20 = n4;
            final int[] array = new int[4];
            for (int i = 0; i < n6; ++i) {
                n = n17;
                n2 = n18;
                n3 = n19;
                n4 = n20;
                for (int j = 0; j < n5; ++j) {
                    int n21;
                    if ((n21 = n + 1) >= n7) {
                        n21 = 0;
                    }
                    int n22;
                    if ((n22 = n2 + 1) >= n8) {
                        n22 = 0;
                    }
                    array[0] = this.inPalette[0xFF & inData[inOff + n + inSpan * n2]];
                    array[1] = this.inPalette[0xFF & inData[inOff + n21 + inSpan * n2]];
                    array[2] = this.inPalette[0xFF & inData[inOff + n + inSpan * n22]];
                    array[3] = this.inPalette[0xFF & inData[inOff + n21 + inSpan * n22]];
                    outData[outOff + j] = TexturePaintContext.blend(array, n3, n4);
                    if ((n3 += n10) < 0) {
                        n3 &= Integer.MAX_VALUE;
                        ++n;
                    }
                    if ((n += n9) >= n7) {
                        n -= n7;
                    }
                    if ((n4 += n12) < 0) {
                        n4 &= Integer.MAX_VALUE;
                        ++n2;
                    }
                    if ((n2 += n11) >= n8) {
                        n2 -= n8;
                    }
                }
                if ((n19 += n14) < 0) {
                    n19 &= Integer.MAX_VALUE;
                    ++n17;
                }
                if ((n17 += n13) >= n7) {
                    n17 -= n7;
                }
                if ((n20 += n16) < 0) {
                    n20 &= Integer.MAX_VALUE;
                    ++n18;
                }
                if ((n18 += n15) >= n8) {
                    n18 -= n8;
                }
                outOff += outSpan;
            }
        }
    }
    
    static class Any extends TexturePaintContext
    {
        WritableRaster srcRas;
        boolean filter;
        
        public Any(final WritableRaster srcRas, final ColorModel colorModel, final AffineTransform affineTransform, final int n, final boolean filter) {
            super(colorModel, affineTransform, srcRas.getWidth(), srcRas.getHeight(), n);
            this.srcRas = srcRas;
            this.filter = filter;
        }
        
        @Override
        public WritableRaster makeRaster(final int n, final int n2) {
            return TexturePaintContext.makeRaster(this.colorModel, this.srcRas, n, n2);
        }
        
        @Override
        public void setRaster(int n, int n2, int n3, int n4, final int n5, final int n6, final int n7, final int n8, final int n9, final int n10, final int n11, final int n12, final int n13, final int n14, final int n15, final int n16) {
            Object o = null;
            int n17 = n;
            int n18 = n2;
            int n19 = n3;
            int n20 = n4;
            final WritableRaster srcRas = this.srcRas;
            final WritableRaster outRas = this.outRas;
            final int[] array = (int[])(this.filter ? new int[4] : null);
            for (int i = 0; i < n6; ++i) {
                n = n17;
                n2 = n18;
                n3 = n19;
                n4 = n20;
                for (int j = 0; j < n5; ++j) {
                    o = srcRas.getDataElements(n, n2, o);
                    if (this.filter) {
                        int n21;
                        if ((n21 = n + 1) >= n7) {
                            n21 = 0;
                        }
                        int n22;
                        if ((n22 = n2 + 1) >= n8) {
                            n22 = 0;
                        }
                        array[0] = this.colorModel.getRGB(o);
                        final Object dataElements = srcRas.getDataElements(n21, n2, o);
                        array[1] = this.colorModel.getRGB(dataElements);
                        final Object dataElements2 = srcRas.getDataElements(n, n22, dataElements);
                        array[2] = this.colorModel.getRGB(dataElements2);
                        final Object dataElements3 = srcRas.getDataElements(n21, n22, dataElements2);
                        array[3] = this.colorModel.getRGB(dataElements3);
                        o = this.colorModel.getDataElements(TexturePaintContext.blend(array, n3, n4), dataElements3);
                    }
                    outRas.setDataElements(j, i, o);
                    if ((n3 += n10) < 0) {
                        n3 &= Integer.MAX_VALUE;
                        ++n;
                    }
                    if ((n += n9) >= n7) {
                        n -= n7;
                    }
                    if ((n4 += n12) < 0) {
                        n4 &= Integer.MAX_VALUE;
                        ++n2;
                    }
                    if ((n2 += n11) >= n8) {
                        n2 -= n8;
                    }
                }
                if ((n19 += n14) < 0) {
                    n19 &= Integer.MAX_VALUE;
                    ++n17;
                }
                if ((n17 += n13) >= n7) {
                    n17 -= n7;
                }
                if ((n20 += n16) < 0) {
                    n20 &= Integer.MAX_VALUE;
                    ++n18;
                }
                if ((n18 += n15) >= n8) {
                    n18 -= n8;
                }
            }
        }
    }
}
