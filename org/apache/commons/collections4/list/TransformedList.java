package org.apache.commons.collections4.list;

import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import java.util.ListIterator;
import java.util.Collection;
import org.apache.commons.collections4.Transformer;
import java.util.List;
import org.apache.commons.collections4.collection.TransformedCollection;

public class TransformedList<E> extends TransformedCollection<E> implements List<E>
{
    private static final long serialVersionUID = 1077193035000013141L;
    
    public static <E> TransformedList<E> transformingList(final List<E> list, final Transformer<? super E, ? extends E> transformer) {
        return new TransformedList<E>(list, transformer);
    }
    
    public static <E> TransformedList<E> transformedList(final List<E> list, final Transformer<? super E, ? extends E> transformer) {
        final TransformedList<E> decorated = new TransformedList<E>(list, transformer);
        if (list.size() > 0) {
            final E[] values = (E[])list.toArray();
            list.clear();
            for (final E value : values) {
                decorated.decorated().add(transformer.transform(value));
            }
        }
        return decorated;
    }
    
    protected TransformedList(final List<E> list, final Transformer<? super E, ? extends E> transformer) {
        super(list, transformer);
    }
    
    protected List<E> getList() {
        return (List)this.decorated();
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
        return this.getList().get(index);
    }
    
    @Override
    public int indexOf(final Object object) {
        return this.getList().indexOf(object);
    }
    
    @Override
    public int lastIndexOf(final Object object) {
        return this.getList().lastIndexOf(object);
    }
    
    @Override
    public E remove(final int index) {
        return this.getList().remove(index);
    }
    
    @Override
    public void add(final int index, E object) {
        object = this.transform(object);
        this.getList().add(index, object);
    }
    
    @Override
    public boolean addAll(final int index, Collection<? extends E> coll) {
        coll = this.transform(coll);
        return this.getList().addAll(index, coll);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator<E> listIterator(final int i) {
        return new TransformedListIterator(this.getList().listIterator(i));
    }
    
    @Override
    public E set(final int index, E object) {
        object = this.transform(object);
        return this.getList().set(index, object);
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> sub = this.getList().subList(fromIndex, toIndex);
        return new TransformedList((List<Object>)sub, (Transformer<? super Object, ?>)this.transformer);
    }
    
    protected class TransformedListIterator extends AbstractListIteratorDecorator<E>
    {
        protected TransformedListIterator(final ListIterator<E> iterator) {
            super(iterator);
        }
        
        @Override
        public void add(E object) {
            object = (E)TransformedCollection.this.transform(object);
            this.getListIterator().add(object);
        }
        
        @Override
        public void set(E object) {
            object = (E)TransformedCollection.this.transform(object);
            this.getListIterator().set(object);
        }
    }
}
