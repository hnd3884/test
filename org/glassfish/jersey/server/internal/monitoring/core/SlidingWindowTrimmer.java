package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.ConcurrentNavigableMap;

public interface SlidingWindowTrimmer<V>
{
    void trim(final ConcurrentNavigableMap<Long, V> p0, final long p1);
    
    void setTimeReservoir(final TimeReservoir<V> p0);
}
