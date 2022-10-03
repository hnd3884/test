package com.microsoft.sqlserver.jdbc;

import java.util.Hashtable;
import java.sql.DriverPropertyInfo;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.Enumeration;
import java.text.MessageFormat;
import org.ietf.jgss.GSSCredential;
import java.util.Properties;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.logging.Logger;
import java.util.concurrent.atomic.AtomicInteger;
import java.sql.Driver;

public final class SQLServerDriver implements Driver
{
    static final String PRODUCT_NAME = "Microsoft JDBC Driver 8.2 for SQL Server";
    static final String AUTH_DLL_NAME;
    static final String DEFAULT_APP_NAME = "Microsoft JDBC Driver for SQL Server";
    private static final String[] TRUE_FALSE;
    private static final SQLServerDriverPropertyInfo[] DRIVER_PROPERTIES;
    private static final SQLServerDriverPropertyInfo[] DRIVER_PROPERTIES_PROPERTY_ONLY;
    private static final String[][] driverPropertiesSynonyms;
    private static final AtomicInteger baseID;
    private final int instanceID;
    private final String traceID;
    private static final Logger loggerExternal;
    private static final Logger parentLogger;
    private final String loggingClassName;
    private static final Logger drLogger;
    private static Driver mssqlDriver;
    
    private static int nextInstanceID() {
        return SQLServerDriver.baseID.incrementAndGet();
    }
    
    @Override
    public final String toString() {
        return this.traceID;
    }
    
    String getClassNameLogging() {
        return this.loggingClassName;
    }
    
    public static void register() throws SQLException {
        if (!isRegistered()) {
            DriverManager.registerDriver(SQLServerDriver.mssqlDriver = new SQLServerDriver());
        }
    }
    
    public static void deregister() throws SQLException {
        if (isRegistered()) {
            DriverManager.deregisterDriver(SQLServerDriver.mssqlDriver);
            SQLServerDriver.mssqlDriver = null;
        }
    }
    
    public static boolean isRegistered() {
        return SQLServerDriver.mssqlDriver != null;
    }
    
    public SQLServerDriver() {
        this.instanceID = nextInstanceID();
        this.traceID = "SQLServerDriver:" + this.instanceID;
        this.loggingClassName = "com.microsoft.sqlserver.jdbc.SQLServerDriver:" + this.instanceID;
    }
    
    static Properties fixupProperties(final Properties props) throws SQLServerException {
        final Properties fixedup = new Properties();
        final Enumeration<?> e = ((Hashtable<?, V>)props).keys();
        while (e.hasMoreElements()) {
            final String name = (String)e.nextElement();
            String newname = getNormalizedPropertyName(name, SQLServerDriver.drLogger);
            if (null == newname) {
                newname = getPropertyOnlyName(name, SQLServerDriver.drLogger);
            }
            if (null != newname) {
                final String val = props.getProperty(name);
                if (null != val) {
                    fixedup.setProperty(newname, val);
                }
                else {
                    if (!"gsscredential".equalsIgnoreCase(newname) || !(props.get(name) instanceof GSSCredential)) {
                        final MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_invalidpropertyValue"));
                        final Object[] msgArgs = { name };
                        throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
                    }
                    ((Hashtable<String, Object>)fixedup).put(newname, ((Hashtable<K, Object>)props).get(name));
                }
            }
        }
        return fixedup;
    }
    
    static Properties mergeURLAndSuppliedProperties(final Properties urlProps, final Properties suppliedProperties) throws SQLServerException {
        if (null == suppliedProperties) {
            return urlProps;
        }
        if (suppliedProperties.isEmpty()) {
            return urlProps;
        }
        final Properties suppliedPropertiesFixed = fixupProperties(suppliedProperties);
        for (final SQLServerDriverPropertyInfo DRIVER_PROPERTY : SQLServerDriver.DRIVER_PROPERTIES) {
            final String sProp = DRIVER_PROPERTY.getName();
            final String sPropVal = suppliedPropertiesFixed.getProperty(sProp);
            if (null != sPropVal) {
                ((Hashtable<String, String>)urlProps).put(sProp, sPropVal);
            }
        }
        for (final SQLServerDriverPropertyInfo aDRIVER_PROPERTIES_PROPERTY_ONLY : SQLServerDriver.DRIVER_PROPERTIES_PROPERTY_ONLY) {
            final String sProp = aDRIVER_PROPERTIES_PROPERTY_ONLY.getName();
            final Object oPropVal = ((Hashtable<K, Object>)suppliedPropertiesFixed).get(sProp);
            if (null != oPropVal) {
                ((Hashtable<String, Object>)urlProps).put(sProp, oPropVal);
            }
        }
        return urlProps;
    }
    
