package org.apache.commons.compress.compressors.zstandard;

import org.apache.commons.compress.utils.IOUtils;
import com.github.luben.zstd.BufferPool;
import java.io.IOException;
import java.io.InputStream;
import com.github.luben.zstd.ZstdInputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class ZstdCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final CountingInputStream countingStream;
    private final ZstdInputStream decIS;
    
    public ZstdCompressorInputStream(final InputStream in) throws IOException {
        this.decIS = new ZstdInputStream((InputStream)(this.countingStream = new CountingInputStream(in)));
    }
    
    public ZstdCompressorInputStream(final InputStream in, final BufferPool bufferPool) throws IOException {
        this.decIS = new ZstdInputStream((InputStream)(this.countingStream = new CountingInputStream(in)), bufferPool);
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
        return this.read(b, 0, b.length);
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
        if (len == 0) {
            return 0;
        }
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
