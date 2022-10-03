package org.glassfish.hk2.api;

public interface SingleCache<T>
{
    T getCache();
    
    boolean isCacheSet();
    
    void setCache(final T p0);
    
    void releaseCache();
}
