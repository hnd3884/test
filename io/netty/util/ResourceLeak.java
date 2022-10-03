package io.netty.util;

@Deprecated
public interface ResourceLeak
{
    void record();
    
    void record(final Object p0);
    
    boolean close();
}
