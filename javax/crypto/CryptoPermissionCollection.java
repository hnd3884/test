package javax.crypto;

import java.util.Enumeration;
import java.security.Permission;
import java.util.Vector;
import java.io.Serializable;
import java.security.PermissionCollection;

final class CryptoPermissionCollection extends PermissionCollection implements Serializable
{
    private static final long serialVersionUID = -511215555898802763L;
    private Vector<Permission> permissions;
    
    CryptoPermissionCollection() {
        this.permissions = new Vector<Permission>(3);
    }
    
    @Override
    public void add(final Permission permission) {
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        if (!(permission instanceof CryptoPermission)) {
            return;
        }
        this.permissions.addElement(permission);
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof CryptoPermission)) {
            return false;
        }
        final CryptoPermission cryptoPermission = (CryptoPermission)permission;
        final Enumeration<Permission> elements = this.permissions.elements();
        while (elements.hasMoreElements()) {
            if (elements.nextElement().implies(cryptoPermission)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Enumeration<Permission> elements() {
        return this.permissions.elements();
    }
}
