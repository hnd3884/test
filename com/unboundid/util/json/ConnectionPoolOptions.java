package com.unboundid.util.json;

import com.unboundid.ldap.sdk.LDAPConnectionPool;
import java.util.Iterator;
import java.util.Collections;
import com.unboundid.util.StaticUtils;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import java.util.Collection;
import java.util.EnumSet;
import com.unboundid.ldap.sdk.OperationType;
import java.util.Set;
import com.unboundid.ldap.sdk.GetEntryLDAPConnectionPoolHealthCheck;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
final class ConnectionPoolOptions
{
    private static final String FIELD_CREATE_IF_NECESSARY = "create-if-necessary";
    private static final String FIELD_HEALTH_CHECK_GET_ENTRY_DN = "health-check-get-entry-dn";
    private static final String FIELD_HEALTH_CHECK_GET_ENTRY_TIMEOUT_MILLIS = "health-check-get-entry-maximum-response-time-millis";
    private static final String FIELD_HEALTH_CHECK_INTERVAL_MILLIS = "health-check-interval-millis";
    private static final String FIELD_INITIAL_CONNECT_THREADS = "initial-connect-threads";
    private static final String FIELD_INVOKE_AUTHENTICATION_HEALTH_CHECKS = "invoke-authentication-health-checks";
    private static final String FIELD_INVOKE_BACKGROUND_HEALTH_CHECKS = "invoke-background-health-checks";
    private static final String FIELD_INVOKE_CHECKOUT_HEALTH_CHECKS = "invoke-checkout-health-checks";
    private static final String FIELD_INVOKE_CREATE_HEALTH_CHECKS = "invoke-create-health-checks";
    private static final String FIELD_INVOKE_EXCEPTION_HEALTH_CHECKS = "invoke-exception-health-checks";
    private static final String FIELD_INVOKE_RELEASE_HEALTH_CHECKS = "invoke-release-health-checks";
    private static final String FIELD_MAX_CONNECTION_AGE_MILLIS = "maximum-connection-age-millis";
    private static final String FIELD_MAX_DEFUNCT_REPLACEMENT_CONNECTION_AGE_MILLIS = "maximum-defunct-replacement-connection-age-millis";
    private static final String FIELD_MAX_WAIT_TIME_MILLIS = "maximum-wait-time-millis";
    private static final String FIELD_RETRY_FAILED_OPS = "retry-failed-operations-due-to-invalid-connections";
    private final boolean createIfNecessary;
    private final GetEntryLDAPConnectionPoolHealthCheck healthCheck;
    private final int initialConnectThreads;
    private final long healthCheckIntervalMillis;
    private final long maxConnectionAgeMillis;
    private final long maxWaitTimeMillis;
    private final Long maxDefunctReplacementConnectionAgeMillis;
    private final Set<OperationType> retryOperationTypes;
    
