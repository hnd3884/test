package com.sun.org.glassfish.external.probe.provider;

import java.util.Iterator;
import java.util.Vector;

public class StatsProviderManager
{
    static StatsProviderManagerDelegate spmd;
    static Vector<StatsProviderInfo> toBeRegistered;
    
    private StatsProviderManager() {
    }
    
    public static boolean register(final String configElement, final PluginPoint pp, final String subTreeRoot, final Object statsProvider) {
        return register(pp, configElement, subTreeRoot, statsProvider, null);
    }
    
    public static boolean register(final PluginPoint pp, final String configElement, final String subTreeRoot, final Object statsProvider, final String invokerId) {
        final StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
        return registerStatsProvider(spInfo);
    }
    
    public static boolean register(final String configElement, final PluginPoint pp, final String subTreeRoot, final Object statsProvider, final String configLevelStr) {
        return register(configElement, pp, subTreeRoot, statsProvider, configLevelStr, null);
    }
    
    public static boolean register(final String configElement, final PluginPoint pp, final String subTreeRoot, final Object statsProvider, final String configLevelStr, final String invokerId) {
        final StatsProviderInfo spInfo = new StatsProviderInfo(configElement, pp, subTreeRoot, statsProvider, invokerId);
        spInfo.setConfigLevel(configLevelStr);
        return registerStatsProvider(spInfo);
    }
    
    private static boolean registerStatsProvider(final StatsProviderInfo spInfo) {
        if (StatsProviderManager.spmd == null) {
            StatsProviderManager.toBeRegistered.add(spInfo);
            return false;
        }
        StatsProviderManager.spmd.register(spInfo);
        return true;
    }
    
    public static boolean unregister(final Object statsProvider) {
        if (StatsProviderManager.spmd == null) {
            for (final StatsProviderInfo spInfo : StatsProviderManager.toBeRegistered) {
                if (spInfo.getStatsProvider() == statsProvider) {
                    StatsProviderManager.toBeRegistered.remove(spInfo);
                    break;
                }
            }
            return false;
        }
        StatsProviderManager.spmd.unregister(statsProvider);
        return true;
    }
    
    public static boolean hasListeners(final String probeStr) {
        return StatsProviderManager.spmd != null && StatsProviderManager.spmd.hasListeners(probeStr);
    }
    
    public static void setStatsProviderManagerDelegate(final StatsProviderManagerDelegate lspmd) {
        if (lspmd == null) {
            return;
        }
        StatsProviderManager.spmd = lspmd;
        for (final StatsProviderInfo spInfo : StatsProviderManager.toBeRegistered) {
            StatsProviderManager.spmd.register(spInfo);
        }
        StatsProviderManager.toBeRegistered.clear();
    }
    
    static {
        StatsProviderManager.toBeRegistered = new Vector<StatsProviderInfo>();
    }
}
