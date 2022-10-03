package com.sun.xml.internal.org.jvnet.staxex;

import javax.xml.stream.XMLStreamException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamWriter;
import java.io.FilterOutputStream;

public class Base64EncoderStream extends FilterOutputStream
{
    private byte[] buffer;
    private int bufsize;
    private XMLStreamWriter outWriter;
    private static final char[] pem_array;
    
    public Base64EncoderStream(final OutputStream out) {
        super(out);
        this.bufsize = 0;
        this.buffer = new byte[3];
    }
    
    public Base64EncoderStream(final XMLStreamWriter outWriter, final OutputStream out) {
        super(out);
        this.bufsize = 0;
        this.buffer = new byte[3];
        this.outWriter = outWriter;
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
        try {
            this.outWriter.flush();
        }
        catch (final XMLStreamException ex) {
            Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        this.out.close();
    }
    
    private void encode() throws IOException {
        final char[] buf = new char[4];
        if (this.bufsize == 1) {
            final byte a = this.buffer[0];
            final byte b = 0;
            final byte c = 0;
            buf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
            buf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[3] = (buf[2] = '=');
        }
        else if (this.bufsize == 2) {
            final byte a = this.buffer[0];
            final byte b = this.buffer[1];
            final byte c = 0;
            buf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
            buf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[2] = Base64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)];
            buf[3] = '=';
        }
        else {
            final byte a = this.buffer[0];
            final byte b = this.buffer[1];
            final byte c = this.buffer[2];
            buf[0] = Base64EncoderStream.pem_array[a >>> 2 & 0x3F];
            buf[1] = Base64EncoderStream.pem_array[(a << 4 & 0x30) + (b >>> 4 & 0xF)];
            buf[2] = Base64EncoderStream.pem_array[(b << 2 & 0x3C) + (c >>> 6 & 0x3)];
            buf[3] = Base64EncoderStream.pem_array[c & 0x3F];
        }
        try {
            this.outWriter.writeCharacters(buf, 0, 4);
        }
        catch (final XMLStreamException ex) {
            Logger.getLogger(Base64EncoderStream.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException(ex);
        }
    }
    
    static {
        pem_array = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };
    }
}
