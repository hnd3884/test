package org.apache.commons.collections4.comparators;

import org.apache.commons.collections4.ComparatorUtils;
import java.io.Serializable;
import java.util.Comparator;

public class ReverseComparator<E> implements Comparator<E>, Serializable
{
    private static final long serialVersionUID = 2858887242028539265L;
    private final Comparator<? super E> comparator;
    
    public ReverseComparator() {
        this(null);
    }
    
    public ReverseComparator(final Comparator<? super E> comparator) {
        this.comparator = ((comparator == null) ? ComparatorUtils.NATURAL_COMPARATOR : comparator);
    }
    
    @Override
    public int compare(final E obj1, final E obj2) {
        return this.comparator.compare((Object)obj2, (Object)obj1);
    }
    
    @Override
    public int hashCode() {
        return "ReverseComparator".hashCode() ^ this.comparator.hashCode();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final ReverseComparator<?> thatrc = (ReverseComparator<?>)object;
            return this.comparator.equals(thatrc.comparator);
        }
        return false;
    }
}
