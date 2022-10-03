package com.btr.proxy.selector.whitelist;

import java.util.Iterator;
import com.btr.proxy.util.ProxyUtil;
import java.net.Proxy;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import com.btr.proxy.util.UriFilter;
import java.util.List;
import java.net.ProxySelector;

public class ProxyBypassListSelector extends ProxySelector
{
    private ProxySelector delegate;
    private List<UriFilter> whiteListFilter;
    
    public ProxyBypassListSelector(final List<UriFilter> whiteListFilter, final ProxySelector proxySelector) {
        if (whiteListFilter == null) {
            throw new NullPointerException("Whitelist must not be null.");
        }
        if (proxySelector == null) {
            throw new NullPointerException("ProxySelector must not be null.");
        }
        this.delegate = proxySelector;
        this.whiteListFilter = whiteListFilter;
    }
    
    public ProxyBypassListSelector(final String whiteList, final ProxySelector proxySelector) {
        this(new DefaultWhiteListParser().parseWhiteList(whiteList), proxySelector);
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        for (final UriFilter filter : this.whiteListFilter) {
            if (filter.accept(uri)) {
                return ProxyUtil.noProxyList();
            }
        }
        return this.delegate.select(uri);
    }
}
