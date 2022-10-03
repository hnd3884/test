package com.sun.imageio.plugins.png;

public class RowFilter
{
    private static final int abs(final int n) {
        return (n < 0) ? (-n) : n;
    }
    
    protected static int subFilter(final byte[] array, final byte[] array2, final int n, final int n2) {
        int n3 = 0;
        for (int i = n; i < n2 + n; ++i) {
            final int n4 = (array[i] & 0xFF) - (array[i - n] & 0xFF);
            array2[i] = (byte)n4;
            n3 += abs(n4);
        }
        return n3;
    }
    
    protected static int upFilter(final byte[] array, final byte[] array2, final byte[] array3, final int n, final int n2) {
        int n3 = 0;
        for (int i = n; i < n2 + n; ++i) {
            final int n4 = (array[i] & 0xFF) - (array2[i] & 0xFF);
            array3[i] = (byte)n4;
            n3 += abs(n4);
        }
        return n3;
    }
    
    protected final int paethPredictor(final int n, final int n2, final int n3) {
        final int n4 = n + n2 - n3;
        final int abs = abs(n4 - n);
        final int abs2 = abs(n4 - n2);
        final int abs3 = abs(n4 - n3);
        if (abs <= abs2 && abs <= abs3) {
            return n;
        }
        if (abs2 <= abs3) {
            return n2;
        }
        return n3;
    }
    
    public int filterRow(final int n, final byte[] array, final byte[] array2, final byte[][] array3, final int n2, final int n3) {
        if (n != 3) {
            System.arraycopy(array, n3, array3[0], n3, n2);
            return 0;
        }
        final int[] array4 = new int[5];
        for (int i = 0; i < 5; ++i) {
            array4[i] = Integer.MAX_VALUE;
        }
        int n4 = 0;
        for (int j = n3; j < n2 + n3; ++j) {
            n4 += (array[j] & 0xFF);
        }
        array4[0] = n4;
        array4[1] = subFilter(array, array3[1], n3, n2);
        array4[2] = upFilter(array, array2, array3[2], n3, n2);
        final byte[] array5 = array3[3];
        int n5 = 0;
        for (int k = n3; k < n2 + n3; ++k) {
            final int n6 = (array[k] & 0xFF) - ((array[k - n3] & 0xFF) + (array2[k] & 0xFF)) / 2;
            array5[k] = (byte)n6;
            n5 += abs(n6);
        }
        array4[3] = n5;
        final byte[] array6 = array3[4];
        int n7 = 0;
        for (int l = n3; l < n2 + n3; ++l) {
            final int n8 = (array[l] & 0xFF) - this.paethPredictor(array[l - n3] & 0xFF, array2[l] & 0xFF, array2[l - n3] & 0xFF);
            array6[l] = (byte)n8;
            n7 += abs(n8);
        }
        array4[4] = n7;
        int n9 = array4[0];
        int n10 = 0;
        for (int n11 = 1; n11 < 5; ++n11) {
            if (array4[n11] < n9) {
                n9 = array4[n11];
                n10 = n11;
            }
        }
        if (n10 == 0) {
            System.arraycopy(array, n3, array3[0], n3, n2);
        }
        return n10;
    }
}
