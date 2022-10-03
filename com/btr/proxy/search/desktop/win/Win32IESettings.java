package com.btr.proxy.search.desktop.win;

public class Win32IESettings
{
    private boolean autoDetect;
    private String autoConfigUrl;
    private String proxy;
    private String proxyBypass;
    
    public Win32IESettings(final boolean autoDetect, final String autoConfigUrl, final String proxy, final String proxyBypass) {
        this.autoDetect = autoDetect;
        this.autoConfigUrl = autoConfigUrl;
        this.proxy = proxy;
        this.proxyBypass = proxyBypass;
    }
    
    public boolean isAutoDetect() {
        return this.autoDetect;
    }
    
    public String getAutoConfigUrl() {
        return this.autoConfigUrl;
    }
    
    public String getProxy() {
        return this.proxy;
    }
    
    public String getProxyBypass() {
        return this.proxyBypass;
    }
}
