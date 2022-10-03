package io.opencensus.trace.export;

import java.util.Iterator;
import java.util.HashSet;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import io.opencensus.trace.Status;
import java.util.Collections;
import java.util.HashMap;
import io.opencensus.internal.Utils;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import java.util.Set;
import java.util.Collection;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class SampledSpanStore
{
    protected SampledSpanStore() {
    }
    
    static SampledSpanStore newNoopSampledSpanStore() {
        return new NoopSampledSpanStore();
    }
    
    public abstract Summary getSummary();
    
    public abstract Collection<SpanData> getLatencySampledSpans(final LatencyFilter p0);
    
    public abstract Collection<SpanData> getErrorSampledSpans(final ErrorFilter p0);
    
    @Deprecated
    public abstract void registerSpanNamesForCollection(final Collection<String> p0);
    
    @Deprecated
    public abstract void unregisterSpanNamesForCollection(final Collection<String> p0);
    
    public abstract Set<String> getRegisteredSpanNamesForCollection();
    
    @Immutable
    public abstract static class Summary
    {
        Summary() {
        }
        
        public static Summary create(final Map<String, PerSpanNameSummary> perSpanNameSummary) {
            return new AutoValue_SampledSpanStore_Summary(Collections.unmodifiableMap((Map<? extends String, ? extends PerSpanNameSummary>)new HashMap<String, PerSpanNameSummary>(Utils.checkNotNull(perSpanNameSummary, "perSpanNameSummary"))));
        }
        
        public abstract Map<String, PerSpanNameSummary> getPerSpanNameSummary();
    }
    
    @Immutable
    public abstract static class PerSpanNameSummary
    {
        PerSpanNameSummary() {
        }
        
        public static PerSpanNameSummary create(final Map<LatencyBucketBoundaries, Integer> numbersOfLatencySampledSpans, final Map<Status.CanonicalCode, Integer> numbersOfErrorSampledSpans) {
            return new AutoValue_SampledSpanStore_PerSpanNameSummary(Collections.unmodifiableMap((Map<? extends LatencyBucketBoundaries, ? extends Integer>)new HashMap<LatencyBucketBoundaries, Integer>(Utils.checkNotNull(numbersOfLatencySampledSpans, "numbersOfLatencySampledSpans"))), Collections.unmodifiableMap((Map<? extends Status.CanonicalCode, ? extends Integer>)new HashMap<Status.CanonicalCode, Integer>(Utils.checkNotNull(numbersOfErrorSampledSpans, "numbersOfErrorSampledSpans"))));
        }
        
        public abstract Map<LatencyBucketBoundaries, Integer> getNumbersOfLatencySampledSpans();
        
        public abstract Map<Status.CanonicalCode, Integer> getNumbersOfErrorSampledSpans();
    }
    
    public enum LatencyBucketBoundaries
    {
        ZERO_MICROSx10(0L, TimeUnit.MICROSECONDS.toNanos(10L)), 
        MICROSx10_MICROSx100(TimeUnit.MICROSECONDS.toNanos(10L), TimeUnit.MICROSECONDS.toNanos(100L)), 
        MICROSx100_MILLIx1(TimeUnit.MICROSECONDS.toNanos(100L), TimeUnit.MILLISECONDS.toNanos(1L)), 
        MILLIx1_MILLIx10(TimeUnit.MILLISECONDS.toNanos(1L), TimeUnit.MILLISECONDS.toNanos(10L)), 
        MILLIx10_MILLIx100(TimeUnit.MILLISECONDS.toNanos(10L), TimeUnit.MILLISECONDS.toNanos(100L)), 
        MILLIx100_SECONDx1(TimeUnit.MILLISECONDS.toNanos(100L), TimeUnit.SECONDS.toNanos(1L)), 
        SECONDx1_SECONDx10(TimeUnit.SECONDS.toNanos(1L), TimeUnit.SECONDS.toNanos(10L)), 
        SECONDx10_SECONDx100(TimeUnit.SECONDS.toNanos(10L), TimeUnit.SECONDS.toNanos(100L)), 
        SECONDx100_MAX(TimeUnit.SECONDS.toNanos(100L), Long.MAX_VALUE);
        
        private final long latencyLowerNs;
        private final long latencyUpperNs;
        
        private LatencyBucketBoundaries(final long latencyLowerNs, final long latencyUpperNs) {
            this.latencyLowerNs = latencyLowerNs;
            this.latencyUpperNs = latencyUpperNs;
        }
        
        public long getLatencyLowerNs() {
            return this.latencyLowerNs;
        }
        
        public long getLatencyUpperNs() {
            return this.latencyUpperNs;
        }
    }
    
    @Immutable
    public abstract static class LatencyFilter
    {
        LatencyFilter() {
        }
        
        public static LatencyFilter create(final String spanName, final long latencyLowerNs, final long latencyUpperNs, final int maxSpansToReturn) {
            Utils.checkArgument(maxSpansToReturn >= 0, (Object)"Negative maxSpansToReturn.");
            Utils.checkArgument(latencyLowerNs >= 0L, (Object)"Negative latencyLowerNs");
            Utils.checkArgument(latencyUpperNs >= 0L, (Object)"Negative latencyUpperNs");
            return new AutoValue_SampledSpanStore_LatencyFilter(spanName, latencyLowerNs, latencyUpperNs, maxSpansToReturn);
        }
        
        public abstract String getSpanName();
        
        public abstract long getLatencyLowerNs();
        
        public abstract long getLatencyUpperNs();
        
        public abstract int getMaxSpansToReturn();
    }
    
    @Immutable
    public abstract static class ErrorFilter
    {
        ErrorFilter() {
        }
        
        public static ErrorFilter create(final String spanName, @Nullable final Status.CanonicalCode canonicalCode, final int maxSpansToReturn) {
            if (canonicalCode != null) {
                Utils.checkArgument(canonicalCode != Status.CanonicalCode.OK, (Object)"Invalid canonical code.");
            }
            Utils.checkArgument(maxSpansToReturn >= 0, (Object)"Negative maxSpansToReturn.");
            return new AutoValue_SampledSpanStore_ErrorFilter(spanName, canonicalCode, maxSpansToReturn);
        }
        
        public abstract String getSpanName();
        
        @Nullable
        public abstract Status.CanonicalCode getCanonicalCode();
        
        public abstract int getMaxSpansToReturn();
    }
    
    @ThreadSafe
    private static final class NoopSampledSpanStore extends SampledSpanStore
    {
        private static final PerSpanNameSummary EMPTY_PER_SPAN_NAME_SUMMARY;
        @GuardedBy("registeredSpanNames")
        private final Set<String> registeredSpanNames;
        
        private NoopSampledSpanStore() {
            this.registeredSpanNames = new HashSet<String>();
        }
        
        @Override
        public Summary getSummary() {
            final Map<String, PerSpanNameSummary> result = new HashMap<String, PerSpanNameSummary>();
            synchronized (this.registeredSpanNames) {
                for (final String registeredSpanName : this.registeredSpanNames) {
                    result.put(registeredSpanName, NoopSampledSpanStore.EMPTY_PER_SPAN_NAME_SUMMARY);
                }
            }
            return Summary.create(result);
        }
        
        @Override
        public Collection<SpanData> getLatencySampledSpans(final LatencyFilter filter) {
            Utils.checkNotNull(filter, "latencyFilter");
            return (Collection<SpanData>)Collections.emptyList();
        }
        
        @Override
        public Collection<SpanData> getErrorSampledSpans(final ErrorFilter filter) {
            Utils.checkNotNull(filter, "errorFilter");
            return (Collection<SpanData>)Collections.emptyList();
        }
        
        @Override
        public void registerSpanNamesForCollection(final Collection<String> spanNames) {
            Utils.checkNotNull(spanNames, "spanNames");
            synchronized (this.registeredSpanNames) {
                this.registeredSpanNames.addAll(spanNames);
            }
        }
        
        @Override
        public void unregisterSpanNamesForCollection(final Collection<String> spanNames) {
            Utils.checkNotNull(spanNames, "spanNames");
            synchronized (this.registeredSpanNames) {
                this.registeredSpanNames.removeAll(spanNames);
            }
        }
        
        @Override
        public Set<String> getRegisteredSpanNamesForCollection() {
            synchronized (this.registeredSpanNames) {
                return Collections.unmodifiableSet((Set<? extends String>)new HashSet<String>(this.registeredSpanNames));
            }
        }
        
        static {
            EMPTY_PER_SPAN_NAME_SUMMARY = PerSpanNameSummary.create(Collections.emptyMap(), Collections.emptyMap());
        }
    }
}
