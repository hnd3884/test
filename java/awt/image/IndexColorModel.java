package java.awt.image;

import java.util.Hashtable;
import java.awt.Point;
import java.util.Arrays;
import java.awt.color.ColorSpace;
import sun.awt.image.BufImgSurfaceData;
import java.math.BigInteger;

public class IndexColorModel extends ColorModel
{
    private int[] rgb;
    private int map_size;
    private int pixel_mask;
    private int transparent_index;
    private boolean allgrayopaque;
    private BigInteger validBits;
    private BufImgSurfaceData.ICMColorData colorData;
    private static int[] opaqueBits;
    private static int[] alphaBits;
    private static final int CACHESIZE = 40;
    private int[] lookupcache;
    
    private static native void initIDs();
    
    public IndexColorModel(final int n, final int n2, final byte[] array, final byte[] array2, final byte[] array3) {
        super(n, IndexColorModel.opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(n));
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        this.setRGBs(n2, array, array2, array3, null);
        this.calculatePixelMask();
    }
    
    public IndexColorModel(final int n, final int n2, final byte[] array, final byte[] array2, final byte[] array3, final int transparentPixel) {
        super(n, IndexColorModel.opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(n));
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        this.setRGBs(n2, array, array2, array3, null);
        this.setTransparentPixel(transparentPixel);
        this.calculatePixelMask();
    }
    
    public IndexColorModel(final int n, final int n2, final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        super(n, IndexColorModel.alphaBits, ColorSpace.getInstance(1000), true, false, 3, ColorModel.getDefaultTransferType(n));
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        this.setRGBs(n2, array, array2, array3, array4);
        this.calculatePixelMask();
    }
    
    public IndexColorModel(final int n, final int n2, final byte[] array, final int n3, final boolean b) {
        this(n, n2, array, n3, b, -1);
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
    }
    
    public IndexColorModel(final int n, final int map_size, final byte[] array, final int n2, final boolean b, final int transparentPixel) {
        super(n, IndexColorModel.opaqueBits, ColorSpace.getInstance(1000), false, false, 1, ColorModel.getDefaultTransferType(n));
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        if (map_size < 1) {
            throw new IllegalArgumentException("Map size (" + map_size + ") must be >= 1");
        }
        this.map_size = map_size;
        this.rgb = new int[this.calcRealMapSize(n, map_size)];
        int n3 = n2;
        int n4 = 255;
        boolean allgrayopaque = true;
        int transparency = 1;
        for (int i = 0; i < map_size; ++i) {
            final int n5 = array[n3++] & 0xFF;
            final int n6 = array[n3++] & 0xFF;
            final int n7 = array[n3++] & 0xFF;
            allgrayopaque = (allgrayopaque && n5 == n6 && n6 == n7);
            if (b) {
                n4 = (array[n3++] & 0xFF);
                if (n4 != 255) {
                    if (n4 == 0) {
                        if (transparency == 1) {
                            transparency = 2;
                        }
                        if (this.transparent_index < 0) {
                            this.transparent_index = i;
                        }
                    }
                    else {
                        transparency = 3;
                    }
                    allgrayopaque = false;
                }
            }
            this.rgb[i] = (n4 << 24 | n5 << 16 | n6 << 8 | n7);
        }
        this.allgrayopaque = allgrayopaque;
        this.setTransparency(transparency);
        this.setTransparentPixel(transparentPixel);
        this.calculatePixelMask();
    }
    
    public IndexColorModel(final int n, final int n2, final int[] array, final int n3, final boolean b, final int transparentPixel, final int n4) {
        super(n, IndexColorModel.opaqueBits, ColorSpace.getInstance(1000), false, false, 1, n4);
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        if (n2 < 1) {
            throw new IllegalArgumentException("Map size (" + n2 + ") must be >= 1");
        }
        if (n4 != 0 && n4 != 1) {
            throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
        }
        this.setRGBs(n2, array, n3, b);
        this.setTransparentPixel(transparentPixel);
        this.calculatePixelMask();
    }
    
