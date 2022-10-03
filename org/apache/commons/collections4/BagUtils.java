package org.apache.commons.collections4;

import org.apache.commons.collections4.bag.TreeBag;
import org.apache.commons.collections4.bag.HashBag;
import org.apache.commons.collections4.bag.TransformedSortedBag;
import org.apache.commons.collections4.bag.PredicatedSortedBag;
import org.apache.commons.collections4.bag.UnmodifiableSortedBag;
import org.apache.commons.collections4.bag.SynchronizedSortedBag;
import org.apache.commons.collections4.bag.CollectionBag;
import org.apache.commons.collections4.bag.TransformedBag;
import org.apache.commons.collections4.bag.PredicatedBag;
import org.apache.commons.collections4.bag.UnmodifiableBag;
import org.apache.commons.collections4.bag.SynchronizedBag;

public class BagUtils
{
    public static final Bag EMPTY_BAG;
    public static final Bag EMPTY_SORTED_BAG;
    
    private BagUtils() {
    }
    
    public static <E> Bag<E> synchronizedBag(final Bag<E> bag) {
        return SynchronizedBag.synchronizedBag(bag);
    }
    
    public static <E> Bag<E> unmodifiableBag(final Bag<? extends E> bag) {
        return UnmodifiableBag.unmodifiableBag(bag);
    }
    
    public static <E> Bag<E> predicatedBag(final Bag<E> bag, final Predicate<? super E> predicate) {
        return PredicatedBag.predicatedBag(bag, predicate);
    }
    
    public static <E> Bag<E> transformingBag(final Bag<E> bag, final Transformer<? super E, ? extends E> transformer) {
        return TransformedBag.transformingBag(bag, transformer);
    }
    
    public static <E> Bag<E> collectionBag(final Bag<E> bag) {
        return CollectionBag.collectionBag(bag);
    }
    
    public static <E> SortedBag<E> synchronizedSortedBag(final SortedBag<E> bag) {
        return SynchronizedSortedBag.synchronizedSortedBag(bag);
    }
    
    public static <E> SortedBag<E> unmodifiableSortedBag(final SortedBag<E> bag) {
        return UnmodifiableSortedBag.unmodifiableSortedBag(bag);
    }
    
    public static <E> SortedBag<E> predicatedSortedBag(final SortedBag<E> bag, final Predicate<? super E> predicate) {
        return PredicatedSortedBag.predicatedSortedBag(bag, predicate);
    }
    
    public static <E> SortedBag<E> transformingSortedBag(final SortedBag<E> bag, final Transformer<? super E, ? extends E> transformer) {
        return TransformedSortedBag.transformingSortedBag(bag, transformer);
    }
    
    public static <E> Bag<E> emptyBag() {
        return BagUtils.EMPTY_BAG;
    }
    
    public static <E> SortedBag<E> emptySortedBag() {
        return (SortedBag)BagUtils.EMPTY_SORTED_BAG;
    }
    
    static {
        EMPTY_BAG = UnmodifiableBag.unmodifiableBag((Bag<?>)new HashBag<Object>());
        EMPTY_SORTED_BAG = UnmodifiableSortedBag.unmodifiableSortedBag(new TreeBag<Object>());
    }
}
