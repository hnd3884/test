package java.rmi;

@Deprecated
public class ServerRuntimeException extends RemoteException
{
    private static final long serialVersionUID = 7054464920481467219L;
    
    @Deprecated
    public ServerRuntimeException(final String s, final Exception ex) {
        super(s, ex);
    }
}
