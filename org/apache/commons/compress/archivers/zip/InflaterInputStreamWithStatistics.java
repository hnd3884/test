package org.apache.commons.compress.archivers.zip;

import java.io.IOException;
import java.util.zip.Inflater;
import java.io.InputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import java.util.zip.InflaterInputStream;

class InflaterInputStreamWithStatistics extends InflaterInputStream implements InputStreamStatistics
{
    private long compressedCount;
    private long uncompressedCount;
    
    public InflaterInputStreamWithStatistics(final InputStream in) {
        super(in);
    }
    
    public InflaterInputStreamWithStatistics(final InputStream in, final Inflater inf) {
        super(in, inf);
    }
    
    public InflaterInputStreamWithStatistics(final InputStream in, final Inflater inf, final int size) {
        super(in, inf, size);
    }
    
    @Override
    protected void fill() throws IOException {
        super.fill();
        this.compressedCount += this.inf.getRemaining();
    }
    
    @Override
    public int read() throws IOException {
        final int b = super.read();
        if (b > -1) {
            ++this.uncompressedCount;
        }
        return b;
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        final int bytes = super.read(b, off, len);
        if (bytes > -1) {
            this.uncompressedCount += bytes;
        }
        return bytes;
    }
    
    @Override
    public long getCompressedCount() {
        return this.compressedCount;
    }
    
    @Override
    public long getUncompressedCount() {
        return this.uncompressedCount;
    }
}
