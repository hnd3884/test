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
public final class GroupCacheMonitorEntry extends MonitorEntry
{
    static final String GROUP_CACHE_MONITOR_OC = "ds-group-cache-monitor-entry";
    private static final String ATTR_CURRENT_CACHE_USED_BYTES = "current-cache-used-bytes";
    private static final String ATTR_CURRENT_CACHE_USED_PERCENT = "current-cache-used-as-percentage-of-max-heap";
    private static final String ATTR_CURRENT_CACHE_USED_UPDATE_MILLIS = "current-cache-used-update-ms";
    private static final String ATTR_DYNAMIC_GROUP_ENTRIES = "dynamic-group-entries";
    private static final String ATTR_STATIC_GROUP_ENTRIES = "static-group-entries";
    private static final String ATTR_TOTAL_STATIC_GROUP_MEMBERS = "static-group-members";
    private static final String ATTR_UNIQUE_STATIC_GROUP_MEMBERS = "static-group-unique-members";
    private static final String ATTR_VIRTUAL_STATIC_GROUP_ENTRIES = "virtual-static-group-entries";
    private static final long serialVersionUID = -5665905374595185773L;
    private final Double currentCacheUsedUpdateMillis;
    private final Integer currentCacheUsedPercent;
    private final Long currentCacheUsedBytes;
    private final Long dynamicGroupEntries;
    private final Long staticGroupEntries;
    private final Long staticGroupMembers;
    private final Long staticGroupUniqueMembers;
    private final Long virtualStaticGroupEntries;
    
    public GroupCacheMonitorEntry(final Entry entry) {
        super(entry);
        this.staticGroupEntries = this.getLong("static-group-entries");
        this.staticGroupMembers = this.getLong("static-group-members");
        this.staticGroupUniqueMembers = this.getLong("static-group-unique-members");
        this.dynamicGroupEntries = this.getLong("dynamic-group-entries");
        this.virtualStaticGroupEntries = this.getLong("virtual-static-group-entries");
        this.currentCacheUsedBytes = this.getLong("current-cache-used-bytes");
        this.currentCacheUsedPercent = this.getInteger("current-cache-used-as-percentage-of-max-heap");
        this.currentCacheUsedUpdateMillis = this.getDouble("current-cache-used-update-ms");
    }
    
    public Long getStaticGroupEntries() {
        return this.staticGroupEntries;
    }
    
    public Long getTotalStaticGroupMembers() {
        return this.staticGroupMembers;
    }
    
    public Long getUniqueStaticGroupMembers() {
        return this.staticGroupUniqueMembers;
    }
    
    public Long getDynamicGroupEntries() {
        return this.dynamicGroupEntries;
    }
    
    public Long getVirtualStaticGroupEntries() {
        return this.virtualStaticGroupEntries;
    }
    
    public Long getCurrentCacheUsedBytes() {
        return this.currentCacheUsedBytes;
    }
    
    public Integer getCurrentCacheUsedAsPercentOfMaxHeap() {
        return this.currentCacheUsedPercent;
    }
    
    public Double getCurrentCacheUsedUpdateDurationMillis() {
        return this.currentCacheUsedUpdateMillis;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_GROUP_CACHE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_GROUP_CACHE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(8));
        if (this.staticGroupEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "static-group-entries", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_STATIC_GROUP_ENTRIES.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_STATIC_GROUP_ENTRIES.get(), this.staticGroupEntries);
        }
        if (this.staticGroupMembers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "static-group-members", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_STATIC_GROUP_MEMBERS.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_STATIC_GROUP_MEMBERS.get(), this.staticGroupMembers);
        }
        if (this.staticGroupUniqueMembers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "static-group-unique-members", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_STATIC_GROUP_UNIQUE_MEMBERS.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_STATIC_GROUP_UNIQUE_MEMBERS.get(), this.staticGroupUniqueMembers);
        }
        if (this.dynamicGroupEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "dynamic-group-entries", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_DYNAMIC_GROUP_ENTRIES.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_DYNAMIC_GROUP_ENTRIES.get(), this.dynamicGroupEntries);
        }
        if (this.virtualStaticGroupEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "virtual-static-group-entries", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_VIRTUAL_STATIC_GROUP_ENTRIES.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_VIRTUAL_STATIC_GROUP_ENTRIES.get(), this.virtualStaticGroupEntries);
        }
        if (this.currentCacheUsedBytes != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-cache-used-bytes", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_CACHE_SIZE_BYTES.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_CACHE_SIZE_BYTES.get(), this.currentCacheUsedBytes);
        }
        if (this.currentCacheUsedPercent != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-cache-used-as-percentage-of-max-heap", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_CACHE_SIZE_PERCENT.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_CACHE_SIZE_PERCENT.get(), this.currentCacheUsedPercent);
        }
        if (this.currentCacheUsedUpdateMillis != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-cache-used-update-ms", MonitorMessages.INFO_GROUP_CACHE_DISPNAME_CACHE_SIZE_UPDATE_MILLIS.get(), MonitorMessages.INFO_GROUP_CACHE_DESC_CACHE_SIZE_UPDATE_MILLIS.get(), this.currentCacheUsedUpdateMillis);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
