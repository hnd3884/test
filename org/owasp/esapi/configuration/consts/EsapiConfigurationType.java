package org.owasp.esapi.configuration.consts;

public enum EsapiConfigurationType
{
    PROPERTIES("properties"), 
    XML("xml");
    
    String typeName;
    
    private EsapiConfigurationType(final String typeName) {
        this.typeName = typeName;
    }
    
    public String getTypeName() {
        return this.typeName;
    }
}
