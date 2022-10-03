package org.glassfish.jersey.server;

import java.io.Closeable;

public interface CloseableService
{
    boolean add(final Closeable p0);
    
    void close();
}
