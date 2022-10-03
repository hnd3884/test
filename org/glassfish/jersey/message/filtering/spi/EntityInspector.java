package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface EntityInspector
{
    void inspect(final Class<?> p0, final boolean p1);
}
