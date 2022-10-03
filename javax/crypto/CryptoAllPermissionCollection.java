package javax.crypto;

import java.util.Vector;
import java.util.Enumeration;
import java.security.Permission;
import java.io.Serializable;
import java.security.PermissionCollection;

final class CryptoAllPermissionCollection extends PermissionCollection implements Serializable
{
    private static final long serialVersionUID = 7450076868380144072L;
    private boolean all_allowed;
    
    CryptoAllPermissionCollection() {
        this.all_allowed = false;
    }
    
    @Override
    public void add(final Permission permission) {
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        if (permission != CryptoAllPermission.INSTANCE) {
            return;
        }
        this.all_allowed = true;
    }
    
    @Override
    public boolean implies(final Permission permission) {
        return permission instanceof CryptoPermission && this.all_allowed;
    }
    
    @Override
    public Enumeration<Permission> elements() {
        final Vector vector = new Vector(1);
        if (this.all_allowed) {
            vector.add(CryptoAllPermission.INSTANCE);
        }
        return vector.elements();
    }
}
