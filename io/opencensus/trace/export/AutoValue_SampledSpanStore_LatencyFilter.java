package io.opencensus.trace.export;

final class AutoValue_SampledSpanStore_LatencyFilter extends SampledSpanStore.LatencyFilter
{
    private final String spanName;
    private final long latencyLowerNs;
    private final long latencyUpperNs;
    private final int maxSpansToReturn;
    
    AutoValue_SampledSpanStore_LatencyFilter(final String spanName, final long latencyLowerNs, final long latencyUpperNs, final int maxSpansToReturn) {
        if (spanName == null) {
            throw new NullPointerException("Null spanName");
        }
        this.spanName = spanName;
        this.latencyLowerNs = latencyLowerNs;
        this.latencyUpperNs = latencyUpperNs;
        this.maxSpansToReturn = maxSpansToReturn;
    }
    
    @Override
    public String getSpanName() {
        return this.spanName;
    }
    
    @Override
    public long getLatencyLowerNs() {
        return this.latencyLowerNs;
    }
    
    @Override
    public long getLatencyUpperNs() {
        return this.latencyUpperNs;
    }
    
    @Override
    public int getMaxSpansToReturn() {
        return this.maxSpansToReturn;
    }
    
    @Override
    public String toString() {
        return "LatencyFilter{spanName=" + this.spanName + ", latencyLowerNs=" + this.latencyLowerNs + ", latencyUpperNs=" + this.latencyUpperNs + ", maxSpansToReturn=" + this.maxSpansToReturn + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SampledSpanStore.LatencyFilter) {
            final SampledSpanStore.LatencyFilter that = (SampledSpanStore.LatencyFilter)o;
            return this.spanName.equals(that.getSpanName()) && this.latencyLowerNs == that.getLatencyLowerNs() && this.latencyUpperNs == that.getLatencyUpperNs() && this.maxSpansToReturn == that.getMaxSpansToReturn();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h ^= this.spanName.hashCode();
        h *= 1000003;
        h = (int)((long)h ^ (this.latencyLowerNs >>> 32 ^ this.latencyLowerNs));
        h *= 1000003;
        h = (int)((long)h ^ (this.latencyUpperNs >>> 32 ^ this.latencyUpperNs));
        h *= 1000003;
        h ^= this.maxSpansToReturn;
        return h;
    }
}
