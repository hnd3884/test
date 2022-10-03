package com.unboundid.util;

import java.io.IOException;
import java.io.InputStream;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class RateLimitedInputStream extends InputStream
{
    private final FixedRateBarrier rateLimiter;
    private final InputStream wrappedStream;
    private final int maxBytesPerRead;
    
    public RateLimitedInputStream(final InputStream wrappedStream, final int maxBytesPerSecond) {
        Validator.ensureTrue(wrappedStream != null, "RateLimitedInputStream.wrappedStream must not be null.");
        Validator.ensureTrue(maxBytesPerSecond > 0, "RateLimitedInputStream.maxBytesPerSecond must be greater than zero.  The provided value was " + maxBytesPerSecond);
        this.wrappedStream = wrappedStream;
        this.rateLimiter = new FixedRateBarrier(1000L, maxBytesPerSecond);
        this.maxBytesPerRead = Math.max(1, maxBytesPerSecond / 100);
    }
    
    @Override
    public void close() throws IOException {
        this.wrappedStream.close();
    }
    
    @Override
    public int read() throws IOException {
        this.rateLimiter.await();
        return this.wrappedStream.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int length) throws IOException {
        if (length <= 0) {
            return 0;
        }
        if (length <= this.maxBytesPerRead) {
            this.rateLimiter.await(length);
            return this.wrappedStream.read(b, offset, length);
        }
        int pos = offset;
        int remainingLength = length;
        int totalBytesRead = 0;
        while (remainingLength > 0) {
            final int lengthThisRead = Math.min(remainingLength, this.maxBytesPerRead);
            this.rateLimiter.await(lengthThisRead);
            final int bytesRead = this.wrappedStream.read(b, pos, lengthThisRead);
            if (bytesRead < 0) {
                break;
            }
            pos += bytesRead;
            totalBytesRead += bytesRead;
            remainingLength -= bytesRead;
        }
        return totalBytesRead;
    }
    
    @Override
    public int available() throws IOException {
        return this.wrappedStream.available();
    }
    
    @Override
    public boolean markSupported() {
        return this.wrappedStream.markSupported();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.wrappedStream.mark(readLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.wrappedStream.reset();
    }
}
