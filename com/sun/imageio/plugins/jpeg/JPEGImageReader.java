package com.sun.imageio.plugins.jpeg;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import java.awt.image.ColorModel;
import java.awt.RenderingHints;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageReadParam;
import java.util.ArrayList;
import java.util.Iterator;
import javax.imageio.ImageTypeSpecifier;
import java.awt.color.CMMException;
import java.util.Arrays;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import javax.imageio.IIOException;
import java.io.IOException;
import sun.java2d.Disposer;
import javax.imageio.spi.ImageReaderSpi;
import sun.java2d.DisposerRecord;
import javax.imageio.plugins.jpeg.JPEGHuffmanTable;
import javax.imageio.plugins.jpeg.JPEGQTable;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.color.ColorSpace;
import java.util.List;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;

public class JPEGImageReader extends ImageReader
{
    private boolean debug;
    private long structPointer;
    private ImageInputStream iis;
    private List imagePositions;
    private int numImages;
    protected static final int WARNING_NO_EOI = 0;
    protected static final int WARNING_NO_JFIF_IN_THUMB = 1;
    protected static final int WARNING_IGNORE_INVALID_ICC = 2;
    private static final int MAX_WARNING = 2;
    private int currentImage;
    private int width;
    private int height;
    private int colorSpaceCode;
    private int outColorSpaceCode;
    private int numComponents;
    private ColorSpace iccCS;
    private ColorConvertOp convert;
    private BufferedImage image;
    private WritableRaster raster;
    private WritableRaster target;
    private DataBufferByte buffer;
    private Rectangle destROI;
    private int[] destinationBands;
    private JPEGMetadata streamMetadata;
    private JPEGMetadata imageMetadata;
    private int imageMetadataIndex;
    private boolean haveSeeked;
    private JPEGQTable[] abbrevQTables;
    private JPEGHuffmanTable[] abbrevDCHuffmanTables;
    private JPEGHuffmanTable[] abbrevACHuffmanTables;
    private int minProgressivePass;
    private int maxProgressivePass;
    private static final int UNKNOWN = -1;
    private static final int MIN_ESTIMATED_PASSES = 10;
    private int knownPassCount;
    private int pass;
    private float percentToDate;
    private float previousPassPercentage;
    private int progInterval;
    private boolean tablesOnlyChecked;
    private Object disposerReferent;
    private DisposerRecord disposerRecord;
    private Thread theThread;
    private int theLockCount;
    private CallBackLock cbLock;
    
    private static native void initReaderIDs(final Class p0, final Class p1, final Class p2);
    
    public JPEGImageReader(final ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
        this.debug = false;
        this.structPointer = 0L;
        this.iis = null;
        this.imagePositions = null;
        this.numImages = 0;
        this.currentImage = -1;
        this.iccCS = null;
        this.convert = null;
        this.image = null;
        this.raster = null;
        this.target = null;
        this.buffer = null;
        this.destROI = null;
        this.destinationBands = null;
        this.streamMetadata = null;
        this.imageMetadata = null;
        this.imageMetadataIndex = -1;
        this.haveSeeked = false;
        this.abbrevQTables = null;
        this.abbrevDCHuffmanTables = null;
        this.abbrevACHuffmanTables = null;
        this.minProgressivePass = 0;
        this.maxProgressivePass = Integer.MAX_VALUE;
        this.knownPassCount = -1;
        this.pass = 0;
        this.percentToDate = 0.0f;
        this.previousPassPercentage = 0.0f;
        this.progInterval = 0;
        this.tablesOnlyChecked = false;
        this.disposerReferent = new Object();
        this.theThread = null;
        this.theLockCount = 0;
        this.cbLock = new CallBackLock();
        this.structPointer = this.initJPEGImageReader();
        this.disposerRecord = new JPEGReaderDisposerRecord(this.structPointer);
        Disposer.addRecord(this.disposerReferent, this.disposerRecord);
    }
    
