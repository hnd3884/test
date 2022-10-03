package com.maverick.util;

import java.io.IOException;
import java.math.BigInteger;
import java.io.ByteArrayOutputStream;

public class ByteArrayWriter extends ByteArrayOutputStream
{
    public ByteArrayWriter() {
    }
    
    public ByteArrayWriter(final int n) {
        super(n);
    }
    
    public byte[] array() {
        return super.buf;
    }
    
    public void move(final int n) {
        super.count += n;
    }
    
    public void writeBigInteger(final BigInteger bigInteger) throws IOException {
        final byte[] byteArray = bigInteger.toByteArray();
        this.writeInt(byteArray.length);
        this.write(byteArray);
    }
    
    public void writeBoolean(final boolean b) {
        this.write(b ? 1 : 0);
    }
    
    public void writeBinaryString(final byte[] array) throws IOException {
        if (array == null) {
            this.writeInt(0);
        }
        else {
            this.writeBinaryString(array, 0, array.length);
        }
    }
    
    public void writeBinaryString(final byte[] array, final int n, final int n2) throws IOException {
        if (array == null) {
            this.writeInt(0);
        }
        else {
            this.writeInt(n2);
            this.write(array, n, n2);
        }
    }
    
    public void writeMPINT(final BigInteger bigInteger) {
        final short n = (short)((bigInteger.bitLength() + 7) / 8);
        final byte[] byteArray = bigInteger.toByteArray();
        this.writeShort((short)bigInteger.bitLength());
        if (byteArray[0] == 0) {
            this.write(byteArray, 1, n);
        }
        else {
            this.write(byteArray, 0, n);
        }
    }
    
    public void writeShort(final short n) {
        this.write(n >>> 8 & 0xFF);
        this.write(n >>> 0 & 0xFF);
    }
    
    public void writeInt(final long n) throws IOException {
        this.write(new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n });
    }
    
    public void writeInt(final int n) throws IOException {
        this.write(new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n });
    }
    
    public static byte[] encodeInt(final int n) {
        return new byte[] { (byte)(n >> 24), (byte)(n >> 16), (byte)(n >> 8), (byte)n };
    }
    
    public static void encodeInt(final byte[] array, int n, final int n2) {
        array[n++] = (byte)(n2 >> 24);
        array[n++] = (byte)(n2 >> 16);
        array[n++] = (byte)(n2 >> 8);
        array[n] = (byte)n2;
    }
    
    public void writeUINT32(final UnsignedInteger32 unsignedInteger32) throws IOException {
        this.writeInt(unsignedInteger32.longValue());
    }
    
    public void writeUINT64(final UnsignedInteger64 unsignedInteger64) throws IOException {
        final byte[] array = new byte[8];
        final byte[] byteArray = unsignedInteger64.bigIntValue().toByteArray();
        System.arraycopy(byteArray, 0, array, array.length - byteArray.length, byteArray.length);
        this.write(array);
    }
    
    public void writeUINT64(final long n) throws IOException {
        this.writeUINT64(new UnsignedInteger64(n));
    }
    
    public void writeString(final String s) throws IOException {
        this.writeString(s, ByteArrayReader.getCharsetEncoding());
    }
    
    public void writeString(final String s, final String s2) throws IOException {
        if (s == null) {
            this.writeInt(0);
        }
        else {
            byte[] array;
            if (ByteArrayReader.encode) {
                array = s.getBytes(s2);
            }
            else {
                array = s.getBytes();
            }
            this.writeInt(array.length);
            this.write(array);
        }
    }
}
