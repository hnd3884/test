package org.apache.commons.collections.comparators;

import java.io.Serializable;
import java.util.Comparator;

public final class BooleanComparator implements Comparator, Serializable
{
    private static final long serialVersionUID = 1830042991606340609L;
    private static final BooleanComparator TRUE_FIRST;
    private static final BooleanComparator FALSE_FIRST;
    private boolean trueFirst;
    
    public static BooleanComparator getTrueFirstComparator() {
        return BooleanComparator.TRUE_FIRST;
    }
    
    public static BooleanComparator getFalseFirstComparator() {
        return BooleanComparator.FALSE_FIRST;
    }
    
    public static BooleanComparator getBooleanComparator(final boolean trueFirst) {
        return trueFirst ? BooleanComparator.TRUE_FIRST : BooleanComparator.FALSE_FIRST;
    }
    
    public BooleanComparator() {
        this(false);
    }
    
    public BooleanComparator(final boolean trueFirst) {
        this.trueFirst = false;
        this.trueFirst = trueFirst;
    }
    
    public int compare(final Object obj1, final Object obj2) {
        return this.compare((Boolean)obj1, (Boolean)obj2);
    }
    
    public int compare(final Boolean b1, final Boolean b2) {
        final boolean v1 = b1;
        final boolean v2 = b2;
        return (v1 ^ v2) ? ((v1 ^ this.trueFirst) ? 1 : -1) : 0;
    }
    
    public int hashCode() {
        final int hash = "BooleanComparator".hashCode();
        return this.trueFirst ? (-1 * hash) : hash;
    }
    
    public boolean equals(final Object object) {
        return this == object || (object instanceof BooleanComparator && this.trueFirst == ((BooleanComparator)object).trueFirst);
    }
    
    public boolean sortsTrueFirst() {
        return this.trueFirst;
    }
    
    static {
        TRUE_FIRST = new BooleanComparator(true);
        FALSE_FIRST = new BooleanComparator(false);
    }
}
