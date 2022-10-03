package sun.security.acl;

import java.security.acl.Group;
import java.util.Enumeration;
import java.security.acl.Permission;
import java.util.Vector;
import java.security.Principal;
import java.security.acl.AclEntry;

public class AclEntryImpl implements AclEntry
{
    private Principal user;
    private Vector<Permission> permissionSet;
    private boolean negative;
    
    public AclEntryImpl(final Principal user) {
        this.user = null;
        this.permissionSet = new Vector<Permission>(10, 10);
        this.negative = false;
        this.user = user;
    }
    
    public AclEntryImpl() {
        this.user = null;
        this.permissionSet = new Vector<Permission>(10, 10);
        this.negative = false;
    }
    
    @Override
    public boolean setPrincipal(final Principal user) {
        if (this.user != null) {
            return false;
        }
        this.user = user;
        return true;
    }
    
    @Override
    public void setNegativePermissions() {
        this.negative = true;
    }
    
    @Override
    public boolean isNegative() {
        return this.negative;
    }
    
    @Override
    public boolean addPermission(final Permission permission) {
        if (this.permissionSet.contains(permission)) {
            return false;
        }
        this.permissionSet.addElement(permission);
        return true;
    }
    
    @Override
    public boolean removePermission(final Permission permission) {
        return this.permissionSet.removeElement(permission);
    }
    
    @Override
    public boolean checkPermission(final Permission permission) {
        return this.permissionSet.contains(permission);
    }
    
    @Override
    public Enumeration<Permission> permissions() {
        return this.permissionSet.elements();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        if (this.negative) {
            sb.append("-");
        }
        else {
            sb.append("+");
        }
        if (this.user instanceof Group) {
            sb.append("Group.");
        }
        else {
            sb.append("User.");
        }
        sb.append(this.user + "=");
        final Enumeration<Permission> permissions = this.permissions();
        while (permissions.hasMoreElements()) {
            sb.append(permissions.nextElement());
            if (permissions.hasMoreElements()) {
                sb.append(",");
            }
        }
        return new String(sb);
    }
    
    @Override
    public synchronized Object clone() {
        final AclEntryImpl aclEntryImpl = new AclEntryImpl(this.user);
        aclEntryImpl.permissionSet = (Vector)this.permissionSet.clone();
        aclEntryImpl.negative = this.negative;
        return aclEntryImpl;
    }
    
    @Override
    public Principal getPrincipal() {
        return this.user;
    }
}
