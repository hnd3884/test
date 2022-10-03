package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class UnixPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -2951667807323493631L;
    private String name;
    
    public UnixPrincipal(final String name) {
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
        return new MessageFormat(ResourcesMgr.getString("UnixPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof UnixPrincipal && this.getName().equals(((UnixPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
