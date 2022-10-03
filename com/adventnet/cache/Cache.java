package com.adventnet.cache;

import com.adventnet.cache.dataobject.CASObject;
import java.util.Map;
import com.adventnet.cache.exception.CacheException;

public interface Cache
{
    String getName();
    
    int getType();
    
    Object get(final String p0) throws CacheException;
    
    Map<String, Object> getMulti(final String[] p0) throws CacheException;
    
    void put(final String p0, final Object p1) throws CacheException;
    
    void put(final String p0, final Object p1, final long p2) throws CacheException;
    
    void remove(final String p0) throws CacheException;
    
    Map getStats() throws CacheException;
    
    void purgeCache() throws CacheException;
    
    CASObject getForSet(final String p0) throws CacheException;
    
    boolean checkAndSet(final String p0, final long p1, final Object p2) throws CacheException;
    
    boolean checkAndSet(final String p0, final long p1, final Object p2, final long p3) throws CacheException;
    
    long addOrIncrement(final String p0, final long p1) throws CacheException;
    
    long increment(final String p0) throws CacheException;
    
    long increment(final String p0, final long p1) throws CacheException;
    
    void append(final String p0, final String p1) throws CacheException;
    
    void prepend(final String p0, final String p1) throws CacheException;
    
    boolean add(final String p0, final Object p1) throws CacheException;
    
    boolean add(final String p0, final Object p1, final long p2) throws CacheException;
}
