package com.sun.xml.internal.ws.spi.db;

public interface PropertySetter
{
    Class getType();
    
     <A> A getAnnotation(final Class<A> p0);
    
    void set(final Object p0, final Object p1);
}
