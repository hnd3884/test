package com.sun.imageio.plugins.png;

import javax.imageio.metadata.IIOMetadata;
import java.util.Arrays;
import java.awt.color.ColorSpace;
import java.util.ArrayList;
import javax.imageio.ImageTypeSpecifier;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.util.zip.Inflater;
import java.util.Enumeration;
import java.io.SequenceInputStream;
import java.util.zip.ZipException;
import sun.awt.image.ByteInterleavedRaster;
import com.sun.imageio.plugins.common.ReaderUtil;
import java.awt.image.DataBufferUShort;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import java.io.ByteArrayInputStream;
import javax.imageio.IIOException;
import java.io.IOException;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;
import javax.imageio.spi.ImageReaderSpi;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.awt.Point;
import java.awt.Rectangle;
import javax.imageio.ImageReadParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.ImageReader;

public class PNGImageReader extends ImageReader
{
    static final int IHDR_TYPE = 1229472850;
    static final int PLTE_TYPE = 1347179589;
    static final int IDAT_TYPE = 1229209940;
    static final int IEND_TYPE = 1229278788;
    static final int bKGD_TYPE = 1649100612;
    static final int cHRM_TYPE = 1665684045;
    static final int gAMA_TYPE = 1732332865;
    static final int hIST_TYPE = 1749635924;
    static final int iCCP_TYPE = 1766015824;
    static final int iTXt_TYPE = 1767135348;
    static final int pHYs_TYPE = 1883789683;
    static final int sBIT_TYPE = 1933723988;
    static final int sPLT_TYPE = 1934642260;
    static final int sRGB_TYPE = 1934772034;
    static final int tEXt_TYPE = 1950701684;
    static final int tIME_TYPE = 1950960965;
    static final int tRNS_TYPE = 1951551059;
    static final int zTXt_TYPE = 2052348020;
    static final int PNG_COLOR_GRAY = 0;
    static final int PNG_COLOR_RGB = 2;
    static final int PNG_COLOR_PALETTE = 3;
    static final int PNG_COLOR_GRAY_ALPHA = 4;
    static final int PNG_COLOR_RGB_ALPHA = 6;
    static final int[] inputBandsForColorType;
    static final int PNG_FILTER_NONE = 0;
    static final int PNG_FILTER_SUB = 1;
    static final int PNG_FILTER_UP = 2;
    static final int PNG_FILTER_AVERAGE = 3;
    static final int PNG_FILTER_PAETH = 4;
    static final int[] adam7XOffset;
    static final int[] adam7YOffset;
    static final int[] adam7XSubsampling;
    static final int[] adam7YSubsampling;
    private static final boolean debug = true;
    ImageInputStream stream;
    boolean gotHeader;
    boolean gotMetadata;
    ImageReadParam lastParam;
    long imageStartPosition;
    Rectangle sourceRegion;
    int sourceXSubsampling;
    int sourceYSubsampling;
    int sourceMinProgressivePass;
    int sourceMaxProgressivePass;
    int[] sourceBands;
    int[] destinationBands;
    Point destinationOffset;
    PNGMetadata metadata;
    DataInputStream pixelStream;
    BufferedImage theImage;
    int pixelsDone;
    int totalPixels;
    private static final int[][] bandOffsets;
    
    public PNGImageReader(final ImageReaderSpi imageReaderSpi) {
        super(imageReaderSpi);
        this.stream = null;
        this.gotHeader = false;
        this.gotMetadata = false;
        this.lastParam = null;
        this.imageStartPosition = -1L;
        this.sourceRegion = null;
        this.sourceXSubsampling = -1;
        this.sourceYSubsampling = -1;
        this.sourceMinProgressivePass = 0;
        this.sourceMaxProgressivePass = 6;
        this.sourceBands = null;
        this.destinationBands = null;
        this.destinationOffset = new Point(0, 0);
        this.metadata = new PNGMetadata();
        this.pixelStream = null;
        this.theImage = null;
        this.pixelsDone = 0;
    }
    
    @Override
    public void setInput(final Object o, final boolean b, final boolean b2) {
        super.setInput(o, b, b2);
        this.stream = (ImageInputStream)o;
        this.resetStreamSettings();
    }
    
