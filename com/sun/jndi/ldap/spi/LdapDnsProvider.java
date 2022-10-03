package com.sun.jndi.ldap.spi;

import javax.naming.NamingException;
import java.util.Optional;
import java.util.Map;
import java.security.Permission;

public abstract class LdapDnsProvider
{
    private static final RuntimePermission DNSPROVIDER_PERMISSION;
    
    protected LdapDnsProvider() {
        this(checkPermission());
    }
    
    private LdapDnsProvider(final Void void1) {
    }
    
    private static Void checkPermission() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkPermission(LdapDnsProvider.DNSPROVIDER_PERMISSION);
        }
        return null;
    }
    
    public abstract Optional<LdapDnsProviderResult> lookupEndpoints(final String p0, final Map<?, ?> p1) throws NamingException;
    
    static {
        DNSPROVIDER_PERMISSION = new RuntimePermission("ldapDnsProvider");
    }
}
