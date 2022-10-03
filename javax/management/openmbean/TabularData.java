package javax.management.openmbean;

import java.util.Collection;
import java.util.Set;

public interface TabularData
{
    TabularType getTabularType();
    
    Object[] calculateIndex(final CompositeData p0);
    
    int size();
    
    boolean isEmpty();
    
    boolean containsKey(final Object[] p0);
    
    boolean containsValue(final CompositeData p0);
    
    CompositeData get(final Object[] p0);
    
    void put(final CompositeData p0);
    
    CompositeData remove(final Object[] p0);
    
    void putAll(final CompositeData[] p0);
    
    void clear();
    
    Set<?> keySet();
    
    Collection<?> values();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
