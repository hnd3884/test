package com.microsoft.sqlserver.jdbc;

import java.util.Hashtable;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.sql.SQLException;
import java.util.Enumeration;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import javax.naming.Reference;
import org.ietf.jgss.GSSCredential;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Level;
import java.sql.Connection;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.Referenceable;
import java.io.Serializable;
import javax.sql.DataSource;

public class SQLServerDataSource implements ISQLServerDataSource, DataSource, Serializable, Referenceable
{
    static final Logger dsLogger;
    static final Logger loggerExternal;
    private static final Logger parentLogger;
    private final String loggingClassName;
    private boolean trustStorePasswordStripped;
    private static final long serialVersionUID = 654861379544314296L;
    private Properties connectionProps;
    private String dataSourceURL;
    private String dataSourceDescription;
    private static final AtomicInteger baseDataSourceID;
    private final String traceID;
    private transient PrintWriter logWriter;
    
    public SQLServerDataSource() {
        this.trustStorePasswordStripped = false;
        this.connectionProps = new Properties();
        final int dataSourceID = nextDataSourceID();
        final String nameL = this.getClass().getName();
        this.traceID = nameL.substring(1 + nameL.lastIndexOf(46)) + ":" + dataSourceID;
        this.loggingClassName = "com.microsoft.sqlserver.jdbc." + nameL.substring(1 + nameL.lastIndexOf(46)) + ":" + dataSourceID;
    }
    
    String getClassNameLogging() {
        return this.loggingClassName;
    }
    
    @Override
    public String toString() {
        return this.traceID;
    }
    
    @Override
    public Connection getConnection() throws SQLServerException {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getConnection");
        final Connection con = this.getConnectionInternal(null, null, null);
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getConnection", con);
        return con;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLServerException {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getConnection", new Object[] { username, "Password not traced" });
        }
        final Connection con = this.getConnectionInternal(username, password, null);
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getConnection", con);
        return con;
    }
    
