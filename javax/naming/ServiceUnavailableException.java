package javax.naming;

public class ServiceUnavailableException extends NamingException
{
    private static final long serialVersionUID = -4996964726566773444L;
    
    public ServiceUnavailableException(final String s) {
        super(s);
    }
    
    public ServiceUnavailableException() {
    }
}
