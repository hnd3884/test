package org.apache.commons.collections4.iterators;

import org.apache.commons.collections4.Unmodifiable;
import org.apache.commons.collections4.MapIterator;

public final class UnmodifiableMapIterator<K, V> implements MapIterator<K, V>, Unmodifiable
{
    private final MapIterator<? extends K, ? extends V> iterator;
    
    public static <K, V> MapIterator<K, V> unmodifiableMapIterator(final MapIterator<? extends K, ? extends V> iterator) {
        if (iterator == null) {
            throw new NullPointerException("MapIterator must not be null");
        }
        if (iterator instanceof Unmodifiable) {
            final MapIterator<K, V> tmpIterator = (MapIterator<K, V>)iterator;
            return tmpIterator;
        }
        return new UnmodifiableMapIterator<K, V>(iterator);
    }
    
    private UnmodifiableMapIterator(final MapIterator<? extends K, ? extends V> iterator) {
        this.iterator = iterator;
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
