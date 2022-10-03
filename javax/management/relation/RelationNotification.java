package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import com.sun.jmx.mbeanserver.Util;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;
import java.io.ObjectStreamField;
import javax.management.Notification;

public class RelationNotification extends Notification
{
    private static final long oldSerialVersionUID = -2126464566505527147L;
    private static final long newSerialVersionUID = -6871117877523310399L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    public static final String RELATION_BASIC_CREATION = "jmx.relation.creation.basic";
    public static final String RELATION_MBEAN_CREATION = "jmx.relation.creation.mbean";
    public static final String RELATION_BASIC_UPDATE = "jmx.relation.update.basic";
    public static final String RELATION_MBEAN_UPDATE = "jmx.relation.update.mbean";
    public static final String RELATION_BASIC_REMOVAL = "jmx.relation.removal.basic";
    public static final String RELATION_MBEAN_REMOVAL = "jmx.relation.removal.mbean";
    private String relationId;
    private String relationTypeName;
    private ObjectName relationObjName;
    private List<ObjectName> unregisterMBeanList;
    private String roleName;
    private List<ObjectName> oldRoleValue;
    private List<ObjectName> newRoleValue;
    
    public RelationNotification(final String s, final Object o, final long n, final long n2, final String s2, final String relationId, final String relationTypeName, final ObjectName objectName, final List<ObjectName> list) throws IllegalArgumentException {
        super(s, o, n, n2, s2);
        this.relationId = null;
        this.relationTypeName = null;
        this.relationObjName = null;
        this.unregisterMBeanList = null;
        this.roleName = null;
        this.oldRoleValue = null;
        this.newRoleValue = null;
        if (!this.isValidBasicStrict(s, o, relationId, relationTypeName) || !this.isValidCreate(s)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.relationId = relationId;
        this.relationTypeName = relationTypeName;
        this.relationObjName = this.safeGetObjectName(objectName);
        this.unregisterMBeanList = this.safeGetObjectNameList(list);
    }
    
    public RelationNotification(final String s, final Object o, final long n, final long n2, final String s2, final String relationId, final String relationTypeName, final ObjectName objectName, final String roleName, final List<ObjectName> list, final List<ObjectName> list2) throws IllegalArgumentException {
        super(s, o, n, n2, s2);
        this.relationId = null;
        this.relationTypeName = null;
        this.relationObjName = null;
        this.unregisterMBeanList = null;
        this.roleName = null;
        this.oldRoleValue = null;
        this.newRoleValue = null;
        if (!this.isValidBasicStrict(s, o, relationId, relationTypeName) || !this.isValidUpdate(s, roleName, list, list2)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.relationId = relationId;
        this.relationTypeName = relationTypeName;
        this.relationObjName = this.safeGetObjectName(objectName);
        this.roleName = roleName;
        this.oldRoleValue = this.safeGetObjectNameList(list2);
        this.newRoleValue = this.safeGetObjectNameList(list);
    }
    
    public String getRelationId() {
        return this.relationId;
    }
    
    public String getRelationTypeName() {
        return this.relationTypeName;
    }
    
    public ObjectName getObjectName() {
        return this.relationObjName;
    }
    
    public List<ObjectName> getMBeansToUnregister() {
        List<Object> emptyList;
        if (this.unregisterMBeanList != null) {
            emptyList = new ArrayList<Object>(this.unregisterMBeanList);
        }
        else {
            emptyList = Collections.emptyList();
        }
        return (List<ObjectName>)emptyList;
    }
    
    public String getRoleName() {
        String roleName = null;
        if (this.roleName != null) {
            roleName = this.roleName;
        }
        return roleName;
    }
    
    public List<ObjectName> getOldRoleValue() {
        List<Object> emptyList;
        if (this.oldRoleValue != null) {
            emptyList = new ArrayList<Object>(this.oldRoleValue);
        }
        else {
            emptyList = Collections.emptyList();
        }
        return (List<ObjectName>)emptyList;
    }
    
    public List<ObjectName> getNewRoleValue() {
        List<Object> emptyList;
        if (this.newRoleValue != null) {
            emptyList = new ArrayList<Object>(this.newRoleValue);
        }
        else {
            emptyList = Collections.emptyList();
        }
        return (List<ObjectName>)emptyList;
    }
    
    private boolean isValidBasicStrict(final String s, final Object o, final String s2, final String s3) {
        return o != null && this.isValidBasic(s, o, s2, s3);
    }
    
    private boolean isValidBasic(final String s, final Object o, final String s2, final String s3) {
        return s != null && s2 != null && s3 != null && (o == null || o instanceof RelationService || o instanceof ObjectName);
    }
    
    private boolean isValidCreate(final String s) {
        return new HashSet(Arrays.asList("jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean")).contains(s);
    }
    
    private boolean isValidUpdate(final String s, final String s2, final List<ObjectName> list, final List<ObjectName> list2) {
        return (s.equals("jmx.relation.update.basic") || s.equals("jmx.relation.update.mbean")) && s2 != null && list2 != null && list != null;
    }
    
    private ArrayList<ObjectName> safeGetObjectNameList(final List<ObjectName> list) {
        ArrayList<ObjectName> list2 = null;
        if (list != null) {
            list2 = new ArrayList<ObjectName>();
            final Iterator<ObjectName> iterator = list.iterator();
            while (iterator.hasNext()) {
                list2.add(ObjectName.getInstance(iterator.next()));
            }
        }
        return list2;
    }
    
    private ObjectName safeGetObjectName(final ObjectName objectName) {
        ObjectName instance = null;
        if (objectName != null) {
            instance = ObjectName.getInstance(objectName);
        }
        return instance;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        String relationId;
        String relationTypeName;
        String roleName;
        ObjectName objectName;
        List list;
        List list2;
        List list3;
        if (RelationNotification.compat) {
            relationId = (String)fields.get("myRelId", null);
            relationTypeName = (String)fields.get("myRelTypeName", null);
            roleName = (String)fields.get("myRoleName", null);
            objectName = (ObjectName)fields.get("myRelObjName", null);
            list = Util.cast(fields.get("myNewRoleValue", null));
            list2 = Util.cast(fields.get("myOldRoleValue", null));
            list3 = Util.cast(fields.get("myUnregMBeanList", null));
        }
        else {
            relationId = (String)fields.get("relationId", null);
            relationTypeName = (String)fields.get("relationTypeName", null);
            roleName = (String)fields.get("roleName", null);
            objectName = (ObjectName)fields.get("relationObjName", null);
            list = Util.cast(fields.get("newRoleValue", null));
            list2 = Util.cast(fields.get("oldRoleValue", null));
            list3 = Util.cast(fields.get("unregisterMBeanList", null));
        }
        final String type = super.getType();
        if (!this.isValidBasic(type, super.getSource(), relationId, relationTypeName) || (!this.isValidCreate(type) && !this.isValidUpdate(type, roleName, list, list2))) {
            super.setSource(null);
            throw new InvalidObjectException("Invalid object read");
        }
        this.relationObjName = this.safeGetObjectName(objectName);
        this.newRoleValue = this.safeGetObjectNameList(list);
        this.oldRoleValue = this.safeGetObjectNameList(list2);
        this.unregisterMBeanList = this.safeGetObjectNameList(list3);
        this.relationId = relationId;
        this.relationTypeName = relationTypeName;
        this.roleName = roleName;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (RelationNotification.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myNewRoleValue", this.newRoleValue);
            putFields.put("myOldRoleValue", this.oldRoleValue);
            putFields.put("myRelId", this.relationId);
            putFields.put("myRelObjName", this.relationObjName);
            putFields.put("myRelTypeName", this.relationTypeName);
            putFields.put("myRoleName", this.roleName);
            putFields.put("myUnregMBeanList", this.unregisterMBeanList);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myNewRoleValue", ArrayList.class), new ObjectStreamField("myOldRoleValue", ArrayList.class), new ObjectStreamField("myRelId", String.class), new ObjectStreamField("myRelObjName", ObjectName.class), new ObjectStreamField("myRelTypeName", String.class), new ObjectStreamField("myRoleName", String.class), new ObjectStreamField("myUnregMBeanList", ArrayList.class) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("newRoleValue", List.class), new ObjectStreamField("oldRoleValue", List.class), new ObjectStreamField("relationId", String.class), new ObjectStreamField("relationObjName", ObjectName.class), new ObjectStreamField("relationTypeName", String.class), new ObjectStreamField("roleName", String.class), new ObjectStreamField("unregisterMBeanList", List.class) };
        RelationNotification.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            RelationNotification.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (RelationNotification.compat) {
            serialPersistentFields = RelationNotification.oldSerialPersistentFields;
            serialVersionUID = -2126464566505527147L;
        }
        else {
            serialPersistentFields = RelationNotification.newSerialPersistentFields;
            serialVersionUID = -6871117877523310399L;
        }
    }
}
