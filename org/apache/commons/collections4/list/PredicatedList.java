package org.apache.commons.collections4.list;

import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.collections4.Predicate;
import java.util.List;
import org.apache.commons.collections4.collection.PredicatedCollection;

public class PredicatedList<E> extends PredicatedCollection<E> implements List<E>
{
    private static final long serialVersionUID = -5722039223898659102L;
    
    public static <T> PredicatedList<T> predicatedList(final List<T> list, final Predicate<? super T> predicate) {
        return new PredicatedList<T>(list, predicate);
    }
    
    protected PredicatedList(final List<E> list, final Predicate<? super E> predicate) {
        super(list, predicate);
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
    public E remove(final int index) {
        return this.decorated().remove(index);
    }
    
    @Override
    public void add(final int index, final E object) {
        this.validate(object);
        this.decorated().add(index, object);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        for (final E aColl : coll) {
            this.validate(aColl);
        }
        return this.decorated().addAll(index, coll);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator<E> listIterator(final int i) {
        return new PredicatedListIterator(this.decorated().listIterator(i));
    }
    
    @Override
    public E set(final int index, final E object) {
        this.validate(object);
        return this.decorated().set(index, object);
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> sub = this.decorated().subList(fromIndex, toIndex);
        return new PredicatedList((List<Object>)sub, (Predicate<? super Object>)this.predicate);
    }
    
    protected class PredicatedListIterator extends AbstractListIteratorDecorator<E>
    {
        protected PredicatedListIterator(final ListIterator<E> iterator) {
            super(iterator);
        }
        
        @Override
        public void add(final E object) {
            PredicatedCollection.this.validate(object);
            this.getListIterator().add(object);
        }
        
        @Override
        public void set(final E object) {
            PredicatedCollection.this.validate(object);
            this.getListIterator().set(object);
        }
    }
}
