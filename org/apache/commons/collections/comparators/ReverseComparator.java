package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

public class ReverseComparator implements Comparator, Serializable
{
    private static final long serialVersionUID = 2858887242028539265L;
    private Comparator comparator;
    
    public ReverseComparator() {
        this(null);
    }
    
    public ReverseComparator(final Comparator comparator) {
        if (comparator != null) {
            this.comparator = comparator;
        }
        else {
            this.comparator = ComparableComparator.getInstance();
        }
    }
    
    public int compare(final Object obj1, final Object obj2) {
        return this.comparator.compare(obj2, obj1);
    }
    
    public int hashCode() {
        return "ReverseComparator".hashCode() ^ this.comparator.hashCode();
    }
    
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (null == object) {
            return false;
        }
        if (object.getClass().equals(this.getClass())) {
            final ReverseComparator thatrc = (ReverseComparator)object;
            return this.comparator.equals(thatrc.comparator);
        }
        return false;
    }
}
