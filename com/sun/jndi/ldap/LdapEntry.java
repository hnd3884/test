package com.sun.jndi.ldap;

import javax.naming.ldap.Control;
import java.util.Vector;
import javax.naming.directory.Attributes;

final class LdapEntry
{
    String DN;
    Attributes attributes;
    Vector<Control> respCtls;
    
    LdapEntry(final String dn, final Attributes attributes) {
        this.respCtls = null;
        this.DN = dn;
        this.attributes = attributes;
    }
    
    LdapEntry(final String dn, final Attributes attributes, final Vector<Control> respCtls) {
        this.respCtls = null;
        this.DN = dn;
        this.attributes = attributes;
        this.respCtls = respCtls;
    }
}
