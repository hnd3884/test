package com.sun.jndi.dns;

import javax.naming.NamingException;
import javax.naming.Name;
import javax.naming.NameParser;

class DnsNameParser implements NameParser
{
    @Override
    public Name parse(final String s) throws NamingException {
        return new DnsName(s);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof DnsNameParser;
    }
    
    @Override
    public int hashCode() {
        return DnsNameParser.class.hashCode() + 1;
    }
}
