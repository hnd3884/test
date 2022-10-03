package com.sun.imageio.plugins.gif;

import com.sun.imageio.plugins.common.ReaderUtil;
import java.io.EOFException;
import java.nio.ByteOrder;
import java.io.IOException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageReadParam;
import java.util.Iterator;
import java.awt.image.SampleModel;
import java.awt.image.ColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.IndexColorModel;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOException;
import java.util.ArrayList;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;

public class GIFImageReader extends ImageReader
{
    ImageInputStream stream;
    boolean gotHeader;
    GIFStreamMetadata streamMetadata;
    int currIndex;
    GIFImageMetadata imageMetadata;
    List imageStartPosition;
    int imageMetadataLength;
    int numImages;
    byte[] block;
    int blockLength;
    int bitPos;
    int nextByte;
    int initCodeSize;
    int clearCode;
    int eofCode;
    int next32Bits;
    boolean lastBlockFound;
    BufferedImage theImage;
    WritableRaster theTile;
    int width;
    int height;
    int streamX;
    int streamY;
    int rowsDone;
    int interlacePass;
    private byte[] fallbackColorTable;
    static final int[] interlaceIncrement;
    static final int[] interlaceOffset;
    Rectangle sourceRegion;
    int sourceXSubsampling;
    int sourceYSubsampling;
    int sourceMinProgressivePass;
    int sourceMaxProgressivePass;
    Point destinationOffset;
    Rectangle destinationRegion;
    int updateMinY;
    int updateYStep;
    boolean decodeThisRow;
    int destY;
    byte[] rowBuf;
    private static byte[] defaultPalette;
    
    public GIFImageReader(final ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
        this.stream = null;
        this.gotHeader = false;
        this.streamMetadata = null;
        this.currIndex = -1;
        this.imageMetadata = null;
        this.imageStartPosition = new ArrayList();
        this.numImages = -1;
        this.block = new byte[255];
        this.blockLength = 0;
        this.bitPos = 0;
        this.nextByte = 0;
        this.next32Bits = 0;
        this.lastBlockFound = false;
        this.theImage = null;
        this.theTile = null;
        this.width = -1;
        this.height = -1;
        this.streamX = -1;
        this.streamY = -1;
        this.rowsDone = 0;
        this.interlacePass = 0;
        this.fallbackColorTable = null;
        this.decodeThisRow = true;
        this.destY = 0;
    }
    
    @Override
    public void setInput(final Object o, final boolean b, final boolean b2) {
        super.setInput(o, b, b2);
        if (o != null) {
            if (!(o instanceof ImageInputStream)) {
                throw new IllegalArgumentException("input not an ImageInputStream!");
            }
            this.stream = (ImageInputStream)o;
        }
        else {
            this.stream = null;
        }
        this.resetStreamSettings();
    }
    
