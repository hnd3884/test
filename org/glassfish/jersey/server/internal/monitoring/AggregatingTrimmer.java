package org.glassfish.jersey.server.internal.monitoring;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Collection;
import org.glassfish.jersey.internal.guava.TreeMultimap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.TimeUnit;
import org.glassfish.jersey.server.internal.monitoring.core.TimeReservoir;
import java.util.List;
import org.glassfish.jersey.server.internal.monitoring.core.SlidingWindowTrimmer;

class AggregatingTrimmer implements SlidingWindowTrimmer<Long>
{
    private final List<TimeReservoir<AggregatedValueObject>> aggregatedReservoirListeners;
    private TimeReservoir<Long> timeReservoirNotifier;
    private final long startTime;
    private final TimeUnit startUnitTime;
    private final long chunkSize;
    private final AtomicBoolean locked;
    
    public AggregatingTrimmer(final long startTime, final TimeUnit startUnitTime, final long chunkTimeSize, final TimeUnit chunkTimeSizeUnit) {
        this.aggregatedReservoirListeners = new CopyOnWriteArrayList<TimeReservoir<AggregatedValueObject>>();
        this.locked = new AtomicBoolean(false);
        this.startTime = startTime;
        this.startUnitTime = startUnitTime;
        this.chunkSize = TimeUnit.NANOSECONDS.convert(chunkTimeSize, chunkTimeSizeUnit) << 8;
    }
    
    @Override
    public void trim(final ConcurrentNavigableMap<Long, Long> map, final long key) {
        if (!this.locked.compareAndSet(false, true)) {
            return;
        }
        final TreeMultimap<Long, Long> trimMultiMap = (TreeMultimap<Long, Long>)TreeMultimap.create();
        final NavigableMap<Long, Collection<Long>> trimMap = trimMultiMap.asMap();
        try {
            final ConcurrentNavigableMap<Long, Long> headMap = map.headMap(Long.valueOf(key));
            while (!headMap.isEmpty()) {
                final Map.Entry<Long, Long> entry = headMap.pollFirstEntry();
                trimMultiMap.put((Object)entry.getKey(), (Object)entry.getValue());
            }
        }
        finally {
            this.locked.set(false);
        }
        for (Map.Entry<Long, Collection<Long>> firstEntry = trimMap.firstEntry(); firstEntry != null; firstEntry = trimMap.firstEntry()) {
            final long chunkLowerBound = this.lowerBound(firstEntry.getKey());
            final long chunkUpperBound = this.upperBound(chunkLowerBound, key);
            final SortedMap<Long, Collection<Long>> chunkMap = trimMap.headMap(chunkUpperBound);
            final AggregatedValueObject aggregatedValueObject = AggregatedValueObject.createFromMultiValues(chunkMap.values());
            for (final TimeReservoir<AggregatedValueObject> aggregatedReservoir : this.aggregatedReservoirListeners) {
                aggregatedReservoir.update(aggregatedValueObject, chunkLowerBound >> 8, TimeUnit.NANOSECONDS);
            }
            chunkMap.clear();
        }
    }
    
    private long upperBound(final long chunkLowerBound, final long key) {
        final long chunkUpperBoundCandidate = chunkLowerBound + this.chunkSize;
        return (chunkUpperBoundCandidate < key) ? chunkUpperBoundCandidate : key;
    }
    
    private long lowerBound(final Long key) {
        return lowerBound(key, TimeUnit.NANOSECONDS.convert(this.startTime, this.startUnitTime), this.chunkSize, 8);
    }
    
    static long lowerBound(final long key, final long startTime, final long chunkSize, final int power) {
        final long offset = startTime % chunkSize << power;
        if (key - offset >= 0L) {
            return (key - offset) / chunkSize * chunkSize + offset;
        }
        return (key - offset - chunkSize + 1L) / chunkSize * chunkSize + offset;
    }
    
    public void register(final TimeReservoir<AggregatedValueObject> timeReservoirListener) {
        this.aggregatedReservoirListeners.add(timeReservoirListener);
    }
    
    @Override
    public void setTimeReservoir(final TimeReservoir<Long> timeReservoirNotifier) {
        this.timeReservoirNotifier = timeReservoirNotifier;
    }
    
    public TimeReservoir<Long> getTimeReservoirNotifier() {
        return this.timeReservoirNotifier;
    }
}
