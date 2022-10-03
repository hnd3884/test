package com.sun.security.auth;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public final class LdapPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 6820120005580754861L;
    private final String nameString;
    private final LdapName name;
    
    public LdapPrincipal(final String nameString) throws InvalidNameException {
        if (nameString == null) {
            throw new NullPointerException("null name is illegal");
        }
        this.name = this.getLdapName(nameString);
        this.nameString = nameString;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof LdapPrincipal) {
            try {
                return this.name.equals(this.getLdapName(((LdapPrincipal)o).getName()));
            }
            catch (final InvalidNameException ex) {
                return false;
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String getName() {
        return this.nameString;
    }
    
    @Override
    public String toString() {
        return this.name.toString();
    }
    
    private LdapName getLdapName(final String s) throws InvalidNameException {
        return new LdapName(s);
    }
}
