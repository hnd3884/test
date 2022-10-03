package com.zoho.security.wafad;

import java.util.Iterator;
import java.util.ArrayList;
import com.zoho.security.attackdiscovery.AttackDiscovery;
import com.zoho.security.attackdiscovery.AttackDiscoveryName;
import java.util.List;

public abstract class AttackDiscoveryProvider
{
    protected List<AttackDiscoveryName> getAttackDiscoveryInfos() {
        return null;
    }
    
    protected abstract List<Class<? extends AttackDiscovery>> getAttackDiscoveryClasses();
    
    final List<AttackDiscoveryName> getAllAttackDiscoveryInfos() {
        final List<AttackDiscoveryName> allAttackDiscoveryInfos = new ArrayList<AttackDiscoveryName>();
        final List<Class<? extends AttackDiscovery>> attackDiscoveryClasses = this.getAttackDiscoveryClasses();
        if (attackDiscoveryClasses != null) {
            for (final Class<? extends AttackDiscovery> attackDiscoveryClass : attackDiscoveryClasses) {
                allAttackDiscoveryInfos.add((AttackDiscoveryName)new AttackDiscoveryName() {
                    public Class<? extends AttackDiscovery> getAttackDiscoveryClass() {
                        return attackDiscoveryClass;
                    }
                });
            }
        }
        final List<AttackDiscoveryName> attackDiscoveryInfosFromProvider = this.getAttackDiscoveryInfos();
        if (attackDiscoveryInfosFromProvider != null) {
            for (final AttackDiscoveryName attackDiscoveryInfo : attackDiscoveryInfosFromProvider) {
                allAttackDiscoveryInfos.add(attackDiscoveryInfo);
            }
        }
        return allAttackDiscoveryInfos.isEmpty() ? null : allAttackDiscoveryInfos;
    }
}
