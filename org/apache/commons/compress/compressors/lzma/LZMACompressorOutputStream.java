package org.apache.commons.compress.compressors.lzma;

import java.io.IOException;
import org.tukaani.xz.LZMA2Options;
import java.io.OutputStream;
import org.tukaani.xz.LZMAOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;

public class LZMACompressorOutputStream extends CompressorOutputStream
{
    private final LZMAOutputStream out;
    
    public LZMACompressorOutputStream(final OutputStream outputStream) throws IOException {
        this.out = new LZMAOutputStream(outputStream, new LZMA2Options(), -1L);
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.out.write(b);
    }
    
    @Override
    public void write(final byte[] buf, final int off, final int len) throws IOException {
        this.out.write(buf, off, len);
    }
    
    @Override
    public void flush() throws IOException {
    }
    
    public void finish() throws IOException {
        this.out.finish();
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