    static String getNormalizedPropertyName(final String name, final Logger logger) {
        if (null == name) {
            return name;
        }
        for (final String[] driverPropertiesSynonym : SQLServerDriver.driverPropertiesSynonyms) {
            if (driverPropertiesSynonym[0].equalsIgnoreCase(name)) {
                return driverPropertiesSynonym[1];
            }
        }
        for (final SQLServerDriverPropertyInfo DRIVER_PROPERTY : SQLServerDriver.DRIVER_PROPERTIES) {
            if (DRIVER_PROPERTY.getName().equalsIgnoreCase(name)) {
                return DRIVER_PROPERTY.getName();
            }
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Unknown property" + name);
        }
        return null;
    }
    
    static String getPropertyOnlyName(final String name, final Logger logger) {
        if (null == name) {
            return name;
        }
        for (final SQLServerDriverPropertyInfo aDRIVER_PROPERTIES_PROPERTY_ONLY : SQLServerDriver.DRIVER_PROPERTIES_PROPERTY_ONLY) {
            if (aDRIVER_PROPERTIES_PROPERTY_ONLY.getName().equalsIgnoreCase(name)) {
                return aDRIVER_PROPERTIES_PROPERTY_ONLY.getName();
            }
        }
        if (logger.isLoggable(Level.FINER)) {
            logger.finer("Unknown property" + name);
        }
        return null;
    }
    
