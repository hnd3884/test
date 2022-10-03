package sun.security.util;

import java.security.Permission;

public interface PermissionFactory<T extends Permission>
{
    T newPermission(final String p0);
}
