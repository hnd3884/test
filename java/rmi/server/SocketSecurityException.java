package java.rmi.server;

@Deprecated
public class SocketSecurityException extends ExportException
{
    private static final long serialVersionUID = -7622072999407781979L;
    
    public SocketSecurityException(final String s) {
        super(s);
    }
    
    public SocketSecurityException(final String s, final Exception ex) {
        super(s, ex);
    }
}
