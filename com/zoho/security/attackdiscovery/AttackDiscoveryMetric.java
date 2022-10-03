package com.zoho.security.attackdiscovery;

public interface AttackDiscoveryMetric
{
    void record(final Class<? extends AttackDiscovery> p0, final String p1, final String p2, final long p3, final boolean p4);
}
