package com.microsoft.sqlserver.jdbc;

import java.util.logging.Level;
import java.util.logging.Logger;

class CacheClear implements Runnable
{
    private String keylookupValue;
    private static Logger aeLogger;
    
    CacheClear(final String keylookupValue) {
        this.keylookupValue = keylookupValue;
    }
    
    @Override
    public void run() {
        synchronized (SQLServerSymmetricKeyCache.lock) {
            final SQLServerSymmetricKeyCache instance = SQLServerSymmetricKeyCache.getInstance();
            if (instance.getCache().containsKey(this.keylookupValue)) {
                instance.getCache().get(this.keylookupValue).zeroOutKey();
                instance.getCache().remove(this.keylookupValue);
                if (CacheClear.aeLogger.isLoggable(Level.FINE)) {
                    CacheClear.aeLogger.fine("Removed encryption key from cache...");
                }
            }
        }
    }
    
    static {
        CacheClear.aeLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.CacheClear");
    }
}
