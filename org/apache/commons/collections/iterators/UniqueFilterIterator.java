package org.apache.commons.collections.iterators;

import org.apache.commons.collections.functors.UniquePredicate;
import java.util.Iterator;

public class UniqueFilterIterator extends FilterIterator
{
    public UniqueFilterIterator(final Iterator iterator) {
        super(iterator, UniquePredicate.getInstance());
    }
}
