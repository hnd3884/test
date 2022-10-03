package sun.awt.image;

import java.awt.image.IndexColorModel;
import java.io.BufferedInputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.util.GregorianCalendar;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.awt.image.ColorModel;
import java.util.Hashtable;

public class PNGImageDecoder extends ImageDecoder
{
    private static final int GRAY = 0;
    private static final int PALETTE = 1;
    private static final int COLOR = 2;
    private static final int ALPHA = 4;
    private static final int bKGDChunk = 1649100612;
    private static final int cHRMChunk = 1665684045;
    private static final int gAMAChunk = 1732332865;
    private static final int hISTChunk = 1749635924;
    private static final int IDATChunk = 1229209940;
    private static final int IENDChunk = 1229278788;
    private static final int IHDRChunk = 1229472850;
    private static final int PLTEChunk = 1347179589;
    private static final int pHYsChunk = 1883789683;
    private static final int sBITChunk = 1933723988;
    private static final int tEXtChunk = 1950701684;
    private static final int tIMEChunk = 1950960965;
    private static final int tRNSChunk = 1951551059;
    private static final int zTXtChunk = 2052348020;
    private int width;
    private int height;
    private int bitDepth;
    private int colorType;
    private int compressionMethod;
    private int filterMethod;
    private int interlaceMethod;
    private int gamma;
    private Hashtable properties;
    private ColorModel cm;
    private byte[] red_map;
    private byte[] green_map;
    private byte[] blue_map;
    private byte[] alpha_map;
    private int transparentPixel;
    private byte[] transparentPixel_16;
    private static ColorModel[] greyModels;
    private static final byte[] startingRow;
    private static final byte[] startingCol;
    private static final byte[] rowIncrement;
    private static final byte[] colIncrement;
    private static final byte[] blockHeight;
    private static final byte[] blockWidth;
    int pos;
    int limit;
    int chunkStart;
    int chunkKey;
    int chunkLength;
    int chunkCRC;
    boolean seenEOF;
    private static final byte[] signature;
    PNGFilterInputStream inputStream;
    InputStream underlyingInputStream;
    byte[] inbuf;
    private static boolean checkCRC;
    private static final int[] crc_table;
    
    private void property(final String s, final Object o) {
        if (o == null) {
            return;
        }
        if (this.properties == null) {
            this.properties = new Hashtable();
        }
        this.properties.put(s, o);
    }
    
    private void property(final String s, final float n) {
        this.property(s, new Float(n));
    }
    
    private final void pngassert(final boolean b) throws IOException {
        if (!b) {
            final PNGException ex = new PNGException("Broken file");
            ex.printStackTrace();
            throw ex;
        }
    }
    
