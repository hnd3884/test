package com.sun.imageio.plugins.jpeg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.awt.color.ColorSpace;
import java.awt.image.RenderedImage;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.color.ICC_ColorSpace;
import java.awt.Rectangle;
import javax.imageio.IIOException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.IIOImage;
import org.w3c.dom.Node;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import java.util.Locale;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.ImageWriteParam;
import sun.java2d.Disposer;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.Dimension;
import sun.java2d.DisposerRecord;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import java.awt.image.ColorConvertOp;
import java.awt.color.ICC_Profile;
import java.util.List;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;

public class JPEGImageWriter extends ImageWriter
{
    private boolean debug;
    private long structPointer;
    private ImageOutputStream ios;
    private Raster srcRas;
    private WritableRaster raster;
    private boolean indexed;
    private IndexColorModel indexCM;
    private boolean convertTosRGB;
    private WritableRaster converted;
    private boolean isAlphaPremultiplied;
    private ColorModel srcCM;
    private List thumbnails;
    private ICC_Profile iccProfile;
    private int sourceXOffset;
    private int sourceYOffset;
    private int sourceWidth;
    private int[] srcBands;
    private int sourceHeight;
    private int currentImage;
    private ColorConvertOp convertOp;
    private JPEGQTable[] streamQTables;
    private JPEGHuffmanTable[] streamDCHuffmanTables;
    private JPEGHuffmanTable[] streamACHuffmanTables;
    private boolean ignoreJFIF;
    private boolean forceJFIF;
    private boolean ignoreAdobe;
    private int newAdobeTransform;
    private boolean writeDefaultJFIF;
    private boolean writeAdobe;
    private JPEGMetadata metadata;
    private boolean sequencePrepared;
    private int numScans;
    private Object disposerReferent;
    private DisposerRecord disposerRecord;
    protected static final int WARNING_DEST_IGNORED = 0;
    protected static final int WARNING_STREAM_METADATA_IGNORED = 1;
    protected static final int WARNING_DEST_METADATA_COMP_MISMATCH = 2;
    protected static final int WARNING_DEST_METADATA_JFIF_MISMATCH = 3;
    protected static final int WARNING_DEST_METADATA_ADOBE_MISMATCH = 4;
    protected static final int WARNING_IMAGE_METADATA_JFIF_MISMATCH = 5;
    protected static final int WARNING_IMAGE_METADATA_ADOBE_MISMATCH = 6;
    protected static final int WARNING_METADATA_NOT_JPEG_FOR_RASTER = 7;
    protected static final int WARNING_NO_BANDS_ON_INDEXED = 8;
    protected static final int WARNING_ILLEGAL_THUMBNAIL = 9;
    protected static final int WARNING_IGNORING_THUMBS = 10;
    protected static final int WARNING_FORCING_JFIF = 11;
    protected static final int WARNING_THUMB_CLIPPED = 12;
    protected static final int WARNING_METADATA_ADJUSTED_FOR_THUMB = 13;
    protected static final int WARNING_NO_RGB_THUMB_AS_INDEXED = 14;
    protected static final int WARNING_NO_GRAY_THUMB_AS_INDEXED = 15;
    private static final int MAX_WARNING = 15;
    static final Dimension[] preferredThumbSizes;
    private Thread theThread;
    private int theLockCount;
    private CallBackLock cbLock;
    
    public JPEGImageWriter(final ImageWriterSpi imageWriterSpi) {
        super(imageWriterSpi);
        this.debug = false;
        this.structPointer = 0L;
        this.ios = null;
        this.srcRas = null;
        this.raster = null;
        this.indexed = false;
        this.indexCM = null;
        this.convertTosRGB = false;
        this.converted = null;
        this.isAlphaPremultiplied = false;
        this.srcCM = null;
        this.thumbnails = null;
        this.iccProfile = null;
        this.sourceXOffset = 0;
        this.sourceYOffset = 0;
        this.sourceWidth = 0;
        this.srcBands = null;
        this.sourceHeight = 0;
        this.currentImage = 0;
        this.convertOp = null;
        this.streamQTables = null;
        this.streamDCHuffmanTables = null;
        this.streamACHuffmanTables = null;
        this.ignoreJFIF = false;
        this.forceJFIF = false;
        this.ignoreAdobe = false;
        this.newAdobeTransform = -1;
        this.writeDefaultJFIF = false;
        this.writeAdobe = false;
        this.metadata = null;
        this.sequencePrepared = false;
        this.numScans = 0;
        this.disposerReferent = new Object();
        this.theThread = null;
        this.theLockCount = 0;
        this.cbLock = new CallBackLock();
        this.structPointer = this.initJPEGImageWriter();
        this.disposerRecord = new JPEGWriterDisposerRecord(this.structPointer);
        Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    }
    
