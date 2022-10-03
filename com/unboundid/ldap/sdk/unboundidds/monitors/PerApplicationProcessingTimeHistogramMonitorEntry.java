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
public final class PerApplicationProcessingTimeHistogramMonitorEntry extends ProcessingTimeHistogramMonitorEntry
{
    private static final long serialVersionUID = 1467986373260986009L;
    static final String PER_APPLICATION_PROCESSING_TIME_HISTOGRAM_MONITOR_OC = "ds-per-application-processing-time-histogram-monitor-entry";
    private static final String ATTR_APPLICATION_NAME = "applicationName";
    private final String applicationName;
    
    public PerApplicationProcessingTimeHistogramMonitorEntry(final Entry entry) {
        super(entry);
        this.applicationName = entry.getAttributeValue("applicationName");
    }
    
    public String getApplicationName() {
        return this.applicationName;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_PER_APP_PROCESSING_TIME_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_PER_APP_PROCESSING_TIME_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final Map<String, MonitorAttribute> superAttrs = super.getMonitorAttributes();
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(superAttrs.size() + 1));
        attrs.putAll((Map<?, ?>)superAttrs);
        if (this.applicationName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "applicationName", MonitorMessages.INFO_PER_APP_PROCESSING_TIME_DISPNAME_APP_NAME.get(), MonitorMessages.INFO_PER_APP_PROCESSING_TIME_DESC_APP_NAME.get(), this.applicationName);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
