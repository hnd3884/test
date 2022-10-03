package org.apache.commons.collections;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.TransformingComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.BooleanComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import java.util.Collection;
import org.apache.commons.collections.comparators.ComparatorChain;
import java.util.Comparator;

public class ComparatorUtils
{
    public static final Comparator NATURAL_COMPARATOR;
    
    public static Comparator naturalComparator() {
        return ComparatorUtils.NATURAL_COMPARATOR;
    }
    
    public static Comparator chainedComparator(final Comparator comparator1, final Comparator comparator2) {
        return chainedComparator(new Comparator[] { comparator1, comparator2 });
    }
    
    public static Comparator chainedComparator(final Comparator[] comparators) {
        final ComparatorChain chain = new ComparatorChain();
        for (int i = 0; i < comparators.length; ++i) {
            if (comparators[i] == null) {
                throw new NullPointerException("Comparator cannot be null");
            }
            chain.addComparator(comparators[i]);
        }
        return chain;
    }
    
    public static Comparator chainedComparator(final Collection comparators) {
        return chainedComparator(comparators.toArray(new Comparator[comparators.size()]));
    }
    
    public static Comparator reversedComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new ReverseComparator(comparator);
    }
    
    public static Comparator booleanComparator(final boolean trueFirst) {
        return BooleanComparator.getBooleanComparator(trueFirst);
    }
    
    public static Comparator nullLowComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, false);
    }
    
    public static Comparator nullHighComparator(Comparator comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new NullComparator(comparator, true);
    }
    
    public static Comparator transformedComparator(Comparator comparator, final Transformer transformer) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new TransformingComparator(transformer, comparator);
    }
    
    public static Object min(final Object o1, final Object o2, Comparator comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        final int c = comparator.compare(o1, o2);
        return (c < 0) ? o1 : o2;
    }
    
    public static Object max(final Object o1, final Object o2, Comparator comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        final int c = comparator.compare(o1, o2);
        return (c > 0) ? o1 : o2;
    }
    
    static {
        NATURAL_COMPARATOR = ComparableComparator.getInstance();
    }
}
