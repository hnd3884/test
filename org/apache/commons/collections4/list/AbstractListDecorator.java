package org.apache.commons.collections4.list;

import java.util.ListIterator;
import java.util.Collection;
import java.util.List;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractListDecorator<E> extends AbstractCollectionDecorator<E> implements List<E>
{
    private static final long serialVersionUID = 4500739654952315623L;
    
    protected AbstractListDecorator() {
    }
    
    protected AbstractListDecorator(final List<E> list) {
        super(list);
    }
    
    @Override
    protected List<E> decorated() {
        return (List)super.decorated();
    }
    
    @Override
    public boolean equals(final Object object) {
        return object == this || this.decorated().equals(object);
    }
    
    @Override
    public int hashCode() {
        return this.decorated().hashCode();
    }
    
    @Override
    public void add(final int index, final E object) {
        this.decorated().add(index, object);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        return this.decorated().addAll(index, coll);
    }
    
    @Override
    public E get(final int index) {
        return this.decorated().get(index);
    }
    
    @Override
    public int indexOf(final Object object) {
        return this.decorated().indexOf(object);
    }
    
    @Override
    public int lastIndexOf(final Object object) {
        return this.decorated().lastIndexOf(object);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.decorated().listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        return this.decorated().listIterator(index);
    }
    
    @Override
    public E remove(final int index) {
        return this.decorated().remove(index);
    }
    
    @Override
    public E set(final int index, final E object) {
        return this.decorated().set(index, object);
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return this.decorated().subList(fromIndex, toIndex);
    }
}
