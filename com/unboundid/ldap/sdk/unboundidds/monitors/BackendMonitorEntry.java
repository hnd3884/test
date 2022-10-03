package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Iterator;
import java.util.Collections;
import com.unboundid.util.DebugType;
import com.unboundid.util.Debug;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.Entry;
import java.util.Map;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class BackendMonitorEntry extends MonitorEntry
{
    static final String BACKEND_MONITOR_OC = "ds-backend-monitor-entry";
    private static final String ATTR_BACKEND_ID = "ds-backend-id";
    private static final String ATTR_BASE_DN = "ds-backend-base-dn";
    private static final String ATTR_ENTRIES_PER_BASE_DN = "ds-base-dn-entry-count";
    private static final String ATTR_IS_PRIVATE = "ds-backend-is-private";
    private static final String ATTR_SOFT_DELETE_COUNT = "ds-soft-delete-operations-count";
    private static final String ATTR_TOTAL_ENTRIES = "ds-backend-entry-count";
    private static final String ATTR_UNDELETE_COUNT = "ds-undelete-operations-count";
    private static final String ATTR_WRITABILITY_MODE = "ds-backend-writability-mode";
    private static final long serialVersionUID = -4256944695436807547L;
    private final Boolean isPrivate;
    private final List<String> baseDNs;
    private final Long softDeleteCount;
    private final Long totalEntries;
    private final Long undeleteCount;
    private final Map<String, Long> entriesPerBaseDN;
    private final String backendID;
    private final String writabilityMode;
    
    public BackendMonitorEntry(final Entry entry) {
        super(entry);
        this.backendID = this.getString("ds-backend-id");
        this.baseDNs = this.getStrings("ds-backend-base-dn");
        this.isPrivate = this.getBoolean("ds-backend-is-private");
        this.softDeleteCount = this.getLong("ds-soft-delete-operations-count");
        this.totalEntries = this.getLong("ds-backend-entry-count");
        this.undeleteCount = this.getLong("ds-undelete-operations-count");
        this.writabilityMode = this.getString("ds-backend-writability-mode");
        final List<String> entriesPerBase = this.getStrings("ds-base-dn-entry-count");
        final LinkedHashMap<String, Long> countMap = new LinkedHashMap<String, Long>(StaticUtils.computeMapCapacity(entriesPerBase.size()));
        for (final String s : entriesPerBase) {
            try {
                final int spacePos = s.indexOf(32);
                final Long l = Long.parseLong(s.substring(0, spacePos));
                final String dn = s.substring(spacePos + 1).trim();
                countMap.put(dn, l);
            }
            catch (final Exception e) {
                Debug.debugException(e);
                if (!Debug.debugEnabled(DebugType.MONITOR)) {
                    continue;
                }
                Debug.debugMonitor(entry, "Cannot parse value '" + s + "' for attribute " + "ds-base-dn-entry-count");
            }
        }
        this.entriesPerBaseDN = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)countMap);
    }
    
    public String getBackendID() {
        return this.backendID;
    }
    
    public List<String> getBaseDNs() {
        return this.baseDNs;
    }
    
    public Boolean isPrivate() {
        return this.isPrivate;
    }
    
    public String getWritabilityMode() {
        return this.writabilityMode;
    }
    
    public Long getTotalEntries() {
        return this.totalEntries;
    }
    
    public Map<String, Long> getEntriesPerBaseDN() {
        return this.entriesPerBaseDN;
    }
    
    public Long getSoftDeleteCount() {
        return this.softDeleteCount;
    }
    
    public Long getUndeleteCount() {
        return this.undeleteCount;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_BACKEND_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_BACKEND_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(20));
        if (this.backendID != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-backend-id", MonitorMessages.INFO_BACKEND_DISPNAME_BACKEND_ID.get(), MonitorMessages.INFO_BACKEND_DESC_BACKEND_ID.get(), this.backendID);
        }
        if (!this.baseDNs.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-backend-base-dn", MonitorMessages.INFO_BACKEND_DISPNAME_BASE_DN.get(), MonitorMessages.INFO_BACKEND_DESC_BASE_DN.get(), this.baseDNs);
        }
        if (this.totalEntries != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-backend-entry-count", MonitorMessages.INFO_BACKEND_DISPNAME_TOTAL_ENTRIES.get(), MonitorMessages.INFO_BACKEND_DESC_TOTAL_ENTRIES.get(), this.totalEntries);
        }
        for (final String baseDN : this.entriesPerBaseDN.keySet()) {
            final Long count = this.entriesPerBaseDN.get(baseDN);
            MonitorEntry.addMonitorAttribute(attrs, "ds-base-dn-entry-count-" + baseDN, MonitorMessages.INFO_BACKEND_DISPNAME_ENTRY_COUNT.get(baseDN), MonitorMessages.INFO_BACKEND_DESC_ENTRY_COUNT.get(baseDN), count);
        }
        if (this.softDeleteCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-soft-delete-operations-count", MonitorMessages.INFO_BACKEND_DISPNAME_SOFT_DELETE_COUNT.get(), MonitorMessages.INFO_BACKEND_DESC_SOFT_DELETE_COUNT.get(), this.softDeleteCount);
        }
        if (this.undeleteCount != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-undelete-operations-count", MonitorMessages.INFO_BACKEND_DISPNAME_UNDELETE_COUNT.get(), MonitorMessages.INFO_BACKEND_DESC_UNDELETE_COUNT.get(), this.undeleteCount);
        }
        if (this.writabilityMode != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-backend-writability-mode", MonitorMessages.INFO_BACKEND_DISPNAME_WRITABILITY_MODE.get(), MonitorMessages.INFO_BACKEND_DESC_WRITABILITY_MODE.get(), this.writabilityMode);
        }
        if (this.isPrivate != null) {
            MonitorEntry.addMonitorAttribute(attrs, "ds-backend-is-private", MonitorMessages.INFO_BACKEND_DISPNAME_IS_PRIVATE.get(), MonitorMessages.INFO_BACKEND_DESC_IS_PRIVATE.get(), this.isPrivate);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
