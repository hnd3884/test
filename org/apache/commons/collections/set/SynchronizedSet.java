package org.apache.commons.collections.set;

import java.util.Collection;
import java.util.Set;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedSet extends SynchronizedCollection implements Set
{
    private static final long serialVersionUID = -8304417378626543635L;
    
    public static Set decorate(final Set set) {
        return new SynchronizedSet(set);
    }
    
    protected SynchronizedSet(final Set set) {
        super(set);
    }
    
    protected SynchronizedSet(final Set set, final Object lock) {
        super(set, lock);
    }
    
    protected Set getSet() {
        return (Set)this.collection;
    }
}
