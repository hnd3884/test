package com.sun.security.auth;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported(false)
@Deprecated
public class SolarisNumericUserPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = -3178578484679887104L;
    private static final ResourceBundle rb;
    private String name;
    
    public SolarisNumericUserPrincipal(final String name) {
        if (name == null) {
            throw new NullPointerException(SolarisNumericUserPrincipal.rb.getString("provided.null.name"));
        }
        this.name = name;
    }
    
    public SolarisNumericUserPrincipal(final long n) {
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
        return SolarisNumericUserPrincipal.rb.getString("SolarisNumericUserPrincipal.") + this.name;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && (this == o || (o instanceof SolarisNumericUserPrincipal && this.getName().equals(((SolarisNumericUserPrincipal)o).getName())));
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
