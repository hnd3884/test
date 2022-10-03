package org.apache.catalina.connector;

import java.io.Serializable;
import java.security.Principal;

public class CoyotePrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 1L;
    protected final String name;
    
    public CoyotePrincipal(final String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return "CoyotePrincipal[" + this.name + "]";
    }
}
