package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeSnapshot;
import java.util.Collection;
import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.AbstractSlidingWindowTimeReservoir;

class AggregatedSlidingWindowTimeReservoir extends AbstractSlidingWindowTimeReservoir<AggregatedValueObject>
{
    private final AggregatingTrimmer notifier;
    
    public AggregatedSlidingWindowTimeReservoir(final long window, final TimeUnit windowUnit, final long startTime, final TimeUnit startTimeUnit, final AggregatingTrimmer notifier) {
        super(window, windowUnit, startTime, startTimeUnit);
        (this.notifier = notifier).register(this);
    }
    
    @Override
    protected UniformTimeSnapshot snapshot(final Collection<AggregatedValueObject> values, final long timeInterval, final TimeUnit timeIntervalUnit, final long time, final TimeUnit timeUnit) {
        final UniformTimeSnapshot notTrimmedMeasurementsSnapshot = this.notifier.getTimeReservoirNotifier().getSnapshot(time, timeUnit);
        AggregatedValueObject[] arrayValues = new AggregatedValueObject[values.size()];
        arrayValues = values.toArray(arrayValues);
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        long count = 0L;
        double meanNumerator = 0.0;
        for (final AggregatedValueObject value : arrayValues) {
            min = Math.min(min, value.getMin());
            max = Math.max(max, value.getMax());
            count += value.getCount();
            meanNumerator += value.getCount() * value.getMean();
        }
        if (notTrimmedMeasurementsSnapshot.size() > 0L) {
            min = Math.min(min, notTrimmedMeasurementsSnapshot.getMin());
            max = Math.max(max, notTrimmedMeasurementsSnapshot.getMax());
            count += notTrimmedMeasurementsSnapshot.size();
            meanNumerator += notTrimmedMeasurementsSnapshot.size() * notTrimmedMeasurementsSnapshot.getMean();
        }
        if (count == 0L) {
            return new UniformTimeSimpleSnapshot(0L, 0L, 0.0, 0L, timeInterval, timeIntervalUnit);
        }
        return new UniformTimeSimpleSnapshot(max, min, meanNumerator / count, count, timeInterval, timeIntervalUnit);
    }
}
