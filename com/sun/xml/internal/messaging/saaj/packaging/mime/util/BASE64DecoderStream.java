package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class BASE64DecoderStream extends FilterInputStream
{
    private byte[] buffer;
    private int bufsize;
    private int index;
    private static final char[] pem_array;
    private static final byte[] pem_convert_array;
    private byte[] decode_buffer;
    
    public BASE64DecoderStream(final InputStream in) {
        super(in);
        this.bufsize = 0;
        this.index = 0;
        this.decode_buffer = new byte[4];
        this.buffer = new byte[3];
    }
    
    @Override
    public int read() throws IOException {
        if (this.index >= this.bufsize) {
            this.decode();
            if (this.bufsize == 0) {
                return -1;
            }
            this.index = 0;
        }
        return this.buffer[this.index++] & 0xFF;
    }
    
    @Override
    public int read(final byte[] buf, final int off, final int len) throws IOException {
        int i = 0;
        while (i < len) {
            final int c;
            if ((c = this.read()) == -1) {
                if (i == 0) {
                    i = -1;
                    break;
                }
                break;
            }
            else {
                buf[off + i] = (byte)c;
                ++i;
            }
        }
        return i;
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int available() throws IOException {
        return this.in.available() * 3 / 4 + (this.bufsize - this.index);
    }
    
    private void decode() throws IOException {
        this.bufsize = 0;
        int got = 0;
        while (got < 4) {
            final int i = this.in.read();
            if (i == -1) {
                if (got == 0) {
                    return;
                }
                throw new IOException("Error in encoded stream, got " + got);
            }
            else {
                if ((i < 0 || i >= 256 || i != 61) && BASE64DecoderStream.pem_convert_array[i] == -1) {
                    continue;
                }
                this.decode_buffer[got++] = (byte)i;
            }
        }
        byte a = BASE64DecoderStream.pem_convert_array[this.decode_buffer[0] & 0xFF];
        byte b = BASE64DecoderStream.pem_convert_array[this.decode_buffer[1] & 0xFF];
        this.buffer[this.bufsize++] = (byte)((a << 2 & 0xFC) | (b >>> 4 & 0x3));
        if (this.decode_buffer[2] == 61) {
            return;
        }
        a = b;
        b = BASE64DecoderStream.pem_convert_array[this.decode_buffer[2] & 0xFF];
        this.buffer[this.bufsize++] = (byte)((a << 4 & 0xF0) | (b >>> 2 & 0xF));
        if (this.decode_buffer[3] == 61) {
            return;
        }
        a = b;
        b = BASE64DecoderStream.pem_convert_array[this.decode_buffer[3] & 0xFF];
        this.buffer[this.bufsize++] = (byte)((a << 6 & 0xC0) | (b & 0x3F));
    }
    
    public static byte[] decode(final byte[] inbuf) {
        int size = inbuf.length / 4 * 3;
        if (size == 0) {
            return inbuf;
        }
        if (inbuf[inbuf.length - 1] == 61) {
            --size;
            if (inbuf[inbuf.length - 2] == 61) {
                --size;
            }
        }
        final byte[] outbuf = new byte[size];
        int inpos = 0;
        int outpos = 0;
        for (size = inbuf.length; size > 0; size -= 4) {
            byte a = BASE64DecoderStream.pem_convert_array[inbuf[inpos++] & 0xFF];
            byte b = BASE64DecoderStream.pem_convert_array[inbuf[inpos++] & 0xFF];
            outbuf[outpos++] = (byte)((a << 2 & 0xFC) | (b >>> 4 & 0x3));
            if (inbuf[inpos] == 61) {
                return outbuf;
            }
            a = b;
            b = BASE64DecoderStream.pem_convert_array[inbuf[inpos++] & 0xFF];
            outbuf[outpos++] = (byte)((a << 4 & 0xF0) | (b >>> 2 & 0xF));
            if (inbuf[inpos] == 61) {
                return outbuf;
            }
            a = b;
            b = BASE64DecoderStream.pem_convert_array[inbuf[inpos++] & 0xFF];
            outbuf[outpos++] = (byte)((a << 6 & 0xC0) | (b & 0x3F));
        }
        return outbuf;
    }
    
    static {
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
        pem_convert_array = new byte[256];
        for (int i = 0; i < 255; ++i) {
            BASE64DecoderStream.pem_convert_array[i] = -1;
        }
        for (int i = 0; i < BASE64DecoderStream.pem_array.length; ++i) {
            BASE64DecoderStream.pem_convert_array[BASE64DecoderStream.pem_array[i]] = (byte)i;
        }
    }
}
