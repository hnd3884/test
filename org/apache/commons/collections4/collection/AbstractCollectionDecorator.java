package org.apache.commons.collections4.collection;

import java.util.Iterator;
import java.io.Serializable;
import java.util.Collection;

public abstract class AbstractCollectionDecorator<E> implements Collection<E>, Serializable
{
    private static final long serialVersionUID = 6249888059822088500L;
    private Collection<E> collection;
    
    protected AbstractCollectionDecorator() {
    }
    
    protected AbstractCollectionDecorator(final Collection<E> coll) {
        if (coll == null) {
            throw new NullPointerException("Collection must not be null.");
        }
        this.collection = coll;
    }
    
    protected Collection<E> decorated() {
        return this.collection;
    }
    
    protected void setCollection(final Collection<E> coll) {
        this.collection = coll;
    }
    
    @Override
    public boolean add(final E object) {
        return this.decorated().add(object);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        return this.decorated().addAll(coll);
    }
    
    @Override
    public void clear() {
        this.decorated().clear();
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.decorated().contains(object);
    }
    
    @Override
    public boolean isEmpty() {
        return this.decorated().isEmpty();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.decorated().iterator();
    }
    
    @Override
    public boolean remove(final Object object) {
        return this.decorated().remove(object);
    }
    
    @Override
    public int size() {
        return this.decorated().size();
    }
    
    @Override
    public Object[] toArray() {
        return this.decorated().toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] object) {
        return this.decorated().toArray(object);
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        return this.decorated().containsAll(coll);
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        return this.decorated().removeAll(coll);
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        return this.decorated().retainAll(coll);
    }
    
    @Override
    public String toString() {
        return this.decorated().toString();
    }
}
