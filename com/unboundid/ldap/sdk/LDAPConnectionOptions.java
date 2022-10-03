package com.unboundid.ldap.sdk;

import java.net.InetAddress;
import java.lang.reflect.Method;
import com.unboundid.util.ssl.TrustAllSSLSocketVerifier;
import java.util.Arrays;
import java.util.Iterator;
import com.unboundid.util.DebugType;
import java.util.logging.Level;
import com.unboundid.util.Debug;
import com.unboundid.util.StaticUtils;
import com.unboundid.util.Validator;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Collections;
import com.unboundid.util.ssl.SSLSocketVerifier;
import java.util.Map;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.Mutable;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class LDAPConnectionOptions
{
    private static final String PROPERTY_PREFIX;
    public static final String PROPERTY_DEFAULT_ABANDON_ON_TIMEOUT;
    private static final boolean DEFAULT_ABANDON_ON_TIMEOUT;
    private static final boolean DEFAULT_AUTO_RECONNECT = false;
    public static final String PROPERTY_DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD;
    private static final boolean DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD;
    public static final String PROPERTY_DEFAULT_CAPTURE_CONNECT_STACK_TRACE;
    private static final boolean DEFAULT_CAPTURE_CONNECT_STACK_TRACE;
    public static final String PROPERTY_DEFAULT_FOLLOW_REFERRALS;
    private static final boolean DEFAULT_FOLLOW_REFERRALS;
    public static final String PROPERTY_DEFAULT_REFERRAL_HOP_LIMIT;
    private static final int DEFAULT_REFERRAL_HOP_LIMIT;
    public static final String PROPERTY_DEFAULT_USE_SCHEMA;
    private static final boolean DEFAULT_USE_SCHEMA;
    public static final String PROPERTY_DEFAULT_USE_POOLED_SCHEMA;
    private static final boolean DEFAULT_USE_POOLED_SCHEMA;
    public static final String PROPERTY_DEFAULT_POOLED_SCHEMA_TIMEOUT_MILLIS;
    private static final long DEFAULT_POOLED_SCHEMA_TIMEOUT_MILLIS = 3600000L;
    public static final String PROPERTY_DEFAULT_USE_KEEPALIVE;
    private static final boolean DEFAULT_USE_KEEPALIVE;
    public static final String PROPERTY_DEFAULT_USE_LINGER;
    private static final boolean DEFAULT_USE_LINGER;
    public static final String PROPERTY_DEFAULT_LINGER_TIMEOUT_SECONDS;
    private static final int DEFAULT_LINGER_TIMEOUT_SECONDS;
    public static final String PROPERTY_DEFAULT_USE_REUSE_ADDRESS;
    private static final boolean DEFAULT_USE_REUSE_ADDRESS;
    public static final String PROPERTY_DEFAULT_USE_SYNCHRONOUS_MODE;
    private static final boolean DEFAULT_USE_SYNCHRONOUS_MODE;
    public static final String PROPERTY_DEFAULT_USE_TCP_NODELAY;
    private static final boolean DEFAULT_USE_TCP_NODELAY;
    public static final String PROPERTY_DEFAULT_CONNECT_TIMEOUT_MILLIS;
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_MAX_MESSAGE_SIZE_BYTES;
    private static final int DEFAULT_MAX_MESSAGE_SIZE_BYTES;
    public static final String PROPERTY_DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;
    private static final int DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;
    public static final String PROPERTY_DEFAULT_SEND_BUFFER_SIZE_BYTES;
    private static final int DEFAULT_SEND_BUFFER_SIZE_BYTES;
    public static final String PROPERTY_DEFAULT_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_ADD_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_BIND_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_COMPARE_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_DELETE_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_EXTENDED_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_MODIFY_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_MODIFY_DN_RESPONSE_TIMEOUT_MILLIS;
    public static final String PROPERTY_DEFAULT_SEARCH_RESPONSE_TIMEOUT_MILLIS;
    private static final long DEFAULT_RESPONSE_TIMEOUT_MILLIS;
    private static final Map<OperationType, Long> DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_OPERATION_TYPE;
    private static final Map<String, Long> DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_EXTENDED_OPERATION_TYPE;
    public static final NameResolver DEFAULT_NAME_RESOLVER;
    public static final String PROPERTY_DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE;
    private static final boolean DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE;
    private static final SSLSocketVerifier DEFAULT_SSL_SOCKET_VERIFIER;
    private boolean abandonOnTimeout;
    private boolean allowConcurrentSocketFactoryUse;
    private boolean autoReconnect;
    private boolean bindWithDNRequiresPassword;
    private boolean captureConnectStackTrace;
    private boolean followReferrals;
    private boolean useKeepAlive;
    private boolean useLinger;
    private boolean useReuseAddress;
    private boolean usePooledSchema;
    private boolean useSchema;
    private boolean useSynchronousMode;
    private boolean useTCPNoDelay;
    private DisconnectHandler disconnectHandler;
    private int connectTimeoutMillis;
    private int lingerTimeoutSeconds;
    private int maxMessageSizeBytes;
    private int receiveBufferSizeBytes;
    private int referralHopLimit;
    private int sendBufferSizeBytes;
    private long pooledSchemaTimeoutMillis;
    private long responseTimeoutMillis;
    private Map<OperationType, Long> responseTimeoutMillisByOperationType;
    private Map<String, Long> responseTimeoutMillisByExtendedOperationType;
    private NameResolver nameResolver;
    private ReferralConnector referralConnector;
    private SSLSocketVerifier sslSocketVerifier;
    private UnsolicitedNotificationHandler unsolicitedNotificationHandler;
    
    public LDAPConnectionOptions() {
        this.abandonOnTimeout = LDAPConnectionOptions.DEFAULT_ABANDON_ON_TIMEOUT;
        this.autoReconnect = false;
        this.bindWithDNRequiresPassword = LDAPConnectionOptions.DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD;
        this.captureConnectStackTrace = LDAPConnectionOptions.DEFAULT_CAPTURE_CONNECT_STACK_TRACE;
        this.followReferrals = LDAPConnectionOptions.DEFAULT_FOLLOW_REFERRALS;
        this.nameResolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        this.useKeepAlive = LDAPConnectionOptions.DEFAULT_USE_KEEPALIVE;
        this.useLinger = LDAPConnectionOptions.DEFAULT_USE_LINGER;
        this.useReuseAddress = LDAPConnectionOptions.DEFAULT_USE_REUSE_ADDRESS;
        this.usePooledSchema = LDAPConnectionOptions.DEFAULT_USE_POOLED_SCHEMA;
        this.useSchema = LDAPConnectionOptions.DEFAULT_USE_SCHEMA;
        this.useSynchronousMode = LDAPConnectionOptions.DEFAULT_USE_SYNCHRONOUS_MODE;
        this.useTCPNoDelay = LDAPConnectionOptions.DEFAULT_USE_TCP_NODELAY;
        this.connectTimeoutMillis = LDAPConnectionOptions.DEFAULT_CONNECT_TIMEOUT_MILLIS;
        this.lingerTimeoutSeconds = LDAPConnectionOptions.DEFAULT_LINGER_TIMEOUT_SECONDS;
        this.maxMessageSizeBytes = LDAPConnectionOptions.DEFAULT_MAX_MESSAGE_SIZE_BYTES;
        this.referralHopLimit = LDAPConnectionOptions.DEFAULT_REFERRAL_HOP_LIMIT;
        this.pooledSchemaTimeoutMillis = 3600000L;
        this.responseTimeoutMillis = LDAPConnectionOptions.DEFAULT_RESPONSE_TIMEOUT_MILLIS;
        this.receiveBufferSizeBytes = LDAPConnectionOptions.DEFAULT_RECEIVE_BUFFER_SIZE_BYTES;
        this.sendBufferSizeBytes = LDAPConnectionOptions.DEFAULT_SEND_BUFFER_SIZE_BYTES;
        this.disconnectHandler = null;
        this.referralConnector = null;
        this.sslSocketVerifier = LDAPConnectionOptions.DEFAULT_SSL_SOCKET_VERIFIER;
        this.unsolicitedNotificationHandler = null;
        this.responseTimeoutMillisByOperationType = LDAPConnectionOptions.DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_OPERATION_TYPE;
        this.responseTimeoutMillisByExtendedOperationType = LDAPConnectionOptions.DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_EXTENDED_OPERATION_TYPE;
        this.allowConcurrentSocketFactoryUse = LDAPConnectionOptions.DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE;
    }
    
    public LDAPConnectionOptions duplicate() {
        final LDAPConnectionOptions o = new LDAPConnectionOptions();
        o.abandonOnTimeout = this.abandonOnTimeout;
        o.allowConcurrentSocketFactoryUse = this.allowConcurrentSocketFactoryUse;
        o.autoReconnect = this.autoReconnect;
        o.bindWithDNRequiresPassword = this.bindWithDNRequiresPassword;
        o.captureConnectStackTrace = this.captureConnectStackTrace;
        o.followReferrals = this.followReferrals;
        o.nameResolver = this.nameResolver;
        o.useKeepAlive = this.useKeepAlive;
        o.useLinger = this.useLinger;
        o.useReuseAddress = this.useReuseAddress;
        o.usePooledSchema = this.usePooledSchema;
        o.useSchema = this.useSchema;
        o.useSynchronousMode = this.useSynchronousMode;
        o.useTCPNoDelay = this.useTCPNoDelay;
        o.connectTimeoutMillis = this.connectTimeoutMillis;
        o.lingerTimeoutSeconds = this.lingerTimeoutSeconds;
        o.maxMessageSizeBytes = this.maxMessageSizeBytes;
        o.pooledSchemaTimeoutMillis = this.pooledSchemaTimeoutMillis;
        o.responseTimeoutMillis = this.responseTimeoutMillis;
        o.referralConnector = this.referralConnector;
        o.referralHopLimit = this.referralHopLimit;
        o.disconnectHandler = this.disconnectHandler;
        o.unsolicitedNotificationHandler = this.unsolicitedNotificationHandler;
        o.receiveBufferSizeBytes = this.receiveBufferSizeBytes;
        o.sendBufferSizeBytes = this.sendBufferSizeBytes;
        o.sslSocketVerifier = this.sslSocketVerifier;
        o.responseTimeoutMillisByOperationType = this.responseTimeoutMillisByOperationType;
        o.responseTimeoutMillisByExtendedOperationType = this.responseTimeoutMillisByExtendedOperationType;
        return o;
    }
    
    @Deprecated
    public boolean autoReconnect() {
        return this.autoReconnect;
    }
    
    @Deprecated
    public void setAutoReconnect(final boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }
    
    public NameResolver getNameResolver() {
        return this.nameResolver;
    }
    
    public void setNameResolver(final NameResolver nameResolver) {
        if (nameResolver == null) {
            this.nameResolver = LDAPConnectionOptions.DEFAULT_NAME_RESOLVER;
        }
        else {
            this.nameResolver = nameResolver;
        }
    }
    
    public boolean bindWithDNRequiresPassword() {
        return this.bindWithDNRequiresPassword;
    }
    
    public void setBindWithDNRequiresPassword(final boolean bindWithDNRequiresPassword) {
        this.bindWithDNRequiresPassword = bindWithDNRequiresPassword;
    }
    
    public boolean captureConnectStackTrace() {
        return this.captureConnectStackTrace;
    }
    
    public void setCaptureConnectStackTrace(final boolean captureConnectStackTrace) {
        this.captureConnectStackTrace = captureConnectStackTrace;
    }
    
    public int getConnectTimeoutMillis() {
        return this.connectTimeoutMillis;
    }
    
    public void setConnectTimeoutMillis(final int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }
    
    public long getResponseTimeoutMillis() {
        return this.responseTimeoutMillis;
    }
    
    public void setResponseTimeoutMillis(final long responseTimeoutMillis) {
        this.responseTimeoutMillis = Math.max(0L, responseTimeoutMillis);
        this.responseTimeoutMillisByExtendedOperationType = Collections.emptyMap();
        final EnumMap<OperationType, Long> newOperationTimeouts = new EnumMap<OperationType, Long>(OperationType.class);
        for (final OperationType t : OperationType.values()) {
            newOperationTimeouts.put(t, this.responseTimeoutMillis);
        }
        this.responseTimeoutMillisByOperationType = Collections.unmodifiableMap((Map<? extends OperationType, ? extends Long>)newOperationTimeouts);
    }
    
    public long getResponseTimeoutMillis(final OperationType operationType) {
        return this.responseTimeoutMillisByOperationType.get(operationType);
    }
    
    public void setResponseTimeoutMillis(final OperationType operationType, final long responseTimeoutMillis) {
        final EnumMap<OperationType, Long> newOperationTimeouts = new EnumMap<OperationType, Long>(OperationType.class);
        newOperationTimeouts.putAll(this.responseTimeoutMillisByOperationType);
        newOperationTimeouts.put(operationType, Math.max(0L, responseTimeoutMillis));
        this.responseTimeoutMillisByOperationType = Collections.unmodifiableMap((Map<? extends OperationType, ? extends Long>)newOperationTimeouts);
    }
    
    public long getExtendedOperationResponseTimeoutMillis(final String requestOID) {
        final Long timeout = this.responseTimeoutMillisByExtendedOperationType.get(requestOID);
        if (timeout == null) {
            return this.responseTimeoutMillisByOperationType.get(OperationType.EXTENDED);
        }
        return timeout;
    }
    
    public void setExtendedOperationResponseTimeoutMillis(final String requestOID, final long responseTimeoutMillis) {
        final HashMap<String, Long> newExtOpTimeouts = new HashMap<String, Long>(this.responseTimeoutMillisByExtendedOperationType);
        newExtOpTimeouts.put(requestOID, responseTimeoutMillis);
        this.responseTimeoutMillisByExtendedOperationType = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)newExtOpTimeouts);
    }
    
    public boolean abandonOnTimeout() {
        return this.abandonOnTimeout;
    }
    
    public void setAbandonOnTimeout(final boolean abandonOnTimeout) {
        this.abandonOnTimeout = abandonOnTimeout;
    }
    
    public boolean useKeepAlive() {
        return this.useKeepAlive;
    }
    
    public void setUseKeepAlive(final boolean useKeepAlive) {
        this.useKeepAlive = useKeepAlive;
    }
    
    public boolean useLinger() {
        return this.useLinger;
    }
    
    public int getLingerTimeoutSeconds() {
        return this.lingerTimeoutSeconds;
    }
    
    public void setUseLinger(final boolean useLinger, final int lingerTimeoutSeconds) {
        this.useLinger = useLinger;
        this.lingerTimeoutSeconds = lingerTimeoutSeconds;
    }
    
    public boolean useReuseAddress() {
        return this.useReuseAddress;
    }
    
    public void setUseReuseAddress(final boolean useReuseAddress) {
        this.useReuseAddress = useReuseAddress;
    }
    
    public boolean useSchema() {
        return this.useSchema;
    }
    
    public void setUseSchema(final boolean useSchema) {
        this.useSchema = useSchema;
        if (useSchema) {
            this.usePooledSchema = false;
        }
    }
    
    public boolean usePooledSchema() {
        return this.usePooledSchema;
    }
    
    public void setUsePooledSchema(final boolean usePooledSchema) {
        this.usePooledSchema = usePooledSchema;
        if (usePooledSchema) {
            this.useSchema = false;
        }
    }
    
    public long getPooledSchemaTimeoutMillis() {
        return this.pooledSchemaTimeoutMillis;
    }
    
    public void setPooledSchemaTimeoutMillis(final long pooledSchemaTimeoutMillis) {
        this.pooledSchemaTimeoutMillis = Math.max(0L, pooledSchemaTimeoutMillis);
    }
    
    public boolean useSynchronousMode() {
        return this.useSynchronousMode;
    }
    
    public void setUseSynchronousMode(final boolean useSynchronousMode) {
        this.useSynchronousMode = useSynchronousMode;
    }
    
    public boolean useTCPNoDelay() {
        return this.useTCPNoDelay;
    }
    
    public void setUseTCPNoDelay(final boolean useTCPNoDelay) {
        this.useTCPNoDelay = useTCPNoDelay;
    }
    
    public boolean followReferrals() {
        return this.followReferrals;
    }
    
    public void setFollowReferrals(final boolean followReferrals) {
        this.followReferrals = followReferrals;
    }
    
    public int getReferralHopLimit() {
        return this.referralHopLimit;
    }
    
    public void setReferralHopLimit(final int referralHopLimit) {
        Validator.ensureTrue(referralHopLimit > 0, "LDAPConnectionOptions.referralHopLimit must be greater than 0.");
        this.referralHopLimit = referralHopLimit;
    }
    
    public ReferralConnector getReferralConnector() {
        return this.referralConnector;
    }
    
    public void setReferralConnector(final ReferralConnector referralConnector) {
        this.referralConnector = referralConnector;
    }
    
    public int getMaxMessageSize() {
        return this.maxMessageSizeBytes;
    }
    
    public void setMaxMessageSize(final int maxMessageSizeBytes) {
        this.maxMessageSizeBytes = Math.max(0, maxMessageSizeBytes);
    }
    
    public DisconnectHandler getDisconnectHandler() {
        return this.disconnectHandler;
    }
    
    public void setDisconnectHandler(final DisconnectHandler handler) {
        this.disconnectHandler = handler;
    }
    
    public UnsolicitedNotificationHandler getUnsolicitedNotificationHandler() {
        return this.unsolicitedNotificationHandler;
    }
    
    public void setUnsolicitedNotificationHandler(final UnsolicitedNotificationHandler handler) {
        this.unsolicitedNotificationHandler = handler;
    }
    
    public int getReceiveBufferSize() {
        return this.receiveBufferSizeBytes;
    }
    
    public void setReceiveBufferSize(final int receiveBufferSizeBytes) {
        this.receiveBufferSizeBytes = Math.max(0, receiveBufferSizeBytes);
    }
    
    public int getSendBufferSize() {
        return this.sendBufferSizeBytes;
    }
    
    public void setSendBufferSize(final int sendBufferSizeBytes) {
        this.sendBufferSizeBytes = Math.max(0, sendBufferSizeBytes);
    }
    
    public boolean allowConcurrentSocketFactoryUse() {
        return this.allowConcurrentSocketFactoryUse;
    }
    
    public void setAllowConcurrentSocketFactoryUse(final boolean allowConcurrentSocketFactoryUse) {
        this.allowConcurrentSocketFactoryUse = allowConcurrentSocketFactoryUse;
    }
    
    public SSLSocketVerifier getSSLSocketVerifier() {
        return this.sslSocketVerifier;
    }
    
    public void setSSLSocketVerifier(final SSLSocketVerifier sslSocketVerifier) {
        if (sslSocketVerifier == null) {
            this.sslSocketVerifier = LDAPConnectionOptions.DEFAULT_SSL_SOCKET_VERIFIER;
        }
        else {
            this.sslSocketVerifier = sslSocketVerifier;
        }
    }
    
    static boolean getSystemProperty(final String propertyName, final boolean defaultValue) {
        final String propertyValue = StaticUtils.getSystemProperty(propertyName);
        if (propertyValue == null) {
            if (Debug.debugEnabled()) {
                Debug.debug(Level.FINE, DebugType.OTHER, "Using the default value of " + defaultValue + " for system " + "property '" + propertyName + "' that is not set.");
            }
            return defaultValue;
        }
        if (propertyValue.equalsIgnoreCase("true")) {
            if (Debug.debugEnabled()) {
                Debug.debug(Level.INFO, DebugType.OTHER, "Using value '" + propertyValue + "' set for system property '" + propertyName + "'.");
            }
            return true;
        }
        if (propertyValue.equalsIgnoreCase("false")) {
            if (Debug.debugEnabled()) {
                Debug.debug(Level.INFO, DebugType.OTHER, "Using value '" + propertyValue + "' set for system property '" + propertyName + "'.");
            }
            return false;
        }
        if (Debug.debugEnabled()) {
            Debug.debug(Level.WARNING, DebugType.OTHER, "Invalid value '" + propertyValue + "' set for system property '" + propertyName + "'.  The value was expected to be either " + "'true' or 'false'.  The default value of " + defaultValue + " will be used instead of the configured value.");
        }
        return defaultValue;
    }
    
    static int getSystemProperty(final String propertyName, final int defaultValue) {
        final String propertyValueString = StaticUtils.getSystemProperty(propertyName);
        if (propertyValueString == null) {
            if (Debug.debugEnabled()) {
                Debug.debug(Level.FINE, DebugType.OTHER, "Using the default value of " + defaultValue + " for system " + "property '" + propertyName + "' that is not set.");
            }
            return defaultValue;
        }
        try {
            final int propertyValueInt = Integer.parseInt(propertyValueString);
            if (Debug.debugEnabled()) {
                Debug.debug(Level.INFO, DebugType.OTHER, "Using value " + propertyValueInt + " set for system property '" + propertyName + "'.");
            }
            return propertyValueInt;
        }
        catch (final Exception e) {
            if (Debug.debugEnabled()) {
                Debug.debugException(e);
                Debug.debug(Level.WARNING, DebugType.OTHER, "Invalid value '" + propertyValueString + "' set for system " + "property '" + propertyName + "'.  The value was expected " + "to be an integer.  The default value of " + defaultValue + "will be used instead of the configured value.", e);
            }
            return defaultValue;
        }
    }
    
    static Long getSystemProperty(final String propertyName, final Long defaultValue) {
        final String propertyValueString = StaticUtils.getSystemProperty(propertyName);
        if (propertyValueString == null) {
            if (Debug.debugEnabled()) {
                Debug.debug(Level.FINE, DebugType.OTHER, "Using the default value of " + defaultValue + " for system " + "property '" + propertyName + "' that is not set.");
            }
            return defaultValue;
        }
        try {
            final long propertyValueLong = Long.parseLong(propertyValueString);
            if (Debug.debugEnabled()) {
                Debug.debug(Level.INFO, DebugType.OTHER, "Using value " + propertyValueLong + " set for system property '" + propertyName + "'.");
            }
            return propertyValueLong;
        }
        catch (final Exception e) {
            if (Debug.debugEnabled()) {
                Debug.debugException(e);
                Debug.debug(Level.WARNING, DebugType.OTHER, "Invalid value '" + propertyValueString + "' set for system " + "property '" + propertyName + "'.  The value was expected " + "to be a long.  The default value of " + defaultValue + "will be used instead of the configured value.", e);
            }
            return defaultValue;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        this.toString(buffer);
        return buffer.toString();
    }
    
    public void toString(final StringBuilder buffer) {
        buffer.append("LDAPConnectionOptions(autoReconnect=");
        buffer.append(this.autoReconnect);
        buffer.append(", nameResolver=");
        this.nameResolver.toString(buffer);
        buffer.append(", bindWithDNRequiresPassword=");
        buffer.append(this.bindWithDNRequiresPassword);
        buffer.append(", followReferrals=");
        buffer.append(this.followReferrals);
        if (this.followReferrals) {
            buffer.append(", referralHopLimit=");
            buffer.append(this.referralHopLimit);
        }
        if (this.referralConnector != null) {
            buffer.append(", referralConnectorClass=");
            buffer.append(this.referralConnector.getClass().getName());
        }
        buffer.append(", useKeepAlive=");
        buffer.append(this.useKeepAlive);
        buffer.append(", useLinger=");
        if (this.useLinger) {
            buffer.append("true, lingerTimeoutSeconds=");
            buffer.append(this.lingerTimeoutSeconds);
        }
        else {
            buffer.append("false");
        }
        buffer.append(", useReuseAddress=");
        buffer.append(this.useReuseAddress);
        buffer.append(", useSchema=");
        buffer.append(this.useSchema);
        buffer.append(", usePooledSchema=");
        buffer.append(this.usePooledSchema);
        buffer.append(", pooledSchemaTimeoutMillis=");
        buffer.append(this.pooledSchemaTimeoutMillis);
        buffer.append(", useSynchronousMode=");
        buffer.append(this.useSynchronousMode);
        buffer.append(", useTCPNoDelay=");
        buffer.append(this.useTCPNoDelay);
        buffer.append(", captureConnectStackTrace=");
        buffer.append(this.captureConnectStackTrace);
        buffer.append(", connectTimeoutMillis=");
        buffer.append(this.connectTimeoutMillis);
        buffer.append(", responseTimeoutMillis=");
        buffer.append(this.responseTimeoutMillis);
        for (final Map.Entry<OperationType, Long> e : this.responseTimeoutMillisByOperationType.entrySet()) {
            buffer.append(", responseTimeoutMillis.");
            buffer.append(e.getKey().name());
            buffer.append('=');
            buffer.append(e.getValue());
        }
        for (final Map.Entry<String, Long> e2 : this.responseTimeoutMillisByExtendedOperationType.entrySet()) {
            buffer.append(", responseTimeoutMillis.EXTENDED.");
            buffer.append(e2.getKey());
            buffer.append('=');
            buffer.append(e2.getValue());
        }
        buffer.append(", abandonOnTimeout=");
        buffer.append(this.abandonOnTimeout);
        buffer.append(", maxMessageSizeBytes=");
        buffer.append(this.maxMessageSizeBytes);
        buffer.append(", receiveBufferSizeBytes=");
        buffer.append(this.receiveBufferSizeBytes);
        buffer.append(", sendBufferSizeBytes=");
        buffer.append(this.sendBufferSizeBytes);
        buffer.append(", allowConcurrentSocketFactoryUse=");
        buffer.append(this.allowConcurrentSocketFactoryUse);
        if (this.disconnectHandler != null) {
            buffer.append(", disconnectHandlerClass=");
            buffer.append(this.disconnectHandler.getClass().getName());
        }
        if (this.unsolicitedNotificationHandler != null) {
            buffer.append(", unsolicitedNotificationHandlerClass=");
            buffer.append(this.unsolicitedNotificationHandler.getClass().getName());
        }
        buffer.append(", sslSocketVerifierClass='");
        buffer.append(this.sslSocketVerifier.getClass().getName());
        buffer.append('\'');
        buffer.append(')');
    }
    
    static {
        PROPERTY_PREFIX = LDAPConnectionOptions.class.getName() + '.';
        PROPERTY_DEFAULT_ABANDON_ON_TIMEOUT = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultAbandonOnTimeout";
        DEFAULT_ABANDON_ON_TIMEOUT = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_ABANDON_ON_TIMEOUT, false);
        PROPERTY_DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultBindWithDNRequiresPassword";
        DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_BIND_WITH_DN_REQUIRES_PASSWORD, true);
        PROPERTY_DEFAULT_CAPTURE_CONNECT_STACK_TRACE = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultCaptureConnectStackTrace";
        DEFAULT_CAPTURE_CONNECT_STACK_TRACE = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_CAPTURE_CONNECT_STACK_TRACE, false);
        PROPERTY_DEFAULT_FOLLOW_REFERRALS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultFollowReferrals";
        DEFAULT_FOLLOW_REFERRALS = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_FOLLOW_REFERRALS, false);
        PROPERTY_DEFAULT_REFERRAL_HOP_LIMIT = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultReferralHopLimit";
        DEFAULT_REFERRAL_HOP_LIMIT = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_REFERRAL_HOP_LIMIT, 5);
        PROPERTY_DEFAULT_USE_SCHEMA = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseSchema";
        DEFAULT_USE_SCHEMA = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_SCHEMA, false);
        PROPERTY_DEFAULT_USE_POOLED_SCHEMA = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUsePooledSchema";
        DEFAULT_USE_POOLED_SCHEMA = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_POOLED_SCHEMA, false);
        PROPERTY_DEFAULT_POOLED_SCHEMA_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultPooledSchemaTimeoutMillis";
        PROPERTY_DEFAULT_USE_KEEPALIVE = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseKeepalive";
        DEFAULT_USE_KEEPALIVE = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_KEEPALIVE, true);
        PROPERTY_DEFAULT_USE_LINGER = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseLinger";
        DEFAULT_USE_LINGER = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_LINGER, true);
        PROPERTY_DEFAULT_LINGER_TIMEOUT_SECONDS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultLingerTimeoutSeconds";
        DEFAULT_LINGER_TIMEOUT_SECONDS = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_LINGER_TIMEOUT_SECONDS, 5);
        PROPERTY_DEFAULT_USE_REUSE_ADDRESS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseReuseAddress";
        DEFAULT_USE_REUSE_ADDRESS = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_REUSE_ADDRESS, true);
        PROPERTY_DEFAULT_USE_SYNCHRONOUS_MODE = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseSynchronousMode";
        DEFAULT_USE_SYNCHRONOUS_MODE = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_SYNCHRONOUS_MODE, false);
        PROPERTY_DEFAULT_USE_TCP_NODELAY = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultUseTCPNoDelay";
        DEFAULT_USE_TCP_NODELAY = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_USE_TCP_NODELAY, true);
        PROPERTY_DEFAULT_CONNECT_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultConnectTimeoutMillis";
        DEFAULT_CONNECT_TIMEOUT_MILLIS = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_CONNECT_TIMEOUT_MILLIS, 10000);
        PROPERTY_DEFAULT_MAX_MESSAGE_SIZE_BYTES = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultMaxMessageSizeBytes";
        DEFAULT_MAX_MESSAGE_SIZE_BYTES = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_MAX_MESSAGE_SIZE_BYTES, 20971520);
        PROPERTY_DEFAULT_RECEIVE_BUFFER_SIZE_BYTES = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultReceiveBufferSizeBytes";
        DEFAULT_RECEIVE_BUFFER_SIZE_BYTES = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_RECEIVE_BUFFER_SIZE_BYTES, 0);
        PROPERTY_DEFAULT_SEND_BUFFER_SIZE_BYTES = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultSendBufferSizeBytes";
        DEFAULT_SEND_BUFFER_SIZE_BYTES = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_SEND_BUFFER_SIZE_BYTES, 0);
        PROPERTY_DEFAULT_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultResponseTimeoutMillis";
        PROPERTY_DEFAULT_ADD_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultAddResponseTimeoutMillis";
        PROPERTY_DEFAULT_BIND_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultBindResponseTimeoutMillis";
        PROPERTY_DEFAULT_COMPARE_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultCompareResponseTimeoutMillis";
        PROPERTY_DEFAULT_DELETE_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultDeleteResponseTimeoutMillis";
        PROPERTY_DEFAULT_EXTENDED_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultExtendedResponseTimeoutMillis";
        PROPERTY_DEFAULT_MODIFY_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultModifyResponseTimeoutMillis";
        PROPERTY_DEFAULT_MODIFY_DN_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultModifyDNResponseTimeoutMillis";
        PROPERTY_DEFAULT_SEARCH_RESPONSE_TIMEOUT_MILLIS = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultSearchResponseTimeoutMillis";
        Long allOpsTimeout = null;
        final EnumMap<OperationType, Long> timeoutsByOpType = new EnumMap<OperationType, Long>(OperationType.class);
        final HashMap<String, Long> timeoutsByExtOpType = new HashMap<String, Long>(StaticUtils.computeMapCapacity(10));
        final String allOpsPropertyValue = StaticUtils.getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_RESPONSE_TIMEOUT_MILLIS);
        if (allOpsPropertyValue != null) {
            try {
                allOpsTimeout = Math.max(0L, Long.parseLong(allOpsPropertyValue));
                for (final OperationType ot : OperationType.values()) {
                    timeoutsByOpType.put(ot, allOpsTimeout);
                }
                if (Debug.debugEnabled()) {
                    Debug.debug(Level.INFO, DebugType.OTHER, "Using value " + allOpsTimeout + " set for system property '" + LDAPConnectionOptions.PROPERTY_DEFAULT_RESPONSE_TIMEOUT_MILLIS + "'.  This " + "timeout will be used for all operation types.");
                }
            }
            catch (final Exception e) {
                if (Debug.debugEnabled()) {
                    Debug.debugException(e);
                    Debug.debug(Level.WARNING, DebugType.OTHER, "Invalid value '" + allOpsPropertyValue + "' set for system " + "property '" + LDAPConnectionOptions.PROPERTY_DEFAULT_RESPONSE_TIMEOUT_MILLIS + "'.  The value was expected to be a long.  Ignoring " + "this property and proceeding as if it had not been set.");
                }
            }
        }
        if (allOpsTimeout == null) {
            allOpsTimeout = 300000L;
            timeoutsByOpType.put(OperationType.ABANDON, 10000L);
            timeoutsByOpType.put(OperationType.UNBIND, 10000L);
            timeoutsByOpType.put(OperationType.ADD, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_ADD_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.BIND, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_BIND_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.COMPARE, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_COMPARE_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.DELETE, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_DELETE_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.MODIFY, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_MODIFY_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.MODIFY_DN, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_MODIFY_DN_RESPONSE_TIMEOUT_MILLIS, 30000L));
            timeoutsByOpType.put(OperationType.SEARCH, getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_SEARCH_RESPONSE_TIMEOUT_MILLIS, 300000L));
            final String extendedOperationTypePrefix = LDAPConnectionOptions.PROPERTY_DEFAULT_EXTENDED_RESPONSE_TIMEOUT_MILLIS + '.';
            for (final String propertyName : StaticUtils.getSystemProperties(new String[0]).stringPropertyNames()) {
                if (propertyName.startsWith(extendedOperationTypePrefix)) {
                    final Long value = getSystemProperty(propertyName, null);
                    if (value == null) {
                        continue;
                    }
                    final String oid = propertyName.substring(extendedOperationTypePrefix.length());
                    timeoutsByExtOpType.put(oid, value);
                }
            }
            final Long extendedOpTimeout = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_EXTENDED_RESPONSE_TIMEOUT_MILLIS, null);
            if (extendedOpTimeout == null) {
                timeoutsByOpType.put(OperationType.EXTENDED, 300000L);
                for (final String oid2 : Arrays.asList("1.3.6.1.4.1.4203.1.11.1", "1.3.6.1.4.1.1466.20037", "1.3.6.1.4.1.4203.1.11.3", "1.3.6.1.4.1.30221.2.6.55", "1.3.6.1.4.1.30221.2.6.14", "1.3.6.1.4.1.30221.2.6.56", "1.3.6.1.4.1.30221.1.6.2", "1.3.6.1.4.1.30221.2.6.43", "1.3.6.1.4.1.30221.1.6.1", "1.3.6.1.4.1.30221.2.6.54", "1.3.6.1.4.1.30221.2.6.58", "1.3.6.1.4.1.30221.2.6.13", "1.3.6.1.4.1.30221.2.6.15")) {
                    if (!timeoutsByExtOpType.containsKey(oid2)) {
                        timeoutsByExtOpType.put(oid2, 30000L);
                    }
                }
            }
            else {
                timeoutsByOpType.put(OperationType.EXTENDED, extendedOpTimeout);
            }
        }
        NameResolver defaultNameResolver = DefaultNameResolver.getInstance();
        try {
            if (StaticUtils.getSystemProperty("com.unboundid.directory.server.ServerRoot") != null || StaticUtils.getEnvironmentVariable("INSTANCE_ROOT") != null) {
                final Class<?> nrClass = Class.forName("com.unboundid.directory.server.util.OutageSafeDnsCache");
                final Method getNameResolverMethod = nrClass.getMethod("getNameResolver", (Class<?>[])new Class[0]);
                final NameResolver nameResolver = (NameResolver)getNameResolverMethod.invoke(null, new Object[0]);
                final InetAddress localHostAddress = nameResolver.getLocalHost();
                if (localHostAddress != null && nameResolver.getByName(localHostAddress.getHostAddress()) != null) {
                    defaultNameResolver = nameResolver;
                }
            }
        }
        catch (final Throwable t) {
            Debug.debugException(Level.FINEST, t);
        }
        DEFAULT_RESPONSE_TIMEOUT_MILLIS = allOpsTimeout;
        DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_OPERATION_TYPE = Collections.unmodifiableMap((Map<? extends OperationType, ? extends Long>)timeoutsByOpType);
        DEFAULT_RESPONSE_TIMEOUT_MILLIS_BY_EXTENDED_OPERATION_TYPE = Collections.unmodifiableMap((Map<? extends String, ? extends Long>)timeoutsByExtOpType);
        DEFAULT_NAME_RESOLVER = defaultNameResolver;
        PROPERTY_DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE = LDAPConnectionOptions.PROPERTY_PREFIX + "defaultAllowConcurrentSocketFactoryUse";
        DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE = getSystemProperty(LDAPConnectionOptions.PROPERTY_DEFAULT_ALLOW_CONCURRENT_SOCKET_FACTORY_USE, true);
        DEFAULT_SSL_SOCKET_VERIFIER = TrustAllSSLSocketVerifier.getInstance();
    }
}
