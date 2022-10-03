package org.apache.commons.net.io;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public final class FromNetASCIIInputStream extends PushbackInputStream
{
    static final boolean _noConversionRequired;
    static final String _lineSeparator;
    static final byte[] _lineSeparatorBytes;
    private int __length;
    
    public static final boolean isConversionRequired() {
        return !FromNetASCIIInputStream._noConversionRequired;
    }
    
    public FromNetASCIIInputStream(final InputStream input) {
        super(input, FromNetASCIIInputStream._lineSeparatorBytes.length + 1);
        this.__length = 0;
    }
    
    private int __read() throws IOException {
        int ch = super.read();
        if (ch == 13) {
            ch = super.read();
            if (ch != 10) {
                if (ch != -1) {
                    this.unread(ch);
                }
                return 13;
            }
            this.unread(FromNetASCIIInputStream._lineSeparatorBytes);
            ch = super.read();
            --this.__length;
        }
        return ch;
    }
    
    @Override
    public int read() throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            return super.read();
        }
        return this.__read();
    }
    
    @Override
    public int read(final byte[] buffer) throws IOException {
        return this.read(buffer, 0, buffer.length);
    }
    
    @Override
    public int read(final byte[] buffer, int offset, final int length) throws IOException {
        if (FromNetASCIIInputStream._noConversionRequired) {
            return super.read(buffer, offset, length);
        }
        if (length < 1) {
            return 0;
        }
        int ch = this.available();
        this.__length = ((length > ch) ? ch : length);
        if (this.__length < 1) {
            this.__length = 1;
        }
        if ((ch = this.__read()) == -1) {
            return -1;
        }
        final int off = offset;
        int _length;
        do {
            buffer[offset++] = (byte)ch;
            _length = this.__length - 1;
            this.__length = _length;
        } while (_length > 0 && (ch = this.__read()) != -1);
        return offset - off;
    }
    
    @Override
    public int available() throws IOException {
        if (this.in == null) {
            throw new IOException("Stream closed");
        }
        return this.buf.length - this.pos + this.in.available();
    }
    
    static {
        _lineSeparator = System.getProperty("line.separator");
        _noConversionRequired = FromNetASCIIInputStream._lineSeparator.equals("\r\n");
        try {
            _lineSeparatorBytes = FromNetASCIIInputStream._lineSeparator.getBytes("US-ASCII");
        }
        catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("Broken JVM - cannot find US-ASCII charset!", e);
        }
    }
}
