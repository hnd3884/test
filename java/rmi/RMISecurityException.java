package java.rmi;

@Deprecated
public class RMISecurityException extends SecurityException
{
    private static final long serialVersionUID = -8433406075740433514L;
    
    @Deprecated
    public RMISecurityException(final String s) {
        super(s);
    }
    
    @Deprecated
    public RMISecurityException(final String s, final String s2) {
        this(s);
    }
}
