package com.sun.imageio.plugins.gif;

import java.util.Iterator;
import javax.imageio.IIOException;
import java.awt.image.DataBufferByte;
import sun.awt.image.ByteComponentRaster;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import com.sun.imageio.plugins.common.LZWCompressor;
import org.w3c.dom.NodeList;
import java.util.Arrays;
import javax.imageio.metadata.IIOMetadataNode;
import com.sun.imageio.plugins.common.PaletteBuilder;
import java.awt.image.RenderedImage;
import javax.imageio.IIOImage;
import java.nio.ByteOrder;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import javax.imageio.ImageWriteParam;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.IOException;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;

public class GIFImageWriter extends ImageWriter
{
    private static final boolean DEBUG = false;
    static final String STANDARD_METADATA_NAME = "javax_imageio_1.0";
    static final String STREAM_METADATA_NAME = "javax_imageio_gif_stream_1.0";
    static final String IMAGE_METADATA_NAME = "javax_imageio_gif_image_1.0";
    private ImageOutputStream stream;
    private boolean isWritingSequence;
    private boolean wroteSequenceHeader;
    private GIFWritableStreamMetadata theStreamMetadata;
    private int imageIndex;
    
    private static int getNumBits(final int n) throws IOException {
        int n2 = 0;
        switch (n) {
            case 2: {
                n2 = 1;
                break;
            }
            case 4: {
                n2 = 2;
                break;
            }
            case 8: {
                n2 = 3;
                break;
            }
            case 16: {
                n2 = 4;
                break;
            }
            case 32: {
                n2 = 5;
                break;
            }
            case 64: {
                n2 = 6;
                break;
            }
            case 128: {
                n2 = 7;
                break;
            }
            case 256: {
                n2 = 8;
                break;
            }
            default: {
                throw new IOException("Bad palette length: " + n + "!");
            }
        }
        return n2;
    }
    
    private static void computeRegions(final Rectangle rectangle, final Dimension dimension, final ImageWriteParam imageWriteParam) {
        int sourceXSubsampling = 1;
        int sourceYSubsampling = 1;
        if (imageWriteParam != null) {
            final int[] sourceBands = imageWriteParam.getSourceBands();
            if (sourceBands != null && (sourceBands.length != 1 || sourceBands[0] != 0)) {
                throw new IllegalArgumentException("Cannot sub-band image!");
            }
            final Rectangle sourceRegion = imageWriteParam.getSourceRegion();
            if (sourceRegion != null) {
                rectangle.setBounds(sourceRegion.intersection(rectangle));
            }
            final int subsamplingXOffset = imageWriteParam.getSubsamplingXOffset();
            final int subsamplingYOffset = imageWriteParam.getSubsamplingYOffset();
            rectangle.x += subsamplingXOffset;
            rectangle.y += subsamplingYOffset;
            rectangle.width -= subsamplingXOffset;
            rectangle.height -= subsamplingYOffset;
            sourceXSubsampling = imageWriteParam.getSourceXSubsampling();
            sourceYSubsampling = imageWriteParam.getSourceYSubsampling();
        }
        dimension.setSize((rectangle.width + sourceXSubsampling - 1) / sourceXSubsampling, (rectangle.height + sourceYSubsampling - 1) / sourceYSubsampling);
        if (dimension.width <= 0 || dimension.height <= 0) {
            throw new IllegalArgumentException("Empty source region!");
        }
    }
    
