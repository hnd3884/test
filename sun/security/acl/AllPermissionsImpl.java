package sun.security.acl;

import java.security.acl.Permission;

public class AllPermissionsImpl extends PermissionImpl
{
    public AllPermissionsImpl(final String s) {
        super(s);
    }
    
    public boolean equals(final Permission permission) {
        return true;
    }
}
