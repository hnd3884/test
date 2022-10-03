package com.btr.proxy.search.desktop.win;

import com.btr.proxy.util.ProxyException;
import com.btr.proxy.search.browser.ie.IEProxySearchStrategy;
import java.net.ProxySelector;
import com.btr.proxy.search.ProxySearchStrategy;

public class WinProxySearchStrategy implements ProxySearchStrategy
{
    public ProxySelector getProxySelector() throws ProxyException {
        return new IEProxySearchStrategy().getProxySelector();
    }
    
    public Win32IESettings readSettings() {
        return new IEProxySearchStrategy().readSettings();
    }
}
