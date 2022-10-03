package com.sun.org.glassfish.external.probe.provider;

public interface StatsProviderManagerDelegate
{
    void register(final StatsProviderInfo p0);
    
    void unregister(final Object p0);
    
    boolean hasListeners(final String p0);
}
