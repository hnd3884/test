package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeValuesSnapshot;
import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeSnapshot;
import java.util.Collection;
import org.glassfish.jersey.server.internal.monitoring.core.SlidingWindowTrimmer;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.AbstractSlidingWindowTimeReservoir;

class SlidingWindowTimeReservoir extends AbstractSlidingWindowTimeReservoir<Long>
{
    public SlidingWindowTimeReservoir(final long window, final TimeUnit windowUnit, final long startTime, final TimeUnit startTimeUnit, final SlidingWindowTrimmer<Long> trimmer) {
        super(window, windowUnit, startTime, startTimeUnit, trimmer);
    }
    
    public SlidingWindowTimeReservoir(final long window, final TimeUnit windowUnit, final long startTime, final TimeUnit startTimeUnit) {
        this(window, windowUnit, startTime, startTimeUnit, null);
    }
    
    @Override
    protected UniformTimeSnapshot snapshot(final Collection<Long> values, final long timeInterval, final TimeUnit timeIntervalUnit, final long time, final TimeUnit timeUnit) {
        return new UniformTimeValuesSnapshot(values, timeInterval, timeIntervalUnit);
    }
}
