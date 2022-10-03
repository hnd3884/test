package org.apache.lucene.store;

import java.io.IOException;

public final class RateLimitedIndexOutput extends IndexOutput
{
    private final IndexOutput delegate;
    private final RateLimiter rateLimiter;
    private long bytesSinceLastPause;
    private long currentMinPauseCheckBytes;
    
    public RateLimitedIndexOutput(final RateLimiter rateLimiter, final IndexOutput delegate) {
        super("RateLimitedIndexOutput(" + delegate + ")");
        this.delegate = delegate;
        this.rateLimiter = rateLimiter;
        this.currentMinPauseCheckBytes = rateLimiter.getMinPauseCheckBytes();
    }
    
    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
    
    @Override
    public long getFilePointer() {
        return this.delegate.getFilePointer();
    }
    
    @Override
    public long getChecksum() throws IOException {
        return this.delegate.getChecksum();
    }
    
    @Override
    public void writeByte(final byte b) throws IOException {
        ++this.bytesSinceLastPause;
        this.checkRate();
        this.delegate.writeByte(b);
    }
    
    @Override
    public void writeBytes(final byte[] b, final int offset, final int length) throws IOException {
        this.bytesSinceLastPause += length;
        this.checkRate();
        this.delegate.writeBytes(b, offset, length);
    }
    
    private void checkRate() throws IOException {
        if (this.bytesSinceLastPause > this.currentMinPauseCheckBytes) {
            this.rateLimiter.pause(this.bytesSinceLastPause);
            this.bytesSinceLastPause = 0L;
            this.currentMinPauseCheckBytes = this.rateLimiter.getMinPauseCheckBytes();
        }
    }
}
