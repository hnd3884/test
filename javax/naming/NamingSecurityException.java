package javax.naming;

public abstract class NamingSecurityException extends NamingException
{
    private static final long serialVersionUID = 5855287647294685775L;
    
    public NamingSecurityException(final String s) {
        super(s);
    }
    
    public NamingSecurityException() {
    }
}
