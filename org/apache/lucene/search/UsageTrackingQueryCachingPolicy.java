package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.FrequencyTrackingRingBuffer;

public final class UsageTrackingQueryCachingPolicy implements QueryCachingPolicy
{
    private static final int SENTINEL = Integer.MIN_VALUE;
    private final CacheOnLargeSegments segmentPolicy;
    private final FrequencyTrackingRingBuffer recentlyUsedFilters;
    
    static boolean isCostly(final Query query) {
        return query instanceof MultiTermQuery || query instanceof MultiTermQueryConstantScoreWrapper;
    }
    
    static boolean isCheap(final Query query) {
        return query instanceof TermQuery;
    }
    
    public UsageTrackingQueryCachingPolicy(final int minIndexSize, final float minSizeRatio, final int historySize) {
        this(new CacheOnLargeSegments(minIndexSize, minSizeRatio), historySize);
    }
    
    public UsageTrackingQueryCachingPolicy() {
        this(CacheOnLargeSegments.DEFAULT, 256);
    }
    
    private UsageTrackingQueryCachingPolicy(final CacheOnLargeSegments segmentPolicy, final int historySize) {
        this.segmentPolicy = segmentPolicy;
        this.recentlyUsedFilters = new FrequencyTrackingRingBuffer(historySize, Integer.MIN_VALUE);
    }
    
    protected int minFrequencyToCache(final Query query) {
        if (isCostly(query)) {
            return 2;
        }
        if (isCheap(query)) {
            return 20;
        }
        return 5;
    }
    
    @Override
    public void onUse(final Query query) {
        assert !(query instanceof BoostQuery);
        assert !(query instanceof ConstantScoreQuery);
        final int hashCode = query.hashCode();
        synchronized (this) {
            this.recentlyUsedFilters.add(hashCode);
        }
    }
    
    int frequency(final Query query) {
        assert !(query instanceof BoostQuery);
        assert !(query instanceof ConstantScoreQuery);
        final int hashCode = query.hashCode();
        synchronized (this) {
            return this.recentlyUsedFilters.frequency(hashCode);
        }
    }
    
    @Override
    public boolean shouldCache(final Query query, final LeafReaderContext context) throws IOException {
        if (query instanceof MatchAllDocsQuery || query instanceof MatchNoDocsQuery) {
            return false;
        }
        if (query instanceof BooleanQuery) {
            final BooleanQuery bq = (BooleanQuery)query;
            if (bq.clauses().isEmpty()) {
                return false;
            }
        }
        if (query instanceof DisjunctionMaxQuery) {
            final DisjunctionMaxQuery dmq = (DisjunctionMaxQuery)query;
            if (dmq.getDisjuncts().isEmpty()) {
                return false;
            }
        }
        if (!this.segmentPolicy.shouldCache(query, context)) {
            return false;
        }
        final int frequency = this.frequency(query);
        final int minFrequency = this.minFrequencyToCache(query);
        return frequency >= minFrequency;
    }
}
