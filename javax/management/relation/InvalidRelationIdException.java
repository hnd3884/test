package javax.management.relation;

public class InvalidRelationIdException extends RelationException
{
    private static final long serialVersionUID = -7115040321202754171L;
    
    public InvalidRelationIdException() {
    }
    
    public InvalidRelationIdException(final String s) {
        super(s);
    }
}
