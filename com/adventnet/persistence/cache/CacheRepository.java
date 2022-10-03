package com.adventnet.persistence.cache;

import java.io.OutputStream;
import java.util.List;

public interface CacheRepository
{
    void initialize(final int p0, final boolean p1);
    
    Object addToCache(final Object p0, final Object p1);
    
    Object addToCache(final Object p0, final Object p1, final List p2);
    
    Object getFromCache(final Object p0);
    
    Object getFromCache(final Object p0, final List p1, final boolean p2);
    
    void clearCachedData();
    
    void clearCachedData(final String p0);
    
    void removeCachedData(final Object p0);
    
    List removeCachedData(final List p0);
    
    boolean getCloningStatus();
    
    void setCloningStatus(final boolean p0);
    
    @Deprecated
    String getCacheSummary(final boolean p0, final boolean p1);
    
    void writeCacheSummary(final OutputStream p0, final boolean p1, final boolean p2);
    
    void setCachingStatus(final boolean p0);
    
    boolean getCachingStatus();
    
    void setUseSoftReference(final boolean p0);
    
    boolean isUseSoftReference();
    
    String getRemovedTableSummary();
    
    void changeCacheForChangeInDeployment();
    
    void changeCacheForChangeInTables(final Object p0);
    
    int getMaxSize();
    
    void setMaxSize(final int p0);
    
    long currentSize();
    
    long getCount();
    
    long getMissCount();
    
    long putCount();
    
    long putMissCount();
    
    long evictionCount();
}
