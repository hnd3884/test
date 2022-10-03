package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverBooleanProperty
{
    DISABLE_STATEMENT_POOLING("disableStatementPooling", true), 
    ENCRYPT("encrypt", false), 
    INTEGRATED_SECURITY("integratedSecurity", false), 
    LAST_UPDATE_COUNT("lastUpdateCount", true), 
    MULTI_SUBNET_FAILOVER("multiSubnetFailover", false), 
    SERVER_NAME_AS_ACE("serverNameAsACE", false), 
    SEND_STRING_PARAMETERS_AS_UNICODE("sendStringParametersAsUnicode", true), 
    SEND_TIME_AS_DATETIME("sendTimeAsDatetime", true), 
    TRANSPARENT_NETWORK_IP_RESOLUTION("TransparentNetworkIPResolution", true), 
    TRUST_SERVER_CERTIFICATE("trustServerCertificate", false), 
    XOPEN_STATES("xopenStates", false), 
    FIPS("fips", false), 
    ENABLE_PREPARE_ON_FIRST_PREPARED_STATEMENT("enablePrepareOnFirstPreparedStatementCall", false), 
    USE_BULK_COPY_FOR_BATCH_INSERT("useBulkCopyForBatchInsert", false), 
    USE_FMT_ONLY("useFmtOnly", false);
    
    private final String name;
    private final boolean defaultValue;
    
    private SQLServerDriverBooleanProperty(final String name, final boolean defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    boolean getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
