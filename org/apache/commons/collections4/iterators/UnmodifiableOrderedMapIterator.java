package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.OrderedMapIterator;

public final class UnmodifiableOrderedMapIterator<K, V> implements OrderedMapIterator<K, V>, Unmodifiable
{
    private final OrderedMapIterator<? extends K, ? extends V> iterator;
    
    public static <K, V> OrderedMapIterator<K, V> unmodifiableOrderedMapIterator(final OrderedMapIterator<K, ? extends V> iterator) {
        if (iterator == null) {
            throw new NullPointerException("OrderedMapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            final OrderedMapIterator<K, V> tmpIterator = (OrderedMapIterator<K, V>)iterator;
            return tmpIterator;
        }
        return new UnmodifiableOrderedMapIterator<K, V>(iterator);
    }
    
    private UnmodifiableOrderedMapIterator(final OrderedMapIterator<K, ? extends V> iterator) {
        this.iterator = (OrderedMapIterator<? extends K, ? extends V>)iterator;
    }
    
    @Override
    public boolean hasNext() {
        return this.iterator.hasNext();
    }
    
    @Override
    public K next() {
        return (K)this.iterator.next();
    }
    
    @Override
    public boolean hasPrevious() {
        return this.iterator.hasPrevious();
    }
    
    @Override
    public K previous() {
        return (K)this.iterator.previous();
    }
    
    @Override
    public K getKey() {
        return (K)this.iterator.getKey();
    }
    
    @Override
    public V getValue() {
        return (V)this.iterator.getValue();
    }
    
    @Override
    public V setValue(final V value) {
        throw new UnsupportedOperationException("setValue() is not supported");
    }
    
    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() is not supported");
    }
}
