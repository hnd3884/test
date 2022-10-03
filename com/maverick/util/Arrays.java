package com.maverick.util;

public class Arrays
{
    private static int b(final int[] array, final int n, final int n2, final int n3) {
        return (array[n] < array[n2]) ? ((array[n2] < array[n3]) ? n2 : ((array[n] < array[n3]) ? n3 : n)) : ((array[n2] > array[n3]) ? n2 : ((array[n] > array[n3]) ? n3 : n));
    }
    
    private static void c(final int[] array, final int n, final int n2) {
        final int n3 = array[n];
        array[n] = array[n2];
        array[n2] = n3;
    }
    
    public static void sort(final int[] array) {
        b(array, 0, array.length);
    }
    
    private static void b(final int[] array, final int n, final int n2) {
        if (n2 < 7) {
            for (int i = n; i < n2 + n; ++i) {
                for (int n3 = i; n3 > n && array[n3 - 1] > array[n3]; --n3) {
                    c(array, n3, n3 - 1);
                }
            }
            return;
        }
        int n4 = n + (n2 >> 1);
        if (n2 > 7) {
            int b = n;
            int b2 = n + n2 - 1;
            if (n2 > 40) {
                final int n5 = n2 / 8;
                b = b(array, b, b + n5, b + 2 * n5);
                n4 = b(array, n4 - n5, n4, n4 + n5);
                b2 = b(array, b2 - 2 * n5, b2 - n5, b2);
            }
            n4 = b(array, b, n4, b2);
        }
        final int n6 = array[n4];
        int n8;
        int n7 = n8 = n;
        int n10;
        int n9 = n10 = n + n2 - 1;
        while (true) {
            if (n8 <= n9 && array[n8] <= n6) {
                if (array[n8] == n6) {
                    c(array, n7++, n8);
                }
                ++n8;
            }
            else {
                while (n9 >= n8 && array[n9] >= n6) {
                    if (array[n9] == n6) {
                        c(array, n9, n10--);
                    }
                    --n9;
                }
                if (n8 > n9) {
                    break;
                }
                c(array, n8++, n9--);
            }
        }
    }
}
