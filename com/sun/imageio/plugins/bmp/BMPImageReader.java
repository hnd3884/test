package com.sun.imageio.plugins.bmp;

import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.imageio.event.IIOReadWarningListener;
import javax.imageio.event.IIOReadUpdateListener;
import javax.imageio.event.IIOReadProgressListener;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import java.awt.image.ComponentSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferByte;
import java.util.Hashtable;
import java.awt.image.Raster;
import java.awt.Point;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageTypeSpecifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.image.DirectColorModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.IndexColorModel;
import com.sun.imageio.plugins.common.ImageUtil;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.color.ColorSpace;
import javax.imageio.IIOException;
import java.io.IOException;
import com.sun.imageio.plugins.common.I18N;
import java.nio.ByteOrder;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import javax.imageio.ImageReader;

public class BMPImageReader extends ImageReader implements BMPConstants
{
    private static final int VERSION_2_1_BIT = 0;
    private static final int VERSION_2_4_BIT = 1;
    private static final int VERSION_2_8_BIT = 2;
    private static final int VERSION_2_24_BIT = 3;
    private static final int VERSION_3_1_BIT = 4;
    private static final int VERSION_3_4_BIT = 5;
    private static final int VERSION_3_8_BIT = 6;
    private static final int VERSION_3_24_BIT = 7;
    private static final int VERSION_3_NT_16_BIT = 8;
    private static final int VERSION_3_NT_32_BIT = 9;
    private static final int VERSION_4_1_BIT = 10;
    private static final int VERSION_4_4_BIT = 11;
    private static final int VERSION_4_8_BIT = 12;
    private static final int VERSION_4_16_BIT = 13;
    private static final int VERSION_4_24_BIT = 14;
    private static final int VERSION_4_32_BIT = 15;
    private static final int VERSION_3_XP_EMBEDDED = 16;
    private static final int VERSION_4_XP_EMBEDDED = 17;
    private static final int VERSION_5_XP_EMBEDDED = 18;
    private long bitmapFileSize;
    private long bitmapOffset;
    private long compression;
    private long imageSize;
    private byte[] palette;
    private int imageType;
    private int numBands;
    private boolean isBottomUp;
    private int bitsPerPixel;
    private int redMask;
    private int greenMask;
    private int blueMask;
    private int alphaMask;
    private SampleModel sampleModel;
    private SampleModel originalSampleModel;
    private ColorModel colorModel;
    private ColorModel originalColorModel;
    private ImageInputStream iis;
    private boolean gotHeader;
    private int width;
    private int height;
    private Rectangle destinationRegion;
    private Rectangle sourceRegion;
    private BMPMetadata metadata;
    private BufferedImage bi;
    private boolean noTransform;
    private boolean seleBand;
    private int scaleX;
    private int scaleY;
    private int[] sourceBands;
    private int[] destBands;
    private static Boolean isLinkedProfileDisabled;
    private static Boolean isWindowsPlatform;
    
    public BMPImageReader(final ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
        this.iis = null;
        this.gotHeader = false;
        this.noTransform = true;
        this.seleBand = false;
    }
    
