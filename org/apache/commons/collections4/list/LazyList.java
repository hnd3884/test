package org.apache.commons.collections4.list;

import java.util.List;
import org.apache.commons.collections4.Factory;

public class LazyList<E> extends AbstractSerializableListDecorator<E>
{
    private static final long serialVersionUID = -1708388017160694542L;
    private final Factory<? extends E> factory;
    
    public static <E> LazyList<E> lazyList(final List<E> list, final Factory<? extends E> factory) {
        return new LazyList<E>(list, factory);
    }
    
    protected LazyList(final List<E> list, final Factory<? extends E> factory) {
        super(list);
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        this.factory = factory;
    }
    
    @Override
    public E get(final int index) {
        final int size = this.decorated().size();
        if (index >= size) {
            for (int i = size; i < index; ++i) {
                this.decorated().add(null);
            }
            final E object = (E)this.factory.create();
            this.decorated().add(object);
            return object;
        }
        E object = this.decorated().get(index);
        if (object == null) {
            object = (E)this.factory.create();
            this.decorated().set(index, object);
            return object;
        }
        return object;
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> sub = this.decorated().subList(fromIndex, toIndex);
        return new LazyList((List<Object>)sub, this.factory);
    }
}
