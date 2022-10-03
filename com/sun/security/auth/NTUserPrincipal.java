package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class NTUserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -8737649811939033735L;
    private String name;
    
    public NTUserPrincipal(final String name) {
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
        return new MessageFormat(ResourcesMgr.getString("NTUserPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof NTUserPrincipal && this.name.equals(((NTUserPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}
