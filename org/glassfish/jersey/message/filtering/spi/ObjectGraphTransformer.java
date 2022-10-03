package org.glassfish.jersey.message.filtering.spi;

import org.glassfish.jersey.spi.Contract;

@Contract
public interface ObjectGraphTransformer<T>
{
    T transform(final ObjectGraph p0);
}
