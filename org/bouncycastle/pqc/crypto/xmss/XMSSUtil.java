package org.bouncycastle.pqc.crypto.xmss;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.Arrays;

public class XMSSUtil
{
    public static int log2(int n) {
        int n2 = 0;
        while ((n >>= 1) != 0) {
            ++n2;
        }
        return n2;
    }
    
    public static byte[] toBytesBigEndian(long n, final int n2) {
        final byte[] array = new byte[n2];
        for (int i = n2 - 1; i >= 0; --i) {
            array[i] = (byte)n;
            n >>>= 8;
        }
        return array;
    }
    
    public static void longToBigEndian(final long n, final byte[] array, final int n2) {
        if (array == null) {
            throw new NullPointerException("in == null");
        }
        if (array.length - n2 < 8) {
            throw new IllegalArgumentException("not enough space in array");
        }
        array[n2] = (byte)(n >> 56 & 0xFFL);
        array[n2 + 1] = (byte)(n >> 48 & 0xFFL);
        array[n2 + 2] = (byte)(n >> 40 & 0xFFL);
        array[n2 + 3] = (byte)(n >> 32 & 0xFFL);
        array[n2 + 4] = (byte)(n >> 24 & 0xFFL);
        array[n2 + 5] = (byte)(n >> 16 & 0xFFL);
        array[n2 + 6] = (byte)(n >> 8 & 0xFFL);
        array[n2 + 7] = (byte)(n & 0xFFL);
    }
    
    public static long bytesToXBigEndian(final byte[] array, final int n, final int n2) {
        if (array == null) {
            throw new NullPointerException("in == null");
        }
        long n3 = 0L;
        for (int i = n; i < n + n2; ++i) {
            n3 = (n3 << 8 | (long)(array[i] & 0xFF));
        }
        return n3;
    }
    
    public static byte[] cloneArray(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("in == null");
        }
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[i];
        }
        return array2;
    }
    
    public static byte[][] cloneArray(final byte[][] array) {
        if (hasNullPointer(array)) {
            throw new NullPointerException("in has null pointers");
        }
        final byte[][] array2 = new byte[array.length][];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = new byte[array[i].length];
            for (int j = 0; j < array[i].length; ++j) {
                array2[i][j] = array[i][j];
            }
        }
        return array2;
    }
    
    public static boolean areEqual(final byte[][] array, final byte[][] array2) {
        if (hasNullPointer(array) || hasNullPointer(array2)) {
            throw new NullPointerException("a or b == null");
        }
        for (int i = 0; i < array.length; ++i) {
            if (!Arrays.areEqual(array[i], array2[i])) {
                return false;
            }
        }
        return true;
    }
    
    public static void dumpByteArray(final byte[][] array) {
        if (hasNullPointer(array)) {
            throw new NullPointerException("x has null pointers");
        }
        for (int i = 0; i < array.length; ++i) {
            System.out.println(Hex.toHexString(array[i]));
        }
    }
    
    public static boolean hasNullPointer(final byte[][] array) {
        if (array == null) {
            return true;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    public static void copyBytesAtOffset(final byte[] array, final byte[] array2, final int n) {
        if (array == null) {
            throw new NullPointerException("dst == null");
        }
        if (array2 == null) {
            throw new NullPointerException("src == null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (array2.length + n > array.length) {
            throw new IllegalArgumentException("src length + offset must not be greater than size of destination");
        }
        for (int i = 0; i < array2.length; ++i) {
            array[n + i] = array2[i];
        }
    }
    
    public static byte[] extractBytesAtOffset(final byte[] array, final int n, final int n2) {
        if (array == null) {
            throw new NullPointerException("src == null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("offset hast to be >= 0");
        }
        if (n2 < 0) {
            throw new IllegalArgumentException("length hast to be >= 0");
        }
        if (n + n2 > array.length) {
            throw new IllegalArgumentException("offset + length must not be greater then size of source array");
        }
        final byte[] array2 = new byte[n2];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[n + i];
        }
        return array2;
    }
    
    public static boolean isIndexValid(final int n, final long n2) {
        if (n2 < 0L) {
            throw new IllegalStateException("index must not be negative");
        }
        return n2 < 1L << n;
    }
    
    public static int getDigestSize(final Digest digest) {
        if (digest == null) {
            throw new NullPointerException("digest == null");
        }
        final String algorithmName = digest.getAlgorithmName();
        if (algorithmName.equals("SHAKE128")) {
            return 32;
        }
        if (algorithmName.equals("SHAKE256")) {
            return 64;
        }
        return digest.getDigestSize();
    }
    
    public static long getTreeIndex(final long n, final int n2) {
        return n >> n2;
    }
    
    public static int getLeafIndex(final long n, final int n2) {
        return (int)(n & (1L << n2) - 1L);
    }
    
    public static byte[] serialize(final Object o) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(o);
        objectOutputStream.flush();
        return byteArrayOutputStream.toByteArray();
    }
    
    public static Object deserialize(final byte[] array) throws IOException, ClassNotFoundException {
        return new ObjectInputStream(new ByteArrayInputStream(array)).readObject();
    }
    
    public static int calculateTau(final int n, final int n2) {
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            if ((n >> i & 0x1) == 0x0) {
                n3 = i;
                break;
            }
        }
        return n3;
    }
    
    public static boolean isNewBDSInitNeeded(final long n, final int n2, final int n3) {
        return n != 0L && n % (long)Math.pow(1 << n2, n3 + 1) == 0L;
    }
    
    public static boolean isNewAuthenticationPathNeeded(final long n, final int n2, final int n3) {
        return n != 0L && (n + 1L) % (long)Math.pow(1 << n2, n3) == 0L;
    }
}
