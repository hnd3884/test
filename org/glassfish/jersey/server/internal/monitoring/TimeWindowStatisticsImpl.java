package org.glassfish.jersey.server.internal.monitoring;

import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeSnapshot;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;

final class TimeWindowStatisticsImpl implements TimeWindowStatistics
{
    private static final ConcurrentHashMap<Long, TimeWindowStatisticsImpl> EMPTY;
    private final long interval;
    private final long minimumDuration;
    private final long maximumDuration;
    private final long averageDuration;
    private final long totalCount;
    private final double requestsPerSecond;
    
    private TimeWindowStatisticsImpl(final long interval, final double requestsPerSecond, final long minimumDuration, final long maximumDuration, final long averageDuration, final long totalCount) {
        this.interval = interval;
        this.requestsPerSecond = requestsPerSecond;
        this.minimumDuration = minimumDuration;
        this.maximumDuration = maximumDuration;
        this.averageDuration = averageDuration;
        this.totalCount = totalCount;
    }
    
    private TimeWindowStatisticsImpl(final long interval, final UniformTimeSnapshot snapshot) {
        this(interval, snapshot.getRate(TimeUnit.SECONDS), snapshot.getMin(), snapshot.getMax(), (long)snapshot.getMean(), snapshot.size());
    }
    
    @Override
    public long getTimeWindow() {
        return this.interval;
    }
    
    @Override
    public double getRequestsPerSecond() {
        return this.requestsPerSecond;
    }
    
    @Override
    public long getMinimumDuration() {
        return this.minimumDuration;
    }
    
    @Override
    public long getMaximumDuration() {
        return this.maximumDuration;
    }
    
    @Override
    public long getRequestCount() {
        return this.totalCount;
    }
    
    @Override
    public TimeWindowStatistics snapshot() {
        return this;
    }
    
    @Override
    public long getAverageDuration() {
        return this.averageDuration;
    }
    
    static {
        (EMPTY = new ConcurrentHashMap<Long, TimeWindowStatisticsImpl>(6)).putIfAbsent(0L, new TimeWindowStatisticsImpl(0L, 0.0, 0L, 0L, 0L, 0L));
    }
    
    static class Builder<V>
    {
        private final long interval;
        private final TimeReservoir<V> timeReservoir;
        
        Builder(final TimeReservoir<V> timeReservoir) {
            this.interval = timeReservoir.interval(TimeUnit.MILLISECONDS);
            this.timeReservoir = timeReservoir;
        }
        
        void addRequest(final long requestTime, final V duration) {
            this.timeReservoir.update(duration, requestTime, TimeUnit.MILLISECONDS);
        }
        
        TimeWindowStatisticsImpl build() {
            return this.build(System.currentTimeMillis());
        }
        
        TimeWindowStatisticsImpl build(final long currentTime) {
            final UniformTimeSnapshot durationReservoirSnapshot = this.timeReservoir.getSnapshot(currentTime, TimeUnit.MILLISECONDS);
            if (durationReservoirSnapshot.size() == 0L) {
                return this.getOrCreateEmptyStats(this.interval);
            }
            return new TimeWindowStatisticsImpl(this.interval, durationReservoirSnapshot, null);
        }
        
        private TimeWindowStatisticsImpl getOrCreateEmptyStats(final long interval) {
            if (!TimeWindowStatisticsImpl.EMPTY.containsKey(interval)) {
                TimeWindowStatisticsImpl.EMPTY.putIfAbsent(interval, new TimeWindowStatisticsImpl(interval, 0.0, -1L, -1L, -1L, 0L, null));
            }
            return TimeWindowStatisticsImpl.EMPTY.get(interval);
        }
        
        public long getInterval() {
            return this.interval;
        }
    }
}
