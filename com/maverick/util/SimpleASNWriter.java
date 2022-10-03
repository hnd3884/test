package com.maverick.util;

import java.io.ByteArrayOutputStream;

public class SimpleASNWriter
{
    private ByteArrayOutputStream b;
    
    public SimpleASNWriter() {
        this.b = new ByteArrayOutputStream();
    }
    
    public void writeByte(final int n) {
        this.b.write(n);
    }
    
    public void writeData(final byte[] array) {
        this.writeLength(array.length);
        this.b.write(array, 0, array.length);
    }
    
    public void writeLength(final int n) {
        if (n < 128) {
            this.b.write(n);
        }
        else if (n < 256) {
            this.b.write(129);
            this.b.write(n);
        }
        else if (n < 65536) {
            this.b.write(130);
            this.b.write(n >>> 8);
            this.b.write(n);
        }
        else if (n < 16777216) {
            this.b.write(131);
            this.b.write(n >>> 16);
            this.b.write(n >>> 8);
            this.b.write(n);
        }
        else {
            this.b.write(132);
            this.b.write(n >>> 24);
            this.b.write(n >>> 16);
            this.b.write(n >>> 8);
            this.b.write(n);
        }
    }
    
    public byte[] toByteArray() {
        return this.b.toByteArray();
    }
}
