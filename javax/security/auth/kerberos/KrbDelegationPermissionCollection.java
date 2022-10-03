package javax.security.auth.kerberos;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.ObjectStreamField;
import java.security.Permission;
import java.util.List;
import java.io.Serializable;
import java.security.PermissionCollection;

final class KrbDelegationPermissionCollection extends PermissionCollection implements Serializable
{
    private transient List<Permission> perms;
    private static final long serialVersionUID = -3383936936589966948L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public KrbDelegationPermissionCollection() {
        this.perms = new ArrayList<Permission>();
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof DelegationPermission)) {
            return false;
        }
        synchronized (this) {
            final Iterator<Permission> iterator = this.perms.iterator();
            while (iterator.hasNext()) {
                if (iterator.next().implies(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof DelegationPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        synchronized (this) {
            this.perms.add(0, permission);
        }
    }
    
    @Override
    public Enumeration<Permission> elements() {
        synchronized (this) {
            return Collections.enumeration(this.perms);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        final Vector vector = new Vector(this.perms.size());
        synchronized (this) {
            vector.addAll(this.perms);
        }
        objectOutputStream.putFields().put("permissions", vector);
        objectOutputStream.writeFields();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final Vector vector = (Vector)objectInputStream.readFields().get("permissions", null);
        (this.perms = new ArrayList<Permission>(vector.size())).addAll(vector);
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Vector.class) };
    }
}
