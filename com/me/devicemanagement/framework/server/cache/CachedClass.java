package com.me.devicemanagement.framework.server.cache;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

public class CachedClass
{
    private static Map<String, Class> cache;
    
    public static Class forName(final String className) throws ClassNotFoundException {
        Class classObj = CachedClass.cache.get(className);
        if (classObj != null) {
            return classObj;
        }
        try {
            classObj = Class.forName(className);
            CachedClass.cache.put(className, classObj);
        }
        catch (final Exception ex) {
            Logger.getLogger(CachedClass.class.getName()).logp(Level.SEVERE, "CachedClass", "forName", "Exception occurred {0} ", ex);
        }
        return classObj;
    }
    
    static {
        CachedClass.cache = new HashMap<String, Class>();
    }
}
