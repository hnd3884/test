package java.util;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.Permission;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.security.PermissionCollection;

final class PropertyPermissionCollection extends PermissionCollection implements Serializable
{
    private transient Map<String, PropertyPermission> perms;
    private boolean all_allowed;
    private static final long serialVersionUID = 7015263904581634791L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public PropertyPermissionCollection() {
        this.perms = new HashMap<String, PropertyPermission>(32);
        this.all_allowed = false;
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof PropertyPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        final PropertyPermission propertyPermission = (PropertyPermission)permission;
        final String name = propertyPermission.getName();
        synchronized (this) {
            final PropertyPermission propertyPermission2 = this.perms.get(name);
            if (propertyPermission2 != null) {
                final int mask = propertyPermission2.getMask();
                final int mask2 = propertyPermission.getMask();
                if (mask != mask2) {
                    this.perms.put(name, new PropertyPermission(name, PropertyPermission.getActions(mask | mask2)));
                }
            }
            else {
                this.perms.put(name, propertyPermission);
            }
        }
        if (!this.all_allowed && name.equals("*")) {
            this.all_allowed = true;
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof PropertyPermission)) {
            return false;
        }
        final PropertyPermission propertyPermission = (PropertyPermission)permission;
        final int mask = propertyPermission.getMask();
        int n = 0;
        if (this.all_allowed) {
            final PropertyPermission propertyPermission2;
            synchronized (this) {
                propertyPermission2 = this.perms.get("*");
            }
            if (propertyPermission2 != null) {
                n |= propertyPermission2.getMask();
                if ((n & mask) == mask) {
                    return true;
                }
            }
        }
        String s = propertyPermission.getName();
        PropertyPermission propertyPermission2;
        synchronized (this) {
            propertyPermission2 = this.perms.get(s);
        }
        if (propertyPermission2 != null) {
            n |= propertyPermission2.getMask();
            if ((n & mask) == mask) {
                return true;
            }
        }
        int lastIndex;
        for (int n2 = s.length() - 1; (lastIndex = s.lastIndexOf(".", n2)) != -1; n2 = lastIndex - 1) {
            s = s.substring(0, lastIndex + 1) + "*";
            synchronized (this) {
                propertyPermission2 = this.perms.get(s);
            }
            if (propertyPermission2 != null) {
                n |= propertyPermission2.getMask();
                if ((n & mask) == mask) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public Enumeration<Permission> elements() {
        synchronized (this) {
            return (Enumeration<Permission>)Collections.enumeration(this.perms.values());
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
        objectOutputStream.writeFields();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        this.all_allowed = fields.get("all_allowed", false);
        final Hashtable hashtable = (Hashtable)fields.get("permissions", null);
        (this.perms = new HashMap<String, PropertyPermission>(hashtable.size() * 2)).putAll(hashtable);
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE) };
    }
}
