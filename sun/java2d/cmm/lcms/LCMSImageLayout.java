package sun.java2d.cmm.lcms;

import java.awt.image.ComponentSampleModel;
import java.awt.image.ColorModel;
import sun.awt.image.ShortComponentRaster;
import sun.awt.image.ByteComponentRaster;
import sun.awt.image.IntegerComponentRaster;
import java.awt.image.Raster;
import java.awt.image.ComponentColorModel;
import java.awt.image.BufferedImage;

class LCMSImageLayout
{
    public static final int SWAPFIRST = 16384;
    public static final int DOSWAP = 1024;
    public static final int PT_RGB_8;
    public static final int PT_GRAY_8;
    public static final int PT_GRAY_16;
    public static final int PT_RGBA_8;
    public static final int PT_ARGB_8;
    public static final int PT_BGR_8;
    public static final int PT_ABGR_8;
    public static final int PT_BGRA_8;
    public static final int DT_BYTE = 0;
    public static final int DT_SHORT = 1;
    public static final int DT_INT = 2;
    public static final int DT_DOUBLE = 3;
    boolean isIntPacked;
    int pixelType;
    int dataType;
    int width;
    int height;
    int nextRowOffset;
    private int nextPixelOffset;
    int offset;
    private boolean imageAtOnce;
    Object dataArray;
    private int dataArrayLength;
    
    public static int BYTES_SH(final int n) {
        return n;
    }
    
    public static int EXTRA_SH(final int n) {
        return n << 7;
    }
    
    public static int CHANNELS_SH(final int n) {
        return n << 3;
    }
    
    private LCMSImageLayout(final int width, final int pixelType, final int nextPixelOffset) throws ImageLayoutException {
        this.isIntPacked = false;
        this.imageAtOnce = false;
        this.pixelType = pixelType;
        this.width = width;
        this.height = 1;
        this.nextPixelOffset = nextPixelOffset;
        this.nextRowOffset = safeMult(nextPixelOffset, width);
        this.offset = 0;
    }
    
    private LCMSImageLayout(final int width, final int height, final int pixelType, final int nextPixelOffset) throws ImageLayoutException {
        this.isIntPacked = false;
        this.imageAtOnce = false;
        this.pixelType = pixelType;
        this.width = width;
        this.height = height;
        this.nextPixelOffset = nextPixelOffset;
        this.nextRowOffset = safeMult(nextPixelOffset, width);
        this.offset = 0;
    }
    
    public LCMSImageLayout(final byte[] dataArray, final int n, final int n2, final int n3) throws ImageLayoutException {
        this(n, n2, n3);
        this.dataType = 0;
        this.dataArray = dataArray;
        this.dataArrayLength = dataArray.length;
        this.verify();
    }
    
    public LCMSImageLayout(final short[] dataArray, final int n, final int n2, final int n3) throws ImageLayoutException {
        this(n, n2, n3);
        this.dataType = 1;
        this.dataArray = dataArray;
        this.dataArrayLength = 2 * dataArray.length;
        this.verify();
    }
    
    public LCMSImageLayout(final int[] dataArray, final int n, final int n2, final int n3) throws ImageLayoutException {
        this(n, n2, n3);
        this.dataType = 2;
        this.dataArray = dataArray;
        this.dataArrayLength = 4 * dataArray.length;
        this.verify();
    }
    
    public LCMSImageLayout(final double[] dataArray, final int n, final int n2, final int n3) throws ImageLayoutException {
        this(n, n2, n3);
        this.dataType = 3;
        this.dataArray = dataArray;
        this.dataArrayLength = 8 * dataArray.length;
        this.verify();
    }
    
    private LCMSImageLayout() {
        this.isIntPacked = false;
        this.imageAtOnce = false;
    }
    
