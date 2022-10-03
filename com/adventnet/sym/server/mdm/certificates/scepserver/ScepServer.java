package com.adventnet.sym.server.mdm.certificates.scepserver;

import com.me.mdm.api.core.certificate.CredentialCertificate;

public class ScepServer
{
    private Long scepServerId;
    private long customerId;
    private ScepServerType serverType;
    private String serverName;
    private String serverUrl;
    private CredentialCertificate certificate;
    
    public ScepServer() {
    }
    
    public ScepServer(final Long scepServerId, final long customerId, final ScepServerType scepServerType, final String serverName, final String serverUrl, final CredentialCertificate certificate) {
        this.scepServerId = scepServerId;
        this.customerId = customerId;
        this.serverType = scepServerType;
        this.serverName = serverName;
        this.serverUrl = serverUrl;
        this.certificate = certificate;
    }
    
    public Long getScepServerId() {
        return this.scepServerId;
    }
    
    public void setScepServerId(final Long scepServerId) {
        this.scepServerId = scepServerId;
    }
    
    public long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final long customerId) {
        this.customerId = customerId;
    }
    
    public ScepServerType getServerType() {
        return this.serverType;
    }
    
    public void setServerType(final ScepServerType serverType) {
        this.serverType = serverType;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerUrl() {
        return this.serverUrl;
    }
    
    public void setServerUrl(final String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public CredentialCertificate getCertificate() {
        return this.certificate;
    }
    
    public void setCertificate(final CredentialCertificate certificate) {
        this.certificate = certificate;
    }
}
