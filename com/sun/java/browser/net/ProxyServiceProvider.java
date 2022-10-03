package com.sun.java.browser.net;

import java.net.URL;

public interface ProxyServiceProvider
{
    ProxyInfo[] getProxyInfo(final URL p0);
}
