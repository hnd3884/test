package javax.imageio.stream;

import java.io.UTFDataFormatException;
import java.nio.ByteOrder;
import java.io.IOException;

public abstract class ImageOutputStreamImpl extends ImageInputStreamImpl implements ImageOutputStream
{
    @Override
    public abstract void write(final int p0) throws IOException;
    
    @Override
    public void write(final byte[] array) throws IOException {
        this.write(array, 0, array.length);
    }
    
    @Override
    public abstract void write(final byte[] p0, final int p1, final int p2) throws IOException;
    
    @Override
    public void writeBoolean(final boolean b) throws IOException {
        this.write(b ? 1 : 0);
    }
    
    @Override
    public void writeByte(final int n) throws IOException {
        this.write(n);
    }
    
    @Override
    public void writeShort(final int n) throws IOException {
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            this.byteBuf[0] = (byte)(n >>> 8);
            this.byteBuf[1] = (byte)(n >>> 0);
        }
        else {
            this.byteBuf[0] = (byte)(n >>> 0);
            this.byteBuf[1] = (byte)(n >>> 8);
        }
        this.write(this.byteBuf, 0, 2);
    }
    
    @Override
    public void writeChar(final int n) throws IOException {
        this.writeShort(n);
    }
    
    @Override
    public void writeInt(final int n) throws IOException {
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            this.byteBuf[0] = (byte)(n >>> 24);
            this.byteBuf[1] = (byte)(n >>> 16);
            this.byteBuf[2] = (byte)(n >>> 8);
            this.byteBuf[3] = (byte)(n >>> 0);
        }
        else {
            this.byteBuf[0] = (byte)(n >>> 0);
            this.byteBuf[1] = (byte)(n >>> 8);
            this.byteBuf[2] = (byte)(n >>> 16);
            this.byteBuf[3] = (byte)(n >>> 24);
        }
        this.write(this.byteBuf, 0, 4);
    }
    
    @Override
    public void writeLong(final long n) throws IOException {
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            this.byteBuf[0] = (byte)(n >>> 56);
            this.byteBuf[1] = (byte)(n >>> 48);
            this.byteBuf[2] = (byte)(n >>> 40);
            this.byteBuf[3] = (byte)(n >>> 32);
            this.byteBuf[4] = (byte)(n >>> 24);
            this.byteBuf[5] = (byte)(n >>> 16);
            this.byteBuf[6] = (byte)(n >>> 8);
            this.byteBuf[7] = (byte)(n >>> 0);
        }
        else {
            this.byteBuf[0] = (byte)(n >>> 0);
            this.byteBuf[1] = (byte)(n >>> 8);
            this.byteBuf[2] = (byte)(n >>> 16);
            this.byteBuf[3] = (byte)(n >>> 24);
            this.byteBuf[4] = (byte)(n >>> 32);
            this.byteBuf[5] = (byte)(n >>> 40);
            this.byteBuf[6] = (byte)(n >>> 48);
            this.byteBuf[7] = (byte)(n >>> 56);
        }
        this.write(this.byteBuf, 0, 4);
        this.write(this.byteBuf, 4, 4);
    }
    
    @Override
    public void writeFloat(final float n) throws IOException {
        this.writeInt(Float.floatToIntBits(n));
    }
    
    @Override
    public void writeDouble(final double n) throws IOException {
        this.writeLong(Double.doubleToLongBits(n));
    }
    
    @Override
    public void writeBytes(final String s) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.write((byte)s.charAt(i));
        }
    }
    
    @Override
    public void writeChars(final String s) throws IOException {
        final int length = s.length();
        final byte[] array = new byte[length * 2];
        int n = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < length; ++i) {
                final char char1 = s.charAt(i);
                array[n++] = (byte)(char1 >>> 8);
                array[n++] = (byte)(char1 >>> 0);
            }
        }
        else {
            for (int j = 0; j < length; ++j) {
                final char char2 = s.charAt(j);
                array[n++] = (byte)(char2 >>> 0);
                array[n++] = (byte)(char2 >>> 8);
            }
        }
        this.write(array, 0, length * 2);
    }
    
    @Override
    public void writeUTF(final String s) throws IOException {
        final int length = s.length();
        int n = 0;
        final char[] array = new char[length];
        int n2 = 0;
        s.getChars(0, length, array, 0);
        for (final char c : array) {
            if (c >= '\u0001' && c <= '\u007f') {
                ++n;
            }
            else if (c > '\u07ff') {
                n += 3;
            }
            else {
                n += 2;
            }
        }
        if (n > 65535) {
            throw new UTFDataFormatException("utflen > 65536!");
        }
        final byte[] array2 = new byte[n + 2];
        array2[n2++] = (byte)(n >>> 8 & 0xFF);
        array2[n2++] = (byte)(n >>> 0 & 0xFF);
        for (final char c2 : array) {
            if (c2 >= '\u0001' && c2 <= '\u007f') {
                array2[n2++] = (byte)c2;
            }
            else if (c2 > '\u07ff') {
                array2[n2++] = (byte)(0xE0 | (c2 >> 12 & 0xF));
                array2[n2++] = (byte)(0x80 | (c2 >> 6 & 0x3F));
                array2[n2++] = (byte)(0x80 | (c2 >> 0 & 0x3F));
            }
            else {
                array2[n2++] = (byte)(0xC0 | (c2 >> 6 & 0x1F));
                array2[n2++] = (byte)(0x80 | (c2 >> 0 & 0x3F));
            }
        }
        this.write(array2, 0, n + 2);
    }
    
    @Override
    public void writeShorts(final short[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > s.length!");
        }
        final byte[] array2 = new byte[n2 * 2];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final short n4 = array[n + i];
                array2[n3++] = (byte)(n4 >>> 8);
                array2[n3++] = (byte)(n4 >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final short n5 = array[n + j];
                array2[n3++] = (byte)(n5 >>> 0);
                array2[n3++] = (byte)(n5 >>> 8);
            }
        }
        this.write(array2, 0, n2 * 2);
    }
    
    @Override
    public void writeChars(final char[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > c.length!");
        }
        final byte[] array2 = new byte[n2 * 2];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final char c = array[n + i];
                array2[n3++] = (byte)(c >>> 8);
                array2[n3++] = (byte)(c >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final char c2 = array[n + j];
                array2[n3++] = (byte)(c2 >>> 0);
                array2[n3++] = (byte)(c2 >>> 8);
            }
        }
        this.write(array2, 0, n2 * 2);
    }
    
    @Override
    public void writeInts(final int[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > i.length!");
        }
        final byte[] array2 = new byte[n2 * 4];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final int n4 = array[n + i];
                array2[n3++] = (byte)(n4 >>> 24);
                array2[n3++] = (byte)(n4 >>> 16);
                array2[n3++] = (byte)(n4 >>> 8);
                array2[n3++] = (byte)(n4 >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final int n5 = array[n + j];
                array2[n3++] = (byte)(n5 >>> 0);
                array2[n3++] = (byte)(n5 >>> 8);
                array2[n3++] = (byte)(n5 >>> 16);
                array2[n3++] = (byte)(n5 >>> 24);
            }
        }
        this.write(array2, 0, n2 * 4);
    }
    
    @Override
    public void writeLongs(final long[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > l.length!");
        }
        final byte[] array2 = new byte[n2 * 8];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final long n4 = array[n + i];
                array2[n3++] = (byte)(n4 >>> 56);
                array2[n3++] = (byte)(n4 >>> 48);
                array2[n3++] = (byte)(n4 >>> 40);
                array2[n3++] = (byte)(n4 >>> 32);
                array2[n3++] = (byte)(n4 >>> 24);
                array2[n3++] = (byte)(n4 >>> 16);
                array2[n3++] = (byte)(n4 >>> 8);
                array2[n3++] = (byte)(n4 >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final long n5 = array[n + j];
                array2[n3++] = (byte)(n5 >>> 0);
                array2[n3++] = (byte)(n5 >>> 8);
                array2[n3++] = (byte)(n5 >>> 16);
                array2[n3++] = (byte)(n5 >>> 24);
                array2[n3++] = (byte)(n5 >>> 32);
                array2[n3++] = (byte)(n5 >>> 40);
                array2[n3++] = (byte)(n5 >>> 48);
                array2[n3++] = (byte)(n5 >>> 56);
            }
        }
        this.write(array2, 0, n2 * 8);
    }
    
    @Override
    public void writeFloats(final float[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > f.length!");
        }
        final byte[] array2 = new byte[n2 * 4];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final int floatToIntBits = Float.floatToIntBits(array[n + i]);
                array2[n3++] = (byte)(floatToIntBits >>> 24);
                array2[n3++] = (byte)(floatToIntBits >>> 16);
                array2[n3++] = (byte)(floatToIntBits >>> 8);
                array2[n3++] = (byte)(floatToIntBits >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final int floatToIntBits2 = Float.floatToIntBits(array[n + j]);
                array2[n3++] = (byte)(floatToIntBits2 >>> 0);
                array2[n3++] = (byte)(floatToIntBits2 >>> 8);
                array2[n3++] = (byte)(floatToIntBits2 >>> 16);
                array2[n3++] = (byte)(floatToIntBits2 >>> 24);
            }
        }
        this.write(array2, 0, n2 * 4);
    }
    
    @Override
    public void writeDoubles(final double[] array, final int n, final int n2) throws IOException {
        if (n < 0 || n2 < 0 || n + n2 > array.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException("off < 0 || len < 0 || off + len > d.length!");
        }
        final byte[] array2 = new byte[n2 * 8];
        int n3 = 0;
        if (this.byteOrder == ByteOrder.BIG_ENDIAN) {
            for (int i = 0; i < n2; ++i) {
                final long doubleToLongBits = Double.doubleToLongBits(array[n + i]);
                array2[n3++] = (byte)(doubleToLongBits >>> 56);
                array2[n3++] = (byte)(doubleToLongBits >>> 48);
                array2[n3++] = (byte)(doubleToLongBits >>> 40);
                array2[n3++] = (byte)(doubleToLongBits >>> 32);
                array2[n3++] = (byte)(doubleToLongBits >>> 24);
                array2[n3++] = (byte)(doubleToLongBits >>> 16);
                array2[n3++] = (byte)(doubleToLongBits >>> 8);
                array2[n3++] = (byte)(doubleToLongBits >>> 0);
            }
        }
        else {
            for (int j = 0; j < n2; ++j) {
                final long doubleToLongBits2 = Double.doubleToLongBits(array[n + j]);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 0);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 8);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 16);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 24);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 32);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 40);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 48);
                array2[n3++] = (byte)(doubleToLongBits2 >>> 56);
            }
        }
        this.write(array2, 0, n2 * 8);
    }
    
    @Override
    public void writeBit(final int n) throws IOException {
        this.writeBits(0x1L & (long)n, 1);
    }
    
    @Override
    public void writeBits(final long n, int bitOffset) throws IOException {
        this.checkClosed();
        if (bitOffset < 0 || bitOffset > 64) {
            throw new IllegalArgumentException("Bad value for numBits!");
        }
        if (bitOffset == 0) {
            return;
        }
        if (this.getStreamPosition() > 0L || this.bitOffset > 0) {
            final int bitOffset2 = this.bitOffset;
            int read = this.read();
            if (read != -1) {
                this.seek(this.getStreamPosition() - 1L);
            }
            else {
                read = 0;
            }
            if (bitOffset + bitOffset2 < 8) {
                final int n2 = 8 - (bitOffset2 + bitOffset);
                final int n3 = -1 >>> 32 - bitOffset;
                this.write((int)((long)(read & ~(n3 << n2)) | (n & (long)n3) << n2));
                this.seek(this.getStreamPosition() - 1L);
                this.bitOffset = bitOffset2 + bitOffset;
                bitOffset = 0;
            }
            else {
                final int n4 = 8 - bitOffset2;
                final int n5 = -1 >>> 32 - n4;
                this.write((int)((long)(read & ~n5) | (n >> bitOffset - n4 & (long)n5)));
                bitOffset -= n4;
            }
        }
        if (bitOffset > 7) {
            final int n6 = bitOffset % 8;
            for (int i = bitOffset / 8; i > 0; --i) {
                final int n7 = (i - 1) * 8 + n6;
                this.write((int)((n7 == 0) ? (n & 0xFFL) : (n >> n7 & 0xFFL)));
            }
            bitOffset = n6;
        }
        if (bitOffset != 0) {
            int read2 = this.read();
            if (read2 != -1) {
                this.seek(this.getStreamPosition() - 1L);
            }
            else {
                read2 = 0;
            }
            final int n8 = 8 - bitOffset;
            final int n9 = -1 >>> 32 - bitOffset;
            this.write((int)((long)(read2 & ~(n9 << n8)) | (n & (long)n9) << n8));
            this.seek(this.getStreamPosition() - 1L);
            this.bitOffset = bitOffset;
        }
    }
    
    protected final void flushBits() throws IOException {
        this.checkClosed();
        if (this.bitOffset != 0) {
            final int bitOffset = this.bitOffset;
            final int read = this.read();
            int n;
            if (read < 0) {
                n = 0;
                this.bitOffset = 0;
            }
            else {
                this.seek(this.getStreamPosition() - 1L);
                n = (read & -1 << 8 - bitOffset);
            }
            this.write(n);
        }
    }
}
