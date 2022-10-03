package com.maverick.util;

import java.math.BigInteger;
import java.io.IOException;
import java.io.EOFException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayInputStream;

public class ByteArrayReader extends ByteArrayInputStream
{
    private static String b;
    public static boolean encode;
    
    public ByteArrayReader(final byte[] array, final int n, final int n2) {
        super(array, n, n2);
    }
    
    public ByteArrayReader(final byte[] array) {
        super(array, 0, array.length);
    }
    
    public byte[] array() {
        return super.buf;
    }
    
    public static void setCharsetEncoding(final String b) {
        try {
            "123456890".getBytes(b);
            ByteArrayReader.b = b;
            ByteArrayReader.encode = true;
        }
        catch (final UnsupportedEncodingException ex) {
            ByteArrayReader.b = "";
            ByteArrayReader.encode = false;
        }
    }
    
    public static String getCharsetEncoding() {
        return ByteArrayReader.b;
    }
    
    public void readFully(final byte[] array, final int n, final int n2) throws IOException {
        if (n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        int read;
        for (int i = 0; i < n2; i += read) {
            read = this.read(array, n + i, n2 - i);
            if (read < 0) {
                throw new EOFException("Could not read number of bytes requested: " + n2 + ", got " + i + " into buffer size " + array.length + " at offset " + n);
            }
        }
    }
    
    public boolean readBoolean() throws IOException {
        return this.read() == 1;
    }
    
    public void readFully(final byte[] array) throws IOException {
        this.readFully(array, 0, array.length);
    }
    
    public BigInteger readBigInteger() throws IOException {
        final byte[] array = new byte[(int)this.readInt()];
        this.readFully(array);
        return new BigInteger(array);
    }
    
    public UnsignedInteger64 readUINT64() throws IOException {
        final byte[] array = new byte[9];
        this.readFully(array, 1, 8);
        return new UnsignedInteger64(array);
    }
    
    public UnsignedInteger32 readUINT32() throws IOException {
        return new UnsignedInteger32(this.readInt());
    }
    
    public static long readInt(final byte[] array, final int n) {
        return ((long)(array[n] & 0xFF) << 24 & 0xFFFFFFFFL) | (long)((array[n + 1] & 0xFF) << 16) | (long)((array[n + 2] & 0xFF) << 8) | (long)((array[n + 3] & 0xFF) << 0);
    }
    
    public static short readShort(final byte[] array, final int n) {
        return (short)((array[n] & 0xFF) << 8 | (array[n + 1] & 0xFF) << 0);
    }
    
    public byte[] readBinaryString() throws IOException {
        final byte[] array = new byte[(int)this.readInt()];
        this.readFully(array);
        return array;
    }
    
    public long readInt() throws IOException {
        final int read = this.read();
        final int read2 = this.read();
        final int read3 = this.read();
        final int read4 = this.read();
        if ((read | read2 | read3 | read4) < 0) {
            throw new EOFException();
        }
        return (long)((read << 24) + (read2 << 16) + (read3 << 8) + (read4 << 0)) & 0xFFFFFFFFL;
    }
    
    public String readString() throws IOException {
        return this.readString(ByteArrayReader.b);
    }
    
    public String readString(final String s) throws IOException {
        final long int1 = this.readInt();
        if (int1 > this.available()) {
            throw new IOException("Cannot read string of length " + int1 + " bytes when only " + this.available() + " bytes are available");
        }
        final byte[] array = new byte[(int)int1];
        this.readFully(array);
        if (ByteArrayReader.encode) {
            return new String(array, s);
        }
        return new String(array);
    }
    
    public short readShort() throws IOException {
        final int read = this.read();
        final int read2 = this.read();
        if ((read | read2) < 0) {
            throw new EOFException();
        }
        return (short)((read << 8) + (read2 << 0));
    }
    
    public BigInteger readMPINT32() throws IOException {
        final byte[] array = new byte[((int)this.readInt() + 7) / 8 + 1];
        array[0] = 0;
        this.readFully(array, 1, array.length - 1);
        return new BigInteger(array);
    }
    
    public BigInteger readMPINT() throws IOException {
        final byte[] array = new byte[(this.readShort() + 7) / 8 + 1];
        array[0] = 0;
        this.readFully(array, 1, array.length - 1);
        return new BigInteger(array);
    }
    
    public int getPosition() {
        return super.pos;
    }
    
    static {
        setCharsetEncoding(ByteArrayReader.b = "UTF8");
    }
}
