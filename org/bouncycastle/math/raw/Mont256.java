package org.bouncycastle.math.raw;

public abstract class Mont256
{
    private static final long M = 4294967295L;
    
    public static int inverse32(final int n) {
        final int n2 = n * (2 - n * n);
        final int n3 = n2 * (2 - n * n2);
        final int n4 = n3 * (2 - n * n3);
        return n4 * (2 - n * n4);
    }
    
    public static void multAdd(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int n) {
        int n2 = 0;
        final long n3 = (long)array2[0] & 0xFFFFFFFFL;
        for (int i = 0; i < 8; ++i) {
            final long n4 = (long)array3[0] & 0xFFFFFFFFL;
            final long n5 = (long)array[i] & 0xFFFFFFFFL;
            final long n6 = n5 * n3;
            final long n7 = (n6 & 0xFFFFFFFFL) + n4;
            final long n8 = (long)((int)n7 * n) & 0xFFFFFFFFL;
            final long n9 = n8 * ((long)array4[0] & 0xFFFFFFFFL);
            long n10 = (n7 + (n9 & 0xFFFFFFFFL) >>> 32) + (n6 >>> 32) + (n9 >>> 32);
            for (int j = 1; j < 8; ++j) {
                final long n11 = n5 * ((long)array2[j] & 0xFFFFFFFFL);
                final long n12 = n8 * ((long)array4[j] & 0xFFFFFFFFL);
                final long n13 = n10 + ((n11 & 0xFFFFFFFFL) + (n12 & 0xFFFFFFFFL) + ((long)array3[j] & 0xFFFFFFFFL));
                array3[j - 1] = (int)n13;
                n10 = (n13 >>> 32) + (n11 >>> 32) + (n12 >>> 32);
            }
            final long n14 = n10 + ((long)n2 & 0xFFFFFFFFL);
            array3[7] = (int)n14;
            n2 = (int)(n14 >>> 32);
        }
        if (n2 != 0 || Nat256.gte(array3, array4)) {
            Nat256.sub(array3, array4, array3);
        }
    }
    
    public static void multAddXF(final int[] array, final int[] array2, final int[] array3, final int[] array4) {
        int n = 0;
        final long n2 = (long)array2[0] & 0xFFFFFFFFL;
        for (int i = 0; i < 8; ++i) {
            final long n3 = (long)array[i] & 0xFFFFFFFFL;
            final long n4 = n3 * n2 + ((long)array3[0] & 0xFFFFFFFFL);
            final long n5 = n4 & 0xFFFFFFFFL;
            long n6 = (n4 >>> 32) + n5;
            for (int j = 1; j < 8; ++j) {
                final long n7 = n3 * ((long)array2[j] & 0xFFFFFFFFL);
                final long n8 = n5 * ((long)array4[j] & 0xFFFFFFFFL);
                final long n9 = n6 + ((n7 & 0xFFFFFFFFL) + (n8 & 0xFFFFFFFFL) + ((long)array3[j] & 0xFFFFFFFFL));
                array3[j - 1] = (int)n9;
                n6 = (n9 >>> 32) + (n7 >>> 32) + (n8 >>> 32);
            }
            final long n10 = n6 + ((long)n & 0xFFFFFFFFL);
            array3[7] = (int)n10;
            n = (int)(n10 >>> 32);
        }
        if (n != 0 || Nat256.gte(array3, array4)) {
            Nat256.sub(array3, array4, array3);
        }
    }
    
    public static void reduce(final int[] array, final int[] array2, final int n) {
        for (int i = 0; i < 8; ++i) {
            final int n2 = array[0];
            final long n3 = (long)(n2 * n) & 0xFFFFFFFFL;
            long n4 = n3 * ((long)array2[0] & 0xFFFFFFFFL) + ((long)n2 & 0xFFFFFFFFL) >>> 32;
            for (int j = 1; j < 8; ++j) {
                final long n5 = n4 + (n3 * ((long)array2[j] & 0xFFFFFFFFL) + ((long)array[j] & 0xFFFFFFFFL));
                array[j - 1] = (int)n5;
                n4 = n5 >>> 32;
            }
            array[7] = (int)n4;
        }
        if (Nat256.gte(array, array2)) {
            Nat256.sub(array, array2, array);
        }
    }
    
    public static void reduceXF(final int[] array, final int[] array2) {
        for (int i = 0; i < 8; ++i) {
            long n2;
            final long n = n2 = ((long)array[0] & 0xFFFFFFFFL);
            for (int j = 1; j < 8; ++j) {
                final long n3 = n2 + (n * ((long)array2[j] & 0xFFFFFFFFL) + ((long)array[j] & 0xFFFFFFFFL));
                array[j - 1] = (int)n3;
                n2 = n3 >>> 32;
            }
            array[7] = (int)n2;
        }
        if (Nat256.gte(array, array2)) {
            Nat256.sub(array, array2, array);
        }
    }
}
