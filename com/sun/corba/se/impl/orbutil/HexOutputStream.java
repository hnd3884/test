package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.OutputStream;

public class HexOutputStream extends OutputStream
{
    private static final char[] hex;
    private StringWriter writer;
    
    public HexOutputStream(final StringWriter writer) {
        this.writer = writer;
    }
    
    @Override
    public synchronized void write(final int n) throws IOException {
        this.writer.write(HexOutputStream.hex[n >> 4 & 0xF]);
        this.writer.write(HexOutputStream.hex[n >> 0 & 0xF]);
    }
    
    @Override
    public synchronized void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    @Override
    public synchronized void write(final byte[] array, final int n, final int n2) throws IOException {
        for (int i = 0; i < n2; ++i) {
            this.write(array[n + i]);
        }
    }
    
    static {
        hex = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
