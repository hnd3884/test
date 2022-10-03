package java.net;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.security.Permission;
import java.util.ArrayList;
import java.io.ObjectStreamField;
import java.util.List;
import java.io.Serializable;
import java.security.PermissionCollection;

final class SocketPermissionCollection extends PermissionCollection implements Serializable
{
    private transient List<SocketPermission> perms;
    private static final long serialVersionUID = 2787186408602843674L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public SocketPermissionCollection() {
        this.perms = new ArrayList<SocketPermission>();
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof SocketPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        synchronized (this) {
            this.perms.add(0, (SocketPermission)permission);
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof SocketPermission)) {
            return false;
        }
        final SocketPermission socketPermission = (SocketPermission)permission;
        final int mask = socketPermission.getMask();
        int n = 0;
        int n2 = mask;
        synchronized (this) {
            for (int size = this.perms.size(), i = 0; i < size; ++i) {
                final SocketPermission socketPermission2 = this.perms.get(i);
                if ((n2 & socketPermission2.getMask()) != 0x0 && socketPermission2.impliesIgnoreMask(socketPermission)) {
                    n |= socketPermission2.getMask();
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
    public Enumeration<Permission> elements() {
        synchronized (this) {
            return (Enumeration<Permission>)Collections.enumeration(this.perms);
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
        (this.perms = new ArrayList<SocketPermission>(vector.size())).addAll(vector);
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Vector.class) };
    }
}
