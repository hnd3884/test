package java.io;

import java.util.Iterator;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ArrayList;
import java.security.Permission;
import java.util.List;
import java.security.PermissionCollection;

final class FilePermissionCollection extends PermissionCollection implements Serializable
{
    private transient List<Permission> perms;
    private static final long serialVersionUID = 2202956749081564585L;
    private static final ObjectStreamField[] serialPersistentFields;
    
    public FilePermissionCollection() {
        this.perms = new ArrayList<Permission>();
    }
    
    @Override
    public void add(final Permission permission) {
        if (!(permission instanceof FilePermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        synchronized (this) {
            this.perms.add(permission);
        }
    }
    
    @Override
    public boolean implies(final Permission permission) {
        if (!(permission instanceof FilePermission)) {
            return false;
        }
        final FilePermission filePermission = (FilePermission)permission;
        final int mask = filePermission.getMask();
        int n = 0;
        int n2 = mask;
        synchronized (this) {
            for (int size = this.perms.size(), i = 0; i < size; ++i) {
                final FilePermission filePermission2 = this.perms.get(i);
                if ((n2 & filePermission2.getMask()) != 0x0 && filePermission2.impliesIgnoreMask(filePermission)) {
                    n |= filePermission2.getMask();
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
        this.perms = new ArrayList<Permission>(vector.size());
        final Iterator iterator = vector.iterator();
        while (iterator.hasNext()) {
            this.perms.add((Permission)iterator.next());
        }
    }
    
    static {
        serialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("permissions", Vector.class) };
    }
}
