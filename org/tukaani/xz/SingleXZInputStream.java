package org.tukaani.xz;

import org.tukaani.xz.common.DecoderUtil;
import java.io.DataInputStream;
import java.io.IOException;
import org.tukaani.xz.index.IndexHash;
import org.tukaani.xz.check.Check;
import org.tukaani.xz.common.StreamFlags;
import java.io.InputStream;

public class SingleXZInputStream extends InputStream
{
    private InputStream in;
    private final ArrayCache arrayCache;
    private final int memoryLimit;
    private final StreamFlags streamHeaderFlags;
    private final Check check;
    private final boolean verifyCheck;
    private BlockInputStream blockDecoder;
    private final IndexHash indexHash;
    private boolean endReached;
    private IOException exception;
    private final byte[] tempBuf;
    
    private static byte[] readStreamHeader(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[12];
        new DataInputStream(inputStream).readFully(array);
        return array;
    }
    
    public SingleXZInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, -1);
    }
    
    public SingleXZInputStream(final InputStream inputStream, final ArrayCache arrayCache) throws IOException {
        this(inputStream, -1, arrayCache);
    }
    
    public SingleXZInputStream(final InputStream inputStream, final int n) throws IOException {
        this(inputStream, n, true);
    }
    
    public SingleXZInputStream(final InputStream inputStream, final int n, final ArrayCache arrayCache) throws IOException {
        this(inputStream, n, true, arrayCache);
    }
    
    public SingleXZInputStream(final InputStream inputStream, final int n, final boolean b) throws IOException {
        this(inputStream, n, b, ArrayCache.getDefaultCache());
    }
    
    public SingleXZInputStream(final InputStream inputStream, final int n, final boolean b, final ArrayCache arrayCache) throws IOException {
        this(inputStream, n, b, readStreamHeader(inputStream), arrayCache);
    }
    
    SingleXZInputStream(final InputStream in, final int memoryLimit, final boolean verifyCheck, final byte[] array, final ArrayCache arrayCache) throws IOException {
        this.blockDecoder = null;
        this.indexHash = new IndexHash();
        this.endReached = false;
        this.exception = null;
        this.tempBuf = new byte[1];
        this.arrayCache = arrayCache;
        this.in = in;
        this.memoryLimit = memoryLimit;
        this.verifyCheck = verifyCheck;
        this.streamHeaderFlags = DecoderUtil.decodeStreamHeader(array);
        this.check = Check.getInstance(this.streamHeaderFlags.checkType);
    }
    
    public int getCheckType() {
        return this.streamHeaderFlags.checkType;
    }
    
    public String getCheckName() {
        return this.check.getName();
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
        int n2 = 0;
        try {
            while (i > 0) {
                if (this.blockDecoder == null) {
                    try {
                        this.blockDecoder = new BlockInputStream(this.in, this.check, this.verifyCheck, this.memoryLimit, -1L, -1L, this.arrayCache);
                    }
                    catch (final IndexIndicatorException ex) {
                        this.indexHash.validate(this.in);
                        this.validateStreamFooter();
                        this.endReached = true;
                        return (n2 > 0) ? n2 : -1;
                    }
                }
                final int read = this.blockDecoder.read(array, n, i);
                if (read > 0) {
                    n2 += read;
                    n += read;
                    i -= read;
                }
                else {
                    if (read != -1) {
                        continue;
                    }
                    this.indexHash.add(this.blockDecoder.getUnpaddedSize(), this.blockDecoder.getUncompressedSize());
                    this.blockDecoder = null;
                }
            }
        }
        catch (final IOException exception) {
            this.exception = exception;
            if (n2 == 0) {
                throw exception;
            }
        }
        return n2;
    }
    
    private void validateStreamFooter() throws IOException {
        final byte[] array = new byte[12];
        new DataInputStream(this.in).readFully(array);
        final StreamFlags decodeStreamFooter = DecoderUtil.decodeStreamFooter(array);
        if (!DecoderUtil.areStreamFlagsEqual(this.streamHeaderFlags, decodeStreamFooter) || this.indexHash.getIndexSize() != decodeStreamFooter.backwardSize) {
            throw new CorruptedInputException("XZ Stream Footer does not match Stream Header");
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new XZIOException("Stream closed");
        }
        if (this.exception != null) {
            throw this.exception;
        }
        return (this.blockDecoder == null) ? 0 : this.blockDecoder.available();
    }
    
    @Override
    public void close() throws IOException {
        this.close(true);
    }
    
    public void close(final boolean b) throws IOException {
        if (this.in != null) {
            if (this.blockDecoder != null) {
                this.blockDecoder.close();
                this.blockDecoder = null;
            }
            try {
                if (b) {
                    this.in.close();
                }
            }
            finally {
                this.in = null;
            }
        }
    }
}
