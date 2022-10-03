package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentNavigableMap;

public abstract class AbstractSlidingWindowTimeReservoir<V> implements TimeReservoir<V>
{
    private final ConcurrentNavigableMap<Long, V> measurements;
    private final long window;
    private final AtomicLong greatestTick;
    private final AtomicLong updateCount;
    private final AtomicLong startTick;
    private final AtomicInteger trimOff;
    private final SlidingWindowTrimmer<V> trimmer;
    private final long interval;
    private final TimeUnit intervalUnit;
    
    public AbstractSlidingWindowTimeReservoir(final long window, final TimeUnit windowUnit, final long startTime, final TimeUnit startTimeUnit) {
        this(window, windowUnit, startTime, startTimeUnit, null);
    }
    
    public AbstractSlidingWindowTimeReservoir(final long window, final TimeUnit windowUnit, final long startTime, final TimeUnit startTimeUnit, final SlidingWindowTrimmer<V> trimmer) {
        this.trimmer = (SlidingWindowTrimmer<V>)((trimmer != null) ? trimmer : DefaultSlidingWindowTrimmerHolder.INSTANCE);
        this.measurements = new ConcurrentSkipListMap<Long, V>();
        this.interval = window;
        this.intervalUnit = windowUnit;
        this.window = windowUnit.toNanos(window) << 8;
        this.startTick = new AtomicLong(this.tick(startTime, startTimeUnit));
        this.greatestTick = new AtomicLong(this.startTick.get());
        this.updateCount = new AtomicLong(0L);
        this.trimOff = new AtomicInteger(0);
        this.trimmer.setTimeReservoir(this);
    }
    
    @Override
    public int size(final long time, final TimeUnit timeUnit) {
        this.conditionallyUpdateGreatestTick(this.tick(time, timeUnit));
        this.trim();
        return this.measurements.size();
    }
    
    @Override
    public void update(final V value, final long time, final TimeUnit timeUnit) {
        if (this.updateCount.incrementAndGet() % 256L == 0L) {
            this.trim();
        }
        long tick = this.tick(time, timeUnit);
        for (int i = 0; i < 256; ++i) {
            if (this.measurements.putIfAbsent(tick, value) == null) {
                this.conditionallyUpdateGreatestTick(tick);
                return;
            }
            ++tick;
        }
    }
    
    @Override
    public long interval(final TimeUnit timeUnit) {
        return timeUnit.convert(this.interval, this.intervalUnit);
    }
    
    private long conditionallyUpdateGreatestTick(final long tick) {
        while (true) {
            final long currentGreatestTick = this.greatestTick.get();
            if (tick <= currentGreatestTick) {
                return currentGreatestTick;
            }
            if (this.greatestTick.compareAndSet(currentGreatestTick, tick)) {
                return tick;
            }
        }
    }
    
    private void conditionallyUpdateStartTick(final Map.Entry<Long, V> firstEntry) {
        final Long firstEntryKey = (firstEntry != null) ? firstEntry.getKey() : null;
        if (firstEntryKey != null && firstEntryKey < this.startTick.get()) {
            long expectedStartTick;
            do {
                expectedStartTick = this.startTick.get();
            } while (!this.startTick.compareAndSet(expectedStartTick, firstEntryKey));
        }
    }
    
    protected abstract UniformTimeSnapshot snapshot(final Collection<V> p0, final long p1, final TimeUnit p2, final long p3, final TimeUnit p4);
    
    @Override
    public UniformTimeSnapshot getSnapshot(final long time, final TimeUnit timeUnit) {
        this.trimOff.incrementAndGet();
        final long baselineTick = this.conditionallyUpdateGreatestTick(this.tick(time, timeUnit));
        try {
            final ConcurrentNavigableMap<Long, V> windowMap = this.measurements.subMap(Long.valueOf(this.roundTick(baselineTick) - this.window), true, Long.valueOf(baselineTick), true);
            this.conditionallyUpdateStartTick(windowMap.firstEntry());
            final long measuredTickInterval = Math.min(baselineTick - this.startTick.get(), this.window);
            return this.snapshot(windowMap.values(), measuredTickInterval >> 8, TimeUnit.NANOSECONDS, time, timeUnit);
        }
        finally {
            this.trimOff.decrementAndGet();
            this.trim(baselineTick);
        }
    }
    
    private long tick(final long time, final TimeUnit timeUnit) {
        return timeUnit.toNanos(time) << 8;
    }
    
    private void trim() {
        this.trim(this.greatestTick.get());
    }
    
    private void trim(final long baselineTick) {
        if (this.trimEnabled()) {
            final long key = this.roundTick(baselineTick) - this.window;
            this.trimmer.trim(this.measurements, key);
        }
    }
    
    private boolean trimEnabled() {
        return this.trimOff.get() == 0;
    }
    
    private long roundTick(final long tick) {
        return tick >> 8 << 8;
    }
    
    private static final class DefaultSlidingWindowTrimmerHolder
    {
        static final SlidingWindowTrimmer<Object> INSTANCE;
        
        static {
            INSTANCE = new SlidingWindowTrimmer<Object>() {
                @Override
                public void trim(final ConcurrentNavigableMap<Long, Object> map, final long key) {
                    map.headMap(Long.valueOf(key)).clear();
                }
                
                @Override
                public void setTimeReservoir(final TimeReservoir<Object> reservoir) {
                }
            };
        }
    }
}
