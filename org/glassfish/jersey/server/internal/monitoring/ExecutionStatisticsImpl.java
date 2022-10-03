package org.glassfish.jersey.server.internal.monitoring;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import org.glassfish.jersey.server.internal.monitoring.core.UniformTimeReservoir;
import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;
import org.glassfish.jersey.server.internal.monitoring.core.SlidingWindowTrimmer;
import java.util.concurrent.TimeUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import org.glassfish.jersey.server.monitoring.TimeWindowStatistics;
import java.util.Map;
import org.glassfish.jersey.server.monitoring.ExecutionStatistics;

final class ExecutionStatisticsImpl implements ExecutionStatistics
{
    static final ExecutionStatistics EMPTY;
    private final long lastStartTime;
    private final Map<Long, TimeWindowStatistics> timeWindowStatistics;
    
    @Override
    public Date getLastStartTime() {
        return new Date(this.lastStartTime);
    }
    
    @Override
    public Map<Long, TimeWindowStatistics> getTimeWindowStatistics() {
        return this.timeWindowStatistics;
    }
    
    @Override
    public ExecutionStatistics snapshot() {
        return this;
    }
    
    private ExecutionStatisticsImpl(final long lastStartTime, final Map<Long, TimeWindowStatistics> timeWindowStatistics) {
        this.lastStartTime = lastStartTime;
        this.timeWindowStatistics = Collections.unmodifiableMap((Map<? extends Long, ? extends TimeWindowStatistics>)timeWindowStatistics);
    }
    
    static {
        EMPTY = new Builder().build();
    }
    
    static class Builder
    {
        private volatile long lastStartTime;
        private final Map<Long, TimeWindowStatisticsImpl.Builder> intervalStatistics;
        private final Collection<TimeWindowStatisticsImpl.Builder<Long>> updatableIntervalStatistics;
        
        public Builder() {
            final long nowMillis = System.currentTimeMillis();
            final AggregatingTrimmer trimmer = new AggregatingTrimmer(nowMillis, TimeUnit.MILLISECONDS, 1L, TimeUnit.SECONDS);
            final TimeWindowStatisticsImpl.Builder<Long> oneSecondIntervalWindowBuilder = new TimeWindowStatisticsImpl.Builder<Long>(new SlidingWindowTimeReservoir(1L, TimeUnit.SECONDS, nowMillis, TimeUnit.MILLISECONDS, trimmer));
            final TimeWindowStatisticsImpl.Builder<Long> infiniteIntervalWindowBuilder = new TimeWindowStatisticsImpl.Builder<Long>(new UniformTimeReservoir(nowMillis, TimeUnit.MILLISECONDS));
            this.updatableIntervalStatistics = (Collection<TimeWindowStatisticsImpl.Builder<Long>>)Arrays.asList(infiniteIntervalWindowBuilder, oneSecondIntervalWindowBuilder);
            final HashMap<Long, TimeWindowStatisticsImpl.Builder> tmpIntervalStatistics = new HashMap<Long, TimeWindowStatisticsImpl.Builder>(6);
            tmpIntervalStatistics.put(0L, infiniteIntervalWindowBuilder);
            tmpIntervalStatistics.put(TimeUnit.SECONDS.toMillis(1L), oneSecondIntervalWindowBuilder);
            addAggregatedInterval(tmpIntervalStatistics, nowMillis, 15L, TimeUnit.SECONDS, trimmer);
            addAggregatedInterval(tmpIntervalStatistics, nowMillis, 1L, TimeUnit.MINUTES, trimmer);
            addAggregatedInterval(tmpIntervalStatistics, nowMillis, 15L, TimeUnit.MINUTES, trimmer);
            addAggregatedInterval(tmpIntervalStatistics, nowMillis, 1L, TimeUnit.HOURS, trimmer);
            this.intervalStatistics = (Map<Long, TimeWindowStatisticsImpl.Builder>)Collections.unmodifiableMap((Map<? extends Long, ? extends TimeWindowStatisticsImpl.Builder>)tmpIntervalStatistics);
        }
        
        private static void addAggregatedInterval(final Map<Long, TimeWindowStatisticsImpl.Builder> intervalStatisticsMap, final long nowMillis, final long interval, final TimeUnit timeUnit, final AggregatingTrimmer notifier) {
            final long intervalInMillis = timeUnit.toMillis(interval);
            intervalStatisticsMap.put(intervalInMillis, new TimeWindowStatisticsImpl.Builder(new AggregatedSlidingWindowTimeReservoir(intervalInMillis, TimeUnit.MILLISECONDS, nowMillis, TimeUnit.MILLISECONDS, notifier)));
        }
        
        void addExecution(final long startTime, final long duration) {
            for (final TimeWindowStatisticsImpl.Builder<Long> statBuilder : this.updatableIntervalStatistics) {
                statBuilder.addRequest(startTime, duration);
            }
            this.lastStartTime = startTime;
        }
        
        public ExecutionStatisticsImpl build() {
            final Map<Long, TimeWindowStatistics> newIntervalStatistics = new HashMap<Long, TimeWindowStatistics>();
            for (final Map.Entry<Long, TimeWindowStatisticsImpl.Builder> builderEntry : this.intervalStatistics.entrySet()) {
                newIntervalStatistics.put(builderEntry.getKey(), builderEntry.getValue().build());
            }
            return new ExecutionStatisticsImpl(this.lastStartTime, newIntervalStatistics, null);
        }
    }
}
