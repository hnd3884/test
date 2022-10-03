package com.sun.rmi.rmid;

import java.util.Enumeration;
import java.util.Vector;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PermissionCollection;
import java.io.FilePermission;
import java.security.Permission;

public final class ExecPermission extends Permission
{
    private static final long serialVersionUID = -6208470287358147919L;
    private transient FilePermission fp;
    
    public ExecPermission(final String s) {
        super(s);
        this.init(s);
    }
    
    public ExecPermission(final String s, final String s2) {
        this(s);
    }
    
    @Override
    public boolean implies(final Permission permission) {
        return permission instanceof ExecPermission && this.fp.implies(((ExecPermission)permission).fp);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o instanceof ExecPermission && this.fp.equals(((ExecPermission)o).fp));
    }
    
    @Override
    public int hashCode() {
        return this.fp.hashCode();
    }
    
    @Override
    public String getActions() {
        return "";
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new ExecPermissionCollection();
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(this.getName());
    }
    
    private void init(final String s) {
        this.fp = new FilePermission(s, "execute");
    }
    
    private static class ExecPermissionCollection extends PermissionCollection implements Serializable
    {
        private Vector<Permission> permissions;
        private static final long serialVersionUID = -3352558508888368273L;
        
        public ExecPermissionCollection() {
            this.permissions = new Vector<Permission>();
        }
        
        @Override
        public void add(final Permission permission) {
            if (!(permission instanceof ExecPermission)) {
                throw new IllegalArgumentException("invalid permission: " + permission);
            }
            if (this.isReadOnly()) {
                throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
            }
            this.permissions.addElement(permission);
        }
        
        @Override
        public boolean implies(final Permission permission) {
            if (!(permission instanceof ExecPermission)) {
                return false;
            }
            final Enumeration<Permission> elements = this.permissions.elements();
            while (elements.hasMoreElements()) {
                if (elements.nextElement().implies(permission)) {
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
}
