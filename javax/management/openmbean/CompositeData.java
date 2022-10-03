package javax.management.openmbean;

import java.util.Collection;

public interface CompositeData
{
    CompositeType getCompositeType();
    
    Object get(final String p0);
    
    Object[] getAll(final String[] p0);
    
    boolean containsKey(final String p0);
    
    boolean containsValue(final Object p0);
    
    Collection<?> values();
    
    boolean equals(final Object p0);
    
    int hashCode();
    
    String toString();
}
