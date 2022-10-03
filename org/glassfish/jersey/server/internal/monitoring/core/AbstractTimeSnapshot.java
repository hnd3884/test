package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTimeSnapshot implements UniformTimeSnapshot
{
    private final long timeInterval;
    private final TimeUnit timeIntervalUnit;
    
    protected AbstractTimeSnapshot(final long timeInterval, final TimeUnit timeIntervalUnit) {
        this.timeInterval = timeInterval;
        this.timeIntervalUnit = timeIntervalUnit;
    }
    
    @Override
    public long getTimeInterval(final TimeUnit timeUnit) {
        return timeUnit.convert(this.timeInterval, this.timeIntervalUnit);
    }
    
    @Override
    public double getRate(final TimeUnit timeUnit) {
        final double rateInNanos = this.size() / (double)this.getTimeInterval(TimeUnit.NANOSECONDS);
        final long multiplier = TimeUnit.NANOSECONDS.convert(1L, timeUnit);
        return rateInNanos * multiplier;
    }
}