    @Override
    public void setInput(final Object o, final boolean b, final boolean b2) {
        super.setInput(o, b, b2);
        this.iis = (ImageInputStream)o;
        if (this.iis != null) {
            this.iis.setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
        this.resetHeaderInfo();
    }
    
    @Override
    public int getNumImages(final boolean b) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("GetNumImages0"));
        }
        if (this.seekForwardOnly && b) {
            throw new IllegalStateException(I18N.getString("GetNumImages1"));
        }
        return 1;
    }
    
    @Override
    public int getWidth(final int n) throws IOException {
        this.checkIndex(n);
        try {
            this.readHeader();
        }
        catch (final IllegalArgumentException ex) {
            throw new IIOException(I18N.getString("BMPImageReader6"), ex);
        }
        return this.width;
    }
    
    @Override
    public int getHeight(final int n) throws IOException {
        this.checkIndex(n);
        try {
            this.readHeader();
        }
        catch (final IllegalArgumentException ex) {
            throw new IIOException(I18N.getString("BMPImageReader6"), ex);
        }
        return this.height;
    }
    
    private void checkIndex(final int n) {
        if (n != 0) {
            throw new IndexOutOfBoundsException(I18N.getString("BMPImageReader0"));
        }
    }
    
    protected void readHeader() throws IOException, IllegalArgumentException {
        if (this.gotHeader) {
            return;
        }
        if (this.iis == null) {
            throw new IllegalStateException("Input source not set!");
        }
        int int1 = 0;
        int int2 = 0;
        this.metadata = new BMPMetadata();
        this.iis.mark();
        final byte[] array = new byte[2];
        this.iis.read(array);
        if (array[0] != 66 || array[1] != 77) {
            throw new IllegalArgumentException(I18N.getString("BMPImageReader1"));
        }
        this.bitmapFileSize = this.iis.readUnsignedInt();
        this.iis.skipBytes(4);
        this.bitmapOffset = this.iis.readUnsignedInt();
        final long unsignedInt = this.iis.readUnsignedInt();
        if (unsignedInt == 12L) {
            this.width = this.iis.readShort();
            this.height = this.iis.readShort();
        }
        else {
            this.width = this.iis.readInt();
            this.height = this.iis.readInt();
        }
        this.metadata.width = this.width;
        this.metadata.height = this.height;
        this.iis.readUnsignedShort();
        this.bitsPerPixel = this.iis.readUnsignedShort();
        this.metadata.bitsPerPixel = (short)this.bitsPerPixel;
        this.numBands = 3;
        if (unsignedInt == 12L) {
            this.metadata.bmpVersion = "BMP v. 2.x";
            if (this.bitsPerPixel == 1) {
                this.imageType = 0;
            }
            else if (this.bitsPerPixel == 4) {
                this.imageType = 1;
            }
            else if (this.bitsPerPixel == 8) {
                this.imageType = 2;
            }
            else if (this.bitsPerPixel == 24) {
                this.imageType = 3;
            }
            final int paletteSize = (int)((this.bitmapOffset - 14L - unsignedInt) / 3L);
            final int n = paletteSize * 3;
            this.palette = new byte[n];
            this.iis.readFully(this.palette, 0, n);
            this.metadata.palette = this.palette;
            this.metadata.paletteSize = paletteSize;
        }
        else {
            this.compression = this.iis.readUnsignedInt();
            this.imageSize = this.iis.readUnsignedInt();
            final long n2 = this.iis.readInt();
            final long n3 = this.iis.readInt();
            final long unsignedInt2 = this.iis.readUnsignedInt();
            final long unsignedInt3 = this.iis.readUnsignedInt();
            this.metadata.compression = (int)this.compression;
            this.metadata.xPixelsPerMeter = (int)n2;
            this.metadata.yPixelsPerMeter = (int)n3;
            this.metadata.colorsUsed = (int)unsignedInt2;
            this.metadata.colorsImportant = (int)unsignedInt3;
            if (unsignedInt == 40L) {
                switch ((int)this.compression) {
                    case 4:
                    case 5: {
                        this.metadata.bmpVersion = "BMP v. 3.x";
                        this.imageType = 16;
                        break;
                    }
                    case 0:
                    case 1:
                    case 2: {
                        if (this.bitmapOffset < unsignedInt + 14L) {
                            throw new IIOException(I18N.getString("BMPImageReader7"));
                        }
                        final int paletteSize2 = (int)((this.bitmapOffset - 14L - unsignedInt) / 4L);
                        final int n4 = paletteSize2 * 4;
                        this.palette = new byte[n4];
                        this.iis.readFully(this.palette, 0, n4);
                        this.metadata.palette = this.palette;
                        this.metadata.paletteSize = paletteSize2;
                        if (this.bitsPerPixel == 1) {
                            this.imageType = 4;
                        }
                        else if (this.bitsPerPixel == 4) {
                            this.imageType = 5;
                        }
                        else if (this.bitsPerPixel == 8) {
                            this.imageType = 6;
                        }
                        else if (this.bitsPerPixel == 24) {
                            this.imageType = 7;
                        }
                        else if (this.bitsPerPixel == 16) {
                            this.imageType = 8;
                            this.redMask = 31744;
                            this.greenMask = 992;
                            this.blueMask = 31;
                            this.metadata.redMask = this.redMask;
                            this.metadata.greenMask = this.greenMask;
                            this.metadata.blueMask = this.blueMask;
                        }
                        else if (this.bitsPerPixel == 32) {
                            this.imageType = 9;
                            this.redMask = 16711680;
                            this.greenMask = 65280;
                            this.blueMask = 255;
                            this.metadata.redMask = this.redMask;
                            this.metadata.greenMask = this.greenMask;
                            this.metadata.blueMask = this.blueMask;
                        }
                        this.metadata.bmpVersion = "BMP v. 3.x";
                        break;
                    }
                    case 3: {
                        if (this.bitsPerPixel == 16) {
                            this.imageType = 8;
                        }
                        else if (this.bitsPerPixel == 32) {
                            this.imageType = 9;
                        }
                        this.redMask = (int)this.iis.readUnsignedInt();
                        this.greenMask = (int)this.iis.readUnsignedInt();
                        this.blueMask = (int)this.iis.readUnsignedInt();
                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        if (unsignedInt2 != 0L) {
                            final int n5 = (int)unsignedInt2 * 4;
                            this.palette = new byte[n5];
                            this.iis.readFully(this.palette, 0, n5);
                            this.metadata.palette = this.palette;
                            this.metadata.paletteSize = (int)unsignedInt2;
                        }
                        this.metadata.bmpVersion = "BMP v. 3.x NT";
                        break;
                    }
                    default: {
                        throw new IIOException(I18N.getString("BMPImageReader2"));
                    }
                }
            }
            else {
                if (unsignedInt != 108L && unsignedInt != 124L) {
                    throw new IIOException(I18N.getString("BMPImageReader3"));
                }
                if (unsignedInt == 108L) {
                    this.metadata.bmpVersion = "BMP v. 4.x";
                }
                else if (unsignedInt == 124L) {
                    this.metadata.bmpVersion = "BMP v. 5.x";
                }
                this.redMask = (int)this.iis.readUnsignedInt();
                this.greenMask = (int)this.iis.readUnsignedInt();
                this.blueMask = (int)this.iis.readUnsignedInt();
                this.alphaMask = (int)this.iis.readUnsignedInt();
                final long unsignedInt4 = this.iis.readUnsignedInt();
                final int int3 = this.iis.readInt();
                final int int4 = this.iis.readInt();
                final int int5 = this.iis.readInt();
                final int int6 = this.iis.readInt();
                final int int7 = this.iis.readInt();
                final int int8 = this.iis.readInt();
                final int int9 = this.iis.readInt();
                final int int10 = this.iis.readInt();
                final int int11 = this.iis.readInt();
                final long unsignedInt5 = this.iis.readUnsignedInt();
                final long unsignedInt6 = this.iis.readUnsignedInt();
                final long unsignedInt7 = this.iis.readUnsignedInt();
                if (unsignedInt == 124L) {
                    this.metadata.intent = this.iis.readInt();
                    int1 = this.iis.readInt();
                    int2 = this.iis.readInt();
                    this.iis.skipBytes(4);
                }
                this.metadata.colorSpace = (int)unsignedInt4;
                if (unsignedInt4 == 0L) {
                    this.metadata.redX = int3;
                    this.metadata.redY = int4;
                    this.metadata.redZ = int5;
                    this.metadata.greenX = int6;
                    this.metadata.greenY = int7;
                    this.metadata.greenZ = int8;
                    this.metadata.blueX = int9;
                    this.metadata.blueY = int10;
                    this.metadata.blueZ = int11;
                    this.metadata.gammaRed = (int)unsignedInt5;
                    this.metadata.gammaGreen = (int)unsignedInt6;
                    this.metadata.gammaBlue = (int)unsignedInt7;
                }
                final int paletteSize3 = (int)((this.bitmapOffset - 14L - unsignedInt) / 4L);
                final int n6 = paletteSize3 * 4;
                this.palette = new byte[n6];
                this.iis.readFully(this.palette, 0, n6);
                this.metadata.palette = this.palette;
                this.metadata.paletteSize = paletteSize3;
                switch ((int)this.compression) {
                    case 4:
                    case 5: {
                        if (unsignedInt == 108L) {
                            this.imageType = 17;
                            break;
                        }
                        if (unsignedInt == 124L) {
                            this.imageType = 18;
                            break;
                        }
                        break;
                    }
                    default: {
                        if (this.bitsPerPixel == 1) {
                            this.imageType = 10;
                        }
                        else if (this.bitsPerPixel == 4) {
                            this.imageType = 11;
                        }
                        else if (this.bitsPerPixel == 8) {
                            this.imageType = 12;
                        }
                        else if (this.bitsPerPixel == 16) {
                            this.imageType = 13;
                            if ((int)this.compression == 0) {
                                this.redMask = 31744;
                                this.greenMask = 992;
                                this.blueMask = 31;
                            }
                        }
                        else if (this.bitsPerPixel == 24) {
                            this.imageType = 14;
                        }
                        else if (this.bitsPerPixel == 32) {
                            this.imageType = 15;
                            if ((int)this.compression == 0) {
                                this.redMask = 16711680;
                                this.greenMask = 65280;
                                this.blueMask = 255;
                            }
                        }
                        this.metadata.redMask = this.redMask;
                        this.metadata.greenMask = this.greenMask;
                        this.metadata.blueMask = this.blueMask;
                        this.metadata.alphaMask = this.alphaMask;
                        break;
                    }
                }
            }
        }
        if (this.height > 0) {
            this.isBottomUp = true;
        }
        else {
            this.isBottomUp = false;
            this.height = Math.abs(this.height);
        }
        ColorSpace colorSpace = ColorSpace.getInstance(1000);
        if (this.metadata.colorSpace == 3 || this.metadata.colorSpace == 4) {
            this.iis.mark();
            this.iis.skipBytes(int1 - unsignedInt);
            final byte[] array2 = new byte[int2];
            this.iis.readFully(array2, 0, int2);
            this.iis.reset();
            try {
                if (this.metadata.colorSpace == 3 && isLinkedProfileAllowed() && !isUncOrDevicePath(array2)) {
                    colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(new String(array2, "windows-1252")));
                }
                else {
                    colorSpace = new ICC_ColorSpace(ICC_Profile.getInstance(array2));
                }
            }
            catch (final Exception ex) {
                colorSpace = ColorSpace.getInstance(1000);
            }
        }
        if (this.bitsPerPixel == 0 || this.compression == 4L || this.compression == 5L) {
            this.colorModel = null;
            this.sampleModel = null;
        }
        else if (this.bitsPerPixel == 1 || this.bitsPerPixel == 4 || this.bitsPerPixel == 8) {
            this.numBands = 1;
            if (this.bitsPerPixel == 8) {
                final int[] array3 = new int[this.numBands];
                for (int i = 0; i < this.numBands; ++i) {
                    array3[i] = this.numBands - 1 - i;
                }
                this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, array3);
            }
            else {
                this.sampleModel = new MultiPixelPackedSampleModel(0, this.width, this.height, this.bitsPerPixel);
            }
            long n7;
            byte[] array4;
            byte[] array5;
            byte[] array6;
            if (this.imageType == 0 || this.imageType == 1 || this.imageType == 2) {
                n7 = this.palette.length / 3;
                if (n7 > 256L) {
                    n7 = 256L;
                }
                array4 = new byte[(int)n7];
                array5 = new byte[(int)n7];
                array6 = new byte[(int)n7];
                for (int j = 0; j < (int)n7; ++j) {
                    final int n8 = 3 * j;
                    array6[j] = this.palette[n8];
                    array5[j] = this.palette[n8 + 1];
                    array4[j] = this.palette[n8 + 2];
                }
            }
            else {
                n7 = this.palette.length / 4;
                if (n7 > 256L) {
                    n7 = 256L;
                }
                array4 = new byte[(int)n7];
                array5 = new byte[(int)n7];
                array6 = new byte[(int)n7];
                for (int n9 = 0; n9 < n7; ++n9) {
                    final int n10 = 4 * n9;
                    array6[n9] = this.palette[n10];
                    array5[n9] = this.palette[n10 + 1];
                    array4[n9] = this.palette[n10 + 2];
                }
            }
            if (ImageUtil.isIndicesForGrayscale(array4, array5, array6)) {
                this.colorModel = ImageUtil.createColorModel(null, this.sampleModel);
            }
            else {
                this.colorModel = new IndexColorModel(this.bitsPerPixel, (int)n7, array4, array5, array6);
            }
        }
        else if (this.bitsPerPixel == 16) {
            this.numBands = 3;
            this.sampleModel = new SinglePixelPackedSampleModel(1, this.width, this.height, new int[] { this.redMask, this.greenMask, this.blueMask });
            this.colorModel = new DirectColorModel(colorSpace, 16, this.redMask, this.greenMask, this.blueMask, 0, false, 1);
        }
        else if (this.bitsPerPixel == 32) {
            this.numBands = ((this.alphaMask == 0) ? 3 : 4);
            this.sampleModel = new SinglePixelPackedSampleModel(3, this.width, this.height, (this.numBands == 3) ? new int[] { this.redMask, this.greenMask, this.blueMask } : new int[] { this.redMask, this.greenMask, this.blueMask, this.alphaMask });
            this.colorModel = new DirectColorModel(colorSpace, 32, this.redMask, this.greenMask, this.blueMask, this.alphaMask, false, 3);
        }
        else {
            this.numBands = 3;
            final int[] array7 = new int[this.numBands];
            for (int k = 0; k < this.numBands; ++k) {
                array7[k] = this.numBands - 1 - k;
            }
            this.sampleModel = new PixelInterleavedSampleModel(0, this.width, this.height, this.numBands, this.numBands * this.width, array7);
            this.colorModel = ImageUtil.createColorModel(colorSpace, this.sampleModel);
        }
        this.originalSampleModel = this.sampleModel;
        this.originalColorModel = this.colorModel;
        this.iis.reset();
        this.iis.skipBytes(this.bitmapOffset);
        this.gotHeader = true;
    }
    
    @Override
    public Iterator getImageTypes(final int n) throws IOException {
        this.checkIndex(n);
        try {
            this.readHeader();
        }
        catch (final IllegalArgumentException ex) {
            throw new IIOException(I18N.getString("BMPImageReader6"), ex);
        }
        final ArrayList list = new ArrayList(1);
        list.add(new ImageTypeSpecifier(this.originalColorModel, this.originalSampleModel));
        return list.iterator();
    }
    
    @Override
    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }
    
    @Override
    public IIOMetadata getImageMetadata(final int n) throws IOException {
        this.checkIndex(n);
        if (this.metadata == null) {
            try {
                this.readHeader();
            }
            catch (final IllegalArgumentException ex) {
                throw new IIOException(I18N.getString("BMPImageReader6"), ex);
            }
        }
        return this.metadata;
    }
    
    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }
    
    @Override
    public boolean isRandomAccessEasy(final int n) throws IOException {
        this.checkIndex(n);
        try {
            this.readHeader();
        }
        catch (final IllegalArgumentException ex) {
            throw new IIOException(I18N.getString("BMPImageReader6"), ex);
        }
        return this.metadata.compression == 0;
    }
    
    @Override
    public BufferedImage read(final int n, ImageReadParam defaultReadParam) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException(I18N.getString("BMPImageReader5"));
        }
        this.checkIndex(n);
        this.clearAbortRequest();
        this.processImageStarted(n);
        if (defaultReadParam == null) {
            defaultReadParam = this.getDefaultReadParam();
        }
        try {
            this.readHeader();
        }
        catch (final IllegalArgumentException ex) {
            throw new IIOException(I18N.getString("BMPImageReader6"), ex);
        }
        this.sourceRegion = new Rectangle(0, 0, 0, 0);
        this.destinationRegion = new Rectangle(0, 0, 0, 0);
        ImageReader.computeRegions(defaultReadParam, this.width, this.height, defaultReadParam.getDestination(), this.sourceRegion, this.destinationRegion);
        this.scaleX = defaultReadParam.getSourceXSubsampling();
        this.scaleY = defaultReadParam.getSourceYSubsampling();
        this.sourceBands = defaultReadParam.getSourceBands();
        this.destBands = defaultReadParam.getDestinationBands();
        this.seleBand = (this.sourceBands != null && this.destBands != null);
        this.noTransform = (this.destinationRegion.equals(new Rectangle(0, 0, this.width, this.height)) || this.seleBand);
        if (!this.seleBand) {
            this.sourceBands = new int[this.numBands];
            this.destBands = new int[this.numBands];
            for (int i = 0; i < this.numBands; ++i) {
                this.destBands[i] = (this.sourceBands[i] = i);
            }
        }
        this.bi = defaultReadParam.getDestination();
        WritableRaster writableRaster = null;
        if (this.bi == null) {
            if (this.sampleModel != null && this.colorModel != null) {
                this.sampleModel = this.sampleModel.createCompatibleSampleModel(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
                if (this.seleBand) {
                    this.sampleModel = this.sampleModel.createSubsetSampleModel(this.sourceBands);
                }
                writableRaster = Raster.createWritableRaster(this.sampleModel, new Point());
                this.bi = new BufferedImage(this.colorModel, writableRaster, false, null);
            }
        }
        else {
            writableRaster = this.bi.getWritableTile(0, 0);
            this.sampleModel = this.bi.getSampleModel();
            this.colorModel = this.bi.getColorModel();
            this.noTransform &= this.destinationRegion.equals(writableRaster.getBounds());
        }
        byte[] array = null;
        short[] array2 = null;
        int[] array3 = null;
        if (this.sampleModel != null) {
            if (this.sampleModel.getDataType() == 0) {
                array = ((DataBufferByte)writableRaster.getDataBuffer()).getData();
            }
            else if (this.sampleModel.getDataType() == 1) {
                array2 = ((DataBufferUShort)writableRaster.getDataBuffer()).getData();
            }
            else if (this.sampleModel.getDataType() == 3) {
                array3 = ((DataBufferInt)writableRaster.getDataBuffer()).getData();
            }
        }
        Label_1027: {
            switch (this.imageType) {
                case 0: {
                    this.read1Bit(array);
                    break;
                }
                case 1: {
                    this.read4Bit(array);
                    break;
                }
                case 2: {
                    this.read8Bit(array);
                    break;
                }
                case 3: {
                    this.read24Bit(array);
                    break;
                }
                case 4: {
                    this.read1Bit(array);
                    break;
                }
                case 5: {
                    switch ((int)this.compression) {
                        case 0: {
                            this.read4Bit(array);
                            break Label_1027;
                        }
                        case 2: {
                            this.readRLE4(array);
                            break Label_1027;
                        }
                        default: {
                            throw new IIOException(I18N.getString("BMPImageReader1"));
                        }
                    }
                    break;
                }
                case 6: {
                    switch ((int)this.compression) {
                        case 0: {
                            this.read8Bit(array);
                            break Label_1027;
                        }
                        case 1: {
                            this.readRLE8(array);
                            break Label_1027;
                        }
                        default: {
                            throw new IIOException(I18N.getString("BMPImageReader1"));
                        }
                    }
                    break;
                }
                case 7: {
                    this.read24Bit(array);
                    break;
                }
                case 8: {
                    this.read16Bit(array2);
                    break;
                }
                case 9: {
                    this.read32Bit(array3);
                    break;
                }
                case 16:
                case 17:
                case 18: {
                    this.bi = this.readEmbedded((int)this.compression, this.bi, defaultReadParam);
                    break;
                }
                case 10: {
                    this.read1Bit(array);
                    break;
                }
                case 11: {
                    switch ((int)this.compression) {
                        case 0: {
                            this.read4Bit(array);
                            break Label_1027;
                        }
                        case 2: {
                            this.readRLE4(array);
                            break Label_1027;
                        }
                        default: {
                            throw new IIOException(I18N.getString("BMPImageReader1"));
                        }
                    }
                    break;
                }
                case 12: {
                    switch ((int)this.compression) {
                        case 0: {
                            this.read8Bit(array);
                            break Label_1027;
                        }
                        case 1: {
                            this.readRLE8(array);
                            break Label_1027;
                        }
                        default: {
                            throw new IIOException(I18N.getString("BMPImageReader1"));
                        }
                    }
                    break;
                }
                case 13: {
                    this.read16Bit(array2);
                    break;
                }
                case 14: {
                    this.read24Bit(array);
                    break;
                }
                case 15: {
                    this.read32Bit(array3);
                    break;
                }
            }
        }
        if (this.abortRequested()) {
            this.processReadAborted();
        }
        else {
            this.processImageComplete();
        }
        return this.bi;
    }
    
    @Override
    public boolean canReadRaster() {
        return true;
    }
    
    @Override
    public Raster readRaster(final int n, final ImageReadParam imageReadParam) throws IOException {
        return this.read(n, imageReadParam).getData();
    }
    
    private void resetHeaderInfo() {
        this.gotHeader = false;
        this.bi = null;
        final SampleModel sampleModel = null;
        this.originalSampleModel = sampleModel;
        this.sampleModel = sampleModel;
        final ColorModel colorModel = null;
        this.originalColorModel = colorModel;
        this.colorModel = colorModel;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.iis = null;
        this.resetHeaderInfo();
    }
    
    private void read1Bit(final byte[] array) throws IOException {
        final int n = (this.width + 7) / 8;
        int n2 = n % 4;
        if (n2 != 0) {
            n2 = 4 - n2;
        }
        final int n3 = n + n2;
        if (this.noTransform) {
            int n4 = this.isBottomUp ? ((this.height - 1) * n) : 0;
            for (int n5 = 0; n5 < this.height && !this.abortRequested(); ++n5) {
                this.iis.readFully(array, n4, n);
                this.iis.skipBytes(n2);
                n4 += (this.isBottomUp ? (-n) : n);
                this.processImageUpdate(this.bi, 0, n5, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n5 / this.destinationRegion.height);
            }
        }
        else {
            final byte[] array2 = new byte[n3];
            final int scanlineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(n3 * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)));
            }
            else {
                this.iis.skipBytes(n3 * this.sourceRegion.y);
            }
            final int n6 = n3 * (this.scaleY - 1);
            final int[] array3 = new int[this.destinationRegion.width];
            final int[] array4 = new int[this.destinationRegion.width];
            final int[] array5 = new int[this.destinationRegion.width];
            final int[] array6 = new int[this.destinationRegion.width];
            for (int i = this.destinationRegion.x, x = this.sourceRegion.x, n7 = 0; i < this.destinationRegion.x + this.destinationRegion.width; ++i, ++n7, x += this.scaleX) {
                array5[n7] = x >> 3;
                array3[n7] = 7 - (x & 0x7);
                array6[n7] = i >> 3;
                array4[n7] = 7 - (i & 0x7);
            }
            int n8 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n8 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            for (int j = 0, y = this.sourceRegion.y; j < this.destinationRegion.height; ++j, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.read(array2, 0, n3);
                for (int k = 0; k < this.destinationRegion.width; ++k) {
                    final int n9 = array2[array5[k]] >> array3[k] & 0x1;
                    final int n10 = n8 + array6[k];
                    array[n10] |= (byte)(n9 << array4[k]);
                }
                n8 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n6);
                this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * j / this.destinationRegion.height);
            }
        }
    }
    
    private void read4Bit(final byte[] array) throws IOException {
        final int n = (this.width + 1) / 2;
        int n2 = n % 4;
        if (n2 != 0) {
            n2 = 4 - n2;
        }
        final int n3 = n + n2;
        if (this.noTransform) {
            int n4 = this.isBottomUp ? ((this.height - 1) * n) : 0;
            for (int n5 = 0; n5 < this.height && !this.abortRequested(); ++n5) {
                this.iis.readFully(array, n4, n);
                this.iis.skipBytes(n2);
                n4 += (this.isBottomUp ? (-n) : n);
                this.processImageUpdate(this.bi, 0, n5, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n5 / this.destinationRegion.height);
            }
        }
        else {
            final byte[] array2 = new byte[n3];
            final int scanlineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(n3 * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)));
            }
            else {
                this.iis.skipBytes(n3 * this.sourceRegion.y);
            }
            final int n6 = n3 * (this.scaleY - 1);
            final int[] array3 = new int[this.destinationRegion.width];
            final int[] array4 = new int[this.destinationRegion.width];
            final int[] array5 = new int[this.destinationRegion.width];
            final int[] array6 = new int[this.destinationRegion.width];
            for (int i = this.destinationRegion.x, x = this.sourceRegion.x, n7 = 0; i < this.destinationRegion.x + this.destinationRegion.width; ++i, ++n7, x += this.scaleX) {
                array5[n7] = x >> 1;
                array3[n7] = 1 - (x & 0x1) << 2;
                array6[n7] = i >> 1;
                array4[n7] = 1 - (i & 0x1) << 2;
            }
            int n8 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n8 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            for (int j = 0, y = this.sourceRegion.y; j < this.destinationRegion.height; ++j, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.read(array2, 0, n3);
                for (int k = 0; k < this.destinationRegion.width; ++k) {
                    final int n9 = array2[array5[k]] >> array3[k] & 0xF;
                    final int n10 = n8 + array6[k];
                    array[n10] |= (byte)(n9 << array4[k]);
                }
                n8 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n6);
                this.processImageUpdate(this.bi, 0, j, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * j / this.destinationRegion.height);
            }
        }
    }
    
    private void read8Bit(final byte[] array) throws IOException {
        int n = this.width % 4;
        if (n != 0) {
            n = 4 - n;
        }
        final int n2 = this.width + n;
        if (this.noTransform) {
            int n3 = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
            for (int n4 = 0; n4 < this.height && !this.abortRequested(); ++n4) {
                this.iis.readFully(array, n3, this.width);
                this.iis.skipBytes(n);
                n3 += (this.isBottomUp ? (-this.width) : this.width);
                this.processImageUpdate(this.bi, 0, n4, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n4 / this.destinationRegion.height);
            }
        }
        else {
            final byte[] array2 = new byte[n2];
            final int scanlineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(n2 * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)));
            }
            else {
                this.iis.skipBytes(n2 * this.sourceRegion.y);
            }
            final int n5 = n2 * (this.scaleY - 1);
            int n6 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n6 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            int n7 = n6 + this.destinationRegion.x;
            for (int i = 0, y = this.sourceRegion.y; i < this.destinationRegion.height; ++i, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.read(array2, 0, n2);
                for (int j = 0, x = this.sourceRegion.x; j < this.destinationRegion.width; ++j, x += this.scaleX) {
                    array[n7 + j] = array2[x];
                }
                n7 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n5);
                this.processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * i / this.destinationRegion.height);
            }
        }
    }
    
    private void read24Bit(final byte[] array) throws IOException {
        int n = this.width * 3 % 4;
        if (n != 0) {
            n = 4 - n;
        }
        final int n2 = this.width * 3;
        final int n3 = n2 + n;
        if (this.noTransform) {
            int n4 = this.isBottomUp ? ((this.height - 1) * this.width * 3) : 0;
            for (int n5 = 0; n5 < this.height && !this.abortRequested(); ++n5) {
                this.iis.readFully(array, n4, n2);
                this.iis.skipBytes(n);
                n4 += (this.isBottomUp ? (-n2) : n2);
                this.processImageUpdate(this.bi, 0, n5, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n5 / this.destinationRegion.height);
            }
        }
        else {
            final byte[] array2 = new byte[n3];
            final int scanlineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(n3 * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)));
            }
            else {
                this.iis.skipBytes(n3 * this.sourceRegion.y);
            }
            final int n6 = n3 * (this.scaleY - 1);
            int n7 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n7 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            int n8 = n7 + this.destinationRegion.x * 3;
            for (int i = 0, y = this.sourceRegion.y; i < this.destinationRegion.height; ++i, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.read(array2, 0, n3);
                for (int j = 0, n9 = 3 * this.sourceRegion.x; j < this.destinationRegion.width; ++j, n9 += 3 * this.scaleX) {
                    final int n10 = 3 * j + n8;
                    for (int k = 0; k < this.destBands.length; ++k) {
                        array[n10 + this.destBands[k]] = array2[n9 + this.sourceBands[k]];
                    }
                }
                n8 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n6);
                this.processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * i / this.destinationRegion.height);
            }
        }
    }
    
    private void read16Bit(final short[] array) throws IOException {
        int n = this.width * 2 % 4;
        if (n != 0) {
            n = 4 - n;
        }
        final int n2 = this.width + n / 2;
        if (this.noTransform) {
            int n3 = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
            for (int n4 = 0; n4 < this.height && !this.abortRequested(); ++n4) {
                this.iis.readFully(array, n3, this.width);
                this.iis.skipBytes(n);
                n3 += (this.isBottomUp ? (-this.width) : this.width);
                this.processImageUpdate(this.bi, 0, n4, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n4 / this.destinationRegion.height);
            }
        }
        else {
            final short[] array2 = new short[n2];
            final int scanlineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(n2 * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)) << 1);
            }
            else {
                this.iis.skipBytes(n2 * this.sourceRegion.y << 1);
            }
            final int n5 = n2 * (this.scaleY - 1) << 1;
            int n6 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n6 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            int n7 = n6 + this.destinationRegion.x;
            for (int i = 0, y = this.sourceRegion.y; i < this.destinationRegion.height; ++i, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.readFully(array2, 0, n2);
                for (int j = 0, x = this.sourceRegion.x; j < this.destinationRegion.width; ++j, x += this.scaleX) {
                    array[n7 + j] = array2[x];
                }
                n7 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n5);
                this.processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * i / this.destinationRegion.height);
            }
        }
    }
    
    private void read32Bit(final int[] array) throws IOException {
        if (this.noTransform) {
            int n = this.isBottomUp ? ((this.height - 1) * this.width) : 0;
            for (int n2 = 0; n2 < this.height && !this.abortRequested(); ++n2) {
                this.iis.readFully(array, n, this.width);
                n += (this.isBottomUp ? (-this.width) : this.width);
                this.processImageUpdate(this.bi, 0, n2, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * n2 / this.destinationRegion.height);
            }
        }
        else {
            final int[] array2 = new int[this.width];
            final int scanlineStride = ((SinglePixelPackedSampleModel)this.sampleModel).getScanlineStride();
            if (this.isBottomUp) {
                this.iis.skipBytes(this.width * (this.height - 1 - (this.sourceRegion.y + (this.destinationRegion.height - 1) * this.scaleY)) << 2);
            }
            else {
                this.iis.skipBytes(this.width * this.sourceRegion.y << 2);
            }
            final int n3 = this.width * (this.scaleY - 1) << 2;
            int n4 = this.destinationRegion.y * scanlineStride;
            if (this.isBottomUp) {
                n4 += (this.destinationRegion.height - 1) * scanlineStride;
            }
            int n5 = n4 + this.destinationRegion.x;
            for (int i = 0, y = this.sourceRegion.y; i < this.destinationRegion.height; ++i, y += this.scaleY) {
                if (this.abortRequested()) {
                    break;
                }
                this.iis.readFully(array2, 0, this.width);
                for (int j = 0, x = this.sourceRegion.x; j < this.destinationRegion.width; ++j, x += this.scaleX) {
                    array[n5 + j] = array2[x];
                }
                n5 += (this.isBottomUp ? (-scanlineStride) : scanlineStride);
                this.iis.skipBytes(n3);
                this.processImageUpdate(this.bi, 0, i, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                this.processImageProgress(100.0f * i / this.destinationRegion.height);
            }
        }
    }
    
    private void readRLE8(final byte[] array) throws IOException {
        int n = (int)this.imageSize;
        if (n == 0) {
            n = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int n2 = 0;
        final int n3 = this.width % 4;
        if (n3 != 0) {
            n2 = 4 - n3;
        }
        final byte[] array2 = new byte[n];
        this.iis.readFully(array2, 0, n);
        this.decodeRLE8(n, n2, array2, array);
    }
    
    private void decodeRLE8(final int n, final int n2, final byte[] array, final byte[] array2) throws IOException {
        final byte[] array3 = new byte[this.width * this.height];
        int i = 0;
        int n3 = 0;
        boolean b = false;
        int n4 = this.isBottomUp ? (this.height - 1) : 0;
        final int scanlineStride = ((ComponentSampleModel)this.sampleModel).getScanlineStride();
        int n5 = 0;
        while (i != n) {
            final int n6 = array[i++] & 0xFF;
            if (n6 == 0) {
                switch (array[i++] & 0xFF) {
                    case 0: {
                        if (n4 >= this.sourceRegion.y && n4 < this.sourceRegion.y + this.sourceRegion.height) {
                            if (this.noTransform) {
                                int n7 = n4 * this.width;
                                for (int j = 0; j < this.width; ++j) {
                                    array2[n7++] = array3[j];
                                }
                                this.processImageUpdate(this.bi, 0, n4, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                                ++n5;
                            }
                            else if ((n4 - this.sourceRegion.y) % this.scaleY == 0) {
                                final int n8 = (n4 - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                                int n9 = n8 * scanlineStride + this.destinationRegion.x;
                                for (int k = this.sourceRegion.x; k < this.sourceRegion.x + this.sourceRegion.width; k += this.scaleX) {
                                    array2[n9++] = array3[k];
                                }
                                this.processImageUpdate(this.bi, 0, n8, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                                ++n5;
                            }
                        }
                        this.processImageProgress(100.0f * n5 / this.destinationRegion.height);
                        n4 += (this.isBottomUp ? -1 : 1);
                        n3 = 0;
                        if (this.abortRequested()) {
                            b = true;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        b = true;
                        break;
                    }
                    case 2: {
                        n3 += (array[i++] & 0xFF) + (array[i] & 0xFF) * this.width;
                        break;
                    }
                    default: {
                        final int n10 = array[i - 1] & 0xFF;
                        for (int l = 0; l < n10; ++l) {
                            array3[n3++] = (byte)(array[i++] & 0xFF);
                        }
                        if ((n10 & 0x1) == 0x1) {
                            ++i;
                            break;
                        }
                        break;
                    }
                }
            }
            else {
                for (int n11 = 0; n11 < n6; ++n11) {
                    array3[n3++] = (byte)(array[i] & 0xFF);
                }
                ++i;
            }
            if (b) {
                break;
            }
        }
    }
    
    private void readRLE4(final byte[] array) throws IOException {
        int n = (int)this.imageSize;
        if (n == 0) {
            n = (int)(this.bitmapFileSize - this.bitmapOffset);
        }
        int n2 = 0;
        final int n3 = this.width % 4;
        if (n3 != 0) {
            n2 = 4 - n3;
        }
        final byte[] array2 = new byte[n];
        this.iis.readFully(array2, 0, n);
        this.decodeRLE4(n, n2, array2, array);
    }
    
    private void decodeRLE4(final int n, final int n2, final byte[] array, final byte[] array2) throws IOException {
        final byte[] array3 = new byte[this.width];
        int i = 0;
        int n3 = 0;
        boolean b = false;
        int n4 = this.isBottomUp ? (this.height - 1) : 0;
        final int scanlineStride = ((MultiPixelPackedSampleModel)this.sampleModel).getScanlineStride();
        int n5 = 0;
        while (i != n) {
            final int n6 = array[i++] & 0xFF;
            if (n6 == 0) {
                switch (array[i++] & 0xFF) {
                    case 0: {
                        if (n4 >= this.sourceRegion.y && n4 < this.sourceRegion.y + this.sourceRegion.height) {
                            if (this.noTransform) {
                                int n7 = n4 * (this.width + 1 >> 1);
                                int j = 0;
                                int n8 = 0;
                                while (j < this.width >> 1) {
                                    array2[n7++] = (byte)(array3[n8++] << 4 | array3[n8++]);
                                    ++j;
                                }
                                if ((this.width & 0x1) == 0x1) {
                                    final int n9 = n7;
                                    array2[n9] |= (byte)(array3[this.width - 1] << 4);
                                }
                                this.processImageUpdate(this.bi, 0, n4, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                                ++n5;
                            }
                            else if ((n4 - this.sourceRegion.y) % this.scaleY == 0) {
                                final int n10 = (n4 - this.sourceRegion.y) / this.scaleY + this.destinationRegion.y;
                                int n11 = n10 * scanlineStride + (this.destinationRegion.x >> 1);
                                int n12 = 1 - (this.destinationRegion.x & 0x1) << 2;
                                for (int k = this.sourceRegion.x; k < this.sourceRegion.x + this.sourceRegion.width; k += this.scaleX) {
                                    final int n13 = n11;
                                    array2[n13] |= (byte)(array3[k] << n12);
                                    n12 += 4;
                                    if (n12 == 4) {
                                        ++n11;
                                    }
                                    n12 &= 0x7;
                                }
                                this.processImageUpdate(this.bi, 0, n10, this.destinationRegion.width, 1, 1, 1, new int[] { 0 });
                                ++n5;
                            }
                        }
                        this.processImageProgress(100.0f * n5 / this.destinationRegion.height);
                        n4 += (this.isBottomUp ? -1 : 1);
                        n3 = 0;
                        if (this.abortRequested()) {
                            b = true;
                            break;
                        }
                        break;
                    }
                    case 1: {
                        b = true;
                        break;
                    }
                    case 2: {
                        n3 += (array[i++] & 0xFF) + (array[i] & 0xFF) * this.width;
                        break;
                    }
                    default: {
                        final int n14 = array[i - 1] & 0xFF;
                        for (int l = 0; l < n14; ++l) {
                            array3[n3++] = (byte)(((l & 0x1) == 0x0) ? ((array[i] & 0xF0) >> 4) : (array[i++] & 0xF));
                        }
                        if ((n14 & 0x1) == 0x1) {
                            ++i;
                        }
                        if (((int)Math.ceil(n14 / 2) & 0x1) == 0x1) {
                            ++i;
                            break;
                        }
                        break;
                    }
                }
            }
            else {
                final int[] array4 = { (array[i] & 0xF0) >> 4, array[i] & 0xF };
                for (int n15 = 0; n15 < n6 && n3 < this.width; array3[n3++] = (byte)array4[n15 & 0x1], ++n15) {}
                ++i;
            }
            if (b) {
                break;
            }
        }
    }
    
    private BufferedImage readEmbedded(final int n, BufferedImage bufferedImage, final ImageReadParam imageReadParam) throws IOException {
        String s = null;
        switch (n) {
            case 4: {
                s = "JPEG";
                break;
            }
            case 5: {
                s = "PNG";
                break;
            }
            default: {
                throw new IOException("Unexpected compression type: " + n);
            }
        }
        final ImageReader imageReader = ImageIO.getImageReadersByFormatName(s).next();
        if (imageReader == null) {
            throw new RuntimeException(I18N.getString("BMPImageReader4") + " " + s);
        }
        final byte[] array = new byte[(int)this.imageSize];
        this.iis.read(array);
        imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(array)));
        if (bufferedImage == null) {
            bufferedImage = imageReader.getImageTypes(0).next().createBufferedImage(this.destinationRegion.x + this.destinationRegion.width, this.destinationRegion.y + this.destinationRegion.height);
        }
        imageReader.addIIOReadProgressListener(new EmbeddedProgressAdapter() {
            @Override
            public void imageProgress(final ImageReader imageReader, final float n) {
                ImageReader.this.processImageProgress(n);
            }
        });
        imageReader.addIIOReadUpdateListener(new IIOReadUpdateListener() {
            @Override
            public void imageUpdate(final ImageReader imageReader, final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
                ImageReader.this.processImageUpdate(bufferedImage, n, n2, n3, n4, n5, n6, array);
            }
            
            @Override
            public void passComplete(final ImageReader imageReader, final BufferedImage bufferedImage) {
                ImageReader.this.processPassComplete(bufferedImage);
            }
            
            @Override
            public void passStarted(final ImageReader imageReader, final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int[] array) {
                ImageReader.this.processPassStarted(bufferedImage, n, n2, n3, n4, n5, n6, n7, array);
            }
            
            @Override
            public void thumbnailPassComplete(final ImageReader imageReader, final BufferedImage bufferedImage) {
            }
            
            @Override
            public void thumbnailPassStarted(final ImageReader imageReader, final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int[] array) {
            }
            
            @Override
            public void thumbnailUpdate(final ImageReader imageReader, final BufferedImage bufferedImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int[] array) {
            }
        });
        imageReader.addIIOReadWarningListener(new IIOReadWarningListener() {
            @Override
            public void warningOccurred(final ImageReader imageReader, final String s) {
                ImageReader.this.processWarningOccurred(s);
            }
        });
        final ImageReadParam defaultReadParam = imageReader.getDefaultReadParam();
        defaultReadParam.setDestination(bufferedImage);
        defaultReadParam.setDestinationBands(imageReadParam.getDestinationBands());
        defaultReadParam.setDestinationOffset(imageReadParam.getDestinationOffset());
        defaultReadParam.setSourceBands(imageReadParam.getSourceBands());
        defaultReadParam.setSourceRegion(imageReadParam.getSourceRegion());
        defaultReadParam.setSourceSubsampling(imageReadParam.getSourceXSubsampling(), imageReadParam.getSourceYSubsampling(), imageReadParam.getSubsamplingXOffset(), imageReadParam.getSubsamplingYOffset());
        imageReader.read(0, defaultReadParam);
        return bufferedImage;
    }
    
    private static boolean isLinkedProfileAllowed() {
        if (BMPImageReader.isLinkedProfileDisabled == null) {
            BMPImageReader.isLinkedProfileDisabled = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    return Boolean.getBoolean("sun.imageio.plugins.bmp.disableLinkedProfiles");
                }
            });
        }
        return !BMPImageReader.isLinkedProfileDisabled;
    }
    
    private static boolean isUncOrDevicePath(final byte[] array) {
        if (BMPImageReader.isWindowsPlatform == null) {
            BMPImageReader.isWindowsPlatform = AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
                @Override
                public Boolean run() {
                    final String property = System.getProperty("os.name");
                    return property != null && property.toLowerCase().startsWith("win");
                }
            });
        }
        if (!BMPImageReader.isWindowsPlatform) {
            return false;
        }
        if (array[0] == 47) {
            array[0] = 92;
        }
        if (array[1] == 47) {
            array[1] = 92;
        }
        if (array[3] == 47) {
            array[3] = 92;
        }
        return array[0] == 92 && array[1] == 92 && (array[2] != 63 || array[3] != 92 || ((array[4] == 85 || array[4] == 117) && (array[5] == 78 || array[5] == 110) && (array[6] == 67 || array[6] == 99)));
    }
    
    static {
        BMPImageReader.isLinkedProfileDisabled = null;
        BMPImageReader.isWindowsPlatform = null;
    }
    
    private class EmbeddedProgressAdapter implements IIOReadProgressListener
    {
        @Override
        public void imageComplete(final ImageReader imageReader) {
        }
        
        @Override
        public void imageProgress(final ImageReader imageReader, final float n) {
        }
        
        @Override
        public void imageStarted(final ImageReader imageReader, final int n) {
        }
        
        @Override
        public void thumbnailComplete(final ImageReader imageReader) {
        }
        
        @Override
        public void thumbnailProgress(final ImageReader imageReader, final float n) {
        }
        
        @Override
        public void thumbnailStarted(final ImageReader imageReader, final int n, final int n2) {
        }
        
        @Override
        public void sequenceComplete(final ImageReader imageReader) {
        }
        
        @Override
        public void sequenceStarted(final ImageReader imageReader, final int n) {
        }
        
        @Override
        public void readAborted(final ImageReader imageReader) {
        }
    }
}
