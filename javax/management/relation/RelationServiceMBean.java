package javax.management.relation;

import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.ObjectName;
import java.util.List;

public interface RelationServiceMBean
{
    void isActive() throws RelationServiceNotRegisteredException;
    
    boolean getPurgeFlag();
    
    void setPurgeFlag(final boolean p0);
    
    void createRelationType(final String p0, final RoleInfo[] p1) throws IllegalArgumentException, InvalidRelationTypeException;
    
    void addRelationType(final RelationType p0) throws IllegalArgumentException, InvalidRelationTypeException;
    
    List<String> getAllRelationTypeNames();
    
    List<RoleInfo> getRoleInfos(final String p0) throws IllegalArgumentException, RelationTypeNotFoundException;
    
    RoleInfo getRoleInfo(final String p0, final String p1) throws IllegalArgumentException, RelationTypeNotFoundException, RoleInfoNotFoundException;
    
    void removeRelationType(final String p0) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationTypeNotFoundException;
    
    void createRelation(final String p0, final String p1, final RoleList p2) throws RelationServiceNotRegisteredException, IllegalArgumentException, RoleNotFoundException, InvalidRelationIdException, RelationTypeNotFoundException, InvalidRoleValueException;
    
    void addRelation(final ObjectName p0) throws IllegalArgumentException, RelationServiceNotRegisteredException, NoSuchMethodException, InvalidRelationIdException, InstanceNotFoundException, InvalidRelationServiceException, RelationTypeNotFoundException, RoleNotFoundException, InvalidRoleValueException;
    
    ObjectName isRelationMBean(final String p0) throws IllegalArgumentException, RelationNotFoundException;
    
    String isRelation(final ObjectName p0) throws IllegalArgumentException;
    
    Boolean hasRelation(final String p0) throws IllegalArgumentException;
    
    List<String> getAllRelationIds();
    
    Integer checkRoleReading(final String p0, final String p1) throws IllegalArgumentException, RelationTypeNotFoundException;
    
    Integer checkRoleWriting(final Role p0, final String p1, final Boolean p2) throws IllegalArgumentException, RelationTypeNotFoundException;
    
    void sendRelationCreationNotification(final String p0) throws IllegalArgumentException, RelationNotFoundException;
    
    void sendRoleUpdateNotification(final String p0, final Role p1, final List<ObjectName> p2) throws IllegalArgumentException, RelationNotFoundException;
    
    void sendRelationRemovalNotification(final String p0, final List<ObjectName> p1) throws IllegalArgumentException, RelationNotFoundException;
    
    void updateRoleMap(final String p0, final Role p1, final List<ObjectName> p2) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationNotFoundException;
    
    void removeRelation(final String p0) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;
    
    void purgeRelations() throws RelationServiceNotRegisteredException;
    
    Map<String, List<String>> findReferencingRelations(final ObjectName p0, final String p1, final String p2) throws IllegalArgumentException;
    
    Map<ObjectName, List<String>> findAssociatedMBeans(final ObjectName p0, final String p1, final String p2) throws IllegalArgumentException;
    
    List<String> findRelationsOfType(final String p0) throws IllegalArgumentException, RelationTypeNotFoundException;
    
    List<ObjectName> getRole(final String p0, final String p1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException;
    
    RoleResult getRoles(final String p0, final String[] p1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;
    
    RoleResult getAllRoles(final String p0) throws IllegalArgumentException, RelationNotFoundException, RelationServiceNotRegisteredException;
    
    Integer getRoleCardinality(final String p0, final String p1) throws IllegalArgumentException, RelationNotFoundException, RoleNotFoundException;
    
    void setRole(final String p0, final Role p1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException, RoleNotFoundException, InvalidRoleValueException, RelationTypeNotFoundException;
    
    RoleResult setRoles(final String p0, final RoleList p1) throws RelationServiceNotRegisteredException, IllegalArgumentException, RelationNotFoundException;
    
    Map<ObjectName, List<String>> getReferencedMBeans(final String p0) throws IllegalArgumentException, RelationNotFoundException;
    
    String getRelationTypeName(final String p0) throws IllegalArgumentException, RelationNotFoundException;
}
