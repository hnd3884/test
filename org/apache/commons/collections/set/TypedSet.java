package org.apache.commons.collections.set;

import org.apache.commons.collections.functors.InstanceofPredicate;
import java.util.Set;

public class TypedSet
{
    public static Set decorate(final Set set, final Class type) {
        return new PredicatedSet(set, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedSet() {
    }
}
