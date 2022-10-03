package org.apache.lucene.util;

public abstract class InPlaceMergeSorter extends Sorter
{
    @Override
    public final void sort(final int from, final int to) {
        this.checkRange(from, to);
        this.mergeSort(from, to);
    }
    
    void mergeSort(final int from, final int to) {
        if (to - from < 20) {
            this.insertionSort(from, to);
        }
        else {
            final int mid = from + to >>> 1;
            this.mergeSort(from, mid);
            this.mergeSort(mid, to);
            this.mergeInPlace(from, mid, to);
        }
    }
}
