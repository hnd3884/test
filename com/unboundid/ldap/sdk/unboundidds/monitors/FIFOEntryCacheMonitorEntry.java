package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FIFOEntryCacheMonitorEntry extends MonitorEntry
{
    static final String FIFO_ENTRY_CACHE_MONITOR_OC = "ds-fifo-entry-cache-monitor-entry";
    private static final String ATTR_CACHE_NAME = "cacheName";
    private static final String ATTR_ENTRY_CACHE_HITS = "entryCacheHits";
    private static final String ATTR_ENTRY_CACHE_TRIES = "entryCacheTries";
    private static final String ATTR_ENTRY_CACHE_HIT_RATIO = "entryCacheHitRatio";
    private static final String ATTR_MAX_ENTRY_CACHE_SIZE = "maxEntryCacheSize";
    private static final String ATTR_CURRENT_ENTRY_CACHE_COUNT = "currentEntryCacheCount";
    private static final String ATTR_MAX_ENTRY_CACHE_COUNT = "maxEntryCacheCount";
    private static final String ATTR_ENTRIES_ADDED_OR_UPDATED = "entriesAddedOrUpdated";
    private static final String ATTR_EVICTIONS_DUE_TO_MAX_MEMORY = "evictionsDueToMaxMemory";
    private static final String ATTR_EVICTIONS_DUE_TO_MAX_ENTRIES = "evictionsDueToMaxEntries";
    private static final String ATTR_ENTRIES_NOT_ADDED_ALREADY_PRESENT = "entriesNotAddedAlreadyPresent";
    private static final String ATTR_ENTRIES_NOT_ADDED_DUE_TO_MAX_MEMORY = "entriesNotAddedDueToMaxMemory";
    private static final String ATTR_ENTRIES_NOT_ADDED_DUE_TO_FILTER = "entriesNotAddedDueToFilter";
    private static final String ATTR_ENTRIES_NOT_ADDED_DUE_TO_ENTRY_SMALLNESS = "entriesNotAddedDueToEntrySmallness";
    private static final String ATTR_LOW_MEMORY_OCCURRENCES = "lowMemoryOccurrences";
    private static final String ATTR_PERCENT_FULL_MAX_ENTRIES = "percentFullMaxEntries";
    private static final String ATTR_JVM_MEMORY_MAX_PERCENT_THRESHOLD = "jvmMemoryMaxPercentThreshold";
    private static final String ATTR_JVM_MEMORY_CURRENT_PERCENT_FULL = "jvmMemoryCurrentPercentFull";
    private static final String ATTR_JVM_MEMORY_BELOW_MAX_MEMORY_PERCENT = "jvmMemoryBelowMaxMemoryPercent";
    private static final String ATTR_IS_FULL = "isFull";
    private static final String ATTR_CAPACITY_DETAILS = "capacityDetails";
    private static final long serialVersionUID = -3340643698412829407L;
    private final Boolean isFull;
    private final Long currentEntryCacheCount;
    private final Long entriesAddedOrUpdated;
    private final Long entriesNotAddedAlreadyPresent;
    private final Long entriesNotAddedDueToEntrySmallness;
    private final Long entriesNotAddedDueToFilter;
    private final Long entriesNotAddedDueToMaxMemory;
    private final Long entryCacheHitRatio;
    private final Long entryCacheHits;
    private final Long entryCacheTries;
    private final Long evictionsDueToMaxEntries;
    private final Long evictionsDueToMaxMemory;
    private final Long jvmMemoryBelowMaxMemoryPercent;
    private final Long jvmMemoryCurrentPercentFull;
    private final Long jvmMemoryMaxPercentThreshold;
    private final Long lowMemoryOccurrences;
    private final Long maxEntryCacheCount;
    private final Long maxEntryCacheSize;
    private final Long percentFullMaxEntries;
    private final String cacheName;
    private final String capacityDetails;
    
    public FIFOEntryCacheMonitorEntry(final Entry entry) {
        super(entry);
        this.isFull = this.getBoolean("isFull");
        this.currentEntryCacheCount = this.getLong("currentEntryCacheCount");
        this.entriesAddedOrUpdated = this.getLong("entriesAddedOrUpdated");
        this.entriesNotAddedAlreadyPresent = this.getLong("entriesNotAddedAlreadyPresent");
        this.entriesNotAddedDueToEntrySmallness = this.getLong("entriesNotAddedDueToEntrySmallness");
        this.entriesNotAddedDueToFilter = this.getLong("entriesNotAddedDueToFilter");
        this.entriesNotAddedDueToMaxMemory = this.getLong("entriesNotAddedDueToMaxMemory");
        this.entryCacheHitRatio = this.getLong("entryCacheHitRatio");
        this.entryCacheHits = this.getLong("entryCacheHits");
        this.entryCacheTries = this.getLong("entryCacheTries");
        this.evictionsDueToMaxEntries = this.getLong("evictionsDueToMaxEntries");
        this.evictionsDueToMaxMemory = this.getLong("evictionsDueToMaxMemory");
        this.jvmMemoryBelowMaxMemoryPercent = this.getLong("jvmMemoryBelowMaxMemoryPercent");
        this.jvmMemoryCurrentPercentFull = this.getLong("jvmMemoryCurrentPercentFull");
        this.jvmMemoryMaxPercentThreshold = this.getLong("jvmMemoryMaxPercentThreshold");
        this.lowMemoryOccurrences = this.getLong("lowMemoryOccurrences");
        this.maxEntryCacheCount = this.getLong("maxEntryCacheCount");
        this.maxEntryCacheSize = this.getLong("maxEntryCacheSize");
        this.percentFullMaxEntries = this.getLong("percentFullMaxEntries");
        this.cacheName = this.getString("cacheName");
        this.capacityDetails = this.getString("capacityDetails");
    }
    
    public String getCacheName() {
        return this.cacheName;
    }
    
    public Long getEntryCacheHits() {
        return this.entryCacheHits;
    }
    
    public Long getEntryCacheTries() {
        return this.entryCacheTries;
    }
    
    public Long getEntryCacheHitRatio() {
        return this.entryCacheHitRatio;
    }
    
    public Long getMaxEntryCacheSizeBytes() {
        return this.maxEntryCacheSize;
    }
    
    public Long getCurrentEntryCacheCount() {
        return this.currentEntryCacheCount;
    }
    
    public Long getMaxEntryCacheCount() {
        return this.maxEntryCacheCount;
    }
    
    public Long getEntriesAddedOrUpdated() {
        return this.entriesAddedOrUpdated;
    }
    
    public Long getEvictionsDueToMaxMemory() {
        return this.evictionsDueToMaxMemory;
    }
    
    public Long getEvictionsDueToMaxEntries() {
        return this.evictionsDueToMaxEntries;
    }
    
    public Long getEntriesNotAddedAlreadyPresent() {
        return this.entriesNotAddedAlreadyPresent;
    }
    
    public Long getEntriesNotAddedDueToMaxMemory() {
        return this.entriesNotAddedDueToMaxMemory;
    }
    
    public Long getEntriesNotAddedDueToFilter() {
        return this.entriesNotAddedDueToFilter;
    }
    
    public Long getEntriesNotAddedDueToEntrySmallness() {
        return this.entriesNotAddedDueToEntrySmallness;
    }
    
    public Long getLowMemoryOccurrences() {
        return this.lowMemoryOccurrences;
    }
    
    public Long getPercentFullMaxEntries() {
        return this.percentFullMaxEntries;
    }
    
    public Long getJVMMemoryMaxPercentThreshold() {
        return this.jvmMemoryMaxPercentThreshold;
    }
    
    public Long getJVMMemoryCurrentPercentFull() {
        return this.jvmMemoryCurrentPercentFull;
    }
    
    public Long getJVMMemoryBelowMaxMemoryPercent() {
        return this.jvmMemoryBelowMaxMemoryPercent;
    }
    
    public Boolean isFull() {
        return this.isFull;
    }
    
    public String getCapacityDetails() {
        return this.capacityDetails;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_FIFO_ENTRY_CACHE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_FIFO_ENTRY_CACHE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(30));
        if (this.cacheName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "cacheName", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_CACHE_NAME.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_CACHE_NAME.get(), this.cacheName);
        }
        if (this.entryCacheHits != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheHits", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_HITS.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_HITS.get(), this.entryCacheHits);
        }
        if (this.entryCacheTries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheTries", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_TRIES.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_TRIES.get(), this.entryCacheTries);
        }
        if (this.entryCacheHitRatio != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheHitRatio", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_HIT_RATIO.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_HIT_RATIO.get(), this.entryCacheHitRatio);
        }
        if (this.maxEntryCacheSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxEntryCacheSize", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_MAX_MEM.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_MAX_MEM.get(), this.maxEntryCacheSize);
        }
        if (this.currentEntryCacheCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentEntryCacheCount", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_CURRENT_COUNT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_CURRENT_COUNT.get(), this.currentEntryCacheCount);
        }
        if (this.maxEntryCacheCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxEntryCacheCount", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_MAX_COUNT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_MAX_COUNT.get(), this.maxEntryCacheCount);
        }
        if (this.entriesAddedOrUpdated != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entriesAddedOrUpdated", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_PUT_COUNT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_PUT_COUNT.get(), this.entriesAddedOrUpdated);
        }
        if (this.evictionsDueToMaxMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "evictionsDueToMaxMemory", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_EVICT_MEM.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_EVICT_MEM.get(), this.evictionsDueToMaxMemory);
        }
        if (this.evictionsDueToMaxEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "evictionsDueToMaxEntries", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_EVICT_COUNT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_EVICT_COUNT.get(), this.evictionsDueToMaxEntries);
        }
        if (this.entriesNotAddedAlreadyPresent != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entriesNotAddedAlreadyPresent", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_NO_PUT_ALREADY_PRESENT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_NO_PUT_ALREADY_PRESENT.get(), this.entriesNotAddedAlreadyPresent);
        }
        if (this.entriesNotAddedDueToMaxMemory != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entriesNotAddedDueToMaxMemory", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_NO_PUT_MEM.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_NO_PUT_MEM.get(), this.entriesNotAddedDueToMaxMemory);
        }
        if (this.entriesNotAddedDueToFilter != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entriesNotAddedDueToFilter", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_NO_PUT_FILTER.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_NO_PUT_FILTER.get(), this.entriesNotAddedDueToFilter);
        }
        if (this.entriesNotAddedDueToEntrySmallness != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entriesNotAddedDueToEntrySmallness", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_NO_PUT_TOO_SMALL.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_NO_PUT_TOO_SMALL.get(), this.entriesNotAddedDueToEntrySmallness);
        }
        if (this.lowMemoryOccurrences != null) {
            MonitorEntry.addMonitorAttribute(attrs, "lowMemoryOccurrences", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_LOW_MEM_COUNT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_LOW_MEM_COUNT.get(), this.lowMemoryOccurrences);
        }
        if (this.percentFullMaxEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "percentFullMaxEntries", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_ENTRY_COUNT_PERCENT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_ENTRY_COUNT_PERCENT.get(), this.percentFullMaxEntries);
        }
        if (this.jvmMemoryMaxPercentThreshold != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmMemoryMaxPercentThreshold", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_JVM_MEM_MAX_PERCENT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_JVM_MEM_MAX_PERCENT.get(), this.jvmMemoryMaxPercentThreshold);
        }
        if (this.jvmMemoryCurrentPercentFull != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmMemoryCurrentPercentFull", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_JVM_MEM_CURRENT_PERCENT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_JVM_MEM_CURRENT_PERCENT.get(), this.jvmMemoryCurrentPercentFull);
        }
        if (this.jvmMemoryBelowMaxMemoryPercent != null) {
            MonitorEntry.addMonitorAttribute(attrs, "jvmMemoryBelowMaxMemoryPercent", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_JVM_MEM_BELOW_MAX_PERCENT.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_JVM_MEM_BELOW_MAX_PERCENT.get(), this.jvmMemoryBelowMaxMemoryPercent);
        }
        if (this.isFull != null) {
            MonitorEntry.addMonitorAttribute(attrs, "isFull", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_IS_FULL.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_IS_FULL.get(), this.isFull);
        }
        if (this.capacityDetails != null) {
            MonitorEntry.addMonitorAttribute(attrs, "capacityDetails", MonitorMessages.INFO_FIFO_ENTRY_CACHE_DISPNAME_CAPACITY_DETAILS.get(), MonitorMessages.INFO_FIFO_ENTRY_CACHE_DESC_CAPACITY_DETAILS.get(), this.capacityDetails);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
