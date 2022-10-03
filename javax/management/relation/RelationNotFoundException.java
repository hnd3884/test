package javax.management.relation;

public class RelationNotFoundException extends RelationException
{
    private static final long serialVersionUID = -3793951411158559116L;
    
    public RelationNotFoundException() {
    }
    
    public RelationNotFoundException(final String s) {
        super(s);
    }
}
