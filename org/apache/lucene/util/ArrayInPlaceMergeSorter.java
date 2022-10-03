package org.apache.lucene.util;

import java.util.Comparator;

final class ArrayInPlaceMergeSorter<T> extends InPlaceMergeSorter
{
    private final T[] arr;
    private final Comparator<? super T> comparator;
    
    public ArrayInPlaceMergeSorter(final T[] arr, final Comparator<? super T> comparator) {
        this.arr = arr;
        this.comparator = comparator;
    }
    
    @Override
    protected int compare(final int i, final int j) {
        return this.comparator.compare((Object)this.arr[i], (Object)this.arr[j]);
    }
    
    @Override
    protected void swap(final int i, final int j) {
        ArrayUtil.swap(this.arr, i, j);
    }
}
