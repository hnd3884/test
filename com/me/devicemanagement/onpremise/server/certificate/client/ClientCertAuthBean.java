package com.me.devicemanagement.onpremise.server.certificate.client;

import java.util.Map;
import java.util.HashMap;

public class ClientCertAuthBean
{
    private static ClientCertAuthBean clientCertAuthBean;
    private Boolean isRedisEnabled;
    private HashMap<String, String> clientAuthDataMap;
    private HashMap<String, String> dsClientAuthDataMap;
    private Integer agentTomcatPort;
    private Map clientCertAuthConfig;
    private Boolean isClientCertificateAuthenticationEnabled;
    private boolean isClientCertAuthForceDisabled;
    private String sgsClientCertSerial;
    
    private ClientCertAuthBean() {
        this.isRedisEnabled = false;
        this.clientAuthDataMap = new HashMap<String, String>();
        this.dsClientAuthDataMap = new HashMap<String, String>();
    }
    
    public static ClientCertAuthBean getInstance() {
        if (ClientCertAuthBean.clientCertAuthBean == null) {
            ClientCertAuthBean.clientCertAuthBean = new ClientCertAuthBean();
        }
        return ClientCertAuthBean.clientCertAuthBean;
    }
    
    public void addEntryToClientCertAuthMap(final String key, final String value) {
        ClientCertAuthBean.clientCertAuthBean.clientAuthDataMap.put(key, value);
    }
    
    public void addEntryToDSClientCertAuthMap(final String key, final String value) {
        ClientCertAuthBean.clientCertAuthBean.dsClientAuthDataMap.put(key, value);
    }
    
    public void removeEntryToClientCertAuthMap(final String key) {
        ClientCertAuthBean.clientCertAuthBean.clientAuthDataMap.remove(key);
    }
    
    public void removeEntryToDSClientCertAuthMap(final String key) {
        ClientCertAuthBean.clientCertAuthBean.dsClientAuthDataMap.remove(key);
    }
    
    public String getEntryFromClientCertAuthMap(final String key) {
        return this.clientAuthDataMap.get(key);
    }
    
    public String getEntryFromDSClientCertAuthMap(final String key) {
        return this.dsClientAuthDataMap.get(key);
    }
    
    public Boolean getIsRedisEnabled() {
        return this.isRedisEnabled;
    }
    
    public void setIsRedisEnabled(final Boolean redisEnabled) {
        this.isRedisEnabled = redisEnabled;
    }
    
    public Integer getAgentTomcatPort() {
        return this.agentTomcatPort;
    }
    
    public void setAgentTomcatPort(final Integer agentTomcatPort) {
        this.agentTomcatPort = agentTomcatPort;
    }
    
    public void clearAuthDataMap() {
        this.clientAuthDataMap.clear();
    }
    
    public void clearDSAuthDataMap() {
        this.dsClientAuthDataMap.clear();
    }
    
    public int getAgentAuthMapSize() {
        return this.clientAuthDataMap.size();
    }
    
    public int getDSAuthMapSize() {
        return this.dsClientAuthDataMap.size();
    }
    
    public int getDSMapSize() {
        return this.dsClientAuthDataMap.size();
    }
    
    public Map getClientCertAuthConfig() {
        return this.clientCertAuthConfig;
    }
    
    public void setClientCertAuthConfig(final Map clientCertAuthConfig) {
        this.clientCertAuthConfig = clientCertAuthConfig;
    }
    
    public Boolean getIsClientCertificateAuthenticationEnabled() {
        return this.isClientCertificateAuthenticationEnabled;
    }
    
    public void setIsClientCertificateAuthenticationEnabled(final Boolean clientCertificateAuthenticationEnabled) {
        this.isClientCertificateAuthenticationEnabled = clientCertificateAuthenticationEnabled;
    }
    
    public Boolean isClientCertAuthForceDisabled() {
        return this.isClientCertAuthForceDisabled;
    }
    
    public void setIsClientCertAuthForceDisabled(final boolean forceDisableClientCertAuth) {
        this.isClientCertAuthForceDisabled = forceDisableClientCertAuth;
    }
    
    public String getSgsClientCertSerial() {
        return this.sgsClientCertSerial;
    }
    
    public void setSgsClientCertSerial(final String sgsClientCertSerial) {
        this.sgsClientCertSerial = sgsClientCertSerial;
    }
    
    public Boolean isValidPort(final int requestPort) {
        return this.agentTomcatPort == requestPort;
    }
}
