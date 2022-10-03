package org.apache.lucene.util;

import java.util.Comparator;

final class ArrayTimSorter<T> extends TimSorter
{
    private final Comparator<? super T> comparator;
    private final T[] arr;
    private final T[] tmp;
    
    public ArrayTimSorter(final T[] arr, final Comparator<? super T> comparator, final int maxTempSlots) {
        super(maxTempSlots);
        this.arr = arr;
        this.comparator = comparator;
        if (maxTempSlots > 0) {
            final T[] tmp = (T[])new Object[maxTempSlots];
            this.tmp = tmp;
        }
        else {
            this.tmp = null;
        }
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
    protected void copy(final int src, final int dest) {
        this.arr[dest] = this.arr[src];
    }
    
    @Override
    protected void save(final int start, final int len) {
        System.arraycopy(this.arr, start, this.tmp, 0, len);
    }
    
    @Override
    protected void restore(final int src, final int dest) {
        this.arr[dest] = this.tmp[src];
    }
    
    @Override
    protected int compareSaved(final int i, final int j) {
        return this.comparator.compare((Object)this.tmp[i], (Object)this.arr[j]);
    }
}
