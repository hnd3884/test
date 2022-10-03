package com.sun.xml.internal.messaging.saaj.packaging.mime.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class BASE64EncoderStream extends FilterOutputStream
{
    private byte[] buffer;
    private int bufsize;
    private int count;
    private int bytesPerLine;
    private static final char[] pem_array;
    
    public BASE64EncoderStream(final OutputStream out, final int bytesPerLine) {
        super(out);
        this.bufsize = 0;
        this.count = 0;
        this.buffer = new byte[3];
        this.bytesPerLine = bytesPerLine;
    }
    
    public BASE64EncoderStream(final OutputStream out) {
        this(out, 76);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        for (int i = 0; i < len; ++i) {
            this.write(b[off + i]);
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final int c) throws IOException {
        this.buffer[this.bufsize++] = (byte)c;
        if (this.bufsize == 3) {
            this.encode();
            this.bufsize = 0;
        }
    }
    
    @Override
    public void flush() throws IOException {
        if (this.bufsize > 0) {
            this.encode();
            this.bufsize = 0;
        }
        this.out.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.out.close();
    }
    
    private void encode() throws IOException {
        if (this.count + 4 > this.bytesPerLine) {
            this.out.write(13);
            this.out.write(10);
            this.count = 0;
        }
        if (this.bufsize == 1) {
            final byte a = this.buffer[0];
            final byte b = 0;
            final byte c = 0;
            this.out.write(BASE64EncoderStream.pem_array[a >>> 2 & 0x3F]);
            this.out.write(BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(61);
            this.out.write(61);
        }
        else if (this.bufsize == 2) {
            final byte a = this.buffer[0];
            final byte b = this.buffer[1];
            final byte c = 0;
            this.out.write(BASE64EncoderStream.pem_array[a >>> 2 & 0x3F]);
            this.out.write(BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(BASE64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
            this.out.write(61);
        }
        else {
            final byte a = this.buffer[0];
            final byte b = this.buffer[1];
            final byte c = this.buffer[2];
            this.out.write(BASE64EncoderStream.pem_array[a >>> 2 & 0x3F]);
            this.out.write(BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)]);
            this.out.write(BASE64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)]);
            this.out.write(BASE64EncoderStream.pem_array[c & 0x3F]);
        }
        this.count += 4;
    }
    
    public static byte[] encode(final byte[] inbuf) {
        if (inbuf.length == 0) {
            return inbuf;
        }
        final byte[] outbuf = new byte[(inbuf.length + 2) / 3 * 4];
        int inpos = 0;
        int outpos = 0;
        for (int size = inbuf.length; size > 0; size -= 3) {
            if (size == 1) {
                final byte a = inbuf[inpos++];
                final byte b = 0;
                final byte c = 0;
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[a >>> 2 & 0x3F];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
                outbuf[outpos++] = 61;
                outbuf[outpos++] = 61;
            }
            else if (size == 2) {
                final byte a = inbuf[inpos++];
                final byte b = inbuf[inpos++];
                final byte c = 0;
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[a >>> 2 & 0x3F];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)];
                outbuf[outpos++] = 61;
            }
            else {
                final byte a = inbuf[inpos++];
                final byte b = inbuf[inpos++];
                final byte c = inbuf[inpos++];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[a >>> 2 & 0x3F];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)];
                outbuf[outpos++] = (byte)BASE64EncoderStream.pem_array[c & 0x3F];
            }
        }
        return outbuf;
    }
    
    static {
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    }
}
