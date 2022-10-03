package com.sun.jndi.url.ldap;

import javax.naming.NamingException;
import javax.naming.CompositeName;
import com.sun.jndi.ldap.LdapCtx;
import com.sun.jndi.ldap.LdapURL;
import javax.naming.spi.ResolveResult;
import com.sun.jndi.ldap.LdapCtxFactory;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class ldapURLContextFactory implements ObjectFactory
{
    @Override
    public Object getObjectInstance(final Object o, final Name name, final Context context, final Hashtable<?, ?> hashtable) throws Exception {
        if (o == null) {
            return new ldapURLContext(hashtable);
        }
        return LdapCtxFactory.getLdapCtxInstance(o, hashtable);
    }
    
    static ResolveResult getUsingURLIgnoreRootDN(final String s, final Hashtable<?, ?> hashtable) throws NamingException {
        final LdapURL ldapURL = new LdapURL(s);
        final LdapCtx ldapCtx = new LdapCtx("", ldapURL.getHost(), ldapURL.getPort(), hashtable, ldapURL.useSsl());
        final String s2 = (ldapURL.getDN() != null) ? ldapURL.getDN() : "";
        final CompositeName compositeName = new CompositeName();
        if (!"".equals(s2)) {
            compositeName.add(s2);
        }
        return new ResolveResult(ldapCtx, compositeName);
    }
}
