package javax.management.relation;

public class InvalidRelationServiceException extends RelationException
{
    private static final long serialVersionUID = 3400722103759507559L;
    
    public InvalidRelationServiceException() {
    }
    
    public InvalidRelationServiceException(final String s) {
        super(s);
    }
}
