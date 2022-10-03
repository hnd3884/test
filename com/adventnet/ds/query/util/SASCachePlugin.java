package com.adventnet.ds.query.util;

public abstract class SASCachePlugin
{
    private static SASCachePlugin plugin;
    
    public abstract Object get(final Object p0) throws Exception;
    
    public abstract void put(final Object p0, final Object p1) throws Exception;
    
    public abstract void invalidate(final Object p0) throws Exception;
    
    public static void setSASCachePluginImpl(final SASCachePlugin plugin) {
        SASCachePlugin.plugin = plugin;
    }
    
    public static SASCachePlugin getSASCachePluginImpl() {
        return SASCachePlugin.plugin;
    }
}
