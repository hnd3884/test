package org.apache.commons.collections.list;

import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.collection.SynchronizedCollection;

public class SynchronizedList extends SynchronizedCollection implements List
{
    private static final long serialVersionUID = -1403835447328619437L;
    
    public static List decorate(final List list) {
        return new SynchronizedList(list);
    }
    
    protected SynchronizedList(final List list) {
        super(list);
    }
    
    protected SynchronizedList(final List list, final Object lock) {
        super(list, lock);
    }
    
    protected List getList() {
        return (List)this.collection;
    }
    
    public void add(final int index, final Object object) {
        synchronized (this.lock) {
            this.getList().add(index, object);
        }
    }
    
    public boolean addAll(final int index, final Collection coll) {
        synchronized (this.lock) {
            return this.getList().addAll(index, coll);
        }
    }
    
    public Object get(final int index) {
        synchronized (this.lock) {
            return this.getList().get(index);
        }
    }
    
    public int indexOf(final Object object) {
        synchronized (this.lock) {
            return this.getList().indexOf(object);
        }
    }
    
    public int lastIndexOf(final Object object) {
        synchronized (this.lock) {
            return this.getList().lastIndexOf(object);
        }
    }
    
    public ListIterator listIterator() {
        return this.getList().listIterator();
    }
    
    public ListIterator listIterator(final int index) {
        return this.getList().listIterator(index);
    }
    
    public Object remove(final int index) {
        synchronized (this.lock) {
            return this.getList().remove(index);
        }
    }
    
    public Object set(final int index, final Object object) {
        synchronized (this.lock) {
            return this.getList().set(index, object);
        }
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        synchronized (this.lock) {
            final List list = this.getList().subList(fromIndex, toIndex);
            return new SynchronizedList(list, this.lock);
        }
    }
}
