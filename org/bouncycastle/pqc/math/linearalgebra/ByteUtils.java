package org.bouncycastle.pqc.math.linearalgebra;

public final class ByteUtils
{
    private static final char[] HEX_CHARS;
    
    private ByteUtils() {
    }
    
    public static boolean equals(final byte[] array, final byte[] array2) {
        if (array == null) {
            return array2 == null;
        }
        if (array2 == null) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        boolean b = true;
        for (int i = array.length - 1; i >= 0; --i) {
            b &= (array[i] == array2[i]);
        }
        return b;
    }
    
    public static boolean equals(final byte[][] array, final byte[][] array2) {
        if (array.length != array2.length) {
            return false;
        }
        boolean b = true;
        for (int i = array.length - 1; i >= 0; --i) {
            b &= equals(array[i], array2[i]);
        }
        return b;
    }
    
    public static boolean equals(final byte[][][] array, final byte[][][] array2) {
        if (array.length != array2.length) {
            return false;
        }
        boolean b = true;
        for (int i = array.length - 1; i >= 0; --i) {
            if (array[i].length != array2[i].length) {
                return false;
            }
            for (int j = array[i].length - 1; j >= 0; --j) {
                b &= equals(array[i][j], array2[i][j]);
            }
        }
        return b;
    }
    
    public static int deepHashCode(final byte[] array) {
        int n = 1;
        for (int i = 0; i < array.length; ++i) {
            n = 31 * n + array[i];
        }
        return n;
    }
    
    public static int deepHashCode(final byte[][] array) {
        int n = 1;
        for (int i = 0; i < array.length; ++i) {
            n = 31 * n + deepHashCode(array[i]);
        }
        return n;
    }
    
    public static int deepHashCode(final byte[][][] array) {
        int n = 1;
        for (int i = 0; i < array.length; ++i) {
            n = 31 * n + deepHashCode(array[i]);
        }
        return n;
    }
    
    public static byte[] clone(final byte[] array) {
        if (array == null) {
            return null;
        }
        final byte[] array2 = new byte[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static byte[] fromHexString(final String s) {
        final char[] charArray = s.toUpperCase().toCharArray();
        int n = 0;
        for (int i = 0; i < charArray.length; ++i) {
            if ((charArray[i] >= '0' && charArray[i] <= '9') || (charArray[i] >= 'A' && charArray[i] <= 'F')) {
                ++n;
            }
        }
        final byte[] array = new byte[n + 1 >> 1];
        int n2 = n & 0x1;
        for (int j = 0; j < charArray.length; ++j) {
            if (charArray[j] >= '0' && charArray[j] <= '9') {
                final byte[] array2 = array;
                final int n3 = n2 >> 1;
                array2[n3] <<= 4;
                final byte[] array3 = array;
                final int n4 = n2 >> 1;
                array3[n4] |= (byte)(charArray[j] - '0');
            }
            else {
                if (charArray[j] < 'A' || charArray[j] > 'F') {
                    continue;
                }
                final byte[] array4 = array;
                final int n5 = n2 >> 1;
                array4[n5] <<= 4;
                final byte[] array5 = array;
                final int n6 = n2 >> 1;
                array5[n6] |= (byte)(charArray[j] - 'A' + 10);
            }
            ++n2;
        }
        return array;
    }
    
    public static String toHexString(final byte[] array) {
        String string = "";
        for (int i = 0; i < array.length; ++i) {
            string = string + ByteUtils.HEX_CHARS[array[i] >>> 4 & 0xF] + ByteUtils.HEX_CHARS[array[i] & 0xF];
        }
        return string;
    }
    
    public static String toHexString(final byte[] array, final String s, final String s2) {
        String s3 = new String(s);
        for (int i = 0; i < array.length; ++i) {
            s3 = s3 + ByteUtils.HEX_CHARS[array[i] >>> 4 & 0xF] + ByteUtils.HEX_CHARS[array[i] & 0xF];
            if (i < array.length - 1) {
                s3 += s2;
            }
        }
        return s3;
    }
    
    public static String toBinaryString(final byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            final byte b = array[i];
            for (int j = 0; j < 8; ++j) {
                s += (b >>> j & 0x1);
            }
            if (i != array.length - 1) {
                s += " ";
            }
        }
        return s;
    }
    
    public static byte[] xor(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length];
        for (int i = array.length - 1; i >= 0; --i) {
            array3[i] = (byte)(array[i] ^ array2[i]);
        }
        return array3;
    }
    
    public static byte[] concatenate(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static byte[] concatenate(final byte[][] array) {
        final int length = array[0].length;
        final byte[] array2 = new byte[array.length * length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            System.arraycopy(array[i], 0, array2, n, length);
            n += length;
        }
        return array2;
    }
    
    public static byte[][] split(final byte[] array, final int n) throws ArrayIndexOutOfBoundsException {
        if (n > array.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final byte[][] array2 = { new byte[n], new byte[array.length - n] };
        System.arraycopy(array, 0, array2[0], 0, n);
        System.arraycopy(array, n, array2[1], 0, array.length - n);
        return array2;
    }
    
    public static byte[] subArray(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2 - n];
        System.arraycopy(array, n, array2, 0, n2 - n);
        return array2;
    }
    
    public static byte[] subArray(final byte[] array, final int n) {
        return subArray(array, n, array.length);
    }
    
    public static char[] toCharArray(final byte[] array) {
        final char[] array2 = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = (char)array[i];
        }
        return array2;
    }
    
    static {
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
}
