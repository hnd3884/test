package com.btr.proxy.selector.misc;

import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.net.Proxy;
import java.util.List;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.net.ProxySelector;

public class BufferedProxySelector extends ProxySelector
{
    private ProxySelector delegate;
    private ConcurrentHashMap<String, CacheEntry> cache;
    private int maxSize;
    private long ttl;
    
    public BufferedProxySelector(final int maxSize, final long ttl, final ProxySelector delegate) {
        this.cache = new ConcurrentHashMap<String, CacheEntry>();
        this.maxSize = maxSize;
        this.delegate = delegate;
        this.ttl = ttl;
    }
    
    @Override
    public void connectFailed(final URI uri, final SocketAddress sa, final IOException ioe) {
        this.delegate.connectFailed(uri, sa, ioe);
    }
    
    @Override
    public List<Proxy> select(final URI uri) {
        final String cacheKey = uri.toString();
        CacheEntry entry = this.cache.get(cacheKey);
        if (entry == null || entry.isExpired()) {
            final List<Proxy> result = this.delegate.select(uri);
            entry = new CacheEntry(result, System.nanoTime() + this.ttl * 1000L * 1000L);
            synchronized (this.cache) {
                if (this.cache.size() >= this.maxSize) {
                    this.purgeCache();
                }
                this.cache.put(cacheKey, entry);
            }
        }
        return entry.result;
    }
    
    private void purgeCache() {
        boolean removedOne = false;
        Map.Entry<String, CacheEntry> oldest = null;
        final Set<Map.Entry<String, CacheEntry>> entries = this.cache.entrySet();
        final Iterator<Map.Entry<String, CacheEntry>> it = entries.iterator();
        while (it.hasNext()) {
            final Map.Entry<String, CacheEntry> entry = it.next();
            if (entry.getValue().isExpired()) {
                it.remove();
                removedOne = true;
            }
            else {
                if (oldest != null && entry.getValue().expireAt >= oldest.getValue().expireAt) {
                    continue;
                }
                oldest = entry;
            }
        }
        if (!removedOne && oldest != null) {
            this.cache.remove(oldest.getKey());
        }
    }
    
    private static class CacheEntry
    {
        List<Proxy> result;
        long expireAt;
        
        public CacheEntry(final List<Proxy> r, final long expireAt) {
            (this.result = new ArrayList<Proxy>(r.size())).addAll(r);
            this.result = Collections.unmodifiableList((List<? extends Proxy>)this.result);
            this.expireAt = expireAt;
        }
        
        public boolean isExpired() {
            return System.nanoTime() >= this.expireAt;
        }
    }
}
