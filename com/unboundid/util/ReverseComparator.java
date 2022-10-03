package com.unboundid.util;

import java.io.Serializable;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ReverseComparator<T> implements Comparator<T>, Serializable
{
    private static final long serialVersionUID = -4615537960027681276L;
    private final Comparator<T> baseComparator;
    
    public ReverseComparator() {
        this.baseComparator = null;
    }
    
    public ReverseComparator(final Comparator<T> baseComparator) {
        this.baseComparator = baseComparator;
    }
    
    @Override
    public int compare(final T o1, final T o2) {
        int baseValue;
        if (this.baseComparator == null) {
            baseValue = ((Comparable)o1).compareTo(o2);
        }
        else {
            baseValue = this.baseComparator.compare(o1, o2);
        }
        if (baseValue < 0) {
            return 1;
        }
        if (baseValue > 0) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        if (this.baseComparator == null) {
            return 0;
        }
        return this.baseComparator.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (!o.getClass().equals(ReverseComparator.class)) {
            return false;
        }
        final ReverseComparator<T> c = (ReverseComparator<T>)o;
        if (this.baseComparator == null) {
            return c.baseComparator == null;
        }
        return this.baseComparator.equals(c.baseComparator);
    }
}
