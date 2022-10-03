package javax.naming;

public class CommunicationException extends NamingException
{
    private static final long serialVersionUID = 3618507780299986611L;
    
    public CommunicationException(final String s) {
        super(s);
    }
    
    public CommunicationException() {
    }
}