    private String readNullTerminatedString(final String s, final int n) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int n2 = 0;
        int read;
        while (n > n2++ && (read = this.stream.read()) != 0) {
            if (read == -1) {
                throw new EOFException();
            }
            byteArrayOutputStream.write(read);
        }
        return new String(byteArrayOutputStream.toByteArray(), s);
    }
    
    private void readHeader() throws IIOException {
        if (this.gotHeader) {
            return;
        }
        if (this.stream == null) {
            throw new IllegalStateException("Input source not set!");
        }
        try {
            final byte[] array = new byte[8];
            this.stream.readFully(array);
            if (array[0] != -119 || array[1] != 80 || array[2] != 78 || array[3] != 71 || array[4] != 13 || array[5] != 10 || array[6] != 26 || array[7] != 10) {
                throw new IIOException("Bad PNG signature!");
            }
            if (this.stream.readInt() != 13) {
                throw new IIOException("Bad length for IHDR chunk!");
            }
            if (this.stream.readInt() != 1229472850) {
                throw new IIOException("Bad type for IHDR chunk!");
            }
            this.metadata = new PNGMetadata();
            final int int1 = this.stream.readInt();
            final int int2 = this.stream.readInt();
            this.stream.readFully(array, 0, 5);
            final int ihdr_bitDepth = array[0] & 0xFF;
            final int ihdr_colorType = array[1] & 0xFF;
            final int ihdr_compressionMethod = array[2] & 0xFF;
            final int ihdr_filterMethod = array[3] & 0xFF;
            final int ihdr_interlaceMethod = array[4] & 0xFF;
            this.stream.skipBytes(4);
            this.stream.flushBefore(this.stream.getStreamPosition());
            if (int1 == 0) {
                throw new IIOException("Image width == 0!");
            }
            if (int2 == 0) {
                throw new IIOException("Image height == 0!");
            }
            if (ihdr_bitDepth != 1 && ihdr_bitDepth != 2 && ihdr_bitDepth != 4 && ihdr_bitDepth != 8 && ihdr_bitDepth != 16) {
                throw new IIOException("Bit depth must be 1, 2, 4, 8, or 16!");
            }
            if (ihdr_colorType != 0 && ihdr_colorType != 2 && ihdr_colorType != 3 && ihdr_colorType != 4 && ihdr_colorType != 6) {
                throw new IIOException("Color type must be 0, 2, 3, 4, or 6!");
            }
            if (ihdr_colorType == 3 && ihdr_bitDepth == 16) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if ((ihdr_colorType == 2 || ihdr_colorType == 6 || ihdr_colorType == 4) && ihdr_bitDepth != 8 && ihdr_bitDepth != 16) {
                throw new IIOException("Bad color type/bit depth combination!");
            }
            if (ihdr_compressionMethod != 0) {
                throw new IIOException("Unknown compression method (not 0)!");
            }
            if (ihdr_filterMethod != 0) {
                throw new IIOException("Unknown filter method (not 0)!");
            }
            if (ihdr_interlaceMethod != 0 && ihdr_interlaceMethod != 1) {
                throw new IIOException("Unknown interlace method (not 0 or 1)!");
            }
            this.metadata.IHDR_present = true;
            this.metadata.IHDR_width = int1;
            this.metadata.IHDR_height = int2;
            this.metadata.IHDR_bitDepth = ihdr_bitDepth;
            this.metadata.IHDR_colorType = ihdr_colorType;
            this.metadata.IHDR_compressionMethod = ihdr_compressionMethod;
            this.metadata.IHDR_filterMethod = ihdr_filterMethod;
            this.metadata.IHDR_interlaceMethod = ihdr_interlaceMethod;
            this.gotHeader = true;
        }
        catch (final IOException ex) {
            throw new IIOException("I/O error reading PNG header!", ex);
        }
    }
    
    private void parse_PLTE_chunk(final int n) throws IOException {
        if (this.metadata.PLTE_present) {
            this.processWarningOccurred("A PNG image may not contain more than one PLTE chunk.\nThe chunk wil be ignored.");
            return;
        }
        if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
            this.processWarningOccurred("A PNG gray or gray alpha image cannot have a PLTE chunk.\nThe chunk wil be ignored.");
            return;
        }
        final byte[] array = new byte[n];
        this.stream.readFully(array);
        int min = n / 3;
        if (this.metadata.IHDR_colorType == 3) {
            final int n2 = 1 << this.metadata.IHDR_bitDepth;
            if (min > n2) {
                this.processWarningOccurred("PLTE chunk contains too many entries for bit depth, ignoring extras.");
                min = n2;
            }
            min = Math.min(min, n2);
        }
        int n3;
        if (min > 16) {
            n3 = 256;
        }
        else if (min > 4) {
            n3 = 16;
        }
        else if (min > 2) {
            n3 = 4;
        }
        else {
            n3 = 2;
        }
        this.metadata.PLTE_present = true;
        this.metadata.PLTE_red = new byte[n3];
        this.metadata.PLTE_green = new byte[n3];
        this.metadata.PLTE_blue = new byte[n3];
        int n4 = 0;
        for (int i = 0; i < min; ++i) {
            this.metadata.PLTE_red[i] = array[n4++];
            this.metadata.PLTE_green[i] = array[n4++];
            this.metadata.PLTE_blue[i] = array[n4++];
        }
    }
    
    private void parse_bKGD_chunk() throws IOException {
        if (this.metadata.IHDR_colorType == 3) {
            this.metadata.bKGD_colorType = 3;
            this.metadata.bKGD_index = this.stream.readUnsignedByte();
        }
        else if (this.metadata.IHDR_colorType == 0 || this.metadata.IHDR_colorType == 4) {
            this.metadata.bKGD_colorType = 0;
            this.metadata.bKGD_gray = this.stream.readUnsignedShort();
        }
        else {
            this.metadata.bKGD_colorType = 2;
            this.metadata.bKGD_red = this.stream.readUnsignedShort();
            this.metadata.bKGD_green = this.stream.readUnsignedShort();
            this.metadata.bKGD_blue = this.stream.readUnsignedShort();
        }
        this.metadata.bKGD_present = true;
    }
    
    private void parse_cHRM_chunk() throws IOException {
        this.metadata.cHRM_whitePointX = this.stream.readInt();
        this.metadata.cHRM_whitePointY = this.stream.readInt();
        this.metadata.cHRM_redX = this.stream.readInt();
        this.metadata.cHRM_redY = this.stream.readInt();
        this.metadata.cHRM_greenX = this.stream.readInt();
        this.metadata.cHRM_greenY = this.stream.readInt();
        this.metadata.cHRM_blueX = this.stream.readInt();
        this.metadata.cHRM_blueY = this.stream.readInt();
        this.metadata.cHRM_present = true;
    }
    
    private void parse_gAMA_chunk() throws IOException {
        this.metadata.gAMA_gamma = this.stream.readInt();
        this.metadata.gAMA_present = true;
    }
    
    private void parse_hIST_chunk(final int n) throws IOException, IIOException {
        if (!this.metadata.PLTE_present) {
            throw new IIOException("hIST chunk without prior PLTE chunk!");
        }
        this.metadata.hIST_histogram = new char[n / 2];
        this.stream.readFully(this.metadata.hIST_histogram, 0, this.metadata.hIST_histogram.length);
        this.metadata.hIST_present = true;
    }
    
    private void parse_iCCP_chunk(final int n) throws IOException {
        final String nullTerminatedString = this.readNullTerminatedString("ISO-8859-1", 80);
        this.metadata.iCCP_profileName = nullTerminatedString;
        this.metadata.iCCP_compressionMethod = this.stream.readUnsignedByte();
        final byte[] iccp_compressedProfile = new byte[n - nullTerminatedString.length() - 2];
        this.stream.readFully(iccp_compressedProfile);
        this.metadata.iCCP_compressedProfile = iccp_compressedProfile;
        this.metadata.iCCP_present = true;
    }
    
    private void parse_iTXt_chunk(final int n) throws IOException {
        final long streamPosition = this.stream.getStreamPosition();
        this.metadata.iTXt_keyword.add(this.readNullTerminatedString("ISO-8859-1", 80));
        final int unsignedByte = this.stream.readUnsignedByte();
        this.metadata.iTXt_compressionFlag.add(unsignedByte == 1);
        this.metadata.iTXt_compressionMethod.add(this.stream.readUnsignedByte());
        this.metadata.iTXt_languageTag.add(this.readNullTerminatedString("UTF8", 80));
        this.metadata.iTXt_translatedKeyword.add(this.readNullTerminatedString("UTF8", (int)(streamPosition + n - this.stream.getStreamPosition())));
        final byte[] array = new byte[(int)(streamPosition + n - this.stream.getStreamPosition())];
        this.stream.readFully(array);
        String s;
        if (unsignedByte == 1) {
            s = new String(inflate(array), "UTF8");
        }
        else {
            s = new String(array, "UTF8");
        }
        this.metadata.iTXt_text.add(s);
    }
    
    private void parse_pHYs_chunk() throws IOException {
        this.metadata.pHYs_pixelsPerUnitXAxis = this.stream.readInt();
        this.metadata.pHYs_pixelsPerUnitYAxis = this.stream.readInt();
        this.metadata.pHYs_unitSpecifier = this.stream.readUnsignedByte();
        this.metadata.pHYs_present = true;
    }
    
    private void parse_sBIT_chunk() throws IOException {
        final int ihdr_colorType = this.metadata.IHDR_colorType;
        if (ihdr_colorType == 0 || ihdr_colorType == 4) {
            this.metadata.sBIT_grayBits = this.stream.readUnsignedByte();
        }
        else if (ihdr_colorType == 2 || ihdr_colorType == 3 || ihdr_colorType == 6) {
            this.metadata.sBIT_redBits = this.stream.readUnsignedByte();
            this.metadata.sBIT_greenBits = this.stream.readUnsignedByte();
            this.metadata.sBIT_blueBits = this.stream.readUnsignedByte();
        }
        if (ihdr_colorType == 4 || ihdr_colorType == 6) {
            this.metadata.sBIT_alphaBits = this.stream.readUnsignedByte();
        }
        this.metadata.sBIT_colorType = ihdr_colorType;
        this.metadata.sBIT_present = true;
    }
    
    private void parse_sPLT_chunk(int n) throws IOException, IIOException {
        this.metadata.sPLT_paletteName = this.readNullTerminatedString("ISO-8859-1", 80);
        n -= this.metadata.sPLT_paletteName.length() + 1;
        final int unsignedByte = this.stream.readUnsignedByte();
        this.metadata.sPLT_sampleDepth = unsignedByte;
        final int n2 = n / (4 * (unsignedByte / 8) + 2);
        this.metadata.sPLT_red = new int[n2];
        this.metadata.sPLT_green = new int[n2];
        this.metadata.sPLT_blue = new int[n2];
        this.metadata.sPLT_alpha = new int[n2];
        this.metadata.sPLT_frequency = new int[n2];
        if (unsignedByte == 8) {
            for (int i = 0; i < n2; ++i) {
                this.metadata.sPLT_red[i] = this.stream.readUnsignedByte();
                this.metadata.sPLT_green[i] = this.stream.readUnsignedByte();
                this.metadata.sPLT_blue[i] = this.stream.readUnsignedByte();
                this.metadata.sPLT_alpha[i] = this.stream.readUnsignedByte();
                this.metadata.sPLT_frequency[i] = this.stream.readUnsignedShort();
            }
        }
        else {
            if (unsignedByte != 16) {
                throw new IIOException("sPLT sample depth not 8 or 16!");
            }
            for (int j = 0; j < n2; ++j) {
                this.metadata.sPLT_red[j] = this.stream.readUnsignedShort();
                this.metadata.sPLT_green[j] = this.stream.readUnsignedShort();
                this.metadata.sPLT_blue[j] = this.stream.readUnsignedShort();
                this.metadata.sPLT_alpha[j] = this.stream.readUnsignedShort();
                this.metadata.sPLT_frequency[j] = this.stream.readUnsignedShort();
            }
        }
        this.metadata.sPLT_present = true;
    }
    
    private void parse_sRGB_chunk() throws IOException {
        this.metadata.sRGB_renderingIntent = this.stream.readUnsignedByte();
        this.metadata.sRGB_present = true;
    }
    
    private void parse_tEXt_chunk(final int n) throws IOException {
        final String nullTerminatedString = this.readNullTerminatedString("ISO-8859-1", 80);
        this.metadata.tEXt_keyword.add(nullTerminatedString);
        final byte[] array = new byte[n - nullTerminatedString.length() - 1];
        this.stream.readFully(array);
        this.metadata.tEXt_text.add(new String(array, "ISO-8859-1"));
    }
    
    private void parse_tIME_chunk() throws IOException {
        this.metadata.tIME_year = this.stream.readUnsignedShort();
        this.metadata.tIME_month = this.stream.readUnsignedByte();
        this.metadata.tIME_day = this.stream.readUnsignedByte();
        this.metadata.tIME_hour = this.stream.readUnsignedByte();
        this.metadata.tIME_minute = this.stream.readUnsignedByte();
        this.metadata.tIME_second = this.stream.readUnsignedByte();
        this.metadata.tIME_present = true;
    }
    
    private void parse_tRNS_chunk(final int n) throws IOException {
        final int ihdr_colorType = this.metadata.IHDR_colorType;
        if (ihdr_colorType == 3) {
            if (!this.metadata.PLTE_present) {
                this.processWarningOccurred("tRNS chunk without prior PLTE chunk, ignoring it.");
                return;
            }
            final int length = this.metadata.PLTE_red.length;
            int n2 = n;
            if (n2 > length) {
                this.processWarningOccurred("tRNS chunk has more entries than prior PLTE chunk, ignoring extras.");
                n2 = length;
            }
            this.metadata.tRNS_alpha = new byte[n2];
            this.metadata.tRNS_colorType = 3;
            this.stream.read(this.metadata.tRNS_alpha, 0, n2);
            this.stream.skipBytes(n - n2);
        }
        else if (ihdr_colorType == 0) {
            if (n != 2) {
                this.processWarningOccurred("tRNS chunk for gray image must have length 2, ignoring chunk.");
                this.stream.skipBytes(n);
                return;
            }
            this.metadata.tRNS_gray = this.stream.readUnsignedShort();
            this.metadata.tRNS_colorType = 0;
        }
        else {
            if (ihdr_colorType != 2) {
                this.processWarningOccurred("Gray+Alpha and RGBS images may not have a tRNS chunk, ignoring it.");
                return;
            }
            if (n != 6) {
                this.processWarningOccurred("tRNS chunk for RGB image must have length 6, ignoring chunk.");
                this.stream.skipBytes(n);
                return;
            }
            this.metadata.tRNS_red = this.stream.readUnsignedShort();
            this.metadata.tRNS_green = this.stream.readUnsignedShort();
            this.metadata.tRNS_blue = this.stream.readUnsignedShort();
            this.metadata.tRNS_colorType = 2;
        }
        this.metadata.tRNS_present = true;
    }
    
    private static byte[] inflate(final byte[] array) throws IOException {
        final InflaterInputStream inflaterInputStream = new InflaterInputStream(new ByteArrayInputStream(array));
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int read;
            while ((read = inflaterInputStream.read()) != -1) {
                byteArrayOutputStream.write(read);
            }
        }
        finally {
            inflaterInputStream.close();
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private void parse_zTXt_chunk(final int n) throws IOException {
        final String nullTerminatedString = this.readNullTerminatedString("ISO-8859-1", 80);
        this.metadata.zTXt_keyword.add(nullTerminatedString);
        this.metadata.zTXt_compressionMethod.add(new Integer(this.stream.readUnsignedByte()));
        final byte[] array = new byte[n - nullTerminatedString.length() - 2];
        this.stream.readFully(array);
        this.metadata.zTXt_text.add(new String(inflate(array), "ISO-8859-1"));
    }
    
    private void readMetadata() throws IIOException {
        if (this.gotMetadata) {
            return;
        }
        this.readHeader();
        final int ihdr_colorType = this.metadata.IHDR_colorType;
        if (this.ignoreMetadata && ihdr_colorType != 3) {
            try {
                while (true) {
                    final int int1 = this.stream.readInt();
                    if (this.stream.readInt() == 1229209940) {
                        break;
                    }
                    this.stream.skipBytes(int1 + 4);
                }
                this.stream.skipBytes(-8);
                this.imageStartPosition = this.stream.getStreamPosition();
            }
            catch (final IOException ex) {
                throw new IIOException("Error skipping PNG metadata", ex);
            }
            this.gotMetadata = true;
            return;
        }
        Label_0123: {
            break Label_0123;
            try {
                while (true) {
                    final int int2 = this.stream.readInt();
                    final int int3 = this.stream.readInt();
                    if (int2 < 0) {
                        throw new IIOException("Invalid chunk lenght " + int2);
                    }
                    int int4;
                    try {
                        this.stream.mark();
                        this.stream.seek(this.stream.getStreamPosition() + int2);
                        int4 = this.stream.readInt();
                        this.stream.reset();
                    }
                    catch (final IOException ex2) {
                        throw new IIOException("Invalid chunk length " + int2);
                    }
                    switch (int3) {
                        case 1229209940: {
                            this.stream.skipBytes(-8);
                            this.imageStartPosition = this.stream.getStreamPosition();
                            break Label_0123;
                        }
                        case 1347179589: {
                            this.parse_PLTE_chunk(int2);
                            break;
                        }
                        case 1649100612: {
                            this.parse_bKGD_chunk();
                            break;
                        }
                        case 1665684045: {
                            this.parse_cHRM_chunk();
                            break;
                        }
                        case 1732332865: {
                            this.parse_gAMA_chunk();
                            break;
                        }
                        case 1749635924: {
                            this.parse_hIST_chunk(int2);
                            break;
                        }
                        case 1766015824: {
                            this.parse_iCCP_chunk(int2);
                            break;
                        }
                        case 1767135348: {
                            if (this.ignoreMetadata) {
                                this.stream.skipBytes(int2);
                                break;
                            }
                            this.parse_iTXt_chunk(int2);
                            break;
                        }
                        case 1883789683: {
                            this.parse_pHYs_chunk();
                            break;
                        }
                        case 1933723988: {
                            this.parse_sBIT_chunk();
                            break;
                        }
                        case 1934642260: {
                            this.parse_sPLT_chunk(int2);
                            break;
                        }
                        case 1934772034: {
                            this.parse_sRGB_chunk();
                            break;
                        }
                        case 1950701684: {
                            this.parse_tEXt_chunk(int2);
                            break;
                        }
                        case 1950960965: {
                            this.parse_tIME_chunk();
                            break;
                        }
                        case 1951551059: {
                            this.parse_tRNS_chunk(int2);
                            break;
                        }
                        case 2052348020: {
                            if (this.ignoreMetadata) {
                                this.stream.skipBytes(int2);
                                break;
                            }
                            this.parse_zTXt_chunk(int2);
                            break;
                        }
                        default: {
                            final byte[] array = new byte[int2];
                            this.stream.readFully(array);
                            final StringBuilder sb = new StringBuilder(4);
                            sb.append((char)(int3 >>> 24));
                            sb.append((char)(int3 >> 16 & 0xFF));
                            sb.append((char)(int3 >> 8 & 0xFF));
                            sb.append((char)(int3 & 0xFF));
                            if (int3 >>> 28 == 0) {
                                this.processWarningOccurred("Encountered unknown chunk with critical bit set!");
                            }
                            this.metadata.unknownChunkType.add(sb.toString());
                            this.metadata.unknownChunkData.add(array);
                            break;
                        }
                    }
                    if (int4 != this.stream.readInt()) {
                        throw new IIOException("Failed to read a chunk of type " + int3);
                    }
                    this.stream.flushBefore(this.stream.getStreamPosition());
                }
            }
            catch (final IOException ex3) {
                throw new IIOException("Error reading PNG metadata", ex3);
            }
        }
        this.gotMetadata = true;
    }
    
    private static void decodeSubFilter(final byte[] array, final int n, final int n2, final int n3) {
        for (int i = n3; i < n2; ++i) {
            array[i + n] = (byte)((array[i + n] & 0xFF) + (array[i + n - n3] & 0xFF));
        }
    }
    
    private static void decodeUpFilter(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) {
        for (int i = 0; i < n3; ++i) {
            array[i + n] = (byte)((array[i + n] & 0xFF) + (array2[i + n2] & 0xFF));
        }
    }
    
    private static void decodeAverageFilter(final byte[] array, final int n, final byte[] array2, final int n2, final int n3, final int n4) {
        for (int i = 0; i < n4; ++i) {
            array[i + n] = (byte)((array[i + n] & 0xFF) + (array2[i + n2] & 0xFF) / 2);
        }
        for (int j = n4; j < n3; ++j) {
            array[j + n] = (byte)((array[j + n] & 0xFF) + ((array[j + n - n4] & 0xFF) + (array2[j + n2] & 0xFF)) / 2);
        }
    }
    
    private static int paethPredictor(final int n, final int n2, final int n3) {
        final int n4 = n + n2 - n3;
        final int abs = Math.abs(n4 - n);
        final int abs2 = Math.abs(n4 - n2);
        final int abs3 = Math.abs(n4 - n3);
        if (abs <= abs2 && abs <= abs3) {
            return n;
        }
        if (abs2 <= abs3) {
            return n2;
        }
        return n3;
    }
    
    private static void decodePaethFilter(final byte[] array, final int n, final byte[] array2, final int n2, final int n3, final int n4) {
        for (int i = 0; i < n4; ++i) {
            array[i + n] = (byte)((array[i + n] & 0xFF) + (array2[i + n2] & 0xFF));
        }
        for (int j = n4; j < n3; ++j) {
            array[j + n] = (byte)((array[j + n] & 0xFF) + paethPredictor(array[j + n - n4] & 0xFF, array2[j + n2] & 0xFF, array2[j + n2 - n4] & 0xFF));
        }
    }
    
    private WritableRaster createRaster(final int n, final int n2, final int n3, final int n4, final int n5) {
        final Point point = new Point(0, 0);
        WritableRaster writableRaster;
        if (n5 < 8 && n3 == 1) {
            writableRaster = Raster.createPackedRaster(new DataBufferByte(n2 * n4), n, n2, n5, point);
        }
        else if (n5 <= 8) {
            writableRaster = Raster.createInterleavedRaster(new DataBufferByte(n2 * n4), n, n2, n4, n3, PNGImageReader.bandOffsets[n3], point);
        }
        else {
            writableRaster = Raster.createInterleavedRaster(new DataBufferUShort(n2 * n4), n, n2, n4, n3, PNGImageReader.bandOffsets[n3], point);
        }
        return writableRaster;
    }
    
    private void skipPass(final int n, final int n2) throws IOException, IIOException {
        if (n == 0 || n2 == 0) {
            return;
        }
        final int n3 = (PNGImageReader.inputBandsForColorType[this.metadata.IHDR_colorType] * n * this.metadata.IHDR_bitDepth + 7) / 8;
        for (int i = 0; i < n2; ++i) {
            this.pixelStream.skipBytes(1 + n3);
            if (this.abortRequested()) {
                return;
            }
        }
    }
    
    private void updateImageProgress(final int n) {
        this.pixelsDone += n;
        this.processImageProgress(100.0f * this.pixelsDone / this.totalPixels);
    }
    
    private void decodePass(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7) throws IOException {
        if (n6 == 0 || n7 == 0) {
            return;
        }
        WritableRaster writableRaster = this.theImage.getWritableTile(0, 0);
        final int minX = writableRaster.getMinX();
        final int n8 = minX + writableRaster.getWidth() - 1;
        final int minY = writableRaster.getMinY();
        final int n9 = minY + writableRaster.getHeight() - 1;
        final int[] computeUpdatedPixels = ReaderUtil.computeUpdatedPixels(this.sourceRegion, this.destinationOffset, minX, minY, n8, n9, this.sourceXSubsampling, this.sourceYSubsampling, n2, n3, n6, n7, n4, n5);
        final int n10 = computeUpdatedPixels[0];
        final int n11 = computeUpdatedPixels[1];
        final int n12 = computeUpdatedPixels[2];
        final int n13 = computeUpdatedPixels[4];
        final int n14 = computeUpdatedPixels[5];
        final int ihdr_bitDepth = this.metadata.IHDR_bitDepth;
        final int n15 = PNGImageReader.inputBandsForColorType[this.metadata.IHDR_colorType];
        final int n16 = ((ihdr_bitDepth == 16) ? 2 : 1) * n15;
        final int n17 = (n15 * n6 * ihdr_bitDepth + 7) / 8;
        final int n18 = (ihdr_bitDepth == 16) ? (n17 / 2) : n17;
        if (n12 == 0) {
            for (int i = 0; i < n7; ++i) {
                this.updateImageProgress(n6);
                this.pixelStream.skipBytes(1 + n17);
            }
            return;
        }
        final int n19 = ((n10 - this.destinationOffset.x) * this.sourceXSubsampling + this.sourceRegion.x - n2) / n4;
        final int n20 = n13 * this.sourceXSubsampling / n4;
        Object data = null;
        short[] data2 = null;
        byte[] array = new byte[n17];
        byte[] array2 = new byte[n17];
        WritableRaster writableRaster2 = this.createRaster(n6, 1, n15, n18, ihdr_bitDepth);
        final int[] pixel = writableRaster2.getPixel(0, 0, (int[])null);
        final DataBuffer dataBuffer = writableRaster2.getDataBuffer();
        if (dataBuffer.getDataType() == 0) {
            data = ((DataBufferByte)dataBuffer).getData();
        }
        else {
            data2 = ((DataBufferUShort)dataBuffer).getData();
        }
        this.processPassStarted(this.theImage, n, this.sourceMinProgressivePass, this.sourceMaxProgressivePass, n10, n11, n13, n14, this.destinationBands);
        if (this.sourceBands != null) {
            writableRaster2 = writableRaster2.createWritableChild(0, 0, writableRaster2.getWidth(), 1, 0, 0, this.sourceBands);
        }
        if (this.destinationBands != null) {
            writableRaster = writableRaster.createWritableChild(0, 0, writableRaster.getWidth(), writableRaster.getHeight(), 0, 0, this.destinationBands);
        }
        boolean b = false;
        final int[] sampleSize = writableRaster.getSampleModel().getSampleSize();
        final int length = sampleSize.length;
        for (int j = 0; j < length; ++j) {
            if (sampleSize[j] != ihdr_bitDepth) {
                b = true;
                break;
            }
        }
        int[][] array3 = null;
        if (b) {
            final int n21 = (1 << ihdr_bitDepth) - 1;
            final int n22 = n21 / 2;
            array3 = new int[length][];
            for (int k = 0; k < length; ++k) {
                final int n23 = (1 << sampleSize[k]) - 1;
                array3[k] = new int[n21 + 1];
                for (int l = 0; l <= n21; ++l) {
                    array3[k][l] = (l * n23 + n22) / n21;
                }
            }
        }
        final boolean b2 = n20 == 1 && n13 == 1 && !b && writableRaster instanceof ByteInterleavedRaster;
        if (b2) {
            writableRaster2 = writableRaster2.createWritableChild(n19, 0, n12, 1, 0, 0, null);
        }
        for (int n24 = 0; n24 < n7; ++n24) {
            this.updateImageProgress(n6);
            final int read = this.pixelStream.read();
            try {
                final byte[] array4 = array2;
                array2 = array;
                array = array4;
                this.pixelStream.readFully(array, 0, n17);
            }
            catch (final ZipException ex) {
                throw ex;
            }
            switch (read) {
                case 0: {
                    break;
                }
                case 1: {
                    decodeSubFilter(array, 0, n17, n16);
                    break;
                }
                case 2: {
                    decodeUpFilter(array, 0, array2, 0, n17);
                    break;
                }
                case 3: {
                    decodeAverageFilter(array, 0, array2, 0, n17, n16);
                    break;
                }
                case 4: {
                    decodePaethFilter(array, 0, array2, 0, n17, n16);
                    break;
                }
                default: {
                    throw new IIOException("Unknown row filter type (= " + read + ")!");
                }
            }
            if (ihdr_bitDepth < 16) {
                System.arraycopy(array, 0, data, 0, n17);
            }
            else {
                int n25 = 0;
                for (int n26 = 0; n26 < n18; ++n26) {
                    data2[n26] = (short)(array[n25] << 8 | (array[n25 + 1] & 0xFF));
                    n25 += 2;
                }
            }
            final int n27 = n24 * n5 + n3;
            if (n27 >= this.sourceRegion.y && n27 < this.sourceRegion.y + this.sourceRegion.height && (n27 - this.sourceRegion.y) % this.sourceYSubsampling == 0) {
                final int n28 = this.destinationOffset.y + (n27 - this.sourceRegion.y) / this.sourceYSubsampling;
                if (n28 >= minY) {
                    if (n28 > n9) {
                        break;
                    }
                    if (b2) {
                        writableRaster.setRect(n10, n28, writableRaster2);
                    }
                    else {
                        int n29 = n19;
                        for (int n30 = n10; n30 < n10 + n12; n30 += n13) {
                            writableRaster2.getPixel(n29, 0, pixel);
                            if (b) {
                                for (int n31 = 0; n31 < length; ++n31) {
                                    pixel[n31] = array3[n31][pixel[n31]];
                                }
                            }
                            writableRaster.setPixel(n30, n28, pixel);
                            n29 += n20;
                        }
                    }
                    this.processImageUpdate(this.theImage, n10, n28, n12, 1, n13, n14, this.destinationBands);
                    if (this.abortRequested()) {
                        return;
                    }
                }
            }
        }
        this.processPassComplete(this.theImage);
    }
    
    private void decodeImage() throws IOException, IIOException {
        final int ihdr_width = this.metadata.IHDR_width;
        final int ihdr_height = this.metadata.IHDR_height;
        this.pixelsDone = 0;
        this.totalPixels = ihdr_width * ihdr_height;
        this.clearAbortRequest();
        if (this.metadata.IHDR_interlaceMethod == 0) {
            this.decodePass(0, 0, 0, 1, 1, ihdr_width, ihdr_height);
        }
        else {
            for (int i = 0; i <= this.sourceMaxProgressivePass; ++i) {
                final int n = PNGImageReader.adam7XOffset[i];
                final int n2 = PNGImageReader.adam7YOffset[i];
                final int n3 = PNGImageReader.adam7XSubsampling[i];
                final int n4 = PNGImageReader.adam7YSubsampling[i];
                final int n5 = PNGImageReader.adam7XSubsampling[i + 1] - 1;
                final int n6 = PNGImageReader.adam7YSubsampling[i + 1] - 1;
                if (i >= this.sourceMinProgressivePass) {
                    this.decodePass(i, n, n2, n3, n4, (ihdr_width + n5) / n3, (ihdr_height + n6) / n4);
                }
                else {
                    this.skipPass((ihdr_width + n5) / n3, (ihdr_height + n6) / n4);
                }
                if (this.abortRequested()) {
                    return;
                }
            }
        }
    }
    
    private void readImage(final ImageReadParam imageReadParam) throws IIOException {
        this.readMetadata();
        final int ihdr_width = this.metadata.IHDR_width;
        final int ihdr_height = this.metadata.IHDR_height;
        this.sourceXSubsampling = 1;
        this.sourceYSubsampling = 1;
        this.sourceMinProgressivePass = 0;
        this.sourceMaxProgressivePass = 6;
        this.sourceBands = null;
        this.destinationBands = null;
        this.destinationOffset = new Point(0, 0);
        if (imageReadParam != null) {
            this.sourceXSubsampling = imageReadParam.getSourceXSubsampling();
            this.sourceYSubsampling = imageReadParam.getSourceYSubsampling();
            this.sourceMinProgressivePass = Math.max(imageReadParam.getSourceMinProgressivePass(), 0);
            this.sourceMaxProgressivePass = Math.min(imageReadParam.getSourceMaxProgressivePass(), 6);
            this.sourceBands = imageReadParam.getSourceBands();
            this.destinationBands = imageReadParam.getDestinationBands();
            this.destinationOffset = imageReadParam.getDestinationOffset();
        }
        Inflater inflater = null;
        try {
            this.stream.seek(this.imageStartPosition);
            final SequenceInputStream sequenceInputStream = new SequenceInputStream(new PNGImageDataEnumeration(this.stream));
            inflater = new Inflater();
            this.pixelStream = new DataInputStream(new BufferedInputStream(new InflaterInputStream(sequenceInputStream, inflater)));
            this.theImage = ImageReader.getDestination(imageReadParam, this.getImageTypes(0), ihdr_width, ihdr_height);
            final Rectangle rectangle = new Rectangle(0, 0, 0, 0);
            this.sourceRegion = new Rectangle(0, 0, 0, 0);
            ImageReader.computeRegions(imageReadParam, ihdr_width, ihdr_height, this.theImage, this.sourceRegion, rectangle);
            this.destinationOffset.setLocation(rectangle.getLocation());
            ImageReader.checkReadParamBandSettings(imageReadParam, PNGImageReader.inputBandsForColorType[this.metadata.IHDR_colorType], this.theImage.getSampleModel().getNumBands());
            this.processImageStarted(0);
            this.decodeImage();
            if (this.abortRequested()) {
                this.processReadAborted();
            }
            else {
                this.processImageComplete();
            }
        }
        catch (final IOException ex) {
            throw new IIOException("Error reading PNG image data", ex);
        }
        finally {
            if (inflater != null) {
                inflater.end();
            }
        }
    }
    
    @Override
    public int getNumImages(final boolean b) throws IIOException {
        if (this.stream == null) {
            throw new IllegalStateException("No input source set!");
        }
        if (this.seekForwardOnly && b) {
            throw new IllegalStateException("seekForwardOnly and allowSearch can't both be true!");
        }
        return 1;
    }
    
    @Override
    public int getWidth(final int n) throws IIOException {
        if (n != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        this.readHeader();
        return this.metadata.IHDR_width;
    }
    
    @Override
    public int getHeight(final int n) throws IIOException {
        if (n != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        this.readHeader();
        return this.metadata.IHDR_height;
    }
    
    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(final int n) throws IIOException {
        if (n != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        this.readHeader();
        final ArrayList list = new ArrayList(1);
        final int ihdr_bitDepth = this.metadata.IHDR_bitDepth;
        final int ihdr_colorType = this.metadata.IHDR_colorType;
        int n2;
        if (ihdr_bitDepth <= 8) {
            n2 = 0;
        }
        else {
            n2 = 1;
        }
        switch (ihdr_colorType) {
            case 0: {
                list.add(ImageTypeSpecifier.createGrayscale(ihdr_bitDepth, n2, false));
                break;
            }
            case 2: {
                if (ihdr_bitDepth == 8) {
                    list.add(ImageTypeSpecifier.createFromBufferedImageType(5));
                    list.add(ImageTypeSpecifier.createFromBufferedImageType(1));
                    list.add(ImageTypeSpecifier.createFromBufferedImageType(4));
                }
                list.add(ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1000), new int[] { 0, 1, 2 }, n2, false, false));
                break;
            }
            case 3: {
                this.readMetadata();
                final int n3 = 1 << ihdr_bitDepth;
                byte[] array = this.metadata.PLTE_red;
                byte[] array2 = this.metadata.PLTE_green;
                byte[] array3 = this.metadata.PLTE_blue;
                if (this.metadata.PLTE_red.length < n3) {
                    array = Arrays.copyOf(this.metadata.PLTE_red, n3);
                    Arrays.fill(array, this.metadata.PLTE_red.length, n3, this.metadata.PLTE_red[this.metadata.PLTE_red.length - 1]);
                    array2 = Arrays.copyOf(this.metadata.PLTE_green, n3);
                    Arrays.fill(array2, this.metadata.PLTE_green.length, n3, this.metadata.PLTE_green[this.metadata.PLTE_green.length - 1]);
                    array3 = Arrays.copyOf(this.metadata.PLTE_blue, n3);
                    Arrays.fill(array3, this.metadata.PLTE_blue.length, n3, this.metadata.PLTE_blue[this.metadata.PLTE_blue.length - 1]);
                }
                byte[] array4 = null;
                if (this.metadata.tRNS_present && this.metadata.tRNS_alpha != null) {
                    if (this.metadata.tRNS_alpha.length == array.length) {
                        array4 = this.metadata.tRNS_alpha;
                    }
                    else {
                        array4 = Arrays.copyOf(this.metadata.tRNS_alpha, array.length);
                        Arrays.fill(array4, this.metadata.tRNS_alpha.length, array.length, (byte)(-1));
                    }
                }
                list.add(ImageTypeSpecifier.createIndexed(array, array2, array3, array4, ihdr_bitDepth, 0));
                break;
            }
            case 4: {
                list.add(ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1003), new int[] { 0, 1 }, n2, true, false));
                break;
            }
            case 6: {
                if (ihdr_bitDepth == 8) {
                    list.add(ImageTypeSpecifier.createFromBufferedImageType(6));
                    list.add(ImageTypeSpecifier.createFromBufferedImageType(2));
                }
                list.add(ImageTypeSpecifier.createInterleaved(ColorSpace.getInstance(1000), new int[] { 0, 1, 2, 3 }, n2, true, false));
                break;
            }
        }
        return list.iterator();
    }
    
    @Override
    public ImageTypeSpecifier getRawImageType(final int n) throws IOException {
        final Iterator<ImageTypeSpecifier> imageTypes = this.getImageTypes(n);
        ImageTypeSpecifier imageTypeSpecifier;
        do {
            imageTypeSpecifier = imageTypes.next();
        } while (imageTypes.hasNext());
        return imageTypeSpecifier;
    }
    
    @Override
    public ImageReadParam getDefaultReadParam() {
        return new ImageReadParam();
    }
    
    @Override
    public IIOMetadata getStreamMetadata() throws IIOException {
        return null;
    }
    
    @Override
    public IIOMetadata getImageMetadata(final int n) throws IIOException {
        if (n != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        this.readMetadata();
        return this.metadata;
    }
    
    @Override
    public BufferedImage read(final int n, final ImageReadParam imageReadParam) throws IIOException {
        if (n != 0) {
            throw new IndexOutOfBoundsException("imageIndex != 0!");
        }
        this.readImage(imageReadParam);
        return this.theImage;
    }
    
    @Override
    public void reset() {
        super.reset();
        this.resetStreamSettings();
    }
    
    private void resetStreamSettings() {
        this.gotHeader = false;
        this.gotMetadata = false;
        this.metadata = null;
        this.pixelStream = null;
    }
    
    static {
        inputBandsForColorType = new int[] { 1, -1, 3, 1, 2, -1, 4 };
        adam7XOffset = new int[] { 0, 4, 0, 2, 0, 1, 0 };
        adam7YOffset = new int[] { 0, 0, 4, 0, 2, 0, 1 };
        adam7XSubsampling = new int[] { 8, 8, 4, 4, 2, 2, 1, 1 };
        adam7YSubsampling = new int[] { 8, 8, 8, 4, 4, 2, 2, 1 };
        bandOffsets = new int[][] { null, { 0 }, { 0, 1 }, { 0, 1, 2 }, { 0, 1, 2, 3 } };
    }
}