    protected boolean handleChunk(final int n, final byte[] array, final int n2, final int n3) throws IOException {
        switch (n) {
            case 1649100612: {
                Object o = null;
                switch (this.colorType) {
                    case 2:
                    case 6: {
                        this.pngassert(n3 == 6);
                        o = new Color(array[n2] & 0xFF, array[n2 + 2] & 0xFF, array[n2 + 4] & 0xFF);
                        break;
                    }
                    case 3:
                    case 7: {
                        this.pngassert(n3 == 1);
                        final int n4 = array[n2] & 0xFF;
                        this.pngassert(this.red_map != null && n4 < this.red_map.length);
                        o = new Color(this.red_map[n4] & 0xFF, this.green_map[n4] & 0xFF, this.blue_map[n4] & 0xFF);
                        break;
                    }
                    case 0:
                    case 4: {
                        this.pngassert(n3 == 2);
                        final int n5 = array[n2] & 0xFF;
                        o = new Color(n5, n5, n5);
                        break;
                    }
                }
                if (o != null) {
                    this.property("background", o);
                    break;
                }
                break;
            }
            case 1665684045: {
                this.property("chromaticities", new Chromaticities(this.getInt(n2), this.getInt(n2 + 4), this.getInt(n2 + 8), this.getInt(n2 + 12), this.getInt(n2 + 16), this.getInt(n2 + 20), this.getInt(n2 + 24), this.getInt(n2 + 28)));
                break;
            }
            case 1732332865: {
                if (n3 != 4) {
                    throw new PNGException("bogus gAMA");
                }
                this.gamma = this.getInt(n2);
                if (this.gamma != 100000) {
                    this.property("gamma", this.gamma / 100000.0f);
                    break;
                }
                break;
            }
            case 1229209940: {
                return false;
            }
            case 1229472850: {
                if (n3 != 13 || (this.width = this.getInt(n2)) == 0 || (this.height = this.getInt(n2 + 4)) == 0) {
                    throw new PNGException("bogus IHDR");
                }
                this.bitDepth = this.getByte(n2 + 8);
                this.colorType = this.getByte(n2 + 9);
                this.compressionMethod = this.getByte(n2 + 10);
                this.filterMethod = this.getByte(n2 + 11);
                this.interlaceMethod = this.getByte(n2 + 12);
                break;
            }
            case 1347179589: {
                final int n6 = n3 / 3;
                this.red_map = new byte[n6];
                this.green_map = new byte[n6];
                this.blue_map = new byte[n6];
                for (int i = 0, n7 = n2; i < n6; ++i, n7 += 3) {
                    this.red_map[i] = array[n7];
                    this.green_map[i] = array[n7 + 1];
                    this.blue_map[i] = array[n7 + 2];
                }
            }
            case 1883789683: {}
            case 1950701684: {
                int n8;
                for (n8 = 0; n8 < n3 && array[n2 + n8] != 0; ++n8) {}
                if (n8 < n3) {
                    this.property(new String(array, n2, n8), new String(array, n2 + n8 + 1, n3 - n8 - 1));
                    break;
                }
                break;
            }
            case 1950960965: {
                this.property("modtime", new GregorianCalendar(this.getShort(n2 + 0), this.getByte(n2 + 2) - 1, this.getByte(n2 + 3), this.getByte(n2 + 4), this.getByte(n2 + 5), this.getByte(n2 + 6)).getTime());
                break;
            }
            case 1951551059: {
                switch (this.colorType) {
                    case 3:
                    case 7: {
                        int length = n3;
                        if (this.red_map != null) {
                            length = this.red_map.length;
                        }
                        System.arraycopy(array, n2, this.alpha_map = new byte[length], 0, (n3 < length) ? n3 : length);
                        while (--length >= n3) {
                            this.alpha_map[length] = -1;
                        }
                        break;
                    }
                    case 2:
                    case 6: {
                        this.pngassert(n3 == 6);
                        if (this.bitDepth == 16) {
                            this.transparentPixel_16 = new byte[6];
                            for (int j = 0; j < 6; ++j) {
                                this.transparentPixel_16[j] = (byte)this.getByte(n2 + j);
                            }
                            break;
                        }
                        this.transparentPixel = ((this.getShort(n2 + 0) & 0xFF) << 16 | (this.getShort(n2 + 2) & 0xFF) << 8 | (this.getShort(n2 + 4) & 0xFF));
                        break;
                    }
                    case 0:
                    case 4: {
                        this.pngassert(n3 == 2);
                        final int short1 = this.getShort(n2);
                        final int n9 = 0xFF & ((this.bitDepth == 16) ? (short1 >> 8) : short1);
                        this.transparentPixel = (n9 << 16 | n9 << 8 | n9);
                        break;
                    }
                }
                break;
            }
        }
        return true;
    }
    
