package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class HostSystemRecentCPUAndMemoryMonitorEntry extends MonitorEntry
{
    static final String HOST_SYSTEM_RECENT_CPU_AND_MEMORY_MONITOR_OC = "ds-host-system-cpu-memory-monitor-entry";
    private static final String ATTR_RECENT_CPU_IDLE = "recent-cpu-idle";
    private static final String ATTR_RECENT_CPU_IOWAIT = "recent-cpu-iowait";
    private static final String ATTR_RECENT_CPU_SYSTEM = "recent-cpu-system";
    private static final String ATTR_RECENT_TOTAL_CPU_BUSY = "recent-cpu-used";
    private static final String ATTR_RECENT_CPU_USER = "recent-cpu-user";
    private static final String ATTR_RECENT_MEMORY_FREE_GB = "recent-memory-free-gb";
    private static final String ATTR_RECENT_MEMORY_FREE_PCT = "recent-memory-pct-free";
    private static final String ATTR_TIMESTAMP = "timestamp";
    private static final String ATTR_TOTAL_MEMORY_GB = "total-memory-gb";
    private static final long serialVersionUID = -4408434740529394905L;
    private final Date timestamp;
    private final Double recentCPUIdle;
    private final Double recentCPUIOWait;
    private final Double recentCPUSystem;
    private final Double recentCPUTotalBusy;
    private final Double recentCPUUser;
    private final Double recentMemoryFreeGB;
    private final Double recentMemoryPercentFree;
    private final Double totalMemoryGB;
    
    public HostSystemRecentCPUAndMemoryMonitorEntry(final Entry entry) {
        super(entry);
        this.timestamp = this.getDate("timestamp");
        this.recentCPUIdle = this.getDouble("recent-cpu-idle");
        this.recentCPUIOWait = this.getDouble("recent-cpu-iowait");
        this.recentCPUSystem = this.getDouble("recent-cpu-system");
        this.recentCPUUser = this.getDouble("recent-cpu-user");
        this.recentCPUTotalBusy = this.getDouble("recent-cpu-used");
        this.recentMemoryFreeGB = this.getDouble("recent-memory-free-gb");
        this.recentMemoryPercentFree = this.getDouble("recent-memory-pct-free");
        this.totalMemoryGB = this.getDouble("total-memory-gb");
    }
    
    public Date getUpdateTime() {
        return this.timestamp;
    }
    
    public Double getRecentCPUTotalBusyPercent() {
        return this.recentCPUTotalBusy;
    }
    
    public Double getRecentCPUUserPercent() {
        return this.recentCPUUser;
    }
    
    public Double getRecentCPUSystemPercent() {
        return this.recentCPUSystem;
    }
    
    public Double getRecentCPUIOWaitPercent() {
        return this.recentCPUIOWait;
    }
    
    public Double getRecentCPUIdlePercent() {
        return this.recentCPUIdle;
    }
    
    public Double getTotalSystemMemoryGB() {
        return this.totalMemoryGB;
    }
    
    public Double getRecentSystemMemoryFreeGB() {
        return this.recentMemoryFreeGB;
    }
    
    public Double getRecentSystemMemoryPercentFree() {
        return this.recentMemoryPercentFree;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_CPU_MEM_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_CPU_MEM_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(9));
        if (this.timestamp != null) {
            MonitorEntry.addMonitorAttribute(attrs, "timestamp", MonitorMessages.INFO_CPU_MEM_DISPNAME_TIMESTAMP.get(), MonitorMessages.INFO_CPU_MEM_DESC_TIMESTAMP.get(), this.timestamp);
        }
        if (this.recentCPUTotalBusy != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-cpu-used", MonitorMessages.INFO_CPU_MEM_DISPNAME_RECENT_CPU_TOTAL_BUSY.get(), MonitorMessages.INFO_CPU_MEM_DESC_RECENT_CPU_TOTAL_BUSY.get(), this.recentCPUTotalBusy);
        }
        if (this.recentCPUUser != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-cpu-user", MonitorMessages.INFO_CPU_MEM_DISPNAME_RECENT_CPU_USER.get(), MonitorMessages.INFO_CPU_MEM_DESC_RECENT_CPU_USER.get(), this.recentCPUUser);
        }
        if (this.recentCPUSystem != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-cpu-system", MonitorMessages.INFO_CPU_MEM_DISPNAME_RECENT_CPU_SYSTEM.get(), MonitorMessages.INFO_CPU_MEM_DESC_RECENT_CPU_SYSTEM.get(), this.recentCPUSystem);
        }
        if (this.recentCPUIOWait != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-cpu-iowait", MonitorMessages.INFO_CPU_MEM_DISPNAME_RECENT_CPU_IOWAIT.get(), MonitorMessages.INFO_CPU_MEM_DESC_RECENT_CPU_IOWAIT.get(), this.recentCPUIOWait);
        }
        if (this.recentCPUIdle != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-cpu-idle", MonitorMessages.INFO_CPU_MEM_DISPNAME_RECENT_CPU_IDLE.get(), MonitorMessages.INFO_CPU_MEM_DESC_RECENT_CPU_IDLE.get(), this.recentCPUIdle);
        }
        if (this.totalMemoryGB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "total-memory-gb", MonitorMessages.INFO_CPU_MEM_DISPNAME_TOTAL_MEM.get(), MonitorMessages.INFO_CPU_MEM_DESC_TOTAL_MEM.get(), this.totalMemoryGB);
        }
        if (this.recentMemoryFreeGB != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-memory-free-gb", MonitorMessages.INFO_CPU_MEM_DISPNAME_FREE_MEM_GB.get(), MonitorMessages.INFO_CPU_MEM_DESC_FREE_MEM_GB.get(), this.recentMemoryFreeGB);
        }
        if (this.recentMemoryPercentFree != null) {
            MonitorEntry.addMonitorAttribute(attrs, "recent-memory-pct-free", MonitorMessages.INFO_CPU_MEM_DISPNAME_FREE_MEM_PCT.get(), MonitorMessages.INFO_CPU_MEM_DESC_FREE_MEM_PCT.get(), this.recentMemoryPercentFree);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
