package org.glassfish.jersey.internal.guava;

import java.io.Serializable;

class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final K key;
    private final V value;
    
    ImmutableEntry(final K key, final V value) {
        this.key = key;
        this.value = value;
    }
    
    @Override
    public final K getKey() {
        return this.key;
    }
    
    @Override
    public final V getValue() {
        return this.value;
    }
    
    @Override
    public final V setValue(final V value) {
        throw new UnsupportedOperationException();
    }
}
