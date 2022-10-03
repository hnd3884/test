package org.apache.commons.collections.collection;

import org.apache.commons.collections.functors.InstanceofPredicate;
import java.util.Collection;

public class TypedCollection
{
    public static Collection decorate(final Collection coll, final Class type) {
        return new PredicatedCollection(coll, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedCollection() {
    }
}
