package org.apache.tika.fork;

class TimeoutLimits
{
    private final long pulseMS;
    private final long parseTimeoutMS;
    private final long waitTimeoutMS;
    
    TimeoutLimits(final long pulseMS, final long parseTimeoutMS, final long waitTimeoutMS) {
        this.pulseMS = pulseMS;
        this.parseTimeoutMS = parseTimeoutMS;
        this.waitTimeoutMS = waitTimeoutMS;
    }
    
    public long getPulseMS() {
        return this.pulseMS;
    }
    
    public long getParseTimeoutMS() {
        return this.parseTimeoutMS;
    }
    
    public long getWaitTimeoutMS() {
        return this.waitTimeoutMS;
    }
}