    @Override
    public int getNumImages(final boolean b) throws IIOException {
        if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
        }
        if (this.seekForwardOnly && b) {
            throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
        }
        if (this.numImages > 0) {
            return this.numImages;
        }
        if (b) {
            this.numImages = this.locateImage(Integer.MAX_VALUE) + 1;
        }
        return this.numImages;
    }
    
    private void checkIndex(final int minIndex) {
        if (minIndex < this.minIndex) {
            throw new IndexOutOfBoundsException("imageIndex < minIndex!");
        }
        if (this.seekForwardOnly) {
            this.minIndex = minIndex;
        }
    }
    
    @Override
    public int getWidth(final int n) throws IIOException {
        this.checkIndex(n);
        if (this.locateImage(n) != n) {
            throw new IndexOutOfBoundsException();
        }
        this.readMetadata();
        return this.imageMetadata.imageWidth;
    }
    
    @Override
    public int getHeight(final int n) throws IIOException {
        this.checkIndex(n);
        if (this.locateImage(n) != n) {
            throw new IndexOutOfBoundsException();
        }
        this.readMetadata();
        return this.imageMetadata.imageHeight;
    }
    
    private ImageTypeSpecifier createIndexed(final byte[] array, final byte[] array2, final byte[] array3, final int n) {
        IndexColorModel indexColorModel;
        if (this.imageMetadata.transparentColorFlag) {
            indexColorModel = new IndexColorModel(n, array.length, array, array2, array3, Math.min(this.imageMetadata.transparentColorIndex, array.length - 1));
        }
        else {
            indexColorModel = new IndexColorModel(n, array.length, array, array2, array3);
        }
        SampleModel sampleModel;
        if (n == 8) {
            sampleModel = new PixelInterleavedSampleModel(0, 1, 1, 1, 1, new int[] { 0 });
        }
        else {
            sampleModel = new MultiPixelPackedSampleModel(0, 1, 1, n);
        }
        return new ImageTypeSpecifier(indexColorModel, sampleModel);
    }
    
    @Override
    public Iterator getImageTypes(final int n) throws IIOException {
        this.checkIndex(n);
        if (this.locateImage(n) != n) {
            throw new IndexOutOfBoundsException();
        }
        this.readMetadata();
        final ArrayList list = new ArrayList(1);
        byte[] array;
        if (this.imageMetadata.localColorTable != null) {
            array = this.imageMetadata.localColorTable;
            this.fallbackColorTable = this.imageMetadata.localColorTable;
        }
        else {
            array = this.streamMetadata.globalColorTable;
        }
        if (array == null) {
            if (this.fallbackColorTable == null) {
                this.processWarningOccurred("Use default color table.");
                this.fallbackColorTable = getDefaultPalette();
            }
            array = this.fallbackColorTable;
        }
        final int n2 = array.length / 3;
        int n3;
        if (n2 == 2) {
            n3 = 1;
        }
        else if (n2 == 4) {
            n3 = 2;
        }
        else if (n2 == 8 || n2 == 16) {
            n3 = 4;
        }
        else {
            n3 = 8;
        }
        final int n4 = 1 << n3;
        final byte[] array2 = new byte[n4];
        final byte[] array3 = new byte[n4];
        final byte[] array4 = new byte[n4];
        int n5 = 0;
        for (int i = 0; i < n2; ++i) {
            array2[i] = array[n5++];
            array3[i] = array[n5++];
            array4[i] = array[n5++];
        }
        list.add(this.createIndexed(array2, array3, array4, n3));
        return list.iterator();
    }
    
    @Override
    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }
    
    @Override
    public IIOMetadata getStreamMetadata() throws IIOException {
        this.readHeader();
        return this.streamMetadata;
    }
    
    @Override
    public IIOMetadata getImageMetadata(final int n) throws IIOException {
        this.checkIndex(n);
        if (this.locateImage(n) != n) {
            throw new IndexOutOfBoundsException("Bad image index!");
        }
        this.readMetadata();
        return this.imageMetadata;
    }
    
    private void initNext32Bits() {
        this.next32Bits = (this.block[0] & 0xFF);
        this.next32Bits |= (this.block[1] & 0xFF) << 8;
        this.next32Bits |= (this.block[2] & 0xFF) << 16;
        this.next32Bits |= this.block[3] << 24;
        this.nextByte = 4;
    }
    
    private int getCode(final int n, final int n2) throws IOException {
        if (this.bitPos + n > 32) {
            return this.eofCode;
        }
        final int n3 = this.next32Bits >> this.bitPos & n2;
        this.bitPos += n;
        while (this.bitPos >= 8 && !this.lastBlockFound) {
            this.next32Bits >>>= 8;
            this.bitPos -= 8;
            if (this.nextByte >= this.blockLength) {
                this.blockLength = this.stream.readUnsignedByte();
                if (this.blockLength == 0) {
                    this.lastBlockFound = true;
                    return n3;
                }
                int i = this.blockLength;
                int n4 = 0;
                while (i > 0) {
                    final int read = this.stream.read(this.block, n4, i);
                    n4 += read;
                    i -= read;
                }
                this.nextByte = 0;
            }
            this.next32Bits |= this.block[this.nextByte++] << 24;
        }
        return n3;
    }
    
    public void initializeStringTable(final int[] array, final byte[] array2, final byte[] array3, final int[] array4) {
        final int n = 1 << this.initCodeSize;
        for (int i = 0; i < n; ++i) {
            array[i] = -1;
            array2[i] = (byte)i;
            array3[i] = (byte)i;
            array4[i] = 1;
        }
        for (int j = n; j < 4096; ++j) {
            array[j] = -1;
            array4[j] = 1;
        }
    }
    
    private void outputRow() {
        final int min = Math.min(this.sourceRegion.width, this.destinationRegion.width * this.sourceXSubsampling);
        int x = this.destinationRegion.x;
        if (this.sourceXSubsampling == 1) {
            this.theTile.setDataElements(x, this.destY, min, 1, this.rowBuf);
        }
        else {
            for (int i = 0; i < min; i += this.sourceXSubsampling, ++x) {
                this.theTile.setSample(x, this.destY, 0, this.rowBuf[i] & 0xFF);
            }
        }
        if (this.updateListeners != null) {
            this.processImageUpdate(this.theImage, x, this.destY, min, 1, 1, this.updateYStep, new int[] { 0 });
        }
    }
    
    private void computeDecodeThisRow() {
        this.decodeThisRow = (this.destY < this.destinationRegion.y + this.destinationRegion.height && this.streamY >= this.sourceRegion.y && this.streamY < this.sourceRegion.y + this.sourceRegion.height && (this.streamY - this.sourceRegion.y) % this.sourceYSubsampling == 0);
    }
    
    private void outputPixels(final byte[] array, final int n) {
        if (this.interlacePass < this.sourceMinProgressivePass || this.interlacePass > this.sourceMaxProgressivePass) {
            return;
        }
        for (int i = 0; i < n; ++i) {
            if (this.streamX >= this.sourceRegion.x) {
                this.rowBuf[this.streamX - this.sourceRegion.x] = array[i];
            }
            ++this.streamX;
            if (this.streamX == this.width) {
                ++this.rowsDone;
                this.processImageProgress(100.0f * this.rowsDone / this.height);
                if (this.decodeThisRow) {
                    this.outputRow();
                }
                this.streamX = 0;
                if (this.imageMetadata.interlaceFlag) {
                    this.streamY += GIFImageReader.interlaceIncrement[this.interlacePass];
                    if (this.streamY >= this.height) {
                        if (this.updateListeners != null) {
                            this.processPassComplete(this.theImage);
                        }
                        ++this.interlacePass;
                        if (this.interlacePass > this.sourceMaxProgressivePass) {
                            return;
                        }
                        this.streamY = GIFImageReader.interlaceOffset[this.interlacePass];
                        this.startPass(this.interlacePass);
                    }
                }
                else {
                    ++this.streamY;
                }
                this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
                this.computeDecodeThisRow();
            }
        }
    }
    
    private void readHeader() throws IIOException {
        if (this.gotHeader) {
            return;
        }
        if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
        }
        this.streamMetadata = new GIFStreamMetadata();
        try {
            this.stream.setByteOrder(ByteOrder.LITTLE_ENDIAN);
            final byte[] array = new byte[6];
            this.stream.readFully(array);
            final StringBuffer sb = new StringBuffer(3);
            sb.append((char)array[3]);
            sb.append((char)array[4]);
            sb.append((char)array[5]);
            this.streamMetadata.version = sb.toString();
            this.streamMetadata.logicalScreenWidth = this.stream.readUnsignedShort();
            this.streamMetadata.logicalScreenHeight = this.stream.readUnsignedShort();
            final int unsignedByte = this.stream.readUnsignedByte();
            final boolean b = (unsignedByte & 0x80) != 0x0;
            this.streamMetadata.colorResolution = (unsignedByte >> 4 & 0x7) + 1;
            this.streamMetadata.sortFlag = ((unsignedByte & 0x8) != 0x0);
            final int n = 1 << (unsignedByte & 0x7) + 1;
            this.streamMetadata.backgroundColorIndex = this.stream.readUnsignedByte();
            this.streamMetadata.pixelAspectRatio = this.stream.readUnsignedByte();
            if (b) {
                this.streamMetadata.globalColorTable = new byte[3 * n];
                this.stream.readFully(this.streamMetadata.globalColorTable);
            }
            else {
                this.streamMetadata.globalColorTable = null;
            }
            this.imageStartPosition.add(this.stream.getStreamPosition());
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error reading header!", ex);
        }
        this.gotHeader = true;
    }
    
    private boolean skipImage() throws IIOException {
        try {
            while (true) {
                final int unsignedByte = this.stream.readUnsignedByte();
                if (unsignedByte == 44) {
                    this.stream.skipBytes(8);
                    final int unsignedByte2 = this.stream.readUnsignedByte();
                    if ((unsignedByte2 & 0x80) != 0x0) {
                        this.stream.skipBytes(3 * (1 << (unsignedByte2 & 0x7) + 1));
                    }
                    this.stream.skipBytes(1);
                    int i;
                    do {
                        i = this.stream.readUnsignedByte();
                        this.stream.skipBytes(i);
                    } while (i > 0);
                    return true;
                }
                if (unsignedByte == 59) {
                    return false;
                }
                if (unsignedByte == 33) {
                    this.stream.readUnsignedByte();
                    int j;
                    do {
                        j = this.stream.readUnsignedByte();
                        this.stream.skipBytes(j);
                    } while (j > 0);
                }
                else {
                    if (unsignedByte == 0) {
                        return false;
                    }
                    int k;
                    do {
                        k = this.stream.readUnsignedByte();
                        this.stream.skipBytes(k);
                    } while (k > 0);
                }
            }
        }
        catch (final EOFException ex) {
            return false;
        }
        catch (final IOException ex2) {
            throw new IIOException("I/O error locating image!", ex2);
        }
    }
    
    private int locateImage(final int currIndex) throws IIOException {
        this.readHeader();
        try {
            int i = Math.min(currIndex, this.imageStartPosition.size() - 1);
            this.stream.seek((long)this.imageStartPosition.get(i));
            while (i < currIndex) {
                if (!this.skipImage()) {
                    return --i;
                }
                this.imageStartPosition.add(new Long(this.stream.getStreamPosition()));
                ++i;
            }
        }
        catch (final IOException ex) {
            throw new IIOException("Couldn't seek!", ex);
        }
        if (this.currIndex != currIndex) {
            this.imageMetadata = null;
        }
        return this.currIndex = currIndex;
    }
    
    private byte[] concatenateBlocks() throws IOException {
        byte[] array = new byte[0];
        while (true) {
            final int unsignedByte = this.stream.readUnsignedByte();
            if (unsignedByte == 0) {
                break;
            }
            final byte[] array2 = new byte[array.length + unsignedByte];
            System.arraycopy(array, 0, array2, 0, array.length);
            this.stream.readFully(array2, array.length, unsignedByte);
            array = array2;
        }
        return array;
    }
    
    private void readMetadata() throws IIOException {
        if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
        }
        try {
            this.imageMetadata = new GIFImageMetadata();
            final long streamPosition = this.stream.getStreamPosition();
            while (true) {
                final int unsignedByte = this.stream.readUnsignedByte();
                if (unsignedByte == 44) {
                    this.imageMetadata.imageLeftPosition = this.stream.readUnsignedShort();
                    this.imageMetadata.imageTopPosition = this.stream.readUnsignedShort();
                    this.imageMetadata.imageWidth = this.stream.readUnsignedShort();
                    this.imageMetadata.imageHeight = this.stream.readUnsignedShort();
                    final int unsignedByte2 = this.stream.readUnsignedByte();
                    final boolean b = (unsignedByte2 & 0x80) != 0x0;
                    this.imageMetadata.interlaceFlag = ((unsignedByte2 & 0x40) != 0x0);
                    this.imageMetadata.sortFlag = ((unsignedByte2 & 0x20) != 0x0);
                    final int n = 1 << (unsignedByte2 & 0x7) + 1;
                    if (b) {
                        this.imageMetadata.localColorTable = new byte[3 * n];
                        this.stream.readFully(this.imageMetadata.localColorTable);
                    }
                    else {
                        this.imageMetadata.localColorTable = null;
                    }
                    this.imageMetadataLength = (int)(this.stream.getStreamPosition() - streamPosition);
                    return;
                }
                if (unsignedByte == 33) {
                    final int unsignedByte3 = this.stream.readUnsignedByte();
                    if (unsignedByte3 == 249) {
                        this.stream.readUnsignedByte();
                        final int unsignedByte4 = this.stream.readUnsignedByte();
                        this.imageMetadata.disposalMethod = (unsignedByte4 >> 2 & 0x3);
                        this.imageMetadata.userInputFlag = ((unsignedByte4 & 0x2) != 0x0);
                        this.imageMetadata.transparentColorFlag = ((unsignedByte4 & 0x1) != 0x0);
                        this.imageMetadata.delayTime = this.stream.readUnsignedShort();
                        this.imageMetadata.transparentColorIndex = this.stream.readUnsignedByte();
                        this.stream.readUnsignedByte();
                    }
                    else if (unsignedByte3 == 1) {
                        this.stream.readUnsignedByte();
                        this.imageMetadata.hasPlainTextExtension = true;
                        this.imageMetadata.textGridLeft = this.stream.readUnsignedShort();
                        this.imageMetadata.textGridTop = this.stream.readUnsignedShort();
                        this.imageMetadata.textGridWidth = this.stream.readUnsignedShort();
                        this.imageMetadata.textGridHeight = this.stream.readUnsignedShort();
                        this.imageMetadata.characterCellWidth = this.stream.readUnsignedByte();
                        this.imageMetadata.characterCellHeight = this.stream.readUnsignedByte();
                        this.imageMetadata.textForegroundColor = this.stream.readUnsignedByte();
                        this.imageMetadata.textBackgroundColor = this.stream.readUnsignedByte();
                        this.imageMetadata.text = this.concatenateBlocks();
                    }
                    else if (unsignedByte3 == 254) {
                        final byte[] concatenateBlocks = this.concatenateBlocks();
                        if (this.imageMetadata.comments == null) {
                            this.imageMetadata.comments = new ArrayList();
                        }
                        this.imageMetadata.comments.add(concatenateBlocks);
                    }
                    else if (unsignedByte3 == 255) {
                        final int unsignedByte5 = this.stream.readUnsignedByte();
                        final byte[] array = new byte[8];
                        final byte[] array2 = new byte[3];
                        final byte[] array3 = new byte[unsignedByte5];
                        this.stream.readFully(array3);
                        final int copyData = this.copyData(array3, this.copyData(array3, 0, array), array2);
                        byte[] concatenateBlocks2 = this.concatenateBlocks();
                        if (copyData < unsignedByte5) {
                            final int n2 = unsignedByte5 - copyData;
                            final byte[] array4 = new byte[n2 + concatenateBlocks2.length];
                            System.arraycopy(array3, copyData, array4, 0, n2);
                            System.arraycopy(concatenateBlocks2, 0, array4, n2, concatenateBlocks2.length);
                            concatenateBlocks2 = array4;
                        }
                        if (this.imageMetadata.applicationIDs == null) {
                            this.imageMetadata.applicationIDs = new ArrayList();
                            this.imageMetadata.authenticationCodes = new ArrayList();
                            this.imageMetadata.applicationData = new ArrayList();
                        }
                        this.imageMetadata.applicationIDs.add(array);
                        this.imageMetadata.authenticationCodes.add(array2);
                        this.imageMetadata.applicationData.add(concatenateBlocks2);
                    }
                    else {
                        int i;
                        do {
                            i = this.stream.readUnsignedByte();
                            this.stream.skipBytes(i);
                        } while (i > 0);
                    }
                }
                else {
                    if (unsignedByte == 59) {
                        throw new IndexOutOfBoundsException("Attempt to read past end of image sequence!");
                    }
                    throw new IIOException("Unexpected block type " + unsignedByte + "!");
                }
            }
        }
        catch (final IIOException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw new IIOException("I/O error reading image metadata!", ex2);
        }
    }
    
    private int copyData(final byte[] array, final int n, final byte[] array2) {
        int length = array2.length;
        final int n2 = array.length - n;
        if (length > n2) {
            length = n2;
        }
        System.arraycopy(array, n, array2, 0, length);
        return n + length;
    }
    
    private void startPass(final int n) {
        if (this.updateListeners == null || !this.imageMetadata.interlaceFlag) {
            return;
        }
        final int n2 = GIFImageReader.interlaceOffset[this.interlacePass];
        final int n3 = GIFImageReader.interlaceIncrement[this.interlacePass];
        final int[] computeUpdatedPixels = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, this.destinationRegion.x, this.destinationRegion.y, this.destinationRegion.x + this.destinationRegion.width - 1, this.destinationRegion.y + this.destinationRegion.height - 1, this.sourceXSubsampling, this.sourceYSubsampling, 0, n2, this.destinationRegion.width, (this.destinationRegion.height + n3 - 1) / n3, 1, n3);
        this.updateMinY = computeUpdatedPixels[1];
        this.updateYStep = computeUpdatedPixels[5];
        this.processPassStarted(this.theImage, this.interlacePass, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, 0, this.updateMinY, 1, this.updateYStep, new int[] { 0 });
    }
    
    @Override
    public BufferedImage read(final int n, ImageReadParam defaultReadParam) throws IIOException {
        if (this.stream == null) {
            throw new IllegalStateException("Input not set!");
        }
        this.checkIndex(n);
        if (this.locateImage(n) != n) {
            throw new IndexOutOfBoundsException("imageIndex out of bounds!");
        }
        this.clearAbortRequest();
        this.readMetadata();
        if (defaultReadParam == null) {
            defaultReadParam = this.getDefaultReadParam();
        }
        this.theImage = ImageReader.getDestination(defaultReadParam, this.getImageTypes(n), this.imageMetadata.imageWidth, this.imageMetadata.imageHeight);
        this.theTile = this.theImage.getWritableTile(0, 0);
        this.width = this.imageMetadata.imageWidth;
        this.height = this.imageMetadata.imageHeight;
        this.streamX = 0;
        this.streamY = 0;
        this.rowsDone = 0;
        this.interlacePass = 0;
        this.sourceRegion = new Rectangle(0, 0, 0, 0);
        this.destinationRegion = new Rectangle(0, 0, 0, 0);
        ImageReader.computeRegions(defaultReadParam, this.width, this.height, this.theImage, this.sourceRegion, this.destinationRegion);
        this.destinationOffset = new Point(this.destinationRegion.x, this.destinationRegion.y);
        this.sourceXSubsampling = defaultReadParam.getSourceXSubsampling();
        this.sourceYSubsampling = defaultReadParam.getSourceYSubsampling();
        this.sourceMinProgressivePass = Math.max(defaultReadParam.getSourceMinProgressivePass(), 0);
        this.sourceMaxProgressivePass = Math.min(defaultReadParam.getSourceMaxProgressivePass(), 3);
        this.destY = this.destinationRegion.y + (this.streamY - this.sourceRegion.y) / this.sourceYSubsampling;
        this.computeDecodeThisRow();
        this.processImageStarted(n);
        this.startPass(0);
        this.rowBuf = new byte[this.width];
        try {
            this.initCodeSize = this.stream.readUnsignedByte();
            if (this.initCodeSize < 1 || this.initCodeSize > 8) {
                throw new IIOException("Bad code size:" + this.initCodeSize);
            }
            this.blockLength = this.stream.readUnsignedByte();
            int read;
            for (int i = this.blockLength, n2 = 0; i > 0; i -= read, n2 += read) {
                read = this.stream.read(this.block, n2, i);
            }
            this.bitPos = 0;
            this.nextByte = 0;
            this.lastBlockFound = false;
            this.interlacePass = 0;
            this.initNext32Bits();
            this.clearCode = 1 << this.initCodeSize;
            this.eofCode = this.clearCode + 1;
            int n3 = 0;
            final int[] array = new int[4096];
            final byte[] array2 = new byte[4096];
            final byte[] array3 = new byte[4096];
            final int[] array4 = new int[4096];
            final byte[] array5 = new byte[4096];
            this.initializeStringTable(array, array2, array3, array4);
            int n4 = (1 << this.initCodeSize) + 2;
            int n5 = this.initCodeSize + 1;
            int n6 = (1 << n5) - 1;
            while (!this.abortRequested()) {
                int n7 = this.getCode(n5, n6);
                if (n7 == this.clearCode) {
                    this.initializeStringTable(array, array2, array3, array4);
                    n4 = (1 << this.initCodeSize) + 2;
                    n5 = this.initCodeSize + 1;
                    n6 = (1 << n5) - 1;
                    n7 = this.getCode(n5, n6);
                    if (n7 == this.eofCode) {
                        this.processImageComplete();
                        return this.theImage;
                    }
                }
                else {
                    if (n7 == this.eofCode) {
                        this.processImageComplete();
                        return this.theImage;
                    }
                    int n8;
                    if (n7 < n4) {
                        n8 = n7;
                    }
                    else {
                        n8 = n3;
                        if (n7 != n4) {
                            this.processWarningOccurred("Out-of-sequence code!");
                        }
                    }
                    final int n9 = n4;
                    final int n10 = n3;
                    array[n9] = n10;
                    array2[n9] = array3[n8];
                    array3[n9] = array3[n10];
                    array4[n9] = array4[n10] + 1;
                    if (++n4 == 1 << n5 && n4 < 4096) {
                        ++n5;
                        n6 = (1 << n5) - 1;
                    }
                }
                int n11 = n7;
                final int n12 = array4[n11];
                for (int j = n12 - 1; j >= 0; --j) {
                    array5[j] = array2[n11];
                    n11 = array[n11];
                }
                this.outputPixels(array5, n12);
                n3 = n7;
            }
            this.processReadAborted();
            return this.theImage;
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error reading image!", ex);
        }
    }
    
    @Override
    public void reset() {
        super.reset();
        this.resetStreamSettings();
    }
    
    private void resetStreamSettings() {
        this.gotHeader = false;
        this.streamMetadata = null;
        this.currIndex = -1;
        this.imageMetadata = null;
        this.imageStartPosition = new ArrayList();
        this.numImages = -1;
        this.blockLength = 0;
        this.bitPos = 0;
        this.nextByte = 0;
        this.next32Bits = 0;
        this.lastBlockFound = false;
        this.theImage = null;
        this.theTile = null;
        this.width = -1;
        this.height = -1;
        this.streamX = -1;
        this.streamY = -1;
        this.rowsDone = 0;
        this.interlacePass = 0;
        this.fallbackColorTable = null;
    }
    
    private static synchronized byte[] getDefaultPalette() {
        if (GIFImageReader.defaultPalette == null) {
            final IndexColorModel indexColorModel = (IndexColorModel)new BufferedImage(1, 1, 13).getColorModel();
            final int mapSize = indexColorModel.getMapSize();
            final byte[] array = new byte[mapSize];
            final byte[] array2 = new byte[mapSize];
            final byte[] array3 = new byte[mapSize];
            indexColorModel.getReds(array);
            indexColorModel.getGreens(array2);
            indexColorModel.getBlues(array3);
            GIFImageReader.defaultPalette = new byte[mapSize * 3];
            for (int i = 0; i < mapSize; ++i) {
                GIFImageReader.defaultPalette[3 * i + 0] = array[i];
                GIFImageReader.defaultPalette[3 * i + 1] = array2[i];
                GIFImageReader.defaultPalette[3 * i + 2] = array3[i];
            }
        }
        return GIFImageReader.defaultPalette;
    }
    
    static {
        interlaceIncrement = new int[] { 8, 8, 4, 2, -1 };
        interlaceOffset = new int[] { 0, 4, 2, 1, -1 };
        GIFImageReader.defaultPalette = null;
    }
}
