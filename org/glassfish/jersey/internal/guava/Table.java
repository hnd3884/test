package org.glassfish.jersey.internal.guava;

import java.util.Set;
import java.util.Map;

public interface Table<R, C, V>
{
    boolean contains(final Object p0, final Object p1);
    
    boolean containsRow(final Object p0);
    
    boolean containsColumn(final Object p0);
    
    boolean containsValue(final Object p0);
    
    V get(final Object p0, final Object p1);
    
    int size();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    void clear();
    
    V put(final R p0, final C p1, final V p2);
    
    void putAll(final Table<? extends R, ? extends C, ? extends V> p0);
    
    V remove(final Object p0, final Object p1);
    
    Map<C, V> row(final R p0);
    
    Map<R, V> column(final C p0);
    
    Set<Cell<R, C, V>> cellSet();
    
    Set<R> rowKeySet();
    
    Set<C> columnKeySet();
    
    Map<R, Map<C, V>> rowMap();
    
    Map<C, Map<R, V>> columnMap();
    
    public interface Cell<R, C, V>
    {
        R getRowKey();
        
        C getColumnKey();
        
        V getValue();
        
        boolean equals(final Object p0);
        
        int hashCode();
    }
}
