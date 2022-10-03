package java.rmi;

public class AccessException extends RemoteException
{
    private static final long serialVersionUID = 6314925228044966088L;
    
    public AccessException(final String s) {
        super(s);
    }
    
    public AccessException(final String s, final Exception ex) {
        super(s, ex);
    }
}
