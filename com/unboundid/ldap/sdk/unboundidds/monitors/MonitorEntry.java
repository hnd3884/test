package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Arrays;
import java.util.List;
import java.util.Date;
import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.ldap.sdk.Attribute;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.util.Validator;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.ReadOnlyEntry;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotExtensible;
import java.io.Serializable;

@NotExtensible
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public class MonitorEntry implements Serializable
{
    static final String GENERIC_MONITOR_OC = "ds-monitor-entry";
    static final String MONITOR_BASE_DN = "cn=monitor";
    private static final String ATTR_MONITOR_NAME = "cn";
    private static final long serialVersionUID = -8889119758772055683L;
    private final ReadOnlyEntry entry;
    private final String monitorClass;
    private final String monitorName;
    
    public MonitorEntry(final Entry entry) {
        Validator.ensureNotNull(entry);
        this.entry = new ReadOnlyEntry(entry);
        this.monitorClass = getMonitorClass(entry);
        this.monitorName = this.getString("cn");
    }
    
    public final String getDN() {
        return this.entry.getDN();
    }
    
    public final ReadOnlyEntry getEntry() {
        return this.entry;
    }
    
    public final String getMonitorClass() {
        return this.monitorClass;
    }
    
    public final String getMonitorName() {
        return this.monitorName;
    }
    
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_GENERIC_MONITOR_DISPNAME.get();
    }
    
    public String getMonitorDescription() {
        return MonitorMessages.INFO_GENERIC_MONITOR_DESC.get();
    }
    
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(20));
        for (final Attribute a : this.entry.getAttributes()) {
            final String lowerName = StaticUtils.toLowerCase(a.getName());
            if (!lowerName.equals("cn")) {
                if (lowerName.equals("objectclass")) {
                    continue;
                }
                attrs.put(lowerName, new MonitorAttribute(lowerName, a.getName(), "", a.getValues()));
            }
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
    
    public static MonitorEntry decode(final Entry entry) {
        final String monitorClass = getMonitorClass(entry);
        if (monitorClass.equalsIgnoreCase("ds-active-operations-monitor-entry")) {
            return new ActiveOperationsMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-backend-monitor-entry")) {
            return new BackendMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-client-connection-monitor-entry")) {
            return new ClientConnectionMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-connectionhandler-monitor-entry")) {
            return new ConnectionHandlerMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-disk-space-usage-monitor-entry")) {
            return new DiskSpaceUsageMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-entry-cache-monitor-entry")) {
            return new EntryCacheMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-fifo-entry-cache-monitor-entry")) {
            return new FIFOEntryCacheMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-gauge-monitor-entry")) {
            return new GaugeMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-general-monitor-entry")) {
            return new GeneralMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-group-cache-monitor-entry")) {
            return new GroupCacheMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-host-system-cpu-memory-monitor-entry")) {
            return new HostSystemRecentCPUAndMemoryMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-index-monitor-entry")) {
            return new IndexMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-indicator-gauge-monitor-entry")) {
            return new IndicatorGaugeMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-je-environment-monitor-entry")) {
            return new JEEnvironmentMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-ldap-external-server-monitor-entry")) {
            return new LDAPExternalServerMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-ldap-statistics-monitor-entry")) {
            return new LDAPStatisticsMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-load-balancing-algorithm-monitor-entry")) {
            return new LoadBalancingAlgorithmMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-memory-usage-monitor-entry")) {
            return new MemoryUsageMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-numeric-gauge-monitor-entry")) {
            return new NumericGaugeMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-per-application-processing-time-histogram-monitor-entry")) {
            return new PerApplicationProcessingTimeHistogramMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-processing-time-histogram-monitor-entry")) {
            return new ProcessingTimeHistogramMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-replica-monitor-entry")) {
            return new ReplicaMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-replication-server-monitor-entry")) {
            return new ReplicationServerMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-replication-server-summary-monitor-entry")) {
            return new ReplicationSummaryMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-ldap-result-codes-monitor-entry")) {
            return new ResultCodeMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-stack-trace-monitor-entry")) {
            return new StackTraceMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-system-info-monitor-entry")) {
            return new SystemInfoMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-traditional-work-queue-monitor-entry")) {
            return new TraditionalWorkQueueMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-unboundid-work-queue-monitor-entry")) {
            return new UnboundIDWorkQueueMonitorEntry(entry);
        }
        if (monitorClass.equalsIgnoreCase("ds-version-monitor-entry")) {
            return new VersionMonitorEntry(entry);
        }
        return new MonitorEntry(entry);
    }
    
    private static String getMonitorClass(final Entry entry) {
        String monitorOC = null;
        final String[] arr$;
        final String[] ocNames = arr$ = entry.getObjectClassValues();
        for (final String oc : arr$) {
            if (!oc.equalsIgnoreCase("top") && !oc.equalsIgnoreCase("extensibleObject")) {
                if (!oc.equalsIgnoreCase("ds-monitor-entry")) {
                    if (oc.equalsIgnoreCase("ds-numeric-gauge-monitor-entry") || oc.equalsIgnoreCase("ds-indicator-gauge-monitor-entry")) {
                        monitorOC = oc;
                    }
                    else if (oc.equalsIgnoreCase("ds-gauge-monitor-entry")) {
                        if (monitorOC == null) {
                            monitorOC = oc;
                        }
                    }
                    else {
                        if (monitorOC != null && Debug.debugEnabled(DebugType.MONITOR)) {
                            Debug.debugMonitor(entry, "Multiple monitor subclasses detected:  " + monitorOC + " and " + oc);
                        }
                        monitorOC = oc;
                    }
                }
            }
        }
        if (monitorOC == null) {
            if (entry.hasObjectClass("ds-monitor-entry")) {
                Debug.debugMonitor(entry, "No appropriate monitor subclass");
            }
            else {
                Debug.debugMonitor(entry, "Missing the generic monitor class");
            }
            return "ds-monitor-entry";
        }
        return monitorOC;
    }
    
    protected final Boolean getBoolean(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No value for Boolean attribute " + attributeName);
            }
            return null;
        }
        if (valueStr.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (valueStr.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (Debug.debugEnabled(DebugType.MONITOR)) {
            Debug.debugMonitor(this.entry, "Invalid value '" + valueStr + "' for Boolean attribute " + attributeName);
        }
        return null;
    }
    
    protected final Date getDate(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No value for Date attribute " + attributeName);
            }
            return null;
        }
        try {
            return StaticUtils.decodeGeneralizedTime(valueStr);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "Invalid value '" + valueStr + "' for Date attribute " + attributeName);
            }
            return null;
        }
    }
    
    protected final Double getDouble(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No value for Double attribute " + attributeName);
            }
            return null;
        }
        try {
            return Double.parseDouble(valueStr);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "Invalid value '" + valueStr + "' for Double attribute " + attributeName);
            }
            return null;
        }
    }
    
    protected final Integer getInteger(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No value for Integer attribute " + attributeName);
            }
            return null;
        }
        try {
            return Integer.parseInt(valueStr);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "Invalid value '" + valueStr + "' for Integer attribute " + attributeName);
            }
            return null;
        }
    }
    
    protected final Long getLong(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No value for Long attribute " + attributeName);
            }
            return null;
        }
        try {
            return Long.parseLong(valueStr);
        }
        catch (final Exception e) {
            Debug.debugException(e);
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "Invalid value '" + valueStr + "' for Long attribute " + attributeName);
            }
            return null;
        }
    }
    
    protected final String getString(final String attributeName) {
        final String valueStr = this.entry.getAttributeValue(attributeName);
        if (valueStr == null && Debug.debugEnabled(DebugType.MONITOR)) {
            Debug.debugMonitor(this.entry, "No value for string attribute " + attributeName);
        }
        return valueStr;
    }
    
    protected final List<String> getStrings(final String attributeName) {
        final String[] valueStrs = this.entry.getAttributeValues(attributeName);
        if (valueStrs == null) {
            if (Debug.debugEnabled(DebugType.MONITOR)) {
                Debug.debugMonitor(this.entry, "No values for string attribute " + attributeName);
            }
            return Collections.emptyList();
        }
        return Collections.unmodifiableList((List<? extends String>)Arrays.asList((T[])valueStrs));
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final Boolean value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final Date value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final Double value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final Integer value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final Long value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final String value) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, value);
        attrs.put(lowerName, a);
    }
    
    protected static void addMonitorAttribute(final Map<String, MonitorAttribute> attrs, final String name, final String displayName, final String description, final List<String> values) {
        final String lowerName = StaticUtils.toLowerCase(name);
        final MonitorAttribute a = new MonitorAttribute(lowerName, displayName, description, values.toArray(new String[values.size()]));
        attrs.put(lowerName, a);
    }
    
    @Override
    public final String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public final void toString(final StringBuilder buffer) {
        buffer.append("MonitorEntry(dn='");
        buffer.append(this.entry.getDN());
        buffer.append("', monitorClass='");
        buffer.append(this.monitorClass);
        buffer.append('\'');
        final Iterator<MonitorAttribute> iterator = this.getMonitorAttributes().values().iterator();
        while (iterator.hasNext()) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(", ");
            }
        }
        buffer.append(')');
    }
}
