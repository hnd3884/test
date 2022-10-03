package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class NTDomainPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -4408637351440771220L;
    private String name;
    
    public NTDomainPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException(new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources")).format(new Object[] { "name" }));
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("NTDomainPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTDomainPrincipal && this.name.equals(((NTDomainPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
