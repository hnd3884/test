package org.apache.commons.collections4.bag;

import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.AbstractCollectionDecorator;

public abstract class AbstractBagDecorator<E> extends AbstractCollectionDecorator<E> implements Bag<E>
{
    private static final long serialVersionUID = -3768146017343785417L;
    
    protected AbstractBagDecorator() {
    }
    
    protected AbstractBagDecorator(final Bag<E> bag) {
        super(bag);
    }
    
    @Override
    protected Bag<E> decorated() {
        return (Bag)super.decorated();
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
    public int getCount(final Object object) {
        return this.decorated().getCount(object);
    }
    
    @Override
    public boolean add(final E object, final int count) {
        return this.decorated().add(object, count);
    }
    
    @Override
    public boolean remove(final Object object, final int count) {
        return this.decorated().remove(object, count);
    }
    
    @Override
    public Set<E> uniqueSet() {
        return this.decorated().uniqueSet();
    }
}
