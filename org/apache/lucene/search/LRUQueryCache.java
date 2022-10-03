package org.apache.lucene.search;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.ReaderUtil;
import org.apache.lucene.index.Term;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.lucene.util.RamUsageEstimator;
import org.apache.lucene.util.Bits;
import java.io.IOException;
import org.apache.lucene.util.RoaringDocIdSet;
import org.apache.lucene.util.Accountables;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map;
import org.apache.lucene.util.Accountable;

public class LRUQueryCache implements QueryCache, Accountable
{
    static final long QUERY_DEFAULT_RAM_BYTES_USED = 192L;
    static final long HASHTABLE_RAM_BYTES_PER_ENTRY;
    static final long LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY;
    private final int maxSize;
    private final long maxRamBytesUsed;
    private final Map<Query, Query> uniqueQueries;
    private final Set<Query> mostRecentlyUsedQueries;
    private final Map<Object, LeafCache> cache;
    private volatile long ramBytesUsed;
    private volatile long hitCount;
    private volatile long missCount;
    private volatile long cacheCount;
    private volatile long cacheSize;
    
    public LRUQueryCache(final int maxSize, final long maxRamBytesUsed) {
        this.maxSize = maxSize;
        this.maxRamBytesUsed = maxRamBytesUsed;
        this.uniqueQueries = new LinkedHashMap<Query, Query>(16, 0.75f, true);
        this.mostRecentlyUsedQueries = this.uniqueQueries.keySet();
        this.cache = new IdentityHashMap<Object, LeafCache>();
        this.ramBytesUsed = 0L;
    }
    
    protected void onHit(final Object readerCoreKey, final Query query) {
        ++this.hitCount;
    }
    
    protected void onMiss(final Object readerCoreKey, final Query query) {
        assert query != null;
        ++this.missCount;
    }
    
    protected void onQueryCache(final Query query, final long ramBytesUsed) {
        this.ramBytesUsed += ramBytesUsed;
    }
    
    protected void onQueryEviction(final Query query, final long ramBytesUsed) {
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
        final int size = this.mostRecentlyUsedQueries.size();
        return size != 0 && (size > this.maxSize || this.ramBytesUsed() > this.maxRamBytesUsed);
    }
    
