package com.theorem.radius3.login;

import java.io.Serializable;
import java.security.Principal;

public final class RADIUSPrincipal implements Principal, Serializable
{
    private String a;
    
    public RADIUSPrincipal(final String a) {
        if (a == null) {
            throw new NullPointerException("illegal null input");
        }
        this.a = a;
    }
    
    public final String getName() {
        return this.a;
    }
    
    public final String toString() {
        return "RADIUSPrincipal:  " + this.a;
    }
    
    public final boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof RADIUSPrincipal && this.getName().equals(((RADIUSPrincipal)o).getName())));
    }
    
    public final int hashCode() {
        return this.a.hashCode();
    }
}
