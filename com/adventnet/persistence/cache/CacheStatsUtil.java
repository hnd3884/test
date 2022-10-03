package com.adventnet.persistence.cache;

import java.util.concurrent.atomic.AtomicLong;

public class CacheStatsUtil
{
    private AtomicLong getCount;
    private AtomicLong getMissCount;
    private AtomicLong putCount;
    private AtomicLong putMissCount;
    private AtomicLong evictionCount;
    
    public CacheStatsUtil() {
        this.getCount = new AtomicLong();
        this.getMissCount = new AtomicLong();
        this.putCount = new AtomicLong();
        this.putMissCount = new AtomicLong();
        this.evictionCount = new AtomicLong();
    }
    
    public long getCount() {
        return this.getCount.get();
    }
    
    public long getMissCount() {
        return this.getMissCount.get();
    }
    
    public long putCount() {
        return this.putCount.get();
    }
    
    public long putMissCount() {
        return this.putMissCount.get();
    }
    
    public long evictionCount() {
        return this.evictionCount.get();
    }
    
    public void incrGetCount() {
        this.getCount.getAndIncrement();
    }
    
    public void incrGetMissCount() {
        this.getMissCount.getAndIncrement();
    }
    
    public void incrPutCount() {
        this.putCount.getAndIncrement();
    }
    
    public void incrPutMissCount() {
        this.putMissCount.getAndIncrement();
    }
    
    public void incrEvictionCount() {
        this.evictionCount.getAndIncrement();
    }
}