    @Override
    public void setOutput(final Object output) {
        this.setThreadLock();
        try {
            this.cbLock.check();
            super.setOutput(output);
            this.resetInternalState();
            this.ios = (ImageOutputStream)output;
            this.setDest(this.structPointer);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public ImageWriteParam getDefaultWriteParam() {
        return new JPEGImageWriteParam(null);
    }
    
    @Override
    public IIOMetadata getDefaultStreamMetadata(final ImageWriteParam imageWriteParam) {
        this.setThreadLock();
        try {
            return new JPEGMetadata(imageWriteParam, this);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public IIOMetadata getDefaultImageMetadata(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        this.setThreadLock();
        try {
            return new JPEGMetadata(imageTypeSpecifier, imageWriteParam, this);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public IIOMetadata convertStreamMetadata(final IIOMetadata iioMetadata, final ImageWriteParam imageWriteParam) {
        if (iioMetadata instanceof JPEGMetadata && ((JPEGMetadata)iioMetadata).isStream) {
            return iioMetadata;
        }
        return null;
    }
    
    @Override
    public IIOMetadata convertImageMetadata(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        this.setThreadLock();
        try {
            return this.convertImageMetadataOnThread(iioMetadata, imageTypeSpecifier, imageWriteParam);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private IIOMetadata convertImageMetadataOnThread(final IIOMetadata iioMetadata, final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam) {
        if (!(iioMetadata instanceof JPEGMetadata)) {
            if (iioMetadata.isStandardMetadataFormatSupported()) {
                final String s = "javax_imageio_1.0";
                final Node asTree = iioMetadata.getAsTree(s);
                if (asTree != null) {
                    final JPEGMetadata jpegMetadata = new JPEGMetadata(imageTypeSpecifier, imageWriteParam, this);
                    try {
                        jpegMetadata.setFromTree(s, asTree);
                    }
                    catch (final IIOInvalidTreeException ex) {
                        return null;
                    }
                    return jpegMetadata;
                }
            }
            return null;
        }
        if (!((JPEGMetadata)iioMetadata).isStream) {
            return iioMetadata;
        }
        return null;
    }
    
    @Override
    public int getNumThumbnailsSupported(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        if (this.jfifOK(imageTypeSpecifier, imageWriteParam, iioMetadata, iioMetadata2)) {
            return Integer.MAX_VALUE;
        }
        return 0;
    }
    
    @Override
    public Dimension[] getPreferredThumbnailSizes(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        if (this.jfifOK(imageTypeSpecifier, imageWriteParam, iioMetadata, iioMetadata2)) {
            return JPEGImageWriter.preferredThumbSizes.clone();
        }
        return null;
    }
    
    private boolean jfifOK(final ImageTypeSpecifier imageTypeSpecifier, final ImageWriteParam imageWriteParam, final IIOMetadata iioMetadata, final IIOMetadata iioMetadata2) {
        if (imageTypeSpecifier != null && !JPEG.isJFIFcompliant(imageTypeSpecifier, true)) {
            return false;
        }
        if (iioMetadata2 != null) {
            JPEGMetadata jpegMetadata;
            if (iioMetadata2 instanceof JPEGMetadata) {
                jpegMetadata = (JPEGMetadata)iioMetadata2;
            }
            else {
                jpegMetadata = (JPEGMetadata)this.convertImageMetadata(iioMetadata2, imageTypeSpecifier, imageWriteParam);
            }
            if (jpegMetadata.findMarkerSegment(JFIFMarkerSegment.class, true) == null) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean canWriteRasters() {
        return true;
    }
    
    @Override
    public void write(final IIOMetadata iioMetadata, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            this.writeOnThread(iioMetadata, iioImage, imageWriteParam);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private void writeOnThread(final IIOMetadata iioMetadata, final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        if (this.ios == null) {
            throw new IllegalStateException("Output has not been set!");
        }
        if (iioImage == null) {
            throw new IllegalArgumentException("image is null!");
        }
        if (iioMetadata != null) {
            this.warningOccurred(1);
        }
        final boolean hasRaster = iioImage.hasRaster();
        RenderedImage renderedImage = null;
        if (hasRaster) {
            this.srcRas = iioImage.getRaster();
        }
        else {
            renderedImage = iioImage.getRenderedImage();
            if (renderedImage instanceof BufferedImage) {
                this.srcRas = ((BufferedImage)renderedImage).getRaster();
            }
            else if (renderedImage.getNumXTiles() == 1 && renderedImage.getNumYTiles() == 1) {
                this.srcRas = renderedImage.getTile(renderedImage.getMinTileX(), renderedImage.getMinTileY());
                if (this.srcRas.getWidth() != renderedImage.getWidth() || this.srcRas.getHeight() != renderedImage.getHeight()) {
                    this.srcRas = this.srcRas.createChild(this.srcRas.getMinX(), this.srcRas.getMinY(), renderedImage.getWidth(), renderedImage.getHeight(), this.srcRas.getMinX(), this.srcRas.getMinY(), null);
                }
            }
            else {
                this.srcRas = renderedImage.getData();
            }
        }
        int n = this.srcRas.getNumBands();
        this.indexed = false;
        this.indexCM = null;
        ColorModel colorModel = null;
        ColorSpace colorSpace = null;
        this.isAlphaPremultiplied = false;
        this.srcCM = null;
        if (!hasRaster) {
            colorModel = renderedImage.getColorModel();
            if (colorModel != null) {
                colorSpace = colorModel.getColorSpace();
                if (colorModel instanceof IndexColorModel) {
                    this.indexed = true;
                    this.indexCM = (IndexColorModel)colorModel;
                    n = colorModel.getNumComponents();
                }
                if (colorModel.isAlphaPremultiplied()) {
                    this.isAlphaPremultiplied = true;
                    this.srcCM = colorModel;
                }
            }
        }
        this.srcBands = JPEG.bandOffsets[n - 1];
        int length = n;
        if (imageWriteParam != null) {
            final int[] sourceBands = imageWriteParam.getSourceBands();
            if (sourceBands != null) {
                if (this.indexed) {
                    this.warningOccurred(8);
                }
                else {
                    this.srcBands = sourceBands;
                    length = this.srcBands.length;
                    if (length > n) {
                        throw new IIOException("ImageWriteParam specifies too many source bands");
                    }
                }
            }
        }
        final boolean b = length != n;
        final boolean b2 = !hasRaster && !b;
        int[] sampleSize;
        if (!this.indexed) {
            sampleSize = this.srcRas.getSampleModel().getSampleSize();
            if (b) {
                final int[] array = new int[length];
                for (int i = 0; i < length; ++i) {
                    array[i] = sampleSize[this.srcBands[i]];
                }
                sampleSize = array;
            }
        }
        else {
            final int[] sampleSize2 = this.srcRas.getSampleModel().getSampleSize();
            sampleSize = new int[n];
            for (int j = 0; j < n; ++j) {
                sampleSize[j] = sampleSize2[0];
            }
        }
        for (int k = 0; k < sampleSize.length; ++k) {
            if (sampleSize[k] <= 0 || sampleSize[k] > 8) {
                throw new IIOException("Illegal band size: should be 0 < size <= 8");
            }
            if (this.indexed) {
                sampleSize[k] = 8;
            }
        }
        if (this.debug) {
            System.out.println("numSrcBands is " + n);
            System.out.println("numBandsUsed is " + length);
            System.out.println("usingBandSubset is " + b);
            System.out.println("fullImage is " + b2);
            System.out.print("Band sizes:");
            for (int l = 0; l < sampleSize.length; ++l) {
                System.out.print(" " + sampleSize[l]);
            }
            System.out.println();
        }
        ImageTypeSpecifier destinationType = null;
        if (imageWriteParam != null) {
            destinationType = imageWriteParam.getDestinationType();
            if (b2 && destinationType != null) {
                this.warningOccurred(0);
                destinationType = null;
            }
        }
        this.sourceXOffset = this.srcRas.getMinX();
        this.sourceYOffset = this.srcRas.getMinY();
        final int width = this.srcRas.getWidth();
        final int height = this.srcRas.getHeight();
        this.sourceWidth = width;
        this.sourceHeight = height;
        int sourceXSubsampling = 1;
        int sourceYSubsampling = 1;
        int subsamplingXOffset = 0;
        int subsamplingYOffset = 0;
        JPEGQTable[] array2 = null;
        JPEGHuffmanTable[] array3 = null;
        JPEGHuffmanTable[] array4 = null;
        boolean optimizeHuffmanTables = false;
        JPEGImageWriteParam jpegImageWriteParam = null;
        int progressiveMode = 0;
        if (imageWriteParam != null) {
            final Rectangle sourceRegion = imageWriteParam.getSourceRegion();
            if (sourceRegion != null) {
                final Rectangle intersection = sourceRegion.intersection(new Rectangle(this.sourceXOffset, this.sourceYOffset, this.sourceWidth, this.sourceHeight));
                this.sourceXOffset = intersection.x;
                this.sourceYOffset = intersection.y;
                this.sourceWidth = intersection.width;
                this.sourceHeight = intersection.height;
            }
            if (this.sourceWidth + this.sourceXOffset > width) {
                this.sourceWidth = width - this.sourceXOffset;
            }
            if (this.sourceHeight + this.sourceYOffset > height) {
                this.sourceHeight = height - this.sourceYOffset;
            }
            sourceXSubsampling = imageWriteParam.getSourceXSubsampling();
            sourceYSubsampling = imageWriteParam.getSourceYSubsampling();
            subsamplingXOffset = imageWriteParam.getSubsamplingXOffset();
            subsamplingYOffset = imageWriteParam.getSubsamplingYOffset();
            switch (imageWriteParam.getCompressionMode()) {
                case 0: {
                    throw new IIOException("JPEG compression cannot be disabled");
                }
                case 2: {
                    final float convertToLinearQuality = JPEG.convertToLinearQuality(imageWriteParam.getCompressionQuality());
                    array2 = new JPEGQTable[] { JPEGQTable.K1Luminance.getScaledInstance(convertToLinearQuality, true), JPEGQTable.K2Chrominance.getScaledInstance(convertToLinearQuality, true) };
                    break;
                }
                case 1: {
                    array2 = new JPEGQTable[] { JPEGQTable.K1Div2Luminance, JPEGQTable.K2Div2Chrominance };
                    break;
                }
            }
            progressiveMode = imageWriteParam.getProgressiveMode();
            if (imageWriteParam instanceof JPEGImageWriteParam) {
                jpegImageWriteParam = (JPEGImageWriteParam)imageWriteParam;
                optimizeHuffmanTables = jpegImageWriteParam.getOptimizeHuffmanTables();
            }
        }
        final IIOMetadata metadata = iioImage.getMetadata();
        if (metadata != null) {
            if (metadata instanceof JPEGMetadata) {
                this.metadata = (JPEGMetadata)metadata;
                if (this.debug) {
                    System.out.println("We have metadata, and it's JPEG metadata");
                }
            }
            else if (!hasRaster) {
                ImageTypeSpecifier imageTypeSpecifier = destinationType;
                if (imageTypeSpecifier == null) {
                    imageTypeSpecifier = new ImageTypeSpecifier(renderedImage);
                }
                this.metadata = (JPEGMetadata)this.convertImageMetadata(metadata, imageTypeSpecifier, imageWriteParam);
            }
            else {
                this.warningOccurred(7);
            }
        }
        this.ignoreJFIF = false;
        this.ignoreAdobe = false;
        this.newAdobeTransform = -1;
        this.writeDefaultJFIF = false;
        this.writeAdobe = false;
        int n2 = 0;
        int n3 = 0;
        JFIFMarkerSegment jfifMarkerSegment = null;
        AdobeMarkerSegment adobeMarkerSegment = null;
        SOFMarkerSegment sofMarkerSegment = null;
        if (this.metadata != null) {
            jfifMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
            adobeMarkerSegment = (AdobeMarkerSegment)this.metadata.findMarkerSegment(AdobeMarkerSegment.class, true);
            sofMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
        }
        this.iccProfile = null;
        this.convertTosRGB = false;
        this.converted = null;
        Label_1997: {
            if (destinationType != null) {
                if (length != destinationType.getNumBands()) {
                    throw new IIOException("Number of source bands != number of destination bands");
                }
                final ColorSpace colorSpace2 = destinationType.getColorModel().getColorSpace();
                if (this.metadata != null) {
                    this.checkSOFBands(sofMarkerSegment, length);
                    this.checkJFIF(jfifMarkerSegment, destinationType, false);
                    if (jfifMarkerSegment != null && !this.ignoreJFIF && JPEG.isNonStandardICC(colorSpace2)) {
                        this.iccProfile = ((ICC_ColorSpace)colorSpace2).getProfile();
                    }
                    this.checkAdobe(adobeMarkerSegment, destinationType, false);
                }
                else {
                    if (JPEG.isJFIFcompliant(destinationType, false)) {
                        this.writeDefaultJFIF = true;
                        if (JPEG.isNonStandardICC(colorSpace2)) {
                            this.iccProfile = ((ICC_ColorSpace)colorSpace2).getProfile();
                        }
                    }
                    else {
                        final int transformForType = JPEG.transformForType(destinationType, false);
                        if (transformForType != -1) {
                            this.writeAdobe = true;
                            this.newAdobeTransform = transformForType;
                        }
                    }
                    this.metadata = new JPEGMetadata(destinationType, null, this);
                }
                n2 = this.getSrcCSType(destinationType);
                n3 = this.getDefaultDestCSType(destinationType);
            }
            else if (this.metadata == null) {
                if (b2) {
                    this.metadata = new JPEGMetadata(new ImageTypeSpecifier(renderedImage), imageWriteParam, this);
                    if (this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true) != null) {
                        final ColorSpace colorSpace3 = renderedImage.getColorModel().getColorSpace();
                        if (JPEG.isNonStandardICC(colorSpace3)) {
                            this.iccProfile = ((ICC_ColorSpace)colorSpace3).getProfile();
                        }
                    }
                    n2 = this.getSrcCSType(renderedImage);
                    n3 = this.getDefaultDestCSType(renderedImage);
                }
            }
            else {
                this.checkSOFBands(sofMarkerSegment, length);
                if (b2) {
                    final ImageTypeSpecifier imageTypeSpecifier2 = new ImageTypeSpecifier(renderedImage);
                    n2 = this.getSrcCSType(renderedImage);
                    if (colorModel != null) {
                        final boolean hasAlpha = colorModel.hasAlpha();
                        switch (colorSpace.getType()) {
                            case 6: {
                                if (!hasAlpha) {
                                    n3 = 1;
                                }
                                else if (jfifMarkerSegment != null) {
                                    this.ignoreJFIF = true;
                                    this.warningOccurred(5);
                                }
                                if (adobeMarkerSegment != null && adobeMarkerSegment.transform != 0) {
                                    this.newAdobeTransform = 0;
                                    this.warningOccurred(6);
                                    break;
                                }
                                break;
                            }
                            case 5: {
                                if (jfifMarkerSegment != null) {
                                    n3 = 3;
                                    if (JPEG.isNonStandardICC(colorSpace) || (colorSpace instanceof ICC_ColorSpace && jfifMarkerSegment.iccSegment != null)) {
                                        this.iccProfile = ((ICC_ColorSpace)colorSpace).getProfile();
                                        break;
                                    }
                                    break;
                                }
                                else if (adobeMarkerSegment != null) {
                                    switch (adobeMarkerSegment.transform) {
                                        case 0: {
                                            n3 = 2;
                                            break Label_1997;
                                        }
                                        case 1: {
                                            n3 = 3;
                                            break Label_1997;
                                        }
                                        default: {
                                            this.warningOccurred(6);
                                            this.newAdobeTransform = 0;
                                            n3 = 2;
                                            break Label_1997;
                                        }
                                    }
                                }
                                else {
                                    final int iDencodedCSType = sofMarkerSegment.getIDencodedCSType();
                                    if (iDencodedCSType != 0) {
                                        n3 = iDencodedCSType;
                                        break;
                                    }
                                    if (this.isSubsampled(sofMarkerSegment.componentSpecs)) {
                                        n3 = 3;
                                        break;
                                    }
                                    n3 = 2;
                                    break;
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
        boolean b3 = false;
        int[] collectScans = null;
        if (this.metadata != null) {
            if (sofMarkerSegment == null) {
                sofMarkerSegment = (SOFMarkerSegment)this.metadata.findMarkerSegment(SOFMarkerSegment.class, true);
            }
            if (sofMarkerSegment != null && sofMarkerSegment.tag == 194) {
                b3 = true;
                if (progressiveMode == 3) {
                    collectScans = this.collectScans(this.metadata, sofMarkerSegment);
                }
                else {
                    this.numScans = 0;
                }
            }
            if (jfifMarkerSegment == null) {
                jfifMarkerSegment = (JFIFMarkerSegment)this.metadata.findMarkerSegment(JFIFMarkerSegment.class, true);
            }
        }
        this.thumbnails = iioImage.getThumbnails();
        final int numThumbnails = iioImage.getNumThumbnails();
        this.forceJFIF = false;
        if (!this.writeDefaultJFIF) {
            if (this.metadata == null) {
                this.thumbnails = null;
                if (numThumbnails != 0) {
                    this.warningOccurred(10);
                }
            }
            else if (!b2) {
                if (jfifMarkerSegment == null) {
                    this.thumbnails = null;
                    if (numThumbnails != 0) {
                        this.warningOccurred(10);
                    }
                }
            }
            else if (jfifMarkerSegment == null) {
                if (n3 == 1 || n3 == 3) {
                    if (numThumbnails != 0) {
                        this.forceJFIF = true;
                        this.warningOccurred(11);
                    }
                }
                else {
                    this.thumbnails = null;
                    if (numThumbnails != 0) {
                        this.warningOccurred(10);
                    }
                }
            }
        }
        final boolean b4 = this.metadata != null || this.writeDefaultJFIF || this.writeAdobe;
        boolean b5 = true;
        boolean b6 = true;
        DQTMarkerSegment dqtMarkerSegment = null;
        DHTMarkerSegment dhtMarkerSegment = null;
        int restartInterval = 0;
        if (this.metadata != null) {
            dqtMarkerSegment = (DQTMarkerSegment)this.metadata.findMarkerSegment(DQTMarkerSegment.class, true);
            dhtMarkerSegment = (DHTMarkerSegment)this.metadata.findMarkerSegment(DHTMarkerSegment.class, true);
            final DRIMarkerSegment driMarkerSegment = (DRIMarkerSegment)this.metadata.findMarkerSegment(DRIMarkerSegment.class, true);
            if (driMarkerSegment != null) {
                restartInterval = driMarkerSegment.restartInterval;
            }
            if (dqtMarkerSegment == null) {
                b5 = false;
            }
            if (dhtMarkerSegment == null) {
                b6 = false;
            }
        }
        if (array2 == null) {
            if (dqtMarkerSegment != null) {
                array2 = this.collectQTablesFromMetadata(this.metadata);
            }
            else if (this.streamQTables != null) {
                array2 = this.streamQTables;
            }
            else if (jpegImageWriteParam != null && jpegImageWriteParam.areTablesSet()) {
                array2 = jpegImageWriteParam.getQTables();
            }
            else {
                array2 = JPEG.getDefaultQTables();
            }
        }
        if (!optimizeHuffmanTables) {
            if (dhtMarkerSegment != null && !b3) {
                array3 = this.collectHTablesFromMetadata(this.metadata, true);
                array4 = this.collectHTablesFromMetadata(this.metadata, false);
            }
            else if (this.streamDCHuffmanTables != null) {
                array3 = this.streamDCHuffmanTables;
                array4 = this.streamACHuffmanTables;
            }
            else if (jpegImageWriteParam != null && jpegImageWriteParam.areTablesSet()) {
                array3 = jpegImageWriteParam.getDCHuffmanTables();
                array4 = jpegImageWriteParam.getACHuffmanTables();
            }
            else {
                array3 = JPEG.getDefaultHuffmanTables(true);
                array4 = JPEG.getDefaultHuffmanTables(false);
            }
        }
        final int[] array5 = new int[length];
        final int[] array6 = new int[length];
        final int[] array7 = new int[length];
        final int[] array8 = new int[length];
        for (int n4 = 0; n4 < length; ++n4) {
            array5[n4] = n4 + 1;
            array7[n4] = (array6[n4] = 1);
            array8[n4] = 0;
        }
        if (sofMarkerSegment != null) {
            for (int n5 = 0; n5 < length; ++n5) {
                if (!this.forceJFIF) {
                    array5[n5] = sofMarkerSegment.componentSpecs[n5].componentId;
                }
                array6[n5] = sofMarkerSegment.componentSpecs[n5].HsamplingFactor;
                array7[n5] = sofMarkerSegment.componentSpecs[n5].VsamplingFactor;
                array8[n5] = sofMarkerSegment.componentSpecs[n5].QtableSelector;
            }
        }
        this.sourceXOffset += subsamplingXOffset;
        this.sourceWidth -= subsamplingXOffset;
        this.sourceYOffset += subsamplingYOffset;
        this.sourceHeight -= subsamplingYOffset;
        final int n6 = (this.sourceWidth + sourceXSubsampling - 1) / sourceXSubsampling;
        final int n7 = (this.sourceHeight + sourceYSubsampling - 1) / sourceYSubsampling;
        final int n8 = this.sourceWidth * length;
        final DataBufferByte dataBufferByte = new DataBufferByte(n8);
        this.raster = Raster.createInterleavedRaster(dataBufferByte, this.sourceWidth, 1, n8, length, JPEG.bandOffsets[length - 1], null);
        this.clearAbortRequest();
        this.cbLock.lock();
        try {
            this.processImageStarted(this.currentImage);
        }
        finally {
            this.cbLock.unlock();
        }
        if (this.debug) {
            System.out.println("inCsType: " + n2);
            System.out.println("outCsType: " + n3);
        }
        final boolean writeImage = this.writeImage(this.structPointer, dataBufferByte.getData(), n2, n3, length, sampleSize, this.sourceWidth, n6, n7, sourceXSubsampling, sourceYSubsampling, array2, b5, array3, array4, b6, optimizeHuffmanTables, progressiveMode != 0, this.numScans, collectScans, array5, array6, array7, array8, b4, restartInterval);
        this.cbLock.lock();
        try {
            if (writeImage) {
                this.processWriteAborted();
            }
            else {
                this.processImageComplete();
            }
            this.ios.flush();
        }
        finally {
            this.cbLock.unlock();
        }
        ++this.currentImage;
    }
    
    @Override
    public boolean canWriteSequence() {
        return true;
    }
    
    @Override
    public void prepareWriteSequence(final IIOMetadata iioMetadata) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            this.prepareWriteSequenceOnThread(iioMetadata);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private void prepareWriteSequenceOnThread(final IIOMetadata iioMetadata) throws IOException {
        if (this.ios == null) {
            throw new IllegalStateException("Output has not been set!");
        }
        if (iioMetadata != null) {
            if (!(iioMetadata instanceof JPEGMetadata)) {
                throw new IIOException("Stream metadata must be JPEG metadata");
            }
            final JPEGMetadata jpegMetadata = (JPEGMetadata)iioMetadata;
            if (!jpegMetadata.isStream) {
                throw new IllegalArgumentException("Invalid stream metadata object.");
            }
            if (this.currentImage != 0) {
                throw new IIOException("JPEG Stream metadata must precede all images");
            }
            if (this.sequencePrepared) {
                throw new IIOException("Stream metadata already written!");
            }
            this.streamQTables = this.collectQTablesFromMetadata(jpegMetadata);
            if (this.debug) {
                System.out.println("after collecting from stream metadata, streamQTables.length is " + this.streamQTables.length);
            }
            if (this.streamQTables == null) {
                this.streamQTables = JPEG.getDefaultQTables();
            }
            this.streamDCHuffmanTables = this.collectHTablesFromMetadata(jpegMetadata, true);
            if (this.streamDCHuffmanTables == null) {
                this.streamDCHuffmanTables = JPEG.getDefaultHuffmanTables(true);
            }
            this.streamACHuffmanTables = this.collectHTablesFromMetadata(jpegMetadata, false);
            if (this.streamACHuffmanTables == null) {
                this.streamACHuffmanTables = JPEG.getDefaultHuffmanTables(false);
            }
            this.writeTables(this.structPointer, this.streamQTables, this.streamDCHuffmanTables, this.streamACHuffmanTables);
        }
        this.sequencePrepared = true;
    }
    
    @Override
    public void writeToSequence(final IIOImage iioImage, final ImageWriteParam imageWriteParam) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (!this.sequencePrepared) {
                throw new IllegalStateException("sequencePrepared not called!");
            }
            this.write(null, iioImage, imageWriteParam);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public void endWriteSequence() throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (!this.sequencePrepared) {
                throw new IllegalStateException("sequencePrepared not called!");
            }
            this.sequencePrepared = false;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public synchronized void abort() {
        this.setThreadLock();
        try {
            super.abort();
            this.abortWrite(this.structPointer);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    protected synchronized void clearAbortRequest() {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (this.abortRequested()) {
                super.clearAbortRequest();
                this.resetWriter(this.structPointer);
                this.setDest(this.structPointer);
            }
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private void resetInternalState() {
        this.resetWriter(this.structPointer);
        this.srcRas = null;
        this.raster = null;
        this.convertTosRGB = false;
        this.currentImage = 0;
        this.numScans = 0;
        this.metadata = null;
    }
    
    @Override
    public void reset() {
        this.setThreadLock();
        try {
            this.cbLock.check();
            super.reset();
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public void dispose() {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (this.structPointer != 0L) {
                this.disposerRecord.dispose();
                this.structPointer = 0L;
            }
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    void warningOccurred(final int n) {
        this.cbLock.lock();
        try {
            if (n < 0 || n > 15) {
                throw new InternalError("Invalid warning index");
            }
            this.processWarningOccurred(this.currentImage, "com.sun.imageio.plugins.jpeg.JPEGImageWriterResources", Integer.toString(n));
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    void warningWithMessage(final String s) {
        this.cbLock.lock();
        try {
            this.processWarningOccurred(this.currentImage, s);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    void thumbnailStarted(final int n) {
        this.cbLock.lock();
        try {
            this.processThumbnailStarted(this.currentImage, n);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    void thumbnailProgress(final float n) {
        this.cbLock.lock();
        try {
            this.processThumbnailProgress(n);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    void thumbnailComplete() {
        this.cbLock.lock();
        try {
            this.processThumbnailComplete();
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private void checkSOFBands(final SOFMarkerSegment sofMarkerSegment, final int n) throws IIOException {
        if (sofMarkerSegment != null && sofMarkerSegment.componentSpecs.length != n) {
            throw new IIOException("Metadata components != number of destination bands");
        }
    }
    
    private void checkJFIF(final JFIFMarkerSegment jfifMarkerSegment, final ImageTypeSpecifier imageTypeSpecifier, final boolean b) {
        if (jfifMarkerSegment != null && !JPEG.isJFIFcompliant(imageTypeSpecifier, b)) {
            this.ignoreJFIF = true;
            this.warningOccurred(b ? 5 : 3);
        }
    }
    
    private void checkAdobe(final AdobeMarkerSegment adobeMarkerSegment, final ImageTypeSpecifier imageTypeSpecifier, final boolean b) {
        if (adobeMarkerSegment != null) {
            final int transformForType = JPEG.transformForType(imageTypeSpecifier, b);
            if (adobeMarkerSegment.transform != transformForType) {
                this.warningOccurred(b ? 6 : 4);
                if (transformForType == -1) {
                    this.ignoreAdobe = true;
                }
                else {
                    this.newAdobeTransform = transformForType;
                }
            }
        }
    }
    
    private int[] collectScans(final JPEGMetadata jpegMetadata, final SOFMarkerSegment sofMarkerSegment) {
        final ArrayList list = new ArrayList();
        final int n = 9;
        final int n2 = 4;
        for (final MarkerSegment markerSegment : jpegMetadata.markerSequence) {
            if (markerSegment instanceof SOSMarkerSegment) {
                list.add(markerSegment);
            }
        }
        int[] array = null;
        this.numScans = 0;
        if (!list.isEmpty()) {
            this.numScans = list.size();
            array = new int[this.numScans * n];
            int n3 = 0;
            for (int i = 0; i < this.numScans; ++i) {
                final SOSMarkerSegment sosMarkerSegment = (SOSMarkerSegment)list.get(i);
                array[n3++] = sosMarkerSegment.componentSpecs.length;
                for (int j = 0; j < n2; ++j) {
                    if (j < sosMarkerSegment.componentSpecs.length) {
                        final int componentSelector = sosMarkerSegment.componentSpecs[j].componentSelector;
                        for (int k = 0; k < sofMarkerSegment.componentSpecs.length; ++k) {
                            if (componentSelector == sofMarkerSegment.componentSpecs[k].componentId) {
                                array[n3++] = k;
                                break;
                            }
                        }
                    }
                    else {
                        array[n3++] = 0;
                    }
                }
                array[n3++] = sosMarkerSegment.startSpectralSelection;
                array[n3++] = sosMarkerSegment.endSpectralSelection;
                array[n3++] = sosMarkerSegment.approxHigh;
                array[n3++] = sosMarkerSegment.approxLow;
            }
        }
        return array;
    }
    
    private JPEGQTable[] collectQTablesFromMetadata(final JPEGMetadata jpegMetadata) {
        final ArrayList list = new ArrayList();
        for (final MarkerSegment markerSegment : jpegMetadata.markerSequence) {
            if (markerSegment instanceof DQTMarkerSegment) {
                list.addAll(((DQTMarkerSegment)markerSegment).tables);
            }
        }
        JPEGQTable[] array = null;
        if (list.size() != 0) {
            array = new JPEGQTable[list.size()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = new JPEGQTable(((DQTMarkerSegment.Qtable)list.get(i)).data);
            }
        }
        return array;
    }
    
    private JPEGHuffmanTable[] collectHTablesFromMetadata(final JPEGMetadata jpegMetadata, final boolean b) throws IIOException {
        final ArrayList list = new ArrayList();
        for (final MarkerSegment markerSegment : jpegMetadata.markerSequence) {
            if (markerSegment instanceof DHTMarkerSegment) {
                final DHTMarkerSegment dhtMarkerSegment = (DHTMarkerSegment)markerSegment;
                for (int i = 0; i < dhtMarkerSegment.tables.size(); ++i) {
                    final DHTMarkerSegment.Htable htable = dhtMarkerSegment.tables.get(i);
                    if (htable.tableClass == (b ? 0 : 1)) {
                        list.add(htable);
                    }
                }
            }
        }
        JPEGHuffmanTable[] array = null;
        if (list.size() != 0) {
            final DHTMarkerSegment.Htable[] array2 = new DHTMarkerSegment.Htable[list.size()];
            list.toArray(array2);
            array = new JPEGHuffmanTable[list.size()];
            for (int j = 0; j < array.length; ++j) {
                array[j] = null;
                for (int k = 0; k < list.size(); ++k) {
                    if (array2[k].tableID == j) {
                        if (array[j] != null) {
                            throw new IIOException("Metadata has duplicate Htables!");
                        }
                        array[j] = new JPEGHuffmanTable(array2[k].numCodes, array2[k].values);
                    }
                }
            }
        }
        return array;
    }
    
    private int getSrcCSType(final ImageTypeSpecifier imageTypeSpecifier) {
        return this.getSrcCSType(imageTypeSpecifier.getColorModel());
    }
    
    private int getSrcCSType(final RenderedImage renderedImage) {
        return this.getSrcCSType(renderedImage.getColorModel());
    }
    
    private int getSrcCSType(final ColorModel colorModel) {
        int n = 0;
        if (colorModel != null) {
            colorModel.hasAlpha();
            switch (colorModel.getColorSpace().getType()) {
                case 6: {
                    n = 1;
                    break;
                }
                case 5: {
                    n = 2;
                    break;
                }
                case 3: {
                    n = 3;
                    break;
                }
                case 9: {
                    n = 4;
                    break;
                }
            }
        }
        return n;
    }
    
    private int getDestCSType(final ImageTypeSpecifier imageTypeSpecifier) {
        final ColorModel colorModel = imageTypeSpecifier.getColorModel();
        colorModel.hasAlpha();
        final ColorSpace colorSpace = colorModel.getColorSpace();
        int n = 0;
        switch (colorSpace.getType()) {
            case 6: {
                n = 1;
                break;
            }
            case 5: {
                n = 2;
                break;
            }
            case 3: {
                n = 3;
                break;
            }
            case 9: {
                n = 4;
                break;
            }
        }
        return n;
    }
    
    private int getDefaultDestCSType(final ImageTypeSpecifier imageTypeSpecifier) {
        return this.getDefaultDestCSType(imageTypeSpecifier.getColorModel());
    }
    
    private int getDefaultDestCSType(final RenderedImage renderedImage) {
        return this.getDefaultDestCSType(renderedImage.getColorModel());
    }
    
    private int getDefaultDestCSType(final ColorModel colorModel) {
        int n = 0;
        if (colorModel != null) {
            colorModel.hasAlpha();
            switch (colorModel.getColorSpace().getType()) {
                case 6: {
                    n = 1;
                    break;
                }
                case 5: {
                    n = 3;
                    break;
                }
                case 3: {
                    n = 3;
                    break;
                }
                case 9: {
                    n = 5;
                    break;
                }
            }
        }
        return n;
    }
    
    private boolean isSubsampled(final SOFMarkerSegment.ComponentSpec[] array) {
        final int hsamplingFactor = array[0].HsamplingFactor;
        final int vsamplingFactor = array[0].VsamplingFactor;
        for (int i = 1; i < array.length; ++i) {
            if (array[i].HsamplingFactor != hsamplingFactor || array[i].HsamplingFactor != hsamplingFactor) {
                return true;
            }
        }
        return false;
    }
    
    private static native void initWriterIDs(final Class p0, final Class p1);
    
    private native long initJPEGImageWriter();
    
    private native void setDest(final long p0);
    
    private native boolean writeImage(final long p0, final byte[] p1, final int p2, final int p3, final int p4, final int[] p5, final int p6, final int p7, final int p8, final int p9, final int p10, final JPEGQTable[] p11, final boolean p12, final JPEGHuffmanTable[] p13, final JPEGHuffmanTable[] p14, final boolean p15, final boolean p16, final boolean p17, final int p18, final int[] p19, final int[] p20, final int[] p21, final int[] p22, final int[] p23, final boolean p24, final int p25);
    
    private void writeMetadata() throws IOException {
        if (this.metadata == null) {
            if (this.writeDefaultJFIF) {
                JFIFMarkerSegment.writeDefaultJFIF(this.ios, this.thumbnails, this.iccProfile, this);
            }
            if (this.writeAdobe) {
                AdobeMarkerSegment.writeAdobeSegment(this.ios, this.newAdobeTransform);
            }
        }
        else {
            this.metadata.writeToStream(this.ios, this.ignoreJFIF, this.forceJFIF, this.thumbnails, this.iccProfile, this.ignoreAdobe, this.newAdobeTransform, this);
        }
    }
    
    private native void writeTables(final long p0, final JPEGQTable[] p1, final JPEGHuffmanTable[] p2, final JPEGHuffmanTable[] p3);
    
    private void grabPixels(final int n) {
        Raster rect;
        if (this.indexed) {
            rect = this.indexCM.convertToIntDiscrete(this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + n, this.sourceWidth, 1, 0, 0, new int[] { 0 }), this.indexCM.getTransparency() != 1).getRaster();
        }
        else {
            rect = this.srcRas.createChild(this.sourceXOffset, this.sourceYOffset + n, this.sourceWidth, 1, 0, 0, this.srcBands);
        }
        if (this.convertTosRGB) {
            if (this.debug) {
                System.out.println("Converting to sRGB");
            }
            this.converted = this.convertOp.filter(rect, this.converted);
            rect = this.converted;
        }
        if (this.isAlphaPremultiplied) {
            final WritableRaster compatibleWritableRaster = rect.createCompatibleWritableRaster();
            compatibleWritableRaster.setPixels(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), rect.getPixels(rect.getMinX(), rect.getMinY(), rect.getWidth(), rect.getHeight(), (int[])null));
            this.srcCM.coerceData(compatibleWritableRaster, false);
            rect = compatibleWritableRaster.createChild(compatibleWritableRaster.getMinX(), compatibleWritableRaster.getMinY(), compatibleWritableRaster.getWidth(), compatibleWritableRaster.getHeight(), 0, 0, this.srcBands);
        }
        this.raster.setRect(rect);
        if (n > 7 && n % 8 == 0) {
            this.cbLock.lock();
            try {
                this.processImageProgress(n / (float)this.sourceHeight * 100.0f);
            }
            finally {
                this.cbLock.unlock();
            }
        }
    }
    
    private native void abortWrite(final long p0);
    
    private native void resetWriter(final long p0);
    
    private static native void disposeWriter(final long p0);
    
    private void writeOutputData(final byte[] array, final int n, final int n2) throws IOException {
        this.cbLock.lock();
        try {
            this.ios.write(array, n, n2);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private synchronized void setThreadLock() {
        final Thread currentThread = Thread.currentThread();
        if (this.theThread != null) {
            if (this.theThread != currentThread) {
                throw new IllegalStateException("Attempt to use instance of " + this + " locked on thread " + this.theThread + " from thread " + currentThread);
            }
            ++this.theLockCount;
        }
        else {
            this.theThread = currentThread;
            this.theLockCount = 1;
        }
    }
    
    private synchronized void clearThreadLock() {
        final Thread currentThread = Thread.currentThread();
        if (this.theThread == null || this.theThread != currentThread) {
            throw new IllegalStateException("Attempt to clear thread lock form wrong thread. Locked thread: " + this.theThread + "; current thread: " + currentThread);
        }
        --this.theLockCount;
        if (this.theLockCount == 0) {
            this.theThread = null;
        }
    }
    
    static {
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                System.loadLibrary("jpeg");
                return null;
            }
        });
        initWriterIDs(JPEGQTable.class, JPEGHuffmanTable.class);
        preferredThumbSizes = new Dimension[] { new Dimension(1, 1), new Dimension(255, 255) };
    }
    
    private static class JPEGWriterDisposerRecord implements DisposerRecord
    {
        private long pData;
        
        public JPEGWriterDisposerRecord(final long pData) {
            this.pData = pData;
        }
        
        @Override
        public synchronized void dispose() {
            if (this.pData != 0L) {
                disposeWriter(this.pData);
                this.pData = 0L;
            }
        }
    }
    
    private static class CallBackLock
    {
        private State lockState;
        
        CallBackLock() {
            this.lockState = State.Unlocked;
        }
        
        void check() {
            if (this.lockState != State.Unlocked) {
                throw new IllegalStateException("Access to the writer is not allowed");
            }
        }
        
        private void lock() {
            this.lockState = State.Locked;
        }
        
        private void unlock() {
            this.lockState = State.Unlocked;
        }
        
        private enum State
        {
            Unlocked, 
            Locked;
        }
    }
}
