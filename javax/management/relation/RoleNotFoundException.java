package javax.management.relation;

public class RoleNotFoundException extends RelationException
{
    private static final long serialVersionUID = -2986406101364031481L;
    
    public RoleNotFoundException() {
    }
    
    public RoleNotFoundException(final String s) {
        super(s);
    }
}
