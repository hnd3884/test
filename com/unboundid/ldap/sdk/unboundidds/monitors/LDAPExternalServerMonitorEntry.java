package com.unboundid.ldap.sdk.unboundidds.monitors;

import java.util.Collections;
import java.util.LinkedHashMap;
import com.unboundid.util.StaticUtils;
import java.util.Map;
import com.unboundid.ldap.sdk.Entry;
import java.util.List;
import java.util.Date;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class LDAPExternalServerMonitorEntry extends MonitorEntry
{
    protected static final String LDAP_EXTERNAL_SERVER_MONITOR_OC = "ds-ldap-external-server-monitor-entry";
    private static final String ATTR_ADD_ATTEMPTS = "add-attempts";
    private static final String ATTR_ADD_FAILURES = "add-failures";
    private static final String ATTR_ADD_SUCCESSES = "add-successes";
    private static final String ATTR_BIND_ATTEMPTS = "bind-attempts";
    private static final String ATTR_BIND_FAILURES = "bind-failures";
    private static final String ATTR_BIND_SUCCESSES = "bind-successes";
    private static final String ATTR_COMMUNICATION_SECURITY = "communication-security";
    private static final String ATTR_COMPARE_ATTEMPTS = "compare-attempts";
    private static final String ATTR_COMPARE_FAILURES = "compare-failures";
    private static final String ATTR_COMPARE_SUCCESSES = "compare-successes";
    private static final String ATTR_DELETE_ATTEMPTS = "delete-attempts";
    private static final String ATTR_DELETE_FAILURES = "delete-failures";
    private static final String ATTR_DELETE_SUCCESSES = "delete-successes";
    private static final String ATTR_HEALTH_CHECK_MESSAGE = "health-check-message";
    private static final String ATTR_HEALTH_CHECK_STATE = "health-check-state";
    private static final String ATTR_HEALTH_CHECK_SCORE = "health-check-score";
    private static final String ATTR_HEALTH_CHECK_UPDATE_TIME = "health-check-update-time";
    private static final String ATTR_LOAD_BALANCING_ALGORITHM_DN = "load-balancing-algorithm";
    private static final String ATTR_MODIFY_ATTEMPTS = "modify-attempts";
    private static final String ATTR_MODIFY_FAILURES = "modify-failures";
    private static final String ATTR_MODIFY_SUCCESSES = "modify-successes";
    private static final String ATTR_MODIFY_DN_ATTEMPTS = "modify-dn-attempts";
    private static final String ATTR_MODIFY_DN_FAILURES = "modify-dn-failures";
    private static final String ATTR_MODIFY_DN_SUCCESSES = "modify-dn-successes";
    private static final String ATTR_SEARCH_ATTEMPTS = "search-attempts";
    private static final String ATTR_SEARCH_FAILURES = "search-failures";
    private static final String ATTR_SEARCH_SUCCESSES = "search-successes";
    private static final String ATTR_SERVER_ADDRESS = "server-address";
    private static final String ATTR_SERVER_PORT = "server-port";
    private static final String ATTR_PREFIX_BIND_POOL = "bind-";
    private static final String ATTR_PREFIX_COMMON_POOL = "common-";
    private static final String ATTR_PREFIX_NONBIND_POOL = "non-bind-";
    private static final String ATTR_SUFFIX_AVAILABLE_CONNS = "pool-available-connections";
    private static final String ATTR_SUFFIX_CLOSED_DEFUNCT = "pool-num-closed-defunct";
    private static final String ATTR_SUFFIX_CLOSED_EXPIRED = "pool-num-closed-expired";
    private static final String ATTR_SUFFIX_CLOSED_UNNEEDED = "pool-num-closed-unneeded";
    private static final String ATTR_SUFFIX_FAILED_CHECKOUTS = "pool-num-failed-checkouts";
    private static final String ATTR_SUFFIX_FAILED_CONNECTS = "pool-num-failed-connection-attempts";
    private static final String ATTR_SUFFIX_MAX_AVAILABLE_CONNS = "pool-max-available-connections";
    private static final String ATTR_SUFFIX_RELEASED_VALID = "pool-num-released-valid";
    private static final String ATTR_SUFFIX_SUCCESSFUL_CHECKOUTS = "pool-num-successful-checkouts";
    private static final String ATTR_SUFFIX_SUCCESSFUL_CHECKOUTS_AFTER_WAITING = "pool-num-successful-checkouts-after-waiting";
    private static final String ATTR_SUFFIX_SUCCESSFUL_CHECKOUTS_NEW_CONN = "pool-num-successful-checkouts-new-connection";
    private static final String ATTR_SUFFIX_SUCCESSFUL_CHECKOUTS_WITHOUT_WAITING = "pool-num-successful-checkouts-without-waiting";
    private static final String ATTR_SUFFIX_SUCCESSFUL_CONNECTS = "pool-num-successful-connection-attempts";
    private static final long serialVersionUID = 6054649631882735072L;
    private final Date healthCheckUpdateTime;
    private final HealthCheckState healthCheckState;
    private final List<String> healthCheckMessages;
    private final Long addAttempts;
    private final Long addFailures;
    private final Long addSuccesses;
    private final Long bindAttempts;
    private final Long bindFailures;
    private final Long bindPoolAvailableConnections;
    private final Long bindPoolMaxAvailableConnections;
    private final Long bindPoolNumClosedDefunct;
    private final Long bindPoolNumClosedExpired;
    private final Long bindPoolNumClosedUnneeded;
    private final Long bindPoolNumFailedCheckouts;
    private final Long bindPoolNumFailedConnectionAttempts;
    private final Long bindPoolNumReleasedValid;
    private final Long bindPoolNumSuccessfulCheckouts;
    private final Long bindPoolNumSuccessfulCheckoutsAfterWaiting;
    private final Long bindPoolNumSuccessfulCheckoutsNewConnection;
    private final Long bindPoolNumSuccessfulCheckoutsWithoutWaiting;
    private final Long bindPoolNumSuccessfulConnectionAttempts;
    private final Long bindSuccesses;
    private final Long commonPoolAvailableConnections;
    private final Long commonPoolMaxAvailableConnections;
    private final Long commonPoolNumClosedDefunct;
    private final Long commonPoolNumClosedExpired;
    private final Long commonPoolNumClosedUnneeded;
    private final Long commonPoolNumFailedCheckouts;
    private final Long commonPoolNumFailedConnectionAttempts;
    private final Long commonPoolNumReleasedValid;
    private final Long commonPoolNumSuccessfulCheckouts;
    private final Long commonPoolNumSuccessfulCheckoutsAfterWaiting;
    private final Long commonPoolNumSuccessfulCheckoutsNewConnection;
    private final Long commonPoolNumSuccessfulCheckoutsWithoutWaiting;
    private final Long commonPoolNumSuccessfulConnectionAttempts;
    private final Long compareAttempts;
    private final Long compareFailures;
    private final Long compareSuccesses;
    private final Long deleteAttempts;
    private final Long deleteFailures;
    private final Long deleteSuccesses;
    private final Long healthCheckScore;
    private final Long modifyAttempts;
    private final Long modifyFailures;
    private final Long modifySuccesses;
    private final Long modifyDNAttempts;
    private final Long modifyDNFailures;
    private final Long modifyDNSuccesses;
    private final Long nonBindPoolAvailableConnections;
    private final Long nonBindPoolMaxAvailableConnections;
    private final Long nonBindPoolNumClosedDefunct;
    private final Long nonBindPoolNumClosedExpired;
    private final Long nonBindPoolNumClosedUnneeded;
    private final Long nonBindPoolNumFailedCheckouts;
    private final Long nonBindPoolNumFailedConnectionAttempts;
    private final Long nonBindPoolNumReleasedValid;
    private final Long nonBindPoolNumSuccessfulCheckouts;
    private final Long nonBindPoolNumSuccessfulCheckoutsAfterWaiting;
    private final Long nonBindPoolNumSuccessfulCheckoutsNewConnection;
    private final Long nonBindPoolNumSuccessfulCheckoutsWithoutWaiting;
    private final Long nonBindPoolNumSuccessfulConnectionAttempts;
    private final Long searchAttempts;
    private final Long searchFailures;
    private final Long searchSuccesses;
    private final Long serverPort;
    private final String communicationSecurity;
    private final String loadBalancingAlgorithmDN;
    private final String serverAddress;
    
    public LDAPExternalServerMonitorEntry(final Entry entry) {
        super(entry);
        this.serverAddress = this.getString("server-address");
        this.serverPort = this.getLong("server-port");
        this.communicationSecurity = this.getString("communication-security");
        this.loadBalancingAlgorithmDN = this.getString("load-balancing-algorithm");
        this.healthCheckScore = this.getLong("health-check-score");
        this.healthCheckMessages = this.getStrings("health-check-message");
        this.healthCheckUpdateTime = this.getDate("health-check-update-time");
        this.addAttempts = this.getLong("add-attempts");
        this.addFailures = this.getLong("add-failures");
        this.addSuccesses = this.getLong("add-successes");
        this.bindAttempts = this.getLong("bind-attempts");
        this.bindFailures = this.getLong("bind-failures");
        this.bindSuccesses = this.getLong("bind-successes");
        this.compareAttempts = this.getLong("compare-attempts");
        this.compareFailures = this.getLong("compare-failures");
        this.compareSuccesses = this.getLong("compare-successes");
        this.deleteAttempts = this.getLong("delete-attempts");
        this.deleteFailures = this.getLong("delete-failures");
        this.deleteSuccesses = this.getLong("delete-successes");
        this.modifyAttempts = this.getLong("modify-attempts");
        this.modifyFailures = this.getLong("modify-failures");
        this.modifySuccesses = this.getLong("modify-successes");
        this.modifyDNAttempts = this.getLong("modify-dn-attempts");
        this.modifyDNFailures = this.getLong("modify-dn-failures");
        this.modifyDNSuccesses = this.getLong("modify-dn-successes");
        this.searchAttempts = this.getLong("search-attempts");
        this.searchFailures = this.getLong("search-failures");
        this.searchSuccesses = this.getLong("search-successes");
        this.bindPoolAvailableConnections = this.getLong("bind-pool-available-connections");
        this.bindPoolMaxAvailableConnections = this.getLong("bind-pool-max-available-connections");
        this.bindPoolNumSuccessfulConnectionAttempts = this.getLong("bind-pool-num-successful-connection-attempts");
        this.bindPoolNumFailedConnectionAttempts = this.getLong("bind-pool-num-failed-connection-attempts");
        this.bindPoolNumClosedDefunct = this.getLong("bind-pool-num-closed-defunct");
        this.bindPoolNumClosedExpired = this.getLong("bind-pool-num-closed-expired");
        this.bindPoolNumClosedUnneeded = this.getLong("bind-pool-num-closed-unneeded");
        this.bindPoolNumSuccessfulCheckouts = this.getLong("bind-pool-num-successful-checkouts");
        this.bindPoolNumSuccessfulCheckoutsWithoutWaiting = this.getLong("bind-pool-num-successful-checkouts-without-waiting");
        this.bindPoolNumSuccessfulCheckoutsAfterWaiting = this.getLong("bind-pool-num-successful-checkouts-after-waiting");
        this.bindPoolNumSuccessfulCheckoutsNewConnection = this.getLong("bind-pool-num-successful-checkouts-new-connection");
        this.bindPoolNumFailedCheckouts = this.getLong("bind-pool-num-failed-checkouts");
        this.bindPoolNumReleasedValid = this.getLong("bind-pool-num-released-valid");
        this.commonPoolAvailableConnections = this.getLong("common-pool-available-connections");
        this.commonPoolMaxAvailableConnections = this.getLong("common-pool-max-available-connections");
        this.commonPoolNumSuccessfulConnectionAttempts = this.getLong("common-pool-num-successful-connection-attempts");
        this.commonPoolNumFailedConnectionAttempts = this.getLong("common-pool-num-failed-connection-attempts");
        this.commonPoolNumClosedDefunct = this.getLong("common-pool-num-closed-defunct");
        this.commonPoolNumClosedExpired = this.getLong("common-pool-num-closed-expired");
        this.commonPoolNumClosedUnneeded = this.getLong("common-pool-num-closed-unneeded");
        this.commonPoolNumSuccessfulCheckouts = this.getLong("common-pool-num-successful-checkouts");
        this.commonPoolNumSuccessfulCheckoutsWithoutWaiting = this.getLong("common-pool-num-successful-checkouts-without-waiting");
        this.commonPoolNumSuccessfulCheckoutsAfterWaiting = this.getLong("common-pool-num-successful-checkouts-after-waiting");
        this.commonPoolNumSuccessfulCheckoutsNewConnection = this.getLong("common-pool-num-successful-checkouts-new-connection");
        this.commonPoolNumFailedCheckouts = this.getLong("common-pool-num-failed-checkouts");
        this.commonPoolNumReleasedValid = this.getLong("common-pool-num-released-valid");
        this.nonBindPoolAvailableConnections = this.getLong("non-bind-pool-available-connections");
        this.nonBindPoolMaxAvailableConnections = this.getLong("non-bind-pool-max-available-connections");
        this.nonBindPoolNumSuccessfulConnectionAttempts = this.getLong("non-bind-pool-num-successful-connection-attempts");
        this.nonBindPoolNumFailedConnectionAttempts = this.getLong("non-bind-pool-num-failed-connection-attempts");
        this.nonBindPoolNumClosedDefunct = this.getLong("non-bind-pool-num-closed-defunct");
        this.nonBindPoolNumClosedExpired = this.getLong("non-bind-pool-num-closed-expired");
        this.nonBindPoolNumClosedUnneeded = this.getLong("non-bind-pool-num-closed-unneeded");
        this.nonBindPoolNumSuccessfulCheckouts = this.getLong("non-bind-pool-num-successful-checkouts");
        this.nonBindPoolNumSuccessfulCheckoutsWithoutWaiting = this.getLong("non-bind-pool-num-successful-checkouts-without-waiting");
        this.nonBindPoolNumSuccessfulCheckoutsAfterWaiting = this.getLong("non-bind-pool-num-successful-checkouts-after-waiting");
        this.nonBindPoolNumSuccessfulCheckoutsNewConnection = this.getLong("non-bind-pool-num-successful-checkouts-new-connection");
        this.nonBindPoolNumFailedCheckouts = this.getLong("non-bind-pool-num-failed-checkouts");
        this.nonBindPoolNumReleasedValid = this.getLong("non-bind-pool-num-released-valid");
        final String hcStateStr = this.getString("health-check-state");
        if (hcStateStr == null) {
            this.healthCheckState = null;
        }
        else {
            this.healthCheckState = HealthCheckState.forName(hcStateStr);
        }
    }
    
    public String getServerAddress() {
        return this.serverAddress;
    }
    
    public Long getServerPort() {
        return this.serverPort;
    }
    
    public String getCommunicationSecurity() {
        return this.communicationSecurity;
    }
    
    public String getLoadBalancingAlgorithmDN() {
        return this.loadBalancingAlgorithmDN;
    }
    
    public HealthCheckState getHealthCheckState() {
        return this.healthCheckState;
    }
    
    public Long getHealthCheckScore() {
        return this.healthCheckScore;
    }
    
    public List<String> getHealthCheckMessages() {
        return this.healthCheckMessages;
    }
    
    public Date getHealthCheckUpdateTime() {
        return this.healthCheckUpdateTime;
    }
    
    public Long getAddAttempts() {
        return this.addAttempts;
    }
    
    public Long getAddFailures() {
        return this.addFailures;
    }
    
    public Long getAddSuccesses() {
        return this.addSuccesses;
    }
    
    public Long getBindAttempts() {
        return this.bindAttempts;
    }
    
    public Long getBindFailures() {
        return this.bindFailures;
    }
    
    public Long getBindSuccesses() {
        return this.bindSuccesses;
    }
    
    public Long getCompareAttempts() {
        return this.compareAttempts;
    }
    
    public Long getCompareFailures() {
        return this.compareFailures;
    }
    
    public Long getCompareSuccesses() {
        return this.compareSuccesses;
    }
    
    public Long getDeleteAttempts() {
        return this.deleteAttempts;
    }
    
    public Long getDeleteFailures() {
        return this.deleteFailures;
    }
    
    public Long getDeleteSuccesses() {
        return this.deleteSuccesses;
    }
    
    public Long getModifyAttempts() {
        return this.modifyAttempts;
    }
    
    public Long getModifyFailures() {
        return this.modifyFailures;
    }
    
    public Long getModifySuccesses() {
        return this.modifySuccesses;
    }
    
    public Long getModifyDNAttempts() {
        return this.modifyDNAttempts;
    }
    
    public Long getModifyDNFailures() {
        return this.modifyDNFailures;
    }
    
    public Long getModifyDNSuccesses() {
        return this.modifyDNSuccesses;
    }
    
    public Long getSearchAttempts() {
        return this.searchAttempts;
    }
    
    public Long getSearchFailures() {
        return this.searchFailures;
    }
    
    public Long getSearchSuccesses() {
        return this.searchSuccesses;
    }
    
    public Long getCommonPoolAvailableConnections() {
        return this.commonPoolAvailableConnections;
    }
    
    public Long getCommonPoolMaxAvailableConnections() {
        return this.commonPoolMaxAvailableConnections;
    }
    
    public Long getCommonPoolNumSuccessfulConnectionAttempts() {
        return this.commonPoolNumSuccessfulConnectionAttempts;
    }
    
    public Long getCommonPoolNumFailedConnectionAttempts() {
        return this.commonPoolNumFailedConnectionAttempts;
    }
    
    public Long getCommonPoolNumClosedDefunct() {
        return this.commonPoolNumClosedDefunct;
    }
    
    public Long getCommonPoolNumClosedExpired() {
        return this.commonPoolNumClosedExpired;
    }
    
    public Long getCommonPoolNumClosedUnneeded() {
        return this.commonPoolNumClosedUnneeded;
    }
    
    public Long getCommonPoolTotalSuccessfulCheckouts() {
        return this.commonPoolNumSuccessfulCheckouts;
    }
    
    public Long getCommonPoolNumSuccessfulCheckoutsWithoutWaiting() {
        return this.commonPoolNumSuccessfulCheckoutsWithoutWaiting;
    }
    
    public Long getCommonPoolNumSuccessfulCheckoutsAfterWaiting() {
        return this.commonPoolNumSuccessfulCheckoutsAfterWaiting;
    }
    
    public Long getCommonPoolNumSuccessfulCheckoutsNewConnection() {
        return this.commonPoolNumSuccessfulCheckoutsNewConnection;
    }
    
    public Long getCommonPoolNumFailedCheckouts() {
        return this.commonPoolNumFailedCheckouts;
    }
    
    public Long getCommonPoolNumReleasedValid() {
        return this.commonPoolNumReleasedValid;
    }
    
    public Long getBindPoolAvailableConnections() {
        return this.bindPoolAvailableConnections;
    }
    
    public Long getBindPoolMaxAvailableConnections() {
        return this.bindPoolMaxAvailableConnections;
    }
    
    public Long getBindPoolNumSuccessfulConnectionAttempts() {
        return this.bindPoolNumSuccessfulConnectionAttempts;
    }
    
    public Long getBindPoolNumFailedConnectionAttempts() {
        return this.bindPoolNumFailedConnectionAttempts;
    }
    
    public Long getBindPoolNumClosedDefunct() {
        return this.bindPoolNumClosedDefunct;
    }
    
    public Long getBindPoolNumClosedExpired() {
        return this.bindPoolNumClosedExpired;
    }
    
    public Long getBindPoolNumClosedUnneeded() {
        return this.bindPoolNumClosedUnneeded;
    }
    
    public Long getBindPoolTotalSuccessfulCheckouts() {
        return this.bindPoolNumSuccessfulCheckouts;
    }
    
    public Long getBindPoolNumSuccessfulCheckoutsWithoutWaiting() {
        return this.bindPoolNumSuccessfulCheckoutsWithoutWaiting;
    }
    
    public Long getBindPoolNumSuccessfulCheckoutsAfterWaiting() {
        return this.bindPoolNumSuccessfulCheckoutsAfterWaiting;
    }
    
    public Long getBindPoolNumSuccessfulCheckoutsNewConnection() {
        return this.bindPoolNumSuccessfulCheckoutsNewConnection;
    }
    
    public Long getBindPoolNumFailedCheckouts() {
        return this.bindPoolNumFailedCheckouts;
    }
    
    public Long getBindPoolNumReleasedValid() {
        return this.bindPoolNumReleasedValid;
    }
    
    public Long getNonBindPoolAvailableConnections() {
        return this.nonBindPoolAvailableConnections;
    }
    
    public Long getNonBindPoolMaxAvailableConnections() {
        return this.nonBindPoolMaxAvailableConnections;
    }
    
    public Long getNonBindPoolNumSuccessfulConnectionAttempts() {
        return this.nonBindPoolNumSuccessfulConnectionAttempts;
    }
    
    public Long getNonBindPoolNumFailedConnectionAttempts() {
        return this.nonBindPoolNumFailedConnectionAttempts;
    }
    
    public Long getNonBindPoolNumClosedDefunct() {
        return this.nonBindPoolNumClosedDefunct;
    }
    
    public Long getNonBindPoolNumClosedExpired() {
        return this.nonBindPoolNumClosedExpired;
    }
    
    public Long getNonBindPoolNumClosedUnneeded() {
        return this.nonBindPoolNumClosedUnneeded;
    }
    
    public Long getNonBindPoolTotalSuccessfulCheckouts() {
        return this.nonBindPoolNumSuccessfulCheckouts;
    }
    
    public Long getNonBindPoolNumSuccessfulCheckoutsWithoutWaiting() {
        return this.nonBindPoolNumSuccessfulCheckoutsWithoutWaiting;
    }
    
    public Long getNonBindPoolNumSuccessfulCheckoutsAfterWaiting() {
        return this.nonBindPoolNumSuccessfulCheckoutsAfterWaiting;
    }
    
    public Long getNonBindPoolNumSuccessfulCheckoutsNewConnection() {
        return this.nonBindPoolNumSuccessfulCheckoutsNewConnection;
    }
    
    public Long getNonBindPoolNumFailedCheckouts() {
        return this.nonBindPoolNumFailedCheckouts;
    }
    
    public Long getNonBindPoolNumReleasedValid() {
        return this.nonBindPoolNumReleasedValid;
    }
    
    @Override
    public String getMonitorDisplayName() {
        return MonitorMessages.INFO_LDAP_EXT_SERVER_MONITOR_DISPNAME.get();
    }
    
    @Override
    public String getMonitorDescription() {
        return MonitorMessages.INFO_LDAP_EXT_SERVER_MONITOR_DESC.get();
    }
    
    @Override
    public Map<String, MonitorAttribute> getMonitorAttributes() {
        final LinkedHashMap<String, MonitorAttribute> attrs = new LinkedHashMap<String, MonitorAttribute>(StaticUtils.computeMapCapacity(50));
        if (this.serverAddress != null) {
            MonitorEntry.addMonitorAttribute(attrs, "server-address", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_SERVER_ADDRESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_SERVER_ADDRESS.get(), this.serverAddress);
        }
        if (this.serverPort != null) {
            MonitorEntry.addMonitorAttribute(attrs, "server-port", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_SERVER_PORT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_SERVER_PORT.get(), this.serverPort);
        }
        if (this.communicationSecurity != null) {
            MonitorEntry.addMonitorAttribute(attrs, "communication-security", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMUNICATION_SECURITY.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMUNICATION_SECURITY.get(), this.communicationSecurity);
        }
        if (this.loadBalancingAlgorithmDN != null) {
            MonitorEntry.addMonitorAttribute(attrs, "load-balancing-algorithm", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_LOAD_BALANCING_ALGORITHM_DN.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_LOAD_BALANCING_ALGORITHM_DN.get(), this.loadBalancingAlgorithmDN);
        }
        if (this.healthCheckState != null) {
            MonitorEntry.addMonitorAttribute(attrs, "health-check-state", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_HEALTH_CHECK_STATE.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_HEALTH_CHECK_STATE.get(), this.healthCheckState.getName());
        }
        if (this.healthCheckScore != null) {
            MonitorEntry.addMonitorAttribute(attrs, "health-check-score", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_HEALTH_CHECK_SCORE.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_HEALTH_CHECK_SCORE.get(), this.healthCheckScore);
        }
        if (this.healthCheckMessages != null && !this.healthCheckMessages.isEmpty()) {
            MonitorEntry.addMonitorAttribute(attrs, "health-check-message", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_HEALTH_CHECK_MESSAGE.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_HEALTH_CHECK_MESSAGE.get(), this.healthCheckMessages);
        }
        if (this.healthCheckUpdateTime != null) {
            MonitorEntry.addMonitorAttribute(attrs, "health-check-update-time", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_HEALTH_CHECK_UPDATE_TIME.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_HEALTH_CHECK_UPDATE_TIME.get(), this.healthCheckUpdateTime);
        }
        if (this.commonPoolAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_AVAILABLE_CONNS.get(), this.commonPoolAvailableConnections);
        }
        if (this.commonPoolMaxAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-max-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_MAX_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_MAX_AVAILABLE_CONNS.get(), this.commonPoolMaxAvailableConnections);
        }
        if (this.commonPoolNumSuccessfulConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-successful-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CONNECT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CONNECT_SUCCESS.get(), this.commonPoolNumSuccessfulConnectionAttempts);
        }
        if (this.commonPoolNumFailedConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-failed-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CONNECT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CONNECT_FAILED.get(), this.commonPoolNumFailedConnectionAttempts);
        }
        if (this.commonPoolNumClosedDefunct != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-closed-defunct", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CLOSED_DEFUNCT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CLOSED_DEFUNCT.get(), this.commonPoolNumClosedDefunct);
        }
        if (this.commonPoolNumClosedExpired != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-closed-expired", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CLOSED_EXPIRED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CLOSED_EXPIRED.get(), this.commonPoolNumClosedExpired);
        }
        if (this.commonPoolNumClosedUnneeded != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-closed-unneeded", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CLOSED_UNNEEDED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CLOSED_UNNEEDED.get(), this.commonPoolNumClosedUnneeded);
        }
        if (this.commonPoolNumSuccessfulCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-successful-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CHECKOUT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CHECKOUT_SUCCESS.get(), this.commonPoolNumSuccessfulCheckouts);
        }
        if (this.commonPoolNumSuccessfulCheckoutsWithoutWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-successful-checkouts-without-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CHECKOUT_NO_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CHECKOUT_NO_WAIT.get(), this.commonPoolNumSuccessfulCheckoutsWithoutWaiting);
        }
        if (this.commonPoolNumSuccessfulCheckoutsAfterWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-successful-checkouts-after-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CHECKOUT_WITH_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CHECKOUT_WITH_WAIT.get(), this.commonPoolNumSuccessfulCheckoutsAfterWaiting);
        }
        if (this.commonPoolNumSuccessfulCheckoutsNewConnection != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-successful-checkouts-new-connection", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CHECKOUT_NEW_CONN.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CHECKOUT_NEW_CONN.get(), this.commonPoolNumSuccessfulCheckoutsNewConnection);
        }
        if (this.commonPoolNumFailedCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-failed-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_CHECKOUT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_CHECKOUT_FAILED.get(), this.commonPoolNumFailedCheckouts);
        }
        if (this.commonPoolNumReleasedValid != null) {
            MonitorEntry.addMonitorAttribute(attrs, "common-pool-num-released-valid", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMMON_RELEASED_VALID.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMMON_RELEASED_VALID.get(), this.commonPoolNumReleasedValid);
        }
        if (this.bindPoolAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_AVAILABLE_CONNS.get(), this.bindPoolAvailableConnections);
        }
        if (this.bindPoolMaxAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-max-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_MAX_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_MAX_AVAILABLE_CONNS.get(), this.bindPoolMaxAvailableConnections);
        }
        if (this.bindPoolNumSuccessfulConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-successful-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CONNECT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CONNECT_SUCCESS.get(), this.bindPoolNumSuccessfulConnectionAttempts);
        }
        if (this.bindPoolNumFailedConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-failed-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CONNECT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CONNECT_FAILED.get(), this.bindPoolNumFailedConnectionAttempts);
        }
        if (this.bindPoolNumClosedDefunct != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-closed-defunct", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CLOSED_DEFUNCT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CLOSED_DEFUNCT.get(), this.bindPoolNumClosedDefunct);
        }
        if (this.bindPoolNumClosedExpired != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-closed-expired", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CLOSED_EXPIRED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CLOSED_EXPIRED.get(), this.bindPoolNumClosedExpired);
        }
        if (this.bindPoolNumClosedUnneeded != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-closed-unneeded", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CLOSED_UNNEEDED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CLOSED_UNNEEDED.get(), this.bindPoolNumClosedUnneeded);
        }
        if (this.bindPoolNumSuccessfulCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-successful-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CHECKOUT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CHECKOUT_SUCCESS.get(), this.bindPoolNumSuccessfulCheckouts);
        }
        if (this.bindPoolNumSuccessfulCheckoutsWithoutWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-successful-checkouts-without-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CHECKOUT_NO_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CHECKOUT_NO_WAIT.get(), this.bindPoolNumSuccessfulCheckoutsWithoutWaiting);
        }
        if (this.bindPoolNumSuccessfulCheckoutsAfterWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-successful-checkouts-after-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CHECKOUT_WITH_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CHECKOUT_WITH_WAIT.get(), this.bindPoolNumSuccessfulCheckoutsAfterWaiting);
        }
        if (this.bindPoolNumSuccessfulCheckoutsNewConnection != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-successful-checkouts-new-connection", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CHECKOUT_NEW_CONN.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CHECKOUT_NEW_CONN.get(), this.bindPoolNumSuccessfulCheckoutsNewConnection);
        }
        if (this.bindPoolNumFailedCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-failed-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_CHECKOUT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_CHECKOUT_FAILED.get(), this.bindPoolNumFailedCheckouts);
        }
        if (this.bindPoolNumReleasedValid != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-pool-num-released-valid", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_RELEASED_VALID.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_RELEASED_VALID.get(), this.bindPoolNumReleasedValid);
        }
        if (this.nonBindPoolAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_AVAILABLE_CONNS.get(), this.nonBindPoolAvailableConnections);
        }
        if (this.nonBindPoolMaxAvailableConnections != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-max-available-connections", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_MAX_AVAILABLE_CONNS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_MAX_AVAILABLE_CONNS.get(), this.nonBindPoolMaxAvailableConnections);
        }
        if (this.nonBindPoolNumSuccessfulConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-successful-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CONNECT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CONNECT_SUCCESS.get(), this.nonBindPoolNumSuccessfulConnectionAttempts);
        }
        if (this.nonBindPoolNumFailedConnectionAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-failed-connection-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CONNECT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CONNECT_FAILED.get(), this.nonBindPoolNumFailedConnectionAttempts);
        }
        if (this.nonBindPoolNumClosedDefunct != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-closed-defunct", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CLOSED_DEFUNCT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CLOSED_DEFUNCT.get(), this.nonBindPoolNumClosedDefunct);
        }
        if (this.nonBindPoolNumClosedExpired != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-closed-expired", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CLOSED_EXPIRED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CLOSED_EXPIRED.get(), this.nonBindPoolNumClosedExpired);
        }
        if (this.nonBindPoolNumClosedUnneeded != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-closed-unneeded", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CLOSED_UNNEEDED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CLOSED_UNNEEDED.get(), this.nonBindPoolNumClosedUnneeded);
        }
        if (this.nonBindPoolNumSuccessfulCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-successful-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CHECKOUT_SUCCESS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CHECKOUT_SUCCESS.get(), this.nonBindPoolNumSuccessfulCheckouts);
        }
        if (this.nonBindPoolNumSuccessfulCheckoutsWithoutWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-successful-checkouts-without-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CHECKOUT_NO_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CHECKOUT_NO_WAIT.get(), this.nonBindPoolNumSuccessfulCheckoutsWithoutWaiting);
        }
        if (this.nonBindPoolNumSuccessfulCheckoutsAfterWaiting != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-successful-checkouts-after-waiting", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CHECKOUT_WITH_WAIT.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CHECKOUT_WITH_WAIT.get(), this.nonBindPoolNumSuccessfulCheckoutsAfterWaiting);
        }
        if (this.nonBindPoolNumSuccessfulCheckoutsNewConnection != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-successful-checkouts-new-connection", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CHECKOUT_NEW_CONN.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CHECKOUT_NEW_CONN.get(), this.nonBindPoolNumSuccessfulCheckoutsNewConnection);
        }
        if (this.nonBindPoolNumFailedCheckouts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-failed-checkouts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_CHECKOUT_FAILED.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_CHECKOUT_FAILED.get(), this.nonBindPoolNumFailedCheckouts);
        }
        if (this.nonBindPoolNumReleasedValid != null) {
            MonitorEntry.addMonitorAttribute(attrs, "non-bind-pool-num-released-valid", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_NONBIND_RELEASED_VALID.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_NONBIND_RELEASED_VALID.get(), this.nonBindPoolNumReleasedValid);
        }
        if (this.addAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "add-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_ADD_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_ADD_ATTEMPTS.get(), this.addAttempts);
        }
        if (this.addFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "add-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_ADD_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_ADD_FAILURES.get(), this.addFailures);
        }
        if (this.addSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "add-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_ADD_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_ADD_SUCCESSES.get(), this.addSuccesses);
        }
        if (this.bindAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_ATTEMPTS.get(), this.bindAttempts);
        }
        if (this.bindFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_FAILURES.get(), this.bindFailures);
        }
        if (this.bindSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "bind-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_BIND_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_BIND_SUCCESSES.get(), this.bindSuccesses);
        }
        if (this.compareAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compare-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMPARE_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMPARE_ATTEMPTS.get(), this.compareAttempts);
        }
        if (this.compareFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compare-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMPARE_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMPARE_FAILURES.get(), this.compareFailures);
        }
        if (this.compareSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "compare-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_COMPARE_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_COMPARE_SUCCESSES.get(), this.compareSuccesses);
        }
        if (this.deleteAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "delete-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_DELETE_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_DELETE_ATTEMPTS.get(), this.deleteAttempts);
        }
        if (this.deleteFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "delete-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_DELETE_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_DELETE_FAILURES.get(), this.deleteFailures);
        }
        if (this.deleteSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "delete-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_DELETE_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_DELETE_SUCCESSES.get(), this.deleteSuccesses);
        }
        if (this.modifyAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_ATTEMPTS.get(), this.modifyAttempts);
        }
        if (this.modifyFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_FAILURES.get(), this.modifyFailures);
        }
        if (this.modifySuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_SUCCESSES.get(), this.modifySuccesses);
        }
        if (this.modifyDNAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-dn-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_DN_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_DN_ATTEMPTS.get(), this.modifyDNAttempts);
        }
        if (this.modifyDNFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-dn-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_DN_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_DN_FAILURES.get(), this.modifyDNFailures);
        }
        if (this.modifyDNSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "modify-dn-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_MODIFY_DN_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_MODIFY_DN_SUCCESSES.get(), this.modifyDNSuccesses);
        }
        if (this.searchAttempts != null) {
            MonitorEntry.addMonitorAttribute(attrs, "search-attempts", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_SEARCH_ATTEMPTS.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_SEARCH_ATTEMPTS.get(), this.searchAttempts);
        }
        if (this.searchFailures != null) {
            MonitorEntry.addMonitorAttribute(attrs, "search-failures", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_SEARCH_FAILURES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_SEARCH_FAILURES.get(), this.searchFailures);
        }
        if (this.searchSuccesses != null) {
            MonitorEntry.addMonitorAttribute(attrs, "search-successes", MonitorMessages.INFO_LDAP_EXT_SERVER_DISPNAME_SEARCH_SUCCESSES.get(), MonitorMessages.INFO_LDAP_EXT_SERVER_DESC_SEARCH_SUCCESSES.get(), this.searchSuccesses);
        }
        return Collections.unmodifiableMap((Map<? extends String, ? extends MonitorAttribute>)attrs);
    }
}
