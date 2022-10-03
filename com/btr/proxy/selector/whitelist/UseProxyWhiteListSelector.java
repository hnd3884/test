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

public class UseProxyWhiteListSelector extends ProxySelector
{
    private ProxySelector delegate;
    private List<UriFilter> whiteListFilter;
    
    public UseProxyWhiteListSelector(final String whiteList, final ProxySelector proxySelector) {
        if (whiteList == null) {
            throw new NullPointerException("Whitelist must not be null.");
        }
        if (proxySelector == null) {
            throw new NullPointerException("ProxySelector must not be null.");
        }
        this.delegate = proxySelector;
        final WhiteListParser parser = new DefaultWhiteListParser();
        this.whiteListFilter = parser.parseWhiteList(whiteList);
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        for (final UriFilter filter : this.whiteListFilter) {
            if (filter.accept(uri)) {
                return this.delegate.select(uri);
            }
        }
        return ProxyUtil.noProxyList();
    }
}
