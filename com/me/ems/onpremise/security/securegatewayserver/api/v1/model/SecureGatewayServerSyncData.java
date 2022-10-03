package com.me.ems.onpremise.security.securegatewayserver.api.v1.model;

public class SecureGatewayServerSyncData
{
    boolean enableUI;
    String proxyFileData;
    SecureGatewayServerSecurityConfiguration secureGatewayServerSecurityConfiguration;
    SecureGatewayServerCertificate secureGatewayServerCertificate;
    
    public boolean isEnableUI() {
        return this.enableUI;
    }
    
    public void setEnableUI(final boolean enableUI) {
        this.enableUI = enableUI;
    }
    
    public String getProxyFileData() {
        return this.proxyFileData;
    }
    
    public void setProxyFileData(final String proxyFileData) {
        this.proxyFileData = proxyFileData;
    }
    
    public SecureGatewayServerSecurityConfiguration getSecureGatewayServerSecurityConfiguration() {
        return this.secureGatewayServerSecurityConfiguration;
    }
    
    public void setSecureGatewayServerSecurityConfiguration(final SecureGatewayServerSecurityConfiguration secureGatewayServerSecurityConfiguration) {
        this.secureGatewayServerSecurityConfiguration = secureGatewayServerSecurityConfiguration;
    }
    
    public SecureGatewayServerCertificate getSecureGatewayServerCertificate() {
        return this.secureGatewayServerCertificate;
    }
    
    public void setSecureGatewayServerCertificate(final SecureGatewayServerCertificate secureGatewayServerCertificate) {
        this.secureGatewayServerCertificate = secureGatewayServerCertificate;
    }
}
