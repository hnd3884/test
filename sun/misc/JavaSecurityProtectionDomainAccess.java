package sun.misc;

import java.security.PermissionCollection;
import java.security.ProtectionDomain;

public interface JavaSecurityProtectionDomainAccess
{
    ProtectionDomainCache getProtectionDomainCache();
    
    boolean getStaticPermissionsField(final ProtectionDomain p0);
    
    public interface ProtectionDomainCache
    {
        void put(final ProtectionDomain p0, final PermissionCollection p1);
        
        PermissionCollection get(final ProtectionDomain p0);
    }
}
