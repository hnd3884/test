package org.apache.commons.collections.map;

import org.apache.commons.collections.functors.InstanceofPredicate;
import java.util.SortedMap;

public class TypedSortedMap
{
    public static SortedMap decorate(final SortedMap map, final Class keyType, final Class valueType) {
        return new PredicatedSortedMap(map, InstanceofPredicate.getInstance(keyType), InstanceofPredicate.getInstance(valueType));
    }
    
    protected TypedSortedMap() {
    }
}
