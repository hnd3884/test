package com.sun.imageio.plugins.wbmp;

import java.io.IOException;
import java.awt.image.WritableRaster;
import java.awt.image.SampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.Point;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.Rectangle;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageWriteParam;
import com.sun.imageio.plugins.common.I18N;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;

public class WBMPImageWriter extends ImageWriter
{
    private ImageOutputStream stream;
    
    private static int getNumBits(final int n) {
        int n2 = 32;
        for (int n3 = Integer.MIN_VALUE; n3 != 0 && (n & n3) == 0x0; n3 >>>= 1) {
            --n2;
        }
        return n2;
    }
    
    private static byte[] intToMultiByte(final int n) {
        final byte[] array = new byte[(getNumBits(n) + 6) / 7];
        for (int n2 = array.length - 1, i = 0; i <= n2; ++i) {
            array[i] = (byte)(n >>> (n2 - i) * 7 & 0x7F);
            if (i != n2) {
                final byte[] array2 = array;
                final int n3 = i;
                array2[n3] |= 0xFFFFFF80;
            }
        }
        return array;
    }
    
    public WBMPImageWriter(final ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
        this.stream = null;
    }
    
    @Override
    public void setOutput(final Object output) {
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException(I18N.getString("WBMPImageWriter"));
            }
            this.stream = (ImageOutputStream)output;
        }
        else {
            this.stream = null;
        }
    }
    
    @Override
    public IIOMetadata getDefaultStreamMetadata(final ImageWriteParam imageWriteParam) {
        return null;
    }
    
    @Override
    public IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        final WBMPMetadata wbmpMetadata = new WBMPMetadata();
        wbmpMetadata.wbmpType = 0;
        return wbmpMetadata;
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
            throw new IllegalStateException(I18N.getString("WBMPImageWriter3"));
        }
        if (iioImage == null) {
            throw new IllegalArgumentException(I18N.getString("WBMPImageWriter4"));
        }
        this.clearAbortRequest();
        this.processImageStarted(0);
        if (defaultWriteParam == null) {
            defaultWriteParam = this.getDefaultWriteParam();
        }
        RenderedImage renderedImage = null;
        final boolean hasRaster = iioImage.hasRaster();
        final Rectangle sourceRegion = defaultWriteParam.getSourceRegion();
        Raster rect;
        SampleModel sampleModel;
        if (hasRaster) {
            rect = iioImage.getRaster();
            sampleModel = rect.getSampleModel();
        }
        else {
            renderedImage = iioImage.getRenderedImage();
            sampleModel = renderedImage.getSampleModel();
            rect = renderedImage.getData();
        }
        this.checkSampleModel(sampleModel);
        Rectangle rectangle;
        if (sourceRegion == null) {
            rectangle = rect.getBounds();
        }
        else {
            rectangle = sourceRegion.intersection(rect.getBounds());
        }
        if (rectangle.isEmpty()) {
            throw new RuntimeException(I18N.getString("WBMPImageWriter1"));
        }
        final int sourceXSubsampling = defaultWriteParam.getSourceXSubsampling();
        final int sourceYSubsampling = defaultWriteParam.getSourceYSubsampling();
        final int subsamplingXOffset = defaultWriteParam.getSubsamplingXOffset();
        final int subsamplingYOffset = defaultWriteParam.getSubsamplingYOffset();
        rectangle.translate(subsamplingXOffset, subsamplingYOffset);
        final Rectangle rectangle2 = rectangle;
        rectangle2.width -= subsamplingXOffset;
        final Rectangle rectangle3 = rectangle;
        rectangle3.height -= subsamplingYOffset;
        final int n = rectangle.x / sourceXSubsampling;
        final int n2 = rectangle.y / sourceYSubsampling;
        final int n3 = (rectangle.width + sourceXSubsampling - 1) / sourceXSubsampling;
        final int n4 = (rectangle.height + sourceYSubsampling - 1) / sourceYSubsampling;
        final Rectangle rectangle4 = new Rectangle(n, n2, n3, n4);
        SampleModel compatibleSampleModel;
        final SampleModel sampleModel2 = compatibleSampleModel = sampleModel.createCompatibleSampleModel(n3, n4);
        if (sampleModel2.getDataType() != 0 || !(sampleModel2 instanceof MultiPixelPackedSampleModel) || ((MultiPixelPackedSampleModel)sampleModel2).getDataBitOffset() != 0) {
            compatibleSampleModel = new MultiPixelPackedSampleModel(0, n3, n4, 1, n3 + 7 >> 3, 0);
        }
        if (!rectangle4.equals(rectangle)) {
            if (sourceXSubsampling == 1 && sourceYSubsampling == 1) {
                rect = rect.createChild(rect.getMinX(), rect.getMinY(), n3, n4, n, n2, null);
            }
            else {
                final WritableRaster writableRaster = Raster.createWritableRaster(compatibleSampleModel, new Point(n, n2));
                final byte[] data = ((DataBufferByte)writableRaster.getDataBuffer()).getData();
                int i = n2;
                int y = rectangle.y;
                int n5 = 0;
                while (i < n2 + n4) {
                    for (int j = 0, x = rectangle.x; j < n3; ++j, x += sourceXSubsampling) {
                        final int sample = rect.getSample(x, y, 0);
                        final byte[] array = data;
                        final int n6 = n5 + (j >> 3);
                        array[n6] |= (byte)(sample << 7 - (j & 0x7));
                    }
                    n5 += n3 + 7 >> 3;
                    ++i;
                    y += sourceYSubsampling;
                }
                rect = writableRaster;
            }
        }
        if (!compatibleSampleModel.equals(rect.getSampleModel())) {
            final WritableRaster writableRaster2 = Raster.createWritableRaster(compatibleSampleModel, new Point(rect.getMinX(), rect.getMinY()));
            writableRaster2.setRect(rect);
            rect = writableRaster2;
        }
        boolean b = false;
        if (!hasRaster && renderedImage.getColorModel() instanceof IndexColorModel) {
            final IndexColorModel indexColorModel = (IndexColorModel)renderedImage.getColorModel();
            b = (indexColorModel.getRed(0) > indexColorModel.getRed(1));
        }
        final int scanlineStride = ((MultiPixelPackedSampleModel)compatibleSampleModel).getScanlineStride();
        final int n7 = (n3 + 7) / 8;
        final byte[] data2 = ((DataBufferByte)rect.getDataBuffer()).getData();
        this.stream.write(0);
        this.stream.write(0);
        this.stream.write(intToMultiByte(n3));
        this.stream.write(intToMultiByte(n4));
        if (!b && scanlineStride == n7) {
            this.stream.write(data2, 0, n4 * n7);
            this.processImageProgress(100.0f);
        }
        else {
            int n8 = 0;
            if (!b) {
                for (int n9 = 0; n9 < n4 && !this.abortRequested(); ++n9) {
                    this.stream.write(data2, n8, n7);
                    n8 += scanlineStride;
                    this.processImageProgress(100.0f * n9 / n4);
                }
            }
            else {
                final byte[] array2 = new byte[n7];
                for (int k = 0; k < n4; ++k) {
                    if (this.abortRequested()) {
                        break;
                    }
                    for (int l = 0; l < n7; ++l) {
                        array2[l] = (byte)~data2[l + n8];
                    }
                    this.stream.write(array2, 0, n7);
                    n8 += scanlineStride;
                    this.processImageProgress(100.0f * k / n4);
                }
            }
        }
        if (this.abortRequested()) {
            this.processWriteAborted();
        }
        else {
            this.processImageComplete();
            this.stream.flushBefore(this.stream.getStreamPosition());
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.stream = null;
    }
    
    private void checkSampleModel(final SampleModel sampleModel) {
        final int dataType = sampleModel.getDataType();
        if (dataType < 0 || dataType > 3 || sampleModel.getNumBands() != 1 || sampleModel.getSampleSize(0) != 1) {
            throw new IllegalArgumentException(I18N.getString("WBMPImageWriter2"));
        }
    }
}
