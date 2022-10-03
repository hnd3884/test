package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class ActiveOperationsMonitorEntry extends MonitorEntry
{
    static final String ACTIVE_OPERATIONS_MONITOR_OC = "ds-active-operations-monitor-entry";
    private static final String ATTR_NUM_OPS_IN_PROGRESS = "num-operations-in-progress";
    private static final String ATTR_NUM_PSEARCHES_IN_PROGRESS = "num-persistent-searches-in-progress";
    private static final String ATTR_OP_IN_PROGRESS = "operation-in-progress";
    private static final String ATTR_PSEARCH_IN_PROGRESS = "persistent-search-in-progress";
    private static final long serialVersionUID = -6583987693176406802L;
    private final List<String> activeOperations;
    private final List<String> activePersistentSearches;
    private final Long numOpsInProgress;
    private final Long numPsearchesInProgress;
    
    public ActiveOperationsMonitorEntry(final Entry entry) {
        super(entry);
        this.activeOperations = this.getStrings("operation-in-progress");
        this.activePersistentSearches = this.getStrings("persistent-search-in-progress");
        this.numOpsInProgress = this.getLong("num-operations-in-progress");
        this.numPsearchesInProgress = this.getLong("num-persistent-searches-in-progress");
    }
    
    public Long getNumOperationsInProgress() {
        return this.numOpsInProgress;
    }
    
    public List<String> getActiveOperations() {
        return this.activeOperations;
    }
    
    public Long getNumPersistentSearchesInProgress() {
        return this.numPsearchesInProgress;
    }
    
    public List<String> getActivePersistentSearches() {
        return this.activePersistentSearches;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_ACTIVE_OPERATIONS_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_ACTIVE_OPERATIONS_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(4));
        if (this.numOpsInProgress != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-operations-in-progress", MonitorMessages.INFO_ACTIVE_OPERATIONS_DISPNAME_NUM_OPS_IN_PROGRESS.get(), MonitorMessages.INFO_ACTIVE_OPERATIONS_DESC_NUM_OPS_IN_PROGRESS.get(), this.numOpsInProgress);
        }
        if (!this.activeOperations.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "operation-in-progress", MonitorMessages.INFO_ACTIVE_OPERATIONS_DISPNAME_OPS_IN_PROGRESS.get(), MonitorMessages.INFO_ACTIVE_OPERATIONS_DESC_OPS_IN_PROGRESS.get(), this.activeOperations);
        }
        if (this.numPsearchesInProgress != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-persistent-searches-in-progress", MonitorMessages.INFO_ACTIVE_OPERATIONS_DISPNAME_NUM_PSEARCHES_IN_PROGRESS.get(), MonitorMessages.INFO_ACTIVE_OPERATIONS_DESC_NUM_PSEARCHES_IN_PROGRESS.get(), this.numPsearchesInProgress);
        }
        if (!this.activePersistentSearches.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "persistent-search-in-progress", MonitorMessages.INFO_ACTIVE_OPERATIONS_DISPNAME_PSEARCHES_IN_PROGRESS.get(), MonitorMessages.INFO_ACTIVE_OPERATIONS_DESC_PSEARCHES_IN_PROGRESS.get(), this.activePersistentSearches);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
