package com.btr.proxy.selector.misc;

import java.net.Proxy;
import java.util.List;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import com.btr.proxy.selector.direct.NoProxySelector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.net.ProxySelector;

public class ProtocolDispatchSelector extends ProxySelector
{
    private Map<String, ProxySelector> selectors;
    private ProxySelector fallbackSelector;
    
    public ProtocolDispatchSelector() {
        this.selectors = new ConcurrentHashMap<String, ProxySelector>();
        this.fallbackSelector = NoProxySelector.getInstance();
    }
    
    public void setSelector(final String protocol, final ProxySelector selector) {
        if (protocol == null) {
            throw new NullPointerException("Protocol must not be null.");
        }
        if (selector == null) {
            throw new NullPointerException("Selector must not be null.");
        }
        this.selectors.put(protocol, selector);
    }
    
    public ProxySelector removeSelector(final String protocol) {
        return this.selectors.remove(protocol);
    }
    
    public ProxySelector getSelector(final String protocol) {
        return this.selectors.get(protocol);
    }
    
    public void setFallbackSelector(final ProxySelector selector) {
        if (selector == null) {
            throw new NullPointerException("Selector must not be null.");
        }
        this.fallbackSelector = selector;
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
        ProxySelector selector = this.fallbackSelector;
        final String protocol = uri.getScheme();
        if (protocol != null && this.selectors.get(protocol) != null) {
            selector = this.selectors.get(protocol);
        }
        selector.connectFailed(uri, sa, ioe);
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        ProxySelector selector = this.fallbackSelector;
        final String protocol = uri.getScheme();
        if (protocol != null && this.selectors.get(protocol) != null) {
            selector = this.selectors.get(protocol);
        }
        return selector.select(uri);
    }
}
