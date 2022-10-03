package com.sun.imageio.plugins.bmp;

import java.util.Iterator;
import javax.imageio.event.IIOWriteWarningListener;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.ImageIO;
import java.awt.image.ColorModel;
import java.awt.image.SampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferByte;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.DataBuffer;
import java.io.IOException;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import com.sun.imageio.plugins.common.ImageUtil;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.ImageWriteParam;
import java.nio.ByteOrder;
import com.sun.imageio.plugins.common.I18N;
import javax.imageio.spi.ImageWriterSpi;
import java.io.ByteArrayOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;

public class BMPImageWriter extends ImageWriter implements BMPConstants
{
    private ImageOutputStream stream;
    private ByteArrayOutputStream embedded_stream;
    private int version;
    private int compressionType;
    private boolean isTopDown;
    private int w;
    private int h;
    private int compImageSize;
    private int[] bitMasks;
    private int[] bitPos;
    private byte[] bpixels;
    private short[] spixels;
    private int[] ipixels;
    
    public BMPImageWriter(final ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
        this.stream = null;
        this.embedded_stream = null;
        this.compImageSize = 0;
    }
    
    @Override
    public void setOutput(final Object output) {
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException(I18N.getString("BMPImageWriter0"));
            }
            (this.stream = (ImageOutputStream)output).setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
        else {
            this.stream = null;
        }
    }
    
    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new BMPImageWriteParam();
    }
    
    @Override
    public IIOMetadata getDefaultStreamMetadata(final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        final BMPMetadata bmpMetadata = new BMPMetadata();
        bmpMetadata.bmpVersion = "BMP v. 3.x";
        bmpMetadata.compression = this.getPreferredCompressionType(imageTypeSpecifier);
        if (imageWriteParam != null && imageWriteParam.getCompressionMode() == 2) {
            bmpMetadata.compression = BMPCompressionTypes.getType(imageWriteParam.getCompressionType());
        }
        bmpMetadata.bitsPerPixel = (short)imageTypeSpecifier.getColorModel().getPixelSize();
        return bmpMetadata;
    }
    
    @Override
    public IIOMetadata convertStreamMetadata(final IIOMetadata iioMetadata, final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public IIOMetadata convertImageMetadata(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public boolean canWriteRasters() {
        return true;
    }
    
    @Override
    public void write(final IIOMetadata iioMetadata, final IIOImage iioImage, ImageWriteParam defaultWriteParam) throws IOException {
        if (this.stream == null) {
            throw new IllegalStateException(I18N.getString("BMPImageWriter7"));
        }
        if (iioImage == null) {
            throw new IllegalArgumentException(I18N.getString("BMPImageWriter8"));
        }
        this.clearAbortRequest();
        this.processImageStarted(0);
        if (defaultWriteParam == null) {
            defaultWriteParam = this.getDefaultWriteParam();
        }
        final BMPImageWriteParam bmpImageWriteParam = (BMPImageWriteParam)defaultWriteParam;
        int n = 24;
        int n2 = 0;
        int mapSize = 0;
        IndexColorModel indexColorModel = null;
        RenderedImage renderedImage = null;
        Raster raster = null;
        final boolean hasRaster = iioImage.hasRaster();
        final Rectangle sourceRegion = defaultWriteParam.getSourceRegion();
        this.compImageSize = 0;
        SampleModel sampleModel;
        ColorModel colorModel;
        Rectangle rectangle;
        if (hasRaster) {
            raster = iioImage.getRaster();
            sampleModel = raster.getSampleModel();
            colorModel = ImageUtil.createColorModel(null, sampleModel);
            if (sourceRegion == null) {
                rectangle = raster.getBounds();
            }
            else {
                rectangle = sourceRegion.intersection(raster.getBounds());
            }
        }
        else {
            renderedImage = iioImage.getRenderedImage();
            sampleModel = renderedImage.getSampleModel();
            colorModel = renderedImage.getColorModel();
            final Rectangle rectangle2 = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
            if (sourceRegion == null) {
                rectangle = rectangle2;
            }
            else {
                rectangle = sourceRegion.intersection(rectangle2);
            }
        }
        final IIOMetadata metadata = iioImage.getMetadata();
        BMPMetadata bmpMetadata;
        if (metadata != null && metadata instanceof BMPMetadata) {
            bmpMetadata = (BMPMetadata)metadata;
        }
        else {
            bmpMetadata = (BMPMetadata)this.getDefaultImageMetadata(new ImageTypeSpecifier(colorModel, sampleModel), defaultWriteParam);
        }
        if (rectangle.isEmpty()) {
            throw new RuntimeException(I18N.getString("BMPImageWrite0"));
        }
        final int sourceXSubsampling = defaultWriteParam.getSourceXSubsampling();
        final int sourceYSubsampling = defaultWriteParam.getSourceYSubsampling();
        final int subsamplingXOffset = defaultWriteParam.getSubsamplingXOffset();
        final int subsamplingYOffset = defaultWriteParam.getSubsamplingYOffset();
        final int dataType = sampleModel.getDataType();
        rectangle.translate(subsamplingXOffset, subsamplingYOffset);
        final Rectangle rectangle3 = rectangle;
        rectangle3.width -= subsamplingXOffset;
        final Rectangle rectangle4 = rectangle;
        rectangle4.height -= subsamplingYOffset;
        final int n3 = rectangle.x / sourceXSubsampling;
        final int n4 = rectangle.y / sourceYSubsampling;
        this.w = (rectangle.width + sourceXSubsampling - 1) / sourceXSubsampling;
        this.h = (rectangle.height + sourceYSubsampling - 1) / sourceYSubsampling;
        final int n5 = rectangle.x % sourceXSubsampling;
        final int n6 = rectangle.y % sourceYSubsampling;
        final boolean equals = new Rectangle(n3, n4, this.w, this.h).equals(rectangle);
        int[] sourceBands = defaultWriteParam.getSourceBands();
        boolean b = true;
        int n7 = sampleModel.getNumBands();
        if (sourceBands != null) {
            sampleModel = sampleModel.createSubsetSampleModel(sourceBands);
            colorModel = null;
            b = false;
            n7 = sampleModel.getNumBands();
        }
        else {
            sourceBands = new int[n7];
            for (int i = 0; i < n7; ++i) {
                sourceBands[i] = i;
            }
        }
        int[] bandOffsets = null;
        int n8 = 1;
        if (sampleModel instanceof ComponentSampleModel) {
            bandOffsets = ((ComponentSampleModel)sampleModel).getBandOffsets();
            if (sampleModel instanceof BandedSampleModel) {
                n8 = 0;
            }
            else {
                for (int j = 0; j < bandOffsets.length; ++j) {
                    n8 &= ((bandOffsets[j] == bandOffsets.length - j - 1) ? 1 : 0);
                }
            }
        }
        else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            final int[] bitOffsets = ((SinglePixelPackedSampleModel)sampleModel).getBitOffsets();
            for (int k = 0; k < bitOffsets.length - 1; ++k) {
                n8 &= ((bitOffsets[k] > bitOffsets[k + 1]) ? 1 : 0);
            }
        }
        if (bandOffsets == null) {
            bandOffsets = new int[n7];
            for (int l = 0; l < n7; ++l) {
                bandOffsets[l] = l;
            }
        }
        int n9 = (equals ? 1 : 0) & n8;
        final int[] sampleSize = sampleModel.getSampleSize();
        int n10 = this.w * n7;
        switch (bmpImageWriteParam.getCompressionMode()) {
            case 2: {
                this.compressionType = BMPCompressionTypes.getType(bmpImageWriteParam.getCompressionType());
                break;
            }
            case 3: {
                this.compressionType = bmpMetadata.compression;
                break;
            }
            case 1: {
                this.compressionType = this.getPreferredCompressionType(colorModel, sampleModel);
                break;
            }
            default: {
                this.compressionType = 0;
                break;
            }
        }
        if (!this.canEncodeImage(this.compressionType, colorModel, sampleModel)) {
            throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType));
        }
        byte[] array = null;
        byte[] array2 = null;
        byte[] array3 = null;
        byte[] array4 = null;
        if (this.compressionType == 3) {
            n = DataBuffer.getDataTypeSize(sampleModel.getDataType());
            if (n != 16 && n != 32) {
                n = 32;
                n9 = 0;
            }
            n10 = this.w * n + 7 >> 3;
            n2 = 1;
            mapSize = 3;
            array = new byte[mapSize];
            array2 = new byte[mapSize];
            array3 = new byte[mapSize];
            array4 = new byte[mapSize];
            int redMask = 16711680;
            int greenMask = 65280;
            int blueMask = 255;
            if (n == 16) {
                if (!(colorModel instanceof DirectColorModel)) {
                    throw new IOException("Image can not be encoded with compression type " + BMPCompressionTypes.getName(this.compressionType));
                }
                final DirectColorModel directColorModel = (DirectColorModel)colorModel;
                redMask = directColorModel.getRedMask();
                greenMask = directColorModel.getGreenMask();
                blueMask = directColorModel.getBlueMask();
            }
            this.writeMaskToPalette(redMask, 0, array, array2, array3, array4);
            this.writeMaskToPalette(greenMask, 1, array, array2, array3, array4);
            this.writeMaskToPalette(blueMask, 2, array, array2, array3, array4);
            if (n9 == 0) {
                (this.bitMasks = new int[3])[0] = redMask;
                this.bitMasks[1] = greenMask;
                this.bitMasks[2] = blueMask;
                (this.bitPos = new int[3])[0] = this.firstLowBit(redMask);
                this.bitPos[1] = this.firstLowBit(greenMask);
                this.bitPos[2] = this.firstLowBit(blueMask);
            }
            if (colorModel instanceof IndexColorModel) {
                indexColorModel = (IndexColorModel)colorModel;
            }
        }
        else if (colorModel instanceof IndexColorModel) {
            n2 = 1;
            indexColorModel = (IndexColorModel)colorModel;
            mapSize = indexColorModel.getMapSize();
            if (mapSize <= 2) {
                n = 1;
                n10 = this.w + 7 >> 3;
            }
            else if (mapSize <= 16) {
                n = 4;
                n10 = this.w + 1 >> 1;
            }
            else if (mapSize <= 256) {
                n = 8;
            }
            else {
                n = 24;
                n2 = 0;
                mapSize = 0;
                n10 = this.w * 3;
            }
            if (n2 == 1) {
                array = new byte[mapSize];
                array2 = new byte[mapSize];
                array3 = new byte[mapSize];
                array4 = new byte[mapSize];
                indexColorModel.getAlphas(array4);
                indexColorModel.getReds(array);
                indexColorModel.getGreens(array2);
                indexColorModel.getBlues(array3);
            }
        }
        else if (n7 == 1) {
            n2 = 1;
            mapSize = 256;
            n = sampleSize[0];
            n10 = this.w * n + 7 >> 3;
            array = new byte[256];
            array2 = new byte[256];
            array3 = new byte[256];
            array4 = new byte[256];
            for (int n11 = 0; n11 < 256; ++n11) {
                array[n11] = (byte)n11;
                array2[n11] = (byte)n11;
                array3[n11] = (byte)n11;
                array4[n11] = -1;
            }
        }
        else if (sampleModel instanceof SinglePixelPackedSampleModel && b) {
            final int[] sampleSize2 = sampleModel.getSampleSize();
            int n12 = 0;
            final int[] array5 = sampleSize2;
            for (int length = array5.length, n13 = 0; n13 < length; ++n13) {
                n12 += array5[n13];
            }
            n = this.roundBpp(n12);
            if (n != DataBuffer.getDataTypeSize(sampleModel.getDataType())) {
                n9 = 0;
            }
            n10 = this.w * n + 7 >> 3;
        }
        final int n14 = 0;
        final int n15 = 0;
        final int n16 = 0;
        final int n17 = mapSize;
        int n18 = n10 % 4;
        if (n18 != 0) {
            n18 = 4 - n18;
        }
        final int n19 = 54 + mapSize * 4;
        final int n20 = (n10 + n18) * this.h;
        final int n21 = n20 + n19;
        final int n22 = 40;
        final long streamPosition = this.stream.getStreamPosition();
        this.writeFileHeader(n21, n19);
        if (this.compressionType == 0 || this.compressionType == 3) {
            this.isTopDown = bmpImageWriteParam.isTopDown();
        }
        else {
            this.isTopDown = false;
        }
        this.writeInfoHeader(n22, n);
        this.stream.writeInt(this.compressionType);
        this.stream.writeInt(n20);
        this.stream.writeInt(n14);
        this.stream.writeInt(n15);
        this.stream.writeInt(n16);
        this.stream.writeInt(n17);
        if (n2 == 1) {
            if (this.compressionType == 3) {
                for (int n23 = 0; n23 < 3; ++n23) {
                    this.stream.writeInt((array4[n23] & 0xFF) + (array[n23] & 0xFF) * 256 + (array2[n23] & 0xFF) * 65536 + (array3[n23] & 0xFF) * 16777216);
                }
            }
            else {
                for (int n24 = 0; n24 < mapSize; ++n24) {
                    this.stream.writeByte(array3[n24]);
                    this.stream.writeByte(array2[n24]);
                    this.stream.writeByte(array[n24]);
                    this.stream.writeByte(array4[n24]);
                }
            }
        }
        final int n25 = this.w * n7;
        final int[] array6 = new int[n25 * sourceXSubsampling];
        this.bpixels = new byte[n10];
        if (this.compressionType == 4 || this.compressionType == 5) {
            this.embedded_stream = new ByteArrayOutputStream();
            this.writeEmbedded(iioImage, bmpImageWriteParam);
            this.embedded_stream.flush();
            final int size = this.embedded_stream.size();
            final long streamPosition2 = this.stream.getStreamPosition();
            final int n26 = n19 + size;
            this.stream.seek(streamPosition);
            this.writeSize(n26, 2);
            this.stream.seek(streamPosition);
            this.writeSize(size, 34);
            this.stream.seek(streamPosition2);
            this.stream.write(this.embedded_stream.toByteArray());
            this.embedded_stream = null;
            if (this.abortRequested()) {
                this.processWriteAborted();
            }
            else {
                this.processImageComplete();
                this.stream.flushBefore(this.stream.getStreamPosition());
            }
            return;
        }
        int n27 = bandOffsets[0];
        for (int n28 = 1; n28 < bandOffsets.length; ++n28) {
            if (bandOffsets[n28] > n27) {
                n27 = bandOffsets[n28];
            }
        }
        final int[] array7 = new int[n27 + 1];
        int n29 = n10;
        if (n9 != 0 && b) {
            n29 = n10 / (DataBuffer.getDataTypeSize(dataType) >> 3);
        }
        for (int n30 = 0; n30 < this.h && !this.abortRequested(); ++n30) {
            int n31 = n4 + n30;
            if (!this.isTopDown) {
                n31 = n4 + this.h - n30 - 1;
            }
            Raster data = raster;
            final Rectangle rectangle5 = new Rectangle(n3 * sourceXSubsampling + n5, n31 * sourceYSubsampling + n6, (this.w - 1) * sourceXSubsampling + 1, 1);
            if (!hasRaster) {
                data = renderedImage.getData(rectangle5);
            }
            if (n9 != 0 && b) {
                final SampleModel sampleModel2 = data.getSampleModel();
                int n32 = 0;
                final int n33 = rectangle5.x - data.getSampleModelTranslateX();
                final int n34 = rectangle5.y - data.getSampleModelTranslateY();
                if (sampleModel2 instanceof ComponentSampleModel) {
                    final ComponentSampleModel componentSampleModel = (ComponentSampleModel)sampleModel2;
                    n32 = componentSampleModel.getOffset(n33, n34, 0);
                    for (int n35 = 1; n35 < componentSampleModel.getNumBands(); ++n35) {
                        if (n32 > componentSampleModel.getOffset(n33, n34, n35)) {
                            n32 = componentSampleModel.getOffset(n33, n34, n35);
                        }
                    }
                }
                else if (sampleModel2 instanceof MultiPixelPackedSampleModel) {
                    n32 = ((MultiPixelPackedSampleModel)sampleModel2).getOffset(n33, n34);
                }
                else if (sampleModel2 instanceof SinglePixelPackedSampleModel) {
                    n32 = ((SinglePixelPackedSampleModel)sampleModel2).getOffset(n33, n34);
                }
                if (this.compressionType == 0 || this.compressionType == 3) {
                    switch (dataType) {
                        case 0: {
                            this.stream.write(((DataBufferByte)data.getDataBuffer()).getData(), n32, n29);
                            break;
                        }
                        case 2: {
                            this.stream.writeShorts(((DataBufferShort)data.getDataBuffer()).getData(), n32, n29);
                            break;
                        }
                        case 1: {
                            this.stream.writeShorts(((DataBufferUShort)data.getDataBuffer()).getData(), n32, n29);
                            break;
                        }
                        case 3: {
                            this.stream.writeInts(((DataBufferInt)data.getDataBuffer()).getData(), n32, n29);
                            break;
                        }
                    }
                    for (int n36 = 0; n36 < n18; ++n36) {
                        this.stream.writeByte(0);
                    }
                }
                else if (this.compressionType == 2) {
                    if (this.bpixels == null || this.bpixels.length < n25) {
                        this.bpixels = new byte[n25];
                    }
                    data.getPixels(rectangle5.x, rectangle5.y, rectangle5.width, rectangle5.height, array6);
                    for (int n37 = 0; n37 < n25; ++n37) {
                        this.bpixels[n37] = (byte)array6[n37];
                    }
                    this.encodeRLE4(this.bpixels, n25);
                }
                else if (this.compressionType == 1) {
                    if (this.bpixels == null || this.bpixels.length < n25) {
                        this.bpixels = new byte[n25];
                    }
                    data.getPixels(rectangle5.x, rectangle5.y, rectangle5.width, rectangle5.height, array6);
                    for (int n38 = 0; n38 < n25; ++n38) {
                        this.bpixels[n38] = (byte)array6[n38];
                    }
                    this.encodeRLE8(this.bpixels, n25);
                }
            }
            else {
                data.getPixels(rectangle5.x, rectangle5.y, rectangle5.width, rectangle5.height, array6);
                if (sourceXSubsampling != 1 || n27 != n7 - 1) {
                    for (int n39 = 0, n40 = 0, n41 = 0; n39 < this.w; ++n39, n40 += sourceXSubsampling * n7, n41 += n7) {
                        System.arraycopy(array6, n40, array7, 0, array7.length);
                        for (int n42 = 0; n42 < n7; ++n42) {
                            array6[n41 + n42] = array7[sourceBands[n42]];
                        }
                    }
                }
                this.writePixels(0, n25, n, array6, n18, n7, indexColorModel);
            }
            this.processImageProgress(100.0f * (n30 / (float)this.h));
        }
        if (this.compressionType == 2 || this.compressionType == 1) {
            this.stream.writeByte(0);
            this.stream.writeByte(1);
            this.incCompImageSize(2);
            final int compImageSize = this.compImageSize;
            final int n43 = this.compImageSize + n19;
            final long streamPosition3 = this.stream.getStreamPosition();
            this.stream.seek(streamPosition);
            this.writeSize(n43, 2);
            this.stream.seek(streamPosition);
            this.writeSize(compImageSize, 34);
            this.stream.seek(streamPosition3);
        }
        if (this.abortRequested()) {
            this.processWriteAborted();
        }
        else {
            this.processImageComplete();
            this.stream.flushBefore(this.stream.getStreamPosition());
        }
    }
    
    private void writePixels(int n, final int n2, final int n3, final int[] array, final int n4, final int n5, final IndexColorModel indexColorModel) throws IOException {
        int n6 = 0;
        switch (n3) {
            case 1: {
                for (int i = 0; i < n2 / 8; ++i) {
                    this.bpixels[n6++] = (byte)(array[n++] << 7 | array[n++] << 6 | array[n++] << 5 | array[n++] << 4 | array[n++] << 3 | array[n++] << 2 | array[n++] << 1 | array[n++]);
                }
                if (n2 % 8 > 0) {
                    int n7 = 0;
                    for (int j = 0; j < n2 % 8; ++j) {
                        n7 |= array[n++] << 7 - j;
                    }
                    this.bpixels[n6++] = (byte)n7;
                }
                this.stream.write(this.bpixels, 0, (n2 + 7) / 8);
                break;
            }
            case 4: {
                if (this.compressionType == 2) {
                    final byte[] array2 = new byte[n2];
                    for (int k = 0; k < n2; ++k) {
                        array2[k] = (byte)array[n++];
                    }
                    this.encodeRLE4(array2, n2);
                    break;
                }
                for (int l = 0; l < n2 / 2; ++l) {
                    this.bpixels[n6++] = (byte)(array[n++] << 4 | array[n++]);
                }
                if (n2 % 2 == 1) {
                    this.bpixels[n6++] = (byte)(array[n] << 4);
                }
                this.stream.write(this.bpixels, 0, (n2 + 1) / 2);
                break;
            }
            case 8: {
                if (this.compressionType == 1) {
                    for (int n8 = 0; n8 < n2; ++n8) {
                        this.bpixels[n8] = (byte)array[n++];
                    }
                    this.encodeRLE8(this.bpixels, n2);
                    break;
                }
                for (int n9 = 0; n9 < n2; ++n9) {
                    this.bpixels[n9] = (byte)array[n++];
                }
                this.stream.write(this.bpixels, 0, n2);
                break;
            }
            case 16: {
                if (this.spixels == null) {
                    this.spixels = new short[n2 / n5];
                }
                int n10 = 0;
                int n11 = 0;
                while (n10 < n2) {
                    this.spixels[n11] = 0;
                    if (this.compressionType == 0) {
                        this.spixels[n11] = (short)((0x1F & array[n10]) << 10 | (0x1F & array[n10 + 1]) << 5 | (0x1F & array[n10 + 2]));
                        n10 += 3;
                    }
                    else {
                        for (int n12 = 0; n12 < n5; ++n12, ++n10) {
                            final short[] spixels = this.spixels;
                            final int n13 = n11;
                            spixels[n13] |= (short)(array[n10] << this.bitPos[n12] & this.bitMasks[n12]);
                        }
                    }
                    ++n11;
                }
                this.stream.writeShorts(this.spixels, 0, this.spixels.length);
                break;
            }
            case 24: {
                if (n5 == 3) {
                    for (int n14 = 0; n14 < n2; n14 += 3) {
                        this.bpixels[n6++] = (byte)array[n + 2];
                        this.bpixels[n6++] = (byte)array[n + 1];
                        this.bpixels[n6++] = (byte)array[n];
                        n += 3;
                    }
                    this.stream.write(this.bpixels, 0, n2);
                    break;
                }
                final int mapSize = indexColorModel.getMapSize();
                final byte[] array3 = new byte[mapSize];
                final byte[] array4 = new byte[mapSize];
                final byte[] array5 = new byte[mapSize];
                indexColorModel.getReds(array3);
                indexColorModel.getGreens(array4);
                indexColorModel.getBlues(array5);
                for (int n15 = 0; n15 < n2; ++n15) {
                    final int n16 = array[n];
                    this.bpixels[n6++] = array5[n16];
                    this.bpixels[n6++] = array4[n16];
                    this.bpixels[n6++] = array5[n16];
                    ++n;
                }
                this.stream.write(this.bpixels, 0, n2 * 3);
                break;
            }
            case 32: {
                if (this.ipixels == null) {
                    this.ipixels = new int[n2 / n5];
                }
                if (n5 == 3) {
                    int n17 = 0;
                    int n18 = 0;
                    while (n17 < n2) {
                        this.ipixels[n18] = 0;
                        if (this.compressionType == 0) {
                            this.ipixels[n18] = ((0xFF & array[n17 + 2]) << 16 | (0xFF & array[n17 + 1]) << 8 | (0xFF & array[n17]));
                            n17 += 3;
                        }
                        else {
                            for (int n19 = 0; n19 < n5; ++n19, ++n17) {
                                final int[] ipixels = this.ipixels;
                                final int n20 = n18;
                                ipixels[n20] |= (array[n17] << this.bitPos[n19] & this.bitMasks[n19]);
                            }
                        }
                        ++n18;
                    }
                }
                else {
                    for (int n21 = 0; n21 < n2; ++n21) {
                        if (indexColorModel != null) {
                            this.ipixels[n21] = indexColorModel.getRGB(array[n21]);
                        }
                        else {
                            this.ipixels[n21] = (array[n21] << 16 | array[n21] << 8 | array[n21]);
                        }
                    }
                }
                this.stream.writeInts(this.ipixels, 0, this.ipixels.length);
                break;
            }
        }
        if (this.compressionType == 0 || this.compressionType == 3) {
            for (int n22 = 0; n22 < n4; ++n22) {
                this.stream.writeByte(0);
            }
        }
    }
    
    private void encodeRLE8(final byte[] array, final int n) throws IOException {
        int n2 = 1;
        int n3 = -1;
        int i = -1;
        byte b = array[++i];
        final byte[] array2 = new byte[256];
        while (i < n - 1) {
            final byte b2 = array[++i];
            if (b2 == b) {
                if (n3 >= 3) {
                    this.stream.writeByte(0);
                    this.stream.writeByte(n3);
                    this.incCompImageSize(2);
                    for (int j = 0; j < n3; ++j) {
                        this.stream.writeByte(array2[j]);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(n3)) {
                        this.stream.writeByte(0);
                        this.incCompImageSize(1);
                    }
                }
                else if (n3 > -1) {
                    for (int k = 0; k < n3; ++k) {
                        this.stream.writeByte(1);
                        this.stream.writeByte(array2[k]);
                        this.incCompImageSize(2);
                    }
                }
                n3 = -1;
                if (++n2 == 256) {
                    this.stream.writeByte(n2 - 1);
                    this.stream.writeByte(b);
                    this.incCompImageSize(2);
                    n2 = 1;
                }
            }
            else {
                if (n2 > 1) {
                    this.stream.writeByte(n2);
                    this.stream.writeByte(b);
                    this.incCompImageSize(2);
                }
                else if (n3 < 0) {
                    array2[++n3] = b;
                    array2[++n3] = b2;
                }
                else if (n3 < 254) {
                    array2[++n3] = b2;
                }
                else {
                    this.stream.writeByte(0);
                    this.stream.writeByte(n3 + 1);
                    this.incCompImageSize(2);
                    for (int l = 0; l <= n3; ++l) {
                        this.stream.writeByte(array2[l]);
                        this.incCompImageSize(1);
                    }
                    this.stream.writeByte(0);
                    this.incCompImageSize(1);
                    n3 = -1;
                }
                b = b2;
                n2 = 1;
            }
            if (i == n - 1) {
                if (n3 == -1) {
                    this.stream.writeByte(n2);
                    this.stream.writeByte(b);
                    this.incCompImageSize(2);
                    n2 = 1;
                }
                else if (n3 >= 2) {
                    this.stream.writeByte(0);
                    this.stream.writeByte(n3 + 1);
                    this.incCompImageSize(2);
                    for (int n4 = 0; n4 <= n3; ++n4) {
                        this.stream.writeByte(array2[n4]);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(n3 + 1)) {
                        this.stream.writeByte(0);
                        this.incCompImageSize(1);
                    }
                }
                else if (n3 > -1) {
                    for (int n5 = 0; n5 <= n3; ++n5) {
                        this.stream.writeByte(1);
                        this.stream.writeByte(array2[n5]);
                        this.incCompImageSize(2);
                    }
                }
                this.stream.writeByte(0);
                this.stream.writeByte(0);
                this.incCompImageSize(2);
            }
        }
    }
    
    private void encodeRLE4(final byte[] array, final int n) throws IOException {
        int n2 = 2;
        int n3 = -1;
        int i = -1;
        final byte[] array2 = new byte[256];
        byte b = array[++i];
        byte b2 = array[++i];
        while (i < n - 2) {
            final byte b3 = array[++i];
            final byte b4 = array[++i];
            if (b3 == b) {
                if (n3 >= 4) {
                    this.stream.writeByte(0);
                    this.stream.writeByte(n3 - 1);
                    this.incCompImageSize(2);
                    for (int j = 0; j < n3 - 2; j += 2) {
                        this.stream.writeByte((byte)(array2[j] << 4 | array2[j + 1]));
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven(n3 - 1)) {
                        this.stream.writeByte(array2[n3 - 2] << 4 | 0x0);
                        this.incCompImageSize(1);
                    }
                    if (!this.isEven((int)Math.ceil((n3 - 1) / 2))) {
                        this.stream.writeByte(0);
                        this.incCompImageSize(1);
                    }
                }
                else if (n3 > -1) {
                    this.stream.writeByte(2);
                    this.stream.writeByte(array2[0] << 4 | array2[1]);
                    this.incCompImageSize(2);
                }
                n3 = -1;
                if (b4 == b2) {
                    n2 += 2;
                    if (n2 == 256) {
                        this.stream.writeByte(n2 - 1);
                        this.stream.writeByte(b << 4 | b2);
                        this.incCompImageSize(2);
                        n2 = 2;
                        if (i < n - 1) {
                            b = b2;
                            b2 = array[++i];
                        }
                        else {
                            this.stream.writeByte(1);
                            this.stream.writeByte(b2 << 4 | 0x0);
                            this.incCompImageSize(2);
                            n2 = -1;
                        }
                    }
                }
                else {
                    ++n2;
                    final int n4 = b << 4 | b2;
                    this.stream.writeByte(n2);
                    this.stream.writeByte(n4);
                    this.incCompImageSize(2);
                    n2 = 2;
                    b = b4;
                    if (i < n - 1) {
                        b2 = array[++i];
                    }
                    else {
                        this.stream.writeByte(1);
                        this.stream.writeByte(b4 << 4 | 0x0);
                        this.incCompImageSize(2);
                        n2 = -1;
                    }
                }
            }
            else {
                if (n2 > 2) {
                    final int n5 = b << 4 | b2;
                    this.stream.writeByte(n2);
                    this.stream.writeByte(n5);
                    this.incCompImageSize(2);
                }
                else if (n3 < 0) {
                    array2[++n3] = b;
                    array2[++n3] = b2;
                    array2[++n3] = b3;
                    array2[++n3] = b4;
                }
                else if (n3 < 253) {
                    array2[++n3] = b3;
                    array2[++n3] = b4;
                }
                else {
                    this.stream.writeByte(0);
                    this.stream.writeByte(n3 + 1);
                    this.incCompImageSize(2);
                    for (int k = 0; k < n3; k += 2) {
                        this.stream.writeByte((byte)(array2[k] << 4 | array2[k + 1]));
                        this.incCompImageSize(1);
                    }
                    this.stream.writeByte(0);
                    this.incCompImageSize(1);
                    n3 = -1;
                }
                b = b3;
                b2 = b4;
                n2 = 2;
            }
            if (i >= n - 2) {
                if (n3 == -1 && n2 >= 2) {
                    if (i == n - 2) {
                        if (array[++i] == b) {
                            ++n2;
                            final int n6 = b << 4 | b2;
                            this.stream.writeByte(n2);
                            this.stream.writeByte(n6);
                            this.incCompImageSize(2);
                        }
                        else {
                            final int n7 = b << 4 | b2;
                            this.stream.writeByte(n2);
                            this.stream.writeByte(n7);
                            this.stream.writeByte(1);
                            this.stream.writeByte(array[i] << 4 | 0x0);
                            final int n8 = array[i] << 4 | 0x0;
                            this.incCompImageSize(4);
                        }
                    }
                    else {
                        this.stream.writeByte(n2);
                        this.stream.writeByte(b << 4 | b2);
                        this.incCompImageSize(2);
                    }
                }
                else if (n3 > -1) {
                    if (i == n - 2) {
                        array2[++n3] = array[++i];
                    }
                    if (n3 >= 2) {
                        this.stream.writeByte(0);
                        this.stream.writeByte(n3 + 1);
                        this.incCompImageSize(2);
                        for (int l = 0; l < n3; l += 2) {
                            this.stream.writeByte((byte)(array2[l] << 4 | array2[l + 1]));
                            this.incCompImageSize(1);
                        }
                        if (!this.isEven(n3 + 1)) {
                            this.stream.writeByte(array2[n3] << 4 | 0x0);
                            this.incCompImageSize(1);
                        }
                        if (!this.isEven((int)Math.ceil((n3 + 1) / 2))) {
                            this.stream.writeByte(0);
                            this.incCompImageSize(1);
                        }
                    }
                    else {
                        switch (n3) {
                            case 0: {
                                this.stream.writeByte(1);
                                this.stream.writeByte(array2[0] << 4 | 0x0);
                                this.incCompImageSize(2);
                                break;
                            }
                            case 1: {
                                this.stream.writeByte(2);
                                this.stream.writeByte(array2[0] << 4 | array2[1]);
                                this.incCompImageSize(2);
                                break;
                            }
                        }
                    }
                }
                this.stream.writeByte(0);
                this.stream.writeByte(0);
                this.incCompImageSize(2);
            }
        }
    }
    
    private synchronized void incCompImageSize(final int n) {
        this.compImageSize += n;
    }
    
    private boolean isEven(final int n) {
        return n % 2 == 0;
    }
    
    private void writeFileHeader(final int n, final int n2) throws IOException {
        this.stream.writeByte(66);
        this.stream.writeByte(77);
        this.stream.writeInt(n);
        this.stream.writeInt(0);
        this.stream.writeInt(n2);
    }
    
    private void writeInfoHeader(final int n, final int n2) throws IOException {
        this.stream.writeInt(n);
        this.stream.writeInt(this.w);
        this.stream.writeInt(this.isTopDown ? (-this.h) : this.h);
        this.stream.writeShort(1);
        this.stream.writeShort(n2);
    }
    
    private void writeSize(final int n, final int n2) throws IOException {
        this.stream.skipBytes(n2);
        this.stream.writeInt(n);
    }
    
    @Override
    public void reset() {
        super.reset();
        this.stream = null;
    }
    
    private void writeEmbedded(final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        final String s = (this.compressionType == 4) ? "jpeg" : "png";
        final Iterator<ImageWriter> imageWritersByFormatName = ImageIO.getImageWritersByFormatName(s);
        ImageWriter imageWriter = null;
        if (imageWritersByFormatName.hasNext()) {
            imageWriter = imageWritersByFormatName.next();
        }
        if (imageWriter == null) {
            throw new RuntimeException(I18N.getString("BMPImageWrite5") + " " + s);
        }
        if (this.embedded_stream == null) {
            throw new RuntimeException("No stream for writing embedded image!");
        }
        imageWriter.addIIOWriteProgressListener(new IIOWriteProgressAdapter() {
            @Override
            public void imageProgress(final ImageWriter imageWriter, final float n) {
                ImageWriter.this.processImageProgress(n);
            }
        });
        imageWriter.addIIOWriteWarningListener(new IIOWriteWarningListener() {
            @Override
            public void warningOccurred(final ImageWriter imageWriter, final int n, final String s) {
                ImageWriter.this.processWarningOccurred(n, s);
            }
        });
        imageWriter.setOutput(ImageIO.createImageOutputStream(this.embedded_stream));
        final ImageWriteParam defaultWriteParam = imageWriter.getDefaultWriteParam();
        defaultWriteParam.setDestinationOffset(imageWriteParam.getDestinationOffset());
        defaultWriteParam.setSourceBands(imageWriteParam.getSourceBands());
        defaultWriteParam.setSourceRegion(imageWriteParam.getSourceRegion());
        defaultWriteParam.setSourceSubsampling(imageWriteParam.getSourceXSubsampling(), imageWriteParam.getSourceYSubsampling(), imageWriteParam.getSubsamplingXOffset(), imageWriteParam.getSubsamplingYOffset());
        imageWriter.write(null, iioImage, defaultWriteParam);
    }
    
    private int firstLowBit(int n) {
        int n2 = 0;
        while ((n & 0x1) == 0x0) {
            ++n2;
            n >>>= 1;
        }
        return n2;
    }
    
    protected int getPreferredCompressionType(final ColorModel colorModel, final SampleModel sampleModel) {
        return this.getPreferredCompressionType(new ImageTypeSpecifier(colorModel, sampleModel));
    }
    
    protected int getPreferredCompressionType(final ImageTypeSpecifier imageTypeSpecifier) {
        if (imageTypeSpecifier.getBufferedImageType() == 8) {
            return 3;
        }
        return 0;
    }
    
    protected boolean canEncodeImage(final int n, final ColorModel colorModel, final SampleModel sampleModel) {
        return this.canEncodeImage(n, new ImageTypeSpecifier(colorModel, sampleModel));
    }
    
    protected boolean canEncodeImage(final int n, final ImageTypeSpecifier imageTypeSpecifier) {
        if (!this.getOriginatingProvider().canEncodeImage(imageTypeSpecifier)) {
            return false;
        }
        imageTypeSpecifier.getBufferedImageType();
        final int pixelSize = imageTypeSpecifier.getColorModel().getPixelSize();
        if (this.compressionType == 2 && pixelSize != 4) {
            return false;
        }
        if (this.compressionType == 1 && pixelSize != 8) {
            return false;
        }
        if (pixelSize == 16) {
            boolean b = false;
            boolean b2 = false;
            final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
            if (sampleModel instanceof SinglePixelPackedSampleModel) {
                final int[] sampleSize = ((SinglePixelPackedSampleModel)sampleModel).getSampleSize();
                b = true;
                b2 = true;
                for (int i = 0; i < sampleSize.length; ++i) {
                    b &= (sampleSize[i] == 5);
                    b2 &= (sampleSize[i] == 5 || (i == 1 && sampleSize[i] == 6));
                }
            }
            return (this.compressionType == 0 && b) || (this.compressionType == 3 && b2);
        }
        return true;
    }
    
    protected void writeMaskToPalette(final int n, final int n2, final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        array3[n2] = (byte)(0xFF & n >> 24);
        array2[n2] = (byte)(0xFF & n >> 16);
        array[n2] = (byte)(0xFF & n >> 8);
        array4[n2] = (byte)(0xFF & n);
    }
    
    private int roundBpp(final int n) {
        if (n <= 8) {
            return 8;
        }
        if (n <= 16) {
            return 16;
        }
        if (n <= 24) {
            return 24;
        }
        return 32;
    }
    
    private class IIOWriteProgressAdapter implements IIOWriteProgressListener
    {
        @Override
        public void imageComplete(final ImageWriter imageWriter) {
        }
        
        @Override
        public void imageProgress(final ImageWriter imageWriter, final float n) {
        }
        
        @Override
        public void imageStarted(final ImageWriter imageWriter, final int n) {
        }
        
        @Override
        public void thumbnailComplete(final ImageWriter imageWriter) {
        }
        
        @Override
        public void thumbnailProgress(final ImageWriter imageWriter, final float n) {
        }
        
        @Override
        public void thumbnailStarted(final ImageWriter imageWriter, final int n, final int n2) {
        }
        
        @Override
        public void writeAborted(final ImageWriter imageWriter) {
        }
    }
}
