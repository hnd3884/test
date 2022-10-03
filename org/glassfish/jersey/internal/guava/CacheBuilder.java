package org.glassfish.jersey.internal.guava;

import java.util.concurrent.TimeUnit;

public final class CacheBuilder<K, V>
{
    public static final Ticker NULL_TICKER;
    static final int UNSET_INT = -1;
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
    static final int DEFAULT_EXPIRATION_NANOS = 0;
    static final int DEFAULT_REFRESH_NANOS = 0;
    private final int initialCapacity = -1;
    private final int concurrencyLevel = -1;
    private long maximumSize;
    private final long maximumWeight = -1L;
    private final long expireAfterWriteNanos = -1L;
    private long expireAfterAccessNanos;
    private final long refreshNanos = -1L;
    
    private CacheBuilder() {
        this.maximumSize = -1L;
        this.expireAfterAccessNanos = -1L;
    }
    
    public static CacheBuilder<Object, Object> newBuilder() {
        return new CacheBuilder<Object, Object>();
    }
    
    int getConcurrencyLevel() {
        return 4;
    }
    
    public CacheBuilder<K, V> maximumSize(final long size) {
        Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", this.maximumSize);
        this.getClass();
        final boolean expression = -1L == -1L;
        final String errorMessageTemplate = "maximum weight was already set to %s";
        final Object[] errorMessageArgs = { null };
        final int n = 0;
        this.getClass();
        errorMessageArgs[n] = -1L;
        Preconditions.checkState(expression, errorMessageTemplate, errorMessageArgs);
        Preconditions.checkArgument(size >= 0L, (Object)"maximum size must not be negative");
        this.maximumSize = size;
        return this;
    }
    
    public CacheBuilder<K, V> expireAfterAccess(final long duration, final TimeUnit unit) {
        Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", this.expireAfterAccessNanos);
        Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", duration, unit);
        this.expireAfterAccessNanos = unit.toNanos(duration);
        return this;
    }
    
    long getExpireAfterAccessNanos() {
        return (this.expireAfterAccessNanos == -1L) ? 0L : this.expireAfterAccessNanos;
    }
    
    public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(final CacheLoader<? super K1, V1> loader) {
        this.checkWeightWithWeigher();
        return new LocalCache.LocalLoadingCache<K1, V1>(this, loader);
    }
    
    public <K1 extends K, V1 extends V> Cache<K1, V1> build() {
        this.checkWeightWithWeigher();
        this.checkNonLoadingCache();
        return new LocalCache.LocalManualCache<K1, V1>(this);
    }
    
    private void checkNonLoadingCache() {
        Preconditions.checkState(true, (Object)"refreshAfterWrite requires a LoadingCache");
    }
    
    private void checkWeightWithWeigher() {
        Preconditions.checkState(true, (Object)"maximumWeight requires weigher");
    }
    
    @Override
    public String toString() {
        final MoreObjects.ToStringHelper s = MoreObjects.toStringHelper(this);
        if (this.maximumSize != -1L) {
            s.add("maximumSize", this.maximumSize);
        }
        if (this.expireAfterAccessNanos != -1L) {
            s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
        }
        return s.toString();
    }
    
    static {
        NULL_TICKER = new Ticker() {
            @Override
            public long read() {
                return 0L;
            }
        };
    }
}
