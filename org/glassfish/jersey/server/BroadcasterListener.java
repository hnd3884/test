package org.glassfish.jersey.server;

public interface BroadcasterListener<T>
{
    void onException(final ChunkedOutput<T> p0, final Exception p1);
    
    void onClose(final ChunkedOutput<T> p0);
}