    @Override
    public void setLoginTimeout(final int loginTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), loginTimeout);
    }
    
    @Override
    public int getLoginTimeout() {
        final int defaultTimeOut = SQLServerDriverIntProperty.LOGIN_TIMEOUT.getDefaultValue();
        final int logintimeout = this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOGIN_TIMEOUT.toString(), defaultTimeOut);
        return (logintimeout == 0) ? defaultTimeOut : logintimeout;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "setLogWriter", out);
        this.logWriter = out;
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "setLogWriter");
    }
    
    @Override
    public PrintWriter getLogWriter() {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getLogWriter");
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getLogWriter", this.logWriter);
        return this.logWriter;
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return SQLServerDataSource.parentLogger;
    }
    
    @Override
    public void setApplicationName(final String applicationName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_NAME.toString(), applicationName);
    }
    
    @Override
    public String getApplicationName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_NAME.toString(), SQLServerDriverStringProperty.APPLICATION_NAME.getDefaultValue());
    }
    
    @Override
    public void setDatabaseName(final String databaseName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATABASE_NAME.toString(), databaseName);
    }
    
    @Override
    public String getDatabaseName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.DATABASE_NAME.toString(), null);
    }
    
    @Override
    public void setInstanceName(final String instanceName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.INSTANCE_NAME.toString(), instanceName);
    }
    
    @Override
    public String getInstanceName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.INSTANCE_NAME.toString(), null);
    }
    
    @Override
    public void setIntegratedSecurity(final boolean enable) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.INTEGRATED_SECURITY.toString(), enable);
    }
    
    @Override
    public void setAuthenticationScheme(final String authenticationScheme) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION_SCHEME.toString(), authenticationScheme);
    }
    
    @Override
    public void setAuthentication(final String authentication) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION.toString(), authentication);
    }
    
    @Override
    public String getAuthentication() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.AUTHENTICATION.toString(), SQLServerDriverStringProperty.AUTHENTICATION.getDefaultValue());
    }
    
    @Override
    public void setGSSCredentials(final GSSCredential userCredential) {
        this.setObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), userCredential);
    }
    
    @Override
    public GSSCredential getGSSCredentials() {
        return (GSSCredential)this.getObjectProperty(this.connectionProps, SQLServerDriverObjectProperty.GSS_CREDENTIAL.toString(), SQLServerDriverObjectProperty.GSS_CREDENTIAL.getDefaultValue());
    }
    
    @Override
    public void setAccessToken(final String accessToken) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), accessToken);
    }
    
    @Override
    public String getAccessToken() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ACCESS_TOKEN.toString(), null);
    }
    
    @Override
    public void setColumnEncryptionSetting(final String columnEncryptionSetting) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), columnEncryptionSetting);
    }
    
    @Override
    public String getColumnEncryptionSetting() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.COLUMN_ENCRYPTION.toString(), SQLServerDriverStringProperty.COLUMN_ENCRYPTION.getDefaultValue());
    }
    
    @Override
    public void setKeyStoreAuthentication(final String keyStoreAuthentication) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), keyStoreAuthentication);
    }
    
    @Override
    public String getKeyStoreAuthentication() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.toString(), SQLServerDriverStringProperty.KEY_STORE_AUTHENTICATION.getDefaultValue());
    }
    
    @Override
    public void setKeyStoreSecret(final String keyStoreSecret) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_SECRET.toString(), keyStoreSecret);
    }
    
    @Override
    public void setKeyStoreLocation(final String keyStoreLocation) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), keyStoreLocation);
    }
    
    @Override
    public String getKeyStoreLocation() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_STORE_LOCATION.toString(), SQLServerDriverStringProperty.KEY_STORE_LOCATION.getDefaultValue());
    }
    
    @Override
    public void setLastUpdateCount(final boolean lastUpdateCount) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), lastUpdateCount);
    }
    
    @Override
    public boolean getLastUpdateCount() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.toString(), SQLServerDriverBooleanProperty.LAST_UPDATE_COUNT.getDefaultValue());
    }
    
    @Override
    public void setEncrypt(final boolean encrypt) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENCRYPT.toString(), encrypt);
    }
    
    @Override
    public boolean getEncrypt() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENCRYPT.toString(), SQLServerDriverBooleanProperty.ENCRYPT.getDefaultValue());
    }
    
    @Override
    public void setTransparentNetworkIPResolution(final boolean tnir) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), tnir);
    }
    
    @Override
    public boolean getTransparentNetworkIPResolution() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.toString(), SQLServerDriverBooleanProperty.TRANSPARENT_NETWORK_IP_RESOLUTION.getDefaultValue());
    }
    
    @Override
    public void setTrustServerCertificate(final boolean e) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), e);
    }
    
    @Override
    public boolean getTrustServerCertificate() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.toString(), SQLServerDriverBooleanProperty.TRUST_SERVER_CERTIFICATE.getDefaultValue());
    }
    
    @Override
    public void setTrustStoreType(final String trustStoreType) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), trustStoreType);
    }
    
    @Override
    public String getTrustStoreType() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_TYPE.toString(), SQLServerDriverStringProperty.TRUST_STORE_TYPE.getDefaultValue());
    }
    
    @Override
    public void setTrustStore(final String trustStore) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE.toString(), trustStore);
    }
    
    @Override
    public String getTrustStore() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE.toString(), null);
    }
    
    @Override
    public void setTrustStorePassword(final String trustStorePassword) {
        if (trustStorePassword != null) {
            this.trustStorePasswordStripped = false;
        }
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), trustStorePassword);
    }
    
    String getTrustStorePassword() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString(), null);
    }
    
    @Override
    public void setHostNameInCertificate(final String hostName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), hostName);
    }
    
    @Override
    public String getHostNameInCertificate() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.HOSTNAME_IN_CERTIFICATE.toString(), null);
    }
    
    @Override
    public void setLockTimeout(final int lockTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), lockTimeout);
    }
    
    @Override
    public int getLockTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.LOCK_TIMEOUT.toString(), SQLServerDriverIntProperty.LOCK_TIMEOUT.getDefaultValue());
    }
    
    @Override
    public void setPassword(final String password) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.PASSWORD.toString(), password);
    }
    
    String getPassword() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.PASSWORD.toString(), null);
    }
    
    @Override
    public void setPortNumber(final int portNumber) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.PORT_NUMBER.toString(), portNumber);
    }
    
    @Override
    public int getPortNumber() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.PORT_NUMBER.toString(), SQLServerDriverIntProperty.PORT_NUMBER.getDefaultValue());
    }
    
    @Override
    public void setSelectMethod(final String selectMethod) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SELECT_METHOD.toString(), selectMethod);
    }
    
    @Override
    public String getSelectMethod() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SELECT_METHOD.toString(), SQLServerDriverStringProperty.SELECT_METHOD.getDefaultValue());
    }
    
    @Override
    public void setResponseBuffering(final String bufferingMode) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), bufferingMode);
    }
    
    @Override
    public String getResponseBuffering() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.RESPONSE_BUFFERING.toString(), SQLServerDriverStringProperty.RESPONSE_BUFFERING.getDefaultValue());
    }
    
    @Override
    public void setApplicationIntent(final String applicationIntent) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), applicationIntent);
    }
    
    @Override
    public String getApplicationIntent() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.APPLICATION_INTENT.toString(), SQLServerDriverStringProperty.APPLICATION_INTENT.getDefaultValue());
    }
    
    @Override
    public void setSendTimeAsDatetime(final boolean sendTimeAsDatetime) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), sendTimeAsDatetime);
    }
    
    @Override
    public boolean getSendTimeAsDatetime() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.toString(), SQLServerDriverBooleanProperty.SEND_TIME_AS_DATETIME.getDefaultValue());
    }
    
    @Override
    public void setUseFmtOnly(final boolean useFmtOnly) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), useFmtOnly);
    }
    
    @Override
    public boolean getUseFmtOnly() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_FMT_ONLY.toString(), SQLServerDriverBooleanProperty.USE_FMT_ONLY.getDefaultValue());
    }
    
    @Override
    public void setSendStringParametersAsUnicode(final boolean sendStringParametersAsUnicode) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), sendStringParametersAsUnicode);
    }
    
    @Override
    public boolean getSendStringParametersAsUnicode() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.toString(), SQLServerDriverBooleanProperty.SEND_STRING_PARAMETERS_AS_UNICODE.getDefaultValue());
    }
    
    @Override
    public void setServerNameAsACE(final boolean serverNameAsACE) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), serverNameAsACE);
    }
    
    @Override
    public boolean getServerNameAsACE() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.toString(), SQLServerDriverBooleanProperty.SERVER_NAME_AS_ACE.getDefaultValue());
    }
    
    @Override
    public void setServerName(final String serverName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_NAME.toString(), serverName);
    }
    
    @Override
    public String getServerName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_NAME.toString(), null);
    }
    
    @Override
    public void setServerSpn(final String serverSpn) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_SPN.toString(), serverSpn);
    }
    
    @Override
    public String getServerSpn() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SERVER_SPN.toString(), null);
    }
    
    @Override
    public void setFailoverPartner(final String serverName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), serverName);
    }
    
    @Override
    public String getFailoverPartner() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.FAILOVER_PARTNER.toString(), null);
    }
    
    @Override
    public void setMultiSubnetFailover(final boolean multiSubnetFailover) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), multiSubnetFailover);
    }
    
    @Override
    public boolean getMultiSubnetFailover() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.toString(), SQLServerDriverBooleanProperty.MULTI_SUBNET_FAILOVER.getDefaultValue());
    }
    
    @Override
    public void setUser(final String user) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.USER.toString(), user);
    }
    
    @Override
    public String getUser() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.USER.toString(), null);
    }
    
    @Override
    public void setWorkstationID(final String workstationID) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.WORKSTATION_ID.toString(), workstationID);
    }
    
    @Override
    public String getWorkstationID() {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getWorkstationID");
        }
        String getWSID = this.connectionProps.getProperty(SQLServerDriverStringProperty.WORKSTATION_ID.toString());
        if (null == getWSID) {
            getWSID = Util.lookupHostName();
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getWorkstationID", getWSID);
        return getWSID;
    }
    
    @Override
    public void setXopenStates(final boolean xopenStates) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), xopenStates);
    }
    
    @Override
    public boolean getXopenStates() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.XOPEN_STATES.toString(), SQLServerDriverBooleanProperty.XOPEN_STATES.getDefaultValue());
    }
    
    @Override
    public void setFIPS(final boolean fips) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.FIPS.toString(), fips);
    }
    
    @Override
    public boolean getFIPS() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.FIPS.toString(), SQLServerDriverBooleanProperty.FIPS.getDefaultValue());
    }
    
    @Override
    public void setSSLProtocol(final String sslProtocol) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), sslProtocol);
    }
    
    @Override
    public String getSSLProtocol() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.SSL_PROTOCOL.toString(), SQLServerDriverStringProperty.SSL_PROTOCOL.getDefaultValue());
    }
    
    @Override
    public void setTrustManagerClass(final String trustManagerClass) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), trustManagerClass);
    }
    
    @Override
    public String getTrustManagerClass() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CLASS.getDefaultValue());
    }
    
    @Override
    public void setTrustManagerConstructorArg(final String trustManagerConstructorArg) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), trustManagerConstructorArg);
    }
    
    @Override
    public String getTrustManagerConstructorArg() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.toString(), SQLServerDriverStringProperty.TRUST_MANAGER_CONSTRUCTOR_ARG.getDefaultValue());
    }
    
    @Override
    public void setURL(final String url) {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "setURL", url);
        this.dataSourceURL = url;
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "setURL");
    }
    
    @Override
    public String getURL() {
        String url = this.dataSourceURL;
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getURL");
        if (null == this.dataSourceURL) {
            url = "jdbc:sqlserver://";
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getURL", url);
        return url;
    }
    
    @Override
    public void setDescription(final String description) {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "setDescription", description);
        this.dataSourceDescription = description;
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "setDescription");
    }
    
    @Override
    public String getDescription() {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getDescription");
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getDescription", this.dataSourceDescription);
        return this.dataSourceDescription;
    }
    
    @Override
    public void setPacketSize(final int packetSize) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.PACKET_SIZE.toString(), packetSize);
    }
    
    @Override
    public int getPacketSize() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.PACKET_SIZE.toString(), SQLServerDriverIntProperty.PACKET_SIZE.getDefaultValue());
    }
    
    @Override
    public void setQueryTimeout(final int queryTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), queryTimeout);
    }
    
    @Override
    public int getQueryTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.QUERY_TIMEOUT.toString(), SQLServerDriverIntProperty.QUERY_TIMEOUT.getDefaultValue());
    }
    
    @Override
    public void setCancelQueryTimeout(final int cancelQueryTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), cancelQueryTimeout);
    }
    
    @Override
    public int getCancelQueryTimeout() {
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.toString(), SQLServerDriverIntProperty.CANCEL_QUERY_TIMEOUT.getDefaultValue());
    }
    
    @Override
    public void setEnablePrepareOnFirstPreparedStatementCall(final boolean enablePrepareOnFirstPreparedStatementCall) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), enablePrepareOnFirstPreparedStatementCall);
    }
    
    @Override
    public boolean getEnablePrepareOnFirstPreparedStatementCall() {
        final boolean defaultValue = SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.getDefaultValue();
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT.toString(), defaultValue);
    }
    
    @Override
    public void setServerPreparedStatementDiscardThreshold(final int serverPreparedStatementDiscardThreshold) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), serverPreparedStatementDiscardThreshold);
    }
    
    @Override
    public int getServerPreparedStatementDiscardThreshold() {
        final int defaultSize = SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.SERVER_PREPARED_STATEMENT_DISCARD_THRESHOLD.toString(), defaultSize);
    }
    
    @Override
    public void setStatementPoolingCacheSize(final int statementPoolingCacheSize) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), statementPoolingCacheSize);
    }
    
    @Override
    public int getStatementPoolingCacheSize() {
        final int defaultSize = SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.STATEMENT_POOLING_CACHE_SIZE.toString(), defaultSize);
    }
    
    @Override
    public void setDisableStatementPooling(final boolean disableStatementPooling) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), disableStatementPooling);
    }
    
    @Override
    public boolean getDisableStatementPooling() {
        final boolean defaultValue = SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.getDefaultValue();
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.DISABLE_STATEMENT_POOLING.toString(), defaultValue);
    }
    
    @Override
    public void setSocketTimeout(final int socketTimeout) {
        this.setIntProperty(this.connectionProps, SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), socketTimeout);
    }
    
    @Override
    public int getSocketTimeout() {
        final int defaultTimeOut = SQLServerDriverIntProperty.SOCKET_TIMEOUT.getDefaultValue();
        return this.getIntProperty(this.connectionProps, SQLServerDriverIntProperty.SOCKET_TIMEOUT.toString(), defaultTimeOut);
    }
    
    @Override
    public void setUseBulkCopyForBatchInsert(final boolean useBulkCopyForBatchInsert) {
        this.setBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), useBulkCopyForBatchInsert);
    }
    
    @Override
    public boolean getUseBulkCopyForBatchInsert() {
        return this.getBooleanProperty(this.connectionProps, SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.toString(), SQLServerDriverBooleanProperty.USE_BULK_COPY_FOR_BATCH_INSERT.getDefaultValue());
    }
    
    @Override
    public void setJASSConfigurationName(final String configurationName) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), configurationName);
    }
    
    @Override
    public String getJASSConfigurationName() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.JAAS_CONFIG_NAME.toString(), SQLServerDriverStringProperty.JAAS_CONFIG_NAME.getDefaultValue());
    }
    
    @Override
    public void setMSIClientId(final String msiClientId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), msiClientId);
    }
    
    @Override
    public String getMSIClientId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.MSI_CLIENT_ID.toString(), SQLServerDriverStringProperty.MSI_CLIENT_ID.getDefaultValue());
    }
    
    @Override
    public void setKeyVaultProviderClientId(final String keyVaultProviderClientId) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), keyVaultProviderClientId);
    }
    
    @Override
    public String getKeyVaultProviderClientId() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.toString(), SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_ID.getDefaultValue());
    }
    
    @Override
    public void setKeyVaultProviderClientKey(final String keyVaultProviderClientKey) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.KEY_VAULT_PROVIDER_CLIENT_KEY.toString(), keyVaultProviderClientKey);
    }
    
    @Override
    public String getDomain() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.DOMAIN.toString(), SQLServerDriverStringProperty.DOMAIN.getDefaultValue());
    }
    
    @Override
    public void setDomain(final String domain) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.DOMAIN.toString(), domain);
    }
    
    @Override
    public String getEnclaveAttestationUrl() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.getDefaultValue());
    }
    
    @Override
    public void setEnclaveAttestationUrl(final String url) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_URL.toString(), url);
    }
    
    @Override
    public String getEnclaveAttestationProtocol() {
        return this.getStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.getDefaultValue());
    }
    
    @Override
    public void setEnclaveAttestationProtocol(final String protocol) {
        this.setStringProperty(this.connectionProps, SQLServerDriverStringProperty.ENCLAVE_ATTESTATION_PROTOCOL.toString(), protocol);
    }
    
    private void setStringProperty(final Properties props, final String propKey, final String propValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER) && !propKey.contains("password") && !propKey.contains("Password")) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        }
        else {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "set" + propKey);
        }
        if (null != propValue) {
            props.setProperty(propKey, propValue);
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }
    
    private String getStringProperty(final Properties props, final String propKey, final String defaultValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        String propValue = props.getProperty(propKey);
        if (null == propValue) {
            propValue = defaultValue;
        }
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER) && !propKey.contains("password") && !propKey.contains("Password")) {
            SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, propValue);
        }
        return propValue;
    }
    
    private void setIntProperty(final Properties props, final String propKey, final int propValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        }
        props.setProperty(propKey, Integer.valueOf(propValue).toString());
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }
    
    private int getIntProperty(final Properties props, final String propKey, final int defaultValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        final String propValue = props.getProperty(propKey);
        int value = defaultValue;
        if (null != propValue) {
            try {
                value = Integer.parseInt(propValue);
            }
            catch (final NumberFormatException nfe) {
                assert false : "Bad portNumber:-" + propValue;
            }
        }
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, value);
        }
        return value;
    }
    
    private void setBooleanProperty(final Properties props, final String propKey, final boolean propValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "set" + propKey, propValue);
        }
        props.setProperty(propKey, propValue ? "true" : "false");
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }
    
    private boolean getBooleanProperty(final Properties props, final String propKey, final boolean defaultValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        final String propValue = props.getProperty(propKey);
        boolean value;
        if (null == propValue) {
            value = defaultValue;
        }
        else {
            value = Boolean.valueOf(propValue);
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey, value);
        return value;
    }
    
    private void setObjectProperty(final Properties props, final String propKey, final Object propValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "set" + propKey);
        }
        if (null != propValue) {
            ((Hashtable<String, Object>)props).put(propKey, propValue);
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "set" + propKey);
    }
    
    private Object getObjectProperty(final Properties props, final String propKey, final Object defaultValue) {
        if (SQLServerDataSource.loggerExternal.isLoggable(Level.FINER)) {
            SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "get" + propKey);
        }
        Object propValue = ((Hashtable<K, Object>)props).get(propKey);
        if (null == propValue) {
            propValue = defaultValue;
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "get" + propKey);
        return propValue;
    }
    
    SQLServerConnection getConnectionInternal(final String username, final String password, final SQLServerPooledConnection pooledConnection) throws SQLServerException {
        if (this.trustStorePasswordStripped) {
            SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_referencingFailedTSP"), null, true);
        }
        Properties userSuppliedProps;
        if (null != username || null != password) {
            userSuppliedProps = (Properties)this.connectionProps.clone();
            userSuppliedProps.remove(SQLServerDriverStringProperty.USER.toString());
            userSuppliedProps.remove(SQLServerDriverStringProperty.PASSWORD.toString());
            if (null != username) {
                ((Hashtable<String, String>)userSuppliedProps).put(SQLServerDriverStringProperty.USER.toString(), username);
            }
            if (null != password) {
                ((Hashtable<String, String>)userSuppliedProps).put(SQLServerDriverStringProperty.PASSWORD.toString(), password);
            }
        }
        else {
            userSuppliedProps = this.connectionProps;
        }
        Properties mergedProps;
        if (null != this.dataSourceURL) {
            final Properties urlProps = Util.parseUrl(this.dataSourceURL, SQLServerDataSource.dsLogger);
            if (null == urlProps) {
                SQLServerException.makeFromDriverError(null, null, SQLServerException.getErrString("R_errorConnectionString"), null, true);
            }
            mergedProps = SQLServerDriver.mergeURLAndSuppliedProperties(urlProps, userSuppliedProps);
        }
        else {
            mergedProps = userSuppliedProps;
        }
        if (SQLServerDataSource.dsLogger.isLoggable(Level.FINER)) {
            SQLServerDataSource.dsLogger.finer(this.toString() + " Begin create new connection.");
        }
        SQLServerConnection result = null;
        if (Util.use43Wrapper()) {
            result = new SQLServerConnection43(this.toString());
        }
        else {
            result = new SQLServerConnection(this.toString());
        }
        result.connect(mergedProps, pooledConnection);
        if (SQLServerDataSource.dsLogger.isLoggable(Level.FINER)) {
            SQLServerDataSource.dsLogger.finer(this.toString() + " End create new connection " + result.toString());
        }
        return result;
    }
    
    @Override
    public Reference getReference() {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "getReference");
        final Reference ref = this.getReferenceInternal("com.microsoft.sqlserver.jdbc.SQLServerDataSource");
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "getReference", ref);
        return ref;
    }
    
    Reference getReferenceInternal(final String dataSourceClassString) {
        if (SQLServerDataSource.dsLogger.isLoggable(Level.FINER)) {
            SQLServerDataSource.dsLogger.finer(this.toString() + " creating reference for " + dataSourceClassString + ".");
        }
        final Reference ref = new Reference(this.getClass().getName(), "com.microsoft.sqlserver.jdbc.SQLServerDataSourceObjectFactory", null);
        if (null != dataSourceClassString) {
            ref.add(new StringRefAddr("class", dataSourceClassString));
        }
        if (this.trustStorePasswordStripped) {
            ref.add(new StringRefAddr("trustStorePasswordStripped", "true"));
        }
        final Enumeration<?> e = ((Hashtable<?, V>)this.connectionProps).keys();
        while (e.hasMoreElements()) {
            final String propertyName = (String)e.nextElement();
            if (propertyName.equals(SQLServerDriverStringProperty.TRUST_STORE_PASSWORD.toString())) {
                assert !this.trustStorePasswordStripped;
                ref.add(new StringRefAddr("trustStorePasswordStripped", "true"));
            }
            else {
                if (propertyName.contains(SQLServerDriverStringProperty.PASSWORD.toString())) {
                    continue;
                }
                ref.add(new StringRefAddr(propertyName, this.connectionProps.getProperty(propertyName)));
            }
        }
        if (null != this.dataSourceURL) {
            ref.add(new StringRefAddr("dataSourceURL", this.dataSourceURL));
        }
        if (null != this.dataSourceDescription) {
            ref.add(new StringRefAddr("dataSourceDescription", this.dataSourceDescription));
        }
        return ref;
    }
    
    void initializeFromReference(final Reference ref) {
        final Enumeration<?> e = ref.getAll();
        while (e.hasMoreElements()) {
            final StringRefAddr addr = (StringRefAddr)e.nextElement();
            final String propertyName = addr.getType();
            final String propertyValue = (String)addr.getContent();
            if ("dataSourceURL".equals(propertyName)) {
                this.dataSourceURL = propertyValue;
            }
            else if ("dataSourceDescription".equals(propertyName)) {
                this.dataSourceDescription = propertyValue;
            }
            else if ("trustStorePasswordStripped".equals(propertyName)) {
                this.trustStorePasswordStripped = true;
            }
            else {
                if ("class".equals(propertyName)) {
                    continue;
                }
                this.connectionProps.setProperty(propertyName, propertyValue);
            }
        }
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "isWrapperFor", iface);
        final boolean f = iface.isInstance(this);
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "isWrapperFor", f);
        return f;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        SQLServerDataSource.loggerExternal.entering(this.getClassNameLogging(), "unwrap", iface);
        T t;
        try {
            t = iface.cast(this);
        }
        catch (final ClassCastException e) {
            throw new SQLServerException(e.getMessage(), e);
        }
        SQLServerDataSource.loggerExternal.exiting(this.getClassNameLogging(), "unwrap", t);
        return t;
    }
    
    private static int nextDataSourceID() {
        return SQLServerDataSource.baseDataSourceID.incrementAndGet();
    }
    
    private Object writeReplace() throws ObjectStreamException {
        return new SerializationProxy(this);
    }
    
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("");
    }
    
    static {
        dsLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.internals.SQLServerDataSource");
        loggerExternal = Logger.getLogger("com.microsoft.sqlserver.jdbc.DataSource");
        parentLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc");
        baseDataSourceID = new AtomicInteger(0);
    }
    
    private static class SerializationProxy implements Serializable
    {
        private final Reference ref;
        private static final long serialVersionUID = 654661379542314226L;
        
        SerializationProxy(final SQLServerDataSource ds) {
            this.ref = ds.getReferenceInternal(null);
        }
        
        private Object readResolve() {
            final SQLServerDataSource ds = new SQLServerDataSource();
            ds.initializeFromReference(this.ref);
            return ds;
        }
    }
}
