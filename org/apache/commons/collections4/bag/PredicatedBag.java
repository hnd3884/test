package org.apache.commons.collections4.bag;

import java.util.Set;
import java.util.Collection;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.collection.PredicatedCollection;

public class PredicatedBag<E> extends PredicatedCollection<E> implements Bag<E>
{
    private static final long serialVersionUID = -2575833140344736876L;
    
    public static <E> PredicatedBag<E> predicatedBag(final Bag<E> bag, final Predicate<? super E> predicate) {
        return new PredicatedBag<E>(bag, predicate);
    }
    
    protected PredicatedBag(final Bag<E> bag, final Predicate<? super E> predicate) {
        super(bag, predicate);
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
    public boolean add(final E object, final int count) {
        this.validate(object);
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
    
    @Override
    public int getCount(final Object object) {
        return this.decorated().getCount(object);
    }
}
