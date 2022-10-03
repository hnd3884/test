package java.rmi;

public class ServerError extends RemoteException
{
    private static final long serialVersionUID = 8455284893909696482L;
    
    public ServerError(final String s, final Error error) {
        super(s, error);
    }
}
