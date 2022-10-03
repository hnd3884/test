package org.apache.tika.io;

import org.apache.tika.exception.TikaException;
import java.io.IOException;
import java.io.InputStream;

public class EndianUtils
{
    private static final int LONG_SIZE = 8;
    
    public static short readShortLE(final InputStream stream) throws IOException, BufferUnderrunException {
        return (short)readUShortLE(stream);
    }
    
    public static short readShortBE(final InputStream stream) throws IOException, BufferUnderrunException {
        return (short)readUShortBE(stream);
    }
    
    public static int readUShortLE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        if ((ch1 | ch2) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch2 << 8) + ch1;
    }
    
    public static int readUShortBE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        if ((ch1 | ch2) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch1 << 8) + ch2;
    }
    
    public static long readUIntLE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (long)((ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1) & 0xFFFFFFFFL;
    }
    
    public static long readUIntBE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (long)((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4) & 0xFFFFFFFFL;
    }
    
    public static int readIntLE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1;
    }
    
    public static int readIntBE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + ch4;
    }
    
    public static long readLongLE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        final int ch5 = stream.read();
        final int ch6 = stream.read();
        final int ch7 = stream.read();
        final int ch8 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
            throw new BufferUnderrunException();
        }
        return ((long)ch8 << 56) + ((long)ch7 << 48) + ((long)ch6 << 40) + ((long)ch5 << 32) + ((long)ch4 << 24) + (ch3 << 16) + (ch2 << 8) + ch1;
    }
    
    public static long readLongBE(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        final int ch5 = stream.read();
        final int ch6 = stream.read();
        final int ch7 = stream.read();
        final int ch8 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
            throw new BufferUnderrunException();
        }
        return ((long)ch1 << 56) + ((long)ch2 << 48) + ((long)ch3 << 40) + ((long)ch4 << 32) + ((long)ch5 << 24) + (ch6 << 16) + (ch7 << 8) + ch8;
    }
    
    public static long readUE7(final InputStream stream) throws IOException {
        long v = 0L;
        int i;
        while ((i = stream.read()) >= 0) {
            v <<= 7;
            if ((i & 0x80) != 0x80) {
                v += i;
                break;
            }
            v += (i & 0x7F);
        }
        if (i < 0) {
            throw new IOException("Buffer underun; expected one more byte");
        }
        return v;
    }
    
    public static short getShortLE(final byte[] data) {
        return getShortLE(data, 0);
    }
    
    public static short getShortLE(final byte[] data, final int offset) {
        return (short)getUShortLE(data, offset);
    }
    
    public static int getUShortLE(final byte[] data) {
        return getUShortLE(data, 0);
    }
    
    public static int getUShortLE(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        return (b2 << 8) + b0;
    }
    
    public static short getShortBE(final byte[] data) {
        return getShortBE(data, 0);
    }
    
    public static short getShortBE(final byte[] data, final int offset) {
        return (short)getUShortBE(data, offset);
    }
    
    public static int getUShortBE(final byte[] data) {
        return getUShortBE(data, 0);
    }
    
    public static int getUShortBE(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        return (b0 << 8) + b2;
    }
    
    public static int getIntLE(final byte[] data) {
        return getIntLE(data, 0);
    }
    
    public static int getIntLE(final byte[] data, final int offset) {
        int i = offset;
        final int b0 = data[i++] & 0xFF;
        final int b2 = data[i++] & 0xFF;
        final int b3 = data[i++] & 0xFF;
        final int b4 = data[i++] & 0xFF;
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + b0;
    }
    
    public static int getIntBE(final byte[] data) {
        return getIntBE(data, 0);
    }
    
    public static int getIntBE(final byte[] data, final int offset) {
        int i = offset;
        final int b0 = data[i++] & 0xFF;
        final int b2 = data[i++] & 0xFF;
        final int b3 = data[i++] & 0xFF;
        final int b4 = data[i++] & 0xFF;
        return (b0 << 24) + (b2 << 16) + (b3 << 8) + b4;
    }
    
    public static long getUIntLE(final byte[] data) {
        return getUIntLE(data, 0);
    }
    
    public static long getUIntLE(final byte[] data, final int offset) {
        final long retNum = getIntLE(data, offset);
        return retNum & 0xFFFFFFFFL;
    }
    
    public static long getUIntBE(final byte[] data) {
        return getUIntBE(data, 0);
    }
    
    public static long getUIntBE(final byte[] data, final int offset) {
        final long retNum = getIntBE(data, offset);
        return retNum & 0xFFFFFFFFL;
    }
    
    public static long getLongLE(final byte[] data, final int offset) {
        long result = 0L;
        for (int j = offset + 8 - 1; j >= offset; --j) {
            result <<= 8;
            result |= (0xFF & data[j]);
        }
        return result;
    }
    
    public static int ubyteToInt(final byte b) {
        return b & 0xFF;
    }
    
    public static short getUByte(final byte[] data, final int offset) {
        return (short)(data[offset] & 0xFF);
    }
    
    public static class BufferUnderrunException extends TikaException
    {
        private static final long serialVersionUID = 8358288231138076276L;
        
        public BufferUnderrunException() {
            super("Insufficient data left in stream for required read");
        }
    }
}
