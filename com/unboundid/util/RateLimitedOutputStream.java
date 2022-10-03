package com.unboundid.util;

import java.io.IOException;
import java.io.OutputStream;

@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class RateLimitedOutputStream extends OutputStream
{
    private final boolean autoFlush;
    private final FixedRateBarrier rateLimiter;
    private final OutputStream wrappedStream;
    private final int maxBytesPerWrite;
    
    public RateLimitedOutputStream(final OutputStream wrappedStream, final int maxBytesPerSecond, final boolean autoFlush) {
        Validator.ensureTrue(wrappedStream != null, "RateLimitedOutputStream.wrappedStream must not be null.");
        Validator.ensureTrue(maxBytesPerSecond > 0, "RateLimitedOutputStream.maxBytesPerSecond must be greater than zero.  The provided value was " + maxBytesPerSecond);
        this.wrappedStream = wrappedStream;
        this.autoFlush = autoFlush;
        this.rateLimiter = new FixedRateBarrier(1000L, maxBytesPerSecond);
        this.maxBytesPerWrite = Math.max(1, maxBytesPerSecond / 100);
    }
    
    @Override
    public void close() throws IOException {
        this.wrappedStream.close();
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.rateLimiter.await();
        this.wrappedStream.write(b);
        if (this.autoFlush) {
            this.wrappedStream.flush();
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int length) throws IOException {
        if (length <= 0) {
            return;
        }
        if (length <= this.maxBytesPerWrite) {
            this.rateLimiter.await(length);
            this.wrappedStream.write(b, offset, length);
        }
        else {
            int pos = offset;
            int lengthThisWrite;
            for (int remainingToWrite = length; remainingToWrite > 0; remainingToWrite -= lengthThisWrite) {
                lengthThisWrite = Math.min(remainingToWrite, this.maxBytesPerWrite);
                this.rateLimiter.await(lengthThisWrite);
                this.wrappedStream.write(b, pos, lengthThisWrite);
                pos += lengthThisWrite;
            }
        }
        if (this.autoFlush) {
            this.wrappedStream.flush();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.wrappedStream.flush();
    }
}
