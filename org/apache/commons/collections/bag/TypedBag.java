package org.apache.commons.collections.bag;

import org.apache.commons.collections.functors.InstanceofPredicate;
import org.apache.commons.collections.Bag;

public class TypedBag
{
    public static Bag decorate(final Bag bag, final Class type) {
        return new PredicatedBag(bag, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedBag() {
    }
}
