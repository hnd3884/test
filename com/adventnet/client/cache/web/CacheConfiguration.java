package com.adventnet.client.cache.web;

import java.util.ArrayList;
import java.io.Serializable;
import com.adventnet.persistence.cache.DataObjectCache;
import com.adventnet.client.util.web.WebConstants;

public class CacheConfiguration implements WebConstants, DataObjectCache.CacheComparator, Serializable
{
    private long accountId;
    private String configId;
    private String cacheKey;
    private String cacheType;
    private String cacheScope;
    private Object cachedData;
    private ArrayList tablesList;
    private String singleSignOnId;
    
    public CacheConfiguration(final long accountId, final String configId, final String singleSignOnId, final String cacheType, final String cacheScope, final Object cachedData, final ArrayList tablesList) {
        this.accountId = -1L;
        this.configId = null;
        this.cacheKey = null;
        this.cacheType = null;
        this.cacheScope = null;
        this.cachedData = null;
        this.tablesList = null;
        this.singleSignOnId = null;
        this.accountId = accountId;
        this.configId = configId;
        this.singleSignOnId = singleSignOnId;
        this.cacheType = cacheType;
        this.cacheScope = cacheScope;
        this.cachedData = cachedData;
        this.tablesList = tablesList;
    }
    
    public long getAccountId() {
        return this.accountId;
    }
    
    public String getConfigId() {
        return this.configId;
    }
    
    public String getSingleSignOnId() {
        return this.singleSignOnId;
    }
    
    public String getCacheType() {
        return this.cacheType;
    }
    
    public String getCacheScope() {
        return this.cacheScope;
    }
    
    public Object getCachedData() {
        return this.cachedData;
    }
    
    public void setCachedData(final Object cachedData) {
        this.cachedData = cachedData;
    }
    
    public ArrayList getTablesList() {
        return this.tablesList;
    }
    
    public boolean compare(final DataObjectCache.CacheComparator arg) {
        boolean result = true;
        final CacheConfiguration configuration = (CacheConfiguration)arg;
        if (configuration.getAccountId() > 0L && configuration.getAccountId() != this.accountId) {
            result = false;
        }
        if (configuration.getCacheType() != null && !configuration.getCacheType().equals(this.cacheType)) {
            result = false;
        }
        if (configuration.getCacheScope() != null && !configuration.getCacheScope().equals(this.cacheScope)) {
            result = false;
        }
        if (configuration.getConfigId() != null && !configuration.getConfigId().equals(this.configId)) {
            result = false;
        }
        if (configuration.getSingleSignOnId() != null && !configuration.getSingleSignOnId().equals(this.singleSignOnId)) {
            result = false;
        }
        return result;
    }
    
    public String getCacheKey() {
        if (this.cacheKey == null) {
            final StringBuilder cacheKeyBuf = new StringBuilder("CACHE_KEY");
            cacheKeyBuf.append(":");
            if (this.cacheScope.equals("SESSION")) {
                cacheKeyBuf.append(this.singleSignOnId);
                cacheKeyBuf.append(":");
            }
            else if (this.accountId != -1L) {
                cacheKeyBuf.append(this.accountId);
                cacheKeyBuf.append(":");
            }
            cacheKeyBuf.append(this.configId);
            cacheKeyBuf.append(":");
            cacheKeyBuf.append(this.cacheType);
            this.cacheKey = cacheKeyBuf.toString();
        }
        return this.cacheKey;
    }
}