    synchronized DocIdSet get(final Query key, final LeafReaderContext context) {
        assert key.getBoost() == 1.0f;
        assert !(key instanceof BoostQuery);
        assert !(key instanceof ConstantScoreQuery);
        final Object readerKey = context.reader().getCoreCacheKey();
        final LeafCache leafCache = this.cache.get(readerKey);
        if (leafCache == null) {
            this.onMiss(readerKey, key);
            return null;
        }
        final Query singleton = this.uniqueQueries.get(key);
        if (singleton == null) {
            this.onMiss(readerKey, key);
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
    
    synchronized void putIfAbsent(Query query, final LeafReaderContext context, final DocIdSet set) {
        assert !(query instanceof BoostQuery);
        assert !(query instanceof ConstantScoreQuery);
        assert query.getBoost() == 1.0f;
        final Query singleton = this.uniqueQueries.get(query);
        if (singleton == null) {
            this.uniqueQueries.put(query, query);
            this.onQueryCache(singleton, LRUQueryCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY + this.ramBytesUsed(query));
        }
        else {
            query = singleton;
        }
        final Object key = context.reader().getCoreCacheKey();
        LeafCache leafCache = this.cache.get(key);
        if (leafCache == null) {
            leafCache = new LeafCache(key);
            final LeafCache previous = this.cache.put(context.reader().getCoreCacheKey(), leafCache);
            this.ramBytesUsed += LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY;
            assert previous == null;
            context.reader().addCoreClosedListener(new LeafReader.CoreClosedListener() {
                @Override
                public void onClose(final Object ownerCoreCacheKey) {
                    LRUQueryCache.this.clearCoreCacheKey(ownerCoreCacheKey);
                }
            });
        }
        leafCache.putIfAbsent(query, set);
        this.evictIfNecessary();
    }
    
    synchronized void evictIfNecessary() {
        if (this.requiresEviction()) {
            final Iterator<Query> iterator = this.mostRecentlyUsedQueries.iterator();
            do {
                final Query query = iterator.next();
                final int size = this.mostRecentlyUsedQueries.size();
                iterator.remove();
                if (size == this.mostRecentlyUsedQueries.size()) {
                    throw new ConcurrentModificationException("Removal from the cache failed! This is probably due to a query which has been modified after having been put into  the cache or a badly implemented clone(). Query class: [" + query.getClass() + "], query: [" + query + "]");
                }
                this.onEviction(query);
            } while (iterator.hasNext() && this.requiresEviction());
        }
    }
    
    public synchronized void clearCoreCacheKey(final Object coreKey) {
        final LeafCache leafCache = this.cache.remove(coreKey);
        if (leafCache != null) {
            this.ramBytesUsed -= LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY;
            final int numEntries = leafCache.cache.size();
            if (numEntries > 0) {
                this.onDocIdSetEviction(coreKey, numEntries, leafCache.ramBytesUsed);
            }
            else {
                assert numEntries == 0;
                assert leafCache.ramBytesUsed == 0L;
            }
        }
    }
    
    public synchronized void clearQuery(final Query query) {
        final Query singleton = this.uniqueQueries.remove(query);
        if (singleton != null) {
            this.onEviction(singleton);
        }
    }
    
    private void onEviction(final Query singleton) {
        this.onQueryEviction(singleton, LRUQueryCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY + this.ramBytesUsed(singleton));
        for (final LeafCache leafCache : this.cache.values()) {
            leafCache.remove(singleton);
        }
    }
    
    public synchronized void clear() {
        this.cache.clear();
        this.mostRecentlyUsedQueries.clear();
        this.onClear();
    }
    
    synchronized void assertConsistent() {
        if (this.requiresEviction()) {
            throw new AssertionError((Object)("requires evictions: size=" + this.mostRecentlyUsedQueries.size() + ", maxSize=" + this.maxSize + ", ramBytesUsed=" + this.ramBytesUsed() + ", maxRamBytesUsed=" + this.maxRamBytesUsed));
        }
        for (final LeafCache leafCache : this.cache.values()) {
            final Set<Query> keys = Collections.newSetFromMap(new IdentityHashMap<Query, Boolean>());
            keys.addAll(leafCache.cache.keySet());
            keys.removeAll(this.mostRecentlyUsedQueries);
            if (!keys.isEmpty()) {
                throw new AssertionError((Object)("One leaf cache contains more keys than the top-level cache: " + keys));
            }
        }
        long recomputedRamBytesUsed = LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY * this.cache.size() + LRUQueryCache.LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY * this.uniqueQueries.size();
        for (final Query query : this.mostRecentlyUsedQueries) {
            recomputedRamBytesUsed += this.ramBytesUsed(query);
        }
        for (final LeafCache leafCache2 : this.cache.values()) {
            recomputedRamBytesUsed += LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY * leafCache2.cache.size();
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
    
    synchronized List<Query> cachedQueries() {
        return new ArrayList<Query>(this.mostRecentlyUsedQueries);
    }
    
    @Override
    public Weight doCache(Weight weight, final QueryCachingPolicy policy) {
        while (weight instanceof CachingWrapperWeight) {
            weight = ((CachingWrapperWeight)weight).in;
        }
        return new CachingWrapperWeight(weight, policy);
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
    
    protected long ramBytesUsed(final Query query) {
        if (query instanceof Accountable) {
            return ((Accountable)query).ramBytesUsed();
        }
        return 192L;
    }
    
    protected DocIdSet cacheImpl(final BulkScorer scorer, final int maxDoc) throws IOException {
        final RoaringDocIdSet.Builder builder = new RoaringDocIdSet.Builder(maxDoc);
        scorer.score(new LeafCollector() {
            @Override
            public void setScorer(final Scorer scorer) throws IOException {
            }
            
            @Override
            public void collect(final int doc) throws IOException {
                builder.add(doc);
            }
        }, null);
        return builder.build();
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
        LINKED_HASHTABLE_RAM_BYTES_PER_ENTRY = LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY + 2 * RamUsageEstimator.NUM_BYTES_OBJECT_REF;
    }
    
    private class LeafCache implements Accountable
    {
        private final Object key;
        private final Map<Query, DocIdSet> cache;
        private volatile long ramBytesUsed;
        
        LeafCache(final Object key) {
            this.key = key;
            this.cache = new IdentityHashMap<Query, DocIdSet>();
            this.ramBytesUsed = 0L;
        }
        
        private void onDocIdSetCache(final long ramBytesUsed) {
            this.ramBytesUsed += ramBytesUsed;
            LRUQueryCache.this.onDocIdSetCache(this.key, ramBytesUsed);
        }
        
        private void onDocIdSetEviction(final long ramBytesUsed) {
            this.ramBytesUsed -= ramBytesUsed;
            LRUQueryCache.this.onDocIdSetEviction(this.key, 1, ramBytesUsed);
        }
        
        DocIdSet get(final Query query) {
            assert !(query instanceof BoostQuery);
            assert !(query instanceof ConstantScoreQuery);
            assert query.getBoost() == 1.0f;
            return this.cache.get(query);
        }
        
        void putIfAbsent(final Query query, final DocIdSet set) {
            assert !(query instanceof BoostQuery);
            assert !(query instanceof ConstantScoreQuery);
            assert query.getBoost() == 1.0f;
            if (!this.cache.containsKey(query)) {
                this.cache.put(query, set);
                this.onDocIdSetCache(LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY + set.ramBytesUsed());
            }
        }
        
        void remove(final Query query) {
            assert !(query instanceof BoostQuery);
            assert !(query instanceof ConstantScoreQuery);
            assert query.getBoost() == 1.0f;
            final DocIdSet removed = this.cache.remove(query);
            if (removed != null) {
                this.onDocIdSetEviction(LRUQueryCache.HASHTABLE_RAM_BYTES_PER_ENTRY + removed.ramBytesUsed());
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
    
    private class CachingWrapperWeight extends ConstantScoreWeight
    {
        private final Weight in;
        private final QueryCachingPolicy policy;
        private final AtomicBoolean used;
        
        CachingWrapperWeight(final Weight in, final QueryCachingPolicy policy) {
            super(in.getQuery());
            this.in = in;
            this.policy = policy;
            this.used = new AtomicBoolean(false);
        }
        
        @Override
        public void extractTerms(final Set<Term> terms) {
            this.in.extractTerms(terms);
        }
        
        private boolean cacheEntryHasReasonableWorstCaseSize(final int maxDoc) {
            final long worstCaseRamUsage = maxDoc / 8;
            final long totalRamAvailable = LRUQueryCache.this.maxRamBytesUsed;
            return worstCaseRamUsage * 5L < totalRamAvailable;
        }
        
        private DocIdSet cache(final LeafReaderContext context) throws IOException {
            final BulkScorer scorer = this.in.bulkScorer(context);
            if (scorer == null) {
                return DocIdSet.EMPTY;
            }
            return LRUQueryCache.this.cacheImpl(scorer, context.reader().maxDoc());
        }
        
        private boolean shouldCache(final LeafReaderContext context) throws IOException {
            return this.cacheEntryHasReasonableWorstCaseSize(ReaderUtil.getTopLevelContext(context).reader().maxDoc()) && this.policy.shouldCache(this.in.getQuery(), context);
        }
        
        @Override
        public Scorer scorer(final LeafReaderContext context) throws IOException {
            if (this.used.compareAndSet(false, true)) {
                this.policy.onUse(this.getQuery());
            }
            DocIdSet docIdSet = LRUQueryCache.this.get(this.in.getQuery(), context);
            if (docIdSet == null) {
                if (!this.shouldCache(context)) {
                    return this.in.scorer(context);
                }
                docIdSet = this.cache(context);
                LRUQueryCache.this.putIfAbsent(this.in.getQuery(), context, docIdSet);
            }
            assert docIdSet != null;
            if (docIdSet == DocIdSet.EMPTY) {
                return null;
            }
            final DocIdSetIterator disi = docIdSet.iterator();
            if (disi == null) {
                return null;
            }
            return new ConstantScoreScorer(this, 0.0f, disi);
        }
        
        @Override
        public BulkScorer bulkScorer(final LeafReaderContext context) throws IOException {
            if (this.used.compareAndSet(false, true)) {
                this.policy.onUse(this.getQuery());
            }
            DocIdSet docIdSet = LRUQueryCache.this.get(this.in.getQuery(), context);
            if (docIdSet == null) {
                if (!this.shouldCache(context)) {
                    return this.in.bulkScorer(context);
                }
                docIdSet = this.cache(context);
                LRUQueryCache.this.putIfAbsent(this.in.getQuery(), context, docIdSet);
            }
            assert docIdSet != null;
            if (docIdSet == DocIdSet.EMPTY) {
                return null;
            }
            final DocIdSetIterator disi = docIdSet.iterator();
            if (disi == null) {
                return null;
            }
            return new DefaultBulkScorer(new ConstantScoreScorer(this, 0.0f, disi));
        }
    }
}
