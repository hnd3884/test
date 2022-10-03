package java.security;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Enumeration;

final class PermissionsEnumerator implements Enumeration<Permission>
{
    private Iterator<PermissionCollection> perms;
    private Enumeration<Permission> permset;
    
    PermissionsEnumerator(final Iterator<PermissionCollection> perms) {
        this.perms = perms;
        this.permset = this.getNextEnumWithMore();
    }
    
    @Override
    public boolean hasMoreElements() {
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
    public Permission nextElement() {
        if (this.hasMoreElements()) {
            return this.permset.nextElement();
        }
        throw new NoSuchElementException("PermissionsEnumerator");
    }
    
    private Enumeration<Permission> getNextEnumWithMore() {
        while (this.perms.hasNext()) {
            final Enumeration<Permission> elements = this.perms.next().elements();
            if (elements.hasMoreElements()) {
                return elements;
            }
        }
        return null;
    }
}
