package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Attribute;
import java.util.TreeSet;
import java.util.TreeMap;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MemoryUsageMonitorEntry extends MonitorEntry
{
    static final String MEMORY_USAGE_MONITOR_OC = "ds-memory-usage-monitor-entry";
    private static final String ATTR_LONGEST_PAUSE_TIME = "max-detected-pause-time-millis";
    private static final String ATTR_NON_HEAP_USED = "non-heap-memory-bytes-used";
    private static final String ATTR_TOTAL_CONSUMER_MEMORY = "total-bytes-used-by-memory-consumers";
    private static final String ATTR_TOTAL_CONSUMER_MEMORY_AS_PCT_OF_COMMITTED = "memory-consumers-total-as-percent-of-committed-tenured-memory";
    private static final String ATTR_TOTAL_CONSUMER_MEMORY_AS_PCT_OF_MAX = "memory-consumers-total-as-percent-of-maximum-tenured-memory";
    private static final String ATTR_PREFIX_DETECTED_PAUSE = "detected-pauses-over-";
    private static final String ATTR_SUFFIX_TOTAL_COLLECTION_COUNT = "-total-collection-count";
    private static final String ATTR_SUFFIX_TOTAL_COLLECTION_DURATION = "-total-collection-duration";
    private static final String ATTR_SUFFIX_AVERAGE_COLLECTION_DURATION = "-average-collection-duration";
    private static final String ATTR_SUFFIX_RECENT_COLLECTION_DURATION = "-recent-collection-duration";
    private static final String ATTR_SUFFIX_CURRENT_BYTES_USED = "-current-bytes-used";
    private static final String ATTR_SUFFIX_BYTES_USED_AFTER_LAST_COLLECTION = "-bytes-used-after-last-collection";
    private static final String PROPERTY_DETECTED_PAUSE_COUNTS = "detected-pause-counts";
    private static final String ATTR_MAX_RESERVABLE_MEMORY_MB = "maxReservableMemoryMB";
    private static final String ATTR_CURRENT_RESERVED_MEMORY_MB = "currentReservedMemoryMB";
    private static final String ATTR_USED_MEMORY_MB = "usedReservedMemoryMB";
    private static final String ATTR_FREE_MEMORY_MB = "freeReservedMemoryMB";
    private static final String ATTR_RESERVED_MEMORY_PERCENT_FULL = "reservedMemoryPercentFull";
    private static final long serialVersionUID = 1924052253885937441L;
    private final List<String> garbageCollectors;
    private final List<String> memoryPools;
    private final Long currentReservedMemoryMB;
    private final Long freeReservedMemoryMB;
    private final Long maxDetectedPauseTime;
    private final Long maxReservableMemoryMB;
    private final Long nonHeapMemoryUsed;
    private final Long percentOfCommittedTenuredMemory;
    private final Long percentOfMaxTenuredMemory;
    private final Long reservedMemoryPercentFull;
    private final Long totalBytesHeldByConsumers;
    private final Long usedReservedMemoryMB;
    private final Map<Long, Long> detectedPauses;
    private final Map<String, Long> bytesUsedAfterLastCollectionPerMP;
    private final Map<String, Long> currentBytesUsedPerMP;
    private final Map<String, Long> averageCollectionDurationPerGC;
    private final Map<String, Long> recentCollectionDurationPerGC;
    private final Map<String, Long> totalCollectionCountPerGC;
    private final Map<String, Long> totalCollectionDurationPerGC;
    
    public MemoryUsageMonitorEntry(final Entry entry) {
        super(entry);
        this.maxDetectedPauseTime = this.getLong("max-detected-pause-time-millis");
        this.nonHeapMemoryUsed = this.getLong("non-heap-memory-bytes-used");
        this.totalBytesHeldByConsumers = this.getLong("total-bytes-used-by-memory-consumers");
        this.percentOfCommittedTenuredMemory = this.getLong("memory-consumers-total-as-percent-of-committed-tenured-memory");
        this.percentOfMaxTenuredMemory = this.getLong("memory-consumers-total-as-percent-of-maximum-tenured-memory");
        this.maxReservableMemoryMB = this.getLong("maxReservableMemoryMB");
        this.currentReservedMemoryMB = this.getLong("currentReservedMemoryMB");
        this.usedReservedMemoryMB = this.getLong("usedReservedMemoryMB");
        this.freeReservedMemoryMB = this.getLong("freeReservedMemoryMB");
        this.reservedMemoryPercentFull = this.getLong("reservedMemoryPercentFull");
        final TreeMap<Long, Long> pauses = new TreeMap<Long, Long>();
        final TreeSet<String> mpNames = new TreeSet<String>();
        final TreeSet<String> gcNames = new TreeSet<String>();
        final TreeMap<String, Long> averageDurations = new TreeMap<String, Long>();
        final TreeMap<String, Long> currentBytesUsed = new TreeMap<String, Long>();
        final TreeMap<String, Long> lastBytesUsed = new TreeMap<String, Long>();
        final TreeMap<String, Long> recentDurations = new TreeMap<String, Long>();
        final TreeMap<String, Long> totalCounts = new TreeMap<String, Long>();
        final TreeMap<String, Long> totalDurations = new TreeMap<String, Long>();
        for (final Attribute a : entry.getAttributes()) {
            final String name = a.getName();
            final String lowerName = StaticUtils.toLowerCase(name);
            if (lowerName.startsWith("detected-pauses-over-")) {
                final Long l = this.getLong(name);
                final String timeStr = lowerName.substring("detected-pauses-over-".length());
                if (timeStr.endsWith("ms")) {
                    try {
                        final long millis = Long.parseLong(timeStr.substring(0, timeStr.length() - 2));
                        pauses.put(millis, l);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
                else if (timeStr.endsWith("s")) {
                    try {
                        final long millis = 1000L * Long.parseLong(timeStr.substring(0, timeStr.length() - 1));
                        pauses.put(millis, l);
                    }
                    catch (final Exception e) {
                        Debug.debugException(e);
                    }
                }
            }
            int pos = lowerName.indexOf("-average-collection-duration");
            if (pos > 0) {
                final String gcName = name.substring(0, pos);
                gcNames.add(gcName);
                final Long i = this.getLong(name);
                if (i == null) {
                    continue;
                }
                averageDurations.put(StaticUtils.toLowerCase(gcName), i);
            }
            else {
                pos = lowerName.indexOf("-bytes-used-after-last-collection");
                if (pos > 0) {
                    final String mpName = name.substring(0, pos);
                    mpNames.add(mpName);
                    final Long i = this.getLong(name);
                    if (i == null) {
                        continue;
                    }
                    lastBytesUsed.put(StaticUtils.toLowerCase(mpName), i);
                }
                else {
                    pos = lowerName.indexOf("-current-bytes-used");
                    if (pos > 0) {
                        final String mpName = name.substring(0, pos);
                        mpNames.add(mpName);
                        final Long i = this.getLong(name);
                        if (i == null) {
                            continue;
                        }
                        currentBytesUsed.put(StaticUtils.toLowerCase(mpName), i);
                    }
                    else {
                        pos = lowerName.indexOf("-recent-collection-duration");
                        if (pos > 0) {
                            final String gcName = name.substring(0, pos);
                            gcNames.add(gcName);
                            final Long i = this.getLong(name);
                            if (i == null) {
                                continue;
                            }
                            recentDurations.put(StaticUtils.toLowerCase(gcName), i);
                        }
                        else {
                            pos = lowerName.indexOf("-total-collection-count");
                            if (pos > 0 && !lowerName.startsWith("mem-pool-")) {
                                final String gcName = name.substring(0, pos);
                                gcNames.add(gcName);
                                final Long i = this.getLong(name);
                                if (i == null) {
                                    continue;
                                }
                                totalCounts.put(StaticUtils.toLowerCase(gcName), i);
                            }
                            else {
                                pos = lowerName.indexOf("-total-collection-duration");
                                if (pos <= 0) {
                                    continue;
                                }
                                final String gcName = name.substring(0, pos);
                                gcNames.add(gcName);
                                final Long i = this.getLong(name);
                                if (i == null) {
                                    continue;
                                }
                                totalDurations.put(StaticUtils.toLowerCase(gcName), i);
                            }
                        }
                    }
                }
            }
        }
        this.garbageCollectors = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(gcNames));
        this.memoryPools = Collections.unmodifiableList((List<? extends String>)new ArrayList<String>(mpNames));
        this.totalCollectionCountPerGC = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)totalCounts);
        this.totalCollectionDurationPerGC = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)totalDurations);
        this.averageCollectionDurationPerGC = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)averageDurations);
        this.recentCollectionDurationPerGC = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)recentDurations);
        this.bytesUsedAfterLastCollectionPerMP = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)lastBytesUsed);
        this.currentBytesUsedPerMP = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)currentBytesUsed);
        this.detectedPauses = Collections.unmodifiableMap((Map<? extends Long, ? extends Long>)pauses);
    }
    
    public Long getMaxReservableMemoryMB() {
        return this.maxReservableMemoryMB;
    }
    
    public Long getCurrentReservedMemoryMB() {
        return this.currentReservedMemoryMB;
    }
    
    public Long getUsedReservedMemoryMB() {
        return this.usedReservedMemoryMB;
    }
    
    public Long getFreeReservedMemoryMB() {
        return this.freeReservedMemoryMB;
    }
    
    public Long getReservedMemoryPercentFull() {
        return this.reservedMemoryPercentFull;
    }
    
    public List<String> getGarbageCollectorNames() {
        return this.garbageCollectors;
    }
    
    public List<String> getMemoryPoolNames() {
        return this.memoryPools;
    }
    
    public Map<String, Long> getTotalCollectionCounts() {
        return this.totalCollectionCountPerGC;
    }
    
    public Long getTotalCollectionCount(final String collectorName) {
        return this.totalCollectionCountPerGC.get(StaticUtils.toLowerCase(collectorName));
    }
    
    public Map<String, Long> getTotalCollectionDurations() {
        return this.totalCollectionDurationPerGC;
    }
    
    public Long getTotalCollectionDuration(final String collectorName) {
        return this.totalCollectionDurationPerGC.get(StaticUtils.toLowerCase(collectorName));
    }
    
    public Map<String, Long> getAverageCollectionDurations() {
        return this.averageCollectionDurationPerGC;
    }
    
    public Long getAverageCollectionDuration(final String collectorName) {
        return this.averageCollectionDurationPerGC.get(StaticUtils.toLowerCase(collectorName));
    }
    
    public Map<String, Long> getRecentCollectionDurations() {
        return this.recentCollectionDurationPerGC;
    }
    
    public Long getRecentCollectionDuration(final String collectorName) {
        return this.recentCollectionDurationPerGC.get(StaticUtils.toLowerCase(collectorName));
    }
    
    public Map<String, Long> getCurrentBytesUsed() {
        return this.currentBytesUsedPerMP;
    }
    
    public Long getCurrentBytesUsed(final String poolName) {
        return this.currentBytesUsedPerMP.get(StaticUtils.toLowerCase(poolName));
    }
    
    public Map<String, Long> getBytesUsedAfterLastCollection() {
        return this.bytesUsedAfterLastCollectionPerMP;
    }
    
    public Long getBytesUsedAfterLastCollection(final String poolName) {
        return this.bytesUsedAfterLastCollectionPerMP.get(StaticUtils.toLowerCase(poolName));
    }
    
    public Long getNonHeapMemoryBytesUsed() {
        return this.nonHeapMemoryUsed;
    }
    
    public Long getTotalBytesUsedByMemoryConsumers() {
        return this.totalBytesHeldByConsumers;
    }
    
    public Long getPercentageOfMaximumTenuredMemoryUsedByMemoryConsumers() {
        return this.percentOfMaxTenuredMemory;
    }
    
    public Long getPercentageOfCommittedTenuredMemoryUsedByMemoryConsumers() {
        return this.percentOfCommittedTenuredMemory;
    }
    
    public Map<Long, Long> getDetectedPauseCounts() {
        return this.detectedPauses;
    }
    
    public Long getMaxDetectedPauseTimeMillis() {
        return this.maxDetectedPauseTime;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_MEMORY_USAGE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_MEMORY_USAGE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(50));
        if (this.maxReservableMemoryMB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxReservableMemoryMB", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_MAX_MEM.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_MAX_MEM.get(), this.maxReservableMemoryMB);
        }
        if (this.currentReservedMemoryMB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentReservedMemoryMB", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_CURRENT_MEM.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_CURRENT_MEM.get(), this.currentReservedMemoryMB);
        }
        if (this.usedReservedMemoryMB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "usedReservedMemoryMB", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_USED_MEM.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_USED_MEM.get(), this.usedReservedMemoryMB);
        }
        if (this.freeReservedMemoryMB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "freeReservedMemoryMB", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_FREE_MEM.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_FREE_MEM.get(), this.freeReservedMemoryMB);
        }
        if (this.reservedMemoryPercentFull != null) {
            MonitorEntry.addMonitorAttribute(attrs, "reservedMemoryPercentFull", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_RESERVED_PCT.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_RESERVED_PCT.get(), this.reservedMemoryPercentFull);
        }
        if (!this.garbageCollectors.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "gcNames", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_GC_NAMES.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_GC_NAMES.get(), this.garbageCollectors);
        }
        if (!this.totalCollectionCountPerGC.isEmpty()) {
            for (final String name : this.totalCollectionCountPerGC.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "totalCollectionCount-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_TOTAL_COLLECTION_COUNT.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_TOTAL_COLLECTION_COUNT.get(name), this.totalCollectionCountPerGC.get(name));
            }
        }
        if (!this.totalCollectionDurationPerGC.isEmpty()) {
            for (final String name : this.totalCollectionDurationPerGC.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "totalCollectionDuration-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_TOTAL_COLLECTION_DURATION.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_TOTAL_COLLECTION_DURATION.get(name), this.totalCollectionDurationPerGC.get(name));
            }
        }
        if (!this.averageCollectionDurationPerGC.isEmpty()) {
            for (final String name : this.averageCollectionDurationPerGC.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "averageCollectionDuration-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_AVERAGE_COLLECTION_DURATION.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_AVERAGE_COLLECTION_DURATION.get(name), this.averageCollectionDurationPerGC.get(name));
            }
        }
        if (!this.recentCollectionDurationPerGC.isEmpty()) {
            for (final String name : this.recentCollectionDurationPerGC.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "recentCollectionDuration-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_RECENT_COLLECTION_DURATION.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_RECENT_COLLECTION_DURATION.get(name), this.recentCollectionDurationPerGC.get(name));
            }
        }
        if (!this.memoryPools.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "memoryPools", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_MEMORY_POOLS.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_MEMORY_POOLS.get(), this.memoryPools);
        }
        if (!this.currentBytesUsedPerMP.isEmpty()) {
            for (final String name : this.currentBytesUsedPerMP.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "currentBytesUsed-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_CURRENT_BYTES_USED.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_CURRENT_BYTES_USED.get(name), this.currentBytesUsedPerMP.get(name));
            }
        }
        if (!this.bytesUsedAfterLastCollectionPerMP.isEmpty()) {
            for (final String name : this.bytesUsedAfterLastCollectionPerMP.keySet()) {
                MonitorEntry.addMonitorAttribute(attrs, "bytesUsedAfterLastCollection-" + name, MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_BYTES_USED_AFTER_COLLECTION.get(name), MonitorMessages.INFO_MEMORY_USAGE_DESC_BYTES_USED_AFTER_COLLECTION.get(name), this.bytesUsedAfterLastCollectionPerMP.get(name));
            }
        }
        if (this.nonHeapMemoryUsed != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-heap-memory-bytes-used", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_NON_HEAP_MEMORY.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_NON_HEAP_MEMORY.get(), this.nonHeapMemoryUsed);
        }
        if (this.totalBytesHeldByConsumers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-bytes-used-by-memory-consumers", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_TOTAL_CONSUMER_MEMORY.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_TOTAL_CONSUMER_MEMORY.get(), this.totalBytesHeldByConsumers);
        }
        if (this.percentOfMaxTenuredMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "memory-consumers-total-as-percent-of-maximum-tenured-memory", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_CONSUMERS_AS_PCT_OF_MAX.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_CONSUMERS_AS_PCT_OF_MAX.get(), this.percentOfMaxTenuredMemory);
        }
        if (this.percentOfCommittedTenuredMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "memory-consumers-total-as-percent-of-committed-tenured-memory", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_CONSUMERS_AS_PCT_OF_COMMITTED.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_CONSUMERS_AS_PCT_OF_COMMITTED.get(), this.percentOfCommittedTenuredMemory);
        }
        if (!this.detectedPauses.isEmpty()) {
            final ArrayList<String> values = new ArrayList<String>(this.detectedPauses.size());
            for (final Map.Entry<Long, Long> e : this.detectedPauses.entrySet()) {
                values.add(e.getKey() + "ms=" + e.getValue());
            }
            MonitorEntry.addMonitorAttribute(attrs, "detected-pause-counts", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_DETECTED_PAUSES.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_DETECTED_PAUSES.get(), values);
        }
        if (this.maxDetectedPauseTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "max-detected-pause-time-millis", MonitorMessages.INFO_MEMORY_USAGE_DISPNAME_MAX_PAUSE_TIME.get(), MonitorMessages.INFO_MEMORY_USAGE_DESC_MAX_PAUSE_TIME.get(), this.maxDetectedPauseTime);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
