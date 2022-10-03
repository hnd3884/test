package org.apache.commons.collections4.comparators;

import org.apache.commons.collections4.ComparatorUtils;
import java.io.Serializable;
import java.util.Comparator;

public class NullComparator<E> implements Comparator<E>, Serializable
{
    private static final long serialVersionUID = -5820772575483504339L;
    private final Comparator<? super E> nonNullComparator;
    private final boolean nullsAreHigh;
    
    public NullComparator() {
        this(ComparatorUtils.NATURAL_COMPARATOR, true);
    }
    
    public NullComparator(final Comparator<? super E> nonNullComparator) {
        this(nonNullComparator, true);
    }
    
    public NullComparator(final boolean nullsAreHigh) {
        this(ComparatorUtils.NATURAL_COMPARATOR, nullsAreHigh);
    }
    
    public NullComparator(final Comparator<? super E> nonNullComparator, final boolean nullsAreHigh) {
        this.nonNullComparator = nonNullComparator;
        this.nullsAreHigh = nullsAreHigh;
        if (nonNullComparator == null) {
            throw new NullPointerException("null nonNullComparator");
        }
    }
    
    @Override
    public int compare(final E o1, final E o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return this.nullsAreHigh ? 1 : -1;
        }
        if (o2 == null) {
            return this.nullsAreHigh ? -1 : 1;
        }
        return this.nonNullComparator.compare((Object)o1, (Object)o2);
    }
    
    @Override
    public int hashCode() {
        return (this.nullsAreHigh ? -1 : 1) * this.nonNullComparator.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!obj.getClass().equals(this.getClass())) {
            return false;
        }
        final NullComparator<?> other = (NullComparator<?>)obj;
        return this.nullsAreHigh == other.nullsAreHigh && this.nonNullComparator.equals(other.nonNullComparator);
    }
}
