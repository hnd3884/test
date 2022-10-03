package org.apache.commons.collections.bag;

import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.Bag;
import java.io.Serializable;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableBag extends AbstractBagDecorator implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = -1873799975157099624L;
    
    public static Bag decorate(final Bag bag) {
        if (bag instanceof Unmodifiable) {
            return bag;
        }
        return new UnmodifiableBag(bag);
    }
    
    private UnmodifiableBag(final Bag bag) {
        super(bag);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.collection);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.collection = (Collection)in.readObject();
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
    
    public boolean add(final Object object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    public boolean remove(final Object object, final int count) {
        throw new UnsupportedOperationException();
    }
    
    public Set uniqueSet() {
        final Set set = this.getBag().uniqueSet();
        return UnmodifiableSet.decorate(set);
    }
}
