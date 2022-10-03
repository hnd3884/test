package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.UniquePredicate;
import java.util.Iterator;

public class UniqueFilterIterator<E> extends FilterIterator<E>
{
    public UniqueFilterIterator(final Iterator<? extends E> iterator) {
        super(iterator, UniquePredicate.uniquePredicate());
    }
}