    private static byte[] createColorTable(final ColorModel colorModel, final SampleModel sampleModel) {
        byte[] array4;
        if (colorModel instanceof IndexColorModel) {
            final IndexColorModel indexColorModel = (IndexColorModel)colorModel;
            final int mapSize = indexColorModel.getMapSize();
            final int gifPaletteSize = getGifPaletteSize(mapSize);
            final byte[] array = new byte[gifPaletteSize];
            final byte[] array2 = new byte[gifPaletteSize];
            final byte[] array3 = new byte[gifPaletteSize];
            indexColorModel.getReds(array);
            indexColorModel.getGreens(array2);
            indexColorModel.getBlues(array3);
            for (int i = mapSize; i < gifPaletteSize; ++i) {
                array[i] = array[0];
                array2[i] = array2[0];
                array3[i] = array3[0];
            }
            array4 = new byte[3 * gifPaletteSize];
            int n = 0;
            for (int j = 0; j < gifPaletteSize; ++j) {
                array4[n++] = array[j];
                array4[n++] = array2[j];
                array4[n++] = array3[j];
            }
        }
        else if (sampleModel.getNumBands() == 1) {
            int n2 = sampleModel.getSampleSize()[0];
            if (n2 > 8) {
                n2 = 8;
            }
            final int n3 = 3 * (1 << n2);
            array4 = new byte[n3];
            for (int k = 0; k < n3; ++k) {
                array4[k] = (byte)(k / 3);
            }
        }
        else {
            array4 = null;
        }
        return array4;
    }
    
    private static int getGifPaletteSize(int n) {
        if (n <= 2) {
            return 2;
        }
        --n;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }
    
    public GIFImageWriter(final GIFImageWriterSpi gifImageWriterSpi) {
        super(gifImageWriterSpi);
        this.stream = null;
        this.isWritingSequence = false;
        this.wroteSequenceHeader = false;
        this.theStreamMetadata = null;
        this.imageIndex = 0;
    }
    
    @Override
    public boolean canWriteSequence() {
        return true;
    }
    
    private void convertMetadata(final String s, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        String s2 = null;
        final String nativeMetadataFormatName = iioMetadata.getNativeMetadataFormatName();
        if (nativeMetadataFormatName != null && nativeMetadataFormatName.equals(s)) {
            s2 = s;
        }
        else {
            final String[] extraMetadataFormatNames = iioMetadata.getExtraMetadataFormatNames();
            if (extraMetadataFormatNames != null) {
                for (int i = 0; i < extraMetadataFormatNames.length; ++i) {
                    if (extraMetadataFormatNames[i].equals(s)) {
                        s2 = s;
                        break;
                    }
                }
            }
        }
        if (s2 == null && iioMetadata.isStandardMetadataFormatSupported()) {
            s2 = "javax_imageio_1.0";
        }
        if (s2 != null) {
            try {
                iioMetadata2.mergeTree(s2, iioMetadata.getAsTree(s2));
            }
            catch (final IIOInvalidTreeException ex) {}
        }
    }
    
    @Override
    public IIOMetadata convertStreamMetadata(final IIOMetadata iioMetadata, final ImageWriteParam imageWriteParam) {
        if (iioMetadata == null) {
            throw new IllegalArgumentException("inData == null!");
        }
        final IIOMetadata defaultStreamMetadata = this.getDefaultStreamMetadata(imageWriteParam);
        this.convertMetadata("javax_imageio_gif_stream_1.0", iioMetadata, defaultStreamMetadata);
        return defaultStreamMetadata;
    }
    
    @Override
    public IIOMetadata convertImageMetadata(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        if (iioMetadata == null) {
            throw new IllegalArgumentException("inData == null!");
        }
        if (imageTypeSpecifier == null) {
            throw new IllegalArgumentException("imageType == null!");
        }
        final GIFWritableImageMetadata gifWritableImageMetadata = (GIFWritableImageMetadata)this.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
        final boolean interlaceFlag = gifWritableImageMetadata.interlaceFlag;
        this.convertMetadata("javax_imageio_gif_image_1.0", iioMetadata, gifWritableImageMetadata);
        if (imageWriteParam != null && imageWriteParam.canWriteProgressive() && imageWriteParam.getProgressiveMode() != 3) {
            gifWritableImageMetadata.interlaceFlag = interlaceFlag;
        }
        return gifWritableImageMetadata;
    }
    
    @Override
    public void endWriteSequence() throws IOException {
        if (this.stream == null) {
            throw new IllegalStateException("output == null!");
        }
        if (!this.isWritingSequence) {
            throw new IllegalStateException("prepareWriteSequence() was not invoked!");
        }
        this.writeTrailer();
        this.resetLocal();
    }
    
