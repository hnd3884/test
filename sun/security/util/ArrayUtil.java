package sun.security.util;

public final class ArrayUtil
{
    private static void swap(final byte[] array, final int n, final int n2) {
        final byte b = array[n];
        array[n] = array[n2];
        array[n2] = b;
    }
    
    public static void reverse(final byte[] array) {
        for (int i = 0, n = array.length - 1; i < n; ++i, --n) {
            swap(array, i, n);
        }
    }
}
