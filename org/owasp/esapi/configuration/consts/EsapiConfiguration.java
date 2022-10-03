package org.owasp.esapi.configuration.consts;

public enum EsapiConfiguration
{
    OPSTEAM_ESAPI_CFG("org.owasp.esapi.opsteam", 1), 
    DEVTEAM_ESAPI_CFG("org.owasp.esapi.devteam", 2);
    
    String configName;
    int priority;
    
    private EsapiConfiguration(final String configName, final int priority) {
        this.configName = configName;
        this.priority = priority;
    }
    
    public String getConfigName() {
        return this.configName;
    }
    
    public int getPriority() {
        return this.priority;
    }
}
