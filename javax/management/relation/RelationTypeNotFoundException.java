package javax.management.relation;

public class RelationTypeNotFoundException extends RelationException
{
    private static final long serialVersionUID = 1274155316284300752L;
    
    public RelationTypeNotFoundException() {
    }
    
    public RelationTypeNotFoundException(final String s) {
        super(s);
    }
}