    public static LCMSImageLayout createImageLayout(final BufferedImage bufferedImage) throws ImageLayoutException {
        final LCMSImageLayout lcmsImageLayout = new LCMSImageLayout();
        switch (bufferedImage.getType()) {
            case 1: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_ARGB_8;
                lcmsImageLayout.isIntPacked = true;
                break;
            }
            case 2: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_ARGB_8;
                lcmsImageLayout.isIntPacked = true;
                break;
            }
            case 4: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_ABGR_8;
                lcmsImageLayout.isIntPacked = true;
                break;
            }
            case 5: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_BGR_8;
                break;
            }
            case 6: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_ABGR_8;
                break;
            }
            case 10: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_GRAY_8;
                break;
            }
            case 11: {
                lcmsImageLayout.pixelType = LCMSImageLayout.PT_GRAY_16;
                break;
            }
            default: {
                final ColorModel colorModel = bufferedImage.getColorModel();
                if (colorModel instanceof ComponentColorModel) {
                    final int[] componentSize = ((ComponentColorModel)colorModel).getComponentSize();
                    for (int length = componentSize.length, i = 0; i < length; ++i) {
                        if (componentSize[i] != 8) {
                            return null;
                        }
                    }
                    return createImageLayout(bufferedImage.getRaster());
                }
                return null;
            }
        }
        lcmsImageLayout.width = bufferedImage.getWidth();
        lcmsImageLayout.height = bufferedImage.getHeight();
        switch (bufferedImage.getType()) {
            case 1:
            case 2:
            case 4: {
                final IntegerComponentRaster integerComponentRaster = (IntegerComponentRaster)bufferedImage.getRaster();
                lcmsImageLayout.nextRowOffset = safeMult(4, integerComponentRaster.getScanlineStride());
                lcmsImageLayout.nextPixelOffset = safeMult(4, integerComponentRaster.getPixelStride());
                lcmsImageLayout.offset = safeMult(4, integerComponentRaster.getDataOffset(0));
                lcmsImageLayout.dataArray = integerComponentRaster.getDataStorage();
                lcmsImageLayout.dataArrayLength = 4 * integerComponentRaster.getDataStorage().length;
                lcmsImageLayout.dataType = 2;
                if (lcmsImageLayout.nextRowOffset == lcmsImageLayout.width * 4 * integerComponentRaster.getPixelStride()) {
                    lcmsImageLayout.imageAtOnce = true;
                }
                break;
            }
            case 5:
            case 6: {
                final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)bufferedImage.getRaster();
                lcmsImageLayout.nextRowOffset = byteComponentRaster.getScanlineStride();
                lcmsImageLayout.nextPixelOffset = byteComponentRaster.getPixelStride();
                lcmsImageLayout.offset = byteComponentRaster.getDataOffset(bufferedImage.getSampleModel().getNumBands() - 1);
                lcmsImageLayout.dataArray = byteComponentRaster.getDataStorage();
                lcmsImageLayout.dataArrayLength = byteComponentRaster.getDataStorage().length;
                lcmsImageLayout.dataType = 0;
                if (lcmsImageLayout.nextRowOffset == lcmsImageLayout.width * byteComponentRaster.getPixelStride()) {
                    lcmsImageLayout.imageAtOnce = true;
                }
                break;
            }
            case 10: {
                final ByteComponentRaster byteComponentRaster2 = (ByteComponentRaster)bufferedImage.getRaster();
                lcmsImageLayout.nextRowOffset = byteComponentRaster2.getScanlineStride();
                lcmsImageLayout.nextPixelOffset = byteComponentRaster2.getPixelStride();
                lcmsImageLayout.dataArrayLength = byteComponentRaster2.getDataStorage().length;
                lcmsImageLayout.offset = byteComponentRaster2.getDataOffset(0);
                lcmsImageLayout.dataArray = byteComponentRaster2.getDataStorage();
                lcmsImageLayout.dataType = 0;
                if (lcmsImageLayout.nextRowOffset == lcmsImageLayout.width * byteComponentRaster2.getPixelStride()) {
                    lcmsImageLayout.imageAtOnce = true;
                }
                break;
            }
            case 11: {
                final ShortComponentRaster shortComponentRaster = (ShortComponentRaster)bufferedImage.getRaster();
                lcmsImageLayout.nextRowOffset = safeMult(2, shortComponentRaster.getScanlineStride());
                lcmsImageLayout.nextPixelOffset = safeMult(2, shortComponentRaster.getPixelStride());
                lcmsImageLayout.offset = safeMult(2, shortComponentRaster.getDataOffset(0));
                lcmsImageLayout.dataArray = shortComponentRaster.getDataStorage();
                lcmsImageLayout.dataArrayLength = 2 * shortComponentRaster.getDataStorage().length;
                lcmsImageLayout.dataType = 1;
                if (lcmsImageLayout.nextRowOffset == lcmsImageLayout.width * 2 * shortComponentRaster.getPixelStride()) {
                    lcmsImageLayout.imageAtOnce = true;
                }
                break;
            }
            default: {
                return null;
            }
        }
        lcmsImageLayout.verify();
        return lcmsImageLayout;
    }
    
    private void verify() throws ImageLayoutException {
        if (this.offset < 0 || this.offset >= this.dataArrayLength) {
            throw new ImageLayoutException("Invalid image layout");
        }
        if (this.nextPixelOffset != getBytesPerPixel(this.pixelType)) {
            throw new ImageLayoutException("Invalid image layout");
        }
        final int safeAdd = safeAdd(this.offset, safeAdd(safeMult(this.nextPixelOffset, this.width - 1), safeMult(this.nextRowOffset, this.height - 1)));
        if (safeAdd < 0 || safeAdd >= this.dataArrayLength) {
            throw new ImageLayoutException("Invalid image layout");
        }
    }
    
    static int safeAdd(final int n, final int n2) throws ImageLayoutException {
        final long n3 = n + (long)n2;
        if (n3 < -2147483648L || n3 > 2147483647L) {
            throw new ImageLayoutException("Invalid image layout");
        }
        return (int)n3;
    }
    
    static int safeMult(final int n, final int n2) throws ImageLayoutException {
        final long n3 = n * (long)n2;
        if (n3 < -2147483648L || n3 > 2147483647L) {
            throw new ImageLayoutException("Invalid image layout");
        }
        return (int)n3;
    }
    
    public static LCMSImageLayout createImageLayout(final Raster raster) {
        final LCMSImageLayout lcmsImageLayout = new LCMSImageLayout();
        if (raster instanceof ByteComponentRaster && raster.getSampleModel() instanceof ComponentSampleModel) {
            final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)raster;
            final ComponentSampleModel componentSampleModel = (ComponentSampleModel)raster.getSampleModel();
            lcmsImageLayout.pixelType = (CHANNELS_SH(byteComponentRaster.getNumBands()) | BYTES_SH(1));
            final BandOrder bandOrder = BandOrder.getBandOrder(componentSampleModel.getBandOffsets());
            int n = 0;
            switch (bandOrder) {
                case INVERTED: {
                    final LCMSImageLayout lcmsImageLayout2 = lcmsImageLayout;
                    lcmsImageLayout2.pixelType |= 0x400;
                    n = componentSampleModel.getNumBands() - 1;
                    break;
                }
                case DIRECT: {
                    break;
                }
                default: {
                    return null;
                }
            }
            lcmsImageLayout.nextRowOffset = byteComponentRaster.getScanlineStride();
            lcmsImageLayout.nextPixelOffset = byteComponentRaster.getPixelStride();
            lcmsImageLayout.offset = byteComponentRaster.getDataOffset(n);
            lcmsImageLayout.dataArray = byteComponentRaster.getDataStorage();
            lcmsImageLayout.dataType = 0;
            lcmsImageLayout.width = byteComponentRaster.getWidth();
            lcmsImageLayout.height = byteComponentRaster.getHeight();
            if (lcmsImageLayout.nextRowOffset == lcmsImageLayout.width * byteComponentRaster.getPixelStride()) {
                lcmsImageLayout.imageAtOnce = true;
            }
            return lcmsImageLayout;
        }
        return null;
    }
    
    private static int getBytesPerPixel(final int n) {
        return (0x7 & n) * ((0xF & n >> 3) + (0x7 & n >> 7));
    }
    
    static {
        PT_RGB_8 = (CHANNELS_SH(3) | BYTES_SH(1));
        PT_GRAY_8 = (CHANNELS_SH(1) | BYTES_SH(1));
        PT_GRAY_16 = (CHANNELS_SH(1) | BYTES_SH(2));
        PT_RGBA_8 = (EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1));
        PT_ARGB_8 = (EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x4000);
        PT_BGR_8 = (0x400 | CHANNELS_SH(3) | BYTES_SH(1));
        PT_ABGR_8 = (0x400 | EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1));
        PT_BGRA_8 = (EXTRA_SH(1) | CHANNELS_SH(3) | BYTES_SH(1) | 0x400 | 0x4000);
    }
    
    private enum BandOrder
    {
        DIRECT, 
        INVERTED, 
        ARBITRARY, 
        UNKNOWN;
        
        public static BandOrder getBandOrder(final int[] array) {
            BandOrder bandOrder = BandOrder.UNKNOWN;
            final int length = array.length;
            for (int n = 0; bandOrder != BandOrder.ARBITRARY && n < array.length; ++n) {
                switch (bandOrder) {
                    case UNKNOWN: {
                        if (array[n] == n) {
                            bandOrder = BandOrder.DIRECT;
                            break;
                        }
                        if (array[n] == length - 1 - n) {
                            bandOrder = BandOrder.INVERTED;
                            break;
                        }
                        bandOrder = BandOrder.ARBITRARY;
                        break;
                    }
                    case DIRECT: {
                        if (array[n] != n) {
                            bandOrder = BandOrder.ARBITRARY;
                            break;
                        }
                        break;
                    }
                    case INVERTED: {
                        if (array[n] != length - 1 - n) {
                            bandOrder = BandOrder.ARBITRARY;
                            break;
                        }
                        break;
                    }
                }
            }
            return bandOrder;
        }
    }
    
    public static class ImageLayoutException extends Exception
    {
        public ImageLayoutException(final String s) {
            super(s);
        }
    }
}
