package org.glassfish.jersey.message.internal;

public interface CompletableReader<T>
{
    T complete(final T p0);
}
