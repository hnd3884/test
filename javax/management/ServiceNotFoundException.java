package javax.management;

public class ServiceNotFoundException extends OperationsException
{
    private static final long serialVersionUID = -3990675661956646827L;
    
    public ServiceNotFoundException() {
    }
    
    public ServiceNotFoundException(final String s) {
        super(s);
    }
}
