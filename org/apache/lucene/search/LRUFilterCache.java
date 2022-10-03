package org.apache.lucene.search;

import org.apache.lucene.util.Bits;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.RoaringDocIdSet;
import org.apache.lucene.util.Accountables;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.util.Accountable;

@Deprecated
public class LRUFilterCache implements FilterCache, Accountable
{
    static final long FILTER_DEFAULT_RAM_BYTES_USED = 216L;
    static final long HASHTABLE_RAM_BYTES_PER_ENTRY;
    static final long LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY;
    private final int maxSize;
    private final long maxRamBytesUsed;
    private final Map<Filter, Filter> uniqueFilters;
    private final Set<Filter> mostRecentlyUsedFilters;
    private final Map<Object, LeafCache> cache;
    private volatile long ramBytesUsed;
    private volatile long hitCount;
    private volatile long missCount;
    private volatile long cacheCount;
    private volatile long cacheSize;
    
    public LRUFilterCache(final int maxSize, final long maxRamBytesUsed) {
        this.maxSize = maxSize;
        this.maxRamBytesUsed = maxRamBytesUsed;
        this.uniqueFilters = new LinkedHashMap<Filter, Filter>(16, 0.75f, true);
        this.mostRecentlyUsedFilters = this.uniqueFilters.keySet();
        this.cache = new IdentityHashMap<Object, LeafCache>();
        this.ramBytesUsed = 0L;
    }
    
    protected void onHit(final Object readerCoreKey, final Filter filter) {
        ++this.hitCount;
    }
    
    protected void onMiss(final Object readerCoreKey, final Filter filter) {
        assert filter != null;
        ++this.missCount;
    }
    
    protected void onFilterCache(final Filter filter, final long ramBytesUsed) {
        this.ramBytesUsed += ramBytesUsed;
    }
    
    protected void onFilterEviction(final Filter filter, final long ramBytesUsed) {
        this.ramBytesUsed -= ramBytesUsed;
    }
    
    protected void onDocIdSetCache(final Object readerCoreKey, final long ramBytesUsed) {
        ++this.cacheSize;
        ++this.cacheCount;
        this.ramBytesUsed += ramBytesUsed;
    }
    
    protected void onDocIdSetEviction(final Object readerCoreKey, final int numEntries, final long sumRamBytesUsed) {
        this.ramBytesUsed -= sumRamBytesUsed;
        this.cacheSize -= numEntries;
    }
    
    protected void onClear() {
        this.ramBytesUsed = 0L;
        this.cacheSize = 0L;
    }
    
    boolean requiresEviction() {
        final int size = this.mostRecentlyUsedFilters.size();
        return size != 0 && (size > this.maxSize || this.ramBytesUsed() > this.maxRamBytesUsed);
    }
    
    synchronized DocIdSet get(final Filter filter, final LeafReaderContext context) {
        final Object readerKey = context.reader().getCoreCacheKey();
        final LeafCache leafCache = this.cache.get(readerKey);
        if (leafCache == null) {
            this.onMiss(readerKey, filter);
            return null;
        }
        final Filter singleton = this.uniqueFilters.get(filter);
        if (singleton == null) {
            this.onMiss(readerKey, filter);
            return null;
        }
        final DocIdSet cached = leafCache.get(singleton);
        if (cached == null) {
            this.onMiss(readerKey, singleton);
        }
        else {
            this.onHit(readerKey, singleton);
        }
        return cached;
    }
    
    synchronized void putIfAbsent(Filter filter, final LeafReaderContext context, final DocIdSet set) {
        assert set.isCacheable();
        final Filter singleton = this.uniqueFilters.get(filter);
        if (singleton == null) {
            this.uniqueFilters.put(filter, filter);
            this.onFilterCache(singleton, LRUFilterCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY + this.ramBytesUsed(filter));
        }
        else {
            filter = singleton;
        }
        final Object key = context.reader().getCoreCacheKey();
        LeafCache leafCache = this.cache.get(key);
        if (leafCache == null) {
            leafCache = new LeafCache(key);
            final LeafCache previous = this.cache.put(context.reader().getCoreCacheKey(), leafCache);
            this.ramBytesUsed += LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY;
            assert previous == null;
            context.reader().addCoreClosedListener(new LeafReader.CoreClosedListener() {
                @Override
                public void onClose(final Object ownerCoreCacheKey) {
                    LRUFilterCache.this.clearCoreCacheKey(ownerCoreCacheKey);
                }
            });
        }
        leafCache.putIfAbsent(filter, set);
        this.evictIfNecessary();
    }
    
