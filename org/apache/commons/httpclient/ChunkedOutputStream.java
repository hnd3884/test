package org.apache.commons.httpclient;

import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.IOException;
import java.io.OutputStream;

public class ChunkedOutputStream extends OutputStream
{
    private static final byte[] CRLF;
    private static final byte[] ENDCHUNK;
    private static final byte[] ZERO;
    private OutputStream stream;
    private byte[] cache;
    private int cachePosition;
    private boolean wroteLastChunk;
    
    public ChunkedOutputStream(final OutputStream stream, final int bufferSize) throws IOException {
        this.stream = null;
        this.cachePosition = 0;
        this.wroteLastChunk = false;
        this.cache = new byte[bufferSize];
        this.stream = stream;
    }
    
    public ChunkedOutputStream(final OutputStream stream) throws IOException {
        this(stream, 2048);
    }
    
    protected void flushCache() throws IOException {
        if (this.cachePosition > 0) {
            final byte[] chunkHeader = EncodingUtil.getAsciiBytes(Integer.toHexString(this.cachePosition) + "\r\n");
            this.stream.write(chunkHeader, 0, chunkHeader.length);
            this.stream.write(this.cache, 0, this.cachePosition);
            this.stream.write(ChunkedOutputStream.ENDCHUNK, 0, ChunkedOutputStream.ENDCHUNK.length);
            this.cachePosition = 0;
        }
    }
    
    protected void flushCacheWithAppend(final byte[] bufferToAppend, final int off, final int len) throws IOException {
        final byte[] chunkHeader = EncodingUtil.getAsciiBytes(Integer.toHexString(this.cachePosition + len) + "\r\n");
        this.stream.write(chunkHeader, 0, chunkHeader.length);
        this.stream.write(this.cache, 0, this.cachePosition);
        this.stream.write(bufferToAppend, off, len);
        this.stream.write(ChunkedOutputStream.ENDCHUNK, 0, ChunkedOutputStream.ENDCHUNK.length);
        this.cachePosition = 0;
    }
    
    protected void writeClosingChunk() throws IOException {
        this.stream.write(ChunkedOutputStream.ZERO, 0, ChunkedOutputStream.ZERO.length);
        this.stream.write(ChunkedOutputStream.CRLF, 0, ChunkedOutputStream.CRLF.length);
        this.stream.write(ChunkedOutputStream.ENDCHUNK, 0, ChunkedOutputStream.ENDCHUNK.length);
    }
    
    public void finish() throws IOException {
        if (!this.wroteLastChunk) {
            this.flushCache();
            this.writeClosingChunk();
            this.wroteLastChunk = true;
        }
    }
    
    public void write(final int b) throws IOException {
        this.cache[this.cachePosition] = (byte)b;
        ++this.cachePosition;
        if (this.cachePosition == this.cache.length) {
            this.flushCache();
        }
    }
    
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    public void write(final byte[] src, final int off, final int len) throws IOException {
        if (len >= this.cache.length - this.cachePosition) {
            this.flushCacheWithAppend(src, off, len);
        }
        else {
            System.arraycopy(src, off, this.cache, this.cachePosition, len);
            this.cachePosition += len;
        }
    }
    
    public void flush() throws IOException {
        this.stream.flush();
    }
    
    public void close() throws IOException {
        this.finish();
        super.close();
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
        ENDCHUNK = ChunkedOutputStream.CRLF;
        ZERO = new byte[] { 48 };
    }
}
