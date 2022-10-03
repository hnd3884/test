package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.management.NotCompliantMBeanException;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class RoleInfo implements Serializable
{
    private static final long oldSerialVersionUID = 7227256952085334351L;
    private static final long newSerialVersionUID = 2504952983494636987L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    public static final int ROLE_CARDINALITY_INFINITY = -1;
    private String name;
    private boolean isReadable;
    private boolean isWritable;
    private String description;
    private int minDegree;
    private int maxDegree;
    private String referencedMBeanClassName;
    
    public RoleInfo(final String s, final String s2, final boolean b, final boolean b2, final int n, final int n2, final String s3) throws IllegalArgumentException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException {
        this.name = null;
        this.description = null;
        this.referencedMBeanClassName = null;
        this.init(s, s2, b, b2, n, n2, s3);
    }
    
    public RoleInfo(final String s, final String s2, final boolean b, final boolean b2) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
        this.name = null;
        this.description = null;
        this.referencedMBeanClassName = null;
        try {
            this.init(s, s2, b, b2, 1, 1, null);
        }
        catch (final InvalidRoleInfoException ex) {}
    }
    
    public RoleInfo(final String s, final String s2) throws IllegalArgumentException, ClassNotFoundException, NotCompliantMBeanException {
        this.name = null;
        this.description = null;
        this.referencedMBeanClassName = null;
        try {
            this.init(s, s2, true, true, 1, 1, null);
        }
        catch (final InvalidRoleInfoException ex) {}
    }
    
    public RoleInfo(final RoleInfo roleInfo) throws IllegalArgumentException {
        this.name = null;
        this.description = null;
        this.referencedMBeanClassName = null;
        if (roleInfo == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        try {
            this.init(roleInfo.getName(), roleInfo.getRefMBeanClassName(), roleInfo.isReadable(), roleInfo.isWritable(), roleInfo.getMinDegree(), roleInfo.getMaxDegree(), roleInfo.getDescription());
        }
        catch (final InvalidRoleInfoException ex) {}
    }
    
    public String getName() {
        return this.name;
    }
    
    public boolean isReadable() {
        return this.isReadable;
    }
    
    public boolean isWritable() {
        return this.isWritable;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public int getMinDegree() {
        return this.minDegree;
    }
    
    public int getMaxDegree() {
        return this.maxDegree;
    }
    
    public String getRefMBeanClassName() {
        return this.referencedMBeanClassName;
    }
    
    public boolean checkMinDegree(final int n) {
        return n >= -1 && (this.minDegree == -1 || n >= this.minDegree);
    }
    
    public boolean checkMaxDegree(final int n) {
        return n >= -1 && (this.maxDegree == -1 || (n != -1 && n <= this.maxDegree));
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("role info name: " + this.name);
        sb.append("; isReadable: " + this.isReadable);
        sb.append("; isWritable: " + this.isWritable);
        sb.append("; description: " + this.description);
        sb.append("; minimum degree: " + this.minDegree);
        sb.append("; maximum degree: " + this.maxDegree);
        sb.append("; MBean class: " + this.referencedMBeanClassName);
        return sb.toString();
    }
    
    private void init(final String name, final String referencedMBeanClassName, final boolean isReadable, final boolean isWritable, final int minDegree, final int maxDegree, final String description) throws IllegalArgumentException, InvalidRoleInfoException {
        if (name == null || referencedMBeanClassName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.name = name;
        this.isReadable = isReadable;
        this.isWritable = isWritable;
        if (description != null) {
            this.description = description;
        }
        boolean b = false;
        final StringBuilder sb = new StringBuilder();
        if (maxDegree != -1 && (minDegree == -1 || minDegree > maxDegree)) {
            sb.append("Minimum degree ");
            sb.append(minDegree);
            sb.append(" is greater than maximum degree ");
            sb.append(maxDegree);
            b = true;
        }
        else if (minDegree < -1 || maxDegree < -1) {
            sb.append("Minimum or maximum degree has an illegal value, must be [0, ROLE_CARDINALITY_INFINITY].");
            b = true;
        }
        if (b) {
            throw new InvalidRoleInfoException(sb.toString());
        }
        this.minDegree = minDegree;
        this.maxDegree = maxDegree;
        this.referencedMBeanClassName = referencedMBeanClassName;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (RoleInfo.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.name = (String)fields.get("myName", null);
            if (fields.defaulted("myName")) {
                throw new NullPointerException("myName");
            }
            this.isReadable = fields.get("myIsReadableFlg", false);
            if (fields.defaulted("myIsReadableFlg")) {
                throw new NullPointerException("myIsReadableFlg");
            }
            this.isWritable = fields.get("myIsWritableFlg", false);
            if (fields.defaulted("myIsWritableFlg")) {
                throw new NullPointerException("myIsWritableFlg");
            }
            this.description = (String)fields.get("myDescription", null);
            if (fields.defaulted("myDescription")) {
                throw new NullPointerException("myDescription");
            }
            this.minDegree = fields.get("myMinDegree", 0);
            if (fields.defaulted("myMinDegree")) {
                throw new NullPointerException("myMinDegree");
            }
            this.maxDegree = fields.get("myMaxDegree", 0);
            if (fields.defaulted("myMaxDegree")) {
                throw new NullPointerException("myMaxDegree");
            }
            this.referencedMBeanClassName = (String)fields.get("myRefMBeanClassName", null);
            if (fields.defaulted("myRefMBeanClassName")) {
                throw new NullPointerException("myRefMBeanClassName");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (RoleInfo.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myName", this.name);
            putFields.put("myIsReadableFlg", this.isReadable);
            putFields.put("myIsWritableFlg", this.isWritable);
            putFields.put("myDescription", this.description);
            putFields.put("myMinDegree", this.minDegree);
            putFields.put("myMaxDegree", this.maxDegree);
            putFields.put("myRefMBeanClassName", this.referencedMBeanClassName);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myName", String.class), new ObjectStreamField("myIsReadableFlg", Boolean.TYPE), new ObjectStreamField("myIsWritableFlg", Boolean.TYPE), new ObjectStreamField("myDescription", String.class), new ObjectStreamField("myMinDegree", Integer.TYPE), new ObjectStreamField("myMaxDegree", Integer.TYPE), new ObjectStreamField("myRefMBeanClassName", String.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("name", String.class), new ObjectStreamField("isReadable", Boolean.TYPE), new ObjectStreamField("isWritable", Boolean.TYPE), new ObjectStreamField("description", String.class), new ObjectStreamField("minDegree", Integer.TYPE), new ObjectStreamField("maxDegree", Integer.TYPE), new ObjectStreamField("referencedMBeanClassName", String.class) };
        RoleInfo.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            RoleInfo.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (RoleInfo.compat) {
            serialPersistentFields = RoleInfo.oldSerialPersistentFields;
            serialVersionUID = 7227256952085334351L;
        }
        else {
            serialPersistentFields = RoleInfo.newSerialPersistentFields;
            serialVersionUID = 2504952983494636987L;
        }
    }
}