    @Override
    public Connection connect(final String Url, final Properties suppliedProperties) throws SQLServerException {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "connect", "Arguments not traced.");
        SQLServerConnection result = null;
        final Properties connectProperties = this.parseAndMergeProperties(Url, suppliedProperties);
        if (connectProperties != null) {
            if (Util.use43Wrapper()) {
                result = new SQLServerConnection43(this.toString());
            }
            else {
                result = new SQLServerConnection(this.toString());
            }
            result.connect(connectProperties, null);
        }
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "connect", result);
        return result;
    }
    
    private Properties parseAndMergeProperties(final String Url, final Properties suppliedProperties) throws SQLServerException {
        if (Url == null) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        Properties connectProperties = Util.parseUrl(Url, SQLServerDriver.drLogger);
        if (null == connectProperties) {
            return null;
        }
        final String loginTimeoutProp = connectProperties.getProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString());
        final int dmLoginTimeout = DriverManager.getLoginTimeout();
        if (dmLoginTimeout > 0 && null == loginTimeoutProp) {
            connectProperties.setProperty(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), String.valueOf(dmLoginTimeout));
        }
        connectProperties = mergeURLAndSuppliedProperties(connectProperties, suppliedProperties);
        return connectProperties;
    }
    
    @Override
    public boolean acceptsURL(final String url) throws SQLServerException {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "acceptsURL", "Arguments not traced.");
        if (null == url) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_nullConnection"), null, 0, false);
        }
        boolean result = false;
        try {
            result = (Util.parseUrl(url, SQLServerDriver.drLogger) != null);
        }
        catch (final SQLServerException e) {
            result = false;
        }
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "acceptsURL", result);
        return result;
    }
    
    @Override
    public DriverPropertyInfo[] getPropertyInfo(final String Url, final Properties Info) throws SQLServerException {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "getPropertyInfo", "Arguments not traced.");
        final Properties connProperties = this.parseAndMergeProperties(Url, Info);
        if (null == connProperties) {
            throw new SQLServerException(null, SQLServerException.getErrString("R_invalidConnection"), null, 0, false);
        }
        final DriverPropertyInfo[] properties = getPropertyInfoFromProperties(connProperties);
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "getPropertyInfo");
        return properties;
    }
    
    static final DriverPropertyInfo[] getPropertyInfoFromProperties(final Properties props) {
        final DriverPropertyInfo[] properties = new DriverPropertyInfo[SQLServerDriver.DRIVER_PROPERTIES.length];
        for (int i = 0; i < SQLServerDriver.DRIVER_PROPERTIES.length; ++i) {
            properties[i] = SQLServerDriver.DRIVER_PROPERTIES[i].build(props);
        }
        return properties;
    }
    
    @Override
    public int getMajorVersion() {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "getMajorVersion");
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "getMajorVersion", 8);
        return 8;
    }
    
    @Override
    public int getMinorVersion() {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "getMinorVersion");
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "getMinorVersion", 2);
        return 2;
    }
    
    @Override
    public Logger getParentLogger() {
        return SQLServerDriver.parentLogger;
    }
    
    @Override
    public boolean jdbcCompliant() {
        SQLServerDriver.loggerExternal.entering(this.getClassNameLogging(), "jdbcCompliant");
        SQLServerDriver.loggerExternal.exiting(this.getClassNameLogging(), "jdbcCompliant", Boolean.TRUE);
        return true;
    }
    
    static {
        AUTH_DLL_NAME = "mssql-jdbc_auth-8.2.2." + Util.getJVMArchOnWindows() + "";
        TRUE_FALSE = new String[] { "true", "false" };
        DRIVER_PROPERTIES = new SQLServerDriverPropertyInfo[] { new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue(), false, new String[] { ApplicationIntent.READ_ONLY.toString(), ApplicationIntent.READ_WRITE.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.APPLICATION_NAME.toString(), SQLServerDriverStringProperty.APPLICATION_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue(), false, new String[] { ColumnEncryptionSetting.Disabled.toString(), ColumnEncryptionSetting.Enabled.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.DATABASE_NAME.toString(), SQLServerDriverStringProperty.DATABASE_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), Boolean.toString(SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.getDefaultValue()), false, new String[] { "true" }), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.ENCRYPT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.ENCRYPT.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), SQLServerDriverStringProperty.FAILOVER_PARTNER.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.INSTANCE_NAME.toString(), SQLServerDriverStringProperty.INSTANCE_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString(), Boolean.toString(SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.getDefaultValue(), false, new String[] { KeyStoreAuthentication.JavaKeyStorePassword.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_SECRET.toString(), SQLServerDriverStringProperty.KEY_STORE_SECRET.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), SQLServerDriverStringProperty.KEY_STORE_LOCATION.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), Boolean.toString(SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.PACKET_SIZE.toString(), Integer.toString(SQLServerDriverIntProperty.PACKET_SIZE.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.PASSWORD.toString(), SQLServerDriverStringProperty.PASSWORD.getDefaultValue(), true, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.PORT_NUMBER.toString(), Integer.toString(SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue(), false, new String[] { "adaptive", "full" }), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SELECT_METHOD.toString(), SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue(), false, new String[] { "direct", "cursor" }), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.DOMAIN.toString(), SQLServerDriverStringProperty.DOMAIN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SERVER_NAME.toString(), SQLServerDriverStringProperty.SERVER_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SERVER_SPN.toString(), SQLServerDriverStringProperty.SERVER_SPN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), Boolean.toString(SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), Boolean.toString(SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE.toString(), SQLServerDriverStringProperty.TRUST_STORE.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), Boolean.toString(SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.USER.toString(), SQLServerDriverStringProperty.USER.getDefaultValue(), true, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.WORKSTATION_ID.toString(), SQLServerDriverStringProperty.WORKSTATION_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), Boolean.toString(SQLServerDriverBooleanProperty.XOPEN_STATES.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString(), SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.getDefaultValue(), false, new String[] { AuthenticationScheme.javaKerberos.toString(), AuthenticationScheme.nativeAuthentication.toString(), AuthenticationScheme.ntlm.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.AUTHENTICATION.toString(), SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue(), false, new String[] { SqlAuthentication.NotSpecified.toString(), SqlAuthentication.SqlPassword.toString(), SqlAuthentication.ActiveDirectoryPassword.toString(), SqlAuthentication.ActiveDirectoryIntegrated.toString(), SqlAuthentication.ActiveDirectoryMSI.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.FIPS.toString(), Boolean.toString(SQLServerDriverBooleanProperty.FIPS.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), Integer.toString(SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), Integer.toString(SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue(), false, new String[] { SSLProtocol.TLS.toString(), SSLProtocol.TLS_V10.toString(), SSLProtocol.TLS_V11.toString(), SSLProtocol.TLS_V12.toString() }), new SQLServerDriverPropertyInfo(SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), Integer.toString(SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue()), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), Boolean.toString(SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), SQLServerDriverStringProperty.MSI_CLIENT_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), Boolean.toString(SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue()), false, SQLServerDriver.TRUE_FALSE) };
        DRIVER_PROPERTIES_PROPERTY_ONLY = new SQLServerDriverPropertyInfo[] { new SQLServerDriverPropertyInfo(SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), SQLServerDriverStringProperty.ACCESS_TOKEN.getDefaultValue(), false, null), new SQLServerDriverPropertyInfo(SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), SQLServerDriverObjectProperty.GSS_CREDENTIAL.getDefaultValue(), false, null) };
        driverPropertiesSynonyms = new String[][] { { "database", SQLServerDriverStringProperty.DATABASE_NAME.toString() }, { "userName", SQLServerDriverStringProperty.USER.toString() }, { "server", SQLServerDriverStringProperty.SERVER_NAME.toString() }, { "domainName", SQLServerDriverStringProperty.DOMAIN.toString() }, { "port", SQLServerDriverIntProperty.PORT_NUMBER.toString() } };
        baseID = new AtomicInteger(0);
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.Driver");
        parentLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
        drLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerDriver");
        SQLServerDriver.mssqlDriver = null;
        try {
            register();
        }
        catch (final SQLException e) {
            if (SQLServerDriver.drLogger.isLoggable(Level.FINER) && Util.isActivityTraceOn()) {
                SQLServerDriver.drLogger.finer("Error registering driver: " + e);
            }
        }
    }
}
