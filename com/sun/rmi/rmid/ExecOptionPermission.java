package com.sun.rmi.rmid;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.PermissionCollection;
import java.security.Permission;

public final class ExecOptionPermission extends Permission
{
    private transient boolean wildcard;
    private transient String name;
    private static final long serialVersionUID = 5842294756823092756L;
    
    public ExecOptionPermission(final String s) {
        super(s);
        this.init(s);
    }
    
    public ExecOptionPermission(final String s, final String s2) {
        this(s);
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof ExecOptionPermission)) {
            return false;
        }
        final ExecOptionPermission execOptionPermission = (ExecOptionPermission)permission;
        if (!this.wildcard) {
            return !execOptionPermission.wildcard && this.name.equals(execOptionPermission.name);
        }
        if (execOptionPermission.wildcard) {
            return execOptionPermission.name.startsWith(this.name);
        }
        return execOptionPermission.name.length() > this.name.length() && execOptionPermission.name.startsWith(this.name);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o == this || (o != null && o.getClass() == this.getClass() && this.getName().equals(((ExecOptionPermission)o).getName()));
    }
    
    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
    
    @Override
    public String getActions() {
        return "";
    }
    
    @Override
    public PermissionCollection newPermissionCollection() {
        return new ExecOptionPermissionCollection();
    }
    
    private synchronized void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.init(this.getName());
    }
    
    private void init(final String name) {
        if (name == null) {
            throw new NullPointerException("name can't be null");
        }
        if (name.equals("")) {
            throw new IllegalArgumentException("name can't be empty");
        }
        if (name.endsWith(".*") || name.endsWith("=*") || name.equals("*")) {
            this.wildcard = true;
            if (name.length() == 1) {
                this.name = "";
            }
            else {
                this.name = name.substring(0, name.length() - 1);
            }
        }
        else {
            this.name = name;
        }
    }
    
    private static class ExecOptionPermissionCollection extends PermissionCollection implements Serializable
    {
        private Hashtable<String, Permission> permissions;
        private boolean all_allowed;
        private static final long serialVersionUID = -1242475729790124375L;
        
        public ExecOptionPermissionCollection() {
            this.permissions = new Hashtable<String, Permission>(11);
            this.all_allowed = false;
        }
        
        @Override
        public void add(final Permission permission) {
            if (!(permission instanceof ExecOptionPermission)) {
                throw new IllegalArgumentException("invalid permission: " + permission);
            }
            if (this.isReadOnly()) {
                throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
            }
            final ExecOptionPermission execOptionPermission = (ExecOptionPermission)permission;
            this.permissions.put(execOptionPermission.getName(), permission);
            if (!this.all_allowed && execOptionPermission.getName().equals("*")) {
                this.all_allowed = true;
            }
        }
        
        @Override
        public boolean implies(final Permission permission) {
            if (!(permission instanceof ExecOptionPermission)) {
                return false;
            }
            final ExecOptionPermission execOptionPermission = (ExecOptionPermission)permission;
            if (this.all_allowed) {
                return true;
            }
            String s = execOptionPermission.getName();
            final Permission permission2 = this.permissions.get(s);
            if (permission2 != null) {
                return permission2.implies(permission);
            }
            int lastIndex;
            for (int n = s.length() - 1; (lastIndex = s.lastIndexOf(".", n)) != -1; n = lastIndex - 1) {
                s = s.substring(0, lastIndex + 1) + "*";
                final Permission permission3 = this.permissions.get(s);
                if (permission3 != null) {
                    return permission3.implies(permission);
                }
            }
            String s2 = execOptionPermission.getName();
            int lastIndex2;
            for (int n2 = s2.length() - 1; (lastIndex2 = s2.lastIndexOf("=", n2)) != -1; n2 = lastIndex2 - 1) {
                s2 = s2.substring(0, lastIndex2 + 1) + "*";
                final Permission permission4 = this.permissions.get(s2);
                if (permission4 != null) {
                    return permission4.implies(permission);
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
