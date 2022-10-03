package org.apache.commons.compress.compressors.deflate64;

import java.io.Closeable;
import org.apache.commons.compress.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.utils.InputStreamStatistics;
import org.apache.commons.compress.compressors.CompressorInputStream;

public class Deflate64CompressorInputStream extends CompressorInputStream implements InputStreamStatistics
{
    private InputStream originalStream;
    private HuffmanDecoder decoder;
    private long compressedBytesRead;
    private final byte[] oneByte;
    
    public Deflate64CompressorInputStream(final InputStream in) {
        this(new HuffmanDecoder(in));
        this.originalStream = in;
    }
    
    Deflate64CompressorInputStream(final HuffmanDecoder decoder) {
        this.oneByte = new byte[1];
        this.decoder = decoder;
    }
    
    @Override
    public int read() throws IOException {
        while (true) {
            final int r = this.read(this.oneByte);
            switch (r) {
                case 1: {
                    return this.oneByte[0] & 0xFF;
                }
                case -1: {
                    return -1;
                }
                case 0: {
                    continue;
                }
                default: {
                    throw new IllegalStateException("Invalid return value from read: " + r);
                }
            }
        }
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        int read = -1;
        if (this.decoder != null) {
            try {
                read = this.decoder.decode(b, off, len);
            }
            catch (final RuntimeException ex) {
                throw new IOException("Invalid Deflate64 input", ex);
            }
            this.compressedBytesRead = this.decoder.getBytesRead();
            this.count(read);
            if (read == -1) {
                this.closeDecoder();
            }
        }
        return read;
    }
    
    @Override
    public int available() throws IOException {
        return (this.decoder != null) ? this.decoder.available() : 0;
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.closeDecoder();
        }
        finally {
            if (this.originalStream != null) {
                this.originalStream.close();
                this.originalStream = null;
            }
        }
    }
    
    @Override
    public long getCompressedCount() {
        return this.compressedBytesRead;
    }
    
    private void closeDecoder() {
        IOUtils.closeQuietly(this.decoder);
        this.decoder = null;
    }
}
