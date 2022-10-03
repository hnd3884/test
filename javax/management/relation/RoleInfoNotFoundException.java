package javax.management.relation;

public class RoleInfoNotFoundException extends RelationException
{
    private static final long serialVersionUID = 4394092234999959939L;
    
    public RoleInfoNotFoundException() {
    }
    
    public RoleInfoNotFoundException(final String s) {
        super(s);
    }
}
