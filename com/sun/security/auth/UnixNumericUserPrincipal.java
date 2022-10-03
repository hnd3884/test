package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class UnixNumericUserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -4329764253802397821L;
    private String name;
    
    public UnixNumericUserPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException(new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources")).format(new Object[] { "name" }));
        }
        this.name = name;
    }
    
    public UnixNumericUserPrincipal(final long n) {
        this.name = new Long(n).toString();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public long longValue() {
        return new Long(this.name);
    }
    
    @Override
    public String toString() {
        return new MessageFormat(ResourcesMgr.getString("UnixNumericUserPrincipal.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof UnixNumericUserPrincipal && this.getName().equals(((UnixNumericUserPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}
