package com.adventnet.sym.server.mdm;

public class MDMProxy
{
    private String proxyServerHost;
    private int proxyServerPort;
    private String proxyUsername;
    private String proxyPassword;
    
    private MDMProxy() {
        this.proxyUsername = "";
        this.proxyPassword = "";
    }
    
    public MDMProxy(final String proxyServerHost, final int proxyServerPort) {
        this.proxyUsername = "";
        this.proxyPassword = "";
        this.proxyServerHost = proxyServerHost;
        this.proxyServerPort = proxyServerPort;
    }
    
    public MDMProxy(final String proxyServerHost, final int proxyServerPort, final String proxyUsername, final String proxyPassword) {
        this.proxyUsername = "";
        this.proxyPassword = "";
        this.proxyServerHost = proxyServerHost;
        this.proxyServerPort = proxyServerPort;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
    }
    
    public void setProxyUsername(final String proxyUsername) {
        this.proxyUsername = proxyUsername;
    }
    
    public void setProxyPassword(final String proxyPassword) {
        this.proxyPassword = proxyPassword;
    }
    
    public String getProxyServerHost() {
        return this.proxyServerHost;
    }
    
    public int getProxyServerPort() {
        return this.proxyServerPort;
    }
    
    public String getProxyUsername() {
        return this.proxyUsername;
    }
    
    public String getProxyPassword() {
        return this.proxyPassword;
    }
}
