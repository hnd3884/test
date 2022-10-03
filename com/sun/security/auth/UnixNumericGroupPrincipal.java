package com.sun.security.auth;

import java.text.MessageFormat;
import sun.security.util.ResourcesMgr;
import jdk.Exported;
import java.io.Serializable;
import java.security.Principal;

@Exported
public class UnixNumericGroupPrincipal implements Principal, Serializable
{
    private static final long serialVersionUID = 3941535899328403223L;
    private String name;
    private boolean primaryGroup;
    
    public UnixNumericGroupPrincipal(final String name, final boolean primaryGroup) {
        if (name == null) {
            throw new NullPointerException(new MessageFormat(ResourcesMgr.getString("invalid.null.input.value", "sun.security.util.AuthResources")).format(new Object[] { "name" }));
        }
        this.name = name;
        this.primaryGroup = primaryGroup;
    }
    
    public UnixNumericGroupPrincipal(final long n, final boolean primaryGroup) {
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
        if (this.primaryGroup) {
            return new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Primary.Group.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
        }
        return new MessageFormat(ResourcesMgr.getString("UnixNumericGroupPrincipal.Supplementary.Group.name", "sun.security.util.AuthResources")).format(new Object[] { this.name });
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnixNumericGroupPrincipal)) {
            return false;
        }
        final UnixNumericGroupPrincipal unixNumericGroupPrincipal = (UnixNumericGroupPrincipal)o;
        return this.getName().equals(unixNumericGroupPrincipal.getName()) && this.isPrimaryGroup() == unixNumericGroupPrincipal.isPrimaryGroup();
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
