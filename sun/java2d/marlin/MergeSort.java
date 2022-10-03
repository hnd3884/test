package sun.java2d.marlin;

final class MergeSort
{
    public static final int INSERTION_SORT_THRESHOLD = 14;
    
    static void mergeSortNoCopy(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int n, final int n2) {
        if (n > array.length || n > array2.length || n > array3.length || n > array4.length) {
            throw new ArrayIndexOutOfBoundsException("bad arguments: toIndex=" + n);
        }
        mergeSort(array, array2, array, array3, array2, array4, n2, n);
        if (n2 == 0 || array3[n2 - 1] <= array3[n2]) {
            System.arraycopy(array3, 0, array, 0, n);
            System.arraycopy(array4, 0, array2, 0, n);
            return;
        }
        int i = 0;
        int n3 = 0;
        int n4 = n2;
        while (i < n) {
            if (n4 >= n || (n3 < n2 && array3[n3] <= array3[n4])) {
                array[i] = array3[n3];
                array2[i] = array4[n3];
                ++n3;
            }
            else {
                array[i] = array3[n4];
                array2[i] = array4[n4];
                ++n4;
            }
            ++i;
        }
    }
    
    private static void mergeSort(final int[] array, final int[] array2, final int[] array3, final int[] array4, final int[] array5, final int[] array6, final int n, final int n2) {
        final int n3 = n2 - n;
        if (n3 <= 14) {
            array4[n] = array[n];
            array6[n] = array2[n];
            int i = n + 1;
            int n4 = n;
            while (i < n2) {
                final int n5 = array[i];
                final int n6 = array2[i];
                while (array4[n4] > n5) {
                    array4[n4 + 1] = array4[n4];
                    array6[n4 + 1] = array6[n4];
                    if (n4-- == n) {
                        break;
                    }
                }
                array4[n4 + 1] = n5;
                array6[n4 + 1] = n6;
                n4 = i++;
            }
            return;
        }
        final int n7 = n + n2 >> 1;
        mergeSort(array, array2, array4, array3, array6, array5, n, n7);
        mergeSort(array, array2, array4, array3, array6, array5, n7, n2);
        if (array3[n2 - 1] <= array3[n]) {
            final int n8 = n7 - n;
            final int n9 = n2 - n7;
            final int n10 = (n8 != n9) ? 1 : 0;
            System.arraycopy(array3, n, array4, n7 + n10, n8);
            System.arraycopy(array3, n7, array4, n, n9);
            System.arraycopy(array5, n, array6, n7 + n10, n8);
            System.arraycopy(array5, n7, array6, n, n9);
            return;
        }
        if (array3[n7 - 1] <= array3[n7]) {
            System.arraycopy(array3, n, array4, n, n3);
            System.arraycopy(array5, n, array6, n, n3);
            return;
        }
        int j = n;
        int n11 = n;
        int n12 = n7;
        while (j < n2) {
            if (n12 >= n2 || (n11 < n7 && array3[n11] <= array3[n12])) {
                array4[j] = array3[n11];
                array6[j] = array5[n11];
                ++n11;
            }
            else {
                array4[j] = array3[n12];
                array6[j] = array5[n12];
                ++n12;
            }
            ++j;
        }
    }
    
    private MergeSort() {
    }
}
