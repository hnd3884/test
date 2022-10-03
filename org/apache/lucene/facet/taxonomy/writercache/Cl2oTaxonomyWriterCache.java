package org.apache.lucene.facet.taxonomy.writercache;

import org.apache.lucene.facet.taxonomy.FacetLabel;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReadWriteLock;

public class Cl2oTaxonomyWriterCache implements TaxonomyWriterCache
{
    private final ReadWriteLock lock;
    private final int initialCapcity;
    private final int numHashArrays;
    private final float loadFactor;
    private volatile CompactLabelToOrdinal cache;
    
    public Cl2oTaxonomyWriterCache(final int initialCapcity, final float loadFactor, final int numHashArrays) {
        this.lock = new ReentrantReadWriteLock();
        this.cache = new CompactLabelToOrdinal(initialCapcity, loadFactor, numHashArrays);
        this.initialCapcity = initialCapcity;
        this.numHashArrays = numHashArrays;
        this.loadFactor = loadFactor;
    }
    
    @Override
    public void clear() {
        this.lock.writeLock().lock();
        try {
            this.cache = new CompactLabelToOrdinal(this.initialCapcity, this.loadFactor, this.numHashArrays);
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    @Override
    public synchronized void close() {
        this.cache = null;
    }
    
    @Override
    public boolean isFull() {
        return false;
    }
    
    @Override
    public int get(final FacetLabel categoryPath) {
        this.lock.readLock().lock();
        try {
            return this.cache.getOrdinal(categoryPath);
        }
        finally {
            this.lock.readLock().unlock();
        }
    }
    
    @Override
    public boolean put(final FacetLabel categoryPath, final int ordinal) {
        this.lock.writeLock().lock();
        try {
            this.cache.addLabel(categoryPath, ordinal);
            return false;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public int getMemoryUsage() {
        return (this.cache == null) ? 0 : this.cache.getMemoryUsage();
    }
}
