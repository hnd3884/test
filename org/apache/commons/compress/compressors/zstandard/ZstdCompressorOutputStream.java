package org.apache.commons.compress.compressors.zstandard;

import java.io.IOException;
import java.io.OutputStream;
import com.github.luben.zstd.ZstdOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class ZstdCompressorOutputStream extends CompressorOutputStream
{
    private final ZstdOutputStream encOS;
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level, final boolean closeFrameOnFlush, final boolean useChecksum) throws IOException {
        (this.encOS = new ZstdOutputStream(outStream, level)).setCloseFrameOnFlush(closeFrameOnFlush);
        this.encOS.setChecksum(useChecksum);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level, final boolean closeFrameOnFlush) throws IOException {
        (this.encOS = new ZstdOutputStream(outStream, level)).setCloseFrameOnFlush(closeFrameOnFlush);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream, final int level) throws IOException {
        this.encOS = new ZstdOutputStream(outStream, level);
    }
    
    public ZstdCompressorOutputStream(final OutputStream outStream) throws IOException {
        this.encOS = new ZstdOutputStream(outStream);
    }
    
    @Override
    public void close() throws IOException {
        this.encOS.close();
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.encOS.write(b);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws IOException {
        this.encOS.write(buf, off, len);
    }
    
    @Override
    public String toString() {
        return this.encOS.toString();
    }
    
    @Override
    public void flush() throws IOException {
        this.encOS.flush();
    }
}
