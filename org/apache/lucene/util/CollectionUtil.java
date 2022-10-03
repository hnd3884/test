package org.apache.lucene.util;

import java.util.Collections;
import java.util.RandomAccess;
import java.util.Comparator;
import java.util.List;

public final class CollectionUtil
{
    private CollectionUtil() {
    }
    
    public static <T> void introSort(final List<T> list, final Comparator<? super T> comp) {
        final int size = list.size();
        if (size <= 1) {
            return;
        }
        new ListIntroSorter<Object>(list, comp).sort(0, size);
    }
    
    public static <T extends Comparable<? super T>> void introSort(final List<T> list) {
        final int size = list.size();
        if (size <= 1) {
            return;
        }
        introSort(list, ArrayUtil.naturalComparator());
    }
    
    public static <T> void timSort(final List<T> list, final Comparator<? super T> comp) {
        final int size = list.size();
        if (size <= 1) {
            return;
        }
        new ListTimSorter<Object>(list, comp, list.size() / 64).sort(0, size);
    }
    
    public static <T extends Comparable<? super T>> void timSort(final List<T> list) {
        final int size = list.size();
        if (size <= 1) {
            return;
        }
        timSort(list, ArrayUtil.naturalComparator());
    }
    
    private static final class ListIntroSorter<T> extends IntroSorter
    {
        T pivot;
        final List<T> list;
        final Comparator<? super T> comp;
        
        ListIntroSorter(final List<T> list, final Comparator<? super T> comp) {
            if (!(list instanceof RandomAccess)) {
                throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
            }
            this.list = list;
            this.comp = comp;
        }
        
        @Override
        protected void setPivot(final int i) {
            this.pivot = this.list.get(i);
        }
        
        @Override
        protected void swap(final int i, final int j) {
            Collections.swap(this.list, i, j);
        }
        
        @Override
        protected int compare(final int i, final int j) {
            return this.comp.compare((Object)this.list.get(i), (Object)this.list.get(j));
        }
        
        @Override
        protected int comparePivot(final int j) {
            return this.comp.compare((Object)this.pivot, (Object)this.list.get(j));
        }
    }
    
    private static final class ListTimSorter<T> extends TimSorter
    {
        final List<T> list;
        final Comparator<? super T> comp;
        final T[] tmp;
        
        ListTimSorter(final List<T> list, final Comparator<? super T> comp, final int maxTempSlots) {
            super(maxTempSlots);
            if (!(list instanceof RandomAccess)) {
                throw new IllegalArgumentException("CollectionUtil can only sort random access lists in-place.");
            }
            this.list = list;
            this.comp = comp;
            if (maxTempSlots > 0) {
                this.tmp = (T[])new Object[maxTempSlots];
            }
            else {
                this.tmp = null;
            }
        }
        
        @Override
        protected void swap(final int i, final int j) {
            Collections.swap(this.list, i, j);
        }
        
        @Override
        protected void copy(final int src, final int dest) {
            this.list.set(dest, this.list.get(src));
        }
        
        @Override
        protected void save(final int i, final int len) {
            for (int j = 0; j < len; ++j) {
                this.tmp[j] = this.list.get(i + j);
            }
        }
        
        @Override
        protected void restore(final int i, final int j) {
            this.list.set(j, this.tmp[i]);
        }
        
        @Override
        protected int compare(final int i, final int j) {
            return this.comp.compare((Object)this.list.get(i), (Object)this.list.get(j));
        }
        
        @Override
        protected int compareSaved(final int i, final int j) {
            return this.comp.compare((Object)this.tmp[i], (Object)this.list.get(j));
        }
    }
}
