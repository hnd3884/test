package com.me.idps.core.sync.asynch;

class DirectorySyncThreadLocal
{
    static ThreadLocal<Long> domainID;
    static ThreadLocal<Long> syncToken;
    static ThreadLocal<Integer> clientID;
    
    static void clearSyncToken() {
        DirectorySyncThreadLocal.syncToken.remove();
    }
    
    static Long getSyncToken() {
        return DirectorySyncThreadLocal.syncToken.get();
    }
    
    static void setSyncToken(final Long syncTokenID) {
        DirectorySyncThreadLocal.syncToken.set(syncTokenID);
    }
    
    static void clearDomain() {
        DirectorySyncThreadLocal.domainID.remove();
    }
    
    static Long getDomainID() {
        return DirectorySyncThreadLocal.domainID.get();
    }
    
    static void setDomainID(final Long dmDomainID) {
        DirectorySyncThreadLocal.domainID.set(dmDomainID);
    }
    
    static void clearClientID() {
        DirectorySyncThreadLocal.clientID.remove();
    }
    
    static Integer getClientID() {
        return DirectorySyncThreadLocal.clientID.get();
    }
    
    static void setClientID(final Integer domainType) {
        DirectorySyncThreadLocal.clientID.set(domainType);
    }
    
    static {
        DirectorySyncThreadLocal.domainID = new ThreadLocal<Long>();
        DirectorySyncThreadLocal.syncToken = new ThreadLocal<Long>();
        DirectorySyncThreadLocal.clientID = new ThreadLocal<Integer>();
    }
}
