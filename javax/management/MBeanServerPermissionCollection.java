package javax.management;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.security.Permission;
import java.security.PermissionCollection;

class MBeanServerPermissionCollection extends PermissionCollection
{
    private MBeanServerPermission collectionPermission;
    private static final long serialVersionUID = -5661980843569388590L;
    
    @Override
    public synchronized void add(final Permission permission) {
        if (!(permission instanceof MBeanServerPermission)) {
            throw new IllegalArgumentException("Permission not an MBeanServerPermission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("Read-only permission collection");
        }
        final MBeanServerPermission collectionPermission = (MBeanServerPermission)permission;
        if (this.collectionPermission == null) {
            this.collectionPermission = collectionPermission;
        }
        else if (!this.collectionPermission.implies(permission)) {
            this.collectionPermission = new MBeanServerPermission(this.collectionPermission.mask | collectionPermission.mask);
        }
    }
    
    @Override
    public synchronized boolean implies(final Permission permission) {
        return this.collectionPermission != null && this.collectionPermission.implies(permission);
    }
    
    @Override
    public synchronized Enumeration<Permission> elements() {
        Object o;
        if (this.collectionPermission == null) {
            o = Collections.emptySet();
        }
        else {
            o = Collections.singleton(this.collectionPermission);
        }
        return Collections.enumeration((Collection<Permission>)o);
    }
}
