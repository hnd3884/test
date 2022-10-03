package org.bouncycastle.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;

public class CollectionStore<T> implements Store<T>, Iterable<T>
{
    private Collection<T> _local;
    
    public CollectionStore(final Collection<T> collection) {
        this._local = new ArrayList<T>((Collection<? extends T>)collection);
    }
    
    public Collection<T> getMatches(final Selector<T> selector) {
        if (selector == null) {
            return new ArrayList<T>((Collection<? extends T>)this._local);
        }
        final ArrayList list = new ArrayList();
        for (final T next : this._local) {
            if (selector.match(next)) {
                list.add(next);
            }
        }
        return list;
    }
    
    public Iterator<T> iterator() {
        return this.getMatches(null).iterator();
    }
}
