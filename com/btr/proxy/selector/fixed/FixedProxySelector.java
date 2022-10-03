package com.btr.proxy.selector.fixed;

import java.io.IOException;
import java.net.URI;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.ArrayList;
import java.net.Proxy;
import java.util.List;
import java.net.ProxySelector;

public class FixedProxySelector extends ProxySelector
{
    private final List<Proxy> proxyList;
    
    public FixedProxySelector(final Proxy proxy) {
        final List<Proxy> list = new ArrayList<Proxy>(1);
        list.add(proxy);
        this.proxyList = Collections.unmodifiableList((List<? extends Proxy>)list);
    }
    
    public FixedProxySelector(final String proxyHost, final int proxyPort) {
        this(new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyHost, proxyPort)));
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        return this.proxyList;
    }
}
