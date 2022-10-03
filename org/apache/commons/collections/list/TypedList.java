package org.apache.commons.collections.list;

import org.apache.commons.collections.functors.InstanceofPredicate;
import java.util.List;

public class TypedList
{
    public static List decorate(final List list, final Class type) {
        return new PredicatedList(list, InstanceofPredicate.getInstance(type));
    }
    
    protected TypedList() {
    }
}