    synchronized void evictIfNecessary() {
        if (this.requiresEviction()) {
            final Iterator<Filter> iterator = this.mostRecentlyUsedFilters.iterator();
            do {
                final Filter filter = iterator.next();
                iterator.remove();
                this.onEviction(filter);
            } while (iterator.hasNext() && this.requiresEviction());
        }
    }
    
    public synchronized void clearCoreCacheKey(final Object coreKey) {
        final LeafCache leafCache = this.cache.remove(coreKey);
        if (leafCache != null) {
            this.ramBytesUsed -= LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY;
            this.onDocIdSetEviction(coreKey, leafCache.cache.size(), leafCache.ramBytesUsed);
        }
    }
    
    public synchronized void clearFilter(final Filter filter) {
        final Filter singleton = this.uniqueFilters.remove(filter);
        if (singleton != null) {
            this.onEviction(singleton);
        }
    }
    
    private void onEviction(final Filter singleton) {
        this.onFilterEviction(singleton, LRUFilterCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY + this.ramBytesUsed(singleton));
        for (final LeafCache leafCache : this.cache.values()) {
            leafCache.remove(singleton);
        }
    }
    
    public synchronized void clear() {
        this.cache.clear();
        this.mostRecentlyUsedFilters.clear();
        this.onClear();
    }
    
    synchronized void assertConsistent() {
        if (this.requiresEviction()) {
            throw new AssertionError((Object)("requires evictions: size=" + this.mostRecentlyUsedFilters.size() + ", maxSize=" + this.maxSize + ", ramBytesUsed=" + this.ramBytesUsed() + ", maxRamBytesUsed=" + this.maxRamBytesUsed));
        }
        for (final LeafCache leafCache : this.cache.values()) {
            final Set<Filter> keys = Collections.newSetFromMap(new IdentityHashMap<Filter, Boolean>());
            keys.addAll(leafCache.cache.keySet());
            keys.removeAll(this.mostRecentlyUsedFilters);
            if (!keys.isEmpty()) {
                throw new AssertionError((Object)("One leaf cache contains more keys than the top-level cache: " + keys));
            }
        }
        long recomputedRamBytesUsed = LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY * this.cache.size() + LRUFilterCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY * this.uniqueFilters.size();
        for (final Filter filter : this.mostRecentlyUsedFilters) {
            recomputedRamBytesUsed += this.ramBytesUsed(filter);
        }
        for (final LeafCache leafCache2 : this.cache.values()) {
            recomputedRamBytesUsed += LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY * leafCache2.cache.size();
            for (final DocIdSet set : leafCache2.cache.values()) {
                recomputedRamBytesUsed += set.ramBytesUsed();
            }
        }
        if (recomputedRamBytesUsed != this.ramBytesUsed) {
            throw new AssertionError((Object)("ramBytesUsed mismatch : " + this.ramBytesUsed + " != " + recomputedRamBytesUsed));
        }
        long recomputedCacheSize = 0L;
        for (final LeafCache leafCache3 : this.cache.values()) {
            recomputedCacheSize += leafCache3.cache.size();
        }
        if (recomputedCacheSize != this.getCacheSize()) {
            throw new AssertionError((Object)("cacheSize mismatch : " + this.getCacheSize() + " != " + recomputedCacheSize));
        }
    }
    
    synchronized List<Filter> cachedFilters() {
        return new ArrayList<Filter>(this.mostRecentlyUsedFilters);
    }
    
    @Override
    public Filter doCache(Filter filter, final FilterCachingPolicy policy) {
        while (filter instanceof CachingWrapperFilter) {
            filter = ((CachingWrapperFilter)filter).in;
        }
        return new CachingWrapperFilter(filter, policy);
    }
    
    protected DocIdSet docIdSetToCache(final DocIdSet docIdSet, final LeafReader reader) throws IOException {
        if (docIdSet == null || docIdSet.isCacheable()) {
            return docIdSet;
        }
        final DocIdSetIterator it = docIdSet.iterator();
        if (it == null) {
            return null;
        }
        return this.cacheImpl(it, reader);
    }
    
