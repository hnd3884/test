package com.sun.security.auth;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ResourceBundle;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported(false)
@Deprecated
public class SolarisNumericGroupPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 2345199581042573224L;
    private static final ResourceBundle rb;
    private String name;
    private boolean primaryGroup;
    
    public SolarisNumericGroupPrincipal(final String name, final boolean primaryGroup) {
        if (name == null) {
            throw new NullPointerException(SolarisNumericGroupPrincipal.rb.getString("provided.null.name"));
        }
        this.name = name;
        this.primaryGroup = primaryGroup;
    }
    
    public SolarisNumericGroupPrincipal(final long n, final boolean primaryGroup) {
        this.name = new Long(n).toString();
        this.primaryGroup = primaryGroup;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public long longValue() {
        return new Long(this.name);
    }
    
    public boolean isPrimaryGroup() {
        return this.primaryGroup;
    }
    
    @Override
    public String toString() {
        return this.primaryGroup ? (SolarisNumericGroupPrincipal.rb.getString("SolarisNumericGroupPrincipal.Primary.Group.") + this.name) : (SolarisNumericGroupPrincipal.rb.getString("SolarisNumericGroupPrincipal.Supplementary.Group.") + this.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof SolarisNumericGroupPrincipal)) {
            return false;
        }
        final SolarisNumericGroupPrincipal solarisNumericGroupPrincipal = (SolarisNumericGroupPrincipal)o;
        return this.getName().equals(solarisNumericGroupPrincipal.getName()) && this.isPrimaryGroup() == solarisNumericGroupPrincipal.isPrimaryGroup();
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
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
