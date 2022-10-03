package io.opencensus.trace.export;

final class AutoValue_RunningSpanStore_PerSpanNameSummary extends RunningSpanStore.PerSpanNameSummary
{
    private final int numRunningSpans;
    
    AutoValue_RunningSpanStore_PerSpanNameSummary(final int numRunningSpans) {
        this.numRunningSpans = numRunningSpans;
    }
    
    @Override
    public int getNumRunningSpans() {
        return this.numRunningSpans;
    }
    
    @Override
    public String toString() {
        return "PerSpanNameSummary{numRunningSpans=" + this.numRunningSpans + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof RunningSpanStore.PerSpanNameSummary) {
            final RunningSpanStore.PerSpanNameSummary that = (RunningSpanStore.PerSpanNameSummary)o;
            return this.numRunningSpans == that.getNumRunningSpans();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.numRunningSpans;
        return h;
    }
}
