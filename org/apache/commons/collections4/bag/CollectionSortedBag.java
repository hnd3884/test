package org.apache.commons.collections4.bag;

import java.util.Iterator;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.SortedBag;

public final class CollectionSortedBag<E> extends AbstractSortedBagDecorator<E>
{
    private static final long serialVersionUID = -2560033712679053143L;
    
    public static <E> SortedBag<E> collectionSortedBag(final SortedBag<E> bag) {
        return new CollectionSortedBag<E>(bag);
    }
    
    public CollectionSortedBag(final SortedBag<E> bag) {
        super(bag);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.setCollection((Collection<E>)in.readObject());
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        final Iterator<?> e = coll.iterator();
        while (e.hasNext()) {
            if (!this.contains(e.next())) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean add(final E object) {
        return this.add(object, 1);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean changed = false;
        final Iterator<? extends E> i = coll.iterator();
        while (i.hasNext()) {
            final boolean added = this.add(i.next(), 1);
            changed = (changed || added);
        }
        return changed;
    }
    
    @Override
    public boolean remove(final Object object) {
        return this.remove(object, 1);
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        if (coll != null) {
            boolean result = false;
            for (final Object obj : coll) {
                final boolean changed = this.remove(obj, this.getCount(obj));
                result = (result || changed);
            }
            return result;
        }
        return this.decorated().removeAll(null);
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        if (coll != null) {
            boolean modified = false;
            final Iterator<E> e = this.iterator();
            while (e.hasNext()) {
                if (!coll.contains(e.next())) {
                    e.remove();
                    modified = true;
                }
            }
            return modified;
        }
        return this.decorated().retainAll(null);
    }
    
    @Override
    public boolean add(final E object, final int count) {
        this.decorated().add(object, count);
        return true;
    }
}
