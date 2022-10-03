package io.opencensus.trace.export;

import java.util.Map;

final class AutoValue_SampledSpanStore_Summary extends SampledSpanStore.Summary
{
    private final Map<String, SampledSpanStore.PerSpanNameSummary> perSpanNameSummary;
    
    AutoValue_SampledSpanStore_Summary(final Map<String, SampledSpanStore.PerSpanNameSummary> perSpanNameSummary) {
        if (perSpanNameSummary == null) {
            throw new NullPointerException("Null perSpanNameSummary");
        }
        this.perSpanNameSummary = perSpanNameSummary;
    }
    
    @Override
    public Map<String, SampledSpanStore.PerSpanNameSummary> getPerSpanNameSummary() {
        return this.perSpanNameSummary;
    }
    
    @Override
    public String toString() {
        return "Summary{perSpanNameSummary=" + this.perSpanNameSummary + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SampledSpanStore.Summary) {
            final SampledSpanStore.Summary that = (SampledSpanStore.Summary)o;
            return this.perSpanNameSummary.equals(that.getPerSpanNameSummary());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.perSpanNameSummary.hashCode();
        return h;
    }
}
