package org.apache.commons.collections.list;

import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;

public abstract class AbstractListDecorator extends AbstractCollectionDecorator implements List
{
    protected AbstractListDecorator() {
    }
    
    protected AbstractListDecorator(final List list) {
        super(list);
    }
    
    protected List getList() {
        return (List)this.getCollection();
    }
    
    public void add(final int index, final Object object) {
        this.getList().add(index, object);
    }
    
    public boolean addAll(final int index, final Collection coll) {
        return this.getList().addAll(index, coll);
    }
    
    public Object get(final int index) {
        return this.getList().get(index);
    }
    
    public int indexOf(final Object object) {
        return this.getList().indexOf(object);
    }
    
    public int lastIndexOf(final Object object) {
        return this.getList().lastIndexOf(object);
    }
    
    public ListIterator listIterator() {
        return this.getList().listIterator();
    }
    
    public ListIterator listIterator(final int index) {
        return this.getList().listIterator(index);
    }
    
    public Object remove(final int index) {
        return this.getList().remove(index);
    }
    
    public Object set(final int index, final Object object) {
        return this.getList().set(index, object);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        return this.getList().subList(fromIndex, toIndex);
    }
}
