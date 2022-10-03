package org.apache.commons.compress.compressors.brotli;

import org.apache.commons.compress.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.brotli.dec.BrotliInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class BrotliCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final CountingInputStream countingStream;
    private final BrotliInputStream decIS;
    
    public BrotliCompressorInputStream(final InputStream in) throws IOException {
        this.decIS = new BrotliInputStream((InputStream)(this.countingStream = new CountingInputStream(in)));
    }
    
    @Override
    public int available() throws IOException {
        return this.decIS.available();
    }
    
    @Override
    public void close() throws IOException {
        this.decIS.close();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.decIS.read(b);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return IOUtils.skip((InputStream)this.decIS, n);
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        this.decIS.mark(readlimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.decIS.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.decIS.read();
        this.count((ret != -1) ? 1 : 0);
        return ret;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        final int ret = this.decIS.read(buf, off, len);
        this.count(ret);
        return ret;
    }
    
    @Override
    public String toString() {
        return this.decIS.toString();
    }
    
    @Override
    public synchronized void reset() throws IOException {
        this.decIS.reset();
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}
