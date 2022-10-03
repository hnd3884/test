package org.bouncycastle.pqc.crypto.gmss.util;

public class GMSSUtil
{
    public byte[] intToBytesLittleEndian(final int n) {
        return new byte[] { (byte)(n & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 24 & 0xFF) };
    }
    
    public int bytesToIntLittleEndian(final byte[] array) {
        return (array[0] & 0xFF) | (array[1] & 0xFF) << 8 | (array[2] & 0xFF) << 16 | (array[3] & 0xFF) << 24;
    }
    
    public int bytesToIntLittleEndian(final byte[] array, int n) {
        return (array[n++] & 0xFF) | (array[n++] & 0xFF) << 8 | (array[n++] & 0xFF) << 16 | (array[n] & 0xFF) << 24;
    }
    
    public byte[] concatenateArray(final byte[][] array) {
        final byte[] array2 = new byte[array.length * array[0].length];
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            System.arraycopy(array[i], 0, array2, n, array[i].length);
            n += array[i].length;
        }
        return array2;
    }
    
    public void printArray(final String s, final byte[][] array) {
        System.out.println(s);
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            for (int j = 0; j < array[0].length; ++j) {
                System.out.println(n + "; " + array[i][j]);
                ++n;
            }
        }
    }
    
    public void printArray(final String s, final byte[] array) {
        System.out.println(s);
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            System.out.println(n + "; " + array[i]);
            ++n;
        }
    }
    
    public boolean testPowerOfTwo(final int n) {
        int i;
        for (i = 1; i < n; i <<= 1) {}
        return n == i;
    }
    
    public int getLog(final int n) {
        int n2 = 1;
        for (int i = 2; i < n; i <<= 1, ++n2) {}
        return n2;
    }
}
