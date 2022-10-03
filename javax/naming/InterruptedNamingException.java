package javax.naming;

public class InterruptedNamingException extends NamingException
{
    private static final long serialVersionUID = 6404516648893194728L;
    
    public InterruptedNamingException(final String s) {
        super(s);
    }
    
    public InterruptedNamingException() {
    }
}
