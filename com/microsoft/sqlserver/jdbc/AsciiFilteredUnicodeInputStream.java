package com.microsoft.sqlserver.jdbc;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.nio.charset.Charset;
import java.io.Reader;
import java.io.InputStream;

final class AsciiFilteredUnicodeInputStream extends InputStream
{
    private final Reader containedReader;
    private final Charset asciiCharSet;
    private final byte[] bSingleByte;
    
    static AsciiFilteredUnicodeInputStream MakeAsciiFilteredUnicodeInputStream(final BaseInputStream strm, final Reader rd) throws SQLServerException {
        if (BaseInputStream.logger.isLoggable(Level.FINER)) {
            BaseInputStream.logger.finer(strm.toString() + " wrapping in AsciiFilteredInputStream");
        }
        return new AsciiFilteredUnicodeInputStream(rd);
    }
    
    private AsciiFilteredUnicodeInputStream(final Reader rd) throws SQLServerException {
        this.bSingleByte = new byte[1];
        this.containedReader = rd;
        this.asciiCharSet = StandardCharsets.US_ASCII;
    }
    
    @Override
    public void close() throws IOException {
        this.containedReader.close();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.containedReader.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return 0;
    }
    
    @Override
    public int read() throws IOException {
        final int bytesRead = this.read(this.bSingleByte);
        return (-1 == bytesRead) ? -1 : (this.bSingleByte[0] & 0xFF);
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int offset, int maxBytes) throws IOException {
        final char[] tempBufferToHoldCharDataForConversion = new char[maxBytes];
        final int charsRead = this.containedReader.read(tempBufferToHoldCharDataForConversion);
        if (charsRead > 0) {
            if (charsRead < maxBytes) {
                maxBytes = charsRead;
            }
            final ByteBuffer encodedBuff = this.asciiCharSet.encode(CharBuffer.wrap(tempBufferToHoldCharDataForConversion));
            encodedBuff.get(b, offset, maxBytes);
        }
        return charsRead;
    }
    
    @Override
    public boolean markSupported() {
        return this.containedReader.markSupported();
    }
    
    @Override
    public void mark(final int readLimit) {
        try {
            this.containedReader.mark(readLimit);
        }
        catch (final IOException ex) {}
    }
    
    @Override
    public void reset() throws IOException {
        this.containedReader.reset();
    }
}
