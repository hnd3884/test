package com.sun.xml.internal.stream.writers;

import java.io.IOException;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import java.io.OutputStream;
import java.io.Writer;

public final class UTF8OutputStreamWriter extends Writer
{
    OutputStream out;
    int lastUTF16CodePoint;
    
    public UTF8OutputStreamWriter(final OutputStream out) {
        this.lastUTF16CodePoint = 0;
        this.out = out;
    }
    
    public String getEncoding() {
        return "UTF-8";
    }
    
    @Override
    public void write(final int c) throws IOException {
        if (this.lastUTF16CodePoint == 0) {
            if (c < 128) {
                this.out.write(c);
            }
            else if (c < 2048) {
                this.out.write(0xC0 | c >> 6);
                this.out.write(0x80 | (c & 0x3F));
            }
            else if (c <= 65535) {
                if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
                    this.out.write(0xE0 | c >> 12);
                    this.out.write(0x80 | (c >> 6 & 0x3F));
                    this.out.write(0x80 | (c & 0x3F));
                }
                else {
                    this.lastUTF16CodePoint = c;
                }
            }
            return;
        }
        final int uc = ((this.lastUTF16CodePoint & 0x3FF) << 10 | (c & 0x3FF)) + 65536;
        if (uc < 0 || uc >= 2097152) {
            throw new IOException("Atttempting to write invalid Unicode code point '" + uc + "'");
        }
        this.out.write(0xF0 | uc >> 18);
        this.out.write(0x80 | (uc >> 12 & 0x3F));
        this.out.write(0x80 | (uc >> 6 & 0x3F));
        this.out.write(0x80 | (uc & 0x3F));
        this.lastUTF16CodePoint = 0;
    }
    
    @Override
    public void write(final char[] cbuf) throws IOException {
        for (int i = 0; i < cbuf.length; ++i) {
            this.write(cbuf[i]);
        }
    }
    
    @Override
    public void write(final char[] cbuf, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(cbuf[off + i]);
        }
    }
    
    @Override
    public void write(final String str) throws IOException {
        for (int len = str.length(), i = 0; i < len; ++i) {
            this.write(str.charAt(i));
        }
    }
    
    @Override
    public void write(final String str, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(str.charAt(off + i));
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        if (this.lastUTF16CodePoint != 0) {
            throw new IllegalStateException("Attempting to close a UTF8OutputStreamWriter while awaiting for a UTF-16 code unit");
        }
        this.out.close();
    }
}
