package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public interface TimeReservoir<V>
{
    int size(final long p0, final TimeUnit p1);
    
    void update(final V p0, final long p1, final TimeUnit p2);
    
    UniformTimeSnapshot getSnapshot(final long p0, final TimeUnit p1);
    
    long interval(final TimeUnit p0);
}
