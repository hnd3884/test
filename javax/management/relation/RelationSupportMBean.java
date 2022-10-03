package javax.management.relation;

public interface RelationSupportMBean extends Relation
{
    Boolean isInRelationService();
    
    void setRelationServiceManagementFlag(final Boolean p0) throws IllegalArgumentException;
}