    ConnectionPoolOptions(final JSONObject connectionDetailsObject) throws LDAPException {
        boolean create = true;
        boolean invokeAuthentication = false;
        boolean invokeBackground = true;
        boolean invokeCheckout = false;
        boolean invokeCreate = false;
        boolean invokeException = true;
        boolean invokeRelease = false;
        int initialThreads = 1;
        long getEntryTimeout = 10000L;
        long healthCheckInterval = 60000L;
        long maxConnectionAge = 0L;
        long maxWaitTime = 0L;
        Long maxDefunctReplacementAge = null;
        String getDN = null;
        final Set<OperationType> retryTypes = EnumSet.noneOf(OperationType.class);
        final JSONObject o = LDAPConnectionDetailsJSONSpecification.getObject(connectionDetailsObject, "connection-pool-options");
        if (o != null) {
            LDAPConnectionDetailsJSONSpecification.validateAllowedFields(o, "connection-pool-options", "create-if-necessary", "health-check-get-entry-dn", "health-check-get-entry-maximum-response-time-millis", "health-check-interval-millis", "initial-connect-threads", "invoke-authentication-health-checks", "invoke-background-health-checks", "invoke-checkout-health-checks", "invoke-create-health-checks", "invoke-exception-health-checks", "invoke-release-health-checks", "maximum-connection-age-millis", "maximum-defunct-replacement-connection-age-millis", "maximum-wait-time-millis", "retry-failed-operations-due-to-invalid-connections");
            create = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "create-if-necessary", create);
            invokeAuthentication = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-authentication-health-checks", invokeAuthentication);
            invokeBackground = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-background-health-checks", invokeBackground);
            invokeCheckout = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-checkout-health-checks", invokeCheckout);
            invokeCreate = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-create-health-checks", invokeCreate);
            invokeException = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-exception-health-checks", invokeException);
            invokeRelease = LDAPConnectionDetailsJSONSpecification.getBoolean(o, "invoke-release-health-checks", invokeRelease);
            initialThreads = LDAPConnectionDetailsJSONSpecification.getInt(o, "initial-connect-threads", initialThreads, 1, null);
            getEntryTimeout = LDAPConnectionDetailsJSONSpecification.getLong(o, "health-check-get-entry-maximum-response-time-millis", getEntryTimeout, 1L, null);
            healthCheckInterval = LDAPConnectionDetailsJSONSpecification.getLong(o, "health-check-interval-millis", healthCheckInterval, 1L, null);
            maxConnectionAge = LDAPConnectionDetailsJSONSpecification.getLong(o, "maximum-connection-age-millis", maxConnectionAge, 0L, null);
            maxWaitTime = LDAPConnectionDetailsJSONSpecification.getLong(o, "maximum-wait-time-millis", maxWaitTime, 0L, null);
            maxDefunctReplacementAge = LDAPConnectionDetailsJSONSpecification.getLong(o, "maximum-defunct-replacement-connection-age-millis", maxDefunctReplacementAge, 0L, null);
            getDN = LDAPConnectionDetailsJSONSpecification.getString(o, "health-check-get-entry-dn", getDN);
            final JSONValue retryTypesValue = o.getField("retry-failed-operations-due-to-invalid-connections");
            if (retryTypesValue != null) {
                if (retryTypesValue instanceof JSONBoolean) {
                    if (((JSONBoolean)retryTypesValue).booleanValue()) {
                        retryTypes.addAll(EnumSet.allOf(OperationType.class));
                    }
                }
                else {
                    if (!(retryTypesValue instanceof JSONArray)) {
                        throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_POOL_OPTIONS_INVALID_RETRY_TYPES.get("retry-failed-operations-due-to-invalid-connections"));
                    }
                    for (final JSONValue v : ((JSONArray)retryTypesValue).getValues()) {
                        if (!(v instanceof JSONString)) {
                            throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_POOL_OPTIONS_INVALID_RETRY_TYPES.get("retry-failed-operations-due-to-invalid-connections"));
                        }
                        final String s = StaticUtils.toLowerCase(((JSONString)v).stringValue());
                        if (s.equals("add")) {
                            retryTypes.add(OperationType.ADD);
                        }
                        else if (s.equals("bind")) {
                            retryTypes.add(OperationType.BIND);
                        }
                        else if (s.equals("compare")) {
                            retryTypes.add(OperationType.COMPARE);
                        }
                        else if (s.equals("delete")) {
                            retryTypes.add(OperationType.DELETE);
                        }
                        else if (s.equals("extended")) {
                            retryTypes.add(OperationType.EXTENDED);
                        }
                        else if (s.equals("modify")) {
                            retryTypes.add(OperationType.MODIFY);
                        }
                        else if (s.equals("modify-dn")) {
                            retryTypes.add(OperationType.MODIFY_DN);
                        }
                        else {
                            if (!s.equals("search")) {
                                throw new LDAPException(ResultCode.PARAM_ERROR, JSONMessages.ERR_POOL_OPTIONS_INVALID_RETRY_TYPES.get("retry-failed-operations-due-to-invalid-connections"));
                            }
                            retryTypes.add(OperationType.SEARCH);
                        }
                    }
                }
            }
        }
        this.createIfNecessary = create;
        this.initialConnectThreads = initialThreads;
        this.healthCheckIntervalMillis = healthCheckInterval;
        this.maxConnectionAgeMillis = maxConnectionAge;
        this.maxDefunctReplacementConnectionAgeMillis = maxDefunctReplacementAge;
        this.maxWaitTimeMillis = maxWaitTime;
        this.retryOperationTypes = Collections.unmodifiableSet((Set<? extends OperationType>)retryTypes);
        if (getDN == null) {
            this.healthCheck = null;
        }
        else {
            this.healthCheck = new GetEntryLDAPConnectionPoolHealthCheck(getDN, getEntryTimeout, invokeCreate, invokeAuthentication, invokeCheckout, invokeRelease, invokeBackground, invokeException);
        }
    }
    
    int getInitialConnectThreads() {
        return this.initialConnectThreads;
    }
    
    GetEntryLDAPConnectionPoolHealthCheck getHealthCheck() {
        return this.healthCheck;
    }
    
    void applyConnectionPoolSettings(final LDAPConnectionPool pool) {
        pool.setCreateIfNecessary(this.createIfNecessary);
        pool.setHealthCheckIntervalMillis(this.healthCheckIntervalMillis);
        pool.setMaxConnectionAgeMillis(this.maxConnectionAgeMillis);
        pool.setMaxDefunctReplacementConnectionAgeMillis(this.maxDefunctReplacementConnectionAgeMillis);
        pool.setMaxWaitTimeMillis(this.maxWaitTimeMillis);
        pool.setRetryFailedOperationsDueToInvalidConnections(this.retryOperationTypes);
    }
}
