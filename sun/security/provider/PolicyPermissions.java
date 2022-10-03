package sun.security.provider;

import java.util.Enumeration;
import java.security.Permission;
import java.util.Vector;
import java.security.Permissions;
import java.security.CodeSource;
import java.security.PermissionCollection;

class PolicyPermissions extends PermissionCollection
{
    private static final long serialVersionUID = -1954188373270545523L;
    private CodeSource codesource;
    private Permissions perms;
    private AuthPolicyFile policy;
    private boolean notInit;
    private Vector<Permission> additionalPerms;
    
    PolicyPermissions(final AuthPolicyFile policy, final CodeSource codesource) {
        this.codesource = codesource;
        this.policy = policy;
        this.perms = null;
        this.notInit = true;
        this.additionalPerms = null;
    }
    
    @Override
    public void add(final Permission permission) {
        if (this.isReadOnly()) {
            throw new SecurityException(AuthPolicyFile.rb.getString("attempt.to.add.a.Permission.to.a.readonly.PermissionCollection"));
        }
        if (this.perms == null) {
            if (this.additionalPerms == null) {
                this.additionalPerms = new Vector<Permission>();
            }
            this.additionalPerms.add(permission);
        }
        else {
            this.perms.add(permission);
        }
    }
    
    private synchronized void init() {
        if (this.notInit) {
            if (this.perms == null) {
                this.perms = new Permissions();
            }
            if (this.additionalPerms != null) {
                final Enumeration<Permission> elements = this.additionalPerms.elements();
                while (elements.hasMoreElements()) {
                    this.perms.add(elements.nextElement());
                }
                this.additionalPerms = null;
            }
            this.policy.getPermissions(this.perms, this.codesource);
            this.notInit = false;
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (this.notInit) {
            this.init();
        }
        return this.perms.implies(permission);
    }
    
    @Override
    public Enumeration<Permission> elements() {
        if (this.notInit) {
            this.init();
        }
        return this.perms.elements();
    }
    
    @Override
    public String toString() {
        if (this.notInit) {
            this.init();
        }
        return this.perms.toString();
    }
}
