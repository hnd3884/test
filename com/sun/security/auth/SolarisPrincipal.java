package com.sun.security.auth;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported(false)
@Deprecated
public class SolarisPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -7840670002439379038L;
    private static final ResourceBundle rb;
    private String name;
    
    public SolarisPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException(SolarisPrincipal.rb.getString("provided.null.name"));
        }
        this.name = name;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String toString() {
        return SolarisPrincipal.rb.getString("SolarisPrincipal.") + this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof SolarisPrincipal && this.getName().equals(((SolarisPrincipal)o).getName())));
    }
    
    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
    
    static {
        rb = AccessController.doPrivileged((PrivilegedAction<ResourceBundle>)new PrivilegedAction<ResourceBundle>() {
            @Override
            public ResourceBundle run() {
                return ResourceBundle.getBundle("sun.security.util.AuthResources");
            }
        });
    }
}
