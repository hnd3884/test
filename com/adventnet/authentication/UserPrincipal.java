package com.adventnet.authentication;

import java.security.Principal;

public class UserPrincipal implements Principal
{
    String name;
    
    public UserPrincipal(final String username) {
        this.name = new String("");
        this.name = username;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean isEqual = false;
        if (obj instanceof UserPrincipal) {
            isEqual = ((UserPrincipal)obj).getName().equals(this.name);
        }
        return isEqual;
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    @Override
    public String toString() {
        return "UserPrincipal : *****";
    }
}
