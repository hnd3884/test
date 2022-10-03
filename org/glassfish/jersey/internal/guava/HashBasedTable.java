package org.glassfish.jersey.internal.guava;

import java.io.Serializable;
import java.util.Set;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.Map;

public class HashBasedTable<R, C, V> extends StandardTable<R, C, V>
{
    private static final long serialVersionUID = 0L;
    
    private HashBasedTable(final Map<R, Map<C, V>> backingMap, final Factory<C, V> factory) {
        super(backingMap, factory);
    }
    
    public static <R, C, V> HashBasedTable<R, C, V> create() {
        return new HashBasedTable<R, C, V>(new HashMap<R, Map<C, V>>(), new Factory<C, V>(0));
    }
    
    @Override
    public boolean contains(final Object rowKey, final Object columnKey) {
        return super.contains(rowKey, columnKey);
    }
    
    @Override
    public boolean containsColumn(final Object columnKey) {
        return super.containsColumn(columnKey);
    }
    
    @Override
    public boolean containsValue(final Object value) {
        return super.containsValue(value);
    }
    
    @Override
    public V get(final Object rowKey, final Object columnKey) {
        return super.get(rowKey, columnKey);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }
    
    private static class Factory<C, V> implements Supplier<Map<C, V>>, Serializable
    {
        private static final long serialVersionUID = 0L;
        final int expectedSize;
        
        Factory(final int expectedSize) {
            this.expectedSize = expectedSize;
        }
        
        @Override
        public Map<C, V> get() {
            return (Map<C, V>)Maps.newHashMapWithExpectedSize(this.expectedSize);
        }
    }
}
