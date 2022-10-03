package sun.security.acl;

import java.security.acl.Permission;

public class PermissionImpl implements Permission
{
    private String permission;
    
    public PermissionImpl(final String permission) {
        this.permission = permission;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof Permission && this.permission.equals(((Permission)o).toString());
    }
    
    @Override
    public String toString() {
        return this.permission;
    }
    
    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }
}
