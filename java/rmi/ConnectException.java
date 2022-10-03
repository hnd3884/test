package java.rmi;

public class ConnectException extends RemoteException
{
    private static final long serialVersionUID = 4863550261346652506L;
    
    public ConnectException(final String s) {
        super(s);
    }
    
    public ConnectException(final String s, final Exception ex) {
        super(s, ex);
    }
}
