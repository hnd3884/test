package java.rmi;

public class StubNotFoundException extends RemoteException
{
    private static final long serialVersionUID = -7088199405468872373L;
    
    public StubNotFoundException(final String s) {
        super(s);
    }
    
    public StubNotFoundException(final String s, final Exception ex) {
        super(s, ex);
    }
}
