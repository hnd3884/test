package com.microsoft.sqlserver.jdbc;

enum SQLServerDriverStringProperty
{
    APPLICATION_INTENT("applicationIntent", ApplicationIntent.READ_WRITE.toString()), 
    APPLICATION_NAME("applicationName", "Microsoft JDBC Driver for SQL Server"), 
    DATABASE_NAME("databaseName", ""), 
    FAILOVER_PARTNER("failoverPartner", ""), 
    HOSTNAME_IN_CERTIFICATE("hostNameInCertificate", ""), 
    INSTANCE_NAME("instanceName", ""), 
    JAAS_CONFIG_NAME("jaasConfigurationName", "SQLJDBCDriver"), 
    PASSWORD("password", ""), 
    RESPONSE_BUFFERING("responseBuffering", "adaptive"), 
    SELECT_METHOD("selectMethod", "direct"), 
    DOMAIN("domain", ""), 
    SERVER_NAME("serverName", ""), 
    SERVER_SPN("serverSpn", ""), 
    TRUST_STORE_TYPE("trustStoreType", "JKS"), 
    TRUST_STORE("trustStore", ""), 
    TRUST_STORE_PASSWORD("trustStorePassword", ""), 
    TRUST_MANAGER_CLASS("trustManagerClass", ""), 
    TRUST_MANAGER_CONSTRUCTOR_ARG("trustManagerConstructorArg", ""), 
    USER("user", ""), 
    WORKSTATION_ID("workstationID", ""), 
    AUTHENTICATION_SCHEME("authenticationScheme", AuthenticationScheme.nativeAuthentication.toString()), 
    AUTHENTICATION("authentication", SqlAuthentication.NotSpecified.toString()), 
    ACCESS_TOKEN("accessToken", ""), 
    COLUMN_ENCRYPTION("columnEncryptionSetting", ColumnEncryptionSetting.Disabled.toString()), 
    ENCLAVE_ATTESTATION_URL("enclaveAttestationUrl", ""), 
    ENCLAVE_ATTESTATION_PROTOCOL("enclaveAttestationProtocol", ""), 
    KEY_STORE_AUTHENTICATION("keyStoreAuthentication", ""), 
    KEY_STORE_SECRET("keyStoreSecret", ""), 
    KEY_STORE_LOCATION("keyStoreLocation", ""), 
    SSL_PROTOCOL("sslProtocol", SSLProtocol.TLS.toString()), 
    MSI_CLIENT_ID("msiClientId", ""), 
    KEY_VAULT_PROVIDER_CLIENT_ID("keyVaultProviderClientId", ""), 
    KEY_VAULT_PROVIDER_CLIENT_KEY("keyVaultProviderClientKey", "");
    
    private final String name;
    private final String defaultValue;
    
    private SQLServerDriverStringProperty(final String name, final String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }
    
    String getDefaultValue() {
        return this.defaultValue;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
