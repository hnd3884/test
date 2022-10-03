package java.rmi;

public class ServerException extends RemoteException
{
    private static final long serialVersionUID = -4775845313121906682L;
    
    public ServerException(final String s) {
        super(s);
    }
    
    public ServerException(final String s, final Exception ex) {
        super(s, ex);
    }
}