    @Override
    public void produceImage() throws IOException, ImageFormatException {
        try {
            for (int i = 0; i < PNGImageDecoder.signature.length; ++i) {
                if ((PNGImageDecoder.signature[i] & 0xFF) != this.underlyingInputStream.read()) {
                    throw new PNGException("Chunk signature mismatch");
                }
            }
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new InflaterInputStream(this.inputStream, new Inflater()));
            this.getData();
            byte[] array = null;
            int[] array2 = null;
            int width = this.width;
            int n = 0;
            switch (this.bitDepth) {
                case 1: {
                    n = 0;
                    break;
                }
                case 2: {
                    n = 1;
                    break;
                }
                case 4: {
                    n = 2;
                    break;
                }
                case 8: {
                    n = 3;
                    break;
                }
                case 16: {
                    n = 4;
                    break;
                }
                default: {
                    throw new PNGException("invalid depth");
                }
            }
            int width2;
            if (this.interlaceMethod != 0) {
                width *= this.height;
                width2 = this.width;
            }
            else {
                width2 = 0;
            }
            final int n2 = this.colorType | this.bitDepth << 3;
            final int n3 = (1 << ((this.bitDepth >= 8) ? 8 : this.bitDepth)) - 1;
            switch (this.colorType) {
                case 3:
                case 7: {
                    if (this.red_map == null) {
                        throw new PNGException("palette expected");
                    }
                    if (this.alpha_map == null) {
                        this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map);
                    }
                    else {
                        this.cm = new IndexColorModel(this.bitDepth, this.red_map.length, this.red_map, this.green_map, this.blue_map, this.alpha_map);
                    }
                    array = new byte[width];
                    break;
                }
                case 0: {
                    final int n4 = (n >= 4) ? 3 : n;
                    final ColorModel cm = PNGImageDecoder.greyModels[n4];
                    this.cm = cm;
                    if (cm == null) {
                        final int n5 = 1 << (1 << n4);
                        final byte[] array3 = new byte[n5];
                        for (int j = 0; j < n5; ++j) {
                            array3[j] = (byte)(255 * j / (n5 - 1));
                        }
                        if (this.transparentPixel == -1) {
                            this.cm = new IndexColorModel(this.bitDepth, array3.length, array3, array3, array3);
                        }
                        else {
                            this.cm = new IndexColorModel(this.bitDepth, array3.length, array3, array3, array3, this.transparentPixel & 0xFF);
                        }
                        PNGImageDecoder.greyModels[n4] = this.cm;
                    }
                    array = new byte[width];
                    break;
                }
                case 2:
                case 4:
                case 6: {
                    this.cm = ColorModel.getRGBdefault();
                    array2 = new int[width];
                    break;
                }
                default: {
                    throw new PNGException("invalid color type");
                }
            }
            this.setDimensions(this.width, this.height);
            this.setColorModel(this.cm);
            this.setHints((this.interlaceMethod != 0) ? 6 : 30);
            this.headerComplete();
            final int n6 = (((this.colorType & 0x1) != 0x0) ? 1 : ((((this.colorType & 0x2) != 0x0) ? 3 : 1) + (((this.colorType & 0x4) != 0x0) ? 1 : 0))) * this.bitDepth;
            final int n7 = n6 + 7 >> 3;
            int n8;
            int n9;
            if (this.interlaceMethod == 0) {
                n8 = -1;
                n9 = 0;
            }
            else {
                n8 = 0;
                n9 = 7;
            }
            while (++n8 <= n9) {
                int k = PNGImageDecoder.startingRow[n8];
                final byte b = PNGImageDecoder.rowIncrement[n8];
                final byte b2 = PNGImageDecoder.colIncrement[n8];
                final byte b3 = PNGImageDecoder.blockWidth[n8];
                final byte b4 = PNGImageDecoder.blockHeight[n8];
                final byte b5 = PNGImageDecoder.startingCol[n8];
                final int n10 = (this.width - b5 + (b2 - 1)) / b2 * n6 + 7 >> 3;
                if (n10 == 0) {
                    continue;
                }
                final int n11 = (this.interlaceMethod == 0) ? (b * this.width) : 0;
                int n12 = width2 * k;
                int n13 = 1;
                byte[] array6;
                for (byte[] array4 = new byte[n10], array5 = new byte[n10]; k < this.height; k += b, n12 += b * width2, array6 = array4, array4 = array5, array5 = array6, n13 = 0) {
                    final int read = bufferedInputStream.read();
                    int read2;
                    for (int l = 0; l < n10; l += read2) {
                        read2 = bufferedInputStream.read(array4, l, n10 - l);
                        if (read2 <= 0) {
                            throw new PNGException("missing data");
                        }
                    }
                    this.filterRow(array4, (byte[])((n13 != 0) ? null : array5), read, n10, n7);
                    int n14 = b5;
                    int n15 = 0;
                    while (n14 < this.width) {
                        if (array2 != null) {
                            switch (n2) {
                                case 70: {
                                    array2[n14 + n12] = ((array4[n15] & 0xFF) << 16 | (array4[n15 + 1] & 0xFF) << 8 | (array4[n15 + 2] & 0xFF) | (array4[n15 + 3] & 0xFF) << 24);
                                    n15 += 4;
                                    break;
                                }
                                case 134: {
                                    array2[n14 + n12] = ((array4[n15] & 0xFF) << 16 | (array4[n15 + 2] & 0xFF) << 8 | (array4[n15 + 4] & 0xFF) | (array4[n15 + 6] & 0xFF) << 24);
                                    n15 += 8;
                                    break;
                                }
                                case 66: {
                                    int n16 = (array4[n15] & 0xFF) << 16 | (array4[n15 + 1] & 0xFF) << 8 | (array4[n15 + 2] & 0xFF);
                                    if (n16 != this.transparentPixel) {
                                        n16 |= 0xFF000000;
                                    }
                                    array2[n14 + n12] = n16;
                                    n15 += 3;
                                    break;
                                }
                                case 130: {
                                    int n17 = (array4[n15] & 0xFF) << 16 | (array4[n15 + 2] & 0xFF) << 8 | (array4[n15 + 4] & 0xFF);
                                    boolean b6 = this.transparentPixel_16 != null;
                                    for (int n18 = 0; b6 && n18 < 6; b6 &= ((array4[n15 + n18] & 0xFF) == (this.transparentPixel_16[n18] & 0xFF)), ++n18) {}
                                    if (!b6) {
                                        n17 |= 0xFF000000;
                                    }
                                    array2[n14 + n12] = n17;
                                    n15 += 6;
                                    break;
                                }
                                case 68: {
                                    final int n19 = array4[n15] & 0xFF;
                                    array2[n14 + n12] = (n19 << 16 | n19 << 8 | n19 | (array4[n15 + 1] & 0xFF) << 24);
                                    n15 += 2;
                                    break;
                                }
                                case 132: {
                                    final int n20 = array4[n15] & 0xFF;
                                    array2[n14 + n12] = (n20 << 16 | n20 << 8 | n20 | (array4[n15 + 2] & 0xFF) << 24);
                                    n15 += 4;
                                    break;
                                }
                                default: {
                                    throw new PNGException("illegal type/depth");
                                }
                            }
                        }
                        else {
                            switch (this.bitDepth) {
                                case 1: {
                                    array[n14 + n12] = (byte)(array4[n15 >> 3] >> 7 - (n15 & 0x7) & 0x1);
                                    ++n15;
                                    break;
                                }
                                case 2: {
                                    array[n14 + n12] = (byte)(array4[n15 >> 2] >> (3 - (n15 & 0x3)) * 2 & 0x3);
                                    ++n15;
                                    break;
                                }
                                case 4: {
                                    array[n14 + n12] = (byte)(array4[n15 >> 1] >> (1 - (n15 & 0x1)) * 4 & 0xF);
                                    ++n15;
                                    break;
                                }
                                case 8: {
                                    array[n14 + n12] = array4[n15++];
                                    break;
                                }
                                case 16: {
                                    array[n14 + n12] = array4[n15];
                                    n15 += 2;
                                    break;
                                }
                                default: {
                                    throw new PNGException("illegal type/depth");
                                }
                            }
                        }
                        n14 += b2;
                    }
                    if (this.interlaceMethod == 0) {
                        if (array2 != null) {
                            this.sendPixels(0, k, this.width, 1, array2, 0, this.width);
                        }
                        else {
                            this.sendPixels(0, k, this.width, 1, array, 0, this.width);
                        }
                    }
                }
                if (this.interlaceMethod == 0) {
                    continue;
                }
                if (array2 != null) {
                    this.sendPixels(0, 0, this.width, this.height, array2, 0, this.width);
                }
                else {
                    this.sendPixels(0, 0, this.width, this.height, array, 0, this.width);
                }
            }
            this.imageComplete(3, true);
        }
        catch (final IOException ex) {
            if (!this.aborted) {
                this.property("error", ex);
                this.imageComplete(3, true);
                throw ex;
            }
        }
        finally {
            try {
                this.close();
            }
            catch (final Throwable t) {}
        }
    }
    
    private boolean sendPixels(final int n, final int n2, final int n3, final int n4, final int[] array, final int n5, final int n6) {
        if (this.setPixels(n, n2, n3, n4, this.cm, array, n5, n6) <= 0) {
            this.aborted = true;
        }
        return !this.aborted;
    }
    
    private boolean sendPixels(final int n, final int n2, final int n3, final int n4, final byte[] array, final int n5, final int n6) {
        if (this.setPixels(n, n2, n3, n4, this.cm, array, n5, n6) <= 0) {
            this.aborted = true;
        }
        return !this.aborted;
    }
    
    private void filterRow(final byte[] array, final byte[] array2, final int n, final int n2, final int n3) throws IOException {
        int i = 0;
        switch (n) {
            case 0: {
                break;
            }
            case 1: {
                for (int j = n3; j < n2; ++j) {
                    final int n4 = j;
                    array[n4] += array[j - n3];
                }
                break;
            }
            case 2: {
                if (array2 != null) {
                    while (i < n2) {
                        final int n5 = i;
                        array[n5] += array2[i];
                        ++i;
                    }
                    break;
                }
                break;
            }
            case 3: {
                if (array2 != null) {
                    while (i < n3) {
                        final int n6 = i;
                        array[n6] += (byte)((0xFF & array2[i]) >> 1);
                        ++i;
                    }
                    while (i < n2) {
                        final int n7 = i;
                        array[n7] += (byte)((array2[i] & 0xFF) + (array[i - n3] & 0xFF) >> 1);
                        ++i;
                    }
                    break;
                }
                for (int k = n3; k < n2; ++k) {
                    final int n8 = k;
                    array[n8] += (byte)((array[k - n3] & 0xFF) >> 1);
                }
                break;
            }
            case 4: {
                if (array2 != null) {
                    while (i < n3) {
                        final int n9 = i;
                        array[n9] += array2[i];
                        ++i;
                    }
                    while (i < n2) {
                        final int n10 = array[i - n3] & 0xFF;
                        final int n11 = array2[i] & 0xFF;
                        final int n12 = array2[i - n3] & 0xFF;
                        final int n13 = n10 + n11 - n12;
                        final int n14 = (n13 > n10) ? (n13 - n10) : (n10 - n13);
                        final int n15 = (n13 > n11) ? (n13 - n11) : (n11 - n13);
                        final int n16 = (n13 > n12) ? (n13 - n12) : (n12 - n13);
                        final int n17 = i;
                        array[n17] += (byte)((n14 <= n15 && n14 <= n16) ? n10 : ((n15 <= n16) ? n11 : n12));
                        ++i;
                    }
                    break;
                }
                for (int l = n3; l < n2; ++l) {
                    final int n18 = l;
                    array[n18] += array[l - n3];
                }
                break;
            }
            default: {
                throw new PNGException("Illegal filter");
            }
        }
    }
    
    public PNGImageDecoder(final InputStreamImageSource inputStreamImageSource, final InputStream inputStream) throws IOException {
        super(inputStreamImageSource, inputStream);
        this.gamma = 100000;
        this.transparentPixel = -1;
        this.transparentPixel_16 = null;
        this.inbuf = new byte[4096];
        this.inputStream = new PNGFilterInputStream(this, inputStream);
        this.underlyingInputStream = this.inputStream.underlyingInputStream;
    }
    
    private void fill() throws IOException {
        if (!this.seenEOF) {
            if (this.pos > 0 && this.pos < this.limit) {
                System.arraycopy(this.inbuf, this.pos, this.inbuf, 0, this.limit - this.pos);
                this.limit -= this.pos;
                this.pos = 0;
            }
            else if (this.pos >= this.limit) {
                this.pos = 0;
                this.limit = 0;
            }
            final int length = this.inbuf.length;
            while (this.limit < length) {
                final int read = this.underlyingInputStream.read(this.inbuf, this.limit, length - this.limit);
                if (read <= 0) {
                    this.seenEOF = true;
                    break;
                }
                this.limit += read;
            }
        }
    }
    
    private boolean need(final int n) throws IOException {
        if (this.limit - this.pos >= n) {
            return true;
        }
        this.fill();
        if (this.limit - this.pos >= n) {
            return true;
        }
        if (this.seenEOF) {
            return false;
        }
        final byte[] inbuf = new byte[n + 100];
        System.arraycopy(this.inbuf, this.pos, inbuf, 0, this.limit - this.pos);
        this.limit -= this.pos;
        this.pos = 0;
        this.inbuf = inbuf;
        this.fill();
        return this.limit - this.pos >= n;
    }
    
    private final int getInt(final int n) {
        return (this.inbuf[n] & 0xFF) << 24 | (this.inbuf[n + 1] & 0xFF) << 16 | (this.inbuf[n + 2] & 0xFF) << 8 | (this.inbuf[n + 3] & 0xFF);
    }
    
    private final int getShort(final int n) {
        return (short)((this.inbuf[n] & 0xFF) << 8 | (this.inbuf[n + 1] & 0xFF));
    }
    
    private final int getByte(final int n) {
        return this.inbuf[n] & 0xFF;
    }
    
    private final boolean getChunk() throws IOException {
        this.chunkLength = 0;
        if (!this.need(8)) {
            return false;
        }
        this.chunkLength = this.getInt(this.pos);
        this.chunkKey = this.getInt(this.pos + 4);
        if (this.chunkLength < 0) {
            throw new PNGException("bogus length: " + this.chunkLength);
        }
        if (!this.need(this.chunkLength + 12)) {
            return false;
        }
        this.chunkCRC = this.getInt(this.pos + 8 + this.chunkLength);
        this.chunkStart = this.pos + 8;
        if (this.chunkCRC != crc(this.inbuf, this.pos + 4, this.chunkLength + 4) && PNGImageDecoder.checkCRC) {
            throw new PNGException("crc corruption");
        }
        this.pos += this.chunkLength + 12;
        return true;
    }
    
    private void readAll() throws IOException {
        while (this.getChunk()) {
            this.handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength);
        }
    }
    
    boolean getData() throws IOException {
        while (this.chunkLength == 0 && this.getChunk()) {
            if (this.handleChunk(this.chunkKey, this.inbuf, this.chunkStart, this.chunkLength)) {
                this.chunkLength = 0;
            }
        }
        return this.chunkLength > 0;
    }
    
    public static boolean getCheckCRC() {
        return PNGImageDecoder.checkCRC;
    }
    
    public static void setCheckCRC(final boolean checkCRC) {
        PNGImageDecoder.checkCRC = checkCRC;
    }
    
    protected void wrc(int n) {
        n &= 0xFF;
        if (n <= 32 || n > 122) {
            n = 63;
        }
        System.out.write(n);
    }
    
    protected void wrk(final int n) {
        this.wrc(n >> 24);
        this.wrc(n >> 16);
        this.wrc(n >> 8);
        this.wrc(n);
    }
    
    public void print() {
        this.wrk(this.chunkKey);
        System.out.print(" " + this.chunkLength + "\n");
    }
    
    private static int update_crc(final int n, final byte[] array, int n2, int n3) {
        int n4 = n;
        while (--n3 >= 0) {
            n4 = (PNGImageDecoder.crc_table[(n4 ^ array[n2++]) & 0xFF] ^ n4 >>> 8);
        }
        return n4;
    }
    
    private static int crc(final byte[] array, final int n, final int n2) {
        return ~update_crc(-1, array, n, n2);
    }
    
    static {
        PNGImageDecoder.greyModels = new ColorModel[4];
        startingRow = new byte[] { 0, 0, 0, 4, 0, 2, 0, 1 };
        startingCol = new byte[] { 0, 0, 4, 0, 2, 0, 1, 0 };
        rowIncrement = new byte[] { 1, 8, 8, 8, 4, 4, 2, 2 };
        colIncrement = new byte[] { 1, 8, 8, 4, 4, 2, 2, 1 };
        blockHeight = new byte[] { 1, 8, 8, 4, 4, 2, 2, 1 };
        blockWidth = new byte[] { 1, 8, 4, 4, 2, 2, 1, 1 };
        signature = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
        PNGImageDecoder.checkCRC = true;
        crc_table = new int[256];
        for (int i = 0; i < 256; ++i) {
            int n = i;
            for (int j = 0; j < 8; ++j) {
                if ((n & 0x1) != 0x0) {
                    n = (0xEDB88320 ^ n >>> 1);
                }
                else {
                    n >>>= 1;
                }
            }
            PNGImageDecoder.crc_table[i] = n;
        }
    }
    
    public class PNGException extends IOException
    {
        PNGException(final String s) {
            super(s);
        }
    }
    
    public static class Chromaticities
    {
        public float whiteX;
        public float whiteY;
        public float redX;
        public float redY;
        public float greenX;
        public float greenY;
        public float blueX;
        public float blueY;
        
        Chromaticities(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
            this.whiteX = n / 100000.0f;
            this.whiteY = n2 / 100000.0f;
            this.redX = n3 / 100000.0f;
            this.redY = n4 / 100000.0f;
            this.greenX = n5 / 100000.0f;
            this.greenY = n6 / 100000.0f;
            this.blueX = n7 / 100000.0f;
            this.blueY = n8 / 100000.0f;
        }
        
        @Override
        public String toString() {
            return "Chromaticities(white=" + this.whiteX + "," + this.whiteY + ";red=" + this.redX + "," + this.redY + ";green=" + this.greenX + "," + this.greenY + ";blue=" + this.blueX + "," + this.blueY + ")";
        }
    }
}
