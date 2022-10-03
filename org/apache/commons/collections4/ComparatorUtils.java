package org.apache.commons.collections4;

import org.apache.commons.collections4.comparators.ComparableComparator;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.comparators.NullComparator;
import org.apache.commons.collections4.comparators.BooleanComparator;
import org.apache.commons.collections4.comparators.ReverseComparator;
import java.util.Collection;
import org.apache.commons.collections4.comparators.ComparatorChain;
import java.util.Comparator;

public class ComparatorUtils
{
    public static final Comparator NATURAL_COMPARATOR;
    
    private ComparatorUtils() {
    }
    
    public static <E extends Comparable<? super E>> Comparator<E> naturalComparator() {
        return ComparatorUtils.NATURAL_COMPARATOR;
    }
    
    public static <E> Comparator<E> chainedComparator(final Comparator<E>... comparators) {
        final ComparatorChain<E> chain = new ComparatorChain<E>();
        for (final Comparator<E> comparator : comparators) {
            if (comparator == null) {
                throw new NullPointerException("Comparator cannot be null");
            }
            chain.addComparator(comparator);
        }
        return chain;
    }
    
    public static <E> Comparator<E> chainedComparator(final Collection<Comparator<E>> comparators) {
        return chainedComparator((Comparator<E>[])comparators.toArray(new Comparator[comparators.size()]));
    }
    
    public static <E> Comparator<E> reversedComparator(final Comparator<E> comparator) {
        return new ReverseComparator<E>(comparator);
    }
    
    public static Comparator<Boolean> booleanComparator(final boolean trueFirst) {
        return BooleanComparator.booleanComparator(trueFirst);
    }
    
    public static <E> Comparator<E> nullLowComparator(Comparator<E> comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new NullComparator<E>(comparator, false);
    }
    
    public static <E> Comparator<E> nullHighComparator(Comparator<E> comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new NullComparator<E>(comparator, true);
    }
    
    public static <I, O> Comparator<I> transformedComparator(Comparator<O> comparator, final Transformer<? super I, ? extends O> transformer) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        return new TransformingComparator<I, Object>(transformer, comparator);
    }
    
    public static <E> E min(final E o1, final E o2, Comparator<E> comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        final int c = comparator.compare(o1, o2);
        return (c < 0) ? o1 : o2;
    }
    
    public static <E> E max(final E o1, final E o2, Comparator<E> comparator) {
        if (comparator == null) {
            comparator = ComparatorUtils.NATURAL_COMPARATOR;
        }
        final int c = comparator.compare(o1, o2);
        return (c > 0) ? o1 : o2;
    }
    
    static {
        NATURAL_COMPARATOR = ComparableComparator.comparableComparator();
    }
}
