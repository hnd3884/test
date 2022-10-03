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

public class Role implements Serializable
{
    private static final long oldSerialVersionUID = -1959486389343113026L;
    private static final long newSerialVersionUID = -279985518429862552L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private String name;
    private List<ObjectName> objectNameList;
    
    public Role(final String roleName, final List<ObjectName> roleValue) throws IllegalArgumentException {
        this.name = null;
        this.objectNameList = new ArrayList<ObjectName>();
        if (roleName == null || roleValue == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        this.setRoleName(roleName);
        this.setRoleValue(roleValue);
    }
    
    public String getRoleName() {
        return this.name;
    }
    
    public List<ObjectName> getRoleValue() {
        return this.objectNameList;
    }
    
    public void setRoleName(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.name = name;
    }
    
    public void setRoleValue(final List<ObjectName> list) throws IllegalArgumentException {
        if (list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.objectNameList = new ArrayList<ObjectName>(list);
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("role name: " + this.name + "; role value: ");
        final Iterator<ObjectName> iterator = this.objectNameList.iterator();
        while (iterator.hasNext()) {
            sb.append(iterator.next().toString());
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
    
    public Object clone() {
        try {
            return new Role(this.name, this.objectNameList);
        }
        catch (final IllegalArgumentException ex) {
            return null;
        }
    }
    
    public static String roleValueToString(final List<ObjectName> list) throws IllegalArgumentException {
        if (list == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        final StringBuilder sb = new StringBuilder();
        for (final ObjectName objectName : list) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(objectName.toString());
        }
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (Role.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.name = (String)fields.get("myName", null);
            if (fields.defaulted("myName")) {
                throw new NullPointerException("myName");
            }
            this.objectNameList = (List<ObjectName>)Util.cast(fields.get("myObjNameList", null));
            if (fields.defaulted("myObjNameList")) {
                throw new NullPointerException("myObjNameList");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (Role.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myName", this.name);
            putFields.put("myObjNameList", this.objectNameList);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myName", String.class), new ObjectStreamField("myObjNameList", ArrayList.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("name", String.class), new ObjectStreamField("objectNameList", List.class) };
        Role.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            Role.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (Role.compat) {
            serialPersistentFields = Role.oldSerialPersistentFields;
            serialVersionUID = -1959486389343113026L;
        }
        else {
            serialPersistentFields = Role.newSerialPersistentFields;
            serialVersionUID = -279985518429862552L;
        }
    }
}
