package javax.management.relation;

import java.util.Date;
import javax.management.NotificationFilter;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.InvalidAttributeValueException;
import com.sun.jmx.mbeanserver.Util;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import java.util.Collection;
import java.util.Iterator;
import com.sun.jmx.defaults.JmxProperties;
import java.util.ArrayList;
import java.util.HashMap;
import javax.management.MBeanServerNotification;
import javax.management.MBeanServer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;
import javax.management.ObjectName;
import java.util.Map;
import javax.management.NotificationListener;
import javax.management.MBeanRegistration;
import javax.management.NotificationBroadcasterSupport;

public class RelationService extends NotificationBroadcasterSupport implements RelationServiceMBean, MBeanRegistration, NotificationListener
{
    private Map<String, Object> myRelId2ObjMap;
    private Map<String, String> myRelId2RelTypeMap;
    private Map<ObjectName, String> myRelMBeanObjName2RelIdMap;
    private Map<String, RelationType> myRelType2ObjMap;
    private Map<String, List<String>> myRelType2RelIdsMap;
    private final Map<ObjectName, Map<String, List<String>>> myRefedMBeanObjName2RelIdsMap;
    private boolean myPurgeFlag;
    private final AtomicLong atomicSeqNo;
    private ObjectName myObjName;
    private MBeanServer myMBeanServer;
    private MBeanServerNotificationFilter myUnregNtfFilter;
    private List<MBeanServerNotification> myUnregNtfList;
    
