package com.sun.java.browser.net;

import java.net.URL;
import java.io.IOException;

public class ProxyService
{
    private static ProxyServiceProvider provider;
    
    public static void setProvider(final ProxyServiceProvider provider) throws IOException {
        if (null == ProxyService.provider) {
            ProxyService.provider = provider;
            return;
        }
        throw new IOException("Proxy service provider has already been set.");
    }
    
    public static ProxyInfo[] getProxyInfo(final URL url) throws IOException {
        if (null == ProxyService.provider) {
            throw new IOException("Proxy service provider is not yet set");
        }
        return ProxyService.provider.getProxyInfo(url);
    }
    
    static {
        ProxyService.provider = null;
    }
}
