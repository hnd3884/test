package org.apache.commons.compress.utils;

import java.io.IOException;
import java.util.Objects;
import java.util.zip.Checksum;
import java.io.InputStream;

public class ChecksumCalculatingInputStream extends InputStream
{
    private final InputStream in;
    private final Checksum checksum;
    
    public ChecksumCalculatingInputStream(final Checksum checksum, final InputStream inputStream) {
        Objects.requireNonNull(checksum, "checksum");
        Objects.requireNonNull(inputStream, "in");
        this.checksum = checksum;
        this.in = inputStream;
    }
    
    @Override
    public int read() throws IOException {
        final int ret = this.in.read();
        if (ret >= 0) {
            this.checksum.update(ret);
        }
        return ret;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (len == 0) {
            return 0;
        }
        final int ret = this.in.read(b, off, len);
        if (ret >= 0) {
            this.checksum.update(b, off, ret);
        }
        return ret;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.read() >= 0) {
            return 1L;
        }
        return 0L;
    }
    
    public long getValue() {
        return this.checksum.getValue();
    }
}