    @Override
    public long ramBytesUsed() {
        return this.ramBytesUsed;
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        synchronized (this) {
            return Accountables.namedAccountables("segment", this.cache);
        }
    }
    
    protected long ramBytesUsed(final Filter filter) {
        if (filter instanceof Accountable) {
            return ((Accountable)filter).ramBytesUsed();
        }
        return 216L;
    }
    
    protected DocIdSet cacheImpl(final DocIdSetIterator iterator, final LeafReader reader) throws IOException {
        return new RoaringDocIdSet.Builder(reader.maxDoc()).add(iterator).build();
    }
    
    public final long getTotalCount() {
        return this.getHitCount() + this.getMissCount();
    }
    
    public final long getHitCount() {
        return this.hitCount;
    }
    
    public final long getMissCount() {
        return this.missCount;
    }
    
    public final long getCacheSize() {
        return this.cacheSize;
    }
    
    public final long getCacheCount() {
        return this.cacheCount;
    }
    
    public final long getEvictionCount() {
        return this.getCacheCount() - this.getCacheSize();
    }
    
    static {
        HASHTABLE_RAM_BYTES_PER_ENTRY = 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF * 2;
        LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY = LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
    }
    
    private class LeafCache implements Accountable
    {
        private final Object key;
        private final Map<Filter, DocIdSet> cache;
        private volatile long ramBytesUsed;
        
        LeafCache(final Object key) {
            this.key = key;
            this.cache = new IdentityHashMap<Filter, DocIdSet>();
            this.ramBytesUsed = 0L;
        }
        
        private void onDocIdSetCache(final long ramBytesUsed) {
            this.ramBytesUsed += ramBytesUsed;
            LRUFilterCache.this.onDocIdSetCache(this.key, ramBytesUsed);
        }
        
        private void onDocIdSetEviction(final long ramBytesUsed) {
            this.ramBytesUsed -= ramBytesUsed;
            LRUFilterCache.this.onDocIdSetEviction(this.key, 1, ramBytesUsed);
        }
        
        DocIdSet get(final Filter filter) {
            return this.cache.get(filter);
        }
        
        void putIfAbsent(final Filter filter, final DocIdSet set) {
            if (!this.cache.containsKey(filter)) {
                this.cache.put(filter, set);
                this.onDocIdSetCache(LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY + set.ramBytesUsed());
            }
        }
        
        void remove(final Filter filter) {
            final DocIdSet removed = this.cache.remove(filter);
            if (removed != null) {
                this.onDocIdSetEviction(LRUFilterCache.HASHTABLE_RAM_BYTES_PER_ENTRY + removed.ramBytesUsed());
            }
        }
        
        @Override
        public long ramBytesUsed() {
            return this.ramBytesUsed;
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
    }
    
    private class CachingWrapperFilter extends Filter
    {
        private final Filter in;
        private final FilterCachingPolicy policy;
        
        CachingWrapperFilter(final Filter in, final FilterCachingPolicy policy) {
            this.in = in;
            this.policy = policy;
        }
        
        @Override
        public DocIdSet getDocIdSet(final LeafReaderContext context, final Bits acceptDocs) throws IOException {
            if (context.ord == 0) {
                this.policy.onUse(this.in);
            }
            DocIdSet set = LRUFilterCache.this.get(this.in, context);
            if (set == null) {
                set = this.in.getDocIdSet(context, null);
                if (this.policy.shouldCache(this.in, context, set)) {
                    set = LRUFilterCache.this.docIdSetToCache(set, context.reader());
                    if (set == null) {
                        set = DocIdSet.EMPTY;
                    }
                    LRUFilterCache.this.putIfAbsent(this.in, context, set);
                }
            }
            return (set == DocIdSet.EMPTY) ? null : BitsFilteredDocIdSet.wrap(set, acceptDocs);
        }
        
        @Override
        public boolean equals(final Object obj) {
            return super.equals(obj) && this.in.equals(((CachingWrapperFilter)obj).in);
        }
        
        @Override
        public int hashCode() {
            return 31 * super.hashCode() + this.in.hashCode();
        }
        
        @Override
        public String toString(final String field) {
            return "CachingWrapperFilter(" + this.in.toString(field) + ")";
        }
    }
}
