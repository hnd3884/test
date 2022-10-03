package com.me.devicemanagement.framework.server.orglock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FairLockUtil
{
    public ConcurrentMap getMap() {
        return new ConcurrentHashMap();
    }
    
    public String getUniqueId() {
        return "globalLock";
    }
}
