package org.apache.commons.beanutils;

public interface DynaBean
{
    boolean contains(final String p0, final String p1);
    
    Object get(final String p0);
    
    Object get(final String p0, final int p1);
    
    Object get(final String p0, final String p1);
    
    DynaClass getDynaClass();
    
    void remove(final String p0, final String p1);
    
    void set(final String p0, final Object p1);
    
    void set(final String p0, final int p1, final Object p2);
    
    void set(final String p0, final String p1, final Object p2);
}
