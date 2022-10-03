package com.sun.security.auth;

import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public final class UserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 892106070870210969L;
    private final String name;
    
    public UserPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException("null name is illegal");
        }
        this.name = name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof UserPrincipal && this.name.equals(((UserPrincipal)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
}
