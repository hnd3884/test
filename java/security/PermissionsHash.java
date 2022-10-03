package java.security;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.io.ObjectStreamField;
import java.util.Map;
import java.io.Serializable;

final class PermissionsHash extends PermissionCollection implements Serializable
{
    private transient Map<Permission, Permission> permsMap;
    private static final long serialVersionUID = -8491988220802933440L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    PermissionsHash() {
        this.permsMap = new HashMap<Permission, Permission>(11);
    }
    
    @Override
    public void add(final Permission permission) {
        synchronized (this) {
            this.permsMap.put(permission, permission);
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        synchronized (this) {
            if (this.permsMap.get(permission) == null) {
                final Iterator<Permission> iterator = this.permsMap.values().iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().implies(permission)) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
    }
    
    @Override
    public Enumeration<Permission> elements() {
        synchronized (this) {
            return Collections.enumeration(this.permsMap.values());
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Hashtable hashtable = new Hashtable(this.permsMap.size() * 2);
        synchronized (this) {
            hashtable.putAll(this.permsMap);
        }
        objectOutputStream.putFields().put("perms", hashtable);
        objectOutputStream.writeFields();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Hashtable hashtable = (Hashtable)objectInputStream.readFields().get("perms", null);
        (this.permsMap = new HashMap<Permission, Permission>(hashtable.size() * 2)).putAll(hashtable);
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("perms", Hashtable.class) };
    }
}
