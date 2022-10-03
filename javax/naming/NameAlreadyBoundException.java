package javax.naming;

public class NameAlreadyBoundException extends NamingException
{
    private static final long serialVersionUID = -8491441000356780586L;
    
    public NameAlreadyBoundException(final String s) {
        super(s);
    }
    
    public NameAlreadyBoundException() {
    }
}
