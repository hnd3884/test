package sun.security.jgss;

import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class GSSToken
{
    public static final void writeLittleEndian(final int n, final byte[] array) {
        writeLittleEndian(n, array, 0);
    }
    
    public static final void writeLittleEndian(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)n;
        array[n2++] = (byte)(n >>> 8);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 24);
    }
    
    public static final void writeBigEndian(final int n, final byte[] array) {
        writeBigEndian(n, array, 0);
    }
    
    public static final void writeBigEndian(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 24);
        array[n2++] = (byte)(n >>> 16);
        array[n2++] = (byte)(n >>> 8);
        array[n2++] = (byte)n;
    }
    
    public static final int readLittleEndian(final byte[] array, int n, int i) {
        int n2 = 0;
        int n3 = 0;
        while (i > 0) {
            n2 += (array[n] & 0xFF) << n3;
            n3 += 8;
            ++n;
            --i;
        }
        return n2;
    }
    
    public static final int readBigEndian(final byte[] array, int n, int i) {
        int n2 = 0;
        int n3 = (i - 1) * 8;
        while (i > 0) {
            n2 += (array[n] & 0xFF) << n3;
            n3 -= 8;
            ++n;
            --i;
        }
        return n2;
    }
    
    public static final void writeInt(final int n, final OutputStream outputStream) throws IOException {
        outputStream.write(n >>> 8);
        outputStream.write(n);
    }
    
    public static final int writeInt(final int n, final byte[] array, int n2) {
        array[n2++] = (byte)(n >>> 8);
        array[n2++] = (byte)n;
        return n2;
    }
    
    public static final int readInt(final InputStream inputStream) throws IOException {
        return (0xFF & inputStream.read()) << 8 | (0xFF & inputStream.read());
    }
    
    public static final int readInt(final byte[] array, final int n) {
        return (0xFF & array[n]) << 8 | (0xFF & array[n + 1]);
    }
    
    public static final void readFully(final InputStream inputStream, final byte[] array) throws IOException {
        readFully(inputStream, array, 0, array.length);
    }
    
    public static final void readFully(final InputStream inputStream, final byte[] array, int n, int i) throws IOException {
        while (i > 0) {
            final int read = inputStream.read(array, n, i);
            if (read == -1) {
                throw new EOFException("Cannot read all " + i + " bytes needed to form this token!");
            }
            n += read;
            i -= read;
        }
    }
    
    public static final void debug(final String s) {
        System.err.print(s);
    }
    
    public static final String getHexBytes(final byte[] array) {
        return getHexBytes(array, 0, array.length);
    }
    
    public static final String getHexBytes(final byte[] array, final int n) {
        return getHexBytes(array, 0, n);
    }
    
    public static final String getHexBytes(final byte[] array, final int n, final int n2) {
        final StringBuffer sb = new StringBuffer();
        for (int i = n; i < n + n2; ++i) {
            final int n3 = array[i] >> 4 & 0xF;
            final int n4 = array[i] & 0xF;
            sb.append(Integer.toHexString(n3));
            sb.append(Integer.toHexString(n4));
            sb.append(' ');
        }
        return sb.toString();
    }
}
