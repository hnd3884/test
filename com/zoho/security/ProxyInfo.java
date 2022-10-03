package com.zoho.security;

public class ProxyInfo
{
    private String clientIP;
    private String proxyIP;
    private String proxyName;
    
    public ProxyInfo(final String clientIP, final String proxyIP, final String proxyName) {
        this.clientIP = clientIP;
        this.proxyIP = proxyIP;
        this.proxyName = proxyName;
    }
    
    public String getClientIP() {
        return this.clientIP;
    }
    
    public String getProxyIP() {
        return this.proxyIP;
    }
    
    public String getProxyName() {
        return this.proxyName;
    }
}
