package java.awt.image;

import sun.java2d.cmm.PCMM;
import java.util.Collections;
import java.util.WeakHashMap;
import sun.java2d.cmm.ColorTransform;
import sun.java2d.cmm.CMSManager;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.color.ICC_ColorSpace;
import java.util.Map;
import java.awt.color.ColorSpace;
import java.awt.Transparency;

public abstract class ColorModel implements Transparency
{
    private long pData;
    protected int pixel_bits;
    int[] nBits;
    int transparency;
    boolean supportsAlpha;
    boolean isAlphaPremultiplied;
    int numComponents;
    int numColorComponents;
    ColorSpace colorSpace;
    int colorSpaceType;
    int maxBits;
    boolean is_sRGB;
    protected int transferType;
    private static boolean loaded;
    private static ColorModel RGBdefault;
    static byte[] l8Tos8;
    static byte[] s8Tol8;
    static byte[] l16Tos8;
    static short[] s8Tol16;
    static Map<ICC_ColorSpace, byte[]> g8Tos8Map;
    static Map<ICC_ColorSpace, byte[]> lg16Toog8Map;
    static Map<ICC_ColorSpace, byte[]> g16Tos8Map;
    static Map<ICC_ColorSpace, short[]> lg16Toog16Map;
    
    static void loadLibraries() {
        if (!ColorModel.loaded) {
            AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    System.loadLibrary("awt");
                    return null;
                }
            });
            ColorModel.loaded = true;
        }
    }
    
    private static native void initIDs();
    
    public static ColorModel getRGBdefault() {
        if (ColorModel.RGBdefault == null) {
            ColorModel.RGBdefault = new DirectColorModel(32, 16711680, 65280, 255, -16777216);
        }
        return ColorModel.RGBdefault;
    }
    
    public ColorModel(final int n) {
        this.transparency = 3;
        this.supportsAlpha = true;
        this.isAlphaPremultiplied = false;
        this.numComponents = -1;
        this.numColorComponents = -1;
        this.colorSpace = ColorSpace.getInstance(1000);
        this.colorSpaceType = 5;
        this.is_sRGB = true;
        this.pixel_bits = n;
        if (n < 1) {
            throw new IllegalArgumentException("Number of bits must be > 0");
        }
        this.numComponents = 4;
        this.numColorComponents = 3;
        this.maxBits = n;
        this.transferType = getDefaultTransferType(n);
    }
    
    protected ColorModel(final int pixel_bits, final int[] array, final ColorSpace colorSpace, final boolean supportsAlpha, final boolean isAlphaPremultiplied, final int transparency, final int transferType) {
        this.transparency = 3;
        this.supportsAlpha = true;
        this.isAlphaPremultiplied = false;
        this.numComponents = -1;
        this.numColorComponents = -1;
        this.colorSpace = ColorSpace.getInstance(1000);
        this.colorSpaceType = 5;
        this.is_sRGB = true;
        this.colorSpace = colorSpace;
        this.colorSpaceType = colorSpace.getType();
        this.numColorComponents = colorSpace.getNumComponents();
        this.numComponents = this.numColorComponents + (supportsAlpha ? 1 : 0);
        this.supportsAlpha = supportsAlpha;
        if (array.length < this.numComponents) {
            throw new IllegalArgumentException("Number of color/alpha components should be " + this.numComponents + " but length of bits array is " + array.length);
        }
        if (transparency < 1 || transparency > 3) {
            throw new IllegalArgumentException("Unknown transparency: " + transparency);
        }
        if (!this.supportsAlpha) {
            this.isAlphaPremultiplied = false;
            this.transparency = 1;
        }
        else {
            this.isAlphaPremultiplied = isAlphaPremultiplied;
            this.transparency = transparency;
        }
        this.nBits = array.clone();
        this.pixel_bits = pixel_bits;
        if (pixel_bits <= 0) {
            throw new IllegalArgumentException("Number of pixel bits must be > 0");
        }
        this.maxBits = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] < 0) {
                throw new IllegalArgumentException("Number of bits must be >= 0");
            }
            if (this.maxBits < array[i]) {
                this.maxBits = array[i];
            }
        }
        if (this.maxBits == 0) {
            throw new IllegalArgumentException("There must be at least one component with > 0 pixel bits.");
        }
        if (colorSpace != ColorSpace.getInstance(1000)) {
            this.is_sRGB = false;
        }
        this.transferType = transferType;
    }
    
    public final boolean hasAlpha() {
        return this.supportsAlpha;
    }
    
    public final boolean isAlphaPremultiplied() {
        return this.isAlphaPremultiplied;
    }
    
    public final int getTransferType() {
        return this.transferType;
    }
    
    public int getPixelSize() {
        return this.pixel_bits;
    }
    
    public int getComponentSize(final int n) {
        if (this.nBits == null) {
            throw new NullPointerException("Number of bits array is null.");
        }
        return this.nBits[n];
    }
    
    public int[] getComponentSize() {
        if (this.nBits != null) {
            return this.nBits.clone();
        }
        return null;
    }
    
    @Override
    public int getTransparency() {
        return this.transparency;
    }
    
    public int getNumComponents() {
        return this.numComponents;
    }
    
    public int getNumColorComponents() {
        return this.numColorComponents;
    }
    
    public abstract int getRed(final int p0);
    
    public abstract int getGreen(final int p0);
    
    public abstract int getBlue(final int p0);
    
    public abstract int getAlpha(final int p0);
    
    public int getRGB(final int n) {
        return this.getAlpha(n) << 24 | this.getRed(n) << 16 | this.getGreen(n) << 8 | this.getBlue(n) << 0;
    }
    
    public int getRed(final Object o) {
        int n = 0;
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                n = (array[0] & 0xFF);
                n2 = array.length;
                break;
            }
            case 1: {
                final short[] array2 = (short[])o;
                n = (array2[0] & 0xFFFF);
                n2 = array2.length;
                break;
            }
            case 3: {
                final int[] array3 = (int[])o;
                n = array3[0];
                n2 = array3.length;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (n2 == 1) {
            return this.getRed(n);
        }
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public int getGreen(final Object o) {
        int n = 0;
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                n = (array[0] & 0xFF);
                n2 = array.length;
                break;
            }
            case 1: {
                final short[] array2 = (short[])o;
                n = (array2[0] & 0xFFFF);
                n2 = array2.length;
                break;
            }
            case 3: {
                final int[] array3 = (int[])o;
                n = array3[0];
                n2 = array3.length;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (n2 == 1) {
            return this.getGreen(n);
        }
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public int getBlue(final Object o) {
        int n = 0;
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                n = (array[0] & 0xFF);
                n2 = array.length;
                break;
            }
            case 1: {
                final short[] array2 = (short[])o;
                n = (array2[0] & 0xFFFF);
                n2 = array2.length;
                break;
            }
            case 3: {
                final int[] array3 = (int[])o;
                n = array3[0];
                n2 = array3.length;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (n2 == 1) {
            return this.getBlue(n);
        }
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public int getAlpha(final Object o) {
        int n = 0;
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                final byte[] array = (byte[])o;
                n = (array[0] & 0xFF);
                n2 = array.length;
                break;
            }
            case 1: {
                final short[] array2 = (short[])o;
                n = (array2[0] & 0xFFFF);
                n2 = array2.length;
                break;
            }
            case 3: {
                final int[] array3 = (int[])o;
                n = array3[0];
                n2 = array3.length;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        if (n2 == 1) {
            return this.getAlpha(n);
        }
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public int getRGB(final Object o) {
        return this.getAlpha(o) << 24 | this.getRed(o) << 16 | this.getGreen(o) << 8 | this.getBlue(o) << 0;
    }
    
    public Object getDataElements(final int n, final Object o) {
        throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    
    public int[] getComponents(final int n, final int[] array, final int n2) {
        throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    
    public int[] getComponents(final Object o, final int[] array, final int n) {
        throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    
    public int[] getUnnormalizedComponents(final float[] array, final int n, int[] array2, final int n2) {
        if (this.colorSpace == null) {
            throw new UnsupportedOperationException("This method is not supported by this color model.");
        }
        if (this.nBits == null) {
            throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
        }
        if (array.length - n < this.numComponents) {
            throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents);
        }
        if (array2 == null) {
            array2 = new int[n2 + this.numComponents];
        }
        if (this.supportsAlpha && this.isAlphaPremultiplied) {
            final float n3 = array[n + this.numColorComponents];
            for (int i = 0; i < this.numColorComponents; ++i) {
                array2[n2 + i] = (int)(array[n + i] * ((1 << this.nBits[i]) - 1) * n3 + 0.5f);
            }
            array2[n2 + this.numColorComponents] = (int)(n3 * ((1 << this.nBits[this.numColorComponents]) - 1) + 0.5f);
        }
        else {
            for (int j = 0; j < this.numComponents; ++j) {
                array2[n2 + j] = (int)(array[n + j] * ((1 << this.nBits[j]) - 1) + 0.5f);
            }
        }
        return array2;
    }
    
    public float[] getNormalizedComponents(final int[] array, final int n, float[] array2, final int n2) {
        if (this.colorSpace == null) {
            throw new UnsupportedOperationException("This method is not supported by this color model.");
        }
        if (this.nBits == null) {
            throw new UnsupportedOperationException("This method is not supported.  Unable to determine #bits per component.");
        }
        if (array.length - n < this.numComponents) {
            throw new IllegalArgumentException("Incorrect number of components.  Expecting " + this.numComponents);
        }
        if (array2 == null) {
            array2 = new float[this.numComponents + n2];
        }
        if (this.supportsAlpha && this.isAlphaPremultiplied) {
            final float n3 = array[n + this.numColorComponents] / (float)((1 << this.nBits[this.numColorComponents]) - 1);
            if (n3 != 0.0f) {
                for (int i = 0; i < this.numColorComponents; ++i) {
                    array2[n2 + i] = array[n + i] / (n3 * ((1 << this.nBits[i]) - 1));
                }
            }
            else {
                for (int j = 0; j < this.numColorComponents; ++j) {
                    array2[n2 + j] = 0.0f;
                }
            }
            array2[n2 + this.numColorComponents] = n3;
        }
        else {
            for (int k = 0; k < this.numComponents; ++k) {
                array2[n2 + k] = array[n + k] / (float)((1 << this.nBits[k]) - 1);
            }
        }
        return array2;
    }
    
    public int getDataElement(final int[] array, final int n) {
        throw new UnsupportedOperationException("This method is not supported by this color model.");
    }
    
    public Object getDataElements(final int[] array, final int n, final Object o) {
        throw new UnsupportedOperationException("This method has not been implemented for this color model.");
    }
    
    public int getDataElement(final float[] array, final int n) {
        return this.getDataElement(this.getUnnormalizedComponents(array, n, null, 0), 0);
    }
    
    public Object getDataElements(final float[] array, final int n, final Object o) {
        return this.getDataElements(this.getUnnormalizedComponents(array, n, null, 0), 0, o);
    }
    
    public float[] getNormalizedComponents(final Object o, final float[] array, final int n) {
        return this.getNormalizedComponents(this.getComponents(o, null, 0), 0, array, n);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ColorModel)) {
            return false;
        }
        final ColorModel colorModel = (ColorModel)o;
        if (this == colorModel) {
            return true;
        }
        if (this.supportsAlpha != colorModel.hasAlpha() || this.isAlphaPremultiplied != colorModel.isAlphaPremultiplied() || this.pixel_bits != colorModel.getPixelSize() || this.transparency != colorModel.getTransparency() || this.numComponents != colorModel.getNumComponents()) {
            return false;
        }
        final int[] componentSize = colorModel.getComponentSize();
        if (this.nBits != null && componentSize != null) {
            for (int i = 0; i < this.numComponents; ++i) {
                if (this.nBits[i] != componentSize[i]) {
                    return false;
                }
            }
            return true;
        }
        return this.nBits == null && componentSize == null;
    }
    
    @Override
    public int hashCode() {
        int n = (this.supportsAlpha ? 2 : 3) + (this.isAlphaPremultiplied ? 4 : 5) + this.pixel_bits * 6 + this.transparency * 7 + this.numComponents * 8;
        if (this.nBits != null) {
            for (int i = 0; i < this.numComponents; ++i) {
                n += this.nBits[i] * (i + 9);
            }
        }
        return n;
    }
    
    public final ColorSpace getColorSpace() {
        return this.colorSpace;
    }
    
    public ColorModel coerceData(final WritableRaster writableRaster, final boolean b) {
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public boolean isCompatibleRaster(final Raster raster) {
        throw new UnsupportedOperationException("This method has not been implemented for this ColorModel.");
    }
    
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public boolean isCompatibleSampleModel(final SampleModel sampleModel) {
        throw new UnsupportedOperationException("This method is not supported by this color model");
    }
    
    public void finalize() {
    }
    
    public WritableRaster getAlphaRaster(final WritableRaster writableRaster) {
        return null;
    }
    
    @Override
    public String toString() {
        return new String("ColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied);
    }
    
    static int getDefaultTransferType(final int n) {
        if (n <= 8) {
            return 0;
        }
        if (n <= 16) {
            return 1;
        }
        if (n <= 32) {
            return 3;
        }
        return 32;
    }
    
    static boolean isLinearRGBspace(final ColorSpace colorSpace) {
        return colorSpace == CMSManager.LINEAR_RGBspace;
    }
    
    static boolean isLinearGRAYspace(final ColorSpace colorSpace) {
        return colorSpace == CMSManager.GRAYspace;
    }
    
    static byte[] getLinearRGB8TosRGB8LUT() {
        if (ColorModel.l8Tos8 == null) {
            ColorModel.l8Tos8 = new byte[256];
            for (int i = 0; i <= 255; ++i) {
                final float n = i / 255.0f;
                float n2;
                if (n <= 0.0031308f) {
                    n2 = n * 12.92f;
                }
                else {
                    n2 = 1.055f * (float)Math.pow(n, 0.4166666666666667) - 0.055f;
                }
                ColorModel.l8Tos8[i] = (byte)Math.round(n2 * 255.0f);
            }
        }
        return ColorModel.l8Tos8;
    }
    
    static byte[] getsRGB8ToLinearRGB8LUT() {
        if (ColorModel.s8Tol8 == null) {
            ColorModel.s8Tol8 = new byte[256];
            for (int i = 0; i <= 255; ++i) {
                final float n = i / 255.0f;
                float n2;
                if (n <= 0.04045f) {
                    n2 = n / 12.92f;
                }
                else {
                    n2 = (float)Math.pow((n + 0.055f) / 1.055f, 2.4);
                }
                ColorModel.s8Tol8[i] = (byte)Math.round(n2 * 255.0f);
            }
        }
        return ColorModel.s8Tol8;
    }
    
    static byte[] getLinearRGB16TosRGB8LUT() {
        if (ColorModel.l16Tos8 == null) {
            ColorModel.l16Tos8 = new byte[65536];
            for (int i = 0; i <= 65535; ++i) {
                final float n = i / 65535.0f;
                float n2;
                if (n <= 0.0031308f) {
                    n2 = n * 12.92f;
                }
                else {
                    n2 = 1.055f * (float)Math.pow(n, 0.4166666666666667) - 0.055f;
                }
                ColorModel.l16Tos8[i] = (byte)Math.round(n2 * 255.0f);
            }
        }
        return ColorModel.l16Tos8;
    }
    
    static short[] getsRGB8ToLinearRGB16LUT() {
        if (ColorModel.s8Tol16 == null) {
            ColorModel.s8Tol16 = new short[256];
            for (int i = 0; i <= 255; ++i) {
                final float n = i / 255.0f;
                float n2;
                if (n <= 0.04045f) {
                    n2 = n / 12.92f;
                }
                else {
                    n2 = (float)Math.pow((n + 0.055f) / 1.055f, 2.4);
                }
                ColorModel.s8Tol16[i] = (short)Math.round(n2 * 65535.0f);
            }
        }
        return ColorModel.s8Tol16;
    }
    
    static byte[] getGray8TosRGB8LUT(final ICC_ColorSpace icc_ColorSpace) {
        if (isLinearGRAYspace(icc_ColorSpace)) {
            return getLinearRGB8TosRGB8LUT();
        }
        if (ColorModel.g8Tos8Map != null) {
            final byte[] array = ColorModel.g8Tos8Map.get(icc_ColorSpace);
            if (array != null) {
                return array;
            }
        }
        final byte[] array2 = new byte[256];
        for (int i = 0; i <= 255; ++i) {
            array2[i] = (byte)i;
        }
        final ColorTransform[] array3 = new ColorTransform[2];
        final PCMM module = CMSManager.getModule();
        final ICC_ColorSpace icc_ColorSpace2 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
        array3[0] = module.createTransform(icc_ColorSpace.getProfile(), -1, 1);
        array3[1] = module.createTransform(icc_ColorSpace2.getProfile(), -1, 2);
        final byte[] colorConvert = module.createTransform(array3).colorConvert(array2, null);
        for (int j = 0, n = 2; j <= 255; ++j, n += 3) {
            array2[j] = colorConvert[n];
        }
        if (ColorModel.g8Tos8Map == null) {
            ColorModel.g8Tos8Map = Collections.synchronizedMap(new WeakHashMap<ICC_ColorSpace, byte[]>(2));
        }
        ColorModel.g8Tos8Map.put(icc_ColorSpace, array2);
        return array2;
    }
    
    static byte[] getLinearGray16ToOtherGray8LUT(final ICC_ColorSpace icc_ColorSpace) {
        if (ColorModel.lg16Toog8Map != null) {
            final byte[] array = ColorModel.lg16Toog8Map.get(icc_ColorSpace);
            if (array != null) {
                return array;
            }
        }
        final short[] array2 = new short[65536];
        for (int i = 0; i <= 65535; ++i) {
            array2[i] = (short)i;
        }
        final ColorTransform[] array3 = new ColorTransform[2];
        final PCMM module = CMSManager.getModule();
        array3[0] = module.createTransform(((ICC_ColorSpace)ColorSpace.getInstance(1003)).getProfile(), -1, 1);
        array3[1] = module.createTransform(icc_ColorSpace.getProfile(), -1, 2);
        final short[] colorConvert = module.createTransform(array3).colorConvert(array2, null);
        final byte[] array4 = new byte[65536];
        for (int j = 0; j <= 65535; ++j) {
            array4[j] = (byte)((colorConvert[j] & 0xFFFF) * 0.0038910506f + 0.5f);
        }
        if (ColorModel.lg16Toog8Map == null) {
            ColorModel.lg16Toog8Map = Collections.synchronizedMap(new WeakHashMap<ICC_ColorSpace, byte[]>(2));
        }
        ColorModel.lg16Toog8Map.put(icc_ColorSpace, array4);
        return array4;
    }
    
    static byte[] getGray16TosRGB8LUT(final ICC_ColorSpace icc_ColorSpace) {
        if (isLinearGRAYspace(icc_ColorSpace)) {
            return getLinearRGB16TosRGB8LUT();
        }
        if (ColorModel.g16Tos8Map != null) {
            final byte[] array = ColorModel.g16Tos8Map.get(icc_ColorSpace);
            if (array != null) {
                return array;
            }
        }
        final short[] array2 = new short[65536];
        for (int i = 0; i <= 65535; ++i) {
            array2[i] = (short)i;
        }
        final ColorTransform[] array3 = new ColorTransform[2];
        final PCMM module = CMSManager.getModule();
        final ICC_ColorSpace icc_ColorSpace2 = (ICC_ColorSpace)ColorSpace.getInstance(1000);
        array3[0] = module.createTransform(icc_ColorSpace.getProfile(), -1, 1);
        array3[1] = module.createTransform(icc_ColorSpace2.getProfile(), -1, 2);
        final short[] colorConvert = module.createTransform(array3).colorConvert(array2, null);
        final byte[] array4 = new byte[65536];
        for (int j = 0, n = 2; j <= 65535; ++j, n += 3) {
            array4[j] = (byte)((colorConvert[n] & 0xFFFF) * 0.0038910506f + 0.5f);
        }
        if (ColorModel.g16Tos8Map == null) {
            ColorModel.g16Tos8Map = Collections.synchronizedMap(new WeakHashMap<ICC_ColorSpace, byte[]>(2));
        }
        ColorModel.g16Tos8Map.put(icc_ColorSpace, array4);
        return array4;
    }
    
    static short[] getLinearGray16ToOtherGray16LUT(final ICC_ColorSpace icc_ColorSpace) {
        if (ColorModel.lg16Toog16Map != null) {
            final short[] array = ColorModel.lg16Toog16Map.get(icc_ColorSpace);
            if (array != null) {
                return array;
            }
        }
        final short[] array2 = new short[65536];
        for (int i = 0; i <= 65535; ++i) {
            array2[i] = (short)i;
        }
        final ColorTransform[] array3 = new ColorTransform[2];
        final PCMM module = CMSManager.getModule();
        array3[0] = module.createTransform(((ICC_ColorSpace)ColorSpace.getInstance(1003)).getProfile(), -1, 1);
        array3[1] = module.createTransform(icc_ColorSpace.getProfile(), -1, 2);
        final short[] colorConvert = module.createTransform(array3).colorConvert(array2, null);
        if (ColorModel.lg16Toog16Map == null) {
            ColorModel.lg16Toog16Map = Collections.synchronizedMap(new WeakHashMap<ICC_ColorSpace, short[]>(2));
        }
        ColorModel.lg16Toog16Map.put(icc_ColorSpace, colorConvert);
        return colorConvert;
    }
    
    static {
        ColorModel.loaded = false;
        loadLibraries();
        initIDs();
        ColorModel.l8Tos8 = null;
        ColorModel.s8Tol8 = null;
        ColorModel.l16Tos8 = null;
        ColorModel.s8Tol16 = null;
        ColorModel.g8Tos8Map = null;
        ColorModel.lg16Toog8Map = null;
        ColorModel.g16Tos8Map = null;
        ColorModel.lg16Toog16Map = null;
    }
}
