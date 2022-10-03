package org.apache.commons.collections.bag;

import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.SortedBag;

public class TypedSortedBag
{
    public static SortedBag decorate(final SortedBag bag, final Class type) {
        return new PredicatedSortedBag(bag, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedSortedBag() {
    }
}
