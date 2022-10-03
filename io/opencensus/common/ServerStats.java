package io.opencensus.common;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ServerStats
{
    ServerStats() {
    }
    
    public abstract long getLbLatencyNs();
    
    public abstract long getServiceLatencyNs();
    
    public abstract byte getTraceOption();
    
    public static ServerStats create(final long lbLatencyNs, final long serviceLatencyNs, final byte traceOption) {
        if (lbLatencyNs < 0L) {
            throw new IllegalArgumentException("'getLbLatencyNs' is less than zero: " + lbLatencyNs);
        }
        if (serviceLatencyNs < 0L) {
            throw new IllegalArgumentException("'getServiceLatencyNs' is less than zero: " + serviceLatencyNs);
        }
        return new AutoValue_ServerStats(lbLatencyNs, serviceLatencyNs, traceOption);
    }
}
