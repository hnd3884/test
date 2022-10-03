package javax.management;

public class AttributeNotFoundException extends OperationsException
{
    private static final long serialVersionUID = 6511584241791106926L;
    
    public AttributeNotFoundException() {
    }
    
    public AttributeNotFoundException(final String s) {
        super(s);
    }
}