    public IndexColorModel(final int n, final int n2, final int[] array, final int n3, final int n4, final BigInteger validBits) {
        super(n, IndexColorModel.alphaBits, ColorSpace.getInstance(1000), true, false, 3, n4);
        this.transparent_index = -1;
        this.colorData = null;
        this.lookupcache = new int[40];
        if (n < 1 || n > 16) {
            throw new IllegalArgumentException("Number of bits must be between 1 and 16.");
        }
        if (n2 < 1) {
            throw new IllegalArgumentException("Map size (" + n2 + ") must be >= 1");
        }
        if (n4 != 0 && n4 != 1) {
            throw new IllegalArgumentException("transferType must be eitherDataBuffer.TYPE_BYTE or DataBuffer.TYPE_USHORT");
        }
        if (validBits != null) {
            for (int i = 0; i < n2; ++i) {
                if (!validBits.testBit(i)) {
                    this.validBits = validBits;
                    break;
                }
            }
        }
        this.setRGBs(n2, array, n3, true);
        this.calculatePixelMask();
    }
    
    private void setRGBs(final int map_size, final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        if (map_size < 1) {
            throw new IllegalArgumentException("Map size (" + map_size + ") must be >= 1");
        }
        this.map_size = map_size;
        this.rgb = new int[this.calcRealMapSize(this.pixel_bits, map_size)];
        int n = 255;
        int transparency = 1;
        boolean allgrayopaque = true;
        for (int i = 0; i < map_size; ++i) {
            final int n2 = array[i] & 0xFF;
            final int n3 = array2[i] & 0xFF;
            final int n4 = array3[i] & 0xFF;
            allgrayopaque = (allgrayopaque && n2 == n3 && n3 == n4);
            if (array4 != null) {
                n = (array4[i] & 0xFF);
                if (n != 255) {
                    if (n == 0) {
                        if (transparency == 1) {
                            transparency = 2;
                        }
                        if (this.transparent_index < 0) {
                            this.transparent_index = i;
                        }
                    }
                    else {
                        transparency = 3;
                    }
                    allgrayopaque = false;
                }
            }
            this.rgb[i] = (n << 24 | n2 << 16 | n3 << 8 | n4);
        }
        this.allgrayopaque = allgrayopaque;
        this.setTransparency(transparency);
    }
    
    private void setRGBs(final int map_size, final int[] array, final int n, final boolean b) {
        this.map_size = map_size;
        this.rgb = new int[this.calcRealMapSize(this.pixel_bits, map_size)];
        int n2 = n;
        int transparency = 1;
        boolean allgrayopaque = true;
        final BigInteger validBits = this.validBits;
        for (int i = 0; i < map_size; ++i, ++n2) {
            if (validBits == null || validBits.testBit(i)) {
                int n3 = array[n2];
                final int n4 = n3 >> 16 & 0xFF;
                final int n5 = n3 >> 8 & 0xFF;
                final int n6 = n3 & 0xFF;
                allgrayopaque = (allgrayopaque && n4 == n5 && n5 == n6);
                if (b) {
                    final int n7 = n3 >>> 24;
                    if (n7 != 255) {
                        if (n7 == 0) {
                            if (transparency == 1) {
                                transparency = 2;
                            }
                            if (this.transparent_index < 0) {
                                this.transparent_index = i;
                            }
                        }
                        else {
                            transparency = 3;
                        }
                        allgrayopaque = false;
                    }
                }
                else {
                    n3 |= 0xFF000000;
                }
                this.rgb[i] = n3;
            }
        }
        this.allgrayopaque = allgrayopaque;
        this.setTransparency(transparency);
    }
    
    private int calcRealMapSize(final int n, final int n2) {
        return Math.max(Math.max(1 << n, n2), 256);
    }
    
    private BigInteger getAllValid() {
        final int n = (this.map_size + 7) / 8;
        final byte[] array = new byte[n];
        Arrays.fill(array, (byte)(-1));
        array[0] = (byte)(255 >>> n * 8 - this.map_size);
        return new BigInteger(1, array);
    }
    
    @Override
    public int getTransparency() {
        return this.transparency;
    }
    
    @Override
    public int[] getComponentSize() {
        if (this.nBits == null) {
            if (this.supportsAlpha) {
                (this.nBits = new int[4])[3] = 8;
            }
            else {
                this.nBits = new int[3];
            }
            final int[] nBits = this.nBits;
            final int n = 0;
            final int[] nBits2 = this.nBits;
            final int n2 = 1;
            final int[] nBits3 = this.nBits;
            final int n3 = 2;
            final int n4 = 8;
            nBits3[n3] = n4;
            nBits[n] = (nBits2[n2] = n4);
        }
        return this.nBits.clone();
    }
    
    public final int getMapSize() {
        return this.map_size;
    }
    
    public final int getTransparentPixel() {
        return this.transparent_index;
    }
    
