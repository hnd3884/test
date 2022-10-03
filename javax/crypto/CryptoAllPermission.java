package javax.crypto;

import java.security.PermissionCollection;
import java.security.Permission;

final class CryptoAllPermission extends CryptoPermission
{
    private static final long serialVersionUID = -5066513634293192112L;
    static final String ALG_NAME = "CryptoAllPermission";
    static final CryptoAllPermission INSTANCE;
    
    private CryptoAllPermission() {
        super("CryptoAllPermission");
    }
    
    @Override
    public boolean implies(final Permission permission) {
        return permission instanceof CryptoPermission;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == CryptoAllPermission.INSTANCE;
    }
    
    @Override
    public int hashCode() {
        return 1;
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new CryptoAllPermissionCollection();
    }
    
    static {
        INSTANCE = new CryptoAllPermission();
    }
}
