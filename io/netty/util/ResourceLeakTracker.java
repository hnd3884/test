package io.netty.util;

public interface ResourceLeakTracker<T>
{
    void record();
    
    void record(final Object p0);
    
    boolean close(final T p0);
}
