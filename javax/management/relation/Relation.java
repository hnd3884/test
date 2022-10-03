package javax.management.relation;

import java.util.Map;
import javax.management.ObjectName;
import java.util.List;

public interface Relation
{
    List<ObjectName> getRole(final String p0) throws IllegalArgumentException, RoleNotFoundException, RelationServiceNotRegisteredException;
    
    RoleResult getRoles(final String[] p0) throws IllegalArgumentException, RelationServiceNotRegisteredException;
    
    Integer getRoleCardinality(final String p0) throws IllegalArgumentException, RoleNotFoundException;
    
    RoleResult getAllRoles() throws RelationServiceNotRegisteredException;
    
    RoleList retrieveAllRoles();
    
    void setRole(final Role p0) throws IllegalArgumentException, RoleNotFoundException, RelationTypeNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationNotFoundException;
    
    RoleResult setRoles(final RoleList p0) throws IllegalArgumentException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
    
    void handleMBeanUnregistration(final ObjectName p0, final String p1) throws IllegalArgumentException, RoleNotFoundException, InvalidRoleValueException, RelationServiceNotRegisteredException, RelationTypeNotFoundException, RelationNotFoundException;
    
    Map<ObjectName, List<String>> getReferencedMBeans();
    
    String getRelationTypeName();
    
    ObjectName getRelationServiceName();
    
    String getRelationId();
}
