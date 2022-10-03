package org.apache.commons.beanutils;

public interface MutableDynaClass extends DynaClass
{
    void add(final String p0);
    
    void add(final String p0, final Class<?> p1);
    
    void add(final String p0, final Class<?> p1, final boolean p2, final boolean p3);
    
    boolean isRestricted();
    
    void remove(final String p0);
    
    void setRestricted(final boolean p0);
}
