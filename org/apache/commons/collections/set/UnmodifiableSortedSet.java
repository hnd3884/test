package org.apache.commons.collections.set;

import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.SortedSet;
import java.io.Serializable;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableSortedSet extends AbstractSortedSetDecorator implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = -725356885467962424L;
    
    public static SortedSet decorate(final SortedSet set) {
        if (set instanceof Unmodifiable) {
            return set;
        }
        return new UnmodifiableSortedSet(set);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.collection);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collection = (Collection)in.readObject();
    }
    
    private UnmodifiableSortedSet(final SortedSet set) {
        super(set);
    }
    
    public Iterator iterator() {
        return UnmodifiableIterator.decorate(this.getCollection().iterator());
    }
    
    public boolean add(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public boolean addAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public boolean removeAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public SortedSet subSet(final Object fromElement, final Object toElement) {
        final SortedSet sub = this.getSortedSet().subSet(fromElement, toElement);
        return new UnmodifiableSortedSet(sub);
    }
    
    public SortedSet headSet(final Object toElement) {
        final SortedSet sub = this.getSortedSet().headSet(toElement);
        return new UnmodifiableSortedSet(sub);
    }
    
    public SortedSet tailSet(final Object fromElement) {
        final SortedSet sub = this.getSortedSet().tailSet(fromElement);
        return new UnmodifiableSortedSet(sub);
    }
}
