package io.opencensus.trace.export;

import io.opencensus.trace.Status;
import java.util.Map;

final class AutoValue_SampledSpanStore_PerSpanNameSummary extends SampledSpanStore.PerSpanNameSummary
{
    private final Map<SampledSpanStore.LatencyBucketBoundaries, Integer> numbersOfLatencySampledSpans;
    private final Map<Status.CanonicalCode, Integer> numbersOfErrorSampledSpans;
    
    AutoValue_SampledSpanStore_PerSpanNameSummary(final Map<SampledSpanStore.LatencyBucketBoundaries, Integer> numbersOfLatencySampledSpans, final Map<Status.CanonicalCode, Integer> numbersOfErrorSampledSpans) {
        if (numbersOfLatencySampledSpans == null) {
            throw new NullPointerException("Null numbersOfLatencySampledSpans");
        }
        this.numbersOfLatencySampledSpans = numbersOfLatencySampledSpans;
        if (numbersOfErrorSampledSpans == null) {
            throw new NullPointerException("Null numbersOfErrorSampledSpans");
        }
        this.numbersOfErrorSampledSpans = numbersOfErrorSampledSpans;
    }
    
    @Override
    public Map<SampledSpanStore.LatencyBucketBoundaries, Integer> getNumbersOfLatencySampledSpans() {
        return this.numbersOfLatencySampledSpans;
    }
    
    @Override
    public Map<Status.CanonicalCode, Integer> getNumbersOfErrorSampledSpans() {
        return this.numbersOfErrorSampledSpans;
    }
    
    @Override
    public String toString() {
        return "PerSpanNameSummary{numbersOfLatencySampledSpans=" + this.numbersOfLatencySampledSpans + ", numbersOfErrorSampledSpans=" + this.numbersOfErrorSampledSpans + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SampledSpanStore.PerSpanNameSummary) {
            final SampledSpanStore.PerSpanNameSummary that = (SampledSpanStore.PerSpanNameSummary)o;
            return this.numbersOfLatencySampledSpans.equals(that.getNumbersOfLatencySampledSpans()) && this.numbersOfErrorSampledSpans.equals(that.getNumbersOfErrorSampledSpans());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.numbersOfLatencySampledSpans.hashCode();
        h *= 1000003;
        h ^= this.numbersOfErrorSampledSpans.hashCode();
        return h;
    }
}
