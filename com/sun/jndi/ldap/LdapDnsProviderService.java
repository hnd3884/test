package com.sun.jndi.ldap;

import javax.naming.NamingException;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import com.sun.jndi.ldap.spi.LdapDnsProviderResult;
import java.util.Hashtable;
import java.security.AccessControlContext;
import java.security.AccessController;
import sun.security.util.SecurityConstants;
import java.security.Permission;
import com.sun.jndi.ldap.spi.LdapDnsProvider;
import java.util.ServiceLoader;

final class LdapDnsProviderService
{
    private static volatile LdapDnsProviderService service;
    private static final Object LOCK;
    private final ServiceLoader<LdapDnsProvider> providers;
    
    private LdapDnsProviderService() {
        if (System.getSecurityManager() == null) {
            this.providers = ServiceLoader.load(LdapDnsProvider.class, ClassLoader.getSystemClassLoader());
        }
        else {
            this.providers = AccessController.doPrivileged(() -> ServiceLoader.load(LdapDnsProvider.class, ClassLoader.getSystemClassLoader()), null, new RuntimePermission("ldapDnsProvider"), SecurityConstants.GET_CLASSLOADER_PERMISSION);
        }
    }
    
    static LdapDnsProviderService getInstance() {
        if (LdapDnsProviderService.service != null) {
            return LdapDnsProviderService.service;
        }
        synchronized (LdapDnsProviderService.LOCK) {
            if (LdapDnsProviderService.service != null) {
                return LdapDnsProviderService.service;
            }
            LdapDnsProviderService.service = new LdapDnsProviderService();
        }
        return LdapDnsProviderService.service;
    }
    
    LdapDnsProviderResult lookupEndpoints(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        LdapDnsProviderResult ldapDnsProviderResult = null;
        final Hashtable hashtable2 = new Hashtable((Map<? extends K, ? extends V>)hashtable);
        synchronized (LdapDnsProviderService.LOCK) {
            for (Iterator<LdapDnsProvider> iterator = this.providers.iterator(); ldapDnsProviderResult == null && iterator.hasNext(); ldapDnsProviderResult = iterator.next().lookupEndpoints(s, hashtable2).filter(ldapDnsProviderResult2 -> !ldapDnsProviderResult2.getEndpoints().isEmpty()).orElse(null)) {}
        }
        if (ldapDnsProviderResult == null) {
            return new DefaultLdapDnsProvider().lookupEndpoints(s, hashtable).orElse(new LdapDnsProviderResult("", Collections.emptyList()));
        }
        return ldapDnsProviderResult;
    }
    
    static {
        LOCK = new int[0];
    }
}
