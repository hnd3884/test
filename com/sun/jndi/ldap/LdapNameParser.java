package com.sun.jndi.ldap;

import javax.naming.NamingException;
import javax.naming.ldap.LdapName;
import javax.naming.Name;
import javax.naming.NameParser;

class LdapNameParser implements NameParser
{
    public LdapNameParser() {
    }
    
    @Override
    public Name parse(final String s) throws NamingException {
        return new LdapName(s);
    }
}