    @Override
    public IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        final GIFWritableImageMetadata gifWritableImageMetadata = new GIFWritableImageMetadata();
        final SampleModel sampleModel = imageTypeSpecifier.getSampleModel();
        final Rectangle rectangle = new Rectangle(sampleModel.getWidth(), sampleModel.getHeight());
        final Dimension dimension = new Dimension();
        computeRegions(rectangle, dimension, imageWriteParam);
        gifWritableImageMetadata.imageWidth = dimension.width;
        gifWritableImageMetadata.imageHeight = dimension.height;
        if (imageWriteParam != null && imageWriteParam.canWriteProgressive() && imageWriteParam.getProgressiveMode() == 0) {
            gifWritableImageMetadata.interlaceFlag = false;
        }
        else {
            gifWritableImageMetadata.interlaceFlag = true;
        }
        final ColorModel colorModel = imageTypeSpecifier.getColorModel();
        gifWritableImageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
        if (colorModel instanceof IndexColorModel) {
            final int transparentPixel = ((IndexColorModel)colorModel).getTransparentPixel();
            if (transparentPixel != -1) {
                gifWritableImageMetadata.transparentColorFlag = true;
                gifWritableImageMetadata.transparentColorIndex = transparentPixel;
            }
        }
        return gifWritableImageMetadata;
    }
    
    @Override
    public IIOMetadata getDefaultStreamMetadata(final ImageWriteParam imageWriteParam) {
        final GIFWritableStreamMetadata gifWritableStreamMetadata = new GIFWritableStreamMetadata();
        gifWritableStreamMetadata.version = "89a";
        return gifWritableStreamMetadata;
    }
    
    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new GIFImageWriteParam(this.getLocale());
    }
    
    @Override
    public void prepareWriteSequence(final IIOMetadata iioMetadata) throws IOException {
        if (this.stream == null) {
            throw new IllegalStateException("Output is not set.");
        }
        this.resetLocal();
        if (iioMetadata == null) {
            this.theStreamMetadata = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata(null);
        }
        else {
            this.convertMetadata("javax_imageio_gif_stream_1.0", iioMetadata, this.theStreamMetadata = new GIFWritableStreamMetadata());
        }
        this.isWritingSequence = true;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.resetLocal();
    }
    
    private void resetLocal() {
        this.isWritingSequence = false;
        this.wroteSequenceHeader = false;
        this.theStreamMetadata = null;
        this.imageIndex = 0;
    }
    
    @Override
    public void setOutput(final Object output) {
        super.setOutput(output);
        if (output != null) {
            if (!(output instanceof ImageOutputStream)) {
                throw new IllegalArgumentException("output is not an ImageOutputStream");
            }
            (this.stream = (ImageOutputStream)output).setByteOrder(ByteOrder.LITTLE_ENDIAN);
        }
        else {
            this.stream = null;
        }
    }
    
    @Override
    public void write(final IIOMetadata iioMetadata, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        if (this.stream == null) {
            throw new IllegalStateException("output == null!");
        }
        if (iioImage == null) {
            throw new IllegalArgumentException("iioimage == null!");
        }
        if (iioImage.hasRaster()) {
            throw new UnsupportedOperationException("canWriteRasters() == false!");
        }
        this.resetLocal();
        GIFWritableStreamMetadata gifWritableStreamMetadata;
        if (iioMetadata == null) {
            gifWritableStreamMetadata = (GIFWritableStreamMetadata)this.getDefaultStreamMetadata(imageWriteParam);
        }
        else {
            gifWritableStreamMetadata = (GIFWritableStreamMetadata)this.convertStreamMetadata(iioMetadata, imageWriteParam);
        }
        this.write(true, true, gifWritableStreamMetadata, iioImage, imageWriteParam);
    }
    
    @Override
    public void writeToSequence(final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        if (this.stream == null) {
            throw new IllegalStateException("output == null!");
        }
        if (iioImage == null) {
            throw new IllegalArgumentException("image == null!");
        }
        if (iioImage.hasRaster()) {
            throw new UnsupportedOperationException("canWriteRasters() == false!");
        }
        if (!this.isWritingSequence) {
            throw new IllegalStateException("prepareWriteSequence() was not invoked!");
        }
        this.write(!this.wroteSequenceHeader, false, this.theStreamMetadata, iioImage, imageWriteParam);
        if (!this.wroteSequenceHeader) {
            this.wroteSequenceHeader = true;
        }
        ++this.imageIndex;
    }
    
    private boolean needToCreateIndex(final RenderedImage renderedImage) {
        final SampleModel sampleModel = renderedImage.getSampleModel();
        final ColorModel colorModel = renderedImage.getColorModel();
        return sampleModel.getNumBands() != 1 || sampleModel.getSampleSize()[0] > 8 || colorModel.getComponentSize()[0] > 8;
    }
    
    private void write(final boolean b, final boolean b2, final IIOMetadata iioMetadata, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.clearAbortRequest();
        RenderedImage renderedImage = iioImage.getRenderedImage();
        if (this.needToCreateIndex(renderedImage)) {
            renderedImage = PaletteBuilder.createIndexedImage(renderedImage);
            iioImage.setRenderedImage(renderedImage);
        }
        final ColorModel colorModel = renderedImage.getColorModel();
        final SampleModel sampleModel = renderedImage.getSampleModel();
        final Rectangle rectangle = new Rectangle(renderedImage.getMinX(), renderedImage.getMinY(), renderedImage.getWidth(), renderedImage.getHeight());
        final Dimension dimension = new Dimension();
        computeRegions(rectangle, dimension, imageWriteParam);
        GIFWritableImageMetadata gifWritableImageMetadata = null;
        if (iioImage.getMetadata() != null) {
            gifWritableImageMetadata = new GIFWritableImageMetadata();
            this.convertMetadata("javax_imageio_gif_image_1.0", iioImage.getMetadata(), gifWritableImageMetadata);
            if (gifWritableImageMetadata.localColorTable == null) {
                gifWritableImageMetadata.localColorTable = createColorTable(colorModel, sampleModel);
                if (colorModel instanceof IndexColorModel) {
                    final int transparentPixel = ((IndexColorModel)colorModel).getTransparentPixel();
                    gifWritableImageMetadata.transparentColorFlag = (transparentPixel != -1);
                    if (gifWritableImageMetadata.transparentColorFlag) {
                        gifWritableImageMetadata.transparentColorIndex = transparentPixel;
                    }
                }
            }
        }
        byte[] array;
        if (b) {
            if (iioMetadata == null) {
                throw new IllegalArgumentException("Cannot write null header!");
            }
            final GIFWritableStreamMetadata gifWritableStreamMetadata = (GIFWritableStreamMetadata)iioMetadata;
            if (gifWritableStreamMetadata.version == null) {
                gifWritableStreamMetadata.version = "89a";
            }
            if (gifWritableStreamMetadata.logicalScreenWidth == -1) {
                gifWritableStreamMetadata.logicalScreenWidth = dimension.width;
            }
            if (gifWritableStreamMetadata.logicalScreenHeight == -1) {
                gifWritableStreamMetadata.logicalScreenHeight = dimension.height;
            }
            if (gifWritableStreamMetadata.colorResolution == -1) {
                gifWritableStreamMetadata.colorResolution = ((colorModel != null) ? colorModel.getComponentSize()[0] : sampleModel.getSampleSize()[0]);
            }
            if (gifWritableStreamMetadata.globalColorTable == null) {
                if (this.isWritingSequence && gifWritableImageMetadata != null && gifWritableImageMetadata.localColorTable != null) {
                    gifWritableStreamMetadata.globalColorTable = gifWritableImageMetadata.localColorTable;
                }
                else if (gifWritableImageMetadata == null || gifWritableImageMetadata.localColorTable == null) {
                    gifWritableStreamMetadata.globalColorTable = createColorTable(colorModel, sampleModel);
                }
            }
            array = gifWritableStreamMetadata.globalColorTable;
            int n;
            if (array != null) {
                n = getNumBits(array.length / 3);
            }
            else if (gifWritableImageMetadata != null && gifWritableImageMetadata.localColorTable != null) {
                n = getNumBits(gifWritableImageMetadata.localColorTable.length / 3);
            }
            else {
                n = sampleModel.getSampleSize(0);
            }
            this.writeHeader(gifWritableStreamMetadata, n);
        }
        else {
            if (!this.isWritingSequence) {
                throw new IllegalArgumentException("Must write header for single image!");
            }
            array = this.theStreamMetadata.globalColorTable;
        }
        this.writeImage(iioImage.getRenderedImage(), gifWritableImageMetadata, imageWriteParam, array, rectangle, dimension);
        if (b2) {
            this.writeTrailer();
        }
    }
    
    private void writeImage(final RenderedImage renderedImage, GIFWritableImageMetadata gifWritableImageMetadata, final ImageWriteParam imageWriteParam, final byte[] array, final Rectangle rectangle, final Dimension dimension) throws IOException {
        renderedImage.getColorModel();
        final SampleModel sampleModel = renderedImage.getSampleModel();
        boolean transparentColorFlag;
        if (gifWritableImageMetadata == null) {
            gifWritableImageMetadata = (GIFWritableImageMetadata)this.getDefaultImageMetadata(new ImageTypeSpecifier(renderedImage), imageWriteParam);
            transparentColorFlag = gifWritableImageMetadata.transparentColorFlag;
        }
        else {
            NodeList elementsByTagName = null;
            try {
                elementsByTagName = ((IIOMetadataNode)gifWritableImageMetadata.getAsTree("javax_imageio_gif_image_1.0")).getElementsByTagName("GraphicControlExtension");
            }
            catch (final IllegalArgumentException ex) {}
            transparentColorFlag = (elementsByTagName != null && elementsByTagName.getLength() > 0);
            if (imageWriteParam != null && imageWriteParam.canWriteProgressive()) {
                if (imageWriteParam.getProgressiveMode() == 0) {
                    gifWritableImageMetadata.interlaceFlag = false;
                }
                else if (imageWriteParam.getProgressiveMode() == 1) {
                    gifWritableImageMetadata.interlaceFlag = true;
                }
            }
        }
        if (Arrays.equals(array, gifWritableImageMetadata.localColorTable)) {
            gifWritableImageMetadata.localColorTable = null;
        }
        gifWritableImageMetadata.imageWidth = dimension.width;
        gifWritableImageMetadata.imageHeight = dimension.height;
        if (transparentColorFlag) {
            this.writeGraphicControlExtension(gifWritableImageMetadata);
        }
        this.writePlainTextExtension(gifWritableImageMetadata);
        this.writeApplicationExtension(gifWritableImageMetadata);
        this.writeCommentExtension(gifWritableImageMetadata);
        this.writeImageDescriptor(gifWritableImageMetadata, getNumBits((gifWritableImageMetadata.localColorTable == null) ? ((array == null) ? sampleModel.getSampleSize(0) : (array.length / 3)) : (gifWritableImageMetadata.localColorTable.length / 3)));
        this.writeRasterData(renderedImage, rectangle, dimension, imageWriteParam, gifWritableImageMetadata.interlaceFlag);
    }
    
    private void writeRows(final RenderedImage renderedImage, final LZWCompressor lzwCompressor, final int n, final int n2, int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9, int n10, final int n11) throws IOException {
        final int[] array = new int[n5];
        final byte[] array2 = new byte[n8];
        final Raster raster = (renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1) ? renderedImage.getTile(0, 0) : renderedImage.getData();
        for (int i = n6; i < n9; i += n7) {
            if (n10 % n11 == 0) {
                if (this.abortRequested()) {
                    this.processWriteAborted();
                    return;
                }
                this.processImageProgress(n10 * 100.0f / n9);
            }
            raster.getSamples(n, n3, n5, 1, 0, array);
            for (int j = 0, n12 = 0; j < n8; ++j, n12 += n2) {
                array2[j] = (byte)array[n12];
            }
            lzwCompressor.compress(array2, 0, n8);
            ++n10;
            n3 += n4;
        }
    }
    
    private void writeRowsOpt(final byte[] array, int n, int n2, final LZWCompressor lzwCompressor, final int n3, final int n4, final int n5, final int n6, int n7, final int n8) throws IOException {
        n += n3 * n2;
        n2 *= n4;
        for (int i = n3; i < n6; i += n4) {
            if (n7 % n8 == 0) {
                if (this.abortRequested()) {
                    this.processWriteAborted();
                    return;
                }
                this.processImageProgress(n7 * 100.0f / n6);
            }
            lzwCompressor.compress(array, n, n5);
            ++n7;
            n += n2;
        }
    }
    
    private void writeRasterData(final RenderedImage renderedImage, final Rectangle rectangle, final Dimension dimension, final ImageWriteParam imageWriteParam, final boolean b) throws IOException {
        final int x = rectangle.x;
        final int y = rectangle.y;
        final int width = rectangle.width;
        final int height = rectangle.height;
        final int width2 = dimension.width;
        final int height2 = dimension.height;
        int sourceXSubsampling;
        int sourceYSubsampling;
        if (imageWriteParam == null) {
            sourceXSubsampling = 1;
            sourceYSubsampling = 1;
        }
        else {
            sourceXSubsampling = imageWriteParam.getSourceXSubsampling();
            sourceYSubsampling = imageWriteParam.getSourceYSubsampling();
        }
        final SampleModel sampleModel = renderedImage.getSampleModel();
        int n = sampleModel.getSampleSize()[0];
        if (n == 1) {
            ++n;
        }
        this.stream.write(n);
        final LZWCompressor lzwCompressor = new LZWCompressor(this.stream, n, false);
        final boolean b2 = sourceXSubsampling == 1 && sourceYSubsampling == 1 && renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1 && sampleModel instanceof ComponentSampleModel && renderedImage.getTile(0, 0) instanceof ByteComponentRaster && renderedImage.getTile(0, 0).getDataBuffer() instanceof DataBufferByte;
        final int n2 = 0;
        final int max = Math.max(height2 / 20, 1);
        this.processImageStarted(this.imageIndex);
        if (b) {
            if (b2) {
                final ByteComponentRaster byteComponentRaster = (ByteComponentRaster)renderedImage.getTile(0, 0);
                final byte[] data = ((DataBufferByte)byteComponentRaster.getDataBuffer()).getData();
                final ComponentSampleModel componentSampleModel = (ComponentSampleModel)byteComponentRaster.getSampleModel();
                final int n3 = componentSampleModel.getOffset(x, y, 0) + byteComponentRaster.getDataOffset(0);
                final int scanlineStride = componentSampleModel.getScanlineStride();
                this.writeRowsOpt(data, n3, scanlineStride, lzwCompressor, 0, 8, width2, height2, n2, max);
                if (this.abortRequested()) {
                    return;
                }
                final int n4 = n2 + height2 / 8;
                this.writeRowsOpt(data, n3, scanlineStride, lzwCompressor, 4, 8, width2, height2, n4, max);
                if (this.abortRequested()) {
                    return;
                }
                final int n5 = n4 + (height2 - 4) / 8;
                this.writeRowsOpt(data, n3, scanlineStride, lzwCompressor, 2, 4, width2, height2, n5, max);
                if (this.abortRequested()) {
                    return;
                }
                this.writeRowsOpt(data, n3, scanlineStride, lzwCompressor, 1, 2, width2, height2, n5 + (height2 - 2) / 4, max);
            }
            else {
                this.writeRows(renderedImage, lzwCompressor, x, sourceXSubsampling, y, 8 * sourceYSubsampling, width, 0, 8, width2, height2, n2, max);
                if (this.abortRequested()) {
                    return;
                }
                final int n6 = n2 + height2 / 8;
                this.writeRows(renderedImage, lzwCompressor, x, sourceXSubsampling, y + 4 * sourceYSubsampling, 8 * sourceYSubsampling, width, 4, 8, width2, height2, n6, max);
                if (this.abortRequested()) {
                    return;
                }
                final int n7 = n6 + (height2 - 4) / 8;
                this.writeRows(renderedImage, lzwCompressor, x, sourceXSubsampling, y + 2 * sourceYSubsampling, 4 * sourceYSubsampling, width, 2, 4, width2, height2, n7, max);
                if (this.abortRequested()) {
                    return;
                }
                this.writeRows(renderedImage, lzwCompressor, x, sourceXSubsampling, y + sourceYSubsampling, 2 * sourceYSubsampling, width, 1, 2, width2, height2, n7 + (height2 - 2) / 4, max);
            }
        }
        else if (b2) {
            final Raster tile = renderedImage.getTile(0, 0);
            final byte[] data2 = ((DataBufferByte)tile.getDataBuffer()).getData();
            final ComponentSampleModel componentSampleModel2 = (ComponentSampleModel)tile.getSampleModel();
            this.writeRowsOpt(data2, componentSampleModel2.getOffset(x, y, 0), componentSampleModel2.getScanlineStride(), lzwCompressor, 0, 1, width2, height2, n2, max);
        }
        else {
            this.writeRows(renderedImage, lzwCompressor, x, sourceXSubsampling, y, sourceYSubsampling, width, 0, 1, width2, height2, n2, max);
        }
        if (this.abortRequested()) {
            return;
        }
        this.processImageProgress(100.0f);
        lzwCompressor.flush();
        this.stream.write(0);
        this.processImageComplete();
    }
    
    private void writeHeader(final String s, final int n, final int n2, final int n3, final int n4, final int n5, final boolean b, final int n6, final byte[] array) throws IOException {
        try {
            this.stream.writeBytes("GIF" + s);
            this.stream.writeShort((short)n);
            this.stream.writeShort((short)n2);
            int n7 = ((array != null) ? 128 : 0) | (n3 - 1 & 0x7) << 4;
            if (b) {
                n7 |= 0x8;
            }
            this.stream.write(n7 | n6 - 1);
            this.stream.write(n5);
            this.stream.write(n4);
            if (array != null) {
                this.stream.write(array);
            }
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error writing header!", ex);
        }
    }
    
    private void writeHeader(final IIOMetadata iioMetadata, final int n) throws IOException {
        GIFWritableStreamMetadata gifWritableStreamMetadata;
        if (iioMetadata instanceof GIFWritableStreamMetadata) {
            gifWritableStreamMetadata = (GIFWritableStreamMetadata)iioMetadata;
        }
        else {
            gifWritableStreamMetadata = new GIFWritableStreamMetadata();
            gifWritableStreamMetadata.setFromTree("javax_imageio_gif_stream_1.0", iioMetadata.getAsTree("javax_imageio_gif_stream_1.0"));
        }
        this.writeHeader(gifWritableStreamMetadata.version, gifWritableStreamMetadata.logicalScreenWidth, gifWritableStreamMetadata.logicalScreenHeight, gifWritableStreamMetadata.colorResolution, gifWritableStreamMetadata.pixelAspectRatio, gifWritableStreamMetadata.backgroundColorIndex, gifWritableStreamMetadata.sortFlag, n, gifWritableStreamMetadata.globalColorTable);
    }
    
    private void writeGraphicControlExtension(final int n, final boolean b, final boolean b2, final int n2, final int n3) throws IOException {
        try {
            this.stream.write(33);
            this.stream.write(249);
            this.stream.write(4);
            int n4 = (n & 0x3) << 2;
            if (b) {
                n4 |= 0x2;
            }
            if (b2) {
                n4 |= 0x1;
            }
            this.stream.write(n4);
            this.stream.writeShort((short)n2);
            this.stream.write(n3);
            this.stream.write(0);
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error writing Graphic Control Extension!", ex);
        }
    }
    
    private void writeGraphicControlExtension(final GIFWritableImageMetadata gifWritableImageMetadata) throws IOException {
        this.writeGraphicControlExtension(gifWritableImageMetadata.disposalMethod, gifWritableImageMetadata.userInputFlag, gifWritableImageMetadata.transparentColorFlag, gifWritableImageMetadata.delayTime, gifWritableImageMetadata.transparentColorIndex);
    }
    
    private void writeBlocks(final byte[] array) throws IOException {
        if (array != null && array.length > 0) {
            int min;
            for (int i = 0; i < array.length; i += min) {
                min = Math.min(array.length - i, 255);
                this.stream.write(min);
                this.stream.write(array, i, min);
            }
        }
    }
    
    private void writePlainTextExtension(final GIFWritableImageMetadata gifWritableImageMetadata) throws IOException {
        if (gifWritableImageMetadata.hasPlainTextExtension) {
            try {
                this.stream.write(33);
                this.stream.write(1);
                this.stream.write(12);
                this.stream.writeShort(gifWritableImageMetadata.textGridLeft);
                this.stream.writeShort(gifWritableImageMetadata.textGridTop);
                this.stream.writeShort(gifWritableImageMetadata.textGridWidth);
                this.stream.writeShort(gifWritableImageMetadata.textGridHeight);
                this.stream.write(gifWritableImageMetadata.characterCellWidth);
                this.stream.write(gifWritableImageMetadata.characterCellHeight);
                this.stream.write(gifWritableImageMetadata.textForegroundColor);
                this.stream.write(gifWritableImageMetadata.textBackgroundColor);
                this.writeBlocks(gifWritableImageMetadata.text);
                this.stream.write(0);
            }
            catch (final IOException ex) {
                throw new IIOException("I/O error writing Plain Text Extension!", ex);
            }
        }
    }
    
    private void writeApplicationExtension(final GIFWritableImageMetadata gifWritableImageMetadata) throws IOException {
        if (gifWritableImageMetadata.applicationIDs != null) {
            final Iterator iterator = gifWritableImageMetadata.applicationIDs.iterator();
            final Iterator iterator2 = gifWritableImageMetadata.authenticationCodes.iterator();
            final Iterator iterator3 = gifWritableImageMetadata.applicationData.iterator();
            while (iterator.hasNext()) {
                try {
                    this.stream.write(33);
                    this.stream.write(255);
                    this.stream.write(11);
                    this.stream.write((byte[])iterator.next(), 0, 8);
                    this.stream.write((byte[])iterator2.next(), 0, 3);
                    this.writeBlocks((byte[])iterator3.next());
                    this.stream.write(0);
                    continue;
                }
                catch (final IOException ex) {
                    throw new IIOException("I/O error writing Application Extension!", ex);
                }
                break;
            }
        }
    }
    
    private void writeCommentExtension(final GIFWritableImageMetadata gifWritableImageMetadata) throws IOException {
        if (gifWritableImageMetadata.comments != null) {
            try {
                final Iterator iterator = gifWritableImageMetadata.comments.iterator();
                while (iterator.hasNext()) {
                    this.stream.write(33);
                    this.stream.write(254);
                    this.writeBlocks((byte[])iterator.next());
                    this.stream.write(0);
                }
            }
            catch (final IOException ex) {
                throw new IIOException("I/O error writing Comment Extension!", ex);
            }
        }
    }
    
    private void writeImageDescriptor(final int n, final int n2, final int n3, final int n4, final boolean b, final boolean b2, final int n5, final byte[] array) throws IOException {
        try {
            this.stream.write(44);
            this.stream.writeShort((short)n);
            this.stream.writeShort((short)n2);
            this.stream.writeShort((short)n3);
            this.stream.writeShort((short)n4);
            int n6 = (array != null) ? 128 : 0;
            if (b) {
                n6 |= 0x40;
            }
            if (b2) {
                n6 |= 0x8;
            }
            this.stream.write(n6 | n5 - 1);
            if (array != null) {
                this.stream.write(array);
            }
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error writing Image Descriptor!", ex);
        }
    }
    
    private void writeImageDescriptor(final GIFWritableImageMetadata gifWritableImageMetadata, final int n) throws IOException {
        this.writeImageDescriptor(gifWritableImageMetadata.imageLeftPosition, gifWritableImageMetadata.imageTopPosition, gifWritableImageMetadata.imageWidth, gifWritableImageMetadata.imageHeight, gifWritableImageMetadata.interlaceFlag, gifWritableImageMetadata.sortFlag, n, gifWritableImageMetadata.localColorTable);
    }
    
    private void writeTrailer() throws IOException {
        this.stream.write(59);
    }
}
