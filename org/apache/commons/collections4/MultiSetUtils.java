package org.apache.commons.collections4;

import org.apache.commons.collections4.multiset.HashMultiSet;
import org.apache.commons.collections4.multiset.PredicatedMultiSet;
import org.apache.commons.collections4.multiset.UnmodifiableMultiSet;
import org.apache.commons.collections4.multiset.SynchronizedMultiSet;

public class MultiSetUtils
{
    public static final MultiSet EMPTY_MULTISET;
    
    private MultiSetUtils() {
    }
    
    public static <E> MultiSet<E> synchronizedMultiSet(final MultiSet<E> multiset) {
        return SynchronizedMultiSet.synchronizedMultiSet(multiset);
    }
    
    public static <E> MultiSet<E> unmodifiableMultiSet(final MultiSet<? extends E> multiset) {
        return UnmodifiableMultiSet.unmodifiableMultiSet(multiset);
    }
    
    public static <E> MultiSet<E> predicatedMultiSet(final MultiSet<E> multiset, final Predicate<? super E> predicate) {
        return PredicatedMultiSet.predicatedMultiSet(multiset, predicate);
    }
    
    public static <E> MultiSet<E> emptyMultiSet() {
        return MultiSetUtils.EMPTY_MULTISET;
    }
    
    static {
        EMPTY_MULTISET = UnmodifiableMultiSet.unmodifiableMultiSet((MultiSet<?>)new HashMultiSet<Object>());
    }
}
