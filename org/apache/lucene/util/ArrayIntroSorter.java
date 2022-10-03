package org.apache.lucene.util;

import java.util.Comparator;

final class ArrayIntroSorter<T> extends IntroSorter
{
    private final T[] arr;
    private final Comparator<? super T> comparator;
    private T pivot;
    
    public ArrayIntroSorter(final T[] arr, final Comparator<? super T> comparator) {
        this.arr = arr;
        this.comparator = comparator;
        this.pivot = null;
    }
    
    @Override
    protected int compare(final int i, final int j) {
        return this.comparator.compare((Object)this.arr[i], (Object)this.arr[j]);
    }
    
    @Override
    protected void swap(final int i, final int j) {
        ArrayUtil.swap(this.arr, i, j);
    }
    
    @Override
    protected void setPivot(final int i) {
        this.pivot = this.arr[i];
    }
    
    @Override
    protected int comparePivot(final int i) {
        return this.comparator.compare((Object)this.pivot, (Object)this.arr[i]);
    }
}
