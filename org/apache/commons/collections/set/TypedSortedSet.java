package org.apache.commons.collections.set;

import org.apache.commons.collections.functors.InstanceofPredicate;
import java.util.SortedSet;

public class TypedSortedSet
{
    public static SortedSet decorate(final SortedSet set, final Class type) {
        return new PredicatedSortedSet(set, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedSortedSet() {
    }
}
