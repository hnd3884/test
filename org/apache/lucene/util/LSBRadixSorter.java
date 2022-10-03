package org.apache.lucene.util;

import java.util.Arrays;

final class LSBRadixSorter
{
    private static final int INSERTION_SORT_THRESHOLD = 30;
    private static final int HISTOGRAM_SIZE = 256;
    private final int[] histogram;
    private int[] buffer;
    
    LSBRadixSorter() {
        this.histogram = new int[256];
        this.buffer = new int[0];
    }
    
    private static void buildHistogram(final int[] array, final int off, final int len, final int[] histogram, final int shift) {
        for (int i = 0; i < len; ++i) {
            final int n;
            final int b = n = (array[off + i] >>> shift & 0xFF);
            ++histogram[n];
        }
    }
    
    private static void sumHistogram(final int[] histogram) {
        int accum = 0;
        for (int i = 0; i < 256; ++i) {
            final int count = histogram[i];
            histogram[i] = accum;
            accum += count;
        }
    }
    
    private static void reorder(final int[] array, final int off, final int len, final int[] histogram, final int shift, final int[] dest, final int destOff) {
        for (int i = 0; i < len; ++i) {
            final int v = array[off + i];
            final int b = v >>> shift & 0xFF;
            dest[destOff + histogram[b]++] = v;
        }
    }
    
    private static boolean sort(final int[] array, final int off, final int len, final int[] histogram, final int shift, final int[] dest, final int destOff) {
        Arrays.fill(histogram, 0);
        buildHistogram(array, off, len, histogram, shift);
        if (histogram[0] == len) {
            return false;
        }
        sumHistogram(histogram);
        reorder(array, off, len, histogram, shift, dest, destOff);
        return true;
    }
    
    private static void insertionSort(final int[] array, final int off, final int len) {
        for (int i = off + 1, end = off + len; i < end; ++i) {
            for (int j = i; j > off && array[j - 1] > array[j]; --j) {
                final int tmp = array[j - 1];
                array[j - 1] = array[j];
                array[j] = tmp;
            }
        }
    }
    
    public void sort(final int[] array, final int off, final int len) {
        if (len < 30) {
            insertionSort(array, off, len);
            return;
        }
        this.buffer = ArrayUtil.grow(this.buffer, len);
        int[] arr = array;
        int arrOff = off;
        int[] buf = this.buffer;
        int bufOff = 0;
        for (int shift = 0; shift <= 24; shift += 8) {
            if (sort(arr, arrOff, len, this.histogram, shift, buf, bufOff)) {
                final int[] tmp = arr;
                final int tmpOff = arrOff;
                arr = buf;
                arrOff = bufOff;
                buf = tmp;
                bufOff = tmpOff;
            }
        }
        if (array == buf) {
            System.arraycopy(arr, arrOff, array, off, len);
        }
    }
}
