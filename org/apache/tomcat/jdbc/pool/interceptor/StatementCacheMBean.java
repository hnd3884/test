package org.apache.tomcat.jdbc.pool.interceptor;

import java.util.concurrent.atomic.AtomicInteger;

public interface StatementCacheMBean
{
    boolean isCachePrepared();
    
    boolean isCacheCallable();
    
    int getMaxCacheSize();
    
    AtomicInteger getCacheSize();
    
    int getCacheSizePerConnection();
}
