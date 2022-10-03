package com.zoho.security.wafad;

import com.zoho.security.wafad.instrument.sqli.MySQLiExceptionAttackDiscovery;
import com.zoho.security.wafad.instrument.sqli.MySQLInjectionAttackDiscovery;
import com.zoho.security.wafad.instrument.pathtraversal.ForwardRequestDispatcherAttackDiscovery;
import com.zoho.security.wafad.instrument.pathtraversal.IncludeRequestDispatcherAttackDiscovery;
import com.zoho.security.wafad.instrument.pathtraversal.NioFilePathTraversalAttackDiscovery;
import com.zoho.security.wafad.instrument.pathtraversal.FilePathTraversalAttackDiscovery;
import com.zoho.security.wafad.instrument.pathtraversal.PathTraversalAttackDiscovery;
import com.zoho.security.attackdiscovery.AttackDiscovery;
import com.zoho.security.attackdiscovery.AttackDiscoveryName;

public enum WAFAttackDiscoveries implements AttackDiscoveryName
{
    PATH_TRAVERSAL_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)PathTraversalAttackDiscovery.class), 
    FILE_PATH_TRAVERSAL_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)FilePathTraversalAttackDiscovery.class), 
    NIO_FILE_PATH_TRAVERSAL_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)NioFilePathTraversalAttackDiscovery.class), 
    INCLUDE_REQ_DISPATCHER_PATH_TRAVERSAL_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)IncludeRequestDispatcherAttackDiscovery.class), 
    FORWARD_REQ_DISPATCHER_PATH_TRAVERSAL_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)ForwardRequestDispatcherAttackDiscovery.class), 
    MYSQL_INJECRION_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)MySQLInjectionAttackDiscovery.class), 
    MYSQLI_EXCEPTION_ATTACK_DISCOVERY((Class<? extends AttackDiscovery>)MySQLiExceptionAttackDiscovery.class);
    
    private final Class<? extends AttackDiscovery> attackDiscoveryClass;
    
    private WAFAttackDiscoveries(final Class<? extends AttackDiscovery> attackDiscoveryClass) {
        this.attackDiscoveryClass = attackDiscoveryClass;
    }
    
    public String getName() {
        return this.name();
    }
    
    public Class<? extends AttackDiscovery> getAttackDiscoveryClass() {
        return this.attackDiscoveryClass;
    }
}
