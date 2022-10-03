package com.me.ems.onpremise.security.certificate.api.core.handlers;

import java.util.Map;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;

public class CertificateCacheHandler
{
    private static CertificateCacheHandler handler;
    
    public static CertificateCacheHandler getInstance() {
        if (CertificateCacheHandler.handler == null) {
            CertificateCacheHandler.handler = new CertificateCacheHandler();
        }
        return CertificateCacheHandler.handler;
    }
    
    public void put(final String key, final Object value) {
        HashMap map = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("CERTIFICATE_CACHE", 6);
        map = ((map == null) ? new HashMap() : map);
        map.put(key, value);
        ApiFactoryProvider.getCacheAccessAPI().putCache("CERTIFICATE_CACHE", (Object)map, 6);
    }
    
    public void putAll(final HashMap hashmap) {
        HashMap map = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("CERTIFICATE_CACHE", 6);
        map = ((map == null) ? new HashMap() : map);
        map.putAll(hashmap);
        ApiFactoryProvider.getCacheAccessAPI().putCache("CERTIFICATE_CACHE", (Object)map, 6);
    }
    
    public Object get(final String key) {
        final HashMap map = (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("CERTIFICATE_CACHE", 6);
        return (map == null) ? null : map.get(key);
    }
    
    public HashMap getAll() {
        return (HashMap)ApiFactoryProvider.getCacheAccessAPI().getCache("CERTIFICATE_CACHE", 6);
    }
    
    public void removeAll() {
        ApiFactoryProvider.getCacheAccessAPI().removeCache("CERTIFICATE_CACHE", 6);
    }
    
    static {
        CertificateCacheHandler.handler = null;
    }
}
