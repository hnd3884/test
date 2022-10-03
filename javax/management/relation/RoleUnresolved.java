package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import com.sun.jmx.mbeanserver.Util;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import javax.management.ObjectName;
import java.util.List;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class RoleUnresolved implements Serializable
{
    private static final long oldSerialVersionUID = -9026457686611660144L;
    private static final long newSerialVersionUID = -48350262537070138L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private String roleName;
    private List<ObjectName> roleValue;
    private int problemType;
    
    public RoleUnresolved(final String roleName, final List<ObjectName> roleValue, final int problemType) throws IllegalArgumentException {
        this.roleName = null;
        this.roleValue = null;
        if (roleName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.setRoleName(roleName);
        this.setRoleValue(roleValue);
        this.setProblemType(problemType);
    }
    
    public String getRoleName() {
        return this.roleName;
    }
    
    public List<ObjectName> getRoleValue() {
        return this.roleValue;
    }
    
    public int getProblemType() {
        return this.problemType;
    }
    
    public void setRoleName(final String roleName) throws IllegalArgumentException {
        if (roleName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.roleName = roleName;
    }
    
    public void setRoleValue(final List<ObjectName> list) {
        if (list != null) {
            this.roleValue = new ArrayList<ObjectName>(list);
        }
        else {
            this.roleValue = null;
        }
    }
    
    public void setProblemType(final int problemType) throws IllegalArgumentException {
        if (!RoleStatus.isRoleStatus(problemType)) {
            throw new IllegalArgumentException("Incorrect problem type.");
        }
        this.problemType = problemType;
    }
    
    public Object clone() {
        try {
            return new RoleUnresolved(this.roleName, this.roleValue, this.problemType);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("role name: " + this.roleName);
        if (this.roleValue != null) {
            sb.append("; value: ");
            final Iterator<ObjectName> iterator = this.roleValue.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
        sb.append("; problem type: " + this.problemType);
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (RoleUnresolved.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.roleName = (String)fields.get("myRoleName", null);
            if (fields.defaulted("myRoleName")) {
                throw new NullPointerException("myRoleName");
            }
            this.roleValue = (List<ObjectName>)Util.cast(fields.get("myRoleValue", null));
            if (fields.defaulted("myRoleValue")) {
                throw new NullPointerException("myRoleValue");
            }
            this.problemType = fields.get("myPbType", 0);
            if (fields.defaulted("myPbType")) {
                throw new NullPointerException("myPbType");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (RoleUnresolved.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myRoleName", this.roleName);
            putFields.put("myRoleValue", this.roleValue);
            putFields.put("myPbType", this.problemType);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myRoleValue", ArrayList.class), new ObjectStreamField("myPbType", Integer.TYPE) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("roleName", String.class), new ObjectStreamField("roleValue", List.class), new ObjectStreamField("problemType", Integer.TYPE) };
        RoleUnresolved.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            RoleUnresolved.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (RoleUnresolved.compat) {
            serialPersistentFields = RoleUnresolved.oldSerialPersistentFields;
            serialVersionUID = -9026457686611660144L;
        }
        else {
            serialPersistentFields = RoleUnresolved.newSerialPersistentFields;
            serialVersionUID = -48350262537070138L;
        }
    }
}
