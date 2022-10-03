package com.microsoft.sqlserver.jdbc;

import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.util.Hashtable;

final class EnclaveSessionCache
{
    private Hashtable<String, EnclaveCacheEntry> sessionCache;
    
    EnclaveSessionCache() {
        this.sessionCache = new Hashtable<String, EnclaveCacheEntry>(0);
    }
    
    void addEntry(final String servername, final String attestationUrl, final BaseAttestationRequest b, final EnclaveSession e) {
        this.sessionCache.put(servername + attestationUrl, new EnclaveCacheEntry(b, e));
    }
    
    void removeEntry(final EnclaveSession e) {
        for (final Map.Entry<String, EnclaveCacheEntry> entry : this.sessionCache.entrySet()) {
            final EnclaveCacheEntry ece = entry.getValue();
            if (Arrays.equals(ece.getEnclaveSession().getSessionID(), e.getSessionID())) {
                this.sessionCache.remove(entry.getKey());
            }
        }
    }
    
    EnclaveCacheEntry getSession(final String key) {
        final EnclaveCacheEntry e = this.sessionCache.get(key);
        if (null != e && e.expired()) {
            this.sessionCache.remove(key);
            return null;
        }
        return e;
    }
}
