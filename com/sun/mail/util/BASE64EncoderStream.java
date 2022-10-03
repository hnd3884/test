package com.sun.mail.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public class BASE64EncoderStream extends FilterOutputStream
{
    private byte[] buffer;
    private int bufsize;
    private byte[] outbuf;
    private int count;
    private int bytesPerLine;
    private int lineLimit;
    private boolean noCRLF;
    private static byte[] newline;
    private static final char[] pem_array;
    
    public BASE64EncoderStream(final OutputStream out, int bytesPerLine) {
        super(out);
        this.bufsize = 0;
        this.count = 0;
        this.noCRLF = false;
        this.buffer = new byte[3];
        if (bytesPerLine == Integer.MAX_VALUE || bytesPerLine < 4) {
            this.noCRLF = true;
            bytesPerLine = 76;
        }
        bytesPerLine = bytesPerLine / 4 * 4;
        this.bytesPerLine = bytesPerLine;
        this.lineLimit = bytesPerLine / 4 * 3;
        if (this.noCRLF) {
            this.outbuf = new byte[bytesPerLine];
        }
        else {
            (this.outbuf = new byte[bytesPerLine + 2])[bytesPerLine] = 13;
            this.outbuf[bytesPerLine + 1] = 10;
        }
    }
    
    public BASE64EncoderStream(final OutputStream out) {
        this(out, 76);
    }
    
    @Override
    public synchronized void write(final byte[] b, int off, final int len) throws IOException {
        final int end = off + len;
        while (this.bufsize != 0 && off < end) {
            this.write(b[off++]);
        }
        int blen = (this.bytesPerLine - this.count) / 4 * 3;
        if (off + blen <= end) {
            int outlen = encodedSize(blen);
            if (!this.noCRLF) {
                this.outbuf[outlen++] = 13;
                this.outbuf[outlen++] = 10;
            }
            this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
            off += blen;
            this.count = 0;
        }
        while (off + this.lineLimit <= end) {
            this.out.write(encode(b, off, this.lineLimit, this.outbuf));
            off += this.lineLimit;
        }
        if (off + 3 <= end) {
            blen = end - off;
            blen = blen / 3 * 3;
            final int outlen = encodedSize(blen);
            this.out.write(encode(b, off, blen, this.outbuf), 0, outlen);
            off += blen;
            this.count += outlen;
        }
        while (off < end) {
            this.write(b[off]);
            ++off;
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public synchronized void write(final int c) throws IOException {
        this.buffer[this.bufsize++] = (byte)c;
        if (this.bufsize == 3) {
            this.encode();
            this.bufsize = 0;
        }
    }
    
    @Override
    public synchronized void flush() throws IOException {
        if (this.bufsize > 0) {
            this.encode();
            this.bufsize = 0;
        }
        this.out.flush();
    }
    
    @Override
    public synchronized void close() throws IOException {
        this.flush();
        if (this.count > 0 && !this.noCRLF) {
            this.out.write(BASE64EncoderStream.newline);
            this.out.flush();
        }
        this.out.close();
    }
    
    private void encode() throws IOException {
        final int osize = encodedSize(this.bufsize);
        this.out.write(encode(this.buffer, 0, this.bufsize, this.outbuf), 0, osize);
        this.count += osize;
        if (this.count >= this.bytesPerLine) {
            if (!this.noCRLF) {
                this.out.write(BASE64EncoderStream.newline);
            }
            this.count = 0;
        }
    }
    
    public static byte[] encode(final byte[] inbuf) {
        if (inbuf.length == 0) {
            return inbuf;
        }
        return encode(inbuf, 0, inbuf.length, null);
    }
    
    private static byte[] encode(final byte[] inbuf, final int off, int size, byte[] outbuf) {
        if (outbuf == null) {
            outbuf = new byte[encodedSize(size)];
        }
        int inpos = off;
        int outpos;
        for (outpos = 0; size >= 3; size -= 3, outpos += 4) {
            int val = inbuf[inpos++] & 0xFF;
            val <<= 8;
            val |= (inbuf[inpos++] & 0xFF);
            val <<= 8;
            val |= (inbuf[inpos++] & 0xFF);
            outbuf[outpos + 3] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 2] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 1] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 0] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
        }
        if (size == 1) {
            int val = inbuf[inpos++] & 0xFF;
            val <<= 4;
            outbuf[outpos + 2] = (outbuf[outpos + 3] = 61);
            outbuf[outpos + 1] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 0] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
        }
        else if (size == 2) {
            int val = inbuf[inpos++] & 0xFF;
            val <<= 8;
            val |= (inbuf[inpos++] & 0xFF);
            val <<= 2;
            outbuf[outpos + 3] = 61;
            outbuf[outpos + 2] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 1] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
            val >>= 6;
            outbuf[outpos + 0] = (byte)BASE64EncoderStream.pem_array[val & 0x3F];
        }
        return outbuf;
    }
    
    private static int encodedSize(final int size) {
        return (size + 2) / 3 * 4;
    }
    
    static {
        BASE64EncoderStream.newline = new byte[] { 13, 10 };
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    }
}
