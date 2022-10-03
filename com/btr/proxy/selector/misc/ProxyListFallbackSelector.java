package com.btr.proxy.selector.misc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.net.Proxy;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ProxySelector;

public class ProxyListFallbackSelector extends ProxySelector
{
    private static final int DEFAULT_RETRY_DELAY = 600000;
    private ProxySelector delegate;
    private ConcurrentHashMap<SocketAddress, Long> failedDelayCache;
    private long retryAfterMs;
    
    public ProxyListFallbackSelector(final ProxySelector delegate) {
        this(600000L, delegate);
    }
    
    public ProxyListFallbackSelector(final long retryAfterMs, final ProxySelector delegate) {
        this.failedDelayCache = new ConcurrentHashMap<SocketAddress, Long>();
        this.delegate = delegate;
        this.retryAfterMs = retryAfterMs;
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
        this.failedDelayCache.put(sa, System.currentTimeMillis());
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        this.cleanupCache();
        final List<Proxy> proxyList = this.delegate.select(uri);
        final List<Proxy> result = this.filterUnresponsiveProxiesFromList(proxyList);
        return result;
    }
    
    private void cleanupCache() {
        final Iterator<Map.Entry<SocketAddress, Long>> it = this.failedDelayCache.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry<SocketAddress, Long> e = it.next();
            final Long lastFailTime = e.getValue();
            if (this.retryDelayHasPassedBy(lastFailTime)) {
                it.remove();
            }
        }
    }
    
    private List<Proxy> filterUnresponsiveProxiesFromList(final List<Proxy> proxyList) {
        if (this.failedDelayCache.isEmpty()) {
            return proxyList;
        }
        final List<Proxy> result = new ArrayList<Proxy>(proxyList.size());
        for (final Proxy proxy : proxyList) {
            if (this.isDirect(proxy) || this.isNotUnresponsive(proxy)) {
                result.add(proxy);
            }
        }
        return result;
    }
    
    private boolean isDirect(final Proxy proxy) {
        return Proxy.NO_PROXY.equals(proxy);
    }
    
    private boolean isNotUnresponsive(final Proxy proxy) {
        final Long lastFailTime = this.failedDelayCache.get(proxy.address());
        return this.retryDelayHasPassedBy(lastFailTime);
    }
    
    private boolean retryDelayHasPassedBy(final Long lastFailTime) {
        return lastFailTime == null || lastFailTime + this.retryAfterMs < System.currentTimeMillis();
    }
    
    final void setRetryAfterMs(final long retryAfterMs) {
        this.retryAfterMs = retryAfterMs;
    }
}
