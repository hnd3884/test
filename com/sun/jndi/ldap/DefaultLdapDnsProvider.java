package com.sun.jndi.ldap;

import javax.naming.NamingException;
import java.util.List;
import java.util.ArrayList;
import com.sun.jndi.ldap.spi.LdapDnsProviderResult;
import java.util.Optional;
import java.util.Map;

public class DefaultLdapDnsProvider
{
    public Optional<LdapDnsProviderResult> lookupEndpoints(final String s, final Map<?, ?> map) throws NamingException {
        if (s == null || map == null) {
            throw new NullPointerException();
        }
        final ArrayList list = new ArrayList();
        final LdapURL ldapURL = new LdapURL(s);
        final String dn = ldapURL.getDN();
        final String host = ldapURL.getHost();
        final int port = ldapURL.getPort();
        String mapDnToDomainName;
        final String[] ldapService;
        if (host == null && port == -1 && dn != null && (mapDnToDomainName = ServiceLocator.mapDnToDomainName(dn)) != null && (ldapService = ServiceLocator.getLdapService(mapDnToDomainName, map)) != null) {
            final String string = ldapURL.getScheme() + "://";
            final String query = ldapURL.getQuery();
            final String string2 = ldapURL.getPath() + ((query != null) ? query : "");
            final String[] array = ldapService;
            for (int length = array.length, i = 0; i < length; ++i) {
                list.add(string + array[i] + string2);
            }
        }
        else {
            mapDnToDomainName = "";
            list.add(s);
        }
        final LdapDnsProviderResult ldapDnsProviderResult = new LdapDnsProviderResult(mapDnToDomainName, list);
        if (ldapDnsProviderResult.getEndpoints().isEmpty() && ldapDnsProviderResult.getDomainName().isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(ldapDnsProviderResult);
    }
}
