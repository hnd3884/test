package io.opencensus.common;

final class AutoValue_ServerStats extends ServerStats
{
    private final long lbLatencyNs;
    private final long serviceLatencyNs;
    private final byte traceOption;
    
    AutoValue_ServerStats(final long lbLatencyNs, final long serviceLatencyNs, final byte traceOption) {
        this.lbLatencyNs = lbLatencyNs;
        this.serviceLatencyNs = serviceLatencyNs;
        this.traceOption = traceOption;
    }
    
    @Override
    public long getLbLatencyNs() {
        return this.lbLatencyNs;
    }
    
    @Override
    public long getServiceLatencyNs() {
        return this.serviceLatencyNs;
    }
    
    @Override
    public byte getTraceOption() {
        return this.traceOption;
    }
    
    @Override
    public String toString() {
        return "ServerStats{lbLatencyNs=" + this.lbLatencyNs + ", serviceLatencyNs=" + this.serviceLatencyNs + ", traceOption=" + this.traceOption + "}";
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof ServerStats) {
            final ServerStats that = (ServerStats)o;
            return this.lbLatencyNs == that.getLbLatencyNs() && this.serviceLatencyNs == that.getServiceLatencyNs() && this.traceOption == that.getTraceOption();
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int h = 1;
        h *= 1000003;
        h = (int)((long)h ^ (this.lbLatencyNs >>> 32 ^ this.lbLatencyNs));
        h *= 1000003;
        h = (int)((long)h ^ (this.serviceLatencyNs >>> 32 ^ this.serviceLatencyNs));
        h *= 1000003;
        h ^= this.traceOption;
        return h;
    }
}