    public RelationService(final boolean purgeFlag) {
        this.myRelId2ObjMap = new HashMap<String, Object>();
        this.myRelId2RelTypeMap = new HashMap<String, String>();
        this.myRelMBeanObjName2RelIdMap = new HashMap<ObjectName, String>();
        this.myRelType2ObjMap = new HashMap<String, RelationType>();
        this.myRelType2RelIdsMap = new HashMap<String, List<String>>();
        this.myRefedMBeanObjName2RelIdsMap = new HashMap<ObjectName, Map<String, List<String>>>();
        this.myPurgeFlag = true;
        this.atomicSeqNo = new AtomicLong();
        this.myObjName = null;
        this.myMBeanServer = null;
        this.myUnregNtfFilter = null;
        this.myUnregNtfList = new ArrayList<MBeanServerNotification>();
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "RelationService");
        this.setPurgeFlag(purgeFlag);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "RelationService");
    }
    
    @Override
    public void isActive() throws RelationServiceNotRegisteredException {
        if (this.myMBeanServer == null) {
            throw new RelationServiceNotRegisteredException("Relation Service not registered in the MBean Server.");
        }
    }
    
    @Override
    public ObjectName preRegister(final MBeanServer myMBeanServer, final ObjectName myObjName) throws Exception {
        this.myMBeanServer = myMBeanServer;
        return this.myObjName = myObjName;
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
    public boolean getPurgeFlag() {
        return this.myPurgeFlag;
    }
    
    @Override
    public void setPurgeFlag(final boolean myPurgeFlag) {
        this.myPurgeFlag = myPurgeFlag;
    }
    
    @Override
    public void createRelationType(final String s, final RoleInfo[] array) throws IllegalArgumentException, InvalidRelationTypeException {
        if (s == null || array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelationType", s);
        this.addRelationTypeInt(new RelationTypeSupport(s, array));
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelationType");
    }
    
    @Override
    public void addRelationType(final RelationType relationType) throws IllegalArgumentException, InvalidRelationTypeException {
        if (relationType == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationType");
        final List<RoleInfo> roleInfos = relationType.getRoleInfos();
        if (roleInfos == null) {
            throw new InvalidRelationTypeException("No role info provided.");
        }
        final RoleInfo[] array = new RoleInfo[roleInfos.size()];
        int n = 0;
        final Iterator iterator = roleInfos.iterator();
        while (iterator.hasNext()) {
            array[n] = (RoleInfo)iterator.next();
            ++n;
        }
        RelationTypeSupport.checkRoleInfos(array);
        this.addRelationTypeInt(relationType);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationType");
    }
    
    @Override
    public List<String> getAllRelationTypeNames() {
        final ArrayList list;
        synchronized (this.myRelType2ObjMap) {
            list = new ArrayList((Collection<? extends E>)this.myRelType2ObjMap.keySet());
        }
        return list;
    }
    
    @Override
    public List<RoleInfo> getRoleInfos(final String s) throws IllegalArgumentException, RelationTypeNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfos", s);
        final RelationType relationType = this.getRelationType(s);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfos");
        return relationType.getRoleInfos();
    }
    
    @Override
    public RoleInfo getRoleInfo(final String s, final String s2) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException {
        if (s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleInfo", new Object[] { s, s2 });
        final RoleInfo roleInfo = this.getRelationType(s).getRoleInfo(s2);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleInfo");
        return roleInfo;
    }
    
    @Override
    public void removeRelationType(final String s) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException {
        this.isActive();
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelationType", s);
        this.getRelationType(s);
        List list = null;
        synchronized (this.myRelType2RelIdsMap) {
            final List list2 = this.myRelType2RelIdsMap.get(s);
            if (list2 != null) {
                list = new ArrayList(list2);
            }
        }
        synchronized (this.myRelType2ObjMap) {
            this.myRelType2ObjMap.remove(s);
        }
        synchronized (this.myRelType2RelIdsMap) {
            this.myRelType2RelIdsMap.remove(s);
        }
        if (list != null) {
            for (final String s2 : list) {
                try {
                    this.removeRelation(s2);
                }
                catch (final RelationNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelationType");
    }
    
    @Override
    public void createRelation(final String s, final String s2, final RoleList list) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
        this.isActive();
        if (s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "createRelation", new Object[] { s, s2, list });
        this.addRelationInt(true, new RelationSupport(s, this.myObjName, s2, list), null, s, s2, list);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "createRelation");
    }
    
    @Override
    public void addRelation(final ObjectName objectName) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelation", objectName);
        this.isActive();
        if (!this.myMBeanServer.isInstanceOf(objectName, "javax.management.relation.Relation")) {
            throw new NoSuchMethodException("This MBean does not implement the Relation interface.");
        }
        String s;
        try {
            s = (String)this.myMBeanServer.getAttribute(objectName, "RelationId");
        }
        catch (final MBeanException ex) {
            throw new RuntimeException(ex.getTargetException().getMessage());
        }
        catch (final ReflectionException ex2) {
            throw new RuntimeException(ex2.getMessage());
        }
        catch (final AttributeNotFoundException ex3) {
            throw new RuntimeException(ex3.getMessage());
        }
        if (s == null) {
            throw new InvalidRelationIdException("This MBean does not provide a relation id.");
        }
        ObjectName objectName2;
        try {
            objectName2 = (ObjectName)this.myMBeanServer.getAttribute(objectName, "RelationServiceName");
        }
        catch (final MBeanException ex4) {
            throw new RuntimeException(ex4.getTargetException().getMessage());
        }
        catch (final ReflectionException ex5) {
            throw new RuntimeException(ex5.getMessage());
        }
        catch (final AttributeNotFoundException ex6) {
            throw new RuntimeException(ex6.getMessage());
        }
        boolean b = false;
        if (objectName2 == null) {
            b = true;
        }
        else if (!objectName2.equals(this.myObjName)) {
            b = true;
        }
        if (b) {
            throw new InvalidRelationServiceException("The Relation Service referenced in the MBean is not the current one.");
        }
        String s2;
        try {
            s2 = (String)this.myMBeanServer.getAttribute(objectName, "RelationTypeName");
        }
        catch (final MBeanException ex7) {
            throw new RuntimeException(ex7.getTargetException().getMessage());
        }
        catch (final ReflectionException ex8) {
            throw new RuntimeException(ex8.getMessage());
        }
        catch (final AttributeNotFoundException ex9) {
            throw new RuntimeException(ex9.getMessage());
        }
        if (s2 == null) {
            throw new RelationTypeNotFoundException("No relation type provided.");
        }
        RoleList list;
        try {
            list = (RoleList)this.myMBeanServer.invoke(objectName, "retrieveAllRoles", null, null);
        }
        catch (final MBeanException ex10) {
            throw new RuntimeException(ex10.getTargetException().getMessage());
        }
        catch (final ReflectionException ex11) {
            throw new RuntimeException(ex11.getMessage());
        }
        this.addRelationInt(false, null, objectName, s, s2, list);
        synchronized (this.myRelMBeanObjName2RelIdMap) {
            this.myRelMBeanObjName2RelIdMap.put(objectName, s);
        }
        try {
            this.myMBeanServer.setAttribute(objectName, new Attribute("RelationServiceManagementFlag", Boolean.TRUE));
        }
        catch (final Exception ex12) {}
        final ArrayList list2 = new ArrayList();
        list2.add(objectName);
        this.updateUnregistrationListener(list2, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelation");
    }
    
    @Override
    public ObjectName isRelationMBean(final String s) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelationMBean", s);
        final Object relation = this.getRelation(s);
        if (relation instanceof ObjectName) {
            return (ObjectName)relation;
        }
        return null;
    }
    
    @Override
    public String isRelation(final ObjectName objectName) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "isRelation", objectName);
        String s = null;
        synchronized (this.myRelMBeanObjName2RelIdMap) {
            final String s2 = this.myRelMBeanObjName2RelIdMap.get(objectName);
            if (s2 != null) {
                s = s2;
            }
        }
        return s;
    }
    
    @Override
    public Boolean hasRelation(final String s) throws IllegalArgumentException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "hasRelation", s);
        try {
            this.getRelation(s);
            return true;
        }
        catch (final RelationNotFoundException ex) {
            return false;
        }
    }
    
    @Override
    public List<String> getAllRelationIds() {
        final ArrayList list;
        synchronized (this.myRelId2ObjMap) {
            list = new ArrayList((Collection<? extends E>)this.myRelId2ObjMap.keySet());
        }
        return list;
    }
    
    @Override
    public Integer checkRoleReading(final String s, final String s2) throws IllegalArgumentException, RelationTypeNotFoundException {
        if (s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleReading", new Object[] { s, s2 });
        final RelationType relationType = this.getRelationType(s2);
        Integer n;
        try {
            n = this.checkRoleInt(1, s, null, relationType.getRoleInfo(s), false);
        }
        catch (final RoleInfoNotFoundException ex) {
            n = 1;
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleReading");
        return n;
    }
    
    @Override
    public Integer checkRoleWriting(final Role role, final String s, final Boolean b) throws IllegalArgumentException, RelationTypeNotFoundException {
        if (role == null || s == null || b == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleWriting", new Object[] { role, s, b });
        final RelationType relationType = this.getRelationType(s);
        final String roleName = role.getRoleName();
        final List<ObjectName> roleValue = role.getRoleValue();
        boolean b2 = true;
        if (b) {
            b2 = false;
        }
        RoleInfo roleInfo;
        try {
            roleInfo = relationType.getRoleInfo(roleName);
        }
        catch (final RoleInfoNotFoundException ex) {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
            return 1;
        }
        final Integer checkRoleInt = this.checkRoleInt(2, roleName, roleValue, roleInfo, b2);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleWriting");
        return checkRoleInt;
    }
    
    @Override
    public void sendRelationCreationNotification(final String s) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationCreationNotification", s);
        final StringBuilder sb = new StringBuilder("Creation of relation ");
        sb.append(s);
        this.sendNotificationInt(1, sb.toString(), s, null, null, null, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationCreationNotification");
    }
    
    @Override
    public void sendRoleUpdateNotification(final String s, final Role role, List<ObjectName> list) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null || role == null || list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        if (!(list instanceof ArrayList)) {
            list = new ArrayList<ObjectName>(list);
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRoleUpdateNotification", new Object[] { s, role, list });
        final String roleName = role.getRoleName();
        final List<ObjectName> roleValue = role.getRoleValue();
        final String roleValueToString = Role.roleValueToString(roleValue);
        final String roleValueToString2 = Role.roleValueToString((List<ObjectName>)list);
        final StringBuilder sb = new StringBuilder("Value of role ");
        sb.append(roleName);
        sb.append(" has changed\nOld value:\n");
        sb.append(roleValueToString2);
        sb.append("\nNew value:\n");
        sb.append(roleValueToString);
        this.sendNotificationInt(2, sb.toString(), s, null, roleName, roleValue, (List<ObjectName>)list);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRoleUpdateNotification");
    }
    
    @Override
    public void sendRelationRemovalNotification(final String s, final List<ObjectName> list) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendRelationRemovalNotification", new Object[] { s, list });
        this.sendNotificationInt(3, "Removal of relation " + s, s, list, null, null, null);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendRelationRemovalNotification");
    }
    
    @Override
    public void updateRoleMap(final String s, final Role role, final List<ObjectName> list) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException {
        if (s == null || role == null || list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateRoleMap", new Object[] { s, role, list });
        this.isActive();
        this.getRelation(s);
        final String roleName = role.getRoleName();
        final List<ObjectName> roleValue = role.getRoleValue();
        final ArrayList list2 = new ArrayList((Collection<? extends E>)list);
        final ArrayList list3 = new ArrayList();
        for (final ObjectName objectName : roleValue) {
            final int index = list2.indexOf(objectName);
            if (index == -1) {
                if (!this.addNewMBeanReference(objectName, s, roleName)) {
                    continue;
                }
                list3.add(objectName);
            }
            else {
                list2.remove(index);
            }
        }
        final ArrayList list4 = new ArrayList();
        for (final ObjectName objectName2 : list2) {
            if (this.removeMBeanReference(objectName2, s, roleName, false)) {
                list4.add(objectName2);
            }
        }
        this.updateUnregistrationListener(list3, list4);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateRoleMap");
    }
    
    @Override
    public void removeRelation(final String s) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
        this.isActive();
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeRelation", s);
        final Object relation = this.getRelation(s);
        if (relation instanceof ObjectName) {
            final ArrayList list = new ArrayList();
            list.add(relation);
            this.updateUnregistrationListener(null, list);
        }
        this.sendRelationRemovalNotification(s, null);
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            for (final ObjectName objectName : this.myRefedMBeanObjName2RelIdsMap.keySet()) {
                final Map map = this.myRefedMBeanObjName2RelIdsMap.get(objectName);
                if (map.containsKey(s)) {
                    map.remove(s);
                    list2.add(objectName);
                }
                if (map.isEmpty()) {
                    list3.add(objectName);
                }
            }
            final Iterator iterator2 = list3.iterator();
            while (iterator2.hasNext()) {
                this.myRefedMBeanObjName2RelIdsMap.remove(iterator2.next());
            }
        }
        synchronized (this.myRelId2ObjMap) {
            this.myRelId2ObjMap.remove(s);
        }
        if (relation instanceof ObjectName) {
            synchronized (this.myRelMBeanObjName2RelIdMap) {
                this.myRelMBeanObjName2RelIdMap.remove(relation);
            }
        }
        final String s2;
        synchronized (this.myRelId2RelTypeMap) {
            s2 = this.myRelId2RelTypeMap.get(s);
            this.myRelId2RelTypeMap.remove(s);
        }
        synchronized (this.myRelType2RelIdsMap) {
            final List list4 = this.myRelType2RelIdsMap.get(s2);
            if (list4 != null) {
                list4.remove(s);
                if (list4.isEmpty()) {
                    this.myRelType2RelIdsMap.remove(s2);
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeRelation");
    }
    
    @Override
    public void purgeRelations() throws RelationServiceNotRegisteredException {
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "purgeRelations");
        this.isActive();
        final ArrayList list;
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            list = new ArrayList((Collection<? extends E>)this.myUnregNtfList);
            this.myUnregNtfList = new ArrayList<MBeanServerNotification>();
        }
        final ArrayList list2 = new ArrayList();
        final HashMap hashMap = new HashMap();
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                final ObjectName mBeanName = ((MBeanServerNotification)iterator.next()).getMBeanName();
                list2.add(mBeanName);
                hashMap.put(mBeanName, this.myRefedMBeanObjName2RelIdsMap.get(mBeanName));
                this.myRefedMBeanObjName2RelIdsMap.remove(mBeanName);
            }
        }
        this.updateUnregistrationListener(null, list2);
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            final ObjectName mBeanName2 = ((MBeanServerNotification)iterator2.next()).getMBeanName();
            for (final Map.Entry entry : ((Map)hashMap.get(mBeanName2)).entrySet()) {
                final String s = (String)entry.getKey();
                final List list3 = (List)entry.getValue();
                try {
                    this.handleReferenceUnregistration(s, mBeanName2, list3);
                }
                catch (final RelationNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                catch (final RoleNotFoundException ex2) {
                    throw new RuntimeException(ex2.getMessage());
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "purgeRelations");
    }
    
    @Override
    public Map<String, List<String>> findReferencingRelations(final ObjectName objectName, final String s, final String s2) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findReferencingRelations", new Object[] { objectName, s, s2 });
        final HashMap hashMap = new HashMap();
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            final Map map = this.myRefedMBeanObjName2RelIdsMap.get(objectName);
            if (map != null) {
                final Set keySet = map.keySet();
                ArrayList list;
                if (s == null) {
                    list = new ArrayList(keySet);
                }
                else {
                    list = new ArrayList();
                    for (final String s3 : keySet) {
                        final String s4;
                        synchronized (this.myRelId2RelTypeMap) {
                            s4 = this.myRelId2RelTypeMap.get(s3);
                        }
                        if (s4.equals(s)) {
                            list.add(s3);
                        }
                    }
                }
                for (final String s5 : list) {
                    final List list2 = (List)map.get(s5);
                    if (s2 == null) {
                        hashMap.put(s5, new ArrayList(list2));
                    }
                    else {
                        if (!list2.contains(s2)) {
                            continue;
                        }
                        final ArrayList list3 = new ArrayList();
                        list3.add(s2);
                        hashMap.put(s5, list3);
                    }
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findReferencingRelations");
        return hashMap;
    }
    
    @Override
    public Map<ObjectName, List<String>> findAssociatedMBeans(final ObjectName objectName, final String s, final String s2) throws IllegalArgumentException {
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findAssociatedMBeans", new Object[] { objectName, s, s2 });
        final Map<String, List<String>> referencingRelations = this.findReferencingRelations(objectName, s, s2);
        final HashMap hashMap = new HashMap();
        for (final String s3 : referencingRelations.keySet()) {
            Map<ObjectName, List<String>> referencedMBeans;
            try {
                referencedMBeans = this.getReferencedMBeans(s3);
            }
            catch (final RelationNotFoundException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            for (final ObjectName objectName2 : referencedMBeans.keySet()) {
                if (!objectName2.equals(objectName)) {
                    final List list = (List)hashMap.get(objectName2);
                    if (list == null) {
                        final ArrayList list2 = new ArrayList();
                        list2.add(s3);
                        hashMap.put(objectName2, list2);
                    }
                    else {
                        list.add(s3);
                    }
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findAssociatedMBeans");
        return hashMap;
    }
    
    @Override
    public List<String> findRelationsOfType(final String s) throws IllegalArgumentException, RelationTypeNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "findRelationsOfType");
        this.getRelationType(s);
        ArrayList list2;
        synchronized (this.myRelType2RelIdsMap) {
            final List list = this.myRelType2RelIdsMap.get(s);
            if (list == null) {
                list2 = new ArrayList();
            }
            else {
                list2 = new ArrayList(list);
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "findRelationsOfType");
        return list2;
    }
    
    @Override
    public List<ObjectName> getRole(final String s, final String s2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException {
        if (s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRole", new Object[] { s, s2 });
        this.isActive();
        final Object relation = this.getRelation(s);
        List list;
        if (relation instanceof RelationSupport) {
            list = Util.cast(((RelationSupport)relation).getRoleInt(s2, true, this, false));
        }
        else {
            final Object[] array = { s2 };
            final String[] array2 = { "java.lang.String" };
            try {
                final List list2 = Util.cast(this.myMBeanServer.invoke((ObjectName)relation, "getRole", array, array2));
                if (list2 == null || list2 instanceof ArrayList) {
                    list = list2;
                }
                else {
                    list = new ArrayList(list2);
                }
            }
            catch (final InstanceNotFoundException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            catch (final ReflectionException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final MBeanException ex3) {
                final Exception targetException = ex3.getTargetException();
                if (targetException instanceof RoleNotFoundException) {
                    throw (RoleNotFoundException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRole");
        return list;
    }
    
    @Override
    public RoleResult getRoles(final String s, final String[] array) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
        if (s == null || array == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", s);
        this.isActive();
        final Object relation = this.getRelation(s);
        RoleResult rolesInt;
        if (relation instanceof RelationSupport) {
            rolesInt = ((RelationSupport)relation).getRolesInt(array, true, this);
        }
        else {
            final Object[] array2 = { array };
            final String[] array3 = { null };
            try {
                array3[0] = array.getClass().getName();
            }
            catch (final Exception ex) {}
            try {
                rolesInt = (RoleResult)this.myMBeanServer.invoke((ObjectName)relation, "getRoles", array2, array3);
            }
            catch (final InstanceNotFoundException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final ReflectionException ex3) {
                throw new RuntimeException(ex3.getMessage());
            }
            catch (final MBeanException ex4) {
                throw new RuntimeException(ex4.getTargetException().getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
        return rolesInt;
    }
    
    @Override
    public RoleResult getAllRoles(final String s) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoles", s);
        final Object relation = this.getRelation(s);
        RoleResult allRolesInt;
        if (relation instanceof RelationSupport) {
            allRolesInt = ((RelationSupport)relation).getAllRolesInt(true, this);
        }
        else {
            try {
                allRolesInt = (RoleResult)this.myMBeanServer.getAttribute((ObjectName)relation, "AllRoles");
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoles");
        return allRolesInt;
    }
    
    @Override
    public Integer getRoleCardinality(final String s, final String s2) throws IllegalArgumentException, RelationNotFoundException, RoleNotFoundException {
        if (s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRoleCardinality", new Object[] { s, s2 });
        final Object relation = this.getRelation(s);
        Integer roleCardinality;
        if (relation instanceof RelationSupport) {
            roleCardinality = ((RelationSupport)relation).getRoleCardinality(s2);
        }
        else {
            final Object[] array = { s2 };
            final String[] array2 = { "java.lang.String" };
            try {
                roleCardinality = (Integer)this.myMBeanServer.invoke((ObjectName)relation, "getRoleCardinality", array, array2);
            }
            catch (final InstanceNotFoundException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            catch (final ReflectionException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final MBeanException ex3) {
                final Exception targetException = ex3.getTargetException();
                if (targetException instanceof RoleNotFoundException) {
                    throw (RoleNotFoundException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRoleCardinality");
        return roleCardinality;
    }
    
    @Override
    public void setRole(final String s, final Role role) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException {
        if (s == null || role == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRole", new Object[] { s, role });
        this.isActive();
        final Object relation = this.getRelation(s);
        Label_0251: {
            if (relation instanceof RelationSupport) {
                try {
                    ((RelationSupport)relation).setRoleInt(role, true, this, false);
                    break Label_0251;
                }
                catch (final RelationTypeNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
            (new Object[1])[0] = role;
            (new String[1])[0] = "javax.management.relation.Role";
            try {
                this.myMBeanServer.setAttribute((ObjectName)relation, new Attribute("Role", role));
            }
            catch (final InstanceNotFoundException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final ReflectionException ex3) {
                throw new RuntimeException(ex3.getMessage());
            }
            catch (final MBeanException ex4) {
                final Exception targetException = ex4.getTargetException();
                if (targetException instanceof RoleNotFoundException) {
                    throw (RoleNotFoundException)targetException;
                }
                if (targetException instanceof InvalidRoleValueException) {
                    throw (InvalidRoleValueException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
            catch (final AttributeNotFoundException ex5) {
                throw new RuntimeException(ex5.getMessage());
            }
            catch (final InvalidAttributeValueException ex6) {
                throw new RuntimeException(ex6.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRole");
    }
    
    @Override
    public RoleResult setRoles(final String s, final RoleList list) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException {
        if (s == null || list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "setRoles", new Object[] { s, list });
        this.isActive();
        final Object relation = this.getRelation(s);
        RoleResult setRolesInt = null;
        Label_0193: {
            if (relation instanceof RelationSupport) {
                try {
                    setRolesInt = ((RelationSupport)relation).setRolesInt(list, true, this);
                    break Label_0193;
                }
                catch (final RelationTypeNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            }
            final Object[] array = { list };
            final String[] array2 = { "javax.management.relation.RoleList" };
            try {
                setRolesInt = (RoleResult)this.myMBeanServer.invoke((ObjectName)relation, "setRoles", array, array2);
            }
            catch (final InstanceNotFoundException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            catch (final ReflectionException ex3) {
                throw new RuntimeException(ex3.getMessage());
            }
            catch (final MBeanException ex4) {
                throw new RuntimeException(ex4.getTargetException().getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "setRoles");
        return setRolesInt;
    }
    
    @Override
    public Map<ObjectName, List<String>> getReferencedMBeans(final String s) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getReferencedMBeans", s);
        final Object relation = this.getRelation(s);
        Map<ObjectName, List<String>> referencedMBeans;
        if (relation instanceof RelationSupport) {
            referencedMBeans = ((RelationSupport)relation).getReferencedMBeans();
        }
        else {
            try {
                referencedMBeans = Util.cast(this.myMBeanServer.getAttribute((ObjectName)relation, "ReferencedMBeans"));
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getReferencedMBeans");
        return referencedMBeans;
    }
    
    @Override
    public String getRelationTypeName(final String s) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationTypeName", s);
        final Object relation = this.getRelation(s);
        String relationTypeName;
        if (relation instanceof RelationSupport) {
            relationTypeName = ((RelationSupport)relation).getRelationTypeName();
        }
        else {
            try {
                relationTypeName = (String)this.myMBeanServer.getAttribute((ObjectName)relation, "RelationTypeName");
            }
            catch (final Exception ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationTypeName");
        return relationTypeName;
    }
    
    @Override
    public void handleNotification(final Notification notification, final Object o) {
        if (notification == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleNotification", notification);
        if (notification instanceof MBeanServerNotification) {
            final MBeanServerNotification mBeanServerNotification = (MBeanServerNotification)notification;
            if (notification.getType().equals("JMX.mbean.unregistered")) {
                final ObjectName mBeanName = ((MBeanServerNotification)notification).getMBeanName();
                boolean b = false;
                synchronized (this.myRefedMBeanObjName2RelIdsMap) {
                    if (this.myRefedMBeanObjName2RelIdsMap.containsKey(mBeanName)) {
                        synchronized (this.myUnregNtfList) {
                            this.myUnregNtfList.add(mBeanServerNotification);
                        }
                        b = true;
                    }
                    if (b && this.myPurgeFlag) {
                        try {
                            this.purgeRelations();
                        }
                        catch (final Exception ex) {
                            throw new RuntimeException(ex.getMessage());
                        }
                    }
                }
                final String s;
                synchronized (this.myRelMBeanObjName2RelIdMap) {
                    s = this.myRelMBeanObjName2RelIdMap.get(mBeanName);
                }
                if (s != null) {
                    try {
                        this.removeRelation(s);
                    }
                    catch (final Exception ex2) {
                        throw new RuntimeException(ex2.getMessage());
                    }
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleNotification");
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getNotificationInfo");
        final MBeanNotificationInfo mBeanNotificationInfo = new MBeanNotificationInfo(new String[] { "jmx.relation.creation.basic", "jmx.relation.creation.mbean", "jmx.relation.update.basic", "jmx.relation.update.mbean", "jmx.relation.removal.basic", "jmx.relation.removal.mbean" }, "javax.management.relation.RelationNotification", "Sent when a relation is created, updated or deleted.");
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getNotificationInfo");
        return new MBeanNotificationInfo[] { mBeanNotificationInfo };
    }
    
    private void addRelationTypeInt(final RelationType relationType) throws IllegalArgumentException, InvalidRelationTypeException {
        if (relationType == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationTypeInt");
        final String relationTypeName = relationType.getRelationTypeName();
        try {
            if (this.getRelationType(relationTypeName) != null) {
                final StringBuilder sb = new StringBuilder("There is already a relation type in the Relation Service with name ");
                sb.append(relationTypeName);
                throw new InvalidRelationTypeException(sb.toString());
            }
        }
        catch (final RelationTypeNotFoundException ex) {}
        synchronized (this.myRelType2ObjMap) {
            this.myRelType2ObjMap.put(relationTypeName, relationType);
        }
        if (relationType instanceof RelationTypeSupport) {
            ((RelationTypeSupport)relationType).setRelationServiceFlag(true);
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationTypeInt");
    }
    
    RelationType getRelationType(final String s) throws IllegalArgumentException, RelationTypeNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelationType", s);
        final RelationType relationType;
        synchronized (this.myRelType2ObjMap) {
            relationType = this.myRelType2ObjMap.get(s);
        }
        if (relationType == null) {
            final StringBuilder sb = new StringBuilder("No relation type created in the Relation Service with the name ");
            sb.append(s);
            throw new RelationTypeNotFoundException(sb.toString());
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelationType");
        return relationType;
    }
    
    Object getRelation(final String s) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "getRelation", s);
        final Object value;
        synchronized (this.myRelId2ObjMap) {
            value = this.myRelId2ObjMap.get(s);
        }
        if (value == null) {
            throw new RelationNotFoundException("No relation associated to relation id " + s);
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "getRelation");
        return value;
    }
    
    private boolean addNewMBeanReference(final ObjectName objectName, final String s, final String s2) throws IllegalArgumentException {
        if (objectName == null || s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addNewMBeanReference", new Object[] { objectName, s, s2 });
        boolean b = false;
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            final Map map = this.myRefedMBeanObjName2RelIdsMap.get(objectName);
            if (map == null) {
                b = true;
                final ArrayList list = new ArrayList();
                list.add(s2);
                final HashMap hashMap = new HashMap();
                hashMap.put(s, list);
                this.myRefedMBeanObjName2RelIdsMap.put(objectName, hashMap);
            }
            else {
                final List list2 = (List)map.get(s);
                if (list2 == null) {
                    final ArrayList list3 = new ArrayList();
                    list3.add(s2);
                    map.put(s, list3);
                }
                else {
                    list2.add(s2);
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addNewMBeanReference");
        return b;
    }
    
    private boolean removeMBeanReference(final ObjectName objectName, final String s, final String s2, final boolean b) throws IllegalArgumentException {
        if (objectName == null || s == null || s2 == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "removeMBeanReference", new Object[] { objectName, s, s2, b });
        boolean b2 = false;
        synchronized (this.myRefedMBeanObjName2RelIdsMap) {
            final Map map = this.myRefedMBeanObjName2RelIdsMap.get(objectName);
            if (map == null) {
                JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
                return true;
            }
            List list = null;
            if (!b) {
                list = (List)map.get(s);
                final int index = list.indexOf(s2);
                if (index != -1) {
                    list.remove(index);
                }
            }
            if (list.isEmpty() || b) {
                map.remove(s);
            }
            if (map.isEmpty()) {
                this.myRefedMBeanObjName2RelIdsMap.remove(objectName);
                b2 = true;
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "removeMBeanReference");
        return b2;
    }
    
    private void updateUnregistrationListener(final List<ObjectName> list, final List<ObjectName> list2) throws RelationServiceNotRegisteredException {
        if (list != null && list2 != null && list.isEmpty() && list2.isEmpty()) {
            return;
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "updateUnregistrationListener", new Object[] { list, list2 });
        this.isActive();
        if (list != null || list2 != null) {
            boolean b = false;
            if (this.myUnregNtfFilter == null) {
                this.myUnregNtfFilter = new MBeanServerNotificationFilter();
                b = true;
            }
            synchronized (this.myUnregNtfFilter) {
                if (list != null) {
                    final Iterator iterator = list.iterator();
                    while (iterator.hasNext()) {
                        this.myUnregNtfFilter.enableObjectName((ObjectName)iterator.next());
                    }
                }
                if (list2 != null) {
                    final Iterator iterator2 = list2.iterator();
                    while (iterator2.hasNext()) {
                        this.myUnregNtfFilter.disableObjectName((ObjectName)iterator2.next());
                    }
                }
                if (b) {
                    try {
                        this.myMBeanServer.addNotificationListener(MBeanServerDelegate.DELEGATE_NAME, this, this.myUnregNtfFilter, null);
                    }
                    catch (final InstanceNotFoundException ex) {
                        throw new RelationServiceNotRegisteredException(ex.getMessage());
                    }
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "updateUnregistrationListener");
    }
    
    private void addRelationInt(final boolean b, final RelationSupport relationSupport, final ObjectName objectName, final String s, final String s2, final RoleList list) throws IllegalArgumentException, RelationServiceNotRegisteredException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException {
        if (s == null || s2 == null || (b && (relationSupport == null || objectName != null)) || (!b && (objectName == null || relationSupport != null))) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "addRelationInt", new Object[] { b, relationSupport, objectName, s, s2, list });
        this.isActive();
        try {
            if (this.getRelation(s) != null) {
                final StringBuilder sb = new StringBuilder("There is already a relation with id ");
                sb.append(s);
                throw new InvalidRelationIdException(sb.toString());
            }
        }
        catch (final RelationNotFoundException ex) {}
        final RelationType relationType = this.getRelationType(s2);
        final ArrayList list2 = new ArrayList(relationType.getRoleInfos());
        if (list != null) {
            for (final Role role : list.asList()) {
                final String roleName = role.getRoleName();
                final List<ObjectName> roleValue = role.getRoleValue();
                RoleInfo roleInfo;
                try {
                    roleInfo = relationType.getRoleInfo(roleName);
                }
                catch (final RoleInfoNotFoundException ex2) {
                    throw new RoleNotFoundException(ex2.getMessage());
                }
                final int intValue = this.checkRoleInt(2, roleName, roleValue, roleInfo, false);
                if (intValue != 0) {
                    throwRoleProblemException(intValue, roleName);
                }
                list2.remove(list2.indexOf(roleInfo));
            }
        }
        this.initializeMissingRoles(b, relationSupport, objectName, s, s2, (List<RoleInfo>)list2);
        synchronized (this.myRelId2ObjMap) {
            if (b) {
                this.myRelId2ObjMap.put(s, relationSupport);
            }
            else {
                this.myRelId2ObjMap.put(s, objectName);
            }
        }
        synchronized (this.myRelId2RelTypeMap) {
            this.myRelId2RelTypeMap.put(s, s2);
        }
        synchronized (this.myRelType2RelIdsMap) {
            List<String> list3 = this.myRelType2RelIdsMap.get(s2);
            boolean b2 = false;
            if (list3 == null) {
                b2 = true;
                list3 = new ArrayList<String>();
            }
            list3.add(s);
            if (b2) {
                this.myRelType2RelIdsMap.put(s2, list3);
            }
        }
        for (final Role role2 : list.asList()) {
            final ArrayList<ObjectName> list4 = new ArrayList<ObjectName>();
            try {
                this.updateRoleMap(s, role2, list4);
            }
            catch (final RelationNotFoundException ex3) {}
        }
        try {
            this.sendRelationCreationNotification(s);
        }
        catch (final RelationNotFoundException ex4) {}
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "addRelationInt");
    }
    
    private Integer checkRoleInt(final int n, final String s, final List<ObjectName> list, final RoleInfo roleInfo, final boolean b) throws IllegalArgumentException {
        if (s == null || roleInfo == null || (n == 2 && list == null)) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "checkRoleInt", new Object[] { n, s, list, roleInfo, b });
        if (!s.equals(roleInfo.getName())) {
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
            return 1;
        }
        if (n == 1) {
            if (!roleInfo.isReadable()) {
                JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                return 2;
            }
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
            return new Integer(0);
        }
        else {
            if (b && !roleInfo.isWritable()) {
                JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                return new Integer(3);
            }
            final int size = list.size();
            if (!roleInfo.checkMinDegree(size)) {
                JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                return new Integer(4);
            }
            if (!roleInfo.checkMaxDegree(size)) {
                JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                return new Integer(5);
            }
            final String refMBeanClassName = roleInfo.getRefMBeanClassName();
            for (final ObjectName objectName : list) {
                if (objectName == null) {
                    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                    return new Integer(7);
                }
                try {
                    if (!this.myMBeanServer.isInstanceOf(objectName, refMBeanClassName)) {
                        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                        return new Integer(6);
                    }
                    continue;
                }
                catch (final InstanceNotFoundException ex) {
                    JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
                    return new Integer(7);
                }
            }
            JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "checkRoleInt");
            return new Integer(0);
        }
    }
    
    private void initializeMissingRoles(final boolean b, final RelationSupport relationSupport, final ObjectName objectName, final String s, final String s2, final List<RoleInfo> list) throws IllegalArgumentException, RelationServiceNotRegisteredException, InvalidRoleValueException {
        if ((b && (relationSupport == null || objectName != null)) || (!b && (objectName == null || relationSupport != null)) || s == null || s2 == null || list == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "initializeMissingRoles", new Object[] { b, relationSupport, objectName, s, s2, list });
        this.isActive();
        final Iterator<RoleInfo> iterator = list.iterator();
        while (iterator.hasNext()) {
            final Role role = new Role(iterator.next().getName(), new ArrayList<ObjectName>());
            if (b) {
                try {
                    relationSupport.setRoleInt(role, true, this, false);
                    continue;
                }
                catch (final RoleNotFoundException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
                catch (final RelationNotFoundException ex2) {
                    throw new RuntimeException(ex2.getMessage());
                }
                catch (final RelationTypeNotFoundException ex3) {
                    throw new RuntimeException(ex3.getMessage());
                }
            }
            (new Object[1])[0] = role;
            (new String[1])[0] = "javax.management.relation.Role";
            try {
                this.myMBeanServer.setAttribute(objectName, new Attribute("Role", role));
            }
            catch (final InstanceNotFoundException ex4) {
                throw new RuntimeException(ex4.getMessage());
            }
            catch (final ReflectionException ex5) {
                throw new RuntimeException(ex5.getMessage());
            }
            catch (final MBeanException ex6) {
                final Exception targetException = ex6.getTargetException();
                if (targetException instanceof InvalidRoleValueException) {
                    throw (InvalidRoleValueException)targetException;
                }
                throw new RuntimeException(targetException.getMessage());
            }
            catch (final AttributeNotFoundException ex7) {
                throw new RuntimeException(ex7.getMessage());
            }
            catch (final InvalidAttributeValueException ex8) {
                throw new RuntimeException(ex8.getMessage());
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "initializeMissingRoles");
    }
    
    static void throwRoleProblemException(final int n, final String s) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException {
        if (s == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        int n2 = 0;
        String s2 = null;
        switch (n) {
            case 1: {
                s2 = " does not exist in relation.";
                n2 = 1;
                break;
            }
            case 2: {
                s2 = " is not readable.";
                n2 = 1;
                break;
            }
            case 3: {
                s2 = " is not writable.";
                n2 = 1;
                break;
            }
            case 4: {
                s2 = " has a number of MBean references less than the expected minimum degree.";
                n2 = 2;
                break;
            }
            case 5: {
                s2 = " has a number of MBean references greater than the expected maximum degree.";
                n2 = 2;
                break;
            }
            case 6: {
                s2 = " has an MBean reference to an MBean not of the expected class of references for that role.";
                n2 = 2;
                break;
            }
            case 7: {
                s2 = " has a reference to null or to an MBean not registered.";
                n2 = 2;
                break;
            }
        }
        final StringBuilder sb = new StringBuilder(s);
        sb.append(s2);
        final String string = sb.toString();
        if (n2 == 1) {
            throw new RoleNotFoundException(string);
        }
        if (n2 == 2) {
            throw new InvalidRoleValueException(string);
        }
    }
    
    private void sendNotificationInt(final int n, final String s, final String s2, final List<ObjectName> list, final String s3, final List<ObjectName> list2, final List<ObjectName> list3) throws IllegalArgumentException, RelationNotFoundException {
        if (s == null || s2 == null || (n != 3 && list != null) || (n == 2 && (s3 == null || list2 == null || list3 == null))) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "sendNotificationInt", new Object[] { n, s, s2, list, s3, list2, list3 });
        final String s4;
        synchronized (this.myRelId2RelTypeMap) {
            s4 = this.myRelId2RelTypeMap.get(s2);
        }
        final ObjectName relationMBean = this.isRelationMBean(s2);
        String s5 = null;
        if (relationMBean != null) {
            switch (n) {
                case 1: {
                    s5 = "jmx.relation.creation.mbean";
                    break;
                }
                case 2: {
                    s5 = "jmx.relation.update.mbean";
                    break;
                }
                case 3: {
                    s5 = "jmx.relation.removal.mbean";
                    break;
                }
            }
        }
        else {
            switch (n) {
                case 1: {
                    s5 = "jmx.relation.creation.basic";
                    break;
                }
                case 2: {
                    s5 = "jmx.relation.update.basic";
                    break;
                }
                case 3: {
                    s5 = "jmx.relation.removal.basic";
                    break;
                }
            }
        }
        final Long value = this.atomicSeqNo.incrementAndGet();
        final long time = new Date().getTime();
        Notification notification = null;
        if (s5.equals("jmx.relation.creation.basic") || s5.equals("jmx.relation.creation.mbean") || s5.equals("jmx.relation.removal.basic") || s5.equals("jmx.relation.removal.mbean")) {
            notification = new RelationNotification(s5, this, value, time, s, s2, s4, relationMBean, list);
        }
        else if (s5.equals("jmx.relation.update.basic") || s5.equals("jmx.relation.update.mbean")) {
            notification = new RelationNotification(s5, this, value, time, s, s2, s4, relationMBean, s3, list2, list3);
        }
        this.sendNotification(notification);
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "sendNotificationInt");
    }
    
    private void handleReferenceUnregistration(final String s, final ObjectName objectName, final List<String> list) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException, RoleNotFoundException {
        if (s == null || list == null || objectName == null) {
            throw new IllegalArgumentException("Invalid parameter.");
        }
        JmxProperties.RELATION_LOGGER.entering(RelationService.class.getName(), "handleReferenceUnregistration", new Object[] { s, objectName, list });
        this.isActive();
        final String relationTypeName = this.getRelationTypeName(s);
        final Object relation = this.getRelation(s);
        int n = 0;
        for (final String s2 : list) {
            if (n != 0) {
                break;
            }
            final int n2 = this.getRoleCardinality(s, s2) - 1;
            RoleInfo roleInfo;
            try {
                roleInfo = this.getRoleInfo(relationTypeName, s2);
            }
            catch (final RelationTypeNotFoundException ex) {
                throw new RuntimeException(ex.getMessage());
            }
            catch (final RoleInfoNotFoundException ex2) {
                throw new RuntimeException(ex2.getMessage());
            }
            if (roleInfo.checkMinDegree(n2)) {
                continue;
            }
            n = 1;
        }
        if (n != 0) {
            this.removeRelation(s);
        }
        else {
            for (final String s3 : list) {
                if (relation instanceof RelationSupport) {
                    try {
                        ((RelationSupport)relation).handleMBeanUnregistrationInt(objectName, s3, true, this);
                        continue;
                    }
                    catch (final RelationTypeNotFoundException ex3) {
                        throw new RuntimeException(ex3.getMessage());
                    }
                    catch (final InvalidRoleValueException ex4) {
                        throw new RuntimeException(ex4.getMessage());
                    }
                }
                final Object[] array = { objectName, s3 };
                final String[] array2 = { "javax.management.ObjectName", "java.lang.String" };
                try {
                    this.myMBeanServer.invoke((ObjectName)relation, "handleMBeanUnregistration", array, array2);
                }
                catch (final InstanceNotFoundException ex5) {
                    throw new RuntimeException(ex5.getMessage());
                }
                catch (final ReflectionException ex6) {
                    throw new RuntimeException(ex6.getMessage());
                }
                catch (final MBeanException ex7) {
                    throw new RuntimeException(ex7.getTargetException().getMessage());
                }
            }
        }
        JmxProperties.RELATION_LOGGER.exiting(RelationService.class.getName(), "handleReferenceUnregistration");
    }
}
