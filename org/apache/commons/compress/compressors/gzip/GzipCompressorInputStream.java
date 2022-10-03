package org.apache.commons.compress.compressors.gzip;

import org.apache.commons.compress.utils.IOUtils;
import java.util.zip.DataFormatException;
import java.io.EOFException;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.nio.charset.StandardCharsets;
import org.apache.commons.compress.utils.ByteUtils;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.io.InputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class GzipCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private static final int FHCRC = 2;
    private static final int FEXTRA = 4;
    private static final int FNAME = 8;
    private static final int FCOMMENT = 16;
    private static final int FRESERVED = 224;
    private final CountingInputStream countingStream;
    private final InputStream in;
    private final boolean decompressConcatenated;
    private final byte[] buf;
    private int bufUsed;
    private Inflater inf;
    private final CRC32 crc;
    private boolean endReached;
    private final byte[] oneByte;
    private final GzipParameters parameters;
    
    public GzipCompressorInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, false);
    }
    
    public GzipCompressorInputStream(final InputStream inputStream, final boolean decompressConcatenated) throws IOException {
        this.buf = new byte[8192];
        this.inf = new Inflater(true);
        this.crc = new CRC32();
        this.oneByte = new byte[1];
        this.parameters = new GzipParameters();
        this.countingStream = new CountingInputStream(inputStream);
        if (this.countingStream.markSupported()) {
            this.in = this.countingStream;
        }
        else {
            this.in = new BufferedInputStream(this.countingStream);
        }
        this.decompressConcatenated = decompressConcatenated;
        this.init(true);
    }
    
    public GzipParameters getMetaData() {
        return this.parameters;
    }
    
    private boolean init(final boolean isFirstMember) throws IOException {
        assert isFirstMember || this.decompressConcatenated;
        final int magic0 = this.in.read();
        if (magic0 == -1 && !isFirstMember) {
            return false;
        }
        if (magic0 != 31 || this.in.read() != 139) {
            throw new IOException(isFirstMember ? "Input is not in the .gz format" : "Garbage after a valid .gz stream");
        }
        final DataInput inData = new DataInputStream(this.in);
        final int method = inData.readUnsignedByte();
        if (method != 8) {
            throw new IOException("Unsupported compression method " + method + " in the .gz header");
        }
        final int flg = inData.readUnsignedByte();
        if ((flg & 0xE0) != 0x0) {
            throw new IOException("Reserved flags are set in the .gz header");
        }
        this.parameters.setModificationTime(ByteUtils.fromLittleEndian(inData, 4) * 1000L);
        switch (inData.readUnsignedByte()) {
            case 2: {
                this.parameters.setCompressionLevel(9);
                break;
            }
            case 4: {
                this.parameters.setCompressionLevel(1);
                break;
            }
        }
        this.parameters.setOperatingSystem(inData.readUnsignedByte());
        if ((flg & 0x4) != 0x0) {
            int xlen = inData.readUnsignedByte();
            xlen |= inData.readUnsignedByte() << 8;
            while (xlen-- > 0) {
                inData.readUnsignedByte();
            }
        }
        if ((flg & 0x8) != 0x0) {
            this.parameters.setFilename(new String(readToNull(inData), StandardCharsets.ISO_8859_1));
        }
        if ((flg & 0x10) != 0x0) {
            this.parameters.setComment(new String(readToNull(inData), StandardCharsets.ISO_8859_1));
        }
        if ((flg & 0x2) != 0x0) {
            inData.readShort();
        }
        this.inf.reset();
        this.crc.reset();
        return true;
    }
    
    private static byte[] readToNull(final DataInput inData) throws IOException {
        try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int b = 0;
            while ((b = inData.readUnsignedByte()) != 0) {
                bos.write(b);
            }
            return bos.toByteArray();
        }
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.oneByte, 0, 1) == -1) ? -1 : (this.oneByte[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        if (this.endReached) {
            return -1;
        }
        int size = 0;
        while (len > 0) {
            if (this.inf.needsInput()) {
                this.in.mark(this.buf.length);
                this.bufUsed = this.in.read(this.buf);
                if (this.bufUsed == -1) {
                    throw new EOFException();
                }
                this.inf.setInput(this.buf, 0, this.bufUsed);
            }
            int ret;
            try {
                ret = this.inf.inflate(b, off, len);
            }
            catch (final DataFormatException e) {
                throw new IOException("Gzip-compressed data is corrupt");
            }
            this.crc.update(b, off, ret);
            off += ret;
            len -= ret;
            size += ret;
            this.count(ret);
            if (this.inf.finished()) {
                this.in.reset();
                final int skipAmount = this.bufUsed - this.inf.getRemaining();
                if (IOUtils.skip(this.in, skipAmount) != skipAmount) {
                    throw new IOException();
                }
                this.bufUsed = 0;
                final DataInput inData = new DataInputStream(this.in);
                final long crcStored = ByteUtils.fromLittleEndian(inData, 4);
                if (crcStored != this.crc.getValue()) {
                    throw new IOException("Gzip-compressed data is corrupt (CRC32 error)");
                }
                final long isize = ByteUtils.fromLittleEndian(inData, 4);
                if (isize != (this.inf.getBytesWritten() & 0xFFFFFFFFL)) {
                    throw new IOException("Gzip-compressed data is corrupt(uncompressed size mismatch)");
                }
                if (!this.decompressConcatenated || !this.init(false)) {
                    this.inf.end();
                    this.inf = null;
                    this.endReached = true;
                    return (size == 0) ? -1 : size;
                }
                continue;
            }
        }
        return size;
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 2 && signature[0] == 31 && signature[1] == -117;
    }
    
    @Override
    public void close() throws IOException {
        if (this.inf != null) {
            this.inf.end();
            this.inf = null;
        }
        if (this.in != System.in) {
            this.in.close();
        }
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}
