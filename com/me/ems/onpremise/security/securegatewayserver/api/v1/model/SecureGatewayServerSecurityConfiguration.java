package com.me.ems.onpremise.security.securegatewayserver.api.v1.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecureGatewayServerSecurityConfiguration
{
    String ciphers;
    Boolean isTLSv2Enabled;
    String sslHonorCipherOrder;
    
    public String getCiphers() {
        return this.ciphers;
    }
    
    public void setCiphers(final String ciphers) {
        this.ciphers = ciphers;
    }
    
    @JsonProperty("isTLSv2Enabled")
    public Boolean getTLSv2Enabled() {
        return this.isTLSv2Enabled;
    }
    
    @JsonProperty("isTLSv2Enabled")
    public void setTLSv2Enabled(final Boolean isTLSv2Enabled) {
        this.isTLSv2Enabled = isTLSv2Enabled;
    }
    
    public String getSSLHonorCipherOrder() {
        return this.sslHonorCipherOrder;
    }
    
    @Override
    public String toString() {
        return "SecureGatewayServerSecurityConfiguration{ciphers='" + this.ciphers + '\'' + ", isTLSv2Enabled=" + this.isTLSv2Enabled + ", sslHonorCipherOrder='" + this.sslHonorCipherOrder + '\'' + '}';
    }
    
    public void setSSLHonorCipherOrder(final String sslHonorCipherOrder) {
        this.sslHonorCipherOrder = sslHonorCipherOrder;
    }
}
