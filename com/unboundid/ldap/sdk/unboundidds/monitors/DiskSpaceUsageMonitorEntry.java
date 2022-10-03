package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Iterator;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class DiskSpaceUsageMonitorEntry extends MonitorEntry
{
    static final String DISK_SPACE_USAGE_MONITOR_OC = "ds-disk-space-usage-monitor-entry";
    private static final String ATTR_CURRENT_STATE = "current-disk-space-state";
    private static final String ATTR_PREFIX_CONSUMER_NAME = "disk-space-consumer-name-";
    private static final String ATTR_PREFIX_CONSUMER_PATH = "disk-space-consumer-path-";
    private static final String ATTR_PREFIX_CONSUMER_TOTAL_BYTES = "disk-space-consumer-total-bytes-";
    private static final String ATTR_PREFIX_CONSUMER_USABLE_BYTES = "disk-space-consumer-usable-bytes-";
    private static final String ATTR_PREFIX_CONSUMER_USABLE_PERCENT = "disk-space-consumer-usable-percent-";
    private static final long serialVersionUID = -4717940564786806566L;
    private final List<DiskSpaceInfo> diskSpaceInfo;
    private final String currentState;
    
    public DiskSpaceUsageMonitorEntry(final Entry entry) {
        super(entry);
        this.currentState = this.getString("current-disk-space-state");
        int i = 1;
        final ArrayList<DiskSpaceInfo> list = new ArrayList<DiskSpaceInfo>(5);
        while (true) {
            final String name = this.getString("disk-space-consumer-name-" + i);
            if (name == null) {
                break;
            }
            final String path = this.getString("disk-space-consumer-path-" + i);
            final Long totalBytes = this.getLong("disk-space-consumer-total-bytes-" + i);
            final Long usableBytes = this.getLong("disk-space-consumer-usable-bytes-" + i);
            final Long usablePercent = this.getLong("disk-space-consumer-usable-percent-" + i);
            list.add(new DiskSpaceInfo(name, path, totalBytes, usableBytes, usablePercent));
            ++i;
        }
        this.diskSpaceInfo = Collections.unmodifiableList((List<? extends DiskSpaceInfo>)list);
    }
    
    public String getCurrentState() {
        return this.currentState;
    }
    
    public List<DiskSpaceInfo> getDiskSpaceInfo() {
        return this.diskSpaceInfo;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_DISK_SPACE_USAGE_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_DISK_SPACE_USAGE_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(10));
        if (this.currentState != null) {
            MonitorEntry.addMonitorAttribute(attrs, "current-disk-space-state", MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_CURRENT_STATE.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_CURRENT_STATE.get(), this.currentState);
        }
        if (!this.diskSpaceInfo.isEmpty()) {
            int i = 1;
            for (final DiskSpaceInfo info : this.diskSpaceInfo) {
                if (info.getConsumerName() != null) {
                    MonitorEntry.addMonitorAttribute(attrs, "disk-space-consumer-name-" + i, MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_DISK_SPACE_CONSUMER_PREFIX.get() + i + MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_NAME_SUFFIX.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_NAME.get(), info.getConsumerName());
                }
                if (info.getPath() != null) {
                    MonitorEntry.addMonitorAttribute(attrs, "disk-space-consumer-path-" + i, MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_DISK_SPACE_CONSUMER_PREFIX.get() + i + MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_PATH_SUFFIX.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_PATH.get(), info.getPath());
                }
                if (info.getTotalBytes() != null) {
                    MonitorEntry.addMonitorAttribute(attrs, "disk-space-consumer-total-bytes-" + i, MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_DISK_SPACE_CONSUMER_PREFIX.get() + i + MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_TOTAL_BYTES_SUFFIX.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_TOTAL_BYTES.get(), info.getTotalBytes());
                }
                if (info.getUsableBytes() != null) {
                    MonitorEntry.addMonitorAttribute(attrs, "disk-space-consumer-usable-bytes-" + i, MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_DISK_SPACE_CONSUMER_PREFIX.get() + i + MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_USABLE_BYTES_SUFFIX.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_USABLE_BYTES.get(), info.getUsableBytes());
                }
                if (info.getUsableBytes() != null) {
                    MonitorEntry.addMonitorAttribute(attrs, "disk-space-consumer-usable-percent-" + i, MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_DISK_SPACE_CONSUMER_PREFIX.get() + i + MonitorMessages.INFO_DISK_SPACE_USAGE_DISPNAME_USABLE_PERCENT_SUFFIX.get(), MonitorMessages.INFO_DISK_SPACE_USAGE_DESC_USABLE_PERCENT.get(), info.getUsablePercent());
                }
                ++i;
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
