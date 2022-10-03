package com.me.devicemanagement.framework.server.orglock;

import org.json.JSONException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;
import java.util.concurrent.ConcurrentMap;

public class FairLockFactory
{
    private static FairLockFactory fairLockFactory;
    public static ConcurrentMap<String, FairLock> fairLockMap;
    private static final Logger LOGGER;
    
    private FairLockFactory() {
    }
    
    public static FairLockFactory getInstance() {
        try {
            if (FairLockFactory.fairLockFactory == null) {
                FairLockFactory.fairLockFactory = new FairLockFactory();
                FairLockUtil fairLockUtil = (FairLockUtil)ApiFactoryProvider.getImplClassInstance("DM_FAIR_LOCK_MAP_CLASS");
                FairLock.enableLog = Boolean.parseBoolean("" + FrameworkConfigurations.getSpecificPropertyIfExists("Org_Lock_config", "enableFairLockLog", (Object)"false"));
                if (fairLockUtil == null) {
                    fairLockUtil = new FairLockUtil();
                }
                FairLockFactory.fairLockMap = fairLockUtil.getMap();
            }
        }
        catch (final JSONException e) {
            FairLockFactory.LOGGER.log(Level.SEVERE, "Exception while reading json in fairlock", (Throwable)e);
        }
        return FairLockFactory.fairLockFactory;
    }
    
    public FairLock getFairLockForOrg(final String key) {
        FairLock fairLock = null;
        fairLock = FairLockFactory.fairLockMap.computeIfAbsent(key, k -> new FairLock(key2));
        try {
            boolean addThreadLocal = false;
            addThreadLocal = Boolean.parseBoolean("" + FrameworkConfigurations.getSpecificPropertyIfExists("Org_Lock_config", "enableFairlockThreadLocal", (Object)"false"));
            if (addThreadLocal) {
                FairLockThreadLocal.setFairLockThreadLocal(key + "_TL", fairLock);
            }
        }
        catch (final JSONException e) {
            FairLockFactory.LOGGER.log(Level.SEVERE, "Exception while reading json in getFairLockForOrg", (Throwable)e);
        }
        return fairLock;
    }
    
    public static String getFairlock() {
        return FairLockFactory.fairLockMap.values().toString();
    }
    
    public static String getFairlockSize() {
        return FairLockFactory.fairLockMap.keySet().size() + "";
    }
    
    static {
        FairLockFactory.fairLockFactory = null;
        LOGGER = Logger.getLogger(FairLockFactory.class.getName());
    }
}
