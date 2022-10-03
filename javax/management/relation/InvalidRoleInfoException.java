package javax.management.relation;

public class InvalidRoleInfoException extends RelationException
{
    private static final long serialVersionUID = 7517834705158932074L;
    
    public InvalidRoleInfoException() {
    }
    
    public InvalidRoleInfoException(final String s) {
        super(s);
    }
}
