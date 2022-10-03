package javax.crypto;

import java.util.NoSuchElementException;
import java.security.PermissionCollection;
import java.security.Permission;
import java.util.Enumeration;

final class PermissionsEnumerator implements Enumeration<Permission>
{
    private Enumeration<PermissionCollection> perms;
    private Enumeration<Permission> permset;
    
    PermissionsEnumerator(final Enumeration<PermissionCollection> perms) {
        this.perms = perms;
        this.permset = this.getNextEnumWithMore();
    }
    
    @Override
    public synchronized boolean hasMoreElements() {
        if (this.permset == null) {
            return false;
        }
        if (this.permset.hasMoreElements()) {
            return true;
        }
        this.permset = this.getNextEnumWithMore();
        return this.permset != null;
    }
    
    @Override
    public synchronized Permission nextElement() {
        if (this.hasMoreElements()) {
            return this.permset.nextElement();
        }
        throw new NoSuchElementException("PermissionsEnumerator");
    }
    
    private Enumeration<Permission> getNextEnumWithMore() {
        while (this.perms.hasMoreElements()) {
            final Enumeration<Permission> elements = this.perms.nextElement().elements();
            if (elements.hasMoreElements()) {
                return elements;
            }
        }
        return null;
    }
}
