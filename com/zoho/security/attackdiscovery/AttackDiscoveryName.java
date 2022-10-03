package com.zoho.security.attackdiscovery;

public interface AttackDiscoveryName
{
    default String getName() {
        return (this.getAttackDiscoveryClass() != null) ? this.getAttackDiscoveryClass().getName() : null;
    }
    
    Class<? extends AttackDiscovery> getAttackDiscoveryClass();
}
