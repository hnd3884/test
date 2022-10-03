package org.apache.axiom.util.base64;

import java.io.IOException;
import java.io.Writer;

public abstract class AbstractBase64DecodingWriter extends Writer
{
    private final char[] in;
    private final byte[] out;
    private int rest;
    
    public AbstractBase64DecodingWriter() {
        this.in = new char[4];
        this.out = new byte[3];
    }
    
    private static boolean isWhitespace(final int c) {
        return c <= 32 && (c == 32 || c == 10 || c == 13 || c == 9);
    }
    
    @Override
    public final void write(final char[] cbuf, int off, int len) throws IOException {
        while (len > 0) {
            this.write(cbuf[off]);
            ++off;
            --len;
        }
    }
    
    @Override
    public final void write(final String str, int off, int len) throws IOException {
        while (len > 0) {
            this.write(str.charAt(off));
            ++off;
            --len;
        }
    }
    
    @Override
    public final void write(final int c) throws IOException {
        if (!isWhitespace(c)) {
            this.in[this.rest++] = (char)c;
            if (this.rest == 4) {
                this.decode(this.in, 0);
                this.rest = 0;
            }
        }
    }
    
    private int decode(final char c) throws IOException {
        if (c == '=') {
            return -1;
        }
        if (c < Base64Constants.S_DECODETABLE.length) {
            final int result = Base64Constants.S_DECODETABLE[c];
            if (result >= 0) {
                return result;
            }
        }
        throw new IOException("Invalid base64 char '" + c + "'");
    }
    
    private void decode(final char[] data, final int off) throws IOException {
        int outlen = 3;
        if (data[off + 3] == '=') {
            outlen = 2;
        }
        if (data[off + 2] == '=') {
            outlen = 1;
        }
        final int b0 = this.decode(data[off]);
        final int b2 = this.decode(data[off + 1]);
        final int b3 = this.decode(data[off + 2]);
        final int b4 = this.decode(data[off + 3]);
        switch (outlen) {
            case 1: {
                this.out[0] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                break;
            }
            case 2: {
                this.out[0] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                this.out[1] = (byte)((b2 << 4 & 0xF0) | (b3 >> 2 & 0xF));
                break;
            }
            case 3: {
                this.out[0] = (byte)((b0 << 2 & 0xFC) | (b2 >> 4 & 0x3));
                this.out[1] = (byte)((b2 << 4 & 0xF0) | (b3 >> 2 & 0xF));
                this.out[2] = (byte)((b3 << 6 & 0xC0) | (b4 & 0x3F));
                break;
            }
        }
        this.doWrite(this.out, outlen);
    }
    
    protected abstract void doWrite(final byte[] p0, final int p1) throws IOException;
}
