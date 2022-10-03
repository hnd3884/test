package com.microsoft.sqlserver.jdbc;

import org.ietf.jgss.GSSCredential;
import javax.sql.CommonDataSource;

public interface ISQLServerDataSource extends CommonDataSource
{
    void setApplicationIntent(final String p0);
    
    String getApplicationIntent();
    
    void setApplicationName(final String p0);
    
    String getApplicationName();
    
    void setDatabaseName(final String p0);
    
    String getDatabaseName();
    
    void setInstanceName(final String p0);
    
    String getInstanceName();
    
    void setIntegratedSecurity(final boolean p0);
    
    void setLastUpdateCount(final boolean p0);
    
    boolean getLastUpdateCount();
    
    void setEncrypt(final boolean p0);
    
    boolean getEncrypt();
    
    void setTransparentNetworkIPResolution(final boolean p0);
    
    boolean getTransparentNetworkIPResolution();
    
    void setTrustServerCertificate(final boolean p0);
    
    boolean getTrustServerCertificate();
    
    void setTrustStoreType(final String p0);
    
    String getTrustStoreType();
    
    void setTrustStore(final String p0);
    
    String getTrustStore();
    
    void setTrustStorePassword(final String p0);
    
    void setHostNameInCertificate(final String p0);
    
    String getHostNameInCertificate();
    
    void setLockTimeout(final int p0);
    
    int getLockTimeout();
    
    void setPassword(final String p0);
    
    void setPortNumber(final int p0);
    
    int getPortNumber();
    
    void setSelectMethod(final String p0);
    
    String getSelectMethod();
    
    void setResponseBuffering(final String p0);
    
    String getResponseBuffering();
    
    void setSendTimeAsDatetime(final boolean p0);
    
    boolean getSendTimeAsDatetime();
    
    void setSendStringParametersAsUnicode(final boolean p0);
    
    boolean getSendStringParametersAsUnicode();
    
    void setServerNameAsACE(final boolean p0);
    
    boolean getServerNameAsACE();
    
    void setServerName(final String p0);
    
    String getServerName();
    
    void setFailoverPartner(final String p0);
    
    String getFailoverPartner();
    
    void setMultiSubnetFailover(final boolean p0);
    
    boolean getMultiSubnetFailover();
    
    void setUser(final String p0);
    
    String getUser();
    
    void setWorkstationID(final String p0);
    
    String getWorkstationID();
    
    void setXopenStates(final boolean p0);
    
    boolean getXopenStates();
    
    void setURL(final String p0);
    
    String getURL();
    
    void setDescription(final String p0);
    
    String getDescription();
    
    void setPacketSize(final int p0);
    
    int getPacketSize();
    
    void setAuthenticationScheme(final String p0);
    
    void setAuthentication(final String p0);
    
    String getAuthentication();
    
    void setServerSpn(final String p0);
    
    String getServerSpn();
    
    void setGSSCredentials(final GSSCredential p0);
    
    GSSCredential getGSSCredentials();
    
    void setAccessToken(final String p0);
    
    String getAccessToken();
    
    void setColumnEncryptionSetting(final String p0);
    
    String getColumnEncryptionSetting();
    
    void setKeyStoreAuthentication(final String p0);
    
    String getKeyStoreAuthentication();
    
    void setKeyStoreSecret(final String p0);
    
    void setKeyStoreLocation(final String p0);
    
    String getKeyStoreLocation();
    
    void setQueryTimeout(final int p0);
    
    int getQueryTimeout();
    
    void setCancelQueryTimeout(final int p0);
    
    int getCancelQueryTimeout();
    
    void setEnablePrepareOnFirstPreparedStatementCall(final boolean p0);
    
    boolean getEnablePrepareOnFirstPreparedStatementCall();
    
    void setServerPreparedStatementDiscardThreshold(final int p0);
    
    int getServerPreparedStatementDiscardThreshold();
    
    void setStatementPoolingCacheSize(final int p0);
    
    int getStatementPoolingCacheSize();
    
    void setDisableStatementPooling(final boolean p0);
    
    boolean getDisableStatementPooling();
    
    void setSocketTimeout(final int p0);
    
    int getSocketTimeout();
    
    void setJASSConfigurationName(final String p0);
    
    String getJASSConfigurationName();
    
    void setFIPS(final boolean p0);
    
    boolean getFIPS();
    
    void setSSLProtocol(final String p0);
    
    String getSSLProtocol();
    
    void setTrustManagerClass(final String p0);
    
    String getTrustManagerClass();
    
    void setTrustManagerConstructorArg(final String p0);
    
    String getTrustManagerConstructorArg();
    
    boolean getUseBulkCopyForBatchInsert();
    
    void setUseBulkCopyForBatchInsert(final boolean p0);
    
    void setMSIClientId(final String p0);
    
    String getMSIClientId();
    
    void setKeyVaultProviderClientId(final String p0);
    
    String getKeyVaultProviderClientId();
    
    void setKeyVaultProviderClientKey(final String p0);
    
    String getDomain();
    
    void setDomain(final String p0);
    
    boolean getUseFmtOnly();
    
    void setUseFmtOnly(final boolean p0);
    
    String getEnclaveAttestationUrl();
    
    void setEnclaveAttestationUrl(final String p0);
    
    String getEnclaveAttestationProtocol();
    
    void setEnclaveAttestationProtocol(final String p0);
}
