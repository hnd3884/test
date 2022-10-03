package org.apache.commons.collections;

import org.apache.commons.collections.bag.TreeBag;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.collections.bag.TransformedSortedBag;
import org.apache.commons.collections.bag.TypedSortedBag;
import org.apache.commons.collections.bag.PredicatedSortedBag;
import org.apache.commons.collections.bag.UnmodifiableSortedBag;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.collections.bag.TransformedBag;
import org.apache.commons.collections.bag.TypedBag;
import org.apache.commons.collections.bag.PredicatedBag;
import org.apache.commons.collections.bag.UnmodifiableBag;
import org.apache.commons.collections.bag.SynchronizedBag;

public class BagUtils
{
    public static final Bag EMPTY_BAG;
    public static final Bag EMPTY_SORTED_BAG;
    
    public static Bag synchronizedBag(final Bag bag) {
        return SynchronizedBag.decorate(bag);
    }
    
    public static Bag unmodifiableBag(final Bag bag) {
        return UnmodifiableBag.decorate(bag);
    }
    
    public static Bag predicatedBag(final Bag bag, final Predicate predicate) {
        return PredicatedBag.decorate(bag, predicate);
    }
    
    public static Bag typedBag(final Bag bag, final Class type) {
        return TypedBag.decorate(bag, type);
    }
    
    public static Bag transformedBag(final Bag bag, final Transformer transformer) {
        return TransformedBag.decorate(bag, transformer);
    }
    
    public static SortedBag synchronizedSortedBag(final SortedBag bag) {
        return SynchronizedSortedBag.decorate(bag);
    }
    
    public static SortedBag unmodifiableSortedBag(final SortedBag bag) {
        return UnmodifiableSortedBag.decorate(bag);
    }
    
    public static SortedBag predicatedSortedBag(final SortedBag bag, final Predicate predicate) {
        return PredicatedSortedBag.decorate(bag, predicate);
    }
    
    public static SortedBag typedSortedBag(final SortedBag bag, final Class type) {
        return TypedSortedBag.decorate(bag, type);
    }
    
    public static SortedBag transformedSortedBag(final SortedBag bag, final Transformer transformer) {
        return TransformedSortedBag.decorate(bag, transformer);
    }
    
    static {
        EMPTY_BAG = UnmodifiableBag.decorate(new HashBag());
        EMPTY_SORTED_BAG = UnmodifiableSortedBag.decorate(new TreeBag());
    }
}
