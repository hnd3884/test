package javax.management.relation;

import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import com.sun.jmx.mbeanserver.Util;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import com.sun.jmx.defaults.JmxProperties;
import java.util.HashMap;
import java.util.Map;
import java.io.ObjectStreamField;

public class RelationTypeSupport implements RelationType
{
    private static final long oldSerialVersionUID = -8179019472410837190L;
    private static final long newSerialVersionUID = 4611072955724144607L;
    private static final ObjectStreamField[] oldSerialPersistentFields;
    private static final ObjectStreamField[] newSerialPersistentFields;
    private static final long serialVersionUID;
    private static final ObjectStreamField[] serialPersistentFields;
    private static boolean compat;
    private String typeName;
    private Map<String, RoleInfo> roleName2InfoMap;
    private boolean isInRelationService;
    
    public RelationTypeSupport(final String s, final RoleInfo[] array) throws IllegalArgumentException, InvalidRelationTypeException {
        this.typeName = null;
        this.roleName2InfoMap = new HashMap<String, RoleInfo>();
        this.isInRelationService = false;
        if (s == null || array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", s);
        this.initMembers(s, array);
        JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
    }
    
    protected RelationTypeSupport(final String typeName) {
        this.typeName = null;
        this.roleName2InfoMap = new HashMap<String, RoleInfo>();
        this.isInRelationService = false;
        if (typeName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "RelationTypeSupport", typeName);
        this.typeName = typeName;
        JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "RelationTypeSupport");
    }
    
    @Override
    public String getRelationTypeName() {
        return this.typeName;
    }
    
    @Override
    public List<RoleInfo> getRoleInfos() {
        return new ArrayList<RoleInfo>(this.roleName2InfoMap.values());
    }
    
    @Override
    public RoleInfo getRoleInfo(final String s) throws IllegalArgumentException, RoleInfoNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "getRoleInfo", s);
        final RoleInfo roleInfo = this.roleName2InfoMap.get(s);
        if (roleInfo == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No role info for role ");
            sb.append(s);
            throw new RoleInfoNotFoundException(sb.toString());
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "getRoleInfo");
        return roleInfo;
    }
    
    protected void addRoleInfo(final RoleInfo roleInfo) throws IllegalArgumentException, InvalidRelationTypeException {
        if (roleInfo == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "addRoleInfo", roleInfo);
        if (this.isInRelationService) {
            throw new RuntimeException("Relation type cannot be updated as it is declared in the Relation Service.");
        }
        final String name = roleInfo.getName();
        if (this.roleName2InfoMap.containsKey(name)) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Two role infos provided for role ");
            sb.append(name);
            throw new InvalidRelationTypeException(sb.toString());
        }
        this.roleName2InfoMap.put(name, new RoleInfo(roleInfo));
        JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "addRoleInfo");
    }
    
    void setRelationServiceFlag(final boolean isInRelationService) {
        this.isInRelationService = isInRelationService;
    }
    
    private void initMembers(final String typeName, final RoleInfo[] array) throws IllegalArgumentException, InvalidRelationTypeException {
        if (typeName == null || array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationTypeSupport.class.getName(), "initMembers", typeName);
        this.typeName = typeName;
        checkRoleInfos(array);
        for (int i = 0; i < array.length; ++i) {
            final RoleInfo roleInfo = array[i];
            this.roleName2InfoMap.put(roleInfo.getName(), new RoleInfo(roleInfo));
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationTypeSupport.class.getName(), "initMembers");
    }
    
    static void checkRoleInfos(final RoleInfo[] array) throws IllegalArgumentException, InvalidRelationTypeException {
        if (array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        if (array.length == 0) {
            throw new InvalidRelationTypeException("No role info provided.");
        }
        final HashSet set = new HashSet();
        for (int i = 0; i < array.length; ++i) {
            final RoleInfo roleInfo = array[i];
            if (roleInfo == null) {
                throw new InvalidRelationTypeException("Null role info provided.");
            }
            final String name = roleInfo.getName();
            if (set.contains(name)) {
                final StringBuilder sb = new StringBuilder();
                sb.append("Two role infos provided for role ");
                sb.append(name);
                throw new InvalidRelationTypeException(sb.toString());
            }
            set.add(name);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (RelationTypeSupport.compat) {
            final ObjectInputStream.GetField fields = objectInputStream.readFields();
            this.typeName = (String)fields.get("myTypeName", null);
            if (fields.defaulted("myTypeName")) {
                throw new NullPointerException("myTypeName");
            }
            this.roleName2InfoMap = (Map<String, RoleInfo>)Util.cast(fields.get("myRoleName2InfoMap", null));
            if (fields.defaulted("myRoleName2InfoMap")) {
                throw new NullPointerException("myRoleName2InfoMap");
            }
            this.isInRelationService = fields.get("myIsInRelServFlg", false);
            if (fields.defaulted("myIsInRelServFlg")) {
                throw new NullPointerException("myIsInRelServFlg");
            }
        }
        else {
            objectInputStream.defaultReadObject();
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (RelationTypeSupport.compat) {
            final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
            putFields.put("myTypeName", this.typeName);
            putFields.put("myRoleName2InfoMap", this.roleName2InfoMap);
            putFields.put("myIsInRelServFlg", this.isInRelationService);
            objectOutputStream.writeFields();
        }
        else {
            objectOutputStream.defaultWriteObject();
        }
    }
    
    static {
        oldSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("myTypeName", String.class), new ObjectStreamField("myRoleName2InfoMap", HashMap.class), new ObjectStreamField("myIsInRelServFlg", Boolean.TYPE) };
        newSerialPersistentFields = new ObjectStreamField[] { new ObjectStreamField("typeName", String.class), new ObjectStreamField("roleName2InfoMap", Map.class), new ObjectStreamField("isInRelationService", Boolean.TYPE) };
        RelationTypeSupport.compat = false;
        try {
            final String s = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("jmx.serial.form"));
            RelationTypeSupport.compat = (s != null && s.equals("1.0"));
        }
        catch (final Exception ex) {}
        if (RelationTypeSupport.compat) {
            serialPersistentFields = RelationTypeSupport.oldSerialPersistentFields;
            serialVersionUID = -8179019472410837190L;
        }
        else {
            serialPersistentFields = RelationTypeSupport.newSerialPersistentFields;
            serialVersionUID = 4611072955724144607L;
        }
    }
}
