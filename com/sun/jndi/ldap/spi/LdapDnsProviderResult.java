package com.sun.jndi.ldap.spi;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public final class LdapDnsProviderResult
{
    private final String domainName;
    private final List<String> endpoints;
    
    public LdapDnsProviderResult(final String s, final List<String> list) {
        this.domainName = ((s == null) ? "" : s);
        this.endpoints = new ArrayList<String>(list);
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public List<String> getEndpoints() {
        return this.endpoints;
    }
}