    public final void getReds(final byte[] array) {
        for (int i = 0; i < this.map_size; ++i) {
            array[i] = (byte)(this.rgb[i] >> 16);
        }
    }
    
    public final void getGreens(final byte[] array) {
        for (int i = 0; i < this.map_size; ++i) {
            array[i] = (byte)(this.rgb[i] >> 8);
        }
    }
    
    public final void getBlues(final byte[] array) {
        for (int i = 0; i < this.map_size; ++i) {
            array[i] = (byte)this.rgb[i];
        }
    }
    
    public final void getAlphas(final byte[] array) {
        for (int i = 0; i < this.map_size; ++i) {
            array[i] = (byte)(this.rgb[i] >> 24);
        }
    }
    
    public final void getRGBs(final int[] array) {
        System.arraycopy(this.rgb, 0, array, 0, this.map_size);
    }
    
    private void setTransparentPixel(final int transparent_index) {
        if (transparent_index >= 0 && transparent_index < this.map_size) {
            final int[] rgb = this.rgb;
            rgb[transparent_index] &= 0xFFFFFF;
            this.transparent_index = transparent_index;
            this.allgrayopaque = false;
            if (this.transparency == 1) {
                this.setTransparency(2);
            }
        }
    }
    
    private void setTransparency(final int transparency) {
        if (this.transparency != transparency) {
            if ((this.transparency = transparency) == 1) {
                this.supportsAlpha = false;
                this.numComponents = 3;
                this.nBits = IndexColorModel.opaqueBits;
            }
            else {
                this.supportsAlpha = true;
                this.numComponents = 4;
                this.nBits = IndexColorModel.alphaBits;
            }
        }
    }
    
    private final void calculatePixelMask() {
        int pixel_bits = this.pixel_bits;
        if (pixel_bits == 3) {
            pixel_bits = 4;
        }
        else if (pixel_bits > 4 && pixel_bits < 8) {
            pixel_bits = 8;
        }
        this.pixel_mask = (1 << pixel_bits) - 1;
    }
    
    @Override
    public final int getRed(final int n) {
        return this.rgb[n & this.pixel_mask] >> 16 & 0xFF;
    }
    
    @Override
    public final int getGreen(final int n) {
        return this.rgb[n & this.pixel_mask] >> 8 & 0xFF;
    }
    
    @Override
    public final int getBlue(final int n) {
        return this.rgb[n & this.pixel_mask] & 0xFF;
    }
    
    @Override
    public final int getAlpha(final int n) {
        return this.rgb[n & this.pixel_mask] >> 24 & 0xFF;
    }
    
    @Override
    public final int getRGB(final int n) {
        return this.rgb[n & this.pixel_mask];
    }
    
