package org.tukaani.xz;

import org.tukaani.xz.rangecoder.RangeDecoder;
import java.io.IOException;
import org.tukaani.xz.lzma.LZMADecoder;
import org.tukaani.xz.rangecoder.RangeDecoderFromBuffer;
import org.tukaani.xz.lz.LZDecoder;
import java.io.DataInputStream;
import java.io.InputStream;

public class LZMA2InputStream extends InputStream
{
    public static final int DICT_SIZE_MIN = 4096;
    public static final int DICT_SIZE_MAX = 2147483632;
    private static final int COMPRESSED_SIZE_MAX = 65536;
    private final ArrayCache arrayCache;
    private DataInputStream in;
    private LZDecoder lz;
    private RangeDecoderFromBuffer rc;
    private LZMADecoder lzma;
    private int uncompressedSize;
    private boolean isLZMAChunk;
    private boolean needDictReset;
    private boolean needProps;
    private boolean endReached;
    private IOException exception;
    private final byte[] tempBuf;
    
    public static int getMemoryUsage(final int n) {
        return 104 + getDictSize(n) / 1024;
    }
    
    private static int getDictSize(final int n) {
        if (n < 4096 || n > 2147483632) {
            throw new IllegalArgumentException("Unsupported dictionary size " + n);
        }
        return n + 15 & 0xFFFFFFF0;
    }
    
    public LZMA2InputStream(final InputStream inputStream, final int n) {
        this(inputStream, n, null);
    }
    
    public LZMA2InputStream(final InputStream inputStream, final int n, final byte[] array) {
        this(inputStream, n, array, ArrayCache.getDefaultCache());
    }
    
    LZMA2InputStream(final InputStream inputStream, final int n, final byte[] array, final ArrayCache arrayCache) {
        this.uncompressedSize = 0;
        this.isLZMAChunk = false;
        this.needDictReset = true;
        this.needProps = true;
        this.endReached = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        if (inputStream == null) {
            throw new NullPointerException();
        }
        this.arrayCache = arrayCache;
        this.in = new DataInputStream(inputStream);
        this.rc = new RangeDecoderFromBuffer(65536, arrayCache);
        this.lz = new LZDecoder(getDictSize(n), array, arrayCache);
        if (array != null && array.length > 0) {
            this.needDictReset = false;
        }
    }
    
    @Override
    public int read() throws IOException {
        return (this.read(this.tempBuf, 0, 1) == -1) ? -1 : (this.tempBuf[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] array, int n, int i) throws IOException {
        if (n < 0 || i < 0 || n + i < 0 || n + i > array.length) {
            throw new IndexOutOfBoundsException();
        }
        if (i == 0) {
            return 0;
        }
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        if (this.endReached) {
            return -1;
        }
        try {
            int n2 = 0;
            while (i > 0) {
                if (this.uncompressedSize == 0) {
                    this.decodeChunkHeader();
                    if (this.endReached) {
                        return (n2 == 0) ? -1 : n2;
                    }
                }
                final int min = Math.min(this.uncompressedSize, i);
                if (!this.isLZMAChunk) {
                    this.lz.copyUncompressed(this.in, min);
                }
                else {
                    this.lz.setLimit(min);
                    this.lzma.decode();
                }
                final int flush = this.lz.flush(array, n);
                n += flush;
                i -= flush;
                n2 += flush;
                this.uncompressedSize -= flush;
                if (this.uncompressedSize == 0 && (!this.rc.isFinished() || this.lz.hasPending())) {
                    throw new CorruptedInputException();
                }
            }
            return n2;
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void decodeChunkHeader() throws IOException {
        final int unsignedByte = this.in.readUnsignedByte();
        if (unsignedByte == 0) {
            this.endReached = true;
            this.putArraysToCache();
            return;
        }
        if (unsignedByte >= 224 || unsignedByte == 1) {
            this.needProps = true;
            this.needDictReset = false;
            this.lz.reset();
        }
        else if (this.needDictReset) {
            throw new CorruptedInputException();
        }
        if (unsignedByte >= 128) {
            this.isLZMAChunk = true;
            this.uncompressedSize = (unsignedByte & 0x1F) << 16;
            this.uncompressedSize += this.in.readUnsignedShort() + 1;
            final int n = this.in.readUnsignedShort() + 1;
            if (unsignedByte >= 192) {
                this.needProps = false;
                this.decodeProps();
            }
            else {
                if (this.needProps) {
                    throw new CorruptedInputException();
                }
                if (unsignedByte >= 160) {
                    this.lzma.reset();
                }
            }
            this.rc.prepareInputBuffer(this.in, n);
        }
        else {
            if (unsignedByte > 2) {
                throw new CorruptedInputException();
            }
            this.isLZMAChunk = false;
            this.uncompressedSize = this.in.readUnsignedShort() + 1;
        }
    }
    
    private void decodeProps() throws IOException {
        final int unsignedByte = this.in.readUnsignedByte();
        if (unsignedByte > 224) {
            throw new CorruptedInputException();
        }
        final int n = unsignedByte / 45;
        final int n2 = unsignedByte - n * 9 * 5;
        final int n3 = n2 / 9;
        final int n4 = n2 - n3 * 9;
        if (n4 + n3 > 4) {
            throw new CorruptedInputException();
        }
        this.lzma = new LZMADecoder(this.lz, this.rc, n4, n3, n);
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return this.isLZMAChunk ? this.uncompressedSize : Math.min(this.uncompressedSize, this.in.available());
    }
    
    private void putArraysToCache() {
        if (this.lz != null) {
            this.lz.putArraysToCache(this.arrayCache);
            this.lz = null;
            this.rc.putArraysToCache(this.arrayCache);
            this.rc = null;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            this.putArraysToCache();
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
}
