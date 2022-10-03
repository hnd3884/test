package com.unboundid.ldap.sdk.unboundidds.monitors;

import com.unboundid.util.Debug;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import java.util.Iterator;
import com.unboundid.ldap.sdk.SearchResult;
import java.util.Collections;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.SearchResultEntry;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.LDAPInterface;
import java.util.List;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class MonitorManager
{
    private MonitorManager() {
    }
    
    public static List<MonitorEntry> getMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<MonitorEntry> getMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<MonitorEntry> monitorEntries = new ArrayList<MonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(MonitorEntry.decode(e));
        }
        return Collections.unmodifiableList((List<? extends MonitorEntry>)monitorEntries);
    }
    
    public static GeneralMonitorEntry getGeneralMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getGeneralMonitorEntry((LDAPInterface)connection);
    }
    
    public static GeneralMonitorEntry getGeneralMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createPresenceFilter("objectClass");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.BASE, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getGeneralMonitorEntry");
            return null;
        }
        return new GeneralMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static ActiveOperationsMonitorEntry getActiveOperationsMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getActiveOperationsMonitorEntry((LDAPInterface)connection);
    }
    
    public static ActiveOperationsMonitorEntry getActiveOperationsMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-active-operations-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getActiveOperationsMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getActiveOperationsMonitorEntry");
        }
        return new ActiveOperationsMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<BackendMonitorEntry> getBackendMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getBackendMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<BackendMonitorEntry> getBackendMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-backend-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<BackendMonitorEntry> monitorEntries = new ArrayList<BackendMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new BackendMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends BackendMonitorEntry>)monitorEntries);
    }
    
    public static ClientConnectionMonitorEntry getClientConnectionMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getClientConnectionMonitorEntry((LDAPInterface)connection);
    }
    
    public static ClientConnectionMonitorEntry getClientConnectionMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-client-connection-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getClientConnectionMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getClientConnectionMonitorEntry");
        }
        return new ClientConnectionMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<ConnectionHandlerMonitorEntry> getConnectionHandlerMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getConnectionHandlerMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<ConnectionHandlerMonitorEntry> getConnectionHandlerMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-connectionhandler-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<ConnectionHandlerMonitorEntry> monitorEntries = new ArrayList<ConnectionHandlerMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new ConnectionHandlerMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends ConnectionHandlerMonitorEntry>)monitorEntries);
    }
    
    public static DiskSpaceUsageMonitorEntry getDiskSpaceUsageMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getDiskSpaceUsageMonitorEntry((LDAPInterface)connection);
    }
    
    public static DiskSpaceUsageMonitorEntry getDiskSpaceUsageMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-disk-space-usage-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getDiskSpaceUsageMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getDiskSpaceUsageMonitorEntry");
        }
        return new DiskSpaceUsageMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static EntryCacheMonitorEntry getEntryCacheMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getEntryCacheMonitorEntry((LDAPInterface)connection);
    }
    
    public static EntryCacheMonitorEntry getEntryCacheMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-entry-cache-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getEntryCacheMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getEntryCacheMonitorEntry");
        }
        return new EntryCacheMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<FIFOEntryCacheMonitorEntry> getFIFOEntryCacheMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getFIFOEntryCacheMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<FIFOEntryCacheMonitorEntry> getFIFOEntryCacheMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-fifo-entry-cache-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<FIFOEntryCacheMonitorEntry> monitorEntries = new ArrayList<FIFOEntryCacheMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new FIFOEntryCacheMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends FIFOEntryCacheMonitorEntry>)monitorEntries);
    }
    
    public static List<GaugeMonitorEntry> getGaugeMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-gauge-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<GaugeMonitorEntry> monitorEntries = new ArrayList<GaugeMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            try {
                monitorEntries.add((GaugeMonitorEntry)MonitorEntry.decode(e));
            }
            catch (final Exception ex) {
                Debug.debugException(ex);
            }
        }
        return Collections.unmodifiableList((List<? extends GaugeMonitorEntry>)monitorEntries);
    }
    
    public static GroupCacheMonitorEntry getGroupCacheMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-group-cache-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getGroupCacheMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getGroupCacheMonitorEntry");
        }
        return new GroupCacheMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static HostSystemRecentCPUAndMemoryMonitorEntry getHostSystemRecentCPUAndMemoryMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-host-system-cpu-memory-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getHostSystemRecentCPUAndMemoryMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getHostSystemRecentCPUAndMemoryMonitorEntry");
        }
        return new HostSystemRecentCPUAndMemoryMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<IndexMonitorEntry> getIndexMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getIndexMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<IndexMonitorEntry> getIndexMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-index-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<IndexMonitorEntry> monitorEntries = new ArrayList<IndexMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new IndexMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends IndexMonitorEntry>)monitorEntries);
    }
    
    public static List<IndicatorGaugeMonitorEntry> getIndicatorGaugeMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-gauge-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<IndicatorGaugeMonitorEntry> monitorEntries = new ArrayList<IndicatorGaugeMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new IndicatorGaugeMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends IndicatorGaugeMonitorEntry>)monitorEntries);
    }
    
    public static List<JEEnvironmentMonitorEntry> getJEEnvironmentMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getJEEnvironmentMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<JEEnvironmentMonitorEntry> getJEEnvironmentMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-je-environment-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<JEEnvironmentMonitorEntry> monitorEntries = new ArrayList<JEEnvironmentMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new JEEnvironmentMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends JEEnvironmentMonitorEntry>)monitorEntries);
    }
    
    public static List<LDAPExternalServerMonitorEntry> getLDAPExternalServerMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getLDAPExternalServerMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<LDAPExternalServerMonitorEntry> getLDAPExternalServerMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-ldap-external-server-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<LDAPExternalServerMonitorEntry> monitorEntries = new ArrayList<LDAPExternalServerMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new LDAPExternalServerMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends LDAPExternalServerMonitorEntry>)monitorEntries);
    }
    
    public static List<LDAPStatisticsMonitorEntry> getLDAPStatisticsMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getLDAPStatisticsMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<LDAPStatisticsMonitorEntry> getLDAPStatisticsMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-ldap-statistics-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<LDAPStatisticsMonitorEntry> monitorEntries = new ArrayList<LDAPStatisticsMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new LDAPStatisticsMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends LDAPStatisticsMonitorEntry>)monitorEntries);
    }
    
    public static List<LoadBalancingAlgorithmMonitorEntry> getLoadBalancingAlgorithmMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getLoadBalancingAlgorithmMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<LoadBalancingAlgorithmMonitorEntry> getLoadBalancingAlgorithmMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-load-balancing-algorithm-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<LoadBalancingAlgorithmMonitorEntry> monitorEntries = new ArrayList<LoadBalancingAlgorithmMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new LoadBalancingAlgorithmMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends LoadBalancingAlgorithmMonitorEntry>)monitorEntries);
    }
    
    public static MemoryUsageMonitorEntry getMemoryUsageMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getMemoryUsageMonitorEntry((LDAPInterface)connection);
    }
    
    public static MemoryUsageMonitorEntry getMemoryUsageMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-memory-usage-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getMemoryUsageMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getMemoryUsageMonitorEntry");
        }
        return new MemoryUsageMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<NumericGaugeMonitorEntry> getNumericGaugeMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-gauge-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<NumericGaugeMonitorEntry> monitorEntries = new ArrayList<NumericGaugeMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new NumericGaugeMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends NumericGaugeMonitorEntry>)monitorEntries);
    }
    
    public static List<PerApplicationProcessingTimeHistogramMonitorEntry> getPerApplicationProcessingTimeHistogramMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getPerApplicationProcessingTimeHistogramMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<PerApplicationProcessingTimeHistogramMonitorEntry> getPerApplicationProcessingTimeHistogramMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-per-application-processing-time-histogram-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getPerApplicationProcessingTimeHistogramMonitorEntries");
            return Collections.emptyList();
        }
        final List<PerApplicationProcessingTimeHistogramMonitorEntry> entries = new ArrayList<PerApplicationProcessingTimeHistogramMonitorEntry>(searchResult.getEntryCount());
        for (final Entry entry : searchResult.getSearchEntries()) {
            entries.add(new PerApplicationProcessingTimeHistogramMonitorEntry(entry));
        }
        return entries;
    }
    
    public static ProcessingTimeHistogramMonitorEntry getProcessingTimeHistogramMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getProcessingTimeHistogramMonitorEntry((LDAPInterface)connection);
    }
    
    public static ProcessingTimeHistogramMonitorEntry getProcessingTimeHistogramMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-processing-time-histogram-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getProcessingTimeHistogramMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getProcessingTimeHistogramMonitorEntry");
        }
        return new ProcessingTimeHistogramMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<ReplicaMonitorEntry> getReplicaMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getReplicaMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<ReplicaMonitorEntry> getReplicaMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-replica-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<ReplicaMonitorEntry> monitorEntries = new ArrayList<ReplicaMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new ReplicaMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends ReplicaMonitorEntry>)monitorEntries);
    }
    
    public static ReplicationServerMonitorEntry getReplicationServerMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getReplicationServerMonitorEntry((LDAPInterface)connection);
    }
    
    public static ReplicationServerMonitorEntry getReplicationServerMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-replication-server-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getReplicationServerMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getReplicationServerMonitorEntry");
        }
        return new ReplicationServerMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static List<ReplicationSummaryMonitorEntry> getReplicationSummaryMonitorEntries(final LDAPConnection connection) throws LDAPSearchException {
        return getReplicationSummaryMonitorEntries((LDAPInterface)connection);
    }
    
    public static List<ReplicationSummaryMonitorEntry> getReplicationSummaryMonitorEntries(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-replication-server-summary-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final ArrayList<ReplicationSummaryMonitorEntry> monitorEntries = new ArrayList<ReplicationSummaryMonitorEntry>(searchResult.getEntryCount());
        for (final SearchResultEntry e : searchResult.getSearchEntries()) {
            monitorEntries.add(new ReplicationSummaryMonitorEntry(e));
        }
        return Collections.unmodifiableList((List<? extends ReplicationSummaryMonitorEntry>)monitorEntries);
    }
    
    public static ResultCodeMonitorEntry getResultCodeMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-ldap-result-codes-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getResultCodeMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getResultCodeMonitorEntry");
        }
        return new ResultCodeMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static SystemInfoMonitorEntry getSystemInfoMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getSystemInfoMonitorEntry((LDAPInterface)connection);
    }
    
    public static SystemInfoMonitorEntry getSystemInfoMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-system-info-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getSystemInfoMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getSystemInfoMonitorEntry");
        }
        return new SystemInfoMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static StackTraceMonitorEntry getStackTraceMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getStackTraceMonitorEntry((LDAPInterface)connection);
    }
    
    public static StackTraceMonitorEntry getStackTraceMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-stack-trace-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getStackTraceMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getStackTraceMonitorEntry");
        }
        return new StackTraceMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static TraditionalWorkQueueMonitorEntry getTraditionalWorkQueueMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getTraditionalWorkQueueMonitorEntry((LDAPInterface)connection);
    }
    
    public static TraditionalWorkQueueMonitorEntry getTraditionalWorkQueueMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-traditional-work-queue-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getTraditionalWorkQueueMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getTraditionalWorkQueueMonitorEntry");
        }
        return new TraditionalWorkQueueMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static UnboundIDWorkQueueMonitorEntry getUnboundIDWorkQueueMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getUnboundIDWorkQueueMonitorEntry((LDAPInterface)connection);
    }
    
    public static UnboundIDWorkQueueMonitorEntry getUnboundIDWorkQueueMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-unboundid-work-queue-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getUnboundIDWorkQueueMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getUnboundIDWorkQueueMonitorEntry");
        }
        return new UnboundIDWorkQueueMonitorEntry(searchResult.getSearchEntries().get(0));
    }
    
    public static VersionMonitorEntry getVersionMonitorEntry(final LDAPConnection connection) throws LDAPSearchException {
        return getVersionMonitorEntry((LDAPInterface)connection);
    }
    
    public static VersionMonitorEntry getVersionMonitorEntry(final LDAPInterface connection) throws LDAPSearchException {
        final Filter filter = Filter.createEqualityFilter("objectClass", "ds-version-monitor-entry");
        final SearchResult searchResult = connection.search("cn=monitor", SearchScope.SUB, filter, new String[0]);
        final int numEntries = searchResult.getEntryCount();
        if (numEntries == 0) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "No entries returned in getVersionMonitorEntry");
            return null;
        }
        if (numEntries != 1) {
            Debug.debug(Level.FINE, DebugType.MONITOR, "Multiple entries returned in getVersionMonitorEntry");
        }
        return new VersionMonitorEntry(searchResult.getSearchEntries().get(0));
    }
}
