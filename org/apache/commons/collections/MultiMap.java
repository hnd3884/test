package org.apache.commons.collections;

import java.util.Collection;
import java.util.Map;

public interface MultiMap extends Map
{
    Object remove(final Object p0, final Object p1);
    
    int size();
    
    Object get(final Object p0);
    
    boolean containsValue(final Object p0);
    
    Object put(final Object p0, final Object p1);
    
    Object remove(final Object p0);
    
    Collection values();
}