    private native long initJPEGImageReader();
    
    protected void warningOccurred(final int n) {
        this.cbLock.lock();
        try {
            if (n < 0 || n > 2) {
                throw new InternalError("Invalid warning index");
            }
            this.processWarningOccurred("com.sun.imageio.plugins.jpeg.JPEGImageReaderResources", Integer.toString(n));
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    protected void warningWithMessage(final String s) {
        this.cbLock.lock();
        try {
            this.processWarningOccurred(s);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    @Override
    public void setInput(final Object o, final boolean b, final boolean ignoreMetadata) {
        this.setThreadLock();
        try {
            this.cbLock.check();
            super.setInput(o, b, ignoreMetadata);
            this.ignoreMetadata = ignoreMetadata;
            this.resetInternalState();
            this.iis = (ImageInputStream)o;
            this.setSource(this.structPointer);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private int readInputData(final byte[] array, final int n, final int n2) throws IOException {
        this.cbLock.lock();
        try {
            return this.iis.read(array, n, n2);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private long skipInputBytes(final long n) throws IOException {
        this.cbLock.lock();
        try {
            return this.iis.skipBytes(n);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private native void setSource(final long p0);
    
    private void checkTablesOnly() throws IOException {
        if (this.debug) {
            System.out.println("Checking for tables-only image");
        }
        final long streamPosition = this.iis.getStreamPosition();
        if (this.debug) {
            System.out.println("saved pos is " + streamPosition);
            System.out.println("length is " + this.iis.length());
        }
        if (this.readNativeHeader(true)) {
            if (this.debug) {
                System.out.println("tables-only image found");
                System.out.println("pos after return from native is " + this.iis.getStreamPosition());
            }
            if (!this.ignoreMetadata) {
                this.iis.seek(streamPosition);
                this.haveSeeked = true;
                this.streamMetadata = new JPEGMetadata(true, false, this.iis, this);
                final long streamPosition2 = this.iis.getStreamPosition();
                if (this.debug) {
                    System.out.println("pos after constructing stream metadata is " + streamPosition2);
                }
            }
            if (this.hasNextImage()) {
                this.imagePositions.add(new Long(this.iis.getStreamPosition()));
            }
        }
        else {
            this.imagePositions.add(new Long(streamPosition));
            this.currentImage = 0;
        }
        if (this.seekForwardOnly && !this.imagePositions.isEmpty()) {
            this.iis.flushBefore(this.imagePositions.get(this.imagePositions.size() - 1));
        }
        this.tablesOnlyChecked = true;
    }
    
    @Override
    public int getNumImages(final boolean b) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            return this.getNumImagesOnThread(b);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private void skipPastImage(final int n) {
        this.cbLock.lock();
        try {
            this.gotoImage(n);
            this.skipImage();
        }
        catch (final IOException | IndexOutOfBoundsException ex) {}
        finally {
            this.cbLock.unlock();
        }
    }
    
    private int getNumImagesOnThread(final boolean b) throws IOException {
        if (this.numImages != 0) {
            return this.numImages;
        }
        if (this.iis == null) {
            throw new IllegalStateException("Input not set");
        }
        if (!b) {
            return -1;
        }
        if (this.seekForwardOnly) {
            throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
        }
        if (!this.tablesOnlyChecked) {
            this.checkTablesOnly();
        }
        this.iis.mark();
        this.gotoImage(0);
        final JPEGBuffer jpegBuffer = new JPEGBuffer(this.iis);
        jpegBuffer.loadBuf(0);
        boolean scanForFF = false;
        while (!scanForFF) {
            scanForFF = jpegBuffer.scanForFF(this);
            switch (jpegBuffer.buf[jpegBuffer.bufPtr] & 0xFF) {
                case 216: {
                    ++this.numImages;
                }
                case 0:
                case 208:
                case 209:
                case 210:
                case 211:
                case 212:
                case 213:
                case 214:
                case 215:
                case 217: {
                    final JPEGBuffer jpegBuffer2 = jpegBuffer;
                    --jpegBuffer2.bufAvail;
                    final JPEGBuffer jpegBuffer3 = jpegBuffer;
                    ++jpegBuffer3.bufPtr;
                    continue;
                }
                default: {
                    final JPEGBuffer jpegBuffer4 = jpegBuffer;
                    --jpegBuffer4.bufAvail;
                    final JPEGBuffer jpegBuffer5 = jpegBuffer;
                    ++jpegBuffer5.bufPtr;
                    jpegBuffer.loadBuf(2);
                    int n = (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF) << 8 | (jpegBuffer.buf[jpegBuffer.bufPtr++] & 0xFF);
                    final JPEGBuffer jpegBuffer6 = jpegBuffer;
                    jpegBuffer6.bufAvail -= 2;
                    n -= 2;
                    jpegBuffer.skipData(n);
                    continue;
                }
            }
        }
        this.iis.reset();
        return this.numImages;
    }
    
    private void gotoImage(final int minIndex) throws IOException {
        if (this.iis == null) {
            throw new IllegalStateException("Input not set");
        }
        if (minIndex < this.minIndex) {
            throw new IndexOutOfBoundsException();
        }
        if (!this.tablesOnlyChecked) {
            this.checkTablesOnly();
        }
        if (this.imagePositions.isEmpty()) {
            throw new IIOException("No image data present to read");
        }
        if (minIndex < this.imagePositions.size()) {
            this.iis.seek(this.imagePositions.get(minIndex));
        }
        else {
            this.iis.seek(this.imagePositions.get(this.imagePositions.size() - 1));
            this.skipImage();
            for (int i = this.imagePositions.size(); i <= minIndex; ++i) {
                if (!this.hasNextImage()) {
                    throw new IndexOutOfBoundsException();
                }
                final Long n = new Long(this.iis.getStreamPosition());
                this.imagePositions.add(n);
                if (this.seekForwardOnly) {
                    this.iis.flushBefore(n);
                }
                if (i < minIndex) {
                    this.skipImage();
                }
            }
        }
        if (this.seekForwardOnly) {
            this.minIndex = minIndex;
        }
        this.haveSeeked = true;
    }
    
    private void skipImage() throws IOException {
        if (this.debug) {
            System.out.println("skipImage called");
        }
        boolean b = false;
        for (int i = this.iis.read(); i != -1; i = this.iis.read()) {
            if (b && i == 217) {
                return;
            }
            b = (i == 255);
        }
        throw new IndexOutOfBoundsException();
    }
    
    private boolean hasNextImage() throws IOException {
        if (this.debug) {
            System.out.print("hasNextImage called; returning ");
        }
        this.iis.mark();
        boolean b = false;
        for (int i = this.iis.read(); i != -1; i = this.iis.read()) {
            if (b && i == 216) {
                this.iis.reset();
                if (this.debug) {
                    System.out.println("true");
                }
                return true;
            }
            b = (i == 255);
        }
        this.iis.reset();
        if (this.debug) {
            System.out.println("false");
        }
        return false;
    }
    
    private void pushBack(final int n) throws IOException {
        if (this.debug) {
            System.out.println("pushing back " + n + " bytes");
        }
        this.cbLock.lock();
        try {
            this.iis.seek(this.iis.getStreamPosition() - n);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private void readHeader(final int currentImage, final boolean b) throws IOException {
        this.gotoImage(currentImage);
        this.readNativeHeader(b);
        this.currentImage = currentImage;
    }
    
    private boolean readNativeHeader(final boolean b) throws IOException {
        final boolean imageHeader = this.readImageHeader(this.structPointer, this.haveSeeked, b);
        this.haveSeeked = false;
        return imageHeader;
    }
    
    private native boolean readImageHeader(final long p0, final boolean p1, final boolean p2) throws IOException;
    
    private void setImageData(final int width, final int height, final int colorSpaceCode, final int outColorSpaceCode, final int numComponents, final byte[] array) {
        this.width = width;
        this.height = height;
        this.colorSpaceCode = colorSpaceCode;
        this.outColorSpaceCode = outColorSpaceCode;
        this.numComponents = numComponents;
        if (array == null) {
            this.iccCS = null;
            return;
        }
        ICC_Profile instance;
        try {
            instance = ICC_Profile.getInstance(array);
        }
        catch (final IllegalArgumentException ex) {
            this.iccCS = null;
            this.warningOccurred(2);
            return;
        }
        final byte[] data = instance.getData();
        ICC_Profile profile = null;
        if (this.iccCS instanceof ICC_ColorSpace) {
            profile = ((ICC_ColorSpace)this.iccCS).getProfile();
        }
        byte[] data2 = null;
        if (profile != null) {
            data2 = profile.getData();
        }
        if (data2 == null || !Arrays.equals(data2, data)) {
            this.iccCS = new ICC_ColorSpace(instance);
            try {
                this.iccCS.fromRGB(new float[] { 1.0f, 0.0f, 0.0f });
            }
            catch (final CMMException ex2) {
                this.iccCS = null;
                this.cbLock.lock();
                try {
                    this.warningOccurred(2);
                }
                finally {
                    this.cbLock.unlock();
                }
            }
        }
    }
    
    @Override
    public int getWidth(final int n) throws IOException {
        this.setThreadLock();
        try {
            if (this.currentImage != n) {
                this.cbLock.check();
                this.readHeader(n, true);
            }
            return this.width;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public int getHeight(final int n) throws IOException {
        this.setThreadLock();
        try {
            if (this.currentImage != n) {
                this.cbLock.check();
                this.readHeader(n, true);
            }
            return this.height;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private ImageTypeProducer getImageType(final int n) {
        ImageTypeProducer typeProducer = null;
        if (n > 0 && n < 6) {
            typeProducer = ImageTypeProducer.getTypeProducer(n);
        }
        return typeProducer;
    }
    
    @Override
    public ImageTypeSpecifier getRawImageType(final int n) throws IOException {
        this.setThreadLock();
        try {
            if (this.currentImage != n) {
                this.cbLock.check();
                this.readHeader(n, true);
            }
            return this.getImageType(this.colorSpaceCode).getType();
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public Iterator getImageTypes(final int n) throws IOException {
        this.setThreadLock();
        try {
            return this.getImageTypesOnThread(n);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private Iterator getImageTypesOnThread(final int n) throws IOException {
        if (this.currentImage != n) {
            this.cbLock.check();
            this.readHeader(n, true);
        }
        final ImageTypeProducer imageType = this.getImageType(this.colorSpaceCode);
        final ArrayList list = new ArrayList(1);
        switch (this.colorSpaceCode) {
            case 1: {
                list.add(imageType);
                list.add(this.getImageType(2));
                break;
            }
            case 2: {
                list.add(imageType);
                list.add(this.getImageType(1));
                break;
            }
            case 3: {
                list.add(this.getImageType(2));
                if (this.iccCS != null) {
                    list.add(new ImageTypeProducer() {
                        @Override
                        protected ImageTypeSpecifier produce() {
                            return ImageTypeSpecifier.createInterleaved(JPEGImageReader.this.iccCS, JPEG.bOffsRGB, 0, false, false);
                        }
                    });
                }
                list.add(this.getImageType(1));
                break;
            }
        }
        return new ImageTypeIterator(list.iterator());
    }
    
    private void checkColorConversion(final BufferedImage bufferedImage, final ImageReadParam imageReadParam) throws IIOException {
        if (imageReadParam != null && (imageReadParam.getSourceBands() != null || imageReadParam.getDestinationBands() != null)) {
            return;
        }
        final ColorModel colorModel = bufferedImage.getColorModel();
        if (colorModel instanceof IndexColorModel) {
            throw new IIOException("IndexColorModel not supported");
        }
        final ColorSpace colorSpace = colorModel.getColorSpace();
        final int type = colorSpace.getType();
        this.convert = null;
        switch (this.outColorSpaceCode) {
            case 1: {
                if (type == 5) {
                    this.setOutColorSpace(this.structPointer, 2);
                    this.outColorSpaceCode = 2;
                    this.numComponents = 3;
                    break;
                }
                if (type != 6) {
                    throw new IIOException("Incompatible color conversion");
                }
                break;
            }
            case 2: {
                if (type == 6) {
                    if (this.colorSpaceCode == 3) {
                        this.setOutColorSpace(this.structPointer, 1);
                        this.outColorSpaceCode = 1;
                        this.numComponents = 1;
                        break;
                    }
                    break;
                }
                else {
                    if (this.iccCS != null && colorModel.getNumComponents() == this.numComponents && colorSpace != this.iccCS) {
                        this.convert = new ColorConvertOp(this.iccCS, colorSpace, null);
                        break;
                    }
                    if (this.iccCS == null && !colorSpace.isCS_sRGB() && colorModel.getNumComponents() == this.numComponents) {
                        this.convert = new ColorConvertOp(JPEG.JCS.sRGB, colorSpace, null);
                        break;
                    }
                    if (type != 5) {
                        throw new IIOException("Incompatible color conversion");
                    }
                    break;
                }
                break;
            }
            default: {
                throw new IIOException("Incompatible color conversion");
            }
        }
    }
    
    private native void setOutColorSpace(final long p0, final int p1);
    
    @Override
    public ImageReadParam getDefaultReadParam() {
        return new JPEGImageReadParam();
    }
    
    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        this.setThreadLock();
        try {
            if (!this.tablesOnlyChecked) {
                this.cbLock.check();
                this.checkTablesOnly();
            }
            return this.streamMetadata;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public IIOMetadata getImageMetadata(final int imageMetadataIndex) throws IOException {
        this.setThreadLock();
        try {
            if (this.imageMetadataIndex == imageMetadataIndex && this.imageMetadata != null) {
                return this.imageMetadata;
            }
            this.cbLock.check();
            this.gotoImage(imageMetadataIndex);
            this.imageMetadata = new JPEGMetadata(false, false, this.iis, this);
            this.imageMetadataIndex = imageMetadataIndex;
            return this.imageMetadata;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public BufferedImage read(final int n, final ImageReadParam imageReadParam) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            try {
                this.readInternal(n, imageReadParam, false);
            }
            catch (final RuntimeException ex) {
                this.resetLibraryState(this.structPointer);
                throw ex;
            }
            catch (final IOException ex2) {
                this.resetLibraryState(this.structPointer);
                throw ex2;
            }
            final BufferedImage image = this.image;
            this.image = null;
            return image;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private Raster readInternal(final int n, final ImageReadParam imageReadParam, final boolean b) throws IOException {
        this.readHeader(n, false);
        WritableRaster raster = null;
        int numBands = 0;
        if (!b) {
            final Iterator imageTypes = this.getImageTypes(n);
            if (!imageTypes.hasNext()) {
                throw new IIOException("Unsupported Image Type");
            }
            this.image = ImageReader.getDestination(imageReadParam, imageTypes, this.width, this.height);
            raster = this.image.getRaster();
            numBands = this.image.getSampleModel().getNumBands();
            this.checkColorConversion(this.image, imageReadParam);
            ImageReader.checkReadParamBandSettings(imageReadParam, this.numComponents, numBands);
        }
        else {
            this.setOutColorSpace(this.structPointer, this.colorSpaceCode);
            this.image = null;
        }
        int[] array = JPEG.bandOffsets[this.numComponents - 1];
        int length = b ? this.numComponents : numBands;
        this.destinationBands = null;
        final Rectangle rectangle = new Rectangle(0, 0, 0, 0);
        this.destROI = new Rectangle(0, 0, 0, 0);
        ImageReader.computeRegions(imageReadParam, this.width, this.height, this.image, rectangle, this.destROI);
        int sourceXSubsampling = 1;
        int sourceYSubsampling = 1;
        this.minProgressivePass = 0;
        this.maxProgressivePass = Integer.MAX_VALUE;
        if (imageReadParam != null) {
            sourceXSubsampling = imageReadParam.getSourceXSubsampling();
            sourceYSubsampling = imageReadParam.getSourceYSubsampling();
            final int[] sourceBands = imageReadParam.getSourceBands();
            if (sourceBands != null) {
                array = sourceBands;
                length = array.length;
            }
            if (!b) {
                this.destinationBands = imageReadParam.getDestinationBands();
            }
            this.minProgressivePass = imageReadParam.getSourceMinProgressivePass();
            this.maxProgressivePass = imageReadParam.getSourceMaxProgressivePass();
            if (imageReadParam instanceof JPEGImageReadParam) {
                final JPEGImageReadParam jpegImageReadParam = (JPEGImageReadParam)imageReadParam;
                if (jpegImageReadParam.areTablesSet()) {
                    this.abbrevQTables = jpegImageReadParam.getQTables();
                    this.abbrevDCHuffmanTables = jpegImageReadParam.getDCHuffmanTables();
                    this.abbrevACHuffmanTables = jpegImageReadParam.getACHuffmanTables();
                }
            }
        }
        final int n2 = this.destROI.width * length;
        this.buffer = new DataBufferByte(n2);
        final int[] array2 = JPEG.bandOffsets[length - 1];
        this.raster = Raster.createInterleavedRaster(this.buffer, this.destROI.width, 1, n2, length, array2, null);
        if (b) {
            this.target = Raster.createInterleavedRaster(0, this.destROI.width, this.destROI.height, n2, length, array2, null);
        }
        else {
            this.target = raster;
        }
        final int[] sampleSize = this.target.getSampleModel().getSampleSize();
        for (int i = 0; i < sampleSize.length; ++i) {
            if (sampleSize[i] <= 0 || sampleSize[i] > 8) {
                throw new IIOException("Illegal band size: should be 0 < size <= 8");
            }
        }
        final boolean b2 = this.updateListeners != null || this.progressListeners != null;
        this.initProgressData();
        if (n == this.imageMetadataIndex) {
            this.knownPassCount = 0;
            final Iterator iterator = this.imageMetadata.markerSequence.iterator();
            while (iterator.hasNext()) {
                if (iterator.next() instanceof SOSMarkerSegment) {
                    ++this.knownPassCount;
                }
            }
        }
        this.progInterval = Math.max((this.target.getHeight() - 1) / 20, 1);
        if (this.knownPassCount > 0) {
            this.progInterval *= this.knownPassCount;
        }
        else if (this.maxProgressivePass != Integer.MAX_VALUE) {
            this.progInterval *= this.maxProgressivePass - this.minProgressivePass + 1;
        }
        if (this.debug) {
            System.out.println("**** Read Data *****");
            System.out.println("numRasterBands is " + length);
            System.out.print("srcBands:");
            for (int j = 0; j < array.length; ++j) {
                System.out.print(" " + array[j]);
            }
            System.out.println();
            System.out.println("destination bands is " + this.destinationBands);
            if (this.destinationBands != null) {
                for (int k = 0; k < this.destinationBands.length; ++k) {
                    System.out.print(" " + this.destinationBands[k]);
                }
                System.out.println();
            }
            System.out.println("sourceROI is " + rectangle);
            System.out.println("destROI is " + this.destROI);
            System.out.println("periodX is " + sourceXSubsampling);
            System.out.println("periodY is " + sourceYSubsampling);
            System.out.println("minProgressivePass is " + this.minProgressivePass);
            System.out.println("maxProgressivePass is " + this.maxProgressivePass);
            System.out.println("callbackUpdates is " + b2);
        }
        this.processImageStarted(this.currentImage);
        if (this.readImage(n, this.structPointer, this.buffer.getData(), length, array, sampleSize, rectangle.x, rectangle.y, rectangle.width, rectangle.height, sourceXSubsampling, sourceYSubsampling, this.abbrevQTables, this.abbrevDCHuffmanTables, this.abbrevACHuffmanTables, this.minProgressivePass, this.maxProgressivePass, b2)) {
            this.processReadAborted();
        }
        else {
            this.processImageComplete();
        }
        return this.target;
    }
    
    private void acceptPixels(final int n, final boolean b) {
        if (this.convert != null) {
            this.convert.filter(this.raster, this.raster);
        }
        this.target.setRect(this.destROI.x, this.destROI.y + n, this.raster);
        this.cbLock.lock();
        try {
            this.processImageUpdate(this.image, this.destROI.x, this.destROI.y + n, this.raster.getWidth(), 1, 1, 1, this.destinationBands);
            if (n > 0 && n % this.progInterval == 0) {
                final int n2 = this.target.getHeight() - 1;
                final float n3 = n / (float)n2;
                if (b) {
                    if (this.knownPassCount != -1) {
                        this.processImageProgress((this.pass + n3) * 100.0f / this.knownPassCount);
                    }
                    else if (this.maxProgressivePass != Integer.MAX_VALUE) {
                        this.processImageProgress((this.pass + n3) * 100.0f / (this.maxProgressivePass - this.minProgressivePass + 1));
                    }
                    else {
                        final int max = Math.max(2, 10 - this.pass);
                        final int n4 = this.pass + max - 1;
                        this.progInterval = Math.max(n2 / 20 * n4, n4);
                        if (n % this.progInterval == 0) {
                            this.percentToDate = this.previousPassPercentage + (1.0f - this.previousPassPercentage) * n3 / max;
                            if (this.debug) {
                                System.out.print("pass= " + this.pass);
                                System.out.print(", y= " + n);
                                System.out.print(", progInt= " + this.progInterval);
                                System.out.print(", % of pass: " + n3);
                                System.out.print(", rem. passes: " + max);
                                System.out.print(", prev%: " + this.previousPassPercentage);
                                System.out.print(", %ToDate: " + this.percentToDate);
                                System.out.print(" ");
                            }
                            this.processImageProgress(this.percentToDate * 100.0f);
                        }
                    }
                }
                else {
                    this.processImageProgress(n3 * 100.0f);
                }
            }
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private void initProgressData() {
        this.knownPassCount = -1;
        this.pass = 0;
        this.percentToDate = 0.0f;
        this.previousPassPercentage = 0.0f;
        this.progInterval = 0;
    }
    
    private void passStarted(final int pass) {
        this.cbLock.lock();
        try {
            this.pass = pass;
            this.previousPassPercentage = this.percentToDate;
            this.processPassStarted(this.image, pass, this.minProgressivePass, this.maxProgressivePass, 0, 0, 1, 1, this.destinationBands);
        }
        finally {
            this.cbLock.unlock();
        }
    }
    
    private void passComplete() {
        this.cbLock.lock();
        try {
            this.processPassComplete(this.image);
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
    
    private native boolean readImage(final int p0, final long p1, final byte[] p2, final int p3, final int[] p4, final int[] p5, final int p6, final int p7, final int p8, final int p9, final int p10, final int p11, final JPEGQTable[] p12, final JPEGHuffmanTable[] p13, final JPEGHuffmanTable[] p14, final int p15, final int p16, final boolean p17);
    
    @Override
    public void abort() {
        this.setThreadLock();
        try {
            super.abort();
            this.abortRead(this.structPointer);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private native void abortRead(final long p0);
    
    private native void resetLibraryState(final long p0);
    
    @Override
    public boolean canReadRaster() {
        return true;
    }
    
    @Override
    public Raster readRaster(final int n, final ImageReadParam imageReadParam) throws IOException {
        this.setThreadLock();
        Raster internal = null;
        try {
            this.cbLock.check();
            Point destinationOffset = null;
            if (imageReadParam != null) {
                destinationOffset = imageReadParam.getDestinationOffset();
                imageReadParam.setDestinationOffset(new Point(0, 0));
            }
            internal = this.readInternal(n, imageReadParam, true);
            if (destinationOffset != null) {
                this.target = this.target.createWritableTranslatedChild(destinationOffset.x, destinationOffset.y);
            }
        }
        catch (final RuntimeException ex) {
            this.resetLibraryState(this.structPointer);
            throw ex;
        }
        catch (final IOException ex2) {
            this.resetLibraryState(this.structPointer);
            throw ex2;
        }
        finally {
            this.clearThreadLock();
        }
        return internal;
    }
    
    @Override
    public boolean readerSupportsThumbnails() {
        return true;
    }
    
    @Override
    public int getNumThumbnails(final int n) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            this.getImageMetadata(n);
            final JFIFMarkerSegment jfifMarkerSegment = (JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true);
            int n2 = 0;
            if (jfifMarkerSegment != null) {
                n2 = ((jfifMarkerSegment.thumb != null) ? 1 : 0) + jfifMarkerSegment.extSegments.size();
            }
            return n2;
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public int getThumbnailWidth(final int n, final int n2) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (n2 < 0 || n2 >= this.getNumThumbnails(n)) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            return ((JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true)).getThumbnailWidth(n2);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public int getThumbnailHeight(final int n, final int n2) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (n2 < 0 || n2 >= this.getNumThumbnails(n)) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            return ((JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true)).getThumbnailHeight(n2);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    @Override
    public BufferedImage readThumbnail(final int n, final int n2) throws IOException {
        this.setThreadLock();
        try {
            this.cbLock.check();
            if (n2 < 0 || n2 >= this.getNumThumbnails(n)) {
                throw new IndexOutOfBoundsException("No such thumbnail");
            }
            return ((JFIFMarkerSegment)this.imageMetadata.findMarkerSegment(JFIFMarkerSegment.class, true)).getThumbnail(this.iis, n2, this);
        }
        finally {
            this.clearThreadLock();
        }
    }
    
    private void resetInternalState() {
        this.resetReader(this.structPointer);
        this.numImages = 0;
        this.imagePositions = new ArrayList();
        this.currentImage = -1;
        this.image = null;
        this.raster = null;
        this.target = null;
        this.buffer = null;
        this.destROI = null;
        this.destinationBands = null;
        this.streamMetadata = null;
        this.imageMetadata = null;
        this.imageMetadataIndex = -1;
        this.haveSeeked = false;
        this.tablesOnlyChecked = false;
        this.iccCS = null;
        this.initProgressData();
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
    
    private native void resetReader(final long p0);
    
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
    
    private static native void disposeReader(final long p0);
    
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
            throw new IllegalStateException("Attempt to clear thread lock  form wrong thread. Locked thread: " + this.theThread + "; current thread: " + currentThread);
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
        initReaderIDs(ImageInputStream.class, JPEGQTable.class, JPEGHuffmanTable.class);
    }
    
    private static class JPEGReaderDisposerRecord implements DisposerRecord
    {
        private long pData;
        
        public JPEGReaderDisposerRecord(final long pData) {
            this.pData = pData;
        }
        
        @Override
        public synchronized void dispose() {
            if (this.pData != 0L) {
                disposeReader(this.pData);
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
                throw new IllegalStateException("Access to the reader is not allowed");
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
