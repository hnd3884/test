package com.azul.crs.com.fasterxml.jackson.databind.util;

public interface LookupCache<K, V>
{
    int size();
    
    V get(final Object p0);
    
    V put(final K p0, final V p1);
    
    V putIfAbsent(final K p0, final V p1);
    
    void clear();
}
