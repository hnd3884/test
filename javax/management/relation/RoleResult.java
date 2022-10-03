package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class RoleResult implements Serializable
{
    private static final long oldSerialVersionUID = 3786616013762091099L;
    private static final long newSerialVersionUID = -6304063118040985512L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private RoleList roleList;
    private RoleUnresolvedList unresolvedRoleList;
    
    public RoleResult(final RoleList roles, final RoleUnresolvedList rolesUnresolved) {
        this.roleList = null;
        this.unresolvedRoleList = null;
        this.setRoles(roles);
        this.setRolesUnresolved(rolesUnresolved);
    }
    
    public RoleList getRoles() {
        return this.roleList;
    }
    
    public RoleUnresolvedList getRolesUnresolved() {
        return this.unresolvedRoleList;
    }
    
    public void setRoles(final RoleList list) {
        if (list != null) {
            this.roleList = new RoleList();
            final Iterator<Object> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.roleList.add((Role)iterator.next().clone());
            }
        }
        else {
            this.roleList = null;
        }
    }
    
    public void setRolesUnresolved(final RoleUnresolvedList list) {
        if (list != null) {
            this.unresolvedRoleList = new RoleUnresolvedList();
            final Iterator<Object> iterator = list.iterator();
            while (iterator.hasNext()) {
                this.unresolvedRoleList.add((RoleUnresolved)iterator.next().clone());
            }
        }
        else {
            this.unresolvedRoleList = null;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (RoleResult.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.roleList = (RoleList)fields.get("myRoleList", null);
            if (fields.defaulted("myRoleList")) {
                throw new NullPointerException("myRoleList");
            }
            this.unresolvedRoleList = (RoleUnresolvedList)fields.get("myRoleUnresList", null);
            if (fields.defaulted("myRoleUnresList")) {
                throw new NullPointerException("myRoleUnresList");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (RoleResult.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myRoleList", this.roleList);
            putFields.put("myRoleUnresList", this.unresolvedRoleList);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myRoleList", RoleList.class), new ObjectStreamField("myRoleUnresList", RoleUnresolvedList.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("roleList", RoleList.class), new ObjectStreamField("unresolvedRoleList", RoleUnresolvedList.class) };
        RoleResult.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            RoleResult.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (RoleResult.compat) {
            serialPersistentFields = RoleResult.oldSerialPersistentFields;
            serialVersionUID = 3786616013762091099L;
        }
        else {
            serialPersistentFields = RoleResult.newSerialPersistentFields;
            serialVersionUID = -6304063118040985512L;
        }
    }
}
