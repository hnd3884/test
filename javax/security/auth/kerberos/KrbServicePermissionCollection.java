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

final class KrbServicePermissionCollection extends PermissionCollection implements Serializable
{
    private transient List<Permission> perms;
    private static final long serialVersionUID = -4118834211490102011L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public KrbServicePermissionCollection() {
        this.perms = new ArrayList<Permission>();
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof ServicePermission)) {
            return false;
        }
        final ServicePermission servicePermission = (ServicePermission)permission;
        final int mask = servicePermission.getMask();
        if (mask == 0) {
            final Iterator<Permission> iterator = this.perms.iterator();
            while (iterator.hasNext()) {
                if (((ServicePermission)iterator.next()).impliesIgnoreMask(servicePermission)) {
                    return true;
                }
            }
            return false;
        }
        int n = 0;
        int n2 = mask;
        synchronized (this) {
            for (int size = this.perms.size(), i = 0; i < size; ++i) {
                final ServicePermission servicePermission2 = this.perms.get(i);
                if ((n2 & servicePermission2.getMask()) != 0x0 && servicePermission2.impliesIgnoreMask(servicePermission)) {
                    n |= servicePermission2.getMask();
                    if ((n & mask) == mask) {
                        return true;
                    }
                    n2 = (mask ^ n);
                }
            }
        }
        return false;
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof ServicePermission)) {
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
