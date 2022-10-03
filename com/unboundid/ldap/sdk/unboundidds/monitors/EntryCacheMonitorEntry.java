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
public final class EntryCacheMonitorEntry extends MonitorEntry
{
    static final String ENTRY_CACHE_MONITOR_OC = "ds-entry-cache-monitor-entry";
    private static final String ATTR_CURRENT_COUNT = "currentEntryCacheCount";
    private static final String ATTR_CURRENT_SIZE = "currentEntryCacheSize";
    private static final String ATTR_HIT_RATIO = "entryCacheHitRatio";
    private static final String ATTR_HITS = "entryCacheHits";
    private static final String ATTR_MAX_COUNT = "maxEntryCacheCount";
    private static final String ATTR_MAX_SIZE = "maxEntryCacheSize";
    private static final String ATTR_TRIES = "entryCacheTries";
    private static final long serialVersionUID = 2468261007112908567L;
    private final Double hitRatio;
    private final Long cacheHits;
    private final Long cacheMisses;
    private final Long cacheTries;
    private final Long currentCount;
    private final Long currentSize;
    private final Long maxCount;
    private final Long maxSize;
    
    public EntryCacheMonitorEntry(final Entry entry) {
        super(entry);
        this.cacheHits = this.getLong("entryCacheHits");
        this.cacheTries = this.getLong("entryCacheTries");
        this.hitRatio = this.getDouble("entryCacheHitRatio");
        this.currentCount = this.getLong("currentEntryCacheCount");
        this.maxCount = this.getLong("maxEntryCacheCount");
        this.currentSize = this.getLong("currentEntryCacheSize");
        this.maxSize = this.getLong("maxEntryCacheSize");
        if (this.cacheHits == null || this.cacheTries == null) {
            this.cacheMisses = null;
        }
        else {
            this.cacheMisses = this.cacheTries - this.cacheHits;
        }
    }
    
    public Long getCacheTries() {
        return this.cacheTries;
    }
    
    public Long getCacheHits() {
        return this.cacheHits;
    }
    
    public Long getCacheMisses() {
        return this.cacheMisses;
    }
    
    public Double getCacheHitRatio() {
        return this.hitRatio;
    }
    
    public Long getCurrentCount() {
        return this.currentCount;
    }
    
    public Long getMaxCount() {
        return this.maxCount;
    }
    
    public Long getCurrentCacheSize() {
        return this.currentSize;
    }
    
    public Long getMaxCacheSize() {
        return this.maxSize;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_ENTRY_CACHE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_ENTRY_CACHE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(20));
        if (this.cacheTries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheTries", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_TRIES.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_TRIES.get(), this.cacheTries);
        }
        if (this.cacheHits != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheHits", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_HITS.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_HITS.get(), this.cacheHits);
        }
        if (this.cacheMisses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheMisses", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_MISSES.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_MISSES.get(), this.cacheMisses);
        }
        if (this.hitRatio != null) {
            MonitorEntry.addMonitorAttribute(attrs, "entryCacheHitRatio", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_HIT_RATIO.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_HIT_RATIO.get(), this.hitRatio);
        }
        if (this.currentCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentEntryCacheCount", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_CURRENT_COUNT.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_CURRENT_COUNT.get(), this.currentCount);
        }
        if (this.maxCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxEntryCacheCount", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_MAX_COUNT.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_MAX_COUNT.get(), this.maxCount);
        }
        if (this.currentSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "currentEntryCacheSize", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_CURRENT_SIZE.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_CURRENT_SIZE.get(), this.currentSize);
        }
        if (this.maxSize != null) {
            MonitorEntry.addMonitorAttribute(attrs, "maxEntryCacheSize", MonitorMessages.INFO_ENTRY_CACHE_DISPNAME_MAX_SIZE.get(), MonitorMessages.INFO_ENTRY_CACHE_DESC_MAX_SIZE.get(), this.maxSize);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
