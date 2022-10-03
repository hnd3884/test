package org.apache.commons.collections.collection;

import java.util.Iterator;
import java.util.Collection;

public abstract class AbstractCollectionDecorator implements Collection
{
    protected Collection collection;
    
    protected AbstractCollectionDecorator() {
    }
    
    protected AbstractCollectionDecorator(final Collection coll) {
        if (coll == null) {
            throw new IllegalArgumentException("Collection must not be null");
        }
        this.collection = coll;
    }
    
    protected Collection getCollection() {
        return this.collection;
    }
    
    public boolean add(final Object object) {
        return this.collection.add(object);
    }
    
    public boolean addAll(final Collection coll) {
        return this.collection.addAll(coll);
    }
    
    public void clear() {
        this.collection.clear();
    }
    
    public boolean contains(final Object object) {
        return this.collection.contains(object);
    }
    
    public boolean isEmpty() {
        return this.collection.isEmpty();
    }
    
    public Iterator iterator() {
        return this.collection.iterator();
    }
    
    public boolean remove(final Object object) {
        return this.collection.remove(object);
    }
    
    public int size() {
        return this.collection.size();
    }
    
    public Object[] toArray() {
        return this.collection.toArray();
    }
    
    public Object[] toArray(final Object[] object) {
        return this.collection.toArray(object);
    }
    
    public boolean containsAll(final Collection coll) {
        return this.collection.containsAll(coll);
    }
    
    public boolean removeAll(final Collection coll) {
        return this.collection.removeAll(coll);
    }
    
    public boolean retainAll(final Collection coll) {
        return this.collection.retainAll(coll);
    }
    
    public boolean equals(final Object object) {
        return object == this || this.collection.equals(object);
    }
    
    public int hashCode() {
        return this.collection.hashCode();
    }
    
    public String toString() {
        return this.collection.toString();
    }
}
