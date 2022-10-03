package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.util.Debug;
import java.util.ArrayList;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LoadBalancingAlgorithmMonitorEntry extends MonitorEntry
{
    protected static final String LOAD_BALANCING_ALGORITHM_MONITOR_OC = "ds-load-balancing-algorithm-monitor-entry";
    private static final String ATTR_ALGORITHM_NAME = "algorithm-name";
    private static final String ATTR_CONFIG_ENTRY_DN = "config-entry-dn";
    private static final String ATTR_HEALTH_CHECK_STATE = "health-check-state";
    private static final String ATTR_LDAP_EXTERNAL_SERVER = "ldap-external-server";
    private static final String ATTR_LOCAL_SERVERS_HEALTH_CHECK_STATE = "local-servers-health-check-state";
    private static final String ATTR_NON_LOCAL_SERVERS_HEALTH_CHECK_STATE = "non-local-servers-health-check-state";
    private static final String ATTR_NUM_AVAILABLE = "num-available-servers";
    private static final String ATTR_NUM_DEGRADED = "num-degraded-servers";
    private static final String ATTR_NUM_UNAVAILABLE = "num-unavailable-servers";
    private static final long serialVersionUID = -5251924301718025205L;
    private final HealthCheckState healthCheckState;
    private final HealthCheckState localServersHealthCheckState;
    private final HealthCheckState nonLocalServersHealthCheckState;
    private final List<LoadBalancingAlgorithmServerAvailabilityData> serverAvailabilityData;
    private final Long numAvailableServers;
    private final Long numDegradedServers;
    private final Long numUnavailableServers;
    private final String algorithmName;
    private final String configEntryDN;
    
    public LoadBalancingAlgorithmMonitorEntry(final Entry entry) {
        super(entry);
        this.algorithmName = this.getString("algorithm-name");
        this.configEntryDN = this.getString("config-entry-dn");
        this.numAvailableServers = this.getLong("num-available-servers");
        this.numDegradedServers = this.getLong("num-degraded-servers");
        this.numUnavailableServers = this.getLong("num-unavailable-servers");
        final String hcStateStr = this.getString("health-check-state");
        if (hcStateStr == null) {
            this.healthCheckState = null;
        }
        else {
            this.healthCheckState = HealthCheckState.forName(hcStateStr);
        }
        final String localHCStateStr = this.getString("local-servers-health-check-state");
        if (localHCStateStr == null) {
            this.localServersHealthCheckState = null;
        }
        else {
            this.localServersHealthCheckState = HealthCheckState.forName(localHCStateStr);
        }
        final String nonLocalHCStateStr = this.getString("non-local-servers-health-check-state");
        if (nonLocalHCStateStr == null) {
            this.nonLocalServersHealthCheckState = null;
        }
        else {
            this.nonLocalServersHealthCheckState = HealthCheckState.forName(nonLocalHCStateStr);
        }
        final List<String> externalServerStrings = this.getStrings("ldap-external-server");
        final ArrayList<LoadBalancingAlgorithmServerAvailabilityData> serverData = new ArrayList<LoadBalancingAlgorithmServerAvailabilityData>(externalServerStrings.size());
        for (final String s : externalServerStrings) {
            try {
                serverData.add(new LoadBalancingAlgorithmServerAvailabilityData(s));
            }
            catch (final Exception e) {
                Debug.debugException(e);
            }
        }
        this.serverAvailabilityData = Collections.unmodifiableList((List<? extends LoadBalancingAlgorithmServerAvailabilityData>)serverData);
    }
    
    public String getAlgorithmName() {
        return this.algorithmName;
    }
    
    public String getConfigEntryDN() {
        return this.configEntryDN;
    }
    
    public HealthCheckState getHealthCheckState() {
        return this.healthCheckState;
    }
    
    public HealthCheckState getLocalServersHealthCheckState() {
        return this.localServersHealthCheckState;
    }
    
    public HealthCheckState getNonLocalServersHealthCheckState() {
        return this.nonLocalServersHealthCheckState;
    }
    
    public List<LoadBalancingAlgorithmServerAvailabilityData> getServerAvailabilityData() {
        return this.serverAvailabilityData;
    }
    
    public Long getNumAvailableServers() {
        return this.numAvailableServers;
    }
    
    public Long getNumDegradedServers() {
        return this.numDegradedServers;
    }
    
    public Long getNumUnavailableServers() {
        return this.numUnavailableServers;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(9));
        if (this.algorithmName != null) {
            MonitorEntry.addMonitorAttribute(attrs, "algorithm-name", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_ALGORITHM_NAME.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_ALGORITHM_NAME.get(), this.algorithmName);
        }
        if (this.configEntryDN != null) {
            MonitorEntry.addMonitorAttribute(attrs, "config-entry-dn", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_CONFIG_ENTRY_DN.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_CONFIG_ENTRY_DN.get(), this.configEntryDN);
        }
        if (this.healthCheckState != null) {
            MonitorEntry.addMonitorAttribute(attrs, "health-check-state", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_HEALTH_CHECK_STATE.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_HEALTH_CHECK_STATE.get(), this.healthCheckState.name());
        }
        if (this.localServersHealthCheckState != null) {
            MonitorEntry.addMonitorAttribute(attrs, "local-servers-health-check-state", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_L_HEALTH_CHECK_STATE.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_L_HEALTH_CHECK_STATE.get(), this.localServersHealthCheckState.name());
        }
        if (this.nonLocalServersHealthCheckState != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-local-servers-health-check-state", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_NL_HEALTH_CHECK_STATE.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_NL_HEALTH_CHECK_STATE.get(), this.nonLocalServersHealthCheckState.name());
        }
        if (this.serverAvailabilityData != null && !this.serverAvailabilityData.isEmpty()) {
            final ArrayList<String> availabilityStrings = new ArrayList<String>(this.serverAvailabilityData.size());
            for (final LoadBalancingAlgorithmServerAvailabilityData d : this.serverAvailabilityData) {
                availabilityStrings.add(d.toCompactString());
            }
            MonitorEntry.addMonitorAttribute(attrs, "ldap-external-server", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_SERVER_DATA.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_SERVER_DATA.get(), availabilityStrings);
        }
        if (this.numAvailableServers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-available-servers", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_NUM_AVAILABLE.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_NUM_AVAILABLE.get(), this.numAvailableServers);
        }
        if (this.numDegradedServers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-degraded-servers", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_NUM_DEGRADED.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_NUM_DEGRADED.get(), this.numDegradedServers);
        }
        if (this.numUnavailableServers != null) {
            MonitorEntry.addMonitorAttribute(attrs, "num-unavailable-servers", MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DISPNAME_NUM_UNAVAILABLE.get(), MonitorMessages.INFO_LOAD_BALANCING_ALGORITHM_DESC_NUM_UNAVAILABLE.get(), this.numUnavailableServers);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
