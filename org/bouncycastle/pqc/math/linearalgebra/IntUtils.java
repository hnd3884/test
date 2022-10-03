package org.bouncycastle.pqc.math.linearalgebra;

public final class IntUtils
{
    private IntUtils() {
    }
    
    public static boolean equals(final int[] array, final int[] array2) {
        if (array.length != array2.length) {
            return false;
        }
        boolean b = true;
        for (int i = array.length - 1; i >= 0; --i) {
            b &= (array[i] == array2[i]);
        }
        return b;
    }
    
    public static int[] clone(final int[] array) {
        final int[] array2 = new int[array.length];
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static void fill(final int[] array, final int n) {
        for (int i = array.length - 1; i >= 0; --i) {
            array[i] = n;
        }
    }
    
    public static void quicksort(final int[] array) {
        quicksort(array, 0, array.length - 1);
    }
    
    public static void quicksort(final int[] array, final int n, final int n2) {
        if (n2 > n) {
            final int partition = partition(array, n, n2, n2);
            quicksort(array, n, partition - 1);
            quicksort(array, partition + 1, n2);
        }
    }
    
    private static int partition(final int[] array, final int n, final int n2, final int n3) {
        final int n4 = array[n3];
        array[n3] = array[n2];
        array[n2] = n4;
        int n5 = n;
        for (int i = n; i < n2; ++i) {
            if (array[i] <= n4) {
                final int n6 = array[n5];
                array[n5] = array[i];
                array[i] = n6;
                ++n5;
            }
        }
        final int n7 = array[n5];
        array[n5] = array[n2];
        array[n2] = n7;
        return n5;
    }
    
    public static int[] subArray(final int[] array, final int n, final int n2) {
        final int[] array2 = new int[n2 - n];
        System.arraycopy(array, n, array2, 0, n2 - n);
        return array2;
    }
    
    public static String toString(final int[] array) {
        String string = "";
        for (int i = 0; i < array.length; ++i) {
            string = string + array[i] + " ";
        }
        return string;
    }
    
    public static String toHexString(final int[] array) {
        return ByteUtils.toHexString(BigEndianConversions.toByteArray(array));
    }
}
