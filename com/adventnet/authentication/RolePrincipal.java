package com.adventnet.authentication;

import java.security.Principal;

public class RolePrincipal implements Principal
{
    String name;
    
    public RolePrincipal(final String roleName) {
        this.name = new String("");
        this.name = roleName;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean isEqual = false;
        if (obj instanceof RolePrincipal) {
            isEqual = ((RolePrincipal)obj).getName().equals(this.name);
        }
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return "RolePrincipal : " + this.name;
    }
}