    @Override
    public synchronized Object getDataElements(final int n, final Object o) {
        final int n2 = n >> 16 & 0xFF;
        final int n3 = n >> 8 & 0xFF;
        final int n4 = n & 0xFF;
        final int n5 = n >>> 24;
        int transparent_index = 0;
        for (int n6 = 38; n6 >= 0 && (transparent_index = this.lookupcache[n6]) != 0; n6 -= 2) {
            if (n == this.lookupcache[n6 + 1]) {
                return this.installpixel(o, ~transparent_index);
            }
        }
        if (this.allgrayopaque) {
            int n7 = 256;
            final int n8 = (n2 * 77 + n3 * 150 + n4 * 29 + 128) / 256;
            for (int i = 0; i < this.map_size; ++i) {
                if (this.rgb[i] != 0) {
                    int n9 = (this.rgb[i] & 0xFF) - n8;
                    if (n9 < 0) {
                        n9 = -n9;
                    }
                    if (n9 < n7) {
                        transparent_index = i;
                        if (n9 == 0) {
                            break;
                        }
                        n7 = n9;
                    }
                }
            }
        }
        else if (this.transparency == 1) {
            int n10 = Integer.MAX_VALUE;
            final int[] rgb = this.rgb;
            for (int j = 0; j < this.map_size; ++j) {
                final int n11 = rgb[j];
                if (n11 == n && n11 != 0) {
                    transparent_index = j;
                    n10 = 0;
                    break;
                }
            }
            if (n10 != 0) {
                for (int k = 0; k < this.map_size; ++k) {
                    final int n12 = rgb[k];
                    if (n12 != 0) {
                        final int n13 = (n12 >> 16 & 0xFF) - n2;
                        final int n14 = n13 * n13;
                        if (n14 < n10) {
                            final int n15 = (n12 >> 8 & 0xFF) - n3;
                            final int n16 = n14 + n15 * n15;
                            if (n16 < n10) {
                                final int n17 = (n12 & 0xFF) - n4;
                                final int n18 = n16 + n17 * n17;
                                if (n18 < n10) {
                                    transparent_index = k;
                                    n10 = n18;
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (n5 == 0 && this.transparent_index >= 0) {
            transparent_index = this.transparent_index;
        }
        else {
            int n19 = Integer.MAX_VALUE;
            final int[] rgb2 = this.rgb;
            for (int l = 0; l < this.map_size; ++l) {
                final int n20 = rgb2[l];
                if (n20 == n) {
                    if (this.validBits == null || this.validBits.testBit(l)) {
                        transparent_index = l;
                        break;
                    }
                }
                else {
                    final int n21 = (n20 >> 16 & 0xFF) - n2;
                    final int n22 = n21 * n21;
                    if (n22 < n19) {
                        final int n23 = (n20 >> 8 & 0xFF) - n3;
                        final int n24 = n22 + n23 * n23;
                        if (n24 < n19) {
                            final int n25 = (n20 & 0xFF) - n4;
                            final int n26 = n24 + n25 * n25;
                            if (n26 < n19) {
                                final int n27 = (n20 >>> 24) - n5;
                                final int n28 = n26 + n27 * n27;
                                if (n28 < n19 && (this.validBits == null || this.validBits.testBit(l))) {
                                    transparent_index = l;
                                    n19 = n28;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.arraycopy(this.lookupcache, 2, this.lookupcache, 0, 38);
        this.lookupcache[39] = n;
        this.lookupcache[38] = ~transparent_index;
        return this.installpixel(o, transparent_index);
    }
    
    private Object installpixel(Object o, final int n) {
        switch (this.transferType) {
            case 3: {
                int[] array;
                if (o == null) {
                    array = (int[])(o = new int[] { 0 });
                }
                else {
                    array = (int[])o;
                }
                array[0] = n;
                break;
            }
            case 0: {
                byte[] array2;
                if (o == null) {
                    array2 = (byte[])(o = new byte[] { 0 });
                }
                else {
                    array2 = (byte[])o;
                }
                array2[0] = (byte)n;
                break;
            }
            case 1: {
                short[] array3;
                if (o == null) {
                    array3 = (short[])(o = new short[] { 0 });
                }
                else {
                    array3 = (short[])o;
                }
                array3[0] = (short)n;
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return o;
    }
    
    @Override
    public int[] getComponents(final int n, int[] array, final int n2) {
        if (array == null) {
            array = new int[n2 + this.numComponents];
        }
        array[n2 + 0] = this.getRed(n);
        array[n2 + 1] = this.getGreen(n);
        array[n2 + 2] = this.getBlue(n);
        if (this.supportsAlpha && array.length - n2 > 3) {
            array[n2 + 3] = this.getAlpha(n);
        }
        return array;
    }
    
    @Override
    public int[] getComponents(final Object o, final int[] array, final int n) {
        int n2 = 0;
        switch (this.transferType) {
            case 0: {
                n2 = (((byte[])o)[0] & 0xFF);
                break;
            }
            case 1: {
                n2 = (((short[])o)[0] & 0xFFFF);
                break;
            }
            case 3: {
                n2 = ((int[])o)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return this.getComponents(n2, array, n);
    }
    
    @Override
    public int getDataElement(final int[] array, final int n) {
        final int n2 = array[n + 0] << 16 | array[n + 1] << 8 | array[n + 2];
        int n3;
        if (this.supportsAlpha) {
            n3 = (n2 | array[n + 3] << 24);
        }
        else {
            n3 = (n2 | 0xFF000000);
        }
        final Object dataElements = this.getDataElements(n3, null);
        int n4 = 0;
        switch (this.transferType) {
            case 0: {
                n4 = (((byte[])dataElements)[0] & 0xFF);
                break;
            }
            case 1: {
                n4 = ((short[])dataElements)[0];
                break;
            }
            case 3: {
                n4 = ((int[])dataElements)[0];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return n4;
    }
    
    @Override
    public Object getDataElements(final int[] array, final int n, final Object o) {
        final int n2 = array[n + 0] << 16 | array[n + 1] << 8 | array[n + 2];
        int n3;
        if (this.supportsAlpha) {
            n3 = (n2 | array[n + 3] << 24);
        }
        else {
            n3 = (n2 & 0xFF000000);
        }
        return this.getDataElements(n3, o);
    }
    
    @Override
    public WritableRaster createCompatibleWritableRaster(final int n, final int n2) {
        WritableRaster writableRaster;
        if (this.pixel_bits == 1 || this.pixel_bits == 2 || this.pixel_bits == 4) {
            writableRaster = Raster.createPackedRaster(0, n, n2, 1, this.pixel_bits, null);
        }
        else if (this.pixel_bits <= 8) {
            writableRaster = Raster.createInterleavedRaster(0, n, n2, 1, null);
        }
        else {
            if (this.pixel_bits > 16) {
                throw new UnsupportedOperationException("This method is not supported  for pixel bits > 16.");
            }
            writableRaster = Raster.createInterleavedRaster(1, n, n2, 1, null);
        }
        return writableRaster;
    }
    
    @Override
    public boolean isCompatibleRaster(final Raster raster) {
        final int sampleSize = raster.getSampleModel().getSampleSize(0);
        return raster.getTransferType() == this.transferType && raster.getNumBands() == 1 && 1 << sampleSize >= this.map_size;
    }
    
    @Override
    public SampleModel createCompatibleSampleModel(final int n, final int n2) {
        final int[] array = { 0 };
        if (this.pixel_bits == 1 || this.pixel_bits == 2 || this.pixel_bits == 4) {
            return new MultiPixelPackedSampleModel(this.transferType, n, n2, this.pixel_bits);
        }
        return new ComponentSampleModel(this.transferType, n, n2, 1, n, array);
    }
    
    @Override
    public boolean isCompatibleSampleModel(final SampleModel sampleModel) {
        return (sampleModel instanceof ComponentSampleModel || sampleModel instanceof MultiPixelPackedSampleModel) && sampleModel.getTransferType() == this.transferType && sampleModel.getNumBands() == 1;
    }
    
    public BufferedImage convertToIntDiscrete(final Raster raster, final boolean b) {
        if (!this.isCompatibleRaster(raster)) {
            throw new IllegalArgumentException("This raster is not compatiblewith this IndexColorModel.");
        }
        ColorModel rgBdefault;
        if (b || this.transparency == 3) {
            rgBdefault = ColorModel.getRGBdefault();
        }
        else if (this.transparency == 2) {
            rgBdefault = new DirectColorModel(25, 16711680, 65280, 255, 16777216);
        }
        else {
            rgBdefault = new DirectColorModel(24, 16711680, 65280, 255);
        }
        final int width = raster.getWidth();
        final int height = raster.getHeight();
        final WritableRaster compatibleWritableRaster = rgBdefault.createCompatibleWritableRaster(width, height);
        Object dataElements = null;
        final int minX = raster.getMinX();
        for (int minY = raster.getMinY(), i = 0; i < height; ++i, ++minY) {
            dataElements = raster.getDataElements(minX, minY, width, 1, dataElements);
            int[] intArray;
            if (dataElements instanceof int[]) {
                intArray = (int[])dataElements;
            }
            else {
                intArray = DataBuffer.toIntArray(dataElements);
            }
            for (int j = 0; j < width; ++j) {
                intArray[j] = this.rgb[intArray[j] & this.pixel_mask];
            }
            compatibleWritableRaster.setDataElements(0, i, width, 1, intArray);
        }
        return new BufferedImage(rgBdefault, compatibleWritableRaster, false, null);
    }
    
    public boolean isValid(final int n) {
        return n >= 0 && n < this.map_size && (this.validBits == null || this.validBits.testBit(n));
    }
    
    public boolean isValid() {
        return this.validBits == null;
    }
    
    public BigInteger getValidPixels() {
        if (this.validBits == null) {
            return this.getAllValid();
        }
        return this.validBits;
    }
    
    @Override
    public void finalize() {
    }
    
    @Override
    public String toString() {
        return new String("IndexColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.numComponents + " color space = " + this.colorSpace + " transparency = " + this.transparency + " transIndex   = " + this.transparent_index + " has alpha = " + this.supportsAlpha + " isAlphaPre = " + this.isAlphaPremultiplied);
    }
    
    static {
        IndexColorModel.opaqueBits = new int[] { 8, 8, 8 };
        IndexColorModel.alphaBits = new int[] { 8, 8, 8, 8 };
        ColorModel.loadLibraries();
        initIDs();
    }
}
