package javax.management.relation;

import java.io.Serializable;
import javax.management.InstanceNotFoundException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.sun.jmx.mbeanserver.Util;
import java.util.List;
import com.sun.jmx.defaults.JmxProperties;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MBeanRegistration;

public class RelationSupport implements RelationSupportMBean, MBeanRegistration
{
    private String myRelId;
    private ObjectName myRelServiceName;
    private MBeanServer myRelServiceMBeanServer;
    private String myRelTypeName;
    private final Map<String, Role> myRoleName2ValueMap;
    private final AtomicBoolean myInRelServFlg;
    
    public RelationSupport(final String s, final ObjectName objectName, final String s2, final RoleList list) throws InvalidRoleValueException, IllegalArgumentException {
        this.myRelId = null;
        this.myRelServiceName = null;
        this.myRelServiceMBeanServer = null;
        this.myRelTypeName = null;
        this.myRoleName2ValueMap = new HashMap<String, Role>();
        this.myInRelServFlg = new AtomicBoolean();
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
        this.initMembers(s, objectName, null, s2, list);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
    }
    
    public RelationSupport(final String s, final ObjectName objectName, final MBeanServer mBeanServer, final String s2, final RoleList list) throws InvalidRoleValueException, IllegalArgumentException {
        this.myRelId = null;
        this.myRelServiceName = null;
        this.myRelServiceMBeanServer = null;
        this.myRelTypeName = null;
        this.myRoleName2ValueMap = new HashMap<String, Role>();
        this.myInRelServFlg = new AtomicBoolean();
        if (mBeanServer == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "RelationSupport");
        this.initMembers(s, objectName, mBeanServer, s2, list);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "RelationSupport");
    }
    
    @Override
    public List<ObjectName> getRole(final String s) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRole", s);
        final List list = Util.cast(this.getRoleInt(s, false, null, false));
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRole");
        return list;
    }
    
    @Override
    public RoleResult getRoles(final String[] array) throws IllegalArgumentException, RelationServiceNotRegisteredException {
        if (array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoles");
        final RoleResult rolesInt = this.getRolesInt(array, false, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoles");
        return rolesInt;
    }
    
    @Override
    public RoleResult getAllRoles() throws RelationServiceNotRegisteredException {
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRoles");
        RoleResult allRolesInt = null;
        try {
            allRolesInt = this.getAllRolesInt(false, null);
        }
        catch (final IllegalArgumentException ex) {}
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRoles");
        return allRolesInt;
    }
    
    @Override
    public RoleList retrieveAllRoles() {
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "retrieveAllRoles");
        final RoleList list;
        synchronized (this.myRoleName2ValueMap) {
            list = new RoleList(new ArrayList<Role>(this.myRoleName2ValueMap.values()));
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "retrieveAllRoles");
        return list;
    }
    
    @Override
    public Integer getRoleCardinality(final String s) throws IllegalArgumentException, RoleNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleCardinality", s);
        final Role role;
        synchronized (this.myRoleName2ValueMap) {
            role = this.myRoleName2ValueMap.get(s);
        }
        if (role == null) {
            final int n = 1;
            try {
                RelationService.throwRoleProblemException(n, s);
            }
            catch (final InvalidRoleValueException ex) {}
        }
        final List<ObjectName> roleValue = role.getRoleValue();
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleCardinality");
        return roleValue.size();
    }
    
    @Override
    public void setRole(final Role role) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException {
        if (role == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRole", role);
        this.setRoleInt(role, false, null, false);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRole");
    }
    
    @Override
    public RoleResult setRoles(final RoleList list) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
        if (list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoles", list);
        final RoleResult setRolesInt = this.setRolesInt(list, false, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoles");
        return setRolesInt;
    }
    
    @Override
    public void handleMBeanUnregistration(final ObjectName objectName, final String s) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
        if (objectName == null || s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistration", new Object[] { objectName, s });
        this.handleMBeanUnregistrationInt(objectName, s, false, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistration");
    }
    
    @Override
    public Map<ObjectName, List<String>> getReferencedMBeans() {
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getReferencedMBeans");
        final HashMap hashMap = new HashMap();
        synchronized (this.myRoleName2ValueMap) {
            for (final Role role : this.myRoleName2ValueMap.values()) {
                final String roleName = role.getRoleName();
                for (final ObjectName objectName : role.getRoleValue()) {
                    List list = (List)hashMap.get(objectName);
                    boolean b = false;
                    if (list == null) {
                        b = true;
                        list = new ArrayList();
                    }
                    list.add(roleName);
                    if (b) {
                        hashMap.put(objectName, list);
                    }
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getReferencedMBeans");
        return hashMap;
    }
    
    @Override
    public String getRelationTypeName() {
        return this.myRelTypeName;
    }
    
    @Override
    public ObjectName getRelationServiceName() {
        return this.myRelServiceName;
    }
    
    @Override
    public String getRelationId() {
        return this.myRelId;
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer myRelServiceMBeanServer, final ObjectName objectName) throws Exception {
        this.myRelServiceMBeanServer = myRelServiceMBeanServer;
        return objectName;
    }
    
    @Override
    public void postRegister(final Boolean b) {
    }
    
    @Override
    public void preDeregister() throws Exception {
    }
    
    @Override
    public void postDeregister() {
    }
    
    @Override
    public Boolean isInRelationService() {
        return this.myInRelServFlg.get();
    }
    
    @Override
    public void setRelationServiceManagementFlag(final Boolean b) throws IllegalArgumentException {
        if (b == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        this.myInRelServFlg.set(b);
    }
    
    Object getRoleInt(final String s, final boolean b, final RelationService relationService, final boolean b2) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException {
        if (s == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRoleInt", s);
        final Role role;
        synchronized (this.myRoleName2ValueMap) {
            role = this.myRoleName2ValueMap.get(s);
        }
        int intValue;
        if (role == null) {
            intValue = 1;
        }
        else {
            Integer checkRoleReading = null;
            Label_0233: {
                if (b) {
                    try {
                        checkRoleReading = relationService.checkRoleReading(s, this.myRelTypeName);
                        break Label_0233;
                    }
                    catch (final RelationTypeNotFoundException ex) {
                        throw new RuntimeException(ex.getMessage());
                    }
                }
                final Object[] array = { s, this.myRelTypeName };
                final String[] array2 = { "java.lang.String", "java.lang.String" };
                try {
                    checkRoleReading = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleReading", array, array2);
                }
                catch (final MBeanException ex2) {
                    throw new RuntimeException("incorrect relation type");
                }
                catch (final ReflectionException ex3) {
                    throw new RuntimeException(ex3.getMessage());
                }
                catch (final InstanceNotFoundException ex4) {
                    throw new RelationServiceNotRegisteredException(ex4.getMessage());
                }
            }
            intValue = checkRoleReading;
        }
        Serializable s2;
        if (intValue == 0) {
            if (!b2) {
                s2 = new ArrayList<Object>(role.getRoleValue());
            }
            else {
                s2 = (Role)role.clone();
            }
        }
        else {
            if (!b2) {
                try {
                    RelationService.throwRoleProblemException(intValue, s);
                    return null;
                }
                catch (final InvalidRoleValueException ex5) {
                    throw new RuntimeException(ex5.getMessage());
                }
            }
            s2 = new RoleUnresolved(s, null, intValue);
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRoleInt");
        return s2;
    }
    
    RoleResult getRolesInt(final String[] array, final boolean b, final RelationService relationService) throws IllegalArgumentException, RelationServiceNotRegisteredException {
        if (array == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getRolesInt");
        final RoleList list = new RoleList();
        final RoleUnresolvedList list2 = new RoleUnresolvedList();
        for (int i = 0; i < array.length; ++i) {
            final String s = array[i];
            Object roleInt;
            try {
                roleInt = this.getRoleInt(s, b, relationService, true);
            }
            catch (final RoleNotFoundException ex) {
                return null;
            }
            if (roleInt instanceof Role) {
                try {
                    list.add((Role)roleInt);
                    continue;
                }
                catch (final IllegalArgumentException ex2) {
                    throw new RuntimeException(ex2.getMessage());
                }
            }
            if (roleInt instanceof RoleUnresolved) {
                try {
                    list2.add((RoleUnresolved)roleInt);
                }
                catch (final IllegalArgumentException ex3) {
                    throw new RuntimeException(ex3.getMessage());
                }
            }
        }
        final RoleResult roleResult = new RoleResult(list, list2);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getRolesInt");
        return roleResult;
    }
    
    RoleResult getAllRolesInt(final boolean b, final RelationService relationService) throws IllegalArgumentException, RelationServiceNotRegisteredException {
        if (b && relationService == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "getAllRolesInt");
        final ArrayList list;
        synchronized (this.myRoleName2ValueMap) {
            list = new ArrayList((Collection<? extends E>)this.myRoleName2ValueMap.keySet());
        }
        final String[] array = new String[list.size()];
        list.toArray(array);
        final RoleResult rolesInt = this.getRolesInt(array, b, relationService);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "getAllRolesInt");
        return rolesInt;
    }
    
    Object setRoleInt(final Role role, final boolean b, final RelationService relationService, final boolean b2) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
        if (role == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRoleInt", new Object[] { role, b, relationService, b2 });
        final String roleName = role.getRoleName();
        final Role role2;
        synchronized (this.myRoleName2ValueMap) {
            role2 = this.myRoleName2ValueMap.get(roleName);
        }
        Boolean b3;
        List<ObjectName> roleValue;
        if (role2 == null) {
            b3 = true;
            roleValue = new ArrayList<ObjectName>();
        }
        else {
            b3 = false;
            roleValue = role2.getRoleValue();
        }
        int intValue;
        try {
            Integer checkRoleWriting;
            if (b) {
                checkRoleWriting = relationService.checkRoleWriting(role, this.myRelTypeName, b3);
            }
            else {
                checkRoleWriting = (Integer)this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "checkRoleWriting", new Object[] { role, this.myRelTypeName, b3 }, new String[] { "javax.management.relation.Role", "java.lang.String", "java.lang.Boolean" });
            }
            intValue = checkRoleWriting;
        }
        catch (final MBeanException ex) {
            final Exception targetException = ex.getTargetException();
            if (targetException instanceof RelationTypeNotFoundException) {
                throw (RelationTypeNotFoundException)targetException;
            }
            throw new RuntimeException(targetException.getMessage());
        }
        catch (final ReflectionException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
        catch (final RelationTypeNotFoundException ex3) {
            throw new RuntimeException(ex3.getMessage());
        }
        catch (final InstanceNotFoundException ex4) {
            throw new RelationServiceNotRegisteredException(ex4.getMessage());
        }
        Object o = null;
        if (intValue == 0) {
            if (!b3) {
                this.sendRoleUpdateNotification(role, roleValue, b, relationService);
                this.updateRelationServiceMap(role, roleValue, b, relationService);
            }
            synchronized (this.myRoleName2ValueMap) {
                this.myRoleName2ValueMap.put(roleName, (Role)role.clone());
            }
            if (b2) {
                o = role;
            }
        }
        else {
            if (!b2) {
                RelationService.throwRoleProblemException(intValue, roleName);
                return null;
            }
            o = new RoleUnresolved(roleName, role.getRoleValue(), intValue);
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRoleInt");
        return o;
    }
    
    private void sendRoleUpdateNotification(final Role role, final List<ObjectName> list, final boolean b, final RelationService relationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
        if (role == null || list == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "sendRoleUpdateNotification", new Object[] { role, list, b, relationService });
        Label_0238: {
            if (b) {
                try {
                    relationService.sendRoleUpdateNotification(this.myRelId, role, list);
                    break Label_0238;
                }
                catch (final RelationNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
            final Object[] array = { this.myRelId, role, list };
            final String[] array2 = { "java.lang.String", "javax.management.relation.Role", "java.util.List" };
            try {
                this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "sendRoleUpdateNotification", array, array2);
            }
            catch (final ReflectionException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final InstanceNotFoundException ex3) {
                throw new RelationServiceNotRegisteredException(ex3.getMessage());
            }
            catch (final MBeanException ex4) {
                final Exception targetException = ex4.getTargetException();
                if (targetException instanceof RelationNotFoundException) {
                    throw (RelationNotFoundException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "sendRoleUpdateNotification");
    }
    
    private void updateRelationServiceMap(final Role role, final List<ObjectName> list, final boolean b, final RelationService relationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
        if (role == null || list == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "updateRelationServiceMap", new Object[] { role, list, b, relationService });
        Label_0238: {
            if (b) {
                try {
                    relationService.updateRoleMap(this.myRelId, role, list);
                    break Label_0238;
                }
                catch (final RelationNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
            final Object[] array = { this.myRelId, role, list };
            final String[] array2 = { "java.lang.String", "javax.management.relation.Role", "java.util.List" };
            try {
                this.myRelServiceMBeanServer.invoke(this.myRelServiceName, "updateRoleMap", array, array2);
            }
            catch (final ReflectionException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final InstanceNotFoundException ex3) {
                throw new RelationServiceNotRegisteredException(ex3.getMessage());
            }
            catch (final MBeanException ex4) {
                final Exception targetException = ex4.getTargetException();
                if (targetException instanceof RelationNotFoundException) {
                    throw (RelationNotFoundException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "updateRelationServiceMap");
    }
    
    RoleResult setRolesInt(final RoleList list, final boolean b, final RelationService relationService) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
        if (list == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "setRolesInt", new Object[] { list, b, relationService });
        final RoleList list2 = new RoleList();
        final RoleUnresolvedList list3 = new RoleUnresolvedList();
        for (final Role role : list.asList()) {
            Object setRoleInt = null;
            try {
                setRoleInt = this.setRoleInt(role, b, relationService, true);
            }
            catch (final RoleNotFoundException ex) {}
            catch (final InvalidRoleValueException ex2) {}
            if (setRoleInt instanceof Role) {
                try {
                    list2.add((Role)setRoleInt);
                    continue;
                }
                catch (final IllegalArgumentException ex3) {
                    throw new RuntimeException(ex3.getMessage());
                }
            }
            if (setRoleInt instanceof RoleUnresolved) {
                try {
                    list3.add((RoleUnresolved)setRoleInt);
                }
                catch (final IllegalArgumentException ex4) {
                    throw new RuntimeException(ex4.getMessage());
                }
            }
        }
        final RoleResult roleResult = new RoleResult(list2, list3);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "setRolesInt");
        return roleResult;
    }
    
    private void initMembers(final String myRelId, final ObjectName myRelServiceName, final MBeanServer myRelServiceMBeanServer, final String myRelTypeName, final RoleList list) throws InvalidRoleValueException, IllegalArgumentException {
        if (myRelId == null || myRelServiceName == null || myRelTypeName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initMembers", new Object[] { myRelId, myRelServiceName, myRelServiceMBeanServer, myRelTypeName, list });
        this.myRelId = myRelId;
        this.myRelServiceName = myRelServiceName;
        this.myRelServiceMBeanServer = myRelServiceMBeanServer;
        this.myRelTypeName = myRelTypeName;
        this.initRoleMap(list);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initMembers");
    }
    
    private void initRoleMap(final RoleList list) throws InvalidRoleValueException {
        if (list == null) {
            return;
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "initRoleMap", list);
        synchronized (this.myRoleName2ValueMap) {
            for (final Role role : list.asList()) {
                final String roleName = role.getRoleName();
                if (this.myRoleName2ValueMap.containsKey(roleName)) {
                    final StringBuilder sb = new StringBuilder("Role name ");
                    sb.append(roleName);
                    sb.append(" used for two roles.");
                    throw new InvalidRoleValueException(sb.toString());
                }
                this.myRoleName2ValueMap.put(roleName, (Role)role.clone());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "initRoleMap");
    }
    
    void handleMBeanUnregistrationInt(final ObjectName objectName, final String s, final boolean b, final RelationService relationService) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException {
        if (objectName == null || s == null || (b && relationService == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationSupport.class.getName(), "handleMBeanUnregistrationInt", new Object[] { objectName, s, b, relationService });
        final Role role;
        synchronized (this.myRoleName2ValueMap) {
            role = this.myRoleName2ValueMap.get(s);
        }
        if (role == null) {
            final StringBuilder sb = new StringBuilder();
            sb.append("No role with name ");
            sb.append(s);
            throw new RoleNotFoundException(sb.toString());
        }
        final ArrayList list = new ArrayList(role.getRoleValue());
        list.remove(objectName);
        this.setRoleInt(new Role(s, (List<ObjectName>)list), b, relationService, false);
        JmxProperties.RELATION_LOGGER.exiting(RelationSupport.class.getName(), "handleMBeanUnregistrationInt");
    }
}
