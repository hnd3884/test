package com.btr.proxy.selector.direct;

import com.btr.proxy.util.ProxyUtil;
import java.net.Proxy;
import java.util.List;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.net.ProxySelector;

public class NoProxySelector extends ProxySelector
{
    private static NoProxySelector instance;
    
    private NoProxySelector() {
    }
    
    public static synchronized NoProxySelector getInstance() {
        if (NoProxySelector.instance == null) {
            NoProxySelector.instance = new NoProxySelector();
        }
        return NoProxySelector.instance;
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        return ProxyUtil.noProxyList();
    }
}
