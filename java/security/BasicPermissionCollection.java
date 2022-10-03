package java.security;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.io.ObjectStreamField;
import java.util.Map;
import java.io.Serializable;

final class BasicPermissionCollection extends PermissionCollection implements Serializable
{
    private static final long serialVersionUID = 739301742472979399L;
    private transient Map<String, Permission> perms;
    private boolean all_allowed;
    private Class<?> permClass;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public BasicPermissionCollection(final Class<?> permClass) {
        this.perms = new HashMap<String, Permission>(11);
        this.all_allowed = false;
        this.permClass = permClass;
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof BasicPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        final BasicPermission basicPermission = (BasicPermission)permission;
        if (this.permClass == null) {
            this.permClass = basicPermission.getClass();
        }
        else if (basicPermission.getClass() != this.permClass) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        synchronized (this) {
            this.perms.put(basicPermission.getCanonicalName(), permission);
        }
        if (!this.all_allowed && basicPermission.getCanonicalName().equals("*")) {
            this.all_allowed = true;
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof BasicPermission)) {
            return false;
        }
        final BasicPermission basicPermission = (BasicPermission)permission;
        if (basicPermission.getClass() != this.permClass) {
            return false;
        }
        if (this.all_allowed) {
            return true;
        }
        String s = basicPermission.getCanonicalName();
        Permission permission2;
        synchronized (this) {
            permission2 = this.perms.get(s);
        }
        if (permission2 != null) {
            return permission2.implies(permission);
        }
        int lastIndex;
        for (int n = s.length() - 1; (lastIndex = s.lastIndexOf(".", n)) != -1; n = lastIndex - 1) {
            s = s.substring(0, lastIndex + 1) + "*";
            synchronized (this) {
                permission2 = this.perms.get(s);
            }
            if (permission2 != null) {
                return permission2.implies(permission);
            }
        }
        return false;
    }
    
    @Override
    public Enumeration<Permission> elements() {
        synchronized (this) {
            return Collections.enumeration(this.perms.values());
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Hashtable hashtable = new Hashtable(this.perms.size() * 2);
        synchronized (this) {
            hashtable.putAll(this.perms);
        }
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("all_allowed", this.all_allowed);
        putFields.put("permissions", hashtable);
        putFields.put("permClass", this.permClass);
        objectOutputStream.writeFields();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final Hashtable hashtable = (Hashtable)fields.get("permissions", null);
        (this.perms = new HashMap<String, Permission>(hashtable.size() * 2)).putAll(hashtable);
        this.all_allowed = fields.get("all_allowed", false);
        this.permClass = (Class)fields.get("permClass", null);
        if (this.permClass == null) {
            final Enumeration elements = hashtable.elements();
            if (elements.hasMoreElements()) {
                this.permClass = ((Permission)elements.nextElement()).getClass();
            }
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("permClass", Class.class) };
    }
}
