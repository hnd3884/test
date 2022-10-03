package org.tukaani.xz;

import org.tukaani.xz.rangecoder.RangeDecoder;
import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.lzma.LZMADecoder;
import org.tukaani.xz.rangecoder.RangeDecoderFromStream;
import org.tukaani.xz.lz.LZDecoder;
import java.io.InputStream;

public class LZMAInputStream extends InputStream
{
    public static final int DICT_SIZE_MAX = 2147483632;
    private InputStream in;
    private ArrayCache arrayCache;
    private LZDecoder lz;
    private RangeDecoderFromStream rc;
    private LZMADecoder lzma;
    private boolean endReached;
    private boolean relaxedEndCondition;
    private final byte[] tempBuf;
    private long remainingSize;
    private IOException exception;
    
    public static int getMemoryUsage(final int n, final byte b) throws UnsupportedOptionsException, CorruptedInputException {
        if (n < 0 || n > 2147483632) {
            throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
        }
        final int n2 = b & 0xFF;
        if (n2 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
        }
        final int n3 = n2 % 45;
        final int n4 = n3 / 9;
        return getMemoryUsage(n, n3 - n4 * 9, n4);
    }
    
    public static int getMemoryUsage(final int n, final int n2, final int n3) {
        if (n2 < 0 || n2 > 8 || n3 < 0 || n3 > 4) {
            throw new IllegalArgumentException("Invalid lc or lp");
        }
        return 10 + getDictSize(n) / 1024 + (1536 << n2 + n3) / 1024;
    }
    
    private static int getDictSize(int n) {
        if (n < 0 || n > 2147483632) {
            throw new IllegalArgumentException("LZMA dictionary is too big for this implementation");
        }
        if (n < 4096) {
            n = 4096;
        }
        return n + 15 & 0xFFFFFFF0;
    }
    
    public LZMAInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, -1);
    }
    
    public LZMAInputStream(final InputStream inputStream, final ArrayCache arrayCache) throws IOException {
        this(inputStream, -1, arrayCache);
    }
    
    public LZMAInputStream(final InputStream inputStream, final int n) throws IOException {
        this(inputStream, n, ArrayCache.getDefaultCache());
    }
    
    public LZMAInputStream(final InputStream inputStream, final int n, final ArrayCache arrayCache) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        final DataInputStream dataInputStream = new DataInputStream(inputStream);
        final byte byte1 = dataInputStream.readByte();
        int n2 = 0;
        for (int i = 0; i < 4; ++i) {
            n2 |= dataInputStream.readUnsignedByte() << 8 * i;
        }
        long n3 = 0L;
        for (int j = 0; j < 8; ++j) {
            n3 |= (long)dataInputStream.readUnsignedByte() << 8 * j;
        }
        final int memoryUsage = getMemoryUsage(n2, byte1);
        if (n != -1 && memoryUsage > n) {
            throw new MemoryLimitException(memoryUsage, n);
        }
        this.initialize(inputStream, n3, byte1, n2, null, arrayCache);
    }
    
    public LZMAInputStream(final InputStream inputStream, final long n, final byte b, final int n2) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        this.initialize(inputStream, n, b, n2, null, ArrayCache.getDefaultCache());
    }
    
    public LZMAInputStream(final InputStream inputStream, final long n, final byte b, final int n2, final byte[] array) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        this.initialize(inputStream, n, b, n2, array, ArrayCache.getDefaultCache());
    }
    
    public LZMAInputStream(final InputStream inputStream, final long n, final byte b, final int n2, final byte[] array, final ArrayCache arrayCache) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        this.initialize(inputStream, n, b, n2, array, arrayCache);
    }
    
    public LZMAInputStream(final InputStream inputStream, final long n, final int n2, final int n3, final int n4, final int n5, final byte[] array) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        this.initialize(inputStream, n, n2, n3, n4, n5, array, ArrayCache.getDefaultCache());
    }
    
    public LZMAInputStream(final InputStream inputStream, final long n, final int n2, final int n3, final int n4, final int n5, final byte[] array, final ArrayCache arrayCache) throws IOException {
        this.endReached = false;
        this.relaxedEndCondition = false;
        this.tempBuf = new byte[1];
        this.exception = null;
        this.initialize(inputStream, n, n2, n3, n4, n5, array, arrayCache);
    }
    
    private void initialize(final InputStream inputStream, final long n, final byte b, final int n2, final byte[] array, final ArrayCache arrayCache) throws IOException {
        if (n < -1L) {
            throw new UnsupportedOptionsException("Uncompressed size is too big");
        }
        final int n3 = b & 0xFF;
        if (n3 > 224) {
            throw new CorruptedInputException("Invalid LZMA properties byte");
        }
        final int n4 = n3 / 45;
        final int n5 = n3 - n4 * 9 * 5;
        final int n6 = n5 / 9;
        final int n7 = n5 - n6 * 9;
        if (n2 < 0 || n2 > 2147483632) {
            throw new UnsupportedOptionsException("LZMA dictionary is too big for this implementation");
        }
        this.initialize(inputStream, n, n7, n6, n4, n2, array, arrayCache);
    }
    
    private void initialize(final InputStream in, final long remainingSize, final int n, final int n2, final int n3, int n4, final byte[] array, final ArrayCache arrayCache) throws IOException {
        if (remainingSize < -1L || n < 0 || n > 8 || n2 < 0 || n2 > 4 || n3 < 0 || n3 > 4) {
            throw new IllegalArgumentException();
        }
        this.in = in;
        this.arrayCache = arrayCache;
        n4 = getDictSize(n4);
        if (remainingSize >= 0L && n4 > remainingSize) {
            n4 = getDictSize((int)remainingSize);
        }
        this.lz = new LZDecoder(getDictSize(n4), array, arrayCache);
        this.rc = new RangeDecoderFromStream(in);
        this.lzma = new LZMADecoder(this.lz, this.rc, n, n2, n3);
        this.remainingSize = remainingSize;
    }
    
    public void enableRelaxedEndCondition() {
        this.relaxedEndCondition = true;
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
                int limit = i;
                if (this.remainingSize >= 0L && this.remainingSize < i) {
                    limit = (int)this.remainingSize;
                }
                this.lz.setLimit(limit);
                try {
                    this.lzma.decode();
                }
                catch (final CorruptedInputException ex) {
                    if (this.remainingSize != -1L || !this.lzma.endMarkerDetected()) {
                        throw ex;
                    }
                    this.endReached = true;
                    this.rc.normalize();
                }
                final int flush = this.lz.flush(array, n);
                n += flush;
                i -= flush;
                n2 += flush;
                if (this.remainingSize >= 0L) {
                    this.remainingSize -= flush;
                    assert this.remainingSize >= 0L;
                    if (this.remainingSize == 0L) {
                        this.endReached = true;
                    }
                }
                if (this.endReached) {
                    if (this.lz.hasPending() || (!this.relaxedEndCondition && !this.rc.isFinished())) {
                        throw new CorruptedInputException();
                    }
                    this.putArraysToCache();
                    return (n2 == 0) ? -1 : n2;
                }
            }
            return n2;
        }
        catch (final IOException exception) {
            throw this.exception = exception;
        }
    }
    
    private void putArraysToCache() {
        if (this.lz != null) {
            this.lz.putArraysToCache(this.arrayCache);
            this.lz = null;
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
