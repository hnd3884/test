package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.FrequencyTrackingRingBuffer;

public final class UsageTrackingFilterCachingPolicy implements FilterCachingPolicy
{
    private static final int SENTINEL = Integer.MIN_VALUE;
    private final CacheOnLargeSegments segmentPolicy;
    private final FrequencyTrackingRingBuffer recentlyUsedFilters;
    private final int minFrequencyCostlyFilters;
    private final int minFrequencyCheapFilters;
    private final int minFrequencyOtherFilters;
    
    static boolean isCostly(final Filter filter) {
        return filter instanceof QueryWrapperFilter && ((QueryWrapperFilter)filter).getQuery() instanceof MultiTermQuery;
    }
    
    static boolean isCheapToCache(final DocIdSet set) {
        return set == null || set.isCacheable();
    }
    
    public UsageTrackingFilterCachingPolicy(final float minSizeRatio, final int historySize, final int minFrequencyCostlyFilters, final int minFrequencyCheapFilters, final int minFrequencyOtherFilters) {
        this(new CacheOnLargeSegments(minSizeRatio), historySize, minFrequencyCostlyFilters, minFrequencyCheapFilters, minFrequencyOtherFilters);
    }
    
    public UsageTrackingFilterCachingPolicy() {
        this(CacheOnLargeSegments.DEFAULT, 256, 2, 2, 5);
    }
    
    private UsageTrackingFilterCachingPolicy(final CacheOnLargeSegments segmentPolicy, final int historySize, final int minFrequencyCostlyFilters, final int minFrequencyCheapFilters, final int minFrequencyOtherFilters) {
        this.segmentPolicy = segmentPolicy;
        if (minFrequencyOtherFilters < minFrequencyCheapFilters || minFrequencyOtherFilters < minFrequencyCheapFilters) {
            throw new IllegalArgumentException("it does not make sense to cache regular filters more aggressively than filters that are costly to produce or cheap to cache");
        }
        if (minFrequencyCheapFilters > historySize || minFrequencyCostlyFilters > historySize || minFrequencyOtherFilters > historySize) {
            throw new IllegalArgumentException("The minimum frequencies should be less than the size of the history of filters that are being tracked");
        }
        this.recentlyUsedFilters = new FrequencyTrackingRingBuffer(historySize, Integer.MIN_VALUE);
        this.minFrequencyCostlyFilters = minFrequencyCostlyFilters;
        this.minFrequencyCheapFilters = minFrequencyCheapFilters;
        this.minFrequencyOtherFilters = minFrequencyOtherFilters;
    }
    
    @Override
    public void onUse(final Filter filter) {
        synchronized (this) {
            this.recentlyUsedFilters.add(filter.hashCode());
        }
    }
    
    @Override
    public boolean shouldCache(final Filter filter, final LeafReaderContext context, final DocIdSet set) throws IOException {
        if (!this.segmentPolicy.shouldCache(filter, context, set)) {
            return false;
        }
        final int frequency;
        synchronized (this) {
            frequency = this.recentlyUsedFilters.frequency(filter.hashCode());
        }
        return frequency >= this.minFrequencyOtherFilters || (isCostly(filter) && frequency >= this.minFrequencyCostlyFilters) || (isCheapToCache(set) && frequency >= this.minFrequencyCheapFilters);
    }
}
