package com.sun.xml.internal.ws.spi.db;

public interface PropertyGetter
{
    Class getType();
    
     <A> A getAnnotation(final Class<A> p0);
    
    Object get(final Object p0);
}
