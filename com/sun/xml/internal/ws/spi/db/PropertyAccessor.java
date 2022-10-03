package com.sun.xml.internal.ws.spi.db;

public interface PropertyAccessor<B, V>
{
    V get(final B p0) throws DatabindingException;
    
    void set(final B p0, final V p1) throws DatabindingException;
}
