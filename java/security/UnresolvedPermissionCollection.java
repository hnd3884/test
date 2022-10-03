package java.security;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Collections;
import java.util.Collection;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.ObjectStreamField;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

final class UnresolvedPermissionCollection extends PermissionCollection implements Serializable
{
    private transient Map<String, List<UnresolvedPermission>> perms;
    private static final long serialVersionUID = -7176153071733132400L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public UnresolvedPermissionCollection() {
        this.perms = new HashMap<String, List<UnresolvedPermission>>(11);
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof UnresolvedPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        final UnresolvedPermission unresolvedPermission = (UnresolvedPermission)permission;
        List list;
        synchronized (this) {
            list = this.perms.get(unresolvedPermission.getName());
            if (list == null) {
                list = new ArrayList();
                this.perms.put(unresolvedPermission.getName(), list);
            }
        }
        synchronized (list) {
            list.add(unresolvedPermission);
        }
    }
    
    List<UnresolvedPermission> getUnresolvedPermissions(final Permission permission) {
        synchronized (this) {
            return this.perms.get(permission.getClass().getName());
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        return false;
    }
    
    @Override
    public Enumeration<Permission> elements() {
        final ArrayList list = new ArrayList();
        synchronized (this) {
            for (final List list2 : this.perms.values()) {
                synchronized (list2) {
                    list.addAll(list2);
                }
            }
        }
        return Collections.enumeration((Collection<Permission>)list);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Hashtable hashtable = new Hashtable(this.perms.size() * 2);
        synchronized (this) {
            for (final Map.Entry entry : this.perms.entrySet()) {
                final List list = (List)entry.getValue();
                final Vector vector = new Vector(list.size());
                synchronized (list) {
                    vector.addAll(list);
                }
                hashtable.put(entry.getKey(), vector);
            }
        }
        objectOutputStream.putFields().put("permissions", hashtable);
        objectOutputStream.writeFields();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Hashtable hashtable = (Hashtable)objectInputStream.readFields().get("permissions", null);
        this.perms = new HashMap<String, List<UnresolvedPermission>>(hashtable.size() * 2);
        for (final Map.Entry entry : hashtable.entrySet()) {
            final Vector vector = (Vector)entry.getValue();
            final ArrayList list = new ArrayList(vector.size());
            list.addAll(vector);
            this.perms.put((String)entry.getKey(), (ArrayList)list);
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Hashtable.class) };
    }
}
