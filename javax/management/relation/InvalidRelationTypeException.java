package javax.management.relation;

public class InvalidRelationTypeException extends RelationException
{
    private static final long serialVersionUID = 3007446608299169961L;
    
    public InvalidRelationTypeException() {
    }
    
    public InvalidRelationTypeException(final String s) {
        super(s);
    }
}
