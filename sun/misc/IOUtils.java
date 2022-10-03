package sun.misc;

import java.util.Objects;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils
{
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final int MAX_BUFFER_SIZE = 2147483639;
    
    public static byte[] readExactlyNBytes(final InputStream inputStream, final int n) throws IOException {
        if (n < 0) {
            throw new IOException("length cannot be negative: " + n);
        }
        final byte[] nBytes = readNBytes(inputStream, n);
        if (nBytes.length < n) {
            throw new EOFException();
        }
        return nBytes;
    }
    
    public static byte[] readAllBytes(final InputStream inputStream) throws IOException {
        return readNBytes(inputStream, Integer.MAX_VALUE);
    }
    
    public static byte[] readNBytes(final InputStream inputStream, final int n) throws IOException {
        if (n < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        List<byte[]> list = null;
        byte[] array = null;
        int n2 = 0;
        int n3 = n;
        int read;
        do {
            byte[] array2;
            int n4;
            for (array2 = new byte[Math.min(n3, 8192)], n4 = 0; (read = inputStream.read(array2, n4, Math.min(array2.length - n4, n3))) > 0; n4 += read, n3 -= read) {}
            if (n4 > 0) {
                if (2147483639 - n2 < n4) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                n2 += n4;
                if (array == null) {
                    array = array2;
                }
                else {
                    if (list == null) {
                        list = new ArrayList<byte[]>();
                        list.add(array);
                    }
                    list.add(array2);
                }
            }
        } while (read >= 0 && n3 > 0);
        if (list != null) {
            final byte[] array3 = new byte[n2];
            int n5 = 0;
            int n6 = n2;
            for (final byte[] array4 : list) {
                final int min = Math.min(array4.length, n6);
                System.arraycopy(array4, 0, array3, n5, min);
                n5 += min;
                n6 -= min;
            }
            return array3;
        }
        if (array == null) {
            return new byte[0];
        }
        return (array.length == n2) ? array : Arrays.copyOf(array, n2);
    }
    
    public static int readNBytes(final InputStream inputStream, final byte[] array, final int n, final int n2) throws IOException {
        Objects.requireNonNull(array);
        if (n < 0 || n2 < 0 || n2 > array.length - n) {
            throw new IndexOutOfBoundsException();
        }
        int i;
        int read;
        for (i = 0; i < n2; i += read) {
            read = inputStream.read(array, n + i, n2 - i);
            if (read < 0) {
                break;
            }
        }
        return i;
    }
    
    public static byte[] readFully(final InputStream inputStream, final int n, final boolean b) throws IOException {
        if (n < 0) {
            throw new IOException("length cannot be negative: " + n);
        }
        if (b) {
            return readExactlyNBytes(inputStream, n);
        }
        return readNBytes(inputStream, n);
    }
}
