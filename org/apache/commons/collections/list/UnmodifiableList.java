package org.apache.commons.collections.list;

import org.apache.commons.collections.iterators.UnmodifiableListIterator;
import java.util.ListIterator;
import java.util.Collection;
import org.apache.commons.collections.iterators.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableList extends AbstractSerializableListDecorator implements Unmodifiable
{
    private static final long serialVersionUID = 6595182819922443652L;
    
    public static List decorate(final List list) {
        if (list instanceof Unmodifiable) {
            return list;
        }
        return new UnmodifiableList(list);
    }
    
    private UnmodifiableList(final List list) {
        super(list);
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
    
    public ListIterator listIterator() {
        return UnmodifiableListIterator.decorate(this.getList().listIterator());
    }
    
    public ListIterator listIterator(final int index) {
        return UnmodifiableListIterator.decorate(this.getList().listIterator(index));
    }
    
    public void add(final int index, final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public boolean addAll(final int index, final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    public Object remove(final int index) {
        throw new UnsupportedOperationException();
    }
    
    public Object set(final int index, final Object object) {
        throw new UnsupportedOperationException();
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        final List sub = this.getList().subList(fromIndex, toIndex);
        return new UnmodifiableList(sub);
    }
}
