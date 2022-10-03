package com.unboundid.util;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldif.LDIFRecord;
import com.unboundid.ldap.sdk.AbstractConnectionPool;
import com.unboundid.asn1.ASN1Buffer;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.ldap.protocol.LDAPResponse;
import com.unboundid.ldap.sdk.InternalSDKHelper;
import com.unboundid.ldap.sdk.LDAPRequest;
import com.unboundid.ldap.sdk.DisconnectType;
import com.unboundid.ldap.sdk.LDAPConnection;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.EnumSet;
import java.util.logging.Logger;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class Debug implements Serializable
{
    public static final String PROPERTY_DEBUG_ENABLED = "com.unboundid.ldap.sdk.debug.enabled";
    public static final String PROPERTY_INCLUDE_STACK_TRACE = "com.unboundid.ldap.sdk.debug.includeStackTrace";
    public static final String PROPERTY_DEBUG_LEVEL = "com.unboundid.ldap.sdk.debug.level";
    public static final String PROPERTY_DEBUG_TYPE = "com.unboundid.ldap.sdk.debug.type";
    public static final String PROPERTY_INCLUDE_CAUSE_IN_EXCEPTION_MESSAGES = "com.unboundid.ldap.sdk.debug.includeCauseInExceptionMessages";
    public static final String PROPERTY_INCLUDE_STACK_TRACE_IN_EXCEPTION_MESSAGES = "com.unboundid.ldap.sdk.debug.includeStackTraceInExceptionMessages";
    public static final String LOGGER_NAME = "com.unboundid.ldap.sdk";
    private static final Logger logger;
    private static final long serialVersionUID = -6079754380415146030L;
    private static boolean debugEnabled;
    private static boolean includeStackTrace;
    private static EnumSet<DebugType> debugTypes;
    
    private Debug() {
    }
    
    public static void initialize() {
        Debug.includeStackTrace = false;
        Debug.debugEnabled = false;
        Debug.debugTypes = EnumSet.allOf(DebugType.class);
        StaticUtils.setLoggerLevel(Debug.logger, Level.ALL);
    }
    
    public static void initialize(final Properties properties) {
        initialize();
        if (properties == null || properties.isEmpty()) {
            return;
        }
        final String enabledProp = properties.getProperty("com.unboundid.ldap.sdk.debug.enabled");
        if (enabledProp != null && !enabledProp.isEmpty()) {
            if (enabledProp.equalsIgnoreCase("true")) {
                Debug.debugEnabled = true;
            }
            else {
                if (!enabledProp.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("Invalid value '" + enabledProp + "' for property " + "com.unboundid.ldap.sdk.debug.enabled" + ".  The value must be either " + "'true' or 'false'.");
                }
                Debug.debugEnabled = false;
            }
        }
        final String stackProp = properties.getProperty("com.unboundid.ldap.sdk.debug.includeStackTrace");
        if (stackProp != null && !stackProp.isEmpty()) {
            if (stackProp.equalsIgnoreCase("true")) {
                Debug.includeStackTrace = true;
            }
            else {
                if (!stackProp.equalsIgnoreCase("false")) {
                    throw new IllegalArgumentException("Invalid value '" + stackProp + "' for property " + "com.unboundid.ldap.sdk.debug.includeStackTrace" + ".  The value must be either " + "'true' or 'false'.");
                }
                Debug.includeStackTrace = false;
            }
        }
        final String typesProp = properties.getProperty("com.unboundid.ldap.sdk.debug.type");
        if (typesProp != null && !typesProp.isEmpty()) {
            Debug.debugTypes = EnumSet.noneOf(DebugType.class);
            final StringTokenizer t = new StringTokenizer(typesProp, ", ");
            while (t.hasMoreTokens()) {
                final String debugTypeName = t.nextToken();
                final DebugType debugType = DebugType.forName(debugTypeName);
                if (debugType == null) {
                    throw new IllegalArgumentException("Invalid value '" + debugTypeName + "' for property " + "com.unboundid.ldap.sdk.debug.type" + ".  Allowed values include:  " + DebugType.getTypeNameList() + '.');
                }
                Debug.debugTypes.add(debugType);
            }
        }
        final String levelProp = properties.getProperty("com.unboundid.ldap.sdk.debug.level");
        if (levelProp != null && !levelProp.isEmpty()) {
            StaticUtils.setLoggerLevel(Debug.logger, Level.parse(levelProp));
        }
    }
    
    public static Logger getLogger() {
        return Debug.logger;
    }
    
    public static boolean debugEnabled() {
        return Debug.debugEnabled;
    }
    
    public static boolean debugEnabled(final DebugType debugType) {
        return Debug.debugEnabled && Debug.debugTypes.contains(debugType);
    }
    
    public static void setEnabled(final boolean enabled) {
        Debug.debugTypes = EnumSet.allOf(DebugType.class);
        Debug.debugEnabled = enabled;
    }
    
    public static void setEnabled(final boolean enabled, final Set<DebugType> types) {
        if (types == null || types.isEmpty()) {
            Debug.debugTypes = EnumSet.allOf(DebugType.class);
        }
        else {
            Debug.debugTypes = EnumSet.copyOf(types);
        }
        Debug.debugEnabled = enabled;
    }
    
    public static boolean includeStackTrace() {
        return Debug.includeStackTrace;
    }
    
    public static void setIncludeStackTrace(final boolean includeStackTrace) {
        Debug.includeStackTrace = includeStackTrace;
    }
    
    public static EnumSet<DebugType> getDebugTypes() {
        return Debug.debugTypes;
    }
    
    public static void debugException(final Throwable t) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.EXCEPTION)) {
            debugException(Level.WARNING, t);
        }
    }
    
    public static void debugException(final Level l, final Throwable t) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.EXCEPTION)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("caughtException=\"");
            StaticUtils.getStackTrace(t, buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString(), t);
        }
    }
    
    public static void debugConnect(final String h, final int p) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugConnect(Level.INFO, h, p, null);
        }
    }
    
    public static void debugConnect(final Level l, final String h, final int p) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugConnect(l, h, p, null);
        }
    }
    
    public static void debugConnect(final String h, final int p, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugConnect(Level.INFO, h, p, c);
        }
    }
    
    public static void debugConnect(final Level l, final String h, final int p, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("connectedTo=\"");
            buffer.append(h);
            buffer.append(':');
            buffer.append(p);
            buffer.append('\"');
            if (c != null) {
                buffer.append(" connectionID=");
                buffer.append(c.getConnectionID());
                final String connectionName = c.getConnectionName();
                if (connectionName != null) {
                    buffer.append(" connectionName=\"");
                    buffer.append(connectionName);
                    buffer.append('\"');
                }
                final String connectionPoolName = c.getConnectionPoolName();
                if (connectionPoolName != null) {
                    buffer.append(" connectionPoolName=\"");
                    buffer.append(connectionPoolName);
                    buffer.append('\"');
                }
            }
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugDisconnect(final String h, final int p, final DisconnectType t, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugDisconnect(Level.INFO, h, p, null, t, m, e);
        }
    }
    
    public static void debugDisconnect(final Level l, final String h, final int p, final DisconnectType t, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugDisconnect(l, h, p, null, t, m, e);
        }
    }
    
    public static void debugDisconnect(final String h, final int p, final LDAPConnection c, final DisconnectType t, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            debugDisconnect(Level.INFO, h, p, c, t, m, e);
        }
    }
    
    public static void debugDisconnect(final Level l, final String h, final int p, final LDAPConnection c, final DisconnectType t, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECT)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            if (c != null) {
                buffer.append("connectionID=");
                buffer.append(c.getConnectionID());
                final String connectionName = c.getConnectionName();
                if (connectionName != null) {
                    buffer.append(" connectionName=\"");
                    buffer.append(connectionName);
                    buffer.append('\"');
                }
                final String connectionPoolName = c.getConnectionPoolName();
                if (connectionPoolName != null) {
                    buffer.append(" connectionPoolName=\"");
                    buffer.append(connectionPoolName);
                    buffer.append('\"');
                }
                buffer.append(' ');
            }
            buffer.append("disconnectedFrom=\"");
            buffer.append(h);
            buffer.append(':');
            buffer.append(p);
            buffer.append("\" disconnectType=\"");
            buffer.append(t.name());
            buffer.append('\"');
            if (m != null) {
                buffer.append("\" disconnectMessage=\"");
                buffer.append(m);
                buffer.append('\"');
            }
            if (e != null) {
                buffer.append("\" disconnectCause=\"");
                StaticUtils.getStackTrace(e, buffer);
                buffer.append('\"');
            }
            Debug.logger.log(l, buffer.toString(), c);
        }
    }
    
    public static void debugLDAPRequest(final LDAPRequest r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPRequest(Level.INFO, r, -1, null);
        }
    }
    
    public static void debugLDAPRequest(final Level l, final LDAPRequest r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPRequest(l, r, -1, null);
        }
    }
    
    public static void debugLDAPRequest(final LDAPRequest r, final int i, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPRequest(Level.INFO, r, i, c);
        }
    }
    
    public static void debugLDAPRequest(final Level l, final LDAPRequest r, final int i, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPRequest(Level.INFO, String.valueOf(r), i, c);
        }
    }
    
    public static void debugLDAPRequest(final Level l, final String s, final int i, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            if (c != null) {
                buffer.append("connectionID=");
                buffer.append(c.getConnectionID());
                final String connectionName = c.getConnectionName();
                if (connectionName != null) {
                    buffer.append(" connectionName=\"");
                    buffer.append(connectionName);
                    buffer.append('\"');
                }
                final String connectionPoolName = c.getConnectionPoolName();
                if (connectionPoolName != null) {
                    buffer.append(" connectionPoolName=\"");
                    buffer.append(connectionPoolName);
                    buffer.append('\"');
                }
                buffer.append(" connectedTo=\"");
                buffer.append(c.getConnectedAddress());
                buffer.append(':');
                buffer.append(c.getConnectedPort());
                buffer.append("\" ");
                try {
                    final int soTimeout = InternalSDKHelper.getSoTimeout(c);
                    buffer.append("socketTimeoutMillis=");
                    buffer.append(soTimeout);
                    buffer.append(' ');
                }
                catch (final Exception ex) {}
            }
            if (i >= 0) {
                buffer.append("messageID=");
                buffer.append(i);
                buffer.append(' ');
            }
            buffer.append("sendingLDAPRequest=\"");
            buffer.append(s);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugLDAPResult(final LDAPResponse r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPResult(Level.INFO, r, null);
        }
    }
    
    public static void debugLDAPResult(final Level l, final LDAPResponse r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPResult(l, r, null);
        }
    }
    
    public static void debugLDAPResult(final LDAPResponse r, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            debugLDAPResult(Level.INFO, r, c);
        }
    }
    
    public static void debugLDAPResult(final Level l, final LDAPResponse r, final LDAPConnection c) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDAP)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            if (c != null) {
                buffer.append("connectionID=");
                buffer.append(c.getConnectionID());
                final String connectionName = c.getConnectionName();
                if (connectionName != null) {
                    buffer.append(" connectionName=\"");
                    buffer.append(connectionName);
                    buffer.append('\"');
                }
                final String connectionPoolName = c.getConnectionPoolName();
                if (connectionPoolName != null) {
                    buffer.append(" connectionPoolName=\"");
                    buffer.append(connectionPoolName);
                    buffer.append('\"');
                }
                buffer.append(" connectedTo=\"");
                buffer.append(c.getConnectedAddress());
                buffer.append(':');
                buffer.append(c.getConnectedPort());
                buffer.append("\" ");
            }
            buffer.append("readLDAPResult=\"");
            r.toString(buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugASN1Write(final ASN1Element e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            debugASN1Write(Level.INFO, e);
        }
    }
    
    public static void debugASN1Write(final Level l, final ASN1Element e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("writingASN1Element=\"");
            e.toString(buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugASN1Write(final ASN1Buffer b) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            debugASN1Write(Level.INFO, b);
        }
    }
    
    public static void debugASN1Write(final Level l, final ASN1Buffer b) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("writingASN1Element=\"");
            StaticUtils.toHex(b.toByteArray(), buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugASN1Read(final ASN1Element e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            debugASN1Read(Level.INFO, e);
        }
    }
    
    public static void debugASN1Read(final Level l, final ASN1Element e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("readASN1Element=\"");
            e.toString(buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugASN1Read(final Level l, final String dataType, final int berType, final int length, final Object value) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.ASN1)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("readASN1Element=\"dataType='");
            buffer.append(dataType);
            buffer.append("' berType='");
            buffer.append(StaticUtils.toHex((byte)(berType & 0xFF)));
            buffer.append("' valueLength=");
            buffer.append(length);
            if (value != null) {
                buffer.append(" value='");
                if (value instanceof byte[]) {
                    StaticUtils.toHex((byte[])value, buffer);
                }
                else {
                    buffer.append(value);
                }
                buffer.append('\'');
            }
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugConnectionPool(final Level l, final AbstractConnectionPool p, final LDAPConnection c, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CONNECTION_POOL)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            final String poolName = p.getConnectionPoolName();
            buffer.append("connectionPool=\"");
            if (poolName == null) {
                buffer.append("{unnamed}");
            }
            else {
                buffer.append(poolName);
            }
            buffer.append("\" ");
            if (c != null) {
                buffer.append(" connectionID=");
                buffer.append(c.getConnectionID());
                final String hostPort = c.getHostPort();
                if (hostPort != null && !hostPort.isEmpty()) {
                    buffer.append(" connectedTo=\"");
                    buffer.append(hostPort);
                    buffer.append('\"');
                }
            }
            final long currentAvailable = p.getCurrentAvailableConnections();
            if (currentAvailable >= 0L) {
                buffer.append(" currentAvailableConnections=");
                buffer.append(currentAvailable);
            }
            final long maxAvailable = p.getMaximumAvailableConnections();
            if (maxAvailable >= 0L) {
                buffer.append(" maxAvailableConnections=");
                buffer.append(maxAvailable);
            }
            buffer.append(" message=\"");
            buffer.append(m);
            buffer.append('\"');
            if (e != null) {
                buffer.append(" exception=\"");
                StaticUtils.getStackTrace(e, buffer);
                buffer.append('\"');
            }
            Debug.logger.log(l, buffer.toString(), e);
        }
    }
    
    public static void debugLDIFWrite(final LDIFRecord r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDIF)) {
            debugLDIFWrite(Level.INFO, r);
        }
    }
    
    public static void debugLDIFWrite(final Level l, final LDIFRecord r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDIF)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("writingLDIFRecord=\"");
            r.toString(buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugLDIFRead(final LDIFRecord r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDIF)) {
            debugLDIFRead(Level.INFO, r);
        }
    }
    
    public static void debugLDIFRead(final Level l, final LDIFRecord r) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.LDIF)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("readLDIFRecord=\"");
            r.toString(buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugMonitor(final Entry e, final String m) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.MONITOR)) {
            debugMonitor(Level.FINE, e, m);
        }
    }
    
    public static void debugMonitor(final Level l, final Entry e, final String m) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.MONITOR)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("monitorEntryDN=\"");
            buffer.append(e.getDN());
            buffer.append("\" message=\"");
            buffer.append(m);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debugCodingError(final Throwable t) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(DebugType.CODING_ERROR)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, Level.SEVERE);
            buffer.append("codingError=\"");
            StaticUtils.getStackTrace(t, buffer);
            buffer.append('\"');
            Debug.logger.log(Level.SEVERE, buffer.toString());
        }
    }
    
    public static void debug(final Level l, final DebugType t, final String m) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(t)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("message=\"");
            buffer.append(m);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString());
        }
    }
    
    public static void debug(final Level l, final DebugType t, final String m, final Throwable e) {
        if (Debug.debugEnabled && Debug.debugTypes.contains(t)) {
            final StringBuilder buffer = new StringBuilder();
            addCommonHeader(buffer, l);
            buffer.append("message=\"");
            buffer.append(m);
            buffer.append('\"');
            buffer.append(" exception=\"");
            StaticUtils.getStackTrace(e, buffer);
            buffer.append('\"');
            Debug.logger.log(l, buffer.toString(), e);
        }
    }
    
    private static void addCommonHeader(final StringBuilder buffer, final Level level) {
        buffer.append("level=\"");
        buffer.append(level.getName());
        buffer.append("\" threadID=");
        buffer.append(Thread.currentThread().getId());
        buffer.append(" threadName=\"");
        buffer.append(Thread.currentThread().getName());
        if (Debug.includeStackTrace) {
            buffer.append("\" calledFrom=\"");
            boolean appended = false;
            boolean foundDebug = false;
            for (final StackTraceElement e : Thread.currentThread().getStackTrace()) {
                final String className = e.getClassName();
                if (className.equals(Debug.class.getName())) {
                    foundDebug = true;
                }
                else if (foundDebug) {
                    if (appended) {
                        buffer.append(" / ");
                    }
                    appended = true;
                    buffer.append(e.getMethodName());
                    buffer.append('(');
                    buffer.append(e.getFileName());
                    final int lineNumber = e.getLineNumber();
                    if (lineNumber > 0) {
                        buffer.append(':');
                        buffer.append(lineNumber);
                    }
                    else if (e.isNativeMethod()) {
                        buffer.append(":native");
                    }
                    buffer.append(')');
                }
            }
        }
        buffer.append("\" ldapSDKVersion=\"");
        buffer.append("4.0.14");
        buffer.append("\" revision=\"");
        buffer.append("c0fb784eebf9d36a67c736d0428fb3577f2e25bb");
        buffer.append("\" ");
    }
    
    static {
        logger = Logger.getLogger("com.unboundid.ldap.sdk");
        initialize(StaticUtils.getSystemProperties("com.unboundid.ldap.sdk.debug.enabled", "com.unboundid.ldap.sdk.debug.level", "com.unboundid.ldap.sdk.debug.type", "com.unboundid.ldap.sdk.debug.includeStackTrace"));
    }
}
