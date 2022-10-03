package org.apache.commons.compress.compressors.xz;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.compress.MemoryLimitException;
import org.tukaani.xz.SingleXZInputStream;
import org.tukaani.xz.XZInputStream;
import java.io.IOException;
import org.tukaani.xz.XZ;
import java.io.InputStream;
import org.apache.commons.compress.utils.CountingInputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class XZCompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private final CountingInputStream countingStream;
    private final InputStream in;
    
    public static boolean matches(final byte[] signature, final int length) {
        if (length < XZ.HEADER_MAGIC.length) {
            return false;
        }
        for (int i = 0; i < XZ.HEADER_MAGIC.length; ++i) {
            if (signature[i] != XZ.HEADER_MAGIC[i]) {
                return false;
            }
        }
        return true;
    }
    
    public XZCompressorInputStream(final InputStream inputStream) throws IOException {
        this(inputStream, false);
    }
    
    public XZCompressorInputStream(final InputStream inputStream, final boolean decompressConcatenated) throws IOException {
        this(inputStream, decompressConcatenated, -1);
    }
    
    public XZCompressorInputStream(final InputStream inputStream, final boolean decompressConcatenated, final int memoryLimitInKb) throws IOException {
        this.countingStream = new CountingInputStream(inputStream);
        if (decompressConcatenated) {
            this.in = (InputStream)new XZInputStream((InputStream)this.countingStream, memoryLimitInKb);
        }
        else {
            this.in = (InputStream)new SingleXZInputStream((InputStream)this.countingStream, memoryLimitInKb);
        }
    }
    
    @Override
    public int read() throws IOException {
        try {
            final int ret = this.in.read();
            this.count((ret == -1) ? -1 : 1);
            return ret;
        }
        catch (final org.tukaani.xz.MemoryLimitException e) {
            throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), (Exception)e);
        }
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        try {
            final int ret = this.in.read(buf, off, len);
            this.count(ret);
            return ret;
        }
        catch (final org.tukaani.xz.MemoryLimitException e) {
            throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), (Exception)e);
        }
    }
    
    @Override
    public long skip(final long n) throws IOException {
        try {
            return IOUtils.skip(this.in, n);
        }
        catch (final org.tukaani.xz.MemoryLimitException e) {
            throw new MemoryLimitException(e.getMemoryNeeded(), e.getMemoryLimit(), (Exception)e);
        }
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available();
    }
    
    @Override
    public void close() throws IOException {
        this.in.close();
    }
    
    @Override
    public long getCompressedCount() {
        return this.countingStream.getBytesRead();
    }
}
