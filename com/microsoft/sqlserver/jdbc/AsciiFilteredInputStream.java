package com.microsoft.sqlserver.jdbc;

import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;

final class AsciiFilteredInputStream extends InputStream
{
    private final InputStream containedStream;
    private static final byte[] ASCII_FILTER;
    
    AsciiFilteredInputStream(final BaseInputStream containedStream) throws SQLServerException {
        if (BaseInputStream.logger.isLoggable(Level.FINER)) {
            BaseInputStream.logger.finer(containedStream.toString() + " wrapping in AsciiFilteredInputStream");
        }
        this.containedStream = containedStream;
    }
    
    @Override
    public void close() throws IOException {
        this.containedStream.close();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.containedStream.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.containedStream.available();
    }
    
    @Override
    public int read() throws IOException {
        final int value = this.containedStream.read();
        if (value >= 0 && value <= 255) {
            return AsciiFilteredInputStream.ASCII_FILTER[value];
        }
        return value;
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        final int bytesRead = this.containedStream.read(b);
        if (bytesRead > 0) {
            assert bytesRead <= b.length;
            for (int i = 0; i < bytesRead; ++i) {
                b[i] = AsciiFilteredInputStream.ASCII_FILTER[b[i] & 0xFF];
            }
        }
        return bytesRead;
    }
    
    @Override
    public int read(final byte[] b, final int offset, final int maxBytes) throws IOException {
        final int bytesRead = this.containedStream.read(b, offset, maxBytes);
        if (bytesRead > 0) {
            assert offset + bytesRead <= b.length;
            for (int i = 0; i < bytesRead; ++i) {
                b[offset + i] = AsciiFilteredInputStream.ASCII_FILTER[b[offset + i] & 0xFF];
            }
        }
        return bytesRead;
    }
    
    @Override
    public boolean markSupported() {
        return this.containedStream.markSupported();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.containedStream.mark(readLimit);
    }
    
    @Override
    public void reset() throws IOException {
        this.containedStream.reset();
    }
    
    static {
        ASCII_FILTER = new byte[256];
        for (int i = 0; i < 128; ++i) {
            AsciiFilteredInputStream.ASCII_FILTER[i] = (byte)i;
        }
        for (int i = 128; i < 256; ++i) {
            AsciiFilteredInputStream.ASCII_FILTER[i] = 63;
        }
    }
}
