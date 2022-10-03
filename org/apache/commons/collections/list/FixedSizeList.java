package org.apache.commons.collections.list;

import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import java.util.ListIterator;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.BoundedCollection;

public class FixedSizeList extends AbstractSerializableListDecorator implements BoundedCollection
{
    private static final long serialVersionUID = -2218010673611160319L;
    
    public static List decorate(final List list) {
        return new FixedSizeList(list);
    }
    
    protected FixedSizeList(final List list) {
        super(list);
    }
    
    public boolean add(final Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public void add(final int index, final Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public boolean addAll(final Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public boolean addAll(final int index, final Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public Object get(final int index) {
        return this.getList().get(index);
    }
    
    public int indexOf(final Object object) {
        return this.getList().indexOf(object);
    }
    
    public Iterator iterator() {
        return UnmodifiableIterator.decorate(this.getCollection().iterator());
    }
    
    public int lastIndexOf(final Object object) {
        return this.getList().lastIndexOf(object);
    }
    
    public ListIterator listIterator() {
        return new FixedSizeListIterator(this.getList().listIterator(0));
    }
    
    public ListIterator listIterator(final int index) {
        return new FixedSizeListIterator(this.getList().listIterator(index));
    }
    
    public Object remove(final int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public boolean removeAll(final Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public boolean retainAll(final Collection coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }
    
    public Object set(final int index, final Object object) {
        return this.getList().set(index, object);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        final List sub = this.getList().subList(fromIndex, toIndex);
        return new FixedSizeList(sub);
    }
    
    public boolean isFull() {
        return true;
    }
    
    public int maxSize() {
        return this.size();
    }
    
    static class FixedSizeListIterator extends AbstractListIteratorDecorator
    {
        protected FixedSizeListIterator(final ListIterator iterator) {
            super(iterator);
        }
        
        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }
        
        public void add(final Object object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }
}
