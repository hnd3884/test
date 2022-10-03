package com.me.ems.onpremise.security.securegatewayserver.api.v1.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecureGatewayServerCertificate
{
    String webServerName;
    String serverCertificate;
    String serverKey;
    String intermediateCertificate;
    @JsonProperty("ClientRootCA.crt")
    String clientRootCACertificate;
    @JsonProperty("sgsAgentClientCertificateHeaderName")
    String clientCertificateHeaderName;
    @JsonProperty("isClientCertificateVerificationEnabled")
    Boolean isClientCertificateVerificationEnabled;
    Map<String, String> sgsClientCertificateKeyPair;
    
    public String getWebServerName() {
        return this.webServerName;
    }
    
    public void setWebServerName(final String webServerName) {
        this.webServerName = webServerName;
    }
    
    public String getServerCertificate() {
        return this.serverCertificate;
    }
    
    public void setServerCertificate(final String serverCertificate) {
        this.serverCertificate = serverCertificate;
    }
    
    public String getServerKey() {
        return this.serverKey;
    }
    
    public void setServerKey(final String serverKey) {
        this.serverKey = serverKey;
    }
    
    public String getIntermediateCertificate() {
        return this.intermediateCertificate;
    }
    
    public void setIntermediateCertificate(final String intermediateCertificate) {
        this.intermediateCertificate = intermediateCertificate;
    }
    
    public void setClientRootCACertificate(final String clientRootCACertificate) {
        this.clientRootCACertificate = clientRootCACertificate;
    }
    
    public void setClientCertificateHeaderName(final String clientCertificateHeaderName) {
        this.clientCertificateHeaderName = clientCertificateHeaderName;
    }
    
    public void setClientCertificateVerificationEnabled(final Boolean clientCertificateVerificationEnabled) {
        this.isClientCertificateVerificationEnabled = clientCertificateVerificationEnabled;
    }
    
    public void setSgsClientCertificateKeyPair(final Map<String, String> sgsClientCertificateKeyPair) {
        this.sgsClientCertificateKeyPair = sgsClientCertificateKeyPair;
    }
    
    public String getClientRootCACertificate() {
        return this.clientRootCACertificate;
    }
    
    public String getClientCertificateHeaderName() {
        return this.clientCertificateHeaderName;
    }
    
    public Boolean getClientCertificateVerificationEnabled() {
        return this.isClientCertificateVerificationEnabled;
    }
    
    public Map<String, String> getSgsClientCertificateKeyPair() {
        return this.sgsClientCertificateKeyPair;
    }
    
    @Override
    public String toString() {
        return "SecureGatewayServerCertificate{webServerName='" + this.webServerName + '\'' + ", serverCertificate='" + this.serverCertificate + '\'' + ", intermediateCertificate='" + this.intermediateCertificate + '\'' + ", clientRootCACertificate='" + this.clientRootCACertificate + '\'' + ", clientCertificateHeaderName='" + this.clientCertificateHeaderName + '\'' + ", isClientCertificateVerificationEnabled=" + this.isClientCertificateVerificationEnabled + '}';
    }
}
